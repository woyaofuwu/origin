package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.trade;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.DestroyUserComm;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.requestdata.DestroyUserNowReqData;

public class DestroyUserNowTrade extends BaseTrade implements ITrade
{
	/**
	 * 实现父类抽象方法
	 */
	public void createBusiTradeData(BusiTradeData btd) throws Exception
	{
		this.createClearScoreTrade(btd);
		// 用户相关资料处理
		this.createEndUserInfoTrade(btd);
		// 修改用户主体服务
		this.ModifyMainSvcStateByUserid(btd);// 一定要放在服务状态变更订单生成之后做。
		// 信控欠费销户和携出销户设置主台账字段SubscribeType和执行时间
		this.modifyMainTrade(btd);
		this.createOtherTrade(btd);
	}

	/**
	 * 构造积分扣减台账
	 * 
	 * @param reqData
	 * @param btd
	 * @throws Exception
	 */
	private void createClearScoreTrade(BusiTradeData<BaseTradeData> btd) throws Exception
	{
		DestroyUserNowReqData reqData = (DestroyUserNowReqData) btd.getRD();
		String netTypeCode = reqData.getUca().getUser().getNetTypeCode();
		if (StringUtils.equals("18", netTypeCode))
		{
			return;
		} // 无线固话无积分
		String userId = reqData.getUca().getUserId();
		int oldScore = 0;
		IDataset scoreDataset = AcctCall.queryUserScore(userId);
		if (IDataUtil.isNotEmpty(scoreDataset))
		{
			oldScore = Integer.parseInt(scoreDataset.getData(0).getString("SCORE_VALUE", "0"));
		}
		if (oldScore > 0)
		{
			ScoreTradeData scoreTD = new ScoreTradeData();
			scoreTD.setUserId(userId);
			scoreTD.setCancelTag("0");
			scoreTD.setSerialNumber(btd.getRD().getUca().getSerialNumber());
			scoreTD.setScore(String.valueOf(oldScore));// 原积分
			scoreTD.setScoreTag("0");// 清理标识
			scoreTD.setScoreChanged(String.valueOf(-oldScore));// 积分改变
			scoreTD.setRemark("立即销户积分清零");
			btd.add(btd.getRD().getUca().getSerialNumber(), scoreTD);
		}
	}

	// 终止用户相关资料订单
	private void createEndUserInfoTrade(BusiTradeData<BaseTradeData> btd) throws Exception
	{
		DestroyUserComm destroyComm = new DestroyUserComm();
		destroyComm.createEndRelationUUTrade(btd);// uu关系

		destroyComm.createEndUserTrade(btd);// 用户
		modifyUserRemoveReasonCode(btd);
		destroyComm.createEndSvcInfoTrade(btd);// 服务
		destroyComm.createEndDiscntInfoTrade(btd);// 优惠
		destroyComm.createEndProductTrade(btd);// 产品
		destroyComm.createEndAttrInfoTrade(btd);// 属性
		destroyComm.createEndResInfoTrade(btd);// 资源
		destroyComm.createEndElementInfo(btd);// 元素
		destroyComm.createEndOtherTrade(btd);// 其他信息
		destroyComm.createEndShareRelaInfoTrade(btd);// 共享关系
		destroyComm.createEndSaleActiveTrade(btd);// 营销活动
		destroyComm.createEndScoreAcctAndPlanTrade(btd); // 积分账户

		// destroyComm.createEndUserNetNpTrade(btd);// 销户不需要终止携转资料，号码回收时才终止
		if (!StringUtils.equals("42", btd.getTradeTypeCode())) // 携出销户不中止付费关系
		{
			destroyComm.createEndPayRelationTradeByRelaUU(btd);// 付费关系
		}
	}

	/**
	 * 构建服务状态变更订单表
	 * 
	 * @param btd
	 * @throws Exception
	 */
	private void ModifyMainSvcStateByUserid(BusiTradeData btd) throws Exception
	{
		ChangeSvcStateComm bean = new ChangeSvcStateComm();
		bean.modifyMainSvcStateByUserId(btd);
	}

