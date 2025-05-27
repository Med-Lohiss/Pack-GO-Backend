package com.packandgo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class PexelsService {

	@Value("${pexels.api.key}")
	private String pexelsApiKey;

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	public PexelsService(RestTemplate restTemplate, ObjectMapper objectMapper) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
	}

	public String obtenerImagenPorUbicacion(String ubicacion) {
		String url = "https://api.pexels.com/v1/search?query=" + ubicacion + "&per_page=1";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", pexelsApiKey);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				String body = response.getBody();

				JsonNode root = objectMapper.readTree(body);
				JsonNode photos = root.path("photos");

				if (photos.isArray() && photos.size() > 0) {
					JsonNode photo = photos.get(0);
					JsonNode src = photo.path("src");
					JsonNode original = src.path("original");

					if (!original.isMissingNode()) {
						return original.asText();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
