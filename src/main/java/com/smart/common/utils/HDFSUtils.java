/**
 * Created by licheng on 2017年6月26日.
 */
package com.smart.common.utils;

import com.smart.web.base.Loader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Created by licheng on 2017年6月26日.
 */
public class HDFSUtils {
	public static final String hdfsSplit = "\\u0001";
	private static String filePath;
	private int size;//获取的行数
	private FileSystem fs;

	public HDFSUtils(){
		this(Loader.pro.get("hadoop.nameNode"));
	}

	public HDFSUtils(String filePath){
		//System.setProperty("HADOOP_USER_NAME","tescomm");
		this.filePath = filePath;
		try {
			fs = FileSystem.get(URI.create(filePath),new Configuration());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 下载文件
	 * hdfsPath: hdfs 相对路径
	 * disPath: 磁盘路径
	 * Created by licheng on 2017年6月28日.
	 */
	public void get(String hdfsPath,String diskPath){
		try {
			/*InputStream in = fs.open(new Path(hdfsPath));
			OutputStream out = new FileOutputStream(diskPath);
			IOUtils.copyBytes(in, out, 4096, true);*/
			fs.copyToLocalFile(new Path(hdfsPath), new Path(diskPath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void put(String localPath,String hdfsPath){
		put(localPath,hdfsPath,false);
	}

	/**
	 *
	 * Param:
	 * 		isDel: 上传至hdfs之后，是否删除文件
	 * Return:
	 * Created by licheng on 2017/8/23.
	 */
	public void put(String localPath,String hdfsPath,boolean isDel){
		try {
			if(!fs.exists(new Path(hdfsPath))){//如果不存在目录即创建该目录
				mkdir(hdfsPath);
			}
			File file = new File(localPath);
			String newPath = hdfsPath + file.getName();
			if(fs.exists(new Path(hdfsPath))){
				delete(newPath);
			}
			fs.copyFromLocalFile(isDel,new Path(localPath),new Path(hdfsPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void put(String localPath,String hdfsPath,PutType type){
		File file = new File(localPath);
		File[] files = file.listFiles();
		switch(type){
			case inner:
				for(File f: files){
					put(f.getPath(),hdfsPath);
				}
				break;
			case innerAndDel:
				for(File f: files){
					put(f.getPath(),hdfsPath,true);
				}
				break;

		}
	}

	public void  mkdir(String hdfsPath){
		try {
			fs.mkdirs(new Path(hdfsPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除hdfs上的文件
	 * Param:
	 * Return:
	 * Created by licheng on 2017/8/9.
	 */
	public void delete(String filePath){
		try {
			fs.delete(new Path(filePath),true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Param:
	 * 		hdfsPath: hdfs路径
	 * 		list: 结果写入到list
	 * Return:
	 * Created by licheng on 2017/8/9.
	 */
	public void getDirFileNames(String hdfsPath,List<String> list){
		getDirFileNames(hdfsPath,list,null);
	}

	/**
	 *
	 * Param:
	 * 		hdfsPath: hdfs路径
	 * 		list: 结果写入到list
	 * 		filter: 过滤掉的文件
	 * Return:
	 * Created by licheng on 2017/8/9.
	 */
	public void getDirFileNames(String hdfsPath,List<String> list,List<String> filter){
		FileStatus[] files = null;
		try {
			if(!fs.exists(new Path(hdfsPath))){
				return;
			}
			files = fs.listStatus(new Path(hdfsPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(filter == null){//不需要过滤
			for(FileStatus fs: files){
				String name = fs.getPath().getName();
				list.add(name);
			}
		}else{
			for(FileStatus fs: files){//过滤
				String name = fs.getPath().getName();
				if(!filter.contains(name)){
					list.add(name);
				}
			}
		}

	}

	/**
	 * copy文件，input，output，两个参数只能是文件
	 * 若有同名文件将会被覆盖
	 * Param:
	 * Return:
	 * Created by licheng on 2017/8/17.
	 */
	public void copy(String input,String output){
		try {
			FSDataInputStream in = fs.open(new Path(input));
			FSDataOutputStream out = fs.create(new Path(output));
			byte[] b= new byte[in.available()];
			int hasRead=0;
			while((hasRead=in.read(b))>0){
				out.write(b, 0, hasRead);
			}
			//4:资源的关闭
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将 inputPath目录中的所有文件copy至outputPath目录
	 * Param:
	 * Return:
	 * Created by licheng on 2017/8/17.
	 */
	public void copyDir(String inputPath,String outputPath){
		try {
			FileStatus[] files = fs.listStatus(new Path(inputPath));
			for(FileStatus f: files){
				copy(f.getPath().toString(),outputPath + f.getPath().getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close(){
		try {
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Param:
	 * 		inner: 将本地文件夹里的全部内容上传至hdfs
	 * 		innerAndDel: 将本地文件夹里的全部内容上传至hdfs并且删除上传后的本地文件
	 * Return:
	 * Created by licheng on 2017/8/3.
	 */
	public enum PutType{
		inner,innerAndDel
	}



	public static void main(String[] args) {
//		HDFSUtils hdfs = new HDFSUtils("hdfs://cloud138:8020/");
		//hdfs.get("/user/tescomm/licheng/test/1.txt","D:/temp/");
//		hdfs.put("D:/temp/test.jar","/user/tescomm/licheng/test");

		/*List<String> list = new ArrayList<String>();
		hdfs.getDirFileNames("/user/tescomm/scheduler/",list);*/

	}
}
