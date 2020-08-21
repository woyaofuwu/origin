
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupInfoQueryDAO;

public class QueryVPMNExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        IDataset dataset = new DatasetList();
        String mode = inParam.getString("cond_QueryModeVPMN");
        if (StringUtils.isBlank(mode))
        {
            CSAppException.apperr(GrpException.CRM_GRP_725);
        }
        String vpnNo = inParam.getString("cond_VPN_NO");
        String sn = inParam.getString("cond_SERIAL_NUMBER");
        String removeTag = inParam.getString("cond_REMOVE_TAG");
        IData param = new DataMap();
        param.put("VPN_NO", vpnNo);
        param.put("SERIAL_NUMBER", sn);
        param.put("REMOVE_TAG", removeTag);

        if ("0".equals(mode))
        { // 按VPMN编码
            IData userInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(vpnNo);
            if (IDataUtil.isNotEmpty(userInfo))
            {
                param.put("USER_ID", userInfo.get("USER_ID"));
                dataset = GroupInfoQueryDAO.qryVpmnInfoExport(param, pg);
            }

        }
        else if ("1".equals(mode))
        { // 成员服务号码

            IData userInfo = UserInfoQry.getUserInfoBySN(sn);
            String mebRouteId = Route.getCrmDefaultDb();
            if (IDataUtil.isNotEmpty(userInfo))
            {
                mebRouteId = userInfo.getString("EPARCHY_CODE");
                String mebUserId = userInfo.getString("USER_ID");
                param.put("MEB_USER_ID", mebUserId);
                IDataset uuds = GroupInfoQueryDAO.qryVpmnInfoRelationByRouteId(param, pg, mebRouteId);
                if (IDataUtil.isNotEmpty(uuds))
                {
                    IData data = new DataMap();
                    data = uuds.getData(0);
                    param.put("USER_ID", data.get("USER_ID_A"));
                    dataset = GroupInfoQueryDAO.qryVpmnInfoExport(param, pg);
                }
            }
        }

        if (IDataUtil.isNotEmpty(dataset))
        {

            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                IData data = dataset.getData(i);
                String startDate = data.getString("START_DATE", "");
                String endDate = data.getString("END_DATE", "");
                data.put("START_DATE", !"".equals(startDate) ? SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND) : "");
                data.put("END_DATE", !"".equals(endDate) ? SysDateMgr.decodeTimestamp(endDate, SysDateMgr.PATTERN_STAND) : "");

            }
        }
        return dataset;

    }
}
