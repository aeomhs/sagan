package sagan.renderer.github;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sagan.renderer.RendererProperties;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

public abstract class GithubClient implements Client {

	public static final String API_URL_BASE = "https://api.github.com";

	private static final Pattern NEXT_LINK_PATTERN = Pattern.compile(".*<([^>]*)>;\\s*rel=\"next\".*");

	private static final Logger logger = LoggerFactory.getLogger(GithubClient.class);

	private static final MediaType GITHUB_PREVIEW_TYPE = MediaType.parseMediaType("application/vnd.github.mercy-preview+json");

	protected final RestTemplate restTemplate;

	public GithubClient(RestTemplateBuilder restTemplateBuilder,
						RendererProperties properties) {
		restTemplateBuilder = restTemplateBuilder
				.rootUri(API_URL_BASE)
				.additionalInterceptors(new GithubAcceptInterceptor());
		if (StringUtils.hasText(properties.getGithub().getToken())) {
			this.restTemplate = restTemplateBuilder
					.additionalInterceptors(new GithubAppTokenInterceptor(properties.getGithub().getToken()))
					.build();
		}
		else {
			this.logger.warn("GitHub API access will be rate-limited at 60 req/hour");
			this.restTemplate = restTemplateBuilder.build();
		}
	}

	protected Optional<String> findNextPageLink(ResponseEntity response) {
		List<String> links = response.getHeaders().get("Link");
		if (links == null) {
			return Optional.empty();
		}
		return links.stream()
				.map(NEXT_LINK_PATTERN::matcher)
				.filter(Matcher::matches)
				.map(matcher -> matcher.group(1))
				.findFirst();
	}

	protected static class GithubAppTokenInterceptor implements ClientHttpRequestInterceptor {

		private final String token;

		GithubAppTokenInterceptor(String token) {
			this.token = token;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body,
				ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
			if (StringUtils.hasText(this.token)) {
				httpRequest.getHeaders().set(HttpHeaders.AUTHORIZATION,
						"Token " + this.token);
			}
			return clientHttpRequestExecution.execute(httpRequest, body);
		}

	}

	protected static class GithubAcceptInterceptor implements ClientHttpRequestInterceptor {

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body,
				ClientHttpRequestExecution execution) throws IOException {
			request.getHeaders().setAccept(Collections.singletonList(GITHUB_PREVIEW_TYPE));
			return execution.execute(request, body);
		}
	}

}