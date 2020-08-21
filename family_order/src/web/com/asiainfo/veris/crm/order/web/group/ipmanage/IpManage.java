
package com.asiainfo.veris.crm.order.web.group.ipmanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.uca.UCAInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class IpManage extends GroupBasePage
{

    /**
     * 校验固定电话，获取新虚拟USER_ID,拼必选服务，包串
     * 
     * @param pd
     * @param td
     * @return IDataset
     * @throws Exception
     */
    public void checkPhone(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();
        IData param = new DataMap();
        IDataset phones = new DatasetList();
        IData phone = new DataMap();
        IData indata = new DataMap();
        String ipPhone = condData.getString("SERIAL_NUMBER_G", "");
        String ipService = condData.getString("IPSERVICE", "");
        String ippackage = condData.getString("PACKAGESVC", "");
        String productHidden = condData.getString("PRODUCT_ID_HIDDEN", "");
        String serviceStr = "", serviceStrTemp = "", packageSvc = "", packageSvcTemp = "";
        // 取系统时间
        String phoneTimes = SysDateMgr.getSysTime();
        phone.put("phoneTime", phoneTimes);
        
        // 查找产品改造服务
        if (ipService.equals(""))
        {
            indata.put("PRODUCT_ID", productHidden);
            indata.put("TRADE_STAFF_ID", "SUPERUSR");
            indata.put("FORCE_TAG", "1");

            String staffId = "SUPERUSR";

            boolean service = true;

            IDataset serviceDatas = CSViewCall.call(this, "CS.SvcInfoQrySVC.getSvcByProudctId", indata);
            if (IDataUtil.isNotEmpty(serviceDatas))
            {
                for (int p = 0; p < serviceDatas.size(); p++)
                {
                    IData tempSvc = serviceDatas.getData(p);
                    String svcId = tempSvc.getString("SERVICE_ID");

                    service = StaffPrivUtil.isSvcPriv(staffId, svcId);

                    if (service)
                    {
                        serviceStrTemp = tempSvc.getString("SERVICE_ID", "");
                        packageSvcTemp = "-1";
                        serviceStr += serviceStrTemp + "@";
                        packageSvc += packageSvcTemp + "@" + serviceStrTemp + "~";
                    }
                }
            }
        }
        else
        {
            serviceStr = ipService;
            packageSvc = ippackage;
        }
        param.put("SERIAL_NUMBER", ipPhone);
        param.put("NET_TYPE_CODE", "00");
        param.put("REMOVE_TAG", "0");
        IDataset tempPhones = CSViewCall.call(this, "CS.UserInfoQrySVC.queryUserInfoBySN", param, null);
        if (tempPhones.size() == 1)
        {
            String serialNumber = "";
            IData tempphone = (IData) tempPhones.get(0);
            String serialNumB = tempphone.getString("SERIAL_NUMBER");
            IDataset tempUUs = RelationUUInfoIntfViewUtil.qryRelaUUInfosBySerialNumberB(this, serialNumB, getTradeEparchyCode());
            if (IDataUtil.isNotEmpty(tempUUs) && tempUUs.size() == 1)
            {
                IData tempUU = (IData) tempUUs.get(0);
                String strRelationCode = tempUU.getString("RELATION_TYPE_CODE");
                String userIdA = tempUU.getString("USER_ID_A");
                IDataset relationList = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCodeRoleCodeB(this, userIdA, strRelationCode, "1", getTradeEparchyCode());

                if (IDataUtil.isNotEmpty(relationList) && relationList.size() == 1)
                {
                    IData list = (IData) relationList.get(0);
                    serialNumber = list.getString("SERIAL_NUMBER_B");
                }
            }
            if ("".equals(serialNumber))
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该号码已经开通了IP业务!");
            }
            else
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该号码已经开通了IP业务!绑定的手机号码为" + serialNumber);
            }
        }
        else
        {
            IData data = new DataMap();
            data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
            IDataset out = CSViewCall.call(this, "CS.SeqMgrSVC.getUserId", data);
            String userid = out.getData(0).getString("seq_id", "");
            phone.put("USER_ID_B", userid);
        }
        phone.put("serviceStr", serviceStr);
        phone.put("packageSvc", packageSvc);
        phone.put("SERIAL_NUMBER", ipPhone);
        phone.put("DISCNT_CODE", condData.getString("DISCNT_CODE"));
        phone.put("PASSWORD", condData.getString("PASSWORD"));
        phone.put("DISCNT_CODE", condData.getString("DISCNT_CODE"));
        phone.put("PRODUCT_ID", productHidden);
        phones.add(phone);
        setAjax(phones);

    }

    public void confirm(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();
        IData dataInput = new DataMap();
        dataInput.put("GRP_SERIAL_NUMBER", condData.getString("cond_SERIAL_NUMBER"));
        dataInput.put("CUST_NAME", condData.getString("CUST_NAME"));
        dataInput.put("PSPT_TYPE_CODE", condData.getString("PSPT_TYPE_CODE"));
        dataInput.put("PSPT_ID", condData.getString("PSPT_ID"));
        dataInput.put("PSPT_ADDRESS", condData.getString("PSPT_ADDRESS"));
        dataInput.put("POST_CODE", condData.getString("POST_CODE"));
        dataInput.put("CONTACT_PHONE", condData.getString("CONTACT_PHONE"));
        dataInput.put("REMARK", condData.getString("REMARK"));
        dataInput.put("USER_TYPE_CODE", condData.getString("USER_TYPE_CODE"));
        dataInput.put("PAY_NAME", condData.getString("PAY_NAME"));
        dataInput.put("PAY_MODE_CODE", condData.getString("PAY_MODE_CODE"));
        dataInput.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        dataInput.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        dataInput.put("TRADE_TYPE_CODE", "2914");
        dataInput.put("DATA_LIST", new DatasetList(condData.getString("DATA_LIST", "[]")));
        IDataset dataset = CSViewCall.call(this, "SS.IpManageSVC.crtTrade", dataInput);
        setAjax(dataset);
    }

    /**
     * 根据手机USER_ID获取IP直通车固定号码资料
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBindIPPhone(String userId) throws Exception
    {
        IDataset others = new DatasetList();
        IDataset bindipphones = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCodeRoleCodeB(this, userId, "51", "1", getTradeEparchyCode());

        if (IDataUtil.isNotEmpty(bindipphones))
        {
            for (int i = 0; i < bindipphones.size(); i++)
            {
                IData userinfo = new DataMap();
                IData bindipphone = bindipphones.getData(i);

                IData param = new DataMap();
                param.put("USER_ID", bindipphone.getString("USER_ID_B"));
                param.put("REMOVE_TAG", "0");
                param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
                IDataset userinfos = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfosByUserId", param);
                if (IDataUtil.isEmpty(userinfos))
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "无IP固定号码用户主表资料");
                }

                for (int n = 0; n < userinfos.size(); n++)
                {
                    userinfo = userinfos.getData(n);
                    String productId = userinfo.getString("PRODUCT_ID");
                    if ("608001".equals(productId))// 取集团成员附加产品product_id供页面使用
                    {
                        userinfo.put("USER_ID_B", userinfo.getString("USER_ID"));
                        userinfo.put("SERIAL_NUMBER_G", userinfo.getString("SERIAL_NUMBER"));
                        userinfo.put("BRAND_CODE", userinfo.getString("BRAND_CODE"));
                        userinfo.put("PRODUCT_ID", userinfo.getString("PRODUCT_ID"));
                        userinfo.put("OLD_BRAND_CODE", userinfo.getString("BRAND_CODE"));
                        userinfo.put("OLD_PRODUCT_ID", userinfo.getString("PRODUCT_ID"));
                        userinfo.put("M_DEAL_TAG", "4");
                        userinfo.put("TEMP_PWD", userinfo.getString("USER_PASSWD"));
                        userinfo.put("CONDITIONM", "未修改");
                        userinfo.put("OLCOM_TAG", "0");
                        break;
                    }
                }

                IData info = new DataMap();
                info.put("USER_ID", userinfo.getString("USER_ID"));
                info.put("USER_ID_A", userId);
                info.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

                IDataset usersvcinfos = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.getUserSvcByUserIdAB", info);
                //info.put("PRODUCT_ID", userinfo.getString("PRODUCT_ID"));

                IDataset userdiscnts = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.getUserDiscntByUserIdAB", info);

                if (IDataUtil.isNotEmpty(usersvcinfos))
                {
                    String servieid = "";
                    String package_svc = "";
                    for (int j = 0; j < usersvcinfos.size(); j++)
                    {
                        IData usersvcinfo = usersvcinfos.getData(j);
                        servieid = usersvcinfo.getString("SERVICE_ID") + "@";
                        package_svc = usersvcinfo.getString("PACKAGE_ID") + "@" + usersvcinfo.getString("SERVICE_ID") + "~";
                    }
                    userinfo.put("IPServiceText", servieid);
                    userinfo.put("OLD_IPServiceText", servieid);
                    userinfo.put("PACKAGESVC", package_svc);
                }
                if (IDataUtil.isNotEmpty(userdiscnts))
                {
                    IData userdiscnt = userdiscnts.getData(0);
                    userinfo.put("OLD_DISCNT_CODE", userdiscnt.getString("DISCNT_CODE"));
                    userinfo.put("DISCNT_CODE", userdiscnt.getString("DISCNT_CODE"));
                }
                others.add(userinfo);
            }
        }
        setOtherList(others);
    }
    
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IDataset selectList = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndEparchyCode(this, "CSM", "4", getTradeEparchyCode());
        if (IDataUtil.isEmpty(selectList))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "获取查询IP电话号码类型无数据！");
        }

        setSelectList(selectList);
    }

    public void queryIpUserInfosBySN(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);
        String serial_number = condData.getString("SERIAL_NUMBER");

        // 用户信息查询
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serial_number);

        IData result = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, serial_number);

        if (IDataUtil.isEmpty(result))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该服务号码不存在！");
        }

        IData custInfo = result.getData("GRP_CUST_INFO");
        IData userInfo = result.getData("GRP_USER_INFO");
        IData acctInfo = result.getData("GRP_ACCT_INFO");
        condData.put("cond_GROUP_ID", custInfo.getString("GROUP_ID"));

        String brandCode = userInfo.getString("BRAND_CODE");

        if (!"IP10".equals(brandCode))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "不是IP集团号码，不能办理本业务");
        }
        setUserinfo(userInfo);
        String custId = custInfo.getString("CUST_ID");
        IData custDataset = UCAInfoIntf.qryGrpCustomerInfoByCustId(this, custId);
        if (IDataUtil.isEmpty(custDataset))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该服务号码客户资料不存在!");
        }
        custInfo.put("PSPT_ID", custDataset.getString("PSPT_ID"));
        custInfo.put("PSPT_TYPE_CODE", custDataset.getString("PSPT_TYPE_CODE"));
        setCustinfo(custInfo);

        String paymodecode = acctInfo.getString("PAY_MODE_CODE");
        if (!"0".equals(paymodecode))
        {
            inparam.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
            IDataset acctsigninfos = CSViewCall.call(this, "CS.AcctInfoQrySVC.getConsignInfoByAcctId", inparam);

            if (IDataUtil.isNotEmpty(acctsigninfos))
            {
                IData acctsigninfo = acctsigninfos.getData(0);

                acctInfo.put("SUPER_BANK_CODE", acctsigninfo.getString("SUPER_BANK_CODE"));
                acctInfo.put("BANK_CODE", acctsigninfo.getString("BANK_CODE"));
                acctInfo.put("CONTRACT_ID", acctsigninfo.getString("CONTRACT_ID"));
                acctInfo.put("BANK_ACCT_NO", acctsigninfo.getString("BANK_ACCT_NO"));
            }
        }
        setAcctinfo(acctInfo);

        String userId = userInfo.getString("USER_ID");
        queryBindIPPhone(userId);
        queryInfo(cycle);

        IData info = new DataMap();
        String product_id = "608001";
        info.put("PRODUCT_ID", product_id);
        
        IData productsList = CSViewCall.callone(this, "SS.ProductInfoSVC.getProductByPK", info);

        IDataset selectListProduct = new DatasetList();
        IData productData = new DataMap();
        
        String product_name = productsList.getString("PRODUCT_NAME");
        String productname = product_id + "|" + product_name;
        productData.put("PRODUCT_NAME", productname);
        productData.put("PRODUCT_ID", productsList.getString("PRODUCT_ID"));
        selectListProduct.add(productData);

        setSelectListProduct(selectListProduct);

        info.put("SERIAL_NUMBER_S", getTradeEparchyCode());
        info.put("SERIAL_NUMBER_END_S", getTradeEparchyCode());
        setInfo(info);

        info.put("USER_ID", userId);

        IDataset selectListDiscnt = CSViewCall.call(this, "CS.GrpUserPkgInfoQrySVC.getGrpCustomizeDiscntByUserId", info);
        setSelectListDiscnt(selectListDiscnt);
    }

    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        confirm(cycle);
    }

    public abstract void setInfo(IData info);

    public abstract void setAcctinfo(IData acctinfo);

    public abstract void setUserinfo(IData userinfo);

    public abstract void setCustinfo(IData custinfo);

    public abstract void setCondition(IData condition);

    public abstract void setOtherList(IDataset otherList);

    public abstract void setSelectList(IDataset selectList);

    public abstract void setSelectListDiscnt(IDataset selectListDiscnt);

    public abstract void setSelectListProduct(IDataset selectListProduct);

}
