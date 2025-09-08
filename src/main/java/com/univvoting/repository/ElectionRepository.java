package com.univvoting.repository;

import com.univvoting.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;


public interface ElectionRepository extends JpaRepository<Election, UUID> {
}
