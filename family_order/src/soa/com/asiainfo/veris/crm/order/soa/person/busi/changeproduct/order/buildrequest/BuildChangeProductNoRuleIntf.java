package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**    
 * Copyright: Copyright  2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildChangeProductNoRuleIntf.java
 * @Description: 产品变更不校验规则数据构建类
 *
 * @version: v1.0.0
 * @author: maoke
 * @date: Oct 2, 2014 10:40:30 AM 
 *
 * Modification History:
 * Date            Author      Version        Description
 *-------------------------------------------------------*
 * Oct 2, 2014    maoke       v1.0.0           修改原因	
 */
public class BuildChangeProductNoRuleIntf extends BuildChangeProductIntf implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        super.buildBusiRequestData(param, brd);
    }
    
    /**
     * 重写不校验规则
     */
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {
    
    }
}
