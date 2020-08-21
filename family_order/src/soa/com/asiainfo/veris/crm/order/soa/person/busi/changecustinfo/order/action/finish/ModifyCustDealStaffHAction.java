package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.firstcall.FirstCallTimeBean;

public class ModifyCustDealStaffHAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		 String brand_code = mainTrade.getString("BRAND_CODE");
		 IDataset tradeOtherInfo = TradeOtherInfoQry.getTradeOtherByTradeId(mainTrade.getString("TRADE_ID"));
		 if (IDataUtil.isNotEmpty(tradeOtherInfo)){
			 for (int i = 0; i < tradeOtherInfo.size(); i++) {
				 IData tradeOther = tradeOtherInfo.getData(i);
					if("CHRN".equals(tradeOther.getString("RSRV_VALUE_CODE",""))&&"实名制办理".equals(tradeOther.getString("RSRV_VALUE",""))&&"60".equals(tradeOther.getString("RSRV_STR4"))
							&&("G001".equals(brand_code)||"G002".equals(brand_code)||"G010".equals(brand_code))){
						
						 System.out.println("进入ModifyCustDealStaffHAction");
				         FirstCallTimeBean bean = (FirstCallTimeBean) BeanManager.createBean(FirstCallTimeBean.class);
				         String iv_cust_name =  mainTrade.getString("CUST_NAME");
				 		 String oper_staff_id = mainTrade.getString("TRADE_STAFF_ID");
				         String oper_depart_id = mainTrade.getString("TRADE_DEPART_ID");
				         String iv_cust_id = mainTrade.getString("CUST_ID"); 
				         String iv_userid = mainTrade.getString("USER_ID"); 
				         String iv_serial_number = mainTrade.getString("SERIAL_NUMBER"); 
				         String iv_eparchy_code = mainTrade.getString("EPARCHY_CODE"); 
				         String iv_city_code = mainTrade.getString("CITY_CODE"); 
				         IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(iv_cust_id);
				         if (IDataUtil.isEmpty(custInfo))
				         {
				        	 iv_cust_name = custInfo.getString("CUST_NAME");
				         }
				         IData userInfo = UcaInfoQry.qryUserInfoByUserId(iv_userid);
				         if(IDataUtil.isNotEmpty(userInfo)){
				        	 iv_eparchy_code = userInfo.getString("EPARCHY_CODE");
				        	 iv_city_code = userInfo.getString("CITY_CODE");
				         }
				         String iv_product_id = "-1";
				         String iv_brand_code = "-1";
				         IData userMainProductInfo = UcaInfoQry.qryMainProdInfoByUserId(iv_userid);
				         if (IDataUtil.isNotEmpty(userMainProductInfo))
				         {
				        	 iv_product_id = userMainProductInfo.getString("PRODUCT_ID");
				        	 iv_brand_code = userMainProductInfo.getString("BRAND_CODE");
				         }
				         
				         bean.insTradeH(iv_cust_id, iv_cust_name, iv_userid, iv_serial_number, iv_eparchy_code, iv_city_code, iv_product_id, iv_brand_code, oper_staff_id, oper_depart_id);
				         bean.insTradeStaffH(iv_cust_id, iv_cust_name, iv_userid, iv_serial_number, iv_eparchy_code, iv_city_code, iv_product_id, iv_brand_code, oper_staff_id, oper_depart_id);
				         
				         
			        	 String iv_opendate = SysDateMgr.getSysTime();
			        	 
			        	 int result2 = 0 ;
				         IDataset newProductElements = ProductElementsCache.getProductElements(iv_product_id);
				         if(IDataUtil.isNotEmpty(newProductElements))
				     	 {
				         	for (int j = 0; j < newProductElements.size(); j++)
				     		{
				         		IData ProductElement = newProductElements.getData(j);
				         		String strElementID = ProductElement.getString("ELEMENT_ID", "");
				     			String strElementTypeCode = ProductElement.getString("ELEMENT_TYPE_CODE", "");
				     			String strElementForceTag = ProductElement.getString("ELEMENT_FORCE_TAG", "");
				     			String strGroupForceTag = ProductElement.getString("PACKAGE_FORCE_TAG", "");
				     			//取主产品下的构成必选优惠，或者主产品下必选组的优惠
				 		        if("D".equals(strElementTypeCode) && ("1".equals(strElementForceTag) || "1".equals(strGroupForceTag)))
				 				{
				 		        	IDataset userDiscnts = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(iv_userid);
				 		        	if(IDataUtil.isNotEmpty(userDiscnts))
				 		        	{
				 			        	for (int k = 0; k < userDiscnts.size(); k++) {
				 			        		IData userDiscnt = userDiscnts.getData(k);
				 			        		String discntCode = userDiscnt.getString("DISCNT_CODE", "");
				 			        		if(strElementID.equals(discntCode)){
				 			        			result2 = bean.updUserDiscntStartDate(iv_userid, discntCode,iv_opendate);
				 			        		}
				 						}
				 		        	}
				 				}
				 		        
				 		        
				 		        
				     		}
				     	 }
				         String iv_sync_sequence = SeqMgr.getSyncIncreId();
				         
				         bean.updUserProductStartDate(iv_userid, iv_product_id,iv_opendate);//修改主产品的开始时间
				         bean.updTfFUserInfochangeStartDate(iv_userid, iv_product_id,iv_opendate);//更新用户重要信息异动的开始时间
				         bean.updTiBUserInfochangeStartDate(iv_sync_sequence,iv_userid,iv_product_id);//插入用户重要信息异动给账务
				         
				         if (result2>0)
				         {
				             bean.insTibDiscnt(iv_sync_sequence, iv_userid);
				         }
				         
				         
				         //实名制操作
				         String iv_needmodifyindate = "";
				         String iv_needmodifyopenmode = "";
				         IData tagInfoMode = TagInfoQry.queryTagInfo("CS_CHR_NeedModifyOpenmode");
				         if (IDataUtil.isEmpty(tagInfoMode) || StringUtils.isEmpty(tagInfoMode.getString("TAG_CHAR")))
				         {
				             iv_needmodifyopenmode = "0";
				         }
				         else
				         {
				             iv_needmodifyopenmode = tagInfoMode.getString("TAG_CHAR");
				         }
				         
				         IData tagInfoDate = TagInfoQry.queryTagInfo("CS_CHR_NeedModifyIndate");
				         if (IDataUtil.isEmpty(tagInfoDate) || StringUtils.isEmpty(tagInfoDate.getString("TAG_CHAR")))
				         {
				             iv_needmodifyindate = "0";
				         }
				         else
				         {
				             iv_needmodifyindate = tagInfoDate.getString("TAG_CHAR");
				         }
				         
						 bean.updUserModeDate(iv_opendate, iv_needmodifyopenmode, iv_needmodifyindate, iv_userid);
						 bean.insTibUser(iv_sync_sequence, iv_userid);
				         
						 
				         bean.insTiSync(iv_sync_sequence);
						
					}
			 }
		 }
			 
		
		
		
	}

}
