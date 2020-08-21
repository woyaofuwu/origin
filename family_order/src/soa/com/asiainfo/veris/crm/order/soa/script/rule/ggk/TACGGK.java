
package com.asiainfo.veris.crm.order.soa.script.rule.ggk;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TACGGK extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TACGGK.class);

    /**
     * 刮刮卡业务，查询指定用户标识判断是否办理过刮刮卡业务
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TACGGK() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        IData param = new DataMap();

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        String strEparchyCode = databus.getString("EPARCHY_CODE", "");

        /* 开始逻辑规则校验 */

        IDataset listTag = BreQryForCommparaOrTag.getTagByTagCode(strEparchyCode, "CS_CHR_USEGGCARD", "CSM", "0");

        if (listTag.size() > 0 && "0".equals(listTag.getData(0).getString("TAG_CHAR")))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 0, "方便促销活动结束");
        }
        else
        {
            String strRsrvStr10 = databus.getString("RSRV_STR10");

            /* 兑奖 */
            if ("1".equals(strRsrvStr10))
            {
                param.clear();
                param.put("USER_ID", databus.getString("USER_ID"));
                param.put("RSRV_VALUE_CODE", "GGTH");

                IDataset listUserOther = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE", param);

                if (listUserOther.size() == 0)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201036, "业务登记后条件判断:用户未办理刮刮卡相关业务!");
                }
                else
                {
                    int iCount = 0;
                    for (Iterator iter = listUserOther.iterator(); iter.hasNext();)
                    {
                        IData userother = (IData) iter.next();

                        if ("1".equals(userother.getString("RSRV_STR6")))
                        {
                            iCount++;
                        }
                    }

                    if ("0737".equals(strEparchyCode) && iCount >= 4)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201037, "业务登记后条件判断:用户办理过4次刮刮卡兑奖业务，不能再次办理!");
                    }
                    else if (iCount >= 3)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201037, "业务登记后条件判断:用户办理过3次刮刮卡兑奖业务，不能再次办理!");
                    }
                }
            }
            /* 补录 */
            else if ("0".equals(strRsrvStr10))
            {
                param.clear();
                param.put("USER_ID", databus.getString("USER_ID"));
                param.put("RSRV_VALUE_CODE", "GGTH");
                param.put("RSRV_STR1", databus.getString("RSRV_STR1"));
                param.put("RSRV_STR2", "*");
                param.put("NUM", "1");

                if (Dao.qryByRecordCount("TD_S_CPARAM", "ExistsJoinGGcardNum", param))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201043, "业务登记后条件判断:用户已参与同类型活动一次，不能再次参与!");
                }
            }

            /* 判断GGK是否已经被使用 */
            param.clear();
            param.put("RSRV_VALUE_CODE", "GGTH");
            param.put("RSRV_VALUE", databus.getString("RSRV_STR3"));

            IDataset list = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_VALUECODE", param);

            if (list.size() > 0 && (("1".equals(strRsrvStr10) && "1".equals(list.getData(0).getString("RSRV_STR6")) || "0".equals(strRsrvStr10))))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201040, "业务登记后条件判断:该刮刮卡已办理过兑换业务!");
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TACGGK() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
