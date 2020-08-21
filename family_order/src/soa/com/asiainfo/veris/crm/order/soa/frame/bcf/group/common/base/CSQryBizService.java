
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.proxy.TransProxy;

public class CSQryBizService extends CSBizService
{
    // 给查询接口转换路由用
    public void setTrans(IData input) throws Exception
    {
        String svc = this.getVisit().getXTransCode();

        transGroupOrderBiz(svc, input);
    }

    // 处理查询的trans 翻译,主要用于接口数据转换,不支持路由
    public void transGroupOrderBiz(String svcName, IData indata) throws Exception
    {
        indata.put("X_TRANS_CODE", svcName);

        ITrans iTrans = TransProxy.getInstance(indata);

        iTrans.transRequestData(indata);
    }
}
