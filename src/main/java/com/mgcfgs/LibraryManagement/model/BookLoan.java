package com.mgcfgs.LibraryManagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "book_loans")
public class BookLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private RegisterUser member;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    // Constructors
    public BookLoan() {
    }
    
    public BookLoan(Book book, RegisterUser member, LocalDate dueDate) {
        this();
        this.book = book;
        this.member = member;
        this.dueDate = dueDate;
        this.issueDate = LocalDate.now();
        this.status = LoanStatus.ACTIVE;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public RegisterUser getMember() {
        return member;
    }

    public void setMember(RegisterUser member) {
        this.member = member;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    // Business logic
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && status == LoanStatus.ACTIVE;
    }

    public void markAsReturned() {
        this.returnDate = LocalDate.now();
        this.status = isOverdue() ? LoanStatus.RETURNED_OVERDUE : LoanStatus.RETURNED;
        if (this.book != null) {
            this.book.returnLoan(this);
        }
    }

    public enum LoanStatus {
        ACTIVE,
        RETURNED,
        RETURNED_OVERDUE
    }

    @Override
    public String toString() {
        return "BookLoan{id=" + id +
               ", bookTitle=" + (book != null ? book.getTitle() : "null") +
               ", memberName=" + (member != null ? member.getName() : "null") +
               ", issueDate=" + issueDate +
               ", dueDate=" + dueDate +
               ", status=" + status + "}";
    }
}