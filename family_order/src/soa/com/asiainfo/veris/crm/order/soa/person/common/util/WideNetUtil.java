package com.asiainfo.veris.crm.order.soa.person.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.PackagePrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;

/**
 * 融合宽带工具类
 * 
 * @author zyc
 * 
 */
public class WideNetUtil {

	/**
	 * 获取转出现金类存折编码
	 * 1627,TOP_SET_BOX_NOTES 该参数为已有参数，海南现场增加的，截至到20160528还未上线
	 * @return
	 * @throws Exception
	 */
	public static String getOutDepositCode()throws Exception {
		String outDepositCode = "0";
		
		IDataset commparaInfos = CommparaInfoQry.getCommNetInfo("CSM","1627","TOP_SET_BOX_NOTES");
		if(IDataUtil.isNotEmpty(commparaInfos))
		{
			outDepositCode = "";
			for(int i = 0 ; i < commparaInfos.size() ; i ++)
			{
				String paraCode1 = commparaInfos.getData(i).getString("PARA_CODE1","");
				if(paraCode1 != null && !"".equals(paraCode1))
				{
					if(StringUtils.isNotBlank(outDepositCode))	
					{
						outDepositCode = outDepositCode + "|" + paraCode1;
					}
					else
					{
						outDepositCode = paraCode1;
					}
				}
			}
		}

		return outDepositCode;
	}
	
	/**
	 * 根据手机号码，获取用户现金类账户余额
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static String qryBalanceDepositBySn(String serialNumber) throws Exception
	{
		int allBalanceFee = 0;
		IDataset allAccountDeposit = AcctCall.queryAccountDepositBySn(serialNumber);
		
		String depositCodes = getOutDepositCode();
		
		String depositCode [] = depositCodes.split("\\|");
		
		for(int i = 0 ; i < allAccountDeposit.size() ; i++ )
		{
			IData accountDeposit = allAccountDeposit.getData(i);
			String tempDepositCode = accountDeposit.getString("DEPOSIT_CODE", "");
            String depositBalance = accountDeposit.getString("DEPOSIT_BALANCE", "0");
            
			for(int j = 0 ; j < depositCode.length ; j++)
			{
                 if(tempDepositCode.equals(depositCode[j]))
                 {
                	 allBalanceFee += Integer.parseInt(depositBalance)  ;
                 }
			}
		}
		
		return String.valueOf(allBalanceFee);
	}
	
	
	/**
     * 查询营销包下面所有默认必选元素费用
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getWideNetSaleAtiveTradeFee(String productId, String packageId) throws Exception
    {
        IDataset tradeFeeDataset = new DatasetList();
        
        //查询下面所有
        IDataset forceAndDefaultElements = UPackageElementInfoQry.queryForceDefaultElementByPackageId(packageId, "1", "1");
        
        if (IDataUtil.isEmpty(forceAndDefaultElements))
        {
            return null;
        }
        
        for (int i = 0; i < forceAndDefaultElements.size(); i++)
        {
            IData forceAndDefaultElement = forceAndDefaultElements.getData(i);
            //查询营销活动费用配置
            IDataset businessFee = ProductFeeInfoQry.getSaleActiveFee("240", BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, forceAndDefaultElement.getString("ELEMENT_TYPE_CODE"), forceAndDefaultElement.getString("ELEMENT_ID"), productId);
            
            if (IDataUtil.isNotEmpty(businessFee))
            {
                tradeFeeDataset.addAll(businessFee);
            }
        }
        
        return tradeFeeDataset;
    }
	
	/**
	 * 宽带营销活动权限校验
	 * @param staffId
	 * @param saleActiveList
	 * @return
	 * @throws Exception
	 * @author yuyj3
	 */
	public static IDataset filterWideSaleActiveListByPriv(String staffId, IDataset saleActiveList) throws Exception
	{
	    IDataset resultList = new DatasetList(); 
        
        if (IDataUtil.isNotEmpty(saleActiveList))
        {
            IData saleActive = null;
            
            IDataset checkPrivSaleActiveList = new DatasetList(); 
            
            for (int i = 0; i < saleActiveList.size(); i++)
            {
                saleActive = saleActiveList.getData(i);
                
                //标记该营销包是否需要做权限校验，局方要求只有需要做权限控制的营销活动才校验，其他不校验
                String isCheckPriv = saleActive.getString("PARA_CODE19","N");
                
                saleActive.put("PACKAGE_ID", saleActive.getString("PARA_CODE5"));
                
                //如果需要进行权限校验，局方要求只有需要做权限控制的营销活动才校验，其他不校验
                if ("Y".equals(isCheckPriv))
                {
                    checkPrivSaleActiveList.add(saleActive);
                }
                else
                {
                    resultList.add(saleActive);
                }
            }
            
            PackagePrivUtil.filterPackageListByPriv(staffId , checkPrivSaleActiveList);
            
            resultList.addAll(checkPrivSaleActiveList);
        }
        
        return resultList;
	}
	
