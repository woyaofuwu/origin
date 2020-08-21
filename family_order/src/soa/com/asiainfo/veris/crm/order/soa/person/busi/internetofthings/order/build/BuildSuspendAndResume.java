
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.build;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.requestdata.SuspendAndResumeReqData;

public class BuildSuspendAndResume extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        SuspendAndResumeReqData prd = (SuspendAndResumeReqData) brd;
        String suspendServices = param.getString("SUSPEND_SERVICE");
        String resumeServices = param.getString("RESUME_SERVICE");
        String moreOper = param.getString("MORE_OPER","");
        prd.setMore_oper(moreOper);
        
        //表示流量用尽关停
        String closeOper = param.getString("CLOSE_OPER");
        prd.setClose_oper(closeOper);
        if (suspendServices != null && !"".equals(suspendServices))
        {
            String[] suspendArray = suspendServices.split(",");
            for (int i = 0; i < suspendArray.length; i++)
            {
            	String[] svcApnList = suspendArray[i].split("&");
                prd.getSuspendList().add(svcApnList[0]);
            	prd.getSvcInstIdList().add(svcApnList[1]);
            }
        }
        if (resumeServices != null && !"".equals(resumeServices))
        {
            String[] resumeArray = resumeServices.split(",");
            for (int j = 0; j < resumeArray.length; j++)
            {
            	String[] svcApnList = resumeArray[j].split("&");
                prd.getResumeList().add(svcApnList[0]);
            	prd.getSvcInstIdList().add(svcApnList[1]);
            	
            }
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new SuspendAndResumeReqData();
    }

}
