
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 功能：用户IMSI信息查询 作者：GongGuang
 */
public class QueryUserImsiInfoQry extends CSBizBean
{
    /**
     * 功能：获取IMSI数据文件FTP配置参数
     */
    public static IDataset getImsiFtpParams(Pagination page) throws Exception
    {
        IData params = new DataMap();
        SQLParser parser = new SQLParser(params);
        parser.addSQL("select t.* from td_s_commpara t where 1 = 1");
        parser.addSQL(" and t.param_attr = 188");
        parser.addSQL(" and t.param_code = '0'");
        parser.addSQL(" and t.subsys_code = 'CSM'");
        return Dao.qryByParse(parser, page);
    }
}
