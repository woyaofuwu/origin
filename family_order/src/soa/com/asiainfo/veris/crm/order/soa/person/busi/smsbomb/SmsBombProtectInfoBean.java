package com.asiainfo.veris.crm.order.soa.person.busi.smsbomb;




import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class SmsBombProtectInfoBean extends CSBizBean{
	
	private static Logger logger = Logger.getLogger(SmsBombProtectInfoBean.class);
	
	/**
	 * 
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public IDataset querySmsBombProtectInfoBySn(IData param) throws Exception
	{
		return SmsBombProtectInfoQry.querySmsBombProtectInfoBySn(param);
	}

	/**
	 * 
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public IDataset querySmsBombInfoBySn(IData param) throws Exception
	{
		return SmsBombProtectInfoQry.querySmsBombInfoBySn(param);
	}
	
	/**
	 * 
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public IDataset querySmsBombWhiteInfoByRecvId(IData param) throws Exception
	{
		IDataset results = SmsBombProtectInfoQry.querySmsBombWhiteInfoByRecvId(param);
		if(IDataUtil.isNotEmpty(results))
		{
			for (int i = 0; i < results.size(); i++) 
			{
				IData data = results.getData(i);
				String whiteType = data.getString("WHITE_TYPE","");
				String whiteName = StaticUtil.getStaticValue("WHITE_NUM_TYPE", whiteType);
				data.put("WHITE_TYPE_NAME", whiteName);
			}
		}
		return results;
	}
	
	/**
	 * 批量增加保护名单
	 * @param param
	 * @throws Exception
	 */
	public void addProtectInfos(IData param) throws Exception
	{
		String serialNumber = param.getString("PROTECT_NUM","");
		
		//归属省ID
		IDataset results = SmsBombProtectInfoQry.qryEpareyCodeOut(serialNumber);
		if(IDataUtil.isEmpty(results))
		{
		    CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码不是移动号码");
		}
		
		String prodId = results.getData(0).getString("PROV_CODE");
		param.put("PROV_ID", prodId);
		
		//获得唯一编码
	    String   provinceId = getProvinceID();
	    String  indictSeq = getIndictSequence(provinceId);
	    param.put("RECV_ID", indictSeq);
	    
		String expireDate  = param.getString("EXPIRE_DATE","");
		IData data = new DataMap();
		IDataset addDatas = new DatasetList();
		IDataset addSubDatas = new DatasetList();
		
		data.put("PROV_ID", prodId);
		data.put("EXPIRE_DATE", expireDate);
		data.put("RECV_ID", indictSeq);
		data.put("SERIAL_NUMBER", serialNumber);
		data.put("REMOVE_TAG", "0");
		data.put("CREATE_STAFF_ID",  CSBizBean.getVisit().getStaffId());
		data.put("UPDATE_TYPE","1");//新增
		addDatas.add(data);
	
		
		//处理新增白名单
		String protectDataSub  = param.getString("PROTECT_DATASUB","");
		StringBuilder whiteListStr = new StringBuilder("");
		if (!StringUtils.isBlank(protectDataSub) && protectDataSub.length() > 2)
		{
			IDataset dataSet = new DatasetList(protectDataSub);
			if(IDataUtil.isNotEmpty(dataSet))
			{
				for (int i = 0; i < dataSet.size(); i++) 
				{
					IData whiteData = dataSet.getData(i);
					if(IDataUtil.isNotEmpty(whiteData))
					{
						String whiteNum = whiteData.getString("WHITE_NUM","");
						String whiteType = whiteData.getString("WHITE_TYPE","");
						whiteData.put("RECV_ID", indictSeq);
						whiteData.put("REMOVE_TAG", "0");
						whiteData.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
						addSubDatas.add(whiteData);
						whiteListStr.append("1|");
						whiteListStr.append(whiteType);
						whiteListStr.append("|");
						whiteListStr.append(whiteNum);
						whiteListStr.append(",");
					}
				}
			}
		}
		String resultList = "";
		if(whiteListStr.length() > 0)
		{
			resultList = whiteListStr.substring(0, whiteListStr.length() - 1);
		}
		param.put("WHITE_LIST", resultList);
		param.put("UPDATE_TYPE", "1");
		
		SmsBombProtectInfoQry.addProtectInfos(addDatas);
		SmsBombProtectInfoQry.addProtectWhiteInfos(addSubDatas);
	    syncSmsProtectList(param);
	}
	
	/**
	 * 删除保护名单
	 * @param param
	 * @throws Exception
	 */
	public void delProtectInfos(IData param) throws Exception
	{
		String updateStaffId = CSBizBean.getVisit().getStaffId();
		param.put("UPDATE_STAFF_ID",  updateStaffId);
		param.put("UPDATE_TYPE","2");//删除
		
		String rescId = param.getString("RECV_ID","");
		String provinceId = param.getString("PROV_ID","");
		String newRescId = getIndictSequence(provinceId);
		
		IData paramIn = new DataMap();
		paramIn.put("PROTECT_NUM",param.getString("SERIAL_NUMBER",""));
		paramIn.put("PROV_ID",provinceId);
		paramIn.put("UPDATE_TYPE","2");//删除
		paramIn.put("RECV_ID",newRescId);
		paramIn.put("EXPIRE_DATE",param.getString("EXPIRE_DATE",""));
		
		IData delSubParam = new DataMap();
		delSubParam.put("UPDATE_STAFF_ID",updateStaffId);
		delSubParam.put("REMOVE_TAG","1");//删除
		delSubParam.put("RECV_ID",rescId);
		
		SmsBombProtectInfoQry.delProtectWhiteInfoById(delSubParam);
		SmsBombProtectInfoQry.delProtectInfos(param);
		
		syncSmsProtectList(paramIn);
	}
	
	/**
	 * 修改保护名单
	 * @param param
	 * @throws Exception
	 */
	public void updateProtectInfos(IData param) throws Exception
	{
//		IData updateData = new DataMap();
//		updateData.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER",""));
//		updateData.put("UPDATE_TYPE", param.getString("UPDATE_TYPE",""));
//		updateData.put("UPDATE_STAFF_ID",  CSBizBean.getVisit().getStaffId());
		
		String expireDate = param.getString("EXPIRE_DATE","");
//		if(StringUtils.isNotBlank(expireDate))
//		{
//			updateData.put("EXPIRE_DATE", expireDate);
//		}
		
		String oldRecvId = param.getString("RECV_ID","");
		
		IDataset addSubDatas = new DatasetList();
		IDataset updateSubDatas = new DatasetList();
		//处理新增白名单
		String protectDataSub  = param.getString("PROTECT_DATASUB","");
		StringBuilder whiteListStr = new StringBuilder("");
		if (!StringUtils.isBlank(protectDataSub) && protectDataSub.length() > 2)
		{
			IDataset dataSet = new DatasetList(protectDataSub);
			if(IDataUtil.isNotEmpty(dataSet))
			{
				for (int i = 0; i < dataSet.size(); i++) 
				{
					IData whiteData = dataSet.getData(i);
					if(IDataUtil.isNotEmpty(whiteData))
					{
						String whiteNum = whiteData.getString("WHITE_NUM","");
						String whiteType = whiteData.getString("WHITE_TYPE","");
						String tag = whiteData.getString("tag","");
						
						if("0".equals(tag))//新增
						{
							IData addData = new DataMap();
							addData.put("WHITE_NUM", whiteNum);
							addData.put("WHITE_TYPE", whiteType);
							addData.put("REMOVE_TAG", "0");
							addData.put("RECV_ID", oldRecvId);
							addData.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
							addSubDatas.add(addData);
							
							whiteListStr.append("1|");
							whiteListStr.append(whiteType);
							whiteListStr.append("|");
							whiteListStr.append(whiteNum);
							whiteListStr.append(",");
						}
						else if("1".equals(tag))//删除
						{
							IData delData = new DataMap();
							delData.put("WHITE_NUM", whiteNum);
							delData.put("REMOVE_TAG", "1");
							delData.put("RECV_ID", oldRecvId);
							delData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
							updateSubDatas.add(delData);
							
							whiteListStr.append("0|");
							whiteListStr.append(whiteType);
							whiteListStr.append("|");
							whiteListStr.append(whiteNum);
							whiteListStr.append(",");
						}
					}
				}
			}
		}
		String resultList = "";
		if(whiteListStr.length() > 0)
		{
			resultList = whiteListStr.substring(0, whiteListStr.length() - 1);
		}
		
		if(IDataUtil.isNotEmpty(addSubDatas))
		{
			SmsBombProtectInfoQry.addProtectWhiteInfos(addSubDatas);
		}
		if(IDataUtil.isNotEmpty(updateSubDatas))
		{
			SmsBombProtectInfoQry.delProtectWhiteInfos(updateSubDatas);
		}
		param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		param.put("UPDATE_TYPE", "3");
		
		if(StringUtils.isBlank(expireDate))
		{
			SmsBombProtectInfoQry.updateProtectInfoNo(param);
		}
		else 
		{
			SmsBombProtectInfoQry.updateProtectInfos(param);
		}
		
		String provinceId = param.getString("PROV_ID","");
		String newRescId = getIndictSequence(provinceId);
		
		IData paramIn = new DataMap();
		paramIn.put("PROTECT_NUM",param.getString("SERIAL_NUMBER",""));
		paramIn.put("PROV_ID",provinceId);
		paramIn.put("UPDATE_TYPE","3");//修改
		paramIn.put("RECV_ID",newRescId);
		paramIn.put("EXPIRE_DATE",expireDate);
		paramIn.put("WHITE_LIST", resultList);
		
		syncSmsProtectList(paramIn);
	}
	
	//同步IBOSS
	public void syncSmsProtectList(IData param) throws Exception
	{	
		logger.debug("----同步IBOSS入参为param:"+param.toString());
		
		IData result = IBossCall.syncProtectSmsBombInfo(param);
		
		logger.debug("IBOSS响应参数param:" + result.toString());
		
		if (!"0000".equals(result.getString("X_RSPCODE", "")))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,
					"落地方处理失败:" + result.getString("X_RESULTINFO"));
		}
	}
	
	
	/**
	 * 获取省代码
	 * 
	 * @param cycle
	 * @return
	 * @throws Exception
	 */
	public String getProvinceID() throws Exception
	{
	    String provinceID = "";
	
	    if ("XINJ".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "991";
	    }
	    if ("HAIN".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "898";
	    }
	    if ("HNAN".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "731";
	    }
	    if ("QHAI".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "971";
	    }
	    if ("SHXI".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "290";
	    }
	    if ("TJIN".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "220";
	    }
	    if ("YUNN".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "871";
	    }
	    return provinceID;
	}
	
	/**
	 * //服务请求标识 数据构造： YYYYMMDD＋CSVC＋3位省代码＋7位流水号
	 * @return
	 * @throws Exception
	 */
	public String getIndictSequence(String provinceID) throws Exception
	{	
	    String tempSeq = SeqMgr.getCustContact();
	    String finalSeq = "";
	    String indictSeq = "";
	    if (tempSeq.length() == 7)
	    {
	        finalSeq = tempSeq;
	    }
	    else
	    {
	        finalSeq = tempSeq.substring(tempSeq.length() - 7, tempSeq.length());
	    }
	    indictSeq = SysDateMgr.getNowCycle() + "CSVC" + provinceID + finalSeq;
	    return indictSeq;
	}
	
	public void addProtectInfosnew(IData param) throws Exception
	{
		String serialNumber = param.getString("PROTECT_NUM","");
		String rescId = param.getString("RECV_ID","");
		//归属省ID
		IDataset results = SmsBombProtectInfoQry.qryEpareyCodeOut(serialNumber);
		if(IDataUtil.isEmpty(results))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码不是移动号码");
		}

		String prodId = results.getData(0).getString("PROV_CODE");
		param.put("PROV_ID", prodId);

		//获得唯一编码
		String   provinceId = getProvinceID();

		IDataset addSubDatas = new DatasetList();

		//处理新增白名单
		String protectDataSub  = param.getString("PROTECT_DATASUB","");
		StringBuilder whiteListStr = new StringBuilder("");
		if (!StringUtils.isBlank(protectDataSub) && protectDataSub.length() > 2)
		{
			IDataset dataSet = new DatasetList(protectDataSub);
			if(IDataUtil.isNotEmpty(dataSet))
			{
				for (int i = 0; i < dataSet.size(); i++)
				{
					IData whiteData = dataSet.getData(i);
					if(IDataUtil.isNotEmpty(whiteData))
					{
						String whiteNum = whiteData.getString("WHITE_NUM","");
						String whiteType = whiteData.getString("WHITE_TYPE","");
						whiteData.put("RECV_ID", rescId);
						whiteData.put("REMOVE_TAG", "0");
						whiteData.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
						addSubDatas.add(whiteData);
						whiteListStr.append("1|");
						whiteListStr.append(whiteType);
						whiteListStr.append("|");
						whiteListStr.append(whiteNum);
						whiteListStr.append(",");
					}
				}
			}
		}
		String resultList = "";
		if(whiteListStr.length() > 0)
		{
			resultList = whiteListStr.substring(0, whiteListStr.length() - 1);
		}
		param.put("WHITE_LIST", resultList);
		param.put("UPDATE_TYPE", "3");
	    
		String  indictSeq = getIndictSequence(provinceId);
	    param.put("RECV_ID", indictSeq);
		syncSmsProtectList(param);
		
		SmsBombProtectInfoQry.addProtectWhiteInfos(addSubDatas);
	}

	public void delProtectInfosnew(IData param) throws Exception
	{
		String serialNumber = param.getString("PROTECT_NUM","");
		String rescId = param.getString("RECV_ID","");
		String updateStaffId = CSBizBean.getVisit().getStaffId();
		
		param.put("UPDATE_STAFF_ID",  updateStaffId);
		
		IDataset delSubDatas = new DatasetList();
		//归属省ID
		IDataset results = SmsBombProtectInfoQry.qryEpareyCodeOut(serialNumber);
		if(IDataUtil.isEmpty(results))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码不是移动号码");
		}

		String prodId = results.getData(0).getString("PROV_CODE");
		param.put("PROV_ID", prodId);

		//获得唯一编码
		String   provinceId = getProvinceID();

		IDataset addSubDatas = new DatasetList();

		//处理新增白名单
		String protectDataSub  = param.getString("PROTECT_DATASUBNEW","");
		StringBuilder whiteListStr = new StringBuilder("");
		if (!StringUtils.isBlank(protectDataSub) && protectDataSub.length() > 2)
		{
			IDataset dataSet = new DatasetList(protectDataSub);
			if(IDataUtil.isNotEmpty(dataSet))
			{
				for (int i = 0; i < dataSet.size(); i++)
				{
					IData delSubParam = new DataMap();
					IData whiteData = dataSet.getData(i);
					if(IDataUtil.isNotEmpty(whiteData))
					{
						String whiteNum = whiteData.getString("WHITE_NUM","");
						String whiteType = whiteData.getString("WHITE_TYPE","");
						whiteData.put("RECV_ID", rescId);
						whiteData.put("REMOVE_TAG", "0");
						whiteData.put("CREATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
						addSubDatas.add(whiteData);
						whiteListStr.append("0|");
						whiteListStr.append(whiteType);
						whiteListStr.append("|");
						whiteListStr.append(whiteNum);
						whiteListStr.append(",");
						
						delSubParam.put("UPDATE_STAFF_ID",updateStaffId);
						delSubParam.put("REMOVE_TAG","1");//删除
						delSubParam.put("RECV_ID",rescId);
						delSubParam.put("WHITE_NUM",whiteNum);
						delSubDatas.add(delSubParam);
					}
				}
			}
		}
		String resultList = "";
		if(whiteListStr.length() > 0)
		{
			resultList = whiteListStr.substring(0, whiteListStr.length() - 1);
		}
		param.put("WHITE_LIST", resultList);
		param.put("UPDATE_TYPE", "3");
		String  indictSeq = getIndictSequence(provinceId);
	    param.put("RECV_ID", indictSeq);

		syncSmsProtectList(param);
		
		if(IDataUtil.isNotEmpty(delSubDatas)){
			for(int i=0;i<delSubDatas.size();i++)
			{
				SmsBombProtectInfoQry.delProtectWhiteInfoByIdnew(delSubDatas.getData(i));
			}
			
		}
		

	}

}
