
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.ChangeSvcStateReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildChangeSvcStateReqData.java
 * @Description:
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-3-12 下午4:05:39
 */

public class BuildChangeSvcStateReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        
        ChangeSvcStateReqData changeSvcStateBrd = (ChangeSvcStateReqData) brd;
        
        changeSvcStateBrd.setIsStopWide(param.getString("IS_STOP_WIDE",""));
        changeSvcStateBrd.setIsOpenWide(param.getString("IS_OPEN_WIDE",""));
    }

    @Override
    public UcaData buildUcaData(IData param) throws Exception
    {
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        if ("46".equals(tradeTypeCode) || "44".equals(tradeTypeCode))
        {
            String userId = param.getString("USER_ID");
            return UcaDataFactory.getDestroyUcaByUserId(userId);
        }
        else
        {
            return super.buildUcaData(param);
        }

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ChangeSvcStateReqData();
    }

}
