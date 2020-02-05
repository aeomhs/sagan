package sagan.renderer.guides;

import java.util.List;
import java.util.stream.Collectors;

import sagan.renderer.RendererProperties;
import sagan.renderer.github.*;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * API for listing guides repositories and rendering them as {@link GuideContentResource}
 */
@RestController
@RequestMapping(path = "/guides", produces = MediaTypes.HAL_JSON_VALUE)
public class GuidesController {

	private final GuideRenderer guideRenderer;

	private final OrgGithubClient orgGithubClient;

	private final UserGithubClient userGithubClient;

	private final RendererProperties properties;

	private final GuideResourceAssembler guideAssembler = new GuideResourceAssembler();

	public GuidesController(GuideRenderer guideRenderer, OrgGithubClient orgGithub, UserGithubClient userGithub,
			RendererProperties properties) {
		this.guideRenderer = guideRenderer;
		this.orgGithubClient = orgGithub;
		this.userGithubClient = userGithub;
		this.properties = properties;
	}

	@ExceptionHandler(GithubResourceNotFoundException.class)
	public ResponseEntity resourceNotFound() {
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/")
	public Resources<GuideResource> listGuides() {
		List<GuideResource> guideResources =
				getGuideResourceListByRepoOwner(orgGithubClient, properties.getGuides().getOrganization());

		List<GuideResource> userGuideResources =
				getGuideResourceListByRepoOwner(userGithubClient, properties.getGuides().getOwner().getName());
		guideResources.addAll(userGuideResources);

		Resources<GuideResource> resources = new Resources<>(guideResources);
		for (GuideType type : GuideType.values()) {
			if (!GuideType.UNKNOWN.equals(type)) {
				resources.add(linkTo(methodOn(GuidesController.class).showGuide(type.getSlug(), null))
						.withRel(type.getSlug()));
			}
		}
		return resources;
	}

	private List<GuideResource> getGuideResourceListByRepoOwner(GithubClient client, String repositoryOwner) {
		return this.guideAssembler
				.toResources(client.fetchRepositories(repositoryOwner))
				.stream().filter(guide -> !guide.getType().equals(GuideType.UNKNOWN))
				.collect(Collectors.toList());
	}

	@GetMapping("/{type}/{guide}")
	public ResponseEntity<GuideResource> showGuide(@PathVariable String type, @PathVariable String guide) {
		GuideType guideType = GuideType.fromSlug(type);
		if (GuideType.UNKNOWN.equals(guideType)) {
			return ResponseEntity.notFound().build();
		}
		// TODO Don't control by exception
		Repository repository;
		GuideResource guideResource;
		try {
			repository = this.orgGithubClient.fetchRepository(properties.getGuides().getOrganization(),
					guideType.getPrefix() + guide);
		} catch (GithubResourceNotFoundException ex) {
			// user repo
			repository = this.userGithubClient.fetchRepository(properties.getGuides().getOwner().getName(),
					guideType.getPrefix() + guide);
		}

		guideResource = this.guideAssembler.toResource(repository);
		if (guideResource.getType().equals(GuideType.UNKNOWN)) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(guideResource);
	}

	@GetMapping("/{type}/{guide}/content")
	public ResponseEntity<GuideContentResource> renderGuide(@PathVariable String type, @PathVariable String guide) {
		GuideType guideType = GuideType.fromSlug(type);
		if (GuideType.UNKNOWN.equals(guideType)) {
			return ResponseEntity.notFound().build();
		}

		GuideContentResource guideContentResource = this.guideRenderer.render(guideType, guide);
		guideContentResource.add(linkTo(methodOn(GuidesController.class).renderGuide(guideType.getSlug(), guide)).withSelfRel());
		guideContentResource.add(linkTo(methodOn(GuidesController.class).showGuide(guideType.getSlug(), guide)).withRel("guide"));
		return ResponseEntity.ok(guideContentResource);
	}
}