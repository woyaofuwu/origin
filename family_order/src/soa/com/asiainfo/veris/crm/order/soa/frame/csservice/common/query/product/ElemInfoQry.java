package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class ElemInfoQry
{
	/*三代无用代码删除*/
    /*public static IDataset getDescInfo(String elemId, String elemType, String chanId) throws Exception
    {
        IData data = new DataMap();
        data.put("ELEMENT_ID", elemId);
        data.put("ELEMENT_TYPE_CODE", elemType);
        data.put("CHANNEL_ID", chanId);
        return Dao.qryByCode("TD_B_ELEMENT_DESC", "SEL_ALL_BY_ELEMENT_ID_TYPE", data, Route.CONN_CRM_CEN);

    }*/

    public static String getElemNameByElemTypeCodeId(String elemTypeCode, String elemId) throws Exception
    {
        if (elemTypeCode.equals("D"))
        {
            return UDiscntInfoQry.getDiscntNameByDiscntCode(elemId);
        }
        else if (elemTypeCode.equals("S"))
        {
            return USvcInfoQry.getSvcNameBySvcId(elemId);
        }
        else if (elemTypeCode.equals("Z"))
        {
            return UPlatSvcInfoQry.getSvcNameBySvcId(elemId);
        }

        return "";
    }

    public static void fillElementName(IDataset elements) throws Exception
    {
        if (IDataUtil.isEmpty(elements))
        {
            return;
        }

        IDataset offer_type_code_list = new DatasetList();
        for (Object object : elements)
        {
            IData elementData = (IData) object;
            IData data = new DataMap();
            data.put("OFFER_CODE", elementData.getString("ELEMENT_ID"));
            data.put("OFFER_TYPE", elementData.getString("ELEMENT_TYPE_CODE"));
            offer_type_code_list.add(data);
        }
        IDataset results = UpcCall.qryOfferNamesByOfferTypesOfferCodes(offer_type_code_list, null);

        if (IDataUtil.isEmpty(results))
        {
            return;
        }
        for (int i = 0, size = elements.size(); i < size; i++)
        {
            IData element = elements.getData(i);
            for (int j = 0, jsize = results.size(); j < jsize; j++)
            {
                IData result = results.getData(j);
                if (StringUtils.equals(element.getString("ELEMENT_TYPE_CODE"), result.getString("OFFER_TYPE")) 
                        && StringUtils.equals(element.getString("ELEMENT_ID"), result.getString("OFFER_CODE")))
                {
                    element.put("ELEMENT_NAME", result.getString("OFFER_NAME"));
                }
            }
        }
    }

}
