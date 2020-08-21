
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:该随E行捆绑GPRS号码存在有效的随e行捆绑优惠,故不可以取消绑定关系!【TradeCheckAfter】
 * @author: xiaocl
 */
/*
 * SELECT COUNT(1) recordcount from tf_f_user_discnt a where user_id=to_number(:VUSER_ID) and
 * partition_id=mod(to_number(:VUSER_ID),10000) and user_id_a=-1 and end_date>sysdate and exists (select 1 from
 * td_s_commpara where subsys_code='CSM' and param_attr=80 and eparchy_code=:VEPARCHY_CODE and sysdate between
 * start_date and end_date and para_code10 is not null and para_code10=to_char(a.discnt_code))
 */
public class CheckSnGprsDiscntRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckSnGprsDiscntRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckSnGprsDiscntRule() >>>>>>>>>>>>>>>>>>");
        /* 逻辑单元节点定义区域 */
        boolean bResult = false;

        /* 总线相关信息包括台账信息资料信息获取 区域 */
        IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT");
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        String strUserIdA = "-1";
        /*
         * 外部查询相关信息获取区域
         */
        IDataset userCommpara80 = BreQryForCommparaOrTag.getCommpara("CSM", 80, strEparchyCode);
        if (IDataUtil.isEmpty(userCommpara80))
            return false;
        for (Iterator iter = listUserDiscnt.iterator(); iter.hasNext();)
        {
            IData userDiscnt = (IData) iter.next();
            for (int iUserCommpara80 = 0, iSize = userCommpara80.size(); iUserCommpara80 < iSize; iUserCommpara80++)
            {
                if (userDiscnt.getString("USER_ID_A").equals(strUserIdA) && userDiscnt.getString("DISCNT_CODE").equals(userCommpara80.getData(iUserCommpara80).getString("PARA_CODE10")))
                {
                    bResult = true;

                    break;
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckSnGprsDiscntRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
