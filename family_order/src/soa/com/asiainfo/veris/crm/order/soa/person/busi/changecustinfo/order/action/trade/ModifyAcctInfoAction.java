
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.requestdata.ModifyCustInfoReqData;

/**
 * 修改账户资料action ，实名制受理接口过来的需要同时修改账户资料
 * 
 * @author Administrator
 */
public class ModifyAcctInfoAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        ModifyCustInfoReqData rd = (ModifyCustInfoReqData) btd.getRD();


        // 实名制受理接口进来的需要修改账户名称为客户名称、修改付费类型为现金。
        AccountTradeData acctTradeData = rd.getUca().getAccount();
        /**
         * 付费方式是现金的(0)，用户姓名才改
         * @author zhuoyingzhi
         * 20161025
         */
        if("0".equals(acctTradeData.getPayModeCode())){
            acctTradeData.setPayName(rd.getCustName());
            /**
             * REQ201610170020关于客户资料变更优化的需求
             * @author zhuoyingzhi
             * 20161024
             * 通过客户资料变更、微信补登记等方式进行实名登记，不对付费方式进行修改，仍保留原有的付费方式
             */
            //acctTradeData.setPayModeCode("0");
            acctTradeData.setCityCode(rd.getUca().getUser().getCityCode());
            acctTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);

            btd.add(rd.getUca().getSerialNumber(), acctTradeData);
        }

    }

}
