
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.ccsp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * 融合通信一级boss出参转换
 * 
 * @author zhangbo18
 */
public class CcspIbossOutFilter implements IFilterOut
{

    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        IData result = new DataMap();
        result.putAll(input);
        UcaData uca = btd.getRD().getUca();
    	result.put("ACCEPT_DATE", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        result.put("TRANS_ID", input.getString("TRANS_ID"));
        result.put("OPER_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        String brandCode = uca.getBrandCode();
        if ("G001".equals(brandCode))
        {
            brandCode = "01";
        }
        else if ("G010".indexOf(brandCode) > -1)
        {
            brandCode = "03";
        }
        else if ("G002_G003_G004_G006_G015_G021_G022_G023".indexOf(brandCode) > -1)
        {
            brandCode = "02";
        }
        else
        {
            brandCode = "09";
        }
        result.put("BRAND_CODE", brandCode);
        //接口返回报文中增加用户IMSI值
        if ("06".equals(input.getString("OPER_CODE"))
        		||"08".equals(input.getString("OPER_CODE"))){
	        IDataset userRes = UserResInfoQry.queryUserResByUserIdResType(uca.getUserId(), "1");
			if (null != userRes && !userRes.isEmpty()){
				IData ur = (IData) userRes.get(0);
				result.put("IMSI", ur.getString("IMSI"));
			}
        }
        return result;
    }

}
