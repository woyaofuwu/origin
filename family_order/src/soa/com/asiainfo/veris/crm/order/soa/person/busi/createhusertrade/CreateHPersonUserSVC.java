package com.asiainfo.veris.crm.order.soa.person.busi.createhusertrade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.Des;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createhusertrade.order.CreateHPersonUserRegSVC;

public class CreateHPersonUserSVC extends CSBizService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 和校园异网号码开户校验
	 * @param input
	 * @return
	 * @throws Exception
	 * @date 2018-1-26 16:39:51
	 */
	public IDataset checkHSerialNumber(IData input) throws Exception {
		String serialNubmer = input.getString("SERIAL_NUMBER");
		String hSerialNumber = "H" + serialNubmer;
		
		CreateHPersonUserBean bean = BeanManager.createBean(CreateHPersonUserBean.class);

		IDataset snInfos = bean.checkHSerialNumber(input);
		
		if (IDataUtil.isEmpty(snInfos))
		{
			//移动号码的话，再判断是不是异网号码
			IDataset npSerial = bean.checkNpSerialNumber(input);
			if(IDataUtil.isNotEmpty(npSerial)){
				IData catalog = npSerial.getData(0);
				String userTagSet = catalog.getString("USER_TAG_SET");//userTagSet=4是已携出号码，可以办理和校园
				if(StringUtils.isNotEmpty(userTagSet) && userTagSet.equals("4")){
				}else{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "移动号码无法办理此业务！"); // [%s]
				}
			}else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "移动号码无法办理此业务！");
			}
		}
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(hSerialNumber);
        
        if (IDataUtil.isNotEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1153, serialNubmer); // 号码[%s]已经开户，请重新输入！
        }
        
		IDataset unFinishTrades = TradeInfoQry.getMainTradeBySn(hSerialNumber, BizRoute.getTradeEparchyCode());
        
        if (IDataUtil.isNotEmpty(unFinishTrades))
        {
            for (int i = 0, count = unFinishTrades.size(); i < count; i++)
            {
            	IData unFinishTrade = unFinishTrades.getData(i);
	            String tradeId = unFinishTrade.getString("TRADE_ID");
	            String acceptDate = unFinishTrade.getString("ACCEPT_DATE");
	            // 得到定单类型
	            String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(unFinishTrade.getString("TRADE_TYPE_CODE"));
	            CSAppException.apperr(CrmCommException.CRM_COMM_982, tradeTypeName, tradeId, acceptDate); // 用户有未完工的订单[%s][%s][%s]请稍后！
            }
        }
        
        String tradeTypeCode = "7510";
        // 0- 业务费用, 根据业务类型编码、产品标识、大客户等级标识、地市编码获取对应业务的通用费用，进入页面需要获取的费用
        String tradeFeeType = "0";
        IDataset results = ProductFeeInfoQry.getTradeFee(tradeTypeCode,tradeFeeType, BizRoute.getTradeEparchyCode());
        
        return results;
	}
	
	/**
	 * 获取产品类型
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset getProductTypeList (IData input) throws Exception {
		String parentPtypeCode = "0000";
		IDataset productTypeList = UProductInfoQry.getProductsType(parentPtypeCode, this.getPagination());
        IData productTypeData = getParentChildProductTypeList(productTypeList);
        IDataset parentProductTypeList = productTypeData.getDataset("PARENT_PRODUCT_TYPE_LIST");
        String strBindDefaultTag = "";
        String strBindBrand = "";
        // 绑定默认标记
        if (!StringUtils.isBlank(strBindDefaultTag))
        {
            filterProductTypeListByDefaultTag(parentProductTypeList, strBindDefaultTag);
        }
        // 绑定 品牌
        if (!StringUtils.isBlank(strBindBrand))
        {
            filterProductTypeListByBrandCode(parentProductTypeList, strBindBrand);
        }
        productTypeList = parentProductTypeList;
        
        return productTypeList;
	}
	
    /**
     * 获取父子二级产品类型列表
     * 
     * @param productTypeList
     * @return
     * @throws Exception
     */
    private static IData getParentChildProductTypeList(IDataset productTypeList) throws Exception
    {
        IData productTypeData = null;
        IData returnData = new DataMap();
        String parentPtypeCode = "";
        IDataset parentProductTypeList = new DatasetList();
        IDataset childProductTypeList = new DatasetList();
        for (int i = 0; i < productTypeList.size(); i++)
        {
            productTypeData = productTypeList.getData(i);
            parentPtypeCode = productTypeData.getString("PARENT_PTYPE_CODE");
            if ("0000".equals(parentPtypeCode))
            {
                parentProductTypeList.add(productTypeData);
            }
            else
            {
                childProductTypeList.add(productTypeData);
            }
        }
        returnData.put("PARENT_PRODUCT_TYPE_LIST", parentProductTypeList);
        returnData.put("CHILD_PRODUCT_TYPE_LIST", childProductTypeList);
        return returnData;
    }

    /**
     * 根据产品类型默认标记过滤产品类型
     * 
     * @param productTypeList
     * @param strDefaultTag
     * @return
     * @throws Exception
     */
    private static IDataset filterProductTypeListByDefaultTag(IDataset productTypeList, String strDefaultTag) throws Exception
    {
        IData productTypeData = null;
        String defaultTag = "";// 默认标记
        for (int i = 0; i < productTypeList.size(); i++)
        {
            productTypeData = productTypeList.getData(i);
            defaultTag = productTypeData.getString("DEFAULT_TAG");
            if (strDefaultTag.indexOf(defaultTag) < 0)
            {
                productTypeList.remove(i);
                i--;
            }
        }
        return productTypeList;
    }
    
    /**
     * 根据品牌过滤产品类型
     * 
     * @param productTypeList
     * @param strBrandCode
     * @return
     * @throws Exception
     */
    private static IDataset filterProductTypeListByBrandCode(IDataset productTypeList, String strBrandCode) throws Exception
    {
        // 通过strBrandCode查询所有对应的产品类型
        IDataset dataset = ProductTypeInfoQry.getProductTypeByBrand(strBrandCode);
        String strProductTypes = "";
        for (int i = 0; i < dataset.size(); i++)
        {
            strProductTypes = strProductTypes + "," + dataset.getData(i).getString("PRODUCT_TYPE_CODE");
        }
        IData productTypeData = null;
        String brandCode = "";
        for (int i = 0; i < productTypeList.size(); i++)
        {
            productTypeData = productTypeList.getData(i);
            brandCode = productTypeData.getString("PRODUCT_TYPE_CODE");
            if (strProductTypes.indexOf(brandCode) < 0)
            {
                productTypeList.remove(i);
                i--;
            }
        }
        return productTypeList;
    }
    
    /**
     * 根据产品类型获取产品信息
     * @param input
     * @return
     * @throws Exception
     * @author zhaohj3
     */
    public IData getProductInfoByProductType(IData input) throws Exception
    {
        IData resultData = new DataMap();
        
        String productType = input.getString("PRODUCT_TYPE");
        
        String productMode = "";
        
        if ("1".equals(productType))
        {
            productMode = "25"; // 25-和校园异网用户商品
        }
        
        resultData.put("PRODUCT_LIST", ProductInfoQry.getHProductInfo(productMode, CSBizBean.getTradeEparchyCode()));
        
        return resultData;
    }
    
    /**
     * 新增和教育异网号码开户接口(对外提供接口)
     * @param input
     * @return
     * @throws Exception
     * @author huangmx5
     */
    public IDataset openHUser(IData input) throws Exception
    {
        
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "PSPT_TYPE_CODE");
		IDataUtil.chkParam(input, "PSPT_ID");
		IDataUtil.chkParam(input, "CUST_NAME");
		IDataUtil.chkParam(input, "PSPT_ADDR");
		IDataUtil.chkParam(input, "BIRTHDAY");
		IDataUtil.chkParam(input, "PHONE");
		IDataUtil.chkParam(input, "PAY_NAME");
		IDataUtil.chkParam(input, "PAY_MODE_CODE");
		IDataUtil.chkParam(input, "ACCT_DAY");
		IDataUtil.chkParam(input, "USER_PASSWD");
		IDataUtil.chkParam(input, "BRAND");
		IDataUtil.chkParam(input, "PRODUCT_TYPE_CODE");
		IDataUtil.chkParam(input, "PRODUCT_ID");
		
		IDataUtil.chkParam(input, "X_TRADE_PAYMONEY");
		IDataUtil.chkParam(input, "X_TRADE_FEESUB");
		
		IDataset elements = new DatasetList();
		IData data = new DataMap();
		//和校园构建请求数据没有根据product_id获取优惠和服务，这里封装一下
		IDataset svcElems = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, input.getString("PRODUCT_ID"), BofConst.ELEMENT_TYPE_CODE_SVC);
		if (svcElems.isEmpty())
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_192, input.getString("PRODUCT_ID"));
        }
		for (int i = 0; i < svcElems.size(); i++)
        {
            IData elem = svcElems.getData(i);
            String serviceId = elem.getString("SERVICE_ID");
            String offerType = elem.getString("ELEMENT_TYPE_CODE");
            data.put("ELEMENT_ID", serviceId);
            data.put("ELEMENT_TYPE_CODE", offerType);
            data.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            data.put("PRODUCT_TYPE_CODE", input.getString("PRODUCT_TYPE_CODE"));
            //没有这个字段会报错
            data.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
            elements.add(data);
            input.put("SELECTED_ELEMENTS", elements);
            
        }
		
		IDataset tradePaymoney = new DatasetList(input.getString("X_TRADE_PAYMONEY"));
		IDataset tradeFeesub = new DatasetList(input.getString("X_TRADE_FEESUB"));
		input.put("X_TRADE_PAYMONEY", tradePaymoney);
		input.put("X_TRADE_FEESUB", tradeFeesub);
        //校验密码必须为6位数,并加密
        String passWd = input.getString("USER_PASSWD").trim();
        if(passWd.length() != 6){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"密码长度为6");
        }
        if(StringUtils.isNotBlank(passWd) && !"".equals(passWd)){
        	String userPasswd = getPassWd(passWd);
        	input.put("USER_PASSWD", userPasswd+"xxyy");
        }
       
        CreateHPersonUserRegSVC createHPersonUserRegSVC = new CreateHPersonUserRegSVC();
        IDataset datas = createHPersonUserRegSVC.tradeReg(input);
        
        return datas;
    }
    /**
     * DES加密
     * @param input
     * @return
     * @throws Exception
     * @author huangmx5
     */
    public String getPassWd(String data) throws Exception{
    	
    	String firstKey = "c";
		String secondKey = "x";
		String thirdKey = "y";
    	Des des = new Des();
		String passWd = des.strEnc(data, firstKey, secondKey, thirdKey);
    	
    	return passWd;
    }
    
   
}
