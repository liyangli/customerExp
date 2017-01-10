package com.bohui.utils;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库连接池
 * 主要进行设定对应连接池.方便进行操作.不适用是进行关闭所有连接
 * Created by liyangli on 17/1/10.
 */
public class DBConnPool {

    private int maxConn = 5;
    //对应读取数据库连接
    private ArrayBlockingQueue<ReadDB> queue = new ArrayBlockingQueue<ReadDB>(maxConn);



    public DBConnPool(){
        for(int i=0;i<maxConn;i++){
            queue.add(new ReadDB());
        }
    }

    /**
     * 或其中的一个连接对象,如果为空会一直等待;
     * @return
     */
    public ReadDB findReadDB(){
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用完毕进行对应连接释放;
     */
    public void putDB(ReadDB readDB){
       queue.add(readDB);
    }

    /**
     * 整体完成需要统一进行触发对应关闭连接.释放对应资源;
     */
    public void close(){
        for(int i=0;i<maxConn;i++){
            ReadDB readDB = queue.poll();
            readDB.close();
        }
    }

}
