
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyMemberSingleReqData;
import com.asiainfo.veris.crm.iorder.soa.family.common.buildrequest.BuildBaseFamilyBusiReqData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
/**
 * @Description 单个成员删除构建类
 * @Auther: zhenggang
 * @Date: 2020/7/31 18:15
 * @version: V1.0
 */
public class BuildDelSingleFamilyMemberReqData extends BuildBaseFamilyBusiReqData implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        FamilyMemberSingleReqData reqData = (FamilyMemberSingleReqData) brd;
        // 1.入参校验
        checkInParam(param);
        // 2.时间
        reqData.setFamilyEffectiveDate(SysDateMgr.getFirstDayOfNextMonth());
        // 3.公用构建
        super.buildBusiRequestData(param, brd);
    }

    private void checkInParam(IData param) throws Exception
    {
        IDataUtil.chkParam(param, "TOP_TRADE_ID");
        IDataUtil.chkParam(param, "TOP_EPARCHY_CODE");
        IDataUtil.chkParam(param, "FAMILY_SERIAL_NUMBER");
        IDataUtil.chkParam(param, "FAMILY_USER_ID");
        IDataUtil.chkParam(param, "FAMILY_PRODUCT_ID");
        IDataUtil.chkParam(param, "MAIN_SERIAL_NUMBER");
        IDataUtil.chkParam(param, "ORDER_TYPE_CODE");
        // 到单删接口里面角色信息必须传
        IDataUtil.chkParam(param, "ROLE_CODE");
    }

    @Override
    public UcaData buildUcaData(IData param) throws Exception
    {
        return super.buildUcaData(param);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FamilyMemberSingleReqData();
    }
}
