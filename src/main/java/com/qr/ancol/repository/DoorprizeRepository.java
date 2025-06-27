package com.qr.ancol.repository;

import com.qr.ancol.entity.Doorprize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoorprizeRepository extends JpaRepository<Doorprize, Long> {
}