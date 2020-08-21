package com.asiainfo.veris.crm.order.soa.person.busi.realtimemarketing;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeSetXml.ID;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQrySVC;
import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.realtimemarketing.RealTimeMarketingInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.HttpUtil;

public class RealTimeMarketingInfoSVC extends CSBizService
{
	private static Logger logger = Logger.getLogger(RealTimeMarketingInfoSVC.class);

	private static final long serialVersionUID = 1L;

	public static final int getCharLength(String value, int length)
	{
		char chars[] = value.toCharArray();
		int charidx = 0;
		for (int charlen = 0; charlen < length && charidx < chars.length; charidx++)
			if (chars[charidx] > '\200')
			{
				charlen += 2;
				if (charlen > length)
				{
					charidx = charidx - 1;
				}
			} else
			{
				charlen++;
			}

		return charidx;
	}

	/**
	 * 写短信表TI_O_SMS 原来执行TI_O_SMS-INS_SMSCO_CS，有些值是写死的，这里使用默认值
	 * 
	 * @param data
	 * @throws Exception
	 */
	public static void insSms(IData data) throws Exception
	{
		IData sendData = prepareSmsData(data);
		RealTimeMarketingInfoBean bean = BeanManager.createBean(RealTimeMarketingInfoBean.class);
		bean.insSms(sendData);
	}
	
	/**
	 * 准备短信数据
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IData prepareSmsData(IData data) throws Exception
	{
		IData sendData = new DataMap();

		String sysdate = SysDateMgr.getSysTime();
		
		/*------------------------以下是原来需要传入的值--------------------------*/
		// 判断是否为空，如果空，则新生成
		String smsNoticeId = data.getString("SMS_NOTICE_ID", "");
		if (StringUtils.isBlank(smsNoticeId))
		{
			smsNoticeId = SeqMgr.getSmsSendId();
			sendData.put("SMS_NOTICE_ID", smsNoticeId);
		}
		
