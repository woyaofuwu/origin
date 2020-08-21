
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public class PayMoneyData
{
    private String payMoneyCode;

    private String money;

    /**
     * 获取付费金额
     * 
     * @return
     */
    public String getMoney()
    {
        return money;
    }

    /**
     * 获取付费类型 0-现金，见td_s_paymoney
     * 
     * @return
     */
    public String getPayMoneyCode()
    {
        return payMoneyCode;
    }

    /**
     * 设置付费金额
     * 
     * @param money
     */
    public void setMoney(String money)
    {
        this.money = money;
    }

    /**
     * 设置付费类型
     * 
     * @param payMoneyCode
     */
    public void setPayMoneyCode(String payMoneyCode)
    {
        this.payMoneyCode = payMoneyCode;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("PAY_MONEY_CODE", this.payMoneyCode);
        data.put("MONEY", this.money);

        return data;
    }
}
