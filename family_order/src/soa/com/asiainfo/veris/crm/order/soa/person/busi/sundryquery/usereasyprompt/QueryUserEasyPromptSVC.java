
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.usereasyprompt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUserEasyPromptSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 功能：便捷服务意见查询 作者：GongGuang
     */
    public IDataset queryUserEasyPrompt(IData data) throws Exception
    {
        IData params = new DataMap();
        if ((data.getString("SERIAL_NUMBER1") == null || "".equals(data.getString("SERIAL_NUMBER1"))) && (data.getString("SERIAL_NUMBER2") == null || "".equals(data.getString("SERIAL_NUMBER2"))))
        {
            params.put("SERIAL_NUMBER1", "00000000000");
            params.put("SERIAL_NUMBER2", "99999999999");
        }
        else
        {
            params.put("SERIAL_NUMBER1", data.getString("SERIAL_NUMBER1"));
            params.put("SERIAL_NUMBER2", data.getString("SERIAL_NUMBER2"));
        }

        if ((data.getString("ACCEPT_DATE1") == null || "".equals(data.getString("ACCEPT_DATE1"))) && (data.getString("ACCEPT_DATE2") == null || "".equals(data.getString("ACCEPT_DATE2"))))
        {
            params.put("ACCEPT_DATE1", "1970-01-01");
            params.put("ACCEPT_DATE2", "3000-01-01");
        }
        else
        {
            params.put("ACCEPT_DATE1", data.getString("ACCEPT_DATE1").trim());
            params.put("ACCEPT_DATE2", data.getString("ACCEPT_DATE2").trim());
        }

        if (data.getString("SMS_TYPE_CODE") == null || "".equals(data.getString("SMS_TYPE_CODE")))
        {
            params.put("SMS_TYPE_CODE", "");
        }
        else
        {
            params.put("SMS_TYPE_CODE", data.getString("SMS_TYPE_CODE"));
        }
        QueryUserEasyPromptBean bean = (QueryUserEasyPromptBean) BeanManager.createBean(QueryUserEasyPromptBean.class);
        return bean.queryUserEasyPrompt(params, getPagination());
    }
}
