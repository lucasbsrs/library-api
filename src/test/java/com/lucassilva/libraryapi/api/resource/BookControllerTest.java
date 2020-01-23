package com.lucassilva.libraryapi.api.resource;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucassilva.libraryapi.api.dto.BookDTO;
import com.lucassilva.libraryapi.api.service.BookService;
import com.lucassilva.libraryapi.exception.BusinessException;
import com.lucassilva.libraryapi.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService bookService;
	
	@Test
	@DisplayName("Deve criar um livro com sucesso")
	public void createBookTest() throws Exception {
		
		BookDTO dto = createNewBook();
		
		Book savedBook = Book.builder().id(1L).author("Manel Loureiro").title("Apocalipse Z").isbn("001").build();
		
		BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);

		String json = new ObjectMapper().writeValueAsString(savedBook);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
														.post(BOOK_API)
														.contentType(MediaType.APPLICATION_JSON)
														.accept(MediaType.APPLICATION_JSON)
														.content(json);
		
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isCreated() )
			.andExpect( MockMvcResultMatchers.jsonPath("id").isNotEmpty() )
			.andExpect( MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()) )
			.andExpect( MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()) )
			.andExpect( MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()) );
		
	}
	
	@Test
	@DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro.")
	public void createInvalidBookTest() throws Exception {
		
		String json = new ObjectMapper().writeValueAsString(new BookDTO());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isBadRequest() )
			.andExpect( MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));

	}
	
	@Test
	@DisplayName("Deve lançar erro ao tentar cadastrar livro com isbn já utilizado por outro")
	public void createBookDuplicatedIsnb() throws Exception {
		
		BookDTO dto = createNewBook();

		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(bookService.save(Mockito.any(Book.class)))
				  .willThrow(new BusinessException("Isbn já cadastrado."));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API) 
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isBadRequest() )
			.andExpect( MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
			.andExpect( MockMvcResultMatchers.jsonPath("errors[0]").value("Isbn já cadastrado."));
	}
	
	@Test
	@DisplayName("Deve obter informações de um livro")
	public void getBookDetails() throws Exception {
		
		//cenário
		Long id = 1L;
		
		Book book = Book.builder()
						.id(id)
						.title(createNewBook().getTitle())
						.author(createNewBook().getAuthor())
						.isbn(createNewBook().getIsbn())
						.build();
		
		BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));
		
		//execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/" + id)) 
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isOk() )
			.andExpect( MockMvcResultMatchers.jsonPath("id").value(id) )
			.andExpect( MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()) )
			.andExpect( MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()) )
			.andExpect( MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()) );
		
	}
	
	@Test
	@DisplayName("Deve retornar resource not found quando o livro procurado não existir")
	public void booknotFoundTest() throws Exception {
		
		//cenário

		BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		//execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/" + 1)) 
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
			.perform(request)
			.andExpect( MockMvcResultMatchers.status().isNotFound() );
		
	}
	
	private BookDTO createNewBook() {
		return BookDTO.builder().author("Manel Loureiro").title("Apocalipse Z").isbn("001").build();
	}
	
}
