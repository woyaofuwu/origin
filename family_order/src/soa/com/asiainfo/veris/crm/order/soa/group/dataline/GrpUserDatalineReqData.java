
package com.asiainfo.veris.crm.order.soa.group.dataline;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class GrpUserDatalineReqData extends GroupReqData
{
    private String GRP_USER_ID;
    
    private String CRMNO_ATTR_VALUE;
    
    private IDataset dataLine;
    
    public String getGRP_USER_ID()
    {
        return GRP_USER_ID;
    }

    public void setGRP_USER_ID(String grp_user_id)
    {
        GRP_USER_ID = grp_user_id;
    }

    public String getCRMNO_ATTR_VALUE()
    {
        return CRMNO_ATTR_VALUE;
    }

    public void setCRMNO_ATTR_VALUE(String crmno_attr_value)
    {
        CRMNO_ATTR_VALUE = crmno_attr_value;
    }

    public IDataset getDataLine()
    {
        return dataLine;
    }

    public void setDataLine(IDataset dataLine)
    {
        this.dataLine = dataLine;
    }

}
