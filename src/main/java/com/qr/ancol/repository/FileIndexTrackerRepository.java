package com.qr.ancol.repository;

import com.qr.ancol.entity.FileIndexTracker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileIndexTrackerRepository extends JpaRepository<FileIndexTracker, Long> {
    Optional<FileIndexTracker> findByType(String type);
}
