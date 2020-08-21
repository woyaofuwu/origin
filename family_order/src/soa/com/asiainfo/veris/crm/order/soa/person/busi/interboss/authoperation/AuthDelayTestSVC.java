
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.authoperation;


import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;


public class AuthDelayTestSVC extends CSBizService
{
	public IData authDelay(IData data) throws Exception{
		IData inparam = new DataMap();
		inparam.put("ID_TYPE", "01");
		inparam.put("ID_VALUE", data.getString("ID_VALUE"));
		addOprNumb(inparam);
		inparam.put("IDENT_CODE", data.getString("IDENT_CODE"));
		inparam.put("EFFECTIVE_TIME", data.getString("EFFECTIVE_TIME"));
		inparam.put("BIZ_VERSION", "1.0.0");
		
		//调用IBOSS接口
		IData result = IBossCall.authDelayIBOSS(inparam);
		if(IDataUtil.isNotEmpty(result)){
			if("0000".equals(result.getString("BIZ_ORDER_RESULT"))){
				
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "凭证延时失败"+result.getString("RESULT_DESC"));
			}
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "凭证延时iboss错误");
		}
		return result;
	}
	
	public IData logout(IData data) throws Exception{
		IData inparam = new DataMap();
		inparam.put("ID_TYPE", "01");
		inparam.put("ID_VALUE", data.getString("ID_VALUE"));
		addOprNumb(inparam);
		inparam.put("IDENT_CODE", data.getString("IDENT_CODE"));
		inparam.put("BIZ_VERSION", "1.0.0");
		
		//调用IBOSS接口
		IData result = IBossCall.authLogoutIBOSS(inparam);
		if(IDataUtil.isNotEmpty(result)){
			if("0000".equals(result.getString("BIZ_ORDER_RESULT"))){
				
			}else{
				if("3018".equals(result.getString("BIZ_ORDER_RESULT"))){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "鉴权登出失败"+result.getString("RESULT_DESC"));
				}else{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "鉴权登出失败,归属省未返回明确原因！");
				}
			}
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "鉴权登出iboss错误");
		}
		return result;
	}
	
	public IData addOprNumb(IData inparam) throws Exception
	 {
	     //补充操作流水号
		 String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		 String seqRealId = SeqMgr.getRealId();
		 inparam.put("OPR_NUMB", "BOSS"+"898"+ date + seqRealId);
	     return inparam;
	 }
}
