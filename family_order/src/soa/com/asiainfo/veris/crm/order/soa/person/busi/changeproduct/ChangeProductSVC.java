
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;


import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBindInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sharemeal.ShareMealBean;

import java.math.BigDecimal;

public class ChangeProductSVC extends CSBizService
{

    /**
     * 用户业务提示信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset afterSubmitSnTipsInfo(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");

        IDataset results = new DatasetList();

        String saleActiveTips = querySaleActiveTipsInfo(userId);

        String bindTips = this.bindBecomeDueTipsInfo(userId);

        if (StringUtils.isNotBlank(saleActiveTips))
        {
            results.add(getTipsInfoData("1", saleActiveTips, "110"));
        }

        if (StringUtils.isNotBlank(bindTips))
        {
            results.add(getTipsInfoData("1", bindTips, "110"));
        }

        return results;
    }

    /**
     * 绑定到期提醒
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public String bindBecomeDueTipsInfo(String userId) throws Exception
    {
        String msg = "";

        IDataset bindInfos = UserBindInfoQry.queryUserBindByUserId(userId);
        if (IDataUtil.isEmpty(bindInfos))// 是否为中高端用户
        {
            return msg;
        }

        IDataset maxSaleActives = UserBindInfoQry.queryMaxSaleActiveByUserId(userId);
        if (IDataUtil.isEmpty(maxSaleActives))
        {
            IDataset allSaleActives = UserSaleActiveInfoQry.queryAllSaleActiveByUserId(userId);

            if (IDataUtil.isEmpty(allSaleActives))
            {
                msg = "该用户为拍照中高端客户,但从未办理任何捆绑业务,请推荐客户办理营销活动!";
            }
        }
        else
        {
            IData saleActive = maxSaleActives.getData(0);

            msg = "该用户为拍照中高端客户,其最后一笔合约捆绑类业务为【" + saleActive.getString("PRODUCT_NAME") + "】,此业务在\"" + SysDateMgr.decodeTimestamp(saleActive.getString("END_DATE"), SysDateMgr.PATTERN_STAND) + "\"即将到期,请推荐客户办理营销活动!";
        }

        return msg;
    }
    
    /**
     * @Description: 选择产品时业务提示信息
     * @param:参数描述
     * @return：返回结果描述
     * @version: v1.0.0
     * @author: maoke
     * @date: May 19, 2014 8:28:49 PM
     */
    public IDataset changeProductTipsInfo(IData param) throws Exception
    {
        IDataset results = new DatasetList();

        String userId = param.getString("USER_ID");
        String custId = param.getString("CUST_ID");
        String oldProductId = param.getString("USER_PRODUCT_ID");
        String newProductId = param.getString("NEW_PRODUCT_ID");
        String oldBrand = UProductInfoQry.qryProductByPK(oldProductId).getString("BRAND_CODE");
        String newBrand = UProductInfoQry.qryProductByPK(newProductId).getString("BRAND_CODE");
        String bookingDate = param.getString("BOOKING_DATE");
        
        
        
        String newProductName = UProductInfoQry.getProductNameByProductId(newProductId);
        if (StringUtils.isNotBlank(newProductName))
        {
            String tipsInfo = "你所办理的新产品是【" + newProductName + "】,是否继续？";
            
            IData commpara = new DataMap();
            commpara.put("SUBSYS_CODE", "CSM");
            commpara.put("PARAM_ATTR", "5453");
            commpara.put("PARAM_CODE", "TIPINFO");
            commpara.put("PARA_CODE1", newProductId);
            commpara.put("PARA_CODE4", "1");
            IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
            
            if(IDataUtil.isNotEmpty(commparaDs))
            {
            	tipsInfo = tipsInfo + "\n" + commparaDs.getData(0).getString("PARA_CODE20");
            }
 
            IData usd = UcaInfoQry.qryUserInfoByUserIdFromDB(userId,"0898");
            String sn = "KD_"+usd.getString("SERIAL_NUMBER");
            IDataset wideNet = WidenetInfoQry.getUserWidenetInfoBySerialNumber(sn);
            if(IDataUtil.isNotEmpty(wideNet))
            {
                commpara.put("PARA_CODE4", "KD");
                commpara.put("SUBSYS_CODE", "CSM");
                commpara.put("PARAM_ATTR", "5453");
                commpara.put("PARAM_CODE", "TIPINFO");
                commpara.put("PARA_CODE1", oldProductId);
                IDataset commparaDsOldKD = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                
                commpara.remove("PARA_CODE1");
                commpara.put("PARA_CODE1", newProductId);
                IDataset commparaDsNewKD = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                
                if(IDataUtil.isNotEmpty(commparaDsOldKD) && IDataUtil.isEmpty(commparaDsNewKD))
                {
                	tipsInfo = tipsInfo + "\n" + commparaDsOldKD.getData(0).getString("PARA_CODE20");
                }
            }else{
            	commpara.put("PARA_CODE4", "NKD");
            	commpara.put("SUBSYS_CODE", "CSM");
                commpara.put("PARAM_ATTR", "5453");
                commpara.put("PARAM_CODE", "TIPINFO");
                commpara.put("PARA_CODE1", newProductId);
                IDataset commparaNKD = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                
                if(IDataUtil.isNotEmpty(commparaNKD))
                {
                	tipsInfo = tipsInfo + "\n" + commparaNKD.getData(0).getString("PARA_CODE20");
                }
            }
            results.add(getTipsInfoData("2", tipsInfo, "110"));
        }

        String productTransInfo = getProductTransInfo(oldProductId, newProductId);
        if (StringUtils.isNotBlank(productTransInfo))
        {
            results.add(getTipsInfoData("2", productTransInfo, "110"));
        }

        String custVipCancelInfo = this.getCustVipCancelTipsInfo(custId, oldBrand, newBrand, newProductName);
        if (StringUtils.isNotBlank(custVipCancelInfo))
        {
            results.add(getTipsInfoData("2", custVipCancelInfo, "110"));
        }

        IData cancelSaleActiveData = new ChangeProductBean().getCancelSaleActiveTag(userId, oldBrand, newBrand, oldProductId, newProductId, bookingDate);
        if (IDataUtil.isNotEmpty(cancelSaleActiveData))
        {
            if ("1".equals(cancelSaleActiveData.getString("SALEACTIVE_CANCEL_TAG", "")))
            {
                String saleActiveTipsInfo = cancelSaleActiveData.getString("MESSAGE", "");
                if (StringUtils.isNotBlank(saleActiveTipsInfo))
                {
                    results.add(getTipsInfoData("2", saleActiveTipsInfo, "110"));
                }
            }
        }
      
        
        return results;
    }

