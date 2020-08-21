package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.onecertificatefiveno;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class OnecertificatefiveNoQry {
	/**
	 * 根据条件获取一证五号差异记录
	 * @param tradeDate
	 * @param tel
	 * @param page
	 * @return
	 * @throws Exception
	 * @author huangzl3
	 */
	public static IDataset queryData(String PHONENUMBER,String SRECONDATE,String ERECONDATE,String COMPRESULT,String SYNRESULT,Pagination page)throws Exception{
		 IDataset list=new DatasetList();
		 IData params = new DataMap();
		 params.put("PHONE_NUMBER", PHONENUMBER);
		 params.put("SRECONDATE",SRECONDATE);
		 params.put("ERECONDATE",ERECONDATE);
		 params.put("COMP_RESULT",COMPRESULT);
		 params.put("SYN_RESULT",SYNRESULT);
	     SQLParser parser = new SQLParser(params);
	     parser.addSQL("SELECT  ");
	     parser.addSQL("A.IBSYSID,A.RECON_DATE,A.PHONE_NUMBER,A.CID_CATEGORY,A.CID_CODE,A.NAME_CODE,A.COMP_RESULT,A.SYN_RESULT,A.FILE_NAME,A.UPDATE_TIME");
	     parser.addSQL(" FROM TF_F_USER_ALL_OPENLIMIT_RESULT A ");
	     parser.addSQL(" WHERE 1=1 ");
	     if(!"".equals(PHONENUMBER)){
	    	 parser.addSQL(" AND A.PHONE_NUMBER = :PHONE_NUMBER");
	     }
	     if(!"".equals(SRECONDATE)){
	    	 parser.addSQL(" AND TO_DATE(A.RECON_DATE,'YYYY-MM-DD HH24:MI:SS') >= TO_DATE(:SRECONDATE,'YYYY-MM-DD HH24:MI:SS') ");
	     }
	     if(!"".equals(ERECONDATE)){
	    	 parser.addSQL(" AND TO_DATE(A.RECON_DATE,'YYYY-MM-DD HH24:MI:SS') <= TO_DATE(:ERECONDATE,'YYYY-MM-DD HH24:MI:SS')");
	     }
	     if(!"".equals(COMPRESULT)){
	    	 parser.addSQL(" AND A.COMP_RESULT = :COMP_RESULT");
	     }
	     if(!"".equals(SYNRESULT)){
	    	 parser.addSQL(" AND A.SYN_RESULT = :SYN_RESULT");
	     }
	     list = Dao.qryByParse(parser, page);
	     return  list;
	}
	
	public static int updateData(String SYNRESULT, String PHONENUMBER, String COMPRESULT) throws Exception{ 
		 IData params = new DataMap();
		 params.put("SYN_RESULT", SYNRESULT);
		 params.put("PHONE_NUMBER", PHONENUMBER);
		 params.put("COMP_RESULT",COMPRESULT);
		 return Dao.executeUpdateByCodeCode("TF_F_USER_ALL_OPENLIMIT_RESULT", "UPDATE_ACCOUNTDIFFER_DATA", params);
	}
}
