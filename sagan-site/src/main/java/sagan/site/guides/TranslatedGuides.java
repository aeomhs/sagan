package sagan.site.guides;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import sagan.projects.Project;
import sagan.site.renderer.GuideType;
import sagan.site.renderer.SaganRendererClient;

import java.util.Optional;

/**
 * Repository implementation providing data access services for translated guides.
 */
@Component
public class TranslatedGuides extends AbstractGuidesRepository<TranslatedGuide> {

    private static Logger logger = LoggerFactory.getLogger(TranslatedGuides.class);

    public static final String CACHE_TRANSLATEDS = "cache.translateds";

    public static final Class<?> CACHE_TRANSLATEDS_TYPE = GuideHeader[].class;

    public static final String CACHE_TRANSLATED = "cache.translated";

    public static final Class<?> CACHE_TRANSLATED_TYPE = TranslatedGuide.class;

    @Autowired
    public TranslatedGuides(SaganRendererClient client) {
        super(client, GuideType.TRANSLATED);
    }

    @Override
    @Cacheable(CACHE_TRANSLATEDS)
    public GuideHeader[] findAll() {
        return super.findAll();
    }

    @Override
    @Cacheable(cacheNames = CACHE_TRANSLATEDS, key="#project.id")
    public GuideHeader[] findByProject(Project project) {
        return super.findByProject(project);
    }

    @Override
    public Optional<GuideHeader> findGuideHeaderByName(String name) {
        return super.findGuideHeaderByName(name);
    }

    @Override
    @Cacheable(CACHE_TRANSLATED)
    public Optional<TranslatedGuide> findByName(String name) {
        return super.findByName(name);
    }

    @CacheEvict(CACHE_TRANSLATEDS)
    public void evictListFromCache() {
        logger.info("Translated Guides evicted from cache");
    }

    @CacheEvict(CACHE_TRANSLATED)
    public void evictFromCache(String guide) {
        logger.info("Translated Guide evicted from cache: {}", guide);
    }

}
