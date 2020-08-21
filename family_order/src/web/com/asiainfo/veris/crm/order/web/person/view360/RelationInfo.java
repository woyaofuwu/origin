
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RelationInfo extends PersonBasePage
{

    /**
     * 用户关系信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.GetUser360ViewSVC.qryRelationInfo", data, getPagination("RelationNav"));
        
        IDataset out = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryIsCmccStaff", data);
        setIsCmccStaff(out.getData(0).getString("IS_STAFF_USER","0"));
        setCond(data);
        setInfos(output.getData());
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
    
    public abstract void setIsCmccStaff(String isCmccStaff);
}
