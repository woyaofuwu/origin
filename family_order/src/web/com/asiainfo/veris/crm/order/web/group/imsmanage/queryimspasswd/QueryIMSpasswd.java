
package com.asiainfo.veris.crm.order.web.group.imsmanage.queryimspasswd;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QueryIMSpasswd extends GroupBasePage
{
    public abstract void setCondition(IData condition);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
   
    public abstract void setInfoCount(long infoCount);
    
    /**
     * 作用：查询集团基本信息
     * 
     * @author 
     * @param cycle
     * @exception Throwable
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData conParams = getData();
        String groupId = conParams.getString("cond_GROUP_ID", "");
        if (StringUtils.isBlank(groupId))
        {
            groupId = conParams.getString("POP_cond_GROUP_ID");
        }

        // 调用后台服务，查集团客户信息
        IData custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);

        if (IDataUtil.isEmpty(custInfo))
        {
            IData errresult = setRetInfo(-1, "查询集团信息失败", "该集团客户信息不存在！");
            setAjax(errresult);
            return;
        }
        setGroupInfo(custInfo);
        
        String custId = custInfo.getString("CUST_ID");
        String custManagerId =  custInfo.getString("CUST_MANAGER_ID");//集团的客户经理ID
        
        String curManagerId = getVisit().getStaffId();//当前登录的用户
        if(!curManagerId.equals(custManagerId)){
            IData errresult = setRetInfo(-1, "查询IMS集团成员密码", "只有集团客户经理才能查询该集团下IMS融合业务的成员密码！");
            setAjax(errresult);
            return;
        }
        
        // 查询IMPU信息
        IData iparam = new DataMap();
        iparam.put("CUST_ID", custId);
        iparam.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IDataOutput dataOutput = CSViewCall.callPage(this, "CS.UserImpuInfoQrySVC.queryUserImpuPasswdInfoByCustId", 
                   iparam, getPagination("pageNav"));
        
        IDataset impuInfos = new DatasetList();
        if (null != dataOutput && dataOutput.getData().size() > 0){
            impuInfos = dataOutput.getData();
            if(IDataUtil.isNotEmpty(impuInfos)){
                for (int i = 0, size = impuInfos.size(); i < size; i++)
                {
                    IData impuInfo = impuInfos.getData(i);
                    if(IDataUtil.isNotEmpty(impuInfo)){
                        String imsPassword = impuInfo.getString("IMS_PASSWORD");
                        if(StringUtils.isNotBlank(imsPassword)){
                            String password = DESUtil.decrypt(imsPassword);
                            impuInfo.put("IMS_PASSWORD", password);
                        }
                    }
                }            
            }
        }
        
        setInfos(impuInfos);
        setInfoCount(dataOutput.getDataCount());
        
    }
    
    /**
     * 根据成员的号码查询IMS集团成员的密码
     * @param cycle
     * @throws Throwable
     */
    public void getGrpImsInfoByMebNumber(IRequestCycle cycle) throws Throwable
    {
        IData conParams = getData();
        String serialNumberB = conParams.getString("cond_SERIALNUMBER_B", "");
     
        IData mebInfo = UCAInfoIntfViewUtil.qryUserInfoBySn(this, serialNumberB,false);
        if(IDataUtil.isEmpty(mebInfo)){
            IData errresult = setRetInfo(-1, "查询成员号码信息失败", "该成员用户信息不存在！");
            setAjax(errresult);
            return;
        }
        
        //是否订购了多媒体桌面电话
        String userIdB = mebInfo.getString("USER_ID","");
        IData userParam = new DataMap();
        userParam.put("USER_ID_B", userIdB);
        userParam.put("RELATION_TYPE_CODE", "S1");
        IDataset userRelation = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserIdBAndRelaTypeCode", userParam);
        if(IDataUtil.isEmpty(userRelation)){
            IData errresult = setRetInfo(-1, "查询该号码信息是否订购多媒体桌面电话失败", "该号码未订购多媒体桌面电话！");
            setAjax(errresult);
            return;
        }
        
        
        String grpUserId = userRelation.getData(0).getString("USER_ID_A","");
        String serialNumberA = userRelation.getData(0).getString("SERIAL_NUMBER_A","");
        IData grpUserInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, grpUserId, false);
        if(IDataUtil.isEmpty(grpUserInfo)){
            IData errresult = setRetInfo(-1, "查询该成员所在的多媒体桌面电话集团信息失败", "根据USER_ID["+ grpUserId + "]，查找集团用户信息不存在！");
            setAjax(errresult);
            return;
        }
        
        String grpCustId = grpUserInfo.getString("CUST_ID","");
        //调用后台服务，查集团客户信息
        IData grpCustInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, grpCustId, false);
        if(IDataUtil.isEmpty(grpCustInfo)){
            IData errresult = setRetInfo(-1, "查询集团信息失败", "根据CUST_ID["+ grpCustId + "],查询集团客户信息不存在！");
            setAjax(errresult);
            return;
        }        
        
        String custManagerId =  grpCustInfo.getString("CUST_MANAGER_ID");//集团的客户经理ID
        String curManagerId = getVisit().getStaffId();//当前登录的用户
        
        if(!curManagerId.equals(custManagerId)){
            IData errresult = setRetInfo(-1, "查询IMS集团成员密码", "只有集团客户经理才能查询该集团下IMS融合业务的成员密码！");
            setAjax(errresult);
            return;
        }
        
        setGroupInfo(grpCustInfo);
        
        // 查询IMPU信息
        IData iparam = new DataMap();
        iparam.put("USER_ID", userIdB);
        IDataOutput dataOutput = CSViewCall.callPage(this, "CS.UserImpuInfoQrySVC.queryUserImpuPasswdByUserId", 
                 iparam, getPagination("pageNav"));
        
        IDataset impuInfos = new DatasetList();
        if (null != dataOutput && dataOutput.getData().size() > 0){
            impuInfos = dataOutput.getData();
            if(IDataUtil.isNotEmpty(impuInfos)){
                for (int i = 0, size = impuInfos.size(); i < size; i++)
                {
                    IData impuInfo = impuInfos.getData(i);
                    if(IDataUtil.isNotEmpty(impuInfo)){
                        String imsPassword = impuInfo.getString("IMS_PASSWORD");
                        if(StringUtils.isNotBlank(imsPassword)){
                            String password = DESUtil.decrypt(imsPassword);
                            impuInfo.put("IMS_PASSWORD", password);
                            impuInfo.put("SERIAL_NUMBER_A", serialNumberA);
                        }
                    }
                }            
            }
        }
        
        setInfos(impuInfos);
        setInfoCount(dataOutput.getDataCount());
        setCondition(conParams);
    }
}
