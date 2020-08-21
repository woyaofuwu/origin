
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 */
public class CheckChooseNetWork extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /**
     * 用户激活状态判断
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String errorMsg = "";
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        IData resData = new DataMap();
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// auth组建查询时调用
        {
            String userId = databus.getString("USER_ID");
            IDataset resInfos = UserResInfoQry.getUserResInfoByUserId(userId);
            if (IDataUtil.isNull(resInfos))
            {
                errorMsg = "获取用户资源资料无数据!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "3771", errorMsg);
                // return false;
            }
            for (int i = 0; i < resInfos.size(); i++)
            {

                String resTypeCode = resInfos.getData(i).getString("RES_TYPE_CODE");

                if (resTypeCode.equals("1"))
                {
                    resData = resInfos.getData(i);
                    break;
                }
            }
            if (resData.getString("IMSI").compareTo("46007") > 0)
            {

                errorMsg = "TD用户（IMSI为46007）,不能办理该业务!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "3772", errorMsg);
                // return false;
            }
        }

        return false;
    }

}
