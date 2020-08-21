
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.WlwBusiHelperGrp;

public class CheckMebCSQ extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckMebWLWOrder.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckMebCSQ() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = true;
        String errCode = databus.getString("RULE_BIZ_ID");
        String userIdB = databus.getString("USER_ID_B", "");

        
        //限制NBIOT产品的测试期套餐不允许退订
        IDataset discntList = BofQuery.queryUserAllValidDiscnt(userIdB,"0898");
        IDataset discntTestList = WlwBusiHelperGrp.getWlwDiscnt(discntList,"ALL").getDataset("TEST_DISCNT");
        if (IDataUtil.isNotEmpty(discntTestList)) {
        	for (Iterator iterator = discntTestList.iterator(); iterator.hasNext();)
            {
                IData discnt = (IData) iterator.next();
                String discntCode = discnt.getString("DISCNT_CODE");
                if (("1215001").equals(discntCode)
                		||("1215002").equals(discntCode)
                		||("1215003").equals(discntCode)) {
                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "NB-IOT产品成员号码仍有测试套餐，不能进行取消退订。");
                	return false;
				}
            }
		}
        
        
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CheckMebCSQ() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
