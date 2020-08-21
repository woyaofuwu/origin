
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trademgr.QuerySnPaymentInfoBean;

public class PayMentInfoSVC extends CSBizService
{
    /**
     * 查询用户手机缴费通使用情况
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @author:chenzg
     * @date:2010-9-9
     */

    public IData queryUseSnPaymentInfo(IData data) throws Exception
    {

        IData returnMap = null;

        QuerySnPaymentInfoBean bean = BeanManager.createBean(QuerySnPaymentInfoBean.class);

        // -------判断必传参数--------
        IDataUtil.chkParam(data, "SERIAL_NUMBER");

        // 查询用户手机缴费通使用情况

        IDataset ds = bean.querySnUsePaymentInfo(data);

        if (ds != null && ds.size() > 0)
        {

            returnMap = ds.getData(0);

        }
        else
        {

            returnMap = new DataMap();

        }

        return returnMap;

    }

}
