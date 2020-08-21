/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.script.rule.cancel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CancelCheckTime.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-2 上午11:43:27 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-2 chengxf2 v1.0.0 修改原因
 */

public class CancelCheckTime extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CancelCheckTime.class);

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-2 上午11:43:27 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-2 chengxf2 v1.0.0 修改原因
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CancelCheckTime() >>>>>>>>>>>>>>>>>>");

        int iTradeTypeCode = databus.getInt("TRADE_TYPE_CODE");
        if (iTradeTypeCode == 600 || iTradeTypeCode == 610 || iTradeTypeCode == 611 || iTradeTypeCode == 612)
        {
            IData tradeInfo = databus.getData("TRADE_INFO");
            
            String serialNumber = tradeInfo.getString("SERIAL_NUMBER");
            
            if ("KD_".equals(serialNumber.substring(0, 3)))
            {
                serialNumber = serialNumber.substring(3);
            }
            
            //0开头为商务宽带，不需要进行此校验
            if (!serialNumber.startsWith("0"))
            {
                IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
                String userId = userInfo.getString("USER_ID");
                
                //是否有未完工的魔百和工单
                IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("3800", userId, "0");
                if (IDataUtil.isNotEmpty(outDataset))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20160610, "客户有魔百和订购错单，请联系业务支撑后台处理后撤单。");
                    return true;
                }
                
                // 2.是否有购机信息
//                IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
//                if (DataSetUtils.isNotBlank(boxInfos))
//                {
//                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20160611, "客户有魔百和业务，请退订后再撤单，可联系10086退订。");
//                    return true;
//                }
            }
            
        }
        
        return false;
    }

}
