
package com.asiainfo.veris.crm.order.soa.person.busi.userother;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserOtherSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

	protected static Logger log = Logger.getLogger(UserOtherSVC.class);

    public IDataset insertUserOther(IData param) throws Exception
    {
       
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
		boolean flag = Dao.insert("TF_F_USER_OTHER", param);
		if(flag){
			data.put("FLAG", "TRUE");
	        dataset.add(data);
		}
        return dataset;
    }
 
}