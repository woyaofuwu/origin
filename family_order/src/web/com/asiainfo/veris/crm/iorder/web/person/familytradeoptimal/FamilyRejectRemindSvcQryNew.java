
package com.asiainfo.veris.crm.iorder.web.person.familytradeoptimal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 亲亲网拒收提醒短信查询
 * 
 * @author zhouwu
 * @date 2014-06-18 14:32:18
 */
public abstract class FamilyRejectRemindSvcQryNew extends PersonBasePage
{

    /**
     * 拒收提醒短信查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryReject(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtData = CSViewCall.call(this, "SS.FamilyCreateSVC.getRejectRemindInfo", pageData);
        setViceInfos(rtData);
    }

    public abstract void setViceInfo(IData viceInfo);

    public abstract void setViceInfos(IDataset viceInfos);
}
