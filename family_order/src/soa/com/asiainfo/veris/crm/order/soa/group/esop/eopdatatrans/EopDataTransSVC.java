package com.asiainfo.veris.crm.order.soa.group.esop.eopdatatrans;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EopDataTransSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IData eopDataTransOfferData(IData param) throws Exception
    {
        String ibsysid = param.getString("IBSYSID");
        String subIbsysid = param.getString("SUB_IBSYSID");
        String recordNum = param.getString("RECORD_NUM");
        
        return EopDataTransBean.eopDataTransOfferData(ibsysid, subIbsysid, recordNum);
    }
}
