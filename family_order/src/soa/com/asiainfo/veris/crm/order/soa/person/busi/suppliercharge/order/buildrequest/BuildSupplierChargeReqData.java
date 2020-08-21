
package com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge.order.requestdata.SupplierChargeReqData;

public class BuildSupplierChargeReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        SupplierChargeReqData reqData = (SupplierChargeReqData) brd;
        reqData.setCustName(param.getString("CORP_NAME", ""));
        reqData.setRemark("手机卖场供应商缴费信息");
        
        reqData.setAcceptMonth(param.getString("ACCEPT_MONTH",  SysDateMgr.getCurMonth()));
        reqData.setChnlId(param.getString("CHNL_ID"));
        reqData.setFactoryCode(param.getString("FACTORY_CODE"));
        reqData.setYear(param.getString("YEAR"));
        reqData.setRsrvStr3(param.getString("RSRV_STR3"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new SupplierChargeReqData();
    }

}
