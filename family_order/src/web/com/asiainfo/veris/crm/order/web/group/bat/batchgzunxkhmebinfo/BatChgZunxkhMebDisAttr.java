
package com.asiainfo.veris.crm.order.web.group.bat.batchgzunxkhmebinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public abstract class BatChgZunxkhMebDisAttr extends CSBasePage
{
	public abstract void setDiscntList(IDataset discntList);
	 
	public abstract void setCondition(IData condition);
	  
	public void init(IRequestCycle cycle) throws Exception
    {
		IDataset discntList = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(this, 
				"CSM", "4006", "D", getTradeEparchyCode());
		if (IDataUtil.isNotEmpty(discntList))
        {
            for (int i = 0, row = discntList.size(); i < row; i++)
            {
                IData discntData = discntList.getData(i);
                discntData.put("DISCNT_DESC", discntData.getString("PARAM_NAME") + "|" + discntData.getString("PARA_CODE1"));
            }
        }
		setDiscntList(discntList);
		
		setCondition(getData());
    }
   
}
