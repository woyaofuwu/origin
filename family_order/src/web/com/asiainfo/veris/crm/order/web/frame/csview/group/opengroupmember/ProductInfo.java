
package com.asiainfo.veris.crm.order.web.frame.csview.group.opengroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupProductPage;

public abstract class ProductInfo extends GroupProductPage
{

    /**
     * 校验资源信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkResourceInfo(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData();
        inparam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        inparam.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "CS.OpenGroupMemberSVC.checkResourceSn", inparam);

        IData retdata = dataset.getData(0);

        IData checkResultData = new DataMap();

        if (IDataUtil.isNotEmpty(retdata))
        {
            checkResultData.putAll(retdata);
            if (retdata.getString("X_RESULTCODE") != null && retdata.getString("X_RESULTCODE").equals("-1")) {
            	setAjax(checkResultData);
            	return;
			}
            
            String resNumber = inparam.getString("RES_VALUE", "");
            setResNumber(resNumber);

            IDataset resList = retdata.getDataset("RES_LIST");
            if (resList != null && resList.size() > 0)
            {
                setResList(resList);
            }
        }
        else
        {
            checkResultData.put("X_RESULTCODE", "-1");
            checkResultData.put("X_RESULTINFO", "资源调用异常！");
        }

        setAjax(checkResultData);

    }

    /**
     * 点击左侧产品包树中的节点后 调用此方法 刷新右侧下方的包展现区域
     * 
     * @param cycle
     * @throws Exception
     */
    public void getPackage(IRequestCycle cycle) throws Exception
    {
        String extendElements = getParameter("EXTEND_ELEMENTS", "");

        if (extendElements.length() > 0)
        {
            IDataset extendElementsSet = new DatasetList(extendElements);
            setExtendElements(extendElementsSet);
        }
    }

    /**
     * 初始化页面
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {

        String grpUserEparchyCode = getData().getString("GRP_USER_EPARCHYCODE");

        // 获取选择的产品
        productId = getData().getString("PRODUCT_ID");
        if (StringUtils.isBlank(productId))
        {
            CSViewException.apperr(GrpException.CRM_GRP_400);
        }

        String groupId = getData().getString("POP_cond_GROUP_ID");
        String custId = getData().getString("CUST_ID", "");

        setGroupId(groupId);

        // 获取产品配置信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryOpnMbProductCtrlInfoByProductId(this, productId);
        setProductCtrlInfo(productCtrlInfo);

        IData dynParam = new DataMap();
        dynParam.put("PRODUCT_ID", productId);
        dynParam.put("CUST_ID", custId);
        dynParam.put("GROUP_ID", groupId);
        dynParam.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);
        dynParam.put("EPARCHY_CODE", grpUserEparchyCode);
        dynParam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        // ESOP参数
        String eos = getData().getString("EOS");
        if (!StringUtils.isEmpty(eos) && !"{}".equals(eos))
        {
            dynParam.put("EOS", new DatasetList(eos));
        }
        setDynParam(dynParam);

        // 集团成员用户三户信息初始化
        initialOpenMeb(cycle);

        // 资源初始化
        initialRes(cycle);

        IData cond = new DataMap();
        cond.put("EPARCHY_CODE", grpUserEparchyCode);
        cond.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);

        setCond(cond);

        setAjax("PRODUCT_ID", productId);
    }

    /**
     * 产品成员开户初始化(三户资料)
     * 
     * @param cycle
     * @throws Exception
     */
    public void initialOpenMeb(IRequestCycle cycle) throws Exception
    {
        IData memCustInfo = getData("MEM_CUST_INFO");
        IData memUserInfo = getData("MEM_USER_INFO");
        IData memAcctInfo = getData("MEM_ACCT_INFO");

        setMemUserInfo(memUserInfo);
        setMemCustInfo(memCustInfo);
        setMemAcctInfo(memAcctInfo);

    }

    /**
     * 资源初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initialRes(IRequestCycle cycle) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);

        // 资源类型和名称
        IDataset resList = new DatasetList();
        resList = CSViewCall.call(this, "CS.ProductInfoQrySVC.getResTypeByMainProduct", param);
 
        if (IDataUtil.isNotEmpty(resList))
        {
            // 非专线
            setTag("0");
            setResList(resList);
        }

    }

    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setCond(IData param);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setDynParam(IData param);

    public abstract void setExtendElements(IDataset extendElements);

    public abstract void setGroupId(String groupId);

    public abstract void setInfo(IData info);

    public abstract void setMemAcctInfo(IData mAcctInfo);

    public abstract void setMemCustInfo(IData mCustInfo);

    public abstract void setMemUserInfo(IData mUserInfo);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setResList(IDataset reslist);

    public abstract void setResNumber(String resNumber);

    public abstract void setTag(String tag);

    public abstract void setUserInfo(IData userInfo);

}
