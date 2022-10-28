package com.edrone.randomstringgenerator.services;

import com.edrone.randomstringgenerator.data.models.Job;
import com.edrone.randomstringgenerator.data.repositories.JobRepository;
import com.edrone.randomstringgenerator.dtos.Request.AddJobRequest;
import com.edrone.randomstringgenerator.dtos.Response.AddJobResponse;
import com.edrone.randomstringgenerator.dtos.Response.RunningJobResponse;
import com.edrone.randomstringgenerator.exceptions.RandomStringGeneratorException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private static int numberOfRunningJobs = 0;
    private final JobRepository jobRepository;

    @Override
    public AddJobResponse addJob(AddJobRequest addJobRequest) throws ExecutionException, InterruptedException, IOException {
        String[] suggestedCharacters = addJobRequest.getSuggestedCharacters().split(",");
        checkThatJobCanBeProcessed(addJobRequest, suggestedCharacters);
        CompletableFuture<List<String>> generatedUniqueStrings = generateUniqueStrings(addJobRequest, suggestedCharacters);
        Job job = Job.builder()
                .completed(false)
                .fileName(generateFileName())
                .build();
        Job savedJob = jobRepository.save(job);
        saveGeneratedStringToFile(generatedUniqueStrings.get(), addJobRequest, job);
    return AddJobResponse.builder()
            .fileName(savedJob.getFileName())
            .message("Job processing in progress..")
            .build();
    }

    @Override
    public Job findJobByFileName(String fileName) {
        return jobRepository.findByFileName(fileName).orElseThrow(()->
                new RandomStringGeneratorException("job with file name - "+fileName+" not found",404));
    }


    @Override
    public RunningJobResponse getNumberOfJobsRunning() {
        return RunningJobResponse
                .builder()
                .numberOfRunningJobs(numberOfRunningJobs)
                .build();
    }

    @Override
    public ResponseEntity<?> getCompletedJob(String fileName) throws MalformedURLException {
        Job foundJob = jobRepository.findByFileName(fileName).orElseThrow(()-> new
                RandomStringGeneratorException("Job with file name - "+fileName+" does not Exist",404));
        if(!foundJob.isCompleted()){
            throw new RandomStringGeneratorException("Job with file name - "+fileName+" is still processing. Please try again later",400);
        }
        Path path = getPath(foundJob);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/plain;charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private Path getPath(Job foundJob) {
        File file = new File("");
        String fileBasePath = file.getAbsolutePath();
        return Paths.get(fileBasePath + "/"+ foundJob.getFileName());
    }

    private void checkThatJobCanBeProcessed(AddJobRequest addJobRequest, String[] suggestedCharacters) {
        validateMinimumStringLength(addJobRequest);
        int estimatedStringSize = computeNumberOfStringsThatCanBeGenerated(addJobRequest, suggestedCharacters);
        validateThatRequestedNumberOfStringsCanBeGenerated(addJobRequest,estimatedStringSize);
        checkThatSuggestedCharactersAreValid(suggestedCharacters);
    }

    @Async
    CompletableFuture<List<String>> generateUniqueStrings(AddJobRequest addJobRequest, String[] suggestedCharacters) {
        numberOfRunningJobs+=1;
        List<String> completeList = new ArrayList<>();
        for(int i=addJobRequest.getMinimumStringLength(); i<=addJobRequest.getMaximumStringLength(); i++){
            List<String> result = getListOfGeneratedStrings(suggestedCharacters, i);
            completeList.addAll(result);
        }
        return CompletableFuture.completedFuture(new HashSet<>(completeList).stream().toList());
    }

    @Async
    void saveGeneratedStringToFile(List<String> generatedList, AddJobRequest addJobRequest, Job job) throws IOException {
        System.out.println(generatedList.size());
        System.out.println(addJobRequest.getExpectedStringNumber());
        for (int i = 0; i < addJobRequest.getExpectedStringNumber(); i++) {
                FileWriter fileWriter = new FileWriter(job.getFileName(), true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(generatedList.get(i));
                bufferedWriter.newLine();
                bufferedWriter.close();
        }
        numberOfRunningJobs -=1;
        job.setFileUrl(getPath(job).toString());
        job.setCompleted(true);
        jobRepository.save(job);
    }
    public static List<String> getListOfGeneratedStrings(String[] elements, int lengthOfList) {
        if(lengthOfList == 1) return Arrays.stream(elements).toList();
        else {
            List<String> generatedStringsList = new ArrayList<>();
            List<String> generatedStringsSubList = getListOfGeneratedStrings(elements, lengthOfList-1);
            for (String element : elements) {
                for (String generatedString : generatedStringsSubList) {
                    //add the newly appended combination to the list
                    generatedStringsList.add((element + generatedString));
                }
            }
            return generatedStringsList;
        }
    }

    private String generateFileName(){
        String fileName = "Job-"+UUID.randomUUID().toString().substring(1,6);
        if(jobRepository.existsByFileName(fileName)){
            generateFileName();
        }
        return fileName;
    }

    private void validateThatRequestedNumberOfStringsCanBeGenerated(AddJobRequest addJobRequest, int estimatedStringSize) {
        if(addJobRequest.getExpectedStringNumber() > estimatedStringSize){
            throw new RandomStringGeneratorException(String.format("""
                    The requested number of strings (%d) is greater than the number of strings that can be generated.
                    Only %d strings can be generated""", addJobRequest.getExpectedStringNumber(), estimatedStringSize),400);
        }
    }

    private void validateMinimumStringLength(AddJobRequest addJobRequest) {
        if (addJobRequest.getMinimumStringLength() < 1) {
            throw new RandomStringGeneratorException("The minimum string length can not be less than 1", 400);
        }
    }

    private int computeNumberOfStringsThatCanBeGenerated(AddJobRequest addJobRequest, String[] suggestedCharacters) {
        int estimatedStringSize = 0;
        for (int i = addJobRequest.getMinimumStringLength(); i <= addJobRequest.getMaximumStringLength() ; i++) {
            estimatedStringSize += Math.pow(suggestedCharacters.length, i);
        }
        return estimatedStringSize;
    }

    private void checkThatSuggestedCharactersAreValid(String[] suggestedCharacters) {
        for (String suggestedCharacter : suggestedCharacters) {
            if (suggestedCharacter.length() != 1) {
                throw new RandomStringGeneratorException("Invalid characters, ensure you separate each character by a comma when trying again",400);
            }
        }
    }
}
