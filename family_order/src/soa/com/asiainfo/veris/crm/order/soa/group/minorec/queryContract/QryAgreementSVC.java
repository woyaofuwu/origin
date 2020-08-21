package com.asiainfo.veris.crm.order.soa.group.minorec.queryContract;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class QryAgreementSVC extends CSBizService{

    /**
     *
     */
    private static final long serialVersionUID = -5015406099262325272L;

    public IData qryAgreementInfo(IData param) throws Exception{
        return QryAgreementBean.qryAgreementInfo(param);
    }

    public IDataset qryAgreementAttachInfo(IData param) throws Exception{
        return QryAgreementBean.qryAgreementAttachInfo(param);
    }

    public IDataset qryAgreementDef(IData param) throws Exception{
        return QryAgreementBean.qryAgreementDef(param);
    }

    public IData qryGrpInfoByGrpId(IData param) throws Exception{
        String groupId = param.getString("GROUP_ID");

        IData data = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        //IDataset dataset = IDataUtil.idToIds(data);

        return data;
    }


    public IDataset queryTestProductTypeAndInfo(IData param) throws Exception{
        String type = param.getString("TEST_TYPE");
        IDataset dataset = new DatasetList();
        if(type.equals("1")){
            String parentPtypeCode = param.getString("PARENT_PTYPE_CODE");
            dataset = UpcCall.qryCatalogsByUpCatalogId(parentPtypeCode);
            if(dataset!=null && dataset.size()>0){
                for (int i = 0, size = dataset.size(); i < size; i++)
                {
                    IData catalog = dataset.getData(i);
                    catalog.put("PRODUCT_TYPE_CODE", catalog.getString("CATALOG_ID"));
                    catalog.put("PRODUCT_TYPE_NAME", catalog.getString("CATALOG_NAME"));
                }
            }
        }else{
            String productTypeCode = param.getString("PRODUCT_TYPE_CODE");

            dataset = UpcCall.qryOffersByCatalogId(productTypeCode);

            if (DataUtils.isNotEmpty(dataset))
            {
                for (int i = 0, size = dataset.size(); i < size; i++)
                {
                    IData offer = dataset.getData(i);
                    offer.put("PRODUCT_ID", offer.getString("OFFER_CODE"));
                    offer.put("PRODUCT_NAME", offer.getString("OFFER_NAME"));
                }
            }
        }
        return dataset;
    }

}
