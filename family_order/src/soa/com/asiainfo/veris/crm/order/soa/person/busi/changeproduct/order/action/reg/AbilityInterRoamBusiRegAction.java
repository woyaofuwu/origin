package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.exception.InterRoamDayException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.interroamday.InterRoamDayBean;

public class AbilityInterRoamBusiRegAction implements ITradeAction {

	public void executeAction(BusiTradeData btd) throws Exception {

		List<DiscntTradeData> discntTradeDatas = btd.get("TF_B_TRADE_DISCNT");
		if (discntTradeDatas != null && discntTradeDatas.size() > 0) {

			IData configContainer = new DataMap();

			for (int i = 0, size = discntTradeDatas.size(); i < size; i++) {
				DiscntTradeData tradeData = discntTradeDatas.get(i);

				// 如果为新增
				if (tradeData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)) {
					String discntCode = tradeData.getElementId();

					//因为需要取预留字段，所以这个接口不行
					IDataset discntConfigDatas = UpcCall.queryGroupComRel("99990000", BofConst.ELEMENT_TYPE_CODE_DISCNT, discntCode);//PkgElemInfoQry.getServElementByPk("99990000", "D", discntCode);

					if (IDataUtil.isNotEmpty(discntConfigDatas)) {
						IData configData = discntConfigDatas.getData(0);

						// 能力定向套餐
						if (configData.getString("RSRV_TAG1", "").equals("3")) {
							configContainer.put(discntCode, configData);
						}

					}
				}
			}

			// 如果存在定向套餐的数据，需要进行相关规则的判断
			if (configContainer != null && !configContainer.isEmpty()) 
			{

				try {

					UcaData uca = btd.getRD().getUca();
					String userId = uca.getUserId();

					/*
					 * 判断用户是否办理了国际漫游套餐
					 */
					IData userparams = new DataMap();
					userparams.put("USER_ID", userId);
					userparams.put("SERVICE_ID", "19");
					IDataset svcInfos = UserSvcInfoQry.getSvcUserId(userId, "19");
					if (svcInfos == null || svcInfos.size() <= 0) 
					{
						CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_300001);
					}

					Set<String> discntKeys = configContainer.keySet();
					Iterator<String> it = discntKeys.iterator();

					/*
					 * 查询国漫包下用户订购的套餐信息
					 */
					InterRoamDayBean bean = new InterRoamDayBean();

					IData input = new DataMap();
					input.put("PACKAGE_ID", "99990000");
					input.put("ELEMENT_ID", "");
					input.put("USER_ID", userId);
					input.put("ELEMENT_TYPE_CODE", "D");
					List<DiscntTradeData> userDiscntsResults = uca.getDiscntsByGroupId("99990000");

					/*
					 * 对每个定向套餐进行判断相关互斥规则
					 */
					while (it.hasNext()) 
					{
						String discntCode = it.next();
						
						IData inputPID = new DataMap();
						inputPID.put("PACKAGE_ID", "99990000");
						inputPID.put("USER_ID", userId);
						List<DiscntTradeData> idsPID = this.filterThisMonth(userDiscntsResults);
						
						int nCount = 0;
						if (ArrayUtil.isNotEmpty(idsPID)) 
						{
							for (int i = 0; i < idsPID.size(); i++) 
							{
								DiscntTradeData idPID = idsPID.get(i);
								String pidDiscntCode = idPID.getDiscntCode();
								
								if("99991105".equals(discntCode) || "99991106".equals(discntCode) || "99991107".equals(discntCode) ||
								   "99991116".equals(discntCode) || "99991117".equals(discntCode) || "99991118".equals(discntCode))  
								{
									if("99991105".equals(pidDiscntCode) || "99991106".equals(pidDiscntCode) || "99991107".equals(pidDiscntCode) ||
									   "99991116".equals(pidDiscntCode) || "99991117".equals(pidDiscntCode) || "99991118".equals(pidDiscntCode))
									{
										nCount++;
									}
								}
							}
						}
						
						if(nCount >= 2)
						{
							CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_300007);
						}

						// 获取配置的数据
						IData configDiscntData = configContainer.getData(discntCode);
						String newOpenArea = configDiscntData.getString("RSRV_STR3");

						/*
						 * 验证相关规则
						 */
						if (userDiscntsResults != null && userDiscntsResults.size() > 0) 
						{
							for (int i = 0; i < userDiscntsResults.size(); i++) 
							{
								DiscntTradeData userDiscntsResult = userDiscntsResults.get(i);
								String userDiscntCode = userDiscntsResult.getDiscntCode();

								// 定向套餐同向的互斥
								IData result = UpcCall.queryGroupComRel("99990000", BofConst.ELEMENT_TYPE_CODE_DISCNT, userDiscntCode).getData(0);//bean.getPackageDiscnt("99990000", userDiscntCode);
								String openArea = result.getString("RSRV_STR3", "");
								if (openArea.equals(newOpenArea)) 
								{
									CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_300003);
								}

								/*
								 * “17个方向包多天套餐”、“32国包7天套餐和国际漫游数据流量日套餐包（ 99990001
								 * ）
								 */
								// 判断用户是否存在相关日套餐的互斥
								IDataset discntImcompats = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3719", newOpenArea, userDiscntCode);
								if (IDataUtil.isNotEmpty(discntImcompats)) 
								{
									String discntName = discntImcompats.getData(0).getString("PARA_CODE2", "");
									CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_300002, discntName);
								}

								IDataset countryImcompats = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3719", newOpenArea, openArea);
								if (IDataUtil.isNotEmpty(countryImcompats)) 
								{
									String coutryName = countryImcompats.getData(0).getString("PARA_CODE2", "");
									CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_300004, coutryName);
								}

							}
						}

						/*
						 * 绑定相关费用信息
						 */
