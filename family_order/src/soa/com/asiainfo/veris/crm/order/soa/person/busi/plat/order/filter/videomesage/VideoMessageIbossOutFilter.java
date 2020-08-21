
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.videomesage;

import net.sf.json.JSONArray;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

/**
 * 视频留言出参转换
 * 
 * @author bobo
 */
public class VideoMessageIbossOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        input.put("IDVALUE", input.getString("SERIAL_NUMBER"));
        if (PlatConstants.OPER_ORDER.equals(input.getString("OPER_CODE")))
        {

            JSONArray infoCodes = JSONArray.fromObject(input.getString("INFO_CODE"));
            JSONArray infoValues = JSONArray.fromObject(input.getString("INFO_VALUE"));
            if (infoCodes != null && infoCodes.size() > 0)
            {
                int size = infoCodes.size();
                for (int i = 0; i < size; i++)
                {
                    if (String.valueOf(infoCodes.get(i)).equals("PASSWD"))
                    {
                        input.put("PASSWD", String.valueOf(infoValues.get(i)));
                    }
                }
            }
        }

        return input;
    }

}
