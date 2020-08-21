
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UStaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import net.sf.json.JSONObject;


/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductExpireSVC.java
 * @Description: 产品变更到期提醒处理服务类 1. 取消营销活动 2. 调用客户管理接口 3. 调用营销活动管理接口 4. 批量【和4G活动】处理
 * @version: v1.0.0
 * @author: maoke
 * @date: Sep 8, 2014 11:30:15 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Sep 8, 2014 maoke v1.0.0 修改原因
 */
public class ChangeProductExpireSVC extends CSBizService
{

	/**
	 * @Description: 产品变更取消营销活动
	 * @param mainTrade
	 * @throws Exception
	 * @author: maoke
	 * @date: Aug 19, 2014 10:43:36 AM
	 */
	public IDataset cancelSaleActive(IData mainTrade) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String tradeStaffId = mainTrade.getString("TRADE_STAFF_ID");
		String tradeDepartId = mainTrade.getString("TRADE_DEPART_ID");
		String tradeCityCode = mainTrade.getString("TRADE_CITY_CODE");
		String tradeEparchyCode = mainTrade.getString("TRADE_EPARCHY_CODE");

		IDataset result = new DatasetList();

		if (isProductChange(tradeId))
		{
			String oldProductId = mainTrade.getString("RSRV_STR2");// 老产品ID
			String newProductId = mainTrade.getString("PRODUCT_ID");
			String oldBrand = mainTrade.getString("RSRV_STR1");// 老品牌
			String newBrand = mainTrade.getString("BRAND_CODE");
			String bookingDate = mainTrade.getString("RSRV_STR3");// 预约时间
			String processTagSet = mainTrade.getString("PROCESS_TAG_SET");
			boolean isBookingTag = false;

			if ("1".equals(processTagSet.substring(18, 19)))// 预约
			{
				isBookingTag = true;
			}
			
			//如果没有日期，赋受理时间
			if(StringUtils.isEmpty(bookingDate)){
				bookingDate = mainTrade.getString("ACCEPT_DATE");// 受理时间
			}

			String cancelDate = this.getCancelSaleActiveDate(userId, bookingDate, isBookingTag);

			IDataset cancelSaleActiveList = UserSaleActiveInfoQry.queryCancelSaleActives(userId, oldBrand, newBrand, oldProductId, newProductId, cancelDate);

			if (IDataUtil.isNotEmpty(cancelSaleActiveList))
			{
				for (int i = 0, size = cancelSaleActiveList.size(); i < size; i++)
				{
					IData cancelSale = cancelSaleActiveList.getData(i);

					IData cancelParam = new DataMap();
					cancelParam.put("SERIAL_NUMBER", serialNumber);
					cancelParam.put("PRODUCT_ID", cancelSale.getString("PRODUCT_ID"));
					cancelParam.put("PACKAGE_ID", cancelSale.getString("PACKAGE_ID"));
					cancelParam.put("RELATION_TRADE_ID", cancelSale.getString("RELATION_TRADE_ID"));
					cancelParam.put("FORCE_END_DATE", cancelDate);
					cancelParam.put("TRADE_STAFF_ID", tradeStaffId);
					cancelParam.put("TRADE_DEPART_ID", tradeDepartId);
					cancelParam.put("TRADE_CITY_CODE", tradeCityCode);
					cancelParam.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
					cancelParam.put("REMARK", "产品变更营销活动取消");

					IData callData = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", cancelParam).getData(0);

					IData returnData = new DataMap();
					returnData.clear();
					returnData.put("产品变更营销活动取消:", "ORDER_ID=[" + callData.getString("ORDER_ID", "") + "]," + "TRADE_ID=[" + callData.getString("TRADE_ID", "") + "]" + "FORCE_END_DATE=[" + cancelParam.getString("FORCE_END_DATE", "") + "]");

					result.add(returnData);
				}
			}
		}

