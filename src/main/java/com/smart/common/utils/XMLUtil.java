package com.smart.common.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by licheng on 2017/8/21.
 */
public class XMLUtil<T> {
    private Document doc;
    private String xmlPath = null;
    public XMLUtil(String xmlPath){
        this.xmlPath = xmlPath;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new File(xmlPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Element createElement(Element father,String tagName,Map<String,String> attribute){
        Element e = doc.createElement(tagName);
        for(Map.Entry<String,String> attr: attribute.entrySet()){
            e.setAttribute(attr.getKey(),attr.getValue());
        }
        father.appendChild(e);
        return e;
    }

    public void save(){
        TransformerFactory tff = TransformerFactory.newInstance();
        Transformer tf = null;
        try {
            tf = tff.newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(xmlPath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将xml构造为对象
     * Param:
     *       tag: xml标签的name,可连续下钻 eg: (root node),可取出root标签下的node标签下的所有标签，并转为泛型对象。
     *       clazz: 转型后的结果对象
     * Return: 
     * Created by licheng on 2017/8/22.
     */
    public List<T> parseObj(String tag, Class<T> clazz){
        String[] tagArr = tag.split(" ");
        List<T> list = new ArrayList<T>();
        NodeList nodes = null;
        for(String n: tagArr){
            if(nodes == null){
                nodes = doc.getElementsByTagName(n);
            }else{
                Element e = (Element) nodes.item(0);
                nodes = e.getElementsByTagName(n);
            }
        }

        for(int i = 0;i < nodes.getLength();i++){
            NodeList objNode = nodes.item(i).getChildNodes();
            T t = null;
            try {
                t = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Field[] fs = t.getClass().getDeclaredFields();
            for(int j = 0;j < objNode.getLength();j++){
                Node ojbNode = objNode.item(j);
                if(ojbNode.getNodeType() == Node.TEXT_NODE) continue;//DOM规范，空格回车均为一个节点，过滤它们！
                String nodeName = ojbNode.getNodeName().toLowerCase();
                String nodeText = ojbNode.getTextContent();
                for(Field f: fs){
                    if(nodeName.equals(f.getName().toLowerCase())){//node名称和反射的全局变量名称相同
                        f.setAccessible(true);
                        try {
                            f.set(t,nodeText);//为全局变量赋值
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            list.add(t);
        }
        return list;
    }

}
