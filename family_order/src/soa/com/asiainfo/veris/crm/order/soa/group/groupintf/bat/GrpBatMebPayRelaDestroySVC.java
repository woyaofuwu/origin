
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class GrpBatMebPayRelaDestroySVC extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    private static final String SERVICE_NAME = "SS.MebPayRelaDestroySVC.mebPayRelaDestroy";

    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;
    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        // 校验成员特殊付费信息
        validateMebPayReal(batData);
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("PAY_LIST", batData.getDataset("PAY_LIST"));
        svcData.put("USER_ID", getGrpUcaData().getUserId()); // 集团用户ID
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber()); // 成员服务号码
    }

    /**
     * 校验成员特殊付费信息
     * 
     * @param batData
     * @throws Exception
     */
    public void validateMebPayReal(IData batData) throws Exception
    {
        String serialNumber = IDataUtil.getMandaData(batData, "SERIAL_NUMBER"); // 成员服务号码

        String grpAcctId = IDataUtil.getMandaData(batData, "DATA1"); // 集团账户ID

        String grpUserId = IDataUtil.getMandaData(batData, "DATA2"); // 集团用户ID

        String payItemCode = IDataUtil.getMandaData(batData, "DATA3"); // 付费账目

        // 校验集团用户信息
        IData param = new DataMap();
        param.put("USER_ID", grpUserId);

        chkGroupUCAByUserId(param);

        // 校验成员用户信息
        param.clear();
        param.put("SERIAL_NUMBER", serialNumber);

        chkMemberUCABySerialNumber(param);

        String userId = getMebUcaData().getUser().getUserId();

        // 查询成员特殊付费信息
        IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(userId, grpUserId, grpAcctId, payItemCode);

        // 查询成员非默认付费信息
        IDataset userPayList = PayRelaInfoQry.qryPayRelaByUserAcctIdDefTag(userId, grpAcctId, "0");

        IDataset payList = new DatasetList();

        for (int i = 0, iRow = userPayList.size(); i < iRow; i++)
        {
            IData userPayData = userPayList.getData(i);

            for (int j = 0, jRow = userSpecialPayList.size(); j < jRow; j++)
            {
                IData userSpecialPyaData = userSpecialPayList.getData(j);

                if (userPayData.getString("PAYITEM_CODE", "").equals(userSpecialPyaData.getString("PAYITEM_CODE")) && userPayData.getString("LIMIT", "").equals(userSpecialPyaData.getString("LIMIT"))
                        && userPayData.getString("LIMIT_TYPE", "").equals(userSpecialPyaData.getString("LIMIT_TYPE")))
                {
                    payList.add(userPayData);
                }
            }
        }

        batData.put("PAY_LIST", payList);
    }

}
