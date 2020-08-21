package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.person.busi.giveuserclass.GiveUserClassBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGiveClassInfoQry;

public class GiveOrCancelUserClass implements ITradeFinishAction {
	
    private static Logger logger = Logger.getLogger(GiveOrCancelUserClass.class);
	
	@Override
	public void executeAction(IData mainTrade) throws Exception {

		/*
		 * productTrades 产品变更时的参数:
		 * 
		 * [{"USER_ID_A":"-1","PRODUCT_MODE":"00",
		 * "MODIFY_TAG":"0","RSRV_NUM4":null
		 * ,"RSRV_NUM5":null,"USER_ID":"1119070241086997"
		 * ,"END_DATE":"2050-12-31 23:59:59",
		 * "RSRV_NUM1":null,"RSRV_NUM2":null,"OLD_BRAND_CODE"
		 * :"G001","RSRV_NUM3":null,"MAIN_TAG":"1","INST_ID":"1119070219180646",
		 * "OLD_PRODUCT_ID"
		 * :"10007601","BRAND_CODE":"G001","UPDATE_DEPART_ID":"36601"
		 * ,"TRADE_ID":
		 * "1119070213556700","REMARK":null,"ACCEPT_MONTH":"7","START_DATE"
		 * :"2019-07-02 10:34:22",
		 * "UPDATE_TIME":"2019-07-02 10:34:22","PRODUCT_ID"
		 * :"10007604","RSRV_DATE3"
		 * :null,"RSRV_DATE2":null,"RSRV_DATE1":null,"RSRV_STR5"
		 * :null,"UPDATE_STAFF_ID":"SUPERUSR","RSRV_STR3":null,
		 * "RSRV_STR4":null,
		 * "RSRV_STR1":null,"RSRV_STR2":null,"CAMPN_ID":null,"RSRV_TAG2"
		 * :null,"RSRV_TAG3":null,"RSRV_TAG1":null},
		 * 
		 * {"USER_ID_A":"-1","PRODUCT_MODE":"00",
		 * "MODIFY_TAG":"1","RSRV_NUM4":null
		 * ,"RSRV_NUM5":null,"USER_ID":"1119070241086997"
		 * ,"END_DATE":"2019-07-02 10:34:21","RSRV_NUM1":null,"RSRV_NUM2":null,
		 * "OLD_BRAND_CODE"
		 * :null,"RSRV_NUM3":null,"MAIN_TAG":"1","INST_ID":"1119070219180381"
		 * ,"OLD_PRODUCT_ID"
		 * :null,"BRAND_CODE":"G001","UPDATE_DEPART_ID":"36601",
		 * "TRADE_ID":"1119070213556700"
		 * ,"REMARK":null,"ACCEPT_MONTH":"7","START_DATE"
		 * :"2019-07-02 10:29:06","UPDATE_TIME":"2019-07-02 10:34:22",
		 * "PRODUCT_ID"
		 * :"10007601","RSRV_DATE3":null,"RSRV_DATE2":null,"RSRV_DATE1"
		 * :null,"RSRV_STR5"
		 * :null,"UPDATE_STAFF_ID":"SUPERUSR","RSRV_STR3":null,"RSRV_STR4"
		 * :null,"RSRV_STR1":null,
		 * "RSRV_STR2":null,"CAMPN_ID":null,"RSRV_TAG2":null
		 * ,"RSRV_TAG3":null,"RSRV_TAG1":null}],
		 */

		/*
		 * productTrades 开户时的参数:
		 * 
		 * [{"USER_ID_A":"-1","PRODUCT_MODE":"00",
		 * "MODIFY_TAG":"0","RSRV_NUM4":null
		 * ,"RSRV_NUM5":null,"USER_ID":"1119070241086997"
		 * ,"END_DATE":"2050-12-31 23:59:59","RSRV_NUM1":null,"RSRV_NUM2":null,
		 * "OLD_BRAND_CODE"
		 * :null,"RSRV_NUM3":null,"MAIN_TAG":"1","INST_ID":"1119070219180381"
		 * ,"OLD_PRODUCT_ID"
		 * :null,"BRAND_CODE":"G001","UPDATE_DEPART_ID":"36601",
		 * "TRADE_ID":"1119070213556681"
		 * ,"REMARK":null,"ACCEPT_MONTH":"7","START_DATE"
		 * :"2019-07-02 10:29:06","UPDATE_TIME"
		 * :"2019-07-02 10:29:06","PRODUCT_ID":"10007601",
		 * "RSRV_DATE3":null,"RSRV_DATE2"
		 * :null,"RSRV_DATE1":null,"RSRV_STR5":null
		 * ,"UPDATE_STAFF_ID":"SUPERUSR",
		 * "RSRV_STR3":null,"RSRV_STR4":null,"RSRV_STR1":null,
		 * "RSRV_STR2":null,"CAMPN_ID"
		 * :null,"RSRV_TAG2":null,"RSRV_TAG3":null,"RSRV_TAG1":null}],
		 */

		/*
		 * productTrades 销户时的参数:
		 * 
		 * [{"USER_ID_A":"-1" "PRODUCT_MODE":"00" "MODIFY_TAG":"1"
		 * "RSRV_NUM4":null "RSRV_NUM5":null "USER_ID":"1118041235825632"
		 * "END_DATE":"2019-07-04 11:27:05" "RSRV_NUM1":null "RSRV_NUM2":null
		 * "OLD_BRAND_CODE":null "RSRV_NUM3":null "MAIN_TAG":"1"
		 * "INST_ID":"1118041295189692" "OLD_PRODUCT_ID":null
		 * "BRAND_CODE":"G001" "UPDATE_DEPART_ID":"36601"
		 * "TRADE_ID":"1119070413591722" "REMARK":null "ACCEPT_MONTH":"7"
		 * "START_DATE":"2018-04-12 16:03:30"
		 * "UPDATE_TIME":"2019-07-04 11:27:05" "PRODUCT_ID":"10000000"
		 * "RSRV_DATE3":null "RSRV_DATE2":null "RSRV_DATE1":null
		 * "RSRV_STR5":null "UPDATE_STAFF_ID":"SUPERUSR" "RSRV_STR3":null
		 * "RSRV_STR4":null "RSRV_STR1":null "RSRV_STR2":null "CAMPN_ID":null
		 * "RSRV_TAG2":null "RSRV_TAG3":null "RSRV_TAG1":null}]
		 */

		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID", "");
		String serialNum = mainTrade.getString("SERIAL_NUMBER", "");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

		IDataset productTrades = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
		logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx115 "+productTrades);
		if (IDataUtil.isNotEmpty(productTrades)) {

			for (int i = 0; i < productTrades.size(); i++) {
				IData product = productTrades.getData(i);
				String modifyTag = product.getString("MODIFY_TAG");
				String oldProductId = "";
				String productId = "";
				String startDate = "";
				String endDate = "";

				if ("10".equals(tradeTypeCode) || "110".equals(tradeTypeCode)) {
					
					if ("0".equals(modifyTag)) {
						
						if ("110".equals(tradeTypeCode)) {// 如果是产品变更
							 oldProductId = product.getString("OLD_PRODUCT_ID");
							 productId = product.getString("PRODUCT_ID");
							 startDate = product.getString("START_DATE");// 新套餐的生效时间
							 endDate = product.getString("END_DATE");// 新套餐的结束时间
						     logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx135 "+product);
						     
							 if (StringUtils.isNotBlank(oldProductId) && StringUtils.isNotBlank(productId) && !StringUtils.equals(productId, oldProductId)) {

								// 结束主号tf_f_user_info_class表记录。查看该手机号码在全球通权益表tf_f_user_info_class表里是否存在，如存在则将记录结束掉，即end_date=新套餐的开始时间
								//TODO enddate_TF_F_USER_INFO_CLASS(userId, serialNum, startDate);
								
								// 结束TL_B_USER_GIVE_CLASS表记录，并截止明细表里之前赠送号码的全球通权益。
								// 查看该手机号码在全球通权益表tl_b_user_give_class和tl_b_user_give_class_detail表里是否存在，如存在则将记录结束掉，即end_date=新套餐的开始时间
								enddate_TL_B_USER_GIVE_CLASS(userId, serialNum, startDate);

								// 再重新插入新记录tf_f_user_info_class,tl_b_user_give_class
								inserttables(mainTrade, productId, startDate, endDate);
								
							 }
						} else if ("10".equals(tradeTypeCode)) {// 如果是开户
							 startDate = product.getString("START_DATE");// 新套餐的生效时间
							 endDate = product.getString("END_DATE");// 新套餐的结束时间
							 productId = product.getString("PRODUCT_ID");
						     logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx154 "+product);							 
							if (StringUtils.isNotBlank(productId)) {
								// 插入新记录tf_f_user_info_class,tl_b_user_give_class
								inserttables(mainTrade, productId, startDate, endDate);
							}
						}
					}
				} else if ("192".equals(tradeTypeCode)) {
				     logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx162 "+product);							 
				     
					 endDate = product.getString("END_DATE");// 产品的结束时间
					 // 结束主号tf_f_user_info_class表记录。查看该手机号码在全球通权益表tf_f_user_info_class表里是否存在，如存在则将记录结束掉，即end_date=新套餐的开始时间					
					 //TODO enddate_TF_F_USER_INFO_CLASS(userId, serialNum, endDate);

					 // 结束TL_B_USER_GIVE_CLASS表记录。查看该手机号码在全球通权益表tl_b_user_give_class和tl_b_user_give_class_detail表里是否存在，如存在则将记录结束掉，即end_date=新套餐的开始时间
					enddate_TL_B_USER_GIVE_CLASS(userId, serialNum, endDate);
				}
			}
		}
	}
	
