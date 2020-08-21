package com.asiainfo.veris.crm.order.soa.person.busi.bat.batsmsimportquery;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUnfinishTradeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QuerybatSmsImportQry;

public class BatSmsImportQueryBean extends CSBizBean{
	

    /**
     * 功能：未完工工单查询 作者：GongGuang
     */
    public IDataset batSmsImportQuery(IData data, Pagination page) throws Exception
    {
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String servicearea = data.getString("SERVICE_AREA", "");
        String projectname = data.getString("PROJECT_NAME", "");
        String noticecontent = data.getString("NOTICE_CONTENT", "");
        IDataset dataSet = QuerybatSmsImportQry.batSmsImportQuery(serialNumber, servicearea, projectname, noticecontent, page);
        return dataSet;
    }

}