		return result;
	}

	/**
	 * 获取取消营销活动时间
	 * 
	 * @param userId
	 * @param bookingDate
	 * @param isBookingTag
	 * @return
	 * @throws Exception
	 */
	public String getCancelSaleActiveDate(String userId, String bookingDate, boolean isBookingTag) throws Exception
	{
		// 获取用户账期
		IDataset userAcctDays = UcaInfoQry.qryUserAcctDaysByUserId(userId);

		String acctDay = "";
		String firstDay = "";
		String nextAcctDay = "";
		String nextFirstDay = "";
		String startDate = "";
		String nextStartDate = "";
		if (IDataUtil.isNotEmpty(userAcctDays))
		{
			int size = userAcctDays.size();
			IData userAcctDay = userAcctDays.getData(0);
			acctDay = userAcctDay.getString("ACCT_DAY");
			firstDay = userAcctDay.getString("FIRST_DATE");
			startDate = userAcctDay.getString("START_DATE");
			if (size > 1)
			{
				IData userNextAcctDay = userAcctDays.getData(1);
				nextAcctDay = userNextAcctDay.getString("ACCT_DAY");
				nextFirstDay = userNextAcctDay.getString("FIRST_DATE");
				nextStartDate = userNextAcctDay.getString("START_DATE");
			}
			AcctTimeEnv env = new AcctTimeEnv(acctDay, firstDay, nextAcctDay, nextFirstDay, startDate, nextStartDate);
			AcctTimeEnvManager.setAcctTimeEnv(env);
		}

		String cancelDate = "";

		if (isBookingTag)
		{
			int bookingDateDay = SysDateMgr.getIntDayByDate(bookingDate);
			int userAcctDay = 1;

			AcctTimeEnv env = AcctTimeEnvManager.getAcctTimeEnv();
			if (env != null)
			{
				userAcctDay = Integer.parseInt(env.getAcctDay());
			}

			if (bookingDateDay != userAcctDay)
			{
				cancelDate = SysDateMgr.getAddMonthsLastDay(1, bookingDate).substring(0, 10) + SysDateMgr.END_DATE;
			}
			else
			{
				cancelDate = SysDateMgr.getAddMonthsLastDay(-1, bookingDate).substring(0, 10) + SysDateMgr.END_DATE;
			}
		}
		else
		{
			cancelDate = SysDateMgr.getAddMonthsLastDay(1, bookingDate).substring(0, 10) + SysDateMgr.END_DATE;
		}

		return cancelDate;
	}

	/**
	 * @Description: 积分清零
	 * @param tradeTypeCode
	 * @param tradeId
	 * @param userId
	 * @throws Exception
	 * @author: maoke
	 * @date: Aug 19, 2014 3:41:04 PM
	 */
	public void cleanScoreValue(String tradeTypeCode, String tradeId, String userId) throws Exception
	{
		IDataset tradeScore = TradeScoreInfoQry.qryTradeScoreInfos(tradeId, "0");

		if (IDataUtil.isNotEmpty(tradeScore))
		{
			AcctCall.cancelScoreValue(tradeTypeCode, tradeId, userId);
		}
	}

	/**
	 * @Description: 取消大客户调用客户管理接口
	 * @param mainTrade
	 * @throws Exception
	 * @author: maoke
	 * @date: Aug 19, 2014 10:46:06 AM
	 */
	public void dealCallCustmanmSvc(IData mainTrade) throws Exception
	{
		String processTagSet = mainTrade.getString("PROCESS_TAG_SET").substring(3, 4);

		if ("1".equals(processTagSet))
		{
			CSAppCall.call("CM.BusiEntry", mainTrade);
		}
	}

	/**
	 * @Description: 调用营销管理接口
	 * @param mainTrade
	 * @throws Exception
	 * @author: maoke
	 * @date: Aug 19, 2014 10:40:33 AM
	 */
	public void dealCallSalemanmSvc(IData mainTrade) throws Exception
	{
		/*--办理国漫调用营销管理 ITF_CRM_AddRoamJob
		select * from code_code t where t.sql_ref = 'SEL_RN_IMPORTANT_BY_USERID';
		--品牌变更调用营销管理 ITF_CRM_AddBrandJob
		select * from code_code t where t.sql_ref = 'SEL_BRAND_CHANGE_BY_USER_ID';*/

		String userId = mainTrade.getString("USER_ID");
		String tradeId = mainTrade.getString("TRADE_ID");
		String productId = mainTrade.getString("PRODUCT_ID");

		IDataset saleDatas = TradeSvcInfoQry.getDataForSalemanmByRoming(userId, tradeId);

		if (IDataUtil.isNotEmpty(saleDatas))
		{
			IData saleParam = saleDatas.getData(0);

			saleParam.put("EPARCHY_CODE", saleParam.getString("CITY_CODE"));// 为了匹配客服字段才将CITY_CODE放入EPARCHY_CODE
			saleParam.put("BRAND_CODE", mainTrade.getString("RSRV_STR1"));// 取老品牌
			saleParam.put("CREDIT_VALUE", "0");// 调用信用度接口

			SccCall.createRoamJob(saleParam);
		}

		IDataset saleDataBrand = UserInfoQry.getDataForSaleByChangeBrand(userId, tradeId);

		if (IDataUtil.isNotEmpty(saleDataBrand))
		{
			IData saleParam = saleDataBrand.getData(0);

			saleParam.put("EPARCHY_CODE", saleParam.getString("CITY_CODE"));// 为了匹配客服字段才将CITY_CODE放入EPARCHY_CODE
			saleParam.put("PRODUCT_ID", productId);
			saleParam.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
			saleParam.put("CUST_CODE", saleParam.getString("SERIAL_NUMBER"));

			SccCall.createBrandJob(saleParam);
		}
		
		try{
			//客户更改全网统一资费 关键时刻接口
			//1 获取用户信用星级
			//查询用户信用星级，大于4星才生成工作
	        IData parameter = new DataMap();
	        parameter.put("IDTYPE", 0 );
	        parameter.put("ID", userId );
	        
	        IDataset creditinfos = CSAppCall.call("QCC_ITF_GetCreditInfos", parameter);
	        if (creditinfos!=null && creditinfos.size() > 0)
	        {
	            IData info = creditinfos.getData(0);
	            if ( info!= null && info.size()>0 )
	            {
	            	String strcreditclass = info.getString("CREDIT_CLASS", "-1");
	            	int creditclass = Integer.parseInt(strcreditclass);
	            	if( creditclass >= 4 ){
	            		//2 判断productid是否属于统一资费
	            		String productName = StaticUtil.getStaticValue("UNIFIED_PACKAGE_JOB_PRODUCT", productId);
	            		if( null != productName && !"".equals(productName) ){//查到记录说明是统一资费套餐
	            			IData param = new DataMap();
	            	        param.put("USER_ID", userId);
	            	        boolean ismatch = false;
	            	        String discnt_id = "";
	            	        String discnt_name = "";
	            	        //3、判断distinctid是否属于流量套餐，取出流量套餐
	            	        IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_CRITICALMOMENT", param);
	            	        for( int i = 0; i < dataset.size(); i++ ){
	            	        	String tmpdisctid = dataset.getData(i).getString("DISCNT_CODE","");
	            	        	String tmpdiscntname = StaticUtil.getStaticValue("UNIFIED_PACKAGE_JOB_GPRS", tmpdisctid);
	            	        	if( null != tmpdiscntname && !"".equals(tmpdiscntname)){
	            	        		discnt_id = tmpdisctid;
	            	        		discnt_name = tmpdiscntname;
	            	        		ismatch = true;
	            	        		break;
	            	        	}
	            	        }
	            	        
	            	        IDataset unifieddata = new DatasetList();

	                          //原sql  "TF_B_TRADE_PRODUCT", "SEL_USER_PRODUCT_CRITICALMOMENT"
	            	        IDataset results = Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_USER_PRODUCT_CRITICALMOMENT_NEW1", mainTrade,Route.getJourDb(BizRoute.getRouteId()));//拆分sql
                            if(IDataUtil.isNotEmpty(results)){
                            	for(int i=0;i<results.size();i++){
                                	String product_id=results.getData(i).getString("PRODUCT_ID");
        	            	        String  products = UStaticInfoQry.getStaticValue("UNIFIED_PACKAGE_JOB_PRODUCT", product_id);//拆分sql
                                    if(StringUtils.isBlank(products)){
                                    	results.remove(i);
                                    	i--;
                                    }
                            	}
                            }
	            	        
	            	        IDataset unifieddata1 = Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_USER_PRODUCT_CRITICALMOMENT_NEW2", mainTrade);//拆分sql
	          
	            	        for (int i = 0; i < unifieddata1.size(); i++)
	            	        {
	            	            IData unifieddataUser = unifieddata1.getData(i);

	            	            for (int j = 0; j < results.size(); j++)
	            	            {
	            	                IData resultsUser = results.getData(j);
	            	                String userId1 = unifieddataUser.getString("USER_ID");
	            	                String userId2 = resultsUser.getString("USER_ID");

	            	                if (StringUtils.equals(userId1, userId2))
	            	                {
	            	                	unifieddata.add(unifieddataUser);

	            	                }
	            	            }
	            	        }
	            	        
	            	        
	            	        if( IDataUtil.isNotEmpty(unifieddata) ){//符合条件，调用营销接口
	            	        	IData unifiedparam = unifieddata.getData(0);
	            	        	unifiedparam.put("EPARCHY_CODE", unifiedparam.getString("CITY_CODE"));// 为了匹配客服字段才将CITY_CODE放入EPARCHY_CODE
	            	        	unifiedparam.put("PRODUCT_ID", productId);
	            	        	unifiedparam.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
	            	        	unifiedparam.put("CUST_CODE", unifiedparam.getString("SERIAL_NUMBER"));
	            	        	unifiedparam.put("DISTINCT_CODE",discnt_id);
	            	        	unifiedparam.put("DISCNT_NAME",discnt_name);
	            	        	unifiedparam.put("BRAND_CODE", mainTrade.getString("RSRV_STR1"));// 取老品牌
	            	        	SccCall.createUnifiedPackageJob(unifiedparam);
	            	        }
	            		}
	            	}
	            }
	        }
		}catch (Exception e) {
			// TODO: handle exception
			return;
		}
		
		
	}

	public IDataset dealExpire(IData mainTrade) throws Exception
	{
		IDataset result = new DatasetList();

		String tradeId = mainTrade.getString("TRADE_ID");
		// String userId = mainTrade.getString("USER_ID");
		// String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

		// 查历史台账 如存在未返销的 才进行处理
		IData mainHiTrade = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", null);

		if (IDataUtil.isNotEmpty(mainHiTrade))
		{
			// 实名制后 无积分清零
			// this.cleanScoreValue(tradeTypeCode, tradeId, userId);

			// 调用客户管理
			this.dealCallCustmanmSvc(mainTrade);

			// 处理营销管理接口
			this.dealCallSalemanmSvc(mainTrade);

			// 产品变更取消营销活动
			result = this.cancelSaleActive(mainTrade);

			// 批量【和4G活动】处理
			this.updUserExpactByBatch(mainTrade);
		}

		return result;
	}

	/**
	 * @Description: 是否产品变更
	 * @param tradeId
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Aug 19, 2014 10:51:42 AM
	 */
	public boolean isProductChange(String tradeId) throws Exception
	{
		IDataset productTrade = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
		if (IDataUtil.isNotEmpty(productTrade))
		{
			for (int i = 0; i < productTrade.size(); i++)
			{
				if ("1".equals(productTrade.getData(i).getString("MAIN_TAG")))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @Description: 批量过来【关于配合集团“和4G，点亮精彩世界”开发专属流量包的需求】更新
	 * @param mainTrade
	 * @throws Exception
	 * @author: maoke
	 * @date: Aug 28, 2014 4:35:01 PM
	 */
	public void updUserExpactByBatch(IData mainTrade) throws Exception
	{
		String batchId = mainTrade.getString("BATCH_ID");

		if (StringUtils.isNotBlank(batchId))
		{
			String tradeId = mainTrade.getString("TRADE_ID");

			IDataset userDiscnts = TradeDiscntInfoQry.qryTradeDiscntInfos(tradeId, "0", null);

			if (IDataUtil.isNotEmpty(userDiscnts))
			{
				for (int i = 0, size = userDiscnts.size(); i < size; i++)
				{
					String discntCode = userDiscnts.getData(i).getString("DISCNT_CODE");
					String eparchyCode = userDiscnts.getData(i).getString("EPARCHY_CODE");

					IDataset commpara9008 = CommparaInfoQry.getCommparaByCode1("CSM", "9008", discntCode, eparchyCode);

					if (IDataUtil.isNotEmpty(commpara9008))
					{
						String paramCode = commpara9008.getData(0).getString("PARAM_CODE");

						if ("WX".equals(paramCode) || "YG".equals(paramCode))
						{
							StringBuilder updSql = new StringBuilder("UPDATE TF_O_USER_TDEXPACT ");
							updSql.append("SET ACTIVE_FLAG = :ACTIVE_FLAG, ");
							updSql.append("SET UPDATE_TIME = SYSDATE ");
							updSql.append("WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
							updSql.append("AND ACCEPT_MONTH = TO_CHAR(SYSDATE, 'MM') ");
							updSql.append("AND ACTIVE_FLAG IS NULL ");
							updSql.append("AND PRIZE_TYPE_CODE IS NOT NULL ");

							IData updData = new DataMap();
							updData.put("ACTIVE_FLAG", "0");
							updData.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER"));

							Dao.executeUpdate(updSql, updData, Route.CONN_CRM_CEN);
						}
					}
				}
			}
		}
	}
	/**
	 * REQ201907010036  产品、优惠预约办理在生效时触发提醒 by liangdg3
	 * AEE到期任务短信提醒调用该接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public void  sendSmsNoticeForBookProduct(IData input) throws Exception{
			String dealCond=input.getString("DEAL_COND");
			JSONObject jasonObject = JSONObject.fromObject(dealCond);
	     	String serialNumber=jasonObject.getString("SERIAL_NUMBER");
	     	String userId=jasonObject.getString("USER_ID");
	     	String acceptDate=jasonObject.getString("ACCEPT_DATE");
	     	String startDate=jasonObject.getString("START_DATE");
	     	String tradeTypeName=jasonObject.getString("TRADE_TYPE_NAME");
            String smsType=jasonObject.getString("OFFER_TYPE");
            String offerCode=jasonObject.getString("OFFER_CODE");

            boolean isSendSms=false;
            if("P".equals(smsType)&&StringUtils.isNotBlank(offerCode)){
                IDataset userProductByUserIdProductId = UserProductInfoQry.getUserProductByUserIdProductId(userId, offerCode);
                if(IDataUtil.isNotEmpty(userProductByUserIdProductId)){
                    isSendSms=true;
                }
            }else if("D".equals(smsType)&&StringUtils.isNotBlank(offerCode)){
                IDataset allDiscntByUserId = UserDiscntInfoQry.getAllDiscntByUserId(userId, offerCode);
                if(IDataUtil.isNotEmpty(allDiscntByUserId)){
                    isSendSms=true;
                }
            }

            if(isSendSms){
                // 拼短信表参数
                IData param = new DataMap();

                param.put("NOTICE_CONTENT", "【生效提醒】您好！您于"+acceptDate+"办理的"+tradeTypeName+"业务，于"+startDate+"生效。【中国移动】");
                param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                param.put("RECV_OBJECT", serialNumber);
                param.put("RECV_ID", userId);
                param.put("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId());
                param.put("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId());

                param.put("REMARK", "产品预约变更到期消息提醒");
                String seq = SeqMgr.getSmsSendId();
                long seq_id = Long.parseLong(seq);
                param.put("SMS_NOTICE_ID", seq_id);
                param.put("PARTITION_ID", seq_id % 1000);
                param.put("SEND_COUNT_CODE", "1");
                param.put("REFERED_COUNT", "0");
                param.put("CHAN_ID", "11");
                param.put("SMS_NET_TAG", "0");
                param.put("RECV_OBJECT_TYPE", "00");
                param.put("SMS_TYPE_CODE", "20");//20用户办理业务通知
                param.put("SMS_KIND_CODE", "02");//02短信通知
                param.put("NOTICE_CONTENT_TYPE", "0");//0指定内容发送
                param.put("FORCE_REFER_COUNT", "1");
                param.put("FORCE_START_TIME",SysDateMgr.getSysTime());//指定起始时间
                param.put("FORCE_OBJECT", "10086");
                param.put("SMS_PRIORITY", "50");
                param.put("DEAL_STATE", "15");// 处理状态，0：已处理，15未处理
                param.put("SEND_TIME_CODE", "1");
                param.put("SEND_OBJECT_CODE", "6");
                param.put("REFER_TIME", SysDateMgr.getSysTime());
                param.put("DEAL_TIME", SysDateMgr.getSysTime());
                param.put("MONTH", SysDateMgr.getCurMonth());
                param.put("DAY", SysDateMgr.getCurDay());
                Dao.insert("TI_O_SMS_BATCH", param,Route.CONN_UOP_UEC);
           }
	}	
}
