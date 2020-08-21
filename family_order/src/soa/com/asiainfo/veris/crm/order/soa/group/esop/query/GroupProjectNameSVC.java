package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GroupProjectNameSVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;

    public static IDataset qryProjectName(IData param) throws Exception
    {
	    return GroupProjectNameBean.qryProjectName(param);
    }

    public static void insertProjectName(IData param) throws Exception
    {
	    GroupProjectNameBean.insertProjectName(param);
    }
}
