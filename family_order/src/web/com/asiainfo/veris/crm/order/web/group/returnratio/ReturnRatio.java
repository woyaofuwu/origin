
package com.asiainfo.veris.crm.order.web.group.returnratio;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class ReturnRatio extends GroupBasePage
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
        String staffId = getVisit().getStaffId();

        boolean enableFlag = false;
        
        // 查询区域信息
        IData svcData = new DataMap();
        if (staffId.substring(0, 4).matches("HNSJ|HNYD|SUPE"))
        {
            svcData.put("AREA_FRAME", getTradeEparchyCode());
            enableFlag = true;
        }
        else
        {
            svcData.put("AREA_FRAME", getVisit().getCityCode());
            enableFlag = false;
        }

        IDataset cityList = CSViewCall.call(this, "CS.AreaInfoQrySVC.qryAeraByAreaFrame", svcData);

        condData.put("CITY_CODE", getVisit().getCityCode());
        condData.put("ENABLE_FLAG", enableFlag);

        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", "RETURNRATIO");
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	condData.put("AUDIT_INFO_SHOW","false");
        }else{
        	condData.put("AUDIT_INFO_SHOW","true");
        }
        
        // 设置返回值
        setCityList(cityList);
        setCondition(condData);
    }
    
    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryRatioOtherInfo(IRequestCycle cycle) throws Exception
    {
        IDataset dataList = new DatasetList();
        IData condData = getData("cond", true);

        IData data = new DataMap();
        data.put("QRY_GROUP_ID", condData.getString("QRY_GROUP_ID"));
        data.put("SERIAL_NUMBER", condData.getString("SERIAL_NUMBER"));
        data.put("START_DATE", condData.getString("START_DATE"));
        data.put("END_DATE", condData.getString("END_DATE"));
        data.put("CITY_CODE", condData.getString("CITY_CODE"));
        
        String groupId = condData.getString("QRY_GROUP_ID","");
        if(StringUtils.isNotBlank(groupId)){
            IData custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
            if(IDataUtil.isEmpty(custInfo)){
                return;
            }
            String custId = custInfo.getString("CUST_ID");
            data.put("CUST_ID", custId);
        }
        
        
        IDataOutput outPut = CSViewCall.callPage(this, "SS.ReturnRationSVC.queryRatioOtherInfo", data, getPagination("ratioNavBar"));
        dataList = outPut.getData();
        long ratioCount = outPut.getDataCount();
        setRatioList(dataList);
        setRatioCount(ratioCount);

        String loginStaffId = getVisit().getStaffId(); // 系统登录工号

        // 查询区域信息
        IData svcData = new DataMap();

        if (loginStaffId.substring(0, 4).matches("HNSJ|HNYD|SUPE"))
        {
            svcData.put("AREA_FRAME", getTradeEparchyCode());
        }
        else
        {
            svcData.put("AREA_FRAME", getVisit().getCityCode());
        }

        IDataset cityList = CSViewCall.call(this, "CS.AreaInfoQrySVC.qryAeraByAreaFrame", svcData);

        condData.put("CITY_CODE", condData.getString("CITY_CODE"));
        condData.put("ENABLE_FLAG", condData.getString("ENABLE_FLAG"));

        setCityList(cityList);
        setCondition(condData);

        // 设置返回数据
        setAjax(condData);
                
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
     * 提交方法
     * 
     * @param cycle
     * @throws Throwable
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Throwable
    {
        IData condData = getData();

        String operation = condData.getString("OPERATION","");
        String activeApproveCode = "";
        
        // 调用服务数据
        IData svcData = new DataMap();
        
        if(operation != null && "0".equals(operation)){//新增操作
            
            String user_id = condData.getString("USER_ID");
            String groupId = condData.getString("GROUP_ID");
            activeApproveCode = condData.getString("ACTIVE_APPROVE_CODE");
            if(org.apache.commons.lang.StringUtils.isNotBlank(activeApproveCode)&&activeApproveCode.getBytes().length>50){
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "活动审批编号字符不能超过50，请重新输入！");
            }
            
            svcData.put("GROUP_ID", groupId);
            svcData.put("USER_ID", user_id);
            svcData.put("RETURN_RATION", condData.getString("RETURN_RATION"));
            svcData.put("START_ACTIVE_DATE", condData.getString("START_ACTIVE_DATE"));
            svcData.put("END_ACTIVE_DATE", condData.getString("END_ACTIVE_DATE"));
            svcData.put("ACTIVE_APPROVE_CODE", condData.getString("ACTIVE_APPROVE_CODE"));
            svcData.put("OPERATION", operation);
            
        } else if(operation != null && "2".equals(operation)){//修改操作
            
            String user_id = condData.getString("EUSER_ID");
            activeApproveCode = condData.getString("EACTIVE_APPROVE_CODE");
            if(org.apache.commons.lang.StringUtils.isNotBlank(activeApproveCode)&&activeApproveCode.getBytes().length>50){
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "活动审批编号字符不能超过50，请重新输入！");
            }
            
            IData resultData = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, user_id);
            if(IDataUtil.isNotEmpty(resultData)){
                IData grpCustInfo = resultData.getData("GRP_CUST_INFO");
                if(IDataUtil.isNotEmpty(grpCustInfo)){
                    svcData.put("GROUP_ID",grpCustInfo.getString("GROUP_ID"));
                }
            }
            svcData.put("USER_ID", user_id);
            svcData.put("EUSER_ID", user_id);
            svcData.put("ERETURN_RATION", condData.getString("ERETURN_RATION"));
            svcData.put("ESTART_ACTIVE_DATE", condData.getString("ESTART_ACTIVE_DATE"));
            svcData.put("EEND_ACTIVE_DATE", condData.getString("EEND_ACTIVE_DATE"));
            svcData.put("EACTIVE_APPROVE_CODE", condData.getString("EACTIVE_APPROVE_CODE"));
            svcData.put("INST_ID", condData.getString("INST_ID"));
            svcData.put("OPERATION", operation);
            
        } else if(operation != null && "1".equals(operation)){//删除操作
            
            String user_id = condData.getString("EUSER_ID");
            
            IData resultData = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, user_id);
            if(IDataUtil.isNotEmpty(resultData)){
                IData grpCustInfo = resultData.getData("GRP_CUST_INFO");
                if(IDataUtil.isNotEmpty(grpCustInfo)){
                    svcData.put("GROUP_ID",grpCustInfo.getString("GROUP_ID"));
                }
            }
            
            svcData.put("USER_ID", user_id);
            svcData.put("EUSER_ID", user_id);
            svcData.put("ERETURN_RATION", condData.getString("ERETURN_RATION"));
            svcData.put("ESTART_ACTIVE_DATE", condData.getString("ESTART_ACTIVE_DATE"));
            svcData.put("EEND_ACTIVE_DATE", condData.getString("EEND_ACTIVE_DATE"));
            svcData.put("EACTIVE_APPROVE_CODE", condData.getString("EACTIVE_APPROVE_CODE"));
            svcData.put("INST_ID", condData.getString("INST_ID"));
            svcData.put("OPERATION", operation);
            
        }
                
        String voucherFileList = condData.getString("VOUCHER_FILE_LIST","");
        String auditStaffId = condData.getString("AUDIT_STAFF_ID","");
        if(StringUtils.isNotBlank(voucherFileList))
        {
        	svcData.put("VOUCHER_FILE_LIST", voucherFileList);
        }
        if(StringUtils.isNotBlank(auditStaffId))
        {
        	svcData.put("AUDIT_STAFF_ID", auditStaffId);
        }
        
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.ReturnRationSVC.crtTrade", svcData);
        
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", "RETURNRATIO");
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	condData.put("AUDIT_INFO_SHOW","false");
        }else{
        	condData.put("AUDIT_INFO_SHOW","true");
        }
        
        // 设置返回数据
        setAjax(retDataset);

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
    public void queryTradeInfo(IRequestCycle cycle, IData custinfo) throws Exception
    {
        String groupId = custinfo.getString("GROUP_ID", "");
        if (StringUtils.isBlank(groupId))
        {
            groupId = getParameter("cond_GROUP_ID");
        }
  
        // 调用后台服务，查集团客户信息
        IData custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        String custId = custInfo.getString("CUST_ID", "");
        IData param = new DataMap();
        param.put("CUST_ID", custId);

        IDataset tradeInfos = new DatasetList();

        // 调用后台服务，根据CUST_ID查询集团的用户信息
        IData params = new DataMap();
        params.put("CUST_ID", custId);
        IDataset result = CSViewCall.call(this, "SS.ReturnRationSVC.queryProduct", params);

        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0; i < result.size(); i++)
            {
                tradeInfos.add(result.getData(i));
                String productId = result.getData(i).getString("PRODUCT_ID");
                String serialNumber = result.getData(i).getString("SERIAL_NUMBER");

                // 查询产品信息
                String productNameString = ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, productId);
                String productname = productId + "|" + productNameString + "|" + serialNumber;
                tradeInfos.getData(i).put("PRODUCT_NAME", productname);
            }
        }
        else
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该集团没有满足条件的用户");
        }

        setInfos(tradeInfos);
    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setCityList(IDataset cityList);
    
    public abstract void setCondition(IData condition);
    
    public abstract void setRatioCount(long ratioCount);

    public abstract void setRatioList(IDataset ratioList);

}
