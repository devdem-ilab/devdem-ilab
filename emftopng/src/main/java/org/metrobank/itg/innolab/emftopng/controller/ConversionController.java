package org.metrobank.itg.innolab.emftopng.controller;

import java.util.Map;

import org.metrobank.itg.innolab.emftopng.service.ConversionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/convert")
public class ConversionController {

    @Autowired
    private ConversionService service;
    
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Map<String, String>> getEmfBase64String(@RequestBody Map<String, String> request) {
        String base64Emf = request.get("base64Emf");

        if (base64Emf == null || base64Emf.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Base64 EMF string is required."));
        }

        try {
            String base64Png = service.convertEmfToPng(base64Emf);
            return ResponseEntity.ok(Map.of("base64Png", base64Png));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Conversion failed: " + e.getMessage()));
        }
    }
    
}
