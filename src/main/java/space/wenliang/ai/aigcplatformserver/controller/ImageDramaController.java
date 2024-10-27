package space.wenliang.ai.aigcplatformserver.controller;

import cn.hutool.core.io.IoUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import space.wenliang.ai.aigcplatformserver.bean.ChapterExpose;
import space.wenliang.ai.aigcplatformserver.bean.ControlsUpdate;
import space.wenliang.ai.aigcplatformserver.bean.DramaAdd;
import space.wenliang.ai.aigcplatformserver.bean.DramaSummary;
import space.wenliang.ai.aigcplatformserver.bean.ProjectQuery;
import space.wenliang.ai.aigcplatformserver.bean.Subtitle;
import space.wenliang.ai.aigcplatformserver.bean.TextRoleChange;
import space.wenliang.ai.aigcplatformserver.bean.UpdateModelInfo;
import space.wenliang.ai.aigcplatformserver.common.Page;
import space.wenliang.ai.aigcplatformserver.common.Result;
import space.wenliang.ai.aigcplatformserver.config.EnvConfig;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoEntity;
import space.wenliang.ai.aigcplatformserver.entity.DramaInfoInferenceEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageCommonRoleEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageDramaEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageProjectEntity;
import space.wenliang.ai.aigcplatformserver.entity.ImageRoleEntity;
import space.wenliang.ai.aigcplatformserver.service.DramaInfoService;
import space.wenliang.ai.aigcplatformserver.service.ImageDramaService;
import space.wenliang.ai.aigcplatformserver.service.ImageProjectService;
import space.wenliang.ai.aigcplatformserver.service.business.BDramaInfoService;
import space.wenliang.ai.aigcplatformserver.service.business.BImageDramaService;
import space.wenliang.ai.aigcplatformserver.service.cache.GlobalSettingService;
import space.wenliang.ai.aigcplatformserver.spring.annotation.SingleValueParam;
import space.wenliang.ai.aigcplatformserver.util.FileUtils;
import space.wenliang.ai.aigcplatformserver.util.SubtitleUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("imageDrama")
@RequiredArgsConstructor
public class ImageDramaController {

    private final EnvConfig envConfig;
    private final ImageDramaService imageDramaService;
    private final ImageProjectService imageProjectService;
    private final DramaInfoService dramaInfoService;
    private final BImageDramaService bImageDramaService;
    private final BDramaInfoService bDramaInfoService;
    private final GlobalSettingService globalSettingService;

    @PostMapping("pageChapters")
    public Result<Object> pageChapters(@RequestBody ProjectQuery projectQuery) {
        Page<ImageDramaEntity> page = bImageDramaService.pageChapters(projectQuery);
        return Result.success(page);
    }

    @PostMapping("chapters4Sort")
    public Result<List<ImageDramaEntity>> chapters4Sort(@SingleValueParam("projectId") String projectId) {
        List<ImageDramaEntity> list = bImageDramaService.chapters4Sort(projectId);
        return Result.success(list);
    }

    @PostMapping(value = "deleteChapter")
    public Result<Object> deleteChapter(@RequestBody ImageDramaEntity textChapter) throws IOException {
        bImageDramaService.deleteChapter(textChapter);
        return Result.success();
    }

    @PostMapping("getTextChapter")
    public Result<Object> getContent(@SingleValueParam("projectId") String projectId,
                                     @SingleValueParam("chapterId") String chapterId) {
        ImageDramaEntity textChapter = imageDramaService.getImageDramaAndContent(projectId, chapterId);
        if (Objects.nonNull(textChapter)) {
            Map<String, DramaSummary> chapterSummaryMap = dramaInfoService.chapterSummaryMap();
            DramaSummary chapterSummary = chapterSummaryMap.get(chapterId);
            if (Objects.nonNull(chapterSummary)) {
                textChapter.setImageTaskState(chapterSummary.getMaxTaskState());
            }
        }
        return Result.success(textChapter);
    }


    @PostMapping("chapterEdit")
    public Result<Object> chapterEdit(@RequestBody ImageDramaEntity textChapter) {
        bImageDramaService.chapterEdit(textChapter);
        return Result.success();
    }

    @SneakyThrows
    @PostMapping("chapterAdd")
    public Result<Object> chapterAdd(@RequestPart("chapterAdd") DramaAdd chapterAdd,
                                     @RequestPart(value = "file") MultipartFile file) {
        String content = IoUtil.read(file.getInputStream(), StandardCharsets.UTF_8);
        chapterAdd.getImageDrama().setContent(content);
        bImageDramaService.chapterAdd(chapterAdd);
        return Result.success();
    }

