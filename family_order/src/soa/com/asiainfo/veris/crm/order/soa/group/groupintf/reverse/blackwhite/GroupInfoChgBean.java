
package com.asiainfo.veris.crm.order.soa.group.groupintf.reverse.blackwhite;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.dao.Dao;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class GroupInfoChgBean extends GroupBean
{
	/****
	 * 同步记录EC客户信息变更记录
	 * @param ecBusiInfo
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	 public static IData synChgGroupInfo(IDataset grp_platsvcs,IData ecBusiInfo, Pagination pagination) throws Exception
	 {
		 defParamsValue(ecBusiInfo, "RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2," +
	 		"RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
		 if (IDataUtil.isNotEmpty(grp_platsvcs)){
			 for(int i = 0 ; i < grp_platsvcs.size() ; i++){
				 if (IDataUtil.isNotEmpty(grp_platsvcs.getData(i)))
				 {
					 ecBusiInfo.putAll(grp_platsvcs.getData(i));
				 }
			 }
		 }
		 String insid = SeqMgr.getInstId();
		 ecBusiInfo.put("INST_ID", insid);
		 ecBusiInfo.put("GROUP_ID", ecBusiInfo.getString("EC_ID"));
		 ecBusiInfo.put("SYNC_FLAG", "01");
		 ecBusiInfo.put("BIZ_AREA", "01");
		 ecBusiInfo.put("REMARK", "EC信息变更接口操作");
		 ecBusiInfo.put("UP_STAFF_ID", "");
		 ecBusiInfo.put("UP_DEPART_ID", "");
		 ecBusiInfo.put("UP_DEPART_ID", "");
		 ecBusiInfo.put("PARTITION_ID", SysDateMgr.getCurMonth());
		 boolean result_num = Dao.insert("TI_B_ECBUSIINFO", ecBusiInfo, Route.CONN_CRM_CG);
		 IData result = new DataMap();
		 result.put("SUCC_NUM", result_num);
		 return result;
	 }
	 /***
	  * 同步记录EC客户信息变更记录
	  * @param ecBusiInfo
	  * @param pagination
	  * @return
	  * @throws Exception
	  */
	public static IData synChgEcBusiInfo(IDataset grp_custInfos,IData ecBusiInfo, Pagination pagination) throws Exception
	{
		defParamsValue(ecBusiInfo, "RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2," +
 		"RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3");
	 	if (IDataUtil.isNotEmpty(grp_custInfos)){
	 		if (IDataUtil.isNotEmpty(grp_custInfos.getData(0)))
	 		{
	 			ecBusiInfo.putAll(grp_custInfos.getData(0));
	 		}
	 	}
	 	ecBusiInfo.put("GROUP_ID", ecBusiInfo.getString("EC_ID"));
	 	IData staffInfo = UStaffInfoQry.qryStaffInfoByPK(ecBusiInfo.getString("STAFF_ID"));
	 	ecBusiInfo.put("EMAIL", staffInfo.getString("EMAIL"));
	 	ecBusiInfo.put("SYNC_FLAG", "01");
	 	ecBusiInfo.put("BIZ_AREA", "01");
	 	ecBusiInfo.put("REMARK", "EC业务信息变更接口操作");
	 	ecBusiInfo.put("UP_STAFF_ID", "");
	 	ecBusiInfo.put("UP_DEPART_ID", "");
	 	ecBusiInfo.put("UP_DEPART_ID", "");
		ecBusiInfo.put("PARTITION_ID", SysDateMgr.getCurMonth());
	 	if (StringUtils.isBlank(ecBusiInfo.getString("EFF_TIME")))
	 	{
	    	ecBusiInfo.put("EFF_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	 	}
	 	boolean result_num =  Dao.insert("TI_B_ECBUSIINFO", ecBusiInfo,Route.CONN_CRM_CG);
	 	IData result = new DataMap();
	 	result.put("SUCC_NUM", result_num);
		return result;
	}
	
	/***
	 * 记录待二次确认白名单信息
	 * @param whiteBusi_Info
	 * @return
	 * @throws Exception
	 */
	public static IData regWhiteConfirmInfo(IData whiteInfo) throws Exception
    {
		IData result = new DataMap();
		if (IDataUtil.isNotEmpty(whiteInfo)){
			defParamsValue(whiteInfo, "RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_STR1," +
					"RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5");
			whiteInfo.put("DATA_TYPE", "01");
			whiteInfo.put("SYNC_FLAG", "01");
			whiteInfo.put("OP_CODE", "01");
			whiteInfo.put("REMARK", "新增待二次确认白名单信息");
			whiteInfo.put("PARTITION_ID", SysDateMgr.getCurMonth());
			boolean result_num = Dao.insert("TI_B_WHITECONFIRM", whiteInfo,Route.CONN_CRM_CG);
			result.put("SUCC_NUM", result_num);
		}
		return result;
    }
	
	/***
	 * 构造待二次确认参数
	 * @param map
	 * @param grpInfo
	 * @return
	 * @throws Exception
	 */
	public static void buildWhieConfirmInfo(IData map,IData grpInfo) throws Exception
	{
		grpInfo.put("EC_ID", grpInfo.getString("GROUP_ID"));
		grpInfo.put("PROD_ID", grpInfo.getString("INST_ID"));
		grpInfo.put("PROD_NAME", grpInfo.getString("BIZ_NAME"));
		grpInfo.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER"));
		grpInfo.put("EFFT_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND)); 
		grpInfo.put("RSRV_STR1", map.getString("REQUEST_ID"));  
	}

	/***
	 * 白名单用户变更信息
	 * @param white_Info
	 * @return
	 * @throws Exception
	 */
    public static IData chgWhiteUserInfo(IData white_Info) throws Exception
    {
		IData result = new DataMap();
		if (IDataUtil.isNotEmpty(white_Info)){
			defParamsValue(white_Info, "RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_STR1," +
					"RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5");
		 	white_Info.put("DATA_TYPE", "02");
		 	white_Info.put("SYNC_FLAG", "01");
		 	white_Info.put("REMARK", "新增白名单用户变更信息");
		 	white_Info.put("PARTITION_ID", SysDateMgr.getCurMonth());
			boolean result_num = Dao.insert("TI_B_WHITECONFIRM", white_Info,Route.CONN_CRM_CG);
			result.put("SUCC_NUM", result_num);
		}
    	return result;
    }
    /***
     * 查询集团服务信息
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpPlatInfo(IData params) throws Exception
    {
    	if (IDataUtil.isNotEmpty(params)){
    		return Dao.qryByCode("TF_F_USER_GRP_PLATSVC","SEL_GRPINFO_BY_USERID", params,Route.CONN_CRM_CG);
		}
    	return null;
    }

    /***
     * 查询集团服务信息
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpPlatInfoByInstID(IData params) throws Exception
    {
    	if (IDataUtil.isNotEmpty(params)){
    		return Dao.qryByCode("TF_F_USER_GRP_PLATSVC","SEL_GRP_PLATSVC_BY_INSTID", params,Route.CONN_CRM_CG);
		}
    	return null;
    }
    
    /***
	 * 查询待二次确认记录
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryWhiteConfirmInfo(IData params) throws Exception
	{
		if (IDataUtil.isNotEmpty(params)){
    		return Dao.qryByCode("TI_B_WHITECONFIRM","SEL_WHITEINFO_BY_COND", params,Route.CONN_CRM_CG);
		}
    	return null;
	}

    /***
	 * 修改待二次确认记录
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset updWhiteConfirmInfo(IData params) throws Exception
	{
		if (IDataUtil.isNotEmpty(params)){
    		return Dao.qryByCode("TI_B_WHITECONFIRM","UPD_WHITEINFO_BY_INSTID", params,Route.CONN_CRM_CG);
		}
    	return null;
	}
	
    /***
	 * 查询二次确认结果记录
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryConfirmResultInfo(IData params) throws Exception
	{
		if (IDataUtil.isNotEmpty(params)){
    		return Dao.qryByCode("TI_BH_WHITECONFIRM","SEL_CONFIRMRESULT_BY_COND", params,Route.CONN_CRM_CG);
		}
    	return null;
	}
	
	/***
	 * 查询待二次确认记录
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryWhiteInfoBySeriNum(IData params) throws Exception
	{
		if (IDataUtil.isNotEmpty(params)){
    		return Dao.qryByCode("TI_B_WHITECONFIRM","SEL_WHITEINFO_BY_SERINUM", params,Route.CONN_CRM_CG);
		}
    	return null;
	}
	
	/***
	 * 校验是否需要二次短信确认
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static boolean chkTwoConfirm(IData params) throws Exception
	{
		return chkTwoConfirm(params, null);
	}
	
	/***
	 * 校验是否需要二次短信确认
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static boolean chkTwoConfirm(IData params,String operFlag) throws Exception
	{
		IDataset results = qryGrpPlatInfo(params);
		if (IDataUtil.isNotEmpty(results))
		{
			IData result = results.getData(0);
			if (IDataUtil.isNotEmpty(result) && "0".equals(result.getString("CONFIRMFLAG","1")))
			{
				//新增成员时，如果已经同步过，则不需要再次下发二次确认短信，退出后再加入的场景会在加入时的下一步校验住。
				//防止二次确认后再进入该逻辑
				if ("ADD".equals(operFlag))
				{
					result.put("EC_ID", result.getString("GROUP_ID"));
					result.put("SERIAL_NUMBER", params.getString("SERIAL_NUMBER"));
					IDataset whiteConfirm = qryWhiteConfirmInfo(result);
					if (IDataUtil.isNotEmpty(whiteConfirm)){
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	 /***
	  * 给预留字段赋默认值
	  * @param data
	  * @param keys
	  * @throws Exception
	  */
	 private static void defParamsValue(IData data,String keys) throws Exception
	 {
		 if(StringUtils.isNotBlank(keys)){
			 String[] keyArr = keys.split(",");
			 for(int i = 0 ; i < keyArr.length ; i++){
				 if (StringUtils.isBlank(data.getString(keyArr[i])))
				 {
					 data.put(keyArr[i], "");
				 }
			 }
		 }
	 }
}
