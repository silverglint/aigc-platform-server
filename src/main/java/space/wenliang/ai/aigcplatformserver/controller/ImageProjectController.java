package space.wenliang.ai.aigcplatformserver.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import space.wenliang.ai.aigcplatformserver.bean.ImageProject;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.entity.ImageProjectEntity;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.business.BImageProjectService;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("imageProject")
@RequiredArgsConstructor
public class ImageProjectController {

    private final BImageProjectService bImageProjectService;

    @PostMapping("projectList")
    public Result<Object> projectList() {
        List<ImageProject> list = bImageProjectService.projectList();
        return Result.success(list);
    }

    @PostMapping("getImageProject")
    public Result<Object> getImageProject(@SingleValueParam("projectId") String projectId) {
        ImageProjectEntity textProject = bImageProjectService.getByProjectId(projectId);
        return Result.success(textProject);
    }

    @PostMapping("createProject")
    public Result<Object> createProject(@RequestParam("project") String project,
                                        @RequestParam("projectType") String projectType,
                                        @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        if (StringUtils.equals(projectType, "short_text") && file == null) {
            throw new BizException("短章节必须上传内容");
        }
        StringBuilder content = new StringBuilder();
        if (file != null) {
            Charset charset = FileUtils.detectCharset(file.getBytes());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), charset))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (StringUtils.isNotBlank(line)) {
                        content.append(line.stripLeading()).append("\n");
                    }
                }
            }
        }
        bImageProjectService.createProject(project, projectType, content.toString());
        return Result.success();
    }

    @PostMapping("updateProject")
    public Result<Object> updateProject(@RequestBody ImageProjectEntity textProjectEntity) throws IOException {
        bImageProjectService.updateProject(textProjectEntity);
        return Result.success();
    }


    @PostMapping("deleteProject")
    public Result<Object> deleteProject(@RequestBody ImageProjectEntity textProjectEntity) throws IOException {
        bImageProjectService.deleteProject(textProjectEntity);
        return Result.success();
    }

    @PostMapping("tmpChapterSplit")
    public Result<Object> tmpChapterSplit(@SingleValueParam("projectId") String projectId,
                                          @SingleValueParam("chapterPattern") String chapterPattern,
                                          @SingleValueParam("dialoguePattern") String dialoguePattern) {
        List<String> chapterTitles = bImageProjectService.tmpChapterSplit(projectId, chapterPattern, dialoguePattern);
        return Result.success(chapterTitles);
    }

    @PostMapping("chapterSplit")
    public Result<Object> chapterSplit(@SingleValueParam("projectId") String projectId,
                                       @SingleValueParam("chapterPattern") String chapterPattern,
                                       @SingleValueParam("dialoguePattern") String dialoguePattern) {
        bImageProjectService.chapterSplit(projectId, chapterPattern, dialoguePattern);
        return Result.success();
    }
}
