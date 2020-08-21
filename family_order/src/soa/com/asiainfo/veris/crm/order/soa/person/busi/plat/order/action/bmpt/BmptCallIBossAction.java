
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.bmpt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class BmptCallIBossAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        if (PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode()) && !"6".equals(CSBizService.getVisit().getInModeCode()))
        {
            IDataset results = IBossCall.callBmptAuth(CSBizService.getVisit(), uca.getSerialNumber());
            if (results != null && results.size() > 0)
            {
                IData result = results.getData(0);
                if (!"0".equals(result.getString("X_RSPTYPE")) || !"0000".equals(result.getString("X_RSPCODE")))
                {
                    CSAppException.apperr(PlatException.CRM_PLAT_68, result.getString("X_RSPDESC"));
                }
                else
                {
                    if ("01".equals(result.getString("USER_STATUS")))
                    {
                        CSAppException.apperr(PlatException.CRM_PLAT_69);
                    }
                    else if ("99".equals(result.getString("USER_STATUS")))
                    {
                        CSAppException.apperr(PlatException.CRM_PLAT_70);
                    }
                }
            }
        }

    }

}
