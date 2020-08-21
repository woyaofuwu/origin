
package com.asiainfo.veris.crm.order.web.group.bat.batchgdesktoptelmebinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public abstract class BatChgDeskTopTelMebInfo extends CSBasePage
{
	public abstract void setDiscntList(IDataset discntList);
	 
	public abstract void setCondition(IData condition);
	  
	public abstract void setSvcList(IDataset svcList);
	
	public void init(IRequestCycle cycle) throws Exception
    {
		//String batchOperType = getData().getString("BATCH_OPER_TYPE", "NOXXXX");
		
		IDataset discntList = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(this, 
				"CSM", "4004", "D", getTradeEparchyCode());
		if (IDataUtil.isNotEmpty(discntList))
        {
            for (int i = 0, row = discntList.size(); i < row; i++)
            {
                IData discntData = discntList.getData(i);
                discntData.put("DISCNT_DESC", discntData.getString("PARAM_NAME") + "|" + discntData.getString("PARA_CODE1"));
            }
        }
		setDiscntList(discntList);
		
		IDataset svcList = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(this, 
				"CSM", "4004", "S", getTradeEparchyCode());
		if (IDataUtil.isNotEmpty(svcList))
        {
            for (int i = 0, row = svcList.size(); i < row; i++)
            {
                IData svcData = svcList.getData(i);
                svcData.put("DISCNT_DESC", svcData.getString("PARAM_NAME") + "|" + svcData.getString("PARA_CODE1"));
            }
        }
		setSvcList(svcList);
		setCondition(getData());
    }
   
}
