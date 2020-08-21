
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.mail139;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class MailIbossOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        input.put("ACCEPT_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
        input.put("TRANS_ID", input.getString("TRANS_ID"));
        input.put("OPER_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
        String brandCode = uca.getBrandCode();
        if ("G001".equals(brandCode))
        {
            brandCode = "01";
        }
        else if ("G010".equals(brandCode))
        {
            brandCode = "03";
        }
        else if ("G002_G003_G004_G006_G015_G021_G022_G023".indexOf(brandCode) > -1)
        {
            brandCode = "02";
        }
        else
        {
            brandCode = "09";
        }
        input.put("BRAND_CODE", brandCode);
        input.put("PARA_NUM", "0");
        input.put("PARA_NAME", "");
        input.put("PARA_VALUE", "");
        return input;
    }

}
