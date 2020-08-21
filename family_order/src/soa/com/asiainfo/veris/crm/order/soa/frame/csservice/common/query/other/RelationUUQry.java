
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RelationUUQry extends CSBizBean
{

	  public static IDataset getUserRelationByIdBDate(String useridb, String timepoint) throws Exception
	    {
	        IData param = new DataMap();
	        param.put("USER_ID_B", useridb);
	        param.put("TIME_POINT", timepoint);
	        IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_UUBB_BY_IDB_DATE", param);
	        return userRelationInfos;
	    }

}
