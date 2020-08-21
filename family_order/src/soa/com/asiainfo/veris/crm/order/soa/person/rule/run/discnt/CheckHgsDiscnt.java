package com.asiainfo.veris.crm.order.soa.person.rule.run.discnt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import org.apache.log4j.Logger;

/**
 * 180724海工商校园网宽带BOSS对接需求
 *
 * @date 2018-08-13
 */
public class CheckHgsDiscnt extends BreBase implements IBREScript {
    private static Logger logger = Logger.getLogger(CheckHgsDiscnt.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckHgsDiscnt() >>>>>>>>>>>>>>>>>>");
        }
        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = new DatasetList(databus.getString("TF_B_TRADE_DISCNT"));
        String eparchyCode = databus.getString("EPARCHY_CODE");
        boolean isBefore = false;
        for (int i = 0; i < listTradeDiscnt.size(); i++) {
            IData tradeDiscnt = listTradeDiscnt.getData(i);
            String startDate = tradeDiscnt.getString("START_DATE");
            IDataset comparaList = CommparaInfoQry.getCommpara("CSM","3978", tradeDiscnt.getString("DISCNT_CODE"),eparchyCode);
            if (IDataUtil.isNotEmpty(comparaList) && SysDateMgr.getIntDayByDate(startDate) < 25)
            {
                isBefore = true;
                break;
            }
        }
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckHgsDiscnt() " + isBefore + "<<<<<<<<<<<<<<<<<<<");
        }
        return isBefore;
    }
}
