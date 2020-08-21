package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.action;

import org.apache.commons.lang.StringUtils;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.MfcCommonUtil;

public class ActiveSaleUpHairAction implements ITradeAction {
	/**
	 * 主动销户，过户，改号，主号码欠费销户加action调BBOSS上发接口
	 */

	public void executeAction(BusiTradeData btd) throws Exception {
		String trade_type_code = btd.getRD().getTradeType().getTradeTypeCode();
		String serial_number = btd.getRD().getUca().getSerialNumber();
		String sa = "";
		String relationtypecode = "MF";
		IDataset relationUULists = MfcCommonUtil.getRelationUusByUserSnRole(
				serial_number, relationtypecode, "", null);
		if("7240".equals(trade_type_code)){	
			String nowDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			if(IDataUtil.isNotEmpty(relationUULists)){
				IData inparan =new DataMap();
				IData inparam =new DataMap();
				IDataset CHG_USER =new DatasetList();
				
				inparan.put("COMPANY_ID","898" );
				inparan.put("PKG_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
				inparan.put("PKG_SEQ",SeqMgr.getInstId());
				inparam.put("USER_TYPE", "01");
				inparam.put("USER_VALUE", serial_number);
				inparam.put("STATUS_CHG_TIME",nowDate );
				inparam.put("STATUS_OPR_TIME",nowDate );
				inparam.put("OTHER_STATUS","" );
				inparam.put("USER_STATUS","04" );	
				CHG_USER.add(inparam);
				inparan.put("CHG_USER",CHG_USER );
				inparan.put("CHG_USER_SUM","1" );
				inparan.put("BIZ_VERSION","1.0.0" );
				CSAppCall.call("SS.FamilyGroupSVC.changStateSendBBoss",inparan);
			}
		}	 else {
			if(IDataUtil.isNotEmpty(relationUULists)){
				CSAppException.apperr(CrmUserException.CRM_USER_783,
				"该号码存在家庭网，请先注销家庭网再来办理此业务");
			}		
			}	
	}
}