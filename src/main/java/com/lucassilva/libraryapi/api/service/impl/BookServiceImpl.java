package com.lucassilva.libraryapi.api.service.impl;

import java.util.Optional;

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

	@Override
	public Optional<Book> getById(Long id) {
		return repository.findById(id);
	}

	@Override
	public void delete(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("Livro não pode ser null");
		}
			
		this.repository.delete(book);
	}

	@Override
	public Book update(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("Livro não pode ser null");
		}
		
		return this.repository.save(book);
	}

}
