package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.dto.AuthorDTO;
import com.example.quanlynhasach.model.Author;
import com.example.quanlynhasach.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    // Lấy tất cả author
    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        try {
            List<AuthorDTO> authorDTOs = authorService.getAllAuthors()
                    .stream()
                    .map(authorService::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(authorDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lấy author theo id
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable int id) {
        Author author = authorService.getAuthorById(id);
        if (author == null)
            return ResponseEntity.notFound().build();

        AuthorDTO dto = authorService.convertToDTO(author);
        return ResponseEntity.ok(dto);
    }

    // Tạo author mới
    @PostMapping
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        try {
            Author created = authorService.createAuthor(author);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Cập nhật author
    @PatchMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable int id, @RequestBody Author author) {
        try {
            Author updatedAuthor = authorService.updateAuthor(id, author);
            if (updatedAuthor != null) {
                return ResponseEntity.ok(updatedAuthor);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Xóa author
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable int id) {
        try {
            boolean deleted = authorService.deleteAuthor(id);
            if (deleted) {
                return ResponseEntity.ok("Đã xóa tác giả có ID = " + id);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}