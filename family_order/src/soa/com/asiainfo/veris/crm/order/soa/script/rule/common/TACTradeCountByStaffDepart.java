
package com.asiainfo.veris.crm.order.soa.script.rule.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TACTradeCountByStaffDepart extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TACTradeCountByStaffDepart.class);

    public boolean checkAcceptDate(String strAcceptDate) throws Exception
    {
        String strAcceptDateSub = strAcceptDate.length() > 16 ? strAcceptDate.substring(11, 19) : SysDateMgr.getFirstTime00000();

        if (strAcceptDateSub.compareTo("07:00") >= 0 && strAcceptDateSub.compareTo("19:30") <= 0)
        {
            return true;
        }

        return false;
    }

    public IData getTimeTag(String strAcceptDate) throws Exception
    {
        IData param = new DataMap();
        String strAcceptDateSub = strAcceptDate.length() > 16 ? strAcceptDate.substring(11, 16) : SysDateMgr.getFirstTime00000();

        if (strAcceptDateSub.compareTo("19:30:00") >= 0 && strAcceptDateSub.compareTo(SysDateMgr.getEndTime235959()) <= 0)
        {
            param.put("START", "0");
            param.put("END", "1");
        }
        else if (strAcceptDateSub.compareTo(SysDateMgr.getFirstTime00000()) >= 0 && strAcceptDateSub.compareTo("07:00:00") <= 0)
        {
            param.put("START", "-1");
            param.put("END", "0");
        }

        return param;
    }

    /**
     * 根据员工部门限制该部门晚上19：30 － 第二天7：00 只能办理多少业务量
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TACTradeCountByStaffDepart() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        String strBrandCode = databus.getString("RSRV_STR1", "");
        if ("".equals(strBrandCode))
        {
            strBrandCode = databus.getString("BRAND_CODE");
        }

        String strAcceptDate = databus.getString("ACCEPT_DATE");

        /* 开始逻辑规则校验 */

        /* 如果业务时间在19：30 － 第二天7：00间 */
        if (checkAcceptDate(strAcceptDate))
        {
            IData param = new DataMap();
            param.put("DEPART_ID", databus.getString("TRADE_DEPART_ID", ""));
            param.put("LIMIT_TAG", "0");
            param.put("EPARCHY_CODE", databus.getString("EPARCHY_CODE"));

            /* 部门需要监控 */
            if (Dao.qryByRecordCount("TD_B_BUSIDEPART_CHECK", "SEL_LIMIT_BUSIDEPART_CHECK", param))
            {
                /* 获取限制业务数量阀值 */
                IDataset listTag = BreQryForCommparaOrTag.getTagByTagCode(databus.getString("EPARCHY_CODE"), "CS_MONITORTYPE_" + databus.getString("TRADE_TYPE_CODE"), "CSM", "0");

                int iLimitCount = listTag.size() > 0 ? listTag.getData(0).getInt("TAG_INFO", 3000) : 3000;

                param.clear();
                param.putAll(getTimeTag(strAcceptDate));
                param.put("TRADE_TYPE_CODE", databus.getString("TRADE_TYPE_CODE"));

                int iCount = Dao.qryByCode("TD_S_CPARAM", "ExistsBhTradeCountByTimeSEAndDepartAndTTC", param).getData(0).getInt("RECORDCOUNT");
                if (iCount > iLimitCount)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201199, "#业务登记后条件判断:业务办理时间在19：30-次日07：00则判断该部门在19：30后办理业务笔>监控笔数!【业务笔数" + iCount + " > 限制阀值" + iLimitCount + "】");
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TACTradeCountByStaffDepart() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
