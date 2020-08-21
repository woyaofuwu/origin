package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaModData;

/**
 * @author Administrator
 */
public class EcPackageData
{
    private String offerId;

    private String upOfferId;

    private String offerType;

    private String operCode;

    private List<ChaModData> chaSpecs = new ArrayList<ChaModData>();

    public List<ChaModData> getChaSpecs()
    {
        return chaSpecs;
    }

    public void setChaSpecs(List<ChaModData> chaSpecs)
    {
        this.chaSpecs = chaSpecs;
    }

    public void addChaSpec(ChaModData chaSpec)
    {
        this.chaSpecs.add(chaSpec);
    }

    public void setOfferId(String offerId)
    {

        this.offerId = offerId;
    }

    public String getOfferId()
    {

        return offerId;
    }

    public void setUpOfferId(String upOfferId)
    {

        this.upOfferId = upOfferId;
    }

    public String getUpOfferId()
    {

        return upOfferId;
    }

    public void setOfferType(String offerType)
    {

        this.offerType = offerType;
    }

    public String getOfferType()
    {

        return offerType;
    }

    public void setOperCode(String operCode)
    {

        this.operCode = operCode;
    }

    public String getOperCode()
    {

        return operCode;
    }

    public static EcPackageData getInstance(IData packageData)
    {

        if (packageData == null)
            return null;
        EcPackageData ecPackage = new EcPackageData();

        String operCode = packageData.getString("OPER_CODE");
        if (StringUtils.isEmpty(operCode))
        {
            operCode = packageData.getString("MODIFY_TAG");
        }

        ecPackage.setOperCode(operCode);
        ecPackage.setOfferId(packageData.getString("OFFER_ID"));
        ecPackage.setOfferType(packageData.getString("OFFER_TYPE"));
        ecPackage.setUpOfferId(packageData.getString("UP_OFFER_ID"));

        IDataset chaSpecs = packageData.getDataset("SERV_PARAM");
        if (IDataUtil.isNotEmpty(chaSpecs))
        {
            for (Object chaSpec : chaSpecs)
            {
                ecPackage.addChaSpec(ChaModData.getInstance((IData)chaSpec));
            }
        }

        return ecPackage;

    }
}
