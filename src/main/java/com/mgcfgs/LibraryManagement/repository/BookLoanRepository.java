package com.mgcfgs.LibraryManagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mgcfgs.LibraryManagement.model.Book;
import com.mgcfgs.LibraryManagement.model.BookLoan;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    @Query("SELECT bl.book FROM BookLoan bl WHERE bl.status = 'ACTIVE'")
    List<Book> findAllIssuedBooks();

    @Query("SELECT bl FROM BookLoan bl WHERE bl.book.id = :bookId AND bl.status = 'ACTIVE'")
    List<BookLoan> findActiveLoansByBookId(@Param("bookId") Long bookId);

    @Query("SELECT COUNT(bl) FROM BookLoan bl WHERE bl.member.id = :memberId AND bl.status = 'ACTIVE'")
    int countActiveLoansByMember(@Param("memberId") Long memberId);

    // @Query("SELECT bl FROM BookLoan bl WHERE bl.member.id = :memberId")
    // List<BookLoan> findByMemberId(Long memberId);

    @Query("SELECT bl FROM BookLoan bl WHERE bl.member.id = :memberId AND bl.returnDate IS NULL")
    List<BookLoan> findByMemberIdAndReturnDateIsNull(@Param("memberId") Long memberId);
    
        
        long countByDueDateBeforeAndReturnDateIsNull(LocalDate currentDate);

		long countByReturnDateIsNotNull();
		long countByReturnDateBefore(LocalDate date);


    }
    