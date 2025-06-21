package com.workshopapp.workshopservice.controller.publi;

import com.workshopapp.workshopservice.dto.DiagnoseRequest;
import com.workshopapp.workshopservice.dto.DiagnoseResponse;
import com.workshopapp.workshopservice.service.GptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advisor")
public class GptAdvisorController {

    private final GptService gptService;

    public GptAdvisorController(GptService gptService) {
        this.gptService = gptService;
    }

    @PostMapping("/diagnose")
    public ResponseEntity<DiagnoseResponse> diagnose(@RequestBody DiagnoseRequest request) {
        String suggestion = gptService.getSuggestedService(request.getDescription());
        return ResponseEntity.ok(new DiagnoseResponse(suggestion));
    }
}
