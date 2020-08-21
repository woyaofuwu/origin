
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

/**
 * 测试期 变更请求数据
 * 
 * @author xiekl
 */
public class ChgTestPeriodReqData extends BaseReqData
{
    protected ProductModuleData testingDiscnt;// 测试期优惠
    protected String testFlag;// 套餐类型标识

    public ProductModuleData getTestingDiscnt()
    {
        return testingDiscnt;
    }

    public void setTestingDiscnt(ProductModuleData testingDiscnt)
    {
        this.testingDiscnt = testingDiscnt;
    }
    
    public String getTestFlag()
    {
        return testFlag;
    }

    public void setTestFlag(String testFlag)
    {
        this.testFlag = testFlag;
    }

}
