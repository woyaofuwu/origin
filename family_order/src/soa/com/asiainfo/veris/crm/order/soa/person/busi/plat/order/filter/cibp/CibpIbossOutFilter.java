
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.cibp;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;

/**
 * 互联网电视一级boss出参转换
 * 
 * @author xiekl
 */
public class CibpIbossOutFilter implements IFilterOut
{

    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        input.put("OPR_NUMB", input.getString("TRANS_ID", input.getString("OPR_NUMB")));
        input.put("TRANS_ID", input.getString("TRANS_ID", input.getString("OPR_NUMB")));
        input.put("INTF_TRADE_ID", input.getString("TRANS_ID", input.getString("INTF_TRADE_ID")));
        input.put("ID_TYPE", input.getString("ID_TYPE", "01"));
        input.put("ID_VALUE", input.getString("SERIAL_NUMBER"));
        List<AttrTradeData> attrs = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);
        String stbId = input.getString("STB_ID");

        if (StringUtils.isBlank(stbId))
        {
            for (int i = 0; i < attrs.size(); i++)
            {
                if ("STB_ID".equals(attrs.get(i).getAttrCode()) && "0".equals(attrs.get(i).getModifyTag()))
                {
                    stbId = attrs.get(i).getAttrValue();
                }
            }
        }

        input.put("STB_ID", stbId);
        return input;
    }

}
