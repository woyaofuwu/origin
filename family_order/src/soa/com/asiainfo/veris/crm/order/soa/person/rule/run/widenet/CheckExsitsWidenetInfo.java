package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

/**
 * 校验是否已经办理了宽带
 * 
 * @author chenzm
 * @date 2014-05-23
 */
public class CheckExsitsWidenetInfo extends BreBase implements IBREScript {

    private static final long serialVersionUID = 1L;

    /**
     * 是否已经办理了宽带判断
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception {

        String errorInfo = "";
        String serialNumber = databus.getString("SERIAL_NUMBER");
        // 中小企业商务宽带开户不走该规则
        String ecUserId = databus.getString("EC_USER_ID");
        String ecserialNumber = databus.getString("EC_SERIAL_NUMBER");
        if (StringUtils.isNotBlank(ecUserId) && StringUtils.isNotBlank(ecserialNumber)) { // 中小企业商务宽带开户不走该规则
            return false;
        }
        IData widenetInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
        if (IDataUtil.isNotEmpty(widenetInfo)) {// 已办理宽带

            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber);
            if (IDataUtil.isNotEmpty(widenetInfos)) {
                String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
                if ("1".equals(wideType)) {
                    errorInfo = "当前号码已办理过FTTB宽带开户";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604003", errorInfo);
                } else if ("2".equals(wideType)) {
                    errorInfo = "当前号码已办理过ADSL宽带开户";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", errorInfo);
                } else if ("5".equals(wideType)) {
                    errorInfo = "当前号码已办理过铁通FTTH宽带开户";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", errorInfo);
                } else if ("6".equals(wideType)) {
                    errorInfo = "当前号码已办理过铁通FTTB宽带开户";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", errorInfo);
                } else if ("3".equals(wideType)) {
                    errorInfo = "当前号码已办理过光纤宽带开户";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604005", errorInfo);
                } else if ("4".equals(wideType)) {
                    errorInfo = "当前号码已办理过校园宽带开户";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604006", errorInfo);
                }

            } else {
                errorInfo = "当前号码已办理过宽带开户";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604007", errorInfo);
            }

        }
        return false;
    }

}
