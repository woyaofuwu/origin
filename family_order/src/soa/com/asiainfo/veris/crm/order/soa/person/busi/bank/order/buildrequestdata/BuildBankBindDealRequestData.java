
package com.asiainfo.veris.crm.order.soa.person.busi.bank.order.buildrequestdata;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.bank.order.requestdata.BankBindDealRequestData;

/**
 * 手机号码和银联卡绑定数据请求处理类
 * 
 * @author wukw3
 */
public class BuildBankBindDealRequestData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	BankBindDealRequestData bankBindRD = (BankBindDealRequestData) brd;
    	bankBindRD.setBankName(param.getString("BANK_NAME"));
    	bankBindRD.setBankCardNo(param.getString("BANK_CARD_NO"));
    	bankBindRD.setPassWord(param.getString("PASS_WORD"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new BankBindDealRequestData();
    }

}
