package com.asiainfo.veris.crm.order.soa.group.esop.hang;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ScrDataSoaTransSVC extends CSBizService{

	private static final long serialVersionUID = 1L;

    public IDataset qrybuildWorkform(IData param) throws Exception
    {
    	ScrDataSoaTrans bean = new ScrDataSoaTrans();
        return bean.buildWorkform(param);
    }
}
