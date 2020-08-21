package com.asiainfo.veris.crm.order.soa.group.esop.query;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.query.sysorg.UAreaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;


public class WorkformMarketingSVC extends GroupOrderService
{
	private static final long serialVersionUID = 1L;
	
	public static void saveMarketingInfo(IData inparam) throws Exception
	{
		String ibsysID = inparam.getString("IBSYSID","");
		String nodeId = inparam.getString("NODE_ID", "");
		String bpmTempletId = inparam.getString("BPM_TEMPLET_ID", "");
		String busiformId = inparam.getString("BUSIFORM_ID","");
		
		IDataset eopSubscribeInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysID);
		if (IDataUtil.isEmpty(eopSubscribeInfos)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据IBSYSID="+ibsysID+"获取TF_B_EOP_SUBSCRIBE表数据失败！");
		}
		
		IDataset eweInfos = EweNodeQry.qryEweByBusiformId(busiformId);
		if (IDataUtil.isEmpty(eweInfos)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据busiformId="+busiformId+"获取TF_B_EWE表数据失败！");
		}
		
		IData infoData = EweConfigQry.qryByConfigParamNameRsrv1("DATA_TRANS", bpmTempletId, nodeId, "0");
		if (IDataUtil.isEmpty(infoData)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取节点数据抽取对应关系配置错误，请确认TD_B_EWE_CONFIG表数据是否正确！");
		}
		
		String preNodeId = infoData.getString("PARAMVALUE");
		
