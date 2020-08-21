
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.changepasswd;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 
 */
public abstract class WidenetPswChg extends PersonBasePage
{

    /**
     * 查询宽带资料后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        IData wideInfo = dataset.first();
        if(wideInfo == null){
        	CSViewException.apperr(WidenetException.CRM_WIDENET_1,data.getString("USER_ID"));
        }else{
        	wideInfo.put("WIDE_TYPE",wideInfo.getString("RSRV_STR2"));
        	 setWideInfo(wideInfo);
        }
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        IDataset dataset = CSViewCall.call(this, "SS.WidenetPswChgRegSVC.tradeReg", data);
        setAjax(dataset);

    }

    public abstract void setInfo(IData info);

    public abstract void setWideInfo(IData wideInfo);

}
