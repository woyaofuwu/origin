
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CheckTDUserInfoDISCNTInfoRule extends BreBase implements IBREScript
{

    /**
     * 
     */
    private static final long serialVersionUID = 3118612922634871859L;

    private static Logger logger = Logger.getLogger(CheckTDUserInfoDISCNTInfoRule.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckTDUserInfoDISCNTInfoRule() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;

        /* 总线相关信息包括台账信息资料信息获取 区域 */
        String user_id = databus.getString("USER_ID_B");// 成员用户标识

        String err = "";

        IDataset userVoicdeInfo = UserSvcInfoQry.queryUserSvcByUserId(user_id, "0", databus.getString("EPARCHY_CODE"));
        if (userVoicdeInfo.size() <= 0)
        {

            err = "用户未开通语音通话功能，不能加入集团！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 50101, err.toString());
            return bResult;
        }
        IDataset discntDataset = UserDiscntInfoQry.getAllValidDiscntByUserId(user_id);

        boolean flag = false;
        if (IDataUtil.isNotEmpty(discntDataset))
        {
            for (int i = 0; i < discntDataset.size(); i++)
            {
                IData discnt = (IData) discntDataset.get(i);
                //String discnt_code = discnt.getString("DISCNT_CODE");
                String product_id = discnt.getString("PRODUCT_ID");
                /*if ("9433".equals(discnt_code) || "9434".equals(discnt_code) || "5920".equals(discnt_code) || ("1261".equals(discnt_code)))
                {
                    flag = true;
                    break;
                }*/
                if ("10009433".equals(product_id))
                {
                    flag = true;
                    break;
                }
            }
        }

        if (!flag)
        {
            //err = "用户未订购TD无线座机（集团）18元套餐[9433]或TD无线座机（家庭）18元套[9434]或无线固话家庭套餐[5920]或神州行户户通座机套餐[1261]，不能加入集团产品！";
            err = "用户未订购TD无线固话18元套餐（PRODUCT_ID=10009433），不能加入集团产品！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 50101, err.toString());
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckTDUserInfoDISCNTInfoRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
