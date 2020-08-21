
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupInfoQueryDAO;

public class GroupMebAdcMasInfoExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        String isFlag = "".equals(inParam.getString("cond_IS_Flag", "")) ? "0" : "1"; // 是否查询历史 勾选为1
        String groupId = inParam.getString("POP_cond_GROUP_ID", "");

        IData param = new DataMap();
        param.put("IS_Flag", isFlag);
        param.put("GROUP_ID", groupId);
        IDataset dataset = new DatasetList();
        // 本想指定分页导出，平台支持不完善
        // boolean isCurPage = inParam.getBoolean("isCurPage");
        // int current = inParam.getInt("pageNav2_current"); // 当前页
        // int pageSize = inParam.getInt("pageNav2_pagesize"); // 每页条数
        // Pagination pag = new Pagination();
        // pag.setPageSize(pageSize);
        // pag.setCurrent(current);
        dataset = GroupInfoQueryDAO.qryProductMebInfoExport(param, pg);

        return dataset;
    }
}
