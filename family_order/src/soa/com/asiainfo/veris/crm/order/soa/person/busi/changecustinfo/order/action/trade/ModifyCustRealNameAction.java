package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;

public class ModifyCustRealNameAction implements ITradeAction{
	
	static Logger logger = Logger.getLogger(ModifyCustRealNameAction.class); 

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		//IDataset tradeOtherInfo = TradeOtherInfoQry.getTradeOtherByTradeId(mainTrade.getString("TRADE_ID"));
		List<OtherTradeData> tradeOtherInfo = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
	    if(tradeOtherInfo!=null&&tradeOtherInfo.size()>0){
	    	for (int i = 0; i < tradeOtherInfo.size(); i++) {
	    		OtherTradeData tradeOther = tradeOtherInfo.get(i);
				if("CHRN".equals(tradeOther.getRsrvValueCode())&&"实名制办理".equals(tradeOther.getRsrvValue())&&"60".equals(tradeOther.getRsrvStr4())){
					
					 UserTradeData userTrade = btd.getRD().getUca().getUser();
					 UserTradeData userTradeData = userTrade.clone();
				    
					 userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
					 userTradeData.setOpenDate(SysDateMgr.getSysTime());
					 userTradeData.setAcctTag("0");
					 userTradeData.setFirstCallTime(SysDateMgr.getSysTime());
					
					 IData tagInfoMode = TagInfoQry.queryTagInfo("CS_CHR_NeedModifyOpenmode");
					 String iv_needmodifyopenmode = "";
					 if (IDataUtil.isEmpty(tagInfoMode) || StringUtils.isEmpty(tagInfoMode.getString("TAG_CHAR")))
					 {
			            iv_needmodifyopenmode = "0";
					 }
					 else
					 {
			            iv_needmodifyopenmode = tagInfoMode.getString("TAG_CHAR");
					 }
					 if("1".equals(iv_needmodifyopenmode)){
						 userTradeData.setOpenMode("0");
					 }
					 
					 IData tagInfoDate = TagInfoQry.queryTagInfo("CS_CHR_NeedModifyIndate");
					 String iv_needmodifyindate = "";
					 if (IDataUtil.isEmpty(tagInfoDate) || StringUtils.isEmpty(tagInfoDate.getString("TAG_CHAR")))
					 {
			            iv_needmodifyindate = "0";
					 }
					 else
					 {
			            iv_needmodifyindate = tagInfoDate.getString("TAG_CHAR");
					 }
					 if("1".equals(iv_needmodifyindate)){
						 userTradeData.setInDate(SysDateMgr.getSysTime());
					 }
					 
					 if("F".equals(userTrade.getUserTypeCode())){
						 userTradeData.setUserTypeCode("0");
					 }
					 userTradeData.setRemark("实名制用户激活");

			         btd.add(btd.getMainTradeData().getSerialNumber(), userTradeData);
			         
//			         System.out.println("进入ModifyCustRealNameAction");
//			         FirstCallTimeBean bean = (FirstCallTimeBean) BeanManager.createBean(FirstCallTimeBean.class);
//			         String iv_cust_name = btd.getMainTradeData().getCustName();
//			 		 String oper_staff_id = CSBizBean.getVisit().getStaffId();
//			         String oper_depart_id = CSBizBean.getVisit().getDepartId();
//			         String iv_cust_id = btd.getMainTradeData().getCustId();
//			         String iv_userid = btd.getMainTradeData().getUserId();
//			         String iv_serial_number = btd.getMainTradeData().getSerialNumber();
//			         String iv_eparchy_code = btd.getRD().getUca().getUser().getEparchyCode();  
//			         String iv_city_code = btd.getRD().getUca().getUser().getCityCode();
//			         String iv_product_id = "-1";
//			         String iv_brand_code = "-1";
//			         IData userMainProductInfo = UcaInfoQry.qryMainProdInfoByUserId(iv_userid);
//			         if (IDataUtil.isNotEmpty(userMainProductInfo))
//			         {
//			        	 iv_product_id = userMainProductInfo.getString("PRODUCT_ID");
//			        	 iv_brand_code = userMainProductInfo.getString("BRAND_CODE");
//			         }
//			         
//			         bean.insTradeH(iv_cust_id, iv_cust_name, iv_userid, iv_serial_number, iv_eparchy_code, iv_city_code, iv_product_id, iv_brand_code, oper_staff_id, oper_depart_id);
//		             bean.insTradeStaffH(iv_cust_id, iv_cust_name, iv_userid, iv_serial_number, iv_eparchy_code, iv_city_code, iv_product_id, iv_brand_code, oper_staff_id, oper_depart_id);
//			         
			         
		    	}
	    	}
		 
	    }
		
	}

}
