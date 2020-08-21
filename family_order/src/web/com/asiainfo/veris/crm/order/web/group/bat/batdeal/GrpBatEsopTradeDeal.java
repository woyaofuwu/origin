
package com.asiainfo.veris.crm.order.web.group.bat.batdeal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FuncrightException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;

public abstract class GrpBatEsopTradeDeal extends CSBasePage
{

    /**
     * create campaign
     * 
     * @param cycle
     * @throws Exception
     */

    public void createBatDealInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();

        param = CSViewCall.call(this, "SS.BatDealBeanSvc.createBatTask", param).getData(0); // 创建批量任务
        getData().put("IN_PARAM", param);// 将创建批量任务参数放入getData()中成为批量明细的入参

        // importData(cycle); // 导入批量数据

        String batchTaskId = param.getString("BATCH_TASK_ID");
        IData paramData = new DataMap();
        paramData.put("BATCH_TASK_ID", batchTaskId);
        IDataset retDataSet = CSViewCall.call(this, "CS.BatTradeInfoQrySVC.getBatchIdByBatchTaskId", paramData);
        String batchId = retDataSet.getData(0).getString("BATCH_ID");

        paramData.clear();
        paramData.put("BATCH_ID", batchId);

        String batch_oper_type = param.getString("BATCH_OPER_TYPE", "");
        paramData.put("BATCH_OPER_TYPE", batch_oper_type);
        if (batch_oper_type.equals("BATADDBBOSSMEMBER") || batch_oper_type.equals("BATDELBBOSSMEMBER") || batch_oper_type.equals("BATCONBBOSSMEMBER") || batch_oper_type.equals("BATPASBBOSSMEMBER") || batch_oper_type.equals("BATMODBBOSSMEMBER"))
        {// BOSS成员
            CSViewCall.call(this, "SS.BatDealBBossBeanSvc.startBatDealBBossMember", paramData);
        }
        else if (batch_oper_type.equals("BATADDYDZFMEM") || batch_oper_type.equals("BATCONFIRMYDZFMEM") || batch_oper_type.equals("BATOPENYDZFMEM"))
        {// BBOSS一点支付
            CSViewCall.call(this, "SS.BatDealBBossBeanSvc.startBBossYDZFBatDeals", paramData);
        }
        else if (batch_oper_type.equals("BATADDHYYYKMEM") || batch_oper_type.equals("BATOPENHYYYKMEM"))
        {// BBOSS行业应用卡
            CSViewCall.call(this, "SS.BatDealBBossBeanSvc.startBBossHYYYKBatDeals", paramData);
        }
        else
        {
            CSViewCall.call(this, "CS.BatTaskSVC.batTaskNowRun", paramData);
        }

