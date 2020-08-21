package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.cmrb;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class CrmbIbossExceptionFilter implements IFilterException {

	@Override
	public IData transferException(Throwable e, IData input) throws Exception {
		IData result = new DataMap();
		result.putAll(input);
		
        UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
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
        result.put("PARA_NUM", "0");
        result.put("PARA_NAME", "");
        result.put("PARA_VALUE", "");
        
        String error =  Utility.parseExceptionMessage(e); 
        String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
        result.put("X_RESULTCODE", errorArray[0] );
        result.put("X_RESULTINFO", errorArray[1]);
        input.put("X_RSPDESC", errorArray[1]);
        result.put("X_RSPTYPE", "2");
        result.put("X_RSPCODE", "2998"); 
        
        
        return result;
	}

}
