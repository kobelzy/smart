package com.smart.web.modules.bdw.beans;

/**
 * Created by licheng on 2017/11/24.
 */
public class Trans {
    private String watch;//监控目录
    private String bak;//备份目录
    private String hdfs;//hdfs上传目录
    private String error;//错误目录

    public String getWatch() {
        return toPath(watch);
    }

    public void setWatch(String watch) {
        this.watch = watch;
    }

    public String getBak() {
        return toPath(bak);
    }

    public void setBak(String bak) {
        this.bak = bak;
    }

    public String getHdfs() {
        return toPath(hdfs);
    }

    public void setHdfs(String hdfs) {
        this.hdfs = hdfs;
    }

    public String getError() {
        return toPath(error);
    }

    public void setError(String error) {
        this.error = error;
    }

    private String toPath(String path){
        return path.endsWith("/") ? path : path + "/";
    }
}
