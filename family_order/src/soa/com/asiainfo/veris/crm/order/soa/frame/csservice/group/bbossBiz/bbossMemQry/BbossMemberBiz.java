package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossMemQry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchMebDisQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

/**
 * 
 * @author chenyi
 * 2015-2-2
 * 4.11. 成员订购查询接口
 *
 */
public class BbossMemberBiz{

    public static IDataset  getOrderInfo(IData map) throws Exception {
        IDataset retDataset = new DatasetList();
        String productOfferid=map.getString("PRODUCT_ID");//集团订购关系
        String productSpecNum=map.getString("PRODUCT_SPEC_NUMBER");//集团规格编码
        String serial_number=map.getString("SERIAL_NUMBER");//手机号
        String stat_month=map.getString("STAT_MONTH");//查询账期
        
        String productID=GrpCommonBean.merchToProduct(productSpecNum, 2, null);//本地产品编码
        //1-查询手机路由
        String routeEparchCode = RouteInfoQry.getEparchyCodeBySnForCrm(serial_number);
        
        //2-获取成员用户id
        IData userInfo=UcaInfoQry.qryUserInfoBySn(serial_number, routeEparchCode);
        if(IDataUtil.isEmpty(userInfo)){
             CSAppException.apperr(CrmUserException.CRM_USER_552);
        }
        String userId=userInfo.getString("USER_ID");
        //3-拼装报文头
        IData rspResult = new DataMap();
        rspResult.put("SEQ_NO", map.getString("SEQ_NO"));
        rspResult.put("X_RESULTCODE","0");
        rspResult.put("X_RESULTINFO","OK");
        
        //4-判读是否有成员订购信息
        IDataset mebInfoDataset=isExistRelaInfo(productOfferid, userId, productID, stat_month);
        if(IDataUtil.isNotEmpty(mebInfoDataset))
        {
            //4.1-拼装订购节点信息
            makMembOrdInfo(retDataset, productSpecNum, userId, productID, mebInfoDataset, map.getString("SEQ_NO"), serial_number,stat_month,productOfferid);
        }
        else
        {
            // 4.2.1-判断无订购关系叠加包
            IDataset package_code = new DatasetList();
            IDataset param_value = new DatasetList();
            IDataset eff_date = new DatasetList();
            IDataset exp_date = new DatasetList();
            IDataset order_type = new DatasetList();
            String eff_dateStr = SysDateMgr.decodeTimestamp(stat_month, SysDateMgr.PATTERN_STAND);
            String exp_dateStr = SysDateMgr.getAddMonthsLastDay(0, eff_dateStr); 

            fluxRefuelPackage(userId, package_code, param_value, eff_date, exp_date, order_type, eff_dateStr, exp_dateStr, productOfferid);
            
               //判断是否存在有效订购信息
            if(IDataUtil.isNotEmpty(order_type))
            {
                // for (int i = 0,sizeI = order_type.size(); i < sizeI; i++)
                //{
                    IData tmpRsp = new DataMap();
                    tmpRsp.put("SERIAL_NUMBER",serial_number);
                    tmpRsp.put("SEQ_NO", map.getString("SEQ_NO"));
                    tmpRsp.put("X_RESULTCODE","0");
                    tmpRsp.put("X_RESULTINFO","OK");
                    tmpRsp.put("RES_CODE","0");
                    tmpRsp.put("PACKAGE_CODE", package_code);
                    tmpRsp.put("PARAM_VALUE", param_value);
                    tmpRsp.put("EFF_DATE", eff_date);
                    tmpRsp.put("EXP_DATE", exp_date);
                    tmpRsp.put("ORDER_TYPE", order_type);
                    tmpRsp.put("X_RECORDNUM", order_type.size());
                    retDataset.add(tmpRsp);
              //  }
                
            }
            else
            {
                //4.2.2-无有效成员
                rspResult.put("RES_CODE","1");
                rspResult.put("SERIAL_NUMBER",serial_number);
                rspResult.put("SEQ_NO", map.getString("SEQ_NO"));
                rspResult.put("X_RESULTCODE","0");
                rspResult.put("X_RESULTINFO","OK");
                retDataset.add(rspResult);
            }
        }
        return retDataset;
    }
    /**
     *chenyi
     *2015-2-3
     *组装订单信息
     * @param rspResult
     * @param productSpecNum
     * @param user_id
     * @throws Exception 
     */
    private static void makMembOrdInfo(IDataset rspData,String productSpecNum,String user_id,String productID,IDataset mebInfoDataset, String seqNumber, String sn,String stat_month, String productOfferid) throws Exception 
    {
        //统付业务
         if("99904".equals(productSpecNum)||"99905".equals(productSpecNum)||
                    "99908".equals(productSpecNum) || "99909".equals(productSpecNum))
         {
             getCenpayOrInfo(rspData,mebInfoDataset,user_id,productID,seqNumber,sn,stat_month, productOfferid);
         }
         
         //集团客户一点支付
         if("99902".equals(productSpecNum)||"99903".equals(productSpecNum)){
             getOnePayOrdInfo(user_id,rspData,mebInfoDataset,seqNumber,sn,stat_month);
         }
    }
    
