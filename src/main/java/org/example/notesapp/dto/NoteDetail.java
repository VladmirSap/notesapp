package org.example.notesapp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.notesapp.model.Tag;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class NoteDetail {
    private String id;
    private String title;
    private String text;
    private LocalDateTime createdDate;
    private List<Tag> tags;
}
