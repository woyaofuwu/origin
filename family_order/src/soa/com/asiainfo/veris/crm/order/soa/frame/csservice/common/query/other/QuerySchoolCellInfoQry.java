
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public class QuerySchoolCellInfoQry extends CSBizBean
{
    /**
     * 查询校园卡名称
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset querySchoolCards() throws Exception
    {
        IData params = new DataMap();
        return Dao.qryByCode("TD_B_DISCNT", "SEL_SCHOOL_CARD", params);
    }

    public static IDataset querySchoolCellInfo(String discntCode, String lac, String cellId, Pagination pagination) throws Exception
    {
		IDataset fileNames=UItemBInfoQry.qryOfferChaValByFieldNameOfferCodeAndOfferType(discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT, "10000000");
		String attrFileCode="";
		if(IDataUtil.isNotEmpty(fileNames)){
		 attrFileCode = fileNames.getData(0).getString("ATTR_FIELD_CODE");
 
		 }    	
        IData params = new DataMap();
        params.put("DISCNT_CODE", discntCode);
        params.put("LAC", lac);
        params.put("CELL_ID", cellId);
   	    params.put("SPEC_AREA_ID", attrFileCode);

       // return Dao.qryByCode("TD_B_DISCNT", "SEL_CELL_INFO_SCHOOL_NEW", params, pagination, Route.CONN_CRM_CEN);
        IDataset resutls=Dao.qryByCode("TD_B_DISCNT", "SEL_CELL_INFO_SCHOOL_NEW1", params,pagination,Route.CONN_CRM_CEN);    	    		

    	return resutls;
        
        
    }

}
