package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.groupsharerelation;

import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @auther : lixx9
 * @createDate :  2020/7/20
 * @describe :
 */
public class GroupShareRelationRegSVC extends OrderService  {

    @Override
    public String getOrderTypeCode() throws Exception {

        return FamilyConstants.FamilyTradeType.SHARE_RELATION_MANAGE.getValue();

    }

    @Override
    public String getTradeTypeCode() throws Exception {

        return FamilyConstants.FamilyTradeType.SHARE_RELATION_MANAGE.getValue();

    }

}
