package com.asiainfo.veris.crm.iorder.web.igroup.esop.supplementlinedata;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class SupplementLineData extends CSBasePage {

    public abstract void setInfo(IData info) throws Exception;
    
    public abstract void setLineList(IDataset lineList) throws Exception;
    
    public abstract void setGroupInfo(IData groupInfo) throws Exception;

    public void qryLineInfos(IRequestCycle cycle) throws Exception {
        String groupId = getData().getString("GROUP_ID");
        String sn = getData().getString("SERIAL_NUMBER");
        String productno = getData().getString("PRODUCTNO");
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("SERIAL_NUMBER", sn);
        param.put("PRODUCTNO", productno);
        
        IDataset lineInfos = CSViewCall.call(this, "SS.GrpLineInfoQrySVC.queryLineByGroupIdSnProductNo", param);
        
        setLineList(lineInfos);
    }
    
    public void qryLineInfoByProductNo(IRequestCycle cycle) throws Exception {
        String productNo = getData().getString("PRODUCTNO");
        IData param = new DataMap();
        param.put("PRODUCTNO", productNo);
        
        IData lineInfo = CSViewCall.callone(this, "SS.GrpLineInfoQrySVC.queryLineByProductNO", param);
        
        if(IDataUtil.isNotEmpty(lineInfo)){
            String userId = lineInfo.getString("USER_ID");
            IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userId);
            lineInfo.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
        }
        
        setInfo(lineInfo);
        
        //查询客户信息
        String groupId = getData().getString("GROUP_ID");
        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        setGroupInfo(group);
        
    }

    public void submit(IRequestCycle cycle) throws Exception {
        String ss = getData().getString("LINE_DATA");
        IData lineData = new DataMap(ss);
        if(IDataUtil.isEmpty(lineData)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到页面修改的专线数据！");
        }
        
        String userId = lineData.getString("USER_ID");
        IData param = new DataMap();
        param.put("LINE_DATE", lineData);
        param.put("USER_ID", userId);
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userId);
        param.put("USER_EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        
        IDataset results = CSViewCall.call(this, "SS.ModifyDataLineSVC.crtTrade", param);
        
        if(IDataUtil.isNotEmpty(results)){
            IData data = new DataMap();
            data.put("ORDER_ID", results.first().getString("TRADE_ID"));
            setAjax(data);
        }
    }
}
