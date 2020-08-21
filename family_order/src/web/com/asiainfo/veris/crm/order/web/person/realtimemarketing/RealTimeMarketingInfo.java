package com.asiainfo.veris.crm.order.web.person.realtimemarketing;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RealTimeMarketingInfo extends PersonBasePage
{

	/**
	 * 实时营销活动接受按钮
	 * <p>
	 * Title: createDept
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param cycle
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-13 下午08:23:49
	 */
	private static final Logger log = Logger.getLogger(RealTimeMarketingInfo.class); 

	public void Accept(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
		log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx42 "+pageData);
		
		IData send = new DataMap();
		send.put("SERIAL_NUMBER", pageData.getString("AUTH_SERIAL_NUMBER"));
		send.put("TRADEID", pageData.getString("REQ_ID"));
		send.put("CITYCODE", pageData.getString("CITY_CODE"));
		send.put("SALEACTID", pageData.getString("SALE_ACT_ID"));
		send.put("STEPID", pageData.getString("OBJECT_ID"));
		send.put("STATUS", "0");
		send.put("MSGTYPE", "FeedBackMinorReqMessage");
		log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx51 "+send);

		IDataset ds = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.newlandcomputerIntf", send);
		log.info("RealTimeMarketingInfoxxxxxxxxxxxxxxxx57 "+ds);
		String backDoor = pageData.getString("BACK_DOOR","0");
		IDataset comm = new DatasetList();

		if (IDataUtil.isNotEmpty(ds)|| "1".equals(backDoor))
		{
			if ("0".equals(ds.getData(0).getString("ret_code")) || "1".equals(backDoor))
			{
				send.put("PRO_TYPE", pageData.getString("PRO_TYPE", "99"));
				send.put("OTHER_REFUSE_REASON", pageData.getString("OTHER_REFUSE_REASON", ""));
				send.put("REFUSE_REMARK", pageData.getString("REFUSE_REMARK", ""));
				log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx64 "+send);

				IData reqId = new DataMap();
				reqId.put("REQ_ID",pageData.getString("REQ_ID", ""));
				if (StringUtils.isNotBlank(pageData.getString("SALE_ACT_ID", ""))) {
					reqId.put("SALE_ACT_ID",pageData.getString("SALE_ACT_ID", ""));
				}
				log.info("RealTimeMarketingInfoxxxxxxxxxxxxxxxx74 "+reqId);
				IDataset protypecodes = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.getRealTimeMarketingTradeByRID", reqId);
				log.info("RealTimeMarketingInfoxxxxxxxxxxxxxxxx74 "+protypecodes);
				if(IDataUtil.isNotEmpty(protypecodes) && protypecodes.size() > 0)
				{
					IData data = new DataMap();
					data.put("ACT_ID",protypecodes.getData(0).getString("PRO_TYPE_CODE", ""));
					data.put("ACT_TYPE",pageData.getString("PRO_TYPE", ""));
					log.info("RealTimeMarketingInfoxxxxxxxxxxxxxxxx68 "+data);

					IDataset redirectDatas = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.queryRedirectProduct", data);
                    log.info("RealTimeMarketingInfoxxxxxxxxxxxxxxxx83 "+redirectDatas);
					if (IDataUtil.isNotEmpty(redirectDatas) && redirectDatas.size() > 0){
						send.put("PRO_TYPE", redirectDatas.getData(0).getString("PARA_CODE3"));
					}
				}
                log.info("RealTimeMarketingInfoxxxxxxxxxxxxxxxx88 "+send);
				comm = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.queryComparaByAttrAndCode1", send);
				CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.uprealtimemarketinTrade", send);
			}
		}
		log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx70 "+comm);

		if (IDataUtil.isNotEmpty(comm))
		{
			comm.getData(0).put("ret_code", "0");
			setAjax("MESSAGE", comm.getData(0).toString());
		} else
		{
			IData msg = new DataMap();
			msg.put("ret_code", "1");
			comm.add(msg);
			setAjax("MESSAGE", comm.getData(0).toString());
		}
	}
	
	/**
	 * 实时营销活动犹豫按钮
	 * <p>
	 * Title: Hesitate
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param cycle
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-31 下午03:28:49
	 */
	public void Hesitate(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
		
		IData send = new DataMap();
		send.put("SERIAL_NUMBER", pageData.getString("AUTH_SERIAL_NUMBER"));
		send.put("TRADEID", pageData.getString("REQ_ID"));
		send.put("CITYCODE", pageData.getString("CITY_CODE"));
		send.put("SALEACTID", pageData.getString("SALE_ACT_ID"));
		send.put("STEPID", pageData.getString("OBJECT_ID"));
		send.put("STATUS", "2");
		send.put("MSGTYPE", "FeedBackMinorReqMessage");
		send.put("REFUSE_REASON_CODE", pageData.getString("REFUSE_REASON_CODE", "0"));
		
		IDataset ds = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.newlandcomputerIntf", send);
		
		if (IDataUtil.isNotEmpty(ds))
		{
			if ("0".equals(ds.getData(0).getString("ret_code")))
			{
				send.put("OTHER_REFUSE_REASON", pageData.getString("OTHER_REFUSE_REASON", ""));
				send.put("REFUSE_REMARK", pageData.getString("REFUSE_REMARK", ""));
				CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.uprealtimemarketinTrade", send);
			}
		}

		setAjax("MESSAGE", ds.getData(0).toString());
	}

	/**
	 * 实时营销活动拒绝按钮
	 * <p>
	 * Title: Refused
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param cycle
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-31 下午03:29:12
	 */
	public void Refused(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();

		IData send = new DataMap();
		send.put("SERIAL_NUMBER", pageData.getString("AUTH_SERIAL_NUMBER"));
		send.put("TRADEID", pageData.getString("REQ_ID"));
		send.put("CITYCODE", pageData.getString("CITY_CODE"));
		send.put("SALEACTID", pageData.getString("SALE_ACT_ID"));
		send.put("STEPID", pageData.getString("OBJECT_ID"));
		send.put("STATUS", "1");
		send.put("MSGTYPE", "FeedBackMinorReqMessage");
		send.put("REFUSE_REASON_CODE", pageData.getString("REFUSE_REASON_CODE", "0"));

		IDataset ds = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.newlandcomputerIntf", send);

		if (IDataUtil.isNotEmpty(ds))
		{
			if ("0".equals(ds.getData(0).getString("ret_code")))
			{
				send.put("OTHER_REFUSE_REASON", pageData.getString("OTHER_REFUSE_REASON", ""));
				send.put("REFUSE_REMARK", pageData.getString("REFUSE_REMARK", ""));
				send.put("SMS_SCRIPT", ds.getData(0).getString("SMS_SCRIPT"));
				CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.uprealtimemarketinTrade", send);
			}
		}

		setAjax("MESSAGE", ds.getData(0).toString());
	}

	/**
	 * 实时营销活动下发短信按钮
	 * <p>
	 * Title: Sendsms
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param cycle
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-31 下午03:29:27
	 */
	public void Sendsms(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
		log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx202"+pageData);

		IData send = new DataMap();
		send.put("SERIAL_NUMBER", pageData.getString("AUTH_SERIAL_NUMBER"));
		send.put("USER_ID", pageData.getString("USER_ID"));
		//send.put("SMS_CONTENT", pageData.getString("SMS_CONTENT", ""));
		send.put("SMS_PORT", pageData.getString("SMS_PORT", ""));
		send.put("REQ_ID", pageData.getString("REQ_ID", ""));
		send.put("SALE_ACT_ID", pageData.getString("SALE_ACT_ID", ""));
		
		log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx209 "+send);

		IDataset ds = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.createDept", send);
		log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx212 "+ds);

		//REQ201812290025营业厅营销功能优化需求		
		send.clear();
		send.put("SERIAL_NUMBER", pageData.getString("AUTH_SERIAL_NUMBER"));
		send.put("TRADEID", pageData.getString("REQ_ID"));
		send.put("CITYCODE", pageData.getString("CITY_CODE"));
		send.put("SALEACTID", pageData.getString("SALE_ACT_ID"));
		send.put("STEPID", pageData.getString("OBJECT_ID"));
		send.put("STATUS", "3");//下发短信
		send.put("MSGTYPE", "FeedBackMinorReqMessage");
		send.put("REFUSE_REASON_CODE", "0");
		log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx224 "+send);

		IDataset ds2 = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.newlandcomputerIntf", send);
		log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx227 "+ds2);

		//REQ201812290025营业厅营销功能优化需求	
		
		setAjax("MESSAGE", ds.getData(0).toString());
	}
	
	/**
	 * 查询实时营销推荐信息
	 * <p>
	 * Title: loadChildInfo
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * <p>
	 * Company: AsiaInfo
	 * </p>
	 * 
	 * @param cycle
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-3-13 下午03:02:21
	 */
	public void loadChildInfo(IRequestCycle cycle) throws Exception
	{
		IData pagedata = getData();
		log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx253 "+pagedata);
		IData editInfo = new DataMap();
		IData custInfo = new DataMap();
		IData userInfo = new DataMap();

		if (!"1".equals(pagedata.getString("PUSH_FLAG", "")))
		{
			log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx253 "+pagedata);
			custInfo = new DataMap(pagedata.getString("CUST_INFO"));
			userInfo = new DataMap(pagedata.getString("USER_INFO"));
		} else
		{
			String serialNumber = pagedata.getString("AUTH_SERIAL_NUMBER");
			pagedata.put("SERIAL_NUMBER", serialNumber);

			// 获取三户资料
			IDataset datasetUca = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.getAllInfobySn", pagedata);
			userInfo = datasetUca.getData(0).getData("USER_INFO");
			custInfo = datasetUca.getData(0).getData("CUST_INFO");
			log.error("RealTimeMarketingInfoxxxxxxxxxxxxxxxx253 "+pagedata);
		}
		
		// 加载三户资料
		String open_date = userInfo.getString("OPEN_DATE", "").substring(0, 10);
		String current_date = SysDateMgr.getSysTime().toString().substring(0, 10);
		
		if (current_date.equals(open_date))
		{
			userInfo.put("TRADE_TYPE_CODE_A", "A");
		}
		
		userInfo.put("BRAND", UpcViewCall.getBrandNameByBrandCode(this, userInfo.getString("BRAND_CODE")));
		userInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, PersonConst.ELEMENT_TYPE_CODE_PRODUCT, userInfo.getString("PRODUCT_ID")));
		userInfo.put("STATE_NAME", UpcViewCall.queryStateNameBySvcId(this, "0", userInfo.getString("USER_STATE_CODESET")));
		
		editInfo.putAll(custInfo);
		editInfo.putAll(userInfo);
		setEditInfo(editInfo);
		
		// 该用户注销标志不是正常状态,不能进行新业务推荐！
		String remove_tag = userInfo.getString("REMOVE_TAG", "");
		if (!remove_tag.equals("0"))
		{
			CSViewException.apperr(CrmUserException.CRM_USER_538);
		}
		
		IDataset RecomdList = new DatasetList();
		IDataset UseOwnList = new DatasetList();
		IDataset RefuseList = new DatasetList();
		IDataset VipList = new DatasetList();
		IDataset HistoryConsumeList = new DatasetList();
		IDataset ZhongDuanList = new DatasetList();
		IDataset historyConsumeList = new DatasetList();
		IData tempdata = new DataMap();
		IData info = new DataMap();
		String historyConsumeInfo = "";
		
		tempdata.putAll(userInfo);
		tempdata.put("CTYPE", pagedata.getString("CTYPE",""));												
		RecomdList = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.getRecomdInfo", tempdata);
		setRecomdLists(RecomdList);// 获取用户推荐集
		
		RefuseList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getRefuseInfo", tempdata);
		setForeachs(RefuseList);

		UseOwnList = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.getUseOwnInfo", tempdata);
		
		IData info1 = new DataMap();
		if (IDataUtil.isNotEmpty(UseOwnList))
		{
			for (int i = 0; i < UseOwnList.size(); i++)
			{
				info1.putAll(UseOwnList.getData(i));
			}
		}
		
		VipList = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.Vipquery", tempdata);

		if (IDataUtil.isNotEmpty(VipList))
		{
			for (int i = 0; i < VipList.size(); i++)
			{
				if (StringUtils.isBlank(VipList.getData(i).getString("RET")))
				{
					info1.put("vipinfo", VipList.getData(i).getString("RET"));
				}
			}
		}

		HistoryConsumeList = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.getHistoryConsumeInfo", tempdata);

		if (IDataUtil.isNotEmpty(HistoryConsumeList))
		{
			for (int i = 0; i < HistoryConsumeList.size(); i++)
			{
				if (StringUtils.isNotBlank(HistoryConsumeList.getData(i).getString("RET")))
				{
					historyConsumeInfo = HistoryConsumeList.getData(i).getString("RET");
				}
			}
		}

		ZhongDuanList = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.getZhongDuanInfo", tempdata);

		if (IDataUtil.isNotEmpty(ZhongDuanList))
		{
			IData zhongDuanInfo = ZhongDuanList.getData(0);

			if (IDataUtil.isNotEmpty(zhongDuanInfo))
			{
				if (!zhongDuanInfo.getString("tag").equals("0"))
				{
					info.put("history_CONSUME_ACTION", historyConsumeInfo);
					info.put("PHONE_QUALITY_NAME", zhongDuanInfo.getString("PHONE_QUALITY_NAME"));
					info.put("PHONE_MODEL_NAME", zhongDuanInfo.getString("PHONE_MODEL_NAME"));
					setInfo(info);
				}
			}
		}
		
		historyConsumeList = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.getHistoryRecomdInfo", tempdata);

		setHistoryConsumeList(historyConsumeList);

		setInfo1(info1);// 《新业务推荐受理》界面优化

		// 调用新大陆http接口进行主反馈
		if (IDataUtil.isNotEmpty(RecomdList))
		{
			if (!"0".equals(RecomdList.getData(0).getString("APROCESS_TAG")))
			{
				IData send = new DataMap();
				send.put("SERIAL_NUMBER", RecomdList.getData(0).getString("SERIAL_NUMBER"));
				send.put("TRADEID", RecomdList.getData(0).getString("REQ_ID"));
				send.put("CITYCODE", RecomdList.getData(0).getString("CITY_CODE"));
				send.put("MSGTYPE", "FeedBackMainReqMessage");

				IDataset ds = CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.newlandcomputerIntf", send);

				if (IDataUtil.isNotEmpty(ds))
				{
					if ("0".equals(ds.getData(0).getString("ret_code")))
					{
						CSViewCall.call(this, "SS.RealTimeMarketingInfoSVC.uprealtimemarketinG", send);
					}
				}
			}
		}
	}
	
	public abstract void setEditInfo(IData editInfo);

	public abstract void setForeachs(IDataset ForeachList);

	public abstract void setHistoryConsumeList(IDataset historyConsumeList);

	public abstract void setInfo(IData info);

	public abstract void setInfo1(IData info1);

	public abstract void setRecomdLists(IDataset productList);
	
}
