
package com.asiainfo.veris.crm.order.soa.person.rule.run.pspt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * 检查实名制开户的数量限制
 * 
 * @author liutt
 */
public class CheckRealNameCreateUserLimit extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData pgData = databus.getData("REQDATA");// 请求的数据
            if (IDataUtil.isNotEmpty(pgData))
            {
                UcaData ucaData = (UcaData) databus.get("UCADATA");
                CustomerTradeData customer = ucaData.getUserOriginalData().getCustomer();
                String isRealName = customer.getIsRealName();
                String oldCustName = customer.getCustName();
                String oldPsptId = customer.getPsptId();
                // 带*代表页面有模糊化的情况，肯定是没有修改客户名称了
                String custName = pgData.getString("CUST_NAME").contains("*") ? oldCustName : pgData.getString("CUST_NAME");
                String psptId = pgData.getString("PSPT_ID").contains("*") ? oldPsptId : pgData.getString("PSPT_ID");
                
                //20141021 liutt add 如果当前用户修改之前就已经实名制，并且没有修改客户名称与证件ID，则不做本规则判断限制
                if(StringUtils.equals("1", isRealName) && StringUtils.equals(oldCustName, custName) && StringUtils.equals(oldPsptId, psptId)){
                	 return false;
                }                
                
                if (StringUtils.isNotBlank(pgData.getString("IS_REAL_NAME")))
                {
                    isRealName = pgData.getString("IS_REAL_NAME");
                }
                if (StringUtils.equals("1", isRealName))
                {  
                	int rCount;
                	int rLimit;
                	String psptTyeCode = pgData.getString("PSPT_TYPE_CODE");
                	String brandCode = ucaData.getBrandCode();
                	IDataset otherInfo = UserOtherInfoQry.getOtherInfoByCodeUserId(ucaData.getUserId(),"HYYYKBATCHOPEN");//行业应用卡判断
                	String userType = "";
                	if(("D".equals(psptTyeCode)||"E".equals(psptTyeCode)||"G".equals(psptTyeCode)
            	    	||"L".equals(psptTyeCode)||"M".equals(psptTyeCode)) 
            	    	 &&("G001".equals(brandCode)||"G002".equals(brandCode)||"G010".equals(brandCode)
            	    			||"PWLW".equals(brandCode) ||"IMSG".equals(brandCode) || IDataUtil.isNotEmpty(otherInfo))){
                		
                		if("PWLW".equals(brandCode) || "IMSG".equals(brandCode) || IDataUtil.isNotEmpty(otherInfo)){
                			userType = "1";//物联网卡（含IMS、行业应用卡）
                		}else{
                			userType = "0";//移动电话
                		}
                		
                		rCount = UserInfoQry.getRealNameUserCountByPspt2New(custName, psptId, userType);
	                    rLimit = UserInfoQry.getRealNameUserLimitByPsptNew(custName, psptId, userType);

                	}else {
	                	rCount = UserInfoQry.getRealNameUserCountByPspt2(custName, psptId);
	                    rLimit = UserInfoQry.getRealNameUserLimitByPspt(custName, psptId);

                	}
                    if (rCount >= rLimit)
                    {
                        return true;// 证件号码psptId实名制开户的数量已达到最大值rLimit个，请更换其它证件！
                    }
                }
            }
        }
        return false;
    }

}
