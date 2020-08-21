
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class GrpBatAddVpmnOutNetSVC extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    private static final String SERVICE_NAME = "SS.AddVpmnOutNetSVC.crtTrade";

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

        // 查询集团用户VPN信息
        String grpUserId = getGrpUcaData().getUserId();

        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(grpUserId);

        if (IDataUtil.isEmpty(userVpnList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_6, grpUserId);
        }

        String maxOutNum = userVpnList.getData(0).getString("MAX_OUTNUM", "0");

        // 查询网外号码
        IDataset relaList = RelaUUInfoQry.getAllMebByUserIdA(grpUserId, "41");

        // 判断网外号码的数量是否达到最大值
        if (maxOutNum.equals(relaList.size()))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_7, getGrpUcaData().getSerialNumber());
        }

        // 校验网外号码
        validateOutSN(batData);

        // 校验网外号码短号
        validateOutShortCode(batData);
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("SERIAL_NUMBER", getGrpUcaData().getUser().getSerialNumber());
        svcData.put("OUT_SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("OUT_SHORT_CODE", batData.getString("DATA1"));
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
    }

    /**
     * 校验网外短号码
     * 
     * @param batData
     * @throws Exception
     */
    public void validateOutShortCode(IData batData) throws Exception
    {
        String shortCode = batData.getString("DATA1");

        if (StringUtils.isBlank(shortCode))
        {
            return;
        }

        // 短号长度校验
        int length = shortCode.length();

        if (length < 3 || length > 7)
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_53);
        }

        // 短号首位校验
        if (shortCode.substring(0, 1).matches("0|1|5|9"))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_9);
        }

        String grpUserId = getGrpUcaData().getUserId();

        // 判断短号是否已经存在
        IDataset relaList = RelaUUInfoQry.qryRelaOutNetInfo(grpUserId, null, shortCode, null, null, null);

        if (IDataUtil.isNotEmpty(relaList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_10);
        }

        // 查询母VPMN下面的子VPMN信息
        IDataset vpmnRelaList = RelaUUInfoQry.getRelaUUInfoByUserIdaForGrp(grpUserId, "40", null);

        // 判断在子VPMN信息中是否存在
        if (IDataUtil.isNotEmpty(vpmnRelaList))
        {
            for (int i = 0, row = vpmnRelaList.size(); i < row; i++)
            {
                String userIdB = vpmnRelaList.getData(i).getString("USER_ID_B");

                relaList = RelaUUInfoQry.qryRelaOutNetInfo(userIdB, null, shortCode, null, null, null);

                if (IDataUtil.isNotEmpty(relaList))
                {
                    CSAppException.apperr(VpmnUserException.VPMN_USER_11);
                }
            }
        }
    }

    /**
     * 校验网外号码信息
     * 
     * @param batData
     * @throws Exception
     */
    public void validateOutSN(IData batData) throws Exception
    {
        String serialNumber = IDataUtil.getMandaData(batData, "SERIAL_NUMBER");

        int length = serialNumber.length();

        if (length < 5 || length > 15)
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_52);
        }

        // 判断改服务号码是否已经是网外号码
        IDataset relaList = RelaUUInfoQry.queryRelaUUBySnb(serialNumber, "41");

        if (IDataUtil.isNotEmpty(relaList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_5, serialNumber, getGrpUcaData().getSerialNumber());
        }
    }

}
