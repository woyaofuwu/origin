
package com.asiainfo.veris.crm.order.soa.person.busi.badness;

import java.util.Random;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.DedInfoException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.BadnessInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.badness.sundryquery.BadnessQueryBean;
import com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr.ManageForbiddenPointBean;

public class BadnessManageBean extends CSBizBean
{
    public static final String PROVINCE_HEAD = "000";// 归属省为总部

    public static final String DEAL_STATE_FEEDBACK = "02";// 处理回复

    /**
     * 不良信息归档
     * 
     * @Function: archBadnessInfos
     * @Description: TODO
     * @date Jul 12, 2014 10:06:23 AM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset archBadnessInfos(IData data) throws Exception
    {
        IDataset badInfos = new DatasetList(data.getString("BADNESS_TABLE", "{}"));
        if (IDataUtil.isNotEmpty(badInfos))
        {
            for (int i = 0, size = badInfos.size(); i < size; i++)
            {
                IData badInfo = badInfos.getData(i);
                String staffId = getVisit().getStaffName() + "--" + getVisit().getStaffId();
                String departId = getVisit().getDepartName() + "--" + getVisit().getDepartId();

                IData param = new DataMap();
                String infoRecvId = badInfo.getString("INFO_RECV_ID");
                param.put("INFO_RECV_ID", infoRecvId);
                param.put("STATE", PersonConst.STATE_FEEDBACK);
                param.put("REPORT_CUST_PROVINCE", "898");
                param.put("BADNESS_INFO_PROVINCE", "898");
                IDataset result = new BadnessQueryBean().queryBadHastenInfo(param, null);

                if (IDataUtil.isNotEmpty(result))
                {
                    IData bad = result.getData(0);
                    // 老系统没有对TF_F_BADNESS_INO_DEAL表判断是否存在记录，容易主键冲突,且没有处理TF_F_BADNESS_INFO表，每次查询都会重复查询到，需局方确认
                    IData badDeal = BadnessInfoQry.qryBadnessDealInfoByPK(infoRecvId, PersonConst.STATE_ARCH);
                    if (IDataUtil.isEmpty(badDeal))
                    {
                        param.clear();
                        data.put("INFO_RECV_ID", infoRecvId);
                        data.put("DEAL_RAMARK", data.getString("DEAL_RAMARK"));
                        data.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP"));
                        data.put("SERV_REQUEST_TYPE", bad.getString("SERV_REQUEST_TYPE"));
                        data.put("DEAL_STAFF_ID", staffId);
                        data.put("DEAL_DEPART_ID", departId);
                        data.put("DEAL_DATE", SysDateMgr.getSysDate());
                        data.put("STATE", PersonConst.STATE_ARCH);
                        Dao.insert("TF_F_BADNESS_INO_DEAL", data, Route.CONN_CRM_CEN);
                    }

                    if ("898".equals(bad.getString("REPORT_CUST_PROVINCE")) && !"898".equals(bad.getString("BADNESS_INFO_PROVINCE")) && PersonConst.STATE_FEEDBACK.equals(bad.getString("STATE")))
                    {
                        IData params = new DataMap();
                        params.put("INFO_RECV_ID", infoRecvId);
                        params.put("DEAL_DATE", SysDateMgr.getOtherSecondsOfSysDate(1));
                        params.put("STATE", PersonConst.STATE_ARCH); // 归档;
                        params.put("FINISH_DATE", SysDateMgr.getOtherSecondsOfSysDate(1));// //归档时间缺失
                        params.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP"));
                        Dao.save("TF_F_BADNESS_INFO", params, new String[]
                        { "INFO_RECV_ID" }, Route.CONN_CRM_CEN);

                        IData msgParam = new DataMap();
                        msgParam.put("INDICT_TIME", bad.getString("REPORT_TIME"));
                        msgParam.put("MSISDN", bad.getString("REPORT_SERIAL_NUMBER"));
                        msgParam.put("TARGET_NO", bad.getString("BADNESS_INFO"));
                        msgParam.put("INDICT_SEQ", infoRecvId);
                        msgParam.put("DEAL_RESULT", bad.getString("DEAL_RAMARK"));
                        msgParam.put("FULL_RSLT", bad.getString("DEAL_REMARK_MAKEUP"));
                        msgParam.put("TRADE_DEPART_ID", departId);
                        msgParam.put("TRADE_STAFF_ID", staffId);
                        msgParam.put("CUR_TIME", SysDateMgr.getSysTime());// 归档时间缺失
                        CSAppCall.call("SS.BadnessManageInterSVC.updateBadDealInfoByMsgIntf", msgParam);
                    }
                }
            }
        }
        return null;
    }

    private IDataset blackReg(IData data) throws Exception
    {
        IData tmp = new DataMap();
        tmp.put("TRADE_CITY_CODE", getVisit().getCityCode());
        tmp.put("TRADE_STAFF_ID", getVisit().getStaffId());
        tmp.put("TRADE_DEPART_ID", getVisit().getDepartId());
        tmp.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        tmp.put("INDICT_SEQ", genNewIndictSeq()); // data.getString("INDICT_SEQ", "")); //
        tmp.put("OLD_INDICT_SEQ", data.getString("INDICT_SEQ", ""));
        tmp.put("PROVINCE", "898");
        tmp.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        tmp.put("HANDLING_TIME", SysDateMgr.getSysTime()); // 人工触发处理时间为空
        tmp.put("UPDATE_TIME", tmp.getString("HANDLING_TIME", "")); // 最后处理时间
        /*
         * 0101 仅关闭短信功能 0102 关闭主叫号码所有功能（即停机） 0103 开启短信功能 0104 开启主叫号码所有功能（即开机） 0201 仅关闭语音主叫功能 0202 关闭主叫号码所有功能（即停机） 0203
         * 开启语音主叫功能 0204 开启主叫号码所有功能（即开机）
         */// 次处只是记录一下规范中的编码，解黑是也是一样，因为实际操作，只能以业务类型的服务变更配置为准
        tmp.put("HANDLING_STATE", "");// data.getString("HANDLING_STATE", "") ); //次处是处理意见，经配置后与规范定义一致 ,,'设计文档中的处理方式'
        // tmp.put("ORI_HANDING_SUGGEST", data.getString("DEAL_RAMARK", ""));// 原始处理意见
        tmp.put("REMOVE_HANDLING_STATE", data.getString("HANDLING_STATE", "")); // 解黑处置方式
        tmp.put("HANDLING_TYPE", "0202");// today 这里可能不对. "1");//"HLR加解黑类型为空"); 刚才问了一级客服，说那个handlingtype应该填0202
        // 设计文档中,接口定义为:0103 投诉联创-黄文亮 16:01:30 刚那个handlingType跟平台确认过了，是填0201
        tmp.put("SOURCE_DATA", data.getString("SOURCE_DATA", "")); // 回复我了，确实应该填原加黑记录里面的sourceData "03");//
        // "数据来源,'01'表示前台举报受理 01：垃圾短信平台 02：骚扰电话平台
        // 先写'03',表示用户投诉
        tmp.put("SERVICE_CONTENT", data.getString("DEAL_REMARK_MAKEUP", ""));// "HLR加解黑原因描述为空");
        tmp.put("NEED_TRADETYPE", data.getString("NEED_TRADETYPE", ""));
        tmp.put("ADD_RESULT", data.getString("DEAL_RAMARK", ""));
        tmp.put("IN_MODE_CODE", "1");// "接入渠道为空");
        tmp.put("BLACK_STATE", "01"); // 前台插入的初始状态
        tmp.put("DATA_CHNL", "01"); // 表示举报后,界面解黑写入
        tmp.put("TRADE_TYPE_CODE", data.getString("NEED_TRADETYPE", ""));
        tmp.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        // tmp.put("BADNESS_CLASS", badness_type) ; //不良信息类别 规范中有要求,即举报处理中的服务请求类别,举报受理中的举报对象类型

