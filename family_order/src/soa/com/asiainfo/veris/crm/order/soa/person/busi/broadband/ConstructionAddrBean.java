package com.asiainfo.veris.crm.order.soa.person.busi.broadband;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ConstructionAddrBean extends CSBizBean
{

	protected static final Logger log = Logger.getLogger(ConstructionAddrBean.class);

	/**
	 * 字段检查(修改)
	 * 
	 * @param data
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	public void checkUPValue(IData data) throws Exception
	{
		if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "宽带虚拟账号不能为空");
		}
		if (StringUtils.isEmpty(data.getString("DATA11", "")))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "施工地址不能为空");
		}
	}

	/**
	 * 修改宽带施工地址
	 * 
	 * @param data
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	public IData upConstructionAddr(IData data) throws Exception
	{
		String sn = "KD_" + data.getString("SERIAL_NUMBER", "");

		data.put("SERIAL_NUMBER", "KD_" + data.getString("SERIAL_NUMBER", ""));
		/*
		 * if(!"KD".equals(sn.substring(0, 2))) {
		 * CSAppException.apperr(CrmCommException.CRM_COMM_103, "请正确输入宽带号码"); }
		 */

		IData user = UcaInfoQry.qryUserInfoBySn(sn);

		if (IDataUtil.isEmpty(user))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该服务号码对应的用户资料信息");
		}
		IData custs = UcaInfoQry.qryCustomerInfoByCustId(user.getString("CUST_ID"));
		IData cust = custs;
		if (IDataUtil.isEmpty(custs))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该服务号码对应的客户资料信息");
		}

		String order_id = SeqMgr.getOrderId();
		String trade_id = SeqMgr.getTradeId();
		data.put("ORDER_ID", order_id);
		data.put("TRADE_ID", trade_id);
		createHisTrade(data, user, cust);
		createHisOrder(data, user, cust);

		data.put("USER_ID", user.getString("USER_ID"));
		upconstructionAddr(data);

		IData result = new DataMap();
		result.put("TRADE_ID", trade_id);
		result.put("ORDER_ID", order_id);
		return result;
	}

	/**
	 * 创建订单历史表
	 * 
	 * @param data
	 * @param user
	 * @param cust
	 * @return
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	private String createHisOrder(IData data, IData user, IData cust) throws Exception
	{
		IData inParam = new DataMap();

		inParam.put("ORDER_ID", data.getString("ORDER_ID"));
		inParam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(data.getString("ORDER_ID")));
		inParam.put("BATCH_ID", data.getString("BATCH_ID"));
		inParam.put("ORDER_TYPE_CODE", "6666");
		inParam.put("TRADE_TYPE_CODE", "6666");
		inParam.put("PRIORITY", data.getString("PRIORITY", "500"));
		inParam.put("ORDER_STATE", "2");
		inParam.put("NEXT_DEAL_TAG", "0");
		inParam.put("CUST_ID", cust.getString("CUST_ID"));
		inParam.put("CUST_NAME", cust.getString("CUST_NAME"));
		inParam.put("PSPT_TYPE_CODE", cust.getString("PSPT_TYPE_CODE"));
		inParam.put("PSPT_ID", cust.getString("PSPT_ID"));
		inParam.put("EPARCHY_CODE", user.getString("EPARCHY_CODE"));
		inParam.put("CITY_CODE", user.getString("CITY_CODE"));
		inParam.put("ACCEPT_DATE", data.getString("ACCEPT_DATE", SysDateMgr.getSysTime()));
		inParam.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID", getVisit().getStaffId()));
		inParam.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID", getVisit().getDepartId()));
		inParam.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE", getVisit().getCityCode()));
		inParam.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
		inParam.put("TERM_IP", data.getString("TERM_IP", ""));
		inParam.put("FEE_STATE", "0");
		inParam.put("OPER_FEE", "0");
		inParam.put("FOREGIFT", "0");
		inParam.put("ADVANCE_PAY", "0");
		inParam.put("FINISH_DATE", SysDateMgr.getSysTime());
		inParam.put("EXEC_TIME", SysDateMgr.getSysTime());
		inParam.put("CANCEL_TAG", "0");
		inParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
		inParam.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID", getVisit().getStaffId()));
		inParam.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID", getVisit().getDepartId()));
		inParam.put("SUBSCRIBE_TYPE", "100");
		inParam.put("IN_MODE_CODE", getVisit().getInModeCode());
		inParam.put("REMARK", "后台批量修改宽带施工地址");

		Dao.insert("TF_BH_ORDER", inParam, Route.getJourDb(Route.CONN_CRM_CG));

		return data.getString("ORDER_ID");
	}

	/**
	 * 创建台帐历史表
	 * 
	 * @param data
	 * @param user
	 * @param cust
	 * @return
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	private String createHisTrade(IData data, IData user, IData cust) throws Exception
	{
		IData inParam = new DataMap();

		inParam.put("TRADE_ID", data.getString("TRADE_ID"));
		inParam.put("ORDER_ID", data.getString("ORDER_ID"));
		inParam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(data.getString("TRADE_ID")));
		inParam.put("BATCH_ID", data.getString("BATCH_ID"));
		inParam.put("TRADE_TYPE_CODE", "6666");
		inParam.put("PRIORITY", data.getString("PRIORITY", "500"));
		inParam.put("SUBSCRIBE_TYPE", "100");
		inParam.put("SUBSCRIBE_STATE", "9");
		inParam.put("NEXT_DEAL_TAG", "0");
		inParam.put("CUST_ID", cust.getString("CUST_ID"));
		inParam.put("CUST_NAME", cust.getString("CUST_NAME"));
		inParam.put("USER_ID", user.getString("USER_ID"));
		inParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		inParam.put("NET_TYPE_CODE", "00");
		inParam.put("EPARCHY_CODE", user.getString("EPARCHY_CODE"));
		inParam.put("CITY_CODE", user.getString("CITY_CODE"));
		inParam.put("PRODUCT_ID", user.getString("PRODUCT_ID"));
		inParam.put("BRAND_CODE", user.getString("BRAND_CODE"));
		inParam.put("ACCEPT_DATE", data.getString("ACCEPT_DATE", SysDateMgr.getSysTime()));
		inParam.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID", getVisit().getStaffId()));
		inParam.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID", getVisit().getDepartId()));
		inParam.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE", getVisit().getCityCode()));
		inParam.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
		inParam.put("TERM_IP", data.getString("TERM_IP", ""));
		inParam.put("FEE_STATE", "0");
		inParam.put("OPER_FEE", "0");
		inParam.put("FOREGIFT", "0");
		inParam.put("ADVANCE_PAY", "0");
		inParam.put("EXEC_TIME", SysDateMgr.getSysTime());
		inParam.put("CANCEL_TAG", "0");
		inParam.put("PROCESS_TAG_SET", "0");
		inParam.put("OLCOM_TAG", "0");
		inParam.put("IN_MODE_CODE", getVisit().getInModeCode());
		inParam.put("REMARK", "后台批量修改宽带施工地址");

		Dao.insert("TF_BH_TRADE", inParam, Route.getJourDb(Route.CONN_CRM_CG));

		return data.getString("TRADE_ID");
	}

	/**
	 * 修改宽带施工地址
	 * 
	 * @param data
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	private void upconstructionAddr(IData data) throws Exception
	{
		boolean flag = checkInformationIsExistByUserId(data);
		if (!flag)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "450000:该用户的提示信息不存在");
		} else
		{
			Dao.executeUpdateByCodeCode("TF_F_USER_WIDENET", "UP_CONSTRUCTIONADDR_BY_USERID", data);
		}
	}

	/**
	 * 通过SERIAL_NUMBER检查用户宽带信息是否已存在
	 * 
	 * @param data
	 * @return true为存在,false为不存在
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	private boolean checkInformationIsExistByUserId(IData data) throws Exception
	{
		boolean flag = true;
		IDataset result = new DatasetList();
		IData inParam = new DataMap();

		inParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		result = UserInfoQry.getWideUserInfoBySN(inParam);
		if (result.size() != 0)
		{
			flag = true;
		} else
		{
			flag = false;
		}

		return flag;
	}
}
