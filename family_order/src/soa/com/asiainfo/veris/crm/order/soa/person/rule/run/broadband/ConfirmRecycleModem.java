
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * @Description: 校验校验拆机时是否有设备要进行回收
 * @version: v1.0.0
 * @author: likai3
 */
public class ConfirmRecycleModem extends BreBase implements IBREScript
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

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// auth组建调用的时候校验
        {
            IDataset userResDataset = UserResInfoQry.getUserResInfoByUserId(databus.getString("USER_ID"));
            if (userResDataset != null && userResDataset.size() > 0)
            {
                for (int i = 0; i < userResDataset.size(); i++)
                {
                    String resTypeCode = userResDataset.getData(i).getString("RES_TYPE_CODE", "");
                    String resKindCode = userResDataset.getData(i).getString("RSRV_STR4", "");
                    String modemUseType = userResDataset.getData(i).getString("RSRV_TAG1", ""); // modem使用方式

                    if ("W".equals(resTypeCode) && "03".equals(resKindCode) && "2".equals(modemUseType)) // modem使用方式2：租用，需要回收设备
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
