package com.bohui;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liyangli on 17/1/9.
 */
public class Customer {
    private Long ID;
    private String SN;
    private Date startTime;
    private Date endTime;
    private Integer vodType;
    private String assetId;
    private String vodResult;
    private String groupCode;

    public Customer(Long ID, String SN, Date startTime, Date endTime, Integer vodType, String assetId, String vodResult, String groupCode) {

        this.ID = ID;
        this.SN = SN;
        this.startTime = startTime;
        this.endTime = endTime;
        this.vodType = vodType;
        this.assetId = assetId;
        this.vodResult = vodResult;
        this.groupCode = groupCode;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getVodType() {
        return vodType;
    }

    public void setVodType(Integer vodType) {
        this.vodType = vodType;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getVodResult() {
        return vodResult;
    }

    public void setVodResult(String vodResult) {
        this.vodResult = vodResult;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    @Override
    public String toString() {

        return ID+","+ SN+","+ sdf.format(startTime)+","+sdf.format(endTime)+","+vodType+","+assetId+","+vodResult+","+groupCode;
    }
}
