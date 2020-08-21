package com.asiainfo.veris.crm.order.soa.group.esop.esopctr;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.*;

public class WorkformMultiSVC extends GroupOrderService 
{
private static final long serialVersionUID = -3923226370322319051L;
	
	public IDataset queryMultiInfo(IData inparam) throws Exception
	{
		String subIbsysid = inparam.getString("SUB_IBSYSID", "");
		IDataset resultInfos = new DatasetList();
		//查询subIbsysid下recordNum
		IDataset attrInfos = WorkformAttrBean.qryRecordNumBySubIbsysid(subIbsysid);
		if(IDataUtil.isNotEmpty(attrInfos))
		{
			for(int i = 0 ; i < attrInfos.size() ; i ++)
			{
				IData attrInfo = attrInfos.getData(i);
				IData temp = new DataMap();
				temp.put("RELE_VALUE", attrInfo.getString("RECORD_NUM", ""));
				temp.put("RELE_CODE", "RECORD_NUM");

				resultInfos.add(temp);
			}
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取拆分数据失败，请确认表[tf_b_eop_attr]是多条数据结构。");
		}
		
		return resultInfos;
	}
	
	
	public IDataset queryAttrInfo(IData inparam) throws Exception
	{
		String nodeId = inparam.getString("NODE_ID", "");
		String bpmTempletId = inparam.getString("BPM_TEMPLET_ID", "");
		String busiformId = inparam.getString("BUSIFORM_ID","");
		
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
		
		IDataset resultInfos = new DatasetList();
		//查询subIbsysid下recordNum
		IDataset attrInfos = WorkformAttrBean.qryRecordNumBySubIbsysid(subIbsysid);
		if(IDataUtil.isNotEmpty(attrInfos))
		{
			for(int i = 0 ; i < attrInfos.size() ; i ++)
			{
				IData attrInfo = attrInfos.getData(i);
				IData temp = new DataMap();
				temp.put("RELE_VALUE", attrInfo.getString("RECORD_NUM", ""));
				temp.put("RELE_CODE", "RECORD_NUM");

				resultInfos.add(temp);
			}
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取拆分数据失败，请确认表[tf_b_eop_attr]是多条数据结构。");
		}
		
		return resultInfos;
	}
	
	public IDataset queryProductInfo(IData inparam) throws Exception
	{
		String ibsysId = inparam.getString("IBSYSID","");
		
		IDataset proDataset = WorkformProductBean.qryProductByIbsysid(ibsysId);
		
		if (IDataUtil.isEmpty(proDataset)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"订单"+ibsysId+"对应TF_B_EOP_PROUDCT数据不存在！");
		}
		
		IDataset resultInfos = new DatasetList();
		proDataset.removeAll(DataHelper.filter(proDataset, "RECORD_NUM=0")); // 出掉0.集团产品
		if(IDataUtil.isNotEmpty(proDataset))
		{
			for(int i = 0 ; i < proDataset.size() ; i ++)
			{
				IData proData = proDataset.getData(i);
				IData temp = new DataMap();
				temp.put("RELE_VALUE", proData.getString("RECORD_NUM", ""));
				temp.put("RELE_CODE", "RECORD_NUM");

				resultInfos.add(temp);
			}
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取拆分数据失败，请确认表[TF_B_EOP_PROUDCT]是多条数据结构。");
		}
		
