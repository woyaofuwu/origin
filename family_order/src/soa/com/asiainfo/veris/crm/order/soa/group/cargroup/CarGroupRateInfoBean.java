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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;



public class CarGroupRateInfoBean extends CSBizBean {
	// 根据集团客户标识查询历史信息
	public static IDataset queryByECID(IData data, Pagination page) throws Exception {
		data.put("CREATE_STAFF_ID", getVisit().getStaffId());

		// 根据要求，使前台的EC_ID(group_id)转换为subs_id
		String subsId = changeGroupIdToSubsId(data.getString("EC_ID", ""));
		data.put("EC_ID", subsId);

		IDataset result = Dao.qryByCodeParser("TF_O_DISCNT_APPROVAL", "SEL_BY_ECID", data, page, Route.CONN_CRM_CEN);

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
			String additionalDocList = result.getData(i).getString("APPROVAL_DOC", "");
			if (StringUtils.isNotBlank(additionalDocList)) {
				String[] doc = additionalDocList.split("-");
				for (int j = 1; j < doc.length; j++) {
					IData param1 = new DataMap();
					param1.putAll(param);
					param1.put("APPROVAL_DOC", doc[j]);
					param1.put("URL", httpHead + "/" + doc[j]);
					result1.add(param1);
				}
			}
			// 没有附件信息的项，直接添加
			else {
				result1.add(param);
			}
		}

