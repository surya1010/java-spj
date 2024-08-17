import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spj.BookTicketRequest;
import com.spj.BookTicketResponse;
import com.spj.LeakyBucket;
import com.spj.Ticket;
import com.spj.TicketBooking;
import com.spj.TicketServiceImpl;
import com.spj.repository.TicketBookingRepository;
import com.spj.repository.TicketRepository;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

public class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketBookingRepository ticketBookingRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @BeforeEach
    public void setUp() {
      MockitoAnnotations.openMocks(this);
      ticketService.startTime = LocalTime.of(10, 0);
      ticketService.endTime = LocalTime.of(10, 20);
      ticketService.requestQueue = new LinkedList<>();
      ticketService.leakyBucket = new LeakyBucket(10000, 100); // Adjust as needed
    }

    @Test
    public void testBookTicketDuringActiveHours() {
      LocalTime now = LocalTime.of(10, 10);
      ticketService.startTime = now.minusMinutes(1);
      ticketService.endTime = now.plusMinutes(1);

      BookTicketRequest request = new BookTicketRequest(1L, 5);
      when(ticketRepository.findById(1L)).thenReturn(Optional.of(createTicket(1L, 100)));

      BookTicketResponse response = ticketService.bookTicket(1L, 5);

      assertTrue(response.isSuccess());
      assertEquals("Booking request received", response.getMessage());
      assertEquals(HttpStatus.OK, response.getStatus());
    }


  @Test
  public void testBookTicketOutsideActiveHours() {
    LocalTime now = LocalTime.of(9, 59);
    ticketService.startTime = now.minusHours(1);
    ticketService.endTime = now.minusMinutes(1);

    BookTicketRequest request = new BookTicketRequest(1L, 5);
    when(ticketRepository.findById(1L)).thenReturn(Optional.of(createTicket(1L, 100)));

    BookTicketResponse response = ticketService.bookTicket(1L, 5);

    assertTrue(response.isSuccess());
    assertEquals("Booking request received", response.getMessage());
    assertEquals(HttpStatus.OK, response.getStatus());
  }

  @Test
  public void testProcessBatch() {
    LocalTime now = LocalTime.of(10, 10);
    ticketService.startTime = now.minusMinutes(1);
    ticketService.endTime = now.plusMinutes(1);

    BookTicketRequest request1 = new BookTicketRequest(1L, 5);
    BookTicketRequest request2 = new BookTicketRequest(2L, 10);

    ticketService.requestQueue.add(request1);
    ticketService.requestQueue.add(request2);

    Ticket ticket1 = createTicket(1L, 100);
    Ticket ticket2 = createTicket(2L, 100);

    when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket1));
    when(ticketRepository.findById(2L)).thenReturn(Optional.of(ticket2));

    ticketService.processBatch();

    verify(ticketRepository, times(1)).save(ticket1);
    verify(ticketRepository, times(1)).save(ticket2);
  }

    private Ticket createTicket(Long id, int quantity) {
      Ticket ticket = new Ticket();
      ticket.setId(id);
      ticket.setName("Concert");
      ticket.setTotalQuantity(BigInteger.valueOf(quantity));
      ticket.setAvailableQuantity(BigInteger.valueOf(quantity));
      return ticket;
    }

}