    /**
     * 统付业务
     * @param rspData
     * @param mebInfoDataset
     * @param userId
     * @param productId
     * @throws Exception
     */
     private static void getCenpayOrInfo(IDataset rspData,IDataset mebInfoDataset,String userId,String productId,String seqNumber,String sn,String stat_month, String productOfferid) throws Exception 
     {
         IDataset package_code = new DatasetList();
         IDataset param_value = new DatasetList();
         IDataset eff_date = new DatasetList();
         IDataset exp_date = new DatasetList();
         IDataset order_type = new DatasetList();
         String eff_dateStr = SysDateMgr.getDateForYYYYMMDD(mebInfoDataset.getData(0).getString("START_DATE"));
         String exp_dateStr = SysDateMgr.getDateForYYYYMMDD(mebInfoDataset.getData(0).getString("END_DATE"));
         
         //1-如果个人套餐包
         IDataset userDisInfo = UserAttrInfoQry.getUserAttrbyUserIdPro(userId,"P","1100",productId,null,stat_month);
         if(IDataUtil.isNotEmpty(userDisInfo))
         {
             order_type.add("01");
             param_value.add("");
             package_code.add(userDisInfo.getData(0).getString("ATTR_VALUE"));
             eff_date.add(eff_dateStr);
             exp_date.add(exp_dateStr);

         }
         //2-如果封顶流量
         IDataset limitFeeInfo = UserAttrInfoQry.getUserAttrbyUserIdPro(userId,"P","1101",productId,null,stat_month);
         if(IDataUtil.isNotEmpty(limitFeeInfo))
         {
             order_type.add("02");
             param_value.add(limitFeeInfo.getData(0).getString("ATTR_VALUE"));
             package_code.add("");
             eff_date.add(eff_dateStr);
             exp_date.add(exp_dateStr);

         }
         
         //3-查询账期内订购叠加包
          eff_dateStr = SysDateMgr.decodeTimestamp(stat_month, SysDateMgr.PATTERN_STAND);
          exp_dateStr = SysDateMgr.getAddMonthsLastDay(0, eff_dateStr);
          fluxRefuelPackage(userId, package_code, param_value, eff_date, exp_date, order_type, eff_dateStr, exp_dateStr, productOfferid);
         
         //判断是否存在有效订购信息
        if(IDataUtil.isNotEmpty(order_type)){
            // for (int i = 0,sizeI = order_type.size(); i < sizeI; i++)
           // {
                IData tmpRsp = new DataMap();
                tmpRsp.put("SERIAL_NUMBER", sn);
                tmpRsp.put("SEQ_NO", seqNumber);
                tmpRsp.put("X_RESULTCODE","0");
                tmpRsp.put("X_RESULTINFO","OK");
                tmpRsp.put("RES_CODE","0");
                tmpRsp.put("PACKAGE_CODE", package_code);
                tmpRsp.put("PARAM_VALUE", param_value);
                tmpRsp.put("EFF_DATE", eff_date);
                tmpRsp.put("EXP_DATE", exp_date);
                tmpRsp.put("ORDER_TYPE", order_type);
                tmpRsp.put("X_RECORDNUM", order_type.size());
                rspData.add(tmpRsp);
           // }
        }else{
            IData tmpRsp = new DataMap();
            tmpRsp.put("SERIAL_NUMBER", sn);
            tmpRsp.put("SEQ_NO", seqNumber);
            tmpRsp.put("X_RESULTCODE","0");
            tmpRsp.put("X_RESULTINFO","OK");
            tmpRsp.put("RES_CODE","1");
            rspData.add(tmpRsp);
        }
         
    }
    /** 
    * @Title: fluxRefuelPackage
    * @Description: 由于不依赖订购关系，叠加包订购需另外判断
    * @param userId
    * @param package_code   叠加包套餐名
    * @param param_value    限量封顶值
    * @param eff_date       生效日期（输出）
    * @param exp_date       失效日期（输出）
    * @param order_type     订购类型：叠加包
    * @param eff_dateStr    生效日期
    * @param exp_dateStr    失效日期
    * @throws Exception  
    * @return void
    * @author chenkh
    * @time 2015年10月15日
    */ 
    private static void fluxRefuelPackage(String userId, IDataset package_code, IDataset param_value, IDataset eff_date, IDataset exp_date, IDataset order_type, String eff_dateStr, String exp_dateStr,String productOffid) throws Exception
    {
        // 3-如果叠加包
        IDataset fluxDataset = UserGrpMerchMebDisQry.qryMebDisInfoByUid(userId,eff_dateStr,exp_dateStr,productOffid);// 查询成员订购的叠加包
        if (IDataUtil.isNotEmpty(fluxDataset))
        {
            for (int j = 0, sizeJ = fluxDataset.size(); j < sizeJ; j++)
            {
                order_type.add("03");
                param_value.add("");
                package_code.add(fluxDataset.getData(j).getString("PRODUCT_DISCNT_CODE"));
                String startdate = SysDateMgr.getDateForYYYYMMDD(fluxDataset.getData(j).getString("START_DATE"));
                //国际流量统付特殊处理：END_DATE
                String productSpecCode = fluxDataset.getData(j).getString("PRODUCT_SPEC_CODE","");
                String enddate;
                if("99910".equals(productSpecCode))
                {
                	enddate = SysDateMgr.getEndDate(SysDateMgr.getDateForYYYYMMDD(fluxDataset.getData(j).getString("END_DATE")));
                }
                else
                {
                	enddate =SysDateMgr.getDateForYYYYMMDD(exp_dateStr);//d叠加包作用时间是月底
                }

                eff_date.add(startdate);
                exp_date.add(enddate);
            }

        }
    }
     /**
      * 集团一点支付
      * @param user_id
      * @param rspData
      * @param mebInfoDataset
      * @throws Exception
      */
     private static void getOnePayOrdInfo(String user_id,IDataset rspDataset,IDataset mebInfoDataset, String seqNumber, String sn,String stat_month) throws Exception
     {
         //1- 获取集团付费关系
         IData rspData = new DataMap();
         IDataset payRelaDataset = PayRelaInfoQry.qryInfosByUserIdAcctIdPayitem(user_id,null,stat_month);
        //2- 没有集团付费关系  无有效的订购列表
         if(IDataUtil.isEmpty(payRelaDataset))
         {
             rspData.put("RES_CODE","1");
         }
         else
         {
             rspData.put("RES_CODE","0");
             IDataset  eff_date= new DatasetList();
             IDataset  exp_date= new DatasetList();
             IDataset  order_type= new DatasetList();
             
             //2-1获取成员订购关系有效时间
             String eff_dateStr=SysDateMgr.getDateForYYYYMMDD(mebInfoDataset.getData(0).getString("START_DATE"));
             String exp_dateStr=SysDateMgr.getDateForYYYYMMDD(mebInfoDataset.getData(0).getString("END_DATE"));
             
             //2-2获取限定方式
             String  limit_type=  payRelaDataset.getData(0).getString("LIMIT_TYPE");//限定方式：0-不限定，1-金额，2-比例
              if("1".equals(limit_type)){
                  order_type.add("04");//04-    全额/限额代付
                  eff_date.add(eff_dateStr);
                  exp_date.add(exp_dateStr);
                  rspData.put("EFF_DATE", eff_date);
                  rspData.put("EXP_DATE", exp_date);
                  rspData.put("ORDER_TYPE", order_type);
                  rspData.put("X_RECORDNUM", order_type.size());
              }
              if("2".equals(limit_type)){
                  order_type.add("05");//05-    比例代付
                  eff_date.add(eff_dateStr);
                  exp_date.add(exp_dateStr);
                  rspData.put("EFF_DATE", eff_date);
                  rspData.put("EXP_DATE", exp_date);
                  rspData.put("ORDER_TYPE", order_type);
                  rspData.put("X_RECORDNUM", order_type.size());
              }
            
         }
         rspData.put("SERIAL_NUMBER", sn);
         rspDataset.add(rspData);
        
     }
    
    /**
     * chenyi
     * 2015-2-3
     *  根据集团订购关系查询是否存在有效的用户关系
     * @param productOffid
     * @param user_id
     * @return
     * @throws Exception 
     */
    private static IDataset isExistRelaInfo(String productOffid,String user_id,String productID,String stat_month) throws Exception
    {
        //1-获取relatypecdode
        String relaTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(null, productID, false);
        //2-查询账期内有效的relation信息
        IDataset mebInfoDataset = UserGrpMerchMebInfoQry.qryMerchMebInfoByUseridProdOff(user_id,productOffid,relaTypeCode,stat_month);
        
        return mebInfoDataset;
    }
}
