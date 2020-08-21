
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TACTradeDiscntByDelete extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TACTradeDiscntByDelete.class);

    /**
     * 判断用户的保底优惠是否被删除
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TACTradeDiscntByDelete() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        String strDiscntJudge = null;
        String strDiscntParam = null;
        String strDiscntRight = null;
        String strTipsInfo = null;
        String strParaCode1 = null;

        boolean bExists = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */
        IDataset listCommpara = BreQryForCommparaOrTag.getCommpara("CSM", 3717, databus.getString("TRADE_TYPE_CODE"), databus.getString("EPARCHY_CODE"));

        for (Iterator iterListCommpara = listCommpara.iterator(); iterListCommpara.hasNext();)
        {
            IData commpara = (IData) iterListCommpara.next();

            strDiscntJudge = commpara.getString("PARA_CODE1");
            strDiscntParam = commpara.getString("PARA_CODE2");
            strDiscntRight = commpara.getString("PARA_CODE3");

            /* 当前被删除的资费的para_code1 */
            strTipsInfo = commpara.getString("PARA_CODE24");

            if ("1".equals(strDiscntJudge))
            {
                IData param = new DataMap();

                param.put("EPARCHY_CODE", databus.getString("EPARCHY_CODE"));
                param.put("PARAM_CODE", strDiscntParam);
                param.put("USER_ID", databus.getString("USER_ID"));

                bExists = Dao.qryByRecordCount("TD_S_CPARAM", "ExistsUserDiscntMulti", param);
            }

            if (("0".equals(strDiscntJudge) || ("0".equals(strDiscntJudge) && bExists)) && !StaffPrivUtil.isFuncDataPriv(databus.getString("TRADE_STAFF_ID"), strDiscntRight))
            {
                IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
                IDataset listCommpara3718 = BreQryForCommparaOrTag.getCommpara("CSM", 3718, databus.getString("EPARCHY_CODE"));

                for (Iterator iterTradeDiscnt = listTradeDiscnt.iterator(); iterTradeDiscnt.hasNext();)
                {
                    bExists = true;

                    IData tradediscnt = (IData) iterTradeDiscnt.next();

                    /* 当前被删除的discnt */
                    if ("1".equals(tradediscnt.getString("MODIFY_TAG")))
                    {
                        for (Iterator iterCommpara3718 = listCommpara3718.iterator(); iterCommpara3718.hasNext();)
                        {
                            IData commpara3718 = (IData) iterCommpara3718.next();

                            /* 能在3718中找到当前被删除的 discnt */
                            if (commpara3718.getString("PARAM_CODE").equals(tradediscnt.getString("DISCNT_CODE")))
                            {
                                /* 记录下当前被删除 discnt 的 para_code1 */
                                strParaCode1 = commpara3718.getString("PARA_CODE1");

                                /* 找当前有没有新增的优惠 */
                                for (Iterator iterAddDiscnt = listTradeDiscnt.iterator(); iterAddDiscnt.hasNext();)
                                {
                                    IData adddiscnt = (IData) iterAddDiscnt.next();

                                    if ("0".equals(adddiscnt.getString("MODIFY_TAG")))
                                    {
                                        for (Iterator iterAdd3718 = listCommpara3718.iterator(); iterAdd3718.hasNext();)
                                        {
                                            IData add3718 = (IData) iterAdd3718.next();

                                            if (adddiscnt.getString("DISCNT_CODE").equals(add3718.getString("PARAM_CODE")) && Integer.parseInt(add3718.getString("PARA_CODE1")) >= Integer.parseInt(strParaCode1))
                                            {
                                                bExists = false;
                                                break;
                                            }
                                        }
                                    }

                                    if (!bExists)
                                    {
                                        break;
                                    }
                                }
                            }

                            if (!bExists)
                            {
                                break;
                            }
                        }
                    }

                    if (bExists)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201130, "#业务登记后条件判断:" + strTipsInfo);
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TACTradeDiscntByDelete() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