	/**
	 * 商务宽带产品过滤
	 * @param staffId
	 * @param saleActiveList
	 * @return
	 * @throws Exception
	 * @author yuyj3
	 */
	public static IDataset filterBusinessProduct(String serialNumber, IDataset productDataset) throws Exception
	{
        
        IDataset resultProducts = new DatasetList();
        
        if (IDataUtil.isNotEmpty(productDataset))
        {
            boolean isBusiness = false;
            
            //没传默认不是商务宽带用户
            if (StringUtils.isNotBlank(serialNumber))
            {
                IData productInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);
                
                if (IDataUtil.isNotEmpty(productInfo))
                {
                    //是否是商务宽带用户
                    if ("7341".equals(productInfo.getString("PRODUCT_ID")))
                    {
                        isBusiness = true;
                    }
                }
            }
          
            //非商务宽带需要过滤掉小薇宽带产品
            if (!isBusiness)
            {
                //商务小微宽带产品ID
                IDataset businessWideProIdDataset = CommparaInfoQry.getCommparaByCodeCode1("CSM", "734", "1", "1");
                
                if (IDataUtil.isNotEmpty(businessWideProIdDataset))
                {
                    String businessWideProId = businessWideProIdDataset.getData(0).getString("PARA_CODE24","");
                    
                    if (StringUtils.isNotBlank(businessWideProId))
                    {
                        IData productData = null;
                        
                        //是否是小微商务宽带产品
                        boolean isY = false;
                        
                        String []businessWideProIdArray = businessWideProId.split("\\|");
                        
                        for (int i = 0; i < productDataset.size(); i++)
                        {
                            isY = false;
                            productData = productDataset.getData(i);
                            
                            for (int j = 0; j < businessWideProIdArray.length; j++)
                            {
                                if (productData.getString("PRODUCT_ID","").equals(businessWideProIdArray[j]))
                                {
                                    isY = true;
                                    
                                    break;
                                }
                            }
                            
                            if (!isY)
                            {
                                resultProducts.add(productData);
                            }
                        }
                    }
                    else
                    {
                        resultProducts = productDataset;
                    }
                }
                else
                {
                    resultProducts = productDataset;
                }
            }
            else
            {
                resultProducts = productDataset;
            }
        }
        
