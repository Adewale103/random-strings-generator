package com.edrone.randomstringgenerator.services;

import com.edrone.randomstringgenerator.data.models.Job;
import com.edrone.randomstringgenerator.dtos.Request.AddJobRequest;
import com.edrone.randomstringgenerator.dtos.Response.AddJobResponse;
import com.edrone.randomstringgenerator.exceptions.RandomStringGeneratorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

@SpringBootTest
class JobServiceImplTest {
    @Autowired
    private JobService jobService;
    private AddJobRequest addJobRequest;
    private AddJobRequest addJobRequest2;
    private AddJobRequest addJobRequest3;
    private AddJobRequest addJobRequest4;

    @BeforeEach
    void setUp() {
        addJobRequest = AddJobRequest.builder()
                .suggestedCharacters("a,f,r,e,t")
                .expectedStringNumber(10)
                .minimumStringLength(1)
                .maximumStringLength(3)
                .build();
        addJobRequest2 = AddJobRequest.builder()
                .suggestedCharacters("m,b,g,t")
                .expectedStringNumber(84)
                .minimumStringLength(1)
                .maximumStringLength(3)
                .build();
        addJobRequest3 = AddJobRequest.builder()
                .suggestedCharacters("r,y,u,s,d,g,h")
                .expectedStringNumber(1000)
                .minimumStringLength(2)
                .maximumStringLength(7)
                .build();
        addJobRequest4 = AddJobRequest.builder()
                .suggestedCharacters("a,f,r,e,t")
                .expectedStringNumber(600)
                .minimumStringLength(1)
                .maximumStringLength(6)
                .build();
    }

    @Test
    public void invalidSuggestedCharacterThrowsExceptionTest() {
        addJobRequest.setSuggestedCharacters("er,/h.,h,n");
        Assertions.assertThrows(RandomStringGeneratorException.class, () -> jobService.addJob(addJobRequest));

    }

    @Test
    public void minimumStringLengthLesserThanOneThrowsExceptionTest() {
        addJobRequest.setMinimumStringLength(0);
        Assertions.assertThrows(RandomStringGeneratorException.class, () -> jobService.addJob(addJobRequest));
    }

    @Test
    public void minimumStringLengthGreaterThanMaximumThrowsExceptionTest() {
        addJobRequest.setMinimumStringLength(5);
        addJobRequest.setMaximumStringLength(3);
        Assertions.assertThrows(RandomStringGeneratorException.class, () -> jobService.addJob(addJobRequest));

    }

    @Test
    public void jobCanBeSavedTest() throws IOException, ExecutionException, InterruptedException {
        AddJobResponse addJobResponse = jobService.addJob(addJobRequest);
        Assertions.assertEquals("Job processing in progress..", addJobResponse.getMessage());
    }

    @Test
    public void multipleJobsCanRunTest() throws IOException, ExecutionException, InterruptedException {
        AddJobResponse addJobResponse = jobService.addJob(addJobRequest);
        AddJobResponse addJobResponse2 = jobService.addJob(addJobRequest2);
        AddJobResponse addJobResponse3 = jobService.addJob(addJobRequest3);
        AddJobResponse addJobResponse4 = jobService.addJob(addJobRequest4);
        Assertions.assertEquals("Job processing in progress..", addJobResponse2.getMessage());
        Assertions.assertEquals("Job processing in progress..", addJobResponse.getMessage());
        Assertions.assertEquals("Job processing in progress..", addJobResponse4.getMessage());
        Assertions.assertEquals("Job processing in progress..", addJobResponse3.getMessage());

    }

    @Test
    public void jobWithLargeStringSizeCanBeGeneratedTest() throws IOException, ExecutionException, InterruptedException {
        AddJobRequest addJobRequest = AddJobRequest.builder()
                .suggestedCharacters("r,y,u,s,d,g,h,p")
                .expectedStringNumber(960792)
                .minimumStringLength(2)
                .maximumStringLength(7)
                .build();
        AddJobResponse addJobResponse = jobService.addJob(addJobRequest);
        Assertions.assertNotNull(addJobResponse.getFileName());
        Assertions.assertNotNull(addJobResponse.getMessage());
    }

    @Test
    public void jobCanBeGottenByFileNameTest() throws MalformedURLException {
        ResponseEntity<?> job = jobService.getCompletedJob("Job-e14b7");
        Assertions.assertEquals(HttpStatus.OK, job.getStatusCode());
    }

    @Test
    public void limitedCharacterOptionsGreaterThanRequestedStingSizeThrowException() {
        AddJobRequest addJobRequest = AddJobRequest.builder()
                .suggestedCharacters("m,b,g")
                .expectedStringNumber(8400)
                .minimumStringLength(1)
                .maximumStringLength(3)
                .build();
        Assertions.assertThrows(RandomStringGeneratorException.class, () -> jobService.addJob(addJobRequest));

    }

    @Test
    public void jobCanBeFoundByFileName() {
        Job foundJob = jobService.findJobByFileName("Job-e14b7");
        File file = new File("");
        String fileBasePath = file.getAbsolutePath();
        Assertions.assertEquals(Paths.get(fileBasePath + "/"+"Job-e14b7").toString(), foundJob.getFileUrl());
    }
}