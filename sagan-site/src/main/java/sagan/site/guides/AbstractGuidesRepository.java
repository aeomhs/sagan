package sagan.site.guides;

import sagan.projects.Project;
import sagan.site.renderer.GuideContent;
import sagan.site.renderer.GuideType;
import sagan.site.renderer.SaganRendererClient;

import java.util.Arrays;
import java.util.Optional;

public abstract class AbstractGuidesRepository<T extends Guide> implements GuidesRepository<T> {

    private final GuideType guideType;

    private final SaganRendererClient client;

    protected AbstractGuidesRepository(SaganRendererClient client, GuideType guideType) {
        this.client = client;
        this.guideType = guideType;
    }

    @Override
    public GuideHeader[] findAll() {
        return Arrays.stream(this.client.fetchGuides(guideType))
                .map(DefaultGuideHeader::new)
                .toArray(DefaultGuideHeader[]::new);
    }

    @Override
    public Optional<GuideHeader> findGuideHeaderByName(String name) {
        DefaultGuideHeader guideHeader = new DefaultGuideHeader(this.client.fetchGuide(guideType, name));
        return Optional.of(guideHeader);
    }

    @Override
    public Optional<T> findByName(String name) {
        DefaultGuideHeader guideHeader = new DefaultGuideHeader(this.client.fetchGuide(guideType, name));
        GuideContent guideContent = this.client.fetchGuideContent(guideType, name);
        return (Optional<T>) Optional.of(GuideFactory.createGuide(guideType, guideHeader, guideContent));
    }

    @Override
    public GuideHeader[] findByProject(Project project) {
        return Arrays.stream(findAll())
                .filter(guide -> guide.getProjects().contains(project.getId()))
                .toArray(GuideHeader[]::new);
    }
}
