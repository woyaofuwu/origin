
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class MsisdnInfoQry
{

	/**
	 * 判断是否从其他运营商转到移动 TD_M_MSISDN_CRM与TD_M_MSISDN表结构相同，数据由用户自己配置
	 * 
	 * @author shixb
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IData getCrmMsisonBySerialnumber(String serialNumber) throws Exception
	{
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);

		IDataset dataset = Dao.qryByCode("TD_M_MSISDN", "SEL_BY_MSISDN", data, Route.CONN_CRM_CEN);
		return dataset.size() > 0 ? (IData) dataset.get(0) : null;
	}
	/**
	 * 从新表TD_MSISDN读取
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IData getCrmMsisonBySerialnumberNew(String serialNumber) throws Exception
	{
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);

		IDataset dataset = Dao.qryByCode("TD_MSISDN", "SEL_BY_MSISDN_SN", data, Route.CONN_CRM_CEN);
		return dataset.size() > 0 ? (IData) dataset.get(0) : null;
	}

	 /**
     * 本省网内号码验证
     * 
     * @author chenyi
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getMsisonBySerialnumber(String SERIAL_NUMBER, String PROV_CODE,String ASP, Pagination page) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", SERIAL_NUMBER);
        param.put("PROV_CODE", PROV_CODE);
        param.put("ASP", ASP);

        IDataset dataset = Dao.qryByCode("TD_M_MSISDN", "SEL_BY_NUMPROV", param, page, Route.CONN_CRM_CEN);
        return dataset.size() > 0 ? dataset.getData(0) : new DataMap();
    }

	/**
	 * 查询手机号码查询成员user信息
	 * 
	 * @param pd
	 * @param 海南需要查TD_M_MSISDN
	 * @return IDataset 成员用户信息
	 * @author
	 * @throws Throwable
	 */

	public static IDataset getMsdnBySn(String serialNumber) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);
		inparams.put("PROV_CODE", "898");
		inparams.put("ASP", "1");// 运营商 1-移动 2-联通 3-电信 6-卫通
		inparams.put("HOME_TYPE", "1");// 归属类型 1-本地，在一级BOSS中，只要是本省都可以置[1] 3-国内，非本省的，而又是国内的，都置[3]

		// 查询手机号码归属地
		IData mofficeInfo = MsisdnInfoQry.getMsisdnBySnNum(inparams);

		if (IDataUtil.isNotEmpty(mofficeInfo))
		{
			String usereaprchy_code = mofficeInfo.getString("AREA_CODE");
			IDataset userInfos = UserInfoQry.qryUserInfoBySnNetTag(serialNumber, "0", "00", usereaprchy_code);
			return userInfos;
		}

		// 在网段表中服务号码没有记录,遍历地州库
		String[] connNames = Route.getAllCrmDb();

		if (connNames == null)
		{
			return new DatasetList();
		}

		String connName = "";
		int len = connNames.length;

		for (int i = 0; i < len; i++)
		{
			connName = connNames[i];
			IDataset userInfoList = UserInfoQry.qryUserInfoBySnNetTag(serialNumber, "0", "06", connName);

			if (IDataUtil.isNotEmpty(userInfoList))
			{
				return userInfoList;
			}
		}
		return new DatasetList();
	}

	/**
	 * todo code_code 表里没有SEL_BY_NUM 查询号码是否在一级BOSS号段表内
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 * @author chenl
	 * @date 2009-10-7
	 */
	public static IDataset getMsisdnBySN(IData inparams) throws Exception
	{

		IDataset mofficeInfos = Dao.qryByCode("TD_M_MSISDN", "SEL_BY_NUM", inparams, Route.CONN_CRM_CEN);
		if (mofficeInfos != null && mofficeInfos.size() > 0)
			return mofficeInfos;

		// 在网段表中服务号码没有记录遍历地州库
		String[] connNames = Route.getAllCrmDb();
		if (connNames == null)
			return null;

		int len = connNames.length;

		String serialNumber = inparams.getString("SERIAL_NUMBER", "");
		for (int i = 0; i < len; i++)
		{
			String connName = connNames[i];
			if (connName.indexOf("crm") >= 0)
			{
				// String REMOVE_TAG = "0";
				// inparams.put("CONN_NAME", connName);
				// String NET_TYPE_CODE = "00";
				mofficeInfos = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn(serialNumber, connName));
				if (mofficeInfos != null && mofficeInfos.size() > 0)
				{
					IData mofficeInfo = mofficeInfos.getData(0);
					mofficeInfo.put("AREA_CODE", mofficeInfo.getString("EPARCHY_CODE"));
					return mofficeInfos;
				}
			}
		}

		return null;
	}

	/**
	 * 移动网内号码验证
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IData getMsisdnBySnNum(IData inparams) throws Exception
	{

		IDataset mofficeInfos = Dao.qryByCode("TD_M_MSISDN", "SEL_BY_NUM", inparams, Route.CONN_CRM_CEN);
		if (IDataUtil.isNotEmpty(mofficeInfos))
		{
			return mofficeInfos.getData(0);
		}
		return null;

	}

	/**
	 * 根据号码查询是否本省移动网内号码
	 * 
	 * @param inparams
	 * @return IDataset
	 * @throws Exception
	 */
	public static IData getMsisdnBySnNumForCrm(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("PROV_CODE", "898");
		param.put("ASP", "1");// 运营商 1-移动 2-联通 3-电信 6-卫通
		param.put("HOME_TYPE", "1");// 归属类型 1-本地，在一级BOSS中，只要是本省都可以置[1] ; 3-国内，非本省的，而又是国内的，都置[3]
		IData moffice = MsisdnInfoQry.getMsisdnBySnNum(param);
		if (IDataUtil.isNotEmpty(moffice))
		{
			return moffice;
		}
		// 如果没有，遍历所有CRM库查
		String[] connNames = Route.getAllCrmDb();

		if (connNames == null)
		{
			return null;
		}

		String routeId = "";
		int count = connNames.length;

		for (int index = 0; index < count; index++)
		{
			routeId = connNames[index];

			moffice = UcaInfoQry.qryUserInfoBySn(serialNumber, routeId);

			if (IDataUtil.isNotEmpty(moffice))
			{
				return moffice;
			}
		}

		return null;
	}

	public static IDataset getMsisDns(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TD_M_MSISDN", "SEL_BY_MSISDN_A", param, Route.CONN_CRM_CEN);
	}

	public static IDataset getMsisdnsBySerialNumber(String sn) throws Exception
	{
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", sn);
		return Dao.qryByCode("TD_M_MSISDN", "SEL_BY_MSISDN_B", data, Route.CONN_CRM_CEN);
	}
	
	public static IDataset getMsisdnsBySerialNumber_c(String sn) throws Exception
	{
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", sn);
		return Dao.qryByCode("TD_M_MSISDN", "SEL_BY_MSISDN_C", data, Route.CONN_CRM_CEN);
	}

	/**
	 * 移动网内号码验证
	 * 
	 * @author tengg
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IData getMsisonBySerialnumber(String SERIAL_NUMBER, Pagination page) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", SERIAL_NUMBER);

		IDataset dataset = Dao.qryByCode("TD_M_MSISDN", "SEL_BY_MSISDN", param, page, Route.CONN_CRM_CEN);
		return dataset.size() > 0 ? dataset.getData(0) : new DataMap();
	}

	/**
	 * 移动网内号码验证 ASP：运营商 1-移动 2-联通 3-电信 6-卫通
	 * 
	 * @author tengg
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IData getMsisonBySerialnumberAsp(String SERIAL_NUMBER, String asp) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", SERIAL_NUMBER);
		param.put("VASP", asp);

		IDataset dataset = Dao.qryByCode("TD_M_MSISDN", "SEL_BY_MSISDN_ASP", param, Route.CONN_CRM_CEN);
		return dataset.size() > 0 ? dataset.getData(0) : new DataMap();
	}

	public static IDataset getMsisonBySNSP(String serialNumber) throws Exception
	{

		IData param = new DataMap();
		param.put("MSISDN", serialNumber);

		return Dao.qryByCode("TD_M_MSISDN", "SEL_BY_SN_ASP", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据手机号码获取路由信息， 主要是给一级boss业务，需要获取手机号码归属省份时使用
	 * 
	 * @param serialNumber
	 * @return
	 * @author xiekl
	 * @throws Exception
	 */
	public static IData getRouteInfoBySn(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		IDataset resultList = Dao.qryByCodeParser("TD_M_MSISDN", "SEL_BY_NUM", param, Route.CONN_CRM_CEN);
		if (resultList != null && !resultList.isEmpty())
		{
			return resultList.getData(0);
		}
		else
		{
			return null;
		}
	}

	public static IDataset getSwitchBySwitchId(String switchId, String eparchyCode) throws Exception
	{
		IData data = new DataMap();
		data.put("SWITCH_ID", switchId);
		data.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TD_M_SWITCH", "SEL_BY_PK", data, Route.CONN_RES);
	}

	public static IDataset queryEpareycodeout(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select b.* from td_m_msisdn a,(select distinct prov_code,area_code from td_m_msisdn where trim(called_type) = '1') b ");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and :SERIAL_NUMBER between a.begin_msisdn and a.end_msisdn ");
		parser.addSQL(" and a.area_code = b.area_code");
		return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
	}
	
	/**
	 * ADC或MAS成员号段验证
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IData getCrmMsisonBySerialnumberlimit(String serialNumber) throws Exception
	{
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);

		IDataset dataset = Dao.qryByCode("TD_MSISDN", "SEL_BY_MSISDN_SNLIMIT", data, Route.CONN_CRM_CEN);
		return dataset.size() > 0 ? (IData) dataset.get(0) : null;
	}
}
