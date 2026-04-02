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

        String prompt = "You are a CV parser. Convert this into JSON. Return only JSON. Text: " + text;

        String body = """
    {
      "anthropic_version": "bedrock-2023-05-31",
      "max_tokens": 1000,
      "messages": [
        {
          "role": "user",
          "content": "%s"
        }
      ]
    }
    """.formatted(prompt);

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId("arn:aws:bedrock:us-east-1:937566679262:application-inference-profile/n8wo4v1vs0dr")
                .contentType("application/json")
                .accept("application/json")
                .body(SdkBytes.fromUtf8String(body))
                .build();

        InvokeModelResponse response = bedrockClient.invokeModel(request);

        return response.body().asUtf8String();
    }
}
