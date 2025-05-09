package id.ac.ui.cs.advprog.event.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.model.EventBuilder;

@Repository
public interface EventRepository extends JpaRepository<EventBuilder, UUID> {
    
   
}