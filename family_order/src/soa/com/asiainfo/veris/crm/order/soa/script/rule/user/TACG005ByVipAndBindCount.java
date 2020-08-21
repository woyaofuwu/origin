
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TACG005ByVipAndBindCount extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TACG005ByVipAndBindCount.class);

    /**
     * 1.主号码办理时，需要判断绑定的随e行用户数是否超过限制。 2.随e行号码只能被绑定1次。 3.限制VIP卡只能办理一次。
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistCustName() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IData param = new DataMap();

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */

        /* VIP卡号有则判断 */
        if (databus.containsKey("RSRV_STR4"))
        {
            param.put("RSRV_STR4", databus.getString("RSRV_STR4")); // 卡号
            param.put("RSRV_STR2", databus.getString("RSRV_STR2")); // 类别
            param.put("EPARCHY_CODE", databus.getString("TRADE_EPARCHY_CODE")); // 地市编码

            if (Dao.qryByRecordCount("TD_S_CPARAM", "SELCNT_BY_ECARD_VIPNO", param))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201049, "业务登记后条件判断:该VIP卡已经办理了随E行捆绑业务,不能再次办理!");
            }
        }

        /* 未完工判断 */
        param.clear();
        param.put("USER_ID", databus.getString("USER_ID"));
        param.put("TRADE_ID", "-1");

        if (Dao.qryByRecordCount("TD_S_CPARAM", "SEL_BY_LIMIT_G005", param,Route.getJourDb()))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201051, "业务登记后条件判断:该随e行号码已经与其他手机绑定,不能再次办理!");
        }

        /* 判断附卡是否已经被其他用户绑定 */
        param.clear();
        param.put("USER_ID_B", databus.getString("RSRV_STR6"));
        param.put("RELATION_TYPE_CODE", "80");
        param.put("SHORT_CODE", "ZZZZ");

        if (Dao.qryByRecordCount("TD_S_CPARAM", "SELCNT_BY_RELATION_USERIDB", param))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201051, "业务登记后条件判断:该随e行号码已经与其他手机绑定,不能再次办理!");
        }

        /* 判断主卡绑定数量是否达到上限 */
        param.clear();
        param.put("USER_ID_A", databus.getString("USER_ID"));
        param.put("RELATION_TYPE_CODE", "80");
        if ("2".equals(databus.getString("RSRV_STR1"))) // 随e行龙卷风
        {
            param.put("SHORT_CODE", databus.getString("RSRV_STR1"));
        }
        else
        {
            param.put("SHORT_CODE", "ZZZZ");
        }

        int iCount = Dao.qryByCode("TD_S_CPARAM", "SELCNT_BY_RELATION_USERIDA", param).getData(0).getInt("RECORDCOUNT");

        if (iCount > 0)
        {
            if ("2".equals(databus.getString("RSRV_STR1"))) /* 随e行龙卷风 */
            {
                int iECardCanNum = 1;

                /* 获取随e行龙卷风业务可以捆绑的随e行号码个数 */
                IDataset listTag = BreQryForCommparaOrTag.getTagByTagCode(databus.getString("EPARCHY_CODE"), "CS_NUM_ECARDBIND", "CSM", "1");

                if (listTag.size() > 0 && listTag.getData(0).getString("TAG_NUMBER") != null && !"".equals(listTag.getData(0).getString("TAG_NUMBER")))
                {
                    iECardCanNum = listTag.getData(0).getInt("TAG_NUMBER");
                }

                if (iCount >= iECardCanNum)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201054, "业务登记后条件判断:该号码绑定的随E行用户数已达到限制,故不能再次办理!");
                }
            }
            else

            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201055, "业务登记后条件判断:该手机号码已经办理过随e行捆绑业务,故不能再次办理!");
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistCustName() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
