
package com.asiainfo.veris.crm.order.soa.group.groupTrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUserTrans;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.parse.ElementInfoParse;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.parse.ElementParamsParse;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.util.GroupElementParamsUtil;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.util.GroupProductUtil;

public class CreateGroupUserTransHAIN extends CreateGroupUserTrans
{
    // 重载基类
    public void checkRequestData(IData iData) throws Exception
    {
        super.checkRequestData(iData);

        String productId = iData.getString("PRODUCT_ID");

        GroupProductUtil.checkProductCanDo(iData.getString("OPER_TYPE"), productId);
    }

    // 重写基类
    protected void parseElement(IData iData) throws Exception
    {

        ElementInfoParse.parseElmentInfo(iData);

    }

    // 重写基类
    protected void parseElementParams(IData idata) throws Exception
    {
        ElementParamsParse.parseElmentParams(idata);
    }

    // 重写基类
    protected void parseProductParams(IData idata) throws Exception
    {
        // 2.1-处理默认产品参数信息
        String productId = IDataUtil.chkParam(idata, "PRODUCT_ID");// 集团
        String mebBaseProductId = ProductMebInfoQry.getMemberMainProductByProductId(productId);

        IData savePParamData = new DataMap();

        IDataset productParamData = new DatasetList();
        IData inProductParam = idata.getData("PRODUCT_PARAM", new DataMap());

        savePParamData.put("PRODUCT_ID", productId);
        inProductParam = inProductParam.getData(productId);
        productParamData = GroupElementParamsUtil.qryElementParamByElementId(inProductParam, productId, "P", idata.getString("OPER_TYPE"));

        savePParamData.put("PRODUCT_PARAM", productParamData);
        idata.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(savePParamData));
    }

}
