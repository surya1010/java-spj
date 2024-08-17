package com.spj;

import java.util.List;

public interface TicketService {
  List<Ticket> findTicketAvailable(int isAvailable);

  BookTicketResponse bookTicket(Long ticketId, int qty);

}
