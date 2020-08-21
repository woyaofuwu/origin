package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.migu;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * 和生活一级boss入参转换
 * 
 * @author songxw
 */
public class MiguIbossFilter implements IFilterIn
{
    @Override
    public void transferDataInput(IData input) throws Exception
    {
        input.put("OPR_SOURCE", input.getString("CHANNEL"));
    }
}
