
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class SelectDiscnt07320001 extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(SelectDiscnt07320001.class);

    /**
     * 
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SelectDiscnt07320001() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        IData param = new DataMap();
        param.put("USER_ID", databus.getString("USER_ID_B"));
        param.put("DISCNT_CODE", "50483006");

        if (Dao.qryByRecordCount("TD_S_CPARAM", "ExistsUserDiscnt", param))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 821218, "尊敬的客户，请先取消集团2元包干的优惠再退订V网集团!");
        }

        param.put("RELATION_TYPE_CODE", "25");
        param.put("ROLE_CODE_B", "*");
        if (Dao.qryByRecordCount("TD_S_CPARAM", "ExistsRelationUU", param))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 821218, "尊敬的客户，您有移动总机的业务请先退出移动总机集团再退订V网集团!");
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SelectDiscnt07320001() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
