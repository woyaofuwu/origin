
package com.asiainfo.veris.crm.order.soa.group.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class SaleActiveBeanReqData extends GroupReqData
{
    private IDataset selectElements;

    private String saleProductId;

    private String packageId;

    private String needAcct;

    private String campnType;

    private String startDate;

    private String endDate;

    private String onNetStartDate;

    private String onNetEndDate;

    private IDataset saleDiscnts;

    private IData saleActive;

    private IDataset saleActiveFiles;
    
    public IDataset getSelectElements()
    {
        return selectElements;
    }

    public String getSaleProductId()
    {
        return saleProductId;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public String getNeedAcct()
    {
        return needAcct;
    }

    public String getCampnType()
    {
        return campnType;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getOnNetStartDate()
    {
        return onNetStartDate;
    }

    public String getOnNetEndDate()
    {
        return onNetEndDate;
    }

    public IDataset getSaleActiveFiles()
    {
        return saleActiveFiles;
    }
    
    public void setSelectElements(IDataset selectElements)
    {
        this.selectElements = selectElements;
    }

    public void setSaleProductId(String saleProductId)
    {
        this.saleProductId = saleProductId;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setNeedAcct(String needAcct)
    {
        this.needAcct = needAcct;
    }

    public void setCampnType(String campnType)
    {
        this.campnType = campnType;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setOnNetStartDate(String onNetStartDate)
    {
        this.onNetStartDate = onNetStartDate;
    }

    public void setOnNetEndDate(String onNetEndDate)
    {
        this.onNetEndDate = onNetEndDate;
    }

    public IDataset getSaleDiscnts()
    {
        return saleDiscnts;
    }

    public void setSaleDiscnts(IDataset saleDiscnts)
    {
        this.saleDiscnts = saleDiscnts;
    }

    public IData getSaleActive()
    {
        return saleActive;
    }

    public void setSaleActive(IData saleActive)
    {
        this.saleActive = saleActive;
    }
    
    public void setSaleActiveFiles(IDataset saleActiveFiles)
    {
        this.saleActiveFiles = saleActiveFiles;
    }
}
