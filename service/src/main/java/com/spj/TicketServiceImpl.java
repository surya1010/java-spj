package com.spj;

import com.spj.repository.TicketBookingRepository;
import com.spj.repository.TicketRepository;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketServiceImpl implements TicketService {


  @Autowired
  TicketRepository ticketRepository;

  @Autowired
  TicketBookingRepository ticketBookingRepository;

  @Value("${booking.start-time:10:00}")
  public LocalTime startTime;

  @Value("${booking.end-time:10:20}")
  public LocalTime endTime;

  public LeakyBucket leakyBucket = new LeakyBucket(10000, 100); // 10000 tickets, 100 requests per second
  public Queue<BookTicketRequest> requestQueue = new LinkedList<>();
  private final int PROCESS_INTERVAL_MS = 1000; // Interval pemrosesan (ms)


  private static final int BATCH_SIZE_ACTIVE_HOURS = 100;
  private static final int BATCH_SIZE_OUTSIDE_ACTIVE_HOURS = 5;

  private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);


  @Override
  public List<Ticket> findTicketAvailable(int isAvailable) {
    return ticketRepository.findByIsAvailable(isAvailable);
  }

  @Override
  public BookTicketResponse bookTicket(Long ticketId, int qty) {
    LocalTime now = LocalTime.now();

    // Jika di luar jam aktif, bypass rate limiting
    if (now.isBefore(startTime) || now.isAfter(endTime)) {
      synchronized (requestQueue) {
        requestQueue.add(new BookTicketRequest(ticketId, qty));
      }
      return new BookTicketResponse(true, "Booking request received", HttpStatus.OK);
    }

    // Dalam jam aktif, terapkan rate limiting
    if (!leakyBucket.add(1)) {
      return new BookTicketResponse(false, "Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS);
    }

    synchronized (requestQueue) {
      requestQueue.add(new BookTicketRequest(ticketId, qty));
    }
    return new BookTicketResponse(true, "Booking request received", HttpStatus.OK);
  }

  @Scheduled(fixedRate = PROCESS_INTERVAL_MS)
  @Transactional
  public void processBatch() {
    logger.info("Running batch processor");
    LocalTime now = LocalTime.now();
    List<BookTicketRequest> batch = new LinkedList<>();


    int currentBatchSize = now.isBefore(startTime) || now.isAfter(endTime) ? BATCH_SIZE_OUTSIDE_ACTIVE_HOURS : BATCH_SIZE_ACTIVE_HOURS;
    synchronized (requestQueue) {
      for (int i = 0; i < currentBatchSize && !requestQueue.isEmpty(); i++) {
        batch.add(requestQueue.poll());
      }
    }
    if (!batch.isEmpty()) {
      processBatchRequests(batch);
    }
  }

  private void processBatchRequests(List<BookTicketRequest> requests) {
    Map<Long, Integer> ticketQuantities = new HashMap<>();
    for (BookTicketRequest request : requests) {
      ticketQuantities.merge(request.getTicketId(), request.getQty(), Integer::sum);
    }

    for (Map.Entry<Long, Integer> entry : ticketQuantities.entrySet()) {
      Long ticketId = entry.getKey();
      int qty = entry.getValue();

      Ticket ticket = ticketRepository.findById(ticketId)
          .orElseThrow(() -> new RuntimeException("Ticket not found"));

      if (ticket.getAvailableQuantity().intValue() - qty >= 0) {
        ticket.setAvailableQuantity(ticket.getAvailableQuantity().subtract(BigInteger.valueOf(qty)));
        ticketRepository.save(ticket);
        ticketBookingRepository.save(new TicketBooking(ticketId, qty));
      }
    }
  }

}
