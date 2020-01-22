package com.lucassilva.libraryapi.api.service.impl;

import org.springframework.stereotype.Service;

import com.lucassilva.libraryapi.api.repository.BookRepository;
import com.lucassilva.libraryapi.api.service.BookService;
import com.lucassilva.libraryapi.exception.BusinessException;
import com.lucassilva.libraryapi.model.entity.Book;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;
	
	public BookServiceImpl(BookRepository repository) {
		this.repository = repository;
	}
 
	@Override
	public Book save(Book book) {
		if(repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado.");
		}
		
		return repository.save(book);
	}

}