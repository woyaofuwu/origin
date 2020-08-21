
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserClusterInfoQry
{

    /**
     * @Function: getMemberInfo
     * @Description:查询家庭客户成员信息 used
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-4-26 上午9:44:09 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 lijm3 v1.0.0 修改原因
     */
    public static IDataset getMemberInfo(String group_id, String x_tag, Pagination pagination) throws Exception
    {

        String familyid = group_id;
        String xTag = x_tag;
        if ("".equals(familyid))
        {
            return new DatasetList();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" select a.* from tf_f_user_cluster_rela a where 1=1");
        if ("0".equals(xTag))
        {
            sb.append(" and (sysdate between a.start_date and a.end_date) ");
        }
        sb.append(" and a.group_id = ? order by role_code ");
        // Dao.qryBySql(sb, new String[] { familyid }, pagination);
        IData paramData = new DataMap();
        paramData.put("familyid", familyid);

        return Dao.qryBySql(sb, paramData, pagination);
    }
}
