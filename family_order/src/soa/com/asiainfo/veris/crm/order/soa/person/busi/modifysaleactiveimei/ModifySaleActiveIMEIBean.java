
package com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ModifySaleActiveIMEIException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class ModifySaleActiveIMEIBean extends CSBizBean
{
    public IDataset getUserSaleActiveGoodsInfos(IData userInfo) throws Exception
    {
        IDataset goodsInfos = new DatasetList();
        String userId = userInfo.getString("USER_ID");
        UcaData uca = UcaDataFactory.getUcaByUserId(userId);
        goodsInfos = UserSaleActiveInfoQry.getGJHDGoodsInfoByUserId(userId);
        if (goodsInfos == null || goodsInfos.size() == 0)
        {
            CSAppException.apperr(ModifySaleActiveIMEIException.CRM_MODIFYSALEACTIVEIMEI_1);
        }

        return goodsInfos;
    }
}
