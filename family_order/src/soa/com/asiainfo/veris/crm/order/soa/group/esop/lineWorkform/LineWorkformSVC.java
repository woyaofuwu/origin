package com.asiainfo.veris.crm.order.soa.group.esop.lineWorkform;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class LineWorkformSVC extends CSBizService {

	private static final long serialVersionUID = 1L;
    public IDataset qryLineWorkform(IData param) throws Exception {
        IDataset lineWorkformList=LineWorkformBean.qryLineWorkformByCondition(param, getPagination());
        return lineWorkformList;
    }
}
