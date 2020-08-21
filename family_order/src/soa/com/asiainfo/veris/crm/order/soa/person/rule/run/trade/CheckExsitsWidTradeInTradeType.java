
package com.asiainfo.veris.crm.order.soa.person.rule.run.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckExsitsWidTradeInTradeType.java
 * @Description: 校验是否有宽带未完工单，且trade_type_code in ('600', '611', '612', '613')
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-3 下午4:59:46
 */
public class CheckExsitsWidTradeInTradeType extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String serialNumber = databus.getString("SERIAL_NUMBER");
        //修改code_code语句
        IDataset tradeInfos = TradeInfoQry.getWindTradeInfo("KD_" + serialNumber);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            String tradeTypeCode = tradeInfos.getData(0).getString("TRADE_TYPE_CODE");
            IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(tradeTypeCode, CSBizBean.getTradeEparchyCode());
            String tradeType = tradeTypeInfo.getString("TRADE_TYPE");
            StringBuilder errorMsg = new StringBuilder(100);
            errorMsg.append("该手机用户有").append(tradeType).append("宽带未完工工单，不给予办理此业务！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "", errorMsg.toString());
            return true;
        }

        return false;
    }

}
