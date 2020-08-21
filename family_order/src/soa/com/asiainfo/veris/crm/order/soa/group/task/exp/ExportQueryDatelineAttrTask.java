package com.asiainfo.veris.crm.order.soa.group.task.exp;

import java.util.Iterator;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.group.esop.workform.WorkFormBean;

public class ExportQueryDatelineAttrTask extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData inParam, Pagination var2) throws Exception {
        // TODO Auto-generated method stub
        IDataset eomsInfos = WorkFormBean.queryDatelineAttr(inParam);
        if (IDataUtil.isNotEmpty(eomsInfos)) {
            // 统计专线详情数量
            Iterator<Object> eomsInfo = eomsInfos.iterator();
            while (eomsInfo.hasNext()) {
                IData eomsDateline = (IData) eomsInfo.next();
                String dataLineSum = eomsDateline.getString("DATELINE_SUM", "");
                if (StringUtils.isNotBlank(dataLineSum)) {
                    eomsInfo.remove();
                }
            }
        }
        return eomsInfos;
    }

}
