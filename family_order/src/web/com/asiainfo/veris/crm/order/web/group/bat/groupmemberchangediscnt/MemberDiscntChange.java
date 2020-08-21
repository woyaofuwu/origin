
package com.asiainfo.veris.crm.order.web.group.bat.groupmemberchangediscnt;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

/**
 * 集团专网成员批量优惠变更
 * 
 * @author liujy
 */
public abstract class MemberDiscntChange extends GroupBasePage
{

    /**
     * 根据group_id查询集团基本信息 默认传入为cond_GROUP_ID
     * 
     * @author zhujm 2009-03-06
     * @param cycle
     * @throws Throwable
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData groupInfo = queryGroupCustInfo(cycle);
        setGroupInfo(groupInfo);
    }

    public abstract IData getUserInfo();

    /**
     * 初始化批量弹出窗口页面
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        getData().put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        getData().put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        getData().put("EPARCHY_CODE", getTradeEparchyCode());

        setCondition(getData());
    }

    /**
     * 查询包下优惠
     * 
     * @description
     * @author xiaozp
     * @date May 28, 2009
     * @version 1.0.0
     * @param cycle
     * @throws Throwable
     */
    public void queryPackageDiscnt(IRequestCycle cycle) throws Throwable
    {

        String userId = getParameter("USER_ID");

        IData param = new DataMap();
        param.put("USER_ID", userId);

        IDataset memProductList = CSViewCall.call(this, "CS.UserGrpPkgInfoQrySVC.qryGrpCustomizeDiscntByUserId", param);

        setCustomDiscnt(memProductList);
    }

    /**
     * 查询集团客户编码下某产品的全部用户信息
     * 
     * @author xieyuan
     * @throws Throwable
     */
    public void queryUserInfosByProductCustId(IRequestCycle cycle) throws Throwable
    {

        // 获取产品编码
        String product_id = getParameter("PRODUCT_ID", "");
        // 获取集团客户编码
        String cust_id = getParameter("CUST_ID", "");

        IData para = new DataMap();
        para.put("CUST_ID", cust_id);
        para.put("PRODUCT_ID", product_id);

        IDataset userinfos = new DatasetList();
        String brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, product_id);

        if ("BOSG".equals(brandCode))
        {// productId 为BBOSS 商品ID
            IData param = new DataMap();
            param.put("PRODUCT_ID_A", product_id);
            IDataset proList = CSViewCall.call(this, "CS.ProductCompRelaInfoQrySVC.getCompReleInfo", param);
            for (int i = 0, size = proList.size(); i < size; i++)
            {
                IData proData = proList.getData(i);// 取产品product_id信息
                String proProductId = proData.getString("PRODUCT_ID_B");

                IDataset bbossInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, cust_id, proProductId, false);
                if (!IDataUtil.isEmpty(bbossInfo))
                {
                    userinfos.addAll(bbossInfo);
                }
            }
        }
        else
        {
            userinfos = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, cust_id, product_id, false);
        }
        IData userInfo = getUserInfo();
        if (IDataUtil.isNotEmpty(userInfo))
        {
            for (int i = 0, size = userinfos.size(); i < size; i++)
            {
                IData userData = userinfos.getData(i);
                if (userData.getString("USER_ID", "").equals(userInfo.getString("USER_ID", "")))
                {
                    userData.put("CHECKED", "true");
                    break;
                }
            }
        }
        setUserInfos(userinfos.size() > 0 ? userinfos : null);
        
        //add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        IData voucherInfo = new DataMap();
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", product_id);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	voucherInfo.put("MEB_VOUCHER_FILE_SHOW","false");
        }else{
        	voucherInfo.put("MEB_VOUCHER_FILE_SHOW","true");
        }
        this.setVoucherInfo(voucherInfo);
        //add by chenzg@20180705-end-REQ201804280001集团合同管理界面优化需求----
    }

    public abstract void setCondition(IData condition);

    public abstract void setCustomDiscnt(IDataset customDiscnt);// 设置用户选择优惠

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setUserInfo(IData userInfo);// 用户信息

    public abstract void setUserInfos(IDataset userInfos);
    
    public abstract void setVoucherInfo(IData voucherInfo);
}
