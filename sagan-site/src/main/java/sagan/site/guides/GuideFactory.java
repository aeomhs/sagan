package sagan.site.guides;

import sagan.site.renderer.GuideContent;
import sagan.site.renderer.GuideType;

public class GuideFactory<T extends Guide> {

    public static Guide createGuide(GuideType guideType, GuideHeader header, GuideContent content) {
        switch (guideType) {
            case GETTING_STARTED:
                return createGettingStartedGuide(header, content);
            case TUTORIAL:
                return createTutorial(header, content);
            case TOPICAL:
                return createTopical(header, content);
        }
        throw new RuntimeException(String.format("Cannot create Guide instance of %s", guideType));
    }

    private static Guide createGettingStartedGuide(GuideHeader header, GuideContent content) {
        return new GettingStartedGuide(header, content);
    }

    private static Guide createTutorial(GuideHeader header, GuideContent content) {
        return new Tutorial(header, content);
    }

    private static Guide createTopical(GuideHeader header, GuideContent content) {
        return new Topical(header, content);
    }
}
