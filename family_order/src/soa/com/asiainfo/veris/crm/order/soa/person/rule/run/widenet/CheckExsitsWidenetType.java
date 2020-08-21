
package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.biz.util.StaticUtil;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

/**
 * 校验办理的宽带是否是当前宽带业务所对应的宽带
 * 
 * @author chenzm
 * @date 2014-05-29
 */
public class CheckExsitsWidenetType extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {

        String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String userId = databus.getString("USER_ID");
        String errorInfo = "";
        String tradeType = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isEmpty(widenetInfos))
        {
            errorInfo = "该用户尚未开通宽带业务，不能办理" + tradeType + "业务!";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604008", errorInfo);
        }
        else
        {
            String type = widenetInfos.getData(0).getString("RSRV_STR2");
            if ("632".equals(tradeTypeCode) || "633".equals(tradeTypeCode) || "634".equals(tradeTypeCode) || "635".equals(tradeTypeCode) || "9983".equals(tradeTypeCode))
            {
                if (!"4".equals(type))
                {
                    errorInfo = "该用户没办理校园宽带业务,不能办理" + tradeType + "业务!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604009", errorInfo);
                }
            }
            else if ("617".equals(tradeTypeCode) || "619".equals(tradeTypeCode) || "626".equals(tradeTypeCode) || "624".equals(tradeTypeCode) || "622".equals(tradeTypeCode))
            {
                if (!"2".equals(type))
                {
                    errorInfo = "该用户没办理ADSL业务,不能办理" + tradeType + "业务!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604010", errorInfo);
                }
            }
            else if ("618".equals(tradeTypeCode) || "621".equals(tradeTypeCode) || "627".equals(tradeTypeCode) || "625".equals(tradeTypeCode) || "623".equals(tradeTypeCode))
            {
                if (!"3".equals(type))
                {
                    errorInfo = "该用户没办理光纤业务,不能办理" + tradeType + "业务!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604011", errorInfo);
                }
            }
            else if ("603".equals(tradeTypeCode) || "604".equals(tradeTypeCode) || "607".equals(tradeTypeCode) || "606".equals(tradeTypeCode) )// GPON
            {
                if (!"1".equals(type))
                {
                    errorInfo = "该用户没办理Gpon业务,不能办理" + tradeType + "业务!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604012", errorInfo);
                }
            }

        }

        return false;
    }

}
