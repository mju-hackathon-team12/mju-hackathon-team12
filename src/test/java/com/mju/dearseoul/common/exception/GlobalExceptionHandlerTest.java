package com.mju.dearseoul.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new ErrorTestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void businessExceptionsUseTheEnumStatusAndDefaultMessage() throws Exception {
        mvc.perform(get("/test/errors/business"))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.code").value("AI_RATE_LIMIT"))
                .andExpect(jsonPath("$.message").value(ErrorCode.AI_RATE_LIMIT.getMessage()));
    }

    @Test
    void safeCustomMessagesArePreserved() throws Exception {
        mvc.perform(get("/test/errors/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("장소를 찾을 수 없습니다."));
    }

    @Test
    void malformedAndMissingInputsHaveTheSameResponseShape() throws Exception {
        mvc.perform(post("/test/errors/body").contentType(MediaType.APPLICATION_JSON).content("{broken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
        mvc.perform(get("/test/errors/number").param("value", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
        mvc.perform(get("/test/errors/number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
    }

    @Test
    void duplicateErrorsUseSharedErrorCodes() throws Exception {
        mvc.perform(get("/test/errors/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_REQUEST"));
    }

    @Test
    void unexpectedExceptionsDoNotExposeInternalMessages() throws Exception {
        mvc.perform(get("/test/errors/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", aMapWithSize(2)))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

    @Test
    void unsupportedHttpRequestsKeepTheirOriginalStatus() throws Exception {
        mvc.perform(delete("/test/errors/business"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
        mvc.perform(post("/test/errors/body").contentType(MediaType.TEXT_PLAIN).content("hello"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value("UNSUPPORTED_MEDIA_TYPE"));
    }

    @RestController
    @RequestMapping("/test/errors")
    static class ErrorTestController {
        @GetMapping("/business")
        public void business() {
            throw new BusinessException(ErrorCode.AI_RATE_LIMIT);
        }

        @GetMapping("/missing")
        public void missing() {
            throw new BusinessException(ErrorCode.NOT_FOUND, "장소를 찾을 수 없습니다.");
        }

        @PostMapping(value = "/body", consumes = "application/json")
        public Map<String, Object> body(@RequestBody Map<String, Object> input) {
            return input;
        }

        @GetMapping("/number")
        public int number(@RequestParam int value) {
            return value;
        }

        @GetMapping("/conflict")
        public void conflict() {
            throw new DataIntegrityViolationException("internal database constraint");
        }

        @GetMapping("/unexpected")
        public void unexpected() {
            throw new IllegalStateException("private upstream detail and secret credentials");
        }
    }
}
