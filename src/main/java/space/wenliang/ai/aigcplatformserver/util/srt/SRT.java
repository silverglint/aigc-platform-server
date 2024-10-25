package space.wenliang.ai.aigcplatformserver.util.srt;

public class SRT {
    private Integer id;
    private Integer beginTime;
    private Integer endTime;
    private String srtBody;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Integer beginTime) {
        this.beginTime = beginTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public String getSrtBody() {
        return srtBody;
    }

    public void setSrtBody(String srtBody) {
        this.srtBody = srtBody;
    }

    @Override
    public String toString() {
        return "SRT{" +
                "id=" + id +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", srtBody='" + srtBody + '\'' +
                '}';
    }
}
