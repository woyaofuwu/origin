
package com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans.SaleActiveTransBean;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans.order.requestdata.SaleActiveTransReqData;

public class SaleActiveTransReqDataBuilder extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        SaleActiveTransReqData transReqData = (SaleActiveTransReqData) brd;

        SaleActiveTransBean saleActiveTransBean = new SaleActiveTransBean();
        saleActiveTransBean.buildBaseRequestData(transReqData, param);
        saleActiveTransBean.buildTargetUserActiveDate(transReqData);
        saleActiveTransBean.buildSourceActiveDate(transReqData);

        IDataset targetSelectedElements = saleActiveTransBean.buildTartgetSelectedElements(transReqData);
        transReqData.setTargetSelectedElements(targetSelectedElements);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new SaleActiveTransReqData();
    }

}
