package com.asiainfo.veris.crm.order.soa.group.esop.esopdesktop;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeTraQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.OpTaskInfoQry;

/**
 * order
 * Created on 2018/1/15.
 * @author ckh
 */
public class WorkTaskMgrSVC extends GroupOrderService
{
    private static final long serialVersionUID = -5336904143518595735L;

    public IData crtWorkTaskInfo(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.crtWorkTaskInfo(param);
    }

    public IData updWorkTaskInfo(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.updWorkTaskInfo(param);
    }

    public IData updWorkTaskPlanTime(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.updWorkTaskPlanTime(param);
    }

    public IDataset qryWorkTaskInfo(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.qryWorkTaskInfo(param);
    }

    public IDataset qryWorkTaskByStaffIdTaskTypeCodeTaskStatus(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.qryWorkTaskByStaffIdTaskTypeCodeTaskStatus(param, this.getPagination());
    }

    public IDataset queryOpTaskList(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.queryOpTaskList(param, this.getPagination());
    }

    public IDataset queryBusinessOpTaskList(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.queryBusinessOpTaskList(param, this.getPagination());
    }

    public IDataset queryOpTaskHisList(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.queryOpTaskHisList(param, this.getPagination());
    }

    public IDataset queryUnReadList(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.queryUnReadList(param, this.getPagination());
    }

    public IDataset queryUnDoneWorkList(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.queryUnDoneWorkList(param, this.getPagination());
    }

    public IDataset queryDoneWorkList(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.queryDoneWorkList(param, this.getPagination());
    }

    public IDataset queryOpTaskHisByInstId(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.queryOpTaskHisByInstId(param);
    }

    public IDataset queryWorkInfo(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.queryWorkInfo(param);
    }
    public IDataset queryWorkInfoByInfoSign(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.queryWorkInfoByInfoSign(param);
    }

    public void updateOpTaskInfoStatus(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        bean.updateOpTaskInfoStatus(param);
    }

    public void updateOpTaskInstStatus(IData param) throws Exception
    {
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        bean.updateOpTaskInstStatus(param);
    }

    /**
     * 待办工单批量转移
     * @param param
     * @throws Exception
     */
    public void changeWorkInfoReceObj(IData param) throws Exception
    {
        //1、更新TF_F_OP_TASK_INST
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        bean.updateOpTaskInstStaffId(param);

        //2、更新TF_B_EWE_NODE_TRA
        EweNodeTraQry.updateEweNodeTraDealStaffId(param);
    }


    public IDataset qryInfosByInstId(IData param) throws Exception{
        return OpTaskInfoQry.qryInfosByInstId(param);
    }

    /**
     * 供手机端查询中小企代办
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getWorkTaskInfos(IData param) throws Exception{
        WorkTaskMgrBean bean = new WorkTaskMgrBean();
        return bean.getWorkTaskInfos(param,getPagination());
    }
}
