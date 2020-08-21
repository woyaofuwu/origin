
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttnetopen;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @Description: 该类的功能描述
 * @version: v1.0.0
 */

public abstract class CttBroadBandOpen extends PersonBasePage
{

    public void checkBroadbandOpenFree(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        IDataset productFeeList = CSViewCall.call(this, "SS.CttBroadBandOpenSVC.checkBroadbandOpenFree", data);
        if (IDataUtil.isNotEmpty(productFeeList))
        {
            setAjax(productFeeList.getData(0));
        }
    }

    /**
     * 宽带服务变更业务类提交默认方法
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.CttBroadBandOpenRegSVC.tradeReg", data);
        setAjax(dataset);
    }
}
