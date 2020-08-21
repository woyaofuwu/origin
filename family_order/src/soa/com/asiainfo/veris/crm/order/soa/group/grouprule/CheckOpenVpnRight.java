
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;

public class CheckOpenVpnRight extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckOpenVpnRight.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckOpenVpnRight() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);

        String userIdA = databus.getString("USER_ID");// 集团用户标识
        IData data = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);
        if (IDataUtil.isEmpty(data))
        {
            err = "根据集团用户ID【" + userIdA + "】查集团用户信息失败!";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
            return bResult;
        }
        String strEcSN = data.getString("SERIAL_NUMBER");
        String staff_id = CSBizBean.getVisit().getStaffId();
        if ("V0HN001010".equals(strEcSN) || "V0SJ004001".equals(strEcSN))
        {
            IDataset right = StaffInfoQry.queryGrpRightByIdCode(staff_id, "CREATE_HAINMOBILE_MEB_RIGHT", null);
            if (IDataUtil.isEmpty(right))
            {
                err = "您无权办理海南移动公司集团!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                return bResult;
            }
        }
        if (!"SUPERUSR".equals(staff_id))
        {
            IDataset rightInfo1 = StaffInfoQry.queryGrpRightByIdCode(null, "CREATE_VPN_MEMBER", strEcSN); // vpn成员开户的权限
            if (IDataUtil.isNotEmpty(rightInfo1))
            {
                IDataset rightInfo2 = DataHelper.filter(rightInfo1, "STAFF_ID=" + staff_id);
                if (IDataUtil.isEmpty(rightInfo2))
                {
                    err = "您无权办理集团" + strEcSN + "的业务！";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                    return bResult;
                }
            }
        }
        bResult = true;
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckOpenVpnRight() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;

    }
}
