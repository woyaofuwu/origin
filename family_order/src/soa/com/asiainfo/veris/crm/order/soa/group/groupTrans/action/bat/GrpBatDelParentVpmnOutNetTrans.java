
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class GrpBatDelParentVpmnOutNetTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        String grpSerialNumber = IDataUtil.getMandaData(condData, "SERIAL_NUMBER"); // 集团服务号码

        // 校验集团服务号码信息
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", grpSerialNumber);
        // chkGroupUCABySerialNumber(param);
        UcaData grpUcaData = UcaDataFactory.getNormalUcaBySnForGrp(param);

        // 判断网外号码是否属于集团
        String serialNumber = IDataUtil.getMandaData(batData, "SERIAL_NUMBER");

        int length = serialNumber.length();

        if (length < 5 || length > 15)
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_52);
        }

        IDataset relaList = RelaUUInfoQry.queryRelaUUBySnb(serialNumber, "41");

        if (IDataUtil.isEmpty(relaList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_8, serialNumber, grpUcaData.getSerialNumber());
        }
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        // 集团服务号码
        svcData.put("SERIAL_NUMBER", condData.getString("SERIAL_NUMBER"));

        // 网外号码
        svcData.put("OUT_SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));

        svcData.put("REMARK", "批量注销");

        svcData.put(Route.ROUTE_EPARCHY_CODE, batData.getString("EPARCHY_CODE"));
    }
}
