
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 【TradeCheckBefore】
 * @author: xiaocl
 */

public class TBCheckStaffLimit extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        int iCount = 0;
        IData objStaffdataright = new DataMap();
        String strEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
        IDataset tagList = new DatasetList();
        iCount = tagList.size();
        String strStaffId = databus.getString("TRADE_STAFF_ID");
        tagList = ParamInfoQry.getTagInfoBySubSys("CSM_CHR_JUDGESTAFFCLASS", "CSM", "0", strEparchyCode);
        String strCityCode = databus.getString("TRADE_CITY_CODE");

        IDataset userInfo = databus.getDataset("TF_F_USER");
        if (IDataUtil.isEmpty(userInfo))
            return false;
        if (iCount > 0 && ((IData) (tagList.get(0))).getString("OBJ_TAG").equals("1")) // 检查操作员权限
        {
            String strRightClass = StaffPrivUtil.getFieldPrivClass(strStaffId, "SYS005");
            // 0-无权限 1-个人 2-营业厅 3-业务区 4-地州 5-全省
            if ("".equals(strRightClass))
            {
                StringBuilder strError = new StringBuilder("业务受理前条件判断:当前员工无权办理该用户的业务！");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 1414, strError.toString());
            }

            if (strRightClass.equals("1"))
            {
                StringBuilder strError = new StringBuilder("业务受理前条件判断:当前员工无权办理该用户的业务！");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 1414, strError.toString());
            }
            else if (strRightClass.equals("2"))
            {
                StringBuilder strError = new StringBuilder("业务受理前条件判断:当前员工无权办理该用户的业务！");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 1414, strError.toString());
            }
            else if (objStaffdataright.getString("RIGHT_CLASS").equals("3")) // 业务区
            {
                if (!strCityCode.equals(userInfo.first().getString("CITY_CODE")) || !strEparchyCode.equals(userInfo.first().getString("EPARCHY_CODE")))
                {
                    StringBuilder strError = new StringBuilder("业务受理前条件判断:当前员工无权办理该用户的业务！\\n操作员业务区[").append(strEparchyCode).append(".").append("strCityCode");
                    strError.append("],用户归属业务区[").append(userInfo.first().getString("EPARCHY_CODE")).append(".").append(userInfo.first().getString("CITY_CODE")).append("]!");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 1414, strError.toString());
                }
            }
            else if (objStaffdataright.getString("RIGHT_CLASS").equals("4")) // 地州
            {
                if (!strEparchyCode.equals(userInfo.first().getString("EPARCHY_CODE")))
                {
                    StringBuilder strError = new StringBuilder();
                    strError.append("1414: 业务受理前条件判断:当前员工无权办理该用户的业务！<br/>操作员地市编码[").append(strEparchyCode).append("],用户归属地市编码[").append(userInfo.first().getString("EPARCHY_CODE")).append("]!");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 1414, strError.toString());
                }
            }

        }
        return false;
    }
}
