package space.wenliang.ai.aigcplatformserver.service.comfyui;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.comfyui.annotation.TaskProcessListener;
import com.comfyui.annotation.enums.TaskProcessType;
import com.comfyui.common.process.ComfyTaskComplete;
import com.comfyui.common.process.ComfyTaskNodeProgress;
import com.comfyui.common.process.ComfyTaskStart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import space.wenliang.ai.aigcplatformserver.service.business.BDramaInfoService;
import space.wenliang.ai.aigcplatformserver.socket.ImageProjectWebSocketHandler;

/**
 * @author Administrator
 * date   2024/10/27
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TaskMessageHandler {

    private final ImageProjectWebSocketHandler imageProjectWebSocketHandler;
    private final BDramaInfoService bDramaInfoService;

    @TaskProcessListener(TaskProcessType.START)
    private void testListener(ComfyTaskStart comfyTaskStart) {
        JSONObject j1 = new JSONObject();
        j1.put("type", "image_generate_start");
        j1.put("state", "success");
        String taskId = comfyTaskStart.getTaskId();
        String[] split = taskId.split("_");
        j1.put("projectId", split[0]);
        j1.put("chapterId", split[1]);
        j1.put("imgId", split[2]);

        imageProjectWebSocketHandler.sendMessageToProject(split[0], JSON.toJSONString(j1));
        log.info("任务开始, 任务id:{}", split[0]);
    }

    @TaskProcessListener(TaskProcessType.PROGRESS)
    private void testListener3(ComfyTaskNodeProgress progress) {
        log.info("任务进度更新, 任务id: {}, 内部任务id: {}, 当前节点名: {}, 当前进度:{}%", progress.getTaskId(), progress.getComfyTaskId(), progress.getNode().getTitle(), progress.getPercent());
    }

    @TaskProcessListener(TaskProcessType.COMPLETE)
    private void testListener2(ComfyTaskComplete complete) {
        JSONObject j1 = new JSONObject();
        j1.put("type", "image_generate_result");
        j1.put("state", "success");
        String taskId = complete.getTaskId();
        String[] split = taskId.split("_");
        j1.put("projectId", split[0]);
        j1.put("chapterId", split[1]);
        j1.put("imgId", split[2]);

        imageProjectWebSocketHandler.sendMessageToProject(split[0], JSON.toJSONString(j1));
        log.info("任务完成, 任务id: {}, 内部任务id: {}", complete.getTaskId(), complete.getComfyTaskId());
    }
}
