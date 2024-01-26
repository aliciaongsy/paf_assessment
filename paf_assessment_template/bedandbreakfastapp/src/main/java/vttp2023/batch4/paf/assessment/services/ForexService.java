package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ForexService {

	private String url = "https://api.frankfurter.app/latest";

	// TODO: Task 5
	public float convert(String from, String to, float amount) {
		String search = UriComponentsBuilder.fromUriString(url)
			.queryParam("amount", amount)
			.queryParam("from", from.toUpperCase())
			.queryParam("to", to.toUpperCase()).toUriString();

		RequestEntity<Void> req = RequestEntity.get(search).build();

		RestTemplate template = new RestTemplate();

		ResponseEntity<String> resp = template.exchange(req, String.class);

		JsonReader jsonReader = Json.createReader(new StringReader(resp.getBody()));
		JsonObject jsonObject = jsonReader.readObject();
		JsonObject rates = jsonObject.getJsonObject("rates");

		if (rates.isEmpty()){
			return -1000f;
		}
		float convert = rates.getJsonNumber("SGD").longValue();

		return convert;
	}
}
