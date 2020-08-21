package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.grouplogin;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizTempComponent;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.iorder.pub.consts.GroupConstants;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.svcutil.datainfo.uca.IUCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class GroupLogin extends BizTempComponent
{

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String listener = getPage().getData().getString("ajaxListener", "");

        if (isAjax && StringUtils.isNotBlank(listener))
        {
            if ("ajaxQuery".equals(listener))
            {
                ajaxQuery();
            }
            else if ("queryDetailByGroupId".equals(listener))
            {
                queryDetailByGroupId();
            }
            else if ("queryEcOrderedOffers".equals(listener))
            {
                queryEcOrderedOffers();
            }
            else if ("queryGroupHealth".equals(listener))
            {
            	queryGroupHealth();
            }
            else if ("queryGroupContact".equals(listener))
            {
            	queryGroupContact();
            }
            else if ("queryMoreContact".equals(listener))
            {
            	queryMoreContact();
            }
            return;
        }

        String js1 = "frame/login/group/GroupLogin.js";
        if (isAjax)
        {
            includeScript(writer, js1, false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin(js1, false, false);
        }

        String id = getId();

        StringBuilder config = new StringBuilder();
        config.append("{");
        config.append("}");

        StringBuilder initScript = new StringBuilder();

        initScript.append("window.").append(id).append(" = new EnterpriseLogin(\"").append(id).append("\", ").append(config.toString()).append(");\n");

        if (isAjax)
        {
            addScriptContent(writer, initScript.toString());
        }
        else
        {
            getPage().addScriptBeforeBodyEnd(id + "_init", initScript.toString());
        }
    }

    public void ajaxQuery() throws Exception
    {
        String code = getPage().getData().getString("CODE", "");
        String value = getPage().getData().getString("VALUE", "");

        debug("查询参数:" + getPage().getData());

        IData result = new DataMap();
        result.put("COUNT", 0L);
        result.put("DATAS", new DatasetList());

        IDataset datas  = new DatasetList();
        long count = 0L;
        IData input = new DataMap();
        input.put(code, value);

        try
        {
            // 根据集团客户编码查询
            if ("GROUP_ID".equals(code))
            {
                IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, value);
                if(IDataUtil.isNotEmpty(groupInfo))
                {
                    datas = new DatasetList(groupInfo);
                    count = 1L;
                }
                
//                IDataInput dataInput = new DataInput();
//                dataInput.getData().putAll(input);
//            	IDataOutput dataOutput = ServiceFactory.call("CC.enterprise.IEntQuerySV.queryEnterpriseByGroupId", dataInput, getContext().getPagination());
//            	IData row = dataOutput.getData().first(); 
//                datas = row.getDataset("DATAS");
//                count = row.getLong("X_RESULTCOUNT");
            }
            // 根据客户名称查询
            else if ("GROUP_NAME".equals(code))
            {
            	input.clear();
            	input.put("CUST_NAME", value);
            	IDataOutput output = UCAInfoIntfViewUtil.qryGrpCustInfoByCustName(this, value, getContext().getPagination());
                datas = output.getData();
                count = output.getDataCount();
            }
            // 根据898集团产品编码查询
            else if ("GROUP_OFFERID".equals(code))
            {
            	input.clear();
            	input.put("GROUP_OFFERID", value);
            	IDataOutput output = UCAInfoIntfViewUtil.qryGrpCustInfoByCustName(this, value, getContext().getPagination());
                datas = output.getData();
                count = output.getDataCount();
            }
            // 根据集团服务号码查询
            else if ("ACCESS_NUM".equals(code))
            {
//            	IData data = ServiceCaller.call("OC.enterprise.IUmSubscriberQuerySV.querySubByAccessNum", input);
//            	
//                IData subscriber = data.getData("DATAS");
                
                IData subscriber = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, value, false);

                if (IDataUtil.isNotEmpty(subscriber)){
                	String custId = subscriber.getString("CUST_ID");
                	
                	IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId, false);
                	if(IDataUtil.isNotEmpty(groupInfo))
                    {
                        datas = new DatasetList(groupInfo);
                        count = 1L;
                    }
//                	input.clear();
//                	input.put("CUST_ID", custId);
//                	IData row = ServiceCaller.call("CC.enterprise.IEntQuerySV.queryEnterpriseByCustId", input, getContext().getPagination());
//                	datas = row.getDataset("DATAS");
//                	count = row.getLong("X_RESULTCOUNT");
                }
            }
            // 根据成员服务号码查询
            else if ("MEB_ACCESS_NUM".equals(code))
            {
                IData userInfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, value, false);
                String mebUserId = userInfo.getString("USER_ID");
                
                IDataInput dataInput = new DataInput();
                dataInput.getData().put("USER_ID_B", mebUserId);
                dataInput.getData().put("ROUTE_EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
                
                IDataOutput relaOutput = ServiceFactory.call("CS.RelaUUInfoQrySVC.queryRelaUUBBByUserIdB", dataInput, getContext().getPagination());
                IDataset userRels = relaOutput.getData();
                if(userRels != null && userRels.size() > 0)
                {
                    IData grpUserData = new DataMap(); //key:grpuserid, value:grpcustid
                    for(int i = 0, sizeI = userRels.size(); i < sizeI; i++)
                    {
                        String grpUserId = userRels.getData(i).getString("USER_ID_A");
                        String custId = grpUserData.getString(grpUserId);
                        if(StringUtils.isBlank(custId))
                        {
                            IData grpUser = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, grpUserId, false);
                            if(grpUser.isEmpty() || StringUtils.isBlank(grpUser.getString("CUST_ID")))
                            {
                                continue;
                            }
                            custId = grpUser.getString("CUST_ID");
                            
                            grpUserData.put(grpUserId, custId);
                            
                            IData grpCust = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId, false);
                            if(!grpCust.isEmpty())
                            {
                                datas.add(grpCust);
                            }
                        }
                    }
                    if(datas != null && datas.size() > 0)
                    {
                        count = datas.size();
                    }
                }

            }
            else if("KEYMAN_SN".equals(code))
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

        result.put("COUNT", count);
        if(IDataUtil.isNotEmpty(datas))
        {
            for (int i = 0; i < datas.size(); i++)
            {
                IData temp = datas.getData(i);
                temp.put("CLASS_ID", temp.getString("CLASS_ID", GroupConstants.DEFULT_EC_CLASS));
                temp.put("CLASS_NAME", StaticUtil.getStaticValue("CUSTGROUP_CLASSID", temp.getString("CLASS_ID","10")));

            }
            
        }
        result.put("DATAS", datas);

        debug("查询结果:" + result);

        getPage().setAjax(result);
    }

    /**
     * 根据集团编码获取集团详细信息
     * 
     * @throws Exception
     */
    public void queryDetailByGroupId() throws Exception
    {
        String groupId = getPage().getData().getString("GROUP_ID");
        IData result = new DataMap();

        result.put("GROUP_ID", groupId);

        // 集团资料
        IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        if (IDataUtil.isNotEmpty(groupInfo))
        {
        	
            groupInfo.put("CLASS_ID", groupInfo.getString("CLASS_ID", GroupConstants.DEFULT_EC_CLASS));
            groupInfo.put("CLASS_NAME", StaticUtil.getStaticValue("CUSTGROUP_CLASSID", groupInfo.getString("CLASS_ID","10")));

            groupInfo.put("GROUP_TYPE", groupInfo.getString("GROUP_TYPE", GroupConstants.DEFULT_EC_TYPE));
            groupInfo.put("CALLING_TYPE_CODE", groupInfo.getString("CALLING_TYPE_CODE", GroupConstants.DEFULT_EC_CALLINGTYPE));

//            String honer = StaticUtil.getStaticValue(GroupConstants.EC_CLASS, groupInfo.getString("CLASS_ID"));
//            honer = StringUtils.isNotBlank(honer) ? honer : groupInfo.getString("CLASS_ID") + "类";
            String honer = groupInfo.getString("CLASS_ID") + "类";
            String level = GroupConstants.EC_LEVEL.get(groupInfo.getString("CLASS_ID"));
            String groupTypeName = StaticUtil.getStaticValue(GroupConstants.EC_TYPE, groupInfo.getString("GROUP_TYPE"));
            String callTypeName = StaticUtil.getStaticValue(GroupConstants.EC_CALLINGTYPE, groupInfo.getString("CALLING_TYPE_CODE"));

            groupInfo.put("HONOR", honer);
            groupInfo.put("LEVEL", level);
            groupInfo.put("GROUP_TYPE_NAME", groupTypeName);
            groupInfo.put("CALLING_TYPE_NAME", callTypeName);

            result.put("GROUP_INFO", groupInfo);
        }
        else
        {
            CSViewException.apperr(GrpException.CRM_GRP_713, "根据集团编码[GROUP_ID="+groupId+"]没有查询到集团信息！");
        }

        // 集团客户资料
        String custId = groupInfo.getString("CUST_ID", "");
        IData custInfo = UCAInfoIntfViewUtil.qryMebCustInfoByCustIdAndRoute(this, custId, Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(custInfo))
        {
            result.put("CUST_INFO", custInfo);
        }
        else
        {
            CSViewException.apperr(GrpException.CRM_GRP_713, "根据客户标识[CUST_ID="+custId+"]没有查询到客户信息！");
        }

        // 客户经理
        String mgrId = groupInfo.getString("CUST_MANAGER_ID");
        IData mgrInfo = new DataMap();
        IData managerInfo = new DataMap();
        if (StringUtils.isNotEmpty(mgrId))
        {
            managerInfo = IUCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, mgrId);
            if (IDataUtil.isNotEmpty(managerInfo))
            {
                mgrInfo.putAll(managerInfo);
                mgrInfo.put("CUST_MANAGER_ID", mgrId);
                mgrInfo.put("NAME", managerInfo.getString("CUST_MANAGER_NAME"));
                mgrInfo.put("PHONE", managerInfo.getString("SERIAL_NUMBER"));
                mgrInfo.put("EMAIL", managerInfo.getString("EMAIL"));
            }
            else
            {
                mgrInfo.put("CUST_MANAGER_ID", mgrId);
                mgrInfo.put("NAME", "已离岗");
                mgrInfo.put("PHONE", "");
                mgrInfo.put("EMAIL", "");
            }
        }
        else
        {
            mgrInfo.put("CUST_MANAGER_ID", "未分派");
            mgrInfo.put("NAME", "未分派");
            mgrInfo.put("PHONE", "");
            mgrInfo.put("EMAIL", "");
        }

        result.put("MANAGER_INFO", mgrInfo);

        // 系统给该用户推荐
        result.put("SALE_INFOS", new DatasetList());

        // 基本信息
        IData param = new DataMap();
        param.put("CONTACT", groupInfo.getString("JURISTIC_NAME"));
        param.put("CONTACT_PHONE",groupInfo.getString("GROUP_CONTACT_PHONE"));
        param.put("ADDRESS", groupInfo.getString("GROUP_ADDR"));
        param.put("GROUP_TYPE_NAME", groupInfo.getString("GROUP_TYPE_NAME"));
        param.put("CALLING_TYPE_NAME", groupInfo.getString("CALLING_TYPE_NAME"));
        param.put("ORG_TYPE_A", groupInfo.getString("ORG_TYPE_A"));
       String enterpriseTypeName=StaticUtil.getStaticValue(getVisit(), "TD_S_ENTERPRISETYPE", "ENTERPRISE_TYPE_CODE","ENTERPRISE_TYPE" ,groupInfo.getString("ENTERPRISE_TYPE_CODE"));
        param.put("ENTERPRISE_TYPE_CODE", enterpriseTypeName);
        param.put("ORG_CODE", groupInfo.getString("ORG_STRUCT_CODE"));
        param.put("CUST_ID", groupInfo.getString("CUST_ID"));
        result.put("PARAM", param);

        // 已订购产品
