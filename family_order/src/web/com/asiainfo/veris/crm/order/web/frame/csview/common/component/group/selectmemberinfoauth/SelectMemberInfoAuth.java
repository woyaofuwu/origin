
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectmemberinfoauth;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class SelectMemberInfoAuth extends CSBizTempComponent
{

    public abstract String getIsAuth();
    
    public abstract String getPageMode();
    
    public abstract boolean getCacheSn();

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

        if (cycle.isRewinding())
            return;

        String isAuthString = getIsAuth();
        if (!"true".equals(isAuthString) && !"false".equals(isAuthString))
        {
            String staffId = getVisit().getStaffId();
            
            boolean passPriv = false;
            
            // 页面流权限校验
            if ("pageFlow".equals(getPageMode())) 
            {
            	passPriv = StaffPrivUtil.isFuncDataPriv(staffId, "CREATEGROUPMEMPASSWD");
			}
            
            boolean sysPriv = StaffPrivUtil.isFuncDataPriv(staffId, "SYS009");
            if (passPriv == true || sysPriv == true)
            {
                setIsAuth("false");
            }
            else
            {
                setIsAuth("true");
            }
        }

        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/selectmemberinfo/SelectMemberInfo.js");
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/businesstip/businesstip.js");
    }

    public abstract void setIsAuth(String auth);
    
    public abstract void setCacheSn(boolean cacheSn);
    
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
        	setIsAuth("");
        }
    }

}
