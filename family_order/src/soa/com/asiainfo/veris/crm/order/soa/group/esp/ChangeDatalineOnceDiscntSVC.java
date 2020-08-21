package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ChangeDatalineOnceDiscntSVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;

	public IDataset changeDatalineOnceDiscnt(IData map) throws Exception
    {
		String userId = map.getString("USER_ID");
		String productId = map.getString("PRODUCT_ID");
		IDataset elementInfo =  new DatasetList();
		IData element = new DataMap();
		element.put("ELEMENT_ID", DataLineDiscntConst.ONCEElementId);
		element.put("MODIFY_TAG", "0");
		element.put("ELEMENT_TYPE_CODE", "D");
		element.put("PRODUCT_ID", productId);
		element.put("PACKAGE_ID", "0");
		element.put("INST_ID", "");
		element.put("START_DATE",SysDateMgr.getFirstDayOfThisMonth());
		element.put("END_DATE", SysDateMgr.getLastDateThisMonth());
		IDataset attrParam =  new DatasetList();
		IData param =  new DataMap();
		param.put("ATTR_VALUE", map.getString("ATTR_VALUE"));
		param.put("ATTR_CODE", "59701004");
		attrParam.add(param);
		element.put("ATTR_PARAM", attrParam);
		elementInfo.add(element);
		IData inparam =  new DataMap();
		inparam.put("ELEMENT_INFO",elementInfo);//新增资费
		inparam.put("USER_ID", userId);
		inparam.put("PRODUCT_ID", productId);
		inparam.put("USER_EPARCHY_CODE", "0898");
		IDataset result = new DatasetList();
		result = CSAppCall.call("SS.ChangeMemberforOnceElementSVC.crtOrder",inparam);
		return result;
		
    }
}