    @PostMapping("chapterSort")
    public Result<Object> chapterSort(@RequestBody List<ImageDramaEntity> sortChapters) {
        bImageDramaService.chapterSort(sortChapters);
        return Result.success();
    }

    @PostMapping("chapterInfos")
    public Result<Object> chapterInfos(@SingleValueParam("projectId") String projectId,
                                       @SingleValueParam("chapterId") String chapterId) {
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(chapterId)) {
            return Result.success(new ArrayList<>());
        }
        List<DramaInfoEntity> list = bDramaInfoService.chapterInfos(projectId, chapterId);
        return Result.success(list);
    }

    @PostMapping("queryDramaInfo")
    public Result<Object> queryDramaInfo(@RequestBody DramaInfoEntity dramaInfo) {
        DramaInfoEntity info = bDramaInfoService.queryDramaInfo(dramaInfo.getId());
        return Result.success(info);
    }

    @PostMapping("chapterInfoSort")
    public Result<Object> chapterInfoSort(@RequestBody List<DramaInfoEntity> chapterInfoEntities) {
        bDramaInfoService.chapterInfoSort(chapterInfoEntities);
        return Result.success();
    }

    @PostMapping("roles")
    public Result<Object> roles(@SingleValueParam("chapterId") String chapterId) {
        List<ImageRoleEntity> list = bImageDramaService.roles(chapterId);
        return Result.success(list);
    }

    @PostMapping("updateRole")
    public Result<Object> updateRole(@RequestBody ImageRoleEntity textRoleEntity) {
        bImageDramaService.updateRole(textRoleEntity);
        return Result.success();
    }

    @PostMapping("updateRoleModel")
    public Result<Object> updateRoleModel(@RequestBody UpdateModelInfo updateModelInfo) {
        bImageDramaService.updateRoleModel(updateModelInfo);
        return Result.success();
    }

    @PostMapping("roleCombine")
    public Result<Object> roleCombine(@SingleValueParam("projectId") String projectId,
                                      @SingleValueParam("chapterId") String chapterId,
                                      @SingleValueParam("fromRoleName") String fromRoleName,
                                      @SingleValueParam("toRoleName") String toRoleName) {
        bImageDramaService.roleCombine(projectId, chapterId, fromRoleName, toRoleName);
        return Result.success();
    }

    @PostMapping("textRoleChange")
    public Result<Object> textRoleChange(@RequestBody TextRoleChange textRoleChange) {
        bImageDramaService.textRoleChange(textRoleChange);
        return Result.success();
    }

    @PostMapping("saveToCommonRole")
    public Result<Object> saveToCommonRole(@RequestBody ImageRoleEntity textRoleEntity) {
        Boolean result = bImageDramaService.saveToCommonRole(textRoleEntity);
        return Result.success(result);
    }

    @PostMapping("commonRoles")
    public Result<Object> commonRoles(@SingleValueParam("projectId") String projectId) {
        List<ImageCommonRoleEntity> list = bImageDramaService.commonRoles(projectId);
        return Result.success(list);
    }

    @PostMapping("createCommonRole")
    public Result<Object> createCommonRole(@RequestBody ImageCommonRoleEntity textCommonRoleEntity) {
        bImageDramaService.createCommonRole(textCommonRoleEntity);
        return Result.success();
    }

    @PostMapping("updateCommonRole")
    public Result<Object> updateCommonRole(@RequestBody ImageCommonRoleEntity updateModelInfo) {
        bImageDramaService.updateCommonRole(updateModelInfo);
        return Result.success();
    }

    @PostMapping("deleteCommonRole")
    public Result<Object> deleteCommonRole(@RequestBody ImageCommonRoleEntity textCommonRoleEntity) {
        bImageDramaService.deleteCommonRole(textCommonRoleEntity);
        return Result.success();
    }

    @PostMapping("deleteRole")
    public Result<Object> deleteRole(@RequestBody ImageRoleEntity role) {
        bImageDramaService.deleteRole(role);
        return Result.success();
    }

    @PostMapping("queryRoleInferenceCache")
    public Result<Object> queryRoleInferenceCache(@SingleValueParam("projectId") String projectId,
                                                  @SingleValueParam("chapterId") String chapterId) {
        return Result.success(bImageDramaService.queryRoleInferenceCache(projectId, chapterId));
    }

    @PostMapping(value = "audioModelChange")
    public Result<Object> audioModelChange(@RequestBody UpdateModelInfo updateModelInfo) {
        bDramaInfoService.audioModelChange(updateModelInfo);
        return Result.success();
    }

    @PostMapping(value = "updateControls")
    public Result<Object> updateControls(@RequestBody ControlsUpdate controlsUpdate) {
        bDramaInfoService.updateControls(controlsUpdate);
        return Result.success();
    }

    @PostMapping(value = "deleteChapterInfo")
    public Result<Object> deleteChapterInfo(@RequestBody DramaInfoEntity chapterInfoEntity) {
        bDramaInfoService.deleteChapterInfo(chapterInfoEntity);
        return Result.success();
    }

    @PostMapping(value = "updateDramaInfo")
    public Result<Object> updateDramaInfo(@RequestBody DramaInfoEntity chapterInfoEntity) {
        bDramaInfoService.updateDramaInfo(chapterInfoEntity);
        return Result.success();
    }

    @PostMapping(value = "saveDramaInfoInference")
    public Result<Object> saveDramaInfoInference(@RequestBody DramaInfoInferenceEntity dramaInfoInference) {
        bDramaInfoService.saveDramaInfoInference(dramaInfoInference);
        return Result.success();
    }

    @PostMapping(value = "deleteDramaInfoInference")
    public Result<Object> deleteDramaInfoInference(@RequestBody DramaInfoInferenceEntity dramaInfoInference) {
        bDramaInfoService.deleteDramaInfoInference(dramaInfoInference);
        return Result.success();
    }

    @PostMapping(value = "createAudio")
    public Result<Object> createAudio(@RequestBody DramaInfoEntity chapterInfoEntity) {
        bDramaInfoService.addImageCreateTask(chapterInfoEntity);
        return Result.success();
    }

    @PostMapping(value = "startCreateImage")
    public Result<Object> startCreateImage(@SingleValueParam("projectId") String projectId,
                                           @SingleValueParam("chapterId") String chapterId,
                                           @SingleValueParam("actionType") String actionType,
                                           @SingleValueParam("chapterInfoIds") List<Integer> chapterInfoIds) {
        bDramaInfoService.startCreateImage(projectId, chapterId, actionType, chapterInfoIds);
        return Result.success();
    }

    @PostMapping(value = "stopCreateAudio")
    public Result<Object> stopCreateAudio() {
        bDramaInfoService.stopCreateAudio();
        return Result.success();
    }

    @PostMapping(value = "chapterExpose")
    public Result<Object> chapterExpose(@RequestBody ChapterExpose chapterExpose) throws Exception {
//        bImageDramaService.chapterExpose(chapterExpose);
        return Result.success();
    }

    @PostMapping(value = "addChapterInfo")
    public Result<Object> addChapterInfo(@RequestBody DramaInfoEntity chapterInfo) {
        DramaInfoEntity chapterInfoEntity = bDramaInfoService.addChapterInfo(chapterInfo);
        return Result.success(chapterInfoEntity);
    }


    @PostMapping(value = "chapterCondition")
    public Result<Object> chapterCondition(@SingleValueParam("projectId") String projectId,
                                           @SingleValueParam("chapterId") String chapterId) {
        List<DramaInfoEntity> chapterInfos = bDramaInfoService.chapterCondition(projectId, chapterId);
        return Result.success(chapterInfos);
    }

    @PostMapping(value = "getChapterAudio")
    public ResponseEntity<byte[]> getChapterAudio(@SingleValueParam("projectId") String projectId,
                                                  @SingleValueParam("chapterId") String chapterId) throws IOException {

        ImageProjectEntity textProject = imageProjectService.getByProjectId(projectId);
        ImageDramaEntity textChapter = imageDramaService.getByChapterId(chapterId);

        Path audioPath = envConfig.buildProjectPath(
                "text",
                FileUtils.fileNameFormat(textProject.getProjectName()),
                FileUtils.fileNameFormat(textChapter.getChapterName()),
                "output.wav");

        if (Files.exists(audioPath)) {
            return ResponseEntity.ok().body(Files.readAllBytes(audioPath));
        }

        return ResponseEntity.ok().body(null);
    }

    @PostMapping(value = "getChapterSubtitle")
    public Result<Object> getChapterSubtitle(@SingleValueParam("projectId") String projectId,
                                             @SingleValueParam("chapterId") String chapterId) throws IOException {

        ImageProjectEntity textProject = imageProjectService.getByProjectId(projectId);
        ImageDramaEntity textChapter = imageDramaService.getByChapterId(chapterId);

        Path srtPath = envConfig.buildProjectPath(
                "text",
                FileUtils.fileNameFormat(textProject.getProjectName()),
                FileUtils.fileNameFormat(textChapter.getChapterName()),
                "output.srt");

        List<Subtitle> subtitles = new ArrayList<>();
        if (Files.exists(srtPath)) {
            subtitles = SubtitleUtils.readSrtFile(srtPath);
        }

        return Result.success(subtitles);
    }
}
