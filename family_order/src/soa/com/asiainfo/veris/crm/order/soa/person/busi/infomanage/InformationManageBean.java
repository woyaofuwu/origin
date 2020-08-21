package com.asiainfo.veris.crm.order.soa.person.busi.infomanage;

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
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserRecommInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class InformationManageBean extends CSBizBean
{

	public void checkBefore(IData data) throws Exception
	{
		IDataset set = new DatasetList();
		set = UserSvcStateInfoQry.getUserLastStateByUserSvc(data.getString("USER_ID"), "0");
		if (set.size() != 0)
		{
			String state_code = set.getData(0).getString("STATE_CODE", "");
			if (!state_code.equals("0") && !state_code.equals("N"))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "450001:该用户是停机用户，不允许办理");
			}
		} else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "450002:查不到该用户有效的语音服务状态！");
		}

	}

	/**
	 * 字段检查(新增或删除)
	 * 
	 * @param data
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-04
	 */
	public void checkCreateOrUpdateValue(IData data) throws Exception
	{
		// 这2个效验有点特殊处理，没办法。。。
		if (StringUtils.isEmpty(data.getString("ELEMENT_TYPE", "")))
		{
			if (StringUtils.isEmpty(data.getString("DATA1", "")))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "450000:元素类型不能为空");
			} else
			{
				data.put("ELEMENT_TYPE", data.getString("DATA1", ""));
			}
		} else if (!data.getString("ELEMENT_TYPE", "").equals("1") && !data.getString("ELEMENT_TYPE", "").equals("2"))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "450000:元素类型的值只能是1或者2");
		}

		if (StringUtils.isEmpty(data.getString("ELEMENT_ID", "")))
		{
			if (StringUtils.isEmpty(data.getString("ELEMENT_DESC", "")))
			{
				if (StringUtils.isEmpty(data.getString("DATA2", "")))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "450000:元素ID不能为空");
				} else
				{
					data.put("ELEMENT_ID", data.getString("DATA2", ""));
				}
			} else
			{
				data.put("ELEMENT_ID", data.getString("ELEMENT_DESC", ""));
			}

			if (StringUtils.isEmpty(data.getString("RSRV_STR8", "")))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "450000:提示信息不能为空");
			}
		}
	}

	/**
	 * 字段检查(删除)
	 * 
	 * @param data
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	public void checkDelValue(IData data) throws Exception
	{

	}

	/**
	 * 通过USER_ID检查推荐信息是否已存在
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

		inParam.put("USER_ID", data.getString("USER_ID"));
		result = UserRecommInfoQry.getRecommByUserid(inParam);
		if (result.size() != 0)
		{
			flag = true;
		} else
		{
			flag = false;
		}

		return flag;
	}

	/**
	 * 通过ELEMENT_ID检查用户元素推荐信息是否已存在
	 * 
	 * @param data
	 * @return true为存在,false为不存在
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	private boolean checkRecommIsExistByElementId(IData data) throws Exception
	{
		boolean flag = true;
		IDataset result = new DatasetList();
		IData inParam = new DataMap();

		inParam.put("USER_ID", data.getString("USER_ID"));
		inParam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
		inParam.put("RECOMM_TYPE", data.getString("ELEMENT_TYPE"));
		inParam.put("RECOMM_TAG", "0");
		inParam.put("ELEMENT_ID", data.getString("ELEMENT_ID"));
		result = UserRecommInfoQry.getUserRecommByelement(inParam);
		if (result.size() != 0)
		{
			flag = true;
		} else
		{
			flag = false;
		}

		return flag;
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
		inParam.put("ORDER_TYPE_CODE", "520");
		inParam.put("TRADE_TYPE_CODE", "520");
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
		inParam.put("REMARK", "后台批量修改用户推荐提示信息");

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
		inParam.put("TRADE_TYPE_CODE", "520");
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
		inParam.put("REMARK", "后台批量修改用户推荐提示信息");

		Dao.insert("TF_BH_TRADE", inParam, Route.getJourDb(Route.CONN_CRM_CG));

		return data.getString("TRADE_ID");
	}

	/**
	 * 新增用户提示信息
	 * 
	 * @param data
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	private void createInformation(IData data) throws Exception
	{
		IData inParam = new DataMap();
		// 看有没有，有就报错，没有就新增
		boolean flag = checkInformationIsExistByUserId(data);
		if (flag)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "450000:该用户的提示信息已存在");
		} else
		{
			inParam.put("TRADE_ID", data.getString("TRADE_ID"));
			inParam.put("USER_ID", data.getString("USER_ID"));
			inParam.put("NOTICE_CONTENT", "有新业务推荐：" + data.getString("RSRV_STR8"));
			inParam.put("TRADE_ATTR", "3");
			inParam.put("ENABLE_TAG", "1");
			inParam.put("START_DATE", data.getString("START_DATE"));
			inParam.put("END_DATE", data.getString("END_DATE"));
			inParam.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
			inParam.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
			inParam.put("UPDATE_TIME", SysDateMgr.getSysTime());

			Dao.insert("TF_F_USER_INFORMATION", inParam);
		}
	}

	/**
	 * 新增或修改用户新业务推荐信息
	 */
	public IData createOrUpdateInfoManage(IData data) throws Exception
	{
		IData inParam = new DataMap();

		String sn = data.getString("SERIAL_NUMBER");
		IData user = UcaInfoQry.qryUserInfoBySn(sn);

		if (IDataUtil.isEmpty(user))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该服务号码对应的用户资料信息");
		}

		IData custs = UcaInfoQry.qryCustomerInfoByCustId(user.getString("CUST_ID"));
		if (IDataUtil.isEmpty(custs))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该服务号码对应的客户资料信息");
		}

		IData cust = custs;
		checkBefore(user);

		String order_id = SeqMgr.getOrderId();
		String trade_id = SeqMgr.getTradeId();
		data.put("ORDER_ID", order_id);
		data.put("TRADE_ID", trade_id);
		createHisTrade(data, user, cust);
		createHisOrder(data, user, cust);

		data.put("USER_ID", user.getString("USER_ID"));
		createInformation(data);

		data.put("EPARCHY_CODE", user.getString("EPARCHY_CODE"));
		data.put("CUST_NAME", cust.getString("CUST_NAME"));
		createRecomm(data);

		if (StringUtils.isEmpty(cust.getString("RSRV_STR10", "")) || !"1".equals(cust.getString("RSRV_STR10", "")))
		{
			inParam.clear();
			inParam.put("RSRV_STR10", "1");
			inParam.put("CUST_ID", cust.getString("CUST_ID"));
			Dao.executeUpdateByCodeCode("TF_F_CUSTOMER", "UPD_RSRV10", inParam);
		}
		IData result = new DataMap();
		result.put("TRADE_ID", trade_id);
		result.put("ORDER_ID", order_id);
		return result;
	}

	/**
	 * 新增用户推荐信息
	 * 
	 * @param data
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	private void createRecomm(IData data) throws Exception
	{
		IData inParam = new DataMap();
		// 检查
		boolean flag = checkRecommIsExistByElementId(data);
		if (flag)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "450000:该用户的推荐信息已存在");
		} else
		{
			inParam.put("RECOMM_ID", data.getString("TRADE_ID"));
			inParam.put("USER_ID", data.getString("USER_ID"));
			inParam.put("PARTITION_ID", Long.parseLong(data.getString("USER_ID")) % 10000);
			inParam.put("CUST_NAME", data.getString("CUST_NAME"));
			inParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
			inParam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
			inParam.put("RECOMM_EVENT", "ZZZZ");
			inParam.put("RECOMM_TYPE", data.getString("ELEMENT_TYPE"));
			inParam.put("RECOMM_SOURCE", "1");
			inParam.put("RECOMM_PRIORITY", "1");
			inParam.put("PRODUCT_ID", "-1");
			inParam.put("PACKAGE_ID", "-1");
			inParam.put("ELEMENT_ID", data.getString("ELEMENT_ID"));
			if (data.getString("ELEMENT_TYPE").equals("1"))
			{
				// 优惠
				inParam.put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(data.getString("ELEMENT_ID")));
			} else if (data.getString("ELEMENT_TYPE").equals("2"))
			{
				// 服务
				inParam.put("ELEMENT_NAME", USvcInfoQry.getSvcNameBySvcId(data.getString("ELEMENT_ID")));

			} else
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "只能是服务或者优惠");
			}
			inParam.put("RECOMM_TAG", "0");
			inParam.put("START_DATE", data.getString("START_DATE"));
			inParam.put("END_DATE", data.getString("END_DATE"));

			Dao.insert("TF_F_USER_RECOMM", inParam);
		}

	}

	public void dealinsertInformationInfo(IData param) throws Exception
	{
		Dao.insert("TF_F_USER_INFORMATION", param);
	}

	public void dealupInformationInfo(IData param) throws Exception
	{
		Dao.save("TF_F_USER_INFORMATION", param, new String[]
		{ "TRADE_ID" });
	}

	/**
	 * 删除用户新业务推荐信息
	 * 
	 * @param data
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	public IData delInfoManage(IData data) throws Exception
	{
		String sn = data.getString("SERIAL_NUMBER", "");
		IData user = UcaInfoQry.qryUserInfoBySn(sn);

		IData custs = UcaInfoQry.qryCustomerInfoByCustId(user.getString("CUST_ID"));
		IData cust = custs;
		checkBefore(user);
		String order_id = SeqMgr.getOrderId();
		String trade_id = SeqMgr.getTradeId();
		data.put("ORDER_ID", order_id);
		data.put("TRADE_ID", trade_id);
		createHisTrade(data, user, cust);
		createHisOrder(data, user, cust);

		data.put("USER_ID", user.getString("USER_ID"));
		delInformation(data);

		data.put("EPARCHY_CODE", user.getString("EPARCHY_CODE"));
		delRecomm(data);

		IData inParam = new DataMap();
		inParam.clear();
		inParam.put("RSRV_STR10", "0");
		inParam.put("CUST_ID", cust.getString("CUST_ID"));
		Dao.executeUpdateByCodeCode("TF_F_CUSTOMER", "UPD_RSRV10", inParam);

		IData result = new DataMap();
		result.put("TRADE_ID", trade_id);
		result.put("ORDER_ID", order_id);
		return result;
	}

	/**
	 * 删除用户提示信息
	 * 
	 * @param data
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	private void delInformation(IData data) throws Exception
	{
		IData inParam = new DataMap();
		boolean flag = checkInformationIsExistByUserId(data);
		if (!flag)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "450000:该用户的提示信息不存在");
		} else
		{
			inParam.put("USER_ID", data.getString("USER_ID"));
			Dao.executeUpdateByCodeCode("TF_F_USER_INFORMATION", "DEL_RECOMM_BY_USERID", inParam);
		}
	}

	/**
	 * 删除用户推荐信息
	 * 
	 * @param data
	 * @throws Exception
	 * @author xiangyc
	 * @date 2014-8-5
	 */
	private void delRecomm(IData data) throws Exception
	{
		IData inParam = new DataMap();
		// 检查
		boolean flag = checkRecommIsExistByElementId(data);
		if (!flag)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "450000:该用户的推荐信息不存在");
		} else
		{
			inParam.put("USER_ID", data.getString("USER_ID"));
			inParam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
			inParam.put("RECOMM_TYPE", data.getString("ELEMENT_TYPE"));
			inParam.put("ELEMENT_ID", data.getString("ELEMENT_ID"));

			Dao.executeUpdateByCodeCode("TF_F_USER_RECOMM", "DEL_USER_RECOMM_BY_ELEMENTID", inParam);
		}
	}

	public IDataset getAllInformationInfo(IData param) throws Exception
	{

		return UserRecommInfoQry.getInformationByUserid(param);
	}

}
