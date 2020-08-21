package com.asiainfo.veris.crm.order.soa.group.esop.eopqry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsStateBean;

public class EopAttrInfoSVC extends CSBizService 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 查询SI催单[阶段通知]
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset qryInfosForSI(IData param) throws Exception
    {
		IDataset siInfos = EopAttrInfoBean.qrySIInfosByCond(param, this.getPagination());
		
		if(DataUtils.isNotEmpty(siInfos))
		{
			for(int i = 0 ; i < siInfos.size() ; i ++)
			{
				IData siInfo = siInfos.getData(i);
				String groupId = siInfo.getString("GROUP_ID", "");
				
				IData custData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

	            if (IDataUtil.isEmpty(custData))
	            {
	                break;
	            }
	            siInfo.put("CUST_CLASS_ID", custData.getString("CLASS_ID", ""));
	            siInfo.put("CUST_LINK_NAME", custData.getString("GROUP_MGR_CUST_NAME", ""));
	            siInfo.put("CUST_LINK_PHONE", custData.getString("GROUP_MGR_SN", ""));
	            siInfo.put("CUST_ADDR", custData.getString("GROUP_ADDR", ""));
			}
		}
		
		return siInfos;
    }
	
	/**
	 * 查询SI催单[阶段通知]
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset qryInfosForEoms(IData param) throws Exception
    {
		IDataset eomsInfos = EopAttrInfoBean.qryEomsInfosByCond(param, this.getPagination());
		
		if(DataUtils.isNotEmpty(eomsInfos))
		{
			for(int i = 0 ; i < eomsInfos.size() ; i ++)
			{
				IData eomsInfo = eomsInfos.getData(i);
				String groupId = eomsInfo.getString("GROUP_ID", "");
				
				IData custData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

	            if (IDataUtil.isEmpty(custData))
	            {
	                break;
	            }
	            eomsInfo.put("CUST_CLASS_ID", custData.getString("CLASS_ID", ""));
	            eomsInfo.put("CUST_LINK_NAME", custData.getString("GROUP_MGR_CUST_NAME", ""));
	            eomsInfo.put("CUST_LINK_PHONE", custData.getString("GROUP_MGR_SN", ""));
	            eomsInfo.put("CUST_ADDR", custData.getString("GROUP_ADDR", ""));
			}
		}
		
		return eomsInfos;
    }
	
	public IDataset qryByOperType(IData param) throws Exception
	{
		IDataset resultInfos = new DatasetList();
		IDataset siInfos = EopAttrInfoBean.qryInfoByOperType(param);
		String operType = param.getString("OPER_TYPE", "");
		IDataset configInfos = EweConfigQry.qryDistinctValueDescByParamName("EOMS_BUSI_STATE", operType, "0");
		String operName = "";
		if(DataUtils.isNotEmpty(configInfos))
		{
			operName = configInfos.first().getString("VALUEDESC", "");
		}
		if(DataUtils.isNotEmpty(siInfos))
		{
			for(int i = 0 ; i < siInfos.size() ; i ++)
			{
				IData siInfo = siInfos.getData(i);
				siInfo.put("OPTYPE", operName);
				resultInfos.add(siInfo);
			}
		}
		return resultInfos;
	}
	
	public IDataset saveInfo(IData param) throws Exception
	{
		IDataset resultInfos = new DatasetList();
		IDataset eomsInfos = new DatasetList();
		IData tempInfo = new DataMap();
		IData eomsInfo = new DataMap();
		eomsInfo.put("IBSYSID", param.getString("IBSYSID", ""));
		eomsInfo.put("SUB_IBSYSID", param.getString("SUB_IBSYSID", ""));
		eomsInfo.put("RECORD_NUM", param.getString("RECORD_NUM", ""));
		eomsInfo.put("BUSIFORM_ID", param.getString("BUSIFORM_ID", ""));
		eomsInfo.put("TRADE_DRIECT", "0");
		eomsInfo.put("OPER_TYPE", param.getString("OPER_TYPE", ""));
		eomsInfo.put("OPPERSON", param.getString("OPPERSON", ""));
		eomsInfo.put("OPDERAT", param.getString("OPDERAT", ""));
		eomsInfo.put("OPCONTACT", param.getString("OPCONTACT", ""));
		eomsInfo.put("SERIALNO", param.getString("SERIALNO", ""));

		//查询专线实例号，通过state表查询
		IDataset stateInfos = WorkformEomsStateBean.qryEomsStateByIbsysidAndRecordNum(param.getString("IBSYSID", ""), param.getString("RECORD_NUM", ""));
		if(DataUtils.isNotEmpty(stateInfos))
		{
			eomsInfo.put("ProductNO", stateInfos.first().getString("PRODUCT_NO", ""));
		}
		
		eomsInfo.put("ATTR_INFOS", spellAttrInfos(param, "opType", "opDesc", "ProductNO"));
		eomsInfo.put("STATE_TAG", "false");
		eomsInfos.add(eomsInfo);
		tempInfo.put("EOMS_INFOS", eomsInfos);
		//保存esop数据
        CSAppCall.call("SS.WorkformEomsInteractiveSVC.record", tempInfo);
        
        //发送emos报文
       IDataset eomsDataset = WorkformEomsBean.qryworkformEOMSByIbsysidRecordNum(param.getString("IBSYSID", ""), param.getString("RECORD_NUM", ""));
       if(DataUtils.isNotEmpty(eomsDataset))
       {
    	   IData eomsData = eomsDataset.first();
    	   eomsData.put("OPER_TYPE", StringUtils.isNotEmpty(param.getString("OPER_TYPE", "")) ? param.getString("OPER_TYPE", ""):eomsData.getString("OPER_TYPE", ""));
    	   eomsData.put("OPPERSON", StringUtils.isNotEmpty(param.getString("OPPERSON", "")) ? param.getString("OPPERSON", ""):eomsData.getString("OPPERSON", ""));
    	   eomsData.put("OPDERAT", StringUtils.isNotEmpty(param.getString("OPDERAT", "")) ? param.getString("OPDERAT", ""):eomsData.getString("OPDERAT", ""));
    	   eomsData.put("OPCONTACT", StringUtils.isNotEmpty(param.getString("OPCONTACT", "")) ? param.getString("OPCONTACT", ""):eomsData.getString("OPCONTACT", ""));
       }
       
       eomsInfo.put("ACCEPT_DEPART_ID", this.getVisit().getDepartId());
       eomsInfo.put("UPDATE_DEPART_ID",  this.getVisit().getDepartId());
       eomsInfo.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
       eomsInfo.put("ACCEPT_STAFF_ID", this.getVisit().getStaffId());
       eomsInfo.remove("ATTR_INFOS");
       
       eomsInfo.put("opType", param.getString("opType", ""));
       eomsInfo.put("opDesc", param.getString("opDesc", ""));
       eomsInfo.put("ProductNO", param.getString("ProductNO", ""));
       
       //发送报文
       //CSAppCall.call("SS.EweForCrmSVC.dealCRMData", eomsInfo);
	   return resultInfos;
	}
	
	private IDataset spellAttrInfos(IData param, String... keys) throws Exception
	{
		IDataset attrInfos = new DatasetList();
		
		for(int i = 0 ; i < keys.length ; i ++)
		{
			String key = keys[i];
			String attrValue = param.getString(key, "");
			IData attrInfo = new DataMap();
			attrInfo.put("ATTR_CODE", key);
			attrInfo.put("ATTR_VALUE", attrValue);
			attrInfos.add(attrInfo);
		}
		
		return attrInfos;
	}
}