        String eosString = param.getString("EOS");
        if (StringUtils.isEmpty(eosString))
            return;
        IDataset eos = new DatasetList(eosString);
        if (IDataUtil.isNotEmpty(eos))
        {
            IData eosData = eos.getData(0);
            IData esopData = new DataMap();
            // esopData.put("USER_ID", user_id);

            esopData.put("IBSYSID", eosData.getString("IBSYSID", ""));
            esopData.put("PRODUCT_ID", eosData.getString("PRODUCT_ID", ""));
            esopData.put("NODE_ID", eosData.getString("NODE_ID", ""));
            esopData.put("TRADE_ID", eosData.getString("TRADE_ID", ""));
            esopData.put("USER_ID", eosData.getString("USER_ID", ""));
            esopData.put("BPM_TEMPLET_ID", eosData.getString("BPM_TEMPLET_ID", ""));
            esopData.put("MAIN_TEMPLET_ID", eosData.getString("MAIN_TEMPLET_ID", ""));
            // esopData.put("ROLE_ID", getVisit().get);
            esopData.put("CITY_CODE", getVisit().getCityCode());
            esopData.put("DEPART_ID", getVisit().getDepartId());
            esopData.put("DEPART_NAME", getVisit().getDepartName());
            esopData.put("EPARCHY_CODE", getTradeEparchyCode());
            esopData.put("STAFF_ID", getVisit().getStaffId());
            esopData.put("STAFF_NAME", getVisit().getStaffName());
            esopData.put("DEAL_STATE", "2");
            esopData.put("X_SUBTRANS_CODE", "SaveAndSend");
            esopData.put("OPER_CODE", "01");
            esopData.put("ORIG_DOMAIN", "ECRM"); // 发起方应用域代码
            esopData.put("HOME_DOMAIN", "ECRM"); // 归属方应用域代码
            esopData.put("BIPCODE", "EOS2D011"); // 业务交易代码 这个编码要传进来
            esopData.put("ACTIVITYCODE", "T2011011"); // 交易代码 这个编码也要传进来
            esopData.put("BUSI_SIGN", ""); // 报文类型，BPM要基于此判断 eosData.get("","")
            esopData.put("WORK_TYPE", "00"); // 提交类型 页面提交 08失败通知
            esopData.put("PROCESS_TIME", SysDateMgr.getSysTime()); // 处理时间
            esopData.put("ACCEPT_DATE", SysDateMgr.getSysTime()); // 受理时间
            esopData.put("TRADE_EPARCHY_CODE", getTradeEparchyCode()); // 受理地州
            esopData.put("UPDATE_STAFF_ID", getVisit().getStaffId()); // 受理员工
            esopData.put("UPDATE_DEPART_ID", getVisit().getDepartId()); // 受理部门
            esopData.put("TRADE_CITY_CODE", getVisit().getCityCode());
            esopData.put("WORK_ID", eosData.getString("WORK_ID", "")); // BPM工作标识,
            esopData.put("X_RESULTINFO", "TradeOk");
            esopData.put("X_RESULTCODE", "0");

            esopData.put("DEAL_DESC", "esop批量成员添加");
            CSViewCall.call(this, "SS.ESOPTcsGrpBusiIntfSvc.callEosGrpBusi", esopData);
        }
        // CSRedirect.redirectToMsg("批量信息创建成功");
    }

    //

    public void initBatDealInput(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        paraserEsopString(cycle);
        data.put("ESOP_TAG", "ESOP");
        // ctx.setTransfer("tradeData", getTradeData().toString()); 暂时屏蔽
    }

    public void paraserEsopString(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String ibsysid = param.getString("IBSYSID", "");
        String workId = param.getString("WORK_ID", "");
        String operCode = param.getString("OPER_CODE", "");

        if (StringUtils.isEmpty(ibsysid))
        {
            CSViewException.apperr(ParamException.CRM_PARAM_217);
        }

        IData inData = new DataMap();
        inData.put("NODE_ID", param.getString("NODE_ID", ""));
        inData.put("IBSYSID", param.getString("IBSYSID", ""));
        inData.put("SUB_IBSYSID", param.getString("SUB_IBSYSID", ""));
        inData.put("OPER_CODE", "15");
        IData httpResult = CSViewCall.callone(this, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inData);
        if (IDataUtil.isEmpty(httpResult))
            CSViewException.apperr(GrpException.CRM_GRP_508, "接口返回数据为空");

        param.putAll(httpResult);

        String groupId = httpResult.getString("GROUP_ID");
        String productId1 = httpResult.getString("PRODUCT_ID");
        String userId = httpResult.getString("USER_ID");
        String tradeId = httpResult.getString("TRADE_ID");
        if (StringUtils.isEmpty(tradeId))
        {
            tradeId = param.getString("IBSYSID", "");
        }
        // String productTypeCode = httpResult.getString("PRODUCT_TYPE_CODE","");
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId1);

        IData eosData = new DataMap();
        IDataset eos = new DatasetList();
        eosData.put("IBSYSID", param.getString("IBSYSID"));
        eosData.put("SUB_IBSYSID", param.getString("SUB_IBSYSID"));
        eosData.put("NODE_ID", param.getString("NODE_ID"));
        eosData.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
        eosData.put("MAIN_TEMPLET_ID", param.getString("MAIN_TEMPLET_ID"));
        eosData.put("ATTR_CODE", "ESOP");
        eosData.put("ATTR_VALUE", param.getString("IBSYSID"));
        eosData.put("RSRV_STR1", param.getString("NODE_ID"));
        eosData.put("WORK_ID", workId);
        eosData.put("TRADE_ID", tradeId);// param.getString("TRADE_ID", "")
        eosData.put("PRODUCT_ID", productId1);
        eosData.put("USER_ID", userId);
        eos.add(eosData);

        IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);

        String productId = productId1;
        if (!StringUtils.isEmpty(userId))
        {
            IData userInfoData = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userId);
            productId = userInfoData.getString("PRODUCT_ID", "");
        }

        IDataset bizListsw = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, productId, "P", "batEsop", operCode);

        if (IDataUtil.isNotEmpty(bizListsw))
        {
            IData bizData = bizListsw.getData(0);
            String batchOperCode = bizData.getString("ATTR_VALUE", "");

            IData inParam = new DataMap();
            inParam.put("TRADE_ATTR", "2");

            IDataset batchOpertypes = CSViewCall.call(this, "CS.BatDealSVC.queryBatchTypes", inParam);

            IDataset infos = new DatasetList();

            if (IDataUtil.isEmpty(batchOpertypes))
            {
                CSViewException.apperr(FuncrightException.CRM_FUNCRIGHT_4, param.getString("staffId", ""));
            }

            for (int i = 0, size = batchOpertypes.size(); i < size; i++)
            { // 过滤出集团所需要的批量菜单
                if (batchOpertypes.getData(i).getString("CLASS_CODE").equals("GROUPTRADE"))
                    infos.add(batchOpertypes.getData(i));
            }
            if ("".equals(batchOperCode) || infos.toString().indexOf(batchOperCode) == -1)
            {
                CSViewException.apperr(FuncrightException.CRM_FUNCRIGHT_4, param.getString("staffId", ""));
            }

            setBatchOperTypes(infos);

            IData info = new DataMap();
            info.put("BATCH_OPER_CODE", batchOperCode);
            info.put("BATCH_OPER_NAME", bizData.getString("ATTR_NAME", ""));
            info.put("BATCH_TASK_NAME", "ESOP_" + bizData.getString("ATTR_NAME", ""));
            info.put("REMARK", "ESOP批量导入");
            info.put("AUDIT_NO", "ESOP");
            info.put("SMS_FLAG", "0");
            info.put("USER_ID", userId);
            info.put("IBSYSID", ibsysid);
            info.put("PRODUCT_ID", productId);
            info.put("GROUP_ID", groupId);
            info.put("START_DATE", SysDateMgr.getSysDate());
            info.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            info.put("CUST_ID", groupInfo.getString("CUST_ID"));
            info.put("EOS", eos);
            setInfo(info);

            getData().put("BATCH_OPER_TYPE", bizData.getString("ATTR_VALUE", ""));
            queryComps(cycle);
        }
        else
        {
            CSViewException.apperr(FuncrightException.CRM_FUNCRIGHT_3, productId);
        }

    }

    /**
     * 根据业务类型查询组件
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryComps(IRequestCycle cycle) throws Exception
    {
        String batchOperType = getData().getString("BATCH_OPER_TYPE", "");
        /** 获取页面组件 */
        if (!batchOperType.equals(""))
        {
            IData inParam = new DataMap();
            inParam.put("BATCH_OPER_TYPE", batchOperType);

            IData comp = CSViewCall.callone(this, "CS.BatDealSVC.queryBatTypeByPK", inParam);

            comp.put("TEMPLATE_DATA_XLS", "template/bat/group/" + batchOperType + ".xls");
            comp.put("TEMPLATE_FORMART_XML", "import/bat/group/" + batchOperType + ".xml");
            comp.put("BATCH_OPER_TYPE", batchOperType);
            inParam.clear();
            inParam.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
            IData taskIdData = CSViewCall.call(this, "CS.SeqMgrSVC.getBatchId", inParam).getData(0);
            String batTaskId = taskIdData.getString("seq_id");

            comp.put("BATCH_TASK_ID", batTaskId);
            setComp(comp);
        }
    }

    public abstract void setBatchOperTypes(IDataset batchOperTypes);

    public abstract void setComp(IData comp);

    public abstract void setInfo(IData info);

}
