
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class CustomTerminalSVC extends CSBizService
{

    /**
     * 根据IBOSS传过来的数据插表
     * 
     * @param inParam
     * @throws Exception
     */
    public void insPackageDataTable(IData data) throws Exception
    {
        /* 参数校验 star */
        IDataUtil.chkParam(data, "KIND_ID");
        String kindId = data.getString("KIND_ID");
        if (!"BIP2B461_T2002161_1_0".equals(kindId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_536, "200002", "参数KIND_ID传值错误！");
        }

        IDataUtil.chkParam(data, "OPR_NUMB");// 本次操作的流水号
        IDataUtil.chkParam(data, "MOBILE_NO");// 激活客户手机号
        IDataUtil.chkParam(data, "IMEI");// 激活绑定终端IMEI
        IDataUtil.chkParam(data, "MATERIAL_CODE");// 终端机型对应物料编码
        IDataUtil.chkParam(data, "ACTIVATE_TIME");// 申请时间YYYYMMDDHHMMSS (24小时)
        IDataUtil.chkParam(data, "TRADE_STAFF_ID");

        IDataset dataset = IDataUtil.idToIds(data);
        if (IDataUtil.isEmpty(dataset))
        {
            dataset.add(data);
        }

        IDataset result = new DatasetList();
        for (int i = 0, s = dataset.size(); i < s; i++)
        {
            IData temp = dataset.getData(i);
            String recordId = SeqMgr.getRecordId();
            // String activeTime = DateUtil.encodeTimestamp("yyyy-MM-dd HH:mm:ss",
            // temp.getString("ACTIVATE_TIME")).toString();
            String activeTime = SysDateMgr.decodeTimestamp(temp.getString("ACTIVATE_TIME"), SysDateMgr.PATTERN_STAND);
            String batchTime = "";
            if (!temp.getString("BATCH_TIME", "").equals(""))
            {
                batchTime = DateUtils.parseDate(temp.getString("BATCH_TIME"), "yyyy-MM-dd HH:mm:ss").toString();
            }

            IData inData = new DataMap();
            inData.put("RECORD_ID", recordId);
            inData.put("OPR_NUMB", data.getString("OPR_NUMB"));
            inData.put("MOBILE_NO", temp.getString("MOBILE_NO"));
            inData.put("IMEI", temp.getString("IMEI"));
            inData.put("MATERIAL_CODE", temp.getString("MATERIAL_CODE"));
            inData.put("PROVINCE_CODE", temp.getString("PROVINCE_CODE", ""));
            inData.put("CARD_NO", temp.getString("CARD_NO", ""));
            inData.put("BATCH_TIME", batchTime);
            inData.put("ACTIVATE_TIME", activeTime);
            inData.put("DEAL_FLAG", "0");
            inData.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
            inData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            inData.put("REMARK", "客户终端激活指令下发");
            // 客户激活短信中的省公司自定义字符串取值 add by yangyz 20121108
            inData.put("BRANCH_ADDSTRING", temp.getString("BRANCH_ADDSTRING", ""));
            result.add(inData);
        }

        Dao.insert("TI_B_USRPACKAGEDATA_DOWN", result, Route.CONN_CRM_CEN);
    }

    /**
     * 根据IBOSS传过来的数据插表
     * 
     * @param data
     * @throws Exception
     */
    public void insPresentFeeTable(IData data) throws Exception
    {

        /* 参数校验 star */
        IDataUtil.chkParam(data, "KIND_ID");
        String kindId = data.getString("KIND_ID");
        if (!"BIP2B462_T2002162_1_0".equals(kindId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_536, "200011", "参数KIND_ID传值错误！");
        }

        IDataUtil.chkParam(data, "OPR_NUMB");// 本次操作的流水号
        IDataUtil.chkParam(data, "MOBILE_NO");// 激活客户手机号
        IDataUtil.chkParam(data, "IMEI");// 激活绑定终端IMEI
        IDataUtil.chkParam(data, "MATERIAL_CODE");// 终端机型对应物料编码
        IDataUtil.chkParam(data, "PACKAGE_CODE");// 赠费优惠方案编码
        IDataUtil.chkParam(data, "ACTIVATE_TIME");// 申请时间YYYYMMDDHHMMSS (24小时)
        IDataUtil.chkParam(data, "TRADE_STAFF_ID");
        IDataUtil.chkParam(data, "IS_HQ_OPERATION");// 全网统一操盘合约机激活标志

        IDataset dataset = IDataUtil.idToIds(data);
        if (IDataUtil.isEmpty(dataset))
        {
            dataset.add(data);
        }

        IDataset result = new DatasetList();
        for (int i = 0, s = dataset.size(); i < s; i++)
        {
            IData temp = dataset.getData(i);

            if ("1".equals(temp.getString("IS_HQ_OPERATION", "").trim()))
            {
                IDataUtil.chkParam(data, "CONSUME_LIMIT");
                IDataUtil.chkParam(data, "CONTRACT_DURATION");
            }

            String recordId = SeqMgr.getRecordId();
            String activeTime = SysDateMgr.decodeTimestamp(temp.getString("ACTIVATE_TIME"), SysDateMgr.PATTERN_STAND);

            IData inData = new DataMap();
            inData.put("RECORD_ID", recordId);
            inData.put("OPR_NUMB", data.getString("OPR_NUMB"));
            inData.put("MOBILE_NO", temp.getString("MOBILE_NO"));
            inData.put("IMEI", temp.getString("IMEI"));
            inData.put("PROVINCE_CODE", temp.getString("PROVINCE_CODE", ""));
            inData.put("CARD_NO", temp.getString("CARD_NO", ""));
            inData.put("MATERIAL_CODE", temp.getString("MATERIAL_CODE"));
            inData.put("PACKAGE_CODE", temp.getString("PACKAGE_CODE"));
            inData.put("ACTIVATE_TIME", activeTime);
            inData.put("DEAL_FLAG", "0");
            inData.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
            inData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            inData.put("REMARK", "客户赠费指令下发");
            // 全网统一操盘合约机激活标志 0 --非全网统一操盘合约机激活 1 --全网统一操盘合约机激活
            inData.put("IS_HQ_OPERATION", temp.getString("IS_HQ_OPERATION", ""));
            inData.put("BRANCH_ADDSTRING", temp.getString("BRANCH_ADDSTRING", ""));// 客户激活短信中的省公司自定义字符串取值
            inData.put("CONSUME_LIMIT", temp.getString("CONSUME_LIMIT", ""));// 客户承诺月最低消费
            inData.put("CONTRACT_DURATION", temp.getString("CONTRACT_DURATION", ""));// 合约捆绑时长
            result.add(inData);
        }

        Dao.insert("TI_B_USRPRESENTFEE_DOWN", result, Route.CONN_CRM_CEN);
    }
}
