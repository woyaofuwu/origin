
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.pkgelementlist;

import com.ailk.common.data.IData;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class SelectedElementsHandler extends CSBizHttpHandler
{

    public void dealElement() throws Exception
    {
        IData data = this.getData();
        if ("".equals(data.getString("CALL_SVC", "")))
        {
            return;
        }
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        data.put("SVC_NAME", data.getString("CALL_SVC"));
        
        IDataset addElements = new DatasetList(data.getString("ELEMENTS"));
        
        int size = addElements.size();
        IData param =  new DataMap();
		param.put("PDATA_ID", data.getString("PRODUCT_MAIN_ID"));
		param.put("TYPE_ID", "BAT_DISCOUNT_TIMECHANGE");
		for (int i = 0; i < size; i++)
		{
			IData element = addElements.getData(i);
			param.put("DATA_ID", element.getString("ELEMENT_ID"));
			IDataset returnData= CSViewCall.call(this,"CS.StaticInfoQrySVC.getStaticValueByPDTypeMEM", param);
			if(DataUtils.isNotEmpty(returnData)){
				element.put("isNeedChange", 1);
			}else element.put("isNeedChange", 0);
		}
		data.put("ELEMENTS", addElements);
			
        
        IDataset result = CSViewCall.call(this, "CS.ProductComponentSVC.transmit", data);
     
        this.setAjax(result);
    }
}
