
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.netnp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.WasteSmsCancelQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: UserNetNpMgrBean.java
 * @Description: 用户携转资料管理
 * @version: v1.0.0
 * @author: liuke
 * @date: 下午04:22:44 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-10-21 liuke v1.0.0 修改原因
 */
public class UserNetNpMgrBean extends CSBizBean
{

    private static void dealBlackKfInfos(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        IDataset blackInfos = WasteSmsCancelQry.queryIsBlackKf(param);
        if (IDataUtil.isNotEmpty(blackInfos))
        {
            IData cancelHigh = new DataMap();
            cancelHigh.put("SERIAL_NUMBER", serialNumber);
            cancelHigh.put("EPARCHY_CODE", blackInfos.getData(0).getString("EPARCHY_CODE"));
            cancelHigh.put("IDTYPE", "0");
            cancelHigh.put("SMS_CONTENT", "回收号码解除黑名单");
            cancelHigh.put("DEAL_TAG", "1");
            cancelHigh.put("IN_TIME", SysDateMgr.getSysTime());
            cancelHigh.put("RSRV_STR1", SysDateMgr.getSysDate("yyyyMMddHHmmssS"));
            cancelHigh.put("RSRV_STR2", CSBizBean.getVisit().getStaffId());
            cancelHigh.put("END_OFFSET", "2592000");
            Dao.insert("TI_BI_BLACK_KF", cancelHigh);
        }
    }

    /**
     * 终止携转记录
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset endUserNetNpInfo(String serialNumber) throws Exception
    {
        IDataset netNpInfos = UserInfoQry.getNetNPBySN(serialNumber, "0");// 0=正常记录
        if (IDataUtil.isNotEmpty(netNpInfos))
        {
            // 终止携转记录
            IData data = netNpInfos.getData(0);
            data.put("STATE", "1");// 失效
            data.put("PORT_OUT_DATE", SysDateMgr.getSysTime());// 当前时间
            data.put("REMARK", "号码回收终止携转记录");
            Dao.update("TF_F_USER_NETNP", data, new String[]
            { "INST_ID", "PARTITION_ID" }, Route.CONN_CRM_CEN);

            syncUserNetNpInfo(data);
        }

        dealBlackKfInfos(serialNumber);// 应资源的要求加入一个对黑名单的处理
        return new DatasetList();
    }

    /**
     * 同步数据给帐管
     * 
     * @throws Exception
     */
    private static void syncUserNetNpInfo(IData netNpInfo) throws Exception
    {

        String syncSeqId = SeqMgr.getSyncIncreId();
        String day = String.valueOf(Integer.valueOf(syncSeqId.substring(6, 8)));// 2013101562717229

        // 同步主表
        IData synchInfoData = new DataMap();
        synchInfoData.put("SYNC_SEQUENCE", syncSeqId);
        synchInfoData.put("SYNC_DAY", day);
        synchInfoData.put("SYNC_TYPE", "0");
        synchInfoData.put("TRADE_ID", "0");
        synchInfoData.put("STATE", "0");
        Dao.insert("TI_B_SYNCHINFO", synchInfoData);

        // 同步携转资料表
        IData synNetNpData = new DataMap();
        synNetNpData.putAll(netNpInfo);
        synNetNpData.put("SYNC_SEQUENCE", syncSeqId);
        synNetNpData.put("SYNC_DAY", day);
        synNetNpData.put("MODIFY_TAG", "2");
        synNetNpData.put("TRADE_ID", "0");
        Dao.insert("TI_B_USER_NETNP", synNetNpData);
    }
}
