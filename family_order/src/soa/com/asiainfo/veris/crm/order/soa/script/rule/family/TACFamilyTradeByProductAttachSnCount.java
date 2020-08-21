
package com.asiainfo.veris.crm.order.soa.script.rule.family;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TACFamilyTradeByProductAttachSnCount extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TACFamilyTradeByProductAttachSnCount.class);

    /**
     * 判断亲情产品能选择的附卡数量
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistCustName() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        int strFamilyCount = databus.getInt("RSRV_STR3", 0);

        /* 开始逻辑规则校验 */
        IDataset listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 1010, "FAMILYCN", databus.getString("PRODUCT_ID"), databus.getString("EPARCHY_CODE"));

        if (listCommpara.size() == 1 && listCommpara.getData(0).getInt("PARA_CODE2") != 0 && strFamilyCount > listCommpara.getData(0).getInt("PARA_CODE2"))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201121, "业务登记后条件判断:副卡超过限制数[" + listCommpara.getData(0).getInt("PARA_CODE2") + "]个！");
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TACFamilyTradeByProductAttachSnCount() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
