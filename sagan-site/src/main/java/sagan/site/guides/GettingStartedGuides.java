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
 * Repository implementation providing data access services for getting started guides.
 */
@Component
public class GettingStartedGuides extends AbstractGuidesRepository<GettingStartedGuide> {

	private static Logger logger = LoggerFactory.getLogger(GettingStartedGuides.class);

	public static final String CACHE_GUIDES = "cache.guides";

	public static final Class<?> CACHE_GUIDES_TYPE = GuideHeader[].class;

	public static final String CACHE_GUIDE = "cache.guide";

	public static final Class<?> CACHE_GUIDE_TYPE = GettingStartedGuide.class;

	public GettingStartedGuides(SaganRendererClient client) {
		super(client, GuideType.GETTING_STARTED);
	}

	@Override
	@Cacheable(CACHE_GUIDES)
	public GuideHeader[] findAll() {
		return super.findAll();
	}

	@Override
	@Cacheable(cacheNames = CACHE_GUIDES, key="#project.id")
	public GuideHeader[] findByProject(Project project) {
		return super.findByProject(project);
	}

	@Override
	public Optional<GuideHeader> findGuideHeaderByName(String name) {
		return super.findGuideHeaderByName(name);
	}

	@Override
	@Cacheable(CACHE_GUIDE)
	public Optional<GettingStartedGuide> findByName(String name) {
		return super.findByName(name);
	}

	@CacheEvict(CACHE_GUIDES)
	public void evictListFromCache() {
		logger.info("Getting Started guides evicted from cache");
	}

	@CacheEvict(CACHE_GUIDE)
	public void evictFromCache(String guide) {
		logger.info("Getting Started guide evicted from cache: {}", guide);
	}

}
