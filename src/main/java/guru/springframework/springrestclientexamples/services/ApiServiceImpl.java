package guru.springframework.springrestclientexamples.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import guru.springframework.api.domain.User;
import guru.springframework.api.domain.UserData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ApiServiceImpl implements ApiService {
	
	private final String apiUrl;
	
	private RestTemplate restTemplate; 

	public ApiServiceImpl(RestTemplate restTemplate, @Value("${api.url}") String apiUrl) {
		super();
		this.restTemplate = restTemplate;
		this.apiUrl = apiUrl;
	}

	@Override
	public List<User> getUsers(Integer limit) {
		
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(apiUrl).queryParam("limit", limit);
		
		UserData userData = restTemplate.getForObject(uriComponentsBuilder.toUriString(), UserData.class);
        return userData.getData();
	}

	@Override
	public Flux<User> getUsers(Mono<Integer> limit) {

		 return WebClient
	                .create(apiUrl)
	                .get()
	                .uri(uriBuilder -> uriBuilder.queryParam("limit", limit.block()).build())
	                .accept(MediaType.APPLICATION_JSON)
	                .exchange()
	                .flatMap(resp -> resp.bodyToMono(UserData.class))
	                .flatMapIterable(UserData::getData);
	}

}
