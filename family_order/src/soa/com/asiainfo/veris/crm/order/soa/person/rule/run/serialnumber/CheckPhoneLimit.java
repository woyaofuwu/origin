
package com.asiainfo.veris.crm.order.soa.person.rule.run.serialnumber;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class CheckPhoneLimit extends BreBase implements IBREScript
{

    /**
     * 宽带开户号段限制
     * 
     * @author chenzm
     * @date 2014-05-29
     */
    private static final long serialVersionUID = -5609469268923189940L;

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String serialNumber = databus.getString("SERIAL_NUMBER");
        IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "456", null, CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(commparaInfos))
        {
            String paraCode1 = commparaInfos.getData(0).getString("PARA_CODE1");
            String tempStr = serialNumber.substring(0, 3);
            if (tempStr.equals(paraCode1))
            {
                String errorInfo = "该宽带业务限制" + tempStr + "号段办理！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604013", errorInfo);

            }
        }
        return false;
    }

}
