package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.noPhoneStartTopSetBox.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class NoPhoneStartTopSetBoxRegSVC extends OrderService{

	public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", "4902");
    }

    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "4902");
    }
}
