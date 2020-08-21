
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.bean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.unit.BatDealBBossUnit;

/**
 * 行业应用卡批量，包括 主办省上传成员[行业应用卡] 配合省反馈成员开通结果[行业应用卡]
 * 
 * @author fanti3
 */
public class BatDealBBossHYYYKBean
{

    private String merchpSpecCode = "911601"; // 行业应用卡产品编码，IBOSS扫表时，标识业务

    /**
     * @param
     * @desciption 行业应用卡多批量启动
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:37:11
     */
    public IDataset startBBossHYYYKBatDeals(String batchId, String batchOperType) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        if (batchOperType.equals("BATADDHYYYKMEM"))
        {
            // 行业应用卡批量处理
            fileNameList = startHYYYKBatDeal(batchId);
        }
        else if (batchOperType.equals("BATOPENHYYYKMEM"))
        {
            // 行业应用卡配合省反馈成员开通结果
            fileNameList = startHYYYKOpenBatDeal(batchId);
        }

        return fileNameList;
    }

    /**
     * @param
     * @desciption 行业应用卡 主办省上传成员明细文件 批量处理
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:37:00
     */
    public IDataset startHYYYKBatDeal(String batch_id) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        IData inparams = new DataMap();
        inparams.put("BATCH_ID", batch_id);

        // 初始化批量任务ID
        String batchTaskId = "";

        // 查询批量明细
        IDataset bats = BatTradeInfoQry.queryBBossMemberInfosByBidSn(inparams, Route.getJourDb(Route.CONN_CRM_CG));

        String ibsysid = SeqMgr.getCenIbSysId();
        for (int i = 0, size = bats.size(); i < size; i++)
        {
            /** 构造绑定对象，按顺序绑定参数值 */
            IData param = new DataMap();
            IData bat = bats.getData(i);

            batchTaskId = bat.getString("BATCH_TASK_ID");

            param.put("ORG_DOMAIN", "BOSS");
            param.put("IBSYSID_SUB", SeqMgr.getCenIbSysSubId());
            param.put("IBSYSID", ibsysid);
            param.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

            // 省代码
            param.put("PROVCODE", bat.getString("DATA1"));
            // 信息类型
            param.put("INFO_TYPE", bat.getString("DATA2"));
            // 成员号码
            param.put("MSISDN", bat.getString("SERIAL_NUMBER"));
            // 套餐要求
            param.put("FEEPLAN", bat.getString("DATA11"));
            // 成员操作类型
            param.put("OPERCODE", bat.getString("DATA4"));
            // 新开卡数量
            param.put("NEWUSERCOUNT", bat.getString("DATA3"));
            // 成员功能需求
            param.put("PRODINFO", bat.getString("DATA12"));

            Dao.insert("TI_B_BBOSS_USER", param, Route.CONN_CRM_CEN);
        }

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("BATCH_ID", batch_id);
        data.put("STATUS", "1");
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("REMARK", "");
        // 生成行业应用卡批量文件名
        String fileName = BatDealBBossUnit.getMemAttachFileName();
        fileNameList.add(fileName);
        data.put("FILE_NAME", fileName);
        data.put("ORG_DOMAIN", "BOSS");
        data.put("FILE_TYPE", "1");
        data.put("RSRV_STR3", merchpSpecCode); // 和IBOSS约定，用来标识行业应用卡业务

        Dao.insert("TI_B_BBOSS", data);
        // 更新状态
        BatDealBBossUnit.updateYDZFBatState(batch_id);

        // 更新批量任务表备注信息，把批量文件名丢备注
        IData taskParam = new DataMap();
        taskParam.put("BATCH_TASK_ID", batchTaskId);
        taskParam.put("REMARK", fileName);
        BatDealBBossUnit.updateBatTaskByBatchTaskId(taskParam);

        return fileNameList;
    }

    /**
     * @param
     * @desciption 行业应用卡 配合省反馈成员开通结果批量处理
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:36:50
     */
    public IDataset startHYYYKOpenBatDeal(String batch_id) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        IData inparams = new DataMap();
        inparams.put("BATCH_ID", batch_id);

        // 初始化批量任务ID
        String batchTaskId = "";

        // 查询批量明细
        IDataset bats = BatTradeInfoQry.queryBBossMemberInfosByBidSn(inparams, Route.getJourDb(Route.CONN_CRM_CG));

        String ibsysid = SeqMgr.getCenIbSysId();
        for (int i = 0, size = bats.size(); i < size; i++)
        {
            /** 构造绑定对象，按顺序绑定参数值 */
            IData param = new DataMap();
            IData bat = bats.getData(i);

            batchTaskId = bat.getString("BATCH_TASK_ID");

            param.put("ORG_DOMAIN", "BOSS");
            param.put("IBSYSID_SUB", SeqMgr.getCenIbSysSubId());
            param.put("IBSYSID", ibsysid);
            param.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

            // 省代码
            param.put("PROVCODE", bat.getString("DATA1"));
            // 成员号码
            param.put("MSISDN", bat.getString("SERIAL_NUMBER"));
            // 代付关系处理是否成功
            param.put("CENTROLPAYSTATUS", bat.getString("DATA2"));
            // 处理失败原因
            param.put("FAILDESC", bat.getString("DATA11"));
            // 是否新开卡成员
            param.put("ISNEWUSER", bat.getString("DATA3"));
            // 未能足量完成新开卡和建立代付关系的原因
            param.put("NEWUSERFAILDESC", bat.getString("DATA12"));
            // 业务操作处理是否成功
            param.put("RSRV_STR2", bat.getString("DATA2"));
            // 该用户操作失败原因
            param.put("RSRV_STR3", bat.getString("DATA11"));

            Dao.insert("TI_B_BBOSS_USER", param, Route.CONN_CRM_CEN);
        }

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("BATCH_ID", batch_id);
        data.put("STATUS", "1");
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("REMARK", "");
        // 生成行业应用卡批量文件名
        String fileName = BatDealBBossUnit.getMemAttachFileName();
        fileNameList.add(fileName);
        data.put("FILE_NAME", fileName);
        data.put("ORG_DOMAIN", "BOSS");
        data.put("FILE_TYPE", "2");
        data.put("RSRV_STR3", merchpSpecCode); // 和IBOSS约定，用来标识行业应用卡业务

        Dao.insert("TI_B_BBOSS", data);
        // 更新状态
        BatDealBBossUnit.updateYDZFBatState(batch_id);

        // 更新批量任务表备注信息，把批量文件名丢备注
        IData taskParam = new DataMap();
        taskParam.put("BATCH_TASK_ID", batchTaskId);
        taskParam.put("REMARK", fileName);
        BatDealBBossUnit.updateBatTaskByBatchTaskId(taskParam);

        return fileNameList;
    }

}
