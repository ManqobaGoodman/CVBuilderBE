package com.cvbuilder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.List;
import java.util.Map;

@Service
public class ClaudeService {

    private final BedrockRuntimeClient bedrockClient;
    private final ObjectMapper objectMapper;

    public ClaudeService(BedrockRuntimeClient bedrockClient) {
        this.bedrockClient = bedrockClient;
        this.objectMapper = new ObjectMapper();
    }

    public String analyzeCVSection(String text) {

            try {

                String cleanedText = text
                        .replaceAll("\\r?\\n", " ")
                        .replaceAll("\\s+", " ")
                        .trim();

                String prompt = """
You are a professional CV writer.

Improve and rewrite the following CV to make it:
- Professional
- Clear and well-structured
- ATS-friendly

Return ONLY valid JSON in this format:

{
  "name": "",
  "email": "",
  "phone": "",
  "location": "",
  "summary": "",
  "skills": [],
  "education": [],
  "experience": []
}

CV:
""" + cleanedText;

                // ✅ Build request properly using Map
                Map<String, Object> requestBody = Map.of(
                        "anthropic_version", "bedrock-2023-05-31",
                        "max_tokens", 1000,
                        "messages", List.of(
                                Map.of(
                                        "role", "user",
                                        "content", prompt
                                )
                        )
                );

                String body = objectMapper.writeValueAsString(requestBody);

                InvokeModelRequest request = InvokeModelRequest.builder()
                        .modelId("arn:aws:bedrock:us-east-1:937566679262:application-inference-profile/n8wo4v1vs0dr")
                        .contentType("application/json")
                        .accept("application/json")
                        .body(SdkBytes.fromUtf8String(body))
                        .build();

                InvokeModelResponse response = bedrockClient.invokeModel(request);

                return response.body().asUtf8String();

            } catch (Exception e) {
                throw new RuntimeException("Error calling Bedrock: " + e.getMessage(), e);
            }
        }
    }