//        String custId = custInfo.getString("CUST_ID");
        IData offersInfos = queryMainOffersByCustId(custId);

        result.put("PROD_LIST", offersInfos);

        getPage().setAjax(result);
    }

    /**
     * 根据客户编码查询主体商品
     * 
     * @param custId
     * @return
     * @throws Exception
     */
    private IData queryMainOffersByCustId(String custId) throws Exception
    {
        IData ecOrderedOffers = new DataMap();
        
        IDataOutput result = IUCAInfoIntfViewUtil.getGrpUserInfoByCustId(this, custId, getContext().getPagination());

        IDataset offersInfos = result.getData();//.getDataset("DATAS");
        if (IDataUtil.isEmpty(offersInfos))
        {
            ecOrderedOffers.put("COUNT", 0);
            ecOrderedOffers.put("DATAS", new DatasetList());
        }
        else
        {
        	IDataset offerListDataset=new DatasetList();
            for (int i = 0, size = offersInfos.size(); i < size; i++)
            {
                IData offer = offersInfos.getData(i);
                //过滤掉bboss产品 只保留商品
                String userIdA=offer.getString("USER_ID_A");
                if("-1".equals(userIdA)){
                	offer.put("NAME", offer.getString("PRODUCT_NAME", ""));
                	offerListDataset.add(offer);
                }
            }
            ecOrderedOffers.put("COUNT", offerListDataset.size());
            ecOrderedOffers.put("DATAS", offerListDataset);
        }

        return ecOrderedOffers;
    }

    public void queryEcOrderedOffers() throws Exception
    {
        String custId = getPage().getData().getString("CUST_ID");

        IData offersInfos = queryMainOffersByCustId(custId);

        IData result = new DataMap();

        result.put("PROD_LIST", offersInfos);

        getPage().setAjax(result);
    }

    /**
     * 查询信息健康度
     * @throws Exception
     */
    public void queryGroupHealth() throws Exception
    {
        String custId = getPage().getData().getString("CUST_ID");

        IDataOutput result =IUCAInfoIntfViewUtil.getGrpAccountDepositByCustId(this, custId, getContext().getPagination());
        IDataset offersInfos = result.getData();
        if(IDataUtil.isEmpty(offersInfos)){
        	 offersInfos = new DatasetList();
        }
         getPage().setAjax(offersInfos);
       
    }
    
    
    /**
     * 查询集团合同
     * @throws Exception
     */
    public void queryGroupContact() throws Exception
    {
        String custId = getPage().getData().getString("CUST_ID");

        IDataOutput result =IUCAInfoIntfViewUtil.getGrpContactInfoByCustId(this, custId, getContext().getPagination());
        IDataset offersInfos = result.getData();
        if(IDataUtil.isEmpty(offersInfos)){
        	 offersInfos = new DatasetList();
        }
        String key = CacheKey.getContactInfoKey(custId);
        SharedCache.set(key, offersInfos.toString(), 1200);
         getPage().setAjax(offersInfos);
       
    }
    
    /**
     * 查询集团合同
     * @throws Exception
     */
    public void queryMoreContact() throws Exception
    {
        String custId = getPage().getData().getString("CUST_ID");

        String key =CacheKey.getContactInfoKey(custId);
        IDataset initInfo = new DatasetList(SharedCache.get(key).toString());
        getPage().setAjax(initInfo);
    }
    
}
