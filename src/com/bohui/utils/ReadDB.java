package com.bohui.utils;

import com.bohui.Customer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 开始读取对应数据库数据;数据库信息来源于配置
 * Created by liyangli on 17/1/10.
 */
public class ReadDB {
    private String driverClassName;
    private String jdbcUrl;
    private String userName ;
    private String password;
    private String jdbcDriver;
    private String queryTable;
    private String queryColum;

    private PropUtil propUtil;

    Connection con = null;// 创建一个数据库连接
    PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
    ResultSet result = null;// 创建一个结果集对象



    public ReadDB() {
        propUtil = new PropUtil();
        this.userName = propUtil.findVal("userName");
        this.driverClassName = propUtil.findVal("driverClassName");
        this.jdbcUrl = propUtil.findVal("jdbcUrl");
        this.password = propUtil.findVal("password");
        this.jdbcDriver = propUtil.findVal("jdbcDriver");
        this.queryTable = propUtil.findVal("queryTable");
        this.queryColum = propUtil.findVal("queryColum");


        this.init();
    }



    /**
     * 初始化数据库连接
     */
    private void init(){

        try
        {
            Class.forName(driverClassName);// 加载Oracle驱动程序
            con = DriverManager.getConnection(this.jdbcUrl, this.userName, this.password);// 获取连接
            System.out.println("连接成功！");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定大于开始ID的数据量有多少.
     * @return
     */
    public int total(int beginID){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sql = "select count(ID) from T_CUSTOMER where  ID>"+beginID +" and starttime < to_date('"+sdf.format(date)+"','yyyy-mm-dd')";
        int total = 0;
        try{
            pre = con.prepareStatement(sql);// 实例化预编译语句
            result = pre.executeQuery();// 执行查询，注意括号中不需要再加参数
            while (result.next()){
                total = result.getInt(1);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return total;
    }

    public List findObj(int start, int end){
        List<Customer> list = new ArrayList();
        Long startM = System.currentTimeMillis();
        try{
            String sql = "select ID,SN,startTime,endTime,vodtype,assetid,vodResult,groupCode from T_CUSTOMER where  ID>="+start+" and id<= "+end+" order by id asc";
            System.out.println("当前线程名称->"+Thread.currentThread().getName()+",sql->"+sql);
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
                list.add(new Customer(id,sn,startTime,endTime,vodType,assetId,vodResult,groupCode));
            }
            System.out.println("当前线程名称->"+Thread.currentThread().getName()+",sql->"+sql+";list.size->"+list.size());
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (result != null){
                    result.close();
                }
                if (pre != null){

                    pre.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        System.out.println("执行查询数据库消耗的时间:"+(System.currentTimeMillis()-startM));
        return list;
    }

    public void close(){
        try
        {
            // 逐一将上面的几个对象关闭，因为不关闭的话会影响性能、并且占用资源
            // 注意关闭的顺序，最后使用的最先关闭
            if (result != null){
                result.close();
            }
            if (pre != null){

                pre.close();
            }
            if (con != null){
                con.close();
            }
            System.out.println("数据库连接已关闭！");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
