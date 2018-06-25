package com.smart.web.modules.bdw.watcher;


import com.smart.common.utils.HDFSUtils;
import com.smart.common.utils.XMLUtil;
import com.smart.common.wrapper.SmartFile;
import com.smart.common.wrapper.SmartFile.WatcherInterface;
import com.smart.web.base.Loader;
import com.smart.web.modules.bdw.beans.Trans;

import java.io.File;
import java.util.List;

/**
 * Created by licheng on 2017/11/22.
 */
public class Core {
    public void run(){
        String xmlPath = Loader.classpath + "config/transfer.xml";
        XMLUtil xmlUtil = new XMLUtil<Trans>(xmlPath);
        System.out.println("配置文件：" + xmlPath);
        List<Trans> transList = xmlUtil.parseObj("root trans",Trans.class);
        init(transList);
        for(Trans trans: transList){
            watch(trans);
        }
    }

    /**
     * 首次启动初始化
     * Param: 
     * Return: 
     * Created by licheng on 2017/11/27.
     */
    public void init(List<Trans> transList){
        for(Trans trans: transList){
            //遍历每个trans 监控的所有文件
            File file = new File(trans.getWatch());
            if(!file.exists()){
                throw new RuntimeException("所监控的目录不存在:[" + trans.getWatch() + "]");
            }
            File[] dataFiles = file.listFiles();
            for(File f: dataFiles){
                doFile(f,trans);
            }
            //将剩余文件移入error
            SmartFile sf = new SmartFile(file,new File(trans.getError()));
            sf.copy(true);
        }
    }

    public void watch(final Trans trans){
        SmartFile sf = new SmartFile(trans.getWatch());
        sf.watcher(new WatcherInterface(){
            @Override
            public void onCreate(File file) {
                doFile(file,trans);
            }

            @Override
            public void onDelete(File file) {

            }
        });
    }

    public void doFile(File watchFile,Trans trans){
        String fileName = watchFile.getName();
        if(fileName.toLowerCase().endsWith("ok")){//监控到以ok结尾的文件
            String filePath = watchFile.getPath();
            String sourceFilePath = filePath.replace("ok","csv");
            File sourceFile = new File(sourceFilePath);
            SmartFile sf = new SmartFile(sourceFilePath,trans.getBak());
            sf.copy();//备份源文件
            //上传源文件至hdfs
            HDFSUtils hdfs = new HDFSUtils();
            String firstName = sourceFile.getName().split("_")[0];
            String hdfsPath = trans.getHdfs() + firstName + "/";
            hdfs.put(sourceFilePath,hdfsPath,true);
            //删除ok文件
            watchFile.delete();
        }
    }

    public static void main(String[] args){
        Core core = new Core();
        core.run();

    }
}
