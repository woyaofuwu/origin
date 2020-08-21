
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateGhytGroupMember extends CreateGroupMember
{
    private IData paramData = new DataMap();

    /**
     * 构造函数
     * 
     * @author chenzg
     * @date 2018-5-29
     */
    public CreateGhytGroupMember()
    {
    }

    /**
     * 生成登记信息
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        paramData = getParamData();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }

    public IData getParamData() throws Exception
    {
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        IData paramData = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(paramData))
        {
            return null;
        }
        return paramData;
    }

    /**
     * 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        data.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团USER_ID
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpProductId())); // 关系类型编码
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER
        data.put("RSRV_STR10", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER

    }
    
    @Override
    protected void makInit(IData map) throws Exception
    {
        makUcaForMebNormal(map); // 提前查三户
        super.makInit(map);
    }

    
}
