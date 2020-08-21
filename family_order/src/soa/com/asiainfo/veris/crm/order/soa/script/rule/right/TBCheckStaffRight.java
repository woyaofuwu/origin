
package com.asiainfo.veris.crm.order.soa.script.rule.right;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.utils.RuleUtils;

public class TBCheckStaffRight extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckStaffRight.class);

    /**
     * 员工权限大小判断
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistCustName() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        // 判断用户资料是否存在
        if (!RuleUtils.existsUserById(databus))
        {
            return bResult;
        }

        IData param = new DataMap();

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */

        String strIdType = databus.getString("ID_TYPE");

        /* 开始逻辑规则校验 */
        if ("1".equals(strIdType))
        {
            String strTradeEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
            String strEparchyCode = CSBizBean.getUserEparchyCode();

            param.clear();
            param.put("EPARCHY_CODE", strTradeEparchyCode);
            param.put("TAG_CODE", "CSM_CHR_JUDGESTAFFCLASS");
            param.put("SUBSYS_CODE", "CSM");
            param.put("USE_TAG", "0");
            IDataset listTag = Dao.qryByCode("TD_S_TAG", "SEL_ALL_BY_TAGCODE", param, Route.CONN_CRM_CEN);
            // 0-无权限 1-个人 2-营业厅 3-业务区 4-地州 5-全省
            if (listTag.size() > 0 && "1".equals(listTag.getData(0).getString("OBJ_TAG")))
            {
                String strRightClass = StaffPrivUtil.getFieldPrivClass(databus.getString("TRADE_STAFF_ID"), "SYS005");

                if (strRightClass == null || strRightClass.length() == 0)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, 1414, "业务受理前条件判断:当前员工无权办理该用户的业务！");
                }

                if (strRightClass.indexOf("1") > -1 || strRightClass.indexOf("2") > -1)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, 1414, "业务受理前条件判断:当前员工无权办理该用户的业务！");
                }
                else if (strRightClass.indexOf("3") > -1)
                {
                    String strTradeCityCode = databus.getString("TRADE_CITY_CODE");
                    String strCityCode = databus.getDataset("TF_F_USER").getData(0).getString("CITY_CODE");

                    if (!strTradeCityCode.equals(strCityCode) || !strTradeEparchyCode.equals(strEparchyCode))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, 1414, "业务受理前条件判断:当前员工无权办理该用户的业务！\\n操作员业务区[" + strTradeEparchyCode + "." + strTradeCityCode + "],用户归属业务区[" + strEparchyCode + "." + strCityCode + "]");
                    }
                }
                else if (strRightClass.indexOf("4") > -1)
                {
                    if (!strTradeEparchyCode.equals(strEparchyCode))
                    {
                        if (databus.getDataset("TF_F_USER").getData(0).getString("USER_ID").substring(0, 2).equals("99")// 跨地州集团
                                && ("YUNN".equals(databus.getString("PROVINCE_CODE")) || "QHAI".equals(databus.getString("PROVINCE_CODE"))))
                        {
                            // 跳过判断
                        }
                        else
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, 1414, "业务受理前条件判断:当前员工无权办理该用户的业务！\\n操作员地市编码[" + strTradeEparchyCode + "],用户归属地市编码[" + strEparchyCode + "]");
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckStaffRight() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
