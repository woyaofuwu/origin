
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

public class CheckServiceStateLimit implements IServiceStateLimit
{
    private static Logger logger = Logger.getLogger(CheckServiceStateLimit.class);

    @Override
    public void CheckServiceStateLimit(IData databus, IDataset listTradeSvcState, IDataset listUserAllSvcState, CheckProductData checkProductData) throws Exception
    {
        // TODO Auto-generated method stub
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("rule 进入 prodcheck CheckServiceStateLimit() 函数");

        int irlCount = 0;

        String strtsModifyTag = "", strtsServiceId = "", strtsStateCode = "", strtsStartDate = "", strtsEndDate = "";
        String strusModifyTag = "", strusServiceId = "", strusStateCode = "", strusStartDate = "", strusEndDate = "";
        String strrlServiceId = "", strrlStateCode = "";

        IDataset listSvcStateLimit = new DatasetList();

        // 1 获取台账服务状态信息

        // 4 判断限制关系
        int iCountTradeSvcState = listTradeSvcState.size();
        for (int itsIdx = 0; itsIdx < iCountTradeSvcState; itsIdx++)
        {
            IData tradeSvcState = listTradeSvcState.getData(itsIdx);
            strtsModifyTag = tradeSvcState.getString("MODIFY_TAG");
            strtsServiceId = tradeSvcState.getString("SERVICE_ID");
            strtsStateCode = tradeSvcState.getString("STATE_CODE");
            strtsStartDate = tradeSvcState.getString("START_DATE");
            strtsEndDate = tradeSvcState.getString("END_DATE");

            // 4.1 新增
            if (strtsModifyTag.equals("0"))
            {
                // 4.1.1 互斥
                irlCount = BreQryForProduct.tacGetSvcStateLimit(strtsServiceId, strtsStateCode, "0", "A", checkProductData.getEparchyCode(), listSvcStateLimit);
                if (irlCount > 0)
                {
                    int iCountUserAllSvcState = listUserAllSvcState.size();
                    for (int iusIdx = 0; iusIdx < iCountUserAllSvcState; iusIdx++)
                    {
                        IData userAllSvcState = listUserAllSvcState.getData(iusIdx);
                        strusModifyTag = userAllSvcState.getString("MODIFY_TAG");
                        strusServiceId = userAllSvcState.getString("SERVICE_ID");
                        strusStateCode = userAllSvcState.getString("STATE_CODE");
                        strusStartDate = userAllSvcState.getString("START_DATE");
                        strusEndDate = userAllSvcState.getString("END_DATE");

                        // 排除自己的那条记录，但不排除用户已有的同一条服务
                        if (strusServiceId.equals(strtsServiceId) && strusStateCode.equals(strtsStateCode) && strusModifyTag.equals("0") && strusStartDate.equals(strtsStartDate))
                        {
                            continue;
                        }

                        for (int irlIdx = 0; irlIdx < irlCount; irlIdx++)
                        {
                            IData svcStateLimit = listSvcStateLimit.getData(irlIdx);
                            strrlServiceId = svcStateLimit.getString("SERVICE_ID");
                            strrlStateCode = svcStateLimit.getString("STATE_CODE_B");

                            if (strrlStateCode.equals(strusStateCode) && strrlServiceId.equals(strusServiceId) && !strusModifyTag.equals("1")
                                    && ((strusStartDate.compareTo(strtsStartDate) <= 0 && strusEndDate.compareTo(strtsStartDate) >= 0) || (strusStartDate.compareTo(strtsStartDate) >= 0 && strusStartDate.compareTo(strtsEndDate) <= 0)))
                            {
                                String strName = BreQueryHelp.getNameByCode("ServiceName", strtsServiceId);

                                String strNameA = BreQueryHelp.getNameByCode("SvcstateName", new String[]
                                { strtsServiceId, strtsStateCode });
                                String strNameB = BreQueryHelp.getNameByCode("SvcstateName", new String[]
                                { strtsServiceId, strrlStateCode });

                                String strError = "#产品依赖互斥判断:新增服务状态[strName.strNameA]和服务状态[strName.strNameB]互斥，不能同时生效，业务不能继续办理！";

                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201514", strError);
                            }
                        }
                    }
                }
            }
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("rule 退出 prodcheck CheckServiceStateLimit() 函数");
    }

}
