
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CheckRestoreBySnTrade.java
 * @Description: 校验 有没有携号开户未完工工单
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-6-26 上午11:16:20 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-6-26 lijm3 v1.0.0 修改原因
 */
public class CheckRestoreBySnTrade extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData dataBus, BreRuleParam arg1) throws Exception
    {

        String sn = dataBus.getString("SERIAL_NUMBER");
        IDataset ids = TradeInfoQry.getMainTradeBySN(sn, "40");
        if (IDataUtil.isNotEmpty(ids))
        {

            BreTipsHelp.addNorTipsInfo(dataBus, BreFactory.TIPS_TYPE_ERROR, -99, "【" + sn + "】有携入申请未完工的工单，不能再申请！");
            return true;
        }
        return false;
    }

}
