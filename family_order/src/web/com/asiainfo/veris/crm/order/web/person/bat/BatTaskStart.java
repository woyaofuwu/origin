
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatTaskStart extends PersonBasePage
{

    /**
     * @Function: batTaskNowRun
     * @Description: 批量任务立即启动
     * @param：
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 下午7:38:41 2013-7-29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-7-29 huanghui v1.0.0
     */
    public void batTaskNowRun(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String runInfo = data.get("PARAM").toString();
        IDataset reqInfo = new DatasetList(runInfo);
        for (int i = 0; i < reqInfo.size(); i++)
        {
            IData info = (IData) reqInfo.get(i);
            CSViewCall.call(this, "CS.BatDealSVC.batTaskNowRun", info);
        }
    }

    /**
     * @Function: batTaskOnTimeRun
     * @Description: 批量任务预约启动
     * @param：
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 下午7:39:09 2013-7-29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-7-29 huanghui v1.0.0
     */
    public void batTaskOnTimeRun(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String runInfo = data.get("PARAM").toString();
        String startDate = data.get("StartDate").toString();
        String startTime = data.get("StartTime").toString();
        IDataset reqInfo = new DatasetList(runInfo);
        for (int i = 0; i < reqInfo.size(); i++)
        {
            IData info = (IData) reqInfo.get(i);
            String batchId = info.getString("BATCH_ID");
            IData batInfoParam = new DataMap();
            batInfoParam.clear();
            batInfoParam.put("BATCH_ID", batchId);
            IDataset batInfo = CSViewCall.call(this, "CS.BatDealSVC.queryBatTaskBatchInfo", batInfoParam);
            if (IDataUtil.isEmpty(batInfo))
            {
                continue;
            }
            // 0未激活 1已激活 ,只启动未激活批次的任务
            if ("0".equals(((IData) (batInfo.get(0))).getString("ACTIVE_FLAG")))
            {
                info.put("START_DATE", startDate);
                info.put("START_TIME", startTime);
                CSViewCall.call(this, "CS.BatDealSVC.batTaskOnTimeRun", info);
            }
            else
            {
                continue;
            }
        }
    }

    public void initBatTaskStart(IRequestCycle cycle)
    {

    }

    /**
     * @Function: queryBatTaskByPK
     * @Description: 查询可启动的任务改造
     * @param: @param cycle
     * @param: @throws Exception
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 4:55:32 PM May 25, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* May 25, 2013 duxy v1.0.0 新建函数
     */
    public void queryBatTaskStart(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData cond = new DataMap();
        cond.put("BATCH_TASK_ID", data.getString("cond_BATCH_TASK_ID"));
        String fromPage = data.getString("FROM_PAGE", "");
        if ("1".equals(fromPage))
        {
            IDataset taskInfo = CSViewCall.call(this, "CS.BatDealSVC.queryBatTaskByPK", cond);
            if (IDataUtil.isEmpty(taskInfo))
            {
                setTipInfo("未查到该批量任务信息");
                return;
            }
            else
            {
                String taskName = taskInfo.getData(0).getString("BATCH_TASK_NAME", "");
                cond.put("cond_BATCH_TASK_NAME", taskName);
                cond.put("cond_BATCH_TASK_ID", data.getString("cond_BATCH_TASK_ID"));
                //setCondition(cond);
            }
        }
        else if ("2".equals(fromPage)) // 针对一次创建多个批量任务的批量平台业务的处理
        {
            String batchId = data.getString("BATCH_ID");
            String batchIds = "";
            if (StringUtils.isNotBlank(batchId))
            {
                String[] batchIdArray = batchId.split(",");
                for (int i = 0; i < batchIdArray.length; i++)
                {
                    batchIds = batchIds + "'" + batchIdArray[i] + "',";
                }
                cond.put("BATCH_IDS", batchIds.substring(0, batchIds.length() - 1));
            }

        }

        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryBatTaskStart", cond, getPagination("taskNav"));
        if (output.getData() == null || output.getData().size() == 0)
        {
            setTipInfo("没有符合查询条件的【未完工工单】数据~");
        }

        setTaskInfos(output.getData());
        setBatchTaskListCount(output.getDataCount());
        
        /**
         * REQ201806190020_新增行业应用卡批量开户人像比对功能
         * <br/>
         * 往界面回传  批量操作类型
         * @author zhuoyingzhi
         * @date 20180725
         */
        if(IDataUtil.isNotEmpty(output.getData())){
        	String BATCH_OPER_TYPE=output.getData().getData(0).getString("BATCH_OPER_TYPE", "");
        	cond.put("BATCH_OPER_TYPE",BATCH_OPER_TYPE);
        }
        setCondition(cond);
        // initPage(cycle);

    }

    /**
     * 判断批量是否已经打印
     * @param cycle
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20180724
     */
    public void chexkBatTask(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData result=CSViewCall.callone(this, "CS.BatDealSVC.checkBatTask", data);
        setAjax(result);
    }
    
    /**
     * REQ201904260020新增物联网批量开户界面权限控制需求
     * 免人像比对权限判断
     * @author mengqx
     * @date 20190515
     * @param clcle
     * @throws Exception
     */
    public void isBatCmpPic(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
    	
    	param.putAll(data);
    	param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isBatCmpPic", param);
    	setAjax(dataset.getData(0));
    }
    
    public abstract void setBatchOperTypes(IDataset batchOperTypes);

    public abstract void setBatchTaskListCount(long count);

    public abstract void setComp(IData comp);

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setTaskInfo(IData taskInfo);

    public abstract void setTaskInfos(IDataset task);

    public abstract void setTipInfo(String tipInfo);
}
