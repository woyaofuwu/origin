
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserResourceInfo extends PersonBasePage
{
    /**
     * 用户资源信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryResourceInfo", data, getPagination("Resnav"));
        IDataset results = output.getData() ;
        for(int i=0;i<results.size();i++)
        {
        	IData result = results.getData(i);
        	String restypecode = result.getString("RES_TYPE_CODE","");
        	String emptycardid = result.getString("RSRV_STR5","");
        	if(restypecode.equals("1") && !"01".equals(emptycardid))
        	{
        		result.put("EmptyCardId", emptycardid);
        	}
        	
        }
        setCond(data);
        setInfos(results);
        setInfosCount(output.getDataCount());
    }

    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        setCond(data);
    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
