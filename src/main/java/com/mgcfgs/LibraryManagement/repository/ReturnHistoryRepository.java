package com.mgcfgs.LibraryManagement.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.mgcfgs.LibraryManagement.model.ReturnHistory;

@Repository
public interface ReturnHistoryRepository extends JpaRepository<ReturnHistory, Long> {
    // Custom query methods can be defined here if needed
    // For example, find by member or book
    List<ReturnHistory> findByMemberName(String memberName);
    // List<ReturnHistory> findByBook(Book book);

    @Query("SELECT r FROM ReturnHistory r WHERE r.memberId = ?1")
    // This method retrieves all return histories for a specific member
    List<ReturnHistory> findByMemberId(Long id);

	long countByReturnDateIsNotNull();

}
