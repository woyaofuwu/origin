
package com.asiainfo.veris.crm.order.web.person.badness.sundryquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @ClassName: BadnessRestoreManage
 * @Description: 不良信息举报回复
 * @author longtian3
 * @version 1.0
 * @created Apr 4, 2014 10:36:11 AM
 */
public abstract class BadnessRestoreManage extends PersonBasePage
{
    public void queryBadnessInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond");
        Pagination page = this.getPagination("badInfoNav");
        data.put("STATE", "00");
        IDataOutput result = CSViewCall.callPage(this, "SS.BadnessQuerySVC.queryBaseBadnessInfo", data, page);
        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "获取待回复不良信息无数据!");
            setTipInfo("获取待回复不良信息无数据!");
        }

        setCount(result.getDataCount());
        setBadInfos(dataset);
    }

    public void restoreBadnessInfos(IRequestCycle cycle) throws Exception
    {
        IData tabData = getData();
        IData data = getData("cond");
        data.putAll(tabData);

        IDataset result = CSViewCall.call(this, "SS.BadnessManageSVC.restoreBadnessInfos", data);
        if (IDataUtil.isNotEmpty(result))
        {
            this.setAjax("ALERT_INFO", result.getData(0).getString("RESULT_INFO"));
        }
    }
    
    public void getServRequestType(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getServRequestType", data);
        setSixthTypeCodes(result);
        setSevenTypeCodes(null);
    }
    
    public void getFourthTypeCodes(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getFourthTypeCodes", data);
        setFourthTypeCodes(result);
        setFifthTypeCodes(null);
        setSixthTypeCodes(null);
        setSevenTypeCodes(null);
    }
    
    public void getFifthTypeCodes(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getFifthTypeCodes", data);
        setFifthTypeCodes(result);
        setSixthTypeCodes(null);
        setSevenTypeCodes(null);
    }
    
    public void getSevenTypeCodes(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        data.put("SERV_REQUEST_TYPE", data.getString("SIXTH_TYPE_CODE"));
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getSevenTypeCodes", data);
        setSevenTypeCodes(result);
    }
    
    public void revcIdCheckBoxOff(IRequestCycle cycle) throws Exception {
    	setFourthTypeCodes(null);
        setFifthTypeCodes(null);
        setSixthTypeCodes(null);
        setSevenTypeCodes(null);
    }
    
    public void revcIdCheckBoxOn(IRequestCycle cycle) throws Exception {
    	IData data = getData();
    	IDataset fourresult = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getFourthTypeCodes", data);
        setFourthTypeCodes(fourresult);
        
        IDataset fifthresult = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getFifthTypeCodes", data);
        setFifthTypeCodes(fifthresult);
        
        IDataset reqTyperesult = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getServRequestType", data);
        setSixthTypeCodes(reqTyperesult);
        
        IDataset sevenresult = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getSevenTypeCodes", data);
        setSevenTypeCodes(sevenresult);
    }

    public abstract void setSixthTypeCodes(IDataset dataset);
    
    public abstract void setFourthTypeCodes(IDataset dataset);
    
    public abstract void setFifthTypeCodes(IDataset dataset);
    
    public abstract void setSevenTypeCodes(IDataset dataset);

    public abstract void setBadInfo(IData data);

    public abstract void setBadInfos(IDataset dataset);

    public abstract void setCount(long count);

    public abstract void setDealInfo(IData data);

    public abstract void setEditInfo(IData data);

    public abstract void setOtherInfo(IData data);

    public abstract void setOtherInfos(IDataset dataset);

    public abstract void setTipInfo(String tip);

}
