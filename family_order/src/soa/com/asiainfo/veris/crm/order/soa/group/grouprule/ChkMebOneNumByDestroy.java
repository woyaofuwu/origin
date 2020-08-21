
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class ChkMebOneNumByDestroy extends BreBase implements IBREScript
{

    /**
     * 请先退订融合一号通产品之后，再退订V网
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkMebOneNumByDestroy.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckBlackList() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = true;
        String errCode = ErrorMgrUtil.getErrorCode(databus);
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
                IDataset RelauuInfos = RelaUUInfoQry.getUserRelationByUserIdB(strUserIdB);
                if (IDataUtil.isNotEmpty(RelauuInfos))
                {
                    for (int i = 0; i < RelauuInfos.size(); i++)
                    {
                        IData RelauuInfo = (IData) RelauuInfos.get(i);
                        String relationTypeCode = RelauuInfo.getString("RELATION_TYPE_CODE");

                        // 判断是否融合一号通成员
                        if ("S2".equals(relationTypeCode))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "请先退订融合一号通产品之后，再退订V网");
                            bResult = false;
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出ChkMebOneNumByDestroy() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
