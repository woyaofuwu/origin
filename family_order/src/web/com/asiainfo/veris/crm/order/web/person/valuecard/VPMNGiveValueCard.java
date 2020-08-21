/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.valuecard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-8-4 修改历史 Revision 2014-8-4 上午09:33:17
 */
public abstract class VPMNGiveValueCard extends PersonBasePage
{
    public void addClick(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.ValueCardMgrSVC.getValueCardInfo", data);

        IDataset set1 = results.getData(0).getDataset("TABLE1");
        IDataset set2 = results.getData(0).getDataset("TABLE2");

        String temp = data.getString("table2");
        IDataset tempSet = new DatasetList(temp);
        if (tempSet.size() > 0)
        {

            set2.addAll(tempSet);
        }
        setAjax(set2);

        this.setBasicInfos(set1);
        this.setSaleInfos(set2);

    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.ValueCardMgrSVC.getVPMNGiveValueCardUserInfo", data);

        if (IDataUtil.isNotEmpty(results))
        {
            this.setCustInfoView(results.getData(0));
        }
        else
        {
            IData temp = new DataMap();

            temp.put("MONTH", data.getString("MONTH"));
            temp.put("MONTH", "0.0");

            this.setCustInfoView(temp);
        }

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

        IData temp = new DataMap();
        temp.put("VPMN_FEE", "0.0");

        this.setCustInfoView(temp);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        if (data.getString("TRADE_TYPE_CODE").equals("430"))
        {

            IDataset dataset = CSViewCall.call(this, "SS.VPMNGiveValueCardRegSVC.tradeReg", data);
            setAjax(dataset);
        }

    }

    public abstract void setAuditInfos(IDataset dataset);

    public abstract void setBasicInfos(IDataset dataset);

    public abstract void setCond(IData cond);

    public abstract void setSaleInfos(IDataset dataset);
}
