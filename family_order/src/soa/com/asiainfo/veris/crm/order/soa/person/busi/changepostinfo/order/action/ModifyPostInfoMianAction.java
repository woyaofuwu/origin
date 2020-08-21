
package com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.action;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.requestdata.ModifyPostInfoReqData;

/**
 * 邮寄资料信息修改主台账登记
 * 
 * @author liutt
 */
public class ModifyPostInfoMianAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {
        ModifyPostInfoReqData reqData = (ModifyPostInfoReqData) btd.getRD();
        if (StringUtils.equals("1", reqData.getPostTag()))// 如果邮寄标志 为有效
        {
            btd.getMainTradeData().setRsrvStr1("1");// 邮寄标志
            btd.getMainTradeData().setRsrvStr2(reqData.getPostContent());// 邮政投递的内容
            btd.getMainTradeData().setRsrvStr3(reqData.getEmailContent());// EMAIL投递的内容
            btd.getMainTradeData().setRsrvStr4(reqData.getMMScontent());// MMS投递的内容

            btd.getMainTradeData().setRsrvStr5(reqData.getPostCode());// 邮编
            btd.getMainTradeData().setRsrvStr6(reqData.getPostFaxNbr());// 传真
            btd.getMainTradeData().setRsrvStr7(reqData.getPostEmail());// Email地址
            btd.getMainTradeData().setRsrvStr8(reqData.getPostName());// 邮寄名称
            btd.getMainTradeData().setRsrvStr9(reqData.getPostAddress());// 邮寄地址

        }
        else
        {
            btd.getMainTradeData().setRsrvStr1("0");// 邮寄标志
            btd.getMainTradeData().setRsrvStr2("");// 邮政投递的内容
            btd.getMainTradeData().setRsrvStr3("");// EMAIL投递的内容
            btd.getMainTradeData().setRsrvStr4("");// MMS投递的内容
        }
    }
}
