
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;

public class BatGrpMemGfffCreateTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        // 构建产品参数信息
        IData productParam = new DataMap();
        
        productParam.put("NOTIN_SMS_FLAG", condData.getString("NOTIN_SMS_FLAG"));
        productParam.put("NOTIN_sendForSms", condData.getString("NOTIN_sendForSms"));
        productParam.put("NOTIN_SmsInfo", condData.getString("NOTIN_SmsInfo"));
        
        String payEndDate = condData.getString("NOTIN_PAY_END_DATE");
        if (StringUtils.isNotEmpty(payEndDate)){
            productParam.put("NOTIN_PAY_END_DATE", payEndDate);
        }
        
        String payLimitFee = condData.getString("NOTIN_PAY_LIMIT_FEE");
        if (StringUtils.isNotEmpty(payLimitFee)){
            productParam.put("NOTIN_PAY_LIMIT_FEE", payLimitFee);
        }
        
        IDataset productParamDataset = new DatasetList();
        GroupBatTransUtil.buildProductParam(batData.getString("PRODUCT_ID"), productParam, productParamDataset);

        svcData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }

}
