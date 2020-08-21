
package com.asiainfo.veris.crm.order.web.person.cancelchangeproduct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CancelChangeProduct extends PersonBasePage
{

    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData pdData = getData();
        setCondition(pdData);
    }
    /**
     * 执行取消动作
     * 
     * @param cycle
     * @throws Exception
     */
    public void cancelTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pdData = getData();
        IDataset ret = CSViewCall.call(this, "SS.CancelChangeProductSVC.cancelChangeProductTrade", pdData);
        setAjax(ret);
    }

    /**
     * 查询可返销的订单
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCancelTrade(IRequestCycle cycle) throws Exception
    {
        IData ajax = new DataMap();
        ajax.put("QUERY_CODE", "N");
        ajax.put("QUERY_INFO", "该用户没有可取消的预约产品变更数据！");

        IData pdData = getData("cond");
        IData input = new DataMap();
        input.put("SERIAL_NUMBER", pdData.getString("SERIAL_NUMBER"));
        input.put("NET_TYPE_CODE",pdData.getString("NET_TYPE_CODE"));
        IDataset errTradeInfos = CSViewCall.call(this, "SS.CancelChangeProductSVC.queryErrorInfoTrade", input);
        if (IDataUtil.isEmpty(errTradeInfos))
        {
            IDataset cancelTradeInfos = CSViewCall.call(this, "SS.CancelChangeProductSVC.queryChangeProductTrade", input);

            if (IDataUtil.isNotEmpty(cancelTradeInfos))
            {
                ajax.put("QUERY_CODE", "Y");
                setValidCancelTradeList(cancelTradeInfos);
            }
        }
        else
        {
            ajax.put("QUERY_INFO", "该用户有错单未处理,不能办理预约产品变更取消！");
        }
        setAjax(ajax);
    }

    public abstract void setCondition(IData condition);

    public abstract void setValidCancelTradeList(IDataset validCancelTradeList);
}
