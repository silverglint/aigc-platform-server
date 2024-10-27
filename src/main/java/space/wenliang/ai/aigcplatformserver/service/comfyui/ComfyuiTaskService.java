package space.wenliang.ai.aigcplatformserver.service.comfyui;

import com.comfyui.common.entity.ComfyWorkFlow;
import com.comfyui.common.entity.ComfyWorkFlowNode;
import com.comfyui.queue.common.DrawingTaskInfo;
import com.comfyui.queue.common.IDrawingTaskSubmit;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * date   2024/10/27
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ComfyuiTaskService {

    private final IDrawingTaskSubmit taskSubmit;
    private final ResourceLoader resourceLoader;

    @PostConstruct
    public void testSubmitTask() {
        submitFluxBaseTask("test1");
        submitFluxBaseTask("test2");
        log.info("绘图任务提交完成");
    }

    /**
     * 提交任务
     *
     * @param taskId 自定义任务id
     */
    public void submitFluxBaseTask(String taskId) {
        ComfyWorkFlow flow = getFlow("flux-base.json");
        assert flow != null;
        flow.getNode("6").getInputs().put("seed",Math.abs(new Random().nextInt()));
        flow.getNode("336").getInputs().put("text","一个女孩，在跳舞");
        flow.getNode("262").getInputs().put("output_path","E:\\software\\code\\javaProduct\\aigc-platform-server\\model\\test");
        // 提交任务
        taskSubmit.submit(new DrawingTaskInfo(taskId, flow, 5, TimeUnit.MINUTES));
    }

    /**
     * 获取默认的工作流
     */
    private ComfyWorkFlow getFlow(String resourcePath) {
        //从resources文件夹下读取default.json文件
        Resource resource = resourceLoader.getResource("classpath:comfyui/workflow/" + resourcePath);
        StringBuilder flowStr = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                flowStr.append(line);
            }
        } catch (IOException e) {
            return null;
        }
        return ComfyWorkFlow.of(flowStr.toString());
    }
}
