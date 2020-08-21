
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData; 

public class CPEAreaLockReqData extends BaseReqData
{ 

    private String cellId;//  
    private String cellName;
    private String remark;

    
    public String getCellId()
    {
        return cellId;
    }

    public void setCellId(String cellId)
    {
        this.cellId = cellId;
    }  
    public String getCellName()
    {
        return cellName;
    }

    public void setCellName(String cellName)
    {
        this.cellName = cellName;
    } 
}
