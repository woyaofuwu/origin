
package com.asiainfo.veris.crm.order.soa.script.rule.brand;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 某些品牌不能办理礼包业务【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBChecklimitGiftByBrand extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimitGiftByBrand.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitGiftByBrand() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            IData param = new DataMap();
            String strEparchyCode = databus.getString("EPARCHY_CODE");
            String strUserId = databus.getString("USER_ID");

            param.clear();
            param.put("USER_ID", strUserId);
            String strUserBrandCode = UcaInfoQry.qryUserInfoByUserId(strUserId).getString("BRAND_CODE");
            if (StringUtils.isBlank(strUserBrandCode))
                return false;

            param.clear();
            param.put("PARAM_ATTR", "1259");
            param.put("SUBSYS_CODE", "CSM");
            param.put("EPARCHY_CODE", strEparchyCode);

            IDataset listCommpara = Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_BY_ATTR_ORDERED", param);

            if (IDataUtil.isNotEmpty(listCommpara))
            {
                for (int i = 0, iCount = listCommpara.size(); i < iCount; i++)
                {
                    IData map = listCommpara.getData(i);
                    String brandCode = map.getString("PARAM_CODE");
                    if (strUserBrandCode.equals(brandCode))
                    {
                        StringBuilder strb = new StringBuilder("业务登记前条件判断：该用户的品牌为【").append(strUserBrandCode).append("】，不能办理本业务！");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751034, strb.toString());
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitGiftByBrand() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
