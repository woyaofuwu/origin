
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.cmrb;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * 手机阅读绑定 手机阅读一级boss入参转换
 * 
 * @author xiekl
 */
public class CmrbIbossInFilter implements IFilterIn
{

	@Override
	public void transferDataInput(IData input) throws Exception
	{
		String reasonCode = input.getString("REASON_CODE", input.getString("ACTIONREASONID", ""));// 1：用户发起行为
		String serialNumber = input.getString("SERIAL_NUMBER");
		String operCode = input.getString("OPER_CODE");
        if("21".equals(operCode))  //手机阅读书券充值
		{
			input.put("OPER_CODE", "16");   //局数据配置中充值操作代为16
			input.put("PRE_TYPE", "1");    //手机阅读书券充值业务CRM只做充值鉴权，不需要生成台账
		}
        else
        {
        	/*if (!"1".equals(reasonCode) && !"2".equals(reasonCode))
    		{
    			CSAppException.apperr(PlatException.CRM_PLAT_0969);
    		}*/
        	if (!"1".equals(reasonCode) && !"2".equals(reasonCode))  input.put("ACTIVE_TAG", reasonCode);
        	else input.put("ACTIVE_TAG", "1");
    		
    		String bizTypeCode = input.getString("BIZ_TYPE_CODE");
    		
    		if(PlatConstants.PLAT_CMRB.equals(bizTypeCode))
    		{
    			input.put("SERIAL_NUMBER", serialNumber);
    			input.put("GIFT_SERIAL_NUMBER", serialNumber);
    		}
    		
    		if (PlatConstants.PLAT_CMRB_Bing.equals(bizTypeCode))
    		{
    			String msisdn = input.getString("MSISDN");
    			input.put("IN_CARD_NO", msisdn);// 对于绑定类，则必须填写手持终端号码 MSISDN为手持终端号
    		}
    		 

    		String servType = input.getString("SUBSERVTYPE", "");// 如果是绑定 CMRBinding
    		if ("CMRBinding".equals(servType))
    		{
    			input.put("BIZ_TYPE_CODE", PlatConstants.PLAT_CMRB_Bing);// 对于绑定类，则必须填写手持终端号码 MSISDN为手持终端号
    			String msisdn = input.getString("MSISDN");
    			input.put("IN_CARD_NO", msisdn);// 对于绑定类，则必须填写手持终端号码 MSISDN为手持终端号
    		
    		
    			if (!"0".equals(input.getString("FEETYPE", "")))
    			{
    				CSAppException.apperr(PlatException.CRM_PLAT_74, "绑定/去绑定操作的计费类型错");
    			}
    		}
        }
		
	}

}
