
package com.asiainfo.veris.crm.order.web.frame.csview.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

/**
 * 查询跨省工单流转状态(BBOSS)
 * 
 * @author weixb3
 */
public abstract class GrpProvCprt extends CSBasePage
{

    public abstract IData getCondition();

    public abstract IDataset getInfos();

    /**
     * @Description: 初始化工单状态页面查询
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        IDataset poList = UpcViewCall.queryPoByValid(this);
        setPoList(poList);
    }

    /**
     * @Description: 根据页面条件查询出结果
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public void queryProvCprt(IRequestCycle cycle) throws Exception
    {

        IData initdata = getData("cond", true);

        IDataset dataset = CSViewCall.call(this, "CS.TradeOtherInfoQrySVC.queryProvCprt", initdata);

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

        IDataset poList = UpcViewCall.queryPoByValid(this);
        setPoList(poList);
        
        setCondition(initdata);
        setInfos(dataset);

    }

    /**
     * @Description: ESOP初始化工单状态页面查询
     * @author weixb3
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public void queryProvCprtforEsop(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData ctrlInfo = new DataMap();
        IData condition = new DataMap();

        IData extData = CSViewCall.call(this, "CS.TradeExtInfoQrySVC.queryTradeExtForEsop", data).getData(0);
        if (null == extData)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        }
        else
        {
            IData temp = new DataMap();
            temp.put("TRADE_ID", extData.getString("TRADE_ID", ""));
            IDataset resultData = CSViewCall.call(this, "CS.TradeExtInfoQrySVC.queryPotradeStateByTradeIdForEsop", temp);
            setInfos(resultData);

            // 如果获取到ESOP的数据 结束流程按钮有效
            if (resultData.size() > 0)
            {
                condition.put("cond_IS_ENDFLOW", "true");
            }
        }

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

        condition.put("EOS", eos);
        setCondition(condition);
        setCtrlInfo(ctrlInfo);
    }

    /**
     * 发送ESOP报文数据
     * 
     * @param productId
     * @param paramCode
     * @author weixb3
     * @version
     */
    public void sendEsopMsg(IRequestCycle cycle) throws Exception
    {
        IData eosData = new DataMap(getData().getString("EOS"));

        String ibsysid = eosData.getString("IBSYSID", "");
        // 通知ESOP
        if (!"".equals(ibsysid))
        {
            IData param = new DataMap();
            param.put("USER_ID", "");
            param.put("IBSYSID", eosData.getString("IBSYSID", ""));
            param.put("NODE_ID", eosData.getString("NODE_ID", ""));
            param.put("TRADE_ID", "");
            param.put("BPM_TEMPLET_ID", eosData.getString("BPM_TEMPLET_ID", ""));
            param.put("MAIN_TEMPLET_ID", eosData.getString("MAIN_TEMPLET_ID", ""));
            param.put("CITY_CODE", getVisit().getCityCode());
            param.put("DEPART_ID", getVisit().getDepartId());
            param.put("DEPART_NAME", getVisit().getDepartName());
            param.put("EPARCHY_CODE", getTradeEparchyCode());
            param.put("STAFF_ID", getVisit().getStaffId());
            param.put("STAFF_NAME", getVisit().getStaffName());
            param.put("DEAL_STATE", "0");
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
            param.put("WORK_ID", eosData.getString("WORK_ID", "")); // BPM工作标识,界面提交时传其它不传

            CSViewCall.call(this, "SS.ESOPTcsGrpBusiIntfSvc.callEosGrpBusi", param).getData(0);
            IData result = new DataMap();
            result.put("RESULT", "SUCCESS");
            setAjax(result);

        }

    }

    public abstract void setCondition(IData condition);

    public abstract void setCtrlInfo(IData ctrlInfo);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setPoList(IDataset poList);
    
    public abstract void setPoProductList(IDataset poProductList);

}
