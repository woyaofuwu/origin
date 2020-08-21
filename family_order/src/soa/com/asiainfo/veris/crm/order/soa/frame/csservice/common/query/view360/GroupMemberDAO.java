
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GroupMemberDAO
{
    /**
     * 查询集团名称供用户360首页使用，按雇佣关系->兼职关系取数据
     * 
     * @param param
     * @return
     */
    public IDataset queryGroupName(IData param, Pagination pagination) throws Exception
    {

        String user_id = param.getString("USER_ID", "");

        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT GROUP_CUST_NAME FROM TF_F_CUST_GROUPMEMBER ");
        parser.addSQL(" WHERE REMOVE_TAG='0' AND USER_ID=:USER_ID and partition_id =mod(to_number(:USER_ID),10000)  ORDER BY MEMBER_RELA ");
        return Dao.qryByParse(parser, pagination);

    }
}
