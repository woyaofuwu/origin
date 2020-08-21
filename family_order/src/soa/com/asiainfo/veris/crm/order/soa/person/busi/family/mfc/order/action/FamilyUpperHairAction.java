package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.action;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.MfcCommonUtil;

public class FamilyUpperHairAction implements ITradeAction
{
	/**
	 * 主号停、复机加action调BBOSS上发接口
	 */
	
	public void executeAction(BusiTradeData btd) throws Exception
	{
		String trade_type_code = btd.getRD().getTradeType().getTradeTypeCode();
		
		/**
		 * 通过判断 trade_type_code的取值来确定是停机还是复机
		 */
			String serial_number = btd.getRD().getUca().getSerialNumber();
			String relation_type_code = "MF";
			String nowDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			IDataset uuinfos = MfcCommonUtil.getRelationUusByUserSnRole( serial_number,relation_type_code,"1",null);
					IData indata =new DataMap();
					if(IDataUtil.isNotEmpty(uuinfos)){
						IData ucaInfo = UcaInfoQry.qryUserInfoBySn(serial_number,RouteInfoQry.getEparchyCodeBySn(serial_number));//加上remove_tag 的条件

						if(IDataUtil.isNotEmpty(ucaInfo)){						
							IData inparan =new DataMap();
							IData inparam =new DataMap();
							inparan.put("COMPANY_ID","898" );
							inparan.put("PKG_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS() );
							inparan.put("PKG_SEQ",SeqMgr.getInstId());
							inparam.put("USER_TYPE", "01");
							inparam.put("USER_VALUE", serial_number);
							inparam.put("STATUS_CHG_TIME",nowDate );
							inparam.put("STATUS_OPR_TIME",nowDate );
							inparam.put("OTHER_STATUS","" );
							//cancelMobilePay					
							String stoptype = StaticUtil.getStaticValue("STOP_CALCULATION",trade_type_code);
							String againtype=	StaticUtil.getStaticValue("AGAIN_CALCULATION", trade_type_code);
							if(StringUtils.isNotBlank(stoptype)){
								if("B".equals(ucaInfo.getString("USER_STATE_CODESET")) || "T".equals(ucaInfo.getString("USER_STATE_CODESET")) || "A".equals(ucaInfo.getString("USER_STATE_CODESET")) || "G".equals(ucaInfo.getString("USER_STATE_CODESET"))){
						        	//CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码状态无法办理停机业务");
									return;						        	
								}
								inparam.put("USER_STATUS","02" );
							}else if(StringUtils.isNotBlank(againtype)){//复开机
								if("N".equals(ucaInfo.getString("USER_STATE_CODESET")) || "0".equals(ucaInfo.getString("USER_STATE_CODESET"))){
						        	//CSAppException.apperr(CrmUserException.CRM_USER_783, "该号码状态无法办理开机业务");
						        	return;
								}
								inparam.put("USER_STATUS","97" );							
							}
							IDataset  CHG_USER = new DatasetList();
							CHG_USER.add(inparam);
							inparan.put("CHG_USER",CHG_USER );
							inparan.put("CHG_USER_SUM","1" );
							inparan.put("BIZ_VERSION","1.0.0" );
							CSAppCall.call("SS.FamilyGroupSVC.changStateSendBBoss",inparan);
						}
				
					}
				
	
	}
}
