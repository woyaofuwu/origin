
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;

/**
 * @author Administrator
 */
public class ScoreTradeData extends ProductModuleTradeData
{
    private String actionCount;

    private String cancelTag;

    private String endCycleId;

    private String goodsName;

    private String idType;

    private String remark;

    private String resId;

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

    private String ruleId;

    private String score;

    private String scoreChanged;

    private String scoreTag;

    private String scoreTypeCode;

    private String serialNumber;

    private String startCycleId;

    private String valueChanged;

    private String yearId;

    public ScoreTradeData()
    {
        this.elementType = BofConst.ELEMENT_TYPE_CODE_SCORE;
    }

    public ScoreTradeData(IData data)
    {
        this.elementType = BofConst.ELEMENT_TYPE_CODE_SCORE;
        this.actionCount = data.getString("ACTION_COUNT");
        this.cancelTag = data.getString("CANCEL_TAG");
        this.endCycleId = data.getString("END_CYCLE_ID");
        this.goodsName = data.getString("GOODS_NAME");
        this.idType = data.getString("ID_TYPE");
        this.remark = data.getString("REMARK");
        this.resId = data.getString("RES_ID");
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
        this.ruleId = data.getString("RULE_ID");
        this.score = data.getString("SCORE");
        this.scoreChanged = data.getString("SCORE_CHANGED");
        this.scoreTag = data.getString("SCORE_TAG");
        this.scoreTypeCode = data.getString("SCORE_TYPE_CODE");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.startCycleId = data.getString("START_CYCLE_ID");
        this.userId = data.getString("USER_ID");
        this.valueChanged = data.getString("VALUE_CHANGED");
        this.yearId = data.getString("YEAR_ID");
    }

    public ScoreTradeData clone()
    {
        ScoreTradeData scoreTradeData = new ScoreTradeData();
        scoreTradeData.setActionCount(this.getActionCount());
        scoreTradeData.setCancelTag(this.getCancelTag());
        scoreTradeData.setEndCycleId(this.getEndCycleId());
        scoreTradeData.setGoodsName(this.getGoodsName());
        scoreTradeData.setIdType(this.getIdType());
        scoreTradeData.setRemark(this.getRemark());
        scoreTradeData.setResId(this.getResId());
        scoreTradeData.setRsrvStr1(this.getRsrvStr1());
        scoreTradeData.setRsrvStr10(this.getRsrvStr10());
        scoreTradeData.setRsrvStr2(this.getRsrvStr2());
        scoreTradeData.setRsrvStr3(this.getRsrvStr3());
        scoreTradeData.setRsrvStr4(this.getRsrvStr4());
        scoreTradeData.setRsrvStr5(this.getRsrvStr5());
        scoreTradeData.setRsrvStr6(this.getRsrvStr6());
        scoreTradeData.setRsrvStr7(this.getRsrvStr7());
        scoreTradeData.setRsrvStr8(this.getRsrvStr8());
        scoreTradeData.setRsrvStr9(this.getRsrvStr9());
        scoreTradeData.setRuleId(this.getRuleId());
        scoreTradeData.setScore(this.getScore());
        scoreTradeData.setScoreChanged(this.getScoreChanged());
        scoreTradeData.setScoreTag(this.getScoreTag());
        scoreTradeData.setScoreTypeCode(this.getScoreTypeCode());
        scoreTradeData.setSerialNumber(this.getSerialNumber());
        scoreTradeData.setStartCycleId(this.getStartCycleId());
        scoreTradeData.setUserId(this.getUserId());
        scoreTradeData.setValueChanged(this.getValueChanged());
        scoreTradeData.setYearId(this.getYearId());
        return scoreTradeData;
    }

    public String getActionCount()
    {
        return actionCount;
    }

    public String getCancelTag()
    {
        return cancelTag;
    }

    public String getEndCycleId()
    {
        return endCycleId;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public String getIdType()
    {
        return idType;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getResId()
    {
        return resId;
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

    public String getRuleId()
    {
        return ruleId;
    }

    public String getScore()
    {
        return score;
    }

    public String getScoreChanged()
    {
        return scoreChanged;
    }

    public String getScoreTag()
    {
        return scoreTag;
    }

    public String getScoreTypeCode()
    {
        return scoreTypeCode;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getStartCycleId()
    {
        return startCycleId;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_SCORE";
    }

    public String getValueChanged()
    {
        return valueChanged;
    }

    public String getYearId()
    {
        return yearId;
    }

    public void setActionCount(String actionCount)
    {
        this.actionCount = actionCount;
    }

    public void setCancelTag(String cancelTag)
    {
        this.cancelTag = cancelTag;
    }

    public void setEndCycleId(String endCycleId)
    {
        this.endCycleId = endCycleId;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public void setIdType(String idType)
    {
        this.idType = idType;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setResId(String resId)
    {
        this.resId = resId;
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

    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
    }

    public void setScore(String score)
    {
        this.score = score;
    }

    public void setScoreChanged(String scoreChanged)
    {
        this.scoreChanged = scoreChanged;
    }

    public void setScoreTag(String scoreTag)
    {
        this.scoreTag = scoreTag;
    }

    public void setScoreTypeCode(String scoreTypeCode)
    {
        this.scoreTypeCode = scoreTypeCode;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setStartCycleId(String startCycleId)
    {
        this.startCycleId = startCycleId;
    }

    public void setValueChanged(String valueChanged)
    {
        this.valueChanged = valueChanged;
    }

    public void setYearId(String yearId)
    {
        this.yearId = yearId;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("ACTION_COUNT", this.actionCount);
        data.put("CANCEL_TAG", this.cancelTag);
        data.put("END_CYCLE_ID", this.endCycleId);
        data.put("GOODS_NAME", this.goodsName);
        data.put("ID_TYPE", this.idType);
        data.put("REMARK", this.remark);
        data.put("RES_ID", this.resId);
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
        data.put("RULE_ID", this.ruleId);
        data.put("SCORE", this.score);
        data.put("SCORE_CHANGED", this.scoreChanged);
        data.put("SCORE_TAG", this.scoreTag);
        data.put("SCORE_TYPE_CODE", this.scoreTypeCode);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("START_CYCLE_ID", this.startCycleId);
        data.put("USER_ID", this.userId);
        data.put("VALUE_CHANGED", this.valueChanged);
        data.put("YEAR_ID", this.yearId);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
