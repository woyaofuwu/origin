
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;

public class WidenetInfoQry
{

    /**
     * @param serialNumber
     * @return
     * @throws Exception
     * @author chenzm
     */
    public static IDataset getUserInfo(String serialNumber) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_B_TRADE_WIDENETHN", "SEL_WIDEUSER_BY_SN", inparam);
    }

    /**
     * 通过acct_id查找宽带账户资料
     * 
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IDataset getUserWidenetActInfos(String acctId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ACCT_ID", acctId);
        return Dao.qryByCode("TF_F_USER_WIDENET_ACT", "SEL_BY_ACCT_ID", inparam);
    }

    /**
     * 通过acct_id查找宽带账户资料
     * 
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IDataset getUserWidenetActInfosByUserid(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_WIDENET_ACT", "SEL_BY_USERID", inparam);
    }

    /**
     * @param userId
     * @return
     * @throws Exception
     * @author chenzm
     */
    public static IDataset getUserWidenetInfo(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_BY_USERID", inparam);
    }

    /**
     * @param serialNumber
     * @return
     * @throws Exception
     * @author chenzm
     */
    public static IDataset getUserWidenetInfoBySerialNumber(String serialNumber) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_BY_SERIAL_NUMBER_WIDENET", inparam);
    }

    /**
     * 通过acct_id查找宽带账户资料
     * 
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IDataset getWidenetActInfosByUserid(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_WIDENET_ACT", "SEL_BY_USERID_WIDENET_ACT", inparam);
    }

    /**
     * 通过user_id查找宽带资料
     * 
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IDataset getWidenetInfosByUserId(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_BY_USERID_WIDENET", inparam);
    }
    /**
     * 通过user_id查看是否有未完工单add yangsh
     */
    public static IDataset getTradeListByUserId(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE", "SEL_BY_USERID", inparam,Route.getJourDbDefault());
    }
    /**
     * 预约业务重就绪改造
     * @param userId
     * @param timePoint
     * @return
     * @author tangxy
     * @throws Exception
     */
    public static IDataset getWidenetInfoByUserIdDate(String userId, String timePoint) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("TIME_POINT", timePoint);
        return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_BY_USERID_DATE", inparam);
    }
    
    /**
     * 预约业务重就绪改造
     * @param userId
     * @param timePoint
     * @return
     * @author tangxy
     * @throws Exception
     */
    public static IDataset getWidenetActInfoByUserIdDate(String userId, String timePoint) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("TIME_POINT", timePoint);
        return Dao.qryByCode("TF_F_USER_WIDENET_ACT", "SEL_BY_USERID_DATE", inparam);
    }
    
    
    
    /**
     * 根据CUST_ID、产品编码以及号码查询该客户订购的商务宽带中最大的号码
     * 
     * @param data
     * @return
     * @throws Exception
     * @author liaoyi
     */    
    public static IDataset getMaxNetSN(String cust_id, String productId, String serial_number, Pagination pagination) throws Exception
    {

        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        //param.put("PRODUCT_ID", productId);
        param.put("SERIAL_NUMBER", serial_number);
        
    	SQLParser parser = new SQLParser(param);

    	parser.addSQL(" SELECT max(SERIAL_NUMBER) MAX_SERIAL_NUMBER FROM TF_F_USER WHERE CUST_ID =:CUST_ID ");
    	parser.addSQL(" AND SERIAL_NUMBER like 'KD_' || :SERIAL_NUMBER || '%' ");
    	
    	return Dao.qryByParse(parser, pagination);
    }
    
    
    /**
     * 根据CUST_ID、产品编码以及号码查询该客户订购的商务宽带中最大的号码
     * 
     * @param data
     * @return
     * @throws Exception
     * @author yuyj3
     */    
    public static IDataset getTradeMaxNetSN(String cust_id, String productId, String serial_number, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        //param.put("PRODUCT_ID", productId);
        param.put("SERIAL_NUMBER", serial_number);
        
        SQLParser parser = new SQLParser(param);
        
        parser.addSQL(" SELECT max(SERIAL_NUMBER) MAX_SERIAL_NUMBER FROM TF_B_TRADE WHERE CUST_ID =:CUST_ID ");
        parser.addSQL(" AND SERIAL_NUMBER like 'KD_' || :SERIAL_NUMBER || '%' ");
        
        return Dao.qryByParse(parser, pagination, Route.getJourDbDefault());

    }
	
	/**
     * 根据号码查询该客户订购的商务宽带中最大的号码
     * @author yanwu 2017-01-20
     * @param user_Id
     * @return
     * @throws Exception
     */
    public static IDataset getBatMaxNetSN(String serial_number) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serial_number);
        return Dao.qryByCode("TF_B_TRADE_BATDEAL", "SEL_BATDEAL_BY_SN", inparam, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    
    /**
     * @param serialNumber
     * @return
     * @throws Exception
     * @author chenzm
     */
    public static IDataset getKVUserInfo(String serialNumber) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_B_TRADE_WIDENETHN", "SEL_WIDE_KV_USER_BY_SN", inparam);
    }
    
    /**
     * 查询
     * @param userId
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static boolean checkUserIsWideNetUser(String userId, String serialNumber)
    	throws Exception{
    	IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("USER_ID", userId);

        IDataset set=Dao.qryByCode("TF_B_TRADE_WIDENETHN", "CHECK_USER_ID_IS_WIDE_USER", inparam);
        if(IDataUtil.isNotEmpty(set)){
        	int totalNumber=set.getData(0).getInt("TOTAL_NUMBER");
        	if(totalNumber>0){
        		return true;
        	}else{
        		return false;
        	}
        }else{
        	return false;
        }
    }
    /**
     * 查询用户的宽带预约拆机记录
     * @author kangyt 2016-4-13
     * @param user_Id
     * @return
     * @throws Exception
     */
    public static IDataset getWidenetOrderDestroyInfo(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_GPON_DESTROY", "SEL_DESTORY_ORDER_BY_USERID", inparam);
    }
    /**
     * 根据宽带类型查询宽带产品信息，含速率
     * @author yuyj3 2016-5-3
     * @param user_Id
     * @return
     * @throws Exception
     */
    public static IDataset getWidenetProduct_RATE(String productMode, String eparchyCode) throws Exception
    {
        IDataset widenetProductInfos = ProductInfoQry.getWidenetProductInfo(productMode, eparchyCode);
        
        if (IDataUtil.isNotEmpty(widenetProductInfos))
        {
            IData widenetProductInfo = null;
            
            for (int i = 0; i < widenetProductInfos.size(); i++)
            {
                widenetProductInfo = widenetProductInfos.getData(i);
                
                IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, widenetProductInfo.getString("PRODUCT_ID"), "1", "1");
                
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
                        widenetProductInfo.put("WIDE_RATE", rate_ds.getData(0).getString("PARA_CODE1",""));
                        widenetProductInfo.put("WIDE_RATE_SPECIAL", rate_ds.getData(0).getString("PARA_CODE4",""));
                    }
                }
            }
        }
        
        return widenetProductInfos;
    }

    /**
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getCommQryInfo(IData input) throws Exception
    {
        String tabName = input.getString("TAB_NAME");
        String sqlRef = input.getString("SQL_REF");
        return Dao.qryByCode(tabName, sqlRef, input);
    }
/*
	* @param userId
     * @return
     * @throws Exception
     * @author chenzm
     */
    public static IDataset getUserWidenetYearInfo(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_BY_USERID_YEAR", inparam);
    }
    /**
     * @author kangyt
     * 根据产品id，查询宽带对应的速率
     * */
    public static IDataset queryWidenetRateByProductID(String productId) throws Exception
    {
    	IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productId);

        return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_RATE_BY_PRODUCTID", inparam);
    }
    
    /**
     * 查询近三个月已办理过宽带业务信息
     * @param data
     * @return
     * @throws Exception
     */
    public  static IDataset getWidenetThreeMonthInfo(IData  inparam) throws Exception{
    	return  Dao.qryByCode("TF_F_USER", "SEL_USERINFO_BY_SERIAL_NUMBER", inparam);
    }
    
    /**
     * 通过用户id查询TF_F_USER_OTHER信息
     * @return
     * @throws Exception
     */
    public static IDataset  getUserOtherInfoByUserId(String userid)throws Exception{
         IData inparam=new DataMap();
         inparam.put("USER_ID", userid);
         inparam.put("RSRV_VALUE_CODE", "WIDENET_BOOK");
         
    	return  Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USRID_RSRV_VALUE", inparam);
    }
    
    /**
     * 通过手机号码（KD_开头的）查询台帐信息
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset  getTradeInfoBySerialNumber(String  serialNumber)throws Exception{
        IData inparam=new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("TRADE_TYPE_CODE", "600");
   	   return  Dao.qryByCode("TF_B_TRADE", "SEL_BY_TRADETYPECODE_SN", inparam, Route.getJourDb());
    }
    
    
    public static IDataset  getWindenetMove(String  serialNumber, String user_id)throws Exception{
        IData inparam=new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("USER_ID", user_id);
   	   return  Dao.qryByCode("TF_F_USER_WIDENET_MOVE", "SEL_BY_WIDENET_MOVE", inparam, Route.getJourDb());
    }
    
    
}