        return resultProducts;
	}
	
	
	/**
     * @Description: 是否存在预约产品
     * @param userId
     * @return
     * @throws Exception
     * @author: yuyj3
     */
    public static boolean isExistsBookingChangeProduct(String userId) throws Exception
    {
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);

        String sysDate = SysDateMgr.getSysTime();

        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();

            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);

                if (userProduct.getString("START_DATE").compareTo(sysDate) >= 0)
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static String getWidenetUserRate(String serialNumber) throws Exception
    {
        String widenetUserRate = "0";
        
        if (!serialNumber.startsWith("KD_"))
        {
            serialNumber = "KD_" + serialNumber;
        }
        
        IDataset userinfo = UserInfoQry.getUserinfo(serialNumber);
        
        if (IDataUtil.isNotEmpty(userinfo))
        {
            String userId = userinfo.getData(0).getString("USER_ID");
            
            IDataset userSvcs  =  UserSvcInfoQry.queryUserSvcByUserIdAllNew(userId);
            
            if (IDataUtil.isNotEmpty(userSvcs))
            {
                for (int i = 0; i < userSvcs.size(); i++)
                {
                    if ("0".equals(userSvcs.getData(i).getString("MAIN_TAG")))
                    {
                        IDataset rate_ds = CommparaInfoQry.getCommpara("CSM", "4000", userSvcs.getData(i).getString("SERVICE_ID"), "0898");
                        
                        if (IDataUtil.isNotEmpty(rate_ds))
                        {
                            widenetUserRate = rate_ds.getData(0).getString("PARA_CODE1","");
                            break;
                        }
                    }
                }
            }
        }
        
        return widenetUserRate;
    }

    /**
     * 宽带附加活动校验
     * <p>Title: filterWideSaleActiveAttrListByPriv</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param staffId
     * @param saleActiveList
     * @return
     * @throws Exception
     * @author XUYT
     * @date 2017-7-13 下午03:39:13
     */
    public static IDataset filterWideSaleActiveAttrListByPriv(String staffId, IDataset saleActiveList) throws Exception
	{
	    IDataset resultList = new DatasetList(); 
        
        if (IDataUtil.isNotEmpty(saleActiveList))
        {
            IData saleActive = null;
            
            IDataset checkPrivSaleActiveList = new DatasetList(); 
            
            for (int i = 0; i < saleActiveList.size(); i++)
            {
                saleActive = saleActiveList.getData(i);
                
                //标记该营销包是否需要做权限校验，局方要求只有需要做权限控制的营销活动才校验，其他不校验
                String isCheckPriv = saleActive.getString("PARA_CODE21","N");
                
                saleActive.put("PACKAGE_ID", saleActive.getString("PARA_CODE15"));
                
                //如果需要进行权限校验，局方要求只有需要做权限控制的营销活动才校验，其他不校验
                if ("Y".equals(isCheckPriv))
                {
                    checkPrivSaleActiveList.add(saleActive);
                }
                else
                {
                    resultList.add(saleActive);
                }
            }
            
            PackagePrivUtil.filterPackageListByPriv(staffId , checkPrivSaleActiveList);
            
            resultList.addAll(checkPrivSaleActiveList);
        }
        
        return resultList;
	}
    
    /**
     * 获取宽带产品对应的速率
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getWidenetProductRate(String productId) throws Exception
    {
    	String widenetProductRate = "0";
    	
    	IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "1", "1");
        
        if (IDataUtil.isNotEmpty(forceElements))
        {
            IDataset rate_ds = null;
            IData forceElement = null;
            
            for (int j = 0; j < forceElements.size(); j++)
            {
                forceElement = forceElements.getData(j);
                
                if ("S".equals(forceElement.getString("OFFER_TYPE")))
                {
                    //根据产品下的服务ID查询宽带速率
                    rate_ds = CommparaInfoQry.getCommpara("CSM", "4000",forceElement.getString("OFFER_CODE") , "0898");
                    
                    if (IDataUtil.isNotEmpty(rate_ds))
                    {
                    	widenetProductRate = rate_ds.getData(0).getString("PARA_CODE1","");
                    	
                        break;
                    }
                }
            }
        }
        
        return widenetProductRate;
    }
    
    /**
     * 判断区分宽带是无手机宽带还是绑定手机宽带
     * @param serialNum
     * @return
     * @throws Exception
     */
    public static IData getWideNetTypeInfo(String serialNum) throws Exception
    {
    	IData resultData = new DataMap();
    	
    	String widenetSerialNum = "";
    	String phoneSerialNum = "";
    	
		if (StringUtils.isBlank(serialNum))
		{
			CSAppException.appError("210008001", "宽带服务号码为空！");
		}
		
		if (serialNum.startsWith("KD_"))
		{
			widenetSerialNum = serialNum;
			phoneSerialNum = serialNum.substring(3);
		}
		else
		{
			widenetSerialNum = "KD_" + serialNum;
			phoneSerialNum = serialNum;
		}
    	
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(widenetSerialNum);
        
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.appError("210008002", "该宽带号码不存在有效的用户信息！");
        }
        
        resultData.put("IS_NOPHONE_WIDENET", "N");
		
        //RSRV_TAG1为N表示无手机宽带用户
		if ("N".equals(userInfo.getString("RSRV_TAG1","")))
		{
			//是否无手机宽带用户
			resultData.put("IS_NOPHONE_WIDENET", "Y");
		}
		else
		{
			IData phoneUserInfo = UcaInfoQry.qryUserInfoBySn(phoneSerialNum);
	        
	        if (IDataUtil.isEmpty(phoneUserInfo))
	        {
	        	CSAppException.appError("210008002", "该手机号码不存在有效的用户信息！");
	        }
	        
	        resultData.put("USER_ID", phoneUserInfo.getString("USER_ID"));
		}
		
		resultData.put("WIDE_USER_ID", userInfo.getString("USER_ID"));
		resultData.put("SERIAL_NUMBER", phoneSerialNum);
		resultData.put("WIDE_SERIAL_NUMBER", widenetSerialNum);
		
    	return resultData;
	}

	//调用能开服务
    public static IData buildAbilityData(IData params) throws Exception
    {
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ");

        IData param1 = new DataMap();
        //查询BizEvn参数获取能开URL地址
        param1.put("PARAM_NAME", "TWOCITY");
        IDataset Abilityurls = Dao.qryBySql(sqlBuild, param1, "cen");

        String Abilityurl = "";
        if (Abilityurls != null && Abilityurls.size() > 0)
        {
            Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
        }
        else
        {
            CSAppException.appError("-1", "crm.ABILITY.UP接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = Abilityurl;
        IData resultData = AbilityEncrypting.callAbilityPlatCommon(apiAddress,params);

        return resultData;
    }
    
    /**
     * 通过能开调用渠道运营平台生产19位编码
     * @param params
     * @return
     * @throws Exception
     */
    public static IData buildChannelData(IData params) throws Exception{
    	StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ");

        IData param1 = new DataMap();
        //查询BizEvn参数获取能开URL地址
        param1.put("PARAM_NAME", "SYNTOCHANNEL");
        IDataset Abilityurls = Dao.qryBySql(sqlBuild, param1, "cen");

        String Abilityurl = "";
        if (Abilityurls != null && Abilityurls.size() > 0)
        {
            Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
        }
        else
        {
            CSAppException.appError("-1", "crm.ABILITY.UP接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = Abilityurl;
        IData resultData = AbilityEncrypting.callAbilityPlatCommon(apiAddress,params);

        return resultData;
    }
    /**
     * @author yuyz
     * @Description 查询无手机宽带号码的属性
     * @Date 17:08 2020/6/12
     * @Param [numbersInfo]
     * @return
     **/
    public static IDataset getWideNumberAttrInfos(IDataset numbersInfo) throws Exception {
        if (IDataUtil.isNotEmpty(numbersInfo)) {
            for (int i = 0; i < numbersInfo.size(); i++) {
                IData numberInfo = numbersInfo.getData(i);
                String serialNumb = numberInfo.getString("SERIAL_NUMBER");
                if (StringUtils.isNotBlank(serialNumb)) {
                    if (serialNumb.startsWith("KD_178") || serialNumb.startsWith("KD_6")) {
                        numberInfo.put("NUMBER_ATTR", "无手机宽带账户");
                    } else if (serialNumb.startsWith("KV_178") || serialNumb.startsWith("178")) {
                        numberInfo.put("NUMBER_ATTR", "无手机宽带虚拟账户");
                    }
                }
            }
        } else {
            return null;
        }
        return numbersInfo;
    }
    
    /**
     * 根据宽带类型获取产品类型 
     * duhj
     * 
     * @param widetype
     * @return
     * @throws Exception
     */
    public static String getWideProductMode(String widetype) throws Exception
    {
        String productmode = "";// --07：移动GPON宽带，09：ADSL宽带产品，11：移动FTTH宽带，16：海南铁通FTTH，17：海南铁通FTTB，13：校园宽带

        if ("1".equals(widetype))
        {
            productmode = "07";
        }
        else if ("2".equals(widetype))
        {
            productmode = "09";
        }
        else if ("3".equals(widetype))
        {
            productmode = "11";
        }
        else if ("5".equals(widetype))
        {
            productmode = "11"; // 移动FTTH与铁通FTTH合并，使用同一套产品
        }
        else if ("6".equals(widetype))
        {
            productmode = "07"; // 移动FTTB与铁通FTTB合并，使用同一套产品
        }
        return productmode;
    }
    
}

