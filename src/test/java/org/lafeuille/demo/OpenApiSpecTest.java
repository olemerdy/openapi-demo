package org.lafeuille.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OpenApiSpecTest {

    private static final Path outputDir = Paths.get("build", "generated", "docs");

    @BeforeAll
    static void setUp() throws IOException {
        Files.createDirectories(outputDir);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void download_openapi_json() throws Exception {
        var bytes = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("openapi").value("3.0.1"))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        copyToPath(bytes, outputDir.resolve("openapi.json"));
    }

    @Test
    void download_openapi_yaml() throws Exception {
        var bytes = mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        copyToPath(bytes, outputDir.resolve("openapi.yaml"));
    }

    private void copyToPath(byte[] bytes, Path target) throws IOException {
        Files.deleteIfExists(target);
        try (var inputStream = new ByteArrayInputStream(bytes)) {
            Files.copy(inputStream, target);
        }
    }
}
