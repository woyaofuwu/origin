package com.asiainfo.veris.crm.iorder.web.igroup.esop.readwork;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class ReadWork extends CSBasePage
{

    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if("DONE_WORK".equals(data.getString("QUERY_TYPE")))
        {//查询已办工单
            queryDoneWork(data);
        }
        else
        {
            queryUnReadWork(data);
        }
    }
    
    private void queryDoneWork(IData data) throws Exception
    {
        IDataset workList = CSViewCall.call(this, "SS.WorkTaskMgrSVC.queryOpTaskHisByInstId", data);
        if(IDataUtil.isEmpty(workList))
        {
            CSViewException.apperr(GrpException.CRM_GRP_713, "该已办任务已被删除!");
        }
        setWorkInfo(workList.first());
    }
    
    private void queryUnReadWork(IData data) throws Exception
    {
        IDataset workList = CSViewCall.call(this, "SS.WorkTaskMgrSVC.queryWorkInfo", data);
        if(IDataUtil.isEmpty(workList))
        {
            CSViewException.apperr(GrpException.CRM_GRP_713, "该待阅任务已被删除，请重新查询!");
        }
        IData workInfo = workList.first();
        if(EcEsopConstants.INST_STATUS_NOREAD.equals(workInfo.getString("INST_STATUS")))
        {
            IData input = new DataMap();
            input.put("INST_ID", workInfo.getString("INST_ID"));
            input.put("INST_STATUS", EcEsopConstants.INST_STATUS_READED);
            CSViewCall.call(this, "SS.WorkTaskMgrSVC.updateOpTaskInstStatus", input);
        }
        
        String busiTypeCode = workInfo.getString("INFO_CHILD_TYPE");
        if(EcEsopConstants.BUSI_TYPE_CODE_40.equals(busiTypeCode) || EcEsopConstants.BUSI_TYPE_CODE_41.equals(busiTypeCode) 
                || EcEsopConstants.BUSI_TYPE_CODE_42.equals(busiTypeCode) || EcEsopConstants.BUSI_TYPE_CODE_43.equals(busiTypeCode)
                || EcEsopConstants.BUSI_TYPE_CODE_44.equals(busiTypeCode) || EcEsopConstants.BUSI_TYPE_CODE_45.equals(busiTypeCode))
        {
            IData input = new DataMap();
            input.put("INFO_ID", workInfo.getString("INFO_ID"));
            input.put("INFO_STATUS", EcEsopConstants.WORKINFO_STATUS_DONE);
            CSViewCall.call(this, "SS.WorkTaskMgrSVC.updateOpTaskInfoStatus", input);
        }
        setWorkInfo(workInfo);
    }
    
    public abstract void setWorkInfo(IData workInfo) throws Exception;
}
