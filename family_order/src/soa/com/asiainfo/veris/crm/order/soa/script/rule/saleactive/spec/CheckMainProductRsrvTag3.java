
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

/**
 * 69900817 产品用 td_bre_parameter 入参 rsrv_tag3 目前只用 R
 * 
 * @author Mr.Z
 */
public class CheckMainProductRsrvTag3 extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -670170477970481941L;

    private static Logger logger = Logger.getLogger(CheckMainProductRsrvTag3.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckMainProductRsrvTag3() >>>>>>>>>>>>>>>>>>");
        }

        String userMainProductId = databus.getString("USER_PRODUCT_ID");
        String rsrvTag3Temp = ruleParam.getString(databus, "RSRV_TAG3");

        String rsrvTag3 = rsrvTag3Temp == null ? "" : rsrvTag3Temp;

        IData cha = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PRODUCT, userMainProductId, "TD_B_PRODUCT").getData(0);
        if (!rsrvTag3.equals(cha.getString("RSRV_TAG3")))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20047, "用户办理的主套餐不满足要求，不能办理此活动！");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckMainProductRsrvTag3() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
