
package com.asiainfo.veris.crm.order.soa.person.rule.run.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class checkDemolishTelePhone extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // tag ： 0--业务受理时子类业务规则校验 1--业务提交时子类业务规则校验

        String userId = databus.getString("USER_ID");
        // 判断是否千群百号代码号
        IDataset userUUDataset2 = RelaUUInfoQry.getRelaUUInfoByUserIda(userId, "T1", null);
        if (userUUDataset2 != null && userUUDataset2.size() > 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "当前用户是千群百号代表号码用户，请先修改用户代表号码，才能办理业务");
            return false;
        }

        // 判断用户账户付费关系
        String cycId = SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMM) + "01";
        String acctId = databus.getString("ACCT_ID");
        IDataset userPayRelationDataset = PayRelaInfoQry.queryUserPayRelation(acctId, cycId);
        if (userPayRelationDataset != null && userPayRelationDataset.size() > 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "当前用户账户在为其他用户提供付费服务，请在【固话普通付费关系变更】界面进行分账操作后，才能办理业务");
            return false;
        }

        // 确认已回收设备
        IDataset userResDataset = UserResInfoQry.getUserResInfoByUserId(userId);
        if (userResDataset != null && userResDataset.size() > 0)
        {
            for (int i = 0; i < userResDataset.size(); i++)
            {
                String resTypeCode = userResDataset.getData(i).getString("RES_TYPE_CODE", "");
                String resKindCode = userResDataset.getData(i).getString("RSRV_STR4", "");

                if ("W".equals(resTypeCode) && "04".equals(resKindCode)) // 计费器
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户还有计费器设备需要回收，请在【设备回收界面】回收后，才能办理该业务");
                    return false;
                }
            }
        }

        // 确认已缴纳保证金信息
        String msg = "请确认用户已缴纳拆机保证金，缴费拆机保证金后才能办理业务";
        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, -1, msg.toString());

        return true;
    }

}
