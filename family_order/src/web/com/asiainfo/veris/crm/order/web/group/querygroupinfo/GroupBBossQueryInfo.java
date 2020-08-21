
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GroupBBossQueryInfo extends GroupBasePage
{

    public void ajaxSetPospecNumber(IRequestCycle cycle) throws Exception
    {

    }

    public abstract IData getCondition();

    public abstract IDataset getInfos();

    public abstract IDataset getSubInfos();

    /**
     * @Description: 初始化页面方法
     * @author jch
     * @date 2009-8-3
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {

        IData initdata = getData("cond", true);
        // Calendar calendar=Calendar.getInstance();
        // String Year=String.valueOf(calendar.get(Calendar.YEAR));
        // int month=calendar.get(Calendar.MONTH)+1;
        

        initdata.put("cond_START_DATE", SysDateMgr.getFirstDayOfThisMonth4WEB());
        initdata.put("cond_END_DATE", SysDateMgr.getLastDateThisMonth4WEB());
        
        IDataset poList = UpcViewCall.queryPoByValid(this);
        
        IDataset poProductList = UpcViewCall.queryPoproductByPospecNumber(this, initdata.getString("POSPECNUMBER",""));
        setPoList(poList);
        setPoProductList(poProductList);
        
        setCondition(initdata);
        // //Utility.setHintInfo("请输入查询条件~~!");
    }

    /**
     * BBOSS产品用户状态查询
     * 
     * @author liuxx3
     * @date
     * @param cycle
     * @throws exception
     */

    public void qryBbossBizEc(IRequestCycle cycle) throws Exception
    {

        IData param = getData("cond", true);

        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataOutput dop = CSViewCall.callPage(this, "CS.BbossQueryBizSVC.qryBBossBizEc", param, getPagination("infonav"));

        IDataset dataset = dop.getData();
        setInfoCount(dop.getDataCount());

        IData ctrlInfo = new DataMap();
        if (dataset.size() == 0)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        }
        else
        {
            ctrlInfo.put("strHint", "查询成功！");
        }
        
        IDataset poList = UpcViewCall.queryPoByValid(this);
        IDataset poProductList = UpcViewCall.queryPoproductByPospecNumber(this, param.getString("POSPECNUMBER",""));
        setPoList(poList);
        setPoProductList(poProductList);
        setCtrlInfo(ctrlInfo);
        setCondition(getData("cond", true));
        setInfos(dataset);

    }

    /**
     * BBOSS成员用户状态查询
     * 
     * @author liuxx3
     * @date
     * @param cycle
     * @throws exception
     */

    public void qryBBossBizMeb(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);

        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataOutput dop = CSViewCall.callPage(this, "CS.BbossQueryBizSVC.qryBBossBizMeb", param, getPagination("infonav"));

        IDataset dataset = dop.getData();
        setInfoCount(dop.getDataCount());

        IData ctrlInfo = new DataMap();
        if (dataset.size() == 0)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        }
        else
        {
            ctrlInfo.put("strHint", "查询成功！");
        }
        
        IDataset poList = UpcViewCall.queryPoByValid(this);
        IDataset poProductList = UpcViewCall.queryPoproductByPospecNumber(this, param.getString("POSPECNUMBER",""));
        setPoList(poList);
        setPoProductList(poProductList);
        
        setCtrlInfo(ctrlInfo);
        setCondition(getData("cond", true));
        setInfos(dataset);

    }

    /**
     * BBOSS成员状态查询
     * 
     * @author liuxx3
     * @date
     * @param cycle
     * @throws exception
     */

    public void qryBBossBizMebQy(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);

        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataOutput dop = CSViewCall.callPage(this, "CS.BbossQueryBizSVC.qryBBossBizMebQy", param, getPagination("infonav"));

        IDataset dataset = dop.getData();
        setInfoCount(dop.getDataCount());

        IData ctrlInfo = new DataMap();
        if (dataset.size() == 0)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        }
        else
        {
            ctrlInfo.put("strHint", "查询成功！");
        }
        setCtrlInfo(ctrlInfo);
        setCondition(getData("cond", true));
        setInfos(dataset);

    }

    /**
     * BBOSS产品状态查询
     * 
     * @author liuxx3
     * @date
     * @param cycle
     * @throws exception
     */

    public void qryBbossBizProdDg(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);

        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataOutput dop = CSViewCall.callPage(this, "CS.BbossQueryBizSVC.qryBBossBizProdDg", param, getPagination("infonav"));

        IDataset dataset = dop.getData();
        setInfoCount(dop.getDataCount());

        IData ctrlInfo = new DataMap();
        if (dataset.size() == 0)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        }
        else
        {
            ctrlInfo.put("strHint", "查询成功！");
        }
        
        IDataset poList = UpcViewCall.queryPoByValid(this);
        IDataset poProductList = UpcViewCall.queryPoproductByPospecNumber(this, param.getString("POSPECNUMBER",""));
        setPoList(poList);
        setPoProductList(poProductList);
        
        setCtrlInfo(ctrlInfo);
        setCondition(getData("cond", true));
        setInfos(dataset);

    }

    /**
     * BBOSS成员签约关系订购反馈结果查询
     * 
     * @author liuxx3
     * @date
     * @param cycle
     * @throws exception
     */

    public void qryBBossMeb(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);

        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataOutput dop = CSViewCall.callPage(this, "CS.BbossQueryBizSVC.qryBBossMeb", param, getPagination("infonav"));

        IDataset dataset = dop.getData();
        setInfoCount(dop.getDataCount());

        IData ctrlInfo = new DataMap();
        if (dataset.size() == 0)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        }
        else
        {
            ctrlInfo.put("strHint", "查询成功！");
        }
        setCtrlInfo(ctrlInfo);
        setCondition(getData("cond", true));
        setInfos(dataset);

    }

    /**
     * BBOSS产品订购反馈结果查询
     * 
     * @author liuxx3
     * @date
     * @param cycle
     * @throws exception
     */

    public void queryBbossTrade(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);

        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataOutput dop = CSViewCall.callPage(this, "CS.BbossQueryBizSVC.qryBBossTradeInfo", param, getPagination("infonav"));

        IDataset dataset = dop.getData();

        IData ctrlInfo = new DataMap();
        if (dataset.size() == 0)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        }
        else
        {
            ctrlInfo.put("strHint", "查询成功！");
        }
        
        IDataset poList = UpcViewCall.queryPoByValid(this);
        IDataset poProductList = UpcViewCall.queryPoproductByPospecNumber(this, param.getString("POSPECNUMBER",""));
        setPoList(poList);
        setPoProductList(poProductList);
        
        setCtrlInfo(ctrlInfo);
        setCondition(getData("cond", true));
        setInfos(dataset);
        setInfoCount(dop.getDataCount());

    }

    public abstract void setCondition(IData condition);

    public abstract void setCtrlInfo(IData ctrlInfo);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfos(IDataset infos);

    public abstract void setSubInfos(IDataset subInfos);
    
    public abstract void setPoList(IDataset poList);
    
    public abstract void setPoProductList(IDataset poProductList);

}
