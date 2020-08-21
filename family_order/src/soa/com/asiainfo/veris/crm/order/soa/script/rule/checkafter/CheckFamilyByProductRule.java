
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class CheckFamilyByProductRule extends BreBase implements IBREScript

{

    private static Logger logger = Logger.getLogger(CheckFamilyByProductRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckFamilyByProductRule() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String strProductId = databus.getString("PRODUCT_ID");
        String strFamilyCount = databus.getString("RSRV_STR3");
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        String strProductIdPara1 = ruleParam.getString(databus, "PRODUCT_ID_1");
        String strProductIdPara2 = ruleParam.getString(databus, "PRODUCT_ID_2");

        IDataset listComparaInfo = new DatasetList();
        try
        {
            listComparaInfo = BreQryForCommparaOrTag.getCommpara("CSM", 1010, "FAMILYCN", strProductId, strEparchyCode);
        }
        catch (Exception e)
        {
            StringBuilder strError = new StringBuilder("").append("业务登记后条件判断:获取通用参数出错！。");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201117", strError.toString());
            return false;
        }
        if (IDataUtil.isEmpty(listComparaInfo))
        {
            return false;
        }

        /* 开始逻辑规则校验 */
        if (listComparaInfo.size() == 1)
        {
            int iCount = Integer.parseInt(listComparaInfo.getData(0).getString("PARA_CODE2"));
            if (iCount != 0 && Integer.parseInt(strFamilyCount) > iCount)
            {
                if (strProductId.equals(strProductIdPara1))
                {
                    StringBuilder strError = new StringBuilder("").append("尊敬的客户，您已设定满2个亲友号码，如您想更改亲友号码，可发送“SC亲友号码”到10086019先删除一个现有亲友号码后，再发送“SD亲友号码”到10086019设定新的亲友号码。注：短信内容中“亲友号码”指实际手机号码，删除亲友号码免费，设定亲友号码2元/次/个。");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201119", strError.toString());
                }
                else if (strProductId.equals(strProductIdPara2))
                {
                    StringBuilder strError = new StringBuilder("").append("尊敬的客户，您已设定满2个亲友号码，如您想更改亲友号码，可发送“DE亲友号码”到10086019先删除一个现有亲友号码后，再发送“AD亲友号码”到10086019设定新的亲友号码。注：短信内容中“亲友号码”指实际手机号码，删除亲友号码免费，设定亲友号码2元/次/个。");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201120", strError.toString());
                }
                else
                {
                    StringBuilder strError = new StringBuilder();
                    strError.append("业务登记后条件判断:副卡超过限制数").append("[" + iCount + "]个！");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201121", strError.toString());
                }

            }
        }
        else if (listComparaInfo.size() > 1)
        {
            StringBuilder strError = new StringBuilder("业务登记后条件判断:").append("获取通用参数（副卡限制数）返回多条记录！！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201121", strError.toString());
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckFamilyByProductRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;
    }

}