        tmp.put("SUB_NUMBER", data.getString("SERIAL_NUMBER", ""));

        
        IDataset dataset = CSAppCall.call("SS.BadnessManageInterSVC.createHLRStopOpenRegIner", tmp);
        return dataset;
    }

    public IDataset dealBadnessReleaseInfo(IData data) throws Exception
    {
        IDataset dataset = new DatasetList();
        String[] inforecvids = data.getString("INFO_RECV_ID", "").split(",");
        String[] serialNumbers = data.getString("SERIAL_NUMBER", "").split(",");
        String[] states = data.getString("HANDLING_STATE", "").split(",");
        String[] sources = data.getString("SOURCE_DATA", "").split(",");
        for (int i = 0; i < inforecvids.length; i++)
        {
            IDataset result = CommparaInfoQry.getInfoParaCode3("CSM", "1720", states[i]);
            if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(DedInfoException.CRM_DedInfo_35);
            }

            String tradetype = result.getData(0).getString("PARA_CODE5", "");
            if (StringUtils.isBlank(tradetype))
            {
                CSAppException.apperr(DedInfoException.CRM_DedInfo_36);//
            }

            result = CommparaInfoQry.getInfoParaCode3("CSM", "1720", tradetype);
            if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(DedInfoException.CRM_DedInfo_35);
            }

            tradetype = result.getData(0).getString("PARA_CODE1", "");
            if (StringUtils.isBlank(tradetype))
            {
                CSAppException.apperr(DedInfoException.CRM_DedInfo_38);
            }
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serialNumbers[i]);
            param.put("INDICT_SEQ", inforecvids[i]);
            param.put("DEAL_RAMARK", data.getString("DEAL_RAMARK", ""));// 这里是解黑原因.
            param.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP", ""));
            param.put("BADINFO_SERVREQUESTTYPE", data.getString("BADINFO_SERVREQUESTTYPE", ""));// today 这里先填写原始的, 需要转换.
            // blackparam.put("SHORT_SERV_REQ_TYPE", short_serv_req_type ) ;
            param.put("HANDLING_STATE", states[i]); // handling_state[i] ) ;
            param.put("NEED_TRADETYPE", tradetype);
            param.put("SOURCE_DATA", sources[i]); // 2012-10-17 联创-黄文亮 16:01:30回复我了，确实应该填原加黑记录里面的sourceData

            dataset = blackReg(param);
        }

        return dataset;
    }

    /**
     * 不良信息归档 情况一：受理本省 1.直接归档不良信息; 情况二：跨省举报与归属总部 1.触发一级客服接口进行归属省归档;2.受理省不良信息归档;
     * 
     * @param data
     *            INFO_RECV_ID|服务请求标识串,DEAL_REMARK_MAKEUP|归档处理意见
     * @throws Exception
     */
    // public IDataset updateBadInfos(IData data) throws Exception
    // {
    // IDataset badInfos = new DatasetList(data.getString("BADNESS_TABLE", "{}"));
    // String remark = data.getString("DEAL_REMARK_MAKEUP");
    // IDataset local = new DatasetList();
    // IDataset iboss = new DatasetList();
    //
    // if (IDataUtil.isNotEmpty(badInfos))
    // {
    // for (int i = 0; i < badInfos.size(); i++)
    // {
    // IData badInfo = badInfos.getData(i);
    // String revcId = badInfo.getString("INFO_RECV_ID");
    // IData info = BadnessInfoQry.qryBadInfoByPK("TF_F_BADNESS_INFO", revcId);// 获取不良基本信息
    // int typeVal = verifyBadInfoByType(info);
    // if (typeVal == 0)
    // {// 本省处理
    // local.add(info);
    // }
    // else if (typeVal == 1)
    // {// 调用一级客服接口并本省处理
    // iboss.add(info);
    // }
    //
    // String sysTime = SysDateMgr.getSysTime();
    // if (IDataUtil.isNotEmpty(local))
    // {// 本省处理
    // updateBadInfosByLocal(local, sysTime, remark);
    // createBadDealInfo(local, sysTime, remark);
    // }
    //
    // if (IDataUtil.isNotEmpty(iboss))
    // {// 调用一级客服接口并本省处理
    // updateBadInfosByIBoss(iboss, sysTime, remark);
    // }
    // }
    // }
    // return null;
    // }
    //
    // private int[] updateBadInfosByLocal(IDataset dataset, String sysTime, String remark) throws Exception
    // {
    // IDataset params = new DatasetList();
    // for (int i = 0; i < dataset.size(); i++)
    // {
    // IData param = new DataMap();
    // param.put("STATE", PersonConst.STATE_ARCH);
    // param.put("FINISH_DATE", sysTime);
    // param.put("DEAL_REMARK_MAKEUP", remark);
    // param.put("DEAL_DATE", sysTime);
    // param.put("INFO_RECV_ID", dataset.getData(i).getString("INFO_RECV_ID"));
    //
    // params.add(param);
    // }
    // return Dao.executeBatchByCodeCode("TF_F_BADNESS_INFO", "UPD_BADNESS_BY_PK", params, Route.CONN_CRM_CEN);
    // }
    //
    // private void createBadDealInfo(IDataset dataset, String sysTime, String remark) throws Exception
    // {
    // IDataset inDs = new DatasetList(), upDs = new DatasetList();
    // IData inData = null;
    //
    // for (int i = 0; i < dataset.size(); i++)
    // {// 组合新增数据
    // inData = new DataMap();
    // inData.put("INFO_RECV_ID", dataset.getData(i).getString("INFO_RECV_ID", ""));
    // inData.put("DEAL_REMARK_MAKEUP", remark);
    // inData.put("SERV_REQUEST_TYPE", dataset.getData(i).getString("SERV_REQUEST_TYPE", ""));
    // inData.put("DEAL_STAFF_ID", getVisit().getStaffId());
    // inData.put("DEAL_DEPART_ID", getVisit().getDepartId());
    // inData.put("DEAL_DATE", sysTime);// 归档日期
    // inData.put("STATE", "0A");// 处理归档
    //
    // // 判断不良信息处理表获取是否已存在
    // IData dData = BadnessInfoQry.qryBadInfoByPK("TF_F_BADNESS_INO_DEAL", dataset.getData(i).getString("INFO_RECV_ID",
    // ""));
    // if (dData != null && !dData.isEmpty())// 更新-不良处理信息数据
    // upDs.add(inData);
    // else
    // // 新增-不良处理信息数据
    // inDs.add(inData);
    // }
    // if (inDs != null && inDs.size() > 0)
    // {
    // Dao.insert("TF_F_BADNESS_INO_DEAL", inDs, Route.CONN_CRM_CEN);
    // }
    // if (upDs != null && upDs.size() > 0)
    // {
    // for (int i = 0; i < upDs.size(); i++)
    // Dao.save("TF_F_BADNESS_INO_DEAL", upDs.getData(i), Route.CONN_CRM_CEN);
    // }
    // }
    //
    // private int verifyBadInfoByType(IData data) throws Exception
    // {
    // if (IDataUtil.isNotEmpty(data))
    // {
    // String recvProv = data.getString("RECV_PROVINCE", "");// 举报受理省
    // String custProv = data.getString("REPORT_CUST_PROVINCE", "");// 举报用户归属省
    // String badProv = data.getString("BADNESS_INFO_PROVINCE", "");// 被举报号码归属省|总部
    //
    // if (recvProv.equals(custProv))
    // {// A.举报受理省=举报用户归属省的情况
    // if (custProv.equals(badProv))
    // {
    // return 0;// 1.受理本省
    // }
    // else if (!recvProv.equals(badProv) && badProv.equals(PROVINCE_HEAD))
    // {
    // return 1;// 3.归属总部(可能需区分)
    // }
    // else if (!custProv.equals(badProv))
    // {
    // return 1;// 2.跨省举报(可能需区分)
    // }
    // }
    // else if (!recvProv.equals(custProv))
    // {// B.举报受理省!=举报用户归属省的情况
    // if (recvProv.equals(PROVINCE_HEAD))
    // {
    // return -1;// 5.总部下派
    // }
    // else
    // {
    // return 0;// 4.异地举报
    // }
    // }
    // }
    // return -1;
    // }
    //
    // private void updateBadInfosByIBoss(IDataset dataset, String sysTime, String remark) throws Exception
    // {
    // IData param = new DataMap();
    // param.put("HANDING_DEPART", getVisit().getDepartId());
    // param.put("HANDING_STAFF", getVisit().getStaffId());
    // param.put("HANDLING_COMMNET", remark);
    // param.put("PIGEONHOLE_TIME", sysTime);
    // for (int i = 0; i < dataset.size(); i++)
    // {
    // param.put("INDICT_SEQ", dataset.getData(i).getString("INFO_RECV_ID", ""));
    //
    // IData result = IBossCall.dealArchIboss(param);
    // if ("0".equals(result.getString("X_RESULTCODE")))
    // {
    // IData data = new DataMap();
    // // 不良信息归档
    // data.put("STATE", PersonConst.STATE_ARCH);// 归档状态
    // data.put("INFO_RECV_ID", param.getString("INDICT_SEQ", ""));// 请求服务标识
    // data.put("FINISH_DATE", param.getString("PIGEONHOLE_TIME", ""));// 归档时间
    // data.put("DEAL_REMARK_MAKEUP", param.getString("HANDLING_COMMNET", ""));// 归档意见
    // data.put("DEAL_DATE", param.getString("PIGEONHOLE_TIME", ""));// 处理时间
    // Dao.save("TF_F_BADNESS_INFO", data, Route.CONN_CRM_CEN);
    //
    // // 创建处理不良信息归档
    // data.clear();
    // data.put("INFO_RECV_ID", param.getString("INDICT_SEQ", ""));// 请求服务标识
    // data.put("DEAL_REMARK_MAKEUP", param.getString("HANDLING_COMMNET", ""));// 归档意见
    // data.put("SERV_REQUEST_TYPE", dataset.getData(i).getString("SERV_REQUEST_TYPE", "").trim());// 服务请求类别
    // data.put("DEAL_STAFF_ID", param.getString("HANDING_STAFF", ""));// 归档员工
    // data.put("DEAL_DEPART_ID", param.getString("HANDING_DEPART", ""));// 归档部门
    // data.put("DEAL_DATE", SysDateMgr.getSysTime());// 处理时间
    // data.put("STATE", PersonConst.STATE_ARCH);// 处理归档状态
    // Dao.save("TF_F_BADNESS_INO_DEAL", data, Route.CONN_CRM_CEN);
    // }
    // }
    // }

    private String genNewIndictSeq() throws Exception
    {
        String indictId = SeqMgr.getBadnessId().substring(9, 16);
        String indate = SysDateMgr.getSysDate();
        indate = indate.substring(0, 4) + indate.substring(5, 7) + indate.substring(8, 10);
        for (int i = 7; i > indictId.length(); i--)
        {
            indictId = "0" + indictId;
        }
        String eqrev1 = indate + "CSVC" + "898" + indictId;
        return eqrev1;
    }

    private String getDepartId()
    {
        String departId = getVisit().getDepartId();
        String departName = getVisit().getDepartName();
        return departName + "--" + departId;
    }

    private String getStaffId()
    {
        String staffId = getVisit().getStaffId();
        String staffName = getVisit().getStaffName();
        return staffName + "--" + staffId;
    }

    public IDataset hastenBadnessInfos(IData data) throws Exception
    {
        IDataset badInfos = new DatasetList(data.getString("BADNESS_TABLE", "{}"));
        if (IDataUtil.isNotEmpty(badInfos))
        {
            for (int i = 0; i < badInfos.size(); i++)
            {
                IData badInfo = badInfos.getData(i);
                IData param = new DataMap();
                param.put("INFO_RECV_ID", badInfo.getString("INFO_RECV_ID"));
                param.put("STATE", PersonConst.STATE_NORMAL);
                param.put("REPORT_CUST_PROVINCE", "898");
                param.put("BADNESS_INFO_PROVINCE", "898");
                IDataset result = new BadnessQueryBean().queryBadHastenInfo(param, null);

                if (IDataUtil.isNotEmpty(result))
                {
                    IData bad = result.getData(0);
                    // 老系统没有对TF_F_BADNESS_INO_DEAL表判断是否存在记录，容易主键冲突,且没有处理TF_F_BADNESS_INFO表，每次查询都会重复查询到，需局方确认
                    IData badDeal = BadnessInfoQry.qryBadnessDealInfoByPK(badInfo.getString("INFO_RECV_ID"), PersonConst.STATE_HASTEN);
                    if (IDataUtil.isEmpty(badDeal))
                    {
                        param.clear();
                        param.put("INFO_RECV_ID", badInfo.getString("INFO_RECV_ID"));
                        param.put("DEAL_RAMARK", data.getString("DEAL_DEAL_REMARK"));
                        param.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP"));
                        param.put("SERV_REQUEST_TYPE", bad.getString("SERV_REQUEST_TYPE"));
                        param.put("DEAL_STAFF_ID", getStaffId());
                        param.put("DEAL_DEPART_ID", getDepartId());
                        param.put("DEAL_DATE", SysDateMgr.getSysDate());
                        param.put("STATE", PersonConst.STATE_HASTEN);
                        Dao.insert("TF_F_BADNESS_INO_DEAL", param, Route.CONN_CRM_CEN);
                    }

                    if ("898".equals(bad.getString("REPORT_CUST_PROVINCE")) && !"898".equals(bad.getString("BADNESS_INFO_PROVINCE")) && PersonConst.STATE_NORMAL.equals(result.getData(0).getString("STATE")))
                    {

                        param.clear();
                        param.put("INFO_RECV_ID", badInfo.getString("INFO_RECV_ID"));
                        param.put("DEAL_DATE", SysDateMgr.getOtherSecondsOfSysDate(-1));
                        param.put("HASTEN_STATE", PersonConst.STATE_HASTEN); // 催办
                        param.put("RES_DEAL_RAMARK", data.getString("DEAL_REMARK"));
                        param.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP"));
                        Dao.save("TF_F_BADNESS_INFO", param, new String[]
                        { "INFO_RECV_ID" }, Route.CONN_CRM_CEN);

                        IData ibossdata = new DataMap();
                        ibossdata.put("INDICT_SEQ", badInfo.getString("INFO_RECV_ID"));
                        ibossdata.put("DEAL_DATE", SysDateMgr.getOtherSecondsOfSysDate(-1));
                        ibossdata.put("RES_DEAL_RAMARK", data.getString("DEAL_REMARK"));
                        ibossdata.put("HANDLING_COMMNET", data.getString("DEAL_REMARK_MAKEUP"));
                        ibossdata.put("SERV_REQUEST_TYPE", result.getData(0).getString("SERV_REQUEST_TYPE"));
                        ibossdata.put("CONTACT_PHONE", data.getString("CONTACT_PHONE"));
                        ibossdata.put("HANDING_DEPART", getDepartId());
                        ibossdata.put("HANDING_STAFF", getStaffId());

                        IData ibossResult = IBossCall.dealBadInfoIboss(ibossdata);
                        if (!"0000".equals(ibossResult.getString("X_RSPCODE", "")))
                        {
                            CSAppException.apperr(DedInfoException.CRM_DedInfo_80, ibossResult.getString("X_RESULTINFO", "IBOSS接口调用失败!"));
                        }
                    }
                }
            }
        }
        return null;
    }

    private String judgeBrandCode(String brandCode)
    {
        if ("G001".equalsIgnoreCase(brandCode))
        {
            brandCode = "01"; // 全球通
        }
        else if ("G010".equalsIgnoreCase(brandCode))
        {
            brandCode = "02"; // 动感地带
        }
        else
        {
            brandCode = "03"; // 神州行
        }
        return brandCode;
    }

    /**
     * 不良信息回复
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset restoreBadnessInfos(IData data) throws Exception
    {
        String badinfo = data.getString("BADNESS_TABLE");
        IDataset ids = new DatasetList(badinfo);
        for (int i = 0; i < ids.size(); i++)
        {
            String recvId = ids.getData(i).getString("INFO_RECV_ID");
            IDataset badInfos = BadnessInfoQry.qryBadnessInfos(recvId, PersonConst.STATE_HEAD, null, null, null, null, null, null, null, null, null,null, null);
            if (IDataUtil.isEmpty(badInfos))
            {
                continue;
            }

            IData temp = badInfos.getData(0);
            if (PersonConst.PROVINCE_CODE.equals(temp.getString("BADNESS_INFO_PROVINCE")) && PersonConst.STATE_HEAD.equals(temp.getString("STATE")))
            {
                String servRequestType = temp.getString("ALL_SERV_REQUEST_TYPE", "");
                String reportTypeCode = temp.getString("REPORT_TYPE_CODE", "");
                String inModeCode = "";
                if (StringUtils.isNotBlank(reportTypeCode))
                {
                    //分拣已不在CRM进行
//                    BadInfoComplainDealBean dealBean = new BadInfoComplainDealBean();
//                    IData dolldata = dealBean.dealCollation(temp.getString("BADNESS_INFO"));
//                    inModeCode = dolldata.getString("IN_MODE_CODE");
//                    data.put("REPORT_TYPE_CODE1", dealBean.dealReportType(data.getString("REPORT_TYPE_CODE")));
//                    IData overdata = dealBean.dealDataover(dolldata, data);// 处理分拣后数据HNYD-REQ-ZB-20120110-022
//                    servRequestType = overdata.getString("SERV_TYPE", "");
//                    servRequestType = "10" + data.getString("FIRST_TYPE_CODE", "") + data.getString("SECOND_TYPE_CODE", "") + data.getString("THIRD_TYPE_CODE", "");
                }
                //跟据周德顺要求后4位如果是0607则随机拼上一个01，02，03，04值
                if(StringUtils.isNotEmpty(servRequestType) && servRequestType.length()> 4 && "0607".equals(servRequestType.substring(servRequestType.length()-4, servRequestType.length()))){
                	Random rand = new Random();
                	int intRand = rand.nextInt(3) + 1;
                	String strRand = "0" + intRand;
                	servRequestType = servRequestType + strRand;
                }
//                servRequestType = servRequestType + data.getString("SVC_TYPE_ID");
                if (servRequestType.length() >= 6) {
                	servRequestType = servRequestType.substring(0, 6) + dealRequestTypeCode(data);
                } else {
                	servRequestType = "110103" + dealRequestTypeCode(data);
                }

                IData dealdata = new DataMap();
                dealdata.put("INFO_RECV_ID", recvId);
                dealdata.put("DEAL_RAMARK", data.getString("DEAL_RAMARK", ""));
                dealdata.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP", ""));
                dealdata.put("SERV_REQUEST_TYPE", servRequestType);
                dealdata.put("CONTACT_SERIAL_NUMBER", data.getString("CONTACT_PHONE", ""));
                dealdata.put("DEAL_STAFF_ID", getStaffId());
                dealdata.put("DEAL_DEPART_ID", getDepartId());
                dealdata.put("DEAL_DATE", SysDateMgr.getOtherSecondsOfSysDate(-1));
                dealdata.put("STATE", DEAL_STATE_FEEDBACK);
                Dao.insert("TF_F_BADNESS_INO_DEAL", dealdata, Route.CONN_CRM_CEN);

                IData param = new DataMap();
//                IData ibossdata = new DataMap(data);
                IData iboss = new DataMap();
                
                param.put("IN_MODE_CODE", inModeCode);
                param.put("STICK_LIST", temp.getString("STICK_LIST"));
                param.put("INFO_RECV_ID", recvId);
                param.put("DEAL_DATE", SysDateMgr.getOtherSecondsOfSysDate(-1));
                param.put("STATE", DEAL_STATE_FEEDBACK); // 回复
                param.put("RES_DEAL_RAMARK", data.getString("DEAL_RAMARK", ""));
                param.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP", ""));
                param.put("RECV_STAFF_ID", getStaffId());
                param.put("CONTACT_SERIAL_NUMBER", data.getString("CONTACT_PHONE", ""));
                if (!"".equals(reportTypeCode))
                {
                    param.put("REPORT_TYPE_CODE", reportTypeCode);
                }
                if (!"".equals(servRequestType))
                {
                    param.put("SERV_REQUEST_TYPE", servRequestType);
                    param.put("SORT_RESULT_TYPE", servRequestType);
                }

                iboss.put("ATTACH_NAME", temp.getString("STICK_LIST")); // 附件文件名
//                ibossdata.put("INFO_RECV_ID", recvId);
//                ibossdata.put("DEAL_DATE", SysDateMgr.getOtherSecondsOfSysDate(-1));
//                ibossdata.put("RES_DEAL_RAMARK", data.getString("DEAL_REMARK", ""));
//                ibossdata.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP", ""));
//                ibossdata.put("SERV_REQUEST_TYPE", servRequestType);
//                ibossdata.put("CONTACT_PHONE", data.getString("CONTACT_PHONE", ""));
//                ibossdata.put("DEAL_DEPART_ID", getDepartId()); 
//                ibossdata.put("DEAL_STAFF_ID", getStaffId());
                
                iboss.put("INDICT_SEQ", recvId); // 举报全网唯一编码INFO_RECV_ID
                iboss.put("FIRST_REPLY_TIME", SysDateMgr.getOtherSecondsOfSysDate(-1)); // 回复时间
                iboss.put("DEAL_RESULT", data.getString("DEAL_RAMARK", "")); // 举报处理结果
                iboss.put("FULL_RSLT", data.getString("DEAL_REMARK_MAKEUP", "")); // 处理结果补充说明
                iboss.put("SVC_TYPE_ID", servRequestType); // 服务请求编码
                iboss.put("CONTACT_PHONE", data.getString("CONTACT_PHONE", "")); // 联系号码
                iboss.put("HANDING_DEPART", getDepartId()); // 回复部门
                iboss.put("HANDING_STAFF", getStaffId()); // 回复员工

//                IData iboss = ibossdata;//submitDealData(ibossdata);
                String reportSerialNumber = temp.getString("BADNESS_INFO");
                // 1、查询用户入网时间、套餐、品牌、是否实名制
                IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(reportSerialNumber);
                if (IDataUtil.isEmpty(userInfo))
                {
                    iboss.put("SUBS_BRAND", "05");
                }
                else
                {
                    String userId = userInfo.getString("USER_ID");
                    iboss.put("JOIN_TIME", userInfo.getString("IN_DATE", ""));
                    String productId = userInfo.getString("PRODUCT_ID");
                    String productName = userInfo.getString("PRODUCT_NAME");
                    iboss.put("COMBO_TYPE", productId + "-" + productName);
                    String brandCode = userInfo.getString("BRAND_CODE");
                    iboss.put("SUBS_BRAND", judgeBrandCode(brandCode));

                    param.put("BRAND_CODE", judgeBrandCode(brandCode));
                    param.put("IN_DATE", userInfo.getString("IN_DATE", ""));
                    param.put("COMBO_TYPE", productId + "-" + productName);
                    param.put("USER_ID", userId);
                    param.put("CUST_ID", userInfo.getString("CUST_ID", ""));

                    IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID", ""));
                    if (IDataUtil.isEmpty(custInfo))
                    {
                        iboss.put("REAL_NAME_SYS_FLAG", "02");
                    }
                    else
                    {
                        String isRealName = custInfo.getString("IS_REAL_NAME", "");
                        if ("1".equals(isRealName))
                        {
                            iboss.put("REAL_NAME_SYS_FLAG", "01");
                        }
                        else
                        {
                            iboss.put("REAL_NAME_SYS_FLAG", "02");
                        }
                        param.put("IS_REAL_NAME", iboss.getString("REAL_NAME_SYS_FLAG", ""));
                    }

                    // 2、查询用户停机状态
                    IDataset svcInfo = UserSvcInfoQry.getMainSvcUserId(userId);
                    if (IDataUtil.isEmpty(svcInfo))
                    {
                        iboss.put("OUTAGE_STATE", "");
                    }
                    else
                    {
                        String stateCode = svcInfo.getData(0).getString("PersonConst.STATE_CODE", "");
                        if ("0".equals(stateCode))
                        {
                            iboss.put("OUTAGE_STATE", "02");
                        }
                        else
                        {
                            iboss.put("OUTAGE_STATE", "01");
                        }
                        param.put("USER_PersonConst.STATE_CODESET", iboss.getString("OUTAGE_STATE", ""));
                    }

                    // 3、查询用户欠费金额
                    IData owefee = AcctCall.getOweFeeByUserId(userId);
                    String balanceFee = Double.toString(owefee.getDouble("ACCT_BALANCE", 0.00) / 100.0);
                    iboss.put("ARREARAGE", balanceFee);
                    param.put("ALLNEWROWE_FEE", balanceFee);

                    // 4、查询用户短信功能状态
                    IDataset svcstate = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, "5");
                    if (IDataUtil.isEmpty(svcstate))
                    {
                        iboss.put("NOTE_STATE", "02");
                        param.put("NOTE_STATE", "02");
                    }
                    else
                    {
                        String svcStateCode = svcstate.getData(0).getString("STATECODE", "");
                        if ("0".equals(svcStateCode))
                        {
                            iboss.put("NOTE_STATE", "01");
                        }
                        else
                        {
                            iboss.put("NOTE_STATE", "02");
                        }
                        param.put("NOTE_STATE", iboss.getString("NOTE_STATE", ""));
                    }

                    // 5、查询用户彩信功能状态
                    IDataset platsvc = UserPlatSvcInfoQry.queryPlatSvcInfo(userId, "05");
                    if (IDataUtil.isEmpty(platsvc))
                    {
                        iboss.put("MULT_MES_STATE", "02");
                    }
                    else
                    {
                        iboss.put("MULT_MES_STATE", "01");
                    }
                    param.put("MULT_MES_STATE", iboss.getString("MULT_MES_STATE", ""));
                }
                // 6、查询用户其他业务是否消费
                iboss.put("OTHER_BUS_FLAG", "99");
                param.put("OTHER_BUS_FLAG", "99");

                // 7、举报对象
                iboss.put("REP_TYPE", data.getString("REPORT_TYPE_CODE", ""));
                param.put("REPORT_TYPE_CODE", data.getString("REPORT_TYPE_CODE", ""));

                Dao.save("TF_F_BADNESS_INFO", param, new String[]
                { "INFO_RECV_ID" }, Route.CONN_CRM_CEN);
                
                //2014-09-26 点对点短信屏蔽功能，将数据投诉与被投诉号码同步到网监平台
                String servType = temp.getString("ALL_SERV_REQUEST_TYPE", "").trim();
                if(servType!=null && servType.length()>12){
                    servType = servType.trim().substring(0, 12);
                }
                if (servType.equals(ManageForbiddenPointBean.OTHER_PROVINCE_SERV_TYPE) || servType.equals(ManageForbiddenPointBean.OTHER_SYS_SERV_TYPE) || servType.equals(ManageForbiddenPointBean.CURRENT_PROVINCE_SERV_TYPE))
                {
                    //2014-09-26新增，只对0109-对举报号码屏蔽 才插点对点表
                    if(StringUtils.equals("0109", data.getString("DEAL_RAMARK", ""))) {
                        CSAppCall.call("SS.ManageForbiddenPointSVC.addForbiddenData", temp);
                    }
                }
                
                IData ibossResult = IBossCall.dealQBadnessImpeachInfoIboss(iboss);
                // IData ibossResult = new DataMap();
                // ibossResult.put("X_RSPCODE", "0000");
                if (!"0000".equals(ibossResult.getString("X_RSPCODE", "")) || badInfos == null)
                {
                    CSAppException.apperr(DedInfoException.CRM_DedInfo_77, ibossResult.getString("X_RESULTINFO", "IBOSS接口调用失败!"));
                }
            }
            else
            {
                CSAppException.apperr(DedInfoException.CRM_DedInfo_78);
            }
        }
        IData result = new DataMap();
        result.put("RESULT_CODE", "0");
        result.put("RESULT_INFO", "信息处理成功!");
        return IDataUtil.idToIds(result);
    }

    public String dealRequestTypeCode(IData data) {
    	String requestTypeCode = data.getString("FOURTH_TYPE_CODE", "");
    	
    	if (!StringUtils.isEmpty(data.getString("FIFTH_TYPE_CODE", ""))) {
    		requestTypeCode = data.getString("FIFTH_TYPE_CODE", "");
    	}
    	
    	if (!StringUtils.isEmpty(data.getString("SIXTH_TYPE_CODE", ""))) {
    		requestTypeCode = data.getString("SIXTH_TYPE_CODE", "");
    	}
    	
    	if (!StringUtils.isEmpty(data.getString("SERVEN_TYPE_CODE", ""))) {
    		requestTypeCode = data.getString("SERVEN_TYPE_CODE", "");
    	}
    	
    	return requestTypeCode;
    }
    
    /**
     * 不良信息回复举报数据转换
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    private IData submitDealData(IData data) throws Exception
    {
        IData temp = new DataMap();
        temp.put("INDICT_SEQ", data.getString("INFO_RECV_ID", ""));// 举报全网唯一编码INFO_RECV_ID
        temp.put("SVC_TYPE_ID", data.getString("SERV_REQUEST_TYPE", ""));// 服务请求编码
        temp.put("DEAL_RESULT", data.getString("RES_DEAL_RAMARK", ""));// 举报处理结果
        temp.put("FULL_RSLT", data.getString("DEAL_REMARK_MAKEUP", ""));// 处理结果补充说明
        temp.put("HANDING_STAFF", data.getString("DEAL_STAFF_ID", ""));// 回复员工
        temp.put("HANDING_DEPART", data.getString("DEAL_DEPART_ID", ""));// 回复部门
        temp.put("FIRST_REPLY_TIME", data.getString("DEAL_DATE", ""));// 回复时间
        temp.put("CONTACT_PHONE", data.getString("CONTACT_PHONE", ""));// 联系号码
        temp.put("ATTACH_LIST", data.getString("ATTACH_LIST", ""));// 附件列表
        temp.put("ATTACH_NAME", data.getString("ATTACH_NAME", ""));// 附件文件名
        return temp;
    }

    /**
     * 不良信息退回
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset untreadBadnessInfos(IData data) throws Exception
    {
        IDataset badInfos = new DatasetList(data.getString("BADNESS_TABLE", "{}"));
        if (IDataUtil.isNotEmpty(badInfos))
        {
            for (int i = 0; i < badInfos.size(); i++)
            {
                IData badInfo = badInfos.getData(i);
                IData param = new DataMap();
                param.put("INFO_RECV_ID", badInfo.getString("INFO_RECV_ID"));
                IDataset result = new BadnessQueryBean().queryBaseBadnessInfo(param, null);

                IData badDeal = BadnessInfoQry.qryBadnessDealInfoByPK(badInfo.getString("INFO_RECV_ID"), PersonConst.STATE_ARCH);
                if (IDataUtil.isEmpty(badDeal))
                {
                    param.clear();
                    param.put("INFO_RECV_ID", badInfo.getString("INFO_RECV_ID"));
                    param.put("DEAL_RAMARK", data.getString("DEAL_DEAL_REMARK"));
                    param.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP"));
                    param.put("SERV_REQUEST_TYPE", result.getData(0).getString("SERV_REQUEST_TYPE"));
                    param.put("CONTACT_SERIAL_NUMBER", data.getString("CONTACT_PHONE"));
                    param.put("DEAL_STAFF_ID", getStaffId());
                    param.put("DEAL_DEPART_ID", getDepartId());
                    param.put("DEAL_DATE", SysDateMgr.getSysDate());
                    param.put("STATE", PersonConst.STATE_UNTREAD);

                    Dao.insert("TF_F_BADNESS_INO_DEAL", param, Route.CONN_CRM_CEN);
                }
                if (PersonConst.STATE_HEAD.equals(result.getData(0).getString("STATE")))
                {
                    param.clear();
                    param.put("INFO_RECV_ID", badInfo.getString("INFO_RECV_ID"));
                    param.put("DEAL_DATE", SysDateMgr.getOtherSecondsOfSysDate(-1));
                    param.put("STATE", PersonConst.STATE_UNTREAD); // 回退
                    param.put("RES_DEAL_RAMARK", data.getString("DEAL_REMARK"));
                    // setAlertInfo(pd.getData().getString("RES_DEAL_REMARK"));
                    param.put("CONTACT_SERIAL_NUMBER", data.getString("CONTACT_PHONE"));
                    param.put("DEAL_REMARK_MAKEUP", data.getString("DEAL_REMARK_MAKEUP"));
                    Dao.save("TF_F_BADNESS_INFO", param, new String[]
                    { "INFO_RECV_ID" }, Route.CONN_CRM_CEN);

                    IData ibossdata = new DataMap();
                    ibossdata.put("INDICT_SEQ", badInfo.getString("INFO_RECV_ID"));
                    ibossdata.put("UNTREAD_TIME", SysDateMgr.getOtherSecondsOfSysDate(-1));
                    ibossdata.put("HANDLING_COMMNET", data.getString("DEAL_REMARK_MAKEUP"));
                    ibossdata.put("PHONE_NUM", data.getString("CONTACT_PHONE"));
                    ibossdata.put("HANDING_DEPART", getDepartId());
                    ibossdata.put("HANDING_STAFF", getStaffId());

                    IData ibossResult = IBossCall.dealreturnBadIboss(ibossdata);
                    if (!"0000".equals(ibossResult.getString("X_RSPCODE", "")))
                    {
                        CSAppException.apperr(DedInfoException.CRM_DedInfo_79, ibossResult.getString("X_RESULTINFO", "IBOSS接口调用失败!"));
                    }
                }
            }
        }
        return null;
    }
}
