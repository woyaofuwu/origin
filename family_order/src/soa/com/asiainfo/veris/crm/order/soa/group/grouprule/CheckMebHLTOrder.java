package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class CheckMebHLTOrder extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckMebHLTOrder.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckMebHLTOrder() >>>>>>>>>>>>>>>>>>");

        boolean bResult = true;
        String errCode = databus.getString("RULE_BIZ_ID");
        String userIdB = databus.getString("USER_ID_B", "");
        String grpPrdId = databus.getString("PRODUCT_ID");// 集团产品

        //-------判断成员号码是否物联网号码--------
        IData info = UcaInfoQry.qryUserInfoByUserId(userIdB);

        // 和路通产品成员新增界面及删除（含批量），取消原来对147和10648号段的限制判断，改为判断号码类型为物联网号码才可以进行操作
        // String serialNumber = info.getString("SERIAL_NUMBER", "");
        // if (!serialNumber.startsWith("10648") && !serialNumber.startsWith("147"))
        // {
        //     BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "成员号段不符合要求。");
        // }
        String netTypeCode = info.getString("NET_TYPE_CODE", "");
        if (!"07".equals(netTypeCode))  // 号码类型为物联网号码才可以进行操作
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "成员号码不是物联网类型，不符合要求。");
        }

        //-------判断成员号码是否已办理和路通营销活动---------
        /*
        String saleActivePrdId = "68800935";
        IDataset saleInfos = UserSaleActiveInfoQry.queryUserSaleActiveProdId(userIdB, saleActivePrdId, "0");
        if (IDataUtil.isEmpty(saleInfos))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该成员号码["+serialNumber+"]尚未办理和路通营销活动，不能加入和路通产品集团！");
        }
        */
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CheckMebHLTOrder() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
