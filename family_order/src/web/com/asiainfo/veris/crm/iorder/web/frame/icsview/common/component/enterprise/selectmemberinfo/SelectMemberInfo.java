package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.selectmemberinfo;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class SelectMemberInfo extends BizTempComponent
{
    public abstract void setCond(IData data);

    public abstract void setUserInfo(IData infos);

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

    	String isAuthString = getIsAuth();
        if (!"true".equals(isAuthString) && !"false".equals(isAuthString))
        {
            String staffId = getVisit().getStaffId();
            
            boolean passPriv = false;
            
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
        
        // 添加js
        String js1 = "scripts/iorder/icsserv/component/enterprise/selectmemberinfo/SelectMemberInfo.js";
        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax) {
            includeScript(writer, js1, false, false);
        } else {
            getPage().addResAfterBodyBegin(js1, false, false);
        }

        String accessNumber = cycle.getRequestContext().getParameter("cond_SERIAL_NUMBER_INPUT");
        if (StringUtils.isNotEmpty(accessNumber))
        {
            queryMemberInfo(cycle);
        }

    }

    // 查询成员信息
    public void queryMemberInfo(IRequestCycle cycle) throws Exception
    {
        String accessNumber = cycle.getRequestContext().getParameter("cond_SERIAL_NUMBER_INPUT");

        IData result = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, accessNumber);
        this.getPage().setAjax(result);
        this.setUserInfo(result);
    }
    
public abstract String getIsAuth();
    
    public abstract String getPageMode();
    
    public abstract boolean getCacheSn();
    
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
