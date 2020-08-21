package com.asiainfo.veris.crm.order.soa.person.rule.run.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

/**
 * 宽带用户余额校验
 * @author yuyj3
 *
 */
public class CheckWidenetUserBalance extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
    	String serialNumber = databus.getString("SERIAL_NUMBER");//宽带号码
    	serialNumber = serialNumber.replace("KD_", "");//手机号码
    	
    	if(!"1".equals(serialNumber.substring(0, 1)))
    	{
    		//如果第一位不是1，如集团号码办理商务宽带，则不进该规则
    		return false;
    	}
    	
    	
    	int modemDeposit = 0;
    	int topSetBoxDeposit = 0;
    	int topSetBoxSaleActiveFee = 0;
    	int saleActiveFee = 0;
    	
    	//用户总共要转出的费用
        int totalFee = 0;
        
        //用户现金类存折余额
        int totalDepositBalance = 0;
    	
    	IDataset tradeOtherlist = databus.getDataset("TF_B_TRADE_OTHER");
    	
    	if (IDataUtil.isNotEmpty(tradeOtherlist))
    	{
    	    for (int i = 0;  i < tradeOtherlist.size(); i++)
    	    {
    	        IData tradeOtherInfo =  tradeOtherlist.getData(i);
    	        
    	        if ("TOPSETBOX".equals(tradeOtherInfo.getString("RSRV_VALUE_CODE")))
    	        {
    	            topSetBoxDeposit = Integer.valueOf(tradeOtherInfo.getString("RSRV_STR6","0"));
    	        }
    	        else if ("FTTH".equals(tradeOtherInfo.getString("RSRV_VALUE_CODE")))
    	        {
    	            modemDeposit = Integer.valueOf(tradeOtherInfo.getString("RSRV_STR2","0"));
    	        }
    	    }
    	}
    	
    	if(StringUtils.isNotBlank(databus.getString("RSRV_STR7")))
        {
            topSetBoxSaleActiveFee = Integer.valueOf(databus.getString("RSRV_STR7","0"));
        }
    	
        if(StringUtils.isNotBlank(databus.getString("RSRV_STR8")))
        {
            topSetBoxSaleActiveFee = Integer.valueOf(databus.getString("RSRV_STR8","0"));
        }
        
        totalFee = modemDeposit+topSetBoxDeposit+topSetBoxSaleActiveFee+saleActiveFee;
        
        //需要从现金类存折转出的总费用
        if (totalFee > 0)
        {
        	totalDepositBalance = Integer.parseInt(WideNetUtil.qryBalanceDepositBySn(serialNumber));
            
            if(totalDepositBalance < totalFee)
            {
                //提醒用户从现金类存折转出的总费用余额不足，不允许进行宽带开户业务的的办理
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "2016052103", "您的账户存折可用余额不足，请先办理缴费！");
                return true;
            }
        }

        return false;
    }
}
