
package com.asiainfo.veris.crm.order.web.group.demo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class TestDemo extends GroupBasePage
{
    
    public void init(IRequestCycle cycle) throws Exception
    {
    	IData iData = new DataMap();
    	iData.put("OFFER_ID","110010005743");
    	iData.put("USER_EPARCHY_CODE","0898");

    	setCondition(iData);
    	
    	IDataset svcList = new DatasetList();
    	IData svc = new DataMap();
    	svc.put("MODIFY_TAG", "0");
    	svc.put("ELEMENT_NAME", "一卡通增值服务");
    	svc.put("ELEMENT_ID", "120000100031");
    	svc.put("ITEM_INDEX", "0");
    	svc.put("ATTR_PARAM", "123123");
    	
    	svcList.add(svc);
    	
    	setSvcList(svcList);
    }
    
    public abstract void setCondition(IData condition);

    public abstract void setSvcList(IDataset svcList);
}
