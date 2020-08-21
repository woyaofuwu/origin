
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
 * 一点支付批量，包括 主办省上传成员[一点支付] 配合省反馈成员确认结果[一点支付] 配合省反馈成员开通结果[一点支付]
 * 
 * @author fanti3
 */
public class BatDealBBossYDZFBean
{

    private String merchpSpecCode = "99903"; // 一点支付产品编码，IBOSS扫表时，标识业务

    /**
     * @param
     * @desciption 一点支付多批量启动
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:38:12
     */
    public IDataset startBBossYDZFBatDeals(String batchId, String batchOperType) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        if (batchOperType.equals("BATADDYDZFMEM"))
        {
            // 一点支付批量处理
            fileNameList = startYDZFBatDeal(batchId);
        }
        else if (batchOperType.equals("BATCONFIRMYDZFMEM"))
        {
            // 一点支付配合省反馈成员确认结果
            fileNameList = startYDZFConfirmBatDeal(batchId);
        }
        else if (batchOperType.equals("BATOPENYDZFMEM"))
        {
            // 一点支付配合省反馈成员开通结果
            fileNameList = startYDZFOpenBatDeal(batchId);
        }

        return fileNameList;
    }

    /**
     * @param
     * @desciption 一点支付批量处理 成员批量处理
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:38:02
     */
    public IDataset startYDZFBatDeal(String batch_id) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        IData inparams = new DataMap();
        inparams.put("BATCH_ID", batch_id);

        // 初始化批量任务ID
        String batchTaskId = "";

        // 查询批量明细
        IDataset bats = BatTradeInfoQry.queryBBossMemberInfosByBidSn(inparams, Route.getJourDb(Route.CONN_CRM_CG));

        // 生成该批量批次号
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
            param.put("FEEPLAN", bat.getString("DATA4"));
            // 成员操作类型
            param.put("OPERCODE", bat.getString("DATA7"));
            // 户名调查要求
            param.put("ACCOUNTNAMEREQ", bat.getString("DATA8"));
            // 支付类型
            param.put("PAYTYPE", bat.getString("DATA5"));
            // 支付额度
            param.put("PAYAMOUNT", bat.getString("DATA6"));
            // 账期生效规则
            param.put("EFFRULE", bat.getString("DATA9"));
            // 新开卡数量
            param.put("NEWUSERCOUNT", bat.getString("DATA3"));

            Dao.insert("TI_B_BBOSS_USER", param, Route.CONN_CRM_CEN);
        }

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("BATCH_ID", batch_id);
        data.put("STATUS", "1");
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("REMARK", "");
        // 生成一点支付批量文件名
        String fileName = BatDealBBossUnit.getMemAttachFileName();
        fileNameList.add(fileName);
        data.put("FILE_NAME", fileName);
        data.put("ORG_DOMAIN", "BOSS");
        data.put("FILE_TYPE", "1");
        data.put("RSRV_STR3", merchpSpecCode); // 和IBOSS约定，用来标识一点支付业务

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
     * @desciption 一点支付配合省反馈成员确认结果
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:37:39
     */
    public IDataset startYDZFConfirmBatDeal(String batch_id) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        IData inparams = new DataMap();
        inparams.put("BATCH_ID", batch_id);

        // 初始化批量任务ID
        String batchTaskId = "";

        // 查询批量明细
        IDataset bats = BatTradeInfoQry.queryBBossMemberInfosByBidSn(inparams, Route.getJourDb(Route.CONN_CRM_CG));

        // 生成该批量批次号
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
            // 户名是否匹配
            param.put("NAMEMATCH", bat.getString("DATA2"));
            // 当前套餐
            param.put("CURRFEEPLAN", bat.getString("DATA3"));
            // 用户状态
            param.put("USERSTATUS", bat.getString("DATA4"));

            Dao.insert("TI_B_BBOSS_USER", param, Route.CONN_CRM_CEN);
        }

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("BATCH_ID", batch_id);
        data.put("STATUS", "1");
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("REMARK", "");
        // 生成一点支付批量文件名
        String fileName = BatDealBBossUnit.getMemAttachFileName();
        fileNameList.add(fileName);
        data.put("FILE_NAME", fileName);
        data.put("ORG_DOMAIN", "BOSS");
        data.put("FILE_TYPE", "2");
        data.put("RSRV_STR3", merchpSpecCode); // 和IBOSS约定，用来标识一点支付业务

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
     * @desciption 一点支付配合省反馈成员开通结果
     * @author fanti
     * @version 创建时间：2014年9月4日 下午4:37:27
     */
    public IDataset startYDZFOpenBatDeal(String batch_id) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        IData inparams = new DataMap();
        inparams.put("BATCH_ID", batch_id);

        // 初始化批量任务ID
        String batchTaskId = "";

        // 查询批量明细
        IDataset bats = BatTradeInfoQry.queryBBossMemberInfosByBidSn(inparams, Route.getJourDb(Route.CONN_CRM_CG));

        // 生成该批量批次号
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
            // 套餐要求
            param.put("FEEPLAN", bat.getString("DATA9"));
            // 支付类型
            param.put("PAYTYPE", bat.getString("DATA6"));
            // 支付额度
            param.put("PAYAMOUNT", bat.getString("DATA7"));
            // 账期生效规则
            param.put("EFFRULE", bat.getString("DATA3"));
            // 代付关系处理是否成功
            param.put("CENTROLPAYSTATUS", bat.getString("DATA2"));
            // 当前户名
            param.put("ACCOUNTNAME", bat.getString("DATA4"));
            // 处理失败原因
            param.put("FAILDESC", bat.getString("DATA5"));
            // 是否新开卡成员
            param.put("ISNEWUSER", bat.getString("DATA8"));
            // 未能足量完成新开卡和建立代付关系的原因
            param.put("NEWUSERFAILDESC", bat.getString("DATA10"));

            Dao.insert("TI_B_BBOSS_USER", param, Route.CONN_CRM_CEN);
        }

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("BATCH_ID", batch_id);
        data.put("STATUS", "1");
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("REMARK", "");
        // 生成一点支付批量文件名
        String fileName = BatDealBBossUnit.getMemAttachFileName();
        fileNameList.add(fileName);
        data.put("FILE_NAME", fileName);
        data.put("ORG_DOMAIN", "BOSS");
        data.put("FILE_TYPE", "3");
        data.put("RSRV_STR3", merchpSpecCode); // 和IBOSS约定，用来标识一点支付业务

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
