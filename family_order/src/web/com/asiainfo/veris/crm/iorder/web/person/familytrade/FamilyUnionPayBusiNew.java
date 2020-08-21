/**
 * 
 */

package com.asiainfo.veris.crm.iorder.web.person.familytrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-5-21 修改历史 Revision 2014-5-21 下午03:08:00
 */
public abstract class FamilyUnionPayBusiNew extends PersonBasePage
{

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    public void checkBySerialNumber(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();

        pagedata.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.FamilyUnionPaySVC.checkBySerialNumber", pagedata);

        this.setAjax(results.getData(0));

    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();

        pagedata.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.FamilyUnionPaySVC.loadChildTradeInfo", pagedata);

        this.setCommInfo(results.getData(0).getData("FAM_PARA"));

        this.setInfos(results.getData(0).getDataset("QRY_MEMBER_LIST"));

    }

    /**
     * REQ201803260019++关于申请下放二级副以上领导手机代付铁通固定电话费用的权限的需求
     * 有权限的工号，对领导号码，免密添加
     */
    public void checkLeaderSerialNumberAndPermission(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();

        pagedata.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.LeaderInfoSVC.checkBySerialNumberAndPermission", pagedata);

        this.setAjax(results);

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.FamilyUnionPayRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCommInfo(IData info);// 

    public abstract void setInfos(IDataset infos);// 

}
