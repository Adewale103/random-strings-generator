package com.edrone.randomstringgenerator.dtos.Response;

import lombok.*;


@Getter
@AllArgsConstructor
@Builder
public class AddJobResponse {
    private String message;
    private String fileName;
}
