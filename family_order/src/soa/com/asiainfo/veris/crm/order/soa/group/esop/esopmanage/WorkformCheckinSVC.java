package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsStateBean;

public class WorkformCheckinSVC extends GroupOrderService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1325269687705020179L;

	public void record(IData inparam) throws Exception {
		
		String ibsysid = inparam.getString("IBSYSID","");
		String recordNum = inparam.getString("RECORD_NUM","");
		
		IDataset emosDataset = WorkformEomsStateBean.qryEomsStateByIbsysidAndRecordNum(ibsysid, recordNum);
		if (IDataUtil.isEmpty(emosDataset)) {
			return;
		}
		String serialNo = emosDataset.first().getString("SERIALNO");
		
		// 1- 数据转换
		inparam.put("closeSatisfyDegree", "满意");//写死，没有页面提供
		inparam.put("closeDesc", "满意");//写死，没有页面提供
		inparam.put("isRelease", "是");//写死
		inparam.put("serialNo", serialNo);
		
        
        //2- 存储esop数据
        IDataset attrInfos = dealAttrInfo(inparam, "closeSatisfyDegree","isRelease","closeDesc","serialNo");
        
        IData param = new DataMap();
		IDataset eomsInfos = new DatasetList();
		IData eomsInfo = new DataMap();
		eomsInfo.put("IBSYSID", ibsysid);
		eomsInfo.put("RECORD_NUM", recordNum);
		eomsInfo.put("TRADE_DRIECT", "0");
		eomsInfo.put("OPER_TYPE", "checkinWorkSheet");
		eomsInfo.put("ATTACHREF", "");
		eomsInfo.put("ATTR_INFOS", attrInfos);
		eomsInfo.put("SERIALNO", serialNo);
		
		eomsInfos.add(eomsInfo);
		
		param.put("EOMS_INFOS", eomsInfos);
        
        // 3- 登记EOP资料
        CSAppCall.call("SS.WorkformEomsInteractiveSVC.record", param);
	}
	
	public IDataset dealAttrInfo(IData param, String...keys) throws Exception
	{
		IDataset attrInfos = new DatasetList();
		
		for(int i = 0 ; i < keys.length ; i ++)
		{
			String key = keys[i];
			String attrValue = param.getString(key, "");
			String attrName = param.getString(key + "_CH", "");
			IData attrInfo = new DataMap();
			attrInfo.put("ATTR_NAME", attrName);
			attrInfo.put("ATTR_VALUE", attrValue);
			attrInfo.put("ATTR_CODE", key);
            attrInfos.add(attrInfo);
        }
        
        return attrInfos;
	}
	
}