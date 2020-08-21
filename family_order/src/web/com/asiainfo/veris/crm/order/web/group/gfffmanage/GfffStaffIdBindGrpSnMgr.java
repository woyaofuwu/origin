
package com.asiainfo.veris.crm.order.web.group.gfffmanage;

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
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;


public abstract class GfffStaffIdBindGrpSnMgr extends GroupBasePage
{
	public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setCondition(IData condition);
    
    public abstract void setInfosCount(long infosCount);

    public abstract void setMarkInfos(IDataset markInfos);
    
    
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

        conParams.put("START_DATES", SysDateMgr.getSysTime());
        setCondition(conParams);
        
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
        IDataset result = CSViewCall.call(this, 
                "SS.BreakGrpPayMarkSVC.queryProductByCustId", param);

        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0; i < result.size(); i++)
            {
                IData dateSet = result.getData(i);
                String productId = dateSet.getString("PRODUCT_ID");
                String serialNumber = dateSet.getString("SERIAL_NUMBER");
                
                // 查询产品信息
                String productNameString = ProductInfoIntfViewUtil
                    .qryProductNameStrByProductId(this, productId);
                StringBuffer sb = new StringBuffer();
                sb.append(productId);
                sb.append("|");
                sb.append(productNameString);
                sb.append("|");
                sb.append(serialNumber);
                dateSet.put("PRODUCT_NAME", sb.toString());
                
                tradeInfos.add(dateSet);
            }
        }
        else
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该集团没有满足条件的用户");
        }

        setInfos(tradeInfos);
    }
    
    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Throwable
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Throwable
    {
        // 调用服务数据
        IData svcData = new DataMap();
        IData condData = getData();

        String markFlag = condData.getString("MARK_FLAG","");
        String userId = condData.getString("USER_ID");
        String startDate = condData.getString("START_DATES");
        String endDate = condData.getString("END_DATES");
        String remark = condData.getString("REMARK_OTHER");
        
        svcData.put("MARK_FLAG", markFlag);
        svcData.put("USER_ID", userId);
        svcData.put("START_DATES", startDate);
        svcData.put("END_DATES", endDate);
        svcData.put("REMARK_OTHER", remark);
        
        String eparchyCode = getTradeEparchyCode();
        svcData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        svcData.put(Route.USER_EPARCHY_CODE, eparchyCode);

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", "SGPR");
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        
        IDataset userInfos = CSViewCall.call(this, 
                "CS.UserOtherInfoQrySVC.getUserOtherByUseridRsrvcode", param);
        if (IDataUtil.isNotEmpty(userInfos))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该" + 
                    userId + "已经存在欠费不截止代付关系,不可再新增!");
        }
        
        // 调用服务
        IDataset retDataset = CSViewCall.call(this,
                "SS.BreakGrpPayMarkSVC.crtTrade", svcData);
        
        // 设置返回数据
        setAjax(retDataset);

    }
    
    /**
     * 查询集团流量自由充统付流量上限值
     * @param cycle
     * @throws Throwable
     * @Author:chenzg
     * @Date:2017-8-29
     */
    public void queryBindInfos(IRequestCycle cycle) throws Throwable
    {
    	 IData data = getData();
         IData inparam = new DataMap();
         String serialNumber = data.getString("SERIAL_NUM_GRP");
         inparam.put("SERIAL_NUMBER", serialNumber);
         inparam.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID", ""));
         
         IDataOutput markOutput = CSViewCall.callPage(this, "SS.GfffUserMaxGprsSetSVC.qryGfffStaffIdBindInfos", inparam, getPagination("ratioNavBar"));
         
         IDataset dataList = new DatasetList();
         dataList = markOutput.getData();
         long infosCount = markOutput.getDataCount();
         
         setMarkInfos(dataList);
         setInfosCount(infosCount);
         
         setCondition(data);
    }
    
    /**
     * 新增一条绑定数据
     * @param cycle
     * @throws Throwable
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public void submitAdd(IRequestCycle cycle) throws Throwable
    {
        // 调用服务数据
        IData svcData = new DataMap();
        IData condData = getData();
        String tradeStaffId = condData.getString("TRADE_STAFF_ID");
        String serialNumber = condData.getString("SERIAL_NUMBER");
        String eparchyCode = getTradeEparchyCode();
        svcData.put("TRADE_STAFF_ID", tradeStaffId);
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        svcData.put(Route.USER_EPARCHY_CODE, eparchyCode);
        
        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.GfffUserMaxGprsSetSVC.addBindInfo", svcData);
        
        // 设置返回数据
        setAjax(retDataset);
    }
    /**
     * 修改绑定数据
     * @param cycle
     * @throws Throwable
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public void submitMod(IRequestCycle cycle) throws Throwable
    {
        // 调用服务数据
        IData svcData = new DataMap();
        IData condData = getData();
        String tradeStaffId = condData.getString("TRADE_STAFF_ID");
        String serialNumber = condData.getString("SERIAL_NUMBER");
        String instId = condData.getString("INST_ID");
        String eparchyCode = getTradeEparchyCode();
        svcData.put("TRADE_STAFF_ID", tradeStaffId);
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put("INST_ID", instId);
        svcData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        svcData.put(Route.USER_EPARCHY_CODE, eparchyCode);
        
        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.GfffUserMaxGprsSetSVC.modBindInfo", svcData);
        
        // 设置返回数据
        setAjax(retDataset);
    }
    /**
     * 删除绑定输数据
     * @param cycle
     * @throws Throwable
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public void submitDel(IRequestCycle cycle) throws Throwable
    {
        // 调用服务数据
        IData svcData = new DataMap();
        IData condData = getData();
        String tradeStaffId = condData.getString("TRADE_STAFF_ID");
        String serialNumber = condData.getString("SERIAL_NUMBER");
        String instId = condData.getString("INST_ID");
        String eparchyCode = getTradeEparchyCode();
        svcData.put("TRADE_STAFF_ID", tradeStaffId);
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put("INST_ID", instId);
        svcData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        svcData.put(Route.USER_EPARCHY_CODE, eparchyCode);
        
        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.GfffUserMaxGprsSetSVC.delBindInfo", svcData);
        
        // 设置返回数据
        setAjax(retDataset);
    }
}
