package com.mgcfgs.LibraryManagement.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mgcfgs.LibraryManagement.model.ReturnHistory;
import com.mgcfgs.LibraryManagement.repository.ReturnHistoryRepository;

@Service
public class ReturnHistoryServices {

    @Autowired
    private ReturnHistoryRepository returnHistoryRepository;

    public List<ReturnHistory> getAllReturnHistories() {
        return returnHistoryRepository.findAll();
    }

    public void saveReturnHistory(ReturnHistory returnHistory) {
        returnHistoryRepository.save(returnHistory);
    }

    public long getTodaysReturnsCount() {
        return returnHistoryRepository.findAll().stream()
                .filter(r -> r.getReturnDate() != null)
                .filter(r -> r.getReturnDate().isEqual(LocalDate.now()))
                .count();
    }

    public long getOverdueReturnsCount() {
        return returnHistoryRepository.findAll().stream()
                .filter(r -> r.getReturnDate() != null && r.getDueDate() != null)
                .filter(r -> r.getReturnDate().isAfter(r.getDueDate()))
                .count();
    }

    public double getTotalFinesCollected() {
        return returnHistoryRepository.findAll().stream()
                .filter(r -> r.getFineAmount() != null)
                .mapToDouble(ReturnHistory::getFineAmount)
                .sum();
    }

    public List<ReturnHistory> getReturnHistoryByMember(Long id) {
        return returnHistoryRepository.findByMemberId(id);
    }

}
