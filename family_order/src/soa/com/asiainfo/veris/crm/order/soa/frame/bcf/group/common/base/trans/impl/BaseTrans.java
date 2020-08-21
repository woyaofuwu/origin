
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.impl;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.config.RequestTransConfig;

public final class BaseTrans implements ITrans
{

    @Override
    public void transRequestData(IData data) throws Exception
    {

        if ("SMSBUSINESSHANDING".equals(data.getString("BATCH_OPER_TYPE"))&&data.getString("X_TRANS_CODE","").equals("CS.ChangeUserElementSvc.changeUserElement")){
            return;
        }

        List<ITrans> tranList = RequestTransConfig.getRequestTranss(data.getString("X_TRANS_CODE", ""), data);

        if (tranList == null || tranList.size() == 0)
        {
            return;
        }

        for (int i = 0, size = tranList.size(); i < size; i++)
        {
            ITrans iTrans = tranList.get(i);

            // iTrans.checkRequestData(data);//校验
            iTrans.transRequestData(data);// 转换
        }
    }

}
