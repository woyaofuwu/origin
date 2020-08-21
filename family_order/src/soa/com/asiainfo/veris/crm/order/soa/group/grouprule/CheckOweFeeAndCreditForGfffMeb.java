
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cenpaygfffesop.GrpCenpayGfffEsopMgrQry;

public class CheckOweFeeAndCreditForGfffMeb extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    /**
     * 201512291510,201512291511,201512291512规则
     * 流量自由充集团欠费判断
     */
    private static Logger logger = Logger.getLogger(CheckOweFeeAndCreditForGfffMeb.class);
    
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckOweFeeAndCreditForGfffMeb()  >>>>>>>>>>>>>>>>>>");
        
        String userId = databus.getString("USER_ID", "");//集团产品的user_id
        //String custId = databus.getString("CUST_ID", "");//集团产品的cust_id
        String productId = databus.getString("PRODUCT_ID", "");//集团产品的product_id
        
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        if(!"7344".equals(productId))
        {
        	// 设置默认值
            String acctBalance = "0"; // 实时结余
            
            try{
                IData oweFeeData = AcctCall.getOweFeeByUserId(userId);
                acctBalance = oweFeeData.getString("ACCT_BALANCE", "0");
            }catch(Exception e){
                
                if(logger.isDebugEnabled()){
                    logger.info(e);
                }

                if(logger.isInfoEnabled()){
                    logger.info(e);
                }
                
                String err = "";
                err = e.getMessage();
                if(err.contains("CRM_BIZ_167")){
                    err = err.replace("[", "");
                    err = err.replace("]", "");
                    err = err.replace("`", "");
                }
                
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode + "-999", err);
                return false;
            }
            
            if(logger.isDebugEnabled())
            {
    			logger.debug("<<<<<<<<<<CheckOweFeeAndCreditForGfffMeb实时结余>>>>>>>>>>acctBalance=" + acctBalance);
            }
            
            if (Double.parseDouble(acctBalance) * 0.01 < 200)
            {
            	double acctBalanceD = Double.parseDouble(acctBalance) * 0.01;
                
                IDataset creditInfos = null;            
                try{
                    //查询集团产品的信用度
                    //creditInfos = AcctCall.getUserCreditInfos("0", userId);
                    creditInfos = AcctCall.getUserCreditInfo(userId);
                }catch(Exception e){
                    
                    if(logger.isDebugEnabled()){
                        logger.info(e);
                    }

                    if(logger.isInfoEnabled()){
                        logger.info("查询集团产品的信用度错误信息=" + e);
                    }
                    
//                    String err = "";
//                    err = e.getMessage();
//                    if(err.contains("CRM_BIZ_167")){
//                        err = err.replace("[", "");
//                        err = err.replace("]", "");
//                        err = err.replace("`", "");
//                    }
//                    
//                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode + "-998", err);
                    //return false;
                }
                
                String creditValue = "0";
                if (IDataUtil.isNotEmpty(creditInfos))
                {
                    creditValue = creditInfos.getData(0).getString("CREDIT_VALUE","0");
                }
                
                double creditValueD = Double.parseDouble(creditValue);
                
                if(logger.isDebugEnabled())
                {
        			logger.debug("<<<<<<<<<<CheckOweFeeAndCreditForGfffMeb集团用户的信用度>>>>>>>>>>creditValueD=" + creditValueD);
                }
                
                if((acctBalanceD + creditValueD)<200){
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该集团客户目前实时结余已经不足，不能添加成员!");
                    return false;
                }
            }
        }
        
        //------------------------------------------关于是否是已暂停的集团产品-----------------start-------------
        IDataset isSuspendProduct = StaticUtil.getList(CSBizBean.getVisit(), "TD_S_STATIC", "DATA_ID", "PDATA_ID",new java.lang.String[]
        { "TYPE_ID","DATA_ID"}, new java.lang.String[]
        { "ISSUSPEND",productId});
        
        if(IDataUtil.isNotEmpty(isSuspendProduct)){
        	IDataset grpCenPayList = GrpCenpayGfffEsopMgrQry.getUserGrpCenPayByUserIdProductOfferId(userId);
        	if(IDataUtil.isNotEmpty(grpCenPayList)){
        		String isSuspend = grpCenPayList.getData(0).getString("RSRV_STR5","");
        		if("F".equals(isSuspend)){
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该集团客户目前存在违规暂停流量叠加包，不能添加成员!");
        			return false;
        		}
        	}
        }
        //------------------------------------------关于是否是已暂停的集团产品-----------------start-------------
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckOweFeeAndCreditForGfffMeb() <<<<<<<<<<<<<<<<<<<");
        }
        return true;
    }

}
