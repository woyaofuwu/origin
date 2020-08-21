
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangeproduct.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

/**
 * 修改台帐资料，记录速率
 */
public class AddSpeedRate implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        List svcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        if (svcTradeDatas == null || svcTradeDatas.size() == 0)
        {
            return;
        }
        for (int i = 0; i < svcTradeDatas.size(); i++)
        {
            SvcTradeData svcTradeData = (SvcTradeData) svcTradeDatas.get(i);
            if (StringUtils.equals(svcTradeData.getModifyTag(), "0"))
            {
                int speedrate = 0;
                String mainTag = svcTradeData.getMainTag();
                String elementId = svcTradeData.getElementId();
                if (!"1".equals(mainTag) && !"501".equals(elementId))
                {// 续费不处理宽带主体服务
                    IDataset commparas = ParamInfoQry.getCommparaByCode("CSM", "1127", elementId, CSBizBean.getTradeEparchyCode());
                    if (commparas.size() == 0)
                    {
                        CSAppException.apperr(ParamException.CRM_PARAM_503, elementId);
                    }
                    speedrate = Integer.parseInt(commparas.getData(0).getString("PARA_CODE1", "0"));
                    svcTradeData.setRsrvNum1(String.valueOf(speedrate));
                }
            }

        }
    }

}
