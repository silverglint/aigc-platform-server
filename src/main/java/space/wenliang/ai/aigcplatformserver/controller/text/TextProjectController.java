package space.wenliang.ai.aigcplatformserver.controller.text;

import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import space.wenliang.ai.aigcplatformserver.bean.text.TextProject;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.exception.BizException;
import space.wenliang.ai.aigcplatformserver.service.PathService;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;
import space.wenliang.ai.aigcplatformserver.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("text/project")
public class TextProjectController {

    private final PathService pathService;

    public TextProjectController(PathService pathService) {
        this.pathService = pathService;
    }

    @PostMapping("create")
    public Result<Object> create(@RequestParam("project") String project,
                                 @RequestParam("file") MultipartFile file) throws IOException {
        Path projectPath = pathService.buildProjectPath("text", project);
        if (Files.exists(projectPath)) {
            throw new BizException("项目已存在");
        }
        Files.createDirectories(projectPath);
        Path originFilePth = Path.of(projectPath.toString(), "config", "原文.txt");
        Files.createDirectories(originFilePth.getParent());
        Files.write(originFilePth, file.getBytes());

        return Result.success();
    }

    @SneakyThrows
    @PostMapping("list")
    public Result<Object> list() {
        Path textProjectPath = pathService.buildProjectPath("text");
        List<TextProject> projects = Files.list(textProjectPath)
                .filter(Files::isDirectory)
                .sorted(Comparator.comparing(path -> {
                    try {
                        return Files.readAttributes(path, BasicFileAttributes.class).lastAccessTime();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .map(path -> {
                    TextProject textProject = new TextProject();
                    textProject.setProject(path.getFileName().toString());
                    try {
                        Path path1 = Path.of(path.toAbsolutePath().toString(), "章节");
                        if (Files.exists(path1)) {
                            int chapterNum = Files.list(path1)
                                    .filter(Files::isDirectory)
                                    .toList().size();
                            textProject.setChapterNum(chapterNum);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return textProject;
                })
                .toList();
        return Result.success(projects);
    }

    @PostMapping("delete")
    public Result<Object> delete(@SingleValueParam("project") String project) throws IOException {
        Path projectPath = pathService.buildProjectPath("text", project);
        if (Files.exists(projectPath) && Files.isDirectory(projectPath)) {
            FileUtils.deleteDirectoryAll(projectPath);
        }

        return Result.success();
    }
}
