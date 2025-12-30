package org.example.notesapp.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collation = "notes")
@Schema(description = "User Note data")
public class Note {
    @Id
    private String id;
    @NotBlank(message = "Title must not be empty")
    @Schema(description = "Note title", example = "My first Note")
    private String title;
    @NotBlank(message = "Text must not be empty")
    @Schema(description = "Text of note", example = "This is note text")
    private String text;
    @NotNull
    private LocalDateTime createdDate;
    private List<Tag> tags;

    public Note() {
        this.createdDate = LocalDateTime.now();
    }

    public Note(String title, String text, List<Tag> tags) {
        this();
        this.title = title;
        this.text = text;
        this.tags = tags;
    }
}
