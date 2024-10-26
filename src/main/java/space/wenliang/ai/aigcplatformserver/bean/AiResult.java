package space.wenliang.ai.aigcplatformserver.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiResult {

    private List<Role> roles = new ArrayList<>();

    private List<LinesMapping> linesMappings = new ArrayList<>();

    private List<DramaRole> dramaRoles = new ArrayList<>();

    private List<DramaLinesRole> dramaLinesRoles = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Role {
        private String role;
        private String gender;
        private String age;

        public Role(String role) {
            this.role = role;
            this.gender = "未知";
            this.age = "未知";
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinesMapping {
        private String linesIndex;
        private String role;
        private String mood;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DramaRole {
        private String role;
        private String imagePrompt;

        public DramaRole(String role) {
            this.role = role;
            this.imagePrompt = "";
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DramaLinesRole {
        private String linesIndex;
        private String role;
    }
}
