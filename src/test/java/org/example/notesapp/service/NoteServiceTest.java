package org.example.notesapp.service;

import org.example.notesapp.model.Note;
import org.example.notesapp.model.Tag;
import org.example.notesapp.dto.NoteRequest;
import org.example.notesapp.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private Note testNote;
    private NoteRequest testRequest;

    @BeforeEach
    void  setUp() {
        testNote = new Note();
        testNote.setId("1");
        testNote.setTitle("Test title");
        testNote.setText("This is a test note for testing purposes");
        testNote.setCreatedDate(LocalDateTime.now());
        testNote.setTags(Arrays.asList(Tag.PERSONAL));

        testRequest= new NoteRequest();
        testRequest.setTitle("Test title");
        testRequest.setText("This is a test note");
        testRequest.setTags(Arrays.asList(Tag.PERSONAL));

    }

    @Test
    void createNote_ShouldReturnSavedNote() {
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        Note result = noteService.createNote(testRequest);

        assertNotNull(result);
        assertEquals(testNote.getTitle(), result.getTitle());
        assertEquals(testNote.getText(), result.getText());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void getAllNotes_ShouldReturnPageOfNotes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Note> notePage = new PageImpl<>(Collections.singletonList(testNote));

        when(noteRepository.findAllByOrderByCreatedDateDesc(pageable)).thenReturn(notePage);

        var result = noteService.getAllNotes(pageable, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(noteRepository, times(1)).findAllByOrderByCreatedDateDesc(pageable);
    }

    @Test
    void getNoteById_WhenNoteExists_ShouldReturnNote() {
        when(noteRepository.findById("1")).thenReturn(Optional.of(testNote));

        var result = noteService.getNoteById("1");

        assertTrue(result.isPresent());
        assertEquals(testNote.getId(), result.get().getId());
    }

    @Test
    void getNoteById_WhenNoteNotExists_ShouldReturnEmpty() {
        when(noteRepository.findById("1")).thenReturn(Optional.empty());

        var result = noteService.getNoteById("1");

        assertFalse(result.isPresent());
    }

    @Test
    void updateNote_WhenNoteExists_ShouldUpdateAndReturnNote() {
        when(noteRepository.findById("1")).thenReturn(Optional.of(testNote));
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        var result = noteService.updateNote("1", testRequest);

        assertTrue(result.isPresent());
        verify(noteRepository, times(1)).save(testNote);
    }

    @Test
    void deleteNote_WhenNoteExists_ShouldReturnTrue() {
        when(noteRepository.existsById("1")).thenReturn(true);

        boolean result = noteService.deletedNote("1");

        assertTrue(result);
        verify(noteRepository, times(1)).deleteById("1");
    }

    @Test
    void getWordStatistics_ShouldReturnCorrectWordCount() {
        Note note = new Note();
        note.setText("a note is just a note");

        when(noteRepository.findById("1")).thenReturn(Optional.of(note));

        Map<String, Integer> statistics = noteService.getWordStatistics("1");

        assertNotNull(statistics);
        assertEquals(2, statistics.get("note"));
        assertEquals(2, statistics.get("a"));
        assertEquals(1, statistics.get("is"));
        assertEquals(1, statistics.get("just"));

    }

}
