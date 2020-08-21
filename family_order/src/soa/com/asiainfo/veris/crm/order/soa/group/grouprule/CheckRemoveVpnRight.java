
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class CheckRemoveVpnRight extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckRemoveVpnRight.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckRemoveVpnRight() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userIdA = databus.getString("USER_ID");// 集团用户标识

        IDataset vpnInfos = UserVpnInfoQry.qryUserVpnByUserId(userIdA);
        if (IDataUtil.isEmpty(vpnInfos))
        {
            err = "根据集团用户标识【" + userIdA + "】获取不到用户VPN信息！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
            return bResult;
        }
        String vpnNo = vpnInfos.getData(0).getString("VPN_NO");
        String staff_id = CSBizBean.getVisit().getStaffId();
        if ("V0HN001010".equals(vpnNo) || "V0SJ004001".equals(vpnNo))
        {
            IDataset right = StaffInfoQry.queryGrpRightByIdCode(staff_id, "REMOVE_HAINMOBILE_MEB_RIGHT", null);
            if (IDataUtil.isEmpty(right))
            {
                err = "您无权删除海南移动公司集团成员!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                return bResult;
            }
        }
        if (!"SUPERUSR".equals(staff_id))
        {
            IDataset rightInfo1 = StaffInfoQry.queryGrpRightByIdCode(null, "REMOVE_VPN_MEMBER", vpnNo);// vpn成员注销的权限
            if (IDataUtil.isNotEmpty(rightInfo1))
            {
                IDataset rightInfo2 = DataHelper.filter(rightInfo1, "STAFF_ID=" + staff_id);
                if (IDataUtil.isEmpty(rightInfo2))
                {
                    err = "您无权办理集团" + vpnNo + "的业务！";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                    return bResult;
                }
            }
        }
        bResult = true;
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckRemoveVpnRight() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;

    }
}
