package com.smart.web.modules.ynsf2;

import com.smart.common.utils.OozieUtils;
import com.smart.web.base.BaseController;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by licheng on 2017/10/26.
 */
@SpringBootApplication
@RequestMapping("/call")
public class Call extends BaseController{
    @RequestMapping("/oozie")
    @ResponseBody
    public String oozie() {
        HttpServletRequest request = getHttpServletRequest();
        String senceId = request.getParameter("sceneId");//方案名称
        String isautomatic = "1";//手动
        Map map = new HashMap<String,String>();
//        map.put("type",Type);
            map.put("sceneId",senceId);

//        if(Type.equals("1")){
//            //自定义
//        String senceId = request.getParameter("sceneId");
//        }else if(Type.equals("2")){
//            String inputBatch = request.getParameter("inputBatch");
//            map.put("inputBatch",inputBatch);
//        }
       String JobStatus= OozieUtils.run(map);
        return JobStatus;
    }

}
