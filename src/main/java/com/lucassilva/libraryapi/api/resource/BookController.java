package com.lucassilva.libraryapi.api.resource;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.lucassilva.libraryapi.api.dto.BookDTO;
import com.lucassilva.libraryapi.api.exception.ApiErros;
import com.lucassilva.libraryapi.api.service.BookService;
import com.lucassilva.libraryapi.exception.BusinessException;
import com.lucassilva.libraryapi.model.entity.Book;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	@Autowired
	private BookService bookService;
	
	private ModelMapper modelMapper;

	public BookController(BookService bookService, ModelMapper modelMapper) {
		this.bookService = bookService;
		this.modelMapper = modelMapper;
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create(@RequestBody @Valid BookDTO book) {
		
		Book bookEntity = modelMapper.map(book, Book.class);
		
		bookEntity = bookService.save(bookEntity);
		
		return modelMapper.map(book, BookDTO.class);
	}
	
	@GetMapping("{id}")
	public BookDTO get(@PathVariable Long id) {
		
		return bookService.getById(id)
				          .map( book -> modelMapper.map(book, BookDTO.class) )
				          .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		Book book = bookService.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		bookService.delete(book);
	}
	
	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public BookDTO update(@PathVariable Long id, BookDTO dto) {
		
		return bookService.getById(id).map( book -> {
			book.setAuthor(dto.getAuthor());
			book.setTitle(dto.getTitle());
			
			book = bookService.update(book);
			
			return modelMapper.map(book, BookDTO.class);
		}).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErros handleValidationExceptions(MethodArgumentNotValidException exception) {
		BindingResult bindingResult = exception.getBindingResult();
				
		return new ApiErros(bindingResult);
	}
	
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErros handleBusinessException(BusinessException exception) {
		return new ApiErros(exception);
	}
	
}
