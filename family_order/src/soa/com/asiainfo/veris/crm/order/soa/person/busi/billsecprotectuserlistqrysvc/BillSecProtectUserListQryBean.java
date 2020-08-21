/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.billsecprotectuserlistqrysvc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.CrmDAO;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

/**
 * @CREATED
 */
public class BillSecProtectUserListQryBean extends CSBizBean
{
 
    /**
	 * 
     * zhengdx 20180629 
     * 计费安全保护用户清单查询
	 * */
	public static IDataset qryBillSecProtectUserList(String serialNumber,String discntCode,Pagination pagen) throws Exception
    {  
		IData param = new DataMap();
		
		param.put("REMOVE_TAG", "0");
		
		if (serialNumber!=null && ""!=serialNumber)
		{
		  param.put("SERIAL_NUMBER", serialNumber);
		 }
		
		if (discntCode!=null && ""!=discntCode){
			param.put("DISCNT_CODE", discntCode);
        }
          

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT   A.SERIAL_NUMBER,B.DISCNT_CODE PROTECT_TYPE_CODE,C.DATA_NAME PROTECT_TYPE_NAME FROM   TF_F_USER A,TF_F_USER_DISCNT B,TD_S_STATIC C WHERE 1=1 ");
        parser.addSQL(" AND A.REMOVE_TAG=:REMOVE_TAG ");
        parser.addSQL(" AND A.USER_ID=B.USER_ID ");
        parser.addSQL(" AND A.PARTITION_ID=B.PARTITION_ID ");
        parser.addSQL(" AND B.END_DATE>SYSDATE" );
        parser.addSQL(" AND B.DISCNT_CODE=TO_NUMBER(C.DATA_ID) ");
        parser.addSQL(" AND C.TYPE_ID='BILLING_SECURITY_PROTECTTON' ");
        
        if (serialNumber!=null && ""!=serialNumber){
            parser.addSQL(" AND A.SERIAL_NUMBER=:SERIAL_NUMBER");
        }
        
        if (discntCode!=null && ""!=discntCode){
        	parser.addSQL(" AND B.DISCNT_CODE=:DISCNT_CODE ");
        }
        	
        
        
        IDataset dataset = Dao.qryByParse(parser,pagen);
        
        return dataset;
        
        
    }
	
	
}
