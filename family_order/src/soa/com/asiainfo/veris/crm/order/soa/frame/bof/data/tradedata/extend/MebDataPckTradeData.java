
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class MebDataPckTradeData extends BaseTradeData
{
    private String idType;

    private String id;

    private String relaInstId;

    private String userIdB;

    private String serialNumberB;

    private String datapckType;

    private String datapckValue;

    private String datapckCount;

    private String instId;

    private String startDate;

    private String endDate;

    private String rsrvStr1;

    private String rsrvStr10;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;
    
    private String modifyTag;

    public MebDataPckTradeData()
    {

    }

    public MebDataPckTradeData(IData data)
    {
        this.idType = data.getString("ID_TYPE");
        this.id = data.getString("ID");
        this.relaInstId = data.getString("RELA_INST_ID");
        this.userIdB = data.getString("USER_ID_B");
        this.serialNumberB = data.getString("SERIAL_NUMBER_B");
        this.datapckType = data.getString("DATAPCK_TYPE");
        this.datapckValue = data.getString("DATAPCK_VALUE");
        this.datapckCount = data.getString("DATAPCK_COUNT");
        this.instId = data.getString("INST_ID");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.modifyTag = data.getString("MODIFY_TAG");
    }

    @Override
    public MebDataPckTradeData clone()
    {
    	MebDataPckTradeData mebDataPckTradeData = new MebDataPckTradeData();
    	mebDataPckTradeData.setIdType(this.getIdType());
    	mebDataPckTradeData.setId(this.getId());
    	mebDataPckTradeData.setRelaInstId(this.getRelaInstId());
    	mebDataPckTradeData.setUserIdB(this.getUserIdB());
    	mebDataPckTradeData.setSerialNumberB(this.getSerialNumberB());
    	mebDataPckTradeData.setDatapckType(this.getDatapckType());
    	mebDataPckTradeData.setDatapckValue(this.getDatapckValue());
    	mebDataPckTradeData.setDatapckCount(this.getDatapckCount());
    	mebDataPckTradeData.setInstId(this.getInstId());
    	mebDataPckTradeData.setStartDate(this.getStartDate());
    	mebDataPckTradeData.setEndDate(this.getEndDate());
        mebDataPckTradeData.setRsrvStr1(this.getRsrvStr1());
        mebDataPckTradeData.setRsrvStr10(this.getRsrvStr10());
        mebDataPckTradeData.setRsrvStr2(this.getRsrvStr2());
        mebDataPckTradeData.setRsrvStr3(this.getRsrvStr3());
        mebDataPckTradeData.setRsrvStr4(this.getRsrvStr4());
        mebDataPckTradeData.setRsrvStr5(this.getRsrvStr5());
        mebDataPckTradeData.setRsrvStr6(this.getRsrvStr6());
        mebDataPckTradeData.setRsrvStr7(this.getRsrvStr7());
        mebDataPckTradeData.setRsrvStr8(this.getRsrvStr8());
        mebDataPckTradeData.setRsrvStr9(this.getRsrvStr9());
        mebDataPckTradeData.setModifyTag(this.getModifyTag());
        return mebDataPckTradeData;
    }
    public String getModifyTag()
    {
        return modifyTag;
    }
    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }
    public String getIdType()
    {
        return idType;
    }

    public String getId()
    {
        return id;
    }

    public String getRelaInstId()
    {
        return relaInstId;
    }

    public String getUserIdB()
    {
        return userIdB;
    }

    public String getSerialNumberB()
    {
        return serialNumberB;
    }
    
    public String getDatapckType()
    {
        return datapckType;
    }


    public String getDatapckValue()
    {
        return datapckValue;
    }

    public String getDatapckCount()
    {
        return datapckCount;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

   
    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return rsrvStr10;
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

    public String getRsrvStr7()
    {
        return rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return rsrvStr9;
    }

   

    @Override
    public String getTableName()
    {
        return "TF_B_TRADE_MEB_OUTSTOCK";
    }

    public String setIdType(String idType)
    {
        return this.idType = idType;
    }

    public String setId(String id)
    {
        return this.id = id;
    }

    public String setRelaInstId(String relaInstId)
    {
        return this.relaInstId = relaInstId;
    }

    public String setUserIdB(String userIdB)
    {
        return this.userIdB = userIdB;
    }

    public String setSerialNumberB(String serialNumberB)
    {
        return this.serialNumberB = serialNumberB;
    }
    
    public String setDatapckType(String datapckType)
    {
        return this.datapckType = datapckType;
    }


    public String setDatapckValue(String datapckValue)
    {
        return this.datapckValue = datapckValue;
    }

    public String setDatapckCount(String datapckCount)
    {
        return this.datapckCount = datapckCount;
    }

    public String setInstId(String instId)
    {
        return this.instId = instId;
    }

    public String setStartDate(String startDate)
    {
        return this.startDate = startDate;
    }

    public String setEndDate(String endDate)
    {
        return this.endDate = endDate;
    }


    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr10(String rsrvStr10)
    {
        this.rsrvStr10 = rsrvStr10;
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

    public void setRsrvStr7(String rsrvStr7)
    {
        this.rsrvStr7 = rsrvStr7;
    }

    public void setRsrvStr8(String rsrvStr8)
    {
        this.rsrvStr8 = rsrvStr8;
    }

    public void setRsrvStr9(String rsrvStr9)
    {
        this.rsrvStr9 = rsrvStr9;
    }

  

    @Override
    public IData toData()
    {
        IData data = new DataMap();
        
        data.put("ID_TYPE", this.idType);
        data.put("ID", this.id);
        data.put("USER_ID_B", this.userIdB);
        data.put("SERIAL_NUMBER_B", this.serialNumberB);
        data.put("DATAPCK_TYPE", this.datapckType);
        data.put("DATAPCK_VALUE", this.datapckValue);
        data.put("DATAPCK_COUNT", this.datapckCount);
        data.put("INST_ID", this.instId);
        data.put("RELA_INST_ID", this.relaInstId);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("MODIFY_TAG",this.modifyTag);

        return data;
    }

    @Override
    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
