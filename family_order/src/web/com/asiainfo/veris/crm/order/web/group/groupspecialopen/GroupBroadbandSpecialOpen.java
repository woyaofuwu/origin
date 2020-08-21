package com.asiainfo.veris.crm.order.web.group.groupspecialopen;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GroupBroadbandSpecialOpen extends GroupBasePage
{

    /**
     * 根据集团产品编码查询集团用户信息
     * 
     * @param cycle
     * @throws Throwable
     */
    public void getGroupInfoByGrpSn(IRequestCycle cycle) throws Throwable
    {
        IData condData = getData();

        String grpsn = condData.getString("GRP_SERIAL_NUMBER");

        IData result = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpsn);

        if (IDataUtil.isEmpty(result))
        {
            CSViewException.apperr(GrpException.CRM_GRP_268, grpsn);
        }

        IData userInfo = result.getData("GRP_USER_INFO");
        if (!"7341".equals(userInfo.getString("PRODUCT_ID")))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该集团产品编码不是集团商务宽带产品，无法此界面操作！");
        }
        String userid = userInfo.getString("USER_ID");
        queryInfoByUserId(userid);
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

        String userId = condData.getString("cond_USER_ID","");
        String productId = condData.getString("cond_PRODUCT_ID","");
        
        if(!"7341".equals(productId))
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "请输入集团商务宽带产品编码查询信息后再提交！");
        }
        
        if(StringUtils.isBlank(userId))
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "请输入集团产品编码查询信息后再提交！");
        }
        
        // 调用服务数据
        IData svcData = new DataMap();
        svcData.put("USER_ID", condData.getString("cond_USER_ID"));
        svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        // 调用服务
        IDataset dataset = CSViewCall.call(this, "SS.BroadbandMemStateChgBatSVC.crtOpenBat", svcData);
        setAjax(dataset);

    }

    public void queryInfoByUserId(String userid) throws Throwable
    {
        IData datauser = new DataMap();
        datauser.put("USER_ID", userid);
        datauser.put("REMOVE_TAG", "0");

        IData param = new DataMap();
        param.put("USER_ID", userid);
        param.put("SERVICE_ID", "734101");
        param.put("STATE_CODE", "5");
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        
        IData param2 = new DataMap();
        param2.put("USER_ID", userid);
        param2.put("SERVICE_ID", "734101");
        param2.put("STATE_CODE", "0");
        param2.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        
        IData param3 = new DataMap();
        param3.put("USER_ID", userid);
        param3.put("SERVICE_ID", "734101");
        param3.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        
        IDataset svcStateInfos = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.getUserLastStateByUserSvc", param3);
        IDataset svc5StateInfos = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.getUserLastNextStateByUserSvc", param);
        IDataset svc0StateInfos = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.getUserSvcBetweenStateByUserID", param2);
        
        if(IDataUtil.isNotEmpty(svc5StateInfos) && IDataUtil.isNotEmpty(svc0StateInfos))
        {
        	
        }
        else
        {
        	if (IDataUtil.isNotEmpty(svcStateInfos))
            {
                for (int i = 0; i < svcStateInfos.size(); i++)
                {
                    IData svcState = svcStateInfos.getData(i);
                    if (!"5".equals(svcState.getString("STATE_CODE")))
                    {
                    	CSViewException.apperr(GrpException.CRM_GRP_713, "该集团商务宽带状态不是欠费停机状态,业务不能继续!");
                    }
                }
            }
        }
        
        IData data = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, userid);

        IData grpuserinfo = data.getData("GRP_USER_INFO");
        IData grpcustinfo = data.getData("GRP_CUST_INFO");

        IData datainfo = new DataMap();
        datainfo.put("GROUP_ID", grpcustinfo.getString("GROUP_ID"));
        datainfo.put("CUST_NAME", grpcustinfo.getString("CUST_NAME"));
        datainfo.put("SERIAL_NUMBER", grpuserinfo.getString("SERIAL_NUMBER"));
        datainfo.put("CITY_CODE", grpcustinfo.getString("CITY_CODE"));
        datainfo.put("PRODUCT_ID", grpuserinfo.getString("PRODUCT_ID"));
        datainfo.put("USER_ID", userid);

        IData datastaff = new DataMap();
        datastaff.put("STAFF_ID", grpcustinfo.getString("CUST_MANAGER_ID"));

        IDataset staffInfos = CSViewCall.call(this, "CS.StaffInfoQrySVC.qryStaffInfoByStaffId", datastaff);

        if (IDataUtil.isNotEmpty(staffInfos))
        {
            datainfo.put("GROUP_MGR_CUST_NAME", staffInfos.getData(0).getString("STAFF_NAME"));
        }
        else
        {
            CSViewException.apperr(CustException.CRM_CUST_1003);
        }

        IData datacity = new DataMap();

        datacity.put("AREA_CODE", datainfo.getString("CITY_CODE"));
        IDataset cityinfos = CSViewCall.call(this, "CS.AreaInfoQrySVC.getAreaByPk", datacity);
        if (IDataUtil.isNotEmpty(cityinfos))
        {
            datainfo.put("CITY_NAME", cityinfos.getData(0).getString("AREA_NAME"));
        }
        else
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "获取归属市县信息失败");
        }
        setCondition(datainfo);

    }
    
    public abstract void setCondition(IData condition);
    
}
