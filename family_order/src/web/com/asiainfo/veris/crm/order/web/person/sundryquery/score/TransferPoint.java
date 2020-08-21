package com.asiainfo.veris.crm.order.web.person.sundryquery.score; 



import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class TransferPoint extends PersonBasePage
{
    static transient final Logger logger = Logger.getLogger(TransferPoint.class);



    public abstract void setCond(IData cond);

    public void submitProcess(IRequestCycle cycle) throws Exception
    {
        //IData pageData = getData("cond", true);
    	IData pageData = getData();
        String L_MOBILE = pageData.getString("L_MOBILE", "");// 手机号码
        String B_MOBILE = pageData.getString("B_MOBILE", "");// 名单级别
        //String state = pageData.getString("STATE", "");// 状态
   


        IDataset infos = CSViewCall.call(this, "SS.QueryScoreTransferorSVC.TransferorPoint", pageData);

        setCond(pageData);
        setAjax(infos.getData(0));
    }
}
