package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.dto.AuthorDTO;
import com.example.quanlynhasach.model.Author;
import com.example.quanlynhasach.repository.AuthorRepository;
import com.example.quanlynhasach.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Override
    public Author getAuthorById(int id) {
        return authorRepository.findById(id).orElse(null);
    }

    @Override
    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public Author updateAuthor(int id, Author author) {
        return authorRepository.findById(id).map(existingAuthor -> {
            if (author.getName() != null) {
                existingAuthor.setName(author.getName());
            }
            if (author.getBio() != null) {
                existingAuthor.setBio(author.getBio());
            }
            return authorRepository.save(existingAuthor);
        }).orElse(null);
    }

    @Override
    public boolean deleteAuthor(int id) {
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public AuthorDTO convertToDTO(Author author) {
        return new AuthorDTO(author.getId(), author.getName(), author.getBio());
    }
}