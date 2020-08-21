package com.asiainfo.veris.crm.order.soa.group.imscreditmgr;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

/**
 * 创建集团多媒体桌面电话的信控停开机订单
 * 
 * @author think
 * 
 */
public class ChangeImsGrpDesktopTelSvcElement extends GroupBean 
{
	
	private Logger logger = Logger.getLogger(ChangeImsGrpDesktopTelSvcElement.class);

	private static String newStatecode = "";

	private static String oldStatecode = "";

	private static String stateCode = "";

	public void actTradeSub() throws Exception 
	{
		actTradeDataUser();

		actTradeDataSvcState();
	}

	@Override
	protected void makReqData(IData map) throws Exception 
	{
		super.makReqData(map);

		map.put("CHK_FLAG", "BaseInfo");

		map.put("TRADE_TYPE_CODE", this.getTradeTypeCode());

	}

	@Override
	protected void makUca(IData map) throws Exception 
	{
		super.makUcaForGrpNormal(map);
		
		newStatecode = map.getString("USER_STATE_CODESET");

		stateCode = map.getString("STATE_CODE");

		if (logger.isDebugEnabled())
		{
			logger.debug("<<<<<<<map参数>>>>>>" + map);
			logger.debug("<<<<<<<newStatecode参数>>>>>>" + newStatecode);
			logger.debug("<<<<<<<stateCode参数>>>>>>" + stateCode);
		}
	}

	public void actTradeDataUser() throws Exception 
	{
		IData userInfo = reqData.getUca().getUser().toData();
		oldStatecode = userInfo.getString("USER_STATE_CODESET", "");

		if (logger.isDebugEnabled()) 
		{
			logger.debug("<<<<<<<oldStatecode参数>>>>>>" + oldStatecode);
			logger.debug("<<<<<<<newStatecode参数>>>>>>" + newStatecode);
		}

		if (!oldStatecode.equals(newStatecode)) 
		{
			userInfo.put("USER_STATE_CODESET", newStatecode);
			userInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

			String netTypeCode = UProductInfoQry.getNetTypeCodeByProductId(reqData.getUca().getProductId());

			if (StringUtils.isEmpty(netTypeCode)) 
			{
				netTypeCode = "00";
			}

			userInfo.put("NET_TYPE_CODE", netTypeCode);
		}
		else if("5".equals(oldStatecode) && "5".equals(newStatecode))
		{
			CSAppException.apperr(GrpException.CRM_GRP_713, "当前集团多媒体桌面电话产品已经是停机状态,业务不能继续!");
		}
		else if("0".equals(oldStatecode) && "0".equals(newStatecode))
		{
			CSAppException.apperr(GrpException.CRM_GRP_713, "当前集团多媒体桌面电话产品已经是开机状态,业务不能继续!");
		}

		this.addTradeUser(userInfo);
	}

	public void actTradeDataSvcState() throws Exception 
	{
		String userId = reqData.getUca().getUserId();
		String productId = reqData.getUca().getProductId();
		IDataset result = new DatasetList();

		if ("5".equals(newStatecode) || "05".equals(newStatecode))// 停机
		{
			IDataset idataset = UserSvcStateInfoQry.getUserNextMonthMainSvcStateByUId(userId);

			if (IDataUtil.isEmpty(idataset)) 
			{
				CSAppException.apperr(GrpException.CRM_GRP_713, "当前[" + productId + "]没有配置主体服务!");
			}

			// 截止老服务
			IData userSvcSateData = idataset.getData(0);
			userSvcSateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
			userSvcSateData.put("END_DATE", SysDateMgr.getLastDateThisMonth());

			result.add(userSvcSateData);

			// 新增服务状态
			IData addSvcStateData = new DataMap();
			addSvcStateData.put("USER_ID", reqData.getUca().getUserId());
			addSvcStateData.put("STATE_CODE", newStatecode); // 正常
			addSvcStateData.put("MAIN_TAG", "1");
			addSvcStateData.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
			addSvcStateData.put("END_DATE", SysDateMgr.getTheLastTime());
			addSvcStateData.put("SERVICE_ID", userSvcSateData.getString("SERVICE_ID"));
			addSvcStateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
			addSvcStateData.put("INST_ID", SeqMgr.getInstId());// 实例ID
			result.add(addSvcStateData);
		} 
		else// 开机
		{

			IDataset idataset = UserSvcStateInfoQry.getUserMainSvcStateByUId(userId);

			if (IDataUtil.isEmpty(idataset)) 
			{
				CSAppException.apperr(GrpException.CRM_GRP_713, "当前[" + productId + "]没有配置主体服务!");
			}

			String lastStopTime = UserSvcStateInfoQry.getUserLastStopTime(userId);
			if (lastStopTime.compareTo(getAcceptTime()) > 0) 
			{
				for (int i = 0; i < idataset.size(); i++) 
				{
					IData userSvcState = idataset.getData(i);
					String svcstate = userSvcState.getString("STATE_CODE");
					if ("5".equals(svcstate)) 
					{
						userSvcState.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
						userSvcState.put("END_DATE", getAcceptTime());
					} else if ("0".equals(svcstate)) 
					{
						userSvcState.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
						userSvcState.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
					}
					result.add(userSvcState);
				}
			} 
			else 
			{
				// 截止老服务
				IData userSvcSateData = idataset.getData(0);
				userSvcSateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
				userSvcSateData.put("END_DATE", getAcceptTime());

				result.add(userSvcSateData);

				// 新增服务状态
				IData addSvcStateData = new DataMap();
				addSvcStateData.put("USER_ID", reqData.getUca().getUserId());
				addSvcStateData.put("STATE_CODE", newStatecode); // 正常
				addSvcStateData.put("MAIN_TAG", "1");
				addSvcStateData.put("START_DATE", getAcceptTime());
				addSvcStateData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
				addSvcStateData.put("SERVICE_ID", userSvcSateData.getString("SERVICE_ID"));
				addSvcStateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
				addSvcStateData.put("INST_ID", SeqMgr.getInstId());// 实例ID
				result.add(addSvcStateData);
			}
		}

		this.addTradeSvcstate(result);
	}

	/**
	 * 
	 */
	protected void regTrade() throws Exception 
	{
		super.regTrade();
		IData data = bizData.getTrade();

		String netTypeCode = UProductInfoQry.getNetTypeCodeByProductId(reqData.getUca().getProductId());
		if (StringUtils.isEmpty(netTypeCode)) 
		{
			netTypeCode = "00";
		}

		data.put("NET_TYPE_CODE", netTypeCode);
	}

	/**
	 * 
	 */
	protected String setTradeTypeCode() throws Exception
	{
		if ("5".equals(stateCode)) 
		{
			return "3911"; //集团多媒体桌面电话产品恢复
		} 
		else 
		{
			return "3912"; //集团多媒体桌面电话产品暂停
		}
	}

}
