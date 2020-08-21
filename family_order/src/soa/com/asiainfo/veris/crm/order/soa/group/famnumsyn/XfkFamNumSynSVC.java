
package com.asiainfo.veris.crm.order.soa.group.famnumsyn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class XfkFamNumSynSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData param) throws Exception
    {
        XfkFamNumSynBean bean = new XfkFamNumSynBean();

        return bean.crtTrade(param);
    }


    /**
     * 2.3.正向学护卡成员业务办理校验接口
     *
     * @data 2014-7-4
     * @return
     * @throws Exception
     */
    public IDataset qryXfkNumIsExists(IData inData) throws Exception
    {
        return IBossCall.QryXfkNumIsExists(inData);
    }

}
