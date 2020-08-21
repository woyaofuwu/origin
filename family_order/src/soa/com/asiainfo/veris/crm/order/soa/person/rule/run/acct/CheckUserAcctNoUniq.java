
package com.asiainfo.veris.crm.order.soa.person.rule.run.acct;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;

/**
 * 对非现金支付的账户进行唯一性检查
 * 
 * @author liutt
 */
public class CheckUserAcctNoUniq extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData pgData = databus.getData("REQDATA");// 请求的数据
            if (IDataUtil.isNotEmpty(pgData))
            {
                if (!"0".equals(pgData.getString("PAY_MODE_CODE")) && !"5".equals(pgData.getString("PAY_MODE_CODE")) && !StringUtils.isBlank(pgData.getString("BANK_CODE")) && !StringUtils.isBlank(pgData.getString("BANK_ACCT_NO")))
                {
                	
                	 //如果是批量修改银行付费账户的名称，不验证
                	 if(pgData.getString("BATCH_OPER_TYPE", "").equals("MODIFYACYCINFO")){
                		 return false;
                	 }
                	
                	 UcaData ucaData = (UcaData) databus.get("UCADATA");
                     String bankAcctNo = pgData.getString("BANK_ACCT_NO","");
                     if(bankAcctNo.contains("*"))//如果被模糊化
                     {
                    	 bankAcctNo = ucaData.getAccount().getBankAcctNo();
                     }
                     
                    IDataset result = AcctInfoQry.getAcctPayName(bankAcctNo, this.getTradeEparchyCode());
                    if (IDataUtil.isNotEmpty(result))
                    {
                        String userId = databus.getString("USER_ID");
                        String pdpayname = pgData.getString("PAY_NAME");
                        if(pdpayname.contains("*"))//如果被模糊化
                        {
                        	pdpayname = ucaData.getAccount().getPayName();
                        }
                        for (int i = 0, size = result.size(); i < size; i++)
                        {
                            String payName = result.getData(i).getString("PAY_NAME");
                            String userIdTemp = result.getData(i).getString("USER_ID");

                            if (!userId.equals(userIdTemp) && !payName.equals(pdpayname))
                            {
                                return true;// 特殊限制判断:该银行帐号只能对应唯一的帐户名称！
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