	/**
	 * @Function: modifyUserRemoveReasonCode
	 * @Description: 设置销户原因
	 * @param btd
	 * @throws Exception
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2014-6-24 下午7:32:43
	 */
	@SuppressWarnings(
	{ "unused", "rawtypes" })
	private void modifyUserRemoveReasonCode(BusiTradeData btd) throws Exception
	{
		List<UserTradeData> utds = btd.get("TF_B_TRADE_USER");

		if (utds != null)
		{
			DestroyUserNowReqData destroyUserNowReqData = (DestroyUserNowReqData) btd.getRD();
			String tradeTypeCode = btd.getTradeTypeCode();
			String strRemoveReasonCode = destroyUserNowReqData.getRemoveReasonCode();
			if (StringUtils.equals("7230", tradeTypeCode)) // 欠费预销
			{
				strRemoveReasonCode = "17";
			} else if (StringUtils.equals("7240", tradeTypeCode) || StringUtils.equals("7241", tradeTypeCode) || StringUtils.equals("7242", tradeTypeCode) || StringUtils.equals("7243", tradeTypeCode) || StringUtils.equals("7244", tradeTypeCode)) // 欠费注销和3种宽带销户
			{
				strRemoveReasonCode = "18";
			} else if (StringUtils.equals("100", tradeTypeCode))// 过户注销
			{
				strRemoveReasonCode = "16";
			} else if (StringUtils.equals("234", tradeTypeCode))// 遗失卡
			{
				strRemoveReasonCode = "1";
			} else if (StringUtils.equals("1512", tradeTypeCode))// 旅信通销户
			{
				strRemoveReasonCode = "00";
			} else if (StringUtils.equals("47", tradeTypeCode) || StringUtils.equals("48", tradeTypeCode))// 遗失卡
			{
				// 携出欠费注销
			}

			if (StringUtils.isNotEmpty(strRemoveReasonCode))
			{
				btd.getMainTradeData().setRsrvStr2(strRemoveReasonCode);
				String removeReason = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_REMOVE_REASON", "REMOVE_REASON_CODE", "REMOVE_REASON", strRemoveReasonCode);
				btd.getMainTradeData().setRsrvStr8(removeReason);

				destroyUserNowReqData.setRemoveReasonCode(strRemoveReasonCode);
				for (UserTradeData utd : utds)
				{
					utd.setRemoveReasonCode(strRemoveReasonCode);
				}
			}
		}

	}

