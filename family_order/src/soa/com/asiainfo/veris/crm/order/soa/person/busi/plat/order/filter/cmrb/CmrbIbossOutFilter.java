
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.cmrb;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

/**
 * 手机阅读绑定 手机阅读一级boss出参转换
 * 
 * @author xiekl
 */
public class CmrbIbossOutFilter implements IFilterOut
{

    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        IData result = new DataMap();
        result.putAll(input);
        UcaData uca = btd.getRD().getUca();
        String operCode = input.getString("OPER_CODE");
        if("16".equals(operCode))  //手机阅读书券充值
		{
        	String userId = uca.getUserId();
            //调用账务接口查询用户的余额
            IData owefeeData = AcctCall.getOweFeeByUserId(userId);
            long balance = owefeeData.getLong("ACCT_BALANCE");
            long chargeValue = input.getLong("CHARGE_VALUE");
            if(chargeValue<0)
     		{
            	result.put("X_RSPCODE", "3028");
            	result.put("X_RSPTYPE", "2");
            	result.put("X_RSPDESC", "充值金额非法");
     			result.put("X_RESULTCODE", "3028");
     			result.put("X_RESULTINFO", "充值金额非法");
     		}
     		else
     		{
     			if(balance-chargeValue<0)
     			{ 
     				result.put("X_RSPCODE", "3002");
                	result.put("X_RSPTYPE", "2");
                	result.put("X_RSPDESC", "用户余额不足，不能进行书券充值");
     				result.put("X_RESULTCODE", "3002");
     				result.put("X_RESULTINFO", "用户余额不足，不能进行书券充值");
     			}
     			else
     			{
     				result.put("X_RESULTCODE", "0000");
     				result.put("X_RESULTINFO", "用户余额充足，可以进行书券充值");
     			}
     		}
		}
        else
        {
        	result.put("ACCEPT_DATE", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
            result.put("TRANS_ID", input.getString("TRANS_ID"));
            result.put("OPER_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
            String brandCode = uca.getBrandCode();
            if ("G001".equals(brandCode))
            {
                brandCode = "01";
            }
            else if ("G010".indexOf(brandCode) > -1)
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
            result.put("BRAND_CODE", brandCode);
            result.put("PARA_NUM", "0");
            result.put("PARA_NAME", "");
            result.put("PARA_VALUE", "");
        }
        return result;
    }

}
