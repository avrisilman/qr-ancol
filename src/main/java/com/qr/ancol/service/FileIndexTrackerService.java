package com.qr.ancol.service;

import com.qr.ancol.entity.FileIndexTracker;
import com.qr.ancol.repository.FileIndexTrackerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileIndexTrackerService {
    @Autowired
    private FileIndexTrackerRepository trackerRepository;

    public int getNextIndex(String type, int increment) {
        FileIndexTracker tracker = trackerRepository.findByType(type)
                .orElseGet(() -> {
                    FileIndexTracker t = new FileIndexTracker();
                    t.setType(type);
                    t.setCurrentIndex(1);
                    return t;
                });

        int current = tracker.getCurrentIndex();
        tracker.setCurrentIndex(current + increment);
        trackerRepository.save(tracker);
        return current;
    }
}
