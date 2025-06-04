package com.example.gtics_ta.Services;

import com.example.gtics_ta.DTO.ReniecDTO;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Service
public class ReniecService {

    private final String API_URL = "https://api.apis.net.pe/v2/reniec/dni?numero=";
    private final String TOKEN = "apis-token-15581.bC6J7sKx6B0vbq5iaUWVMvN5ElvXamOJ";

    public ReniecDTO consultaPorDNI(String dni) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(TOKEN);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ReniecDTO> response = restTemplate.exchange(
                API_URL + dni,
                HttpMethod.GET,
                entity,
                ReniecDTO.class
        );
        return response.getBody();
    }
}