		return resultInfos;
	}
	
	public IDataset queryProductExtInfo(IData inparam) throws Exception
    {
	    String ibsysId = inparam.getString("IBSYSID","");
	    String busiFormId = inparam.getString("BUSIFORM_ID","");
	    IDataset relaList = EweNodeQry.qryBySubBusiformId(busiFormId);
	    if(IDataUtil.isEmpty(relaList)){
	        CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到一级子流程关系数据");
	    }
	    String grpRecordNum = relaList.first().getString("RELE_VALUE");
	    IDataset proDataset = WorkformProductExtBean.qryProductByParentRecordNum(ibsysId, grpRecordNum);
	    IDataset resultInfos = new DatasetList();
	    if(IDataUtil.isNotEmpty(proDataset))
        {
            for(int i = 0 ; i < proDataset.size() ; i ++)
            {
                IData proData = proDataset.getData(i);
                IData temp = new DataMap();
                temp.put("RELE_VALUE", proData.getString("RECORD_NUM", ""));
                temp.put("RELE_CODE", "RECORD_NUM");
                resultInfos.add(temp);
            }
        }
	    return resultInfos;
    }
	public IDataset queryProductExtInfoForSub(IData inparam) throws Exception{
	    String ibsysId = inparam.getString("IBSYSID","");
        IDataset proDataset = WorkformProductExtBean.qryProductByIbsysid(ibsysId);
        IDataset resultInfos = new DatasetList();
        if(IDataUtil.isNotEmpty(proDataset))
        {
            for(int i = 0 ; i < proDataset.size() ; i ++)
            {
                IData proData = proDataset.getData(i);
                IData temp = new DataMap();
                temp.put("RELE_VALUE", proData.getString("RECORD_NUM", ""));
                temp.put("RELE_CODE", "RECORD_NUM");
                resultInfos.add(temp);
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取拆分数据失败，请确认表[TF_B_EOP_PROUDCT_EXT]是多条数据结构。");
        }
        return resultInfos;
	}
    public IDataset queryProductInfoByEsp(IData inparam) throws Exception
    {
        String ibsysId = inparam.getString("IBSYSID","");
        IDataset proDataset = WorkformProductBean.qryProductByIbsysidAndRsrvstr5(ibsysId,"ESP");
        IDataset resultInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(proDataset))
        {
            proDataset.removeAll(DataHelper.filter(proDataset, "RECORD_NUM=0")); // 出掉0.集团产品
            if(IDataUtil.isNotEmpty(proDataset))
            {
                for(int i = 0 ; i < proDataset.size() ; i ++)
                {
                    IData proData = proDataset.getData(i);
                    IData temp = new DataMap();
                    temp.put("RELE_VALUE", proData.getString("RECORD_NUM", ""));
                    temp.put("RELE_CODE", "RECORD_NUM");
                    resultInfos.add(temp);
                }
            }
        }
        return resultInfos;
    }
	public IDataset queryProductInfoByNoEsp(IData inparam) throws Exception
	{
        String ibsysId = inparam.getString("IBSYSID","");
        IDataset proDataset = WorkformProductBean.qryProductByIbsysidAndRsrvstr5(ibsysId,"");
        IDataset resultInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(proDataset))
        {
            proDataset.removeAll(DataHelper.filter(proDataset, "RECORD_NUM=0")); // 出掉0.集团产品
            if(IDataUtil.isNotEmpty(proDataset))
            {
                for(int i = 0 ; i < proDataset.size() ; i ++)
                {
                    IData proData = proDataset.getData(i);
                    IData temp = new DataMap();
                    temp.put("RELE_VALUE", proData.getString("RECORD_NUM", ""));
                    temp.put("RELE_CODE", "RECORD_NUM");
                    resultInfos.add(temp);
                }
            }
        }
        return resultInfos;
	}
    public IDataset queryProductExtInfoByEsp(IData inparam) throws Exception
    {
        String ibsysId = inparam.getString("IBSYSID","");
        IDataset productExtList = WorkformProductExtBean.qryProductExtByIbsysidAndRsrvstr5(ibsysId,"ESP");
        IDataset resultInfos = new DatasetList();
        if(IDataUtil.isNotEmpty(productExtList))
        {
            for(int i = 0 ; i < productExtList.size() ; i ++)
            {
                IData proData = productExtList.getData(i);
                IData temp = new DataMap();
                temp.put("RELE_VALUE", proData.getString("RECORD_NUM", ""));
                temp.put("RELE_CODE", "RECORD_NUM");
                resultInfos.add(temp);
                break;
            }
        }
        return resultInfos;
    }
    public IDataset queryProductExtInfoByNoEsp(IData inparam) throws Exception
    {
        String ibsysId = inparam.getString("IBSYSID","");
        IDataset productExtList = WorkformProductExtBean.qryProductExtByIbsysidAndRsrvstr5(ibsysId,"");
        IDataset resultInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(productExtList))
        {
            for(int i = 0 ; i < productExtList.size() ; i ++)
            {
                IData proData = productExtList.getData(i);
                IData temp = new DataMap();
                temp.put("RELE_VALUE", proData.getString("RECORD_NUM", ""));
                temp.put("RELE_CODE", "RECORD_NUM");
                resultInfos.add(temp);
            }
        }
        return resultInfos;
    }
}
