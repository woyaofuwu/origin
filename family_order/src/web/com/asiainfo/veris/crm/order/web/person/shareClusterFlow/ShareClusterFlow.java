
package com.asiainfo.veris.crm.order.web.person.shareClusterFlow;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ShareClusterFlow extends PersonBasePage
{

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

        CSViewCall.call(this, "SS.ShareClusterFlowSVC.checkAddMeb", pageData);

        pageData.put("START_DATE", SysDateMgr.getSysTime());
        setAjax(pageData);
    }
    /**
     * 副卡申请加入家庭群组时校验主卡信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkAddMain(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        pageData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IData returnDate=new DataMap();
        IDataset rtDataset =CSViewCall.call(this, "SS.ShareClusterFlowSVC.checkAddMain", pageData);
        if(rtDataset!=null&&rtDataset.size()>0){
        	returnDate=rtDataset.getData(0);
        }
        //pageData.put("START_DATE", SysDateMgr.getSysTime());
        
        //setAjax(pageData);
        returnDate.put("SERIAL_NUMBER",pageData.get("SERIAL_NUMBER_B"));
        setAjax(returnDate);
    }

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IData returnDate=new DataMap();
       
//        CSViewCall.call(this, "SS.ShareClusterFlowSVC.check", data);
        IDataset rtDataset=CSViewCall.call(this, "SS.ShareClusterFlowSVC.check", data);
        if(rtDataset!=null&&rtDataset.size()>0){
        	returnDate.put("IS_EXIST", rtDataset.getData(0).getString("IS_EXIST"));
        	returnDate.put("IS_STOP", rtDataset.getData(0).getString("IS_STOP"));
        	returnDate.put("IS_END", rtDataset.getData(0).getString("IS_END"));
        }else {
        	returnDate.put("IS_EXIST", 0);
        	returnDate.put("IS_STOP", 0);
        	returnDate.put("IS_END", 0);
        }
        setCond(returnDate);
        setViceInfos(CSViewCall.call(this, "SS.ShareClusterFlowSVC.queryFamilyMebList", data));
        setDiscntInfos(CSViewCall.call(this, "SS.ShareClusterFlowSVC.queryFamilyDiscntList", data));
    }

    public void loadInfoForcancel(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        data.put("MEMBER_CANCEL", "1");// 副卡操作
        CSViewCall.call(this, "SS.ShareClusterFlowSVC.check", data);
        setViceInfos(CSViewCall.call(this, "SS.ShareClusterFlowSVC.queryFamilyMeb", data));
        IDataset mebList =CSViewCall.call(this, "SS.ShareClusterFlowSVC.queryFamilyDiscntList", data);
        setDiscntInfos(mebList);
        IData returnDate=new DataMap();
        if(mebList!=null&&mebList.size()>0){
        	returnDate.put("MAIN_SERIAL_NUMBER", mebList.getData(0).get("SERIAL_NUMBER"));
        }
        returnDate.put("IS_EXIST", 0);
        setCond(returnDate);

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        pageData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset rtDataset = CSViewCall.call(this, "SS.ShareClusterFlowRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setDiscntInfo(IData discntInfo);

    public abstract void setDiscntInfos(IDataset discntInfos);

    public abstract void setViceInfo(IData viceInfo);

    public abstract void setViceInfos(IDataset viceInfos);
    
    public abstract void setCond(IData cond);
}
