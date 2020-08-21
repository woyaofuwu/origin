
package com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.order.requestdata.ApnUserBindingForOlcomReqData;



public class BuildApnUserBindingForOlcomReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
    	
    	ApnUserBindingForOlcomReqData reqData = (ApnUserBindingForOlcomReqData) brd;
    	String apnName = param.getString("APN_NAME");
        String apnDesc = param.getString("APN_DESC");
        String apnCntxId = param.getString("APN_CNTXID");
        String apnTplId = param.getString("APN_TPLID");
        String apnType = param.getString("APN_TYPE");
        String apnIPv4Add = param.getString("APN_IPV4ADD");
        String instId = param.getString("APN_INST_ID");
                
        reqData.setApnName(apnName);
        reqData.setApnDesc(apnDesc);
        reqData.setApnCntxId(apnCntxId);
        reqData.setApnTplId(apnTplId);
        reqData.setApnType(apnType);
        reqData.setApnIPV4Add(apnIPv4Add);
        reqData.setApnInstId(instId);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ApnUserBindingForOlcomReqData();
    }

}
