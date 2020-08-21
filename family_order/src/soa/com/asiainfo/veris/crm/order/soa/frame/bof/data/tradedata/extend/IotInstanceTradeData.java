
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class IotInstanceTradeData extends BaseTradeData
{

    private String tradeId;

    private String instId;

    private String prodInstId;

    private String instType; // U用户类型 S服务类型 P产品类型

    private String userId;

    private String subsId;

    private String platCode;

    private String createTime;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    public static String INST_TYPE_USER = "U";

    public static String INST_TYPE_PKG = "P";

    public static String INST_TYPE_SVC = "S";

    public String getCreateTime()
    {
        return createTime;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getInstType()
    {
        return instType;
    }

    public String getPlatCode()
    {
        return platCode;
    }

    public String getProdInstId()
    {
        return prodInstId;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return rsrvStr5;
    }

    public String getRsrvStr6()
    {
        return rsrvStr6;
    }

    public String getSubsId()
    {
        return subsId;
    }

    @Override
    public String getTableName()
    {
        return "TF_F_INSTANCE_PF";
    }

    public String getTradeId()
    {
        return tradeId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setInstType(String instType)
    {
        this.instType = instType;
    }

    public void setPlatCode(String platCode)
    {
        this.platCode = platCode;
    }

    public void setProdInstId(String prodInstId)
    {
        this.prodInstId = prodInstId;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
    }

    public void setRsrvStr6(String rsrvStr6)
    {
        this.rsrvStr6 = rsrvStr6;
    }

    public void setSubsId(String subsId)
    {
        this.subsId = subsId;
    }

    public void setTradeId(String tradeId)
    {
        this.tradeId = tradeId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    @Override
    public IData toData()
    {
        IData data = new DataMap();
        data.put("INST_ID", this.instId);
        data.put("PROD_INST_ID", this.prodInstId);
        data.put("INST_TYPE", this.instType);
        data.put("USER_ID", this.userId);
        data.put("SUBS_ID", this.subsId);
        data.put("PLAT_CODE", this.platCode);
        data.put("CREATE_TIME", this.createTime);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("TRADE_ID", this.tradeId);
        return data;
    }

}
