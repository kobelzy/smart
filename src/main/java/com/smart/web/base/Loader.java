package com.smart.web.base;

import com.smart.common.wrapper.SmartProperties;

/**
 * Created by lzy on 2017/10/30.
 */
public class Loader{
    public static SmartProperties pro;
    public static String classpath;

    public static void run(){
        classpath = Loader.class.getResource("/").getPath();
        System.out.println("classpath:[" + classpath + "]");
        //root = classPath.replaceAll("\\\\", "/") + "WEB-INF/classes/";
        String context = classpath + "config/";
        pro = new SmartProperties();
        for (Enum name : ProNames.values()) {//预加载配置文件
            String properties = context + name.toString() + ".properties";
            pro.load(properties);
            System.out.println("load file:[" + properties + "]");
        }
    }

    private static enum ProNames {
        oozie,jdbc,hadoop,spark
    }
}
