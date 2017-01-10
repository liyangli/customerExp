package com.bohui.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * 资源配置读取文件.主要进行读取对应配置文件
 * Created by liyangli on 17/1/10.
 */
public class PropUtil {
    private String filePath = "/opt/bohui/vsmExport/config.properties";
    private Properties prop = new Properties();
    public PropUtil(){
        try {
//            URL fileURL=this.getClass().getResource(fileName);
//            String filePath = fileURL.getFile();
            System.out.println("filePath->"+filePath);
            prop.load(new FileInputStream(filePath));
        } catch (IOException e) {
            System.out.println("读取配置文件出错了,"+e.getMessage());
        }
    }


    public String findVal(String key,String defVal){
        return prop.getProperty(key,defVal);
    }

   public String findVal(String key){
        return prop.getProperty(key);
    }
}