		IDataset nodeDataset = EweNodeTraQry.qryEweNodeTraByBusiformIdAndNodeId(busiformId, preNodeId);
		if (IDataUtil.isEmpty(nodeDataset)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"流程轨迹"+busiformId+"对应节点"+preNodeId+"数据不存在！");
		}
		String subIbsysid = nodeDataset.first().getString("SUB_BI_SN");
		
		String templetID = eopSubscribeInfos.first().getString("RSRV_STR2");
		if ("DIRECTLINEMARKETINGUPD".equals(templetID)) {
			IData eopAttrData  = WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysID, "C_MARKETING_SELECT", "0");
			String IBSYSID_MARKETING = eopAttrData.getString("ATTR_VALUE");
			if (StringUtils.isEmpty(IBSYSID_MARKETING)) {
				return;
			}
			IData param = new DataMap();
			param.put("IBSYSID_MARKETING", IBSYSID_MARKETING);
			IDataset marketList = WorkformMarketingBean.selMarketingByCondition2(param);
			if (IDataUtil.isEmpty(marketList)) {
				return;
			}
			IData idataNew = new DataMap();
			idataNew.put("IBSYSID", ibsysID);
			idataNew.put("NODE_ID", "apply");
			idataNew.put("RECORD_NUM", "0");
			
			IDataset comAttrs = WorkformAttrBean.qryEopAttrByIbsysidNodeid(ibsysID, "apply", "0");
			
			if (IDataUtil.isEmpty(comAttrs)) {
				if (IDataUtil.isEmpty(comAttrs)) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据ibsysid="+ibsysID+"，recordNum=0获取tf_b_eop_attr表数据失败！");
				}
			}
			
			IData comData = new DataMap();
			for (int i = 0; i < comAttrs.size(); i++) {
				comData.put(comAttrs.getData(i).getString("ATTR_CODE",""), comAttrs.getData(i).getString("ATTR_VALUE",""));
			}
			
			comData.put("SUB_IBSYSID", comAttrs.first().getString("SUB_IBSYSID",""));
			
			String marketingStartDate = comData.getString("C_MARKETINGLINE_STARTDATE","");
			String marketingMonth = comData.getString("C_MARKETING_MONTH","");
			String marketingEndDate = SysDateMgr.endDateOffset(marketingStartDate, marketingMonth, "2");
			String auditStartDate = SysDateMgr.endDateOffset(marketingStartDate, String.valueOf(Integer.valueOf(marketingMonth)-1), "2");

			IData info = new DataMap();
			info.put("IBSYSID_MARKETING", comData.getString("C_MARKETING_SELECT",""));
			info.put("MARKETING_NAME", comData.getString("C_MARKETING_NAME",""));
			info.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(eweInfos.first().getString("CITY_CODE")));
			info.put("APPLY_STAFF_ID", eweInfos.first().getString("ACCEPT_STAFF_ID"));
			info.put("MARKETING_IS_SUCC", comData.getString("C_MARKETING_IS_SUCC",""));
			info.put("MARKETING_MONTH", comData.getString("C_MARKETING_MONTH",""));
			info.put("MARKETING_START_DATE", comData.getString("C_MARKETINGLINE_STARTDATE",""));//营销活动预计开始日期
			info.put("MARKETING_END_DATE", marketingEndDate);//营销活动结束日期
			info.put("AUDIT_START_DATE", auditStartDate);//稽核开始日期
			if(comData.getString("C_MARKETING_OPERATION","").equals("2")){//修改
				info.put("RESULT_CODE", "0");//当前状态 0等待处理 1等待稽核 2稽核通过 -1稽核不通过 
				info.put("RESULT_INFO","等待处理");
			}else{
				info.put("RESULT_CODE", "3");//当前状态 0等待处理 1等待稽核 2稽核通过 -1稽核不通过 
				info.put("RESULT_INFO","已取消");
			}
			info.put("IBSYSID", ibsysID);
			
			updateMarketingInfo(info);
			
			return;
			
		}
		
		IDataset allAttrInfos = new DatasetList();
		
		//取公共数据
		IDataset comAttrInfos = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysid, "0");
		
		
		//查询subIbsysid下recordNum
		IDataset eopAttrInfos = WorkformAttrBean.qryRecordNumBySubIbsysid(subIbsysid);
		if (IDataUtil.isNotEmpty(eopAttrInfos)) {
			for (int i = 0; i < eopAttrInfos.size(); i++) {
				String recordNum = eopAttrInfos.getData(i).getString("RECORD_NUM");
				IDataset attrInfos = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysid, recordNum);
				
				if (IDataUtil.isEmpty(attrInfos)) {
					attrInfos = new DatasetList();
				}
				attrInfos.addAll(comAttrInfos);
				
				IData attrData = new DataMap();
				for (int j = 0; j < attrInfos.size(); j++) {
					attrData.put(attrInfos.getData(j).getString("ATTR_CODE"), attrInfos.getData(j).getString("ATTR_VALUE"));
				}
				
				attrData.put("RECORD_NUM", recordNum);
				allAttrInfos.add(attrData);
				
			}
		}
		
		if (IDataUtil.isNotEmpty(allAttrInfos)) {
			IDataset markertingInfos = new DatasetList();
			
//			for (int k = 0; k < allAttrInfos.size(); k++) {
				IData data = allAttrInfos.getData(0);
				String marketingStartDate = data.getString("C_MARKETINGLINE_STARTDATE","");
				String marketingMonth = data.getString("C_MARKETING_MONTH","");
				String marketingEndDate = SysDateMgr.endDateOffset(marketingStartDate, marketingMonth, "2");
				String auditStartDate = SysDateMgr.endDateOffset(marketingStartDate, String.valueOf(Integer.valueOf(marketingMonth)-1), "2");
				
				IData markData = new DataMap();
				markData.put("IBSYSID_MARKETING", ibsysID);
				markData.put("MARKETING_NAME", data.getString("C_MARKETING_NAME",""));
				markData.put("IBSYSID_AUDIT", "");
				markData.put("CUSTOMNO", data.getString("GROUP_ID"));
				markData.put("CUSTOMNAME", data.getString("CUST_NAME"));
				markData.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(eweInfos.first().getString("CITY_CODE")));
				markData.put("APPLY_STAFF_ID", eweInfos.first().getString("ACCEPT_STAFF_ID"));
				markData.put("AUDIT_STAFF_ID", "");
				markData.put("MARKETING_IS_SUCC", data.getString("C_MARKETING_IS_SUCC",""));
				markData.put("MARKETING_MONTH", data.getString("C_MARKETING_MONTH",""));
				markData.put("MARKETING_START_DATE", data.getString("C_MARKETINGLINE_STARTDATE",""));//营销活动预计开始日期
				markData.put("MARKETING_END_DATE", marketingEndDate);//营销活动结束日期
				markData.put("AUDIT_START_DATE", auditStartDate);//稽核开始日期
				markData.put("HANDLE_CODE", "0");//到期处理当前状态 0初始化 1已转正式计费 2已关闭
				markData.put("HANDLE_INFO", "初始化");//到期处理备注
				markData.put("HANDLE_DATE", "");//到期处理时间
				markData.put("AUDIT_DATE", "");//稽核处理时间
				markData.put("RESULT_CODE", "0");//当前状态 0等待处理 1等待稽核 2稽核通过 -1稽核不通过 
				markData.put("RESULT_INFO","等待处理");
				markData.put("ACCEPT_DATE", SysDateMgr.getSysTime());//执行时间
				markData.put("UPDATE_DATE", SysDateMgr.getSysTime());//修改时间
				markertingInfos.add(markData);
				