    /**
     * 获取取消大客户提示信息
     * 
     * @param custId
     * @param oldBrand
     * @param newBrand
     * @param newProductName
     * @return
     * @throws Exception
     */
    public String getCustVipCancelTipsInfo(String custId, String oldBrand, String newBrand, String newProductName) throws Exception
    {
        String msg = "";

        IData returnCustVipData = new ChangeProductBean().getCustVipIsCancelTag(custId, oldBrand, newBrand);

        if (IDataUtil.isNotEmpty(returnCustVipData))
        {
            if ("1".equals(returnCustVipData.getString("CUST_VIP_CANCEL_TAG", "")))
            {
                msg = "您当前是【" + returnCustVipData.getString("CLASS_NAME") + "】大客户,产品变为" + newProductName + "将取消大客户资格，是否继续？";
            }
        }

        return msg;
    }

    /**
     * 获取新VPMN优惠、以及预约产品时间
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getNewVpmnDiscntBookProductDate(IData param) throws Exception
    {
        IData result = new DataMap();

        String newProductId = param.getString("NEW_PRODUCT_ID");
        String oldVpmnDiscnt = param.getString("OLD_VPMN_DISCNT", "");
        String eparchyCode = param.getString("EPARCHY_CODE");
        String bookingDate = param.getString("BOOKING_DATE", "");
        String vpmnUserIdA = param.getString("VPMN_USER_ID_A");
        String vpmnProductId = param.getString("VPMN_PRODUCT_ID");

        IData newVpmnData = new ChangeProductBean().getNewVpmnDiscnt(newProductId, oldVpmnDiscnt, eparchyCode, bookingDate, vpmnUserIdA, vpmnProductId);

        if (IDataUtil.isNotEmpty(newVpmnData))
        {
            result.putAll(newVpmnData);
        }

        // 海南预约时间处理 前台使用
        String acctDay = param.getString("ACCT_DAY");
        String firstDate = param.getString("FIRST_DATE");
        String tradeStaffId = CSBizBean.getVisit().getStaffId();

        if (StringUtils.isNotBlank(acctDay) && StringUtils.isNotBlank(firstDate))
        {
            IDataset commpara8859 = CommparaInfoQry.getCommparaInfoByCode2("CSM", "8859", "PRODUCT", newProductId, eparchyCode);
            if (IDataUtil.isNotEmpty(commpara8859) && !StaffPrivUtil.isFuncDataPriv(tradeStaffId, "SYS_CRM_CHANGEG010"))
            {
                result.put("BOOKING_PRODUCT", "TRUE");
                result.put("BOOKING_DATE", SysDateMgr.getFirstDayNextAcct(SysDateMgr.getSysDate(), acctDay, firstDate));
            }
            else
            {
                result.put("BOOKING_PRODUCT", "FALSE");
            }
        }

        IDataset results = new DatasetList();
        results.add(result);
        return results;
    }
    
    /**
     * 判断客户身上是否还有宽带1+活动
     * 
     * @param oldProductId
     * @param newProductId
     * @return
     * @throws Exception
     */
    public Boolean getWideSaleActiveInfo(IData params) throws Exception
    {
    	Boolean tag=false;
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL("select t.* from TF_F_USER_SALE_ACTIVE t where t.SERIAL_NUMBER=:SERIAL_NUMBER AND t.PRODUCT_ID=:PRODUCT_ID and t.END_DATE>sysdate and t.END_DATE>t.START_DATE"); 
        IDataset infos=  Dao.qryByParse(parser); 
        if(infos.size()>0){       
      	  tag = true;       
        }

        return tag;
    }
    
