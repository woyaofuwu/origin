
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.simcard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class SimCardCheckMgr extends CSBizBean
{

    public static IData simCardCheck(IData input) throws Exception
    {
        DataMap output = new DataMap();
        String resNo = input.getString("NEW_SIM_CARD_NO", "");

        // IDataset simSet = ResCall.getSimCardInfo("0", resNo, "", "IDLE", getVisit());
        IDataset simSet = new DatasetList();
        IData simData = new DataMap();
        simData.put("SIM_CARD_NO", "89860123451234512345");
        simData.put("SIM_TYPE_CODE", "M");
        simData.put("RES_STATE_CODE", "2");
        simData.put("OCCUPY_TYPE_CODE", "0"); // 选占类型编码
        simData.put("RANDOM_NO", "430524199900909989"); // 选占证件号
        simData.put("OCCUPY_STAFF_ID", "SUPERUSR");
        simData.put("OCCUPY_DEPART_ID", "00000");
        simData.put("VALID_TIME", "1");
        simData.put("EPARCHY_CODE", "0898");
        simData.put("REMARK", "换卡");
        simData.put("RSRV_TAG1", "");
        simData.put("RSRV_TAG2", "");
        simData.put("RSRV_TAG3", "");
        simData.put("RSRV_DATE1", "");
        simData.put("RSRV_DATE2", "");
        simData.put("RSRV_DATE3", "");
        simData.put("RSRV_STR1", "");
        simData.put("RSRV_STR2", "");
        simData.put("RSRV_STR3", "");
        simData.put("RSRV_NUM1", "0");
        simData.put("RSRV_NUM2", "0");
        simData.put("RSRV_NUM3", "0");
        simData.put("IMSI", "460029104704420");
        simData.put("CAPACITY_TYPE_CODE", "3");
        simData.put("X_RESULTCODE", "0");
        simData.put("OPC", "0");
        simData.put("CARD_KIND_CODE", "1B3");
        simSet.add(simData);

        IData simCardInfo = simSet.getData(0);
        String newSimTypeCode = simCardInfo.getString("SIM_TYPE_CODE", "");

        output.put("IMSI", simCardInfo.getString("IMSI", ""));
        output.put("SIM_CARD_NO", simCardInfo.getString("SIM_CARD_NO", ""));
        output.put("KI", simCardInfo.getString("KI", ""));
        output.put("CAPACITY_TYPE_CODE", simCardInfo.getString("CAPACITY_TYPE_CODE", ""));
        output.put("OPC", simCardInfo.getString("OPC"));
        output.put("CARD_KIND_CODE", simCardInfo.getString("CARD_KIND_CODE", ""));// sim的小类编码
        output.put("SIM_TYPE_CODE", simCardInfo.getString("SIM_TYPE_CODE", ""));// sim的小类编码
        // 物联网修改，参数翻译 TODO

        output.put("AGENT_SALE_TAG", simCardInfo.getString("RSRV_STR3", ""));// 该白卡是否为代理商空卡买断。

        // NG2.0 SIM卡带初始密码需求 yangkx 2010-08-16
        output.put("NEW_SIM_CARD_PASSWD", simCardInfo.getString("RSRV_STR6", "")); // 密文
        output.put("NEW_SIM_CARD_PASSWD_KEY", simCardInfo.getString("RSRV_STR5", "")); // 密钥
        // end yangkx 2010-08-16

        String newEmptyCardId = simCardInfo.getString("EMPTY_CARD_ID", "");
        // 如果SIM卡表中EMPTY_CARD_ID字段不为空，表明该卡由白卡写成，到白卡表中取卡类型
        output.put("NEW_EMPTY_CARD_FLAG", false); // 由白卡写成
        if (!newEmptyCardId.equals("") && !"U".equals(newSimTypeCode) && !"X".equals(newSimTypeCode))
        {
            IDataset emptyCardInfo = ResCall.getEmptycardInfo(resNo, "", "USE");
            if (IDataUtil.isEmpty(emptyCardInfo))
            {
                output.put("SIM_TYPE_CODE", emptyCardInfo.getData(0).getString("SIM_TYPE_CODE", ""));
                output.put("CAPACITY_TYPE_CODE", emptyCardInfo.getData(0).getString("CAPACITY_TYPE_CODE", ""));
                // add by zhangxing for HNYD-REQ-20100225-002关于开发代理商白卡空卡买断的报表统计需求
                output.put("NEW_EMPTY_CARD_ID", newEmptyCardId);
                output.put("NEW_AGENT_SALE_TAG", emptyCardInfo.getData(0).getString("RSRV_STR2", ""));// 该白卡是否为代理商空卡买断。
                output.put("NEW_EMPTY_CARD_FLAG", true); // 由白卡写成
            }
        }
        // add by wenhj HNYD-REQ-20110402-010 start
        output.put("SIM_FEE_TAG", input.getString("FEE_TAG", ""));
        output.put("SIM_CARD_SALE_MONEY", "" + input.getInt("SALE_MONEY", 0));
        output.put("SIM_EMPTY_TYPE_CODE", output.getString("SIM_TYPE_CODE"));
        // add by wenhj HNYD-REQ-20110402-010 end
        return output;
    }

    // 检查sim卡是否是4Gusim
    public static IData verifySimcardUSIM(IData input) throws Exception
    {
        String simTypeCode = input.getString("NEW_SIM_TYPE_CODE");

        return input;

    }

}
