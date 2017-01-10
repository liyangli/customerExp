package com.bohui;

import com.bohui.thread.ExpFileTask;
import com.bohui.thread.ReadDBTask;
import com.bohui.utils.DBConnPool;
import com.bohui.utils.MemoryIDFIle;
import com.bohui.utils.PropUtil;
import com.bohui.utils.ReadDB;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * 多线程方式进行操作导出
 * 典型通过生产者消费者模式
 * 生产者:
 * 读取数据库,该处通过多个线程进行处理;通过线程池方式进行管理;
 * 每次获取的数据量通过配置文件进行配置
 * Created by liyangli on 17/1/10.
 */
public class CusExpTask {


    private ExecutorService exec = Executors.newFixedThreadPool(5);

    private MemoryIDFIle memoryIDFIle = new MemoryIDFIle();
    private int INCREASE = 1000;
    private ExpFileTask expFileTask = new ExpFileTask();

    private void exec(){
        //开始执行相关任务
        long start = System.currentTimeMillis();
        DBConnPool dbConnPool = new DBConnPool();
        ReadDB readDB = dbConnPool.findReadDB();
        int startID = memoryIDFIle.readId();
        System.out.println("startID->"+startID);
        int total = readDB.total(startID);
        //使用完成需要进行放回连接池中
        dbConnPool.putDB(readDB);
        memoryIDFIle.writeId(total+startID);
        List<Future> list = new ArrayList<>();
        int svg = total/INCREASE+total%INCREASE;
        System.out.println("total->"+total+",svg->"+svg);
        list.add(exec.submit(expFileTask ));
        for(int i=0;i<svg;i++){
            ReadDBTask readDBTask = new ReadDBTask(dbConnPool, startID + i * INCREASE, startID + (i + 1) * INCREASE);
            final ReadDBTask command = readDBTask;
//            exec.execute(command);
            list.add(exec.submit(command));
        }

        for(Future future: list){
            try {
                future.get();
            } catch (InterruptedException e) {
                System.out.println("线程执行失败:"+e.getMessage());
            } catch (ExecutionException e) {
                System.out.println("执行任务失败:"+e.getMessage());
            }
        }

        System.out.println("执行完成所有的工作消耗时间为->"+(System.currentTimeMillis()-start));
        //所有的执行完毕进行关闭;
        exec.shutdown();
    }
    

    public static void main(String[] args) {
       //开始执行相关任务,通过每天定时进行执行
        CusExpTask expTask = new CusExpTask();
        expTask.exec();
        expTask.removeFile();
    }
    private PropUtil propUtil = new PropUtil();

    private void removeFile() {
        expFileTask.removeFile();

    }
}
