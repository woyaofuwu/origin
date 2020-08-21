
package com.asiainfo.veris.crm.order.web.group.bat.bataddlargessfluxdiscnt;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public abstract class BatAddLargessFluxDiscnt extends CSBasePage
{
    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        setDiscntList(CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(this, "CSM", "7348", "0", getTradeEparchyCode()));
        setCondition(getData());
    }

    public void getNumberInfo(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        String serialNumber = condData.getString("groupSerialNumber");
        
        //查询集团信息
        IData grpUserData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber, false);
        if (IDataUtil.isEmpty(grpUserData)){
            CSViewException.apperr(GrpException.CRM_GRP_122, serialNumber);
        }
        
        // 查询集团客户信息
        IData custData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, grpUserData.getString("CUST_ID", ""));
        if (IDataUtil.isEmpty(custData)){
            CSViewException.apperr(GrpException.CRM_GRP_192, serialNumber);
        }
        
        String grpUserId = grpUserData.getString("USER_ID","");
        IData inParam = new DataMap();
        inParam.put("USER_ID", grpUserId);
        IDataset gffInfoSets = CSViewCall.call(this, "SS.LargessFluxGrpMainSVC.queryUserGrpGfffInfo", inParam);
        if (IDataUtil.isEmpty(gffInfoSets)){
            CSViewException.apperr(GrpException.CRM_GRP_856, serialNumber);
        }
        
        IData conditonData = getData();
        conditonData.put("groupSerialNumbers", serialNumber);
        setCondition(conditonData);
    }
    
    public abstract void setCondition(IData condition);

    public abstract void setDiscntList(IDataset discntList);
}
