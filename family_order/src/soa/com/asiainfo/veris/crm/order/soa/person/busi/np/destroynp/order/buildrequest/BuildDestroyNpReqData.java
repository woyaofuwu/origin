
package com.asiainfo.veris.crm.order.soa.person.busi.np.destroynp.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.np.destroynp.order.requestdata.DestroyNpReqData;

public class BuildDestroyNpReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

    }

    // 重写构造UCA数据的方法
    public UcaData buildUcaData(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        return UcaDataFactory.getDestroyUcaByUserId(userId);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new DestroyNpReqData();
    }
    
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception{
        
    }

}
