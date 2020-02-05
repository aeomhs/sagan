package sagan.renderer.github;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import sagan.renderer.RendererProperties;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Organization Client for the Github developer API
 */
@Component
public class OrgGithubClient extends GithubClient {

    private static final String REPOS_LIST_PATH = "/orgs/%s/repos?per_page=100";

    private static final String REPO_INFO_PATH = "/repos/{organization}/{repositoryName}";

    private static final String REPO_ZIPBALL_PATH = REPO_INFO_PATH + "/zipball";

    public OrgGithubClient(RestTemplateBuilder restTemplateBuilder,
                        RendererProperties properties) {
        super(restTemplateBuilder, properties);
    }

    public byte[] downloadRepositoryAsZipball(String organization, String repository) {
        try {
            byte[] response = this.restTemplate.getForObject(REPO_ZIPBALL_PATH,
                    byte[].class, organization, repository);
            return response;
        }
        catch (HttpClientErrorException ex) {
            throw new GithubResourceNotFoundException(organization, ex);
        }
    }

    public List<Repository> fetchRepositories(String organization) {
        List<Repository> repositories = new ArrayList<>();
        Optional<String> nextPage = Optional.of(String.format(REPOS_LIST_PATH, organization));
        while (nextPage.isPresent()) {
            ResponseEntity<Repository[]> page = this.restTemplate
                    .getForEntity(nextPage.get(), Repository[].class, organization);
            repositories.addAll(Arrays.asList(page.getBody()));
            nextPage = findNextPageLink(page);
        }
        return repositories;
    }

    public Repository fetchRepository(String organization, String repositoryName) {
        try {
            return this.restTemplate
                    .getForObject(REPO_INFO_PATH, Repository.class, organization, repositoryName);
        }
        catch (HttpClientErrorException ex) {
            throw new GithubResourceNotFoundException(organization, repositoryName, ex);
        }
    }

}