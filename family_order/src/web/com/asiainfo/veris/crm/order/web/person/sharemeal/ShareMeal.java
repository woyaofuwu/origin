
package com.asiainfo.veris.crm.order.web.person.sharemeal;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ShareMeal extends PersonBasePage
{
	private final static Logger logger = Logger.getLogger(ShareMeal.class);
    /**
     * 添加成员时校验成员号码
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkAddMeb(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        pageData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset ds = new DatasetList();
        //logger.info("前="+pageData);
        ds = CSViewCall.call(this, "SS.ShareMealSVC.checkAddMeb", pageData);
        
        //pageData.put("START_DATE", SysDateMgr.getSysTime());
        
        ds.getData(0).put("START_DATE", SysDateMgr.getSysTime());
        //logger.info("后="+ds.getData(0));
        setAjax(ds.getData(0));
    }

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        CSViewCall.call(this, "SS.ShareMealSVC.check", data);
        setViceInfos(CSViewCall.call(this, "SS.ShareMealSVC.queryFamilyMebList", data));
        setDiscntInfos(CSViewCall.call(this, "SS.ShareMealSVC.queryFamilyDiscntList", data));
    }

    public void loadInfoForcancel(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        data.put("MEMBER_CANCEL", "1");// 用户副卡取消用
        CSViewCall.call(this, "SS.ShareMealSVC.check", data);
        setViceInfos(CSViewCall.call(this, "SS.ShareMealSVC.queryFamilyMeb", data));

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        pageData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset rtDataset = CSViewCall.call(this, "SS.ShareMealRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setDiscntInfo(IData discntInfo);

    public abstract void setDiscntInfos(IDataset discntInfos);

    public abstract void setViceInfo(IData viceInfo);

    public abstract void setViceInfos(IDataset viceInfos);
}
