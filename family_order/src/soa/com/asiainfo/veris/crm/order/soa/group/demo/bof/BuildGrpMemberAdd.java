/**
 * @Title: BuildGrpMemberAdd.java
 * @Package com.ailk.groupservice.demo.bof
 * @Description: TODO(用一句话描述该文件做什么)
 * @author A18ccms A18ccms_gmail_com
 * @date Feb 21, 2014 2:36:43 PM
 * @version V1.0
 */

package com.asiainfo.veris.crm.order.soa.group.demo.bof;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BuildGrpMemberAdd extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        BaseGrpMemberAddReqData request = (BaseGrpMemberAddReqData) brd;

        request.setTrade_type_code(param.getString("TRADE_TYPE_CODE", "110"));
        request.setOrder_type_code(param.getString("ORDER_TYPE_CODE", "110"));

        if (param.getString("EFFECT_NOW", "").equals("1"))
        {
            request.setEffectNow(true);
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new BaseGrpMemberAddReqData();
    }

    public BaseReqData buildRequestData(IData param) throws Exception
    {
        // 构建基本请求对象
        BaseReqData brd = this.getBlankRequestDataInstance();
        // 构建业务请求对象
        this.buildBusiRequestData(param, brd);
        return brd;
    }

}
