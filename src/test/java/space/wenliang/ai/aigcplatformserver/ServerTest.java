package space.wenliang.ai.aigcplatformserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import space.wenliang.ai.aigcplatformserver.service.comfyui.ComfyuiTaskService;

/**
 * @author Administrator
 * date   2024/10/27
 */
@SpringBootTest
public class ServerTest {

    @Autowired
    ComfyuiTaskService comfyuiTaskService;

    @Test
    public void test() {
        System.out.println(1);
    }
}
