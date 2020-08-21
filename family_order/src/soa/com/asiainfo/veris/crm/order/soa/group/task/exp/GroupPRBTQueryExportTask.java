
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class GroupPRBTQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {

        String queryType = inParam.getString("cond_QUERY_TYPE", "");
        String snA = inParam.getString("cond_SERIAL_NUMBER", "").trim();
        String snB = inParam.getString("cond_SERIAL_NUMBER_B", "").trim();
        String state = inParam.getString("cond_STATE", "0");
        IData param = new DataMap();
        param.put("QUERY_TYPE", queryType);
        param.put("SERIAL_NUMBER", snA);
        param.put("SERIAL_NUMBER_B", snB);
        param.put("STATE", state);
        IDataset dataset = new DatasetList();
        if (!"".equals(snA))
        {// 按产品编码和手机号码查询
            IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(snA);
            if (IDataUtil.isEmpty(grpUserInfo))
            {
                return null;
            }
            String grpUserId = grpUserInfo.getString("USER_ID");
            param.put("USER_ID_A", grpUserId);
            if (!"".equals(snB))
            {// 按产品编码和手机号码查询
                dataset = RelaUUInfoQry.qryGroupPRBTInfo(param);
            }
            else
            {// 按产品编码查询
                dataset = RelaUUInfoQry.qryGroupPRBTByProductId(param);
            }
        }
        else if (!"".equals(snB) && "".equals(snA))
        {// 按手机号码查询
            String routeId = RouteInfoQry.getEparchyCodeBySnForCrm(snB);
            dataset = RelaUUInfoQry.qryGroupPRBTBySN(param, routeId);
        }
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_724);
        }
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData data = dataset.getData(i);
            String startDate = data.getString("START_DATE", "");
            String endDate = data.getString("END_DATE", "");
            String cityCode = data.getString("CITY_CODE", "").trim();
            data.put("START_DATE", !"".equals(startDate) ? SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND) : "");
            data.put("END_DATE", !"".equals(endDate) ? SysDateMgr.decodeTimestamp(endDate, SysDateMgr.PATTERN_STAND) : "");
            data.put("CITY_CODE", !"".equals(cityCode) ? UAreaInfoQry.getAreaNameByAreaCode(cityCode) : "");
        }
        return dataset;
    }

}
