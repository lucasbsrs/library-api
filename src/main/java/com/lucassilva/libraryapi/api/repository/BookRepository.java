package com.lucassilva.libraryapi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucassilva.libraryapi.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

	boolean existsByIsbn(String isbn);

}
