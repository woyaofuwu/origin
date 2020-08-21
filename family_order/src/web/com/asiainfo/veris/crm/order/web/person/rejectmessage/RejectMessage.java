
package com.asiainfo.veris.crm.order.web.person.rejectmessage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RejectMessage extends PersonBasePage
{

    public void checkClick(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();
        // 获取导入数据
        IDataset rejectMList = CSViewCall.call(this, "SS.RejectMessageSVC.importClick", pagedata);
        IDataset rejectList = new DatasetList();
        if (IDataUtil.isNotEmpty(rejectMList))
        {
            if ("1".equals(pagedata.getString("Flag")))
            {
                rejectMList = DataHelper.distinct(rejectMList, "SERIAL_NUMBER", "");
            }

            pagedata.put("rejectMList", rejectMList);
            rejectList = CSViewCall.call(this, "SS.RejectMessageSVC.checkUserService", pagedata);
        }

        int sum = 0;
        Boolean flag = false;
        if (IDataUtil.isNotEmpty(rejectList))
        {
            IDataset result = new DatasetList();
            for (int i = 0; i < rejectList.size(); i++)
            {
                IData recomdInfo = rejectList.getData(i);
                recomdInfo.put("RSRV_STR1", i + 1);
                result.add(recomdInfo);
                sum++;
            }
            this.setSelectList(result);
        }
        // else
        // {
        // CSViewException.apperr(CrmCommException.CRM_COMM_175);
        // }

        String msg = "";

        if (flag)
        {
            msg = "短信息拒收过滤:过滤结果[记录数:0]";
            setAjax("MSG", msg);
        }
        else
        {
            setAjax("SUM", "" + sum);
        }

    }

    /**
     * 获取导入数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void importClick(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();
        // 获取导入数据
        IDataset rejectMList = CSViewCall.call(this, "SS.RejectMessageSVC.importClick", pageData);
        if (IDataUtil.isNotEmpty(rejectMList))
        {
            IDataset result = new DatasetList();

            if ("1".equals(pageData.getString("Flag")))
            {
                rejectMList = DataHelper.distinct(rejectMList, "SERIAL_NUMBER", "");
            }

            for (int i = 0; i < rejectMList.size(); i++)
            {
                IData recomdInfo = rejectMList.getData(i);
                recomdInfo.put("RSRV_STR1", i + 1);
                result.add(recomdInfo);
            }

            setEditList(result);
        }
        else
        {
            CSViewException.apperr(ParamException.CRM_PARAM_383);
        }

    }

    /**
     * 主要用来获取页面初始化时的服务信息
     * 
     * @data 2013-9-25
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();
        // 初始化页面服务参数
        setServiceInfo(pagedata);
    }

    public abstract void setCondition(IData idata);

    public abstract void setEditList(IDataset list);

    public abstract void setInfos(IDataset serviceList);

    public abstract void setSelectList(IDataset list);

    /**
     * 设置服务信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void setServiceInfo(IData data) throws Exception
    {

        IDataset dataset = CSViewCall.call(this, "SS.RejectMessageSVC.getAllServiceInfo", data);
        DataHelper.sort(dataset, "SERVICE_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        setInfos(dataset);
    }

}
