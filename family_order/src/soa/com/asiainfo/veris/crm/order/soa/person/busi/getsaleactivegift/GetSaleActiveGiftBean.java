
package com.asiainfo.veris.crm.order.soa.person.busi.getsaleactivegift;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.GetSaleActiveGiftException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class GetSaleActiveGiftBean extends CSBizBean
{
    public IDataset getGiftInfos(IData userInfo) throws Exception
    {
        String userId = userInfo.getString("USER_ID");
        // 先校验是否有办理营销活动
        IDataset activeInfos = UserSaleActiveInfoQry.queryUserAdvancedPayGifts(userId);
        if (activeInfos == null || activeInfos.size() == 0)
            CSAppException.apperr(GetSaleActiveGiftException.CRM_GETSALEACTIVEGIFT_3);

        if (isVip(userId))
        {
            return UserSaleActiveInfoQry.queryVipUnGotSaleActiveGifts(userId);
        }
        else
        {
            return UserSaleActiveInfoQry.queryUnGotSaleActiveGifts(userId);
        }
    }

    public Boolean isVip(String userId) throws Exception
    {
        UcaData uca = UcaDataFactory.getUcaByUserId(userId);
        VipTradeData vipInfo = uca.getVip();
        if (vipInfo != null)
        {
            String vipTypeCode = vipInfo.getVipTypeCode();
            String vipClassId = vipInfo.getVipClassId();
            if ("0".equals(vipTypeCode) || "5".equals(vipTypeCode))
            {
                if ("1".equals(vipClassId) || "2".equals(vipClassId) || "3".equals(vipClassId) || "4".equals(vipClassId))
                    return true;
            }
        }
        return false;
    }
}
