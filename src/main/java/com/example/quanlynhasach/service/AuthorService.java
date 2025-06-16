package com.example.quanlynhasach.service;

import com.example.quanlynhasach.dto.AuthorDTO;
import com.example.quanlynhasach.model.Author;
import java.util.List;

public interface AuthorService {
    List<Author> getAllAuthors();

    Author getAuthorById(int id);

    Author createAuthor(Author author);

    Author updateAuthor(int id, Author author);

    boolean deleteAuthor(int id);

    AuthorDTO convertToDTO(Author author);

}