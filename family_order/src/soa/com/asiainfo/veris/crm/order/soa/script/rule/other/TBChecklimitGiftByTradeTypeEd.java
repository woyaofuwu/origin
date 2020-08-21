
package com.asiainfo.veris.crm.order.soa.script.rule.other;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TBChecklimitGiftByTradeTypeEd extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimitGiftByTradeTypeEd.class);

    /**
     * Copyright: Copyright 2014 Asiainfo-Linkage
     * 
     * @Description: 已经受理过礼包业务的不能继续办理【TradeCheckBefore】
     * @author: xiaocl
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitGiftByTradeTypeEd() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            IData param = new DataMap();
            param.clear();
            param.put("USER_ID", databus.getString("USER_ID"));
            param.put("SERVICE_MODE", "A5");
            param.put("PROCESS_TAG", "0");
            IDataset list = Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_SRVMODE_HAIN2", param);
            if (IDataUtil.isNotEmpty(list))
            {
                StringBuilder strb = new StringBuilder("业务登记前条件判断：您已经办理了礼包业务【").append(list.get(0, "PROCESS_INFO")).append("】，不能再办理优惠购机业务！");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751034, strb.toString());

            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitGiftByTradeTypeEd() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
