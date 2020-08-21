
package com.asiainfo.veris.crm.order.soa.group.returnratio;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class ReturnRationReqData extends GroupReqData
{

    private String groupId;

    private String rETURN_RATION;

    private String START_ACTIVE_DATE;

    private String END_ACTIVE_DATE;

    private String ACTIVE_APPROVE_CODE;

    
    private String operation ;
    
    private String euserId;
    
    private String instId;
    
    private String eactiveApproveCode;
    
    private String ereturnRation;
    
    private String estartActiveDate;
    
    private String eendActiveDate;
    
    public String getActiveCode()
    {
        return ACTIVE_APPROVE_CODE;
    }

    public String getEndActiveDate()
    {
        return END_ACTIVE_DATE;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getReturnRation()
    {
        return rETURN_RATION;
    }

    public String getStartActiveDate()
    {
        return START_ACTIVE_DATE;
    }

    public void setActiveCode(String ACTIVE_APPROVE_CODE)
    {
        this.ACTIVE_APPROVE_CODE = ACTIVE_APPROVE_CODE;

    }

    public void setEndActiveDate(String END_ACTIVE_DATE)
    {
        this.END_ACTIVE_DATE = END_ACTIVE_DATE;

    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public void setReturnRation(String RETURN_RATION)
    {
        this.rETURN_RATION = RETURN_RATION;

    }

    public void setStartActiveDate(String START_ACTIVE_DATE)
    {
        this.START_ACTIVE_DATE = START_ACTIVE_DATE;

    }

    //做修改、删除时使用
    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    public String getEuserId()
    {
        return euserId;
    }

    public void setEuserId(String euserId)
    {
        this.euserId = euserId;
    }

    public String getInstId()
    {
        return instId;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public String getEactiveApproveCode()
    {
        return eactiveApproveCode;
    }

    public void setEactiveApproveCode(String eactiveApproveCode)
    {
        this.eactiveApproveCode = eactiveApproveCode;
    }

    public String getEreturnRation()
    {
        return ereturnRation;
    }

    public void setEreturnRation(String ereturnRation)
    {
        this.ereturnRation = ereturnRation;
    }

    public String getEstartActiveDate()
    {
        return estartActiveDate;
    }

    public void setEstartActiveDate(String estartActiveDate)
    {
        this.estartActiveDate = estartActiveDate;
    }

    public String getEendActiveDate()
    {
        return eendActiveDate;
    }

    public void setEendActiveDate(String eendActiveDate)
    {
        this.eendActiveDate = eendActiveDate;
    }
    
}
