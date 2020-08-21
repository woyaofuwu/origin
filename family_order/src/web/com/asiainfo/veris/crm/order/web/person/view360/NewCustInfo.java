
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NewCustInfo extends PersonBasePage
{

    /**
     * 客户信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if (data.getString("SHOW_ALL", "").equals("0"))
        {
            data.put(StrUtil.getNotFuzzyKey(), true);
        }
        IData output = CSViewCall.callone(this, "SS.GetUser360ViewSVC.qryCustInfo", data);
        if(IDataUtil.isNotEmpty(output))
        {
            if (output.getString("DEVELOP_STAFF_ID", "").trim().length() < 8)
            {
                output.put("DEVELOP_STAFF_ID", "");
            }
            String isRealName = output.getString("IS_REAL_NAME", "");
            if ((StringUtils.isNotBlank(isRealName) && "0".equals(isRealName))||StringUtils.isBlank(isRealName))
            {
                output.put("IS_REAL_NAME", "非实名");//非实名
            }
            if (StringUtils.isNotBlank(isRealName) && "1".equals(isRealName))
            {
                output.put("IS_REAL_NAME", "是");//实名
            }
        }
        setCustomerInfo(output);

    }

    public abstract void setCond(IData cond);

    public abstract void setCustomerInfo(IData customerInfo);
}
