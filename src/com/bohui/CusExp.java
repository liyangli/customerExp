package com.bohui;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * 用户点播记录进行定时导出数据
 * 主要操作为:
 * 1.定时执行对应任务
 * 2.导出指定表数据
 * 3.导出的数据每次导出为1000条数据.传输的列为指定的列
 * Created by liyangli on 17/1/7.
 */
public class CusExp {


    private  ScheduledExecutorService ses = Executors.newScheduledThreadPool(5);
    private PropUtil propUtil;

    public CusExp() {
        PropUtil propUtil = new PropUtil();
        this.propUtil = propUtil;
        readDB = new ReadDB();
    }

    /**
     * 导出的定时任务
     */
    class ExpTask implements Runnable{

        @Override
        public void run() {
            //开始真正执行对应导出动作.
        }
    }

    /**
     * 资源配置读取文件.主要进行读取对应配置文件
     */
    class PropUtil {
        private String fileName = "config.properties";
        private Properties prop = new Properties();
        public PropUtil(){
            try {
                String path = this.getClass().getClassLoader().getResource("com/bohui").getPath();
                String filePath = path+"/../../"+fileName;
                System.out.println("filePath->"+filePath);
                prop.load(new FileInputStream(filePath));
            } catch (IOException e) {
                System.out.println("读取配置文件出错了,"+e.getMessage());
            }
        }


        String findVal(String key,String defVal){
           return prop.getProperty(key,defVal);
        }

        String findVal(String key){
           return prop.getProperty(key);
        }

    }

    /**
     * 开始读取对应数据库数据;数据库信息来源于配置
     */
    class ReadDB {
        private String driverClassName;
        private String jdbcUrl;
        private String userName ;
        private String password;
        private String jdbcDriver;
        private String queryTable;
        private String queryColum;

        Connection con = null;// 创建一个数据库连接
        PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
        ResultSet result = null;// 创建一个结果集对象

        private String startID = "0";

        public ReadDB() {

            this.userName = propUtil.findVal("userName");
            this.driverClassName = propUtil.findVal("driverClassName");
            this.jdbcUrl = propUtil.findVal("jdbcUrl");
            this.password = propUtil.findVal("password");
            this.jdbcDriver = propUtil.findVal("jdbcDriver");
            this.queryTable = propUtil.findVal("queryTable");
            this.queryColum = propUtil.findVal("queryColum");

            this.init();
            this.readStartID();
        }

        private void readStartID(){
            //读取对应ID文件.进行设定对一个ID
            try{
                String path = this.getClass().getClassLoader().getResource("com/bohui").getPath();
                String fileName = path+"/../../ID";
                FileReader reder = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(reder);
                this.startID = bufferedReader.readLine();
            }catch (Exception e){
                System.out.println("读取文件失败:"+e.getMessage());
            }
        }

        private void writeID(Long ID){
            //读取对应ID文件.进行设定对一个ID
            try{
                String path = this.getClass().getClassLoader().getResource("com/bohui").getPath();
                String fileName = path+"/../../ID";
                FileWriter writer = new FileWriter(fileName);
                writer.write(ID.toString());
                writer.flush();
                writer.close();
            }catch (Exception e){
                System.out.println("读取文件失败:"+e.getMessage());
            }
        }

        /**
         * 初始化数据库连接
         */
        private void init(){

            try
            {
                Class.forName(this.driverClassName);// 加载Oracle驱动程序
                con = DriverManager.getConnection(this.jdbcUrl, this.userName, this.password);// 获取连接
                System.out.println("连接成功！");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public List findObj(){
            List<Customer> list = new ArrayList();
            Long start = System.currentTimeMillis();
            try{
//                "startTime","endTime","vodTime","vodType","assetID","vodResult","groupCode"
                String sql = "select ID,SN,startTime,endTime,vodtype,assetid,vodResult,groupCode from T_CUSTOMER where ID>"+this.startID+" and rownum<= 10000 order by id asc";
                pre = con.prepareStatement(sql);// 实例化预编译语句
                result = pre.executeQuery();// 执行查询，注意括号中不需要再加参数
                while (result.next()){
                    Long id = result.getLong("ID");
                    String sn = result.getString("SN");
                    Date startTime = result.getTimestamp("startTime");
                    Date endTime = result.getTimestamp("endTime");
                    Integer vodType = result.getInt("vodType");
                    String assetId = result.getString("assetid");
                    String vodResult = result.getString("vodResult");
                    String groupCode = result.getString("groupCode");
                    Customer customer = new Customer(id,sn,startTime,endTime,vodType,assetId,vodResult,groupCode);
                    list.add(customer);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            Integer count = list.size();
            if(count == 0){
                return list;
            }
            Long id = list.get(count-1).getID();
            writeID(id);
            System.out.println("执行查询数据库消耗的时间:"+(System.currentTimeMillis()-start));
            return list;
        }

        public void close(){
            try
            {
                // 逐一将上面的几个对象关闭，因为不关闭的话会影响性能、并且占用资源
                // 注意关闭的顺序，最后使用的最先关闭
                if (result != null)
                    result.close();
                if (pre != null)
                    pre.close();
                if (con != null)
                    con.close();
                System.out.println("数据库连接已关闭！");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


    }

    /**
     * 开始导出对应文件
     */
    class ExpFile {

        private File file;
        public ExpFile() {
            String fileName = propUtil.findVal("exportPath","/opt/bohui");
            file = new File(fileName);
            if(!file.exists()){
                file.mkdirs();
            }
        }

        /**
         * 执行真正的导出功能
         */
        public void export(List<Customer> list){
            Map<String,Map<String,StringBuilder>> map = new HashMap<>();
            SimpleDateFormat sdfDD = new SimpleDateFormat("dd,HH");
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

            try {
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
                        FileWriter writer = new FileWriter(file,true);

                        writer.write(content.toString());
                        writer.flush();
                        writer.close();
                    }
                }


            } catch (FileNotFoundException e) {
                System.out.println("解析到导出文件地址出现了错误:"+e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void execTask(){
        //需要开启真正的任务
        ses.scheduleAtFixedRate(new ExpTask(),1,5, TimeUnit.SECONDS);
    }
    private ReadDB readDB ;

    private void readDB(){

        List obj = readDB.findObj();
        List list = obj;
        //开始执行真正的导出功能;
        long start = System.currentTimeMillis();
        ExpFile expFile = new ExpFile();
        //需要记录对一个最大ID值
        expFile.export(list);
        System.out.println("单个执行导出总体消耗时间:"+(System.currentTimeMillis()-start));

        if(list != null && list.size() > 0){
            readDB();
        }

    }


    /**
     * 类入口
     * @param args
     */
    public static void main(String[] args) {

        CusExp ce = new CusExp();
//        ce.execTask();
        long start = System.currentTimeMillis();
        ce.readDB();
        System.out.println("总共消耗时间->"+(System.currentTimeMillis()-start));

        //开始执行删除历史数据
        ce.removeFile();
    }

    private void removeFile() {
        //删除超时文件
    }
}
