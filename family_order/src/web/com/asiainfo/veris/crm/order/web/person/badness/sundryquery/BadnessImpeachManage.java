
package com.asiainfo.veris.crm.order.web.person.badness.sundryquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @ClassName: BadnessImpeachManage
 * @Description: 不良信息举报处理
 * @author longtian3
 * @version 1.0
 * @created Apr 4, 2014 10:29:36 AM
 */
public abstract class BadnessImpeachManage extends PersonBasePage
{
    public void queryBadnessInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond");
        Pagination page = this.getPagination("badInfoNav");

        data.put("STATE", "01");
        data.put("REPORT_CUST_PROVINCE", "898");
        data.put("BADNESS_INFO_PROVINCE", "898");
        IDataOutput result = CSViewCall.callPage(this, "SS.BadnessQuerySVC.queryBadnessInfoImpeach", data, page);
        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "获取待处理不良信息无数据");
            setTipInfo("获取待处理不良信息无数据!");
        }
        else
        {
            setTipInfo(dataset.getData(0).getString("ALERT_INFO"));
        }
        setCount(result.getDataCount());
        setBadInfos(dataset);
    }

    public abstract void setBadInfo(IData data);

    public abstract void setBadInfos(IDataset dataset);

    public abstract void setCount(long count);

    public abstract void setDealInfo(IData data);

    public abstract void setEditInfo(IData data);

    public abstract void setOtherInfo(IData data);

    public abstract void setOtherInfos(IDataset dataset);

    public abstract void setTipInfo(String tip);

    public void submitBadnessInfos(IRequestCycle cycle) throws Exception
    {
        IData tabData = getData();
        IData data = getData("cond");
        data.putAll(tabData);

        IDataset result = CSViewCall.call(this, "SS.BadnessQuerySVC.submitBadnessInfos", data);

        if (IDataUtil.isNotEmpty(result))
        {
            String alertInfo = result.getData(0).getString("ALERT_INFO");
            if (StringUtils.isNotBlank(alertInfo))
            {
                this.setAjax("ALERT_INFO", alertInfo);
            }
        }
        // queryBadnessInfo(cycle);
    }

    public void tempDeal(IRequestCycle cycle) throws Exception
    {
        IData tabData = getData();
        IData data = getData("cond", true);
        data.putAll(tabData);

        IDataset result = CSViewCall.call(this, "SS.BadnessQuerySVC.tempDeal", data);
        if (IDataUtil.isNotEmpty(result))
        {
            String alertInfo = result.getData(0).getString("ALERT_INFO");
            if (StringUtils.isNotBlank(alertInfo))
            {
                this.setAjax("ALERT_INFO", alertInfo);
            }
        }
    }
    
    public void getServRequestType(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getServRequestType", data);
        setServRequestTypes(result);
        setSevenTypeCodes(null);
    }
    
    public void getFourthTypeCodes(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        data.put("REPORT_TYPE_CODE", data.getString("DEAL_ASSORT"));
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getFourthTypeCodes", data);
        setFourthTypeCodes(result);
        setFifthTypeCodes(null);
        setServRequestTypes(null);
        setSevenTypeCodes(null);
    }
    
    public void getFifthTypeCodes(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getFifthTypeCodes", data);
        setFifthTypeCodes(result);
        setServRequestTypes(null);
        setSevenTypeCodes(null);
    }
    
    public void getSevenTypeCodes(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");
        IDataset result = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getSevenTypeCodes", data);
        setSevenTypeCodes(result);
    }
    
    public void revcIdCheckBoxOff(IRequestCycle cycle) throws Exception {
    	setFourthTypeCodes(null);
        setFifthTypeCodes(null);
        setServRequestTypes(null);
        setSevenTypeCodes(null);
    }
    
    public void revcIdCheckBoxOn(IRequestCycle cycle) throws Exception {
    	IData data = getData();
    	IDataset fourresult = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getFourthTypeCodes", data);
        setFourthTypeCodes(fourresult);
        
        IDataset fifthresult = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getFifthTypeCodes", data);
        setFifthTypeCodes(fifthresult);
        
        IDataset reqTyperesult = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getServRequestType", data);
        setServRequestTypes(reqTyperesult);
        
        IDataset sevenresult = CSViewCall.call(this, "SS.BadInfoComplainDealSVC.getSevenTypeCodes", data);
        setSevenTypeCodes(sevenresult);
    }

    public abstract void setServRequestTypes(IDataset dataset);
    
    public abstract void setFourthTypeCodes(IDataset dataset);
    
    public abstract void setFifthTypeCodes(IDataset dataset);
    
    public abstract void setSevenTypeCodes(IDataset dataset);
}
