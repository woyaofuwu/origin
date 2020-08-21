
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
 * 校验是否已经办理了校园宽带(特殊拆机规则)
 * 
 * @author chenzm
 * @date 2014-07-16
 */
public class CheckExsitsSchoolWidenet extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /**
     * 是否已经办理了宽带判断
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {

        String errorInfo = "";
        String serialNumber = databus.getString("SERIAL_NUMBER");
        String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String tradeType = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber);
        if (IDataUtil.isNotEmpty(widenetInfos))
        {
            String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
            if ("4".equals(wideType))
            {
                errorInfo = "该用户为三亚学院校园宽带用户！不能办理" + tradeType + "业务!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604001", errorInfo);
            }

        }
        else
        {
            errorInfo = "该用户未办理过宽带业务！不能办理" + tradeType + "业务!";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604002", errorInfo);
        }
        return false;

    }

}
