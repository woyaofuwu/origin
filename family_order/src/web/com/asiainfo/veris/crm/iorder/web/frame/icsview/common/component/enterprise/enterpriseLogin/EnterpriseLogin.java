package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.enterpriseLogin;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.BizVisit;
import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizTempComponent;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;

public abstract class EnterpriseLogin extends BizTempComponent{

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String listener = getPage().getData().getString("ajaxListener", "");
        
        //有外框信息则不需要加载组件防止登陆对象冲突
        String noLoginFlag= getPage().getData().getString("NOLogin_FLAG", "");
        if(!StringUtils.equals("true", noLoginFlag)){
        	return ;
        }
        

        if (isAjax)
        {
            includeScript(writer, "scripts/iorder/icsserv/component/enterprise/enterpriseLogin/enterpriseLogin.js", false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/enterprise/enterpriseLogin/enterpriseLogin.js", false, false);
        }

        IData info = new DataMap();

        info.put("id", StringUtils.isBlank(getId()) ? "enterpriseLoginItem" : getId());
        
        IDataset custQueryType = StaticUtil.getStaticList("CUST_QUERY_TYPE");
        setCustOperTypes(custQueryType);
        
        if(listener.equals("ajaxQuery")){
        	 ajaxQuery();
        }
       
    }
    /**
     *设置集团查询方式
     * @throws Exception 
     */
    public void  ajaxQuery() throws Exception{

        String code = getPage().getData().getString("CODE", "");
        String value = getPage().getData().getString("VALUE", "");
        String needLogs = getPage().getData().getString("LOGIN_PRAMS");

        debug("查询参数:" + getPage().getData());

        IData result = new DataMap();
        result.put("COUNT", "0");
        result.put("DATAS", new DatasetList());

        IDataset datas  = new DatasetList();
        long count = 0L;
        IDataInput input = new DataInput();
        input.getData().put(code, value);

        try
        {
        	
        	 if("needLog".equals(needLogs)) {
        		 BizVisit bizVisit = (BizVisit) getPage().getData().get("visit");
        		 bizVisit.setSourceName("groupLogin.Login");
        	 }
        	
            // 根据集团客户编码查询
            if ("GROUP_ID".equals(code))
            {
                IData groupInfo = qryGrpCustInfoByGrpId(value);
                if(groupInfo != null && groupInfo.size() > 0)
                {
                    datas = new DatasetList(groupInfo);
                    count = 1L;
                }
            }
            // 根据客户名称查询
            else if ("GROUP_NAME".equals(code))
            {
              input.getData().put("CUST_NAME", value);
              IDataOutput output = ServiceFactory.call("CS.GrpInfoQrySVC.qryGrpInfoByGrpName", input,getContext().getPagination());
              datas = output.getData();
              count = output.getDataCount();
            }
            // 根据集团服务号码查询
            else if ("ACCESS_NUM".equals(code))
            {
                datas = qryGrpCustInfoBySerialNum(value);
                if(datas != null && datas.size() > 0)
                {
                    count = datas.size();
                }
//              IData data = ServiceCaller.call("OC.enterprise.IUmSubscriberQuerySV.querySubByAccessNum", input);
//
//                IData subscriber = data.getData("DATAS");
//
//                if (DataUtils.isNotEmpty(subscriber)){
//                  String custId = subscriber.getString("CUST_ID");
//                  input.clear();
//                  input.put("CUST_ID", custId);
//                  IData row = ServiceCaller.call("CC.enterprise.IEntQuerySV.queryEnterpriseByCustId", input, getContext().getPagination());
//                  datas = row.getDataset("DATAS");
//                  count = row.getLong("X_RESULTCOUNT");
//                }
            }
            // 根据成员服务号码查询
            else if ("MEB_ACCESS_NUM".equals(code))
            {
                IData userInfo = qryUserInfoBySn(value);
                String mebUserId = userInfo.getString("USER_ID");
                
                input.getData().put("USER_ID_B", mebUserId);
                input.getData().put("ROUTE_EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));

                IDataOutput relaUUOutput = ServiceFactory.call("CS.RelaUUInfoQrySVC.getUUByUserIdAB", input, getContext().getPagination());
                IDataset userRels = relaUUOutput.getData();
                IDataOutput relaBBOutput = ServiceFactory.call("CS.RelaUUInfoQrySVC.getBBInfoByUserIdAB", input, getContext().getPagination());
                IDataset userBBRels = relaBBOutput.getData();
                userRels.addAll(userBBRels);
                if(userRels != null && userRels.size() > 0)
                {
                    IData grpUserData = new DataMap(); //key:grpuserid, value:grpcustid
                    IData custUserData =new DataMap();//针对bboss商产品会有条bb关系   只需要查一个custid
                    for(int i = 0, sizeI = userRels.size(); i < sizeI; i++)
                    {
                        String grpUserId = userRels.getData(i).getString("USER_ID_A");
                        String custId = grpUserData.getString(grpUserId);
                        if(StringUtils.isBlank(custId))
                        {
                            IData grpUser = qryGrpUserInfoByUserId(grpUserId);
                            if(IDataUtil.isEmpty(grpUser) || StringUtils.isBlank(grpUser.getString("CUST_ID")))
                            {
                                continue;
                            }
                           
                            custId = grpUser.getString("CUST_ID");
                            if(StringUtils.isNotBlank(custUserData.getString(custId))){
                            	continue;
                            }
                            grpUserData.put(grpUserId, custId);
                            
                            IData grpCust = qryGrpCustInfoByCustId(custId);
                            if(IDataUtil.isNotEmpty(grpCust))
                            {
                                datas.add(grpCust);
                            }
                            custUserData.put(custId, grpUserId);
                        }
                    }
                    if(datas != null && datas.size() > 0)
                    {
                        count = datas.size();
                    }
                }
         
            }else if("KEYMAN_SN".equals(code))
            {//集团关键人号码
                IDataInput dataInput = new DataInput();
                dataInput.getData().put("SERIAL_NUMBER", value);
                
                IDataOutput groupOutput = ServiceFactory.call("CS.GrpInfoQrySVC.queryCustGroupByGrpKeymanSn", dataInput, getContext().getPagination());
                datas = groupOutput.getData();
                count = groupOutput.getDataCount();
            }
            // 异常编码提示
            else
            {

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        setCustInfoList(datas);
     
         
    }
    /**
     * 通过集团编码查询集团资料
     * 
     * @param bc
     * @param groupId
     * @return
     * @throws Exception
     */
    private IData qryGrpCustInfoByGrpId(String groupId) throws Exception
    {
        IData params = new DataMap();
        params.put("GROUP_ID", groupId);
        IDataInput input = new DataInput();
        input.getData().putAll(params);
        IDataOutput output = ServiceFactory.call("CS.UcaInfoQrySVC.qryGrpInfoByGrpId", input);
        if(output.getData() != null)
        {
            return output.getData().first();
        }
        return null;
    }
    private IDataset qryGrpCustInfoBySerialNum(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        IDataInput input = new DataInput();
        input.getData().putAll(param);
        IDataOutput output = ServiceFactory.call("CS.UcaInfoQrySVC.qryUserMainProdInfoBySnForGrp", input);
        
        IDataset userList = output.getData();
        if(userList == null || userList.size() == 0)
        {
            return null;
        }
        
        param.clear();
        param.put("CUST_ID", userList.first().getString("CUST_ID"));
        input.getData().putAll(param);
        output = ServiceFactory.call("CS.UcaInfoQrySVC.qryGrpInfoByCustId", input);
        
        IDataset grpCustList = output.getData();
        if(grpCustList == null || grpCustList.size() == 0)
        {
            return null;
        }
        
        String removeTag = grpCustList.first().getString("REMOVE_TAG", "");
        if (!"0".equals(removeTag))
        {
            return null;
        }
        return grpCustList;
    }
    private IData qryUserInfoBySn(String serialNumber) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        IDataInput input = new DataInput();
        input.getData().putAll(inparam);
        IDataOutput userOutput = ServiceFactory.call("CS.UserInfoQrySVC.getUserInfoBySN", input);
        
        IDataset userList = userOutput.getData();
        if(userList == null || userList.size() == 0)
        {
            return null;
        }
        return userList.first();
    }
    private IData qryGrpUserInfoByUserId(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        IDataInput input = new DataInput();
        input.getData().putAll(inparam);
        IDataOutput userOutput = ServiceFactory.call("CS.UcaInfoQrySVC.qryUserMainProdInfoByUserIdForGrp", input);
        
        IDataset userList = userOutput.getData();
        if(userList == null || userList.size() == 0)
        {
            return null;
        }
        return userList.first();
    }
    private IData qryGrpCustInfoByCustId(String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        
        IDataInput input = new DataInput();
        input.getData().putAll(param);
        
        IDataOutput custOutput = ServiceFactory.call("CS.UcaInfoQrySVC.qryGrpInfoByCustId", input);
        IDataset grpCustList = custOutput.getData();
        if(grpCustList == null || grpCustList.size() == 0)
        {
            return null;
        }
        return grpCustList.first();
    }
    public abstract void setInfo(IData info);
    
    public abstract void setCustInfo(IData custInfo);

    public abstract void setCustOperTypes(IDataset custOperTypes);

    public abstract void setCustInfoList(IDataset custInfoList);

    public abstract String getJsFile();

    public abstract String getButtonChoose();
    

}
