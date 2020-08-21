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



public class GrpWlwDiscntRebateApplyBean extends CSBizBean 
{
	
	/**
	 * 查询折扣信息
	 * @param data
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntApplyInfo(IData data, Pagination page) throws Exception 
	{
		data.put("CREATE_STAFF_ID", getVisit().getStaffId());

		//根据要求,使前台的EC_ID(group_id)转换为subs_id
		String serialNumber = data.getString("EC_ID", "");
		
		checkGrpWlwProductId(serialNumber);
		String subsId = getSubsIdForGroup(serialNumber);
		data.put("EC_ID", subsId);

		IDataset resultInfos = Dao.qryByCodeParser("TF_O_DISCNT_APPLY", "SEL_BY_ECID", data, page, Route.CONN_CRM_CEN);

		String httpHead = queryHttpHead();

		//新建一个IDATASET作为返回
		IDataset resultList = new DatasetList();
		if(IDataUtil.isNotEmpty(resultInfos))
		{
			for (int i = 0; i < resultInfos.size(); i++) 
			{
				IData param = new DataMap();
				param.putAll(resultInfos.getData(i));
				String uptime = param.getString("UPDATE_TIME","");
				if(-1 != uptime.indexOf('.')) 
				{				
					uptime = uptime.substring(0,uptime.indexOf('.'));
				}
				param.put("UPDATE_TIME", uptime);
				String additionalDocList = resultInfos.getData(i).getString("APPROVAL_DOC", "");
				if (StringUtils.isNotBlank(additionalDocList)) 
				{
					String[] doc = additionalDocList.split("-");
					for (int j = 1; j < doc.length; j++) 
					{
						IData param1 = new DataMap();
						param1.putAll(param);
						param1.put("APPROVAL_DOC", doc[j]);
						param1.put("URL", httpHead + "/" + doc[j]);
						resultList.add(param1);
					}
				}
				// 没有附件信息的项，直接添加
				else 
				{
					resultList.add(param);
				}
			}
		}
		return resultList;
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
	 * 提交新的折扣
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public boolean addDiscntApplyInfo(IData input) throws Exception 
	{
		String serialNumber = input.getString("EC_ID", "");
		
		String productId = checkGrpWlwProductId(serialNumber);
		String subsId = getSubsIdForGroup(serialNumber);
		input.put("EC_ID", subsId);
		input.put("PROVINCE_ID", "898");
		input.put("APPROVAL_RSLT", "9");
		input.put("RSRV_STR1", "9");
		if("20005013".equals(productId) || "20200402".equals(productId))
		{
			input.put("MAIN_PRODUCT", "1");
		} 
		else if("20161122".equals(productId) || "20200405".equals(productId)) 
		{
			input.put("MAIN_PRODUCT", "2");
		}
		else if("20171214".equals(productId))
		{
			input.put("MAIN_PRODUCT", "3");
		}
		else if("20161124".equals(productId) || "20200408".equals(productId))
		{
			input.put("MAIN_PRODUCT", "4");
		}
		
		String seq = getSeq();
		//操作流水号=机构省编码+业务编码+时间+序列号
		String OPR_SEQ = "8981" + "BIP3B622" + SysDateMgr.getSysDateYYYYMMDDHHMMSS() + seq;
		
		String productsInfo  = input.getString("DIS_PRODUCTS_INFO","");
		IDataset rateInfos = new DatasetList();
		if (StringUtils.isNotBlank(productsInfo) && productsInfo.length() > 2)
		{
			IDataset dataSet = new DatasetList(productsInfo);
			if(IDataUtil.isNotEmpty(dataSet))
			{
				for (int i = 0; i < dataSet.size(); i++) 
				{
					IData whiteData = dataSet.getData(i);
					if(IDataUtil.isNotEmpty(whiteData))
					{
						IData paramData = new DataMap();
						String prodId = whiteData.getString("PROD_ID","");
						String prodType = whiteData.getString("PROD_TYPE","");
						String rate = whiteData.getString("DISCNT_RATE","");
						String cardBind = whiteData.getString("CARD_BIND","");
						paramData.put("PROD_ID", prodId);
						paramData.put("PROD_TYPE", prodType);
						paramData.put("DISCNT_RATE", rate);
						paramData.put("CARD_BIND", cardBind);
						paramData.put("OPR_SEQ", OPR_SEQ);
						rateInfos.add(paramData);
					}
				}
			}
		}
		else 
		{
			String errMsg = "未获取到提交的折扣产品信息!";
			CSAppException.apperr(CrmCommException.CRM_COMM_103, errMsg);
		}
		
		input.put("OPR_SEQ", OPR_SEQ);
		String addTime = SysDateMgr.getSysTime();
		input.put("CREATE_STAFF_ID", getVisit().getStaffId());
		input.put("CREATE_TIME", addTime);
		input.put("UPDATE_TIME", addTime);
		input.put("APPLY_DATE", addTime);
		
		//增加一个限制，如果表里已存在相同的折扣，则打回
//		IData inparam = new DataMap();
//		inparam.put("EC_ID", subsId);
//		IDataset result = Dao.qryByCodeParser("TF_O_DISCNT_APPROVAL", "SEL_ALL_BY_ECID", inparam, null, Route.CONN_CRM_CEN);
//		
//		if(IDataUtil.isNotEmpty(result)){
//			for (int i = 0; i < result.size(); i++) {
//				if(input.getString("RSRV_STR1", "").equals(result.getData(i).get("RSRV_STR1"))){
//					if(input.getString("DISCNT_RATE", "").equals(result.getData(i).get("DISCNT_RATE"))){
//						//还要判断再次申请同一折扣的情况
//						if(!"0".equals(result.getData(i).get("RSRV_STR2"))){
//							if("1".equals(result.getData(i).get("RSRV_STR2")) && !"0".equals(result.getData(i).get("APPROVAL_RSLT"))){
//								//都相等需要报错
//								CSAppException.apperr(CrmCommException.CRM_COMM_103, "该集团已申请过相同折扣类型的相同的折扣值，不允许再次申请！");
//							}
//							
//						}
//					}
//				}
//			}
//		}
		

		// 获取本地的文件名并拼串
		String fileId = input.getString("PROV_DOC", "");
		if(StringUtils.isNotBlank(fileId)) 
		{
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
			//获取文件类型
			String[] s = fileName.split("\\.");
			//拼接成标准形式，覆盖原本的文件名
			input.put("PROV_DOC", "BOSS2PBSS_DiscountApply_898_" + creaTime4 + "." + s[s.length - 1] + ".gz");
		}

		boolean flag = Dao.insert("TF_O_DISCNT_APPLY", input);
		Dao.insert("TF_O_DISCNT_APPLY_SUB", rateInfos);
		return flag;
	}


	/**
	 * 车联网——审批结果接口
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public int updateCarGroupRateInfo(IData param) throws Exception 
	{
		param.put("UPDATE_TIME", SysDateMgr.getSysTime());
		return Dao.executeUpdateByCodeCode("TF_O_DISCNT_APPROVAL", "UP_BY_OPR_SEQ", param, Route.CONN_CRM_CEN);

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
	 * 获取序列号
	 * @return
	 * @throws Exception
	 */
	private static String getSeq() throws Exception
	{
		IData param = new DataMap();
		IDataset seqs = Dao.qryByCode("TF_O_DISCNT_APPLY", "SEL_SEQ", param, Route.CONN_CRM_CEN);
		IData seq = seqs.getData(0);
		String id = seq.getString("SEQ");
		if (id.length() == 1)
		{
			id = "00000" + id;
		}
		if (id.length() == 2) 
		{
			id = "0000" + id;
		}
		if (id.length() == 3) 
		{
			id = "000" + id;
		}
		if (id.length() == 4) 
		{
			id = "00" + id;
		}
		if (id.length() == 5) 
		{
			id = "0" + id;
		}
		return id;
	}

}
