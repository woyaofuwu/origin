
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.userconn.UserConnQry;

/**
 * @Description: 校验校验拆机时是否有 固话共线，是否确认要移机？继续将解除共线关系
 * @version: v1.0.0
 * @author: likai3
 */
public class ConfirmUserConnection extends BreBase implements IBREScript
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// auth组建调用的时候校验
        {
            IDataset dataset = UserConnQry.getConnInfosByIdA(databus.getString("USER_ID"), "BK");
            if (IDataUtil.isNotEmpty(dataset))
            {
                // String msg = "宽带【"+td.getSerialNumber()+"】与固话共线，是否确认要移机？继续将解除共线关系";
                return true;
            }
        }
        return false;
    }
}
