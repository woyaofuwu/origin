
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;

public class BatGrpMemColorRingCreateTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        // 构建产品参数信息
        IData productParam = new DataMap();
        productParam.put("JOIN_IN", condData.getString("JOIN_IN"));
        productParam.put("CANCEL_LING", condData.getString("CANCEL_LING"));
        IDataset productParamDataset = new DatasetList();
        GroupBatTransUtil.buildProductParam(batData.getString("PRODUCT_ID"), productParam, productParamDataset);

        svcData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }

}
