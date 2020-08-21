
package com.asiainfo.veris.crm.order.web.frame.csview.group.destroygroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.custinfo.custgroupmember.CustGroupMemberIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.multitableinfo.UserProductElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationxxtinfo.RelationXXTInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userplatsvcinfo.UserPlatSvcInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class PreView extends GroupBasePage
{

    /*
     * @description BBOSS商品有多个成员产品，同一个成员有可能被添加到多个成员产品下面
     * @author xunyl
     * @date 2013-08-07
     */
    protected IDataset getBBossUsers(String memUserId, String grpUserId, String productId, String eparchCode) throws Throwable
    {
        // 1- 定义返回对象
        IDataset bbossUserIds = new DatasetList();

        // 2- 根据商品用户编号查询出商品对应的所有产品用户
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);
        IDataset relaUUInfoList = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCodeRoleCodeB(this, grpUserId, relationTypeCode, "0");// 0表示商品与产品UU关系
        if (IDataUtil.isEmpty(relaUUInfoList))
        {
            return bbossUserIds;
        }

        // 3- 校验产品用户是否被成员订购
        for (int i = 0; i < relaUUInfoList.size(); i++)
        {
            IData relaUUInfo = relaUUInfoList.getData(i);
            String productUserId = relaUUInfo.getString("USER_ID_B");
            boolean isMebUserInfo = isMebProductUserInfo(productUserId, memUserId, relationTypeCode, eparchCode);
            if (isMebUserInfo)
            {
                bbossUserIds.add(productUserId);
                ;
            }
        }

        // 4- 返回集团下的所有用户编号
        return bbossUserIds;
    }

    // 修改了一下原有的逻辑，如果成员资料不归属任何集团则不在页面上显示是否退出集团
    public IData getGrpMemberExisInfo(String productId, String serialNumber, String mebEparchCode) throws Exception
    {

        IData info = new DataMap();
        info.put("PRODUCT_ID", productId);
        if (!productId.equals("8000") && !productId.equals("6200"))
        {
            info.put("IFJOIN", "false");
            return info;
        }
        // 8000 和 6200的产品
        IDataset grpMemberInfo = CustGroupMemberIntfViewUtil.qryGrpMebsBySN(this, serialNumber, mebEparchCode);
        if (IDataUtil.isNotEmpty(grpMemberInfo))
        {
            info.put("IFJOIN", "true");
            info.put("JOINVALUE", "1");
            info.put("IFDISABLED", "false");
            info.put("TITLENAME", "退出相应集团");
            // 8000的需要增加权限判断,不允许选择退订
            if (productId.equals("8000") && !StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "GROUPMENBER_VPMN_PRV"))
            {
                info.put("IFDISABLED", "true");
                info.put("TITLENAME", "退出相应集团（工号无成员退出集团资料的权限GROUPMENBER_VPMN_PRV）");
            }

        }
        else
        {
            info.put("IFJOIN", "false");
        }
        return info;
    }

    /*
     * @descripiton 获取成员在该集团下的所有元素信息
     * @author xunyl
     * @date 2013-08-07
     */
    protected void getMebElements(IDataset userdestroyelments, String memUserId, String grpUserId, String productId, String eparchycode,String mebSn) throws Throwable
    {
        IDataset useproductelmemets = UserProductElementInfoIntfViewUtil.qryUserElementInfosByUserIdAndUserIdA(this, memUserId, grpUserId, eparchycode);

        IDataset spElements = UserPlatSvcInfoIntfViewUtil.qryMebPlatSvcInfosByUserIdAndGrpProductId(this, memUserId, productId, eparchycode);
        if (IDataUtil.isNotEmpty(spElements))
        {
            for (int j = 0; j < spElements.size(); j++)
            {
                IData map = spElements.getData(j);
                map.put("ELEMENT_TYPE_NAME", "SP服务");
                map.put("STATE", "DEL");
            }
        }
        if ("10009150".equals(productId))// 如果是校讯通业务则添加姓名
        {
            IDataset xxtElements = RelationXXTInfoIntfViewUtil.qryMemInfoBySNForUIPDestroy(this, mebSn, grpUserId);
            for (int i = 0; i < useproductelmemets.size(); i++)
            {
                IData useproductelmemet = useproductelmemets.getData(i);
                for (int k = 0; k < xxtElements.size(); k++)
                {
                    IData xxtElement = xxtElements.getData(k);
                    if ("D".equals(useproductelmemet.getString("ELEMENT_TYPE_CODE")) && xxtElement.getString("RELA_INST_ID").equals(useproductelmemet.getString("INST_ID")))
                    {
                        useproductelmemet.put("NAME", xxtElement.getString("NAME"));
                    }
                }
            }
        }
        userdestroyelments.addAll(useproductelmemets);
        userdestroyelments.addAll(spElements);
    }

    public abstract String getPamRemark();

    /**
     * 用于组织集团成员业务退定预览界面数据
     * 
     * @author zouli
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        IData baseinfo = getData();
        String eparchycode = baseinfo.getString("MEB_EPARCHY_CODE");
        // 查询集团客户信息
        setGrpCustInfo(UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, baseinfo.getString("CUST_ID")));
        // 查询成员用户信息
        setMebUserInfo(UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(this, baseinfo.getString("MEB_USER_ID", ""), eparchycode));

        String memUserId = baseinfo.getString("MEB_USER_ID");
        String grpUserId = baseinfo.getString("GRP_USER_ID");
        String productId = baseinfo.getString("GRP_PRODUCT_ID");
        String mebSn = baseinfo.getString("cond_SERIAL_NUMBER");
        IDataset userdestroyelments = new DatasetList();
        // 根据产品编号获取产品的品牌信息
        String productBrandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId);
        if ("BOSG".equals(productBrandCode))
        {// BBOSS业务
            IDataset bbossUserIds = getBBossUsers(memUserId, grpUserId, productId, eparchycode);
            for (int i = 0; i < bbossUserIds.size(); i++)
            {
                this.getMebElements(userdestroyelments, memUserId, bbossUserIds.get(i).toString(), productId, eparchycode,mebSn);
            }
        }
        else
        {
            this.getMebElements(userdestroyelments, memUserId, grpUserId, productId, eparchycode,mebSn);
        }

        IData infoData = getGrpMemberExisInfo(productId, mebSn, eparchycode);
        
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "7354");
        iparam.put("PARAM_CODE", productId);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	infoData.put("MEB_FILE_SHOW","true");
        }
        boolean flag = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_OPERENTERPRISE");//有权限免稽核
        //add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        iparam.clear();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", productId);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	infoData.put("MEB_VOUCHER_FILE_SHOW","false");
        }else{
        	if(flag){
        		infoData.put("MEB_VOUCHER_FILE_SHOW","false");
        	}else{
        		infoData.put("MEB_VOUCHER_FILE_SHOW","true");
        	}
        }
        //add by chenzg@20180705-end-REQ201804280001集团合同管理界面优化需求----
        
        // 是否可以退出集团资料
        setInfo(infoData);
        
        setUseElementinfos(userdestroyelments);
        
        // 生成支付组件需要的数据
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("PAY_MONEY_CODE", "0");
        data.put("MONEY", getFeeTotal());
        dataset.add(data);
    }

    /*
     * @description 校验产品用户是否被成员订购
     * @author xunyl
     * @date 2014-05-28
     */
    private boolean isMebProductUserInfo(String userId, String mebUserId, String relationTypeCode, String eparchCode) throws Exception
    {
        // 1- 定义返回变量(默认为false)
        boolean result = false;

        // 2- 查询用户是否为当前商品的产品用户
        IDataset productUserInfoList = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndUserIdARelationTypeCode(this, mebUserId, userId, relationTypeCode, eparchCode);
        if (IDataUtil.isNotEmpty(productUserInfoList))
        {
            result = true;
        }

        // 3- 返回查询结果
        return result;
    }

    public abstract void setGrpCustInfo(IData info);

    public abstract void setInfo(IData info);

    public abstract void setMebUserInfo(IData info);

    public abstract void setUseElementinfos(IDataset useElementinfos);
}
