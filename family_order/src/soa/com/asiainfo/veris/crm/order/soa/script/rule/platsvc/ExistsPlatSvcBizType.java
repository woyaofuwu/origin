
package com.asiainfo.veris.crm.order.soa.script.rule.platsvc;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsPlatSvcBizType extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsPlatSvcBizType.class);

    /**
     * 判断用户是否有开通指定biz_type_code类型的sp业务
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsPlatSvcBizType() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strBizTypeCode = ruleParam.getString(databus, "BIZ_TYPE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listPlatSvc = databus.getDataset("TF_F_USER_PLATSVC");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listPlatSvc.iterator(); iter.hasNext();)
        {
            IData platSvc = (IData) iter.next();

            if (strBizTypeCode.equals(platSvc.getString("BIZ_TYPE_CODE")))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsPlatSvcBizType() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
