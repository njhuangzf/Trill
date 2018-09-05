package com.bird.trill.bean;

public class VideoBean {
    private String thumb;
    private String headPic;
    private String videoUrl;

    public VideoBean() {
        this.thumb = "https://luckybirdpublic.oss-cn-hangzhou.aliyuncs.com/test/img_video_1.png";
        this.videoUrl = "https://luckybirdpublic.oss-cn-hangzhou.aliyuncs.com/test/video_1.mp4";
    }

    public String getThumb() {
        return thumb;
    }

    public String getHeadPic() {
        return headPic;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
