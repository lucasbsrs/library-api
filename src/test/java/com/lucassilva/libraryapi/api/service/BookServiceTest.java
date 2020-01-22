package com.lucassilva.libraryapi.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.lucassilva.libraryapi.api.repository.BookRepository;
import com.lucassilva.libraryapi.api.service.impl.BookServiceImpl;
import com.lucassilva.libraryapi.exception.BusinessException;
import com.lucassilva.libraryapi.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService bookService;
	
	@MockBean
	BookRepository bookRepository;
	
	@BeforeEach
	public void setup() {
		this.bookService = new BookServiceImpl(bookRepository);
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		
		Book book = createValidBook();
		
		Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(Boolean.FALSE);
		
		Mockito.when(bookRepository.save(book)).thenReturn(
					Book.builder()
						.id(11L)
						.isbn("123")
						.title("Meu Livro")
						.author("Lucas")
						.build()
		);
		
		Book savedBook = bookService.save(book);
		
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("123");
		assertThat(savedBook.getTitle()).isEqualTo("Meu Livro");
		assertThat(savedBook.getAuthor()).isEqualTo("Lucas");
	}

	@Test
	@DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
	public void shouldNotSaveABookWithDuplicatedISBN() {
		
		Book book = createValidBook();
		
		Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(Boolean.TRUE);
		
		Throwable exception = Assertions.catchThrowable( () -> bookService.save(book));
		
		assertThat(exception)
					.isInstanceOf(BusinessException.class)
					.hasMessage("Isbn já cadastrado.");
		
		Mockito.verify(bookRepository, Mockito.never()).save(book);
		
	}
	
	private Book createValidBook() {
		return Book.builder().isbn("123").title("Meu Livro").author("Lucas").build();
	}
	
}
