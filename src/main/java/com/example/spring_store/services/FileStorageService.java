package com.example.spring_store.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.spring_store.entities.User;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class FileStorageService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String filePath = "src/main/resources/static/users.json";

    public List<User> readUsers() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return List.of();
        return objectMapper.readValue(file, new TypeReference<>() {});
    }

    public void writeUsers(List<User> users) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), users);
    }
}
