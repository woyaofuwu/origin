
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class GrpBatDelVpmnOutNetSVC extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    private static final String SERVICE_NAME = "SS.DelVpmnOutNetSVC.crtTrade";

    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;
    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        String grpSerialNumber = IDataUtil.getMandaData(condData, "SERIAL_NUMBER"); // 集团服务号码

        // 校验集团服务号码信息
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", grpSerialNumber);
        chkGroupUCABySerialNumber(param);

        // 判断网外号码是否属于集团
        String serialNumber = IDataUtil.getMandaData(batData, "SERIAL_NUMBER");

        IDataset relaList = RelaUUInfoQry.queryRelaUUBySnb(serialNumber, "41");

        if (IDataUtil.isEmpty(relaList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_8, serialNumber, getGrpUcaData().getSerialNumber());
        }
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("SERIAL_NUMBER", getGrpUcaData().getUser().getSerialNumber());
        svcData.put("OUT_SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("REMARK", "批量注销");
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
    }

}
