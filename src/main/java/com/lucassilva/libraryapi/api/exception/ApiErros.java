package com.lucassilva.libraryapi.api.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

import com.lucassilva.libraryapi.exception.BusinessException;

public class ApiErros {

	private List<String> errors;
	
	public ApiErros(BindingResult bindingResult) {
		this.errors = new ArrayList<>();
		
		bindingResult.getAllErrors().forEach( error -> this.errors.add(error.getDefaultMessage()));
	}
	
	public ApiErros(BusinessException businessException) {
		this.errors = Arrays.asList(businessException.getMessage());
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
}
