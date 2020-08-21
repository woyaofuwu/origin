package com.asiainfo.veris.crm.order.soa.group.esop.contractLines;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrHBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeHBean;

public class ContractLinesSVC extends GroupOrderService{
	private static final long serialVersionUID = 1L;
	
	public static IDataset getProductLineInfo(IData data) throws Exception{
		
		IDataset dataset = new DatasetList();
		String productID = data.getString("PRODUCT_ID","");
		String productNO = data.getString("PRODUCTNO","");
		String groupID = data.getString("GROUP_ID","");
		IData param = new DataMap();
		param.put("GROUP_ID", groupID);
		//param.put("PRODUCT_ID", productID);
		param.put("PRODUCTNO", productNO);
		IDataset infos = WorkformSubscribeHBean.qrySubScribeInfoByProductNo(param);
		
		if (DataUtils.isEmpty(infos)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线实例:"+productNO+"属于其他的集团，不能分配给现在的集团!");
		}
		
		for (int i = 0; i < infos.size(); i++) {
			IData info = infos.getData(i);
			String type = info.getString("TAB_TYPE");
			String subIbsysid = info.getString("SUB_IBSYSID");
			
			IDataset atttrDataset  = new DatasetList();
			if ("B".equals(type)) {
				atttrDataset = WorkformAttrBean.qryEopAttrBySubIbsysid(subIbsysid);
			}else if ("BH".equals(type)) {
				atttrDataset = WorkformAttrHBean.qryEopAttrBySubIbsysid(subIbsysid);
			}
			IData attrinfo = new DataMap();
			if (DataUtils.isNotEmpty(atttrDataset)) {
				for (int j = 0; j < atttrDataset.size(); j++) {
					attrinfo.put(atttrDataset.getData(j).getString("ATTR_CODE"), atttrDataset.getData(j).getString("ATTR_VALUE"));
				}
			}
			dataset.add(attrinfo);
		}
		
	    return dataset;
	}
}