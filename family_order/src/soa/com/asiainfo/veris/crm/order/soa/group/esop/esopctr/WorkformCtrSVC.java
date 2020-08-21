package com.asiainfo.veris.crm.order.soa.group.esop.esopctr;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;

public class WorkformCtrSVC extends GroupOrderService
{
	private static final long serialVersionUID = -3923226370322319051L;
	
	public IDataset queryCtrInfo(IData inparam) throws Exception
	{
		IDataset dataset = new DatasetList();
		String recordNum = inparam.getString("RECORD_NUM", "0");
		String subIbsysid = inparam.getString("SUB_IBSYSID", "");
		String ibsysid = inparam.getString("IBSYSID", "");
		
		IDataset workOthers = new DatasetList();
		
		if ("-1".equals(subIbsysid)) {
			String busiformId = inparam.getString("BUSIFORM_ID","");
			IDataset eweInfos = EweNodeQry.qryEweByBusiformId(busiformId);
			if (IDataUtil.isEmpty(eweInfos)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据BUSIFORM_ID="+busiformId+"查询TF_B_EWE表数据失败！");
			}
			String templetType = eweInfos.first().getString("TEMPLET_TYPE");
			if ("1".equals(templetType)) {
				IDataset eweRelInfos = EweNodeQry.qryBySubBusiformId(busiformId);
				if(DataUtils.isEmpty(eweRelInfos))
				{
					return dataset;
				}
				recordNum = eweRelInfos.first().getString("RELE_VALUE","");
				workOthers = WorkformOtherBean.qryOtherInfoByIbsysidAndRecordNum(ibsysid, recordNum);
			}else {
				workOthers = WorkformOtherBean.qryOtherInfoByIbsysid(ibsysid);
			}
			
		}else{
			workOthers = WorkformOtherBean.qryOtherBySubIbsysidRecordNum(subIbsysid, recordNum);
		}
		
		if(DataUtils.isEmpty(workOthers))
		{
			return dataset;
		}
		IData data = new DataMap();
		for(int i = 0 ; i < workOthers.size() ; i ++)
		{
			IData workOther = workOthers.getData(i);
			data.put(workOther.getString("ATTR_CODE", ""), workOther.getString("ATTR_VALUE", ""));
		}
		dataset.add(data);
		
		return dataset;
	}
}
