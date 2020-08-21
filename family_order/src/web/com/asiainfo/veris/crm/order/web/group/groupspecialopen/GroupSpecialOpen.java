
package com.asiainfo.veris.crm.order.web.group.groupspecialopen;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GroupSpecialOpen extends GroupBasePage
{
    public void getCustInfobyCustId(String custId) throws Throwable
    {
        // 调用后台服务，查集团客户信息
        IData param = new DataMap();
        param.put("CUST_ID", custId);

        // 获取配置参数判断是否属于可特殊开机的品牌
        IDataset commParamList = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeParaCode1ParaCode6EparchyCode(this, "CSM", "1111", "TYPECODE", "csGroupSpecialOpen", "15", getTradeEparchyCode());

        if (IDataUtil.isEmpty(commParamList))
        {
            return;
        }
        IDataset productDataset = new DatasetList();

        for (int i = 0, iRow = commParamList.size(); i < iRow; i++)
        {
            IData commParamData = commParamList.getData(i);

            param.put("BRAND_CODE", commParamData.getString("PARA_CODE4", ""));

            // 调用后台服务，根据CUST_ID查询集团的用户信息
            IDataset userList = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoByCandB", param);

            if (IDataUtil.isNotEmpty(userList))
            {
                for (int j = 0, jRow = userList.size(); j < jRow; j++)
                {
                    IData userData = userList.getData(j);

                    String productId = userData.getString("PRODUCT_ID", "");
                    String userId = userData.getString("USER_ID");

                    if (productId.matches("9127|9188"))
                    {
                        continue;
                    }

                    // 查询产品信息
                    String productNameString = ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, productId, false);
                    if (StringUtils.isBlank(productNameString))
                    {
                        continue;
                    }
                    StringBuilder builderName = new StringBuilder();
                    builderName.append(productId);
                    builderName.append("|");
                    builderName.append(productNameString);
                    builderName.append("|");
                    builderName.append(userId);

                    IData addProductData = new DataMap();
                    addProductData.put("PRODUCT_NAME", builderName.toString());
                    addProductData.put("USER_ID", userId);

                    productDataset.add(addProductData);
                }
            }
            setProducts(productDataset);
        }
    }

    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData custinfo = queryGroupCustInfo(cycle);
        setInfo(custinfo);
        String custId = custinfo.getString("CUST_ID");
        getCustInfobyCustId(custId);
    }

    /**
     * 根据集团产品编码查询集团用户信息
     * 
     * @param cycle
     * @throws Throwable
     */
    public void getGroupInfoByGrpSn(IRequestCycle cycle) throws Throwable
    {
        IData condData = getData();

        String grpsn = condData.getString("cond_GRP_SN");

        IData result = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpsn);

        if (IDataUtil.isEmpty(result))
        {
            CSViewException.apperr(GrpException.CRM_GRP_268, grpsn);
        }

        IData custInfo = result.getData("GRP_CUST_INFO");
        IData userInfo = result.getData("GRP_USER_INFO");
        condData.put("cond_GROUP_ID", custInfo.getString("GROUP_ID"));

        String brandCode = userInfo.getString("BRAND_CODE");

        getCustInfobyCustId(custInfo.getString("CUST_ID"));

        IData datauser = new DataMap();
        datauser.put("BRAND_CODE", brandCode);
        datauser.put("REMOVE_TAG", "0");
        datauser.put("SERIAL_NUMBER", grpsn);

        if (!"ADCG".equals(userInfo.getString("BRAND_CODE")) && !"MASG".equals(userInfo.getString("BRAND_CODE")))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品无法此界面操作");
        }
        if ("9188".equals(userInfo.getString("PRODUCT_ID")) || "9127".equals(userInfo.getString("PRODUCT_ID")))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品无法此界面操作");
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
        String services = condData.getString("sercheck");

        if (StringUtils.isBlank(services))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您没有选择要恢复的服务或该集团下的服务都是正常状态,不用办理恢复");
        }

        // 以前的param中还有一个PARA_CODE4 =TIME 调用的服务中没用到，
        IDataset commParams = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeParamCode1EparchyCode(this, "CSM", "1111", "TYPECODE", "csGroupSpecialOpen", getTradeEparchyCode());

        if (IDataUtil.isNotEmpty(commParams))
        {
            for (int i = 0; i < commParams.size(); i++)
            {
                IData data = commParams.getData(i);
                if ("TIME".equals(data.getString("PARA_CODE4")))
                {
                    int max_time = Integer.parseInt(data.getString("PARA_CODE5"));
                    int normal_time = Integer.parseInt(condData.getString("NORMAL_TIME"));
                    if (normal_time > max_time)
                    {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "最大时长只能为:" + max_time + "(小时),请重新修改后提交");
                    }
                    break;
                }
            }
        }
        else
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "获取系统配置最大恢复时长失败");
        }

        // 调用服务数据
        IData svcData = new DataMap();
        svcData.put("sercheck", condData.getString("sercheck"));
        svcData.put("GROUP_ID", condData.getString("cond_GROUP_ID"));
        svcData.put("USER_ID", condData.getString("cond_USER_ID"));
        svcData.put("SERIAL_NUMBER", condData.getString("SERIAL_NUMBER"));
        svcData.put("NORMAL_TIME", condData.getString("NORMAL_TIME"));
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset dataset = CSViewCall.call(this, "SS.GroupSpecialOpenSVC.crtTrade", svcData);
        setAjax(dataset);

    }

    /*
     * 按照集团客户编码查询
     */
    public void queryBaseInfo(IRequestCycle cycle) throws Throwable
    {
        String userid = getData().getString("USER_ID");
        queryInfoByUserId(userid);
    }

    /**
     * 根据集团编码查询集团客户相关信息
     * 
     * @throws Throwable
     */
    public IData queryGroupCustInfo(IRequestCycle cycle) throws Throwable
    {
        IData conParams = getData("cond", true);

        String groupId = conParams.getString("GROUP_ID");
        String custId = conParams.getString("CUST_ID");
        IData custInfo = null;
        if (StringUtils.isNotEmpty(custId))
            custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
        else
            custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);

        return custInfo;
    }

    public void queryInfoByUserId(String userid) throws Throwable
    {
        IData datauser = new DataMap();
        datauser.put("USER_ID", userid);
        datauser.put("REMOVE_TAG", "0");

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

        IDataset services = new DatasetList();// 存放各个服务的相关状态,包括最后一次暂停时间
        IDataset userStates = CSViewCall.call(this, "CS.UserGrpPlatSvcInfoQrySVC.getUserAttrByUserId", datauser);// 查询用户当前订购的服务
        if (IDataUtil.isNotEmpty(userStates))
        {
            for (int i = 0; i < userStates.size(); i++)
            {
                IData userState = userStates.getData(i);
                userState.put("NOW_STATE", "A".equals(userState.getString("BIZ_STATE_CODE")) ? "正常" : "暂停");
                // 根据user_id,service_id查询最后一次暂停的时间
                IData svc = new DataMap();
                svc.put("USER_ID", userState.getString("USER_ID"));
                svc.put("BIZ_STATE_CODE", "N");
                svc.put("SERVICE_ID", userState.getString("SERVICE_ID"));

                IDataset svcInfos = CSViewCall.call(this, "CS.SvcInfoQrySVC.getServInfos", svc);
                if (IDataUtil.isNotEmpty(svcInfos))
                {
                    userState.put("SERVICE_NAME", svcInfos.getData(0).getString("SERVICE_NAME"));
                }
                else
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "获取服务名称失败");
                }
                IDataset querLasts = CSViewCall.call(this, "CS.UserGrpPlatSvcInfoQrySVC.getUserAttrByUserIdandSvc", svc);

                String lastParse = "0";
                if (querLasts != null && querLasts.size() > 0)
                {
                    for (int j = 0; j < querLasts.size(); j++)
                    {
                        IData datat = querLasts.getData(j);
                        if ("1".equals(lastParse.compareTo(SysDateMgr.string2Date(datat.get("START_DATE").toString(), "yyyyMMddhhmmss").toString())))
                        {
                            // 如果lastParse大于取出的Start_date则不做任何操作,否则就赋值
                        }
                        else if ("-1".equals(lastParse.compareTo(SysDateMgr.string2Date(datat.get("START_DATE").toString(), "yyyyMMddhhmmss").toString())))
                        {
                            lastParse = datat.getString("START_DATE");
                        }
                        else
                        {
                            lastParse = datat.getString("START_DATE");
                        }
                    }
                    userState.put("LAST_PARSE", lastParse);
                }
                else
                {
                    userState.put("LAST_PARSE", "无暂停记录");
                }
                services.add(userState);
            }
        }
        else
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "平台服务表查询不到相关数据");
        }
        setServices(services);
    }

    /*
     * 根据成员手机号码查询相关信息
     */
    public void queryMemberInfo2(IRequestCycle cycle) throws Throwable
    {

        IData condData = getData("cond", true);
        String serial_number = condData.getString("SERIAL_NUMBER");
        String relationCode = getData().getString("RELATION_CODE", "");

        IData resultInfo = UCAInfoIntfViewUtil.qryMebOrderedGroupInfosBySn(this, serial_number, relationCode);

        setAjax(resultInfo);

    }

    /* 根据手机号码查询出已经订购的集团产品编码 */
    public void getGroupInfo(IRequestCycle cycle) throws Throwable
    {
        IData condData = getData();
        String serialNumber = condData.getString("cond_GROUP_SERIAL_NUMBER");
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);

        String grpsn = params.getString("SERIAL_NUMBER");

        IData result = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, grpsn);

        if (IDataUtil.isEmpty(result))
        {
            CSViewException.apperr(GrpException.CRM_GRP_268, grpsn);
        }

        IData custInfo = result.getData("GRP_CUST_INFO");
        IData userInfo = result.getData("GRP_USER_INFO");
        condData.put("cond_GROUP_ID", custInfo.getString("GROUP_ID"));

        String brandCode = userInfo.getString("BRAND_CODE");

        getCustInfobyCustId(custInfo.getString("CUST_ID"));

        IData datauser = new DataMap();
        datauser.put("BRAND_CODE", brandCode);
        datauser.put("REMOVE_TAG", "0");
        datauser.put("SERIAL_NUMBER", serialNumber);

        if (!"ADCG".equals(userInfo.getString("BRAND_CODE")) && !"MASG".equals(userInfo.getString("BRAND_CODE")))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品无法此界面操作");
        }
        if ("9188".equals(userInfo.getString("PRODUCT_ID")) || "9127".equals(userInfo.getString("PRODUCT_ID")))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品无法此界面操作");
        }
        String userid = userInfo.getString("USER_ID");

        queryInfoByUserId(userid);

    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public void setPageNull(IRequestCycle cycle) throws Throwable
    {
        setProducts(new DatasetList());
        setServices(new DatasetList());
    }

    public abstract void setProducts(IDataset products);

    public abstract void setServices(IDataset services);

}
