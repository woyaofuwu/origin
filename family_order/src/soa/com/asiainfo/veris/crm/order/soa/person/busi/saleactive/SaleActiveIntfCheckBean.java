package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import java.util.*;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.PackagePrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveCheckBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveBreConst;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

public class SaleActiveIntfCheckBean extends CSBizBean
{
    private static final Logger log = Logger.getLogger(SaleActiveIntfCheckBean.class);

    public IDataset checkActiveByPrdAndPkg(String serialNumber, String campnType, String productId, String xGetMode, String xGetFee, String deviceModelCode, String terminalId, IData input) throws Exception
    {
//        IDataset productPackages = ProductInfoQry.queryProductAllPackages(productId);
        IDataset productPackages = UpcCall.qryOffersByCatalogId(productId);
        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), productPackages);

        boolean isQyyx = SaleActiveUtil.isQyyx(campnType);

        if (IDataUtil.isEmpty(productPackages))
        {
        	CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_48);
        }

        IData param = new DataMap();

        param.putAll(input);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("CAMPN_TYPE", campnType);
        param.put("PRODUCT_ID", productId);
        param.put("DEVICE_MODEL_CODE", deviceModelCode);
        param.put("TERMINAL_ID", terminalId);
        param.put("X_GETFEE", xGetFee);
        param.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);

        IDataset returnDataset = new DatasetList();
        boolean isPkgCheckPass = false;

        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);

        for (int index = 0, size = productPackages.size(); index < size; index++)
        {
            IData packageData = productPackages.getData(index);
            param.put("PACKAGE_ID", packageData.getString("PACKAGE_ID"));

            try
            {
                packageData.put("CAMPN_TYPE", isQyyx ? "1" : "2");

                IDataset checkDataset = CSAppCall.call("SS.SaleActiveRegSVC.check4Intf", param);
                packageData.put("X_CHECK_TAG", "0");
                packageData.put("X_CHECK_INFO", "Check Ok!");
                if ("1".equals(xGetFee))
                {
                    packageData.put("FEE", checkDataset.getData(0).getString("FEE"));
                }
                packageData.put("EXT_DESC", checkDataset.getData(0).getString("EXT_DESC", "")); // 营销包描述
                packageData.put("RSRV_STR1", checkDataset.getData(0).getString("RSRV_STR1", "")); // package_ext的rsrv_str25
                packageData.put("RSRV_STR2", checkDataset.getData(0).getString("RSRV_STR2", "")); // 预存元素费用
                packageData.put("RSRV_STR3", checkDataset.getData(0).getString("RSRV_STR3", "")); // 赠送的信用度
                packageData.put("RSRV_STR4", checkDataset.getData(0).getString("RSRV_STR4", "")); // 终端购机费
                packageData.put("RSRV_STR5", SaleActiveUtil.queryUserCreditClass(uca.getUserId()));
                returnDataset.add(packageData);
                isPkgCheckPass = true;
            }
            catch (Exception e)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("exception>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + e);
                }
                if ("1".equals(xGetMode))
                {
                    String exceptionStr = Utility.parseExceptionMessage(e);
                    String errorArr[] = exceptionStr.split(BaseException.INFO_SPLITE_CHAR);
                    if ("1".equals(xGetFee))
                    {
                    	packageData.put("FEE",       "");
                    }
                    packageData.put("EXT_DESC",  ""); // 营销包描述
                    packageData.put("RSRV_STR1", ""); // package_ext的rsrv_str25
                    packageData.put("RSRV_STR2", ""); // 预存元素费用
                    packageData.put("RSRV_STR3", ""); // 赠送的信用度
                    packageData.put("RSRV_STR4", ""); // 终端购机费
                    packageData.put("RSRV_STR5", "");
                    packageData.put("X_CHECK_TAG",  errorArr[0]);
                    packageData.put("X_CHECK_INFO", errorArr[1]);
                    returnDataset.add(packageData);
                }
            }
        }
        if(!isPkgCheckPass && "1".equals(xGetMode))
        {
        	CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_49, returnDataset.getData(0).getString("X_CHECK_INFO"));
        }

        return returnDataset;
    }
    
    /**
     * 新增接口，暂时只给短厅
     * 结构与checkActiveByPrdAndPkg相同，增加了规则判断与返回 
     * **/
    public IDataset checkActiveByPrdAndPkgForSMS(String serialNumber, String campnType, String productId, String package_id,String xGetMode, String xGetFee, String deviceModelCode, String terminalId, IData input) throws Exception
    {
        IDataset productPackages = new DatasetList();
        if(StringUtils.isNotBlank(package_id)){
//        	productPackages = ProductInfoQry.queryPackageByPID(package_id,productId);
        	productPackages = UpcCall.qryOffersByCatalogIdAndOfferId(productId, package_id, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        }else{
//        	productPackages = ProductInfoQry.queryProductAllPackages(productId);
            productPackages = UpcCall.qryOffersByCatalogId(productId);
        }
        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), productPackages);

        if (IDataUtil.isEmpty(productPackages))
        {
        	CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_48);
        }
        
        
        for(int i=0;i<productPackages.size();i++)
        {
            IData productPackage = productPackages.getData(i);
            
            productPackage.put("PACKAGE_ID", productPackage.getString("OFFER_CODE"));
            productPackage.put("PRODUCT_ID", productId);
            productPackage.put("CAMPN_TYPE", campnType);
            //关于“禧九约消活动”对营销活动外围校验接口改造说明 by wuhao5 预先添加，防止报错
            productPackage.put("ERROR_FLAG", "0");
        }
        
        IData param = new DataMap();
        
        param.putAll(input);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("CAMPN_TYPE", campnType);
        param.put("PRODUCT_ID", productId);
        param.put("DEVICE_MODEL_CODE", deviceModelCode);
        param.put("TERMINAL_ID", terminalId);
        param.put("X_GETFEE", xGetFee);
        param.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
        
        //REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151214
        if(StringUtils.isNotBlank(input.getString("IMEI_CODE","")))
        {
        	param.put("IMEI_CODE", input.getString("IMEI_CODE",""));
        }
        if(StringUtils.isNotBlank(input.getString("GIFT_CODE","")))
        {
        	param.put("GIFT_CODE", input.getString("GIFT_CODE",""));
        }
        //END
        
        if(StringUtils.isNotBlank(input.getString("RES_TYPE_ID","")))
        {
        	param.put("RES_TYPE_ID", input.getString("RES_TYPE_ID",""));
        }
        //关于“禧九约消活动”对营销活动外围校验接口改造说明 by wuhao5 如果CHL_TAG=1 说明是支付宝渠道，不校验包规则
        checkeTROOP(productPackages,serialNumber,input);
        // 关于“套餐升档活动”营销包校验规则  Start
        IDataset commparaInfos6621 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","6621",productId,null);
        if (IDataUtil.isNotEmpty(commparaInfos6621)){
            String paraCode =commparaInfos6621.first().getString("PARAM_CODE","");
            if (paraCode.equals(productId)){
                // 检查目标客户群
                isTroop(productPackages,serialNumber,input,commparaInfos6621);
                // 校验特定的规则
                checkSpecialRule(productPackages,param,serialNumber);
                // 设置标识，不在进行下面的规则校验
                param.put("CHL_TAG","1");
            }
        }// 关于“套餐升档活动”营销包校验规则  End
        if(!"1".equals(param.getString("CHL_TAG",""))) {
            checkPackages(productPackages,param,serialNumber);
        }

        return productPackages;
    }

    private static void checkSpecialRule(IDataset saleActives, IData param, String serialNumber) throws Exception{
        boolean youSeeYouCanFlag = BizEnv.getEnvBoolean("saleactive.YouSeeYouCan");
        if (youSeeYouCanFlag)
        {
            // 所见即所得
            SaleActiveCheckBean saleActiveCheckBean = BeanManager.createBean(SaleActiveCheckBean.class);
            UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
            IData ruleParam = new DataMap();

            boolean isQyyx = SaleActiveUtil.isQyyx(param.getString("CAMPN_TYPE"));

            for (int i = 0, size = saleActives.size(); i < size; i++)
            {
                IData saleActive = saleActives.getData(i);
                uca = UcaDataFactory.getNormalUca(serialNumber);

                param.put("PACKAGE_ID", saleActive.getString("PACKAGE_ID"));
                saleActive.put("CAMPN_TYPE", param.getString("CAMPN_TYPE"));

                IDataset checkDataset = new DatasetList();
                //规则校验
                ruleParam = saleActiveCheckBean.putCommonBreParams(saleActive, uca);
                ruleParam.put("PAGE_RULE", SaleActiveBreConst.BRE_BY_PAGE_SEL_PACKAGE);
                ruleParam.put("ACTION_TYPE", SaleActiveBreConst.ACTION_TYPE_CHK_ACTIVE_TRADE);
                ruleParam.put("ACTIVE_CHECK_MODE", SaleActiveBreConst.ACTIVE_CHECK_MODE_SEL_PKG);

                //REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151214
                if(StringUtils.isNotBlank(param.getString("IMEI_CODE")))
                {
                    ruleParam.put("IMEI_CODE", param.getString("IMEI_CODE"));
                }
                if(StringUtils.isNotBlank(param.getString("GIFT_CODE")))
                {
                    ruleParam.put("GIFT_CODE", param.getString("GIFT_CODE"));
                }
                //END

                if(StringUtils.isNotBlank(param.getString("DEVICE_MODEL_CODE")))
                {
                    ruleParam.put("DEVICE_MODEL_CODE", param.getString("DEVICE_MODEL_CODE"));
                }
                if(StringUtils.isNotBlank(param.getString("RES_TYPE_ID")))
                {
                    ruleParam.put("RES_TYPE_ID", param.getString("RES_TYPE_ID"));
                }
                if(StringUtils.isNotBlank(param.getString("TERMINAL_ID")))
                {
                    ruleParam.put("TERMINAL_ID", param.getString("TERMINAL_ID"));
                }
                IData result = new DataMap();

                // 过滤掉已经校验过不符合目标客户群的营销包
                if(!saleActive.getString("ERROR_FLAG").equals("1")){
                    IDataset list = new DatasetList();
                    IData bizid = new DataMap();
                    //只校验营销活动依赖互斥公共校验
                    bizid.put("RULE_BIZ_ID","201406260003");
                    list.add(bizid);
                    ruleParam.put("LIST_BIZ_ID", list);
                    // bre 规则校验
                    result = BizRule.bre4SuperLimit(ruleParam);
                }

                try
                {
                    saleActive.put("CAMPN_TYPE", isQyyx ? "1" : "2");
                    saleActive.put("EXT_DESC", checkDataset.getData(0).getString("EXT_DESC", "")); // 营销包描述
                    saleActive.put("RSRV_STR1", checkDataset.getData(0).getString("RSRV_STR1", "")); // package_ext的rsrv_str25
                    if ("1".equals(param.getString("X_GETFEE")))
                    {
                        saleActive.put("FEE", checkDataset.getData(0).getString("FEE"));
                    }
                    saleActive.put("RSRV_STR2", checkDataset.getData(0).getString("RSRV_STR2", "")); // 预存元素费用
                    saleActive.put("RSRV_STR3", checkDataset.getData(0).getString("RSRV_STR3", "")); // 赠送的信用度
                    saleActive.put("RSRV_STR4", checkDataset.getData(0).getString("RSRV_STR4", "")); // 终端购机费
                    saleActive.put("RSRV_STR5", SaleActiveUtil.queryUserCreditClass(uca.getUserId()));
                    uca.getUserSaleActives().clear();

                }
                catch (Exception e)
                {
                    if ("1".equals(param.getString("X_GETFEE")))
                    {
                        saleActive.put("FEE", "");
                    }
                    saleActive.put("EXT_DESC",  ""); // 营销包描述
                    saleActive.put("RSRV_STR1", ""); // package_ext的rsrv_str25
                    saleActive.put("RSRV_STR2", ""); // 预存元素费用
                    saleActive.put("RSRV_STR3", ""); // 赠送的信用度
                    saleActive.put("RSRV_STR4", ""); // 终端购机费
                    saleActive.put("RSRV_STR5", "");
                }

                IDataset ruleInfo = result.getDataset("TIPS_TYPE_ERROR");//回去规则的返回信息
                String results = "";//初始化返回信息

                //增加样式的过滤
                IData resultPrc = new DataMap();
                IData resultPrcTmp = new DataMap();
                IData resultPrcTmp2 = new DataMap();

                String usc = uca.getUser().getUserStateCodeset();
                if("5".equals(usc)||"8".equals(usc)||"9".equals(usc)||"A".equals(usc)||"Y".equals(usc)||"Z".equals(usc)){
                    saleActive.put("ERROR_FLAG", "1");
                    resultPrcTmp2.put("TIPS_INFO", "用户欠费");
                    resultPrcTmp2.put("TIPS_CODE", "11112015");
                    ruleInfo.add(resultPrcTmp2);

                }

                if(ProductInfoQry.checkSaleActiveLimitProd(saleActive.getString("PRODUCT_ID",""))){
                    resultPrc = checkByProc(saleActive,uca);
                    if (!"0".equals(resultPrc.getString("V_RESULTCODE")))
                    {
                        saleActive.put("ERROR_FLAG", "1");
                        resultPrcTmp.put("TIPS_INFO", resultPrc.getString("V_RESULTINFO","没有符合条件的营销包"));
                        resultPrcTmp.put("TIPS_CODE", resultPrc.getString("V_RESULTCODE","298"));
                        ruleInfo.add(resultPrcTmp);
                    }
                }

                //如果规则返回不为空，代表有规则拦截，有提示信息
                if (IDataUtil.isNotEmpty(ruleInfo))
                {
                    for(int k = 0;k < ruleInfo.size();k++)
                    {
                        String resultInfo = ruleInfo.getData(k).getString("TIPS_INFO","没有符合条件的营销包");
                        String resultCode = ruleInfo.getData(k).getString("TIPS_CODE","298");
                        if(StringUtils.isBlank(results))
                        {
                            results = resultCode + "$$" + resultInfo;
                        }
                        else
                        {
                            results = results + "##" + resultCode + "$$" + resultInfo;
                        }

                    }
                    saleActive.put("RESULTS", results);
                    saleActive.put("ERROR_FLAG", "1");
                }
                else
                {
                    String flag = saleActive.getString("ERROR_FLAG", "");
                    if(flag.equals("")){// 针对没有被拦截的规则。
                        results = "0$$查询成功";
                        saleActive.put("RESULTS", results);
                        saleActive.put("ERROR_FLAG", "0");
                    }
                }
            }
        }
    }

    public boolean isTroop(IDataset productPackages,String sn,IData input,IDataset commparaInfos6621) throws Exception{

        IDataset newProductPackages = new DatasetList();
        List<Integer> ascData = new ArrayList<Integer>();
        IData data =new DataMap();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        if(IDataUtil.isNotEmpty(userInfo)){
            String userId = userInfo.getString("USER_ID");
            IData param1 = new DataMap();
            param1.put("USER_ID", userId);
            IDataset troopInfos = Dao.qryByCodeParser("TF_SM_TROOP_MEMBER", "SEL_BY_USERID_TROOPID", param1);
            if(IDataUtil.isNotEmpty(troopInfos)){// 根据user_id查找出用户属于的目标客户群

                for(int i=0;i<troopInfos.size();i++){
                    String troopId = troopInfos.getData(i).getString("TROOP_ID");
                    for(int j=0;j<commparaInfos6621.size();j++){
                        if(troopId.equals(commparaInfos6621.getData(j).getString("PARA_CODE2"))){// 根据目标客户群id匹配可以办理的营销包，并放置到data数据中。
                            data.put(commparaInfos6621.getData(j).getString("PARA_CODE1"), commparaInfos6621.getData(j).getString("PARA_CODE3","0"));
                        }
                    }
                }

                if(IDataUtil.isNotEmpty(data)){// 存在符合用户的营销包
                    for(int k=0;k<productPackages.size();k++){
                        String packageId = productPackages.getData(k).getString("PACKAGE_ID");
                        if(StringUtils.isNotBlank(data.getString(packageId,""))){
                            productPackages.getData(k).put("ERROR_FLAG","0");
                            productPackages.getData(k).put("RESULTS","查询成功！");
                        }else {
                            productPackages.getData(k).put("ERROR_FLAG","1");
                            productPackages.getData(k).put("RESULTS","客户不在目标群中，不能办理该活动！");
                        }
                    }
                }else {// 不存在符合的营销包
                    for(int k=0;k<productPackages.size();k++){
                        productPackages.getData(k).put("ERROR_FLAG","1");
                        productPackages.getData(k).put("RESULTS","客户不在目标群中，不能办理该活动！");
                    }
                }

            }else {// 用户不在目标群中。
                for(int k=0;k<productPackages.size();k++){
                    productPackages.getData(k).put("ERROR_FLAG","1");
                    productPackages.getData(k).put("RESULTS","客户不在目标群中，不能办理该活动！");
                }
            }
        }
        return true;
    }
    
    //REQ201909030026关于开发线上办理“约定消费送流量和语音活动”的需求--营销活动接口优化
    public boolean checkeTROOP(IDataset productPackages,String sn,IData input) throws Exception{
    	
    	boolean checkeFLag = true;
    	String productId = productPackages.first().getString("PRODUCT_ID");
    	IDataset commparaInfos6620 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","6620",productId,null);
        //关于“禧九约消活动”对营销活动外围校验接口改造说明 by wuhao5 如果传了该参数，则返回指定数量的包
        String returnNum = input.getString("PACKAGE_NUM","");
    	//先判断该活动是否存在该配置
    	if(IDataUtil.isNotEmpty(commparaInfos6620)){
    		int n = Integer.parseInt(commparaInfos6620.first().getString("PARA_CODE5","0"));//返回最低档次数量
    		if(StringUtils.isNotBlank(returnNum)){
    			n = Integer.parseInt(returnNum);
    		}
    		IDataset newProductPackages = new DatasetList();
    		List<Integer> ascData = new ArrayList<Integer>();
    		IData data =new DataMap();
    		IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        	if(IDataUtil.isNotEmpty(userInfo)){
        		String userId = userInfo.getString("USER_ID");
        		IData param1 = new DataMap();
    			param1.put("USER_ID", userId);
        		IDataset troopInfos = Dao.qryByCodeParser("TF_SM_TROOP_MEMBER", "SEL_BY_USERID_TROOPID", param1);
        		if(IDataUtil.isNotEmpty(troopInfos)){
        			//筛选出目标客户群的活动包--start
        			String  orderData ="";
        			for(int i=0;i<troopInfos.size();i++){
        				String troopId = troopInfos.getData(i).getString("TROOP_ID");
        				for(int j=0;j<commparaInfos6620.size();j++){
        					if(troopId.equals(commparaInfos6620.getData(j).getString("PARA_CODE2"))){
        						data.put(commparaInfos6620.getData(j).getString("PARA_CODE1"), commparaInfos6620.getData(j).getString("PARA_CODE3","0"));
        					}
        				}
        			}
        			
        			if(IDataUtil.isNotEmpty(data)){
                        Set hashSet = new HashSet();
        				for(int k=0;k<productPackages.size();k++){
        					String packageId = productPackages.getData(k).getString("PACKAGE_ID");
        					if(StringUtils.isNotBlank(data.getString(packageId,""))){
        						productPackages.getData(k).put("ORDER_DATA",Integer.parseInt(data.getString(packageId)));
        						newProductPackages.add(productPackages.getData(k));
                                hashSet.add(Integer.parseInt(data.getString(packageId)));
        					}
        				}
                        ascData.addAll(hashSet);
        			}
        			
        			//筛选出目标客户群的活动包--end	
    				Collections.sort(ascData);
    				if(newProductPackages.size()>=n && ascData.size() > n){
    					ascData= ascData.subList(0, n);
    				}
    				productPackages.clear();
    				for(int k=0;k<newProductPackages.size();k++){
    					int  order = Integer.parseInt(newProductPackages.getData(k).getString("ORDER_DATA"));
    					for (int p = 0;p<ascData.size();p++) {
                            if(ascData.get(p)==order){
                                newProductPackages.getData(k).put("ERROR_FLAG","0");
                                productPackages.add(newProductPackages.getData(k));
                            }
                        }
    				}
        		}else{
        			productPackages.clear();
        		}
        		
        	}
        	
        	if(IDataUtil.isNotEmpty(productPackages)){
        		checkeFLag = false;
        	}
    		
    	}
    	
    	return checkeFLag;
    	
    }
    
    
    /**
     * 宽带产品变更同时办理营销活动用,新增接口，暂时只给短厅 
     * 
     * **/
    public IDataset checkWidenetActiveByPrdAndPkgForSMS(String serialNumber, String campnType, String productId, String package_id,String xGetMode, String xGetFee, String deviceModelCode, String terminalId, IData input) throws Exception
    {
        IDataset productPackages = new DatasetList();
        
        if(StringUtils.isNotBlank(package_id))
        {
            productPackages = UpcCall.qryOffersByCatalogIdAndOfferId(productId, package_id, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        }
        else
        {
            productPackages = UpcCall.qryOffersByCatalogId(productId);
        }
        
        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), productPackages);

        String errorInfo = widenetSaleActiveCheck(serialNumber);
        
        if(errorInfo==null || "".equals(errorInfo)){
	        IDataset userinfo = UserInfoQry.getUserinfo(serialNumber);
	        IDataset userSaleActives = SaleActiveInfoQry.getUserSaleActiveInfo(userinfo.getData(0).getString("USER_ID"));
	        for(int k=0; k< userSaleActives.size(); k++){
	        	if(productId.equals(userSaleActives.getData(k).getString("PRODUCT_ID")) && package_id.equals(userSaleActives.getData(k).getString("PACKAGE_ID")) ){
	        		errorInfo = "14324053$$您当前在[宽带1+活动]活动协议期内, 不能办理该类型活动!";
	        		break;
	        	}
	        }
        }
        //“夏”降到底点播 可以办理的营销活动
        IDataset saleActivePackage = new DatasetList();
        
        String widenetUserRate = WideNetUtil.getWidenetUserRate(serialNumber);
        
        IDataset widenetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_"+serialNumber);
        
        String wideType = "";
        
        if (IDataUtil.isNotEmpty(widenetInfo))
        {
            wideType = widenetInfo.getData(0).getString("RSRV_STR2");
            
            if ("4".equals(wideType))
            {
                if (StringUtils.isNotBlank(errorInfo))
                {
                    errorInfo = "20171008$$校园宽带客户,不能拨打点播号办理";
                    wideType="";
                }
            }
            else if ("1".equals(wideType) || "6".equals(wideType))
            {
                wideType = "FTTB";
            }
            else
            {
                wideType = "FTTH";
            }
        }
        
        //参数配置 夏”降到底点播 可以办理的营销活动
        IDataset saleActiveCommparas = CommparaInfoQry.getCommNetInfo("CSM", "178", "DIANBO");
        
        String saleActiveCommparaPackageIds = "";
        if (IDataUtil.isNotEmpty(saleActiveCommparas))
        {
            for (int j = 0; j < saleActiveCommparas.size(); j++)
            {
                if (StringUtils.isNotBlank(wideType))
                {
                    if (wideType.equals(saleActiveCommparas.getData(j).getString("PARA_CODE3")))
                    {
                        saleActiveCommparaPackageIds = saleActiveCommparaPackageIds +"|"+ saleActiveCommparas.getData(j).getString("PARA_CODE2");
                    }
                }
                else
                {
                    saleActiveCommparaPackageIds = saleActiveCommparaPackageIds +"|"+ saleActiveCommparas.getData(j).getString("PARA_CODE2");
                }
            }
            
            for(int i=0;i<productPackages.size();i++)
            {
                IData productPackage = productPackages.getData(i);
                
                if (saleActiveCommparaPackageIds.indexOf(productPackage.getString("OFFER_CODE")) < 0)
                {
                    continue;
                }
                
                productPackage.put("PACKAGE_ID", productPackage.getString("OFFER_CODE"));
                productPackage.put("PRODUCT_ID", productId);
                productPackage.put("CAMPN_TYPE", campnType);
                
                if(StringUtils.isNotBlank(errorInfo))
                {
                    productPackage.put("RESULTS", errorInfo);
                    productPackage.put("ERROR_FLAG", "1");
                }
                else
                {
                    IDataset packageExtInfos = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, productPackage.getString("OFFER_CODE"), "TD_B_PACKAGE_EXT");
                    
                    if (IDataUtil.isNotEmpty(packageExtInfos))
                    {
                        String serviceIds = packageExtInfos.getData(0).getString("RSRV_STR24");
                        
                        if (StringUtils.isNotBlank(serviceIds))
                        {
                            String []serviceIdsArray = serviceIds.split("\\|");
                            String serviceId = "";
                            for (int j = 0; j < serviceIdsArray.length; j++)
                            {
                                serviceId = serviceIdsArray[j];
                                
                                if (StringUtils.isNotBlank(serviceId))
                                {
                                    break;
                                }
                            }
                            
                            IDataset rateDataset = CommparaInfoQry.getCommpara("CSM", "4000",serviceId , "0898");
                            
                            if (IDataUtil.isNotEmpty(rateDataset))
                            {
                                String saleActiveRate = rateDataset.getData(0).getString("PARA_CODE1","");
                                
                                if (StringUtils.isNotBlank(widenetUserRate) && StringUtils.isNotBlank(saleActiveRate))
                                {
                                    if (Integer.valueOf(saleActiveRate) > Integer.valueOf(widenetUserRate))
                                    {
                                        productPackage.put("CHANGE_UP_DOWN_TAG", "1");  //标记升级档，0 不变，1：升档,2:降档,3:产品变，速率不变
                                    }
                                }
                            }
                        }
                        
                        productPackage.put("WIDE_USER_SELECTED_SERVICEIDS", serviceIds);
                    }
                    
                    productPackage.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
                    productPackage.put("ORDER_TYPE_CODE", "601");
                    
                    //默认生效时间为次月1日
                    productPackage.put("BOOKING_DATE", SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime())); 
                }
                
                saleActivePackage.add(productPackage);
            }
        }
        
        if (IDataUtil.isEmpty(saleActivePackage))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_48);
        }
        
        IData param = new DataMap();

        param.putAll(input);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("CAMPN_TYPE", campnType);
        param.put("PRODUCT_ID", productId);
        param.put("DEVICE_MODEL_CODE", deviceModelCode);
        param.put("TERMINAL_ID", terminalId);
        param.put("X_GETFEE", xGetFee);
        param.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
        
        //如果前面的校验已经报错了则不需要在调用营销活动受理框架校验
        if (StringUtils.isEmpty(errorInfo))
        { 
            checkPackages(saleActivePackage,param,serialNumber);
        }
        
        //checkPackages(saleActivePackage,param,serialNumber);
        return saleActivePackage;
    }
    
    
    public String widenetSaleActiveCheck(String serialNumber) throws Exception
    {
        IDataset userinfo = UserInfoQry.getUserinfo(serialNumber);
        String userId = "";
        String widenetUserId = "";
        
        if (IDataUtil.isNotEmpty(userinfo))
        {
            userId=userinfo.getData(0).getString("USER_ID");
        }else
        {
             CSAppException.apperr(CrmCommException.CRM_COMM_103,"未找到用户资料！");
        }
        
        String errorInfo = "";
        
        
        //校验，增加手机号未完工和宽带首月不能办理业务限制
        IDataset unFinishs=checkWidenetUserIfUnFinsh(serialNumber);
        if (!IDataUtil.isEmpty(unFinishs))
        {
            errorInfo = "20172001$$该用户"+serialNumber+"存在未完工工单,不能拨打点播好办理";
            return errorInfo;
        }
        IDataset unFinishs1=checkWidenetUserIfUnFinsh("KD_"+serialNumber);
        if (!IDataUtil.isEmpty(unFinishs1))
        {
            errorInfo = "20172001$$该用户"+"KD_"+serialNumber+"存在未完工工单,不能拨打点播好办理";
            return errorInfo;
        }
        IDataset firstMons=checkWidenetInfoFirstMon("KD_"+serialNumber);
        if (!IDataUtil.isEmpty(firstMons))
        {
            errorInfo = "20172002$$开户当月不能办理非员工套餐的产品变更！";
            return errorInfo;
        }
        //校验1 对于未完工和移机未完工的客户，不能拨打点播号办理
        IDataset widenetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_"+serialNumber);
        
        if (IDataUtil.isEmpty(widenetInfo))
        {
            errorInfo = "20171002$$该用户不是宽带用户或宽带开户未完工,不能拨打点播好办理";
            return errorInfo;
        }
        else
        {
            widenetUserId = widenetInfo.getData(0).getString("USER_ID");
            
            //ADSL制式的客户，不能拨打点播号办理
            if (BofConst.WIDENET_TYPE_ADSL.equals(widenetInfo.getData(0).getString("RSRV_STR2")))
            {
                errorInfo = "20171007$$ADSL制式的宽带客户，不能拨打点播号办理";
                return errorInfo;
            }
        }
        
        //未完工移机工单
        IDataset widenetMoveTrade = TradeInfoQry.getMainTradeByUserIdTypeCode(widenetUserId, "606");
        
        if (IDataUtil.isNotEmpty(widenetMoveTrade))
        {
            errorInfo = "20171005$$移机未完工的客户，不能拨打点播号办理";
            return errorInfo;
        }
        
        IDataset userSaleActives = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
        
        //是否办理了1+营销活动
        boolean isOnePlusSaleActive = false;
        //是否办理了魔百和1+营销活动
        boolean isMoOnePlusSaleActive = false;
        
        if (IDataUtil.isNotEmpty(userSaleActives))
        {
            for (int i = 0; i < userSaleActives.size(); i++)
            {
                //是否办理了1+营销活动
                if ("69908001".equals(userSaleActives.getData(i).getString("PRODUCT_ID")) || "69908015".equals(userSaleActives.getData(i).getString("PRODUCT_ID")))
                {
                    isOnePlusSaleActive = true;
                }
                else if ("69908012".equals(userSaleActives.getData(i).getString("PRODUCT_ID")))
                {
                    isMoOnePlusSaleActive = true;
                }
            }
        }
        
        //宽带1+魔百和的客户，不能拨打点播号办理
        if (isMoOnePlusSaleActive)
        {
            errorInfo = "20171003$$宽带1+魔百和的客户，不能拨打点播号办理";
            return errorInfo;
        }
        
        
        //存量宽带1+活动客户，才能点播办理提档（即办理50M或100M营销活动）
        if (!isOnePlusSaleActive)
        {
            errorInfo = "20171001$$该用户不是存量宽带1+活动客户,不能点播办理提档";
            return errorInfo;
        }
        
        //校验宽带处于免费期，不能拨打点播号办理
        IDataset discntInfo = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(widenetUserId);//通过widenetUserId获取优惠信息
        
        //如果用户无有效的优惠信息，即可能优惠还未生效，或者无优惠
        if (IDataUtil.isEmpty(discntInfo))
        {
            errorInfo = "20171006$$宽带处于免费期，不能拨打点播号办理";
            return errorInfo;
        }
        
        //是否存在预约产品
        if (WideNetUtil.isExistsBookingChangeProduct(widenetUserId))
        {
            errorInfo = "20171004$$已有预约产品变更的客户，不能拨打点播号办理";
            return errorInfo;
        }
        
        return errorInfo;
    }
    
    private static IData checkByProc(IData input,UcaData uca) throws Exception {		
    	
    	IData paramValue = new DataMap();
		
		paramValue.put("V_EVENT_TYPE", "ATTR");
		paramValue.put("V_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
		paramValue.put("V_CITY_CODE", CSBizBean.getVisit().getCityCode());
		paramValue.put("V_DEPART_ID", CSBizBean.getVisit().getDepartId());
		paramValue.put("V_STAFF_ID", CSBizBean.getVisit().getStaffId());
		
		paramValue.put("V_USER_ID", uca.getUserId());
		paramValue.put("V_DEPOSIT_GIFT_ID", uca.getCustId());
		paramValue.put("V_PURCHASE_MODE",input.getString("PRODUCT_ID"));
		paramValue.put("V_PURCHASE_ATTR", input.getString("PACKAGE_ID"));
		paramValue.put("V_TRADE_ID", "-1");
		
		paramValue.put("V_CHECKINFO", input.getString("CHECKINFO", ""));
		paramValue.put("V_RESULTCODE", input.getString("RESULTCODE", ""));
		paramValue.put("V_RESULTINFO", input.getString("RESULTINFO", ""));
		paramValue.put("V_SALE_TYPE", input.getString("CAMPN_TYPE"));
		paramValue.put("V_VIP_TYPE_ID", input.getString("VIP_TYPE_ID", "-1"));
		
		paramValue.put("V_VIP_CLASS_ID", input.getString("VIP_CLASS_ID", "-1"));	
		ProductInfoQry.checkSaleActiveProdByProced(paramValue);
    	return paramValue;
    }
    
    
    public static void checkPackages(IDataset saleActives,IData param, String sn) throws Exception
    {
    	boolean youSeeYouCanFlag = BizEnv.getEnvBoolean("saleactive.YouSeeYouCan");
        if (youSeeYouCanFlag)
        {
            // 所见即所得
            SaleActiveCheckBean saleActiveCheckBean = BeanManager.createBean(SaleActiveCheckBean.class);
            UcaData uca = UcaDataFactory.getNormalUca(sn);
            IData ruleParam = new DataMap();
            
            boolean isQyyx = SaleActiveUtil.isQyyx(param.getString("CAMPN_TYPE"));
            
            for (int i = 0, size = saleActives.size(); i < size; i++)
            {
                IData saleActive = saleActives.getData(i);
                uca = UcaDataFactory.getNormalUca(sn);
                
                param.put("PACKAGE_ID", saleActive.getString("PACKAGE_ID"));
                saleActive.put("CAMPN_TYPE", param.getString("CAMPN_TYPE"));

                IDataset checkDataset = new DatasetList();
                //规则校验
                ruleParam = saleActiveCheckBean.putCommonBreParams(saleActive, uca);
                ruleParam.put("PAGE_RULE", SaleActiveBreConst.BRE_BY_PAGE_SEL_PACKAGE);
                ruleParam.put("ACTION_TYPE", SaleActiveBreConst.ACTION_TYPE_CHK_ACTIVE_TRADE);
                ruleParam.put("ACTIVE_CHECK_MODE", SaleActiveBreConst.ACTIVE_CHECK_MODE_SEL_PKG);
                
                //REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151214
                if(StringUtils.isNotBlank(param.getString("IMEI_CODE")))
                {
                	ruleParam.put("IMEI_CODE", param.getString("IMEI_CODE"));
                }
                if(StringUtils.isNotBlank(param.getString("GIFT_CODE")))
                {
                	ruleParam.put("GIFT_CODE", param.getString("GIFT_CODE"));
                }
                //END
                
                if(StringUtils.isNotBlank(param.getString("DEVICE_MODEL_CODE")))
                {
                	ruleParam.put("DEVICE_MODEL_CODE", param.getString("DEVICE_MODEL_CODE"));
                }
                if(StringUtils.isNotBlank(param.getString("RES_TYPE_ID")))
                {
                	ruleParam.put("RES_TYPE_ID", param.getString("RES_TYPE_ID"));
                }
                if(StringUtils.isNotBlank(param.getString("TERMINAL_ID")))
                {
                	ruleParam.put("TERMINAL_ID", param.getString("TERMINAL_ID"));
                }
                
                //调用规则
                IData result = BizRule.bre4SuperLimit(ruleParam);
                
                try
                {

	                checkDataset = CSAppCall.call("SS.SaleActiveRegSVC.check4Intf", param);
	                saleActive.put("CAMPN_TYPE", isQyyx ? "1" : "2");
	                saleActive.put("EXT_DESC", checkDataset.getData(0).getString("EXT_DESC", "")); // 营销包描述
	                saleActive.put("RSRV_STR1", checkDataset.getData(0).getString("RSRV_STR1", "")); // package_ext的rsrv_str25
                    if ("1".equals(param.getString("X_GETFEE")))
                    {
                        saleActive.put("FEE", checkDataset.getData(0).getString("FEE"));
                    }
	                saleActive.put("RSRV_STR2", checkDataset.getData(0).getString("RSRV_STR2", "")); // 预存元素费用
	                saleActive.put("RSRV_STR3", checkDataset.getData(0).getString("RSRV_STR3", "")); // 赠送的信用度
	                saleActive.put("RSRV_STR4", checkDataset.getData(0).getString("RSRV_STR4", "")); // 终端购机费
	                saleActive.put("RSRV_STR5", SaleActiveUtil.queryUserCreditClass(uca.getUserId()));
	                uca.getUserSaleActives().clear();
	                
                }
                catch (Exception e)
                {
                    if ("1".equals(param.getString("X_GETFEE")))
                    {
                        saleActive.put("FEE", "");
                    }
                	saleActive.put("EXT_DESC",  ""); // 营销包描述
                	saleActive.put("RSRV_STR1", ""); // package_ext的rsrv_str25
                	saleActive.put("RSRV_STR2", ""); // 预存元素费用
                	saleActive.put("RSRV_STR3", ""); // 赠送的信用度
                	saleActive.put("RSRV_STR4", ""); // 终端购机费
                	saleActive.put("RSRV_STR5", "");
                }
                
                IDataset ruleInfo = result.getDataset("TIPS_TYPE_ERROR");//回去规则的返回信息
                String results = "";//初始化返回信息
                
                //增加样式的过滤
                IData resultPrc = new DataMap();
                IData resultPrcTmp = new DataMap();
                IData resultPrcTmp2 = new DataMap();
                
                String usc = uca.getUser().getUserStateCodeset();
                if("5".equals(usc)||"8".equals(usc)||"9".equals(usc)||"A".equals(usc)||"Y".equals(usc)||"Z".equals(usc)){
                	saleActive.put("ERROR_FLAG", "1");
                	resultPrcTmp2.put("TIPS_INFO", "用户欠费");
                	resultPrcTmp2.put("TIPS_CODE", "11112015");
                    ruleInfo.add(resultPrcTmp2);

                }
                
                if(ProductInfoQry.checkSaleActiveLimitProd(saleActive.getString("PRODUCT_ID",""))){
                	resultPrc = checkByProc(saleActive,uca);
                    if (!"0".equals(resultPrc.getString("V_RESULTCODE")))
                    {
                        saleActive.put("ERROR_FLAG", "1");
                        resultPrcTmp.put("TIPS_INFO", resultPrc.getString("V_RESULTINFO","没有符合条件的营销包"));
                        resultPrcTmp.put("TIPS_CODE", resultPrc.getString("V_RESULTCODE","298"));
                        ruleInfo.add(resultPrcTmp);
                    }
        		}
                
                //如果规则返回不为空，代表有规则拦截，有提示信息
	            if (IDataUtil.isNotEmpty(ruleInfo))
	            {
	            	for(int k = 0;k < ruleInfo.size();k++)
	            	{
	            		String resultInfo = ruleInfo.getData(k).getString("TIPS_INFO","没有符合条件的营销包");
	            		String resultCode = ruleInfo.getData(k).getString("TIPS_CODE","298");
	            		if(StringUtils.isBlank(results))
	            		{
	            			results = resultCode + "$$" + resultInfo;
	            		}
	            		else
	            		{
	            			results = results + "##" + resultCode + "$$" + resultInfo;
	            		}
	            		
	            	}
	            	saleActive.put("RESULTS", results);
	                saleActive.put("ERROR_FLAG", "1");
	            }
	            else
	            { 
	        		results = "0$$查询成功";
	        		saleActive.put("RESULTS", results);
	        		saleActive.put("ERROR_FLAG", "0");
	            }
            }
        }
    }
    
    /**
     * 新增接口，只给网厅
     * **/
    public IDataset checkActiveByPrdAndPkgForWEB(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER");
		String campnType = input.getString("CAMPN_TYPE");
		String productId = input.getString("PRODUCT_ID");
		String package_id = input.getString("PACKAGE_ID","");
		String xGetMode = input.getString("X_GETMODE");
		String xGetFee = input.getString("X_GETFEE");
		String deviceModelCode = input.getString("DEVICE_MODEL_CODE");
		String terminalId = input.getString("TERMINAL_ID");
		String callFlag = input.getString("CALL_FLAG");
		
		//获取和处理Package数据
		IDataset productPackages = new DatasetList();
		
        if(StringUtils.isNotBlank(package_id))
        {
        	package_id = package_id.replace(" ", "");
        	String[] packageIdArray = StringUtils.split(package_id, ",");
        	for (int i = 0; i < packageIdArray.length; i++)
        	{
        		//查看是否该包属于该产品下
//        		IDataset tempPackages = ProductInfoQry.queryPackageByPID(packageIdArray[i], productId);
        	    IDataset tempPackages = UpcCall.qryOffersByCatalogIdAndOfferId(productId, packageIdArray[i], BofConst.ELEMENT_TYPE_CODE_PACKAGE);
				if(IDataUtil.isNotEmpty(tempPackages))
				{
					productPackages.add(tempPackages.getData(0));
				}
			}
        }
        else
        {
//        	productPackages = ProductInfoQry.queryProductAllPackages(productId);
            productPackages = UpcCall.qryOffersByCatalogId(productId);
        }
        
        //包权限过滤
        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), productPackages);

        if (IDataUtil.isEmpty(productPackages))
        {
        	CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_48);
        }
		
        for(int i=0;i<productPackages.size();i++)
        {
            IData productPackage = productPackages.getData(i);
            productPackage.put("NODE_COUNT", "0");
            productPackage.put("PRODUCT_MODE", "02");
        }
		//老接口模式
		if("1".equals(callFlag))
		{
			checkActiveByPrdAndPkgForWEB(productPackages, input);
			return productPackages;
		}
		else
		{
	        checkPackagesForWEB(productPackages, input, serialNumber);
	        return productPackages;
		}
    }
    
    public static void checkPackagesForWEB(IDataset saleActives,IData param, String sn) throws Exception
    {
    	boolean youSeeYouCanFlag = BizEnv.getEnvBoolean("saleactive.YouSeeYouCan");
        if (youSeeYouCanFlag)
        {
            // 所见即所得
            SaleActiveCheckBean saleActiveCheckBean = BeanManager.createBean(SaleActiveCheckBean.class);
            UcaData uca = UcaDataFactory.getNormalUca(sn);
            IData ruleParam = new DataMap();
            
            boolean isQyyx = SaleActiveUtil.isQyyx(param.getString("CAMPN_TYPE"));
            
            for (int i = 0, size = saleActives.size(); i < size; i++)
            {
                IData saleActive = saleActives.getData(i);
                uca = UcaDataFactory.getNormalUca(sn);
                
                param.put("PACKAGE_ID", saleActive.getString("PACKAGE_ID"));
                saleActive.put("CAMPN_TYPE", isQyyx ? "1" : "2");
                IDataset checkDataset = new DatasetList();
                //规则校验
                ruleParam = saleActiveCheckBean.putCommonBreParams(saleActive, uca);
                ruleParam.put("PAGE_RULE", SaleActiveBreConst.BRE_BY_PAGE_SEL_PACKAGE);
                ruleParam.put("ACTION_TYPE", SaleActiveBreConst.ACTION_TYPE_CHK_ACTIVE_TRADE);
                ruleParam.put("ACTIVE_CHECK_MODE", SaleActiveBreConst.ACTIVE_CHECK_MODE_SEL_PKG);
                
                //REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151214
                if(StringUtils.isNotBlank(param.getString("IMEI_CODE")))
                {
                	ruleParam.put("IMEI_CODE", param.getString("IMEI_CODE"));
                }
                if(StringUtils.isNotBlank(param.getString("GIFT_CODE")))
                {
                	ruleParam.put("GIFT_CODE", param.getString("GIFT_CODE"));
                }
                //END
                
                if(StringUtils.isNotBlank(param.getString("DEVICE_MODEL_CODE")))
                {
                	ruleParam.put("DEVICE_MODEL_CODE", param.getString("DEVICE_MODEL_CODE"));
                }
                if(StringUtils.isNotBlank(param.getString("RES_TYPE_ID")))
                {
                	ruleParam.put("RES_TYPE_ID", param.getString("RES_TYPE_ID"));
                }
                if(StringUtils.isNotBlank(param.getString("TERMINAL_ID")))
                {
                	ruleParam.put("TERMINAL_ID", param.getString("TERMINAL_ID"));
                }
                
                //调用规则
                IData result = BizRule.bre4SuperLimit(ruleParam);
                IDataset ruleInfo = result.getDataset("TIPS_TYPE_ERROR");//规则的拦截信息
                
                //准备调用营销活动受理接口，进行预受理校验
                String errorInfo = "";//初始化接口返回信息
                
                try
                {
	                checkDataset = CSAppCall.call("SS.SaleActiveRegSVC.check4Intf", param);
	                saleActive.put("EXT_DESC", checkDataset.getData(0).getString("EXT_DESC", "")); // 营销包描述
	                
                    if ("1".equals(param.getString("X_GETFEE")))
                    {
                        saleActive.put("FEE", checkDataset.getData(0).getString("FEE"));
                        saleActive.put("RSRV_STR1", checkDataset.getData(0).getString("RSRV_STR1", "")); // package_ext的rsrv_str25
                        saleActive.put("RSRV_STR2", checkDataset.getData(0).getString("RSRV_STR2", "")); // 预存元素费用
    	                saleActive.put("RSRV_STR3", checkDataset.getData(0).getString("RSRV_STR3", "")); // 赠送的信用度
    	                saleActive.put("RSRV_STR4", checkDataset.getData(0).getString("RSRV_STR4", "")); // 终端购机费
    	                saleActive.put("RSRV_STR5", SaleActiveUtil.queryUserCreditClass(uca.getUserId()));
                    }
	                
	                uca.getUserSaleActives().clear();
	                
                }
                catch (Exception e)
                {
                	saleActive.put("EXT_DESC",  ""); // 营销包描述
                    if ("1".equals(param.getString("X_GETFEE")))
                    {
                        saleActive.put("FEE", "");
                        saleActive.put("RSRV_STR1", ""); // package_ext的rsrv_str25
                    	saleActive.put("RSRV_STR2", ""); // 预存元素费用
                    	saleActive.put("RSRV_STR3", ""); // 赠送的信用度
                    	saleActive.put("RSRV_STR4", ""); // 终端购机费
                    	saleActive.put("RSRV_STR5", "");
                    }

                	String exceptionStr = Utility.parseExceptionMessage(e);
                    String errorArr[] = exceptionStr.split(BaseException.INFO_SPLITE_CHAR);
                    errorInfo = errorArr[1];
                }
                
                String results = "";//初始化返回信息
                
                //增加样式的过滤
                IData resultPrc = new DataMap();
                IData resultPrcTmp = new DataMap();
                IData resultPrcTmp2 = new DataMap();
                
                String usc = uca.getUser().getUserStateCodeset();
                if("5".equals(usc)||"8".equals(usc)||"9".equals(usc)||"A".equals(usc)||"Y".equals(usc)||"Z".equals(usc)){
                	saleActive.put("ERROR_FLAG", "1");
                	resultPrcTmp2.put("TIPS_INFO", "用户欠费");
                	resultPrcTmp2.put("TIPS_CODE", "11112015");
                    ruleInfo.add(resultPrcTmp2);

                }
                
                if(ProductInfoQry.checkSaleActiveLimitProd(saleActive.getString("PRODUCT_ID",""))){
                	resultPrc = checkByProc(saleActive,uca);
                    if (!"0".equals(resultPrc.getString("V_RESULTCODE")))
                    {
                        saleActive.put("ERROR_FLAG", "1");
                        resultPrcTmp.put("TIPS_INFO", resultPrc.getString("V_RESULTINFO","没有符合条件的营销包"));
                        resultPrcTmp.put("TIPS_CODE", resultPrc.getString("V_RESULTCODE","298"));
                        ruleInfo.add(resultPrcTmp);
                    }
        		}
                
                //如果规则返回不为空或预受理校验接口不为空，代表有拦截，有提示信息
	            if (IDataUtil.isNotEmpty(ruleInfo) || StringUtils.isNotBlank(errorInfo))
	            {
	            	for(int k = 0;k < ruleInfo.size();k++)
	            	{
	            		String resultInfo = ruleInfo.getData(k).getString("TIPS_INFO","没有符合条件的营销包");
	            		String resultCode = ruleInfo.getData(k).getString("TIPS_CODE","298");
	            		
	            		//将预受理校验接口中的信息进行过滤，过去掉规则返回的
	            		errorInfo = errorInfo.replace(resultInfo, "");
	            		
	            		if(StringUtils.isBlank(results))
	            		{
	            			results = resultCode + "$$" + resultInfo;
	            		}
	            		else
	            		{
	            			results = results + "##" + resultCode + "$$" + resultInfo;
	            		}
	            		
	            	}
	            	
	            	//对预受理接口返回的信息与进行合并
	            	if(StringUtils.isNotBlank(errorInfo))
	            	{
	            		//如果规则未返回信息
	            		if(StringUtils.isBlank(results))
	            		{
	            			results = "298$$" + errorInfo;
	            		}
	            		else
	            		{
	            			results = results + "##298$$" + errorInfo;
	            		}
	            	}
	            	
	            	//将返回信息中的[]过滤调
	            	results = results.replace("[", "");
	            	results = results.replace("]", "");
	            	
	            	saleActive.put("RESULTS", results);
	                saleActive.put("ERROR_FLAG", "1");
	            }
	            else
	            {
	        		results = "0$$查询成功";
	        		saleActive.put("RESULTS", results);
	        		saleActive.put("ERROR_FLAG", "0");
	            }
            }
        }
    }
    
    /**
     * SERIAL_NUMBER        手机号码
     * CAMPN_TYPE           营销活动类型，不知道有没有用？ 
     * PRODUCT_ID           营销活动产品ID
     * PACKAGE_ID           营销活动包ID
     * X_GETMODE            老接口的返回模式，该接口中不用了，0：无可用包则返回空   1：无可用包需返回错误信息
     * X_GETFEE             是否返回费用
     * DEVICE_MODEL_CODE    机型编码
     * TERMINAL_ID          IMEI串号
     * RES_TYPE_ID          子机型编码
     * CALL_FLAG            调用标识 0：全部包规则校验（与SMS接口一样）    1：部分校验（与老接口一样）
     * */
    public void checkActiveByPrdAndPkgForWEB(IDataset saleActives, IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER");
		String campnType = input.getString("CAMPN_TYPE");
		
        boolean isQyyx = SaleActiveUtil.isQyyx(campnType);
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        
        IData param = new DataMap();
        param.putAll(input);
        param.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
        
        for (int index = 0, size = saleActives.size(); index < size; index++)
        {
            IData saleActive = saleActives.getData(index);
            param.put("PACKAGE_ID", saleActive.getString("PACKAGE_ID"));
            saleActive.put("CAMPN_TYPE", isQyyx ? "1" : "2");
            String errorInfo = "";//初始化接口返回信息
            
            try
            {
                IDataset checkDataset = CSAppCall.call("SS.SaleActiveRegSVC.check4Intf", param);
                saleActive.put("EXT_DESC", checkDataset.getData(0).getString("EXT_DESC", "")); // 营销包描述
                if ("1".equals(param.getString("X_GETFEE")))
                {
                	saleActive.put("FEE", checkDataset.getData(0).getString("FEE"));
                	//按陈琼春要求，把以下扩展字段也全部纳入X_GETFEE=1时才显示
                    saleActive.put("RSRV_STR1", checkDataset.getData(0).getString("RSRV_STR1", "")); // package_ext的rsrv_str25
                    saleActive.put("RSRV_STR2", checkDataset.getData(0).getString("RSRV_STR2", "")); // 预存元素费用
                    saleActive.put("RSRV_STR3", checkDataset.getData(0).getString("RSRV_STR3", "")); // 赠送的信用度
                    saleActive.put("RSRV_STR4", checkDataset.getData(0).getString("RSRV_STR4", "")); // 终端购机费
                    saleActive.put("RSRV_STR5", SaleActiveUtil.queryUserCreditClass(uca.getUserId()));
                }
            }
            catch (Exception e)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("exception>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + e);
                }
                
                saleActive.put("EXT_DESC",  ""); // 营销包描述
                
                if ("1".equals(param.getString("X_GETFEE")))
                {
                	saleActive.put("FEE", "");
                	saleActive.put("RSRV_STR1", ""); // package_ext的rsrv_str25
                    saleActive.put("RSRV_STR2", ""); // 预存元素费用
                    saleActive.put("RSRV_STR3", ""); // 赠送的信用度
                    saleActive.put("RSRV_STR4", ""); // 终端购机费
                    saleActive.put("RSRV_STR5", "");
                }
                
                String exceptionStr = Utility.parseExceptionMessage(e);
                String errorArr[] = exceptionStr.split(BaseException.INFO_SPLITE_CHAR);
                errorInfo = errorArr[1];
            }
            
            String results = "";//初始化返回信息
            
            //如果预受理校验接口不为空，代表有拦截，有提示信息
            if (StringUtils.isNotBlank(errorInfo))
            {
            	results = "298$$" + errorInfo;
            	
            	//将返回信息中的[]过滤调
            	results = results.replace("[", "");
            	results = results.replace("]", "");
            	
            	saleActive.put("RESULTS", results);
                saleActive.put("ERROR_FLAG", "1");
            }
            else
            {
        		results = "0$$查询成功";
        		saleActive.put("RESULTS", results);
        		saleActive.put("ERROR_FLAG", "0");
            }
        }
    }
    
    public static IDataset checkWidenetUserIfUnFinsh(String serialNumber) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_B_TRADE", "SEL_UNFINISH_USER_INFO", inparam,Route.getJourDbDefault());
    }
    public static IDataset checkWidenetInfoFirstMon(String wSerialNumber) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", wSerialNumber);

        return Dao.qryByCode("TF_F_USER", "SEL_WIDENET_IF_FIRST_MON", inparam);
    }

}
