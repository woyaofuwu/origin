
package com.asiainfo.veris.crm.order.web.person.speservice;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NewSvcRecomdPara extends PersonBasePage
{
    /**
     * 根据品牌 获取品牌下的有效产品
     * 
     * @data 2013-9-25
     * @param cycle
     * @throws Exception
     */
    public void getProductByBrand(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData("cond", true);
        IDataset productInfo = new DatasetList();

        IData inparam = new DataMap();
        inparam.put("BRAND_CODE", pagedata.getString("BRAND_CODE"));
        inparam.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        inparam.put("PRODUCT_MODE", "00");// 产品的模式：00-个人基本产品

        productInfo = CSViewCall.call(this, "SS.NewSvcRecomdParaSVC.getProductByBrand", inparam);  //测试屏蔽duhj
       
        setProductLists(productInfo);

    }

    /**
     * 根据品牌 获取品牌下的有效产品
     * 
     * @data 2013-9-25
     * @param cycle
     * @throws Exception
     */
    public void getSVCAndDisByProduct(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData("cond", true);
        IDataset disList = new DatasetList();
        IDataset serviceList = new DatasetList();

        boolean flag = false;

        flag = StaffPrivUtil.isProdPriv(getVisit().getStaffId(), pagedata.getString("PRODUCT_ID"));

        if (!flag)
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_28);
        }

        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", pagedata.getString("PRODUCT_ID"));

        inparam.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
        inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());

        IDataset data = CSViewCall.call(this, "SS.NewSvcRecomdParaSVC.getRecommparaDiscntAndService", inparam);
        IData temp = data.getData(0);
        disList=temp.getDataset("DISCNTCODES");
        serviceList=temp.getDataset("SERVICES");

       // disList = CSViewCall.call(this, "SS.NewSvcRecomdParaSVC.getRecommparaDiscnt", inparam);

      //  serviceList = CSViewCall.call(this, "SS.NewSvcRecomdParaSVC.getRecommparaService", inparam);

        setServiceLists(serviceList);
        setDiscntLists(disList);

    }

    /**
     * 主要用来获取页面初始化时的用户品牌信息
     * 
     * @data 2013-9-25
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();
        IDataset brackInfo = new DatasetList();

        brackInfo = CSViewCall.call(this, "SS.NewSvcRecomdParaSVC.getAllBrandInfo", pagedata);

        setBrandLists(brackInfo);

    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset dataset = CSViewCall.call(this, "SS.NewSvcRecomdParaSVC.updateRecommData", data);
       // setAjax(dataset);
        setAjax("msg", dataset.getData(0).getString("issuccess"));//修改以前版本遗留的问题，操作成功后页面卡着不动  duhj

    }

    public abstract void setBrandLists(IDataset brandList);// 设置产品信息

    public abstract void setDiscntLists(IDataset discntList);// 设置产品下的优惠信息

    public abstract void setEditInfo(IData editInfo);

    public abstract void setForeachs(IDataset ForeachList);

    public abstract void setHistoryConsumeList(IDataset historyConsumeList);

    public abstract void setInfo(IData info);

    public abstract void setInfo1(IData info1);

    public abstract void setProductLists(IDataset productList);// 设置产品信息

    public abstract void setRecomdLists(IDataset productList);

    public abstract void setServiceLists(IDataset serviceList);// 设置产品下的服务信息

}
