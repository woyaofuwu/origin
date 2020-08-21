
package com.asiainfo.veris.crm.order.soa.person.rule.run.cust;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * 校验是否是实名制用户
 * 
 * @author chenzm
 * @date 2014-05-23
 */
public class CheckIsRealName extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /**
     * 实名制用户判断
     * 
     * @author chenzm
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strOrderTypeCode = databus.getString("ORDER_TYPE_CODE");
        String strBrandCode = databus.getString("BRAND_CODE","");
        
        if(!strTradeTypeCode.equals(strOrderTypeCode)&&("110".equals(strTradeTypeCode)||"120".equals(strTradeTypeCode)||"150".equals(strTradeTypeCode))) return false ;
        
        if(CSBizBean.getVisit().getStaffId().indexOf("CREDIT") > -1)//信控过来不要做校验
        {
            return false;
        }
        
        //集团商务宽带、不做非实名制校验
        if("BNBD".equals(strBrandCode)){
        	return false;
        }
        
        //GPRS 最优算，办理安心包，及0-X套餐、不做非实名制校验
        String strOUTRETURNFEE_FLAG = databus.getString("OUTRETURNFEE_FLAG");
        if(strOUTRETURNFEE_FLAG!=null&&"0".equals(strOUTRETURNFEE_FLAG)){
        	return false;
        }
        
        String isRealName = databus.getString("IS_REAL_NAME");

        if (!"1".equals(isRealName))// 非实名制用户
        {
            return true;
        }

        return false;
    }

}
