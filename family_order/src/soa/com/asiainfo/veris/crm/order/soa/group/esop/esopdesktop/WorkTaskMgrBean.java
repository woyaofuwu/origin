package com.asiainfo.veris.crm.order.soa.group.esop.esopdesktop;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.OpTaskHisInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.OpTaskInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.OpTaskInstHisQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.OpTaskInstInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeHBean;

/**
 * order
 * 待办工单管理类
 *
 * @author ckh
 * @date 2018/1/15
 */
public class WorkTaskMgrBean extends GroupBean
{

    private static Logger logger = Logger.getLogger(WorkTaskMgrBean.class);
    /**
     * 新增待办工作
     *
     * @param param
     * @return
     */
    public IData crtWorkTaskInfo(IData param) throws Exception
    {
        IData retData = new DataMap();
        retData.put("X_RESULTCODE", "-1");
        retData.put("X_RESULTINFO", "新增待办失败！");

        if (checkPortParamForCrt(param, retData))
        {
            return retData;
        }

        //截取字段，最长不超过100字符
        String topic = EsopDeskTopUtils.subStr(param.getString("INFO_TOPIC"), 500, "UTF-8");
        param.put("INFO_TOPIC", topic);

        addWorkTaskInfo(param);
        retData.put("X_RESULTCODE", "0");
        retData.put("X_RESULTINFO", "新增待办工作成功");

        return retData;
    }

    /**
     * 更新待办信息
     *
     * @param param
     * @return
     * @throws Exception
     */
    public IData updWorkTaskInfo(IData param) throws Exception
    {
        IData retData = new DataMap();
        retData.put("X_RESULTCODE", "-1");
        retData.put("X_RESULTINFO", "修改待办失败！");


        if (checkPortParamForUpd(param, retData))
        {
            return retData;
        }

        dealWorkTaskInfo(param);

        retData.put("X_RESULTCODE", "0");
        retData.put("X_RESULTINFO", "修改待办工作成功");
        return retData;
    }

    public IData updWorkTaskPlanTime(IData param) throws Exception
    {
        IData retData = new DataMap();

        OpTaskInfoQry.updateOpTaskInfoPlanTime(param);

        retData.put("X_RESULTCODE", "0");
        retData.put("X_RESULTINFO", "修改待办工作成功");
        return retData;
    }

