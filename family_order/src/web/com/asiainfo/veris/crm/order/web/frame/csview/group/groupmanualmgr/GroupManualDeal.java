
package com.asiainfo.veris.crm.order.web.frame.csview.group.groupmanualmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.ftpmgr.FtpFileAction;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class GroupManualDeal extends CSBasePage
{

    /**
     * 根据file_id删除上次的文件
     * 
     * @author zhujm 2009-03-06
     * @param cycle
     * @throws Throwable
     */
    public void deleteFile(IRequestCycle cycle) throws Throwable
    {

        FtpFileAction ftpFileAction = new FtpFileAction();
        ftpFileAction.setVisit(getVisit());
        IData data = getData();
        String param = data.getString("param");
        param = param.substring(0, param.lastIndexOf(","));
        ftpFileAction.deletes(param);
        queryFileInfo(cycle);
        setCondition(data);

    }

    /**
     * 初始化页面
     * 
     * @author weixb3 2013/10/26
     * @param cycle
     * @throws Throwable
     */
    public void initManual(IRequestCycle cycle) throws Throwable
    {

        IData data = getData();

        IData param = new DataMap();
        param.put("cond_CREATE_STAFF_ID", getVisit().getStaffId());

        // ESOP数据初始化
        IData eos = new DataMap();
        eos.put("USER_ID", "");
        eos.put("IBSYSID", data.getString("IBSYSID", ""));
        eos.put("NODE_ID", data.getString("NODE_ID", ""));
        eos.put("TRADE_ID", data.getString("TRADE_ID", ""));
        eos.put("BPM_TEMPLET_ID", data.getString("BPM_TEMPLET_ID", ""));
        eos.put("MAIN_TEMPLET_ID", data.getString("MAIN_TEMPLET_ID", ""));
        // eos.put("ROLE_ID", pd.getContext().getRoles());
        eos.put("CITY_CODE", getVisit().getCityCode());
        eos.put("DEPART_ID", getVisit().getDepartId());
        eos.put("DEPART_NAME", getVisit().getDepartName());
        eos.put("EPARCHY_CODE", getTradeEparchyCode());
        eos.put("STAFF_ID", getVisit().getStaffId());
        eos.put("STAFF_NAME", getVisit().getStaffName());
        eos.put("DEAL_STATE", "2");
        eos.put("X_TRANS_CODE", "ITF_EOS_TcsGrpBusi");
        eos.put("X_SUBTRANS_CODE", "SaveAndSend");
        eos.put("OPER_CODE", "01");
        eos.put("ORIG_DOMAIN", "ECRM"); // 发起方应用域代码
        eos.put("HOME_DOMAIN", "ECRM"); // 归属方应用域代码
        eos.put("BIPCODE", "EOS2D011"); // 业务交易代码 这个编码要传进来
        eos.put("ACTIVITYCODE", "T2011011"); // 交易代码 这个编码也要传进来
        eos.put("BUSI_SIGN", ""); // 报文类型，BPM要基于此判断
        eos.put("WORK_TYPE", "00"); // 提交类型
        eos.put("PROCESS_TIME", SysDateMgr.getSysDate()); // 处理时间
        eos.put("ACCEPT_DATE", SysDateMgr.getSysDate()); // 受理时间
        eos.put("UPDATE_STAFF_ID", getVisit().getStaffId()); // 受理员工
        eos.put("UPDATE_DEPART_ID", getVisit().getDepartId()); // 受理部门
        eos.put("WORK_ID", data.getString("WORK_ID", "")); // BPM工作标识, 界面提交时传其它不传
        eos.put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));
        eos.put("GROUP_ID", data.getString("GROUP_ID", ""));
        eos.put("SELECT_PRODUCT_ID", data.getString("SELECT_PRODUCT_ID", ""));
        eos.put("FLOW_MAIN_ID", data.getString("FLOW_MAIN_ID", ""));
        eos.put("X_RESULTINFO", "TradeOk");
        eos.put("X_RESULTCODE", "0");

        param.put("EOS", eos);
        setCondition(param);
    }

    /**
     * 根据STAFF_ID查询该员工导入的文件类型
     * 
     * @author zhujm 2009-03-06
     * @param cycle
     * @throws Throwable
     */
    public void queryFileInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IDataOutput dop = CSViewCall.callPage(this, "CS.MFileInfoQrySVC.queryFileInfo", data, getPagination("infonav"));

        IDataset dataset = dop.getData();
        setInfoCount(dop.getDataCount());

        IData ctrlInfo = new DataMap();
        if (dataset.size() == 0)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        }
        else
        {
            ctrlInfo.put("strHint", "查询成功！");
        }
        setCtrlInfo(ctrlInfo);
        setCondition(getData());
        setInfos(dataset);
    }

    /**
     * 发送ESOP报文数据
     * 
     * @param productId
     * @param paramCode
     * @author jch
     * @version
     */
    @SuppressWarnings("unchecked")
    public void sendEsopMsg(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap(getData().getString("EOS"));

        // 通知ESOP
        IData param = new DataMap();
        param.put("USER_ID", "");
        param.put("IBSYSID", data.getString("IBSYSID", ""));
        param.put("NODE_ID", data.getString("NODE_ID", ""));
        param.put("TRADE_ID", data.getString("TRADE_ID", ""));
        param.put("BPM_TEMPLET_ID", data.getString("BPM_TEMPLET_ID", ""));
        param.put("MAIN_TEMPLET_ID", data.getString("MAIN_TEMPLET_ID", ""));
        param.put("CITY_CODE", getVisit().getCityCode());
        param.put("DEPART_ID", getVisit().getDepartId());
        param.put("DEPART_NAME", getVisit().getDepartName());
        param.put("EPARCHY_CODE", getTradeEparchyCode());
        param.put("STAFF_ID", getVisit().getStaffId());
        param.put("STAFF_NAME", getVisit().getStaffName());
        param.put("DEAL_STATE", "2");
        param.put("X_TRANS_CODE", "ITF_EOS_TcsGrpBusi");
        param.put("X_SUBTRANS_CODE", "SaveAndSend");
        param.put("OPER_CODE", "01");

        param.put("ORIG_DOMAIN", "ECRM"); // 发起方应用域代码
        param.put("HOME_DOMAIN", "ECRM"); // 归属方应用域代码
        param.put("BIPCODE", "EOS2D011"); // 业务交易代码 这个编码要传进来
        param.put("ACTIVITYCODE", "T2011011"); // 交易代码 这个编码也要传进来
        param.put("BUSI_SIGN", ""); // 报文类型，BPM要基于此判断
        param.put("WORK_TYPE", "00"); // 提交类型
        param.put("PROCESS_TIME", SysDateMgr.getSysDate()); // 处理时间
        param.put("ACCEPT_DATE", SysDateMgr.getSysDate()); // 受理时间
        param.put("UPDATE_STAFF_ID", getVisit().getStaffId()); // 受理员工
        param.put("UPDATE_DEPART_ID", getVisit().getDepartId()); // 受理部门
        param.put("WORK_ID", data.getString("WORK_ID")); // BPM工作标识, 界面提交时传其它不传
        param.put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));
        param.put("GROUP_ID", data.getString("GROUP_ID", ""));
        param.put("SELECT_PRODUCT_ID", data.getString("SELECT_PRODUCT_ID", ""));
        param.put("FLOW_MAIN_ID", data.getString("FLOW_MAIN_ID", ""));
        param.put("X_RESULTINFO", "TradeOk");
        param.put("X_RESULTCODE", "0");
        CSViewCall.call(this, "SS.ESOPTcsGrpBusiIntfSvc.callEosGrpBusi", param).getData(0);
        IData result = new DataMap();
        result.put("RESULT", "SUCCESS");
        setAjax(result);
    }

    public abstract void setCondition(IData condition);

    public abstract void setCtrlInfo(IData ctrlInfo);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfos(IDataset dataset);
}
