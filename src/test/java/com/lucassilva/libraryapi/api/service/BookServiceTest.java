package com.lucassilva.libraryapi.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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
	
	@Test
	@DisplayName("Deve obter um livro por ID")
	public void getByIdTest() {
		
		//cenário
		Long id = 1L;
		
		Book book = createValidBook();
		book.setId(id);
		
		Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));
		
		//execução
		Optional<Book> foundBook = bookService.getById(id);
		
		//verificações
		assertThat(foundBook.isPresent()).isTrue();
		assertThat(foundBook.get().getId()).isEqualTo(id);
		assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
		assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
		assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());

	}
	
	@Test
	@DisplayName("Deve retornar vazo ao obter um livro por Id quando ele não existe na base")
	public void bookNotFoundByIdTest() {
		
		//cenário
		Long id = 1L;
		
		Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());
		
		//execução
		Optional<Book> foundBook = bookService.getById(id);
		
		//verificações
		assertThat(foundBook.isPresent()).isFalse();
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() {
		
		//cenário
		Book book = Book.builder().id(1L).build();
		
		//execução
		org.junit.jupiter.api.Assertions.assertDoesNotThrow( () ->  bookService.delete(book));
		
		//verificações
		Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
	}
	
	@Test
	@DisplayName("Deve ocorrer um erro ao tentar deletar um livro existente")
	public void deleteInvalidBookTest() {
		
		//cenário
		Book book = new Book();
		
		//execução
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.delete(book));
		
		//verificações
		Mockito.verify(bookRepository, Mockito.never()).delete(book);
	}
	
	@Test
	@DisplayName("Deve ocorrer um erro ao tentar atualizar um livro existente")
	public void updateInvalidBookTest() {
		
		//cenário
		Book book = new Book();
		
		//execução
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.update(book));
		
		//verificações
		Mockito.verify(bookRepository, Mockito.never()).save(book);
	}
	
	@Test
	@DisplayName("Deve atualizar um livro")
	public void updateBookTest() {
		
		//cenário
		Long id = 1L;
		
		Book updatingBook = Book.builder().id(id).build();
		
		Book updatedBook = createValidBook();
		updatedBook.setId(id);
		
		Mockito.when(bookRepository.save(updatingBook)).thenReturn(updatedBook);
		
		//execução
		Book book = bookService.update(updatingBook);
		
		//verificações
		assertThat(book.getId()).isEqualTo(updatedBook.getId());
		assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
		assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
		assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());	
	
	}
	
	private Book createValidBook() {
		return Book.builder().isbn("123").title("Meu Livro").author("Lucas").build();
	}
	
}
