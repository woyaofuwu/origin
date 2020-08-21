
package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * @Description: 获取用户服务状态无数据!
 * @version: v1.0.0
 * @author: likai3
 */
public class CheckWidenetUserState extends BreBase implements IBREScript
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
            IData userinfo = UcaInfoQry.qryUserInfoBySn(databus.getString("SERIAL_NUMBER"));
            IDataset datasetsvcstate = UserSvcStateInfoQry.queryUserSvcStateInfo(userinfo.getString("USER_ID"));
            if (IDataUtil.isEmpty(datasetsvcstate))
            {
                return true;
            }
        }
        return false;
    }
}
