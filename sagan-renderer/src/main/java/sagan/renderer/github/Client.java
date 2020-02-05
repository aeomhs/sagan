package sagan.renderer.github;

import java.util.List;

public interface Client {
    /**
     * Download a repository as a zipball
     * @param repositoryOwner the repository owner (organization or userName)
     * @param repository the repository name
     * @return the zipball as raw bytes
     */
    byte[] downloadRepositoryAsZipball(String repositoryOwner, String repository);

    /**
     * Lists all the repositories available under the given organization
     * @param repositoryOwner the repository owner (organization or userName)
     * @return the list of all repositories under that organization
     */
    List<Repository> fetchRepositories(String repositoryOwner);

    /**
     * Fetch repository information under the given organization
     * @param repositoryOwner the repository owner (organization or userName)
     * @param repositoryName the github repository name
     * @return the repository information
     */
    Repository fetchRepository(String repositoryOwner, String repositoryName);
}
