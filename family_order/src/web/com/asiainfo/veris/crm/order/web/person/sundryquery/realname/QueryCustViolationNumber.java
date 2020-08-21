 
package com.asiainfo.veris.crm.order.web.person.sundryquery.realname;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryCustViolationNumber extends PersonQueryPage
{
    /**
     * 全网一证五号违规信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCustViolationNumber(IRequestCycle cycle) throws Exception
    {

        IData cond = getData();  
        cond.put("ID_CARD_TYPE", cond.getString("PSPT_TYPE_CODE", ""));
        cond.put("ID_CARD_NUM", cond.getString("PSPT_ID", ""));
        IDataset results = CSViewCall.call(this, "SS.NationalOpenLimitSVC.queryViolationNum", cond);
        
        setAjax(results);
        setNumberInfos(results);
        setNumberCount(results.size());
    }

    public abstract void setNumberCount(long Count);
    public abstract void setNumberInfos(IDataset infos);
}
