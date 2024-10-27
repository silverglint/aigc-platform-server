package space.wenliang.ai.aigcplatformserver.service.comfyui;

import com.comfyui.common.entity.ComfyWorkFlow;
import com.comfyui.queue.common.DrawingTaskInfo;
import com.comfyui.queue.common.IDrawingTaskSubmit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import space.wenliang.ai.aigcplatformserver.bean.comfyui.FluxBaseParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public void submitFluxBaseTask(FluxBaseParam param) {
        ComfyWorkFlow flow = getFlow("flux-base.json");
        assert flow != null;
        flow.getNode("6").getInputs().put("seed", param.getSeed());
        flow.getNode("336").getInputs().put("text", param.getPrompt());
        flow.getNode("262").getInputs().put("output_path", param.getOutputPath());
        flow.getNode("262").getInputs().put("filename_prefix", param.getImgId());
        flow.getNode("328").getInputs().put("batch_size", param.getLatentBatchSize());
        // 提交任务
        taskSubmit.submit(new DrawingTaskInfo(param.getTaskId(), flow, 5, TimeUnit.MINUTES));
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
