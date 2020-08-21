
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;

public class CheckDelVpnGrpRight extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckDelVpnGrpRight.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckDelVpnGrpRight() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userId = databus.getString("USER_ID");
        IData data = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(data))
        {
            err = "根据集团用户ID【" + userId + "】查集团用户信息失败!";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
            return bResult;
        }
        String strEcSN = data.getString("SERIAL_NUMBER");
        String staff_id = CSBizBean.getVisit().getStaffId();
        if ("V0HN001010".equals(strEcSN) || "V0SJ004001".equals(strEcSN))
        {
            IDataset right = StaffInfoQry.queryGrpRightByIdCode(staff_id, "HAINMOBILE_GRP_RIGHT", null);
            if (IDataUtil.isEmpty(right))
            {
                err = "您无权注销海南移动公司集团!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                return bResult;
            }
        }
        bResult = true;
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckDelVpnGrpRight() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;

    }
}
