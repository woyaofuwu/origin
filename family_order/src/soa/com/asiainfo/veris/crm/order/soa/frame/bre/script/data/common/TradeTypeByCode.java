
package com.asiainfo.veris.crm.order.soa.frame.bre.script.data.common;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TradeTypeByCode extends BreBase implements IBREDataPrepare
{

    private static final Logger logger = Logger.getLogger(TradeTypeByCode.class);

    /**
     * 获取业务类型配置信息
     */
    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TradeTypeByCode() >>>>>>>>>>>>>>>>>>");

        /* 获取业务台账类型配置表信息 */
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", databus.getString("TRADE_TYPE_CODE"));
        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        IData dataTypeCode = UTradeTypeInfoQry.getTradeType(databus.getString("TRADE_TYPE_CODE"), CSBizBean.getUserEparchyCode());

        if (IDataUtil.isEmpty(dataTypeCode))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 666666, "加载业务参数信息出错！");
        }
        
        IDataset listTypeCode = IDataUtil.idToIds(dataTypeCode);
        databus.put("TD_S_TRADETYPE", listTypeCode);

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TradeTypeByCode() <<<<<<<<<<<<<<<<<<<");
    }

}
