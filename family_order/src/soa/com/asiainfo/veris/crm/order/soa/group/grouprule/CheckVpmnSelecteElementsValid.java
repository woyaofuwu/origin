
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class CheckVpmnSelecteElementsValid extends BreBase implements IBREScript
{
    public boolean run(IData databus, BreRuleParam rule) throws Exception
    {
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS"); // 所有选择的元素
        String userId = databus.getString("SELECTED_USER_ID", "-1");
        String userIdA = databus.getString("SELECTED_USER_ID_A", "-1");
        if (StringUtils.isBlank(userElementsStr))
            return false;
        IDataset userElements = new DatasetList(userElementsStr);
        if (IDataUtil.isEmpty(userElements))
            return false;
        String tradeId = databus.getString("TRADE_ID", "");
        if (tradeId.equals(""))
            tradeId = userId;
        int size = userElements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = userElements.getData(i);

            IData ruleElement = new DataMap();
            ruleElement.put("USER_ID_A", userIdA);
            if ("0_1".equals(element.getString("MODIFY_TAG")))
            {
                continue;
            }
            if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE"))) // D： 优惠
            {
                continue;
            }
            else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))// S： 服务
            {
                String svcId = element.getString("ELEMENT_ID");

                if (BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG")) && ("801".equals(svcId) || "862".equals(svcId) || "863".equals(svcId)))
                {
                    String operType = "1";
                    // 校验订购“漫游短号服务”的条件
                    if ("801".equals(svcId))
                    {
                        operType = "2"; // 校验成员是否有校园卡用户和验证短号正确性
                    }
                    try
                    {
                        VpnUnit.validchk801Svc(userId, operType);
                    }
                    catch (Exception e)
                    {
                        err = ErrorMgrUtil.getCharLengthStr(Utility.getBottomException(e).getMessage(), 2000);
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                        return true;
                    }

                }

            }
        }

        return false;
    }
}
