package com.asiainfo.veris.crm.order.soa.person.busi.bat.batreplysmsquery;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BatReplySmsQueryQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUnfinishTradeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QuerybatSmsImportQry;

public class BatReplySmsQueryBean extends CSBizBean{
	

    /**
     * 功能：未完工工单查询 作者：GongGuang
     */
    public IDataset batReplySmsQuery(IData data, Pagination page) throws Exception
    {
        String startDate = data.getString("START_DATE", "");
        String serialnumber = data.getString("SERIAL_NUMBER", "");
        String replyDate = data.getString("REPLY_DATE", "");
        String replyContent = data.getString("REPLY_CONTENT", "");
        
		IDataset dataSet = BatReplySmsQueryQry.batReplySmsQuery(startDate, serialnumber, replyDate, replyContent, page);
        return dataSet;
    }

}
