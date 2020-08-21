
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;

public class CreateGrpBusyApply
{
    public static IDataset bookGrpTrade(IData data) throws Exception
    {
        IData param = new DataMap();
        IDataset results = new DatasetList();
        IData res = new DataMap();

        String modifyTag = IDataUtil.getMandaData(data, "MODIFY_TAG");
        // 0:受理
        if (modifyTag.equals("0"))
        {
            // 集团编码不为空，设置跟进客户经理（默认为集团客户经理）
            if (!"".equals(data.getString("GROUP_ID", "")))
            {
                param.put("GROUP_ID", data.getString("GROUP_ID"));
                String groupId = data.getString("GROUP_ID");
                // 查询集团客户信息
                IData grpGrpInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
                // 设置跟进客户经理
                if (IDataUtil.isNotEmpty(grpGrpInfo))
                {
                    param.put("CUST_MANAGER_ID", grpGrpInfo.getString("CUST_MANAGER_ID"));
                    param.put("CUST_NAME", grpGrpInfo.getString("CUST_NAME"));// 单位名称
                }
            }
            else
            {
                param.put("GROUP_ID", data.getString("GROUP_ID"));
            }

            // 获取APPLY_ID
            String applyID = SeqMgr.getGrpBuzApplyIdForGrp();

            // 来源渠道默认为Z
            String inModeCode = "Z";

            param.put("PRODUCT_NAME", IDataUtil.getMandaData(data, "PRODUCT_ID")); // 处理产品

            // 封装预受理数据
            param.put("APPLY_ID", applyID);
            param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(applyID)); // 分区标识
            param.put("IN_MODE_CODE", inModeCode); // 来源渠道
            param.put("OBJ_TYPE_CODE", "0"); // 作用对象:0是集团
            param.put("CONTACT_PHONE", IDataUtil.getMandaData(data, "PHONE")); // 预约人手机号码
            param.put("ACCEPT_NAME", IDataUtil.getMandaData(data, "REGISTER_NAME")); // 预约人姓名
            param.put("GROUP_ADDR", data.getString("ADDR")); // 预约人地址
            param.put("RSRV_DATE1", IDataUtil.getMandaData(data, "PRE_EXE_DATE")); // 预约完成时间
            param.put("REMARK", data.getString("REMARK")); // 备注
            param.put("RSRV_STR1", data.getString("RSRV_STR1")); // 所属区县
            param.put("RSRV_STR2", data.getString("RSRV_STR2")); // 是否有营业执照
            param.put("RSRV_STR3", IDataUtil.getMandaData(data, "EPARCHY_CODE")); // 地州编码
            param.put("ACCEPT_TIME", SysDateMgr.getSysTime()); // 受理时间
            param.put("DEAL_STATE", "0");// 处理状态:0是已受理
            // param.put("SERIAL_NUMBER", ""); //申请业务号码
            // param.put("POST_CODE", ""); //单位邮编
            // param.put("ACCEPT_PSPT_ID", ""); //业务经办人证件号
            // param.put("WORK_JOB", ""); //业务经办人职位
            // param.put("CONTACT_EMAIL", "");//业务经办人email/传真
            // param.put("OPER_TYPE_CODE", ""); //处理类型

            boolean isSucess = Dao.insert("TF_B_GRP_BUSAPPLY", param, Route.CONN_CRM_CG);
            if (isSucess)
            {
                res.put("X_RESULTCODE", "0");
                res.put("X_RESULTINFO", "预约成功");
                res.put("BOOK_ID", applyID);
            }
            else
            {
                res.put("X_RESULTCODE", "-1");
                res.put("X_RESULTINFO", "预约失败");
                res.put("APPLY_ID", "");
            }
        }
        else if (modifyTag.equals("1"))
        {// 1:取消
            param.put("APPLY_ID", IDataUtil.getMandaData(data, "APPLY_ID"));
            param.put("DEAL_STATE", "4");// 处理状态：4：取消预约

            Dao.save("TF_B_GRP_BUSAPPLY", param, Route.CONN_CRM_CG);

            res.put("X_RESULTCODE", "0");
            res.put("X_RESULTINFO", "取消预约成功");
            res.put("BOOK_ID", data.getString("APPLY_ID"));
        }
        res.put("GROUP_ID", data.getString("GROUP_ID"));
        res.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        res.put("PHONE", data.getString("PHONE"));
        res.put("REGISTER_NAME", data.getString("REGISTER_NAME"));
        res.put("ADDR", data.getString("ADDR"));
        res.put("PRE_EXE_DATE", data.getString("PRE_EXE_DATE"));
        res.put("REMARK", data.getString("REMARK"));
        res.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        res.put("RSRV_STR1", data.getString("RSRV_STR1"));
        res.put("RSRV_STR2", data.getString("RSRV_STR2"));
        results.add(res);
        return results;
    }

    /**
     * 集团业务预受理
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset createGrpBusyApply(IData inParam) throws Exception
    {
        IData param = new DataMap();

        String groupId = inParam.getString("GROUP_ID");

        // 如果存在集团客户编码, 则获取集团客户经理信息
        if (StringUtils.isNotBlank(groupId))
        {
            IData grpCustData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

            if (IDataUtil.isNotEmpty(grpCustData))
            {
                param.put("CUST_MANAGER_ID", grpCustData.getString("CUST_MANAGER_ID"));
            }
        }

        String applyId = SeqMgr.getGrpApplyId();
        String partitionId = applyId.substring(applyId.length() - 4);

        String inModeCode = inParam.getString("IN_MODE_CODE");
        if (StringUtils.isBlank(inModeCode))
            inModeCode = "Z";

        String sysTime = SysDateMgr.getSysTime();

        String attrNameStr = inParam.getString("ATTR_NAME");
        String attrCodeStr = inParam.getString("ATTR_CODE");
        String attrValueStr = inParam.getString("ATTR_VALUE");

        // 处理参数信息
        IDataset attrList = new DatasetList();
        if (StringUtils.isNotBlank(attrCodeStr) && StringUtils.isNotBlank(attrCodeStr))
        {
            String attrCodeArray[] = StringUtils.split(attrCodeStr, ",");
            String attrNameArray[] = StringUtils.split(attrNameStr, ",");
            String attrValueArray[] = StringUtils.split(attrValueStr, ",");
            for (int i = 0, row = attrCodeArray.length; i < row; i++)
            {
                IData attrData = new DataMap();
                attrData.put("ATTR_CODE", attrCodeArray[i]);
                attrData.put("ATTR_NAME", attrNameArray[i]);
                attrData.put("ATTR_VALUE", attrValueArray[i]);
                attrData.put("APPLY_ID", applyId);
                attrData.put("PARTITION_ID", partitionId);
                attrData.put("INST_TYPE", "P");
                attrData.put("START_DATE", sysTime);
                attrData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                attrList.add(attrData);
            }
        }

        String wsTag = inParam.getString("WS_TAG", ""); // 集团客户子网站标识

        // 临时判断条件，应付上线
        String groupType = inParam.getString("GROUP_TYPE", "");
        String groupPhone = inParam.getString("GROUP_PHONE", "");

        // 处理预受理数据
        param.put("APPLY_ID", applyId);
        param.put("ACCEPT_MONTH", sysTime.substring(5, 7)); // 分区标识
        param.put("IN_MODE_CODE", inModeCode); // 来源渠道
        param.put("OBJ_TYPE_CODE", "0"); // 作用对象
        param.put("SERIAL_NUMBER", IDataUtil.getMandaData(inParam, "CONTACT_PHONE")); // 申请业务号码
        param.put("CUST_NAME", IDataUtil.getMandaData(inParam, "CUST_NAME")); // 单位名称
        param.put("GROUP_ADDR", IDataUtil.getMandaData(inParam, "GROUP_ADDR")); // 单位地址
        param.put("POST_CODE", IDataUtil.getMandaData(inParam, "POST_CODE")); // 单位邮编
        param.put("ACCEPT_NAME", IDataUtil.getMandaData(inParam, "ACCEPT_NAME")); // 业务经办人姓名

        param.put("CONTACT_PHONE", IDataUtil.getMandaData(inParam, "CONTACT_PHONE")); // 业务经办人电话
        param.put("CONTACT_EMAIL", inParam.getString("CONTACT_EMAIL"));// 业务经办人email/传真
        param.put("PRODUCT_NAME", IDataUtil.getMandaData(inParam, "PRODUCT_NAME")); // 处理产品
        param.put("OPER_TYPE_CODE", inParam.getString("OPER_TYPE_CODE")); // 处理类型
        param.put("REMARK", inParam.getString("REMARK")); // 备注
        param.put("ACCEPT_TIME", sysTime); // 受理时间
        param.put("DEAL_STATE", "0");// 处理状态

        if ((null != wsTag && "1".equals(wsTag)) || !"".equals(groupType) || !"".equals(groupPhone))
        {
            param.put("RSRV_STR4", inParam.getString("CONTACT_FAX")); // 联系人传真
            param.put("RSRV_STR5", inParam.getString("GROUP_TYPE")); // 公司类型（集团客户子网站）
            param.put("RSRV_STR6", inParam.getString("GROUP_PHONE")); // 公司电话（集团客户子网站）
            param.put("RSRV_STR7", inParam.getString("BUS_CONTACT_NAME", "")); // 业务联系人姓名（集团客户子网站）
            param.put("RSRV_STR8", inParam.getString("BUS_CONTACT_PHONE", "")); // 业务联系人联系电话（集团客户子网站）
            param.put("RSRV_STR9", inParam.getString("APPLY_OPENDATE", "")); // 期望办理时间（集团客户子网站）
            param.put("RSRV_STR10", inParam.getString("APPLY_CONTACTDATE", "")); // 联系时间段（集团客户子网站）
        }
        else
        {
            param.put("ACCEPT_PSPT_ID", inParam.getString("ACCEPT_PSPT_ID")); // 业务经办人证件号
            param.put("WORK_JOB", inParam.getString("WORK_JOB")); // 业务经办人职位
        }

        if (IDataUtil.isNotEmpty(attrList))
        {
            Dao.insert("TF_B_GRP_BUSAPPLY_ATTR", attrList);
            ;
        }

        boolean isSucess = Dao.insert("TF_B_GRP_BUSAPPLY", param);

        IData retData = new DataMap();

        if (isSucess)
        {
            retData.put("X_RESULTCODE", "0");
            retData.put("X_RESULTINFO", "预约成功");
            retData.put("APPLY_ID", applyId);
        }
        else
        {
            retData.put("X_RESULTCODE", "-1");
            retData.put("X_RESULTINFO", "预约失败");
            retData.put("APPLY_ID", "");
        }

        return IDataUtil.idToIds(retData);
    }

    /**
     * 集团业务预受理删除
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset updateGrpBusyApply(IData data) throws Exception
    {
        IData retData = new DataMap();

        String applyId = IDataUtil.getMandaData(data, "APPLY_ID");

        IDataset grpBusyList = GrpInfoQry.qryGrpBusyByApplyId(applyId, null);

        if (IDataUtil.isNotEmpty(grpBusyList))
        {
            IData grpBusyData = grpBusyList.getData(0);
            if ("0".equals(grpBusyData.getString("DEAL_STATE", "")))
            {
                GrpInfoQry.updateGrpBusyByApplyId(applyId);
                retData.put("X_RESULTCODE", "0");
                retData.put("X_RESULTINFO", "删除成功!");
            }
            else
            {
                retData.put("X_RESULTCODE", "-1");
                retData.put("X_RESULTINFO", "已进入处理状态，删除失败！");
            }
        }
        else
        {
            retData.put("X_RESULTCODE", "-1");
            retData.put("X_RESULTINFO", "根据APPLY_ID:[" + applyId + "]未查询到预约数据!");
        }

        return IDataUtil.idToIds(retData);
    }
}
