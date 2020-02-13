package sagan.site.guides;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sagan.projects.Project;
import sagan.site.renderer.GuideType;
import sagan.site.renderer.SaganRendererClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Repository implementation providing data access services for tutorial guides.
 */
@Component
public class Topicals extends AbstractGuidesRepository<Topical> {

	private static Logger logger = LoggerFactory.getLogger(Topicals.class);

	public static final String CACHE_TOPICALS = "cache.topicals";

	public static final Class<?> CACHE_TOPICALS_TYPE = GuideHeader[].class;

	public static final String CACHE_TOPICAL = "cache.topical";

	public static final Class<?> CACHE_TOPICAL_TYPE = Topical.class;

	@Autowired
	public Topicals(SaganRendererClient client) {
		super(client, GuideType.TOPICAL);
	}

	@Override
	@Cacheable(CACHE_TOPICALS)
	public GuideHeader[] findAll() {
		return super.findAll();
	}

	@Override
	@Cacheable(cacheNames = CACHE_TOPICALS, key="#project.id")
	public GuideHeader[] findByProject(Project project) {
		return super.findByProject(project);
	}

	@Override
	public Optional<GuideHeader> findGuideHeaderByName(String name) {
		return super.findGuideHeaderByName(name);
	}

	@Override
	@Cacheable(CACHE_TOPICAL)
	public Optional<Topical> findByName(String name) {
		return super.findByName(name);
	}

	@CacheEvict(CACHE_TOPICALS)
	public void evictListFromCache() {
		logger.info("Tutorials evicted from cache");
	}

	@CacheEvict(CACHE_TOPICAL)
	public void evictFromCache(String guide) {
		logger.info("Tutorial evicted from cache: {}", guide);
	}

}
