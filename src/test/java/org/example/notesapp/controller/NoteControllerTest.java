package org.example.notesapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.notesapp.dto.NoteDetail;
import org.example.notesapp.dto.NoteRequest;
import org.example.notesapp.dto.NoteSummary;
import org.example.notesapp.model.Note;
import org.example.notesapp.model.Tag;
import org.example.notesapp.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NoteControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private NoteService noteService;

    @InjectMocks
    private NoteController noteController;

    private NoteRequest testRequest;
    private NoteSummary testSummary;
    private NoteDetail testDetail;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(noteController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        testRequest = new NoteRequest();
        testRequest.setTitle("Test Title");
        testRequest.setText("Test Text");
        testRequest.setTags(List.of(Tag.PERSONAL));

        testSummary = new NoteSummary("1", "Test Title", LocalDateTime.now());

        testDetail = new NoteDetail();
        testDetail.setId("1");
        testDetail.setTitle("Test Title");
        testDetail.setText("Test Text");
        testDetail.setCreatedDate(LocalDateTime.now());
        testDetail.setTags(List.of(Tag.PERSONAL));


    }

    @Test
    void createNote_ShouldReturnCreated() throws Exception {
        Note note = new Note();
        note.setId("1");
        note.setTitle("Test Title");
        note.setText("Test Text");
        note.setTags(List.of(Tag.PERSONAL));
        when(noteService.createNote(any(NoteRequest.class))).thenReturn(note);

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.text").value("Test Text"));
    }

    @Test
    void getAllNotes_ShouldReturnPage() throws Exception {
        Page<NoteSummary> page = new PageImpl<>(List.of(testSummary));

        lenient().when(noteService.getAllNotes(any(Pageable.class), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/notes")
                        .param("page", "0")
                        .param("size", "10")
                        .param("tags", "")) // порожній tags
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Title"));
    }

    @Test
    void getNoteById_ShouldReturnNote() throws Exception {
        when(noteService.getNoteById("1")).thenReturn(Optional.of(testDetail));

        mockMvc.perform(get("/api/notes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.text").value("Test Text"));
    }

    @Test
    void getNoteById_ShouldReturnNotFound() throws Exception {
        when(noteService.getNoteById("2")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notes/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateNote_ShouldReturnUpdated() throws Exception {
        Note updatedNote = new Note();
        updatedNote.setId("1");
        updatedNote.setTitle("Test Title");
        updatedNote.setText("Test Text");
        updatedNote.setTags(List.of(Tag.PERSONAL));
        when(noteService.updateNote(eq("1"), any(NoteRequest.class)))
                .thenReturn(Optional.of(updatedNote));

        mockMvc.perform(put("/api/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.text").value("Test Text"));
    }

    @Test
    void updateNote_ShouldReturnNotFound() throws Exception {
        when(noteService.updateNote(eq("2"), any(NoteRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/notes/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNote_ShouldReturnOk() throws Exception {
        when(noteService.deletedNote("1")).thenReturn(true);

        mockMvc.perform(delete("/api/notes/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteNote_ShouldReturnNotFound() throws Exception {
        when(noteService.deletedNote("2")).thenReturn(false);

        mockMvc.perform(delete("/api/notes/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getNoteStatistics_ShouldReturnStats() throws Exception {
        Map<String, Integer> stats = Map.of("test", 2);
        when(noteService.getWordStatistics("1")).thenReturn(stats);

        mockMvc.perform(get("/api/notes/1/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.test").value(2));
    }

    @Test
    void getNoteStatistics_ShouldReturnNotFound() throws Exception {
        when(noteService.getWordStatistics("2")).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/api/notes/2/statistics"))
                .andExpect(status().isNotFound());
    }
}
