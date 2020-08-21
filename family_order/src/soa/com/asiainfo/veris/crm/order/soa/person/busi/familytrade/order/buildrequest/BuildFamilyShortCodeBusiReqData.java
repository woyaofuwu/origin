
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.buildrequest;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyMebShortCodeData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyShortCodeBusiReqData;

public class BuildFamilyShortCodeBusiReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        FamilyShortCodeBusiReqData reqData = (FamilyShortCodeBusiReqData) brd;
        UcaData uca = reqData.getUca();
        IDataset mebList = new DatasetList(param.getString("MEB_LIST", "[]"));

        String userId = uca.getUserId();
        //IDataset result = UserDiscntInfoQry.getDiscntsByPMode(userId, "05");
        String discntArray ="3403,3410";
        List<DiscntTradeData> result = uca.getUserDiscntsByDiscntCodeArray(discntArray);
        String userIdA = result.get(0).getUserIdA();
        IDataset uuInfoList = RelaUUInfoQry.qryRelaUUByUIdAAllDB(userIdA, "45");

        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData mebData = mebList.getData(i);
            String sn = mebData.getString("SERIAL_NUMBER_B");
            String shortCode = mebData.getString("SHORT_CODE_B");
            String oldShortCode = "";

            for (int j = i + 1; j < size; j++)
            {
                IData tempMebData = mebList.getData(j);
                String tempSn = tempMebData.getString("SERIAL_NUMBER_B");
                String tempShortCode = tempMebData.getString("SHORT_CODE_B");
                // 不能存在相同的号码
                if (StringUtils.equals(sn, tempSn))
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_734);
                }
                // 不能存在相同的短号
                if (StringUtils.equals(shortCode, tempShortCode))
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_735);
                }

            }

            for (int k = 0, listSize = uuInfoList.size(); k < listSize; k++)
            {
                IData uuInfo = uuInfoList.getData(k);
                String shortCodeTemp = uuInfo.getString("SHORT_CODE");
                if (StringUtils.equals(shortCode, shortCodeTemp))
                {
                    // 短号已经在成员列表
                    CSAppException.apperr(FamilyException.CRM_FAMILY_63, shortCode);
                }

                String snTemp = uuInfo.getString("SERIAL_NUMBER_B");
                if (StringUtils.equals(sn, snTemp))
                {
                    oldShortCode = shortCodeTemp;
                    //break;
                }

            }

            UcaData mebUca = UcaDataFactory.getNormalUca(sn);
            FamilyMebShortCodeData mebShortCodeData = new FamilyMebShortCodeData();
            mebShortCodeData.setUca(mebUca);
            mebShortCodeData.setOldShortCode(oldShortCode);
            mebShortCodeData.setNewShortCode(shortCode);

            reqData.addShortCodeDataList(mebShortCodeData);
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FamilyShortCodeBusiReqData();
    }

}
