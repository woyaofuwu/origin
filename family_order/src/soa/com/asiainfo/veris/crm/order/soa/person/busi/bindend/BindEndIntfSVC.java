
package com.asiainfo.veris.crm.order.soa.person.busi.bindend;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BindEndIntfSVC extends CSBizService
{
    public IData checkEndDate(IData data) throws Exception
    {

        String serialNumber = data.getString("SERIAL_NUMBER");
        IData userinfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = userinfo.getString("USER_ID");
        IDataset dataset = UserInfoQry.getBindEndUser(userId); // 是否为中高端用户
        if (IDataUtil.isEmpty(dataset) || IDataUtil.isEmpty(dataset.getData(0)))
        {

            IData backInfo = new DataMap();
            backInfo.put("X_RESULTCODE", "0");
            backInfo.put("X_RESULTINFO", "该号码不是中高端客户！");
            backInfo.put("X_RECORDNUM", "1");
            return backInfo;
        }

        IDataset dataset1 = UserInfoQry.getBindEndStartDate(userId);
        if (IDataUtil.isEmpty(dataset1) || IDataUtil.isEmpty(dataset1.getData(0)))
        {

            IDataset dataset2 = UserInfoQry.getUserSaleActive(userId);
            if (IDataUtil.isEmpty(dataset2) || IDataUtil.isEmpty(dataset2.getData(0)))
            {
                IData backInfo = new DataMap();
                backInfo.put("X_RESULTCODE", "0");
                backInfo.put("X_RESULTINFO", "该用户为拍照中高端客户，但从未办理任何捆绑业务，请推荐客户办理营销活动。");
                backInfo.put("X_RECORDNUM", "4");
                return backInfo;
            }
            IData backInfo = new DataMap();
            backInfo.put("X_RESULTCODE", "0");
            backInfo.put("X_RESULTINFO", "该用户为拍照中高端客户，但不需要推荐业务。");
            backInfo.put("X_RECORDNUM", "2");
            return backInfo;
        }

        IData info = dataset1.getData(0);
        IData backInfo = new DataMap();
        backInfo.put("X_RESULTCODE", "0");
        backInfo.put("X_RESULTINFO", "该用户为拍照中高端客户，其最后一笔合约捆绑类业务为【" + info.getString("PRODUCT_NAME") + "】,此业务在" + info.getString("END_DATE") + "即将到期，请推荐客户办理营销活动。");
        backInfo.put("X_RECORDNUM", "3");
        return backInfo;
    }
}
