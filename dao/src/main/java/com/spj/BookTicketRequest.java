package com.spj;

public class BookTicketRequest {

  private Long ticketId;
  private int qty;

  public BookTicketRequest() {}
  public BookTicketRequest(Long ticketId, int qty) {
    this.ticketId = ticketId;
    this.qty = qty;
  }

  public Long getTicketId() {
    return ticketId;
  }

  public void setTicketId(Long ticketId) {
    this.ticketId = ticketId;
  }

  public int getQty() {
    return qty;
  }

  public void setQty(int qty) {
    this.qty = qty;
  }

}
