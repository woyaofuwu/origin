
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class CheckMebDeskTelByDestroy extends BreBase implements IBREScript
{

    /**
     * 请先退出其他融合产品之后，再退出桌面电话成员
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckMebDeskTelByDestroy.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckMebDeskTelByDestroy() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = true;
        String errCode = databus.getString("RULE_BIZ_ID");

        IDataset RelauuInfos = RelaUUInfoQry.getUserRelationByUserIdB(databus.getString("USER_ID_B"));
        if (IDataUtil.isNotEmpty(RelauuInfos))
        {
            for (int i = 0; i < RelauuInfos.size(); i++)
            {
                IData RelauuInfo = (IData) RelauuInfos.get(i);
                String relationTypeCode = RelauuInfo.getString("RELATION_TYPE_CODE");

                // 需要过滤普通V网情况
                if ("20".equals(relationTypeCode))
                {// 判断是否融合V网成员
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该用户还是融合V网成员，请退出融合V网之后，再退出桌面电话成员！");
                    bResult = false;
                    break;
                }
                else if ("S2".equals(relationTypeCode))
                {// 判断是否融合一号通成员
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该用户还是融合一号通成员，请退出融合一号通之后，再退出桌面电话成员！");
                    bResult = false;
                    break;
                }
                else if ("S3".equals(relationTypeCode))
                {// 判断是否融合总机成员
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该用户还是融合总机成员，请退出融合总机之后，再退出桌面电话成员！");
                    bResult = false;
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出CheckMebDeskTelByDestroy() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
