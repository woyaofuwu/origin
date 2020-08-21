
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;

public class CheckOweFeeChangeForGfffMeb extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    /**
     * 201512291515,201512291516规则
     * 流量自由充集团欠费判断,只针对订购叠加包的判断
     */
    private static Logger logger = Logger.getLogger(CheckOweFeeChangeForGfffMeb.class);
    
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckOweFeeChangeForGfffMeb()  >>>>>>>>>>>>>>>>>>");
        }
        
        String userId = databus.getString("USER_ID", "");//集团产品的user_id
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        boolean isAddOrNot = false;
        IDataset userElements = null;
        String userElementsStr = "";
        String subTransCode = databus.getString("X_SUBTRANS_CODE","");
        
        //批量进来的
        if(StringUtils.isNotBlank(subTransCode) && StringUtils.equals(subTransCode, "GrpBat")){
            
            userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
            if (StringUtils.isBlank(userElementsStr)){
                return true;
            }
            
            userElements = new DatasetList(userElementsStr);
            if (IDataUtil.isEmpty(userElements)){
                return true;
            }
            
        } else {
            
            userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
            if (StringUtils.isBlank(userElementsStr)){
                return true;
            }
            
            userElements = new DatasetList(userElementsStr);
            if (IDataUtil.isEmpty(userElements)){
                return true;
            }
        }
        
        if(IDataUtil.isNotEmpty(userElements)){
            int size = userElements.size();
            for (int i = 0; i < size; i++)
            {
                IData elements = userElements.getData(i);
                String eleTypeCode = elements.getString("ELEMENT_TYPE_CODE","");
                String modifyTag = elements.getString("MODIFY_TAG","");
                String packageId = elements.getString("PACKAGE_ID","");
                
                //订购叠加包时做一下集团欠费的判断
                if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode) &&
                        BofConst.MODIFY_TAG_ADD.equals(modifyTag) && 
                        ("73430003".equals(packageId) || "73440003".equals(packageId))){
                    isAddOrNot = true;
                    break;
                }
            }
        }
        
        if(isAddOrNot){
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
                
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode + "-888", err);
                return false;
            }
            
            if (Double.parseDouble(acctBalance) * 0.01 < 200)
            {
                double acctBalanceD = Double.parseDouble(acctBalance)  * 0.01;
                
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
                }
                
                String creditValue = "0";
                if (IDataUtil.isNotEmpty(creditInfos))
                {
                    creditValue = creditInfos.getData(0).getString("CREDIT_VALUE","0");
                }
                
                double creditValueD = Double.parseDouble(creditValue);
                
                if((acctBalanceD + creditValueD)<200){
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该集团客户目前实时结余已经不足，不能订购叠加包优惠!");
                    return false;
                }
            }
            
        }
       
        
        if (logger.isDebugEnabled()){
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckOweFeeChangeForGfffMeb() <<<<<<<<<<<<<<<<<<<");
        }
        
        return true;
    }

}
