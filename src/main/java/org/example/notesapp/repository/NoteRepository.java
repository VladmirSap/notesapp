package org.example.notesapp.repository;

import org.example.notesapp.model.Note;
import org.example.notesapp.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    Page<Note> findAllByOrderByCreatedDateDesc(Pageable pageable);

    Page<Note> findByTagsInOrderByCreatedDateDesc(List<Tag> tags, Pageable pageable);

    @Query("{ 'tags': { $in: ?0 } }")
    Page<Note> findByTags(List<Tag> tags, Pageable pageable);

}