	private void enddate_TL_B_USER_GIVE_CLASS(String userId, String serialNum, String endDate) throws Exception {
		IData input = new DataMap();
		input.put("USER_ID", userId);
		input.put("SERIAL_NUMBER", serialNum);
		IDataset userGiveClassData = UserGiveClassInfoQry.queryUserClassByUseridSn(input);
	    logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx180 "+userGiveClassData);							 
	    
		if (IDataUtil.isNotEmpty(userGiveClassData)) {
			input.clear();
			input.put("USER_ID", userId);
			input.put("SERIAL_NUMBER", serialNum);
			input.put("END_DATE", endDate);// 已存在全球通标识的截止时间为新套餐生效时间 
		    logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx187 "+input);							 			
			Dao.executeUpdateByCodeCode("TL_B_USER_GIVE_CLASS", "UPD_END_DATE", input);

			// 接着从明细表里查询出之前赠送的号码，到TF_F_USER_INFO_CLASS表里终止掉
			String tradeId = userGiveClassData.getData(0).getString("TRADEID");
			input.clear();
			input.put("FROMTRADEID", tradeId);
			IDataset userGiveClassDetailData = UserGiveClassInfoQry.queryUserGiveClassDetailByFromtradeid(input);
		    logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx195 "+userGiveClassDetailData);							 						

			if (IDataUtil.isNotEmpty(userGiveClassDetailData)) {
				for (int i = 0; i < userGiveClassDetailData.size(); i++) {
					IData deatail = userGiveClassDetailData.getData(i);
					String giveUserId = deatail.getString("GIVE_USER_ID");
					String giveSerialNumber = deatail.getString("GIVE_SERIAL_NUMBER");
				    logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx202 "+deatail);							 											
					enddate_TF_F_USER_INFO_CLASS(giveUserId, giveSerialNumber, endDate);
					enddate_TL_B_USER_GIVE_CLASS_DETAIL(giveSerialNumber, endDate);
				}
			}
		}
	}
	
