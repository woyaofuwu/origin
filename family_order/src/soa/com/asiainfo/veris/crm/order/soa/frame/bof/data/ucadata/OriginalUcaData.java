
package com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;

public class OriginalUcaData<K extends BaseTradeData>
{
    private Map<String, List<K>> userDatasMap = new HashMap<String, List<K>>();

    private UserTradeData user;

    private CustomerTradeData customer;

    private CustPersonTradeData custPerson;

    private AccountTradeData account;

    public void addData(K data)
    {
        List<K> userDatas = userDatasMap.get(data.getTableName());
        if (userDatas == null)
        {
            userDatas = new ArrayList<K>();
            userDatasMap.put(data.getTableName(), userDatas);
        }
        userDatas.add(data);
    }

    public AccountTradeData getAccount()
    {
        return account;
    }

    public CustomerTradeData getCustomer()
    {
        return customer;
    }

    public CustPersonTradeData getCustPerson()
    {
        return custPerson;
    }

    public List<K> getData(TradeTableEnum table)
    {
        return userDatasMap.get(table.getValue());
    }

    public UserTradeData getUser()
    {
        return user;
    }

    public void setAccount(AccountTradeData account)
    {
        this.account = account;
    }

    public void setCustomer(CustomerTradeData customer)
    {
        this.customer = customer;
    }

    public void setCustPerson(CustPersonTradeData custPerson)
    {
        this.custPerson = custPerson;
    }

    public void setData(TradeTableEnum table, List<K> datas)
    {
        this.userDatasMap.put(table.getValue(), datas);
    }

    public void setUser(UserTradeData user)
    {
        this.user = user;
    }
}
