package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;

public class CheckIdentityUtil {
	
	public static boolean checkIdentitySvc4TradeReg(IData data, String serialNumber) throws Exception {
		//配置共用接口是否由积分商城发起
	    IDataset isMallOfScore = CommparaInfoQry.getCommpara("CSM", "2016", "KIND_ID_IS_MALLOFSCORE", "ZZZZ");
	    if (null == isMallOfScore || isMallOfScore.isEmpty()) {
			return true;
		} else {
			boolean isScoreMallSVC = false;
			for (int i = 0; i < isMallOfScore.size(); i++) {
				if (data.getString(isMallOfScore.getData(i).getString("PARA_CODE2", ""), "").equals(isMallOfScore.getData(i).getString("PARA_CODE1", ""))) {
					isScoreMallSVC = true;
					break;
				}
			}
			if (isScoreMallSVC) {
				//IDENT_CODE鉴权
				CheckIdentityUtil.checkIdentitySvc(data, serialNumber);
			}    			
		}
	    
	    return true;	
	}	

	public static boolean checkIdentitySvc(IData data, String serialNumber) throws Exception {
		
		/** 是否支持IDENT_CODE字段 */
		IDataset supportIdentity = CommparaInfoQry.getCommParas("CSM", "2016", "IDENT_CODE_YES", "1", "ZZZZ");
		if (null == supportIdentity || supportIdentity.isEmpty()) {
			//旧版本没有效验IDENT_CODE这个字段，那么就走旧系统，所以直接返回true
			return true;
		}
		
		/** 接口是否需要校验IDENT_CODE字段 */
		IDataset isUsedNewIdentify = CommparaInfoQry.getCommParas("CSM", "2016", "SUPPORT_IDENT_CODE", data.getString("KIND_ID", "") ,"ZZZZ");
		if (null == isUsedNewIdentify || isUsedNewIdentify.isEmpty()) {
			//只有在配置中的服务接口，才需要鉴权认证。
			return true;
		}
		
		/** 校验IDENT_CODE字段必传 */
		if ("1".equals(isUsedNewIdentify.getData(0).getString("PARA_CODE2"))) {
			IDataUtil.chkParam(data, "IDENT_CODE");
		}
		
		/** 校验IDENT_CODE字段是否存在  */
		String IDENT_CODE = data.getString("IDENT_CODE");
		if (null == IDENT_CODE || "".equals(IDENT_CODE.trim())) {
			return true;
		}
		
		/** 校验客户凭证 */
		IDataset dataset = UserIdentInfoQry.checkIdent(IDENT_CODE, data.getString("BUSINESS_CODE",""), data.getString("IDENT_CODE_TYPE",""), data.getString("IDENT_CODE_LEVEL",""), serialNumber);
		if (IDataUtil.isEmpty(dataset)) {
			CSAppException.apperr(CrmUserException.CRM_USER_938);
		}
		
		return true;
	}
	
}
