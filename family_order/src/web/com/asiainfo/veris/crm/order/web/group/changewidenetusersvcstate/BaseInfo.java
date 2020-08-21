
package com.asiainfo.veris.crm.order.web.group.changewidenetusersvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BaseInfo extends GroupBasePage
{

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        
        String operType= param.getString("STATE_FLAG");
        
        setOperType(operType);

    }
    
    
    public abstract void setOperType(String mebCount);

}
