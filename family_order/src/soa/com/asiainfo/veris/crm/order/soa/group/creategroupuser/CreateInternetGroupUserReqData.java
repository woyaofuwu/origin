
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUserReqData;

public class CreateInternetGroupUserReqData extends CreateGroupUserReqData
{
    private IData interData;

    private IData zjData;

    private IDataset attrData;

    private IDataset commonData;

    private IData dataline;
    
    private int pfWait;

    public int getPfWait()
    {
        return pfWait;
    }

    public void setPfWait(int pfWait)
    {
        this.pfWait = pfWait;
    }

    public IDataset getCommonData()
    {
        return commonData;
    }

    public void setCommonData(IDataset commonData)
    {
        this.commonData = commonData;
    }

    public IData getDataline()
    {
        return dataline;
    }

    public void setDataline(IData dataline)
    {
        this.dataline = dataline;
    }

    public IDataset getAttrData()
    {
        return attrData;
    }

    public void setAttrData(IDataset attrData)
    {
        this.attrData = attrData;
    }

    public IData getInterData()
    {
        return interData;
    }

    public void setInterData(IData interData)
    {
        this.interData = interData;
    }

    public IData getZjData()
    {
        return zjData;
    }

    public void setZjData(IData zjData)
    {
        this.zjData = zjData;
    }

}
