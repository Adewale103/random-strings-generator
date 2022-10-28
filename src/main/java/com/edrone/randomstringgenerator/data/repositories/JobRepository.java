package com.edrone.randomstringgenerator.data.repositories;

import com.edrone.randomstringgenerator.data.models.Job;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface JobRepository extends MongoRepository<Job, String> {
    boolean existsByFileName(String fileName);

    Optional<Job> findByFileName(String fileName);
}
