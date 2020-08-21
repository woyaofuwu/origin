
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: CttOrderCancelBeforeTrade.java
 * @Description: 铁通宽带撤单提交前PBoss校验【childCheckBeforeTrade】
 * @version: v1.0.0
 * @author: likai3 Modification History: Date Author Version Description
 *          -------------------------------------------------------* likai3 v1.0.0 修改原因
 */
public class CttOrderCancelBeforeTrade extends BreBase implements IBREScript
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String errorMsg = "";
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// auth组建调用的时候校验
        {

            IData pbossResData = null;
            IData servParam = new DataMap();
            servParam.put("HOME_DOMAIN", "PCRM");
            servParam.put("ORIG_DOMAIN", "PBOS");
            servParam.put("ACTIVITYCODE", "T4022008");
            servParam.put("BIPCODE", "IOM2B008");

            IData svcCont = new DataMap();
            IData reqInfo = new DataMap();
            reqInfo.put("SUBSCRIBE_ID", databus.getString("SUBSCRIBE_ID"));
            svcCont.put("REQUEST_INFO", reqInfo);
            servParam.put("SVC_CONT", svcCont);
            IDataset result = PBossCall.callPBOSS("TCS_CallPbossPf", servParam);
            if (IDataUtil.isEmpty(result))
            {
                errorMsg = "PBOSS未返回或返回超时!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "-1", errorMsg);
                return true;
            }
            else
            {
                if (!"0".equals(result.getData(0).getString("X_RESULTCODE", "")))
                {
                    errorMsg = "PBOSS返回:" + result.getData(0).getString("X_RESULTCODE", "");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "-1", errorMsg);
                    return true;
                }
            }

        }
        return false;
    }
}
