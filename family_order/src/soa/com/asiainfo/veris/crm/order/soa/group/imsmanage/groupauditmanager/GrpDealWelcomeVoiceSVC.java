
package com.asiainfo.veris.crm.order.soa.group.imsmanage.groupauditmanager;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class GrpDealWelcomeVoiceSVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 融合总机欢迎词查询
     * 
     * @data 2014-7-4
     * @return
     * @throws Exception
     */
    public static IDataset QryGrpAuditWelcomeVoice(IData inData) throws Exception
    {
        return IBossCall.QryGrpAuditWelcomeVoice(inData);
    }

    /**
     * 融合总机欢迎词审核
     * 
     * @data 2014-7-4
     * @return
     * @throws Exception
     */
    public static IDataset DealGrpAuditWelcomeVoice(IData inData) throws Exception
    {
        return IBossCall.DealGrpAuditWelcomeVoice(inData);
    }
}
