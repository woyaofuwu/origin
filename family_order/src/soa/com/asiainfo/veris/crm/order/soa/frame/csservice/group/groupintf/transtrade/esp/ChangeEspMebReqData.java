
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElementReqData;

public class ChangeEspMebReqData extends ChangeMemElementReqData
{


    public IData espProductInfo;// 成员产品信息
    
    public String mebType;//成员类型
    
    public IData getEspProductInfo()
    {
        return espProductInfo;
    }


    public void setEspProductInfo(IData espProductInfo)
    {
        this.espProductInfo = espProductInfo;
    }
    public String getMebType()
    {
        return mebType;
    }
    public void setMebType(String mebType)
    {
    	this.mebType = mebType;
    }

}
