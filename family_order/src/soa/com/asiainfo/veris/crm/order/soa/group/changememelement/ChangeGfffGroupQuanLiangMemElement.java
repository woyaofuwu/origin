
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeGfffGroupQuanLiangMemElement extends ChangeMemElement
{

    public ChangeGfffGroupQuanLiangMemElement()
    {

    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
       
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

    }
    
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);
    }
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }
    
}
