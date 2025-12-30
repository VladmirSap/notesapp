package org.example.notesapp.service;

import org.example.notesapp.dto.NoteDetail;
import org.example.notesapp.dto.NoteRequest;
import org.example.notesapp.dto.NoteSummary;
import org.example.notesapp.model.Note;
import org.example.notesapp.model.Tag;
import org.example.notesapp.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    public Note createNote(NoteRequest request) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setText(request.getText());
        note.setTags(request.getTags() != null ? request.getTags() : new ArrayList<>());
        return noteRepository.save(note);
    }

    public Page<NoteSummary> getAllNotes(Pageable pageable, List<Tag> tags) {
        Page<Note> notes;
        if (tags != null && !tags.isEmpty()) {
            notes = noteRepository.findByTagsInOrderByCreatedDateDesc(tags, pageable);
        } else {
            notes = noteRepository.findAllByOrderByCreatedDateDesc(pageable);
        }

        return notes.map(note -> new NoteSummary(
                note.getId(),
                note.getTitle(),
                note.getCreatedDate()
        ));
    }

    public Optional<NoteDetail> getNoteById(String id) {
        return noteRepository.findById(id)
                .map(note -> {
                    NoteDetail noteDetail = new NoteDetail();
                    noteDetail.setId(note.getId());
                    noteDetail.setTitle(note.getTitle());
                    noteDetail.setText(note.getText());
                    noteDetail.setCreatedDate(note.getCreatedDate());
                    noteDetail.setTags(note.getTags());
                    return noteDetail;
                });
    }

    public Optional<Note> updateNote(String id, NoteRequest request) {
        return noteRepository.findById(id)
                .map(note -> {
                    note.setTitle(request.getTitle());
                    note.setText(request.getText());
                    note.setTags(request.getTags() != null ? request.getTags() : new ArrayList<>());
                    return noteRepository.save(note);
                });
    }

    public boolean deletedNote(String id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Map<String, Integer> getWordStatistics(String id) {
        Optional<Note> note = noteRepository.findById(id);
        if (note.isPresent()) {
            return calculateWordStatistics(note.get().getText());
        }
        return Collections.emptyMap();
    }

    private Map<String, Integer> calculateWordStatistics(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        // Remove punctuation and convert to lowercase
        String cleanedText = text.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();

        // Split into words and count occurrences
        Map<String, Integer> wordCount = Arrays.stream(cleanedText.split("\\s+"))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toMap(
                        word -> word,
                        word -> 1,
                        Integer::sum
                ));

        // Sort by count in descending order
        return wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

}
