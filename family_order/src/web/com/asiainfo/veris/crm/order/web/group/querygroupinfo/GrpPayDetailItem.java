package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpPayDetailItem extends GroupBasePage{

    public void querypaydetailitem(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	String payitemcode = data.getString("cond_PAYITEM_CODE");
    	IData param = new DataMap();
    	param.put("ITEM_ID", payitemcode);
    	//调账务接口获取数据
    	IDataset dataset = CSViewCall.call(this, "SS.GroupInfoQuerySVC.qryItemIdDetails", param);
    	if(IDataUtil.isEmpty(dataset))
    		setHintInfo("调账务接口获取数据失败");
    	else
    		setHintInfo("调账务接口获取数据成功");
    	setInfos(dataset);
    }
    
    public abstract void setInfos(IDataset datset);
    public abstract void setHintInfo(String str);
}
