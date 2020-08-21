
package com.asiainfo.veris.crm.order.soa.person.busi.ipexpress.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.IpExpressException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.ipexpress.order.requestdata.IpExpressRequestData;

public class BuildIpExpressReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        IpExpressRequestData ierd = (IpExpressRequestData) brd;
        List<UserTradeData> ipUserDatas = new ArrayList<UserTradeData>();
        IDataset ipInfos = new DatasetList(param.getString("X_CODING_STR"));
        if (ipInfos == null || ipInfos.size() == 0)
        {
            CSAppException.apperr(IpExpressException.CRM_IPEXPRESS_11);
        }
        for (int i = 0, size = ipInfos.size(); i < size; i++)
        {
            IData ipInfo = ipInfos.getData(i);
            UserTradeData ipUserData = new UserTradeData();
            String productId = ipInfo.getString("col_PRODUCT_ID");
            String newPwd = ipInfo.getString("col_TEMP_PWD");
            String oldPwd = ipInfo.getString("col_OLD_PWD");
            String userIdB = ipInfo.getString("col_USER_ID_B");
            String serialNumberG = ipInfo.getString("col_SERIAL_NUMBER_G");
            String dealTag = ipInfo.getString("col_M_DEAL_TAG");
            String packageSvcInfo = ipInfo.getString("col_PACKAGESVC");
            ipUserData.setUserId(userIdB);
            ipUserData.setUserPasswd(newPwd);
            ipUserData.setRsrvStr1(productId);
            ipUserData.setSerialNumber(serialNumberG);
            ipUserData.setModifyTag(dealTag);
            ipUserData.setRsrvStr2(packageSvcInfo);
            ipUserData.setRsrvStr3(oldPwd);
            ipUserDatas.add(ipUserData);
        }
        ierd.setIpUserDatas(ipUserDatas);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new IpExpressRequestData();
    }

}
