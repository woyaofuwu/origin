
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class CheckMebWLWOrder extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckMebWLWOrder.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckMebWLWOrder() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = true;
        String errCode = databus.getString("RULE_BIZ_ID");
        String userIdB = databus.getString("USER_ID_B", "");

        IData info = UcaInfoQry.qryUserInfoByUserId(userIdB);

        if (!info.getString("SERIAL_NUMBER", "").startsWith("10648") 
        		&& !info.getString("SERIAL_NUMBER", "").startsWith("147")
        		&& !info.getString("SERIAL_NUMBER", "").startsWith("10647")
        		&& !info.getString("SERIAL_NUMBER", "").startsWith("1729")
        		&& !info.getString("SERIAL_NUMBER", "").startsWith("1724")
        		&& !info.getString("SERIAL_NUMBER", "").startsWith("14400")
				&& !info.getString("SERIAL_NUMBER", "").startsWith("14402")
				&& !info.getString("SERIAL_NUMBER", "").startsWith("1729625"))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "号段不符合要求。");
        }

        String productId = databus.getString("PRODUCT_ID");// 集团产品
        String persProduct = ""; // 个人订购的物联网产品id

        IDataset proinfos = UserProductInfoQry.getProductInfo(userIdB, "-1");

        if (IDataUtil.isEmpty(proinfos))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该用户未订购个人物联网产品。");
        }

        persProduct = proinfos.getData(0).getString("PRODUCT_ID");

        // 机器卡
        if (productId.equals("20005013"))
        {
            if (!persProduct.equals("20120706"))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "只有机器卡用户才能添加该成员。");
            }
        }
        // NBIOT产品
        else if (productId.equals("20171214"))
        {
            if (!persProduct.equals("20171219"))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "只有NBIOT物联卡用户才能添加该成员。");
            }
        }
        else if(productId.equals("20161122"))//车联网
        {
        	if(!persProduct.equals("84002059"))
        	{
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "只有车联网用户才能添加该成员。");
        	}
        }
        else if(productId.equals("20161124"))//和对讲
        {
        	if(!persProduct.equals("84006837"))
        	{
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "只有和对讲用户才能添加该成员。");
        	}
        }
        else if(productId.equals("20200402"))//机器卡_2020
        {
        	if(!persProduct.equals("20200401"))
        	{
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "只有机器卡_2020用户才能添加该成员。");
        	}
        }
        else if(productId.equals("20200405"))//车联网_2020
        {
        	if(!persProduct.equals("20200404"))
        	{
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "只有车联网_2020用户才能添加该成员。");
        	}
        }
        else if(productId.equals("20200408"))//和对讲_2020
        {
        	if(!persProduct.equals("20200407"))
        	{
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "只有和对讲_2020用户才能添加该成员。");
        	}
        }
        else
        {
            // 物联通
            if (!persProduct.equals("20120707"))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "只有物联通用户才能添加该成员。");
            }
        }

        IDataset infos = RelaUUInfoQry.qryWlwMebInfoByIDB(userIdB,"9A");
        if(IDataUtil.isNotEmpty(infos))
        {
        	String serialNumberA = "";
        	IData data = infos.getData(0);
        	if(IDataUtil.isNotEmpty(infos))
        	{
        		serialNumberA = data.getString("SERIAL_NUMBER_A","");
        	}
        	String message = "该用户本月是" + serialNumberA + "集团产品的成员,请下个月再做成员新增!";
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, message);
        }
        
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CheckMebWLWOrder() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
