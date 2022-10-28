package com.edrone.randomstringgenerator.dtos.Response;

import lombok.*;


@Getter
@AllArgsConstructor
@Builder
public class RunningJobResponse {
    private int numberOfRunningJobs;
}
