package sagan.site.guides;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.support.nav.Navigation;
import sagan.support.nav.Section;

/**
 * Controller that handles requests for translated docs at /guides/translateds.
 *
 * @see GettingStartedGuideController
 */

@Controller
@Navigation(Section.GUIDES)
@RequestMapping("/guides/translateds")
public class TranslatedGuideController {

    private TranslatedGuides translatedGuides;

    @Autowired
    TranslatedGuideController(TranslatedGuides translatedGuides) {
        this.translatedGuides = translatedGuides;
    }

    @GetMapping("/{translated}")
    public String viewTranslated(@PathVariable String translated, Model model) {
        model.addAttribute("guide", this.translatedGuides.findByName(translated).get());
        model.addAttribute("description",
                "this guide is translated");
        return "guides/gs/guide";
    }

    @GetMapping("/{translated}/images/{image:[a-zA-Z0-9._-]+}")
    public ResponseEntity<byte[]> loadImage(@PathVariable String translated, @PathVariable String image) {
        return this.translatedGuides.findByName(translated)
                .flatMap(gs -> gs.getImageContent(image))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
