package com.spj.repository;

import com.spj.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

  List<Ticket> findByIsAvailable(int isAvailable);
}
