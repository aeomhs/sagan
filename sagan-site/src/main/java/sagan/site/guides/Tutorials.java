package sagan.site.guides;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sagan.projects.Project;
import sagan.site.renderer.GuideType;
import sagan.site.renderer.SaganRendererClient;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Repository implementation providing data access services for tutorial guides.
 */
@Component
public class Tutorials extends AbstractGuidesRepository<Tutorial> {

	private static Logger logger = LoggerFactory.getLogger(Tutorials.class);

	public static final String CACHE_TUTORIALS = "cache.tutorials";

	public static final Class<?> CACHE_TUTORIALS_TYPE = GuideHeader[].class;

	public static final String CACHE_TUTORIAL = "cache.tutorial";

	public static final Class<?> CACHE_TUTORIAL_TYPE = Tutorial.class;

	public Tutorials(SaganRendererClient client) {
		super(client, GuideType.TOPICAL);
	}

	@Override
	@Cacheable(CACHE_TUTORIALS)
	public GuideHeader[] findAll() {
		return super.findAll();
	}

	@Override
	@Cacheable(cacheNames = CACHE_TUTORIALS, key="#project.id")
	public GuideHeader[] findByProject(Project project) {
		return super.findByProject(project);
	}

	@Override
	public Optional<GuideHeader> findGuideHeaderByName(String name) {
		return super.findGuideHeaderByName(name);
	}

	@Override
	@Cacheable(CACHE_TUTORIAL)
	public Optional<Tutorial> findByName(String name) {
		return super.findByName(name);
	}

	@CacheEvict(CACHE_TUTORIALS)
	public void evictListFromCache() {
		logger.info("Tutorials evicted from cache");
	}

	@CacheEvict(CACHE_TUTORIAL)
	public void evictFromCache(String guide) {
		logger.info("Tutorial evicted from cache: {}", guide);
	}

}
