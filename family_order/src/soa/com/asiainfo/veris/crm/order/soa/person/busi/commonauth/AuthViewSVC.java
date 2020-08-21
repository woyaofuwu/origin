
package com.asiainfo.veris.crm.order.soa.person.busi.commonauth;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class AuthViewSVC extends CSBizService
{
    public IDataset getCustInfoByPK(IData param) throws Exception
    {
        return IDataUtil.idToIds(UcaInfoQry.qryPerInfoByCustId(param.getString("CUST_ID")));
    }

    public IDataset getCustInfoByPspt(IData param) throws Exception
    {
        return CustomerInfoQry.getCustInfoByPspt(param.getString("PSPT_TYPE_CODE"), param.getString("PSPT_ID"));
    }

    public IData getUserInfoBySn(IData param) throws Exception
    {
        IData user = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER"), this.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(user))
        {
            IData product = UcaInfoQry.qryMainProdInfoByUserId(user.getString("USER_ID"));
            if (IDataUtil.isNotEmpty(product))
            {
                if (!product.getString("BRAND_CODE").substring(0, 2).equals("TT"))
                {// 品牌以TT开头
                    return user;
                }
                user.put("PRODUCT_ID", product.getString("PRODUCT_ID"));
                user.put("PRODUCT_NAME", product.getString("PRODUCT_NAME"));
            }
        }
        return user;
    }

    public IData IsBlackCust(IData param) throws Exception
    {
        IData data = new DataMap();
        data.put("FLAG", UCustBlackInfoQry.isBlackCust(param.getString("PSPT_TYPE_CODE"), param.getString("PSPT_ID")));
        return data;
    }

    public IDataset qryUserInfoByCusts(IData param) throws Exception
    {
        IDataset users = UserInfoQry.qryUserInfoByCusts(param.getString("CUST_ID"));
        IDataset returnUsers = new DatasetList();
        if (IDataUtil.isNotEmpty(users))
        {
            int size = users.size();
            for (int i = 0; i < size; i++)
            {
                IData user = users.getData(i);
                IData product = UcaInfoQry.qryMainProdInfoByUserId(user.getString("USER_ID"));
                if (IDataUtil.isNotEmpty(product))
                {
                    if (!product.getString("BRAND_CODE").substring(0, 2).equals("TT"))
                    {// 品牌以TT开头
                        continue;
                    }
                    user.put("PRODUCT_ID", product.getString("PRODUCT_ID"));
                    user.put("PRODUCT_NAME", product.getString("PRODUCT_NAME"));
                    returnUsers.add(user);
                }
                else
                {
                    continue;
                }
            }
        }
        return returnUsers;
    }
}
