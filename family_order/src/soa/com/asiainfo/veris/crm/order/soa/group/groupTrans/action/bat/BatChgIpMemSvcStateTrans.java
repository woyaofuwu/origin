
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;

public class BatChgIpMemSvcStateTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        IDataUtil.chkParam(batData, "SERIAL_NUMBER");

        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

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

        // 拼调服务所需参数
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("TRADE_TYPE_CODE", batData.getString("TRADE_TYPE_CODE"));

        svcData.put("REAL_SVC_NAME", "SS.ChangeSvcStateRegSVC.tradeReg"); // 个人业务侧提供停开机服务
    }

}
