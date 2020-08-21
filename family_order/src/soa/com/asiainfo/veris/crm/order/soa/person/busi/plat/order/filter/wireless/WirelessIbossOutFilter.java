
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.wireless;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * 无线音乐 一级boss出参转换
 * 
 * @author xiekl
 */
public class WirelessIbossOutFilter implements IFilterOut
{

    private String transBrand(String brandCode)
    {

        if ("G002".equals(brandCode) || "G003".equals(brandCode))
        {
            return "1";
        }

        if ("G001".equals(brandCode))
        {
            return "0";
        }

        if ("G010".equals(brandCode))
        {
            return "2";
        }

        return "3";
    }

    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        input.put("IDVALUE", input.getString("SERIAL_NUMBER"));
        input.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        input.put("INTF_TRADE_ID", input.getString("INTF_TRADE_ID"));
        input.put("TRADE_ID", btd.getTradeId());
        input.put("BRAND_CODE", transBrand(uca.getBrandCode()));
        String crbt = "0";
        List<SvcTradeData> svcList = uca.getUserSvcBySvcId("20");
        if (svcList == null || svcList.isEmpty())
        {
            crbt = "1";
        }
        input.put("CRBT_USER_TAG", crbt);
        String memberLev = input.getString("MEMBER_LEVEL");
        input.put("START_DATE", input.getString("START_DATE"));
        input.put("RESULT", "00");
        input.put("HIGHEST_LEVEL", memberLev);
        if (StringUtils.isEmpty(memberLev))
        {
            input.put("MemberLv", memberLev);
        }

        return input;
    }

}
