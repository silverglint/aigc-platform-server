package space.wenliang.ai.aigcplatformserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import space.wenliang.ai.aigcplatformserver.socket.GlobalWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.socket.ImageProjectWebSocketHandler;
import space.wenliang.ai.aigcplatformserver.socket.TextProjectWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GlobalWebSocketHandler globalWebSocketHandler;
    private final TextProjectWebSocketHandler textProjectWebSocketHandler;
    private final ImageProjectWebSocketHandler imageProjectWebSocketHandler;

    public WebSocketConfig(GlobalWebSocketHandler globalWebSocketHandler,
                           TextProjectWebSocketHandler textProjectWebSocketHandler, ImageProjectWebSocketHandler imageProjectWebSocketHandler) {
        this.globalWebSocketHandler = globalWebSocketHandler;
        this.textProjectWebSocketHandler = textProjectWebSocketHandler;
        this.imageProjectWebSocketHandler = imageProjectWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(globalWebSocketHandler, "/ws/global").setAllowedOrigins("*");
        registry.addHandler(textProjectWebSocketHandler, "/ws/text").setAllowedOrigins("*");
        registry.addHandler(imageProjectWebSocketHandler, "/ws/image").setAllowedOrigins("*");
    }
}
