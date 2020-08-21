
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.wlan;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * WLAN套餐用尽通知
 * 
 * @author xiekl
 */
public class WlanDiscntUserOutNoticeFilter implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        String operCode = input.getString("OPER_CODE");
        String discntCode = input.getString("DISCNT_CODE");
        String elementId = input.getString("ELEMENT_ID", input.getString("SERVICE_ID"));
        IDataset attrs = new DatasetList();

        if (userInfo != null)
        {
            input.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        }

        if (!"16".equals(operCode))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "操作码错误，操作应该为16");
        }

        IDataset discntConfigList = CommparaInfoQry.getCommparaInfoByCode2("CSM", "3700", elementId, discntCode, BizRoute.getRouteId());

        if (discntConfigList == null || discntConfigList.isEmpty())
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "传入的套餐编码错误" + discntCode);
        }
        IData discntConfig = discntConfigList.getData(0);

        IData attrPackage = new DataMap();
        attrPackage.put("ATTR_CODE", discntConfig.getString("PARA_CODE3", ""));
        attrPackage.put("ATTR_VALUE", discntConfig.getString("PARA_CODE1", ""));
        attrs.add(attrPackage);
        if (IDataUtil.isNotEmpty(attrs))
        {
            input.put("ATTR_PARAM", attrs);
        }

    }

}