	private void enddate_TL_B_USER_GIVE_CLASS_DETAIL(String giveserialNum, String endDate) throws Exception {
		    IData input = new DataMap();		 			 
			input.put("GIVE_SERIAL_NUMBER",giveserialNum);
			input.put("END_DATE", endDate);// 
		    logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx213 "+input);							 			
			Dao.executeUpdateByCodeCode("TL_B_USER_GIVE_CLASS_DETAIL", "UPD_END_DATE", input);		 
	}
	

	private void enddate_TF_F_USER_INFO_CLASS(String userId, String serialNum, String endDate) throws Exception {
		IData input = new DataMap();
		input.put("SERIAL_NUMBER", serialNum);
		IData userclassdata = UserClassInfoQry.queryUserClassBySN(input);
	    logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx213 "+userclassdata);							 											

		if (IDataUtil.isNotEmpty(userclassdata)) {

			GiveUserClassBean bean = BeanManager.createBean(GiveUserClassBean.class);
			String userLevel = userclassdata.getString("USER_CLASS");
			String remark = "赠送号码产品变更，被赠送号码同时截止";
			bean.delClassInfo(userId, serialNum,  null, userLevel, "4",remark);
		}
	}
	
	private void inserttables(IData mainTrade, String productId, String startDate, String endDate) throws Exception {
		IDataset Commpara2245 = CommparaInfoQry.getCommNetInfo("CSM", "2245", productId);
	    logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx228 "+Commpara2245);							 											
		
	    // 新套餐是否在配置表里存在，如有，则插入tf_f_user_info_class表
		if (IDataUtil.isNotEmpty(Commpara2245)) {
			IData commparadata = Commpara2245.getData(0);
		    logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx233 "+commparadata);							 														
			//TODO insert_TF_F_USER_INFO_CLASS(mainTrade, commparadata, startDate, endDate);
			insert_TL_B_USER_GIVE_CLASS(mainTrade, commparadata, productId, startDate, endDate);
		}
	}
	
