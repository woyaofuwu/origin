
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.createmergewideuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MergeWideUserCreate extends PersonBasePage
{
    /**
     * 初始化方法
     * 
     * @author yuyj3
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData param = new DataMap();
        
        //宽带融合开户业务类型统一用600
        param.put("TRADE_TYPE_CODE", "600");
        
        //预约施工时间只能选择48小时之后
        String minDate = SysDateMgr.getAddHoursDate(SysDateMgr.getSysTime(), 48);//SysDateMgr.addDays(2);
        String maxDate = SysDateMgr.addDays2(30);//SysDateMgr.addDays(30);
        param.put("MAX", maxDate);
        param.put("MIN", minDate);
        
        IDataset idsWideBing = new DatasetList();
        IData idWideBingY = new DataMap();
        idWideBingY.put("DATA_ID", "1");
        idWideBingY.put("DATA_NAME", "是");
        idsWideBing.add(idWideBingY);
        
        IData idWideBingN = new DataMap();
        idWideBingN.put("DATA_ID", "0");
        idWideBingN.put("DATA_NAME", "否");
        idsWideBing.add(idWideBingN);
        
        param.put("WideBing", idsWideBing);
        
        setInfo(param);
    }
    
    
    public void checkSerialNumber(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideUserCreateSVC.checkSerialNumber", data);
        IDataset acctInfos = dataset.getData(0).getDataset("ALL_ACCT");
        setAllAcct(acctInfos);
        setAjax(dataset);
    }

    /**
     * 获取产品费用
     * 
     * @author yuyj3
     * @param clcle
     * @throws Exception
     */
    public void getProductFeeInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        data.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        data.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        data.put("PACKAGE_ID", "");
        data.put("ELEMENT_ID", data.getString("ELEMENT_ID"));
        data.put("ELEMENT_TYPE_CODE", "P");
        data.put("TRADE_FEE_TYPE", "3");
        data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "CS.ProductFeeInfoQrySVC.getProductFeeInfo", data);
        setAjax(dataset);
    }

    
    /**
     * 用户鉴权后初始化方法
     * @param clcle
     * @throws Exception
     * @author yuyj3
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData resultData = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.loadChildInfo", data);
        
        if (IDataUtil.isNotEmpty(resultData))
        {
            IDataset topSetBoxProducts  = resultData.getDataset("TOP_SET_BOX_PRODUCTS");
            
            IDataset wideModemStyle  = resultData.getDataset("WIDE_MODEM_STYLE");
            
            IDataset widenetPayModeList = resultData.getDataset("WIDENET_PAY_MODE_LIST");

            IDataset mergeWideUserStyleList = resultData.getDataset("MERGE_WIDE_LIST");

            setTopSetBoxProducts(topSetBoxProducts);
            
            setWideModemStyleList(wideModemStyle);
            
            setWidenetPayModeList(widenetPayModeList);

            setMergeWideUserStyleList(mergeWideUserStyleList);

            setAjax(resultData);
            
            //预约施工时间只能选择48小时之后
            IData param = new DataMap();
            String minDate = SysDateMgr.getAddHoursDate(SysDateMgr.getSysTime(), 48);//SysDateMgr.addDays(2);
            param.put("MIN", minDate);
            setInfo(param);
        }
        
        //初始化IMS固话产品
        IDataset imsProductTypeList = CSViewCall.call(this, "SS.IMSLandLineSVC.onInitTrade", data);
        this.setImsProductTypeList(imsProductTypeList);
    }
    
    /**
     * 查询互联网电视机顶盒基础优惠包（0）和可选优惠包（2）
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void queryTopSetBoxDiscntPackagesByPID(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData retData = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.queryTopSetBoxDiscntPackagesByPID", data);
        
        IDataset basePackages = retData.getDataset("B_P");
        IDataset optionPackages = retData.getDataset("O_P");
        IDataset platSvcPackages = retData.getDataset("P_P");
        IDataset topSetBoxSaleActiveList = retData.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST");
        //BUS201907310012关于开发家庭终端调测费的需求
        IDataset topSetBoxSaleActiveList2 = retData.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST2");
        this.setTopSetBoxSaleActiveList2(topSetBoxSaleActiveList2);
        //BUS201907310012关于开发家庭终端调测费的需求
        this.setBasePackages(basePackages);
        this.setOptionPackages(optionPackages);
        this.setPlatSvcPackages(platSvcPackages);
        this.setTopSetBoxSaleActiveList(topSetBoxSaleActiveList);
        setAjax(retData);
    }
    
    
    /**
     * 根据产品类型获得产品信息
     * @param clcle
     * @throws Exception
     * @author yuyj3
     */
    public void changeWideProductType(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        data.put("ROUTE_EPARCHY_CODE", "0898");
        
        IDataset dataset = CSViewCall.call(this, "SS.MergeWideUserCreateSVC.getWidenetProductInfoByWideType", data);
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), dataset);
        setProductList(dataset);
        setInfo(data);
        setAjax(dataset);
    }
    
    /**
     * 根据产品信息获得与之关联的营销活动
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void getSaleActiveByProductId(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IDataset dataset = CSViewCall.call(this, "SS.MergeWideUserCreateSVC.getSaleActiveByProductId", data);
        
        setSaleActiveList(dataset);
        
        //获取可选包
        data.put("TOPSET_TYPE", "0");
        data.put("PRODUCT_ID", data.getString("productId"));
        IDataset platSvcPackages = CSViewCall.call(this, "SS.InternetTvOpenIntfSVC.queryPlatSvc", data);
        if(IDataUtil.isNotEmpty(platSvcPackages)){
        	 this.setPlatSvcPackages2(platSvcPackages.first().getDataset("PLATSVC_INFO_LIST"));
        }
        setAjax(dataset);
    }
    
    //add by zhangxing3 for BUS201907310012关于开发家庭终端调测费的需求
    public void getSaleActiveByParaCode1(IRequestCycle cycle) throws Exception
    {
        IData data = getData();       
        data.put("PARAM_CODE", "600");
        data.put("PARA_CODE1", "FTTH");
        IDataset dataset = CSViewCall.call(this, "SS.MergeWideUserCreateSVC.getSaleActiveByParaCode1", data);
     // add by chenyw7 for REQ201912030008宽带客户FTTH迁移营销活动需求2—BOSS侧
        if(dataset != null && dataset.size() > 0){
        	for (int i = 0; i < dataset.size(); i++) {
        		IData iData = dataset.getData(i);
        		String paraCode5 = iData.getString("PARA_CODE5","");
        		if(paraCode5.equals("79082840")){
        			dataset.remove(i);
        			break;
        		}
			}
        }
     // add by chenyw7 for REQ201912030008宽带客户FTTH迁移营销活动需求2—BOSS侧
        setSaleActiveList2(dataset);      
        setAjax(dataset);
    }
    //add by zhangxing3 for BUS201907310012关于开发家庭终端调测费的需求 
    
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        String routeId = data.getString("EPARCHY_CODE");
        
        // 客服工号，HAIN, 则默认到0898
        if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
        {
            data.put("EPARCHY_CODE", "0898");
        }
        
        // 商务宽带开户特殊处理，号码需要在原号码后加0000，并递增
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
                
        IDataset wideSNdataset = CSViewCall.call(this, "SS.WideUserCreateSVC.getWideSerialNumber", data);
        String wideSerialNumber = wideSNdataset.getData(0).getString("WIDE_SERIAL_NUMBER");
        data.put("WIDE_SERIAL_NUMBER", wideSerialNumber);
        
        IDataset dataset = CSViewCall.call(this, "SS.MergeWideUserCreateRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
    
    /**
     * 获得光猫费用，并校验现金余额是否足够
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void checkModemDeposit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkModemDeposit", data);
       
        this.setAjax(result);
    }
    
    /**
     * 魔百和营销活动依赖校验
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void checkSaleActiveDependence(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkSaleActiveDependence", data);
       
        this.setAjax(result);
    }
    
    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkTTWideNet(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkTTWideNet", data);
       
        this.setAjax(result);
    }
    
    /**
     * 选择了宽带包年优惠则不能订购宽带营销活动
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void checkSaleActive(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        //如果选择了优惠则需要判断这些优惠是否是包年优惠，如果是包年优惠则不能选择宽带营销活动
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkSaleActive", data);
        IDataset dataset = new DatasetList();
        	
        if("1".equals(result.getString("FLAG", "")))
        {
	        dataset = CSViewCall.call(this, "SS.MergeWideUserCreateSVC.getSaleActiveByPackageId", data);
	        setSaleActiveListAttr(dataset);
        }else
        {
        	setSaleActiveListAttr(dataset);
        }
        this.setAjax(result);
    }
    
    /**
     * 选择了宽带附加营销活动校验
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void checkSaleActiveAttr(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkSaleActiveAttr", data);
        this.setAjax(result);
    }
    
    
    /**
     * 营销活动费用校验
     * @param cycle
     * @throws Exception
     * @author zhangyc5
     */
    public void queryCheckSaleActiveFee(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.queryCheckSaleActiveFee", data);
       
        this.setAjax(result);
    }
    
    /**
     * 营销活动费用校验
     * @param cycle
     * @throws Exception
     * @author zhangyc5
     */
    public void checkSelectedDiscnts(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkSelectedDiscnts", data);
       
        this.setAjax(result);
    }
    
    

    /**
     * 提交前费用校验
     * @param cycle
     * @throws Exception
     * @author zhangyc5
     */
    public void checkFeeBeforeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkFeeBeforeSubmit", data);
       
        this.setAjax(result);
    }
    
    
    /**
     * 根据IMS固话产品类型获得产品信息
     * @param clcle
     * @throws Exception
     * @author yuyj3
     */
    public void getIMSProductByType(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        data.put("ROUTE_EPARCHY_CODE", "0898");
        
        if (StringUtils.isBlank("PRODUCT_TYPE_CODE"))
        {
        	setImsProductList(new DatasetList());
        }
        else
        {
        	IDataset dataset = CSViewCall.call(this, "SS.MergeWideUserCreateSVC.getIMSProductByType", data);
            setImsProductList(dataset);
        }
    }
    
    /**
     * 功能说明：校验固话号 
     */
    public void checkFixPhoneNum(IRequestCycle cycle) throws Exception
    {
    	IData userData = getData(); 
        String fixNum = userData.getString("FIX_NUMBER", ""); 
        userData.put("FIX_NUMBER", "0898"+fixNum);
        IDataset results = CSViewCall.call(this, "SS.IMSLandLineSVC.checkFixPhoneNum", userData);
        setAjax(results.first());
    }
    
    /**
     * 获取IMS固话营销活动
     */
    public void getImsSaleActive(IRequestCycle cycle) throws Exception
    {
    	IData data = getData(); 
        
    	String productId = data.getString("IMS_PRODUCT_ID");

        if (StringUtils.isNotBlank(productId))
        {
        	data.put("PRODUCT_ID", data.getString("IMS_PRODUCT_ID"));
        	data.put("ROUTE_EPARCHY_CODE", "0898");
            IDataset results = CSViewCall.call(this, "SS.IMSLandLineSVC.queryDiscntPackagesByPID", data);
            
            IData retData = results.first();
            
            //IMS固话营销活动，服务侧命名有误
            IDataset imsSaleActiveList = retData.getDataset("TOP_SET_BOX_SALE_ACTIVE_LIST");
            this.setImsSaleActiveList(imsSaleActiveList);
            setAjax(imsSaleActiveList);
        }
        else
        {
        	this.setImsSaleActiveList(new DatasetList());
        	setAjax(new DatasetList());
        }
    }
    
    /**
     * 校验IMS固话营销活动校验
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void checkImsSaleActive(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.IMSLandLineSVC.checkSaleActive", data);
        this.setAjax(result);
    }
    
    
    /**
     * 获取和目营销活动
     */
    public void getHeMuSaleActive(IRequestCycle cycle) throws Exception
    {
    	IData data = getData(); 
        
    	data.put("ROUTE_EPARCHY_CODE", "0898");
        IDataset results = CSViewCall.call(this, "SS.MergeWideUserCreateSVC.getHeMuSaleActive", data);
        setHeMuSaleActiveList(results);
        setAjax(results);
    }
    
    /**
     * 校验和目营销活动校验
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void checkHeMuSaleActive(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkHeMuSaleActive", data);
        this.setAjax(result);
    }
    
    /**
     * 选择了宽带附加营销活动校验
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void checkHeMuTerminal(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkHeMuTerminal", data);
        this.setAjax(result);
    }
    
    
    /**
     * 获取魔百和押金费用
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public void getTopSetBoxDeposit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.getTopSetBoxDeposit", data);
        this.setAjax(result);
    }
    
    /**
     * 初始化魔百和产品列表
     * @param cycle
     * @throws Exception
     */
    public void initTopSetBox(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.getTopSetBoxProducts", data);
        IDataset topSetBoxProducts  = result.getDataset("TOP_SET_BOX_PRODUCTS");
        setTopSetBoxProducts(topSetBoxProducts);
    }
  
    /**
     *  校验是否高价值小区
     * @author lizj
     * @param cycle
     * @throws Exception
     */
    public void checkHightActive(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateIntfSVC.checkHightUserInfo", data);
        this.setAjax(result);
    }
    /**
     * 校验赠送光猫FTTH
     * @author wangck3
     * @param cycle
     * @throws Exception
     */
    public void checkModemFTTH(IRequestCycle cycle)throws Exception{
    	IData data = getData();
    	IData retResult = new DataMap();
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkModemFTTH", data);
        if(IDataUtil.isNotEmpty(result)){
        	retResult.putAll(result);
        	retResult.put("status", "1");
        }else{
        	retResult.put("status", "0");
        }
        this.setAjax(retResult);
    }
    /**
     * 人像信息比对员工信息
     *
     * @author dengyi
     * @param clcle
     * @throws Exception
     */
    public void isCmpPic(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();

        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isCmpPic", param);
        setAjax(dataset.getData(0));
    }
    
    /**
     * 人像信息比对
     * @param clcle
     * @throws Exception
     * @date 20180101
     */
    public void cmpPicInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.cmpPicInfo", param);
        setAjax(dataset.getData(0));
    }

    /**
     * 校验宽带开户或家庭产品开户的联系人电话
     * @author yanghb6
     * @param cycle
     * @throws Exception
     */
    public void checkContactPhone(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateSVC.checkContactPhone", data);
        this.setAjax(result);
    }
    /**
     *  REQ202003180001 “共同战疫宽带助力”活动开发需求
     *  共同战疫宽带助力活动校验
     * @author liangdg3
     * @param cycle
     * @throws Exception
     */
    public void checkTimeLimitedSaleActive(IRequestCycle cycle) throws Exception{
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.MergeWideUserCreateIntfSVC.checkTimeLimitedSaleActive", data);
        this.setAjax(result);
    }
    public abstract void setAllAcct(IDataset datas);

    public abstract void setInfo(IData info);
    
    public abstract void setResInfo(IData info);

    public abstract void setProductList(IDataset datas);
    
    public abstract void setImsProductTypeList(IDataset datas);
    
    public abstract void setImsProductList(IDataset datas);
    
    public abstract void setTopSetBoxProducts(IDataset datas);
    
    public abstract void setWideModemStyleList(IDataset datas);
    
    public abstract void setBasePackages(IDataset datas);
    
    public abstract void setOptionPackages(IDataset datas);
    
    public abstract void setTopSetBoxSaleActiveList(IDataset datas);
    
    public abstract void setSaleActiveList(IDataset datas);
    
    public abstract void setSaleActiveListAttr(IDataset datas);
    
    public abstract void setWidenetPayModeList(IDataset datas);
    
    public abstract void setHeMuSaleActiveList(IDataset heMuSaleActiveList);
    
    public abstract void setImsSaleActiveList(IDataset imsSaleActiveList);

	public abstract void setMergeWideUserStyleList(IDataset mergeWideUserStyleList);

    //add by zhangxing3 for BUS201907310012关于开发家庭终端调测费的需求  
    public abstract void setSaleActiveList2(IDataset datas);    
    public abstract void setTopSetBoxSaleActiveList2(IDataset datas);
    //add by zhangxing3 for BUS201907310012关于开发家庭终端调测费的需求  
    
    public abstract void setPlatSvcPackages(IDataset platSvcPackages);
    public abstract void setPlatSvcPackages2(IDataset platSvcPackages);
}
