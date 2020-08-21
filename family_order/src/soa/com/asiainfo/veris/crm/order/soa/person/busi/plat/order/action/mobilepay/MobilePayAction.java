
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.mobilepay;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

public class MobilePayAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatReqData rq = (PlatReqData) btd.getRD();
        String inModeCode = CSBizBean.getVisit().getInModeCode();

        // BOSS侧主动注销直接调接口处理，且不走 PF
        if (!"6".equals(inModeCode)   && PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode()))
        {
            IDataset resultList = IBossCall.callMobilePayCancel(CSBizBean.getVisit(), uca.getSerialNumber());
            if (resultList != null && !resultList.isEmpty())
            {
                IData result = resultList.getData(0);
                if (!("0000".equals(result.getString("X_RSPCODE")) || "2001".equals(result.getString("X_RSPCODE")) || "2024".equals(result.getString("X_RSPCODE")) || "4005".equals(result.getString("X_RSPCODE"))))
                {// //2024 4005
                    CSAppException.apperr(PlatException.CRM_PLAT_52, "手机支付业务销户/过户鉴权失败！<br>" + result.getString("X_RSPDESC"));
                }
            }

           // pstd.setIsNeedPf("0");  注销后，需要发送给平台，不能在代码写死不走服开，可通过规则表配置
//            btd.add(uca.getSerialNumber(), pstd);
        }

        if (PlatConstants.OPER_ORDER.equals(pstd.getOperCode()))
        {
            if (!"0".equals(uca.getUser().getAcctTag()))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "该用户为未激活状态");
            }

            if (!"6".equals(inModeCode) && !rq.isSync())
            {
                // 非托收
                if (!"1".equals(uca.getAccount().getPayModeCode()))
                {
                    long balance = Long.valueOf(uca.getAcctBlance());
                    if (balance <= 0)
                    {
                        CSAppException.apperr(PlatException.CRM_PLAT_0999_5, "余额不足");
                    }
                }

            }

        }
    }

}
