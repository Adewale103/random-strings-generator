package com.edrone.randomstringgenerator.controller;

import com.edrone.randomstringgenerator.dtos.Request.AddJobRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class JobControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Add Job Test")
    public void addJobTest() throws Exception {
        mockMvc.perform(post("/api/v1/string-generator/")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(new AddJobRequest(1,3,10,"a,b,f")))
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }
    @Test
    @DisplayName("Get Number of Job Test")
    public void getNumberOfJobTest() throws Exception {
        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/api/v1/string-generator/numberOfJobs");
        mockMvc.perform(request.contentType("application/json")).
                andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("Get Job By Filename Test")
    public void GetJobByFileNameTest() throws Exception {
        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.get("/api/v1/string-generator/Job-e14b7");
        mockMvc.perform(request.contentType("application/json")).
                andExpect(status().isOk())
                .andDo(print());
    }

}