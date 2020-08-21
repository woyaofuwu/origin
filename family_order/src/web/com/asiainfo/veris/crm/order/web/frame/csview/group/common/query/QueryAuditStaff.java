
package com.asiainfo.veris.crm.order.web.frame.csview.group.common.query;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class QueryAuditStaff extends CSBasePage
{
    public abstract IData getCond();

    public abstract void setCond(IData cond);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);

    public abstract void setHintInfo(String hintInfo);

    /**
     * 页面初始化
     * @param cycle
     * @throws Exception
     * @author chenzg
     * @date 2018-7-10
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    /**
     * 查询稽核人员信息
     * @param cycle
     * @throws Exception
     * @author chenzg
     * @date 2018-7-10
     */
    public void qryAuditStaffInfos(IRequestCycle cycle) throws Exception
    {
    	String staffId = this.getVisit().getStaffId();
        IData param = getData("cond", true);
        IDataOutput output = CSViewCall.callPage(this, "SS.AuditStaffInfoQrySVC.qryAuditStaffInfo", param, getPagination("pageNav"));
        IDataset staffs = output.getData();
        if (IDataUtil.isEmpty(staffs))
        {
            setHintInfo("没有查询到符合条件的VPMN集团信息~~!");
        }
        //过滤掉自己
        for(int i=0;i<staffs.size();i++){
        	IData each = staffs.getData(i);
        	if(staffId.equals(each.getString("STAFF_ID", ""))){
        		staffs.remove(i);
        		i--;
        	}
        }
        setInfos(staffs);
        setCond(this.getData());
        setPageCounts(staffs.size());
    }
}
