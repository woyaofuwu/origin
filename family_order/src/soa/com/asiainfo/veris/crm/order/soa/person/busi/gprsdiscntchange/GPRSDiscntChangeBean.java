
package com.asiainfo.veris.crm.order.soa.person.busi.gprsdiscntchange;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GPRSDiscntChangeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class GPRSDiscntChangeBean extends CSBizBean
{

    public void checkUserGPRSInfo(IData userInfo) throws Exception
    {
        IDataset enableGPRSDiscnts = new DatasetList();
        String userId = userInfo.getString("USER_ID");

        // 判断用户服务状态
        String userStateCode = ((IData) UcaInfoQry.qryUserInfoByUserId(userId)).getString("USER_STATE_CODESET");
        if (!"0".equals(userStateCode) && !"N".equals(userStateCode))
        {
            IDataset mainSvcInfo = UserSvcInfoQry.getMainSvcUserId(userId);
            String serviceId = mainSvcInfo.getData(0).getString("SERVICE_ID");
            IDataset states = USvcStateInfoQry.qryStateNameBySvcIdStateCode(serviceId, userStateCode);
            String stateName = states.getData(0).getString("STATE_NAME");
            CSAppException.apperr(GPRSDiscntChangeException.CRM_GPRSDISCNTCHANGE_3, stateName);
        }

        // 判断用户是否具有GPRS服务
        IDataset gprsSvcInfos = UserSvcInfoQry.getSvcUserId(userId, "22");
        if (IDataUtil.isEmpty(gprsSvcInfos))
        {
            CSAppException.apperr(GPRSDiscntChangeException.CRM_GPRSDISCNTCHANGE_1);
        }
    }

    /**
     * 获取所有的可选GPRS套餐
     * 
     * @param userInfo
     * @return
     * @throws Exception
     */
    public IDataset getAllGPRSDiscnts(IData userInfo) throws Exception
    {
        IDataset enableGPRSDiscnts = new DatasetList();
        IDataset gprsList = this.getUserGPRSDiscnts(userInfo);

        if (IDataUtil.isEmpty(gprsList))
        {
            CSAppException.apperr(GPRSDiscntChangeException.CRM_GPRSDISCNTCHANGE_2);
        }

//        String packageId = gprsList.getData(0).getString("PACKAGE_ID");

        // 返回用户可选优惠，根据包ID获取
        //订单中心改造 add by hefeng 
/*        IDataset result = DiscntInfoQry.queryDiscntsByPkgId(packageId);
        IDataset allGPRSDiscnts = DiscntInfoQry.queryDiscntsByDtype("5");
        for (int i = 0, size = result.size(); i < size; i++)
        {
            IData temp = result.getData(i);
            for (int j = 0, gprsDiscntSize = allGPRSDiscnts.size(); j < gprsDiscntSize; j++)
            {
                IData gprsDiscnt = allGPRSDiscnts.getData(j);
                if (temp.getString("DISCNT_CODE").equals(gprsDiscnt.getString("DISCNT_CODE")))
                {
                    enableGPRSDiscnts.add(temp);
                }
            }
        }*/
        enableGPRSDiscnts=UPackageElementInfoQry.queryPackageElementsByProductIdDisctypeCode(gprsList.getData(0).getString("PRODUCT_ID"),"5");
        if (IDataUtil.isEmpty(enableGPRSDiscnts))
        {
            CSAppException.apperr(GPRSDiscntChangeException.CRM_GPRSDISCNTCHANGE_4);
        }

        return enableGPRSDiscnts;
    }

    /**
     * 获取用户的GPRS优惠
     * 
     * @param userInfo
     * @return
     * @throws Exception
     */
    public IDataset getUserGPRSDiscnts(IData userInfo) throws Exception
    {
        String sn = userInfo.getString("SERIAL_NUMBER");
        UcaData uca = UcaDataFactory.getNormalUca(sn);
        IDataset userGPRSDiscnts = new DatasetList();

        // 判断用户是否具有GPRS优惠
        List<DiscntTradeData> userDiscntList = uca.getUserDiscnts();
//        IDataset allGPRSDiscnts = DiscntInfoQry.queryDiscntsByDtype("5");
       IDataset allGPRSDiscnts= UPackageElementInfoQry.queryPackageElementsByProductIdDisctypeCode("","5");

        for (int i = 0, size = userDiscntList.size(); i < size; i++)
        {
            DiscntTradeData userDiscnt = userDiscntList.get(i);
            for (int j = 0, discntSize = allGPRSDiscnts.size(); j < discntSize; j++)
            {
                IData gprsDiscnt = allGPRSDiscnts.getData(j);
                if (userDiscnt.getDiscntCode().equals(gprsDiscnt.getString("OFFER_CODE")))
                {
                    IData userDiscntData = userDiscnt.toData();
                    String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(userDiscnt.getDiscntCode());
                    userDiscntData.put("DISCNT_NAME", gprsDiscnt.getString("OFFER_NAME"));
                    userGPRSDiscnts.add(userDiscntData);
                    break;
                }
            }
        }

        if (IDataUtil.isEmpty(userGPRSDiscnts))
        {
            CSAppException.apperr(GPRSDiscntChangeException.CRM_GPRSDISCNTCHANGE_2);
        }

        return userGPRSDiscnts;
    }
}
