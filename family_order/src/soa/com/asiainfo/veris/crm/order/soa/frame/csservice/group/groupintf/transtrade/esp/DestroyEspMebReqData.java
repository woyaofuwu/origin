
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMemberReqData;

public class DestroyEspMebReqData extends DestroyGroupMemberReqData
{
    public IData espProductInfo;// 成员产品信息
    
    public String mebType;//成员类型

    public String effDate;
    
    public String interType;
    

    public IData getEspProductInfo()
    {
        return espProductInfo;
    }

    public String getEffDate()
    {
        return effDate;
    }
    
    public String getMebType()
    {
        return mebType;
    }
    public void setMebType(String mebType)
    {
    	this.mebType = mebType;
    }
    public String getInterType()
    {
        return interType;
    }
    public void setInterType(String interType)
    {
    	this.interType = interType;
    }

    public void setEspProductInfo(IData espProductInfo)
    {
        this.espProductInfo = espProductInfo;
    }

    public void setEffDate(String effDate)
    {
        this.effDate = effDate;
    }


}
