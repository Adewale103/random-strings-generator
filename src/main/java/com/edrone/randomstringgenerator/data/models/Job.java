package com.edrone.randomstringgenerator.data.models;

import lombok.*;
import org.springframework.data.annotation.Id;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Job {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    private String fileName;
    private String fileUrl;
    private boolean completed;
}
