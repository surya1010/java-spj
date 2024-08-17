package com.spj;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TiketController {

  @Autowired
  TicketService ticketService;

  @GetMapping("/available-tiket")
  @Operation(summary = "Get available tickets", description = "Fetch a list of available tickets")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved tickets"),
      @ApiResponse(responseCode = "204", description = "No tickets found")
  })
  public ResponseEntity<List<Ticket>> availableTicket(@RequestParam(required = false) Integer availableStatus) {
    List<Ticket> availableTickets = ticketService.findTicketAvailable(availableStatus);

    if (availableTickets.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(availableTickets);
  }


  @PostMapping("/book-ticket")
  @Operation(summary = "Book a ticket", description = "Submit a booking request for a ticket")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Booking request received"),
      @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
  })
  public BookTicketResponse bookTicket(@RequestBody BookTicketRequest request) {
    return ticketService.bookTicket(request.getTicketId(), request.getQty());
  }
}
