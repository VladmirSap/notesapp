package org.example.notesapp.controller;

import org.example.notesapp.model.Tag;
import jakarta.validation.Valid;
import org.example.notesapp.dto.NoteRequest;
import org.example.notesapp.dto.NoteSummary;
import org.example.notesapp.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping
    public ResponseEntity<?>  createNote(@Valid @RequestBody NoteRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(noteService.createNote(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to create note: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<Tag> tags) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NoteSummary> notes = noteService.getAllNotes(pageable, tags);

        Map<String, Object> response = Map.of(
                "content", notes.getContent(),
                "page", notes.getNumber(),
                "size", notes.getSize(),
                "totalElements", notes.getTotalElements(),
                "totalPages", notes.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable String id) {
        return noteService.getNoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<?> getNoteStatistics(@PathVariable String id) {
        Map<String, Integer> statistics = noteService.getWordStatistics(id);
        if (statistics.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(statistics);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable String id,
                                        @Valid @RequestBody NoteRequest request) {
        return noteService.updateNote(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable String id) {
        if (noteService.deletedNote(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private List<Tag> parseTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return null;
        }

        try {
            return Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(Tag::valueOf)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            // Return null if any tag is invalid
            return null;
        }
    }

}
