
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class CheckVgpoPayRelaAdv extends BreBase implements IBREScript
{

    private static final long serialVersionUID = -2645537682215455597L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // 校验成员
        String sn = databus.getString("SERIAL_NUMBER");// 成员手机号码
        String userId = databus.getString("USER_ID");// 成员用户标识
        String acctId = databus.getString("ACCT_ID");// 成员账户标识
        String operType = databus.getString("OPER_TYPE");

        boolean result = false;

        IData idata = RouteInfoQry.getMofficeInfoBySn(sn);
        if (IDataUtil.isEmpty(idata))
        {// common.error("341412", "成员号码非移动号码不存在，业务不能继续！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "341412", "成员号码非移动号码不存在，业务不能继续");
            return result;
        }

        IData info = new DataMap();
        info.put("USER_ID", userId);// 成员用户id

        if ("1".equals(operType))
        {
            IDataset purchase = UserSaleActiveInfoQry.queryPurchaseInfo(info);

            if (IDataUtil.isNotEmpty(purchase))
            {
                for (int i = 0, size = purchase.size(); i < size; i++)
                {
                    IData datainfo = purchase.getData(i);
                    IDataset comInfos = CommparaInfoQry.getCommparaAllColByParser("CSM", "9987", datainfo.getString("PRODUCT_ID"), "0898");
                    if (IDataUtil.isNotEmpty(comInfos))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "233124", "该用户已办理过不能取消统一付费的约定消费购机活动!");
                        return result;
                    }
                }
            }
        }

        IData payrelationInfo = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
        if (IDataUtil.isEmpty(payrelationInfo))
        {// common.error("233124", "获取用户普通付费关系无记录!!");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "233124", "获取用户普通付费关系无记录!!");
            return result;
        }

        IDataset relationUUInfos = RelaUUInfoQry.queryRelationInfo(userId);
        // common.error("31424", "该用户是一卡双号副卡或一卡付多号并已和主卡绑定付费，不能再办理集团代付!");
        if (IDataUtil.isNotEmpty(relationUUInfos))
        {
            IData relationInfo = (IData) relationUUInfos.get(0);
            String userIdA = relationInfo.getString("USER_ID_A");
            IData grpPayRelaInfo = UcaInfoQry.qryDefaultPayRelaByUserId(userIdA);
            if (IDataUtil.isNotEmpty(grpPayRelaInfo))
            {
                String acctIda = grpPayRelaInfo.getString("ACCT_ID");
                if (acctIda.equals(acctId))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "31424", "该用户是一卡双号副卡或一卡付多号并已和主卡绑定付费，不能再办理集团代付!");
                    return result;
                }

            }
        }

        return true;
    }

}