    /**
     * 获取产品转换提示信息
     * 
     * @param oldProductId
     * @param newProductId
     * @return
     * @throws Exception
     */
    public String getProductTransInfo(String oldProductId, String newProductId) throws Exception
    {
        String msg = "";

        IDataset productTransInfo = ProductInfoQry.getProductTransInfo(oldProductId, newProductId);

        if (IDataUtil.isNotEmpty(productTransInfo))
        {
            msg = productTransInfo.getData(0).getString("RSRV_STR1");
        }

        return msg;
    }

    /**
     * @Description: 该函数的功能描述
     * @param tipsType
     * @param tipsInfo
     * @param tipsCode
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 19, 2014 8:37:08 PM
     */
    public IData getTipsInfoData(String tipsType, String tipsInfo, String tipsCode) throws Exception
    {
        IData tipsInfoData = new DataMap();
        tipsInfoData.clear();

        tipsInfoData.put("TIPS_TYPE", tipsType);
        tipsInfoData.put("TIPS_INFO", tipsInfo);
        tipsInfoData.put("TIPS_CODE", tipsCode);

        return tipsInfoData;
    }
    
    public IDataset getCancelActiveInfos(IData param) throws Exception
    {
    	Boolean flag =false;
    	String serialNumber = param.getString("SERIAL_NUMBER");
        String offerCode = param.getString("NEW_PRODUCT_ID");
        IDataset results = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9921",offerCode,null);
        IData inparam=new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        if(IDataUtil.isNotEmpty(results)){
        	inparam.put("PRODUCT_ID", results.getData(0).getString("PARA_CODE1"));
            flag = getWideSaleActiveInfo(inparam);
        }
        if(flag){
        	return results;
 }else{
        	return null;
        }
        
    }
    
    public IDataset getDisctTipsInfo(IData param) throws Exception
    {
        IDataset results = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","5455",null,null);
        IDataset resultInfos = new  DatasetList();
        if(IDataUtil.isNotEmpty(results)){
         		for(int i=0;i<results.size();i++){
         	       IDataset wideNet = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_"+param.getString("SERIAL_NUMBER"));
         	       if(IDataUtil.isEmpty(wideNet)){
                       if("NKD".equals(results.getData(i).getString("PARA_CODE2"))){
                    	  resultInfos.add(results.getData(i));
             		   }
         	       }else{
         	    	   if("KD".equals(results.getData(i).getString("PARA_CODE2"))){
         	    		  resultInfos.add(results.getData(i));
              		   }
         	       }
                }
        	return resultInfos;
        }else{
        	return null;
        }
        
    }

