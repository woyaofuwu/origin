
package com.asiainfo.veris.crm.order.soa.person.rule.run.svc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TipUserSvcStateChgInfoRule.java
 * @Description: 用户服务状态变更提示信息
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-19 下午4:22:20
 */
public class TipUserSvcStateChgInfoRule extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String productId = databus.getString("PRODUCT_ID");
        String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        IDataset commparaDataset = CommparaInfoQry.getCommpara("CSM", "7636", tradeTypeCode, CSBizBean.getTradeEparchyCode());
        StringBuilder msg = new StringBuilder();
        if (IDataUtil.isNotEmpty(commparaDataset))
        {
            for (int i = 0, size = commparaDataset.size(); i < size; i++)
            {
                IData tempData = commparaDataset.getData(i);
                if (StringUtils.equals(productId, tempData.getString("PARA_CODE1", "")))
                {
                    msg.append(tempData.getString("PARA_CODE24", "")).append("	");
                }
            }
        }
        if (msg.length() > 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 515005, msg.toString());
            return true;
        }
        return false;
    }
}
