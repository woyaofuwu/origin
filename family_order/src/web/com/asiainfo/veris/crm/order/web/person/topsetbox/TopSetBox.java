
package com.asiainfo.veris.crm.order.web.person.topsetbox;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TopSetBox.java
 * @Description: 机顶盒销售、换机
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-7-30 下午4:59:42 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-30 yxd v1.0.0 修改原因
 */
public abstract class TopSetBox extends PersonBasePage
{
    /**
     * @Function: checkTerminal()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午4:58:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public void checkTerminal(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.checkTerminal", data);
        IData retData = dataset.first();
        IDataset products = retData.getDataset("PRODUCT_INFO_SET");
        
        String isHasSaleActive=data.getString("IS_HAS_SALE_ACTIVE","");
        if(isHasSaleActive.equals("0")){
        	//获取机顶盒的押金
            IData param=new DataMap();
            IDataset topsetboxFee=CSViewCall.call(this, "SS.TopSetBoxSVC.obtainTopsetboxForegift", param);
            retData.put("TOPSETBOX_FOREGIFT", topsetboxFee.first().getString("TOP_SETBOX_FEE",""));
        }else{
        	retData.put("TOPSETBOX_FOREGIFT", "0");
        }
        
        this.setResInfo(retData);
        this.setProducts(products);
        this.setAjax(retData);
    }

    /**
     * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put("INTERNET_TV_SOURCE", "OPEN_TOPSETBOX");		//用来标记是做开户
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * @Function: queryDiscntPackagesByPID()
     * @Description: 查询互联网电视机顶盒基础优惠包（0）和可选优惠包（2）
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-4 下午8:04:33 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-4 yxd v1.0.0 修改原因
     */
    public void queryDiscntPackagesByPID(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.queryDiscntPackagesByPID", data);
        IData retData = dataset.first();
        IDataset basePackages = retData.getDataset("B_P");
        IDataset optionPackages = retData.getDataset("O_P");
        this.setBasePackages(basePackages);
        this.setOptionPackages(optionPackages);
    }

    public abstract void setBasePackages(IDataset basePackages);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setOptionPackages(IDataset optionPackages);

    /**
     * @Function: setPageInfo()
     * @Description: 设置页面信息
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午4:58:18 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public void setPageInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        
        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.checkUserForOpenInternetTV", data);
//        IDataset dataset = CSViewCall.call(this, "SS.TopSetBoxSVC.checkUserInfo", data);
        IData retData = dataset.first();
        IData resInfo = retData.getData("RES_INFO_MAP");
        IDataset products = retData.getDataset("PRODUCT_INFO_SET");
        this.setCustInfo(custInfo);
        this.setUserInfo(userInfo);
        this.setUserOldInfo(retData);
        this.setResInfo(resInfo);
        this.setProducts(products);
    }

    public abstract void setProducts(IDataset products);

    public abstract void setResInfo(IData resInfo);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setUserOldInfo(IData userOldInfo);

}
