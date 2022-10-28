package com.edrone.randomstringgenerator.controller;


import com.edrone.randomstringgenerator.dtos.Request.AddJobRequest;
import com.edrone.randomstringgenerator.dtos.Response.AddJobResponse;
import com.edrone.randomstringgenerator.dtos.Response.RunningJobResponse;
import com.edrone.randomstringgenerator.services.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1/string-generator")
@RequiredArgsConstructor
@Slf4j
public class JobController {
    private final JobService jobService;

    @PostMapping("/")
    public ResponseEntity<?> addJob(@RequestBody AddJobRequest addJobRequest) throws IOException, ExecutionException, InterruptedException {
        AddJobResponse addJobResponse = jobService.addJob(addJobRequest);
        return new ResponseEntity<>(addJobResponse, HttpStatus.CREATED);
    }
    @GetMapping("/numberOfJobs")
    public ResponseEntity<?> getNumberOfJobsRunning(){
        RunningJobResponse numberOfJobs = jobService.getNumberOfJobsRunning();
        return new ResponseEntity<>(numberOfJobs, HttpStatus.OK);
    }
    @GetMapping("/{fileName}")
    public ResponseEntity<?> getCompletedJob(@PathVariable String fileName) throws MalformedURLException {
        return jobService.getCompletedJob(fileName);
    }

}
