
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.req.groupsale;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class GroupExchangeReqData extends GroupReqData
{

    private String exchangeRemark; // 备注

    private String exchangeTotal; // 兑换总数

    private String mustMoney; // 所需金额

    private String mayExchangeMoney; // 可兑金额

    private String balance; // 剩余金额

    private String exchangeBalance; // 可兑余额

    private String custId;

    private String paramCode; // 产品类型

    private String exchangeRate; // 兑奖利率

    private IDataset grpuserPaySeleList; // 用户预存列表

    private IDataset exchangeGoodsSeleList; // 兑奖列表

    public String getBalance()
    {
        return balance;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getExchangeBalance()
    {
        return exchangeBalance;
    }

    public IDataset getExchangeGoodsSeleList()
    {
        return exchangeGoodsSeleList;
    }

    public String getExchangeRate()
    {
        return exchangeRate;
    }

    public String getExchangeRemark()
    {
        return exchangeRemark;
    }

    public String getExchangeTotal()
    {
        return exchangeTotal;
    }

    public IDataset getGrpuserPaySeleList()
    {
        return grpuserPaySeleList;
    }

    public String getMayExchangeMoney()
    {
        return mayExchangeMoney;
    }

    public String getMustMoney()
    {
        return mustMoney;
    }

    public String getParamCode()
    {
        return paramCode;
    }

    public void setBalance(String balance)
    {
        this.balance = balance;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setExchangeBalance(String exchangeBalance)
    {
        this.exchangeBalance = exchangeBalance;
    }

    public void setExchangeGoodsSeleList(IDataset exchangeGoodsSeleList)
    {
        this.exchangeGoodsSeleList = exchangeGoodsSeleList;
    }

    public void setExchangeRate(String exchangeRate)
    {
        this.exchangeRate = exchangeRate;
    }

    public void setExchangeRemark(String exchangeRemark)
    {
        this.exchangeRemark = exchangeRemark;
    }

    public void setExchangeTotal(String exchangeTotal)
    {
        this.exchangeTotal = exchangeTotal;
    }

    public void setGrpuserPaySeleList(IDataset grpuserPaySeleList)
    {
        this.grpuserPaySeleList = grpuserPaySeleList;
    }

    public void setMayExchangeMoney(String mayExchangeMoney)
    {
        this.mayExchangeMoney = mayExchangeMoney;
    }

    public void setMustMoney(String mustMoney)
    {
        this.mustMoney = mustMoney;
    }

    public void setParamCode(String paramCode)
    {
        this.paramCode = paramCode;
    }

}
