
package com.asiainfo.veris.crm.order.web.person.speservice;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FixParam extends PersonBasePage
{

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData();
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        // 服务返回结果集
        IDataset dataSet = CSViewCall.call(this, "SS.FixParamSVC.queryCommParamByEparchy", inputData);
        setInfos(dataSet);
    }

    public abstract void setDept(IData info);

    public abstract void setInfos(IDataset info);

    /**
     * submit depts
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitDepts(IRequestCycle cycle) throws Exception
    {
        // 前台传入
        IData inputData = this.getData();
        // 服务输入参数；
        IData dataParam = new DataMap();

        String encodestr = inputData.getString("edit_table");

        if (encodestr == null || encodestr.length() < 1)
        {
            return;
        }
        // 将拼串结合串头描述结合生成数据集
        IDataset dataset = new DatasetList(encodestr);

        dataParam.put("edit_table", dataset);
        dataParam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 执行批量操作逻辑
        CSViewCall.call(this, "SS.FixParamSVC.submitDepts", dataParam);
        // 刷新页面表格
        IDataset dataSet = CSViewCall.call(this, "SS.FixParamSVC.queryCommParamByEparchy", dataParam);
        setInfos(dataSet);

    }

}
