
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.lcpt;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * 和生活一级boss入参转换
 * 
 * @author zhangbo18
 */
public class LcptIbossInFilter implements IFilterIn
{

	@Override
	public void transferDataInput(IData input) throws Exception
	{
		String reasonCode = input.getString("REASON_CODE", input.getString("ACTIONREASONID", ""));// 1 用户发起行为
        String operCode = input.getString("OPER_CODE");
        /*01-业务开通
		02-业务退订
		03-会员级别变更*/
        if("01".equals(operCode))
		{
			input.put("OPER_CODE", "06");
		}else if ("02".equals(operCode)){
			input.put("OPER_CODE", "07");
		}else if ("03".equals(operCode)){
			input.put("OPER_CODE", "06");
			input.put("RSRV_STR5", "03");
		}else{
			CSAppException.apperr(PlatException.CRM_PLAT_0904);
		}
	}

}
