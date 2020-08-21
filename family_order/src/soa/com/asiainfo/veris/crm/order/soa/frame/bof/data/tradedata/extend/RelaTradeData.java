
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class RelaTradeData extends BaseTradeData
{
    private String ctrlType;

    private String orderIdA;

    private String orderIdB;

    private String remark;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvNum4;

    private String rsrvNum5;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String tradeIdA;

    private String tradeIdB;

    private String type;

    public RelaTradeData()
    {

    }

    public RelaTradeData(IData data)
    {
        this.ctrlType = data.getString("CTRL_TYPE");
        this.orderIdA = data.getString("ORDER_ID_A");
        this.orderIdB = data.getString("ORDER_ID_B");
        this.remark = data.getString("REMARK");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvNum4 = data.getString("RSRV_NUM4");
        this.rsrvNum5 = data.getString("RSRV_NUM5");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.tradeIdA = data.getString("TRADE_ID_A");
        this.tradeIdB = data.getString("TRADE_ID_B");
        this.type = data.getString("TYPE");
    }

    public RelaTradeData clone()
    {
        RelaTradeData relaTradeData = new RelaTradeData();
        relaTradeData.setCtrlType(this.getCtrlType());
        relaTradeData.setOrderIdA(this.getOrderIdA());
        relaTradeData.setOrderIdB(this.getOrderIdB());
        relaTradeData.setRemark(this.getRemark());
        relaTradeData.setRsrvDate1(this.getRsrvDate1());
        relaTradeData.setRsrvDate2(this.getRsrvDate2());
        relaTradeData.setRsrvDate3(this.getRsrvDate3());
        relaTradeData.setRsrvNum1(this.getRsrvNum1());
        relaTradeData.setRsrvNum2(this.getRsrvNum2());
        relaTradeData.setRsrvNum3(this.getRsrvNum3());
        relaTradeData.setRsrvNum4(this.getRsrvNum4());
        relaTradeData.setRsrvNum5(this.getRsrvNum5());
        relaTradeData.setRsrvStr1(this.getRsrvStr1());
        relaTradeData.setRsrvStr2(this.getRsrvStr2());
        relaTradeData.setRsrvStr3(this.getRsrvStr3());
        relaTradeData.setRsrvStr4(this.getRsrvStr4());
        relaTradeData.setRsrvStr5(this.getRsrvStr5());
        relaTradeData.setRsrvTag1(this.getRsrvTag1());
        relaTradeData.setRsrvTag2(this.getRsrvTag2());
        relaTradeData.setRsrvTag3(this.getRsrvTag3());
        relaTradeData.setTradeIdA(this.getTradeIdA());
        relaTradeData.setTradeIdB(this.getTradeIdB());
        relaTradeData.setType(this.getType());
        return relaTradeData;
    }

    public String getCtrlType()
    {
        return ctrlType;
    }

    public String getOrderIdA()
    {
        return orderIdA;
    }

    public String getOrderIdB()
    {
        return orderIdB;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRsrvDate1()
    {
        return rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return rsrvDate3;
    }

    public String getRsrvNum1()
    {
        return rsrvNum1;
    }

    public String getRsrvNum2()
    {
        return rsrvNum2;
    }

    public String getRsrvNum3()
    {
        return rsrvNum3;
    }

    public String getRsrvNum4()
    {
        return rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return rsrvNum5;
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

    public String getRsrvTag1()
    {
        return rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return rsrvTag3;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_RELA";
    }

    public String getTradeIdA()
    {
        return tradeIdA;
    }

    public String getTradeIdB()
    {
        return tradeIdB;
    }

    public String getType()
    {
        return type;
    }

    public void setCtrlType(String ctrlType)
    {
        this.ctrlType = ctrlType;
    }

    public void setOrderIdA(String orderIdA)
    {
        this.orderIdA = orderIdA;
    }

    public void setOrderIdB(String orderIdB)
    {
        this.orderIdB = orderIdB;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
    }

    public void setRsrvDate2(String rsrvDate2)
    {
        this.rsrvDate2 = rsrvDate2;
    }

    public void setRsrvDate3(String rsrvDate3)
    {
        this.rsrvDate3 = rsrvDate3;
    }

    public void setRsrvNum1(String rsrvNum1)
    {
        this.rsrvNum1 = rsrvNum1;
    }

    public void setRsrvNum2(String rsrvNum2)
    {
        this.rsrvNum2 = rsrvNum2;
    }

    public void setRsrvNum3(String rsrvNum3)
    {
        this.rsrvNum3 = rsrvNum3;
    }

    public void setRsrvNum4(String rsrvNum4)
    {
        this.rsrvNum4 = rsrvNum4;
    }

    public void setRsrvNum5(String rsrvNum5)
    {
        this.rsrvNum5 = rsrvNum5;
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

    public void setRsrvTag1(String rsrvTag1)
    {
        this.rsrvTag1 = rsrvTag1;
    }

    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public void setRsrvTag3(String rsrvTag3)
    {
        this.rsrvTag3 = rsrvTag3;
    }

    public void setTradeIdA(String tradeIdA)
    {
        this.tradeIdA = tradeIdA;
    }

    public void setTradeIdB(String tradeIdB)
    {
        this.tradeIdB = tradeIdB;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("CTRL_TYPE", this.ctrlType);
        data.put("ORDER_ID_A", this.orderIdA);
        data.put("ORDER_ID_B", this.orderIdB);
        data.put("REMARK", this.remark);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_NUM4", this.rsrvNum4);
        data.put("RSRV_NUM5", this.rsrvNum5);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("TRADE_ID_A", this.tradeIdA);
        data.put("TRADE_ID_B", this.tradeIdB);
        data.put("TYPE", this.type);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
