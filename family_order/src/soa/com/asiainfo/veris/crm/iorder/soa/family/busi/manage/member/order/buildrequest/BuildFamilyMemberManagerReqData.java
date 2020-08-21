
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyMemberManageReqData;
import com.asiainfo.veris.crm.iorder.soa.family.common.buildrequest.BuildBaseFamilyBusiReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
/**
 * @Description 家庭成员管理构建类
 * @Auther: zhenggang
 * @Date: 2020/7/31 18:16
 * @version: V1.0
 */
public class BuildFamilyMemberManagerReqData extends BuildBaseFamilyBusiReqData implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        FamilyMemberManageReqData reqData = (FamilyMemberManageReqData) brd;
        super.buildBusiRequestData(param, brd);
        // 顶层业务类型受理均由家庭用户受理
        reqData.setFamilySn(reqData.getUca().getSerialNumber());
        reqData.setFamilyUserId(reqData.getUca().getUserId());
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FamilyMemberManageReqData();
    }
}
