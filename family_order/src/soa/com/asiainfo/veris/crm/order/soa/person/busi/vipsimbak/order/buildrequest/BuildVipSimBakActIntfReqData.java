
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.requestdata.VipSimBakActReqData;

/**
 * @CREATED by gongp@2013-8-28
 */
public class BuildVipSimBakActIntfReqData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     * @see com.ailk.bof.builder.impl.BaseBuilder#buildBusiRequestData(com.ailk.common.data.IData,
     * com.ailk.bof.data.requestdata.BaseReqData)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        VipSimBakActReqData vrd = (VipSimBakActReqData) brd;

        /*
         * IDataUtil.chkParam(param, "NEWSIMNUM"); IDataUtil.chkParam(param, "OLDSIMNUM");
         */

        vrd.setOldIMSI(param.getString("IMSI"));
        vrd.setOldResCode(param.getString("OLDSIMNUM"));
        vrd.setOldStartDate(param.getString("START_DATE"));

        vrd.setNewKI(param.getString("NEW_KI"));
        vrd.setNewIMSI(param.getString("NEW_IMSI"));
        vrd.setNewSimCardNo(param.getString("NEWSIMNUM"));
        vrd.setRemark(param.getString("REMARK"));
        vrd.setSimCardNo2(param.getString("SimCardNo2", ""));

    }

    /*
     * (non-Javadoc)
     * @see com.ailk.bof.builder.impl.BaseBuilder#getBlankRequestDataInstance()
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new VipSimBakActReqData();
    }

}
