package com.mgcfgs.LibraryManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 100)
    @Column(nullable = false)
    private String author;

    @NotBlank(message = "ISBN is required")
    @Column(unique = true, nullable = false)
    private String isbn;

    @Size(max = 100)
    private String publisher;

    @PastOrPresent(message = "Publication date must be in the past or present")
    private LocalDate publicationDate;

    @NotBlank(message = "Category is required")
    @Size(max = 50)
    private String category;

    @NotBlank(message = "Language is required")
    @Size(max = 30)
    private String language = "English";

    @PositiveOrZero(message = "Total copies must be 0 or more")
    @Column(nullable = false)
    private int totalCopies = 1;

    @PositiveOrZero(message = "Available copies must be 0 or more")
    @Column(nullable = false)
    private int availableCopies = 1;

    @Size(max = 1000)
    private String description;

    // One book can have many loans
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookLoan> bookLoans = new ArrayList<>();

    // Constructors
    public Book() {
    }

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
        if (this.availableCopies > totalCopies) {
            this.availableCopies = totalCopies;
        }
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = Math.min(availableCopies, this.totalCopies);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<BookLoan> getBookLoans() {
        return bookLoans;
    }

    // Utility Methods
    public void addLoan(BookLoan loan) {
        if (loan.getBook() != this) {
            loan.setBook(this);
        }
        this.bookLoans.add(loan);
        this.availableCopies--;
    }

    public void returnLoan(BookLoan loan) {
        this.bookLoans.remove(loan);
        loan.setBook(null);
        this.availableCopies++;
    }

    public boolean isAvailable() {
        return this.availableCopies > 0;
    }

    // Add this method in Book entity or in a DTO
    public String getLastIssuedMemberName() {
        return this.bookLoans != null && !this.bookLoans.isEmpty()
                ? this.bookLoans.get(this.bookLoans.size() - 1).getMember().getName()
                : "N/A";
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", availableCopies=" + availableCopies +
                '}';
    }
}