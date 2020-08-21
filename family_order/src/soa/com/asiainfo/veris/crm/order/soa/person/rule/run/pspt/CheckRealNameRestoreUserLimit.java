
package com.asiainfo.veris.crm.order.soa.person.rule.run.pspt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 复机检查实名制可户下用户的数量限制
 * 
 * @author xiaozb
 */
public class CheckRealNameRestoreUserLimit extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(CheckRealNameRestoreUserLimit.class);
    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        IData custData = CustomerInfoQry.qryCustInfo(databus.getString("CUST_ID"));
        if (IDataUtil.isNotEmpty(custData))
        {
            String isRealName = custData.getString("IS_REAL_NAME");
            if (StringUtils.equals("1", isRealName))
            {
                String custName = custData.getString("CUST_NAME");
                String psptId = custData.getString("PSPT_ID");
                String psptTypeCode = custData.getString("PSPT_TYPE_CODE");
                /**
            	 * REQ201611180016 关于特殊调整我公司营业执照开户使用人不限制5户的需求
            	 * chenxy3 20161212 
            	 * */
            	if("E".equals(psptTypeCode) && "91460000710920952X".equals(psptId) && "中国移动通信集团海南有限公司".equals(custName)){ 
            		return false;
            	}
            	
                int rCount = 0;	
                int rLimit = 5;
            	/**
            	 * 优化单位证件开户阀值权限设置需求
            	 * 复机当做个人移动电话校验
            	 * mengqx
            	 */
            	if(("D".equals(psptTypeCode)||"E".equals(psptTypeCode)||"G".equals(psptTypeCode)
            	    	||"L".equals(psptTypeCode)||"M".equals(psptTypeCode))){
            		String userType = "0";//复机当做个人移动电话校验
            		rCount = UserInfoQry.getRealNameUserCountByPspt2New(custName, psptId, userType);
                    rLimit = UserInfoQry.getRealNameUserLimitByPsptNew(custName, psptId, userType);
            	}else {
                	//add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
                    /*IDataset realUsers = UserInfoQry.getRealNameUserCountByPspt(custName, psptId);
                    int rCount = 0;
                    if (IDataUtil.isNotEmpty(realUsers))
                    {
                        rCount = realUsers.size();
                    }*/              
                    rCount = UserInfoQry.getRealNameUserCountByPspt2New(custName, psptId,"0");
                    rCount += UserInfoQry.getRealNameUserCountByUsePspt(custName, psptId, null);	// 判断一证五号个数以登记该证件为户主和使用人合并计算           
                    //add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
 

            		rLimit = UserInfoQry.getRealNameUserLimitByPspt(custName, psptId);
            	}
            	if (rCount >= rLimit)
            	{
            		return true;// 证件号码psptId实名制开户的数量已达到最大值rLimit个，不能办理此业务！
            	}
            	
            }
        }

        return false;
    }

}
