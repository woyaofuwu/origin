
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class ChkVpmn3037Valid extends BreBase implements IBREScript
{
    /**
     * 成员Vpmn退订，tradetypecode=3037的元素操作规则
     */
    private static final long serialVersionUID = -245534769209563115L;

    public boolean run(IData databus, BreRuleParam rule) throws Exception
    {
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userId = databus.getString("USER_ID_B", "-1");
        String userIdA = databus.getString("USER_ID", "-1");

        String strSpecDiscnt = "1285,1286,1391,";

        IDataset grpMemOrderDisInfos = UserDiscntInfoQry.getUserDiscntInfoByUserIdAB(userId, userIdA); // 成员订购的集团产品优惠

        // 1.如果已有358优惠，且存在个人资费4020、4021、4022；则如果新增的优惠不是（1285，1286，1391和662）的话，报依赖互斥错误 start
        // 增加358优惠变更规则判断：358优惠变更到普通优惠的时候，需要判断是或存在资费，存在资费不允许变更
        String discnt358 = "";
        if (IDataUtil.isNotEmpty(grpMemOrderDisInfos))
        {
            // 判断是否已订购3，5，8资费
            boolean isfound = false;
            for (int i = 0; i < grpMemOrderDisInfos.size(); i++)
            {
                IData map = grpMemOrderDisInfos.getData(i);
                String discntCode = map.getString("DISCNT_CODE", "");
                if (strSpecDiscnt.indexOf(discntCode + ",") >= 0)
                {
                    discnt358 = discntCode;
                    isfound = true;
                    break;
                }
            }

            // 判断是否存在个人资费4020、4021、4022
            String discntPerson = "";
            boolean ifpersondis = false;
            if (isfound)
            { // 如果有358优惠，则查是否存在个人资费4020、4021、4022

                IDataset memOrderDisInfos = UserDiscntInfoQry.getUserDiscntInfoByUserIdAB(userId, "-1"); // 成员订购的个人优惠
                if (IDataUtil.isNotEmpty(memOrderDisInfos))
                {
                    for (int k = 0; k < memOrderDisInfos.size(); k++)
                    {
                        IData discntinfo = memOrderDisInfos.getData(k);
                        String discntscode = discntinfo.getString("DISCNT_CODE");
                        if ("4021".equals(discntscode) || "4022".equals(discntscode) || "4020".equals(discntscode))
                        {
                            String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discnt358);
                            String discntNamePerson = UDiscntInfoQry.getDiscntNameByDiscntCode(discntscode);
                            err = "产品依赖互斥判断:优惠[" + discnt358 + "|" + discntName + "]不能删除, 因为它被用户的另一个优惠[" + discntscode + "|" + discntNamePerson + "]所依赖， 业务不能继续办理!";

                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                            return true;

                        }
                    }
                }

            }
        }
        // 1.如果有358优惠，且存在个人资费4020、4021、4022；则如果新增的优惠不是（1285，1286，1391和662）的话，报依赖互斥错误end

        return false;
    }

}
