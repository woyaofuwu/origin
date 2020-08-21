
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;

public class CheckMebZunXKHForMobile extends BreBase implements IBREScript
{

    /**
     * 移动号码不能新增为尊享客户集团产品成员
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckMebZunXKHForMobile.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckMebZunXKHForMobile() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = true;
        String errCode = databus.getString("RULE_BIZ_ID");
        String strUserIdB = databus.getString("USER_ID_B", "");
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(strUserIdB);

        if (IDataUtil.isEmpty(userInfo))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "规则校验时，根据成员用户标识查询成员用户信息不存在！USER_ID=" + strUserIdB);
            bResult = false;
        }
        else
        {
            String netTypeCode = userInfo.getString("NET_TYPE_CODE", "");
            // 判断当前号码是否是IMS用户
            if (!"05".equals(netTypeCode))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "只有IMS用户才能新增为[尊享客户集团产品]成员!");
                bResult = false;
            }
            else
            {
                IDataset userImpu = UserImpuInfoQry.queryUserImpuInfo(strUserIdB);
                String RSRV_STR1 = userImpu.getData(0).getString("RSRV_STR1", "");
                // 判断当前号码是否是无卡PC客户端IMS用户
                if (!"0".equals(RSRV_STR1))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "固定终端IMS用户才能新增为[尊享客户集团产品]成员!");
                    bResult = false;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出CheckMebZunXKHForMobile() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
