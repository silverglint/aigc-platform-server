package space.wenliang.ai.aigcplatformserver.bean;

import lombok.Data;

@Data
public class DramaSummary {
    private String chapterId;
    private Integer wordCount;
    private Integer textCount;
    private Integer dialogueCount;
    private Integer maxTaskState;
}
