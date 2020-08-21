
package com.asiainfo.veris.crm.order.soa.person.busi.testcarduser.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.testcarduser.requestdata.TestCardUserReqData;

/**
 * REQ201609060001_2016年下半年测试卡功能优化（二）
 * @author zhuoyingzhi
 * 20160926
 *
 */
public class BuildTestCardUserReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData input, BaseReqData brd) throws Exception
    {
    	
    	TestCardUserReqData reqData=(TestCardUserReqData) brd;
    	
         reqData.setSerialNumber(input.getString("SERIAL_NUMBER"));//测试卡号
         reqData.setRsrvValue(input.getString("RSRV_VALUE"));//测试卡类型
        
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new TestCardUserReqData();
    }

}
