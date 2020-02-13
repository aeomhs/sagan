package sagan.site.guides;

import sagan.site.renderer.GuideContent;

public class TranslatedGuide extends AbstractGuide {
    private final static String TYPE_LABEL = "(Translated) Getting Started";

    // only used for JSON serialization
    public TranslatedGuide() {
        this.setTypeLabel(TYPE_LABEL);
    }

    public TranslatedGuide(GuideHeader header, GuideContent content) {
        super(TYPE_LABEL, header, content);
    }
}
