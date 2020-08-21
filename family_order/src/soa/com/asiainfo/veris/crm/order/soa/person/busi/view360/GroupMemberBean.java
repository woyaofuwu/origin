
package com.asiainfo.veris.crm.order.soa.person.busi.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.GroupMemberDAO;

public class GroupMemberBean extends CSBizBean
{
    /**
     * 查询集团名称供用户360首页使用，按雇佣关系->兼职关系取数据
     */
    public IDataset queryGroupName(IData param, Pagination pagination) throws Exception
    {
        GroupMemberDAO dao = new GroupMemberDAO();
        return dao.queryGroupName(param, pagination);
    }
}
