package com.bohui.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;

/**
 * 记录ID操作的类,它自己来进行同步与文件中的数据;
 * Created by liyangli on 17/1/10.
 */
public class MemoryIDFIle {

    private int id;
    private int INCREASE = 1000;
    private int oldId;
//    private String path = this.getClass().getClassLoader().getResource("com/bohui").getPath();
//    private URL fileURL=this.getClass().getResource("/opt/bohui/vsmExport/ID");
    private String fileName = "/opt/bohui/vsmExport/ID";

    public synchronized void setId(int id){
        this.id = id;
    }

    public  int readId(){
        //读取id
        id = readFile();
        return id;
    }

    private int readFile(){
        int num =0;
        try{

            FileReader reder = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(reder);
            String startID = bufferedReader.readLine();
            num = Integer.parseInt(startID);
        }catch (Exception e){
            System.out.println("读取文件失败:"+e.getMessage());
        }
        return num;
    }



    public void writeId(int id) {
        //写入文件中
        try{
            FileWriter writer = new FileWriter(fileName);
            writer.write(String.valueOf(id));
            writer.flush();
            writer.close();
        }catch (Exception e){
            System.out.println("读取文件失败:"+e.getMessage());
        }
    }
}
