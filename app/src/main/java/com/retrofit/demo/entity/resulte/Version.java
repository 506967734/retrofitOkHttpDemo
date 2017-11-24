package com.retrofit.demo.entity.resulte;

/**
 * Created by apple on 2017/8/16.
 */

public class Version {


    /**
     * version : 44
     * url : http://ocv56z6zc.qnssl.com/G_Assistant170718.apk
     * content : 1.来电显示优化2.用户中心头像显示优化
     */

    private String version;
    private String url;
    private String content;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Version{" +
                "version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
