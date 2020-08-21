
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectmeborderinfoauth;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class SelectMebOrderInfoAuth extends CSBizTempComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            // setLimitProducts(null);
            // setLimitType(null);
        	setIsAuth("");
            setRelaCode(0);
        }
    }

    public abstract String getIsAuth();
    
    public abstract boolean getCacheSn();

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

    	if (cycle.isRewinding())
    		return;

    	String isAuthString = getIsAuth();
    	if("1".equals(isAuthString)){
    		setIsAuth("true");
    	}else{
    		if (!"true".equals(isAuthString) && !"false".equals(isAuthString))
    		{
    			String staffId = getVisit().getStaffId();
    			boolean sysPriv = StaffPrivUtil.isFuncDataPriv(staffId, "SYS009");
    			if (sysPriv == true)
    			{
    				setIsAuth("false");
    			}
    			else
    			{
    				setIsAuth("true");
    			}
    		}
    	}

    	cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/selectmeborderinfo/SelectMebOrderInfo.js");
    	cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/businesstip/businesstip.js");

    }

    public abstract void setIsAuth(String auth);

    public abstract void setLimitProducts(String products);

    public abstract void setLimitType(String type);

    public abstract void setRelaCode(int relaCode);
    
    public abstract void setCacheSn(boolean cacheSn);

}