    public IDataset loadChildInfo(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        String custId = param.getString("CUST_ID");

        String routeEparchyCode = BizRoute.getRouteId();
        String userProductName = "";
        String userProductDesc;
        String userBrandName = "";
        String userProductId = "";
        String userBrandCode = "";
        String nextProductId = "";
        String nextBrandCode = "";
        String nextBrandName = "";
        String nextProductName = "";
        String nextProductDesc;
        String nextProductStartDate = "";
        String userProductMode = "";
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
        String sysTime = SysDateMgr.getSysTime();

        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                if (userProduct.getString("START_DATE").compareTo(sysTime) < 0)
                {
                    userProductId = userProduct.getString("PRODUCT_ID");
                    userBrandCode = userProduct.getString("BRAND_CODE");
                    userProductMode = userProduct.getString("PRODUCT_MODE");
                }
                else
                {
                    nextProductId = userProduct.getString("PRODUCT_ID");
                    nextBrandCode = userProduct.getString("BRAND_CODE");
                    nextProductStartDate = userProduct.getString("START_DATE");
                    userProductMode = userProduct.getString("PRODUCT_MODE");
                }
            }
        }

        if (!StringUtils.equals("00", userProductMode) && !StringUtils.equals("15", userProductMode))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_246);
        }

        IData result = new DataMap();

        // 查询用户当前品牌名称，当前产品名称
        userBrandName = UBrandInfoQry.getBrandNameByBrandCode(userBrandCode);
        userProductName = UProductInfoQry.getProductNameByProductId(userProductId);
        userProductDesc = UProductInfoQry.getProductExplainByProductId(userProductId);
        result.put("USER_PRODUCT_NAME", userProductName);
        result.put("USER_PRODUCT_DESC", userProductDesc);
        result.put("USER_PRODUCT_ID", userProductId);
        result.put("USER_BRAND_NAME", userBrandName);

        if (!StringUtils.isBlank(nextProductId))
        {
            nextBrandName = UBrandInfoQry.getBrandNameByBrandCode(nextBrandCode);
            nextProductName = UProductInfoQry.getProductNameByProductId(nextProductId);
            nextProductDesc = UProductInfoQry.getProductExplainByProductId(nextProductId);
            result.put("NEXT_PRODUCT_NAME", nextProductName);
            result.put("NEXT_PRODUCT_DESC", nextProductDesc);
            result.put("NEXT_PRODUCT_ID", nextProductId);
            result.put("NEXT_BRAND_NAME", nextBrandName);
            result.put("NEXT_PRODUCT_START_DATE", nextProductStartDate);
        }
        result.put("EPARCHY_CODE", routeEparchyCode);

        // 海南查询用户已经办理过押金的发票号
        IDataset foregift = UserOtherInfoQry.getUserOtherservByPK(userId, "FG", "0", null);
        if (IDataUtil.isNotEmpty(foregift))
        {
            result.put("INVOICE_DATA", foregift);
        }

        // 海南查询VPMN相关信息
        IData userVpmn = new ChangeProductBean().getUserVpmnData(UcaDataFactory.getUcaByUserId(userId));
        if (IDataUtil.isNotEmpty(userVpmn))
        {
            result.putAll(userVpmn);
        }

        // 海南预约时间处理
        String acctDay = param.getString("ACCT_DAY");
        String firstDate = param.getString("FIRST_DATE");
        if (StringUtils.isNotBlank(acctDay) && StringUtils.isNotBlank(firstDate))
        {
        	IDataset commpara8859 = CommparaInfoQry.getCommparaInfoByCode2("CSM", "8859", "PRODUCT", userProductId,routeEparchyCode); 
        	if (IDataUtil.isNotEmpty(commpara8859) &&
        		 !StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_CRM_CHANGEG010")) 
        	{
        		result.put("BOOKING_PRODUCT", "TRUE");
        		result.put("BOOKING_DATE", SysDateMgr.getFirstDayNextAcct(SysDateMgr.getSysDate(), acctDay, firstDate));
        	}
        	else 
        	{
        		result.put("BOOKING_PRODUCT", "FALSE"); 
        	}
        }
         

        // 是否具备选择预约时间的权限
        if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "PROD_BOOKING_DATE"))
        {
            result.put("BOOKING_DATE_PRIV", "TRUE");
        }
        else
        {
            result.put("BOOKING_DATE_PRIV", "FALSE");
        }

        IDataset custVipInfo = CustVipInfoQry.qryVipInfoByCustId(custId);

        if (IDataUtil.isNotEmpty(custVipInfo))
        {
            String vipClassId = custVipInfo.getData(0).getString("VIP_CLASS_ID", "");

            result.put("VIP_CLASS_ID", vipClassId);
        }

        IDataset results = new DatasetList();
        results.add(result);
        return results;
    }

    /**
     * 营销活动提示信息
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public String querySaleActiveTipsInfo(String userId) throws Exception
    {
        String msg = "";

        IDataset saleActives = UserSaleActiveInfoQry.querySaleActiveByUserIdProcess(userId, "0");

        saleActives = SaleActiveUtil.filterUserSaleActivesByQyyx(saleActives);// 只要签约类的

        if (IDataUtil.isNotEmpty(saleActives))
        {
            msg += "用户参与了活动:";

            for (int i = 0; i < saleActives.size(); i++)
            {
                if (msg.length() > 0)
                {
                    msg += "<br/>";
                }

                String productName = saleActives.get(i, "PRODUCT_NAME") == null ? "营销活动" : saleActives.get(i, "PRODUCT_NAME").toString();
                String packageName = saleActives.get(i, "PACKAGE_NAME") == null ? "营销活动" : saleActives.get(i, "PACKAGE_NAME").toString();

                String startDate = saleActives.get(i, "START_DATE").toString();
                String endDate = saleActives.get(i, "END_DATE").toString();

                msg += productName + "【" + packageName + "】:" + "开始时间:" + startDate + " " + "结束时间:" + endDate;
            }
        }

        return msg;
    }
    
    public IDataset checkShareMealPhoneNum(IData param) throws Exception
    {    
    	 IDataset rs = new DatasetList();
    	 String newproductId = param.getString("NEW_PRODUCT_ID","");
         String mainSn = param.getString("SERIAL_NUMBER","");
         String sharemealOne = param.getString("SHAREMEALONE","");
         String sharemealTwo = param.getString("SHAREMEALTWO","");
         
         if("80003014".equals(newproductId) && StringUtils.isNotBlank(sharemealOne) && StringUtils.isNotBlank(sharemealTwo))
         {
         	ShareMealBean bean = new ShareMealBean();
         	IData data = new DataMap();
         	data.put("SERIAL_NUMBER", mainSn);
         	data.put("SERIAL_NUMBER_B", sharemealOne);
         	data.put("NEW_PRODUCT_ID", newproductId);
         	data.put("START_DATE", param.getString("BOOKING_DATE",SysDateMgr.getSysTime()));
         	
         	//共享副号校验
         	IDataset dsone = bean.checkAddMebIntf(data);
         	if(IDataUtil.isNotEmpty(dsone) && "-1".equals(dsone.getData(0).getString("WARM_TIPS","")))
         	{
                CSAppException.appError("2017121101", dsone.getData(0).getString("WARM_INFO",""));
         	}
         	
         	data.put("SERIAL_NUMBER_B", sharemealTwo);
         	IDataset dstwo = bean.checkAddMebIntf(data);
         	if(IDataUtil.isNotEmpty(dsone) && "-1".equals(dstwo.getData(0).getString("WARM_TIPS","")))
         	{
                CSAppException.appError("2017121102", dstwo.getData(0).getString("WARM_INFO","")); 
         	}
         	
         	
         	//统一付费办理主号校验
         	IData mainUser = UcaInfoQry.qryUserInfoBySn(mainSn);
         	if (IDataUtil.isEmpty(mainUser))
             {
                 CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
             }
         	
         	data.put("USER_ID", mainUser.getString("USER_ID"));
         	data.put("USER_STATE_CODESET", mainUser.getString("USER_STATE_CODESET"));
         	data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
         	IDataset results = CSAppCall.call("SS.FamilyUnionPaySVC.loadChildTradeInfo", data);
         	
         	if(IDataUtil.isNotEmpty(results))
         	{
         		IData map = results.getData(0).getData("FAM_PARA");
         		IDataset memberSun = results.getData(0).getDataset("QRY_MEMBER_LIST");
         		if(IDataUtil.isNotEmpty(memberSun))
         		{
         			if(memberSun.size() + 2 > Integer.parseInt(map.getString("MEB_LIM")))
             		{
                        CSAppException.appError("2017121103", "您的统付成员号码数量已经达到" +map.getString("MEB_LIM")+ "个的上限，不能再增加成员号码！");  
             		}
         		}
         	}
         	
         	//统一付费办理副号校验
         	data.put("MAIN_SERIAL_NUMBER", mainSn);
         	data.put("CHECK_SERIAL_NUMBER", sharemealOne);
         	IDataset resultsone = CSAppCall.call("SS.FamilyUnionPaySVC.checkBySerialNumber", data);
         	
         	data.put("CHECK_SERIAL_NUMBER", sharemealTwo);
         	IDataset resultstwo = CSAppCall.call("SS.FamilyUnionPaySVC.checkBySerialNumber", data);
         	
         	
         	//办理统付业务预登记
         	IDataset  memberdatas = new DatasetList();
         	IData membone = new DataMap();
         	IData membtwo = new DataMap();
         	membone.put("SERIAL_NUMBER_B", sharemealOne);
         	membone.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
         	membone.put("END_DATE", "2050-12-31 23:59:59");
         	membone.put("MODIFY_TAG", "0");
         	memberdatas.add(membone);
         	
         	membtwo.put("SERIAL_NUMBER_B", sharemealTwo);
         	membtwo.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
         	membtwo.put("END_DATE", "2050-12-31 23:59:59");
         	membtwo.put("MODIFY_TAG", "0");
         	memberdatas.add(membtwo);
         	
         	IData tradeData = new DataMap();
         	tradeData.put("SERIAL_NUMBER", mainSn);
         	tradeData.put("AUTH_SERIAL_NUMBER", mainSn);
         	tradeData.put("MEMBER_DATAS", memberdatas);
         	tradeData.put("ORDER_TYPE_CODE", "325");
         	tradeData.put("TRADE_TYPE_CODE", "325");
         	tradeData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
         	tradeData.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
         	IDataset dataset = CSAppCall.call("SS.FamilyUnionPayRegSVC.tradeReg", tradeData);
         	
         	if(IDataUtil.isNotEmpty(dataset) && StringUtils.isNotBlank(dataset.getData(0).getString("TRADE_ID")))
         	{
         		IData tparam = new DataMap();

         		tparam.put("TRADE_ID", dataset.getData(0).getString("TRADE_ID"));

                String sql = "DELETE FROM TP_F_UNIONPAY_MEMBER WHERE TRADE_ID=:TRADE_ID ";

                Dao.executeUpdate(new StringBuilder(sql), tparam);
         	}
         	
         	return dataset;
         }
         
         return rs;
    }

    
    public IData getWideRatTips(IData param) throws Exception
    {    
    	 IDataUtil.chkParam(param, "NEW_PRODUCT_ID"); 
    	 IDataUtil.chkParam(param, "SERIAL_NUMBER");
    	 
    	 String newProductId = param.getString("NEW_PRODUCT_ID");
    	 String serialNumber = param.getString("SERIAL_NUMBER");
    	 
    	 String widenetSerialNum = "";
     	 String phoneSerialNum = "";
    	 
    	 if (serialNumber.startsWith("KD_"))
 		 {
 			widenetSerialNum = serialNumber;
 			phoneSerialNum = serialNumber.substring(3);
 		 }
 		 else
 		 {
 			widenetSerialNum = "KD_" + serialNumber;
 			phoneSerialNum = serialNumber;
 		 }
    	 
    	 IData resultData = new DataMap();
    	 resultData.put("X_RESULTCODE", "2998");
 		
 		 IData userInfo = UcaInfoQry.qryUserInfoBySn(widenetSerialNum);
 		 if (IDataUtil.isNotEmpty(userInfo))
         {
 			 
 			 String userId = userInfo.getString("USER_ID");
 	 		 //用户宽带速率
 	     	 String userRate = this.getWideRate(userId);
 	     	 
 	    	 IDataset commparaInfos778 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","778",newProductId,null);
 	    	 if(IDataUtil.isNotEmpty(commparaInfos778)){
 	    		 String maxRate = commparaInfos778.first().getString("PARA_CODE2","0");
 	    		 
 	    		 IDataset dataset = WidenetInfoQry.getUserWidenetInfo(userId);
 	    		 if (IDataUtil.isNotEmpty(dataset))
 	    		 {
 	    			 String  widetype = dataset.getData(0).getString("RSRV_STR2");
 				     if ("1".equals(widetype)||"6".equals(widetype))//判断是否FTTB
 			         {
 			    		if(new BigDecimal(maxRate).compareTo(new BigDecimal("50"))<0){
 			    			 resultData.put("X_RESULTCODE", "0000");
 			    			 resultData.put("X_RESULTINFO", "用户当前宽带速率与所选择的新套餐不匹配，建议前往宽带产品变更界面为用户同期变更宽带产品（宽带速率）。");
 			    		}
 			         }else{
 			        	 
 			        	 if(new BigDecimal(userRate).compareTo(new BigDecimal(maxRate))>0){
 			        		 resultData.put("X_RESULTCODE", "0000");
 			    			 resultData.put("X_RESULTINFO", "用户当前宽带速率与所选择的新套餐不匹配，建议前往宽带产品变更界面为用户同期变更宽带产品（宽带速率）。");
 			    		 } 
 			         }
 	    		 }
 	    	 }else{//变更为非融合套餐
 	 			 IData userInfo2 = UcaInfoQry.qryUserInfoBySn(phoneSerialNum);
 	 	    	 if (IDataUtil.isNotEmpty(userInfo2))
 	 	         {
 	 	    		  System.out.println("坐标2020022101");
 	 	    		  String userID_SN = userInfo2.getString("USER_ID");
 	 	    		  IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userID_SN);
	 	 	          String sysTime = SysDateMgr.getSysTime();
	 	 	          String userProductId ="";
	 	 	          if (IDataUtil.isNotEmpty(userMainProducts))
	 	 	          {
	 	 	              int size = userMainProducts.size();
	 	 	              for (int i = 0; i < size; i++)
	 	 	              {
	 	 	                  IData userProduct = userMainProducts.getData(i);
	 	 	                  if (userProduct.getString("START_DATE").compareTo(sysTime) < 0)
	 	 	                  {
	 	 	                      userProductId = userProduct.getString("PRODUCT_ID");
	 	 	                  }
	 	 	              }
	 	 	              System.out.println("坐标2020022102"+userProductId);
	 	 	          }	
	 	 	          
	 	 	          if(StringUtils.isNotBlank(userProductId)){
	 	 	        	IDataset commparaInfos = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","778",userProductId,null);
	 	 	            if(IDataUtil.isNotEmpty(commparaInfos)){
	 	 	            	 System.out.println("坐标2020022103");
	 	 	               IData queryParam = new DataMap();
	 	 	               queryParam.put("SERIAL_NUMBER_A", param.getString("SERIAL_NUMBER"));
	 	 	               queryParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
	 	 	               queryParam.put("TRADE_TYPE_CODE", "601");
	 	 	               //用户当前的宽带营销活动
	 	 	               IDataset userActives = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", queryParam);
	 	 	               if (IDataUtil.isEmpty(userActives))
		 	 	           {
	 	 	            	 resultData.put("X_RESULTCODE", "0000");
 			    			 resultData.put("X_RESULTINFO", "用户当前宽带速率与所选择的新套餐不匹配，建议前往宽带产品变更界面为用户同期变更宽带产品（宽带速率）。");
		 	 	           }
	 	 	        		 
	 	 	        	}
	 	 	        	
	 	 	          }
 	 	         }

 	    	 }
         }
 		 
    	 return resultData;
    }	
    
    public String getWideRate(String userId) throws Exception
    {   
    	//用户宽带速率
    	String userRate = "0";
    	
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
		 
		 if (IDataUtil.isNotEmpty(userMainProducts))
		  {
	    	String sysDate = SysDateMgr.getSysTime();
	    	String productId = "";
	    	
	    	int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                if (userProduct.getString("START_DATE").compareTo(sysDate) < 0)
                {
                	productId = userProduct.getString("PRODUCT_ID");
                }
            }
            if(StringUtils.isBlank(productId)){
            	return userRate;
            }
            
            //查询用户宽带产品速率
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
                            break;
                        }
                    }
                }
                
                if (IDataUtil.isNotEmpty(rate_ds))
                {
                	userRate = rate_ds.getData(0).getString("PARA_CODE1","0");
                }
            }
	          
		  }
    	
    	return userRate;
    }


    public IDataset checkProPrice(IData param) throws Exception {
        String flag = "0"; //默认不提示
        String oldOfferCode = param.getString("USER_PRODUCT_ID");
        String newOfferCode = param.getString("NEW_PRODUCT_ID");
        IDataset discntDataset = new DatasetList(param.getString("SELECTED_ELEMENTS"));


        //更换主套餐
        if(!StringUtils.isEmpty(oldOfferCode) && !StringUtils.isEmpty(newOfferCode)) {
            BigDecimal oldPrice = new BigDecimal("0");
            BigDecimal newPrice = new BigDecimal("0");
            //原套餐价格
            IData params = new DataMap();
            params.put(Route.ROUTE_EPARCHY_CODE, "0898");
            params.put("PRODUCT_ID", oldOfferCode);
            IDataset oldProductAmts= CSAppCall.call("SS.CancelWholeNetCreditPurchasesSVC.getProductAmt", params);
            if(IDataUtil.isNotEmpty(oldProductAmts)){
                String oldAmt = oldProductAmts.first().getString("AMT");
                oldPrice = oldPrice.add(new BigDecimal(oldAmt));
            }
            //新套餐价格
            params.clear();
            params.put(Route.ROUTE_EPARCHY_CODE, "0898");
            params.put("PRODUCT_ID", newOfferCode);
            IDataset newProductAmts= CSAppCall.call("SS.CancelWholeNetCreditPurchasesSVC.getProductAmt", params);
            if(IDataUtil.isNotEmpty(newProductAmts)){
                String newAmt = newProductAmts.first().getString("AMT");
                newPrice = newPrice.add(new BigDecimal(newAmt));
            }

            if(oldPrice.doubleValue() == 0 || newPrice.doubleValue() == 0){
                flag = "0";
            }else if(oldPrice.doubleValue() > newPrice.doubleValue()){
                flag = "1";
            }
        }else if(!StringUtils.isEmpty(oldOfferCode) && StringUtils.isEmpty(newOfferCode)){

            IDataset oldProductElements = UpcCall.queryOfferComRelOfferByOfferIdRelOfferType("P",oldOfferCode,"D","0898");
            //产品的构成是否为空
            if(IDataUtil.isNotEmpty(oldProductElements)){
                flag = "0";
            }else {
                //类似4G自选套餐
                //必选优惠
                IDataset offerList = null;

                //产品构成为空就查必选组优惠
                IDataset groupList = UpcCall.queryOfferGroups(oldOfferCode);
                for(Object temp :groupList){
                    IData data1 = (IData)temp;
                    String selectFlag = data1.getString("SELECT_FLAG");
                    //如果是必选组
                    if("0".equals(selectFlag)){
                        offerList = UpcCall.queryGroupComRelOfferByGroupId(data1.getString("GROUP_ID"), "");
                        //产品变更更换的产品
                        if(IDataUtil.isNotEmpty(discntDataset)) {
                            BigDecimal oldPrice = new BigDecimal("0");
                            BigDecimal newPrice = new BigDecimal("0");
                            for (Object temp2: discntDataset) {
                                IData data2 = (IData)temp2;
                                String modifyTag = data2.getString("MODIFY_TAG");
                                String productId = data2.getString("ELEMENT_ID");
                                //新套餐价格
                                if("0".equals(modifyTag)) {
                                    for(Object temp3 :offerList){
                                        IData data3 = (IData)temp3;
                                        String discntCode = data3.getString("OFFER_CODE");
                                        if(productId.equals(discntCode)) {
                                            IData map = new DataMap();
                                            map.put("PRODUCT_OFFERING_ID", discntCode);
                                            IDataset returnList = AcctCall.productOfferingConfig(map);
                                            if(IDataUtil.isNotEmpty(returnList)){
                                                for(int j = 0; j < returnList.size(); j++){
                                                    String type = returnList.getData(j).getString("type");
                                                    if("Z".equals(type)){
                                                        String discntAmt =returnList.getData(j).getString("busiprice","0");
                                                        newPrice = newPrice.add(new BigDecimal(discntAmt));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //旧套餐价格
                                if("1".equals(modifyTag)) {
                                    for(Object temp3 :offerList){
                                        IData data3 = (IData)temp3;
                                        String discntCode = data3.getString("OFFER_CODE");
                                        if(productId.equals(discntCode)) {
                                            IData map = new DataMap();
                                            map.put("PRODUCT_OFFERING_ID", discntCode);
                                            IDataset returnList = AcctCall.productOfferingConfig(map);
                                            if(IDataUtil.isNotEmpty(returnList)){
                                                for(int j = 0; j < returnList.size(); j++){
                                                    String type = returnList.getData(j).getString("type");
                                                    if("Z".equals(type)){
                                                        String discntAmt =returnList.getData(j).getString("busiprice","0");
                                                        oldPrice = oldPrice.add(new BigDecimal(discntAmt));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(oldPrice.doubleValue() == 0 || newPrice.doubleValue() == 0){
                                flag = "0";
                            }else if(oldPrice.doubleValue() > newPrice.doubleValue()){
                                flag = "1";
                            }
                        }
                    }
                }


            }

        }


        IDataset  dataset = new DatasetList();
        IData map = new DataMap();
        map.put("flag", flag);
        dataset.add(map);
        return dataset;
    }
}