	private void insert_TF_F_USER_INFO_CLASS(IData mainTrade, IData commparadata, String startDate, String endDate) throws Exception {
		
		 // USER_CLASS 1 全球通银卡 2 全球通金卡 3 全球通白金卡 4 全球通钻石卡（非终身全球通用户） 5 终身全球通用户 6 全球通体验用户
		
		String userId = mainTrade.getString("USER_ID", "");
		String serialNum = mainTrade.getString("SERIAL_NUMBER", "");
		String userClass = commparadata.getString("PARA_CODE1");

		IData inData = new DataMap();
		inData.put("USER_ID", userId);
		inData.put("SERIAL_NUMBER", serialNum);
		IDataset custInfos = CustomerInfoQry.getCustInfoPsptBySn(serialNum);
		IData custInfo = custInfos.getData(0);
		String psptTypeCode = custInfo.getString("PSPT_TYPE_CODE");
		String birthday = new String();
		if ("0".equals(psptTypeCode) || "1".equals(psptTypeCode) || "2".equals(psptTypeCode)) {
			String idCard = custInfo.getString("PSPT_ID");
			birthday = idCard.substring(6, 14);
		}
		inData.put("USER_CLASS", userClass);
		inData.put("BIRTHDAY", birthday);
		inData.put("IN_DATE", SysDateMgr.getSysTime());
		inData.put("START_DATE", startDate);// 新套餐开始时间
		inData.put("END_DATE", endDate);// 新套餐结束时间
	    logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx265 "+inData);							 											
		
		Dao.executeUpdateByCodeCode("TF_F_USER_INFO_CLASS", "INSERT_INFO_CLASS_ALL", inData);
	}
	
	private void insert_TL_B_USER_GIVE_CLASS(IData mainTrade, IData commparadata, String productId, String startDate, String endDate) throws Exception {
	    logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx278 "+mainTrade);							 											

		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID", "");
		String serialNum = mainTrade.getString("SERIAL_NUMBER", "");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String userClass = commparadata.getString("PARA_CODE1");
		String totalnum = commparadata.getString("PARA_CODE2");

		IData inData = new DataMap();
		inData.put("TRADEID", tradeId);
		inData.put("TRADE_TYPE_CODE", tradeTypeCode);
		inData.put("PRODUCT_ID", productId);
		inData.put("USER_ID", userId);
		inData.put("SERIAL_NUMBER", serialNum);
		inData.put("USER_CLASS", userClass);
		inData.put("TOTAL_NUM", totalnum);
		inData.put("USED_NUM", 0);
		inData.put("UNUSED_NUM", totalnum);
		inData.put("UPDATE_TIME", SysDateMgr.getSysTime());
		inData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
		inData.put("UPDATE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
		inData.put("UPDATE_CITY_CODE", mainTrade.getString("TRADE_CITY_CODE"));
		inData.put("START_DATE", startDate);// 新套餐开始时间
		inData.put("END_DATE", endDate);// 新套餐结束时间
		inData.put("REMARK", "REQ201906240004关于企业尊享计划套餐的开发需求（新增规则）");
	    logger.error("GiveOrCancelUserClassxxxxxxxxxxxxxxxxxxxxxxxx296 "+inData);							 											

		Dao.executeUpdateByCodeCode("TL_B_USER_GIVE_CLASS", "INSERT_GIVE_CLASS_ALL", inData);
	}
	
}
