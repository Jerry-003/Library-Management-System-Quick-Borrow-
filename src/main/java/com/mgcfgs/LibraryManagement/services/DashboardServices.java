package com.mgcfgs.LibraryManagement.services;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mgcfgs.LibraryManagement.repository.BookRepository;
import com.mgcfgs.LibraryManagement.repository.BookLoanRepository;
import com.mgcfgs.LibraryManagement.repository.UserRepository;

@Service
public class DashboardServices {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookLoanRepository bookLoanRepository;

    public long getTotalBooks() {
        return bookRepository.count();
    }

    public long getTotalMembers() {
        return userRepository.count();
    }

    public long getBooksIssued() {
        return bookLoanRepository.count();
    }

    public long getOverdueReturns() {
        return bookLoanRepository.countByDueDateBeforeAndReturnDateIsNull(LocalDate.now());
    }
}
