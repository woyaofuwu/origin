
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.filter;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

/**
 * 手机实名制办理提前校验参数 接口 入参过滤类
 * 
 * @author Administrator
 */
public class RealNameCheckIntfFilter extends RealNameDealIntfFilter
{

    public void transferDataInput(IData inparam) throws Exception
    {
        super.transferDataInput(inparam);
        inparam.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
    }
}
