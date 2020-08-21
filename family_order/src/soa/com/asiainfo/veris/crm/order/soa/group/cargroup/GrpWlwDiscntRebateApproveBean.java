package com.asiainfo.veris.crm.order.soa.group.cargroup;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.DedInfoException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class GrpWlwDiscntRebateApproveBean extends CSBizBean {
	
	
	public static IDataset queryRebateApproveInfo(IData data, Pagination page) throws Exception 
	{
		//根据要求,使前台的EC_ID(group_id)转换为subs_id
		String serialNumber = data.getString("EC_ID", "");
		
		checkGrpWlwProductId(serialNumber);
		String subsId = getSubsIdForGroup(serialNumber);
		data.put("EC_ID", subsId);

		IDataset result = Dao.qryByCodeParser("TF_O_DISCNT_APPLY", "SEL_ALL_BY_ECID", data, page, Route.CONN_CRM_CEN);

		String httpHead = queryHttpHead();

		// 新建一个IDATASET作为返回
		IDataset resultInfos = new DatasetList();
		for (int i = 0; i < result.size(); i++) 
		{
			IData param = new DataMap();
			param.putAll(result.getData(i));
			String uptime = param.getString("UPDATE_TIME","");
			if(-1 != uptime.indexOf('.')) 
			{				
				uptime = uptime.substring(0,uptime.indexOf('.'));
			}
			param.put("UPDATE_TIME", uptime);
			String provDoc = result.getData(i).getString("PROV_DOC", "");
			if (StringUtils.isNotBlank(provDoc)) 
			{
				param.put("URL", httpHead + "/" + provDoc);
			}
			resultInfos.add(param);
		}
		return resultInfos;
	}
	
	/**
	 * 
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	private static String checkGrpWlwProductId(String serialNumber) throws Exception 
	{
		if (StringUtils.isBlank(serialNumber))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "集团产品编码不能为空！");
		}
		
		// 查询集团用户信息
        IData grpUserInfo = UcaInfoQry.qryUserInfoBySnForGrp(serialNumber);
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }
        String grpUserId = grpUserInfo.getString("USER_ID","");
        
		IData grpProductInfo = UcaInfoQry.qryMainProdInfoByUserIdForGrp(grpUserId);
        if (IDataUtil.isEmpty(grpProductInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_017, serialNumber);
        }
        String productId = grpProductInfo.getString("PRODUCT_ID","");
		
        if(!("20005013".equals(productId) || "20161122".equals(productId) 
        		|| "20171214".equals(productId) || "20161124".equals(productId)
        		|| "20200402".equals(productId) || "20200405".equals(productId)
        		|| "20200408".equals(productId)))
        {
        	String errInfo = "该集团产品不是下列中的产品:机器卡、车联网、NB-IOT、和对讲!业务不能进行!";
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, errInfo);
        }
        
        return productId;
	}
	
	/**
	 * 用于查询时转换group_id为subs_id
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	private static String getSubsIdForGroup(String serialNumber) throws Exception 
	{
        IData input = new DataMap();
		input.put("SERIAL_NUMBER", serialNumber);
		IDataset result = Dao.qryByCodeParser("TF_F_INSTANCE_PF", "SEL_SUBSID_BY_GROUPID", input, Route.getCrmDefaultDb());
		if (IDataUtil.isEmpty(result)) 
		{
			String errInfo = "未获取到该集团办理如下的产品：机器卡、车联网、NB-IOT、和对讲,业务不能进行!";
			CSAppException.apperr(CrmCommException.CRM_COMM_103, errInfo);
		}
		
		String subsId = result.getData(0).getString("SUBS_ID");
		
		return subsId;
	}
	
	/**
	 * 车联网审批--查看审批文档
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	private static String queryHttpHead() throws Exception 
	{
		String url = "";
		IData urldata = new DataMap();
		urldata.put("PARAM_ATTR", "7778");
		urldata.put("PARAM_CODE", "URL");
		// 查询附件存放地址
		IDataset urlset = queryUrlPara(urldata);
		if (urlset != null && urlset.size() > 0) 
		{
			url = urlset.getData(0).getString("PARA_CODE1", "").trim();
		} 
		else 
		{
			CSAppException.apperr(DedInfoException.CRM_DedInfo_16);
		}
		return url;
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
		String rsltState = input.getString("RSLT_STATE","");
		String[] oprSeqs = OPR_SEQS.split(",");
		for (int i = 0; i < oprSeqs.length; i++) 
		{
			String oprSeq = oprSeqs[i];
			IData param = queryByOPRSEQ(oprSeq);
			//格式化申请时间为YYYYMMDD
			String applyDate = param.getString("APPLY_DATE",SysDateMgr.getSysDateYYYYMMDD());
			applyDate = applyDate.replace("-", "");
			applyDate = applyDate.replace(":", "");
			applyDate = applyDate.replace(".0", "");
			applyDate = applyDate.replace(" ", "");
			applyDate = applyDate.substring(0, 8);
			param.put("APPLY_DATE", applyDate);
			
			String rsrvStr1Rslt = param.getString("RSRV_STR1","");
			if("9".equals(rsrvStr1Rslt))
			{
				
				if("1".equals(rsltState))//通过
				{
					param.put("OPERATION_TYPE", "ADD");
					param.put("PBOSS_APPROVAL_NUM", "");
					//折扣产品信息
					IDataset subInfos = queryApplySubByOprSeq(oprSeq);
					if(IDataUtil.isEmpty(subInfos))
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"折扣产品信息为空!");
					}
					//subInfos.clear();
					
					//param.put("OPERATION_TYPE", "MOD");
					//param.put("PBOSS_APPROVAL_NUM", param.getString("APPROVAL_NO",""));
					
					
					IDataset outparamList = IBossCall.addCarGroupRateApproveInfo(param,subInfos);
					IData rlt = outparamList.getData(0);
					if (!"0000".equals(rlt.getString("X_RSPCODE", "")) && !"00".equals(rlt.getString("X_RSPCODE", ""))
							&& !"0".equals(rlt.getString("X_RSPCODE", ""))) 
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "很抱歉，物联网折扣申请提交出错:" + rlt.getString("X_RESULTINFO"));
					}
					//提交审核成功
					IData param1 = new DataMap();
					param1.put("OPR_SEQ", oprSeq);
					param1.put("RSRV_STR1", "1");
					int resultFlag = Dao.executeUpdateByCodeCode("TF_O_DISCNT_APPLY", "UP_APPLY_RSLT_BY_OPRSEQ", param1, Route.CONN_CRM_CEN);
					if(resultFlag == 0) 
					{
						flag = false;
					}
				}
				else if("0".equals(rsltState))//不通过
				{
					//提交设置状态为0
					IData param1 = new DataMap();
					param1.put("OPR_SEQ", oprSeq);
					param1.put("RSRV_STR1", "0");
					int resultFlag = Dao.executeUpdateByCodeCode("TF_O_DISCNT_APPLY", "UP_APPLY_RSLT_BY_OPRSEQ", param1, Route.CONN_CRM_CEN);
					if(resultFlag == 0) 
					{
						flag = false;
					}
				}
				
			}
			
		}
		return flag;
	}
	
	/**
	 * 
	 * @param OPR_SEQ
	 * @return
	 * @throws Exception
	 */
	public static IData queryByOPRSEQ(String OPR_SEQ) throws Exception 
	{
		IData input = new DataMap();
		input.put("OPR_SEQ", OPR_SEQ);
		IDataset result = Dao.qryByCodeParser("TF_O_DISCNT_APPLY", "SEL_APPLY_INFO_BY_OPR_SEQ", input, Route.CONN_CRM_CEN);
		return result.getData(0);
	}

	/**
	 * 
	 * @param OPR_SEQ
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryApplySubByOprSeq(String OPR_SEQ) throws Exception 
	{
		IData input = new DataMap();
		input.put("OPR_SEQ", OPR_SEQ);
		IDataset result = Dao.qryByCodeParser("TF_O_DISCNT_APPLY", "SEL_APPLYSUB_INFO_BY_OPR_SEQ", input, Route.CONN_CRM_CEN);
		return result;
	}
	
	/**
	 * 查询查看的url
	 * @param param
	 * @return
	 * @throws Exception
	 */
	private static IDataset queryUrlPara(IData param) throws Exception 
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT * FROM TD_S_COMMPARA A ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND A.PARAM_ATTR= :PARAM_ATTR ");
		parser.addSQL(" AND A.SUBSYS_CODE='CSM' ");
		parser.addSQL(" AND A.PARAM_CODE= :PARAM_CODE ");
		parser.addSQL(" AND END_DATE > SYSDATE ");
		return Dao.qryByParse(parser);
	}
	
	
	/**
	 * 审批结果接口
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public int updateCarGroupRateInfo(IData param) throws Exception 
	{
		param.put("UPDATE_TIME", SysDateMgr.getSysTime());
		return Dao.executeUpdateByCodeCode("TF_O_DISCNT_APPLY", "UP_BY_OPR_SEQ", param, Route.CONN_CRM_CEN);
	}
	
	/**
	 * 
	 * @param data
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryRebateDetailsByOprSeq(IData data, Pagination page) throws Exception 
	{
		IDataset result = Dao.qryByCodeParser("TF_O_DISCNT_APPLY", "SEL_SUBINFO_BY_OPRSEQ_PAGE", data, page, Route.CONN_CRM_CEN);
		return result;
	}
}
