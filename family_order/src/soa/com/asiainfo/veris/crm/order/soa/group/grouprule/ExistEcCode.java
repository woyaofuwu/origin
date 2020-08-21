
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistEcCode extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistEcCode.class);

    private static final long serialVersionUID = 1L;

    /*
     * @description 根据客户编号查询集团BBOSS侧ECCODE
     * @author xunyl
     * @date 2013-09-04
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistEcCode() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取本省客户编号 */
        String custId = databus.getString("CUST_ID");

        /* 根据本省客户编号获取BBOSS侧客户编号 */
        IData ecCodeInfo = UcaInfoQry.qryGrpInfoByCustId(custId);

        /* 开始逻辑规则校验 */
        if (IDataUtil.isNotEmpty(ecCodeInfo))
        {
            if (StringUtils.isEmpty(ecCodeInfo.getString("MP_GROUP_CUST_CODE")))
            {
                bResult = true;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistEcCode() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
