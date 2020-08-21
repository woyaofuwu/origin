package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.administrator;

import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @auther : lixx9
 * @createDate :  2020/8/3
 * @describe :
 */
public class AdministratorChangeRegSVC extends OrderService {


    @Override
    public String getOrderTypeCode() throws Exception {

        return FamilyConstants.FamilyTradeType.ADMINISTRATOR_CHANGE.getValue();

    }

    @Override
    public String getTradeTypeCode() throws Exception {

        return FamilyConstants.FamilyTradeType.ADMINISTRATOR_CHANGE.getValue();

    }

}
