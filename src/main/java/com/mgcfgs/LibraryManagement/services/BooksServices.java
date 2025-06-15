package com.mgcfgs.LibraryManagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mgcfgs.LibraryManagement.model.Book;
import com.mgcfgs.LibraryManagement.repository.BookRepository;

@Service
public class BooksServices {

    @Autowired
    BookRepository bookRepository;

    public void saveBook(Book book) {
        // This method saves the book to the database.
        bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
        // This method retrieves all books from the database.
    }

    public Book getBookById(Long bookId) {
        // This method retrieves a book by its ID from the database.
        return bookRepository.findById(bookId).orElse(null);
    }

    public void deleteBook(Long bookId) {
        // This method deletes a book by its ID from the database.
        bookRepository.deleteById(bookId);
    }

    public void updateBook(Long bookId, Book updatedBook) {
        // This method updates a book by its ID in the database.
        Book existingBook = bookRepository.findById(bookId).orElse(null);
        if (existingBook != null) {
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setPublisher(updatedBook.getPublisher());
            existingBook.setPublicationDate(updatedBook.getPublicationDate());
            bookRepository.save(existingBook);
        }
    }

}