	private void modifyMainTrade(BusiTradeData btd) throws Exception
	{
		MainTradeData mainTradeData = btd.getMainTradeData();
		String strBrandCode = mainTradeData.getBrandCode();
		if ("PWLW".equals(strBrandCode) || "WLWG".equals(strBrandCode))
		{
			String strUserId = mainTradeData.getUserId();
			IDataset result = RelaUUInfoQry.qryByRelaUserIdB(strUserId, "9A", null);
			if (IDataUtil.isNotEmpty(result))
			{
				IData rela = result.getData(0);
				String userIdA = rela.getString("USER_ID_A", "");
				IData userData = UcaInfoQry.qryUserInfoByUserId(userIdA);
				if (IDataUtil.isNotEmpty(userData))
				{
					String strCustIdB = userData.getString("CUST_ID", "");
					mainTradeData.setCustIdB(strCustIdB);
					mainTradeData.setUserIdB(userIdA);
				}
			} else
			{
				mainTradeData.setCustIdB("-1");
				mainTradeData.setUserIdB("-1");
			}
		}
		String tradeTypeCode = btd.getTradeTypeCode();
		if (StringUtils.equals("47", tradeTypeCode) || StringUtils.equals("48", tradeTypeCode))
		{
			mainTradeData.setExecTime(SysDateMgr.END_DATE_FOREVER);
			String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
			mainTradeData.setRemark(tradeTypeName);
			mainTradeData.setSubscribeType("200");
		} else if (StringUtils.equals("7240", tradeTypeCode))
		{
			mainTradeData.setSubscribeType("200");
		}

		DestroyUserNowReqData destroyUserNowReqData = (DestroyUserNowReqData) btd.getRD();
		if (StringUtils.equals("1", destroyUserNowReqData.getActiveTag()))
		{
			mainTradeData.setRsrvStr10("1"); // 未激活买断卡销户标记
		}

		// 针对携入销户、携入欠费销户标志RSRV_STR8字段为1，提供给PF修改销户指令，更新成停机指令 tanjl add by
		// 2015-03-16 begin
		IDataset userNpInfos = UserNpInfoQry.qryUserNpInfosByUserId(btd.getRD().getUca().getUserId());
		if (IDataUtil.isNotEmpty(userNpInfos))
		{
			if (StringUtils.equals("7240", tradeTypeCode) || StringUtils.equals("192", tradeTypeCode))
			{
				mainTradeData.setRsrvStr8("1"); // 针对携入销户、携入欠费销户标志RSRV_STR8字段为1，提供给PF修改销户指令，更新成停机指令
			}
		}
		// 针对携入销户、携入欠费销户标志RSRV_STR8字段为1，提供给PF修改销户指令，更新成停机指令 tanjl add by
		// 2015-03-16 end
		/**
		 * REQ201612260011_新增CPE终端退回和销户界面
		 * 
		 * @author zhuoyingzhi
		 * @date 20170215 使用RsrvStr7 为后面action提供判断 押金是原路返回还是押金沉淀
		 */
		String fallbackTag = btd.getRD().getPageRequestData().getString("FALLBACKTAG", "");
		if (StringUtils.equals("192", tradeTypeCode) && !"".equals(fallbackTag))
		{
			// 界面选择是 押金原路返回还是押金沉淀 0是原路返回 1是押金沉淀
			mainTradeData.setRsrvStr7(fallbackTag);
		}
		if (StringUtils.equals("9723", tradeTypeCode))
		{
			mainTradeData.setNetTypeCode("00");
		}
	}

	public void createOtherTrade(BusiTradeData btd) throws Exception
	{
		String tradeTypeCode = btd.getTradeTypeCode();
		if (tradeTypeCode != null && "192".equals(tradeTypeCode.trim()))
		{// 立即销户
			DestroyUserNowReqData reqData = (DestroyUserNowReqData) btd.getRD();
			String serialNumber = reqData.getUca().getUser().getSerialNumber();
			OtherTradeData otherTD = new OtherTradeData();
			/*IData inData = new DataMap();
			inData.put("SERIAL_NUMBER", serialNumber);
			inData.put("QRY_TAG", "1");
			inData.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());// 用户归属地州
			inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 用户交易地州
			inData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());// 受理业务区
			inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());// 受理部门
			inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());// 受理员工
			inData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
			IDataset phones = CSAppCall.call("RM.ResPhoneIntfSvc.getMphonecodeInfo", inData);*/
			IDataset phones = ResCall.getMphonecodeInfo(serialNumber,"1");
			if (DataSetUtils.isNotBlank(phones))
			{
				IData data = phones.getData(0);
				if (data.getString("BEAUTIFUAL_TAG", "").trim().equals("1"))
				{// 是吉祥号
					otherTD.setUserId(btd.getRD().getUca().getUserId());
					otherTD.setRsrvValueCode("ISBEAUTIFUAL");
					otherTD.setRsrvValue(serialNumber);
					otherTD.setRsrvStr1(CSBizBean.getVisit().getStaffName());
					otherTD.setStartDate(SysDateMgr.getSysTime());
					otherTD.setEndDate(SysDateMgr.addYears(SysDateMgr.getSysTime(), 1) + " 00:00:00");
					otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
					otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
					otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
					otherTD.setInstId(SeqMgr.getInstId());
					btd.add(serialNumber, otherTD);
				}
			}
		}
	}
}