		sendData.put("PARTITION_ID", smsNoticeId.substring(smsNoticeId.length() - 4));
		sendData.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()));
		sendData.put("RECV_OBJECT", data.getString("RECV_OBJECT"));// 手机号（服务号）（集团客户经理）也可以扩展其他业务
		sendData.put("RECV_ID", data.getString("RECV_ID", "-1"));// 因为是向集团客户经理发信息所以默认-1,也可以扩展其他业务
		
		// 短信截取
		String content = data.getString("NOTICE_CONTENT", "");
		int charLength = getCharLength(content, 4000);
		content = content.substring(0, charLength);
		sendData.put("NOTICE_CONTENT", content);
		
		/*------------------------以下是原来写死的值，改用默认值--------------------------*/
		sendData.put("SEND_COUNT_CODE", data.getString("SEND_COUNT_CODE", "1"));// 发送次数编码?
		sendData.put("REFERED_COUNT", data.getString("REFERED_COUNT", "0"));// 发送次数？
		sendData.put("CHAN_ID", data.getString("CHAN_ID", "11"));
		sendData.put("RECV_OBJECT_TYPE", data.getString("RECV_OBJECT_TYPE", "00"));// 00手机号
		sendData.put("SMS_TYPE_CODE", data.getString("SMS_TYPE_CODE", "20"));// 20用户办理业务通知
		sendData.put("SMS_KIND_CODE", data.getString("SMS_KIND_CODE", "02"));// 02与SMS_TYPE_CODE配套
		sendData.put("NOTICE_CONTENT_TYPE", data.getString("NOTICE_CONTENT_TYPE", "0"));// 0指定内容发送
		sendData.put("FORCE_REFER_COUNT", data.getString("FORCE_REFER_COUNT", "1"));// 指定发送次数
		sendData.put("SMS_PRIORITY", data.getString("SMS_PRIORITY", "50"));// 短信优先级
		sendData.put("REFER_TIME", data.getString("REFER_TIME", sysdate));// 提交时间
		sendData.put("REFER_STAFF_ID", data.getString("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId()));// 员工ID
		sendData.put("REFER_DEPART_ID", data.getString("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId()));// 部门ID
		sendData.put("DEAL_TIME", data.getString("DEAL_TIME", sysdate));// 完成时间
		sendData.put("DEAL_STATE", "0");// 处理状态，0：已处理，15未处理
		sendData.put("SEND_OBJECT_CODE", data.getString("SEND_OBJECT_CODE", "6"));// 通知短信,见TD_B_SENDOBJECT
		sendData.put("SEND_TIME_CODE", data.getString("SEND_TIME_CODE", "1"));// 营销时间限制,见TD_B_SENDTIME
		sendData.put("REMARK", data.getString("REMARK"));// 备注
		
		/*------------------------以下是原来没有写入的值--------------------------*/
		sendData.put("BRAND_CODE", data.getString("BRAND_CODE"));
		sendData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入方式编码
		sendData.put("SMS_NET_TAG", data.getString("SMS_NET_TAG", "0"));
		sendData.put("FORCE_OBJECT", data.getString("FORCE_OBJECT"));// 发送方号码
		sendData.put("FORCE_START_TIME", data.getString("FORCE_START_TIME", ""));// 指定起始时间
		sendData.put("FORCE_END_TIME", data.getString("FORCE_END_TIME", ""));// 指定终止时间
		sendData.put("DEAL_STAFFID", data.getString("DEAL_STAFFID"));// 完成员工
		sendData.put("DEAL_DEPARTID", data.getString("DEAL_DEPARTID"));// 完成部门
		sendData.put("REVC1", data.getString("REVC1"));
		sendData.put("REVC2", data.getString("REVC2"));
		sendData.put("REVC3", data.getString("REVC3"));
		sendData.put("REVC4", data.getString("REVC4"));
		sendData.put("MONTH", sysdate.substring(5, 7));// 月份
		sendData.put("DAY", sysdate.substring(8, 10)); // 日期
		
		return sendData;
	}
	
	public IDataset createDept(IData input) throws Exception
	{
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxx149createDept "+input);
		IDataset ajaxReturnDatas = new DatasetList();
		String sn = input.getString("SERIAL_NUMBER", "");
		IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0",sn);
        String userId = "0";
        if(IDataUtil.isNotEmpty(userInfos) && userInfos.size() > 0) {
            userId = userInfos.getData(0).getString("USER_ID");
        }
		String reqid = input.getString("REQ_ID", "").trim();
		String saleactid = input.getString("SALE_ACT_ID", "").trim();		 
				
		IData param = new DataMap();
		param.put("RECV_OBJECT", sn);
		param.put("REVC1", reqid);
		param.put("REVC2", saleactid);
		IDataset smsds1 =Dao.qryByCode("TI_O_SMS", "SEL_BY_SMS_1", param);
		//N分钟内如有相同记录，则不允许再次发送，避免短信炸弹攻击
		if (IDataUtil.isNotEmpty(smsds1)) {
			IData map = new DataMap();
			map.put("ret_code", "1");
			map.put("ret_result", "下发过于频繁，请几分钟后再试！");
			ajaxReturnDatas.add(map);					
			return ajaxReturnDatas;
		}
		
		//当天只能下发5条相同的短信
		IDataset smsds2 =Dao.qryByCode("TI_O_SMS", "SEL_BY_SMS_2", param);
		if (IDataUtil.isNotEmpty(smsds2)&&smsds2.size()>=5) {
			IData map = new DataMap();
			map.put("ret_code", "1");
			map.put("ret_result", "该短信已超过最大下发量，今天不能重复发送！");
			ajaxReturnDatas.add(map);					
			return ajaxReturnDatas;
		}
		
		IData cond = new DataMap();
		cond.put("REQ_ID",reqid);
		cond.put("SALE_ACT_ID", saleactid);		
		IDataset tradedates = RealTimeMarketingInfoQry.getRealTimeMarketingTrade(cond);
		
		if (IDataUtil.isNotEmpty(tradedates) && StringUtils.isNotBlank(tradedates.getData(0).getString("SMS_CONTENT")))
		{
			// 发生短信接口
			IData sendInfo = new DataMap();
			sendInfo.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
			sendInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
			sendInfo.put("RECV_OBJECT", sn );
			sendInfo.put("RECV_ID", userId);
			sendInfo.put("SMS_PRIORITY", input.getString("PRIORITY", "50"));
			sendInfo.put("NOTICE_CONTENT", tradedates.getData(0).getString("SMS_CONTENT"));
			if ("".equals(input.getString("SMS_PORT", "")))
			{
				sendInfo.put("FORCE_OBJECT", "10086");
			} else
			{
				sendInfo.put("FORCE_OBJECT", input.getString("SMS_PORT", "10086"));
			}
			
			sendInfo.put("REMARK", "实时营销推荐受理下发短信");			
			sendInfo.put("REVC1", reqid);
			sendInfo.put("REVC2", saleactid);
			
			insSms(sendInfo);
			IData map = new DataMap();
			map.put("ret_code", "0");
			map.put("NOTICE_CONTENT",sendInfo.getString("NOTICE_CONTENT"));
			ajaxReturnDatas.add(map);
		} else
		{
			IData map = new DataMap();
			map.put("ret_code", "1");
			map.put("ret_result", "没有配置短信内容！");
			ajaxReturnDatas.add(map);
		}
		
		return ajaxReturnDatas;
	}
	
	// 获取三户资料
	public IDataset getAllInfobySn(IData data) throws Exception
	{
		IData param = new DataMap();
		// param.put(StrUtil.getNotFuzzyKey(), true);
		param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
		param.put("TRADE_TYPE_CODE", PersonConst.TRADE_TYPE_CODE_CREATE_PERSON_USER);
		IDataset datasetUca = CSAppCall.call("CS.GetInfosSVC.getUCAInfos", param);

		return datasetUca;
	}
	
	/**
	 * 获取历史消费信息
	 */
	public IDataset getHistoryConsumeInfo(IData param) throws Exception
	{

		IData inparam = new DataMap();
		IDataset ret = new DatasetList();
		IData retData = new DataMap();
		
		String historyConsume = "";

		inparam.put("USER_ID", param.getString("USER_ID"));
		inparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
		inparam.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));
		inparam.put("TRADE_TYPE_CODE_A", param.getString("TRADE_TYPE_CODE_A"));
		inparam.put("Linker", "svcrecomd");

		IDataset historyConsumeInfos = CSAppCall.call("MS.BiIntfOutterSVC.queryMmsfunc", inparam);

		if (IDataUtil.isNotEmpty(historyConsumeInfos))
		{
			IData historyConsumeInfo = historyConsumeInfos.getData(0);

			if (IDataUtil.isNotEmpty(historyConsumeInfo))
			{
				if (!historyConsumeInfo.getString("tag").equals("0"))
				{
					if ("1".equals(historyConsumeInfo.getString("VALUE_FLAG")))
					{
						historyConsume = "该客户是高价值客户，" + "近三个月平均消费金额：" + historyConsumeInfo.getString("AVG_FEE", "0") + "元，近三个月平均新业务消费金额：" + historyConsumeInfo.getString("AVG_NEWFEE", "0") + "元，近三个月平均长途消费金额：" + historyConsumeInfo.getString("AVG_LONGFEE", "0") + "元。";
					} else
					{
						historyConsume = "该客户不是高价值客户，" + "近三个月平均消费金额：" + historyConsumeInfo.getString("AVG_FEE", "0") + "元，近三个月平均新业务消费金额：" + historyConsumeInfo.getString("AVG_NEWFEE", "0") + "元，近三个月平均长途消费金额：" + historyConsumeInfo.getString("AVG_LONGFEE", "0") + "元，用户近三个月平均流量：" + historyConsumeInfo.getString("ATT_DECIMAL1", "0") + "MB。";
					}
					
					retData.put("RET", historyConsume);
					ret.add(retData);
					return ret;
				}				
			}
		}
		
		return ret;
	}
	
	/**
	 * 获取历史推荐信息记录
	 */
	public IDataset getHistoryRecomdInfo(IData input) throws Exception
	{
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxx248 "+input);

		IData inparam = new DataMap();

		IDataset delayList = new DatasetList();
		String cType;
		if(StringUtils.isEmpty(input.getString("CTYPE","0")) || "".equals(input.getString("CTYPE","0"))){
			cType = "0";
		}else{
			cType = input.getString("CTYPE","0");
		}

		String serialNum = input.getString("SERIAL_NUMBER", "");
		String eparchyCode = input.getString("EPARCHY_CODE", "");
		String userId = input.getString("USER_ID", "");

		if (userId.isEmpty() && !serialNum.isEmpty())
		{
			RealTimeMarketingInfoBean bean = new RealTimeMarketingInfoBean();
			IDataset userInfo = bean.getUserInfo(serialNum);
			userId = userInfo.getData(0).getString("USER_ID", "");
		}
		
		inparam.put("USER_ID", userId);
		inparam.put("SERIAL_NUMBER", serialNum);
		inparam.put("EPARCHY_CODE", eparchyCode);
		inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		
		IDataset dealList = new DatasetList();
		IDataset netType = new DatasetList();
		IDataset wideType = new DatasetList();
		IDataset newType = new DatasetList();
		IDataset yuyinType = new DatasetList();
		IDataset rongheType = new DatasetList();

		dealList = RealTimeMarketingInfoQry.getRealTimeMarketingByHUST(inparam);
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxx283 "+dealList);
		if (IDataUtil.isNotEmpty(dealList))
		{
			boolean ascFlag = true;
			for (int i = 0; i < dealList.size(); i++)
			{
				IData recommInfo = dealList.getData(i);
				String businessClass = recommInfo.getString("BUSINESS_CLASS","");
				if("1".equals(businessClass)){
					recommInfo.put("BUSINESS_CLASS", "流量");
					netType.add(recommInfo);
				}else if("2".equals(businessClass)){
					recommInfo.put("BUSINESS_CLASS", "宽带");
					wideType.add(recommInfo);
				}else if("3".equals(businessClass)){
					recommInfo.put("BUSINESS_CLASS", "促销活动");
					newType.add(recommInfo);
				}else if("4".equals(businessClass)){
					recommInfo.put("BUSINESS_CLASS", "语音");
					yuyinType.add(recommInfo);
				}else if("5".equals(businessClass)){
					recommInfo.put("BUSINESS_CLASS", "融合");
					rongheType.add(recommInfo);
				}else{
					recommInfo.put("BUSINESS_CLASS", "");
				}
				String priorityId = recommInfo.getString("PRIORITY_LEVEL");
				if (StringUtils.isBlank(priorityId))
				{
					ascFlag = false;
				}
			}
			logger.debug("RealTimeMarketingInfoSVCxxxxxxxx309 "+netType);
			logger.debug("RealTimeMarketingInfoSVCxxxxxxxx310 "+wideType);
			logger.debug("RealTimeMarketingInfoSVCxxxxxxxx311 "+newType);
			logger.debug("RealTimeMarketingInfoSVCxxxxxxxx312 "+yuyinType);
			logger.debug("RealTimeMarketingInfoSVCxxxxxxxx313 "+rongheType);
			if (ascFlag)
			{
				// 根据优先级展示
				DataHelper.sort(netType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
				DataHelper.sort(wideType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
				DataHelper.sort(newType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
				DataHelper.sort(yuyinType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
				DataHelper.sort(rongheType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);

				
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxx320 "+netType);
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxx321 "+wideType);
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxx322 "+newType);
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxx323 "+yuyinType);
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxx324 "+rongheType);

			}
			// 加入从参数表中得到的短信信息字段
			if("1".equals(cType)){
				delayList.addAll(netType);
			}else if("2".equals(cType)){
				delayList.addAll(wideType);
			}else if("3".equals(cType)){
				delayList.addAll(newType);
			}else if("4".equals(cType)){
				delayList.addAll(yuyinType);
			}else if("5".equals(cType)){
				delayList.addAll(rongheType);
			}
			else if("0".equals(cType)){
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxx333 "+delayList);
				if(IDataUtil.isNotEmpty(netType)){
					delayList.addAll(netType);
				}
				if(IDataUtil.isNotEmpty(wideType)){
					delayList.addAll(wideType);
				}
				if(IDataUtil.isNotEmpty(newType)){
					delayList.addAll(newType);
				}
				if(IDataUtil.isNotEmpty(yuyinType)){
					delayList.addAll(yuyinType);
				}
				if(IDataUtil.isNotEmpty(rongheType)){
					delayList.addAll(rongheType);
				}
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxx343 "+delayList);

			}
		}
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxx348 "+delayList);

		return delayList;
	}

	public IDataset getParaInfo(IData data) throws Exception
	{
		String queryTag = data.getString("QUERY_TAG");
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
		IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", queryTag, tradeTypeCode, "0898");

		return commparaInfos;
	}

	/**
	 * 获取推荐信息
	 */
	public IDataset getRecomdInfo(IData input) throws Exception
	{
		//CTYPE : 0=推荐,1=流量,2=宽带,3=促销,5=语音,6=融合
		
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx367 "+input);
		IData inparam = new DataMap();
		String inMode = CSBizBean.getVisit().getInModeCode();

		IDataset delayList = new DatasetList();
		IDataset temp = new DatasetList();
		String cType;
		if(StringUtils.isEmpty(input.getString("CTYPE","0")) || "".equals(input.getString("CTYPE","0"))){
			cType = "0";
		}else{
			cType = input.getString("CTYPE","0");
		}
		String serialNum = input.getString("SERIAL_NUMBER", "");
		String eparchyCode = input.getString("EPARCHY_CODE", "");
		String userId = input.getString("USER_ID", "");

		if (userId.isEmpty() && !serialNum.isEmpty())
		{
			RealTimeMarketingInfoBean bean = new RealTimeMarketingInfoBean();
			IDataset userInfo = bean.getUserInfo(serialNum);
			userId = userInfo.getData(0).getString("USER_ID", "");
		}
		
		inparam.put("USER_ID", userId);
		inparam.put("SERIAL_NUMBER", serialNum);
		inparam.put("EPARCHY_CODE", eparchyCode);
		inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		IDataset netType = new DatasetList();
		IDataset wideType = new DatasetList();
		IDataset newType = new DatasetList();
		IDataset yuyinType = new DatasetList();
		IDataset rongheType = new DatasetList();
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx400 "+inparam);
		
		temp = RealTimeMarketingInfoQry.getRealTimeMarketingByUST(inparam);
					
		temp = sortlevel(temp, cType);
		if (IDataUtil.isNotEmpty(temp))
		{
			logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx411 "+temp);
			
			boolean ascFlag = true;
			for (int i = 0; i < temp.size(); i++)
			{
				IData recommInfo = temp.getData(i);
				String businessClass = recommInfo.getString("BUSINESS_CLASS","");
				if("1".equals(businessClass)){
					recommInfo.put("BUSINESS_CLASS", "流量");
					netType.add(recommInfo);
					
				}else if("2".equals(businessClass)){
					recommInfo.put("BUSINESS_CLASS", "宽带");
					wideType.add(recommInfo);
				}else if("3".equals(businessClass)){
					recommInfo.put("BUSINESS_CLASS", "促销活动");
					newType.add(recommInfo);
				}else if("4".equals(businessClass)){
					recommInfo.put("BUSINESS_CLASS", "语音");
					yuyinType.add(recommInfo);
				}else if("5".equals(businessClass)){
					recommInfo.put("BUSINESS_CLASS", "融合");
					rongheType.add(recommInfo);
				}			
				else{
					recommInfo.put("BUSINESS_CLASS", "");
				}
				String priorityId = recommInfo.getString("PRIORITY_LEVEL");
				if (StringUtils.isBlank(priorityId))
				{
					ascFlag = false;
				}
			}
			
			/*if (ascFlag)
			{
				// 根据优先级展示
				DataHelper.sort(netType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
				DataHelper.sort(wideType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
				DataHelper.sort(newType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
				
			}*/
			// 加入从参数表中得到的短信信息字段
			if("1".equals(cType)){
				addSMSContent(netType,delayList);
			}else if("2".equals(cType)){
				addSMSContent(wideType,delayList);
			}else if("3".equals(cType)){
				addSMSContent(newType,delayList);
			}else if("4".equals(cType)){
				addSMSContent(yuyinType,delayList);
			}else if("5".equals(cType)){
				addSMSContent(rongheType,delayList);
			}
			else if("0".equals(cType)){
				IDataset allType = new DatasetList();
				//重要：首推列表：展现优先顺序依次为：流量、宽带、语音、融合、促销	，所以以下allType的顺序依据此分类优先顺序add	
				if(IDataUtil.isNotEmpty(netType)){
//					allType.add(netType.getData(0));
					for (int i = 0; i < netType.size(); i++) {//流量
						allType.add(netType.getData(i));
					}
					
				}
				if(IDataUtil.isNotEmpty(wideType)){
//					allType.add(wideType.getData(0));
					for (int i = 0; i < wideType.size(); i++) {//宽带
						allType.add(wideType.getData(i));
					}
				}
				if(IDataUtil.isNotEmpty(yuyinType)){
//					allType.add(newType.getData(0));
					for (int i = 0; i < yuyinType.size(); i++) {//语音
						allType.add(yuyinType.getData(i));
					}
				}
				if(IDataUtil.isNotEmpty(rongheType)){
//					allType.add(newType.getData(0));
					for (int i = 0; i < rongheType.size(); i++) {//融合
						allType.add(rongheType.getData(i));
					}
				}
				if(IDataUtil.isNotEmpty(newType)){
//					allType.add(newType.getData(0));
					for (int i = 0; i < newType.size(); i++) {//促销
						allType.add(newType.getData(i));
					}
				}
				addSMSContent(allType,delayList);
			}
		}else{
			logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx466 "+temp);
			
			if(CSBizBean.getVisit().getStaffId().contains("HNYD"))
			{
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx470 "+temp);

				// 入营销触点表初始化状态0,调用营销平台状态1
				IData ins = new DataMap();
				String tradeId = SeqMgr.getTradeIdFromDb();
				String month = StrUtil.getAcceptMonthById(tradeId);
				ins.put("REQ_ID", tradeId);
				ins.put("ACCEPT_MONTH", month);
				ins.put("ACCEPT_DATE", SysDateMgr.getSysTime());
				ins.put("TRADE_TYPE_CODE", "521");
				ins.put("IN_MODE_CODE", inMode);
				ins.put("USER_ID", userId);
				ins.put("SERIAL_NUMBER", serialNum);
				ins.put("NET_TYPE_CODE", input.getString("NET_TYPE_CODE", "00"));
				ins.put("EPARCHY_CODE", input.getString("EPARCHY_CODE", "0898"));
				ins.put("CITY_CODE", input.getString("CITY_CODE", ""));
				ins.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
				ins.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
				ins.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
				ins.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
				ins.put("PROCESS_TAG", "0");
				ins.put("PROCESS_TIME", SysDateMgr.getSysTime());// 发送营销平台时间
				Dao.executeUpdateByCodeCode("TL_O_REALTIMEMARKETING", "INS_TL_O_REALTIMEMARKETING", ins);
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx496 "+ins);
				
				// 调实时营销接口begin
				IData send = new DataMap();
				send.put("SERIAL_NUMBER", serialNum);
				send.put("TRADEID", tradeId);
				send.put("USERTYPE", "1");
				send.put("MSGTYPE", "CrmRequestMessage");
				send.put("CITYCODE", input.getString("CITY_CODE", ""));
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx506 "+send);
				
				String httpreturn = newlandcomputerIntf(send);
				// 调实时营销接口end
				//String httpreturn ="";
				// 入库实时营销活动字表begin
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx510 "+httpreturn);
				
				realtimemarketingTradeKF(serialNum, tradeId, month, httpreturn);
				
				/*if ("0".equals(cType)) {
					temp = RealTimeMarketingInfoQry.getRealTimeMarketingByUST_1(inparam);
				} else {
					temp = RealTimeMarketingInfoQry.getRealTimeMarketingByUST(inparam);
				}*/
				
				temp = RealTimeMarketingInfoQry.getRealTimeMarketingByUST(inparam);
				temp = sortlevel(temp, cType);
				
				logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx515 "+temp);
				
				if (IDataUtil.isNotEmpty(temp))
				{
					boolean ascFlag = true;
					for (int i = 0; i < temp.size(); i++)
					{
						IData recommInfo = temp.getData(i);
						String businessClass = recommInfo.getString("BUSINESS_CLASS","");
						if("1".equals(businessClass)){
							recommInfo.put("BUSINESS_CLASS", "流量");
							netType.add(recommInfo);
						}else if("2".equals(businessClass)){
							recommInfo.put("BUSINESS_CLASS", "宽带");
							wideType.add(recommInfo);
						}else if("3".equals(businessClass)){
							recommInfo.put("BUSINESS_CLASS", "促销活动");
							newType.add(recommInfo);
						}else if("4".equals(businessClass)){
							recommInfo.put("BUSINESS_CLASS", "语音");
							yuyinType.add(recommInfo);
						}else if("5".equals(businessClass)){
							recommInfo.put("BUSINESS_CLASS", "融合");
							rongheType.add(recommInfo);
						}
						else{
							recommInfo.put("BUSINESS_CLASS", "");
						}
						String priorityId = recommInfo.getString("PRIORITY_LEVEL");
						if (StringUtils.isBlank(priorityId))
						{
							ascFlag = false;
						}
					}
					
					if (ascFlag)
					{
						// 根据优先级展示
						DataHelper.sort(netType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
						DataHelper.sort(wideType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
						DataHelper.sort(newType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
						DataHelper.sort(yuyinType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
						DataHelper.sort(rongheType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
					}
					// 加入从参数表中得到的短信信息字段
					if("1".equals(cType)){
						addSMSContent(netType,delayList);
					}else if("2".equals(cType)){
						addSMSContent(wideType,delayList);
					}else if("3".equals(cType)){
						addSMSContent(newType,delayList);
					}else if("4".equals(cType)){
						addSMSContent(yuyinType,delayList);
					}else if("5".equals(cType)){
						addSMSContent(rongheType,delayList);
					}
					else if("0".equals(cType)){
						IDataset allType = new DatasetList();
						//重要：首推列表：展现优先顺序依次为：流量、宽带、语音、融合、促销	，所以以下allType的顺序依据此分类优先顺序add	
						if(IDataUtil.isNotEmpty(netType)){
//							allType.add(netType.getData(0));
							for (int i = 0; i < netType.size(); i++) {//流量
								allType.add(netType.getData(i));
							}
						}
						if(IDataUtil.isNotEmpty(wideType)){
//							allType.add(wideType.getData(0));
							for (int i = 0; i < wideType.size(); i++) {//宽带
								allType.add(wideType.getData(i));
							}
						}
						if(IDataUtil.isNotEmpty(yuyinType)){

							for (int i = 0; i < yuyinType.size(); i++) {//语音
								allType.add(yuyinType.getData(i));
							}
						}
						if(IDataUtil.isNotEmpty(rongheType)){

							for (int i = 0; i < rongheType.size(); i++) {//融合
								allType.add(rongheType.getData(i));
							}
						}
						if(IDataUtil.isNotEmpty(newType)){
//							allType.add(newType.getData(0));
							for (int i = 0; i < newType.size(); i++) {//促销
								allType.add(newType.getData(i));
							}
						}
						addSMSContent(allType,delayList);
					}
				}
			}
			logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx572 "+delayList);
			
		}
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx567 "+delayList);

		return delayList;
	}

	private IDataset sortlevel(IDataset temp, String cType) throws Exception {
		//过滤重复数据
		if (1 == 1) {
			IDataset nocongfulist = new DatasetList();
			List<Integer> congfuindexlist = new ArrayList<Integer>();

			for (int i = 0; i < temp.size(); i++) {
				IData record1 = temp.getData(i);
				String PRIORITY_LEVEL = record1.getString("PRIORITY_LEVEL", "").trim();
				String SALE_ACT_ID = record1.getString("SALE_ACT_ID", "").trim();
				String TITLE_NAME = record1.getString("TITLE_NAME", "").trim();
				String SALE_ACT_SCRIPT = record1.getString("SALE_ACT_SCRIPT", "").trim();
				String RSRV_STR1 = record1.getString("RSRV_STR1", "").trim();
				String SMS_SCRIPT = record1.getString("SMS_SCRIPT", "").trim();
				String PRO_TYPE_CODE = record1.getString("PRO_TYPE_CODE", "").trim();
				String STEP_ID = record1.getString("STEP_ID", "").trim();

				for (int j = i + 1; j < temp.size(); j++) {
					IData record2 = temp.getData(j);
					String PRIORITY_LEVEL2 = record2.getString("PRIORITY_LEVEL", "").trim();
					String SALE_ACT_ID2 = record2.getString("SALE_ACT_ID", "").trim();
					String TITLE_NAME2 = record2.getString("TITLE_NAME", "").trim();
					String SALE_ACT_SCRIPT2 = record2.getString("SALE_ACT_SCRIPT", "").trim();
					String RSRV_STR12 = record2.getString("RSRV_STR1", "").trim();
					String SMS_SCRIPT2 = record2.getString("SMS_SCRIPT", "").trim();
					String PRO_TYPE_CODE2 = record2.getString("PRO_TYPE_CODE", "").trim();
					String STEP_ID2 = record2.getString("STEP_ID", "").trim();

					if (PRIORITY_LEVEL.equals(PRIORITY_LEVEL2) && SALE_ACT_ID.equals(SALE_ACT_ID2) && TITLE_NAME.equals(TITLE_NAME2) && SALE_ACT_SCRIPT.equals(SALE_ACT_SCRIPT2) && RSRV_STR1.equals(RSRV_STR12) && SMS_SCRIPT.equals(SMS_SCRIPT2) && PRO_TYPE_CODE.equals(PRO_TYPE_CODE2) && STEP_ID.equals(STEP_ID2)) {
						congfuindexlist.add(j);
					}
				}
			}

			for (int i = 0; i < temp.size(); i++) {
				if (!congfuindexlist.contains(i)) {
					nocongfulist.add(temp.getData(i));
				}
			}
			temp = nocongfulist;
		}
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx726 "+temp);

		IDataset finallist = new DatasetList();
		List<String> prioritylevelnetlist = new ArrayList<String>();
		List<String> prioritylevelwidelist = new ArrayList<String>();
		List<String> prioritylevelnewlist = new ArrayList<String>();
		List<String> prioritylevelyuyinlist = new ArrayList<String>();
		List<String> prioritylevelronghelist = new ArrayList<String>();

		
		IDataset staticInfo = CommparaInfoQry.getCommNetInfo("CSM", "6534", "RealTimeMarketing_PRIORITY_LEVEL");
		int showlevel = staticInfo.getData(0).getInt("PARA_CODE1");
		
		//获取各个分类的前N级别
		for (int i = 0; i < temp.size(); i++) {
			IData recommInfo = temp.getData(i);
			String businessClass = recommInfo.getString("BUSINESS_CLASS", "");
			String priorityId = recommInfo.getString("PRIORITY_LEVEL");
			if ("1".equals(businessClass)) {// 流量
				if (prioritylevelnetlist.size() < showlevel) {
					if (!prioritylevelnetlist.contains(priorityId)) {
						prioritylevelnetlist.add(priorityId);
					}
				}
			} else if ("2".equals(businessClass)) {// 宽带
				if (prioritylevelwidelist.size() < showlevel)
					if (!prioritylevelwidelist.contains(priorityId)) {						
							prioritylevelwidelist.add(priorityId);						
					}
			} else if ("3".equals(businessClass)) {// 促销活动
				if (prioritylevelnewlist.size() < showlevel) {
					if (!prioritylevelnewlist.contains(priorityId)) {
						prioritylevelnewlist.add(priorityId);
					}
				}
			}else if ("4".equals(businessClass)) {// 语音
				if (prioritylevelyuyinlist.size() < showlevel) {
					if (!prioritylevelyuyinlist.contains(priorityId)) {
						prioritylevelyuyinlist.add(priorityId);
					}
				}
			}else if ("5".equals(businessClass)) {// 融合
				if (prioritylevelronghelist.size() < showlevel) {
					if (!prioritylevelronghelist.contains(priorityId)) {
						prioritylevelronghelist.add(priorityId);
					}
				}
			}
		}
		
		if ("0".equals(cType)) {// 只显示优先级最高的一条
			
			for (int i = 0; i < temp.size(); i++) {
				IData recommInfo = temp.getData(i);
				String businessClass = recommInfo.getString("BUSINESS_CLASS", "");
				String priorityId = recommInfo.getString("PRIORITY_LEVEL");

				if ("1".equals(businessClass)) {// 流量					 
					if(prioritylevelnetlist.get(0).equals(priorityId)){						
						finallist.add(recommInfo);
					}
				} else if ("2".equals(businessClass)) {// 宽带					 
					if(prioritylevelwidelist.get(0).equals(priorityId)){
						finallist.add(recommInfo);
					}
				} else if ("3".equals(businessClass)) {// 促销活动					 
					if(prioritylevelnewlist.get(0).equals(priorityId)){
						finallist.add(recommInfo);
					}
				} else if ("4".equals(businessClass)) {// 语音					 
					if(prioritylevelyuyinlist.get(0).equals(priorityId)){
						finallist.add(recommInfo);
					}
				} else if ("5".equals(businessClass)) {// 融合					 
					if(prioritylevelronghelist.get(0).equals(priorityId)){
						finallist.add(recommInfo);
					}
				}
			}

		} else {//级别排名在前几位才显示									
			 						
			for (int i = 0; i < temp.size(); i++) {
				IData recommInfo = temp.getData(i);
				String businessClass = recommInfo.getString("BUSINESS_CLASS", "");
				String priorityId = recommInfo.getString("PRIORITY_LEVEL");

				if ("1".equals(businessClass)) {// 流量				
					if(prioritylevelnetlist.contains(priorityId)){
						finallist.add(recommInfo);
					}
				} else if ("2".equals(businessClass)) {// 宽带					 
					if(prioritylevelwidelist.contains(priorityId)){
						finallist.add(recommInfo);
					}
				} else if ("3".equals(businessClass)) {// 促销活动					 
					if(prioritylevelnewlist.contains(priorityId)){
						finallist.add(recommInfo);
					}
				}else if ("4".equals(businessClass)) {// 语音					 
					if(prioritylevelyuyinlist.contains(priorityId)){
						finallist.add(recommInfo);
					}
				}else if ("5".equals(businessClass)) {// 融合					 
					if(prioritylevelronghelist.contains(priorityId)){
						finallist.add(recommInfo);
					}
				}
			}
			
		}
		
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx407 "+finallist);
		temp = finallist;
		return temp;
	}
	
	/**
	 * 加入从参数表中得到的短信信息字段
	 * @param netType
	 * @param delayList
	 * @throws Exception
	 */
	private void addSMSContent(IDataset netType, IDataset delayList) throws Exception {

		for (int i = 0; i < netType.size(); i++)
		{
			IData recommInfo = netType.getData(i);
						
			String recomdCityCode = recommInfo.getString("CITY_CODE");
			if (StringUtils.isNotBlank(recomdCityCode))
			{
				// 异地推荐业务不能受理
				recommInfo = transRecommInfo(recommInfo);
				delayList.add(recommInfo);
			} else
			{
				recommInfo = transRecommInfo(recommInfo);
				delayList.add(recommInfo);
			}
		}
	}
	
	public IData transRecommInfo(IData recommInfo) throws Exception
	{
		
		if (StringUtils.isNotEmpty(recommInfo.getString("SALE_ACT_SCRIPT", "")))
		{
			// 原本使用的是TF_SM_BI_USEROBJGROUP的推荐用语，如果TF_SM_BI_BUSIMANAGE推荐用语有值，替换之。
			recommInfo.remove("TEMPLET_CONTENT");
			recommInfo.put("TEMPLET_CONTENT", recommInfo.getString("SALE_ACT_SCRIPT"));
		}

		if (StringUtils.isNotEmpty(recommInfo.getString("SMS_SCRIPT")))
		{
			recommInfo.put("NOTICE_CONTENT", recommInfo.getString("SMS_SCRIPT"));// 使用经分同步短信内容
		} else
		{
			recommInfo.put("NOTICE_CONTENT", "业务编码：" + recommInfo.getString("OBJECT_ID", "") + "短信信息没有配置，请先配置短信信息！");
		}

		// 根据业务类型匹配URL跳转地址
		String tradeTypeCode = recommInfo.getString("TRADE_TYPE_CODE", "");
		
		if (StringUtils.isNotBlank(tradeTypeCode))
		{
			String elementId = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
			{ "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[]
			{ "NEW_RECOMD_URL", tradeTypeCode });
			
			String url = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_MODFILE", "MOD_CODE", "MOD_NAME", elementId);
			
			String title = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_MODFILE", "MOD_CODE", "REMARK", elementId);
			
			recommInfo.put("MOD_NAME", url);
			recommInfo.put("OBJECT_TYPE_DESC", title);
		}
		return recommInfo;
	}

	public IDataset getRecomdString(IData input) throws Exception
	{
		RealTimeMarketingInfoBean bean = (RealTimeMarketingInfoBean) BeanManager.createBean(RealTimeMarketingInfoBean.class);
		IDataset results = bean.getRecomdString(input);

		return results;
	}

	public IDataset getRefuseInfo(IData input) throws Exception
	{
		IDataset results = StaticInfoQry.getStaticValueByTypeId("REFUSE_REASON_CODE");

		return results;
	}

	public IData getTitleInfo(IData data) throws Exception
	{
		String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
		String elementId = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
		{ "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[]
		{ "NEW_RECOMD_URL", tradeTypeCode });

		String title = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_MODFILE", "MOD_CODE", "REMARK", elementId);
		IData ret = new DataMap();
		ret.put("TITLE", title);
		return ret;
	}

	/**
	 * 查询流量套餐和加油包
	 */
	public IDataset getUseOwnInfo(IData param) throws Exception
	{
		IDataset ret = new DatasetList();
		IData output = new DataMap();

		IDataset tmp = UserDiscntInfoQry.qryUserOwn(param.getString("USER_ID"), "5", CSBizBean.getVisit().getStaffEparchyCode());// 流量套餐
		IDataset tmp1 = UserDiscntInfoQry.qryUserOwn(param.getString("USER_ID"), "8", CSBizBean.getVisit().getStaffEparchyCode());// 加油包

		if (IDataUtil.isNotEmpty(tmp))
		{
			output.put("Datadicnt", tmp.getData(0).getString("ELEMENT_NAME"));
		}
		
		if (IDataUtil.isNotEmpty(tmp1))
		{
			String Adddicnt = "";

			for (int i = 0; i < tmp1.size(); i++)
			{
				Adddicnt += tmp1.getData(i).getString("ELEMENT_NAME");

				if (i != tmp1.size() - 1)
				{
					Adddicnt += "/";
				}
			}
			
			output.put("Adddicnt", Adddicnt);
		}
		
		ret.add(output);
		
		return ret;
		
	}

	/**
	 * 获取终端信息
	 */
	public IDataset getZhongDuanInfo(IData input) throws Exception
	{
		IData inparam = new DataMap();
		IDataset dealList = new DatasetList();

		inparam.put("USER_ID", input.getString("USER_ID"));
		inparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		inparam.put("EPARCHY_CODE", input.getString("EPARCHY_CODE"));
		inparam.put("TRADE_TYPE_CODE_A", input.getString("TRADE_TYPE_CODE_A"));
		inparam.put("Linker", "svcrecomd");

		dealList = CSAppCall.call("MS.BiIntfOutterSVC.queryMmsfunc", inparam);

		return dealList;
	}

	/**
	 * 获取用户ＶＩＰ等级名称
	 */
	public IDataset Vipquery(IData param) throws Exception
	{
		IDataset ret = new DatasetList();
		IData result = new DataMap();
		IData data = new DataMap();
		String vip_type_name = "";
		// 取大客户资料
		data.clear();
		IDataset vipids = CustVipInfoQry.getCustVipByUserId(param.getString("USER_ID"), "0", CSBizBean.getVisit().getStaffEparchyCode());
		IData vipres = new DataMap();

		if (IDataUtil.isEmpty(vipids))
		{
			return null;
		} else
		{
			vipres = (IData) (vipids.get(0));

			// 获取大客户等级名称
			String svc = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(vipres.getString("VIP_TYPE_CODE"), vipres.getString("VIP_CLASS_ID"));
			if (StringUtils.isBlank(svc))
			{
				result.put("CLASS_NAME", "");
			} else
			{
				result.put("CLASS_NAME", svc);
			}

			String vip_type_code = vipres.getString("VIP_TYPE_CODE", "");
			vip_type_name = UVipTypeInfoQry.getVipTypeNameByVipTypeCode(vip_type_code);
		}

		data.put("RET", vip_type_name + result.getString("CLASS_NAME", ""));

		ret.add(data);

		return ret;

	}

	/**
	 * 实时营销
	 * <p>
	 * Title: newgetRecomdInfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-7 下午08:25:17
	 */
	public IDataset newgetRecomdInfo(IData input) throws Exception
	{
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx817 "+input);

		boolean flag = BizEnv.getEnvBoolean("crm_realtimemarketing_switch");
		if (flag)
		{
			return null;
		}
		
		if(hasPriv("NGC_KF_CALLCENTER")){
			return null;
		}
		
		String inMode = CSBizBean.getVisit().getInModeCode();
		String serialNum = input.getString("SERIAL_NUMBER", "");
		String userId = input.getString("USER_ID", "");
		String cityCode = input.getString("CITY_CODE", "");
		String tradetypeCode = input.getString("TRADE_TYPE_CODE", "");

		IData tydata = new DataMap();
		tydata.put("SUBSYS_CODE", "CSM");
		tydata.put("PARAM_ATTR", "7474");
		tydata.put("PARAM_CODE", "REALTIMEMARKETINGTRADETYPECODE");
		tydata.put("PARA_CODE1", tradetypeCode);
		IDataset tradetypecodeDS = CommparaInfoQry.getCommparaInfoBy1To7(tydata);
		if (IDataUtil.isNotEmpty(tradetypecodeDS))
		{
			return null;
		}

		IData cdata = new DataMap();
		cdata.put("SUBSYS_CODE", "CSM");
		cdata.put("PARAM_ATTR", "7474");
		cdata.put("PARAM_CODE", "REALTIMEMARKETINGINMODE");
		cdata.put("PARA_CODE1", inMode);
		IDataset inmodecodeDS = CommparaInfoQry.getCommparaInfoBy1To7(cdata);
		if (IDataUtil.isEmpty(inmodecodeDS))
		{
			return null;
		}
		
		if (userId.isEmpty() && !serialNum.isEmpty())
		{
			RealTimeMarketingInfoBean bean = new RealTimeMarketingInfoBean();
			IDataset userInfo = bean.getUserInfo(serialNum);
			userId = userInfo.getData(0).getString("USER_ID", "");
			cityCode = userInfo.getData(0).getString("CITY_CODE","");
		}
		
		// 入营销触点表初始化状态0,调用营销平台状态1
		IData ins = new DataMap();
		String tradeId = SeqMgr.getTradeIdFromDb();
		String month = StrUtil.getAcceptMonthById(tradeId);
		ins.put("REQ_ID", tradeId);
		ins.put("ACCEPT_MONTH", month);
		ins.put("ACCEPT_DATE", SysDateMgr.getSysTime());
		ins.put("TRADE_TYPE_CODE", tradetypeCode);
		ins.put("IN_MODE_CODE", inMode);
		ins.put("USER_ID", userId);
		ins.put("SERIAL_NUMBER", serialNum);
		ins.put("NET_TYPE_CODE", input.getString("NET_TYPE_CODE", "00"));
		ins.put("EPARCHY_CODE", input.getString("EPARCHY_CODE", "0898"));
		ins.put("CITY_CODE", cityCode);
		ins.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		ins.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		ins.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		ins.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		ins.put("PROCESS_TAG", "1");
		ins.put("PROCESS_TIME", SysDateMgr.getSysTime());// 发送营销平台时间
		Dao.executeUpdateByCodeCode("TL_O_REALTIMEMARKETING", "INS_TL_O_REALTIMEMARKETING", ins);
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx1127 "+ins);
		
		// 调实时营销接口begin
		IData send = new DataMap();
		send.put("SERIAL_NUMBER", serialNum);
		send.put("TRADEID", tradeId);
		send.put("USERTYPE", "1");
		send.put("MSGTYPE", "CrmRequestMessage");
		send.put("CITYCODE", input.getString("CITY_CODE", ""));
		String httpreturn = newlandcomputerIntf(send);
		// 调实时营销接口end
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx895 "+httpreturn);
		
		// 入库实时营销活动字表begin
		realtimemarketingTrade(serialNum, tradeId, month, httpreturn);
		// 入库实时营销活动字表end

		// 接触状态 0、已接触 1、已发送营销平台 2、已返回营销内容 3、已推送前台
		return null;
	}
	
	/**
	 * 实时营销调第三方接口
	 * <p>
	 * Title: newlandcomputerIntf
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param serialNum
	 * @param tradeId
	 * @return
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-30 下午12:06:48
	 */
	public String newlandcomputerIntf(IData send) throws Exception
	{
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxx895 "+send);
		
		// 调实时营销接口begin
		IData inParam = new DataMap();
		IData inParamslist = new DataMap();
		IDataset inParams = new DatasetList();
		inParam.put("msg_type", send.getString("MSGTYPE"));
		inParam.put("req_time", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));

		IData reqId = new DataMap();
		reqId.put("REQ_ID",send.getString("TRADEID"));
        IDataset ids = getRealTimeMarketingTradeByRID2(reqId);
		logger.info("RealTimeMarketingInfoSVC1221+++++++++++" + ids);
		if (DataUtils.isNotEmpty(ids) && ids.size() > 0) {
        	IData data = ids.getData(0);
			inParamslist.put("serial_number", data.getString("SERIAL_NUMBER"));
			logger.info("RealTimeMarketingInfoSVC1225+++++++++++" + inParamslist);
		}else {
			inParamslist.put("serial_number", send.getString("SERIAL_NUMBER"));
		}

		if ("CrmRequestMessage".equals(send.getString("MSGTYPE")))
		{
			inParamslist.put("user_type", send.getString("USERTYPE", "1"));// 用户类型：
			// 0、新增用户
			// 1、存量用户
			inParamslist.put("booth_id", "1");// 点位编码
		}
		
		inParamslist.put("channel_type", "6");// 渠道类型:6(固定值)
		inParamslist.put("business_id", send.getString("MAINLOGIN_DEPART_ID",CSBizBean.getVisit().getDepartId()));// 营业厅编码
		inParamslist.put("oper_id", send.getString("MAINLOGIN_STAFF_ID",CSBizBean.getVisit().getStaffId()));// 营业员编码
		inParamslist.put("stream_seq", send.getString("TRADEID"));
		
		if ("FeedBackMainReqMessage".equals(send.getString("MSGTYPE")))
		{
			inParamslist.put("region", send.getString("CITYCODE"));
			inParamslist.put("feedback_time", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
			inParamslist.put("status", "0");
			inParamslist.put("notes", URLEncoder.encode("实时营销主反馈", "utf-8"));
		}
		
		if ("FeedBackMinorReqMessage".equals(send.getString("MSGTYPE")))
		{
			inParamslist.put("region", send.getString("CITYCODE"));
			inParamslist.put("feedback_time", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));

			IDataset sale_act_list = new DatasetList();
			IData list = new DataMap();
			list.put("sale_act_id", send.getString("SALEACTID"));
			list.put("step_id", send.getString("STEPID"));
			list.put("status", send.getString("STATUS"));
			list.put("notes", URLEncoder.encode("实时营销子反馈", "utf-8"));
			sale_act_list.add(list);
			
			inParamslist.put("sale_act_list", sale_act_list);
		}
		
		inParams.add(inParamslist);
		
		inParam.put("param_list", inParams);
		
		String in = inParam.toString();
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxx948 "+send);
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxx949 "+in);
		
		String httpreturn = HttpUtil.sendGet(send.getString("MSGTYPE"), in);
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxx979 "+httpreturn);
		
		if(StringUtils.isBlank(httpreturn))
		{
			return null;
		}
		// 调实时营销接口end
		return httpreturn;
		
	}
	
	public IDataset peripheralnewlandcomputerIntf(IData send) throws Exception
	{	
		IDataset ruturnmsg = new DatasetList();
		
		// 调实时营销接口begin
		IData inParam = new DataMap();
		IData inParamslist = new DataMap();
		IDataset inParams = new DatasetList();
		inParam.put("msg_type", "CrmRequestMessage");
		inParam.put("req_time", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
		
		inParamslist.put("serial_number", send.getString("SERIAL_NUMBER"));
		
		inParamslist.put("user_type", "1");// 用户类型：
			// 0、新增用户
			// 1、存量用户
		inParamslist.put("booth_id", "1");// 点位编码
		
		inParamslist.put("channel_type", "6");// 渠道类型:6(固定值)
		inParamslist.put("business_id", CSBizBean.getVisit().getDepartId());// 营业厅编码
		inParamslist.put("oper_id", CSBizBean.getVisit().getStaffId());// 营业员编码
		inParamslist.put("stream_seq", SeqMgr.getTradeIdFromDb());
		
		inParams.add(inParamslist);
		
		inParam.put("param_list", inParams);
		
		String in = inParam.toString();
		logger.info("调用新大陆入参信息="+in);
		String httpreturn = HttpUtil.sendGet("CrmRequestMessage", in);
		// 调实时营销接口end
		if (StringUtils.isNotBlank(httpreturn))
		{
			
			IData requesrult = new DataMap(httpreturn);
			// 返回结果。定义为： 0成功 1失败 9 超过时效，需要客户端重新注册。注册通过后，重发该条数据请求。
			if ("0".equals(requesrult.getString("ret_code")))
			{
				IDataset param_list = new DatasetList(requesrult.getString("param_list"));
				if (IDataUtil.isNotEmpty(param_list))
				{
					IDataset sale_act_list = new DatasetList(param_list.getData(0).getString("sale_act_list"));
					
					if (IDataUtil.isNotEmpty(sale_act_list))
					{
						DataHelper.sort(sale_act_list, "priority_level", IDataset.TYPE_INTEGER, IDataset.ORDER_DESCEND);
						for (int i = 0; i < sale_act_list.size(); i++)
						{
							IData sal = sale_act_list.getData(i);
							if(i==5)
							{
								break;
							}
							IData msg = new DataMap();
							msg.put("SALE_ACT_SCRIPT", sal.getString("sale_act_script"));
							msg.put("TITLE_NAME", sal.getString("title_name"));
							msg.put("PRIORITY_LEVEL", sal.getString("priority_level"));
							ruturnmsg.add(msg); 
						}
					}
				}
			}
		}
		
		return ruturnmsg;
	}
	
	/**
	 * 入实时营销表
	 * <p>
	 * Title: realtimemarketingTrade
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param serialNum
	 * @param tradeId
	 * @param month
	 * @param httpreturn
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-30 下午12:07:25
	 */
	public void realtimemarketingTrade(String serialNum, String tradeId, String month, String httpreturn) throws Exception
	{
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx1325 "+tradeId);
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxxx1326 "+httpreturn);

		if (StringUtils.isNotBlank(httpreturn))
		{
			IData requesrult = new DataMap(httpreturn);
			// 返回结果。定义为： 0成功 1失败 9 超过时效，需要客户端重新注册。注册通过后，重发该条数据请求。
			if ("0".equals(requesrult.getString("ret_code")))
			{
				IDataset param_list = new DatasetList(requesrult.getString("param_list"));
				if (IDataUtil.isNotEmpty(param_list))
				{
					IDataset sale_act_list = new DatasetList(param_list.getData(0).getString("sale_act_list"));

					if (IDataUtil.isNotEmpty(sale_act_list))
					{
						for (int i = 0; i < sale_act_list.size(); i++)
						{
							IData sal = sale_act_list.getData(i);
							IDataset prod_list = new DatasetList(sal.getString("prod_list"));
							if (IDataUtil.isEmpty(prod_list))
							{
								continue;
							}
							for (int j = 0; j < prod_list.size(); j++)
							{
								IData pl = prod_list.getData(j);
								pl.put("REQ_ID", tradeId);
								pl.put("ACCEPT_MONTH", month);
								pl.put("ACCEPT_DATE", SysDateMgr.getSysTime());
								pl.put("SALE_ACT_ID", sal.getString("sale_act_id"));
								pl.put("SALE_ACT_NAME", sal.getString("sale_act_name"));
								pl.put("ACT_BEGIN_DATE", sal.getString("act_begin_date"));
								pl.put("ACT_END_DATE", sal.getString("act_end_date"));
								pl.put("STEP_ID", sal.getString("step_id"));
								pl.put("SALE_ACT_SCRIPT", sal.getString("sale_act_script"));
								pl.put("SMS_SCRIPT", sal.getString("sms_script"));
								pl.put("SALE_ACT_TYPE", sal.getString("sale_act_type"));
								pl.put("PRIORITY_LEVEL", sal.getString("priority_level"));
								pl.put("TITLE_NAME", sal.getString("title_name"));
								pl.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
								pl.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
								pl.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
								pl.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
								pl.put("PRO_SUB_TYPE", pl.getString("pro_sub_type"));
								pl.put("PRO_TYPE", pl.getString("pro_type"));
								pl.put("PRO_TYPE_CODE", pl.getString("pro_type_code"));
								pl.put("IS_ONE_KEY", pl.getString("is_one_key"));
								pl.put("SMS_PORT", pl.getString("send_sms_port", "10086"));
								pl.put("RSRV_STR1", sal.getString("business_class","0"));
								
								Dao.executeUpdateByCodeCode("TL_O_REALTIMEMARKETING", "INS_TL_O_REALTIMEMARKETINGTRADE", pl);
								logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx1376 "+pl);
								
							}
						}
						
						// 如果营销平台返回数据，修改状态2
						IData up = new DataMap();
						up.put("REQ_ID", tradeId);
						up.put("PROCESS_TAG", "2");
						up.put("EXEC_TIME", SysDateMgr.getSysTime());// 营销平台返回时间
						up.put("EXEC_RESULT", requesrult.getString("ret_code"));// 营销平台返回编码
						up.put("EXEC_DESC", requesrult.getString("ret_desc"));// 营销平台返回信息
						uprealtimemarketinG2(up);
						
						// 调用消息弹窗begin
						IData ins = new DataMap();
						ins.put("REQ_ID", tradeId);
						sendMessage(ins, up, serialNum);
						// 调用消息弹窗end
					}else
					{
						sendMessage(serialNum);
					}
				} else
				{
					sendMessage(serialNum);
				}
			}else
			{
				sendMessage(serialNum);
			}
		} else
		{
			sendMessage(serialNum);
		}
	}
	
	public void realtimemarketingTradeKF(String serialNum, String tradeId, String month, String httpreturn) throws Exception
	{
		if (StringUtils.isNotBlank(httpreturn))
		{
			IData requesrult = new DataMap(httpreturn);
			// 返回结果。定义为： 0成功 1失败 9 超过时效，需要客户端重新注册。注册通过后，重发该条数据请求。
			if ("0".equals(requesrult.getString("ret_code")))
			{
				IDataset param_list = new DatasetList(requesrult.getString("param_list"));
				if (IDataUtil.isNotEmpty(param_list))
				{
					IDataset sale_act_list = new DatasetList(param_list.getData(0).getString("sale_act_list"));

					if (IDataUtil.isNotEmpty(sale_act_list))
					{
						for (int i = 0; i < sale_act_list.size(); i++)
						{
							IData sal = sale_act_list.getData(i);
							IDataset prod_list = new DatasetList(sal.getString("prod_list"));
							if (IDataUtil.isEmpty(prod_list))
							{
								continue;
							}
							for (int j = 0; j < prod_list.size(); j++)
							{
								IData pl = prod_list.getData(j);
								pl.put("REQ_ID", tradeId);
								pl.put("ACCEPT_MONTH", month);
								pl.put("ACCEPT_DATE", SysDateMgr.getSysTime());
								pl.put("SALE_ACT_ID", sal.getString("sale_act_id"));
								pl.put("SALE_ACT_NAME", sal.getString("sale_act_name"));
								pl.put("ACT_BEGIN_DATE", sal.getString("act_begin_date"));
								pl.put("ACT_END_DATE", sal.getString("act_end_date"));
								pl.put("STEP_ID", sal.getString("step_id"));
								pl.put("SALE_ACT_SCRIPT", sal.getString("sale_act_script"));
								pl.put("SMS_SCRIPT", sal.getString("sms_script"));
								pl.put("SALE_ACT_TYPE", sal.getString("sale_act_type"));
								pl.put("PRIORITY_LEVEL", sal.getString("priority_level"));
								pl.put("TITLE_NAME", sal.getString("title_name"));
								pl.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
								pl.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
								pl.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
								pl.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
								pl.put("PRO_SUB_TYPE", pl.getString("pro_sub_type"));
								pl.put("PRO_TYPE", pl.getString("pro_type"));
								pl.put("PRO_TYPE_CODE", pl.getString("pro_type_code"));
								pl.put("IS_ONE_KEY", pl.getString("is_one_key"));
								pl.put("SMS_PORT", pl.getString("send_sms_port", "10086"));
								pl.put("RSRV_STR1", sal.getString("business_class"));
								Dao.executeUpdateByCodeCode("TL_O_REALTIMEMARKETING", "INS_TL_O_REALTIMEMARKETINGTRADE", pl);
								logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx1191 "+pl);

							}
						}
						
						// 如果营销平台返回数据，修改状态2
						IData up = new DataMap();
						up.put("REQ_ID", tradeId);
						up.put("PROCESS_TAG", "3");
						up.put("EXEC_TIME", SysDateMgr.getSysTime());// 营销平台返回时间
						up.put("EXEC_RESULT", requesrult.getString("ret_code"));// 营销平台返回编码
						up.put("EXEC_DESC", requesrult.getString("ret_desc"));// 营销平台返回信息
						up.put("UPDATE_TIME", SysDateMgr.getSysTime());
						logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx1204 "+up);
						
						uprealtimemarketinG2(up);
					}
				}
			}
		}
	}
	
	/**
	 * 调用消息弹窗
	 * <p>
	 * Title: sendMessage
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param ins
	 * @param up
	 * @param serialNum
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-30 下午12:07:52
	 */
	public void sendMessage(IData ins, IData up, String serialNum) throws Exception
	{
		// IDataset topic = new DatasetList();
//		IDataset topic = RealTimeMarketingInfoQry.getRealTimeMarketingBytradeId(ins);
		
		 
		RealTimeMarketingInfoBean bean = new RealTimeMarketingInfoBean();
		IDataset userInfo = bean.getUserInfo(serialNum);
	    String userId = userInfo.getData(0).getString("USER_ID", "");	    
	    ins.put("USER_ID", userId);				    
	    
		ins.put("SERIAL_NUMBER", serialNum);		
		ins.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		ins.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		ins.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		ins.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxx1407 "+ins);
		IDataset topic = RealTimeMarketingInfoQry.getRealTimeMarketingByUST(ins);
		topic = sortlevel(topic, "0");
		IDataset netType = new DatasetList();
		IDataset wideType = new DatasetList();
		IDataset newType = new DatasetList();
		IDataset yuyinType = new DatasetList();
		IDataset rongheType = new DatasetList();
		if (IDataUtil.isNotEmpty(topic))
		{
			boolean ascFlag = true;
			for (int i = 0; i < topic.size(); i++)
			{	
				
				IData recommInfo = topic.getData(i);
								
				//REQ201901070027  关于优化IOP平台弹窗功能界面的需求
				/*if (!recommInfo.getString("PRIORITY_LEVEL","").trim().equals("1")) {// 不是优先级1
					continue;
				}*/
				//REQ201901070027  关于优化IOP平台弹窗功能界面的需求
				
				String businessClass = recommInfo.getString("RSRV_STR1","0");
				if("1".equals(businessClass)){
					if(netType.size()==0){
						netType.add(recommInfo);	
					}					
				}else if("2".equals(businessClass)){					
					if(wideType.size()==0){
						wideType.add(recommInfo);	
					}	
				}else if("3".equals(businessClass)){					
					if(newType.size()==0){
						newType.add(recommInfo);	
					}	
				}else if("4".equals(businessClass)){					
					if(yuyinType.size()==0){
						yuyinType.add(recommInfo);	
					}	
				}else if("5".equals(businessClass)){					
					if(rongheType.size()==0){
						rongheType.add(recommInfo);	
					}	
				}
				String priorityId = recommInfo.getString("PRIORITY_LEVEL");

				if (StringUtils.isBlank(priorityId))
				{
					ascFlag = false;
				}
			}
			/*if (ascFlag)
			{
				// 根据优先级展示
				DataHelper.sort(netType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
				DataHelper.sort(wideType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
				DataHelper.sort(newType, "PRIORITY_LEVEL", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);

			}*/
			IDataset allType = new DatasetList();
			if(IDataUtil.isNotEmpty(netType)){
				allType.add(netType.getData(0));
			}
			if(IDataUtil.isNotEmpty(wideType)){
				allType.add(wideType.getData(0));
			}
			if(IDataUtil.isNotEmpty(newType)){
				allType.add(newType.getData(0));
			}
			if(IDataUtil.isNotEmpty(yuyinType)){
				allType.add(yuyinType.getData(0));
			}
			if(IDataUtil.isNotEmpty(rongheType)){
				allType.add(rongheType.getData(0));
			}
			String topicmsg = "";
			String sasmsg = "";
			String topicsend = "";
			String business = "";
			logger.debug("RealTimeMarketingInfoSVCxxxxxxxx1303 "+allType);
			for (int i = 0; i < allType.size(); i++)
			{
				topicmsg = allType.getData(i).getString("TITLE_NAME", "");
				sasmsg = allType.getData(i).getString("SALE_ACT_SCRIPT", "");
				business = allType.getData(i).getString("RSRV_STR1", "");				 
				
	/*								
		send.put("SERIAL_NUMBER", pageData.getString("AUTH_SERIAL_NUMBER"));
		send.put("CITYCODE", pageData.getString("CITY_CODE"));
		send.put("SALEACTID", pageData.getString("SALE_ACT_ID"));
		send.put("STEPID", pageData.getString("OBJECT_ID"));
		send.put("USER_ID", pageData.getString("USER_ID"));
		send.put("SMS_CONTENT", pageData.getString("SMS_CONTENT", ""));
		send.put("SMS_PORT", pageData.getString("SMS_PORT", ""));
				*/
				String SERIAL_NUMBER = allType.getData(i).getString("SERIAL_NUMBER", "");	
				String CITYCODE = allType.getData(i).getString("CITY_CODE", "");	
				String SALEACTID = allType.getData(i).getString("SALE_ACT_ID", "");	
				String STEPID = allType.getData(i).getString("OBJECT_ID", "");	
				String USER_ID = allType.getData(i).getString("USER_ID", "");	
				String SMS_CONTENT = allType.getData(i).getString("SMS_CONTENT", "");	
				String SMS_PORT = allType.getData(i).getString("SMS_PORT", "");	
				String REQ_ID = allType.getData(i).getString("REQ_ID", "");	
				String PRO_TYPE = allType.getData(i).getString("PRO_TYPE", "");	
				
				if (i == 0)
				{
					topicsend = business+ "|" + topicmsg + "|" + sasmsg + "|" + SERIAL_NUMBER+ "|" + CITYCODE+ "|" + SALEACTID+ "|" + STEPID+ "|" + USER_ID+ "|" + SMS_CONTENT+ "|" + SMS_PORT+ "|" + REQ_ID+ "|" + PRO_TYPE; 
				} else
				{
					topicsend = topicsend + "&" + business+ "|" + topicmsg + "|" + sasmsg+ "|" + SERIAL_NUMBER+ "|" + CITYCODE+ "|" + SALEACTID+ "|" + STEPID+ "|" + USER_ID+ "|" + SMS_CONTENT+ "|" + SMS_PORT+ "|" + REQ_ID+ "|" + PRO_TYPE;
				}
				
			}
			// 消息推送调用方式(java代码示例)：
			IData message = new DataMap();
			message.put("TYPE", "301"); // 实时营销约定为301
			message.put("TOPIC", topicsend);
			// 打开实时营销界面携带的业务参数，参数不能过长(100个字符)，参数如果有中文需要先用java的UrlEncode进行utf-8编码后再拼进消息内容里
			message.put("PARAMS", "/order/order?service=page/realtimemarketing.RealTimeMarketingInfo&MENU_ID=crm9505&SERIAL_NUMBER=" + serialNum);
			logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxxx1497 "+topicsend);
			logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxxx1498 "+message);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("STAFF_ID", CSBizBean.getVisit().getStaffId()); // 传入需要推送给哪个工号，多个工号逗号分隔
			param.put("CONTENT", message);
			
			try
			{
				// String taskLogId =
				// AsyncTaskMultiCast.getInstance().sendAsyncTask(BizConstants.INFO.INFO_COMET_SEND_TASK_ID,
				// param);
				com.ailk.biz.message.MessageFactory.sendMessage(param);
				// if(!StringUtils.isBlank(taskLogId))
				// {
				// 推送营销数据到前台修改状态3
				up.put("PROCESS_TAG", "3");
				up.put("UPDATE_TIME", SysDateMgr.getSysTime());
				uprealtimemarketinG3(up);
				// }
			} catch (IOException e1)
			{
				// Auto-generated catch block
				e1.printStackTrace();
			} // 多播
		}
	}
	
	public void sendMessage(String serialNum) throws Exception
	{
		String topicmsg = "";
		String sasmsg = "";
		String topicsend = "";
		// 消息推送调用方式(java代码示例)：
		IData message = new DataMap();
		message.put("TYPE", "301"); // 实时营销约定为301
		// message.put("TOPIC", topicsend);
		// 打开实时营销界面携带的业务参数，参数不能过长(100个字符)，参数如果有中文需要先用java的UrlEncode进行utf-8编码后再拼进消息内容里
		message.put("PARAMS", "/order/order?service=page/realtimemarketing.RealTimeMarketingInfo&MENU_ID=crm9505&SERIAL_NUMBER=" + serialNum);
		logger.debug("RealTimeMarketingInfoSVCxxxxxxxxxxxxx1678 "+message);

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("STAFF_ID", CSBizBean.getVisit().getStaffId()); // 传入需要推送给哪个工号，多个工号逗号分隔
		param.put("CONTENT", message);
		
		try
		{
			com.ailk.biz.message.MessageFactory.sendMessage(param);
		} catch (IOException e1)
		{
			e1.printStackTrace();
		} // 多播
	}

	public void uprealtimemarketinG(IData up) throws Exception
	{
		RealTimeMarketingInfoQry.uprealtimemarketinG(up);
	}

	public IDataset getRealTimeMarketingTradeByRID(IData data) throws Exception
	{
		return RealTimeMarketingInfoQry.getRealTimeMarketingTradeByRID(data);
	}

	public IDataset getRealTimeMarketingTradeByRID2(IData data) throws Exception
	{
		return RealTimeMarketingInfoQry.getRealTimeMarketingTradeByRID2(data);
	}

	public void uprealtimemarketinG2(IData up) throws Exception
	{
		RealTimeMarketingInfoQry.uprealtimemarketinG2(up);
	}
	
	public void uprealtimemarketinG3(IData up) throws Exception
	{
		RealTimeMarketingInfoQry.uprealtimemarketinG3(up);
	}

	public void uprealtimemarketinTrade(IData up) throws Exception
	{
		RealTimeMarketingInfoQry.uprealtimemarketinTrade(up);
	}

	public IDataset queryComparaByAttrAndCode1(IData up) throws Exception
	{
		return CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "7474", "REALTIMEMARKETINGJUMP", up.getString("PRO_TYPE", "99"));
	}

	public IDataset queryRedirectProduct(IData data) throws Exception
	{
		return CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "7474", "REALTIMEMARKETINGREDIRECT", data.getString("ACT_ID"),data.getString("ACT_TYPE"),"0898");
	}
}
