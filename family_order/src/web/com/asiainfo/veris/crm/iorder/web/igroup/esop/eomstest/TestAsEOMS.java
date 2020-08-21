package com.asiainfo.veris.crm.iorder.web.igroup.esop.eomstest;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class TestAsEOMS  extends CSBasePage 
{
	public abstract void setInfo(IData info) throws Exception;
	
	public void initPage(IRequestCycle cycle) throws Exception
	{
		IData info = new DataMap();
		//设置下拉框列表，测试用，直接写死
		IDataset nodeInfos = new DatasetList();
		IData type = new DataMap();
		type.put("TYPE_NAME", "驳回");
		type.put("TYPE_ID", "0");
		nodeInfos.add(type);
		IData type1 = new DataMap();
		type1.put("TYPE_NAME", "回复");
		type1.put("TYPE_ID", "1");
		nodeInfos.add(type1);
		
		info.put("TYPE_INFO", nodeInfos);
		this.setInfo(info);
	}
	
	public void submit(IRequestCycle cycle) throws Exception
	{
		IData data = this.getData();
		String nodeType = data.getString("NODE_TYPE", "");
		//根据serialno查询ibsysid
		IData param = new DataMap();
		param.put("SERIALNO", data.getString("SERIALNO", ""));
		IDataset eomsInfos = CSViewCall.call(this,"SS.WorkformEomsVC.qryworkformEOMSBySerialNo", param);
		if(DataUtils.isEmpty(eomsInfos))
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据订单号:["+data.getString("SERIALNO", "")+"]没有查询eoms信息！");
		}
		String sheetType = eomsInfos.first().getString("SHEETTYPE", "");
		//根据ibsysid和专线实例号捞recordnum 
		param.clear();
		param.put("IBSYSID", eomsInfos.first().getString("IBSYSID",""));
		param.put("PRODUCT_NO", data.getString("PRODUCT_NO", ""));
		IDataset emosStateInfos = CSViewCall.call(this,"SS.WorkformEomsStateSVC.qryEomsStateByIbsysidAndProductNo", param);
		if(DataUtils.isEmpty(emosStateInfos))
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据专线实例号:["+data.getString("PRODUCT_NO", "")+"]没有查询专线信息！");
		}
		
		//按ibsysid,recordnum,sheettype捞数据
		param.clear();
		param.put("IBSYSID", eomsInfos.first().getString("IBSYSID",""));
		param.put("RECORD_NUM", emosStateInfos.first().getString("RECORD_NUM",""));
		param.put("OPER_TYPE", "replyWorkSheet");
		IDataset eomsReplyInfos = CSViewCall.call(this,"SS.WorkformEomsSVC.qryEOMSByIbsysidRecordNumOperType", param);
		if(DataUtils.isNotEmpty(eomsReplyInfos))
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据专线实例号:["+data.getString("PRODUCT_NO", "")+"],查询专线已经完工！");
		}
    	
		param.clear();
		boolean flag = false;
		if("31".equals(sheetType) || "35".equals(sheetType))
		{
			flag = true;
		}
		
		if("0".equals(nodeType))
		{
			//如果是0 ,并且是勘察，全量更新state表   如果是0, 不是勘察，按recordnum更新state表
			if(!flag)
			{
				param.put("RECORD_NUM", emosStateInfos.first().getString("RECORD_NUM",""));
	
			}
			
			param.put("IBSYSID", eomsInfos.first().getString("IBSYSID",""));
			param.put("OPER_TYPE", "withdrawWorkSheet");
			CSViewCall.call(this,"SS.WorkformEomsSVC.updateEomsState", param);
		}
		else if("1".equals(nodeType))
		{
			//如果是1，驱动节点
			
			param.put("IBSYSID", eomsInfos.first().getString("IBSYSID",""));
			param.put("FLAG", flag);
			param.put("RECORD_NUM", emosStateInfos.first().getString("RECORD_NUM",""));
			CSViewCall.call(this,"SS.WorkformDriveForTestSVC.dirveTest", param);
		}
	}
}
