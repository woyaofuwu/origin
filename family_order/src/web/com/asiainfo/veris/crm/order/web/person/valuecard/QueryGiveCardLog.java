/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.valuecard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-5-23 修改历史 Revision 2014-5-23 上午11:07:45
 */
public abstract class QueryGiveCardLog extends PersonBasePage
{

    public void getValueCardGiveInfo(IRequestCycle cycle) throws Exception
    {
        Pagination page = getPagination("recordNav");

        IData data = getData("cond", true);

        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        IDataOutput result = CSViewCall.callPage(this, "SS.ValueCardMgrSVC.qryGiveCardLogInfos", data, page);

        setInfos(result.getData());
        setCondition(data);
        setPageCount(result.getDataCount());

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

        // 查询区域信息
        IData svcData = new DataMap();

        /*
         * if ((this.getVisit().getStaffId()).substring(0, 4).matches("HNSJ|HNYD|SUPE")) {
         */
        svcData.put("AREA_FRAME", getTradeEparchyCode());
        /*
         * } else { svcData.put("AREA_FRAME", getVisit().getCityCode()); }
         */

        IDataset cityList = CSViewCall.call(this, "CS.AreaInfoQrySVC.qryAeraByAreaFrame", svcData);

        setCityList(cityList);
    }

    public abstract void setCityList(IDataset cityList);

    public abstract void setCond(IData cond);

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long count);

}
