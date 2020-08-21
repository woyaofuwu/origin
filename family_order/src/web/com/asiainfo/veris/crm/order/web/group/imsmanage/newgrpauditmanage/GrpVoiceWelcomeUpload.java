
package com.asiainfo.veris.crm.order.web.group.imsmanage.newgrpauditmanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpVoiceWelcomeUpload extends GroupBasePage
{
    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);
        setCondition(condData);
    }
    
    /**
     * 根据group_id查询集团基本信息
     * 
     * @param cycle
     * @throws Throwable
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData custinfo = queryGroupCustInfo(cycle);
        setInfo(custinfo);

        queryTradeInfo(cycle, custinfo);
    }

    /**
     * 根据集团编码查询集团客户相关信息
     * 
     * @param cycle
     * @return
     * @throws Throwable
     */
    public IData queryGroupCustInfo(IRequestCycle cycle) throws Exception
    {
        IData conParams = getData("cond", true);
        String groupId = conParams.getString("GROUP_ID");
        String custId = conParams.getString("CUST_ID");

        IData custInfo = null;

        if (StringUtils.isNotEmpty(custId))
        {
            custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
        }
        else
        {
            custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        }
        return custInfo;

    }
    
    /**
     * 查询集团客户工单信息
     * 
     * @author
     * @param cycle
     */
    public void queryTradeInfo(IRequestCycle cycle, IData custInfo) throws Exception
    {
        if (IDataUtil.isEmpty(custInfo))
        {
            return;
        }
        
        String custId = custInfo.getString("CUST_ID", "");
        String groupId = custInfo.getString("GROUP_ID", "");
        // 融合总机
        IDataset userInfos = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, custId, "6130", false);
        
        if (IDataUtil.isEmpty(userInfos))
        {
            CSViewException.apperr(GrpException.CRM_GRP_499, groupId);
        }

        if (IDataUtil.isNotEmpty(userInfos))
        {
            for (int i = 0; i < userInfos.size(); i++)
            {
                String productId = userInfos.getData(i).getString("PRODUCT_ID");
                String serialNumber = userInfos.getData(i).getString("SERIAL_NUMBER");
                
                // 查询产品信息
                String productNameString = ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, productId);
                String productname = productId + "|" + productNameString + "|" + serialNumber;
                userInfos.getData(i).put("PRODUCT_NAME", productname);
            }
        }
        else
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该集团未订购融合总机产品");
        }

        setInfos(userInfos);
    }
    
    
    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();

        // 调用服务数据
        IData svcData = new DataMap();
        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("GRP_FILE_LIST", condData.getString("GRP_FILE_LIST", ""));
        svcData.put("WORDS_DES", condData.getString("WORDS_DES"));
        
        // 查询用户VPN信息
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, condData.getString("USER_ID"));

        if (IDataUtil.isEmpty(userVpnData))
        {
            CSViewException.apperr(GrpException.CRM_GRP_498, condData.getString("USER_ID"));
        }

        if (!"2".equals(userVpnData.getString("VPN_USER_CODE")))
        {
            CSViewException.apperr(GrpException.CRM_GRP_497);
        }
        String vpnNo = userVpnData.getString("VPN_NO");
        svcData.put("VPN_NO", vpnNo);
        
        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.GrpVoiceWelcomeSVC.crtTrade", svcData);

        // 设置返回数据
        setAjax(retDataset);
    }
    
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setCondition(IData condition);

}
