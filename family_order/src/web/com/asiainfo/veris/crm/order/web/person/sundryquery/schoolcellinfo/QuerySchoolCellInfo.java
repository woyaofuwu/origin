
package com.asiainfo.veris.crm.order.web.person.sundryquery.schoolcellinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：校园卡基站信息查询 作者：GongGuang
 */
public abstract class QuerySchoolCellInfo extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 初始化
     */
    public void init(IRequestCycle cycle) throws Exception
    {

        querySchoolCards(cycle);

    }

    /**
     * 查询校园卡名称
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySchoolCards(IRequestCycle cycle) throws Exception
    {

        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset cards = CSViewCall.call(this, "SS.QuerySchoolCellInfoSVC.querySchoolCards", input);
        if (IDataUtil.isNotEmpty(cards))
        {
            for (int i = 0; i < cards.size(); i++)
            {
                String cardName = cards.getData(i).getString("DISCNT_NAME");
                String cardCode = cards.getData(i).getString("DISCNT_CODE");
                cards.getData(i).put("DISCNT_NAME", "[" + cardCode + "]" + cardName);
            }
            setSchoolCards(cards);
        }
    }

    /**
     * 功能：校园卡基站信息查询
     */
    public void querySchoolCellInfo(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.QuerySchoolCellInfoSVC.querySchoolCellInfo", inparam, getPagination("navtnew"));
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【校园卡基站信息】数据~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(results);
        setCount(dataCount.getDataCount());
        setCondition(getData("cond", true));

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

    public abstract void setSchoolCards(IDataset schoolCards);

}
