package space.wenliang.ai.aigcplatformserver.service.comfyui;

import com.comfyui.annotation.TaskProcessListener;
import com.comfyui.annotation.enums.TaskProcessType;
import com.comfyui.common.process.ComfyTaskComplete;
import com.comfyui.common.process.ComfyTaskNodeProgress;
import com.comfyui.common.process.ComfyTaskStart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * date   2024/10/27
 */
@Slf4j
@Component
public class TaskMessageHandler {

    @TaskProcessListener(TaskProcessType.START)
    private void testListener(ComfyTaskStart comfyTaskStart) {
        log.info("任务开始, 任务id:{}", comfyTaskStart.getTaskId());
    }

    @TaskProcessListener(TaskProcessType.PROGRESS)
    private void testListener3(ComfyTaskNodeProgress progress) {
        log.info("任务进度更新, 任务id: {}, 内部任务id: {}, 当前节点名: {}, 当前进度:{}%", progress.getTaskId(), progress.getComfyTaskId(), progress.getNode().getTitle(), progress.getPercent());
    }

    @TaskProcessListener(TaskProcessType.COMPLETE)
    private void testListener2(ComfyTaskComplete complete) {
        log.info("任务完成, 任务id: {}, 内部任务id: {}", complete.getTaskId(), complete.getComfyTaskId());
    }
}
