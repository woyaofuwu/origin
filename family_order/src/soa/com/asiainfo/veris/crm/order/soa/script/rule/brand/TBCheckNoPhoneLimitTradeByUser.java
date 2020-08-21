package com.asiainfo.veris.crm.order.soa.script.rule.brand;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * @Description: 无手机宽带未激活用户业务办理限制规则【TradeCheckBefore】
 * @author: songlm
 */
public class TBCheckNoPhoneLimitTradeByUser extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckNoPhoneLimitTradeByUser.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckNoPhoneLimitTradeByUser() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
        	//1、先获取user表数据，看下rsrv_tag1是否是无手机宽带用户，即RSRV_TAG1=N，如果是则继续，不是则跳出该规则
        	IDataset listUsers = databus.getDataset("TF_F_USER");
        	if(IDataUtil.isNotEmpty(listUsers))
        	{
        		IData listUser = listUsers.getData(0);
        		String rsrvTag1 = listUser.getString("RSRV_TAG1","");
        		if("N".equals(rsrvTag1))
        		{
        			//2、获取TF_F_ACCOUNT的ACCT_TAG是否为2-待激活
        			String strUserId = databus.getString("USER_ID");
        			IData userAcctInfo = UcaInfoQry.qryAcctInfoByUserId(strUserId);
        			if(IDataUtil.isNotEmpty(userAcctInfo))
        			{
        				String acctTag = userAcctInfo.getString("ACCT_TAG","");
        				if("2".equals(acctTag))//2-未激活状态
        				{
        					//3、限制办理的业务类型进行匹配，判断当前业务是否是限制办理的业务类型
        					String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        					IDataset commpara537 = BreQryForCommparaOrTag.getCommpara("CSM", 537, strTradeTypeCode, CSBizBean.getUserEparchyCode());
        					
        					//如果是则拦截
        					if(IDataUtil.isNotEmpty(commpara537))
        					{
        						bResult = true;
        						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6082, "未激活用户暂时无法办理该业务！");
        					}
        				}
        			}
        		}
        	}
        }
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckNoPhoneLimitTradeByUser() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;
    }

}
