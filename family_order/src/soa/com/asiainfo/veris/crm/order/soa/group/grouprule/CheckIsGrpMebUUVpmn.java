
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class CheckIsGrpMebUUVpmn extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckIsGrpMebUUVpmn.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckIsGrpMebUUVpmn() >>>>>>>>>>>>>>>>>>");

        /* 逻辑单元节点定义区域 */
        boolean bResult = false;
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        // 当前用户标识
        String strUserIdB = databus.getString("USER_ID_B", "");

        IData param = new DataMap();
        IDataset idsUU = RelaUUInfoQry.getUserRelationVpmnByUserIdB(strUserIdB);
        if (IDataUtil.isEmpty(idsUU)) // 不是vpmn成员
        {
            bResult = true;
            return bResult;
        }

        // if (idsUU.size() == 1)
        // {
        // String ifbooking = databus.getString("IF_BOOKING", "false");
        // if (ifbooking.equals("true"))
        // {
        // String userIdA = databus.getString("USER_ID", "");
        //
        // IDataset trades = TradeInfoQry.getTradeBookByUserIdTradeType(strUserIdB, "3037");
        // if (trades != null && trades.size() > 0 && !trades.getData(0).getString("USER_ID_B", "").equals(userIdA))
        // {
        // bResult = true;
        // return bResult;
        // }
        // else if (trades != null && trades.size() > 0 && trades.getData(0).getString("USER_ID_B", "").equals(userIdA))
        // {
        // err = "该客户已预约注销了同一个vpmn集团的成员，不能再预约新增该vpmn集团的成员！";
        // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
        // return bResult;
        //
        // }
        // }
        // }

        // 以上情况都不符，则报错
        IData idUU;
        idUU = idsUU.getData(0);

        String strUserIda = idUU.getString("USER_ID_A", "");

        // 查询集团用户信息
        IData idUser = UcaInfoQry.qryUserInfoByUserId(strUserIda);
        if (IDataUtil.isEmpty(idUser))
        {
            err = "判断VPMN成员时，根据集团用户标识查询集团用户信息不存在！USER_ID=" + strUserIda;
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
            return bResult;
        }

        // 得到集团客户标识
        String strCustId = idUser.getString("CUST_ID");

        // 查询集团客户信息
        param.clear();
        IData idGroup = UcaInfoQry.qryGrpInfoByCustId(strCustId);
        if (IDataUtil.isEmpty(idGroup))
        {
            err = "判断VPMN成员时，根据集团客户标识查询集团客户信息不存在！CUST_ID=" + strCustId;
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
            return bResult;
        }

        // 得到集团客户编码、名称
        String strGroupId = idGroup.getString("GROUP_ID");
        String strGroupName = idGroup.getString("CUST_NAME");

        err = "该客户已是【" + strGroupId + strGroupName + "】的VPMN成员。一个成员只能加入一个V网，不能再次加入！";
        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
        return bResult;

    }
}
