package com.edrone.randomstringgenerator.services;

import com.edrone.randomstringgenerator.data.models.Job;
import com.edrone.randomstringgenerator.dtos.Request.AddJobRequest;
import com.edrone.randomstringgenerator.dtos.Response.AddJobResponse;
import com.edrone.randomstringgenerator.dtos.Response.RunningJobResponse;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public interface JobService {
    AddJobResponse addJob(AddJobRequest addJobRequest) throws ExecutionException, InterruptedException, IOException;
    Job findJobByFileName(String fileName);
    RunningJobResponse getNumberOfJobsRunning();
    ResponseEntity<?> getCompletedJob(String fileName)throws MalformedURLException;
}
