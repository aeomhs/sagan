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
 * User Client for the Github developer API
 */
@Component
public class UserGithubClient extends GithubClient {

    private static final String REQ_TYPE = "users";
    /**
     * spring org fetch link : https://api.github.com/orgs/spring-guides/repos?per_page=100
     * local user fetch link : https://api.github.com/users/aeomhs/repos?per_page=100
     */
    private static final String USER_REPOS_LIST_PATH = "/%s/%s/repos?per_page=100";

    /**
     * spring org fetch link : https://api.github.com/repos/spring-guides/gs-testing-web
     * local user fetch link : https://api.github.com/repos/aeomhs/gs-local-env-test
     */
    private static final String USER_REPO_INFO_PATH = "/repos/{userName}/{repositoryName}";

    private static final String USER_REPO_ZIPBALL_PATH = USER_REPO_INFO_PATH + "/zipball";

    public UserGithubClient(RestTemplateBuilder restTemplateBuilder,
                        RendererProperties properties) {
        super(restTemplateBuilder, properties);
    }

    public byte[] downloadRepositoryAsZipball(String userName, String repository) {
        try {
            byte[] response = this.restTemplate.getForObject(USER_REPO_ZIPBALL_PATH,
                    byte[].class, userName, repository);
            return response;
        }
        catch (HttpClientErrorException ex) {
            throw new GithubResourceNotFoundException(userName, ex);
        }
    }

    public List<Repository> fetchRepositories(String userName) {
        List<Repository> repositories = new ArrayList<>();
        Optional<String> nextPage = Optional.of(String.format(USER_REPOS_LIST_PATH, REQ_TYPE, userName));
        while (nextPage.isPresent()) {
            ResponseEntity<Repository[]> page = this.restTemplate
                    .getForEntity(nextPage.get(), Repository[].class, REQ_TYPE, userName);
            repositories.addAll(Arrays.asList(page.getBody()));
            nextPage = findNextPageLink(page);
        }
        return repositories;
    }

    public Repository fetchRepository(String userName, String repositoryName) {
        try {
            return this.restTemplate
                    .getForObject(USER_REPO_INFO_PATH, Repository.class, userName, repositoryName);
        }
        catch (HttpClientErrorException ex) {
            throw new GithubResourceNotFoundException(userName, repositoryName, ex);
        }
    }
}