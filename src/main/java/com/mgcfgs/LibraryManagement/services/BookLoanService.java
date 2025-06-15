package com.mgcfgs.LibraryManagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mgcfgs.LibraryManagement.model.Book;
import com.mgcfgs.LibraryManagement.model.BookLoan;
import com.mgcfgs.LibraryManagement.repository.BookLoanRepository;
import com.mgcfgs.LibraryManagement.repository.BookRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookLoanService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookLoanRepository loanRepository;

    public List<BookLoan> getAllLoans() {
        return loanRepository.findAll();
    }

    public void saveLoan(BookLoan loan) {
        loanRepository.save(loan);
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    public List<Book> getAllIssuedBooks() {
        return loanRepository.findAllIssuedBooks();
    }

    public BookLoan getLoanById(Long loanId) {
        return loanRepository.findById(loanId).orElse(null);
    }

    public void deleteLoan(Long loanId) {
        loanRepository.deleteById(loanId);
    }

    public boolean hasActiveLoans(Long memberId) {
        // Check if the member has any active loans
        List<BookLoan> activeLoans = loanRepository.findByMemberIdAndReturnDateIsNull(memberId);
        return !activeLoans.isEmpty();
    }

    public List<BookLoan> getActiveLoansByMemberId(Long id) {
        return loanRepository.findByMemberIdAndReturnDateIsNull(id);
    }

}