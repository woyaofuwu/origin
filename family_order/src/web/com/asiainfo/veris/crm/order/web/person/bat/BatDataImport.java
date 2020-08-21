
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatDataImport extends PersonBasePage
{

    public void importData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData set = null;
        IDataset sets = CSViewCall.call(this, "SS.BatDataImportSVC.importData", data);
        if (sets.size() > 0)
        {
            set = sets.getData(0);
            setAjax(set);
        }
    }

    public void iPInit(IRequestCycle cycle) throws Exception
    {
        IData params = new DataMap();
        params.put("TERM_IP", getVisit().getRemoteAddr());
        setCondition(params);
    }

    public void pageInit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData set = null;
        IData cond = new DataMap();
        cond.put("BATCH_TASK_ID", data.getString("cond_BATCH_TASK_ID"));
        IDataset sets = CSViewCall.call(this, "CS.BatDealSVC.queryBatTaskByPK", cond);
        if (sets.size() > 0)
        {
            set = sets.getData(0);
            String sBatchOperCode = set.getString("BATCH_OPER_CODE", "");
            IData param = new DataMap();
            param.put("BATCH_OPER_TYPE", sBatchOperCode);
            IDataset batchTypeinfos = CSViewCall.call(this, "CS.BatDealSVC.queryBatTypeByPK", param);

            if (IDataUtil.isNotEmpty(batchTypeinfos))
            {
                String batchOperName = batchTypeinfos.getData(0).getString("BATCH_OPER_NAME");
                set.put("TEMPLATE_DATA_XLS", batchOperName + ".xls");
                set.put("DOWN_URL", "template/bat/" + sBatchOperCode + ".xls");
                set.put("TEMPLATE_FORMART_XML", "import/bat/" + sBatchOperCode + ".xml");
                set.put("IMPORT_CONTROL_ENABLE", "false");
            }
        }
        setTaskInfo(set);
        IData params = new DataMap();
        params.put("cond_BATCH_TASK_ID", getData().getString("cond_BATCH_TASK_ID"));
        params.put("cond_BATCH_TASK_NAME", set.getString("BATCH_TASK_NAME"));
        params.put("TERM_IP", getVisit().getRemoteAddr());
        setCondition(params);
    }

    /**
     * @Function: queryBatTaskByPK
     * @Description: 根据主键查询批量批次信息
     * @param: @param cycle
     * @param: @throws Exception
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 4:55:32 PM May 25, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* May 25, 2013 tangxy v1.0.0 新建函数
     */
    public void queryBatTaskByPK(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData set = null;
        IData cond = new DataMap();
        cond.put("BATCH_TASK_ID", data.getString("cond_BATCH_TASK_ID"));
        IDataset sets = CSViewCall.call(this, "CS.BatDealSVC.queryBatTaskByPK", cond);
        if (sets.size() > 0)
        {
            set = sets.getData(0);
            String sBatchOperCode = set.getString("BATCH_OPER_CODE", "");
            IData param = new DataMap();
            param.put("BATCH_OPER_TYPE", sBatchOperCode);
            IDataset batchTypeinfos = CSViewCall.call(this, "CS.BatDealSVC.queryBatTypeByPK", param);

            if (IDataUtil.isNotEmpty(batchTypeinfos))
            {
                String batchOperName = batchTypeinfos.getData(0).getString("BATCH_OPER_NAME");
                set.put("TEMPLATE_DATA_XLS", batchOperName + ".xls");
                set.put("DOWN_URL", "template/bat/" + sBatchOperCode + ".xls");
                set.put("TEMPLATE_FORMART_XML", "import/bat/" + sBatchOperCode + ".xml");
                set.put("IMPORT_CONTROL_ENABLE", "false");
            }
        }
        setTaskInfo(set);
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

    public abstract void setCondition(IData condition);

    public abstract void setTaskInfo(IData taskInfo);

}
