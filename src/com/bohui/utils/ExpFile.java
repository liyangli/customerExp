package com.bohui.utils;

import com.bohui.Customer;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liyangli on 17/1/10.
 */
public class ExpFile {
    private File file;
    private PropUtil propUtil;
    public ExpFile() {
        propUtil = new PropUtil();
        String fileName = propUtil.findVal("exportPath","/opt/bohui");
        file = new File(fileName);
        if(!file.exists()){
            file.mkdirs();
        }

    }

    private void writeFile(File file,StringBuilder content){
        try{
            FileWriter writer  = new FileWriter(file,true);
            writer.write(content.toString());
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
                System.out.println("解析到导出文件地址出现了错误:"+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行真正的导出功能
     */
    public void export(List<Customer> list){
        Map<String,Map<String,StringBuilder>> map = new HashMap<>();
        SimpleDateFormat sdfDD = new SimpleDateFormat("yyyy-MM-dd,HH");
        for(Customer customer: list){
            //设定对应导出的文件路径;批量直接写入;写入的频率为每1000条记录时自动写入;
            //首先需要判断是否为当天,如果不是当天需要进行设定具体的新的一天;

            Date startTime = customer.getStartTime();
            String dayHour = sdfDD.format(startTime);
            String[] dayHours = dayHour.split(",");
            String day = dayHours[0];
            String hours = dayHours[1];

            Map<String,StringBuilder> hhMap = map.get(day);
            if(hhMap == null){
                hhMap = new HashMap<>();
                map.put(day,hhMap);
            }

            StringBuilder content = hhMap.get(hours);
            if(content == null){
                content = new StringBuilder();
            }
            //进行判断是否为当小时之内.不是当小时之内就创建一个新的小时范围之内
            content.append(customer.toString()).append("\n");
            hhMap.put(hours,content);
        }

            //需要根据对应内容进行设定对应文件加载了;
            String filePath = file.getAbsolutePath();
            Set<Map.Entry<String,Map<String,StringBuilder>>> set = map.entrySet();
            for(Map.Entry<String,Map<String,StringBuilder>> entry:set){
                String day = entry.getKey();
                File dayFile = new File(filePath+"/"+day+"/");
                if(!dayFile.exists()){
                    if(dayFile.mkdirs()){
                        System.out.println("文件创建成功");
                    }else{
                        System.out.println("文件创建失败!!");
                    }
                }
                Map<String,StringBuilder> hhMap = entry.getValue();
                Set<Map.Entry<String,StringBuilder>> hhSet = hhMap.entrySet();
                for(Map.Entry<String,StringBuilder> hhEntry: hhSet){
                    String hour = hhEntry.getKey();
                    StringBuilder content = hhEntry.getValue();
                    //需要真正写入到文件中了;
                    File file = new File(filePath+"/"+day+"/"+hour+".txt");
                    writeFile(file,content);
                }
            }



    }

    public void close() {

    }

    public void removeFile() {
        //进行删除对应文件
        //删除过时文件
        System.out.println("ExpFile==========Delete");
        String fileSaveDay = propUtil.findVal("fileSaveDay","2");
        Date nowTime = new Date();
        long time = nowTime.getTime()/24/60/60/1000*1000*60*60*24 - Integer.parseInt(fileSaveDay)*24*60*60*1000;

        //删除过时文件
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if(pathname.getName().indexOf("-") >-1){
                        return true;
                    }
                    return false;
                }
            });
            if(files == null){
                return;
            }
            for(File ff:files){
                String name = ff.getName();
                Date fileDate = sdf.parse(name);
                long fileTime = fileDate.getTime();
                System.out.println("name->"+name+";time->"+time+";fileTime->"+fileTime);
                if(fileTime < time){
                    System.out.println("删除的文件为->"+name+";time->"+time+";fileTime->"+fileTime);
                    //递归删除所有文件
                    File[] logFiles = ff.listFiles();
                    for(File logFile:logFiles){
                        logFile.delete();
                    }
                    ff.delete();

                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
