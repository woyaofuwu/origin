
package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.filter;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class FlowExchangeFilter implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        /*String isInterface = input.getString("IN_TAG", "1");// 1表示接口

        if (StringUtils.equals(isInterface, "1"))
        {
            IDataUtil.chkParam(input, "SERIAL_NUMBER");

            String serialNumber = input.getString("SERIAL_NUMBER");

            IData user = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);

            if (IDataUtil.isEmpty(user))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
            }

            String userId = user.getString("USER_ID");
            String userProductId = user.getString("PRODUCT_ID");
            String eparchyCode = user.getString("EPARCHY_CODE");
            input.put("USER_ID", userId);
            input.put("USER_PRODUCT_ID", userProductId);
            input.put("EPARCHY_CODE", eparchyCode);

            FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
            bean.loadUpdateFamilyInfo(input);
        }*/
    }

}
