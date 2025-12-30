package org.example.notesapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.notesapp.model.Tag;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NoteRequest {
    @NotBlank(message = "Title must not be blank")
    private String title;
    @NotBlank(message = "Text must not be blank")
    private String text;
    private List<Tag> tags;
}
