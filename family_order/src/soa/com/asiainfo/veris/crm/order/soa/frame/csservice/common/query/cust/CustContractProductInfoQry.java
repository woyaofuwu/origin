
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

public class CustContractProductInfoQry
{

    /**
     * 通过合同ID查询合同产品信息
     *
     * @param contractId
     * @return
     * @throws Exception
     */
    public static IDataset qryContractProductByContId(String contractId) throws Exception
    {
        IData param = new DataMap();
        param.put("CONTRACT_ID", contractId);

        IDataset conProductSet = Dao.qryByCode("TF_F_CUST_CONTRACT_PRODUCT", "SEL_CP_BY_CONID", param);
        if (IDataUtil.isEmpty(conProductSet))
        {
            return conProductSet;
        }
        IDataset productInfos = UProductInfoQry.getProductNamesCataLogIdsByProductInfos(conProductSet);
        if (IDataUtil.isEmpty(productInfos))
        {
            return new DatasetList();
        }
        int size = productInfos.size();
        for (int i = conProductSet.size() - 1; i >= 0 ; i--)
        {
            IData map = conProductSet.getData(i);
            int index= 0;
            for (int j = 0; j < size; j++)
            {
                IData productInfoData =  productInfos.getData(j);
                if (!StringUtils.equals(productInfoData.getString("OFFER_CODE"), map.getString("PRODUCT_ID")))
                {
                    index++;
                    continue;
                }
                if (StringUtils.isBlank(productInfoData.getString("OFFER_NAME")) || StringUtils.isBlank(productInfoData.getString("CATALOG_ID")))
                {
                    conProductSet.remove(i);// 产品下线了 捕获异常删了就行
                    break;
                }
                map.put("PRODUCT_NAME", productInfoData.getString("OFFER_NAME"));
                map.put("PRODUCT_TYPE_CODE", productInfoData.getString("CATALOG_ID"));
                break;
            }
            if (index==size)
            {
                conProductSet.remove(i);// 产品下线了 捕获异常删了就行
            }
        }
        return conProductSet;
    }
    
    public static void insert(IData data) throws Exception
    {
        Dao.insert("TF_F_CUST_CONTRACT_PRODUCT", data, Route.CONN_CRM_CG);
    }

    public static IDataset qryContractProductByContIdCustId(String contractId,String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("CONTRACT_ID", contractId);
        param.put("CUST_ID", custId);
        IDataset conProductSet = Dao.qryByCode("TF_F_CUST_CONTRACT_PRODUCT", "SEL_CP_BY_CONIDCUSTID", param);
        if (IDataUtil.isEmpty(conProductSet))
        {
            return conProductSet;
        }
        IDataset productInfos = UProductInfoQry.getProductNamesCataLogIdsByProductInfos(conProductSet);
        if (IDataUtil.isEmpty(productInfos))
        {
            return new DatasetList();
        }
        int size = productInfos.size();
        for (int i = conProductSet.size() - 1; i >= 0 ; i--)
        {
            IData map = conProductSet.getData(i);
            int index= 0;
            for (int j = 0; j < size; j++)
            {
                IData productInfoData =  productInfos.getData(j);
                if (!StringUtils.equals(productInfoData.getString("OFFER_CODE"), map.getString("PRODUCT_ID")))
                {
                    index++;
                    continue;
                }
                if (StringUtils.isBlank(productInfoData.getString("OFFER_NAME")) || StringUtils.isBlank(productInfoData.getString("CATALOG_ID")))
                {
                    conProductSet.remove(i);// 产品下线了 捕获异常删了就行
                    break;
                }
                map.put("PRODUCT_NAME", productInfoData.getString("OFFER_NAME"));
                map.put("PRODUCT_TYPE_CODE", productInfoData.getString("CATALOG_ID"));
                break;
            }
            if (index==size)
            {
                conProductSet.remove(i);// 产品下线了 捕获异常删了就行
            }
        }
        return conProductSet;
    }

}