//						DeferTradeFeeData deferTradeFee = new DeferTradeFeeData();
//						String fee = configDiscntData.getString("RSRV_STR1");// 费用以分计算
//						String days = configDiscntData.getString("RSRV_STR4");
//						String deferItemCode = "";
//						IData feeParam = CommparaInfoQry.getCommNetInfo("CSM",
//								"3738", days).getData(0);
//						if (IDataUtil.isNotEmpty(feeParam)) {
//							deferItemCode = feeParam.getString("PARA_CODE1");
//						}
//
//						deferTradeFee.setUserId(btd.getRD().getUca()
//								.getUserId());
//						deferTradeFee.setFeeMode("0");
//						deferTradeFee.setFeeTypeCode("0");
//						deferTradeFee.setDeferCycleId("-1");
//						deferTradeFee.setDeferItemCode(deferItemCode);
//						deferTradeFee.setMoney(fee);
//						deferTradeFee.setActTag("1");
//						deferTradeFee.setRsrvStr1("ONCEFEE");// 标识为BBOSS一次性费用
//						deferTradeFee.setRemark("能力平台国漫数据流量定向套餐一次性费用");
//
//						btd.add(uca.getSerialNumber(), deferTradeFee);
//
					}

					/*
					 * 获取“30/60/90套餐的优惠编码” 如果没有办理，就需要进行办理
					 */
					String defaultDiscnt = "";
					//这个接口不适合，因为要取rsrv_tag1
					IDataset results = UpcCall.queryGroupComRelOfferByGroupId("99990000");//PkgElemInfoQry.getPackageElementByPackageId("99990000");
					for (int i = 0; i < results.size(); i++) 
					{
						if ("1".equals(results.getData(i).getString("RSRV_TAG1"))) 
						{
							defaultDiscnt = results.getData(i).getString("ELEMENT_ID");
							break;
						}
					}

					List<DiscntTradeData> userDefaultDiscnts = uca.getUserDiscntByDiscntId(defaultDiscnt);
					boolean notExist = true; //bean.existRoamEighteenDiscnt(userId, defaultDiscnt) == 0;
					if(userDefaultDiscnts != null && userDefaultDiscnts.size() > 0){
						notExist = false;
					}
					if (notExist) 
					{// 默认开通30/60/90套餐

						DiscntTradeData discntTradeData = new DiscntTradeData();

						discntTradeData.setUserId(uca.getUserId());
						discntTradeData.setElementId(defaultDiscnt);
						discntTradeData.setElementType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
						discntTradeData.setCampnId("");
						discntTradeData.setInstId(SeqMgr.getInstId());
						discntTradeData.setProductId("-1");
						discntTradeData.setPackageId("99990000");
						discntTradeData.setUserIdA("-1");
						discntTradeData.setRelationTypeCode("");
						discntTradeData.setSpecTag("0");
						discntTradeData.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
						discntTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
						discntTradeData.setRemark("能力开发平台默认办理");
						discntTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

						btd.add(btd.getRD().getUca().getSerialNumber(), discntTradeData);

					}

					/*
					 * 查询是否同步过
					 */
                    boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
                    if (nowRunFlag)
                    {
                        OrderDataBus dataBus = DataBusManager.getDataBus();
                        String submitType = dataBus.getSubmitType();// addShoppingCart
                        if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
                        {
                            return;
                        }
                    }
					IDataset result = UserOtherInfoQry.getUserOtherUserId(uca.getUserId(), "GMSD", null);
					if (result == null || result.size() <= 0) 
					{
						/*
						 * 等级other表数据
						 */
						OtherTradeData otherData = new OtherTradeData();
						otherData.setRsrvValueCode("GMSD");
						otherData.setRsrvValue("国漫平台锁定");
						otherData.setUserId(uca.getUserId());
						otherData.setStartDate(SysDateMgr.getSysTime());
						otherData.setEndDate(SysDateMgr.getTheLastTime());
						otherData.setStaffId(CSBizBean.getVisit().getStaffId());
						otherData.setDepartId(CSBizBean.getVisit().getDepartId());
						otherData.setRsrvStr1(btd.getRD().getTradeId());
						otherData.setInstId(SeqMgr.getInstId());
						otherData.setModifyTag("0");

						btd.add(uca.getSerialNumber(), otherData);

						/*
						 * 同步给国漫平台
						 */
						IData inparam = new DataMap();
						inparam.put("USER_ID", uca.getUserId());
						IData userRes = bean.getUserRes(inparam);
						String imsi = userRes.getString("IMSI", "");

						// 订购或退订标志
						String oprCode = "03";
						String routeType = "00";
						String routeValue = "000";
						String serialNumber = uca.getSerialNumber();
						String provinceA = PersonConst.INTER_ROAM_PROVINCE;
						String outGroupId = "000";
						String outNetType = "";
						String efftt = SysDateMgr.getSysTime();
						String kindId = "BIP2B145_T2001111_0_0";

						IData synResult = IBossCall.QueryInterRoamDay(imsi, serialNumber, oprCode, routeType, routeValue, kindId, provinceA, outGroupId, outNetType, efftt);

						if (!synResult.getString("X_RSPCODE").equals("0000")) 
						{
							CSAppException.apperr(IBossException.CRM_IBOSS_4, synResult.getString("X_RSPCODE"), synResult.getString("X_RSPDESC"));
						}

					}

				} 
				catch (Exception e) 
				{
					String errorReason="";
					String errorCode="-1";
					
					String error=e.getMessage();
					
					if(error!=null&&error.indexOf("300001")!=-1){
						errorCode="300001";
						errorReason="尊敬的客户，您好！您未开通国际漫游服务，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("300002")!=-1){
						errorCode="300002";
						errorReason="尊敬的客户，您好！您已订购定向套餐，不能再订购其它定向套餐，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("300003")!=-1){
						errorCode="300003";
						errorReason="尊敬的客户，您好！您已订购该套餐，同一时段内不能重复订购，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("300004")!=-1){
						errorCode="300004";
						errorReason="尊敬的客户，您好！您已订购定向套餐，不能再订购其它定向套餐，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("300005")!=-1){
						errorCode="300005";
						errorReason="尊敬的客户，您好！您的余额不足，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("300007")!=-1){
						errorCode="300007";
						errorReason="尊敬的客户，您好！对不起，您本月已享受两次一带一路产品优惠，请下月再办理。感谢您的支持与理解。中国移动";
					}
					else if(error!=null&&error.indexOf("2996")!=-1){
						errorCode="2996";
						errorReason="尊敬的客户，您好！您订购的套餐指令错误，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("2997")!=-1){
						errorCode="2997";
						errorReason="尊敬的客户，您好！您已订购该套餐，同一时段内不能重复订购，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("2998")!=-1){
						errorCode="2998";
						errorReason="尊敬的客户，您好！本次业务办理未成功，请您稍后再试 。中国移动";
					}else{
						errorCode="-1";
						errorReason="尊敬的客户，您好！对不起，本次业务办理未成功，推荐您稍后再试。中国移动";
					}
					
					IDataset smsContents=CommparaInfoQry.getCommNetInfo("CSM", "1031", errorCode);
					if(IDataUtil.isNotEmpty(smsContents)){
						IData smsContentData=smsContents.getData(0);
						
						StringBuilder smsContent=new StringBuilder();
						smsContent.append(smsContentData.getString("PARA_CODE1",""));
						smsContent.append(smsContentData.getString("PARA_CODE2",""));
						smsContent.append(smsContentData.getString("PARA_CODE3",""));
						smsContent.append(smsContentData.getString("PARA_CODE4",""));
						smsContent.append(smsContentData.getString("PARA_CODE5",""));
						smsContent.append(smsContentData.getString("PARA_CODE6",""));
						smsContent.append(smsContentData.getString("PARA_CODE7",""));
						smsContent.append(smsContentData.getString("PARA_CODE8",""));
						smsContent.append(smsContentData.getString("PARA_CODE9",""));
						smsContent.append(smsContentData.getString("PARA_CODE10",""));
						
						errorReason=smsContent.toString();
					}
					
					UcaData uca = btd.getRD().getUca();
					
					//发送错误短信
					AsyncSendSms(uca.getSerialNumber(), errorReason, 
							uca.getUserId(), "国漫定向业务一级BOSS办理");
					
					throw e;
				}

			}

		}
	}

	private void AsyncSendSms(String serialNumber, String noticeContent,
			String userId, String StrRemark) throws Exception {
		Connection conn = null;
		Statement st = null;
		try {
			conn = ConnectionManagerFactory.getConnectionManager()
					.getConnection("crm1");// SessionManager.getInstance().getAsyncConnection("crm1");
			IData smsData = new DataMap();
			smsData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
			smsData.put("RECV_OBJECT", serialNumber);
			smsData.put("RECV_ID", userId);
			smsData.put("NOTICE_CONTENT", noticeContent);
			smsData.put("REMARK", StrRemark);
			IData SmsData = SmsSend.prepareSmsData(smsData);
			String InSql = " INSERT INTO TI_O_SMS(SMS_NOTICE_ID,PARTITION_ID, "
					+ " EPARCHY_CODE,BRAND_CODE,IN_MODE_CODE,SMS_NET_TAG, "
					+ " CHAN_ID,SEND_OBJECT_CODE,SEND_TIME_CODE,SEND_COUNT_CODE, "
					+ " RECV_OBJECT_TYPE,RECV_OBJECT,RECV_ID,SMS_TYPE_CODE, "
					+ " SMS_KIND_CODE,NOTICE_CONTENT_TYPE,NOTICE_CONTENT,REFERED_COUNT, "
					+ " FORCE_REFER_COUNT,FORCE_OBJECT, "
					+ " FORCE_START_TIME,FORCE_END_TIME, " + " SMS_PRIORITY, "
					+ " REFER_TIME, " + " REFER_STAFF_ID,REFER_DEPART_ID,"
					+ " DEAL_TIME," + " DEAL_STAFFID,DEAL_DEPARTID,"
					+ " DEAL_STATE, "
					+ " REMARK,REVC1,REVC2,REVC3,REVC4,MONTH,DAY) ";
			InSql += "VALUES('" + SmsData.getString("SMS_NOTICE_ID") + "','"
					+ SmsData.getString("PARTITION_ID");
			InSql += "','" + SmsData.getString("EPARCHY_CODE") + "','"
					+ SmsData.getString("BRAND_CODE", "") + "','"
					+ SmsData.getString("IN_MODE_CODE") + "','"
					+ SmsData.getString("SMS_NET_TAG");
			InSql += "','" + SmsData.getString("CHAN_ID") + "','"
					+ SmsData.getString("SEND_OBJECT_CODE") + "','"
					+ SmsData.getString("SEND_TIME_CODE") + "','"
					+ SmsData.getString("SEND_COUNT_CODE");
			InSql += "','" + SmsData.getString("RECV_OBJECT_TYPE") + "','"
					+ SmsData.getString("RECV_OBJECT") + "','"
					+ SmsData.getString("RECV_ID") + "','"
					+ SmsData.getString("SMS_TYPE_CODE");
			InSql += "','" + SmsData.getString("SMS_KIND_CODE") + "','"
					+ SmsData.getString("NOTICE_CONTENT_TYPE") + "','"
					+ SmsData.getString("NOTICE_CONTENT") + "','"
					+ SmsData.getString("REFERED_COUNT");
			InSql += "','" + SmsData.getString("FORCE_REFER_COUNT") + "','"
					+ SmsData.getString("FORCE_OBJECT") + "','"
					+ SmsData.getString("FORCE_START_TIME", "") + "','"
					+ SmsData.getString("FORCE_END_TIME", "");
			InSql += "','" + SmsData.getString("SMS_PRIORITY") + "',"
					+ "to_date('" + SmsData.getString("REFER_TIME")
					+ "', 'yyyy-mm-dd hh24:mi:ss')" + ",'"
					+ SmsData.getString("REFER_STAFF_ID") + "','"
					+ SmsData.getString("REFER_DEPART_ID");
			InSql += "'," + "to_date('" + SmsData.getString("DEAL_TIME")
					+ "', 'yyyy-mm-dd hh24:mi:ss')" + ",'"
					+ SmsData.getString("DEAL_STAFFID", "") + "','"
					+ SmsData.getString("DEAL_DEPARTID", "") + "','"
					+ SmsData.getString("DEAL_STATE");
			InSql += "','" + SmsData.getString("REMARK", "") + "','"
					+ SmsData.getString("REVC1", "") + "','"
					+ SmsData.getString("REVC2", "") + "','"
					+ SmsData.getString("REVC3", "");
			InSql += "','" + SmsData.getString("REVC4", "") + "','"
					+ SmsData.getString("MONTH") + "','"
					+ SmsData.getString("DAY") + "')";
			st = conn.createStatement();
			st.execute(InSql);
			conn.commit();
			st.close();
		} catch (Exception ex) {
			try {
				if (conn != null) {
					conn.rollback();
				}
			} catch (Exception ex1) {
				throw ex1;
			}

			throw ex;
		} finally {
			try {
				if (st != null && !st.isClosed()) {
					st.close();
				}
			} catch (Exception ex) {
				throw ex;
			}

			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ex) {
				throw ex;
			}
		}
	}

	private List<DiscntTradeData> filterThisMonth(List<DiscntTradeData> discnts) throws Exception{
		if(ArrayUtil.isEmpty(discnts)){
			return null;
		}
		
		List<DiscntTradeData> rst = new ArrayList<DiscntTradeData>();
		for(DiscntTradeData discnt : discnts){
			if(SysDateMgr.compareTo(discnt.getStartDate(), SysDateMgr.getFirstDayOfThisMonth()) > 0){
				rst.add(discnt);
			}
		}
		return rst;
	}
}
