package com.asiainfo.veris.crm.order.soa.person.busi.selfhelpnew;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;


public class NewTerminalReconQueryBean extends CSBizBean {

	static Logger logger=Logger.getLogger(NewTerminalReconQueryBean.class);
	
    public IDataset queryPayOrder(IData param ,Pagination pagination) throws Exception{
        
    	SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.UUID,T.TRANS_ACTION_ID,T.SERIAL_NUMBER,T.ORDER_NO,T.ACCOUNT_MONEY,T.PAYMENT,T.ORDER_MONNEY,T.GIFT,T.BUSI_TYPE,T.CHARGE_MONEY,T.PAYMENT_TYPE,T.PAY_TRANS,T.PAY_STATUS,T.ORDER_STATUS_CODE,T.ORDER_STATUS_DESC,T.REFUND_ORDER_NO,T.REFUND_FEE,T.SETTLE_DATE,T.PRODUCT_NAME,T.HOME_PROV,T.IS_REFUND,T.HALL_CODE,T.OP_ID,TO_CHAR(T.ADD_TIME,'YYYY-MM-DD HH24:MI:SS') ADD_TIME,T.ACCEPT_MONTH,T.REC_RESULT,TO_CHAR(T.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,T.DEAL_STATE,T.DEAL_INFO,T.REFUND_DATE ");
        parser.addSQL(" FROM TF_B_PAYORDER_RECRESULT T WHERE 1 = 1 ");
        parser.addSQL(" AND T.UPDATE_TIME BETWEEN TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:FINISH_DATE,'YYYY-MM-DD HH24:MI:SS') ");
        if(!param.getString("REC_RESULT").isEmpty()){
			parser.addSQL(" AND T.REC_RESULT = :REC_RESULT ");
		}			   
        parser.addSQL(" ORDER BY T.UPDATE_TIME ");
        IDataset dataset = Dao.qryByParse(parser,pagination);
    	return dataset;    	
    }
}