
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.UpmsQry;

public class ScoreExchangeIntfBean extends CSBizBean
{

    /**
     * 积分商城库存查询（给IVR提供）
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData selectRES(IData input) throws Exception
    {
        String str = input.getString("ITEM_ID");
        if (StringUtils.isEmpty(str))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有传item_id！");
        }
        if ("-".equals(str.substring(str.length() - 1, str.length())))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "多个item_id中，不能有任何一个为空！");
        }
        IData results2 = new DataMap();
        String st[] = str.split("-");
        String SCORE = "";
        for (int i = 0; i < st.length; i++)
        {
            if (st[i] != null && !"".equals(st[i]))
            {
                IDataset results = UpmsQry.queryUpmsGiftInfoByItemId(st[i]);
                if (results != null && results.size() > 0)
                {
                    if (st.length == 1)
                    {
                        results2.put("SCORE", results.getData(0).getString("SCORE"));
                        return results2;
                    }
                    else if (i == 0)
                    {
                        SCORE = results.getData(0).getString("SCORE");
                    }
                    else
                    {
                        SCORE = SCORE + "-" + results.getData(0).getString("SCORE");
                    }
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "编码：" + st[i] + "查不到数据！");
                }
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "多个item_id中，不能有任何一个为空！");
            }

        }

        results2.put("SCORE", SCORE);
        return results2;
    }

}
