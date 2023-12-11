package dev.zawarudo.holo.apis;

import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;

/**
 * Class that handles the connection and interaction with the GitHub repository of the bot.
 */
public class GitHubClient {

    private final GHRepository repository;

    /**
     * Creates a new instance.
     *
     * @param token The OAuth token needed to connect to the GitHub repository.
     * @throws IOException When something went wrong with the connection.
     */
    public GitHubClient(String token) throws IOException {
        GitHub gitHub = GitHub.connectUsingOAuth(token);
        repository = gitHub.getRepository("xHarlock/holobot");
    }

    /**
     * Creates an issue at the repository of Holo and returns the URL.
     *
     * @param title The title of the issue.
     * @param description  The description of the issue.
     * @param label The label the issue should have.
     * @return The URL of the issue located in the repository.
     * @throws IOException When something went wrong with the creation of the issue.
     */
    public String createIssue(String title, String description, String label) throws IOException {
        GHIssueBuilder issueBuilder = repository.createIssue(title).body(description).label(label);
        return issueBuilder.create().getHtmlUrl().toString();
    }
}