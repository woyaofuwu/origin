package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.payrelation;

import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @auther : lixx9
 * @createDate :  2020/8/4
 * @describe :
 */
public class FamilyPayRelationRegSVC extends OrderService {

    @Override
    public String getOrderTypeCode() throws Exception {

        return FamilyConstants.FamilyTradeType.PAY_RELATION_MANAGE.getValue();

    }

    @Override
    public String getTradeTypeCode() throws Exception {

        return FamilyConstants.FamilyTradeType.PAY_RELATION_MANAGE.getValue();

    }


}
