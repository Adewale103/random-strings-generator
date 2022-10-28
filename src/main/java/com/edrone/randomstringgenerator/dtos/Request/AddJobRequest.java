package com.edrone.randomstringgenerator.dtos.Request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddJobRequest {
    private int minimumStringLength;
    private int maximumStringLength;
    private int expectedStringNumber;
    private String suggestedCharacters;
}
