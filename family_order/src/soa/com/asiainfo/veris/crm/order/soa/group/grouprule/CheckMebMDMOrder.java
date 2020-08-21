package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class CheckMebMDMOrder extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckMebMDMOrder.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckMebMDMOrder() >>>>>>>>>>>>>>>>>>");

        boolean bResult = true;
        String errCode = databus.getString("RULE_BIZ_ID");
        String userIdB = databus.getString("USER_ID_B", "");
        //String grpPrdId = databus.getString("PRODUCT_ID");// 集团产品

        //-------判断成员号码是否物联网号码--------
        //IData info = UcaInfoQry.qryUserInfoByUserId(userIdB);
        //String netTypeCode = info.getString("NET_TYPE_CODE", "");
        IDataset infos = UserProductInfoQry.queryAllUserValidMainProducts(userIdB);
  
        //logger.info("CheckMebMDMOrder.netTypeCode="+netTypeCode);
        if ( IDataUtil.isNotEmpty(infos) )  // 物联网卡不可订购
        {
        	IData userProduct = infos.getData(0);
        	if( "PWLW".equals(userProduct.getString("BRAND_CODE")) ){
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "物联网卡不可订购，请重新输入！");
                bResult = false;
        	}
        }
 
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CheckMebMDMOrder() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
