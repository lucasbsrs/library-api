package com.lucassilva.libraryapi.api.service;

import java.util.Optional;

import com.lucassilva.libraryapi.model.entity.Book;

public interface BookService {
	
	Book save(Book any);

	Optional<Book> getById(Long id);
	
}
