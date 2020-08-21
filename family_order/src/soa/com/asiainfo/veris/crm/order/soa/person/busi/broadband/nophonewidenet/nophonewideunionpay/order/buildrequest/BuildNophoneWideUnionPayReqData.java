
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideunionpay.order.buildrequest;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideunionpay.order.requestdata.NophoneWideUnionPayRequestData;

public class BuildNophoneWideUnionPayReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        NophoneWideUnionPayRequestData reqData = (NophoneWideUnionPayRequestData) brd;
        reqData.setPaySerialNumber(param.getString("PAY_SERIAL_NUMBER",""));
        IDataset payUsers = UserInfoQry.getEffUserInfoBySn(param.getString("PAY_SERIAL_NUMBER",""),"0",null);
        if(IDataUtil.isEmpty(payUsers)){
        	CSAppException.appError("-1", "付费号码"+param.getString("PAY_SERIAL_NUMBER")+"无有效用户资料！");
        }
        reqData.setPayUserId(payUsers.getData(0).getString("USER_ID"));
        IData payAccount = UcaInfoQry.qryPayRelaByUserId(payUsers.getData(0).getString("USER_ID"));
        reqData.setPayAcctId(payAccount.getString("ACCT_ID"));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new NophoneWideUnionPayRequestData();
    }

}
