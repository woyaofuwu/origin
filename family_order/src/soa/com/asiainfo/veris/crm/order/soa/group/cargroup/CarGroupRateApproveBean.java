package com.asiainfo.veris.crm.order.soa.group.cargroup;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.DedInfoException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class CarGroupRateApproveBean extends CSBizBean {
	// 根据集团客户标识查询历史信息
	public static IDataset queryByECID(IData data, Pagination page) throws Exception {

		// 根据要求，使前台的EC_ID(group_id)转换为subs_id
		String subsId = changeGroupIdToSubsId(data.getString("EC_ID", ""));
		data.put("EC_ID", subsId);

		IDataset result = Dao.qryByCodeParser("TF_O_DISCNT_APPROVAL", "SEL_ALL_BY_ECID", data, page, Route.CONN_CRM_CEN);

		String httpHead = queryHttpHead();

		// 新建一个IDATASET作为返回
		IDataset result1 = new DatasetList();
		for (int i = 0; i < result.size(); i++) {
			IData param = new DataMap();
			param.putAll(result.getData(i));
			String uptime = param.getString("UPDATE_TIME","");
			if(-1 != uptime.indexOf('.')) {				
				uptime = uptime.substring(0,uptime.indexOf('.'));
			}
			param.put("UPDATE_TIME", uptime);
			String provDoc = result.getData(i).getString("PROV_DOC", "");
			if (StringUtils.isNotBlank(provDoc)) {
				param.put("URL", httpHead + "/" + provDoc);
			}
			result1.add(param);
		}
		return result1;

	}
	

	public static IData queryByOPRSEQ(String OPR_SEQ) throws Exception {
		IData input = new DataMap();
		input.put("OPR_SEQ", OPR_SEQ);
		IDataset result = Dao.qryByCodeParser("TF_O_DISCNT_APPROVAL", "SEL_ALL_BY_ECID", input, Route.CONN_CRM_CEN);
		return result.getData(0);
	}

	/**
	 * 车联网--提交新的集团客户折扣
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public boolean addCarGroupRateApprove(IData input) throws Exception {
		boolean flag = true;
		//批量审批截取条记录的操作流水号
		
		String OPR_SEQS = input.getString("OPR_SEQS", "");
		String[] oprSeqs = OPR_SEQS.split(",");
		for (int i = 0; i < oprSeqs.length; i++) {
			IData param = queryByOPRSEQ(oprSeqs[i]);
			//格式化申请时间为YYYYMMDD
			String applyDate = param.getString("APPLY_DATE",SysDateMgr.getSysDateYYYYMMDD()).replace("-", "").replace(":", "").replace(".0", "").replace(" ", "");
			applyDate = applyDate.substring(0, 8);
			param.put("APPLY_DATE", applyDate);
			//如果审批结果为通过并且折扣申请类型不为车联网定向流量折扣审批，则上发PBOSS
			if("1".equals(input.getString("RSRV_STR2", "")) && !"1".equals(param.getString("RSRV_STR1",""))) {
				param.put("APPLY_TYPE", param.getString("RSRV_STR1",""));
				IDataset outparamList = IBossCall.addCarGroupRateInfo(param);
				IData rlt = outparamList.getData(0);
				if (!"0000".equals(rlt.getString("X_RSPCODE", "")) && !"00".equals(rlt.getString("X_RSPCODE", ""))
						&& !"0".equals(rlt.getString("X_RSPCODE", ""))) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "很抱歉，物联网折扣申请提交出错:" + rlt.getString("X_RESULTINFO"));
				}
			} else if("1".equals(input.getString("RSRV_STR2", "")) && "1".equals(param.getString("RSRV_STR1",""))) {
				IData param1 = new DataMap();
				param1.put("OPR_SEQ", oprSeqs[i]);
				param1.put("APPROVAL_RSLT", "1");
				param1.put("APPROVAL_NO", "");
				param1.put("APPROVAL_COMM", "");
				param1.put("APPROVAL_DOC", "");
				int r = Dao.executeUpdateByCodeCode("TF_O_DISCNT_APPROVAL", "UP_BY_OPR_SEQ", param1, Route.CONN_CRM_CEN);
				if(r==0) flag = false;
			}
			IData upInput = new DataMap();
			upInput.put("RSRV_STR2",input.getString("RSRV_STR2", ""));
			upInput.put("OPR_SEQ", oprSeqs[i]);
			int r = Dao.executeUpdateByCodeCode("TF_O_DISCNT_APPROVAL", "UP_APPRRSLT_BY_OPR_SEQ", upInput,Route.CONN_CRM_CEN);
			if(r==0) flag = false;
		}
		return flag;
	}

	public static IDataset getGroupIdBySubsId(String group_id) throws Exception{
		IData input = new DataMap();
		input.put("SERIAL_NUMBER", group_id);
		IDataset result = new DatasetList();
		result = Dao.qryByCodeParser("TF_F_INSTANCE_PF", "SEL_SUBSID_BY_GROUPID", input, Route.getCrmDefaultDb());
		return result;
	}
	
	/**
	 *  用于查询时转换group_id为subs_id
	 * @param group_id
	 * @return
	 * @throws Exception
	 */
	public static String changeGroupIdToSubsId(String group_id) throws Exception {
		if ("".equals(group_id)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "集团产品编码不能为空！");
		}
		// 先判断集团是否办理车联网开户
		IDataset result = getGroupIdBySubsId(group_id);
		if (IDataUtil.isEmpty(result)) {// 若为空则该用户不允许办理折扣审批
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "该集团未办理车联网、机器卡或者和对讲开户，不允许办理物联网折扣审批！");
		}
		String subsId = result.getData(0).getString("SUBS_ID");
		return subsId;
	}
	
	/**
	 * 查询查看的url
	 */
	public static IDataset queryUrlPara(IData param) throws Exception {

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from td_s_commpara a ");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and a.param_attr= :PARAM_ATTR ");
		parser.addSQL(" and a.subsys_code='CSM' ");
		parser.addSQL(" and a.param_code= :PARAM_CODE ");
		parser.addSQL(" and end_date>sysdate ");

		return Dao.qryByParse(parser);
	}

	/**
	 * 车联网审批--查看审批文档
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static String queryHttpHead() throws Exception {
		String url = "";
		IData urldata = new DataMap();
		urldata.put("PARAM_ATTR", "7778");
		urldata.put("PARAM_CODE", "URL");
		IDataset urlset = queryUrlPara(urldata);// 查询附件存放地址
		if (urlset != null && urlset.size() > 0) {
			url = urlset.getData(0).getString("PARA_CODE1", "").trim();
		} else {
			CSAppException.apperr(DedInfoException.CRM_DedInfo_16);
		}
		return url;

	}
	
	/**
	 * 根据ECID、approvalNo、discntRate查询审批文号、折扣率
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryGrpEcByProNoRate(IData param) throws Exception {
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT T.EC_ID,");
		parser.addSQL("       T.DISCNT_RATE,");
		parser.addSQL("       T.APPROVAL_NO,");
		parser.addSQL("       T.CREATE_TIME,");
		parser.addSQL("       T.CREATE_STAFF_ID");
		parser.addSQL("  FROM TF_O_DISCNT_APPROVAL T");
		parser.addSQL(" WHERE T.EC_ID = :EC_ID");
		parser.addSQL("   AND T.DISCNT_RATE = :DISCNT_RATE");
		parser.addSQL("   AND T.APPROVAL_NO = :APPROVAL_NO");
		return Dao.qryByParse(parser,Route.CONN_CRM_CEN);
	}
	
}
