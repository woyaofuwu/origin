
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.build;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.requestdata.ChgTestPeriodReqData;

public class BuildChgTestPeriod extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ChgTestPeriodReqData prd = (ChgTestPeriodReqData) brd;
        ProductModuleData testingDiscnt = new ProductModuleData();
        testingDiscnt.setElementId(param.getString("test_ELEMENT_ID"));
        testingDiscnt.setEndDate(param.getString("test_END_DATE"));
        testingDiscnt.setStartDate(param.getString("START_DATE"));
        prd.setTestFlag(param.getString("FLAG"));//0测试期，1正常期
        prd.setTestingDiscnt(testingDiscnt);
        // TODO Auto-generated method stub

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ChgTestPeriodReqData();
    }

}
