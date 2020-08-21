
package com.asiainfo.veris.crm.order.web.person.familytradeoptimal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FamilyShortCodeBusiOptimal extends PersonBasePage
{

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        
        IDataset familyDatas=CSViewCall.call(this, "SS.FamilyCreateSVC.getAllMebByMainSn", pageData);

        setUUInfos(familyDatas);
        
        if(IDataUtil.isNotEmpty(familyDatas)){
        	this.setAjax("VALIDE_MEBMER_NUMBER",String.valueOf(familyDatas.size()));
        }else{
        	this.setAjax("VALIDE_MEBMER_NUMBER","-1");
        }
        
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.FamilyShortCodeBusiRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setUUInfo(IData UUInfo);

    public abstract void setUUInfos(IDataset UUInfos);
}
