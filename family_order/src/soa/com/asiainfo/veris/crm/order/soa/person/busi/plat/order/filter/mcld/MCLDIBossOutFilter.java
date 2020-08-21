
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.mcld;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * 彩云平台一级boss输出转换
 * 
 * @author xiekl
 */
public class MCLDIBossOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData result, BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        result.put("OPR_NUMB", result.getString("TRANS_ID"));
        result.put("OPER_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
        String bradCode = uca.getBrandCode();
        if ("G001".equals(bradCode))
        {
            bradCode = "01";
        }
        else if ("G010".indexOf(bradCode) > -1)
        {
            bradCode = "03";
        }
        else if ("G002_G003_G004_G006_G015_G021_G022_G023".indexOf(bradCode) > -1)
        {
            bradCode = "02";
        }
        else
        {
            bradCode = "09";
        }
        result.put("BRAND_CODE", bradCode);
        return result;
    }

}