		return result1;

	}

	/**
	 * 车联网--提交新的集团客户折扣
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public boolean addCarGroupRateInfo(IData input) throws Exception {

		// 根据要求，使前台的EC_ID(group_id)转换为subs_id
		String subsId = changeGroupIdToSubsId(input.getString("EC_ID", ""), input.getString("RSRV_STR1", ""));
		input.put("EC_ID", subsId);
		String seq = getSeq();
		// 操作流水号=机构省编码+业务编码+时间+序列号
		String OPR_SEQ = "8981" + "BIP3B622" + SysDateMgr.getSysDateYYYYMMDDHHMMSS() + seq;
		input.put("OPR_SEQ", OPR_SEQ);
		String addTime = SysDateMgr.getSysTime();
		input.put("CREATE_STAFF_ID", getVisit().getStaffId());
		input.put("CREATE_TIME", addTime);
		input.put("UPDATE_TIME", addTime);
		input.put("APPLY_DATE", addTime);
		input.put("RSRV_STR2", "9");// 省侧审批结果默认为待处理
		
		//增加一个限制，如果表里已存在相同的折扣，则打回
		IData inparam = new DataMap();
		inparam.put("EC_ID", subsId);
		IDataset result = Dao.qryByCodeParser("TF_O_DISCNT_APPROVAL", "SEL_ALL_BY_ECID", inparam, null, Route.CONN_CRM_CEN);
		if(IDataUtil.isNotEmpty(result)){
			for (int i = 0; i < result.size(); i++) {
				if(input.getString("RSRV_STR1", "").equals(result.getData(i).get("RSRV_STR1"))){
					if(input.getString("DISCNT_RATE", "").equals(result.getData(i).get("DISCNT_RATE"))){
						//还要判断再次申请同一折扣的情况
						if(!"0".equals(result.getData(i).get("RSRV_STR2"))){
							if("1".equals(result.getData(i).get("RSRV_STR2")) && !"0".equals(result.getData(i).get("APPROVAL_RSLT"))){
								//都相等需要报错
								CSAppException.apperr(CrmCommException.CRM_COMM_103, "该集团已申请过相同折扣类型的相同的折扣值，不允许再次申请！");
							}
							
						}
					}
				}
			}
		}
		

		// 获取本地的文件名并拼串
		String fileId = input.getString("PROV_DOC", "");
		if (!"".equals(fileId)) {
			IData param = new DataMap();
			param.put("FILE_ID", fileId);
			IData file = Dao.qryByCode("WD_F_FTPFILE", "SEL_BY_FILE_ID", param, Route.CONN_CRM_CEN).getData(0);
			String fileName = file.getString("FILE_NAME", "");
			// creaTime格式类似2018-01-18 12:32:33,转换为一串数字20180118123233
			String creaTime = file.getString("CREA_TIME", "");
			String creaTime1 = creaTime.replace("-", "");
			String creaTime2 = creaTime1.replace(" ", "");
			String creaTime3 = creaTime2.replace(":", "");
			String creaTime4 = creaTime3.replace(".0", "");
			// 获取文件类型
			String[] s = fileName.split("\\.");
			// 拼接成标准形式，覆盖原本的文件名
			input.put("PROV_DOC", "BOSS2PBSS_DiscountApply_898_" + creaTime4 + "." + s[s.length - 1] + ".gz");
		}

		/*
		 * 所有上发PBOSS流程在物联网折扣省侧审批页面处理 CarGroupRateApproveBean.java //调提交审批接口 IDataset
		 * outparamList = IBossCall.addCarGroupRateInfo(input); IData param =
		 * outparamList.getData(0); if (!"0000".equals(param.getString("X_RSPCODE",
		 * ""))&&!"00".equals(param.getString("X_RSPCODE",
		 * ""))&&!"0".equals(param.getString("X_RSPCODE", ""))) {
		 * CSAppException.apperr(CrmCommException.CRM_COMM_103,"很抱歉，车联网折扣申请提交出错:" +
		 * param.getString("X_RESULTINFO")); }
		 */

		return Dao.insert("TF_O_DISCNT_APPROVAL", input);
	}

	public static IDataset getGroupIdBySubsId(String group_id, String prodCode) throws Exception {
		IData input = new DataMap();
		//根据新一代改造，此时不使用group_id 而是直接使用集团订购过产品的user_id 对应的SERIAL_NUMBER，这样可以直接得到对应的SUBS_ID
		input.put("SERIAL_NUMBER", group_id);
		IDataset result = new DatasetList();
		// 获取省内车联网产品编码
		if("".equals(prodCode)){
			//为空时是查询过来，不是新增，就不用输入产品ID，不做校验，因为不用判断apply_type
			result = Dao.qryByCodeParser("TF_F_INSTANCE_PF", "SEL_SUBSID_BY_GROUPID", input, Route.getCrmDefaultDb());
			return result;
		}
		String productId = CommparaInfoQry.getDefaultOpenSvcId("CSM", "9015", prodCode).getData(0)
				.getString("PARAM_CODE");
		input.put("PRODUCT_ID", productId);
		result = Dao.qryByCode("TF_F_INSTANCE_PF", "SEL_SUBSID_BY_GROUPID", input, Route.getCrmDefaultDb());
		return result;
	}

	/**
	 * 用于查询时转换group_id为subs_id
	 * 
	 * @param group_id
	 * @return
	 * @throws Exception
	 */
	public static String changeGroupIdToSubsId(String group_id) throws Exception {
		if ("".equals(group_id)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "集团产品编码不能为空！");
		}
		// 先判断集团是否办理车联网开户
		IDataset result = getGroupIdBySubsId(group_id, "");
		if (IDataUtil.isEmpty(result)) {// 若为空则该用户不允许办理折扣审批
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "该集团未办理车联网、机器卡或者和对讲开户，不允许办理物联网折扣审批！");
		}
		String subsId = result.getData(0).getString("SUBS_ID");
		return subsId;
	}

	/**
	 * 用于新增时转换group_id为subs_id
	 * 
	 * @param group_id
	 * @param apply_type
	 * @return
	 * @throws Exception
	 */
	public static String changeGroupIdToSubsId(String group_id, String apply_type) throws Exception {
		if ("".equals(group_id)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "集团产品编码不能为空！");
		}
		IDataset result = null;

		if ("3".equals(apply_type)) {// 折扣审批为3则为机器开或和对讲折扣审批，先判断机器卡
			result = getGroupIdBySubsId(group_id, "I00010300001");
			if (IDataUtil.isEmpty(result)) {// 若为空则判断是否办理和对讲开户
				result = getGroupIdBySubsId(group_id, "I00011000001");
				if (IDataUtil.isEmpty(result)) {// 若为空则该用户不允许办理折扣审批
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "该集团未办理机器卡或者和对讲开户，不允许办理物联网折扣审批！");
				}
			}
		} else {
			//折扣申请类型为1或2则判车联网
			result = getGroupIdBySubsId(group_id, "I00010800001");
			if(result.isEmpty()) {				
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该集团未办理车联网开户，不允许办理物联网折扣审批！");
			}
		}
		

		String subsId = result.getData(0).getString("SUBS_ID");
		return subsId;
	}

	/**
	 * 车联网——审批结果接口
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public int updateCarGroupRateInfo(IData param) throws Exception {
		param.put("UPDATE_TIME", SysDateMgr.getSysTime());
		return Dao.executeUpdateByCodeCode("TF_O_DISCNT_APPROVAL", "UP_BY_OPR_SEQ", param, Route.CONN_CRM_CEN);

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

	// 获取序列号
	public static String getSeq() throws Exception {
		IData param = new DataMap();
		IDataset seqs = Dao.qryByCode("TF_O_DISCNT_APPROVAL", "SEL_SEQ", param, Route.CONN_CRM_CEN);
		IData seq = seqs.getData(0);
		String id = seq.getString("SEQ");
		if (id.length() == 1) {
			id = "00000" + id;
		}
		if (id.length() == 2) {
			id = "0000" + id;
		}
		if (id.length() == 3) {
			id = "000" + id;
		}
		if (id.length() == 4) {
			id = "00" + id;
		}
		if (id.length() == 5) {
			id = "0" + id;
		}
		return id;

	}

}
