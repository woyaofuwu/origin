
package com.asiainfo.veris.crm.order.soa.person.busi.heeduforschools.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.heeduforschools.order.requestdata.HeEduForSchoolsReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildTopSetBoxRequestData.java
 * @Description:
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-8-4 下午9:59:09 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-4 yxd v1.0.0 修改原因
 */
public class BuildHeEduForSchoolsRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	HeEduForSchoolsReqData reqData = (HeEduForSchoolsReqData) brd;
        reqData.setCondDataA(param.getData("condDataA"));
        reqData.setCondDataB(param.getData("condDataB",new DataMap()));
        reqData.setSerialNumber(param.getString("SERIAL_NUMBER"));
        reqData.setProductId(param.getString("PRODUCT_ID"));
        reqData.setActiveTime(param.getString("ACTIVE_TIME"));
        reqData.setUserId(param.getString("USER_ID"));
        reqData.setSelectType(param.getString("SELECT_TYPE"));


    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new HeEduForSchoolsReqData();
    }

}
