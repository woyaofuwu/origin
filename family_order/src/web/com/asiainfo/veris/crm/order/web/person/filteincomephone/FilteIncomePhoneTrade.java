/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.filteincomephone;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-4-25 修改历史 Revision 2014-4-25 上午09:32:59
 */
public abstract class FilteIncomePhoneTrade extends PersonBasePage
{

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();

        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));

        IDataset results = CSViewCall.call(this, "SS.FilteIncomePhoneSVC.getFilteIncomePhoneTradeInfo", userInfo);

        IData result = results.getData(0);

        this.setCommInfo(result);

        this.setInfos(result.getDataset("FILTERPHONEDS"));

        this.setAjax(result);

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    /**
     * @param cycle
     * @throws Exception
     * @CREATE BY GONGP@2014-4-25
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        if (StringUtils.isBlank(data.getString("SERIAL_NUMBER")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        String operType = data.getString("OPER_TYPE");

        if ("1".equals(operType) || "2".equals(operType))
        {

            setAjax(CSViewCall.call(this, "SS.FilteIncomePhoneDelTradeRegSVC.tradeReg", data));

        }
        else if ("0".equals(operType))
        {

            setAjax(CSViewCall.call(this, "SS.FilteIncomePhoneAddTradeRegSVC.tradeReg", data));
        }
        else
        {

        }
    }

    public abstract void setCommInfo(IData info);// 

    public abstract void setInfos(IDataset infos);// 

}
