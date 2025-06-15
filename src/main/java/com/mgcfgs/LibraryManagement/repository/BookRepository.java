package com.mgcfgs.LibraryManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mgcfgs.LibraryManagement.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {



}