//			}
			
			WorkformMarketingBean.insertWorkformEoms(markertingInfos);
		}
		
	}
	
	private static void updateMarketingInfo(IData data) throws Exception{
		
		String marketingStartDate = data.getString("MARKETING_START_DATE","");
		String marketingMonth = data.getString("MARKETING_MONTH","");
		String marketingEndDate = SysDateMgr.endDateOffset(marketingStartDate, marketingMonth, "2");
		String auditStartDate = SysDateMgr.endDateOffset(marketingStartDate, String.valueOf(Integer.valueOf(marketingMonth)-1), "2");
		String marketingName = data.getString("MARKETING_NAME","");
		String marketingIsSucc = data.getString("MARKETING_IS_SUCC","");
		String applyStaffID = data.getString("ACCEPT_STAFF_ID");
		String cityName = data.getString("CITY_NAME");
		String resultCode = data.getString("RESULT_CODE");
		String resultInfo = data.getString("RESULT_INFO");
		String ibsysid = data.getString("IBSYSID");
		
		String update_time = SysDateMgr.getSysTime();
		data.put("UPDATE_DATE", update_time);
		StringBuilder sql = new StringBuilder(1000);
		sql.append("update TF_B_EOP_MARKETING set UPDATE_DATE=to_date(:UPDATE_DATE,'yyyy-mm-dd hh24:mi:ss') ");
		if (StringUtils.isNotEmpty(marketingStartDate)) {
			sql.append(" ,MARKETING_START_DATE = to_date(:MARKETING_START_DATE,'yyyy-mm-dd hh24:mi:ss') ");
		}
		
		if (StringUtils.isNotEmpty(marketingMonth)) {
			sql.append(" ,MARKETING_MONTH = :MARKETING_MONTH ");
		}
		
		if (StringUtils.isNotEmpty(marketingEndDate)) {
			sql.append(" ,MARKETING_END_DATE = to_date(:MARKETING_END_DATE,'yyyy-mm-dd hh24:mi:ss') ");
		}
		
		if (StringUtils.isNotEmpty(auditStartDate)) {
			sql.append(" ,AUDIT_START_DATE = to_date(:AUDIT_START_DATE,'yyyy-mm-dd hh24:mi:ss') ");
		}
		
		if (StringUtils.isNotEmpty(ibsysid)) {
			sql.append(" ,IBSYSID_MARKETING = :IBSYSID ");
		}
		
		if (StringUtils.isNotEmpty(marketingName)) {
			sql.append(" ,MARKETING_NAME = :MARKETING_NAME ");
		}
		
		if (StringUtils.isNotEmpty(marketingIsSucc)) {
			sql.append(" ,MARKETING_IS_SUCC = :MARKETING_IS_SUCC ");
		}
		
		if (StringUtils.isNotEmpty(applyStaffID)) {
			sql.append(" ,ACCEPT_STAFF_ID = :ACCEPT_STAFF_ID ");
		}
		
		if (StringUtils.isNotEmpty(cityName)) {
			sql.append(" ,CITY_NAME = :CITY_NAME ");
		}
		if (StringUtils.isNotEmpty(resultCode)) {
			sql.append(" ,RESULT_CODE = :RESULT_CODE ");
		}
		if (StringUtils.isNotEmpty(resultInfo)) {
			sql.append(" ,RESULT_INFO = :RESULT_INFO ");
		}
		sql.append(" where IBSYSID_MARKETING=:IBSYSID_MARKETING") ;
		
		Dao.executeUpdate(sql, data, Route.getJourDb(BizRoute.getRouteId()));
		
	}
	
	public  static void sendSMS(IData data) throws Exception{
		StringBuilder sql = new StringBuilder(1000);
		sql.append(" Select a.ibsysid_marketing,a.marketing_name,a.audit_start_date,a.marketing_end_date,a.result_code,a.apply_staff_id ");
		sql.append(" from tf_b_eop_marketing a ");
		sql.append(" Where ((a.result_code in ('0', '-1') and ");
		sql.append(" (a.audit_start_date = (to_date(to_char(sysdate, 'yyyy-mm-dd') ,'yyyy-mm-dd hh24:mi:ss'))  ");//--处理开始当天 
		sql.append(" or a.marketing_end_date = ((to_date(to_char(sysdate, 'yyyy-mm-dd') ,'yyyy-mm-dd hh24:mi:ss')) + 3) ");//--到期3天前
		sql.append(" or a.marketing_end_date <= (to_date(to_char(sysdate, 'yyyy-mm-dd') ,'yyyy-mm-dd hh24:mi:ss')))) ");//--到期当天及以后	
		sql.append(" or (a.result_code = '-1' and ");
		sql.append(" to_date(to_char(a.audit_date, 'yyyy-mm-dd'),'yyyy-mm-dd hh24:mi:ss') = ((to_date(to_char(sysdate, 'yyyy-mm-dd') ,'yyyy-mm-dd hh24:mi:ss')) - 1)))  ");//--稽核后一天
		
		IDataset marketingInfos = Dao.qryBySql(sql, data, Route.getJourDb(BizRoute.getRouteId()));
		if (IDataUtil.isEmpty(marketingInfos)) {
			return;
		}
		
		String sysDate = SysDateMgr.getSysDate();

		for (int i = 0; i < marketingInfos.size(); i++) {
			IData marketingInfo = marketingInfos.getData(i);
			String staffID = marketingInfo.getString("APPLY_STAFF_ID","");
			String serialNumber = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "SERIAL_NUMBER", staffID);
			marketingInfo.put("SERIAL_NUMBER", serialNumber);
			String NOTICE_CONTENT="";
			String SUBSCRIBE_ID = marketingInfo.getString("IBSYSID_MARKETING","");//ESOP营销活动工单号
			String SERIAL_NUMBER = marketingInfo.getString("SERIAL_NUMBER",""); //起单人联系号码
			String TITLE = marketingInfo.getString("MARKETING_NAME","");   //ESOP营销活动工单名称
			String AUDIT_START_DATE = marketingInfo.getString("AUDIT_START_DATE","");   //稽核开始时间
			String MARKETING_END_DATE = marketingInfo.getString("MARKETING_END_DATE",""); //营销活动结束时间
			String RESULT_CODE  = marketingInfo.getString("RESULT_CODE","");        //当前状态
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date auditDate  = formatter.parse(AUDIT_START_DATE);
			Date sysdateDate = formatter.parse(sysDate);
			Date endDate = formatter.parse(MARKETING_END_DATE);
			Date sysdateDate3 = formatter.parse(SysDateMgr.startDateOffset(sysDate, "3", "0"));
			
			int date1 = Integer.parseInt(formatter.format(auditDate));
			int date2 = Integer.parseInt(formatter.format(sysdateDate));
			int date3 = Integer.parseInt(formatter.format(endDate));
			int date4 = Integer.parseInt(formatter.format(sysdateDate3));
			
			if ( date1 == date2) {
				NOTICE_CONTENT = "温馨提示：您的营销活动工单号为["+SUBSCRIBE_ID+"]营销活动名称为["+TITLE+"]的试用营销活动工单体验期截至日期为["+MARKETING_END_DATE+"],请做好客户体验服务跟踪，促成业务签约。";
			}else if (date3 == date4  ) {
				NOTICE_CONTENT = "温馨提示：您的营销活动工单号为["+SUBSCRIBE_ID+"]营销活动名称为["+TITLE+"]的试用营销活动工单体验期截至日期为["+MARKETING_END_DATE+"],请及时登录NGBOSS进行活动到期处理。";
			}else if (date3 <=  date2){
				NOTICE_CONTENT = "温馨提示：您的营销活动工单号为["+SUBSCRIBE_ID+"]营销活动名称为["+TITLE+"]的试用营销活动工单已于["+MARKETING_END_DATE+"],到期，请尽快登录NGBOSS进行活动到期处理。";
			}else if ("-1".equals(RESULT_CODE)) {
				NOTICE_CONTENT = "温馨提示：您的营销活动工单号为["+SUBSCRIBE_ID+"]营销活动名称为["+TITLE+"]的试用营销活动工单于["+SysDateMgr.startDateOffset(sysDate, "1", "0")+"],被认定为稽核不通过，请尽快登录NGBOSS进行活动到期处理。";
			}
			
			IData iData = new DataMap();
			iData.put("RECV_OBJECT", SERIAL_NUMBER);
	        iData.put("NOTICE_CONTENT", NOTICE_CONTENT);
	        iData.put("FORCE_OBJECT", "10086000001");

	        SmsSend.insSms(iData);
			
		}
		
		
				
	}

	public static IDataset selMarketingByCondition2(IData inparam) throws Exception
	{
		inparam.put("MARKETING_END_DATE", SysDateMgr.getSysTime());
		return WorkformMarketingBean.selMarketingByCondition2(inparam);
	}
	
	
	public static IData getMarketingComInfo(IData inparam) throws Exception
	{
		String ibsysid = inparam.getString("IBSYSID","");
		String nodeID = inparam.getString("NODE_ID","");
		String recordNum = inparam.getString("RECORD_NUM");
		IDataset comAttrs = WorkformAttrHBean.qryEopAttrByIbsysidNodeid(ibsysid, nodeID, recordNum);
		
		if (IDataUtil.isEmpty(comAttrs)) {
			if (IDataUtil.isEmpty(comAttrs)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据ibsysid="+ibsysid+"，recordNum=0获取tf_bh_eop_attr表数据失败！");
			}
		}
		
		IData comData = new DataMap();
		for (int i = 0; i < comAttrs.size(); i++) {
			comData.put(comAttrs.getData(i).getString("ATTR_CODE",""), comAttrs.getData(i).getString("ATTR_VALUE",""));
		}
		
		comData.put("SUB_IBSYSID", comAttrs.first().getString("SUB_IBSYSID",""));
		
		return comData;
	}
	
	public static IDataset getMarketingAttrInfo(IData inparam) throws Exception
	{
		String subIbsysid = inparam.getString("SUB_IBSYSID","");
		IDataset eopRecordInfos = WorkformAttrHBean.qryRecordNumBySubIbsysid(subIbsysid);
		if (IDataUtil.isEmpty(eopRecordInfos)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据subIbsysid="+subIbsysid+"，recordNum>0获取tf_bh_eop_attr表数据失败！");
		}
		
		IDataset eopAttrList = new DatasetList();
		for (int i = 0; i < eopRecordInfos.size(); i++) {
			String recordNum = eopRecordInfos.getData(i).getString("RECORD_NUM");
			IDataset eopAttrs = WorkformAttrHBean.qryEopAttrBySubIbsysidRecordNum(subIbsysid, recordNum);
			
			if (IDataUtil.isEmpty(eopAttrs)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据subIbsysid="+subIbsysid+"，recordNum="+recordNum+"获取tf_bh_eop_attr表数据失败！");
			}
			
			IData eopData = new DataMap();
			for (int j = 0; j < eopAttrs.size(); j++) {
				eopData.put(eopAttrs.getData(j).getString("ATTR_CODE"), eopAttrs.getData(j).getString("ATTR_VALUE"));
			}
			eopAttrList.add(eopData);
		}
		
		return eopAttrList;
	}
	
	public static IDataset selMarketingByProductNO(IData inparam) throws Exception{
		
		return WorkformMarketingBean.selMarketingByProductNO(inparam);
		
	}
}