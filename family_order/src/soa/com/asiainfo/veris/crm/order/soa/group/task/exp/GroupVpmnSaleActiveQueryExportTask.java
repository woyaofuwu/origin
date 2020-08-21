
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AsynDealVisitUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.VpmnSaleActiveQry;

public class GroupVpmnSaleActiveQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {

        AsynDealVisitUtil.dealVisitInfo(inParam);

        // 推荐号码
        String serial_number = inParam.getString("cond_SERIAL_NUMBER", "");

        // 活动类型
        String activetype = inParam.getString("cond_ACTIVE_TYPE", "");

        IData uisParam = new DataMap();
        uisParam.put("SERIAL_NUMBER", serial_number);
        uisParam.put("REMOVE_TAG", "0");
        uisParam.put("NET_TYPE_CODE", "00");
        IDataset userInfoset = CSAppCall.call("CS.UserInfoQrySVC.queryUserInfoBySN", uisParam);

        IData userInfo = new DataMap();
        if (IDataUtil.isNotEmpty(userInfoset))
        {
            userInfo = userInfoset.getData(0);
        }
        else
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_118, serial_number);
        }

        IDataset dataset = VpmnSaleActiveQry.queryVPMNSaleActiveByUserIdAActype(userInfo.getString("USER_ID", ""), activetype, userInfo.getString("EPARCHY_CODE"), pg);
        if (IDataUtil.isEmpty(dataset))
        {
            // "没有获取到集团V网营销活动资料数据！"
            CSAppException.apperr(VpmnUserException.VPMN_USER_120);
        }

        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData vpmnData = dataset.getData(i);
            String rsrvDate = vpmnData.getString("RSRV_DATE2", "");
            String giveData = vpmnData.getString("GIVE_DATE", "");
            String giveTag = vpmnData.getString("GIVE_TAG", "");
            String updStaffId = vpmnData.getString("UPDATE_STAFF_ID", "");
            String updDepartId = vpmnData.getString("UPDATE_DEPART_ID", "");
            vpmnData.put("RSRV_DATE2", !"".equals(rsrvDate) ? SysDateMgr.decodeTimestamp(rsrvDate, SysDateMgr.PATTERN_STAND_YYYYMMDD) : "");
            vpmnData.put("GIVE_DATE", !"".equals(giveData) ? SysDateMgr.decodeTimestamp(giveData, SysDateMgr.PATTERN_STAND_YYYYMMDD) : "");
            vpmnData.put("GIVE_TAG", "1".equals(giveTag) ? "已赠送" : "未赠送");
            vpmnData.put("UPDATE_STAFF_ID", !"".equals(updStaffId) ? UStaffInfoQry.getStaffNameByStaffId(updStaffId) : "");
            vpmnData.put("UPDATE_DEPART_ID", !"".equals(updDepartId) ? UDepartInfoQry.getDepartNameByDepartId(updDepartId) : "");
        }
        return dataset;
    }

}