    /**
     * 查询待办信息
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryWorkTaskInfo(IData param) throws Exception
    {
        return OpTaskInfoQry.queryOpTaskByStaffId(param.getString("STAFF_ID"));
    }

    public IDataset qryWorkTaskByStaffIdTaskTypeCodeTaskStatus(IData param, Pagination page) throws Exception
    {
        String staffId = param.getString("STAFF_ID");
        String taskTypeCode = param.getString("INFO_TYPE");
        String taskStatus = param.getString("INFO_STATUS");
        String receObjType = param.getString("RECE_OBJ_TYPE");
        return OpTaskInfoQry.queryOpTaskByStaffIdTaskTypeCodeTaskStatus(staffId, taskTypeCode, taskStatus, receObjType, page);
    }

    public IDataset queryOpTaskList(IData param, Pagination page) throws Exception
    {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT SUBSTR(A.INFO_URL, ");
        sql.addSQL("               INSTR(A.INFO_URL, 'BPM_TEMPLET_ID=') + LENGTH('BPM_TEMPLET_ID='),");
        sql.addSQL("               INSTR(A.INFO_URL, '&', INSTR(A.INFO_URL, 'BPM_TEMPLET_ID=')) - ");
        sql.addSQL("               (INSTR(A.INFO_URL, 'BPM_TEMPLET_ID=') + LENGTH('BPM_TEMPLET_ID='))) BPM_TEMPLET_ID, ");
        sql.addSQL("        SUBSTR(A.INFO_URL, ");
        sql.addSQL("               INSTR(A.INFO_URL, 'BUSIFORM_ID=') + LENGTH('BUSIFORM_ID='), ");
        sql.addSQL("               INSTR(A.INFO_URL, '&', INSTR(A.INFO_URL, 'BUSIFORM_ID=')) - ");
        sql.addSQL("               (INSTR(A.INFO_URL, 'BUSIFORM_ID=') + LENGTH('BUSIFORM_ID='))) BUSIFORM_ID,   ");
        sql.addSQL("        SUBSTR(A.INFO_URL, ");
        sql.addSQL("               INSTR(A.INFO_URL, 'NODE_ID=') + LENGTH('NODE_ID='), ");
        sql.addSQL("               INSTR(A.INFO_URL, '&', INSTR(A.INFO_URL, 'NODE_ID=')) - ");
        sql.addSQL("               (INSTR(A.INFO_URL, 'NODE_ID=') + LENGTH('NODE_ID='))) NODE_ID,  ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, INSTR(A.INFO_CONTENT, 'CRM订单号:') + LENGTH('CRM订单号:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, 'CRM订单号:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, 'CRM订单号:') + LENGTH('CRM订单号:'))) IBSYSID, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, INSTR(A.INFO_CONTENT, '集团编码:') + LENGTH('集团编码:'),  ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '集团编码:')) -  ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '集团编码:') + LENGTH('集团编码:'))) GROUP_ID, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '集团名称:') + LENGTH('集团名称:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '集团名称:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '集团名称:') + LENGTH('集团名称:'))) CUST_NAME, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '产品:') + LENGTH('产品:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '产品:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '产品:') + LENGTH('产品:'))) PRODUCT_NAME, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '业务类型:') + LENGTH('业务类型:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '业务类型:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '业务类型:') + LENGTH('业务类型:'))) OPER_TYPE, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '当前节点:') + LENGTH('当前节点:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '当前节点:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '当前节点:') + LENGTH('当前节点:'))) NODE_NAME, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '业务创建人:') + LENGTH('业务创建人:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '业务创建人:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '业务创建人:') + LENGTH('业务创建人:'))) STAFF_ID,  ");
        sql.addSQL("        A.INFO_ID, ");
        sql.addSQL("        A.INFO_SIGN, ");
        sql.addSQL("        A.INFO_TOPIC, ");
        sql.addSQL("        A.INFO_CHILD_TYPE, ");
        sql.addSQL("        A.INFO_TYPE, ");
        sql.addSQL("        A.INFO_STATUS, ");
        sql.addSQL("        A.INFO_LEVEL, ");
        sql.addSQL("        A.INFO_URL, ");
        sql.addSQL("        A.INFO_CONTENT, ");
        sql.addSQL("        A.INFO_AUTH, ");
        sql.addSQL("        TO_CHAR(A.INFO_SEND_TIME, 'YYYY-MM-DD HH24:MI:SS') INFO_SEND_TIME, ");
        sql.addSQL("        TO_CHAR(A.END_TIME, 'YYYY-MM-DD HH24:MI:SS') END_TIME, ");
        sql.addSQL("        B.INST_ID, ");
        sql.addSQL("        B.RECE_OBJ, ");
        sql.addSQL("        B.RECE_OBJ_TYPE, ");
        sql.addSQL("        B.INST_STATUS, ");
        sql.addSQL("        TO_CHAR(B.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.addSQL("        TO_CHAR(B.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE ");
        sql.addSQL("   FROM TF_F_INFO A, TF_F_INFO_INSTANCE B ");
        sql.addSQL("  WHERE A.INFO_ID = B.INFO_ID ");
        sql.addSQL("    AND A.INFO_TOPIC LIKE '%' || :INFO_TOPIC || '%' ");
        sql.addSQL("    AND A.INFO_CONTENT LIKE '%CRM订单号:' || :IBSYSID || '%' ");
        sql.addSQL("    AND A.INFO_CONTENT LIKE '%集团名称:' || :CUST_NAME || '%' ");
        sql.addSQL("    AND A.INFO_CONTENT LIKE '%集团编码:' || :GROUP_ID || '%' ");
        if(!"".equals(param.getString("INFO_KEYWORDS",""))){
        	sql.addSQL(" AND (A.INFO_TOPIC LIKE '%' || :INFO_KEYWORDS || '%'  or A.INFO_CONTENT LIKE '%' || :INFO_KEYWORDS || '%' )");
        }
        sql.addSQL("    AND A.INFO_TYPE = :INFO_TYPE ");
        sql.addSQL("    AND A.INFO_CHILD_TYPE = :INFO_CHILD_TYPE ");
        sql.addSQL("    AND A.INFO_STATUS = :INFO_STATUS ");
        sql.addSQL("    AND B.START_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD') ");
        sql.addSQL("    AND B.START_DATE < TO_DATE(:END_DATE, 'YYYY-MM-DD') + 1 ");
        sql.addSQL("    AND B.RECE_OBJ_TYPE = :RECE_OBJ_TYPE ");
        sql.addSQL("    AND B.RECE_OBJ = :STAFF_ID ");
        sql.addSQL("    AND B.INST_ID = :INST_ID ");
        sql.addSQL("  ORDER BY B.INST_ID DESC ");

        IDataset infos = Dao.qryByParse(sql, page, Route.CONN_CRM_CEN);

        if (IDataUtil.isNotEmpty(infos)) {
            getInfos(infos);
            return 	infos;
        }else {
            return new DatasetList();
        }

        //return OpTaskInfoQry.queryOpTaskList(param, page);
    }
    public static IDataset getInfos(IDataset infos) throws Exception {
        for (int i = 0; i < infos.size(); i++) {
            IData infoData = infos.getData(i);
            IData param = new DataMap();
            param.put("BPM_TEMPLET_ID", infoData.getString("BPM_TEMPLET_ID"));
            param.put("VALID_TAG", "0");

            IDataset bpmTempletInfos = Dao.qryByCode("TD_B_EWE_FLOW_TEMPLET", "SEL_BY_BPMTEMPLETID_VALID", param, Route.CONN_CRM_CEN);

            if (IDataUtil.isNotEmpty(bpmTempletInfos)) {
                infoData.put("TEMPLET_NAME", bpmTempletInfos.first().getString("TEMPLET_NAME"));
            }

            IData inparam = new DataMap();
            inparam.put("IBSYSID", infoData.getString("IBSYSID"));
            IDataset subscribeInfos = WorkformSubscribeHBean.getSubScribeInfoByIbsysid(inparam);

            if (IDataUtil.isNotEmpty(subscribeInfos)) {
                IData subscribeInfo =subscribeInfos.first();
                if ("B".equals(subscribeInfo.getString("TAB_TYPE"))) {
                    infoData.put("IS_FINISH", "false");
                }else if ("BH".equals(subscribeInfo.getString("TAB_TYPE"))) {
                    infoData.put("IS_FINISH", "true");
                }
            }
        }

        return infos;
    }

    public IDataset queryBusinessOpTaskList(IData param, Pagination page) throws Exception
    {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT SUBSTR(A.INFO_URL, ");
        sql.addSQL("               INSTR(A.INFO_URL, 'BPM_TEMPLET_ID=') + LENGTH('BPM_TEMPLET_ID='),");
        sql.addSQL("               INSTR(A.INFO_URL, '&', INSTR(A.INFO_URL, 'BPM_TEMPLET_ID=')) - ");
        sql.addSQL("               (INSTR(A.INFO_URL, 'BPM_TEMPLET_ID=') + LENGTH('BPM_TEMPLET_ID='))) BPM_TEMPLET_ID, ");
        sql.addSQL("        SUBSTR(A.INFO_URL, ");
        sql.addSQL("               INSTR(A.INFO_URL, 'BUSIFORM_ID=') + LENGTH('BUSIFORM_ID='), ");
        sql.addSQL("               INSTR(A.INFO_URL, '&', INSTR(A.INFO_URL, 'BUSIFORM_ID=')) - ");
        sql.addSQL("               (INSTR(A.INFO_URL, 'BUSIFORM_ID=') + LENGTH('BUSIFORM_ID='))) BUSIFORM_ID,   ");
        sql.addSQL("        SUBSTR(A.INFO_URL, ");
        sql.addSQL("               INSTR(A.INFO_URL, 'NODE_ID=') + LENGTH('NODE_ID='), ");
        sql.addSQL("               INSTR(A.INFO_URL, '&', INSTR(A.INFO_URL, 'NODE_ID=')) - ");
        sql.addSQL("               (INSTR(A.INFO_URL, 'NODE_ID=') + LENGTH('NODE_ID='))) NODE_ID,  ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, INSTR(A.INFO_CONTENT, 'CRM订单号:') + LENGTH('CRM订单号:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, 'CRM订单号:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, 'CRM订单号:') + LENGTH('CRM订单号:'))) IBSYSID, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, INSTR(A.INFO_CONTENT, '集团编码:') + LENGTH('集团编码:'),  ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '集团编码:')) -  ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '集团编码:') + LENGTH('集团编码:'))) GROUP_ID, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '集团名称:') + LENGTH('集团名称:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '集团名称:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '集团名称:') + LENGTH('集团名称:'))) CUST_NAME, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '产品:') + LENGTH('产品:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '产品:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '产品:') + LENGTH('产品:'))) PRODUCT_NAME, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '业务类型:') + LENGTH('业务类型:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '业务类型:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '业务类型:') + LENGTH('业务类型:'))) OPER_TYPE, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '当前节点:') + LENGTH('当前节点:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '当前节点:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '当前节点:') + LENGTH('当前节点:'))) NODE_NAME, ");
        sql.addSQL("        SUBSTR(A.INFO_CONTENT, ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '业务创建人:') + LENGTH('业务创建人:'), ");
        sql.addSQL("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '业务创建人:')) - ");
        sql.addSQL("               (INSTR(A.INFO_CONTENT, '业务创建人:') + LENGTH('业务创建人:'))) STAFF_ID,  ");
        sql.addSQL("        A.INFO_ID, ");
        sql.addSQL("        A.INFO_SIGN, ");
        sql.addSQL("        A.INFO_TOPIC, ");
        sql.addSQL("        A.INFO_CHILD_TYPE, ");
        sql.addSQL("        A.INFO_TYPE, ");
        sql.addSQL("        A.INFO_STATUS, ");
        sql.addSQL("        A.INFO_LEVEL, ");
        sql.addSQL("        A.INFO_URL, ");
        sql.addSQL("        A.INFO_CONTENT, ");
        sql.addSQL("        A.INFO_AUTH, ");
        sql.addSQL("        TO_CHAR(A.INFO_SEND_TIME, 'YYYY-MM-DD HH24:MI:SS') INFO_SEND_TIME, ");
        sql.addSQL("        TO_CHAR(A.END_TIME, 'YYYY-MM-DD HH24:MI:SS') END_TIME, ");
        sql.addSQL("        B.INST_ID, ");
        sql.addSQL("        B.RECE_OBJ, ");
        sql.addSQL("        B.RECE_OBJ_TYPE, ");
        sql.addSQL("        B.INST_STATUS, ");
        sql.addSQL("        TO_CHAR(B.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.addSQL("        TO_CHAR(B.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE ");
        sql.addSQL("   FROM TF_F_INFO A, TF_F_INFO_INSTANCE B ");
        sql.addSQL("  WHERE A.INFO_ID = B.INFO_ID ");
        sql.addSQL("    AND A.INFO_TOPIC LIKE '%' || :INFO_TOPIC || '%' ");
        sql.addSQL("    AND A.INFO_CONTENT LIKE '%CRM订单号:' || :IBSYSID || '%' ");
        sql.addSQL("    AND A.INFO_CONTENT LIKE '%集团名称:' || :CUST_NAME || '%' ");
        sql.addSQL("    AND A.INFO_CONTENT LIKE '%集团编码:' || :GROUP_ID || '%' ");
        sql.addSQL("    AND A.INFO_URL LIKE '%BPM_TEMPLET_ID=' || :BPM_TEMPLET_ID || '%' ");
        sql.addSQL("    AND A.INFO_TYPE = :INFO_TYPE ");
        sql.addSQL("    AND A.INFO_CHILD_TYPE = :INFO_CHILD_TYPE ");
        sql.addSQL("    AND A.INFO_STATUS = :INFO_STATUS ");
        sql.addSQL("    AND B.START_DATE >= TO_DATE(:START_DATE, 'YYYY-MM-DD') ");
        sql.addSQL("    AND B.START_DATE < TO_DATE(:END_DATE, 'YYYY-MM-DD') + 1 ");
        sql.addSQL("    AND B.RECE_OBJ_TYPE = :RECE_OBJ_TYPE ");
        sql.addSQL("    AND B.RECE_OBJ = :STAFF_ID ");
        sql.addSQL("    AND B.INST_ID = :INST_ID ");
        sql.addSQL("  ORDER BY B.INST_ID DESC ");

        IDataset infos = Dao.qryByParse(sql, page, Route.CONN_CRM_CEN);

        if (IDataUtil.isNotEmpty(infos)) {
            getInfos(infos);
            return 	infos;
        }else {
            return new DatasetList();
        }

        //return OpTaskInfoQry.queryOpTaskList(param, page);
    }

    public IDataset queryOpTaskHisList(IData param, Pagination page) throws Exception
    {
        return OpTaskHisInfoQry.queryOpTaskHisList(param, page);
    }

    public IDataset queryUnReadList(IData param, Pagination page) throws Exception
    {
        String staffId = param.getString("STAFF_ID");
        String taskTypeCode = param.getString("INFO_TYPE");
        String taskTopic = param.getString("INFO_TOPIC");
        String taskStatus = param.getString("INFO_STATUS");
        String receObjType = param.getString("RECE_OBJ_TYPE");
        return OpTaskInfoQry.queryUnReadList(staffId, taskTypeCode, taskTopic, taskStatus, receObjType, page);
    }

    public IDataset queryUnDoneWorkList(IData param, Pagination page) throws Exception
    {
        String staffId = param.getString("STAFF_ID");
        String taskTypeCode = param.getString("INFO_TYPE");
        String taskTopic = param.getString("INFO_TOPIC");
        String taskStatus = param.getString("INFO_STATUS");
        String receObjType = param.getString("RECE_OBJ_TYPE");
        return OpTaskInfoQry.queryUnDoneWorkList(staffId, taskTypeCode, taskTopic, taskStatus, receObjType, page);
    }

    public IDataset queryDoneWorkList(IData param, Pagination page) throws Exception
    {
        String staffId = param.getString("STAFF_ID");
        String taskTypeCode = param.getString("INFO_TYPE");
        String taskStatus = param.getString("INFO_STATUS");
        String receObjType = param.getString("RECE_OBJ_TYPE");
        return OpTaskHisInfoQry.queryOpTaskHisByConds(staffId, taskTypeCode, taskStatus, receObjType, page);
    }

    public IDataset queryOpTaskHisByInstId(IData param) throws Exception
    {
        String instId = param.getString("INST_ID");
        return OpTaskHisInfoQry.queryOpTaskHisByInstId(instId);
    }

    public IDataset queryWorkInfo(IData param) throws Exception
    {
        return OpTaskInfoQry.queryTaskInfo(param.getString("INST_ID"));
    }
    public IDataset queryWorkInfoByInfoSign(IData param) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_INFO", "SEL_BY_INFOSIGN", param, Route.CONN_CRM_CEN);
    }


    private void dealWorkTaskInfo(IData param) throws Exception
    {
        // 1-首先处理TF_F_OP_TASK数据
        IData updateTaskData = makOpTaskInfo(param, param.getString("INFO_ID", ""), param.getString("TASK_LEVEL", ""));

        // 2-更新TF_F_OP_TASK表
        OpTaskInfoQry.updateOpTaskInfo(updateTaskData);

        // 3-判断是否需要搬历史表
        String taskStatus = updateTaskData.getString("INFO_STATUS");
        if (EsopDeskTopConst.WORKINFO_STATUS_DONE.equals(taskStatus))
        {
            moveToHistory(updateTaskData);
        }
    }

    private void moveToHistory(IData updateTaskData) throws Exception
    {
        // 3.1-首先查询OP_TASK数据
        IDataset taskInfos = OpTaskInfoQry.queryOpTaskInfoByRelaIdOrTaskSign(updateTaskData.getString("RELA_ID"),
                updateTaskData.getString("INFO_SIGN"), updateTaskData.getString("INFO_TYPE"));

        if (DataUtils.isEmpty(taskInfos))
        {
            return;
        }

        // 3.2-搬OP_TASK历史表
        OpTaskHisInfoQry.insertOpTaskHisInfo(taskInfos);
        // 3.3-循环处理OP_TASK_INST数据
        for (int i = 0; i < taskInfos.size(); i++)
        {
            IData taskInfo = taskInfos.getData(i);
            // 3.3.1-查询OP_TASK_INST表
            IDataset instInfos = OpTaskInstInfoQry.queryOpTaskInstInfo(taskInfo.getString("INFO_ID"));
            // 3.3.2-插入OP_TASK_INST历史表
            OpTaskInstHisQry.insertOpTaskInstHis(instInfos);
        }
        // 3.3.3-删除OP_TASK_INST表
        OpTaskInstInfoQry.deleteOpTaskInstInfoByTaskIdBatch(taskInfos);

        // 3.3-删除OP_TASK表
        OpTaskInfoQry.deleteOpTaskInfoByPkBatch(taskInfos);
    }

    private boolean checkPortParamForUpd(IData param, IData retData)
    {
        String sign = param.getString("INFO_SIGN", "");
        if ("".equals(sign))
        {
            retData.put("X_RESULTCODE", "-1");
            retData.put("X_RESULTINFO", "INFO_SIGN不能为空！");
            return true;
        }
        String taskTypeCode = param.getString("INFO_TYPE", "");
        if ("".equals(taskTypeCode))
        {
            retData.put("X_RESULTCODE", "-1");
            retData.put("X_RESULTINFO", "INFO_TYPE不能为空！");
            return true;
        }
        return false;
    }

    private void addWorkTaskInfo(IData param) throws Exception
    {
        // 赋值到存储对象中
        String taskId = "";//SeqMgr.getOpTaskId();
        String taskLevel = StringUtils.isEmpty(
                param.getString("TASK_LEVEL", "")) ? EsopDeskTopConst.INFO_LEVEL_1 : param.getString("TASK_LEVEL");
        IDataset recvObjs = param.getDataset("RECE_OBJS");
        param.put("INFO_STATUS", EsopDeskTopConst.WORKINFO_STATUS_UNDO);
        IData insertParam = makOpTaskInfo(param, taskId, taskLevel);

        //插入TF_F_OP_TASK表中
        OpTaskInfoQry.insertOpTaskInfo(insertParam);

        IDataset instInsertData = new DatasetList();
        for (int i = 0; i < recvObjs.size(); i++)
        {
            IData instData = new DataMap();
            IData recvObj = recvObjs.getData(i);
            instData.put("INST_ID", "");//SeqMgr.getOpTaskInstId());
            instData.put("INFO_ID", taskId);
            instData.put("EXEC_MONTH", SysDateMgr.getCurMonth());
            instData.put("INST_STATUS", EsopDeskTopConst.INFO_STATUS_NOREAD);
            instData.put("STAFF_ID", recvObj.getString("RECE_OBJ"));
            instData.put("RECE_OBJ_TYPE", EsopDeskTopConst.RECE_OBJ_TYPE_STAFF);
            instData.put("INST_START_TIME", SysDateMgr.getSysTime());
            instData.put("INST_END_TIME", SysDateMgr.END_DATE_FOREVER);
            instInsertData.add(instData);
        }
        //插入TF_F_OP_TASK_INST表
        OpTaskInstInfoQry.insertOpTaskInstInfo(instInsertData);
    }

    private IData makOpTaskInfo(IData param, String taskId, String taskLevel)
    {
        IData insertParam = new DataMap();
        insertParam.put("INFO_ID", taskId);
        insertParam.put("RELA_ID", param.getString("RELA_ID"));
        insertParam.put("INFO_SIGN", param.getString("INFO_SIGN"));
        insertParam.put("INFO_TOPIC", param.getString("INFO_TOPIC"));
        insertParam.put("INFO_TYPE", param.getString("INFO_TYPE"));
        insertParam.put("INFO_STATUS", param.getString("INFO_STATUS"));
        insertParam.put("TASK_LEVEL", taskLevel);
        insertParam.put("INFO_URL", param.getString("INFO_URL"));
        insertParam.put("INFO_CONTENT", param.getString("INFO_CONTENT"));
        insertParam.put("INFO_AUTH", param.getString("INFO_AUTH"));
        insertParam.put("PLAN_FINISH_TIME", param.getString("PLAN_FINISH_TIME"));
        insertParam.put("FINISH_STAFF_ID", param.getString("FINISH_STAFF_ID"));
        insertParam.put("FINISH_TIME", param.getString("FINISH_TIME"));
        insertParam.put("OPER_TYPE", param.getString("OPER_TYPE"));
        insertParam.put("TRADE_TYPE_CODE", param.getString("TRADE_TYPE_CODE"));
        insertParam.put("GROUP_ID", param.getString("GROUP_ID"));
        insertParam.put("CUST_NAME", param.getString("CUST_NAME"));
        insertParam.put("CUST_SERV_LEVEL", param.getString("CUST_SERV_LEVEL"));
        insertParam.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        insertParam.put("PRODUCT_NAME", param.getString("PRODUCT_NAME"));
        insertParam.put("INFO_CHILD_TYPE", param.getString("INFO_CHILD_TYPE"));
        insertParam.put("TASK_NODE_NAME", param.getString("TASK_NODE_NAME"));
        insertParam.put("OWN_FEE", param.getString("OWN_FEE"));
        insertParam.put("CYCLE_ID", param.getString("CYCLE_ID"));
        insertParam.put("BUSI_REQUIREMENT", param.getString("BUSI_REQUIREMENT"));
        insertParam.put("TASK_RESULT", param.getString("TASK_RESULT"));
        return insertParam;
    }

    private boolean checkPortParamForCrt(IData param, IData retData)
    {
        String topic = param.getString("INFO_TOPIC", "");
        if ("".equals(topic))
        {
            retData.put("X_RESULTCODE", "-1");
            retData.put("X_RESULTINFO", "待办标题不能为空！");
            return true;
        }
        String sign = param.getString("INFO_SIGN", "");
        if ("".equals(sign))
        {
            retData.put("X_RESULTCODE", "-1");
            retData.put("X_RESULTINFO", "INFO_SIGN不能为空！");
            return true;
        }
        IDataset ds = param.getDataset("RECE_OBJS");
        if (ds.size() == 0)
        {
            retData.put("X_RESULTCODE", "-1");
            retData.put("X_RESULTINFO", "接受对象不能为空！");
            return true;
        }
        return false;
    }

    public void updateOpTaskInfoStatus(IData updateData) throws Exception
    {
        OpTaskInfoQry.updateOpTaskInfoStatus(updateData);
    }

    public void updateOpTaskInstStatus(IData updateData) throws Exception
    {
        OpTaskInstInfoQry.updateOpTaskInstStatus(updateData);
    }

    public void updateOpTaskInstStaffId(IData updateData) throws Exception
    {
        OpTaskInstInfoQry.updateOpTaskInstStaffId(updateData);
    }

    public IDataset getWorkTaskInfos(IData param,Pagination page) throws Exception{
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT BPM_TEMPLET_ID,IBSYSID,GROUP_ID,CUST_NAME,INFO_TOPIC,INFO_URL,TO_CHAR(INFO_SEND_TIME, 'YYYY-MM-DD HH24:MI:SS') INFO_SEND_TIME ");
        sql.append(" FROM ");
        sql.append("    (SELECT SUBSTR(A.INFO_URL, ");
        sql.append("               INSTR(A.INFO_URL, 'BPM_TEMPLET_ID=') + LENGTH('BPM_TEMPLET_ID='),");
        sql.append("               INSTR(A.INFO_URL, '&', INSTR(A.INFO_URL, 'BPM_TEMPLET_ID=')) - ");
        sql.append("               (INSTR(A.INFO_URL, 'BPM_TEMPLET_ID=') + LENGTH('BPM_TEMPLET_ID='))) BPM_TEMPLET_ID, ");
        sql.append("        SUBSTR(A.INFO_CONTENT, INSTR(A.INFO_CONTENT, 'CRM订单号:') + LENGTH('CRM订单号:'), ");
        sql.append("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, 'CRM订单号:')) - ");
        sql.append("               (INSTR(A.INFO_CONTENT, 'CRM订单号:') + LENGTH('CRM订单号:'))) IBSYSID, ");
        sql.append("        SUBSTR(A.INFO_CONTENT, INSTR(A.INFO_CONTENT, '集团编码:') + LENGTH('集团编码:'),  ");
        sql.append("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '集团编码:')) -  ");
        sql.append("               (INSTR(A.INFO_CONTENT, '集团编码:') + LENGTH('集团编码:'))) GROUP_ID, ");
        sql.append("        SUBSTR(A.INFO_CONTENT, ");
        sql.append("               INSTR(A.INFO_CONTENT, '集团名称:') + LENGTH('集团名称:'), ");
        sql.append("               INSTR(A.INFO_CONTENT, '<', INSTR(A.INFO_CONTENT, '集团名称:')) - ");
        sql.append("               (INSTR(A.INFO_CONTENT, '集团名称:') + LENGTH('集团名称:'))) CUST_NAME, ");
        sql.append("        A.INFO_TOPIC, ");
        sql.append("        A.INFO_URL, ");
        sql.append("        A.INFO_SEND_TIME ");
        sql.append("     FROM TF_F_INFO A, TF_F_INFO_INSTANCE B ");
        sql.append("        WHERE A.INFO_ID = B.INFO_ID ");
        sql.append("        AND B.RECE_OBJ_TYPE = :RECE_OBJ_TYPE ");
        sql.append("        AND B.RECE_OBJ = :STAFF_ID ");
        sql.append("        AND A.INFO_TYPE = :INFO_TYPE ");
        sql.append("        AND A.INFO_STATUS = :INFO_STATUS) ");
        sql.append("  WHERE 1=1 ");
        if(StringUtils.isNotBlank(param.getString("IBSYSID"))){
            sql.append("  AND IBSYSID = :IBSYSID ");
        }
        if(StringUtils.isNotBlank(param.getString("GROUP_ID"))){
            sql.append("  AND GROUP_ID = :GROUP_ID ");
        }
        if(StringUtils.isNotBlank(param.getString("BPM_TEMPLET_ID"))){
            sql.append("  AND BPM_TEMPLET_ID IN ( ");
            sql.append(param.getString("BPM_TEMPLET_ID"));
            sql.append(" )");
        }else{
            return new DatasetList();
        }
        sql.append("  ORDER BY INFO_SEND_TIME DESC ");

        IDataset infos = Dao.qryBySql(sql,param, page, Route.CONN_CRM_CEN);

        if (IDataUtil.isNotEmpty(infos)) {
            getInfos(infos);
            return 	infos;
        }else {
            return new DatasetList();
        }

    }
}
