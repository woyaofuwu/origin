
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateEconnectGroupMenber extends CreateGroupMember
{
    /**
     * 生成登记信息
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        // 2014-7-25 版本合并,ng没有下面判断了
        // String userId = reqData.getGrpUca().getUserId();// 当前集团客户标识
        // String discntCode = "4013";
        // IDataset infos = UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(userId, discntCode,
        // reqData.getGrpUca().getUserEparchyCode());
        // if (IDataUtil.isNotEmpty(infos))
        // {
        // IDataset discnts = reqData.cd.getDiscnt();
        // if (IDataUtil.isNotEmpty(discnts))
        // {
        // for (int i = 0; i < discnts.size(); i++)
        // {
        // IData tmp = discnts.getData(i);
        // String elementId = tmp.getString("ELEMENT_ID", "");
        // String elementType = tmp.getString("ELEMENT_TYPE_CODE", "");
        // if (!"4014".equals(elementId) && elementType.equals("D"))
        // {
        // CSAppException.apperr(UserDiscntException.CRM_USER_DISCNT_2);
        //
        // }
        // }
        // }
        // }

    }

    /**
     * @description 处理主台账表数据
     */
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        data.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团USER_ID
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId())); // 关系类型编码
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER

    }

}
