package space.wenliang.ai.aigcplatformserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import space.wenliang.ai.aigcplatformserver.bean.ImagePromptInferenceParam;
import space.wenliang.ai.aigcplatformserver.bean.ImageRoleInferenceParam;
import space.wenliang.ai.aigcplatformserver.bean.RoleInferenceParam;
import space.wenliang.ai.aigcplatformserver.service.business.BImageDramaService;
import space.wenliang.ai.aigcplatformserver.service.business.BTextChapterService;

@RestController
@RequestMapping("sse")
public class SseController {

    private final BTextChapterService bTextChapterService;
    private final BImageDramaService bImageDramaService;

    public SseController(BTextChapterService bTextChapterService, BImageDramaService bImageDramaService) {
        this.bTextChapterService = bTextChapterService;
        this.bImageDramaService = bImageDramaService;
    }

    @PostMapping("textChapter/roleInference")
    public Flux<String> roleInference(@RequestBody RoleInferenceParam roleInferenceParam) {
        return bTextChapterService.roleInference(roleInferenceParam);
    }

    @PostMapping("imageDrama/roleInference")
    public Flux<String> dramaRoleInference(@RequestBody ImageRoleInferenceParam roleInferenceParam) {
        return bImageDramaService.roleInference(roleInferenceParam);
    }

    @PostMapping("imageDrama/promptInference")
    public Flux<String> dramaPromptInference(@RequestBody ImagePromptInferenceParam promptInferenceParam) {
        return bImageDramaService.imagePromptInference(promptInferenceParam);
    }
}
