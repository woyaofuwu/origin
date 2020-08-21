
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CommParaDAO
{
    /**
     * 查询符合条件的参数列表，为处理PROCESS_TAG_SET参数
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getCommPara(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT para_code2,para_code3,para_code4 from td_s_commpara where 1 = 1 ");
        parser.addSQL(" and subsys_code = :SUBSYS_CODE");
        parser.addSQL(" and param_attr = :PARAM_ATTR");
        parser.addSQL(" and param_code = :PARAM_CODE");
        parser.addSQL(" and eparchy_code = :EPARCHY_CODE");
        return Dao.qryByParse(parser);
    }
}
