package com.smart.web;

import com.smart.web.base.Loader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBoot {
	public static Logger log = LogManager.getLogger(SpringBoot.class);
	/***
	 * 功能实现:
	 *
	 * Author: Lzy
	 * Date: 2018/6/22 17:46
	 * Param: []
	 * Return: void
	 */
	private static void init(){
		Loader.run();
		PropertyConfigurator.configure(Loader.classpath + "/config/log4j.properties");//加载log4j配置文件
	}
	
	


	public static void main(String[] args){
		init();
		SpringApplication.run(SpringBoot.class,args);
/*		Core core = new Core();
		core.run();//watcher*/
	}
}
