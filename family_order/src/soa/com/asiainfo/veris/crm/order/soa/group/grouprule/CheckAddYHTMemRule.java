
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
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupQueryBean;

public class CheckAddYHTMemRule extends BreBase implements IBREScript
{

    /**
     * 该手机成员必须先订购该集团的融合V网或普通V网才能订购融合一号通!
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckAddYHTMemRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckAddYHTMemRule() >>>>>>>>>>>>>>>>>>");
        boolean bResult = true;
        String errCode = databus.getString("RULE_BIZ_ID");
        // ////////////////////////////////////////////////////////////////////////

        String strUserIdB = databus.getString("USER_ID_B", "");
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(strUserIdB);
        if (IDataUtil.isEmpty(userInfo))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "判断一号通成员时，根据成员用户标识查询成员用户信息不存在！USER_ID=" + strUserIdB);
            bResult = false;
        }
        else
        {
            String netTypeCode = userInfo.getString("NET_TYPE_CODE", "");
            if (!"05".equals(netTypeCode))
            {// IMS号码不需要校验
                IData param = new DataMap();
                String custId = databus.getString("CUST_ID", "");
                // 查用户关系表，判断是否有uu关系
                param.put("USER_ID_B", strUserIdB);
                param.put("CUST_ID", custId);
                param.put("RELATION_TYPE_CODE", "20");
                IDataset idsUU = GroupQueryBean.qryRelationUUByCustIdAndUserIdB(param);
                if (IDataUtil.isEmpty(idsUU)) // 不是vpmn成员
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该手机成员必须先订购该集团的融合V网或普通V网才能订购融合一号通!");
                    bResult = false;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出CheckAddYHTMemRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
