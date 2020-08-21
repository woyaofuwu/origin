
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.order.buildrequestdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.order.requestdata.RestoreUserGHReqData;

public class BuildRestoreUserReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        RestoreUserGHReqData restoreUserRD = (RestoreUserGHReqData) brd;
        restoreUserRD.setX_coding_str(data.getString("X_CODING_STR"));
        restoreUserRD.setOpcValue(data.getString("OPC_VALUE", ""));
        restoreUserRD.setFreeSimcardFeeTag(data.getString("FREE_SIMCARD_FEE_TAG", ""));
        restoreUserRD.setScreat(data.getString("SECRET", ""));
        ProductData product = new ProductData(data.getString("PRODUCT_ID"));
        restoreUserRD.setMainProduct(product);

        buildElems(data, restoreUserRD);
    }

    public void buildElems(IData param, RestoreUserGHReqData brd) throws Exception
    {
        /* 拼装子元素 */
        String str = param.getString("SELECTED_ELEMENTS", "");
        if (StringUtils.isNotEmpty(str))
        {
            IDataset elems = new DatasetList(str);
            int len = elems.size();
            for (int i = 0; i < len; i++)
            {
                IData elem = elems.getData(i);
                String elemTypeCode = elem.getString("ELEMENT_TYPE_CODE", "");

                if ("D".equals(elemTypeCode))
                {
                    brd.addPmd(new DiscntData(elem));
                }
                else if ("S".equals(elemTypeCode))
                {
                    // 如果用户有这个服务，则不拼到requestData中
                    if (brd.getUca().checkUserIsExistSvcId(elem.getString("ELEMENT_ID", "")))
                    {
                        continue;
                    }
                    brd.addPmd(new SvcData(elem));
                }
            }
        }

    }

    // 重写构造UCA数据的方法
    public UcaData buildUcaData(IData param) throws Exception
    {
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE", "");
        if (StringUtils.equals(tradeTypeCode, "9706"))
        {
            String userId = param.getString("SELECTED_AUTH_USER", "");
            return UcaDataFactory.getDestroyUcaByUserId(userId);
        }
        else
        {
            String serialNumber = param.getString("SERIAL_NUMBER");
            return UcaDataFactory.getImproperUca(serialNumber);
        }
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new RestoreUserGHReqData();
    }
}
