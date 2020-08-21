
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTimeUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:用户正享受服务赠送，不能办理此优惠！【TradeCheckAfter】 业务登记后条件判断:被赠送用户当前有限制优惠，不能继续办理
 * @author: xiaocl
 */

/*
 * SELECT
 * a.subsys_code,a.param_attr,a.param_code,a.param_name,a.para_code1,a.para_code2,a.para_code3,a.para_code4,a.para_code5
 * ,
 * a.para_code6,a.para_code7,a.para_code8,a.para_code9,a.para_code10,a.para_code11,a.para_code12,a.para_code13,a.para_code14
 * ,a.para_code15,a.para_code16,a.para_code17,a.para_code18,a.para_code19,a.para_code20,a.para_code21,a.para_code22,a.
 * para_code23,a.para_code24,a.para_code25,to_char(a.para_code26,'yyyy-mm-dd hh24:mi:ss')
 * para_code26,to_char(a.para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,to_char(a.para_code28,'yyyy-mm-dd hh24:mi:ss')
 * para_code28,to_char(a.para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,to_char(a.para_code30,'yyyy-mm-dd hh24:mi:ss')
 * para_code30,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss')
 * end_date,a.eparchy_code,a.remark,a.update_staff_id,a.update_depart_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss')
 * update_time FROM td_s_commpara a,tf_b_trade_discnt b,tf_f_relation_uu c WHERE (a.subsys_code=:SUBSYS_CODE AND
 * a.param_attr=14 AND (a.eparchy_code=:EPARCHY_CODE OR a.eparchy_code='ZZZZ') AND sysdate BETWEEN a.start_date AND
 * a.end_date ) AND a.para_code1=c.relation_type_code AND a.para_code19 IS NOT NULL AND
 * (a.para_code19=TO_CHAR(b.discnt_code) OR a.para_code19 LIKE TO_CHAR(b.discnt_code)||'|%' OR a.para_code19 LIKE
 * '%|'||TO_CHAR(b.discnt_code)||'|%' OR a.para_code19 LIKE '|%'||TO_CHAR(b.discnt_code)) AND
 * b.trade_id=TO_NUMBER(:TRADE_ID) AND b.modify_tag='0' AND c.user_id_b=TO_NUMBER(:USER_ID) AND
 * c.partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND c.end_date>=SYSDATE AND ((c.start_date<=b.start_date AND
 * c.end_date>=b.start_date) OR (c.start_date<=b.end_date AND c.end_date>=b.end_date))
 */

public class CheckSvcGivingRule1 extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckSvcGivingRule1.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckSvcGivingRule1() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String strPackageId = ruleParam.getString(databus, "PARAM_ATTR");
        String strUserId = databus.getString("USER_ID");
        String strEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        IDataset lisetUserRelation = databus.getDataset("TF_F_RELATION_UU");
        String strTradeId = databus.getString("TRADE_ID");
        IData map = new DataMap();
        map.put("SUBSYS_CODE", "CSM");
        map.put("PARAM_ATTR", "14");
        map.put("EPARCHY_CODE", strEparchyCode);
        if (Dao.qryByRecordCount("TD_S_COMMPARA", "SEL_ONLY_BY_ATTR_ORDERED", map))
        {
            // SEL_BY_PRESENT_LIMIT 原SQL需翻译成代码
            IDataset comparaInfo = BreQryForCommparaOrTag.getCommpara("CSM", 14, strEparchyCode);
            if (IDataUtil.isEmpty(comparaInfo))
            {
                return false;
            }
            for (int i = 0, iASize = comparaInfo.size(); i < iASize; i++)
            {
                IData comparaData = comparaInfo.getData(i);
                String strParaCode19 = comparaData.getString("PARA_CODE19", "");
                if ("".equals(strParaCode19))
                    return false;
                for (int ii = 0, iBSize = listTradeDiscnt.size(); ii < iBSize; ii++)
                {
                    IData tradeDiscnt = listTradeDiscnt.getData(ii);
                    String strDiscntCode = tradeDiscnt.getString("DISCNT_CODE");
                    String strDiscntStartDate = tradeDiscnt.getString("START_DATE");
                    if (strParaCode19.equals(strDiscntCode) && tradeDiscnt.equals("0") || strParaCode19.indexOf(strDiscntCode) > 0)
                    {
                        for (int iii = 0, iCSize = lisetUserRelation.size(); iii < iCSize; iii++)
                        {
                            IData userRelation = lisetUserRelation.getData(iii);
                            String userRltStartDate = userRelation.getString("START_DATE");
                            if (comparaData.getString("PARA_CODE1").equals(userRelation.getString("RELATION_TYPE_CODE")) && userRelation.getString("END_DATE").compareTo(BreTimeUtil.getCurDate(databus)) >= 0
                                    && (userRltStartDate.compareTo(strDiscntStartDate) <= 0 && userRelation.getString("END_DATE").compareTo(strDiscntStartDate) >= 0) || userRltStartDate.compareTo(tradeDiscnt.getString("END_DATE")) <= 0
                                    && userRelation.getString("END_DATE").compareTo(tradeDiscnt.getString("END_DATE")) >= 0)
                            {
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201165, "业务登记后条件判断:用户正享受服务赠送，不能办理此优惠！");
                            }
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckSvcGivingRule1() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
