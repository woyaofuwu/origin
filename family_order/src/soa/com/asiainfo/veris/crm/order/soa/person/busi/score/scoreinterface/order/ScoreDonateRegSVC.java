
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.CheckIdentityUtil;

public class ScoreDonateRegSVC extends OrderService
{

    /**
	 *
	 */
	private static final long serialVersionUID = 3369509724156554165L;
	@Override
    public String getOrderTypeCode() throws Exception
    {
        return "340";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "340";
    }
    @Override
    public final void setTrans(IData input) throws Exception
    {

        if (StringUtils.equals("SS.ScoreDonateIBossRegSVC.tradeReg", getVisit().getXTransCode()))
        {
            if (StringUtils.isNotBlank(input.getString("L_MOBILE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("L_MOBILE"));
                //return;
            }
            //验证是否是积分商城使用，然后鉴权
      	    CheckIdentityUtil.checkIdentitySvc4TradeReg(input, input.getString("SERIAL_NUMBER"));
        }
        else if (StringUtils.equals("SS.ScoreDonateITFRegSVC.tradeReg", getVisit().getXTransCode()))
        {
        	if (StringUtils.isBlank(input.getString("TRANSFER_POINT", "")))
            {
        		input.put("TRANSFER_POINT", input.getString("DONATE_SCORE"));
            }
        }
        else if (StringUtils.equals("SS.ScoreDonateRegSVC.TradeReg", getVisit().getXTransCode()))
        {
        	if (StringUtils.isBlank(input.getString("TRANSFER_POINT", "")))
            {
        		input.put("TRANSFER_POINT", input.getString("DONATE_SCORE"));
            }
        }
    }
}
