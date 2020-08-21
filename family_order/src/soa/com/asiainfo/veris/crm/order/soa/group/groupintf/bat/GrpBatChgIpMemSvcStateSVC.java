
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class GrpBatChgIpMemSvcStateSVC extends GroupBatService
{

    private static final long serialVersionUID = -3934405392816441949L;

    private static String SERVICE_NAME = "SS.ChangeSvcStateRegSVC.tradeReg"; // 个人业务侧提供停开机服务

    @Override
    public void batInitialSub(IData batData) throws Exception
    {

        svcName = SERVICE_NAME;
    }

    public void batValidateSub(IData batData) throws Exception
    {

        String opType = condData.getString("OPER_TYPE");

        if (StringUtils.isBlank(opType))
        {
            CSAppException.apperr(BatException.CRM_BAT_75); // 操作类型错误!
        }

        int iType = Integer.parseInt(opType);

        switch (iType)
        {
            case 0:
            {// 开机
                batData.put("TRADE_TYPE_CODE", "133");
                break;
            }
            case 1:
            {// 停机
                batData.put("TRADE_TYPE_CODE", "131");
                break;
            }
            default:
            {
                CSAppException.apperr(BatException.CRM_BAT_76); // 没有找到对应的操作类型！
            }
        }
    }

    /**
     * 重写 暂时让它不调规则
     */
    public void builderRuleData(IData batData) throws Exception
    {

    }

    /**
     * 调个人接口传serial_number和trade_type_code
     */
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("TRADE_TYPE_CODE", batData.getString("TRADE_TYPE_CODE"));
    }

}
