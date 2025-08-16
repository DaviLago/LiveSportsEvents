package com.example.sports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.sports.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

}
