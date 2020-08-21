package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class SaleActiveCheckProdByProcedSVC extends CSBizService
{
    /**
     *-- MODE 选择ＰＲＯＤＵＣＴ的事件
      -- ATTR 选择ＰＡＣＫＡＧＲ的事件
      -- TRADE 登记后事件
      -- NoB 校验被赠送号码的开户时间
      -- CHKPKG 只做活动包级别校验，不做活动产品级别校验
      -- MCHECK 选择ＰＲＯＤＵＣＴ的事件查询营销活动满足条件
      -- MCHKPKG 只做活动包级别校验，不做活动产品级别校验
      
    * @Title: checkSaleActiveProdByProced    
    * @Description: 存储过程改成JAVA：P_CSM_CHECKFORSALEACTIVE    
    * @param @param input
    * @param @return
    * @param @throws Exception        
    * @return IData        
    * @throws 
    * @author longtian3
     */
    public IData checkSaleActiveProdByProced(IData input) throws Exception
    {
        IData result = new DataMap();
        
        String eventType = input.getString("V_EVENT_TYPE");
        String eparchyCode = input.getString("V_EPARCHY_CODE");
        String cityCode = input.getString("V_CITY_CODE");
        String departId = input.getString("V_DEPART_ID");
        String staffId = input.getString("V_STAFF_ID");
        
        String userId = input.getString("V_USER_ID");
        String depositGiftId = input.getString("V_DEPOSIT_GIFT_ID");
        String productId = input.getString("V_PURCHASE_MODE");
        String packageId = input.getString("V_PURCHASE_ATTR");
        String tradeId = input.getString("V_TRADE_ID");
        
        String checkInfo = input.getString("V_CHECKINFO");
        String saleType = input.getString("V_SALE_TYPE");
        String vipTypeId = input.getString("V_VIP_TYPE_ID");
        String vidClassId = input.getString("V_VIP_CLASS_ID");
        
        String resultCode = "0";
        String resultInfo = "TradeOk!";
        
        StringBuilder resultMsg = new StringBuilder();
        
        IData productInfo = null;
        IData pkgExtInfo = null;
        
//        if(!"TRANS".equals(saleType))
//        {
//            if(!"-1".equals(productId))
//            {
//                productInfo = UProductInfoQry.qrySaleActiveProductByPK(productId);
//                if(IDataUtil.isEmpty(productInfo))
//                {
//                    if("MCHECK".equals(eventType))
//                    {
//                        resultMsg.append("^").append("-40001::").append("产品已经下线，不能办理！");
//                    }else
//                    {
//                        result.put("V_RESULTCODE", "-20101");
//                        result.put("V_RESULTINFO", "产品已经下线，不能办理！");
//                        return result;
//                    }
//                }
//            }
//            
//            if(!"-1".equals(packageId))
//            {
//                pkgExtInfo = UPackageExtInfoQry.queryPkgExtInfoByPackageId(packageId);
//                if(IDataUtil.isEmpty(pkgExtInfo))
//                {
//                    if("MCHECK".equals(eventType))
//                    {
//                        resultMsg.append("^").append("-40002::").append("包已经下线，不能办理！");
//                    }else
//                    {
//                        result.put("V_RESULTCODE", "-20102");
//                        result.put("V_RESULTINFO", "包已经下线，不能办理！");
//                        return result;
//                    }
//                }
//            }
//            
//            IDataset paraInfos = null;
//            if(!StringUtils.equals(productId, "-1"))
//            {
//                paraInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "157", "0", productId, eparchyCode);
//            }
//            
//            if(!StringUtils.equals(packageId, "-1") && IDataUtil.isEmpty(paraInfos))
//            {
//                paraInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "157", "1", packageId, eparchyCode);
//            }
//            
//            //只做包级别校验，不做活动产品级别校验
//            if(StringUtils.equals(eventType, "CHKPKG") || StringUtils.equals(eventType, "MCHKPKG"))
//            {
//                checkPackage(pkgExtInfo, input, resultMsg);
//            }
//            
//            //1.特殊事件校验
//            if(StringUtils.equals(eventType, "NoB"))
//            {
//                
//            }
//            
//            //2.所有的公用校验
//            
//            
//            //3.选择产品后的校验
//            if(StringUtils.equals(eventType, "MODE") || StringUtils.equals(eventType, "TRADE") || StringUtils.equals(eventType, "MCHECK"))
//            {
//                
//            }
//        }
        
        result.put("V_RESULTCODE", resultCode);
        result.put("V_RESULTINFO", resultInfo);
        return result;
    }
    
    //4.选择包后的校验
    private IData checkPackage(IData pkgExtInfo, IData input, StringBuilder resultMsg) throws Exception
    {
        String eventType = input.getString("V_EVENT_TYPE");
        String userId = input.getString("V_USER_ID");
        String productId = input.getString("V_PURCHASE_MODE");
        
        IData result = new DataMap();
        
        if(StringUtils.equals(eventType, "ATTR") || StringUtils.equals(eventType, "TRADE") || StringUtils.equals(eventType, "CHKPKG") || StringUtils.equals(eventType, "MCHKPKG"))
        {
            //4.1.判断是否大客户才能办理
            String condFactor2 = pkgExtInfo.getString("COND_FACTOR2");
            if(StringUtils.equals(condFactor2, "1"))
            {
                IData param = new DataMap();
                param.put("USER_ID", userId);
                
                StringBuilder sb1 = new StringBuilder();
                sb1.append(SQL_GRPMEMBER);
                IDataset grpMember = Dao.qryBySql(sb1, param);
                
                StringBuilder sb2 = new StringBuilder();
                sb2.append(SQL_CUST_VIP);
                IDataset custVip = Dao.qryBySql(sb2, param);
                
                if(IDataUtil.isEmpty(grpMember) && IDataUtil.isEmpty(custVip))
                {
                    if(StringUtils.equals(eventType, "MCHKPKG"))
                    {
                        resultMsg.append("^").append("-40067::").append("只有集团客户或者VIP大客户才能办理改业务!");
                    }else
                    {
                        result.put("V_RESULTCODE", "-20042");
                        result.put("V_RESULTINFO", "只有集团客户或者VIP大客户才能办理改业务!");
                        return result;
                    }
                }
            }
            
            //4.2.PRODUCT_ID = 69900204 的特殊判断
            if(StringUtils.equals(productId, "69900204"))
            {
                int rsrvStr8 = pkgExtInfo.getInt("RSRV_STR8");
                if(rsrvStr8 > 2)
                {
                    IData param = new DataMap();
                    param.put("USER_ID", userId);
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append(SQL_CUST_GROUPMEMBER);
                    IDataset infos = Dao.qryBySql(sb, param);
                    
                    if(IDataUtil.isEmpty(infos))
                    {
                        if(StringUtils.equals(eventType, "MCHKPKG"))
                        {
                            resultMsg.append("^").append("-40068::").append("非AB类集团用户不能办理本约定消费方式购机！");
                        }else
                        {
                            result.put("V_RESULTCODE", "-20043");
                            result.put("V_RESULTINFO", "非AB类集团用户不能办理本约定消费方式购机！");
                            return result;
                        }
                    }
                    
                    
                }
            }
        }
        
        return result;
    }
    
    private IData  checkCommon(IData paraInfos, IData input) throws Exception
    {
        String userId = input.getString("V_USER_ID");
        
        //TODO:P_CSM_CHECKPURCHASETRADE 未完待续
        
        IData param = new DataMap();
        param.put("USER_ID", userId);
        
        if(IDataUtil.isNotEmpty(paraInfos))
        {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM TF_F_USER_SALE_ACTIVE WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) AND USER_ID = :USER_ID AND PROCESS_TAG = '0' AND NVL(RSRV_DATE2,END_DATE) > SYSDATE");
            IDataset infos = Dao.qryBySql(sql, param);
        }else
        {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM TF_F_USER_SALE_ACTIVE WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) AND USER_ID = :USER_ID AND PROCESS_TAG = '0' AND END_DATE > SYSDATE");
            IDataset infos = Dao.qryBySql(sql, param);
        }
        return null;
    }
    
    private IData  checkSpec() throws Exception
    {
        return null;
    }
    
    private final static String SQL_GRPMEMBER = "SELECT * FROM TF_F_CUST_GROUPMEMBER WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) AND USER_ID = :USER_ID AND REMOVE_TAG = '0'";
    
    private final static String SQL_CUST_VIP = "SELECT * FROM TF_F_CUST_VIP WHERE USER_ID = :USER_ID AND VIP_TYPE_CODE = '0' AND INSTR('1234', VIP_CLASS_ID) > 0 AND REMOVE_TAG = '0'";
    
    private final static String SQL_CUST_GROUPMEMBER = "SELECT A.* FROM TF_F_CUST_GROUPMEMBER A, TF_F_CUST_GROUP B WHERE A.USER_ID = :USER_ID AND A.REMOVE_TAG = '0' AND A.GROUP_ID = B.GROUP_ID AND B.REMOVE_TAG = '0' AND B.CLASS_ID IN ('5', '6', '7', '8')";
}
