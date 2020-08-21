package com.asiainfo.veris.crm.order.soa.person.busi.auth;


import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.dao.CrmDAO;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.BusinessAbilityCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform.AbilityOpenPlatQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ExceptionUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.KjPrintBean;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryInfoUtil;
import org.apache.log4j.Logger;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.cmonline.selfterminal.SelfTerminalUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.CreateNpUserTradeBean;
import java.text.SimpleDateFormat;
import com.ailk.biz.BizEnv;

public class AbilityPlatOrderSVC extends CSBizService
{
    private static final Logger log = Logger.getLogger(AbilityPlatOrderSVC.class);
    private static final long serialVersionUID = 1L;
    private static StringBuilder getInterFaceSQL;

    static {
        getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ");
    }

    /**
     *  综合订单信息同步   CIP00064
     *
     * @param data
     * @return
     * @throws Exception
     * @author zhaohj3
     */
    public IData synOrderInfo(IData data) throws Exception{
        data = new DataMap(data.toString());
        if(log.isDebugEnabled())
        {
           log.debug("待操作数据1："+data.toString());
        }
        
      
        if(log.isDebugEnabled())
        {
            log.debug("待操作数据PAYMENT_INFO："+data.getString("PAYMENT_INFO"));
            IData syncPayment2 = new DataMap(data.getString("PAYMENT_INFO"));
          
            log.debug("待操作数据PAYMENT_INFO1："+syncPayment2.getString("PAYMENT_TIME"));
          
            data.remove("PAYMENT_INFO");
            data.put("PAYMENT_INFO", syncPayment2);
        }
        
        IData result = new DataMap();
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "OK");
    
        IDataUtil.chkParam(data, "CHANNEL_ID");
        IDataUtil.chkParam(data, "ORDER_ID");
        IDataUtil.chkParam(data, "EXT_ORDER_ID");
        IDataUtil.chkParam(data, "CREATE_TIME");
        IDataUtil.chkParam(data, "BUYER_NICK_NAME");

        IDataset list = AbilityPlatOrderBean.queryOrderInfo(data.getString("ORDER_ID"));
        if (IDataUtil.isNotEmpty(list)) {
            return result;
        }

        //支付信息
        IDataUtil.chkParam(data, "PAYMENT_INFO");
        IData syncPayment = new DataMap();
        IData paymentInfo = new DataMap();

        if(IDataUtil.isNotEmpty(data.getData("PAYMENT_INFO")))
        {
            syncPayment = data.getData("PAYMENT_INFO");
            IDataUtil.chkParam(syncPayment, "CHARGE_TYPE");
            IDataUtil.chkParam(syncPayment, "PAYMENT");
            IDataUtil.chkParam(syncPayment, "TOTAL_FEE");
            IDataUtil.chkParam(syncPayment, "PAYMENT_TIME");
            String channel = data.getString("CHANNEL_ID");
            if("320".equals(channel)){
                IDataUtil.chkParam(syncPayment, "PAYMENT_ORGANIZATION");
                IDataUtil.chkParam(syncPayment, "PAYMENT_SERVICE_FEE");
                IDataUtil.chkParam(syncPayment, "IS_SERVICE_FEE_REFUNDABLE");
            }


            paymentInfo.put("paymentType",syncPayment.getString("PAYMENT_TYPE"));  // 支付方式
            paymentInfo.put("chargeType",syncPayment.getString("CHARGE_TYPE"));  // 扣费类型
            paymentInfo.put("paymentOrderID",syncPayment.getString("PAYMENT_ORDER_ID"));  // 第三方支付流水
            paymentInfo.put("paymentTime",syncPayment.getString("PAYMENT_TIME"));  // 支付时间
            paymentInfo.put("orderSum",syncPayment.getString("TOTAL_FEE"));  // 订单总金额金额


            if(log.isDebugEnabled())
            {
                log.debug("待操作数据2：" + syncPayment.toString());
            }
        }
        //配送信息
        IDataUtil.chkParam(data, "NEED_DISTRIBUTION");
        IData syncDistribution = new DataMap();
        if(IDataUtil.isNotEmpty(data.getData("CONSIGNEE_INFO"))){
            syncDistribution = data.getData("CONSIGNEE_INFO");
            IDataUtil.chkParam(syncDistribution, "NAME");
            IDataUtil.chkParam(syncDistribution, "PROVINCE");
            IDataUtil.chkParam(syncDistribution, "CITY");
            IDataUtil.chkParam(syncDistribution, "DISTRICT");
            IDataUtil.chkParam(syncDistribution, "ADDRESS");
            IDataUtil.chkParam(syncDistribution, "MOBILEPHONE");
            if(log.isDebugEnabled())
            {
              log.debug("待操作数据3：" + syncDistribution.toString());
            }
        }
        //发票信息
        IDataUtil.chkParam(data, "NEED_INVOICE");
        IData syncInvoice = new DataMap();
        if(IDataUtil.isNotEmpty(data.getData("INVOICE_INFO"))){
            syncInvoice = data.getData("INVOICE_INFO");
            IDataUtil.chkParam(syncInvoice, "INVOICE_TYPE");
            IDataUtil.chkParam(syncInvoice, "INVOICE_TITLE");
            if(log.isDebugEnabled())
            {
                log.debug("待操作数据4：" + syncInvoice.toString());
            }
        }
      
      /*List xmListList = (List) data.get("SUB_ORDER_LIST");
      JSONArray json = JSONArray.fromObject(xmListList);
      DatasetList xmList = DatasetList.fromJSONArray(json);
      data.remove("SUB_ORDER_LIST");
      data.put("SUB_ORDER_LIST", xmList);*/
      
         //子订单
        if(IDataUtil.isNotEmpty(data.getDataset("SUB_ORDER_LIST")))
        {

            IDataset suborder_list = data.getDataset("SUB_ORDER_LIST");
            for(int i = 0;i<suborder_list.size();i++)
            {
                IData suborder = suborder_list.getData(i);
                IDataUtil.chkParam(suborder, "SUB_ORDER_ID");
                IDataUtil.chkParam(suborder, "SUB_EXT_ORDER_ID");

                if(log.isDebugEnabled())
                {
                    log.debug("待操作数据" + (i+5) + syncInvoice.toString());
                }
              
                IData syncSubscriber = new DataMap();
                IDataUtil.chkParam(suborder, "SUB_SCRIBER_INFO");
                if(IDataUtil.isNotEmpty(suborder.getData("SUB_SCRIBER_INFO"))) {

                    IData subscriber = suborder.getData("SUB_SCRIBER_INFO");
                    syncSubscriber = subscriber;
                    IDataUtil.chkParam(subscriber, "NUMBER_OPR_TYPE");
                    IDataUtil.chkParam(subscriber, "SERVICE_NO");
                    IDataUtil.chkParam(subscriber, "SERVICE_NO_TYPE");
                    String number_opertype = subscriber.getString("NUMBER_OPR_TYPE");
                    if ("01".equals(number_opertype))
                    {
                        subscriber.put("QUANTITY", subscriber.getInt("QUANTITY", 1));
                        IDataUtil.chkParam(subscriber, "NUMBER_PRICE");
                        IDataUtil.chkParam(subscriber, "SIM_PRICE");
                        IDataUtil.chkParam(subscriber, "LEGAL_NAME");
                        IDataUtil.chkParam(subscriber, "CERTIFICATE_TYPE");
                        IDataUtil.chkParam(subscriber, "CERTIFICATE_NO");
                        IDataUtil.chkParam(subscriber, "PIC_NAME_R_PATH"); // 用户拍摄人像图片名称，当numberOprType=01、06和08时必填
                    }
                    if ("06".equals(number_opertype)) {
                        IDataUtil.chkParam(subscriber, "AREA_CODE"); // 固话区号，numberOprType=06和07时必填，填写行政区号，如北京010
                        IDataUtil.chkParam(subscriber, "FT_NO"); // 固话号码，numberOprType=06和07时必填，如固话是010-12345678，本字段传12345678
                        IDataUtil.chkParam(subscriber, "PIC_NAME_R_PATH"); // 用户拍摄人像图片名称，当numberOprType=01、06和08时必填
                    }
                    if ("07".equals(number_opertype)) {
                        IDataUtil.chkParam(subscriber, "AREA_CODE"); // 固话区号，numberOprType=06和07时必填，填写行政区号，如北京010
                        IDataUtil.chkParam(subscriber, "FT_NO"); // 固话号码，numberOprType=06和07时必填，如固话是010-12345678，本字段传12345678
                    }
                    if ("08".equals(number_opertype)) {
                        IDataUtil.chkParam(subscriber, "PIC_NAME_R_PATH"); // 用户拍摄人像图片名称，当numberOprType=01、06和08时必填
                    }
                    if ("20".equals(number_opertype)||"21".equals(number_opertype)||"23".equals(number_opertype)||"24".equals(number_opertype)) {
                        IDataUtil.chkParam(subscriber, "CERTIFICATE_NO");
                    }

                    /*
                     * 集团权益配合改造--综合订单信息同步 【CIP00064】  add by zhengkai5
                     *  权益接口：HAIN_UNHT_QYgroupCreateOrder
                     *
                     * */
                    if ("13".equals(number_opertype)) //  13：非赠送类权益订购（即权益订购）
                    {
                        IData rightsParam = new DataMap();
                        rightsParam.put("chanId",data.getString("CHANNEL_ID"));  // 渠道标识
                        rightsParam.put("orderStatus","01");  // 渠道标识
                        rightsParam.put("telnum",subscriber.getDataset("SERVICE_NO"));  // 原子订单编码
                        rightsParam.put("numberType",subscriber.getInt("SERVICE_NO_TYPE"));
                        rightsParam.put("areaCode",subscriber.getInt("AREA_CODE"));
                        rightsParam.put("userId","");  // ?  用户Id
                        rightsParam.put("userType","");  // ?  0-个人，1-集团2-家庭（默认为0）

                        rightsParam.put("subOrderList",suborder);  // 子订单信息
                        rightsParam.put("paymentInfo",paymentInfo);  // 支付信息

                        IData rigthsResult = BusinessAbilityCall.callBusinessCenterCommon("HAIN_UNHT_QYgroupCreateOrder",rightsParam);
                        if(!"0000".equals(rigthsResult.getString("respCode")))
                        {
                            result.put("BIZ_CODE", rigthsResult.getString("respCode"));
                            result.put("BIZ_DESC", rigthsResult.getString("respDesc"));
                            return result;
                        }
                        continue;
                    }
                    /* add by zhengkai5  end */


                    if (log.isDebugEnabled()) {
                      log.debug("待操作数据 A" + syncInvoice.toString());
                    }
                }

                IDataUtil.chkParam(suborder, "GOODS_INFO");
                IData syncGoods = new DataMap();
                if(IDataUtil.isNotEmpty(suborder.getData("GOODS_INFO"))){
                
                      IData goods = suborder.getData("GOODS_INFO");
                      syncGoods = goods;
                      IDataUtil.chkParam(goods, "GOODS_ID");
                      IDataUtil.chkParam(goods, "GOODS_TITLE");
                      IDataUtil.chkParam(goods, "AMOUNT");
                      IDataUtil.chkParam(goods, "PRICE");
                      IDataUtil.chkParam(goods, "GOODS_PROVINCE");
                      IDataUtil.chkParam(goods, "GOODS_CITY");
                      if(log.isDebugEnabled())
                      {
                          log.debug("待操作数据 B" + goods.toString());
                      } 
//                      IData syncGoodsInfo = new DataMap();
//                      syncGoodsInfo.put("GOODS_ID", goods.getString("GOODS_ID"));
//                      syncGoodsInfo.put("GOODS_TITLE", goods.getString("GOODS_TITLE"));
//                      syncGoodsInfo.put("AMOUNT", goods.getString("AMOUNT"));
//                      syncGoodsInfo.put("PRICE", goods.getString("PRICE"));
//                      syncGoodsInfo.put("GOODS_PROVINCE", goods.getString("GOODS_PROVINCE"));
//                      syncGoodsInfo.put("GOODS_CITY", goods.getString("GOODS_CITY"));
//                      syncGoodsInfo.put("SUBORDER_ID", suborder.getString("SUBORDER_ID"));
//                      syncGoodsInfo.put("RESERVE1", goods.getString("RESERVE1",""));
//                      syncGoodsInfo.put("RESERVE2", goods.getString("RESERVE2",""));
//                      if(log.isDebugEnabled()){
//                          log.debug("待操作的商品表数据："+syncGoodsInfo.toString());
//                      }
//                      if(!AbilityPlatOrderBean.synGoodsInfo(syncGoodsInfo)){
//                          result.put("BIZ_CODE", "4000");
//                          result.put("BIZ_CODE", syncGoodsInfo.getString("GOODS_ID")+"商品信息操作有误");
//                          return result;
//                      }
                  
                }
              
                if(IDataUtil.isNotEmpty(suborder.getDataset("PRODUCT_LIST"))){
                    IDataset product_list = suborder.getDataset("PRODUCT_LIST");
                    for(int j = 0 ; j<product_list.size() ; j++)
                    {
                        IData product = product_list.getData(j);
                        IDataUtil.chkParam(product, "PRODUCT_ID");
                        IDataUtil.chkParam(product, "PRODUCT_TYPE");
                        IData syncProduct = new DataMap();
                        syncProduct.put("PRODUCT_ID", product.getString("PRODUCT_ID"));
                        syncProduct.put("PRODUCT_TYPE", product.getString("PRODUCT_TYPE"));
                        syncProduct.put("SERVICEID_LIST", product.getString("SERVICEID_LIST"));
//                      syncProduct.put("GOODS_ID", goods.getString("GOODS_ID"));
                        syncProduct.put("SUBORDER_ID", suborder.getString("SUB_ORDER_ID"));
      
                        if(log.isDebugEnabled()){
                          log.debug("待操作的产品表数据："+syncProduct.toString());
                        }
                        if(!AbilityPlatOrderBean.synProductInfo(syncProduct)){
                          result.put("BIZ_CODE", "4000");
                          result.put("BIZ_DESC", syncProduct.getString("PRODUCT_ID")+"产品信息操作有误");
                          return result;
                        }
                    }
                }
                IData syncActivity = new DataMap();
                if(IDataUtil.isNotEmpty(suborder.getData("ACTIVITY_INFO")))
                {
                    IData activity = suborder.getData("ACTIVITY_INFO");
                    syncActivity = activity;
                    IDataUtil.chkParam(activity, "ACTIVITY_CODE");
                    IDataUtil.chkParam(activity, "ACTIVITY_TYPE");
                    IDataUtil.chkParam(activity, "ACTIVITY_NAME");
                  
                }
                IDataUtil.chkParam(suborder, "SUBTOTAL_FEE");
                IDataUtil.chkParam(suborder, "ADJUST_FEE");
//              IDataUtil.chkParam(suborder, "ORDER_STATUS");
             
//              if(IDataUtil.isNotEmpty(suborder.getDataset("ADDITIONAL_INFO"))){
//                  IDataset syncAddition_info = suborder.getDataset("ADDITIONAL_INFO");
//                  for(int m=0;m<syncAddition_info.size();m++){
//                      IData syncAdditionall = syncAddition_info.getData(m);
//                      IData syncAddition = new DataMap();
//                      syncAddition.put("ORDER_ID", data.getString("ORDER_ID"));
//                      syncAddition.put("PARA_KEY", syncAdditionall.getString("PARA_KEY"));
//                      syncAddition.put("PARA_VALUE", syncAdditionall.getString("PARA_VALUE"));
//
//                      if(log.isDebugEnabled()){
//                          log.debug("待操作的额外信息表数据："+syncAddition.toString());
//                      }
//                      if(!AbilityPlatOrderBean.synAdditionallInfo(syncAddition)){
//                          result.put("BIZ_CODE", "4000");
//                          result.put("BIZ_DESC", syncAddition.getString("PARA_KEY")+"信息操作有误");
//                          return result;
//                      }
//                  }
//              }
    
              IData syncSubOrder = new DataMap();
              syncSubOrder.put("SUBORDER_ID", suborder.getString("SUB_ORDER_ID"));
              syncSubOrder.put("SUBEXTORDER_ID", suborder.getString("SUB_EXT_ORDER_ID"));
              syncSubOrder.put("ORDER_ID", data.getString("ORDER_ID"));
              
              syncSubOrder.put("NUMBER_OPRTYPE", syncSubscriber.getString("NUMBER_OPR_TYPE"));
              syncSubOrder.put("SERVICENO", syncSubscriber.getString("SERVICE_NO"));
              syncSubOrder.put("SERVICENO_TYPE", syncSubscriber.getString("SERVICE_NO_TYPE"));
              syncSubOrder.put("NUMBER_BRAND", syncSubscriber.getString("NUMBERBRAND"));
              syncSubOrder.put("QUANTITY", syncSubscriber.getInt("QUANTITY"));
              syncSubOrder.put("NUMBER_PRICE", syncSubscriber.getInt("NUMBER_PRICE"));
              syncSubOrder.put("SIM_PRICE", syncSubscriber.getInt("SIM_PRICE"));
              syncSubOrder.put("LEGAL_NAME", syncSubscriber.getString("LEGAL_NAME"));
              syncSubOrder.put("CERTIFICATE_TYPE", syncSubscriber.getString("CERTIFICATE_TYPE"));
              syncSubOrder.put("CERTIFICATE_NO", syncSubscriber.getString("CERTIFICATE_NO"));
              
              syncSubOrder.put("GOODS_ID", syncGoods.getString("GOODS_ID"));
              syncSubOrder.put("GOODS_TITLE", syncGoods.getString("GOODS_TITLE"));
              syncSubOrder.put("AMOUNT", syncGoods.getInt("AMOUNT"));
              syncSubOrder.put("PRICE", syncGoods.getInt("PRICE"));
              syncSubOrder.put("GOODS_PROVINCE", syncGoods.getString("GOODS_PROVINCE"));
              syncSubOrder.put("GOODS_CITY", syncGoods.getString("GOODS_CITY"));
              syncSubOrder.put("GOODS_CITY_CODE", syncGoods.getString("GOODS_CITY_CODE"));

              syncSubOrder.put("ACTIVITY_CODE", syncActivity.getString("ACTIVITY_CODE"));
              syncSubOrder.put("ACTIVITY_TYPE", syncActivity.getString("ACTIVITY_TYPE"));
              syncSubOrder.put("ACTIVITY_NAME", syncActivity.getString("ACTIVITY_NAME"));
              syncSubOrder.put("ACTIVITY_DESC", syncActivity.getString("ACTIVITY_DESC"));
              
              syncSubOrder.put("SUBTOTAL_FEE", suborder.getInt("SUBTOTAL_FEE"));
              syncSubOrder.put("ADJUST_FEE", suborder.getInt("ADJUST_FEE"));
              syncSubOrder.put("ADJUST_REASON", suborder.getString("ADJUST_REASON"));
              syncSubOrder.put("ORDER_STATUS", suborder.getString("ORDER_STATUS"));
              
              syncSubOrder.put("STATE", "TD");
              syncSubOrder.put("DEAL_STATE", suborder.getString("DEAL_STATE"));
              syncSubOrder.put("DEAL_DESC", suborder.getString("DEAL_DESC"));
              syncSubOrder.put("CREATE_TIME", data.getString("CREATE_TIME"));
              syncSubOrder.put("UPDATE_TIME", SysDateMgr.getSysDate());
              syncSubOrder.put("RSRV_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));

              // 关于一级能力开放平台新增、改造和家固话相关接口的通知
              syncSubOrder.put("AREA_CODE", syncSubscriber.getString("AREA_CODE"));
              syncSubOrder.put("FT_NO", syncSubscriber.getString("FT_NO"));
              syncSubOrder.put("PIC_NAME_R_PATH", syncSubscriber.getString("PIC_NAME_R_PATH"));

              //关于一级能力开放平台地址信息调整及改造通知
              if ("20".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
                      || "21".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
                      || "22".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
                      || "23".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
                      || "24".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
              ) {
                  IDataUtil.chkParam(syncDistribution, "PROVICE_CODE");
                  IDataUtil.chkParam(syncDistribution, "CITY_CODE");
              }

              syncSubOrder.put("RSRV_STR4", syncDistribution.getString("PROVICE_CODE"));
              syncSubOrder.put("RSRV_STR5", syncDistribution.getString("CITY_CODE"));
              syncSubOrder.put("RSRV_STR6", syncDistribution.getString("DISTRICT_CODE"));

              if ("20".equals(syncSubscriber.getString("NUMBER_OPR_TYPE")) || "21".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
            		  || "08".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
            		  || "09".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
                      || "23".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
                      || "24".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
              ) {
                  IData inParam = new DataMap();
                  String serialNumber = syncSubscriber.getString("SERVICE_NO");
                  /*//选占
                  try {
                      IDataset results = ResCall.checkResourceForMphone("0", serialNumber, "0", null, null);
                      if (IDataUtil.isEmpty(results)) {
                          syncSubOrder.put("STATE", "FA");
                      }
                  } catch (Exception e) {
                      syncSubOrder.put("STATE", "FA");
                  }
                  //号码预占
                  try {
                      IDataset results = ResCall.resEngrossForMphone(serialNumber);
                  } catch (Exception e) {
                      syncSubOrder.put("STATE", "FA");
                  }*/
                  boolean isPreTag=true;
                  //携入上门写白卡，不走预占
                  if ("24".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))) {
                      isPreTag = false;
                  }
                  //改为身份证号码预占
                  //设置登录信息
                  if("08".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
                		  ||"09".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))){
                	  IDataset staffList=CommparaInfoQry.getCommParas("CSM", "7979", "JZXK_CHANNEL_STAFF", data.getString("CHANNEL_ID"), "0898");
                	  if(IDataUtil.isNotEmpty(staffList)){
 	  	              	 getVisit().setStaffId(staffList.getData(0).getString("PARA_CODE2"));
 	  	              	 getVisit().setDepartId(staffList.getData(0).getString("PARA_CODE3"));
 	  	              	 getVisit().setCityCode(staffList.getData(0).getString("PARA_CODE5"));
 	  	              	 //不需要预占
 	  	              	 if("1".equals(staffList.getData(0).getString("PARA_CODE7"))){
 	  	              		 isPreTag=false;
 	  	              	 }
 	  	              }else{
 	  	            	  isPreTag=false; 
 	  	              }
                  }else{
	                  IDataset oaoStaff=AbilityPlatOrderBean.queryOAOStaff(data.getString("CHANNEL_ID"), syncSubscriber.getString("NUMBER_OPR_TYPE"));
	  	              if(IDataUtil.isNotEmpty(oaoStaff)){
	  	              	 getVisit().setStaffId(oaoStaff.getData(0).getString("STAFF_ID"));
	  	              	 getVisit().setDepartId(oaoStaff.getData(0).getString("DEPT_ID"));
	  	              	 getVisit().setCityCode(oaoStaff.getData(0).getString("CITY_CODE"));
	  	              }
                  }
                  
                  //1、CIP00064订单同步
                  //（1）如果是21类型的订单，状态默认是PC
                  //（2）如果是23类型的订单，状态默认是PC
                  //（3）如果是24类型的订单，状态默认是PC
                  if("21".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
                          ||"23".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
                          ||"24".equals(syncSubscriber.getString("NUMBER_OPR_TYPE"))
                  ){
                	  syncSubOrder.put("STATE", "PC");
                  }

            	  if(isPreTag){
					  IData resParam = new DataMap();
					  resParam.put("RES_NO", syncSubscriber.getString("SERVICE_NO"));//调资源接口需传预占号码
					  resParam.put("RES_TRADE_CODE", "IRes_NetSel_MphoneCode");//普通网上选号 
					  resParam.put("OCCUPY_TYPE_CODE", "1");//选占类型,1：网上选占
					  resParam.put("RES_TYPE_CODE", "0");//0-号码
					  resParam.put("USER_EPARCHY_CODE", CSBizBean.getUserEparchyCode());
					  resParam.put("ROUTE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
					  resParam.put("PSPT_ID", syncSubscriber.getString("CERTIFICATE_NO"));//选占证件号码
					  resParam.put("PSPT_TYPE", "");//选占证件类型，非必传
					  resParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 受理地州
					  resParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 受理业务区
					  resParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 受理部门
					  resParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 受理员工
					  resParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode()); // 接入渠道
					  resParam.put("OPERATE_TIME", "720"); // 预占一个月
					  try {
						  IDataset results = ResCall.resTempOccupyByNetSel(resParam);
						  if (IDataUtil.isEmpty(results)) {
							  syncSubOrder.put("STATE", "FA");
							  syncSubOrder.put("STATUS_DESC", "F06-下单号码预占失败");
						  }
					  } catch (Exception e) {
						  syncSubOrder.put("STATE", "FA");
						  syncSubOrder.put("STATUS_DESC", "F06-下单号码预占失败["+e.getMessage()+"]");
					  }
                  }
                  
              }
              if(log.isDebugEnabled()){
                  log.debug("待操作的子订单表数据："+syncSubOrder.toString());
              }
              if(!AbilityPlatOrderBean.synSubOrderInfo(syncSubOrder)){
                  result.put("BIZ_CODE", "4000");
                  result.put("BIZ_DESC", syncSubOrder.getString("SUBORDER_ID")+"子订单信息操作有误");
                  return result;
              }
          }
      }
      IData syncOrder = new DataMap();
      syncOrder.put("CHANNEL_CODE", data.getString("CHANNEL_ID"));
      syncOrder.put("ORDER_ID", data.getString("ORDER_ID"));
      syncOrder.put("EXTORDER_ID", data.getString("EXT_ORDER_ID"));
      String time = data.getString("CREATE_TIME");
      syncOrder.put("CREATE_TIME", time);
      syncOrder.put("BUYER_NICKNAME", data.getString("BUYER_NICK_NAME"));
      syncOrder.put("BUYER_EMAIL", data.getString("BUYER_EMAIL"));
      syncOrder.put("BUYER_MESSAGE", data.getString("BUYER_MESSAGE"));
      syncOrder.put("TRADE_MEMO", data.getString("TRADE_MEMO"));
      syncOrder.put("SELLER_MEMO", data.getString("SELLER_MEMO"));
      syncOrder.put("SHOP_CODE", data.getString("SHOP_CODE"));
      syncOrder.put("SHOP_NAME", data.getString("SHOP_NAME"));
      syncOrder.put("SELLER_ID", data.getString("SELLER_ID"));
      syncOrder.put("DISCOUNTED_PRICE", syncPayment.getString("DISCOUNTED_PRICE"));
      syncOrder.put("DISCOUNTED_TYPE", syncPayment.getString("DISCOUNTED_TYPE"));
      syncOrder.put("COUPON_CODE", syncPayment.getString("COUPON_CODE"));
      
      syncOrder.put("UNI_CHANNEL_ID", data.getString("UNI_CHANNEL_ID",""));
      syncOrder.put("CHARGE_TYPE", syncPayment.getString("CHARGE_TYPE"));
      syncOrder.put("TOTAL_FEE", syncPayment.getInt("TOTAL_FEE"));
      syncOrder.put("PAYMENT", syncPayment.getInt("PAYMENT"));
      syncOrder.put("PAYMENT_TYPE", syncPayment.getString("PAYMENT_TYPE"));
      syncOrder.put("DISCOUNTED_PRICE", syncPayment.getString("DISCOUNTED_PRICE"));
      syncOrder.put("PAYMENT_ORDERID", syncPayment.getString("PAYMENT_ORDER_ID"));
      syncOrder.put("PAYMENT_TIME", syncPayment.getString("PAYMENT_TIME"));
      syncOrder.put("PAYMENT_ORGANIZATION", syncPayment.getString("PAYMENT_ORGANIZATION"));
      syncOrder.put("PATMENT_SERVICEFEE", syncPayment.getString("PAYMENT_SERVICE_FEE"));
      syncOrder.put("SERVICE_FEE_REFUNDABLE", syncPayment.getString("SERVICE_FEE_REFUNDABLE"));
      syncOrder.put("IS_SERVICE_FEE_REFUNDABLE", syncPayment.getString("IS_SERVICE_FEE_REFUNDABLE"));
      
      syncOrder.put("NEED_DISTRIBUTION", data.getString("NEED_DISTRIBUTION"));
      syncOrder.put("SHIPMENT_COMPANY_CODE", data.getString("SHIPMENT_COMPANY_CODE",""));
      
      syncOrder.put("NAME", syncDistribution.getString("NAME"));
      syncOrder.put("COUNTRY", syncDistribution.getString("COUNTRY"));
      syncOrder.put("PROVINCE", syncDistribution.getString("PROVINCE"));
      syncOrder.put("CITY", syncDistribution.getString("CITY"));
      syncOrder.put("DISTRICT", syncDistribution.getString("DISTRICT"));
      syncOrder.put("ADDRESS", syncDistribution.getString("ADDRESS"));
      syncOrder.put("ADDRESS_ID", syncDistribution.getString("ADDRESS_ID"));

      syncOrder.put("POST_CODE", syncDistribution.getString("POSTCODE"));
      syncOrder.put("MOBILE_PHONE", syncDistribution.getString("MOBILEPHONE"));
      syncOrder.put("LAND_LINE", syncDistribution.getString("LAND_LINE"));
      syncOrder.put("RSRV_STR10", syncDistribution.getString("SCHEDULE_TIME",""));
      
      syncOrder.put("NEED_INVOICE", data.getString("NEED_INVOICE"));
      
      syncOrder.put("INVOICE_TYPE", syncInvoice.getString("INVOICE_TYPE"));
      syncOrder.put("INVOICE_TITLE", syncInvoice.getString("INVOICE_TITLE"));
      syncOrder.put("INVOICE_MEMO", syncInvoice.getString("INVOICE_MEMO"));



      
      if(log.isDebugEnabled()){
          log.debug("待操作的主订单表数据："+syncOrder.toString());
      }
      if(!AbilityPlatOrderBean.synOrderInfo(syncOrder)){
          result.put("BIZ_CODE", "4000");
          result.put("BIZ_DESC", syncOrder.getString("ORDER_ID")+"订单信息操作有误");
      }
      
      return result;
    }


    /**
     * 综合订单信息同步订单处理
     *
     * @param data
     * @return
     * @throws Exception
     * @author zhaohj3
     */
    public IData dealOrderInfo(IData data) throws Exception {
        IData returnData = new DataMap();
        //子订单列表
        IDataset list = AbilityPlatOrderBean.querySuborderInfo(); // TODO 关联 TF_B_CTRM_GERLORDER、TF_B_CTRM_GERLSUBORDER
        log.error("list---------" + list);
        if (IDataUtil.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                IData subOrderData = list.getData(i);
                log.error("subOrderData---------" + subOrderData);
                IDataset order = AbilityPlatOrderBean.queryOrderInfo(subOrderData.getString("ORDER_ID"));
                IData resultItem = new DataMap();
                resultItem.put("RESULT_INFO", "处理成功");
                resultItem.put("RESULT_CODE", "0000");
                resultItem.put("RESULT_DETAIL", "");
                resultItem.put("ORDER_ID", subOrderData.getString("ORDER_ID"));
                resultItem.put("SUBORDER_ID", subOrderData.getString("SUBORDER_ID"));
                IData ret = new DataMap();
                String numberOprType = subOrderData.getString("NUMBER_OPRTYPE");
                try {
                    if ("02".equals(numberOprType)) { // 02：存量（老）用户办理合约/套餐/流量包等商品
                        ret = dealSubOrderList(subOrderData);
                    } else if ("06".equals(numberOprType)) { // 06：和家固话新开户及套餐订购
                        ret = createImsUser(subOrderData);
                    }
                } catch (Exception e) {
                    resultItem.putAll(ExceptionUtils.getExceptionInfo(e));
                    if (e.getCause() != null) {
                        String msg = e.getMessage() + e.getCause().getCause();
                        msg = (msg == null) ? "处理失败" : msg;
                        String resultInfo = (msg.length() > 1024) ? msg.substring(0, 1024) : msg;
                        resultItem.put("RESULT_INFO", resultInfo);
                    }
                    resultItem.put("STATE", "0000".equals(resultItem.getString("RESULT_CODE")) ? "SC" : "FA");//SC:订购完成，FA:订购失败
                }
                IDataset tradeIds = new DatasetList();
                //更新下发表中执行的工单id
                if (IDataUtil.isNotEmpty(ret)) {
                    tradeIds.add(ret.getString("TRADE_ID"));
                    resultItem.put("TRADE_ID", ret.getString("TRADE_ID", ""));
                    resultItem.put("RESULT_CODE", ret.getString("BIZ_CODE"));
                    resultItem.put("RESULT_INFO", ret.getString("BIZ_DESC"));
                    resultItem.put("RESULT_DETAIL", "");
                    resultItem.put("STATE", "0000".equals(resultItem.getString("RESULT_CODE")) ? "SC" : "FA");//SC:订购完成，FA:订购失败
//                    resultItem.put("ROWID", item.getString("ROWID"));
//                    //需要同步订单状态给一级平台
//                    inData.put("IS_SYNC","0");

                    if (StringUtils.isNotBlank(ret.getString("TRADE_ID")) && IDataUtil.isNotEmpty(order)) {
                        String needInvoice = order.getData(0).getString("NEED_INVOICE", "2");//1
                        String chargeType = order.getData(0).getString("CHARGE_TYPE");//0 2 6开具发票
                        String number_opertype = subOrderData.getString("NUMBER_OPRTYPE");
                        if ("1".equals(needInvoice) && ("0".equals(chargeType) || "2".equals(chargeType) || "6".equals(chargeType)) && "02".equals(number_opertype)) {
                            //开具发票
                            log.error("tradeIds---" + (String) tradeIds.get(0));
                            IData printData = new DataMap();
                            printData.put("TRADE_ID", (String) tradeIds.get(0));
                            IDataset mainTradeInfos = TradeInfoQry.getTradeAndBHTradeByTradeId(printData.getString("TRADE_ID"));//查询台账及历史台账
                            if (IDataUtil.isNotEmpty(mainTradeInfos)) {
                                IData mainTradeInfo = mainTradeInfos.getData(0);
                                printData.putAll(mainTradeInfo);
                            }
                            printData.put("NAME", subOrderData.getString("GOODS_TITLE"));
                            log.error("GOODS_TITLE-------------------" + subOrderData.getString("GOODS_TITLE"));
                            buildPrintData(order.getData(0), printData, list.getData(0));
                            try {
                                KjPrintBean bean = BeanManager.createBean(KjPrintBean.class);
                                bean.printKJForSC(printData);
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                        }
                    }
                }
                log.error("dealOrderInfo---resultItem：" + resultItem);
                updateInfo(resultItem);
            }
        }
        returnData.put("RESULT_CODE", "0000");
        returnData.put("RESULT_INFO", "调用成功！");
        returnData.put("bizCode", "0000");
        returnData.put("bizDesc", "调用成功！");
        return returnData;
    }

    /**
     * 处理子订单数据
     *
     * @param orderInfo
     * @return
     * @throws Exception
     * @author zhouxin7
     */
    public IData dealSubOrderList(IData orderInfo) throws Exception {
        IData ret = new DataMap();
        ret.put("BIZ_CODE", "0000");
        ret.put("BIZ_DESC", "订购完成!");
        String serialNumber = orderInfo.getString("SERIAL_NUMBER");
        String route = RouteInfoQry.getEparchyCodeBySn(serialNumber);
        IDataset userInfos = UserInfoQry.getUserCustInfo(serialNumber, route);
        if (IDataUtil.isEmpty(userInfos)) {
            ret.put("BIZ_CODE", "9999");
            ret.put("BIZ_DESC", "用户资料不存在!");
            return ret;
        }
        String uniChannelId = orderInfo.getString("UNI_CHANNEL_ID","");    //新增一级能开UNI_CHANNEL_ID全网编码
        boolean hasVasFlag = false; // 是否有合约产品
        boolean productFlag = false; // 是否有产品变更
        boolean mianproductFlag = false; // 是否有主产品变更
        //开始路由到地州库(route)
        IDataset eleIdList = new DatasetList();
        String productType = "";
        //子订单ID
        String subOrderId = orderInfo.getString("SUBORDER_ID");
        IDataset prodList = AbilityPlatOrderBean.queryProductorderInfo(subOrderId);
        for (int k = 0; k < prodList.size(); k++) {
            IDataset conOrder = AbilityPlatOrderBean.qryGerlProdContractBySubOrderId(subOrderId); // TODO 关联 TF_B_CTRM_GERLPRODUCT、TF_B_CTRM_GERLSUBORDER、TD_B_CTRM_CONTRACT
            if (IDataUtil.isEmpty(conOrder)) {
                conOrder = AbilityPlatOrderBean.qryGerlProdCommonBySubOrderId(subOrderId); // TODO 关联 TF_B_CTRM_GERLPRODUCT、TF_B_CTRM_GERLSUBORDER、TD_B_CTRM_COMMON_PRODUCT
            }

            IData prodInfo = prodList.getData(k);
             /* 查询产品对应关系表信息 */
            String ctrmProductId = prodInfo.getString("PRODUCT_ID"); // CTRM_PRODUCT_ID
            //产品ID得到本地映射的产品
            IDataset relaProducts = AbilityOpenPlatQry.queryListInfo(route, ctrmProductId);
            if (IDataUtil.isEmpty(relaProducts)) {
                ret.put("X_RSPCODE", "9999");
                ret.put("X_RSPDESC", "订单产品ID【" + ctrmProductId + "】不存在本地产品的映射关系，请管理员进行配置！");
                ret.put("X_RESULTCODE", "-1");
                ret.put("BIZ_CODE", "9999");
                ret.put("BIZ_DESC", "订单产品ID【" + ctrmProductId + "】不存在本地产品的映射关系，请管理员进行配置！");
                return ret;
            }

            IData relaProductsData = relaProducts.getData(0);

            IData eleInfo = new DataMap();
            orderInfo.put("EPARCHY_CODE", route);
            orderInfo.put("%101!", orderInfo.getString("CHANNEL_CODE", "")); // 业务订购渠道
            if (IDataUtil.isNotEmpty(conOrder)) { // TD_B_CTRM_CONTRACT里的CONTRACT_NAME相当于TD_B_CTRM_COMMON_PRODUCT的PRODUCT_NAME,add by cy
                String contractName = conOrder.getData(0).containsKey("CONTRACT_NAME") == true ? conOrder.getData(0).getString("CONTRACT_NAME", "") : conOrder.getData(0).getString("PRODUCT_NAME", "");
                orderInfo.put("%102!", contractName); //订购合约内容
                orderInfo.put("%103!", conOrder.getData(0).getString("ACCEPT_DATE", SysDateMgr.getSysTime())); // 订购时间
            }

            orderInfo.put("PARAM_CODE", relaProductsData.getString("PRODUCT_ID")); // PRODUCT_ID
            //能力开放平台产品编码类型,1-终端产品，2-合约产品，3-套餐及增值产品   其中1插表时已处理，2不做处理，3的时候调用产品变更接口
            if ("2".equals(relaProductsData.getString("CTRM_PRODUCT_TYPE"))) {
                if ("2".equals(orderInfo.getString("NEED_DISTRIBUTION"))) {
                    IDataset continfos = AbilityOpenPlatQry.queryContractInfo(ctrmProductId); // TODO 关联查询TD_B_CTRM_CONTRACT c,TD_B_CTRM_RELATION r
                    if (IDataUtil.isEmpty(continfos)) {
                        continfos = AbilityPlatOrderBean.getContractInfo(ctrmProductId);// TODO 关联查询TD_B_CTRM_COMMON_PRODUCT c,TD_B_CTRM_RELATION r
                    }
                    IData continfo = new DataMap();
                    if (IDataUtil.isNotEmpty(continfos)) {
                        continfo = continfos.getData(0);
                        //TD_B_CTRM_CONTRACT的EFFECT_TIME相当于TF_B_CTRM_ORDER_PRODUCT的RSRV_STR1
                        String effectTime = continfo.containsKey("EFFECT_TIME") == true ? continfo.getString("EFFECT_TIME", "1") : continfo.getString("RSRV_STR1", "1");
                        continfo.put("EFFECT_TIME", effectTime);
                    }
                    orderInfo.putAll(continfo);
                    orderInfo.put("PRDOUCT_TAG", "YX21");
                    orderInfo.put("PROVINCE_CODE", "QHAI");
                    orderInfo.put("RES_TYPE_CODE", "4");
                    orderInfo.put("ONNET_ACTIVE_TAG", "1");
                    orderInfo.put("ORDER_TYPE", continfo.getString("ORDER_TYPE", ""));
                    if ("".equals(orderInfo.getString("CAMPN_ID", ""))) {
                        orderInfo.put("CAMPN_ID", orderInfo.getString("PRODUCT_ID"));
                    }

                    //调合约计划的接口
                    orderInfo.put("PRODUCT_ID", relaProductsData.getString("PRODUCT_ID"));
                    orderInfo.put("PACKAGE_ID", relaProductsData.getString("PACKAGE_ID"));
                    orderInfo.put("SERIAL_NUMBER", orderInfo.getString("SERIAL_NUMBER"));
                    orderInfo.put("ACTION_TYPE", "0");
                    orderInfo.put("UNI_CHANNEL", uniChannelId);		//REQ201911120026新增入参UNI_CHANNEL
                    String msg = "";
                    IData retnData = new DataMap();
                    try {
                        log.error("=SS.SaleActiveTrade.tradeReg=orderInfo=:" + orderInfo);
                        IDataset reSet = CSAppCall.call("SS.SaleActiveTrade.tradeReg", orderInfo);
                        log.error("log_合约计划_reSet:" + reSet.toString());
                        if (IDataUtil.isNotEmpty(reSet)) {
                            retnData = reSet.getData(0);
                        }
                        if (IDataUtil.isEmpty(retnData)) {
                            retnData.put("X_CHECK_TAG", "-1");
                        }
                        msg = retnData.getString("X_RESULTINFO", "");
                        msg = msg.length() > 200 ? msg.substring(0, 200) : msg;
                        retnData.put("X_CHECK_INFO", msg);
                    } catch (Exception e) {
                        String me = e.getMessage();
                        retnData.put("X_CHECK_TAG", "-1");
                        if (me.length() > 125) {
                            retnData.put("X_CHECK_INFO", me.substring(0, 120));
                        } else {
                            retnData.put("X_CHECK_INFO", me);
                        }
                    }

                    if (!"-1".equals(retnData.getString("X_CHECK_TAG", ""))) {
                        ret.put("TRADE_ID", retnData.getString("TRADE_ID", ""));//记录
                        QueryInfoUtil.sendSMS(orderInfo);//发送短信
                    } else {
                        ret.put("BIZ_CODE", "2998");
                        ret.put("BIZ_DESC", "调合约计划失败!");
                        ret.put("X_RESULTCODE", "-1");
                        ret.put("X_RSPCODE", "2998");
                        ret.put("X_RSPDESC", retnData.getString("X_CHECK_INFO", "调合约计划失败"));
                        return ret;
                    }
                } else {
                    hasVasFlag = true;
                    continue;
                }
            } else if ("3".equals(relaProductsData.getString("CTRM_PRODUCT_TYPE"))) {
                productFlag = true;
                eleInfo.put("ELEMENT_ID", relaProductsData.getString("ELEMENT_ID"));
                if ("P".equals(relaProductsData.getString("ELEMENT_TYPE_CODE"))) {
                    eleInfo.put("ELEMENT_ID", relaProductsData.getString("PRODUCT_ID"));
                    mianproductFlag = true;
                }
                eleInfo.put("ELEMENT_TYPE_CODE", relaProductsData.getString("ELEMENT_TYPE_CODE"));
                eleInfo.put("MODIFY_TAG", "0");
                //视频流量包才需要校验    add
                productType = relaProductsData.getString("CTRM_PRODUCT_TYPE_CODE", "");
                if ("10200".equals(productType) || "10100".equals(productType)) {
                    try {
                        IData productInfo = new DataMap();
                        productInfo.put("PRODUCT_TYPE", productType);
                        productInfo.put("SERVICE_ID_LIST", relaProductsData.getString("CTRM_PRODUCT_SERVICEID", ""));
                        productInfo.put("PRODUCT_ID", relaProductsData.getString("CTRM_PRODUCT_ID", ""));
                        relaProductsData.putAll(productInfo);
                        //校验产品入参之间的关系，productType=102XX
                        AbilityRuleCheck.checkParamRelation(orderInfo.getString("SERIAL_NUMBER", ""), relaProducts, route);
                        //校验互斥关系以及拼数据
                        IData retData = AbilityRuleCheck.checkVideopckrule(orderInfo.getString("SERIAL_NUMBER"), relaProductsData, route);
                        if (IDataUtil.isNotEmpty(retData)) {
                            eleInfo.put("ATTR_PARAM", retData);
                        }
                    } catch (Exception e) {
                        ret.put("BIZ_CODE", "2998");
                        ret.put("BIZ_DESC", "订购失败!");
                        ret.put("X_RESULTCODE", "-1");
                        ret.put("X_RSPCODE", "2998");
                        ret.put("X_RSPDESC", e.getMessage());
                        return ret;
                    }
                }
                //视频流量包才需要校验  end
                eleIdList.add(eleInfo);
            }
        }

        IData pretnData = new DataMap();
        IDataset neweleList = new DatasetList();
        IData newedata = new DataMap();
        if (mianproductFlag) {
            for (int n = 0; n < eleIdList.size(); n++) {
                IData temp = eleIdList.getData(n);
                if ("P".equals(temp.getString("ELEMENT_TYPE_CODE"))) {
                    newedata = temp;
                } else {
                    neweleList.add(temp);
                }
            }
        } else {
            for (int n = 0; n < eleIdList.size(); n++) {
                IData temp = eleIdList.getData(n);
                if (n == 0) {
                    newedata = temp;
                } else {
                    neweleList.add(temp);
                }
            }
        }
        if (productFlag) {
            IDataset retnSet = new DatasetList();
            try {//如果处理失败也更改子订单状态
                IData infoParam = new DataMap();
                infoParam.putAll(orderInfo);
                infoParam.put("ELEMENT_ID", newedata.getString("ELEMENT_ID", ""));
                infoParam.put("ELEMENT_TYPE_CODE", newedata.getString("ELEMENT_TYPE_CODE", ""));
                infoParam.put("MODIFY_TAG", newedata.getString("MODIFY_TAG", ""));
//                 infoParam.put("ELEMENTS_STR", neweleList);
                infoParam.put("SERIAL_NUMBER", orderInfo.getString("SERIAL_NUMBER"));
                infoParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                infoParam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
                infoParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                infoParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                infoParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                //2019-04-15 产品变更参数新加  预约标志  0非预约
                infoParam.put("BOOKING_TAG", "0");
                if ("10200".equals(productType)) {
                    infoParam.put("PRODUCT_TYPE", productType);
                    IData attrInfo = newedata.getData("ATTR_PARAM");
                    infoParam.putAll(attrInfo);
                }
                //调多元素变更接口
                infoParam.put("UNI_CHANNEL", uniChannelId);		//REQ201911120026新增入参UNI_CHANNEL
                retnSet = CSAppCall.call("SS.ChangeProductIntfSVC.changeProduct", infoParam);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("-------SS.ChangeProductIntfSVC.changeProduct产品变更失败了-------" + e);
                }
                String me = e.getMessage();
                pretnData.put("X_CHECK_TAG", "-1");
                if (me.length() > 125) {
                    pretnData.put("X_CHECK_INFO", me.substring(0, 120));
                } else {
                    pretnData.put("X_CHECK_INFO", me);
                }
            }
            if ("-1".equals(pretnData.getString("X_CHECK_TAG"))) {
                ret.put("BIZ_CODE", "2998");
                ret.put("BIZ_DESC", "调产品变更失败!");
                ret.put("X_RESULTCODE", "-1");
                ret.put("X_RSPCODE", "2998");
                ret.put("X_RSPDESC", pretnData.getString("X_CHECK_INFO", "调产品变更失败"));
                return ret;
            } else {
                IDataset proOrder = AbilityPlatOrderBean.qryGerlProdVasBySubOrderId(subOrderId);  // TODO 关联 TF_B_CTRM_GERLPRODUCT、TF_B_CTRM_GERLSUBORDER、TD_B_CTRM_VAS
                if (IDataUtil.isEmpty(proOrder)) {//如果查不到，就查一遍TD_B_CTRM_COMMON_PRODUCT add by cy
                    proOrder = AbilityPlatOrderBean.qryGerlProdCommonBySubOrderId(subOrderId); // TODO 关联 TF_B_CTRM_GERLPRODUCT、TF_B_CTRM_GERLSUBORDER、TD_B_CTRM_COMMON_PRODUCT
                }
                if (proOrder != null && proOrder.size() > 0) {
                    String vasName = proOrder.getData(0).containsKey("VAS_NAME") == true ? proOrder.getData(0).getString("VAS_NAME", "") : proOrder.getData(0).getString("PRODUCT_NAME", "");
                    orderInfo.put("%102!", vasName); //订购合约内容,TD_B_CTRM_VAS的VAS_NAME相当于TD_B_CTRM_COMMON_PRODUCT的PRODUCT_NAME,add by cy
                    orderInfo.put("%103!", proOrder.getData(0).getString("ACCEPT_DATE", SysDateMgr.getSysTime())); //订购合约内容
                }
                ret.put("TRADE_ID", retnSet.getData(0).getString("TRADE_ID", ""));
                QueryInfoUtil.sendSMS(orderInfo);//发送短信
            }
        }
        // 如果没有合约产品且子订单都登记成功则修改子订单状态
        if (!hasVasFlag) {
            return ret;
        }
        return ret;
    }

    /**
     * 处理和家固话子订单数据
     *
     * @param orderInfo
     * @return ret
     * @author zhaohj3
     * @date 2019-5-24 15:41:08
     * @throws Exception
     */
    public IData createImsUser(IData orderInfo) throws Exception {
        IData ret = new DataMap();
        ret.put("BIZ_CODE", "0000");
        ret.put("BIZ_DESC", "订购完成!");
        String serialNumber = orderInfo.getString("SERIAL_NUMBER");
        String ftNo = orderInfo.getString("FT_NO");
        String areaCode = orderInfo.getString("AREA_CODE");
        String route = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber, route);
        if (IDataUtil.isEmpty(userInfo)) {
            ret.put("BIZ_CODE", "9999");
            ret.put("BIZ_DESC", "用户资料不存在!");
            return ret;
        }
        String uniChannelId = orderInfo.getString("UNI_CHANNEL_ID","");    //新增一级能开UNI_CHANNEL_ID全网编码
        //开始路由到地州库(route)
        IData eleInfo = new DataMap();
        //子订单ID
        String subOrderId = orderInfo.getString("SUBORDER_ID");
        IDataset prodList = AbilityPlatOrderBean.queryProductorderInfo(subOrderId);
        for (int k = 0; k < prodList.size(); k++) {
            IData prodInfo = prodList.getData(k);
             /* 查询产品对应关系表信息 */
            String ctrmProductId = prodInfo.getString("PRODUCT_ID"); // CTRM_PRODUCT_ID
            //产品ID得到本地映射的产品
            IDataset relaProducts = AbilityOpenPlatQry.queryListInfo(route, ctrmProductId);
            if (IDataUtil.isEmpty(relaProducts)) {
                ret.put("X_RSPCODE", "9999");
                ret.put("X_RSPDESC", "订单产品ID【" + ctrmProductId + "】不存在本地产品的映射关系，请管理员进行配置！");
                ret.put("X_RESULTCODE", "-1");
                ret.put("BIZ_CODE", "9999");
                ret.put("BIZ_DESC", "订单产品ID【" + ctrmProductId + "】不存在本地产品的映射关系，请管理员进行配置！");
                return ret;
            }
            IData relaProductsData = relaProducts.getData(0);

            orderInfo.put("EPARCHY_CODE", route);
            orderInfo.put("%101!", orderInfo.getString("CHANNEL_CODE", "")); // 业务订购渠道
            orderInfo.put("PARAM_CODE", relaProductsData.getString("PRODUCT_ID")); // PRODUCT_ID
            //能力开放平台产品编码类型,1-终端产品，2-合约产品，3-套餐及增值产品   其中1插表时已处理，2不做处理，3的时候调用产品变更接口
            if ("3".equals(relaProductsData.getString("CTRM_PRODUCT_TYPE"))) {
                eleInfo.put("ELEMENT_ID", relaProductsData.getString("PRODUCT_ID"));
                eleInfo.put("ELEMENT_TYPE_CODE", relaProductsData.getString("ELEMENT_TYPE_CODE"));
                eleInfo.put("MODIFY_TAG", "0");
            } else {
                ret.put("BIZ_CODE", "2998");
                ret.put("BIZ_DESC", "此产品的本地产品的映射关系配置非套餐及增值产品，中止订购，请检查本地产品的映射关系！");
                return ret;
            }
        }

        String productId = eleInfo.getString("ELEMENT_ID");
        
        //公共参数构造 写死的--吴坚
		getVisit().setStaffId("IBOSS001");
		getVisit().setDepartId("00309");
		getVisit().setCityCode("HNSJ");
		getVisit().setLoginEparchyCode("0898");
		getVisit().setStaffEparchyCode("0898");

        IData infoParam = new DataMap();
        
        IData checkAuthSerialNumdata = new DataMap();
        checkAuthSerialNumdata.put("AUTH_SERIAL_NUMBER", serialNumber);
        IDataset IData = CSAppCall.call("SS.IMSLandLineSVC.checkAuthSerialNum", checkAuthSerialNumdata);
        if(IData != null && IData.size() > 0){
        	String RESULT_CODE = IData.getData(0).getString("RESULT_CODE","");
        	String RESULT_INFO = IData.getData(0).getString("RESULT_INFO","");
        	if(RESULT_CODE.equals("0")){
        		ret.put("BIZ_CODE", "2998");
                ret.put("BIZ_DESC", RESULT_INFO);
                return ret;
        	}
        }
		String citycode = "";
    	//查出手机号码开的宽带地址，业务归属区
    	IDataset dataset1 = RelaUUInfoQry.qrySerialNumberBySnBAndRelationType(serialNumber,"47");
    	if(IDataUtil.isNotEmpty(dataset1)){
    		String userIdA = dataset1.first().getString("USER_ID_A"); 
    		IDataset dataset2 = RelaUUInfoQry.getRelationsByUserIdA("47", userIdA, "2");
			if("2".equals(dataset2.first().getString("ROLE_CODE_B"))){
				String user_id_b = dataset2.first().getString("USER_ID_B");
				IDataset dataset3 = WidenetInfoQry.getUserWidenetInfo(user_id_b);
				citycode = dataset3.first().getString("RSRV_STR4");
			}
    	}else{
    		ret.put("BIZ_CODE", "2998");
            ret.put("BIZ_DESC", "查询不到用户宽带信息！");
            return ret;
    	}
    	
		//选占
		IData userData = new DataMap();
        userData.put("FIX_NUMBER", areaCode + ftNo);
        userData.put("CITYCODE_RSRVSTR4", citycode);
        
        IDataset IDatacheckFixPhoneNum = CSAppCall.call("SS.IMSLandLineSVC.checkFixPhoneNum", userData);
        if(IDatacheckFixPhoneNum != null && IDatacheckFixPhoneNum.size() > 0){
        	String RESULT_CODE = IDatacheckFixPhoneNum.getData(0).getString("RESULT_CODE","");
        	String RESULT_INFO = IDatacheckFixPhoneNum.getData(0).getString("RESULT_INFO","");
        	if(RESULT_CODE.equals("-1")){
        		ret.put("BIZ_CODE", "2998");
                ret.put("BIZ_DESC", RESULT_INFO);
                return ret;
        	}
        }
        
        // 根据产品ID获取组合商品以及其对应的必选依赖元素，拼装SELECTED_ELEMENTS
        //IDataset selectedElements = buildSelectedElements(eleInfo);
        //infoParam.put("SELECTED_ELEMENTS", selectedElements.toString());
        
        IDataset productElements = ProductElementsCache.getProductElements(productId);//根据product_id获取该产品下的所有元素
        infoParam.put("SELECTED_ELEMENTS", productElements.toString());
        infoParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
        infoParam.put("SERIAL_NUMBER", serialNumber);
        infoParam.put("WIDE_SERIAL_NUMBER", areaCode + ftNo);
        infoParam.put("PRODUCT_ID", productId);
        infoParam.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
        infoParam.put("OPEN_IMS", "OPEN_IMS");//在主台账中给预留字段赋值，服开根据此字段来判断是否自动完工，不需要走外线工单
        infoParam.put("UNI_CHANNEL", uniChannelId);			//REQ201911120026新增入参UNI_CHANNEL

        IDataset retnSet = CSAppCall.call("SS.IMSLandLineRegSVC.tradeReg", infoParam);
        if (IDataUtil.isNotEmpty(retnSet)) {
            try {
                IDataset proOrder = AbilityPlatOrderBean.qryGerlProdVasBySubOrderId(subOrderId);  // TODO 关联 TF_B_CTRM_GERLPRODUCT、TF_B_CTRM_GERLSUBORDER、TD_B_CTRM_VAS
                if (IDataUtil.isEmpty(proOrder)) { // 如果查不到，就查一遍TD_B_CTRM_COMMON_PRODUCT add by cy
                    proOrder = AbilityPlatOrderBean.qryGerlProdCommonBySubOrderId(subOrderId); // TODO 关联 TF_B_CTRM_GERLPRODUCT、TF_B_CTRM_GERLSUBORDER、TD_B_CTRM_COMMON_PRODUCT
                }
                if (proOrder != null && proOrder.size() > 0) {
                    String vasName = proOrder.getData(0).containsKey("VAS_NAME") == true ? proOrder.getData(0).getString("VAS_NAME", "") : proOrder.getData(0).getString("PRODUCT_NAME", "");
                    orderInfo.put("%102!", vasName); //订购合约内容,TD_B_CTRM_VAS的VAS_NAME相当于TD_B_CTRM_COMMON_PRODUCT的PRODUCT_NAME,add by cy
                    orderInfo.put("%103!", proOrder.getData(0).getString("ACCEPT_DATE", SysDateMgr.getSysTime())); //订购合约内容
                }
                QueryInfoUtil.sendSMS(orderInfo);//发送短信
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("发送短信失败：" + e.getMessage());
                }
            }
            ret.put("TRADE_ID", retnSet.getData(0).getString("TRADE_ID", ""));
        }
        return ret;
    }

    /**
     * 关于优化电子渠道线上售卡能力的开发需求 - 手机号码和sim卡校验
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkPhoneAndSimCard(IData inputs) throws Exception {
    	IData input=new DataMap(inputs.toString());
        IData reqInfo = input.getData("REQ_INFO");
        String busiType = IDataUtil.chkParam(reqInfo, "BUSI_TYPE");
        if("40".equals(busiType)){
            String subBusiType = IDataUtil.chkParam(reqInfo, "SUB_BUSI_TYPE");
            if ("3".equals(subBusiType))
            {
                //携入上门写白卡
                return this.checkOAOWriteCardPhoneNP(inputs);
            }else {
                //1物流上门写白卡，2面对面写白卡
                return this.checkOAOWriteCardPhone(inputs);
            }
        }
        String tradsactionId = IDataUtil.chkParam(reqInfo, "TRANSACTION_ID");
        String serialNumber = IDataUtil.chkParam(reqInfo, "BILL_ID");
        String simCardNo = IDataUtil.chkParam(reqInfo, "SIM_CARD_NO");
        String orderId = IDataUtil.chkParam(reqInfo, "ORDER_ID");
        String subOrderId = IDataUtil.chkParam(reqInfo, "SUBORDER_ID");
        IData result = new DataMap();
        result.put("TRANSACTION_ID", tradsactionId);
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "成功");

        //订单状态是否正常 不是FA CA
        IDataset list = AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderId, subOrderId);
        if (IDataUtil.isEmpty(list)) {
            result.put("BIZ_CODE", "3057");
            result.put("BIZ_DESC", "根据订单号未查询到订单信息");
            return result;
        }
        
        //设置登录工号========================start
        //查询订单信息
        IDataset oaoOrderInfos= AbilityPlatOrderBean.queryOAOorderInfo(orderId, subOrderId);
        if (IDataUtil.isNotEmpty(oaoOrderInfos)) {
        	if(serialNumber.equals(oaoOrderInfos.getData(0).getString("BILL_ID"))
        			&&simCardNo.equals(oaoOrderInfos.getData(0).getString("SIM_CARD_NO"))){
        		result.put("CUST_CERT_NO", list.getData(0).getString("CERTIFICATE_NO", ""));
        	    result.put("CUST_NAME", list.getData(0).getString("LEGAL_NAME", ""));
                return result;
        	}else{
        		result.put("BIZ_CODE", "2999");
            	result.put("BIZ_DESC", "号码["+serialNumber+"] SIM卡["+simCardNo+"]与原订单["+orderId+"]不符");
            	return result;
        	}
        }
        IDataset mainOrderInfos=AbilityPlatOrderBean.queryOrderInfo(orderId);
        if (IDataUtil.isNotEmpty(mainOrderInfos)) {
        	String channelId=mainOrderInfos.getData(0).getString("CHANNEL_CODE");
        	String numberOprType=list.getData(0).getString("NUMBER_OPRTYPE");
        	IDataset oaoStaff=AbilityPlatOrderBean.queryOAOStaff(channelId, numberOprType);
        	if(IDataUtil.isNotEmpty(oaoStaff)){
        		getVisit().setStaffId(oaoStaff.getData(0).getString("STAFF_ID"));
        		getVisit().setDepartId(oaoStaff.getData(0).getString("DEPT_ID"));
        		getVisit().setCityCode(oaoStaff.getData(0).getString("CITY_CODE"));
        	}
        }else{
        	result.put("BIZ_CODE", "2999");
        	result.put("BIZ_DESC", "根据主订单号未查询到订单信息！");
	        return result;
	    }
        //设置登录工号========================end
        
        IData order = list.first();
        String state = order.getString("STATE");
        String idCard = order.getString("CERTIFICATE_NO", "");
        String idCardType = order.getString("CERTIFICATE_TYPE", "");
        String custName = order.getString("LEGAL_NAME", "");
        result.put("CUST_CERT_NO", idCard);
        result.put("CUST_NAME", custName);

        String createTime = order.getString("CREATE_TIME").substring(0,10);
        int intDay = SysDateMgr.dayInterval(SysDateMgr.getSysDate(),createTime);
        if(intDay>30){
            IData inParam = new DataMap();
            inParam.put("STATE", "FA");
            inParam.put("STATUS_DESC", "H05-超过省公司期限未激活关闭");
            inParam.put("ORDER_ID", orderId);
            inParam.put("SUBORDER_ID", subOrderId);
            updateStatus(inParam);
            result.put("BIZ_CODE", "3057");
            result.put("BIZ_DESC", "订单状态异常，不能激活");
            return result;
        }
        
        //号码是否可用  iccid是否可用
        /*try {
            IDataset dataset = ResCall.checkOpenOrderNumAndSimOAO(serialNumber, simCardNo);
            if (IDataUtil.isEmpty(dataset)) {
                result.put("BIZ_CODE", "2999");
                result.put("BIZ_DESC", "未查询到iccid或号码信息");
                return result;
            }
        } catch (Exception e) {
            result.put("BIZ_CODE", "2999");
            result.put("BIZ_DESC", "校验失败：：" + e.getMessage());
            return result;
        }*/
        
       //iccid是否可用
        try {
            IDataset dataset = ResCall.checkResourceForSim("0", serialNumber, simCardNo, "1");
            if (IDataUtil.isEmpty(dataset)) {
            	IData inParam = new DataMap();
                inParam.put("STATE", "AF");
                inParam.put("STATUS_DESC", "F08-号码与SIM卡局向不一致");
                inParam.put("ORDER_ID", orderId);
                inParam.put("SUBORDER_ID", subOrderId);
                updateStatus(inParam);
                
                result.put("BIZ_CODE", "4058");
                result.put("BIZ_DESC", "未查询到iccid信息");
                return result;
            }
        } catch (Exception e) {
        	IData inParam = new DataMap();
            inParam.put("STATE", "AF");
            inParam.put("STATUS_DESC", "F15-"+e.getMessage());
            inParam.put("ORDER_ID", orderId);
            inParam.put("SUBORDER_ID", subOrderId);
            updateStatus(inParam);
            
            result.put("BIZ_CODE", "2999");
            result.put("BIZ_DESC", "号码选占失败：" + e.getMessage());
            return result;
        }
       
        //一证五号校验
        if (StringUtils.isNotBlank(idCard) && StringUtils.isNotBlank(custName)) {
            NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
            input.put("IDCARD_TYPE", idCardType);
            input.put("CUSTOMER_NAME", custName);
            input.put("IDCARD_NUM", idCard);
            IDataset idCheck = bean.idCheck(input);
            if (IDataUtil.isNotEmpty(idCheck)) {
                int total = Integer.parseInt(idCheck.getData(0).getString("TOTAL", "0"));
                int untrustresult = idCheck.getData(0).getInt("UN_TRUST_RESULT", 0);
                if (untrustresult > 0)
                {
                    IData inParam = new DataMap();
                    inParam.put("STATE", "AF");
                    inParam.put("STATUS_DESC", "F10-一证五号不通过");
                    inParam.put("ORDER_ID", orderId);
                    inParam.put("SUBORDER_ID", subOrderId);
                    updateStatus(inParam);

                    result.put("BIZ_CODE", "23043");
                    result.put("BIZ_DESC", "开户人有不良信息，不满足开户条件，禁止开户");
                    return result;
                }
                if (total > 5) {
                	IData inParam = new DataMap();
                    inParam.put("STATE", "AF");
                    inParam.put("STATUS_DESC", "F10-一证五号不通过");
                    inParam.put("ORDER_ID", orderId);
                    inParam.put("SUBORDER_ID", subOrderId);
                    updateStatus(inParam);
                    
                    result.put("BIZ_CODE", "3006");
                    result.put("BIZ_DESC", "一证五号校验失败");
                    return result;
                }
            }
        }
        return result;
    }
    /**
     * 关于优化电子渠道线上售卡能力的开发需求 - OAO白卡写卡手机号码校验
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkOAOWriteCardPhone(IData inputs) throws Exception {
    	IData input=new DataMap(inputs.toString());
        IData reqInfo = input.getData("REQ_INFO");
        String busiType = IDataUtil.chkParam(reqInfo, "BUSI_TYPE");
        String tradsactionId = IDataUtil.chkParam(reqInfo, "TRANSACTION_ID");
        String serialNumber = IDataUtil.chkParam(reqInfo, "BILL_ID");
        //String simCardNo = IDataUtil.chkParam(reqInfo, "SIM_CARD_NO");
        String orderId = IDataUtil.chkParam(reqInfo, "ORDER_ID");
        String subOrderId = IDataUtil.chkParam(reqInfo, "SUBORDER_ID");
        IData result = new DataMap();
        result.put("TRANSACTION_ID", tradsactionId);
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "成功");

        //订单状态是否正常 不是FA CA
        IDataset list = AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderId, subOrderId);
        if (IDataUtil.isEmpty(list)) {
            result.put("BIZ_CODE", "3057");
            result.put("BIZ_DESC", "根据订单号未查询到订单信息");
            return result;
        }
        
        //设置登录工号========================start
        //查询订单信息
        IDataset oaoOrderInfos= AbilityPlatOrderBean.queryOAOwriteCardOrderInfo(orderId, subOrderId);
        if (IDataUtil.isNotEmpty(oaoOrderInfos)) {
        	if(serialNumber.equals(oaoOrderInfos.getData(0).getString("BILL_ID"))){
        		result.put("CUST_CERT_NO", list.getData(0).getString("CERTIFICATE_NO", ""));
        	    result.put("CUST_NAME", list.getData(0).getString("LEGAL_NAME", ""));
                return result;
        	}else{
        		result.put("BIZ_CODE", "2999");
            	result.put("BIZ_DESC", "号码["+serialNumber+"]与原订单["+orderId+"]不符");
            	return result;
        	}
        }
        IDataset mainOrderInfos=AbilityPlatOrderBean.queryOrderInfo(orderId);
        if (IDataUtil.isNotEmpty(mainOrderInfos)) {
        	String channelId=mainOrderInfos.getData(0).getString("CHANNEL_CODE");
        	String numberOprType=list.getData(0).getString("NUMBER_OPRTYPE");
        	IDataset oaoStaff=AbilityPlatOrderBean.queryOAOStaff(channelId, numberOprType);
        	if(IDataUtil.isNotEmpty(oaoStaff)){
        		getVisit().setStaffId(oaoStaff.getData(0).getString("STAFF_ID"));
        		getVisit().setDepartId(oaoStaff.getData(0).getString("DEPT_ID"));
        		getVisit().setCityCode(oaoStaff.getData(0).getString("CITY_CODE"));
        	}
        }else{
        	result.put("BIZ_CODE", "2999");
        	result.put("BIZ_DESC", "根据主订单号未查询到订单信息！");
	        return result;
	    }
        //设置登录工号========================end
        
        IData order = list.first();
        String state = order.getString("STATE");
        String idCard = order.getString("CERTIFICATE_NO", "");
        String idCardType = order.getString("CERTIFICATE_TYPE", "");
        String custName = order.getString("LEGAL_NAME", "");
        result.put("CUST_CERT_NO", idCard);
        result.put("CUST_NAME", custName);

        String createTime = order.getString("CREATE_TIME").substring(0,10);
        int intDay = SysDateMgr.dayInterval(SysDateMgr.getSysDate(),createTime);
        if(intDay>30){
            IData inParam = new DataMap();
            inParam.put("STATE", "FA");
            inParam.put("STATUS_DESC", "H05-超过省公司期限未激活关闭");
            inParam.put("ORDER_ID", orderId);
            inParam.put("SUBORDER_ID", subOrderId);
            updateStatus(inParam);
            result.put("BIZ_CODE", "3057");
            result.put("BIZ_DESC", "订单状态异常，不能激活");
            return result;
        }
       
        //一证五号校验
        if (StringUtils.isNotBlank(idCard) && StringUtils.isNotBlank(custName)) {
            NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
            input.put("IDCARD_TYPE", idCardType);
            input.put("CUSTOMER_NAME", custName);
            input.put("IDCARD_NUM", idCard);
            IDataset idCheck = bean.idCheck(input);
            if (IDataUtil.isNotEmpty(idCheck)) {
                int total = Integer.parseInt(idCheck.getData(0).getString("TOTAL", "0"));
                int untrustresult = idCheck.getData(0).getInt("UN_TRUST_RESULT", 0);
                if (untrustresult > 0) {
                    IData inParam = new DataMap();
                    inParam.put("STATE", "AF");
                    inParam.put("STATUS_DESC", "F10-一证五号不通过");
                    inParam.put("ORDER_ID", orderId);
                    inParam.put("SUBORDER_ID", subOrderId);
                    updateStatus(inParam);

                    result.put("BIZ_CODE", "23043");
                    result.put("BIZ_DESC", "开户人有不良信息，不满足开户条件，禁止开户");
                    return result;
                }
                if (total > 5) {
                	IData inParam = new DataMap();
                    inParam.put("STATE", "AF");
                    inParam.put("STATUS_DESC", "F10-一证五号不通过");
                    inParam.put("ORDER_ID", orderId);
                    inParam.put("SUBORDER_ID", subOrderId);
                    updateStatus(inParam);
                    
                    result.put("BIZ_CODE", "3006");
                    result.put("BIZ_DESC", "一证五号校验失败");
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 携入上门写卡，异网手机号sim卡校验
     * @param inputs
     * @return
     * @throws Exception
     */
    public IData checkOAOWriteCardPhoneNP(IData inputs) throws Exception {
        IData input=new DataMap(inputs.toString());
        IData reqInfo = input.getData("REQ_INFO");
        String busiType = IDataUtil.chkParam(reqInfo, "BUSI_TYPE");
        String tradsactionId = IDataUtil.chkParam(reqInfo, "TRANSACTION_ID");
        String serialNumber = IDataUtil.chkParam(reqInfo, "BILL_ID");
        //String simCardNo = IDataUtil.chkParam(reqInfo, "SIM_CARD_NO");
        String orderId = IDataUtil.chkParam(reqInfo, "ORDER_ID");
        String subOrderId = IDataUtil.chkParam(reqInfo, "SUBORDER_ID");
        IData result = new DataMap();
        result.put("TRANSACTION_ID", tradsactionId);
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "成功");

        //订单状态是否正常 不是FA CA
        IDataset list = AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderId, subOrderId);
        if (IDataUtil.isEmpty(list)) {
            result.put("BIZ_CODE", "3057");
            result.put("BIZ_DESC", "根据订单号未查询到订单信息");
            return result;
        }

        //设置登录工号========================start
        //查询订单信息
        IDataset oaoOrderInfos= AbilityPlatOrderBean.queryOAOwriteCardOrderInfoNp(orderId, subOrderId,null,null,null );
        if (IDataUtil.isNotEmpty(oaoOrderInfos)) {
            if(serialNumber.equals(oaoOrderInfos.getData(0).getString("BILL_ID"))){
                result.put("CUST_CERT_NO", list.getData(0).getString("CERTIFICATE_NO", ""));
                result.put("CUST_NAME", list.getData(0).getString("LEGAL_NAME", ""));
                return result;
            }else{
                result.put("BIZ_CODE", "2999");
                result.put("BIZ_DESC", "号码["+serialNumber+"]与原订单["+orderId+"]不符");
                return result;
            }
        }
        IDataset mainOrderInfos=AbilityPlatOrderBean.queryOrderInfo(orderId);
        if (IDataUtil.isNotEmpty(mainOrderInfos)) {
            String channelId=mainOrderInfos.getData(0).getString("CHANNEL_CODE");
            String numberOprType=list.getData(0).getString("NUMBER_OPRTYPE");
            IDataset oaoStaff=AbilityPlatOrderBean.queryOAOStaff(channelId, numberOprType);
            if(IDataUtil.isNotEmpty(oaoStaff)){
                getVisit().setStaffId(oaoStaff.getData(0).getString("STAFF_ID"));
                getVisit().setDepartId(oaoStaff.getData(0).getString("DEPT_ID"));
                getVisit().setCityCode(oaoStaff.getData(0).getString("CITY_CODE"));
            }
        }else{
            result.put("BIZ_CODE", "2999");
            result.put("BIZ_DESC", "根据主订单号未查询到订单信息！");
            return result;
        }
        //设置登录工号========================end

        IData order = list.first();
        String state = order.getString("STATE");
        String idCard = order.getString("CERTIFICATE_NO", "");
        String idCardType = order.getString("CERTIFICATE_TYPE", "");
        String custName = order.getString("LEGAL_NAME", "");
        result.put("CUST_CERT_NO", idCard);
        result.put("CUST_NAME", custName);

        String createTime = order.getString("CREATE_TIME").substring(0,10);
        int intDay = SysDateMgr.dayInterval(SysDateMgr.getSysDate(),createTime);
        if(intDay>30){
            IData inParam = new DataMap();
            inParam.put("STATE", "FA");
            inParam.put("STATUS_DESC", "H05-超过省公司期限未激活关闭");
            inParam.put("ORDER_ID", orderId);
            inParam.put("SUBORDER_ID", subOrderId);
            updateStatus(inParam);
            result.put("BIZ_CODE", "3057");
            result.put("BIZ_DESC", "订单状态异常，不能激活");
            return result;
        }

        //一证五号校验
        if (StringUtils.isNotBlank(idCard) && StringUtils.isNotBlank(custName)) {
            NationalOpenLimitBean bean = (NationalOpenLimitBean) BeanManager.createBean(NationalOpenLimitBean.class);
            input.put("IDCARD_TYPE", idCardType);
            input.put("CUSTOMER_NAME", custName);
            input.put("IDCARD_NUM", idCard);
            IDataset idCheck = bean.idCheck(input);
            if (IDataUtil.isNotEmpty(idCheck)) {
                int total = Integer.parseInt(idCheck.getData(0).getString("TOTAL", "0"));
                int untrustresult = idCheck.getData(0).getInt("UN_TRUST_RESULT", 0);
                if (untrustresult > 0) {
                    IData inParam = new DataMap();
                    inParam.put("STATE", "AF");
                    inParam.put("STATUS_DESC", "F10-一证五号不通过");
                    inParam.put("ORDER_ID", orderId);
                    inParam.put("SUBORDER_ID", subOrderId);
                    updateStatus(inParam);

                    result.put("BIZ_CODE", "23043");
                    result.put("BIZ_DESC", "开户人有不良信息，不满足开户条件，禁止开户");
                    return result;
                }
                if (total > 5) {
                    IData inParam = new DataMap();
                    inParam.put("STATE", "AF");
                    inParam.put("STATUS_DESC", "F10-一证五号不通过");
                    inParam.put("ORDER_ID", orderId);
                    inParam.put("SUBORDER_ID", subOrderId);
                    updateStatus(inParam);

                    result.put("BIZ_CODE", "3006");
                    result.put("BIZ_DESC", "一证五号校验失败");
                    return result;
                }
            }
        }

        //黑名单
        boolean checkBlackCust = UCustBlackInfoQry.isBlackCust("0", idCard);
        if (checkBlackCust) {
            result.put("BIZ_CODE",  "2999");
            result.put("BIZ_DESC",  "该用户有黑名单信息");
            return result;
        }
        //异网号码校验
        CreateNpUserTradeBean bean = BeanManager.createBean(CreateNpUserTradeBean.class);
        IData param = new DataMap();
        param.put("SERIAL_NUMBER",serialNumber);
        param.put("NP_BACK",0);
        try {
            bean.checkSerialNumber(param);
        } catch (Exception e) {
            result.put("BIZ_CODE", "2999");
            result.put("BIZ_DESC", e.getMessage());
            return result;
        }

        return result;
    }

    /**
     * 关于优化电子渠道线上售卡能力的开发需求 - 认证成功号码开户激活通知
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IData createUserAndActive(IData inputs) throws Exception {
    	
    	IData input=new DataMap(inputs.toString());
        IData reqInfo = input.getData("REQ_INFO");
        String busiType = IDataUtil.chkParam(reqInfo, "BUSI_TYPE");
        if("40".equals(busiType)){
        	return this.createWriteCardUserAndActive(inputs);
        }
        String simCardNo = IDataUtil.chkParam(reqInfo, "SIM_CARD_NO");
        String tradsactionId = IDataUtil.chkParam(reqInfo, "TRANSACTION_ID");
        String serialNumber = IDataUtil.chkParam(reqInfo, "BILL_ID");
        String orderId = IDataUtil.chkParam(reqInfo, "ORDER_ID");
        String subOrderId = IDataUtil.chkParam(reqInfo, "SUBORDER_ID");
        String custName = IDataUtil.chkParam(reqInfo, "CUST_NAME");
        String idCard = IDataUtil.chkParam(reqInfo, "CUST_CERT_NO");
        String address = IDataUtil.chkParam(reqInfo, "CUST_CERT_ADDR");
        String sex = IDataUtil.chkParam(reqInfo, "GENDER");
        String nation = IDataUtil.chkParam(reqInfo, "NATION");//民族
        String birthday = IDataUtil.chkParam(reqInfo, "BIRTHDAY");
        String issuingAuthority = IDataUtil.chkParam(reqInfo, "IS_SUING_AUTHORITY");
        String certValidDate = IDataUtil.chkParam(reqInfo, "CERT_VALID_DATE");
        String certExpDate = IDataUtil.chkParam(reqInfo, "CERT_EXP_DATE");
        String picNameZ = IDataUtil.chkParam(reqInfo, "PIC_NAME_Z");
        String picNameF = IDataUtil.chkParam(reqInfo, "PIC_NAME_F");
        String picNameR = IDataUtil.chkParam(reqInfo, "PIC_NAME_R");
        String route = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);
        IData ret = new DataMap();
        ret.put("BIZ_CODE", "0000");
        ret.put("BIZ_DESC", "成功");
        IData eleInfo = new DataMap();
        
        //设置登录工号========================start
        //查询订单信息
        IDataset orderInfos= AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderId, subOrderId);
        if (IDataUtil.isEmpty(orderInfos)) {
            ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "根据子订单号未查询到订单信息！");
            return ret;
        }
        IDataset mainOrderInfos=AbilityPlatOrderBean.queryOrderInfo(orderId);
        if (IDataUtil.isNotEmpty(mainOrderInfos)) {
        	String channelId=mainOrderInfos.getData(0).getString("CHANNEL_CODE");
        	String numberOprType=orderInfos.getData(0).getString("NUMBER_OPRTYPE");
        	IDataset oaoStaff=AbilityPlatOrderBean.queryOAOStaff(channelId, numberOprType);
        	if(IDataUtil.isNotEmpty(oaoStaff)){
        		getVisit().setStaffId(oaoStaff.getData(0).getString("STAFF_ID"));
        		getVisit().setDepartId(oaoStaff.getData(0).getString("DEPT_ID"));
        		getVisit().setCityCode(oaoStaff.getData(0).getString("CITY_CODE"));
        	}
        }else{
	       	 ret.put("BIZ_CODE", "2999");
	         ret.put("BIZ_DESC", "根据主订单号未查询到订单信息！");
	         return ret;
	    }
        //设置登录工号========================end
        
        //查询订单信息
        IDataset prodList = AbilityPlatOrderBean.queryProductorderInfo(subOrderId);
        if (IDataUtil.isEmpty(prodList)) {
            ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "根据子订单号未查询到产品信息！");
            return ret;
        }
        String productId="";
        String saleActiveId="";
        String saleActivePackageId="";
        for (int k = 0; k < prodList.size(); k++) {
            IData prodInfo = prodList.getData(k);
            /* 查询产品对应关系表信息 */
            String ctrmProductId = prodInfo.getString("PRODUCT_ID"); // CTRM_PRODUCT_ID
            //产品ID得到本地映射的产品
            IDataset relaProducts = AbilityOpenPlatQry.queryListInfo(route, ctrmProductId);
            if (IDataUtil.isEmpty(relaProducts)) {
                ret.put("BIZ_CODE", "2999");
                ret.put("BIZ_DESC", "订单产品ID【" + ctrmProductId + "】不存在本地产品的映射关系，请管理员进行配置！");
                return ret;
            }

            //能力开放平台产品编码类型,1-终端产品，2-合约产品，3-套餐及增值产品   其中1插表时已处理，2不做处理，3的时候调用产品变更接口
	        for(int i=0;i<relaProducts.size();i++){
	        	IData relaProductsData = relaProducts.getData(i);
            	if ("3".equals(relaProductsData.getString("CTRM_PRODUCT_TYPE"))
            			&&"P".equals(relaProductsData.getString("ELEMENT_TYPE_CODE"))) {
            		productId=relaProductsData.getString("PRODUCT_ID");
	                eleInfo.put("ELEMENT_ID", relaProductsData.getString("PRODUCT_ID"));
	                eleInfo.put("ELEMENT_TYPE_CODE", relaProductsData.getString("ELEMENT_TYPE_CODE"));
	                eleInfo.put("MODIFY_TAG", "0");
	            }else if("2".equals(relaProductsData.getString("CTRM_PRODUCT_TYPE"))){
	            	if(StringUtils.isNotEmpty(relaProductsData.getString("PRODUCT_ID"))
	            			&&(!("-1".equals(relaProductsData.getString("PRODUCT_ID"))))
	            			&&StringUtils.isNotEmpty(relaProductsData.getString("PACKAGE_ID"))
	            			&&(!("-1".equals(relaProductsData.getString("PACKAGE_ID"))))){
	            		saleActiveId= relaProductsData.getString("PRODUCT_ID");
	            		saleActivePackageId= relaProductsData.getString("PACKAGE_ID");
	            	}
	            } 
	        }
        }
        if(StringUtils.isEmpty(productId)){
        	 ret.put("BIZ_CODE", "2998");
             ret.put("BIZ_DESC", "此产品的本地产品的映射关系配置非套餐及增值产品，中止订购，请检查本地产品的映射关系！");
             return ret;
        }
        //拼装
       // IDataset selectedElements = buildSelectedElements(eleInfo);
        //客户信息
        
        IData cond = new DataMap();
        cond.put("CUST_NAME", custName);
        cond.put("PAY_NAME", custName);
        cond.put("PSPT_TYPE_CODE", "0");
        cond.put("PSPT_ID", idCard);
        cond.put("PSPT_ADDR", address);
        
        //设置民族
        IDataset nations=StaticUtil.getStaticList("TD_S_FOLK");
        if(IDataUtil.isNotEmpty(nations)){
        	for(int i=0;i<nations.size();i++){
        		if(nations.getData(i).getString("DATA_NAME","").indexOf(nation)>=0){
        			 cond.put("FOLK_CODE", nations.getData(i).getString("DATA_ID",""));
        			 break;
        		}
        	}
        }
        if(StringUtils.isEmpty(cond.getString("FOLK_CODE"))){
        	cond.put("FOLK_CODE","0");
        }

        cond.put("PSPT_END_DATE", certExpDate);
        cond.put("SIGNING_ORGANIZATION", issuingAuthority);
        cond.put("BIRTHDAY", birthday);
        cond.put("SEX", sex.equals("0")?"F":"M");
        cond.put("IS_RES_PREOCCUPIED", "1");//之前的流程做了预占
        //用户信息
        cond.put("SIM_CARD_NO", simCardNo);
        cond.put("SERIAL_NUMBER", serialNumber);
        //产品
        cond.put("PRODUCT_ID", eleInfo.getString("ELEMENT_ID"));
        //cond.put("SELECTED_ELEMENTS", selectedElements);
        cond.put("TRADE_TYPE_CODE", "10");
        cond.put("ORDER_TYPE_CODE", "10");
        
        //额外增加入参
        cond.put("USER_TYPE_CODE", "0");
        cond.put("PAY_MODE_CODE", "0");
        cond.put("PAY_MONEY_CODE", "0");
        cond.put("ACCT_DAY", "1");
        cond.put("USER_PASSWD", "000000");
        String brandCode = UProductInfoQry.getBrandCodeByProductId(eleInfo.getString("ELEMENT_ID"));
        cond.put("BRAND_CODE", brandCode);
        cond.put("TRADE_DEPART_PASSWD", "0");//孙鑫说没用到，但被校验不能空，所以随便赋值了
        cond.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        cond.put("ADVANCE_PAY", "-1");//预存款 2 0  为了避免出现多一条记录的情况，所以赋值为-1
        cond.put("OPER_FEE", "0");//卡费  0 10
        cond.put("FOREGIFT", "0");//押金费 1 0
        cond.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
      //  Boolean success = true;
        
        if(isAsync()){
	        //改为插表异步
	        IDataset oaoOrderInfos= AbilityPlatOrderBean.queryOAOorderInfo(orderId, subOrderId);
	        if(IDataUtil.isEmpty(oaoOrderInfos)){
	        	reqInfo.put("NATION", cond.getString("FOLK_CODE"));
	        	reqInfo.put("STATE", "0");
	        	reqInfo.put("EXEC_COUNT", "0");
	        	reqInfo.put("ACCEPT_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        	reqInfo.put("PRODUCT_ID", eleInfo.getString("ELEMENT_ID"));
	        	reqInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
	        	reqInfo.put("BRAND_CODE", brandCode);
	        	reqInfo.put("STAFF_ID", getVisit().getStaffId());
	        	reqInfo.put("DEPT_ID", getVisit().getDepartId());
	        	reqInfo.put("IN_MODE_CODE", getVisit().getInModeCode());
	        	reqInfo.put("RSRV_STR2", saleActiveId);
	        	reqInfo.put("RSRV_STR3", saleActivePackageId);
                reqInfo.put("TRANSACTION_ID", tradsactionId);
                reqInfo.put("PIC_NNAME_Z", picNameZ);
                reqInfo.put("PIC_NNAME_F", picNameF);
                reqInfo.put("PIC_NNAME_R", picNameR);
	        	Dao.insert("TF_F_OAOORDER_INFO", reqInfo, Route.CONN_CRM_CEN);
	        }else{
	        	String state=oaoOrderInfos.getData(0).getString("STATE");
	        	String exeState=oaoOrderInfos.getData(0).getString("EXEC_STATE");
	        	if("1".equals(state)&&"-1".equals(exeState)){
	        		AbilityPlatOrderBean.updateOAOorderInfo(orderId, subOrderId);
	        	}
	        }
        }else{//走同步
	        try {
	            //IDataset results = CSAppCall.call("SS.CreatePersonUserRegSVC.tradeReg", cond);
	            IDataset results = CSAppCall.call("SS.CreatePersonUserIntfSVC.tradeReg", cond);
	            
	            IData inParam = new DataMap();
	            inParam.put("STATE", "AC");
	            inParam.put("STATUS_DESC", "订购成功");
	            inParam.put("ORDER_ID", orderId);
	            inParam.put("SUBORDER_ID", subOrderId);
	            updateStatus(inParam);
	            
	        } catch (Exception e) {
	            ret.put("BIZ_CODE", "2998");
	            ret.put("BIZ_DESC", "开户或激活失败：" + e.getMessage());
	            
	            IData inParam = new DataMap();
	            inParam.put("STATE", "AF");
	            inParam.put("STATUS_DESC", "订购失败");
	            inParam.put("ORDER_ID", orderId);
	            inParam.put("SUBORDER_ID", subOrderId);
	            updateStatus(inParam);
	            
	            throw e;
	            //success = false;
	            //return ret;
	        }
        }
        return ret;
    }
    /**
     * 关于优化电子渠道线上售卡能力的开发需求 - OAO白卡写卡认证成功号码开户激活通知
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IData createWriteCardUserAndActive(IData inputs) throws Exception {

    	IData input=new DataMap(inputs.toString());
        IData reqInfo = input.getData("REQ_INFO");
        String busiType = IDataUtil.chkParam(reqInfo, "BUSI_TYPE");
        String subBusiType = IDataUtil.chkParam(reqInfo, "SUB_BUSI_TYPE");
        if ("3".equals(subBusiType)) {
            return createWriteCardUserAndActiveNP(inputs);
        }
        String tradsactionId = IDataUtil.chkParam(reqInfo, "TRANSACTION_ID");
    	//String simCardNo = IDataUtil.chkParam(reqInfo, "SIM_CARD_NO");
        String serialNumber = IDataUtil.chkParam(reqInfo, "BILL_ID");
        String orderId = IDataUtil.chkParam(reqInfo, "ORDER_ID");
        String subOrderId = IDataUtil.chkParam(reqInfo, "SUBORDER_ID");
        String custName = IDataUtil.chkParam(reqInfo, "CUST_NAME");
        String idCard = IDataUtil.chkParam(reqInfo, "CUST_CERT_NO");
        String address = IDataUtil.chkParam(reqInfo, "CUST_CERT_ADDR");
        String sex = IDataUtil.chkParam(reqInfo, "GENDER");
        String nation = IDataUtil.chkParam(reqInfo, "NATION");//民族
        String birthday = IDataUtil.chkParam(reqInfo, "BIRTHDAY");
        String issuingAuthority = IDataUtil.chkParam(reqInfo, "IS_SUING_AUTHORITY");
        String certValidDate = IDataUtil.chkParam(reqInfo, "CERT_VALID_DATE");
        String certExpDate = IDataUtil.chkParam(reqInfo, "CERT_EXP_DATE");
        String picNameZ = IDataUtil.chkParam(reqInfo, "PIC_NAME_Z");
        String picNameF = IDataUtil.chkParam(reqInfo, "PIC_NAME_F");
        String picNameR = IDataUtil.chkParam(reqInfo, "PIC_NAME_R");
        String route = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);
        IData ret = new DataMap();
        ret.put("BIZ_CODE", "0000");
        ret.put("BIZ_DESC", "成功");
        IData eleInfo = new DataMap();

        //设置登录工号========================start
        //查询订单信息
        IDataset orderInfos= AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderId, subOrderId);
        if (IDataUtil.isEmpty(orderInfos)) {
            ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "根据子订单号未查询到订单信息！");
            return ret;
        }
        IDataset mainOrderInfos=AbilityPlatOrderBean.queryOrderInfo(orderId);
        if (IDataUtil.isNotEmpty(mainOrderInfos)) {
        	String channelId=mainOrderInfos.getData(0).getString("CHANNEL_CODE");
        	String numberOprType=orderInfos.getData(0).getString("NUMBER_OPRTYPE");
        	IDataset oaoStaff=AbilityPlatOrderBean.queryOAOStaff(channelId, numberOprType);
        	if(IDataUtil.isNotEmpty(oaoStaff)){
        		getVisit().setStaffId(oaoStaff.getData(0).getString("STAFF_ID"));
        		getVisit().setDepartId(oaoStaff.getData(0).getString("DEPT_ID"));
        		getVisit().setCityCode(oaoStaff.getData(0).getString("CITY_CODE"));
        	}
        }else{
	       	 ret.put("BIZ_CODE", "2999");
	         ret.put("BIZ_DESC", "根据主订单号未查询到订单信息！");
	         return ret;
	    }
        //设置登录工号========================end

        //查询订单信息
        IDataset prodList = AbilityPlatOrderBean.queryProductorderInfo(subOrderId);
        if (IDataUtil.isEmpty(prodList)) {
            ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "根据子订单号未查询到产品信息！");
            return ret;
        }
        String productId="";
        String saleActiveId="";
        String saleActivePackageId="";
        for (int k = 0; k < prodList.size(); k++) {
            IData prodInfo = prodList.getData(k);
            /* 查询产品对应关系表信息 */
            String ctrmProductId = prodInfo.getString("PRODUCT_ID"); // CTRM_PRODUCT_ID
            //产品ID得到本地映射的产品
            IDataset relaProducts = AbilityOpenPlatQry.queryListInfo(route, ctrmProductId);
            if (IDataUtil.isEmpty(relaProducts)) {
                ret.put("BIZ_CODE", "2999");
                ret.put("BIZ_DESC", "订单产品ID【" + ctrmProductId + "】不存在本地产品的映射关系，请管理员进行配置！");
                return ret;
            }
            //能力开放平台产品编码类型,1-终端产品，2-合约产品，3-套餐及增值产品   其中1插表时已处理，2不做处理，3的时候调用产品变更接口
            for(int i=0;i<relaProducts.size();i++){
            	IData relaProductsData = relaProducts.getData(0);
	            if ("3".equals(relaProductsData.getString("CTRM_PRODUCT_TYPE"))
	            		&&"P".equals(relaProductsData.getString("ELEMENT_TYPE_CODE"))) {
	            	productId=relaProductsData.getString("PRODUCT_ID");
	                eleInfo.put("ELEMENT_ID", relaProductsData.getString("PRODUCT_ID"));
	                eleInfo.put("ELEMENT_TYPE_CODE", relaProductsData.getString("ELEMENT_TYPE_CODE"));
	                eleInfo.put("MODIFY_TAG", "0");
	            }else if("2".equals(relaProductsData.getString("CTRM_PRODUCT_TYPE"))){
	            	if(StringUtils.isNotEmpty(relaProductsData.getString("PRODUCT_ID"))
	            			&&(!("-1".equals(relaProductsData.getString("PRODUCT_ID"))))
	            			&&StringUtils.isNotEmpty(relaProductsData.getString("PACKAGE_ID"))
	            			&&(!("-1".equals(relaProductsData.getString("PACKAGE_ID"))))){
	            		saleActiveId= relaProductsData.getString("PRODUCT_ID");
	            		saleActivePackageId= relaProductsData.getString("PACKAGE_ID");
	            	}
	            } 
            }
        }
        if(StringUtils.isEmpty(productId)){
            ret.put("BIZ_CODE", "2998");
            ret.put("BIZ_DESC", "此产品的本地产品的映射关系配置非套餐及增值产品，中止订购，请检查本地产品的映射关系！");
            return ret;
        }
        //拼装
       // IDataset selectedElements = buildSelectedElements(eleInfo);
        //客户信息

        IData cond = new DataMap();
        cond.put("CUST_NAME", custName);
        cond.put("PAY_NAME", custName);
        cond.put("PSPT_TYPE_CODE", "0");
        cond.put("PSPT_ID", idCard);
        cond.put("PSPT_ADDR", address);

        //设置民族
        IDataset nations=StaticUtil.getStaticList("TD_S_FOLK");
        if(IDataUtil.isNotEmpty(nations)){
        	for(int i=0;i<nations.size();i++){
        		if(nations.getData(i).getString("DATA_NAME","").indexOf(nation)>=0){
        			 cond.put("FOLK_CODE", nations.getData(i).getString("DATA_ID",""));
        			 break;
        		}
        	}
        }
        if(StringUtils.isEmpty(cond.getString("FOLK_CODE"))){
        	cond.put("FOLK_CODE","0");
        }

        cond.put("PSPT_END_DATE", certExpDate);
        cond.put("SIGNING_ORGANIZATION", issuingAuthority);
        cond.put("BIRTHDAY", birthday);
        cond.put("SEX", sex.equals("0")?"F":"M");
        cond.put("IS_RES_PREOCCUPIED", "1");//之前的流程做了预占
        //用户信息
        cond.put("SIM_CARD_NO", "");
        cond.put("SERIAL_NUMBER", serialNumber);
        //产品
        cond.put("PRODUCT_ID", eleInfo.getString("ELEMENT_ID"));
        //cond.put("SELECTED_ELEMENTS", selectedElements);
        cond.put("TRADE_TYPE_CODE", "10");
        cond.put("ORDER_TYPE_CODE", "10");

        //额外增加入参
        cond.put("USER_TYPE_CODE", "0");
        cond.put("PAY_MODE_CODE", "0");
        cond.put("PAY_MONEY_CODE", "0");
        cond.put("ACCT_DAY", "1");
        cond.put("USER_PASSWD", "000000");
        String brandCode = UProductInfoQry.getBrandCodeByProductId(eleInfo.getString("ELEMENT_ID"));
        cond.put("BRAND_CODE", brandCode);
        cond.put("TRADE_DEPART_PASSWD", "0");//孙鑫说没用到，但被校验不能空，所以随便赋值了
        cond.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        cond.put("ADVANCE_PAY", "-1");//预存款 2 0  为了避免出现多一条记录的情况，所以赋值为-1
        cond.put("OPER_FEE", "0");//卡费  0 10
        cond.put("FOREGIFT", "0");//押金费 1 0
        cond.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
      //  Boolean success = true;

        //3、 调整【认证成功号码开户激活通知（CIP00122)接口】
        //集运上门写白卡订单（busiType业务类型=40：集运上门写白卡）：省公司接收到该请求，仅保存实名认证信息，不做开户激活，需等待写卡成功后， 接收到“写卡成功订单开户激活通知”再进行开户激活。
        //（1）新加表记录开户信息（TF_F_OAOWRITECARDORDER_INFO） STATE=0
        IDataset oaoOrderInfos= AbilityPlatOrderBean.queryOAOwriteCardOrderInfo(orderId, subOrderId);
        if(IDataUtil.isEmpty(oaoOrderInfos)){
        	reqInfo.put("NATION", cond.getString("FOLK_CODE"));
        	reqInfo.put("STATE", "0");
        	reqInfo.put("EXEC_COUNT", "0");
        	reqInfo.put("ACCEPT_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        	reqInfo.put("PRODUCT_ID", eleInfo.getString("ELEMENT_ID"));
        	reqInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        	reqInfo.put("BRAND_CODE", brandCode);
        	reqInfo.put("STAFF_ID", getVisit().getStaffId());
        	reqInfo.put("DEPT_ID", getVisit().getDepartId());
        	reqInfo.put("IN_MODE_CODE", getVisit().getInModeCode());
        	reqInfo.put("RSRV_STR2", saleActiveId);
        	reqInfo.put("RSRV_STR3", saleActivePackageId);
        	//1：上门写白卡,2：面对面写白卡,3：携号转入写白卡
			reqInfo.put("RSRV_STR9", subBusiType);
            reqInfo.put("TRANSACTION_ID", tradsactionId);
            reqInfo.put("PIC_NNAME_Z", picNameZ);
            reqInfo.put("PIC_NNAME_F", picNameF);
            reqInfo.put("PIC_NNAME_R", picNameR);
        	Dao.insert("TF_F_OAOWRITECARDORDER_INFO", reqInfo, Route.CONN_CRM_CEN);
        }else{
        	String state=oaoOrderInfos.getData(0).getString("STATE");
        	String exeState=oaoOrderInfos.getData(0).getString("EXEC_STATE");
        	if("3".equals(state)&&"-1".equals(exeState)){
        		AbilityPlatOrderBean.updateOAOwriteCardOrderInfo("2",orderId, subOrderId);
        	}
        }
        return ret;
    }
    /**
     * 关于优化电子渠道线上售卡能力的开发需求 -写卡成功订单开户激活通知（新增CIP00141）
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IData createUserAndOAOActive(IData inputs) throws Exception {
    	
    	IData input=new DataMap(inputs.toString());
        String busiType = IDataUtil.chkParam(input, "BUSI_TYPE");
        String subBusiType = "";
        if ("40".equals(busiType)) {
             subBusiType = IDataUtil.chkParam(input, "SUB_BUSI_TYPE");

        }
        //携入激活
        if ("3".equals(subBusiType)) {
            return this.createUserAndOAOActiveNP(input);
        }
        String tradsactionId = IDataUtil.chkParam(input, "TRANSACTION_ID");
        String serialNumber = IDataUtil.chkParam(input, "BILL_ID");
        String orderId = IDataUtil.chkParam(input, "ORDER_ID");
        String subOrderId = IDataUtil.chkParam(input, "SUBORDER_ID");
        String iccid = IDataUtil.chkParam(input, "ICCID");
        
        IData ret = new DataMap();
        ret.put("BIZ_CODE", "0000");
        ret.put("BIZ_DESC", "成功");
        //5、写卡成功订单开户激活通知（CIP00141）
        //（1）判断手机号是否一致
        //（2）入参ICCID与SIM_CARD_NO、和白卡号其中一个相等就通过
        //（3）更新TF_F_OAOWRITECARDORDER_INFO STATE=2
        IDataset orderInfos= AbilityPlatOrderBean.queryOAOwriteCardOrderInfo(orderId, subOrderId);
        if (IDataUtil.isEmpty(orderInfos)) {
            ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "根据子订单号未查询到订单信息！");
            return ret;
        }
        String billId = orderInfos.getData(0).getString("BILL_ID");
        String emptyCard = orderInfos.getData(0).getString("EMPTY_CARD");
        String simCardNo = orderInfos.getData(0).getString("SIM_CARD_NO");
        if(!serialNumber.equals(billId)){
        	ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "手机号码不一致！");
            return ret;
        }
        if(!iccid.equals(emptyCard) && !iccid.equals(simCardNo)){
        	ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "号码对应ICCID不一致！");
            return ret;
        }
        AbilityPlatOrderBean.updateOAOwriteCardOrderInfo("2",orderId, subOrderId);
        return ret;
    }

	/**
	 * 关于优化电子渠道线上售卡能力的开发需求 - OAO携入白卡写卡认证成功号码开户激活通知 CIP00122
	 *
	 * @param
	 * @return
	 * @throws Exception
	 */
	public IData createWriteCardUserAndActiveNP(IData inputs) throws Exception {

		IData input=new DataMap(inputs.toString());
		IData reqInfo = input.getData("REQ_INFO");
		String busiType = IDataUtil.chkParam(reqInfo, "BUSI_TYPE");
		String subBusiType = IDataUtil.chkParam(reqInfo, "SUB_BUSI_TYPE");
		String tradsactionId = IDataUtil.chkParam(reqInfo, "TRANSACTION_ID");
		//String simCardNo = IDataUtil.chkParam(reqInfo, "SIM_CARD_NO");
		String serialNumber = IDataUtil.chkParam(reqInfo, "BILL_ID");
		String orderId = IDataUtil.chkParam(reqInfo, "ORDER_ID");
		String subOrderId = IDataUtil.chkParam(reqInfo, "SUBORDER_ID");
		String custName = IDataUtil.chkParam(reqInfo, "CUST_NAME");
		String idCard = IDataUtil.chkParam(reqInfo, "CUST_CERT_NO");
		String address = IDataUtil.chkParam(reqInfo, "CUST_CERT_ADDR");
		String sex = IDataUtil.chkParam(reqInfo, "GENDER");
		String nation = IDataUtil.chkParam(reqInfo, "NATION");//民族
		String birthday = IDataUtil.chkParam(reqInfo, "BIRTHDAY");
		String issuingAuthority = IDataUtil.chkParam(reqInfo, "IS_SUING_AUTHORITY");
		String certValidDate = IDataUtil.chkParam(reqInfo, "CERT_VALID_DATE");
		String certExpDate = IDataUtil.chkParam(reqInfo, "CERT_EXP_DATE");
		String picNameZ = IDataUtil.chkParam(reqInfo, "PIC_NAME_Z");
		String picNameF = IDataUtil.chkParam(reqInfo, "PIC_NAME_F");
		String picNameR = IDataUtil.chkParam(reqInfo, "PIC_NAME_R");
		String route = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);
		IData ret = new DataMap();
		ret.put("BIZ_CODE", "0000");
		ret.put("BIZ_DESC", "成功");
		IData eleInfo = new DataMap();

		//设置登录工号========================start
		//查询订单信息
		IDataset orderInfos= AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderId, subOrderId);
		if (IDataUtil.isEmpty(orderInfos)) {
			ret.put("BIZ_CODE", "2999");
			ret.put("BIZ_DESC", "根据子订单号未查询到订单信息！");
			return ret;
		}
		IDataset mainOrderInfos=AbilityPlatOrderBean.queryOrderInfo(orderId);
		if (IDataUtil.isNotEmpty(mainOrderInfos)) {
			String channelId=mainOrderInfos.getData(0).getString("CHANNEL_CODE");
			String numberOprType=orderInfos.getData(0).getString("NUMBER_OPRTYPE");
			IDataset oaoStaff=AbilityPlatOrderBean.queryOAOStaff(channelId, numberOprType);
			if(IDataUtil.isNotEmpty(oaoStaff)){
				getVisit().setStaffId(oaoStaff.getData(0).getString("STAFF_ID"));
				getVisit().setDepartId(oaoStaff.getData(0).getString("DEPT_ID"));
				getVisit().setCityCode(oaoStaff.getData(0).getString("CITY_CODE"));
			}
		}else{
			ret.put("BIZ_CODE", "2999");
			ret.put("BIZ_DESC", "根据主订单号未查询到订单信息！");
			return ret;
		}
		//设置登录工号========================end

		//查询订单信息
		IDataset prodList = AbilityPlatOrderBean.queryProductorderInfo(subOrderId);
		if (IDataUtil.isEmpty(prodList)) {
			ret.put("BIZ_CODE", "2999");
			ret.put("BIZ_DESC", "根据子订单号未查询到产品信息！");
			return ret;
		}
		for (int k = 0; k < prodList.size(); k++) {
			IData prodInfo = prodList.getData(k);
			/* 查询产品对应关系表信息 */
			String ctrmProductId = prodInfo.getString("PRODUCT_ID"); // CTRM_PRODUCT_ID
			//产品ID得到本地映射的产品
			IDataset relaProducts = AbilityOpenPlatQry.queryListInfo(route, ctrmProductId);
			if (IDataUtil.isEmpty(relaProducts)) {
				ret.put("BIZ_CODE", "2999");
				ret.put("BIZ_DESC", "订单产品ID【" + ctrmProductId + "】不存在本地产品的映射关系，请管理员进行配置！");
				return ret;
			}
			IData relaProductsData = relaProducts.getData(0);

			//能力开放平台产品编码类型,1-终端产品，2-合约产品，3-套餐及增值产品   其中1插表时已处理，2不做处理，3的时候调用产品变更接口
			if ("3".equals(relaProductsData.getString("CTRM_PRODUCT_TYPE"))) {
				eleInfo.put("ELEMENT_ID", relaProductsData.getString("PRODUCT_ID"));
				eleInfo.put("ELEMENT_TYPE_CODE", relaProductsData.getString("ELEMENT_TYPE_CODE"));
				eleInfo.put("MODIFY_TAG", "0");
			} else {
				ret.put("BIZ_CODE", "2998");
				ret.put("BIZ_DESC", "此产品的本地产品的映射关系配置非套餐及增值产品，中止订购，请检查本地产品的映射关系！");
				return ret;
			}
		}
		//拼装
		// IDataset selectedElements = buildSelectedElements(eleInfo);
		//客户信息

		IData cond = new DataMap();
		cond.put("CUST_NAME", custName);
		cond.put("PAY_NAME", custName);
		cond.put("PSPT_TYPE_CODE", "0");
		cond.put("PSPT_ID", idCard);
		cond.put("PSPT_ADDR", address);

		//设置民族
		IDataset nations=StaticUtil.getStaticList("TD_S_FOLK");
		if(IDataUtil.isNotEmpty(nations)){
			for(int i=0;i<nations.size();i++){
				if(nations.getData(i).getString("DATA_NAME","").indexOf(nation)>=0){
					cond.put("FOLK_CODE", nations.getData(i).getString("DATA_ID",""));
					break;
				}
			}
		}
		if(StringUtils.isEmpty(cond.getString("FOLK_CODE"))){
			cond.put("FOLK_CODE","0");
		}

		cond.put("PSPT_END_DATE", certExpDate);
		cond.put("SIGNING_ORGANIZATION", issuingAuthority);
		cond.put("BIRTHDAY", birthday);
		cond.put("SEX", sex.equals("0")?"F":"M");
		cond.put("IS_RES_PREOCCUPIED", "1");//之前的流程做了预占
		//用户信息
		cond.put("SIM_CARD_NO", "");
		cond.put("SERIAL_NUMBER", serialNumber);
		//产品
		cond.put("PRODUCT_ID", eleInfo.getString("ELEMENT_ID"));
		//cond.put("SELECTED_ELEMENTS", selectedElements);
		cond.put("TRADE_TYPE_CODE", "10");
		cond.put("ORDER_TYPE_CODE", "10");

		//额外增加入参
		cond.put("USER_TYPE_CODE", "0");
		cond.put("PAY_MODE_CODE", "0");
		cond.put("PAY_MONEY_CODE", "0");
		cond.put("ACCT_DAY", "1");
		cond.put("USER_PASSWD", "000000");
		String brandCode = UProductInfoQry.getBrandCodeByProductId(eleInfo.getString("ELEMENT_ID"));
		cond.put("BRAND_CODE", brandCode);
		cond.put("TRADE_DEPART_PASSWD", "0");//孙鑫说没用到，但被校验不能空，所以随便赋值了
		cond.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
		cond.put("ADVANCE_PAY", "-1");//预存款 2 0  为了避免出现多一条记录的情况，所以赋值为-1
		cond.put("OPER_FEE", "0");//卡费  0 10
		cond.put("FOREGIFT", "0");//押金费 1 0
		cond.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

		//携入上门写白卡,subBusiType为3，新加表记录开户信息（TF_F_OAOWRITECARDORDER_INFO_NP） STATE=0
		IDataset  oaoOrderInfos = AbilityPlatOrderBean.queryOAOwriteCardOrderInfoNp(orderId, subOrderId,null,null,null );

		if(IDataUtil.isEmpty(oaoOrderInfos)){
			reqInfo.put("NATION", cond.getString("FOLK_CODE"));
			reqInfo.put("STATE", "0");
			reqInfo.put("EXEC_COUNT", "0");
			reqInfo.put("ACCEPT_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			reqInfo.put("PRODUCT_ID", eleInfo.getString("ELEMENT_ID"));
			reqInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
			reqInfo.put("BRAND_CODE", brandCode);
			reqInfo.put("STAFF_ID", getVisit().getStaffId());
			reqInfo.put("DEPT_ID", getVisit().getDepartId());
			reqInfo.put("IN_MODE_CODE", getVisit().getInModeCode());
			//1：上门写白卡,2：面对面写白卡,3：携号转入写白卡
			reqInfo.put("RSRV_STR1", subBusiType);
			IDataset selecments = SelfTerminalUtil.getElements(reqInfo.getString("PRODUCT_ID"));
			//sql增加字段 TODO
			reqInfo.put("SELECTED_ELEMENTS", selecments);
            reqInfo.put("TRANSACTION_ID", tradsactionId);
            reqInfo.put("PIC_NNAME_Z", picNameZ);
            reqInfo.put("PIC_NNAME_F", picNameF);
            reqInfo.put("PIC_NNAME_R", picNameR);
			Dao.insert("TF_F_OAOWRITECARDORDER_INFO_NP", reqInfo, Route.CONN_CRM_CEN);
		}else{
			String state=oaoOrderInfos.getData(0).getString("STATE");
			String exeState=oaoOrderInfos.getData(0).getString("EXEC_STATE");
			if("3".equals(state)&&"-1".equals(exeState)){
				//更新np表
				IData param = new DataMap();
				param.put("STATE", "0");
				param.put("ORDER_ID", orderId);
				param.put("SUBORDER_ID", subOrderId);
				AbilityPlatOrderBean.updateOAOwriteCardOrderInfoNP(param);
				//AbilityPlatOrderBean.updateOAOwriteCardOrderInfo("2",orderId, subOrderId);
			}

		}
		return ret;
	}

    /**
     * 携入上门写白卡，写卡成功通知
     * @param inputs
     * @return
     * @throws Exception
     */
    public IData createUserAndOAOActiveNP(IData inputs) throws Exception {

        IData input=new DataMap(inputs.toString());
        String busiType = IDataUtil.chkParam(input, "BUSI_TYPE");
        String tradsactionId = IDataUtil.chkParam(input, "TRANSACTION_ID");
        String serialNumber = IDataUtil.chkParam(input, "BILL_ID");
        String orderId = IDataUtil.chkParam(input, "ORDER_ID");
        String subOrderId = IDataUtil.chkParam(input, "SUBORDER_ID");
        String iccid = IDataUtil.chkParam(input, "ICCID");
        String authCode = IDataUtil.chkParam(input, "AUTH_CODE");
        String authCodeExpired = IDataUtil.chkParam(input, "EXPIRED_TIME");

        IData ret = new DataMap();
        ret.put("BIZ_CODE", "0000");
        ret.put("BIZ_DESC", "成功");
        //（1）判断手机号是否一致
        //（2）入参ICCID与SIM_CARD_NO、和白卡号其中一个相等就通过
        //（3）更新TF_F_OAOWRITECARDORDER_INFO_NP STATE=5
        IDataset orderInfos= AbilityPlatOrderBean.queryOAOwriteCardOrderInfoNp(orderId, subOrderId,"4",null,null );
        if (IDataUtil.isEmpty(orderInfos)) {
            ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "根据子订单号未查询到订单信息！");
            return ret;
        }
        String billId = orderInfos.getData(0).getString("BILL_ID");
        String emptyCard = orderInfos.getData(0).getString("EMPTY_CARD");
        String simCardNo = orderInfos.getData(0).getString("SIM_CARD_NO");
        if(!serialNumber.equals(billId)){
            ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "手机号码不一致！");
            return ret;
        }
        if(!iccid.equals(emptyCard) && !iccid.equals(simCardNo)){
            ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "号码对应ICCID不一致！");
            return ret;
        }
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", billId);
        param.put("AUTH_CODE", authCode);
        param.put("AUTH_CODE_EXPIRED", authCodeExpired);
        param.put("ROUTE_EPARCHY_CODE", "0898");
        param.put("PSPT_TYPE_CODE", "0");
        param.put("PSPT_ID", orderInfos.getData(0).get("CUST_CERT_NO"));
        //校验授权码是否有效
        try {
            IData checkResult = CSAppCall.call("SS.CreateNpUserTradeSVC.checkBeforeNpUser", param).getData(0);
            if (!"0".equals(checkResult.get("RESULT_CODE"))) {
                //授权码无效
                ret.put("BIZ_CODE", "2998");
                ret.put("BIZ_DESC", checkResult.get("RESULT_INFO"));
                return ret;
            }
        } catch (Exception e) {
            ret.put("BIZ_CODE", "2998");
            ret.put("BIZ_DESC", e.getMessage());
            return ret;
        }
        IData param2 = new DataMap();
        param2.put("STATE", "5");
        param2.put("ORDER_ID", orderId);
        param2.put("SUBORDER_ID", subOrderId);
        AbilityPlatOrderBean.updateOAOwriteCardOrderInfoNP(param2);
        return ret;
    }

    /**
     * 是否走异步
     * @return
     * @throws Exception
     */
    private boolean isAsync()throws Exception {
    	IDataset datas=StaticUtil.getStaticList("OAOOPEN_ASYNC");
    	if(IDataUtil.isEmpty(datas)){
    		return true;
    	}
    	if(IDataUtil.isEmpty(datas.getData(0))){
    		return true;
    	}
    	if("Y".equals(datas.getData(0).getString("DATA_NAME"))){
    		return true;
    	}
    	return false;
    }
    /**
     * 3.1.1.2	ICCID号段校验（CIP00118）
     * @param inputs
     * @return
     * @throws Exception
     */
    public IData checkICCID(IData inputs) throws Exception {
    	IData input=new DataMap(inputs.toString());
        String orderId = IDataUtil.chkParam(input, "ORDER_ID");
        String subOrderId = IDataUtil.chkParam(input, "SUBORDER_ID");
        String iccid = IDataUtil.chkParam(input, "ICCID");
        String serialNumber = IDataUtil.chkParam(input, "MOBILE_NUMBER");
        
        IData ret = new DataMap();
        ret.put("BIZ_CODE", "0000");
        ret.put("BIZ_DESC", "成功");
        
        //设置登录工号========================start
        //查询订单信息
        IDataset orderInfos= AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderId, subOrderId);
        if (IDataUtil.isEmpty(orderInfos)) {
            ret.put("BIZ_CODE", "2999");
            ret.put("BIZ_DESC", "根据子订单号未查询到订单信息！");
            return ret;
        }
        IDataset mainOrderInfos=AbilityPlatOrderBean.queryOrderInfo(orderId);
        if (IDataUtil.isNotEmpty(mainOrderInfos)) {
        	String channelId=mainOrderInfos.getData(0).getString("CHANNEL_CODE");
        	String numberOprType=orderInfos.getData(0).getString("NUMBER_OPRTYPE");
        	IDataset oaoStaff=AbilityPlatOrderBean.queryOAOStaff(channelId, numberOprType);
        	if(IDataUtil.isNotEmpty(oaoStaff)){
        		getVisit().setStaffId(oaoStaff.getData(0).getString("STAFF_ID"));
        		getVisit().setDepartId(oaoStaff.getData(0).getString("DEPT_ID"));
        	}
        }else{
        	 ret.put("BIZ_CODE", "2999");
             ret.put("BIZ_DESC", "根据主订单号未查询到订单信息！");
             return ret;
        }
        //设置登录工号========================end
        
        IData cond=new DataMap();
        cond.put("SERIAL_NUMBER", serialNumber);
        cond.put("SIM_CARD_NO", iccid);
        CSAppCall.call("SS.CreatePersonUserSVC.checkSimCardNo", cond);
        
        return ret;
    }
    /**
     * 关于优化电子渠道线上售卡能力的开发需求 - 订单状态通知省侧
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IData notifyOrderStatusProv(IData inputs) throws Exception {
    	IData input=new DataMap(inputs.toString());
        String orderId = IDataUtil.chkParam(input, "ORDER_ID");
        String subOrderId = IDataUtil.chkParam(input, "SUBORDER_ID");
        String status = IDataUtil.chkParam(input, "STATUS");
        String phoneNo = IDataUtil.chkParam(input, "PHONE_NUMBER");
        IData inParam = new DataMap();
        inParam.put("STATE", status);
        inParam.put("ORDER_ID", orderId);
        inParam.put("SUBORDER_ID", subOrderId);
        inParam.put("STATUS_DESC", input.getString("STATUS_DESC"));
        inParam.put("OP_CONTACT_NO", input.getString("OP_CONTACT_NO", ""));
        IData shipmentInfo = input.getData("SHIPMENT_INFO");
        if ("SE".equals(status)&&IDataUtil.isNotEmpty(shipmentInfo)) {
            inParam.put("SHIPMENT_COMPONY_CODE", shipmentInfo.getString("SHIPMENT_COMPONY_CODE", ""));
            inParam.put("SHIPMENT_COMPONY", shipmentInfo.getString("SHIPMENT_COMPONY", ""));
            inParam.put("SHIPMENT_NO", shipmentInfo.getString("SHIPMENT_NO", ""));
        } else if ("IN".equals(status)) {
            inParam.put("OP_CONTACT_NAME", input.getString("OP_CONTACT_NAME", ""));
            inParam.put("OP_CONTACT_NO", input.getString("OP_CONTACT_NO", ""));
            inParam.put("OP_CONTACT_PHONE", input.getString("OP_CONTACT_PHONE", ""));
            inParam.put("OP_COMMENTS", input.getString("OP_COMMENTS", ""));
        }
        IData result = new DataMap();
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "同步成功");
        try {
            updateStatus(inParam);
        } catch (Exception e) {
            result.put("BIZ_CODE", "2998");
            result.put("BIZ_DESC", "同步失败");
            log.debug(e.getMessage());
        }
        return result;
    }

    /**
     * 更新订单状态
     *
     * @param inData
     * @throws Exception
     */
    private void updateStatus(IData inData) throws Exception {

        DBConnection conn = new DBConnection("cen1", true, false);
        try {
            inData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            inData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

            SQLParser parser = new SQLParser(inData);
            parser.addSQL(" UPDATE TF_B_CTRM_GERLSUBORDER SET ");
            parser.addSQL(" STATE      = :STATE, ");
            parser.addSQL(" SHIPMENT_COMPANY_CODE      = :SHIPMENT_COMPONY_CODE, ");
            parser.addSQL(" SHIPMENT_NO      = :SHIPMENT_NO, ");
            parser.addSQL(" UPDATE_TIME = SYSDATE, ");
            parser.addSQL(" RSRV_STR1 = '' ");
            
            if(StringUtils.isNotEmpty(inData.getString("STATUS_DESC"))){
            	parser.addSQL(" ,STATUS_DESC = :STATUS_DESC ");
            }
            if(StringUtils.isNotEmpty(inData.getString("OP_CONTACT_NO"))){
            	parser.addSQL(" ,OP_CONTACT_NO = :OP_CONTACT_NO ");
            }
            
            parser.addSQL(" WHERE ORDER_ID = :ORDER_ID  AND SUBORDER_ID = :SUBORDER_ID ");

            CrmDAO dao = DAOManager.createDAO(CrmDAO.class, Route.CONN_CRM_CEN);
            dao.executeUpdate(conn, parser.getSQL(), parser.getParam());

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
        } finally {
            conn.close();
        }
    }

    /**
     * 关于优化电子渠道线上售卡能力的开发需求 - 订单激活状态查询
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryOrderStatus(IData input) throws Exception {
        String orderId = IDataUtil.chkParam(input, "ORDER_ID");
        String subOrderId = IDataUtil.chkParam(input, "SUBORDER_ID");
        String phoneNo = IDataUtil.chkParam(input, "SUBORDER_ID");
        IData result = new DataMap();
        result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        IDataset list = AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderId, subOrderId);
        if (IDataUtil.isEmpty(list)) {
            result.put("BIZ_CODE", "0016");
            result.put("BIZ_DESC", "无订单数据");
            return result;
        } else {
            result.put("BIZ_CODE", "0000");
            result.put("BIZ_DESC", "查询成功");
            String state = list.getData(0).getString("STATE");
            if ("AC".equals(state)) {
                result.put("IS_ACTIVED", "1");
            } else if("AF".equals(state)){
                result.put("IS_ACTIVED", "3");
            }else{
            	result.put("IS_ACTIVED", "2");
            }
            result.put("STATUS", list.getData(0).getString("STATE"));
            result.put("STATUS_DESC", list.getData(0).getString("STATUS_DESC"));
            return result;
        }
    }

    private void updateInfo(IData inData) throws Exception {

        DBConnection conn = new DBConnection("cen1", true, false);
        try {
            inData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            inData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

            SQLParser parser = new SQLParser(inData);
            parser.addSQL(" UPDATE TF_B_CTRM_GERLSUBORDER SET ");
            parser.addSQL(" STATE      = :STATE, ");
            parser.addSQL(" TRADE_ID   = :TRADE_ID, ");
            parser.addSQL(" UPDATE_STAFF_ID   = :UPDATE_STAFF_ID, ");
            parser.addSQL(" UPDATE_DEPART_ID   = :UPDATE_DEPART_ID, ");
            parser.addSQL(" RESULT_INFO   = :RESULT_INFO, ");
            parser.addSQL(" RESULT_CODE   = :RESULT_CODE, ");
            parser.addSQL(" RESULT_DETAIL   = :RESULT_DETAIL, ");
            parser.addSQL(" REMARK   = :REMARK, ");
            parser.addSQL(" RSRV_STR1   = null, ");
            parser.addSQL(" UPDATE_TIME = SYSDATE ");
            parser.addSQL(" WHERE ORDER_ID = :ORDER_ID  AND SUBORDER_ID = :SUBORDER_ID ");

            CrmDAO dao = DAOManager.createDAO(CrmDAO.class, Route.CONN_CRM_CEN);
            dao.executeUpdate(conn, parser.getSQL(), parser.getParam());

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
        } finally {
            conn.close();
        }
    }

    private void buildPrintData(IData syncOrder, IData printData, IData subscriber) throws Exception {
        printData.put("PAY_NAME", syncOrder.getString("INVOICE_TITLE"));
        String strPrintId = SeqMgr.getPrintId(); // 新生成发票PRINT_ID
        printData.put("PRINT_ID", strPrintId);
        printData.put("TYPE", "P0001"); // 票据类型：发票P0001、收据P0002、免填单(业务受理单)P0003
        printData.put("CUST_TYPE", "PERSON");//客户类型
        printData.put("APPLY_CHANNEL", "0");// 开票发起渠道：0-营业个人业务;1-集团有ACCTID业务；2-集团无ACCTID业务；3-账务
        printData.put("TOTAL_MONEY", syncOrder.getString("TOTAL_FEE"));
        printData.put("ABILITY", "1");
        printData.put("TAG", "NKFP");
    }
    
  //OAO售卡-实名认证人工审核状态通知
    public IData notifyStopMobileInfo(IData input) throws Exception {
        IData result = new DataMap();
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "成功");
        IDataUtil.chkParam(input, "BUSI_CODE");
        IDataUtil.chkParam(input, "SOURCE_CODE");
        IDataUtil.chkParam(input, "TARGET_CODE");
        IDataUtil.chkParam(input, "VERSION");
        IDataUtil.chkParam(input, "PROVINCE_CODE");
        IDataUtil.chkParam(input, "REQ_INFO");
        IData reqInfo = input.getData("REQ_INFO");
        String transactionID = IDataUtil.chkParam(reqInfo, "TRANSACTION_ID");
        String billId = IDataUtil.chkParam(reqInfo, "BILL_ID");
        String busiType = IDataUtil.chkParam(reqInfo, "BUSI_TYPE");
        String orderId = IDataUtil.chkParam(reqInfo, "ORDER_ID");
        String suborderId = IDataUtil.chkParam(reqInfo, "SUBORDER_ID");
        String auditStatus = IDataUtil.chkParam(reqInfo, "AUDIT_STATUS");
        String nowTime = SysDateMgr.getSysDateYYYYMMDDHHMMSS();

        IData param = new DataMap();
        //必填字段
        param.put("TRANSACTION_ID", transactionID);//流水号
        param.put("SERIAL_NUMBER", billId);//手机号
        param.put("BUSI_TYPE", busiType);//业务类型
        param.put("SUB_BUSI_TYPE", input.getString("SUB_BUSI_TYPE",""));//子业务类型
        param.put("ORDER_ID", orderId);//订单号
        param.put("SUB_ORDER_ID", suborderId);//子订单号
        param.put("AUDIT_STATUS", auditStatus);//验证结果
        param.put("CREATE_TIME", nowTime);//创建时间
        param.put("UPDATE_TIME", nowTime);//更新时间

        //非必填字段
        param.put("CHANNEL_ID", reqInfo.getString("CHANNEL_ID"));//渠道
        param.put("AUDIT_MESSAGE", reqInfo.getString("AUDIT_MESSAGE"));//审核结果描述

        try {
            AbilityPlatOrderBean.insertStopMobile(param);
        } catch (Exception e) {
            result.put("BIZ_CODE", "2998");
            result.put("BIZ_DESC", e.getMessage());
            return result;
        }
        return result;
    }

    //OAO售卡-复审异常停机通知
    public IData synStopMobileState(IData input) throws Exception {
        IData result = new DataMap();
        IData param1 = new DataMap();
        param1.put("PARAM_NAME", "crm.ABILITY.UP");
        IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
        String Abilityurl = "";
        if (Abilityurls != null && Abilityurls.size() > 0) {
            Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
        } else {
            CSAppException.appError("-1", "crm.ABILITY.UP接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = Abilityurl;
        //能力编码
        //String abilityCode = "";
        //String appId = StaticUtil.getStaticValue("ABILITY_APP_ID", "");//应用ID
        IData param = new DataMap();
        param.put("STOP_FLAG", "1");
        IDataset datalist = AbilityPlatOrderBean.qryAllNotifyFlagIsNull(param);
        if(IDataUtil.isNotEmpty(datalist)){
            for (int i = 0; i < datalist.size(); i++) {
                IData data = datalist.getData(i);
                IData abilityData = new DataMap();
                abilityData.put("orderId", data.getString("ORDER_ID"));
                abilityData.put("subOrderId", data.getString("SUB_ORDER_ID"));
                abilityData.put("serviceNo", data.getString("SERVICE_NO"));
                abilityData.put("status", data.getString("STATUS"));
                abilityData.put("statusDesc", data.getString("STATUS_DESC"));
                abilityData.put("updateTime", data.getString("UPDATE_TIME"));
                try {
                    result = AbilityEncrypting.callAbilityPlatCommon(apiAddress, abilityData);
                    IData notifyFlag = new DataMap();
                    notifyFlag.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                    notifyFlag.put("NOTIFY_FLAG", "1");
                    AbilityPlatOrderBean.updateStopMobileBySn(notifyFlag);
                }catch (Exception e){
                    if (log.isDebugEnabled()) {
                        log.debug(">>>>>>>>>>>>>复审异常停机通知 上发能开报错:" + e.getMessage());
                    }
                }
            }
        }
        return result;
    }

    /**
     * CIP00145	携转授权码查验请求 新增
     */
    public IData checkNpCode(IData input) throws Exception {
//        String serviceType = IDataUtil.chkParam(input, "SERVICE_TYPE");
        String authReqId = IDataUtil.chkParam(input, "AUTH_REQ_ID");
        String orderId = IDataUtil.chkParam(input, "ORDER_ID");
        String subOrderId = IDataUtil.chkParam(input, "SUBORDER_ID");
        String provinceCode = IDataUtil.chkParam(input, "PROVINCE_CODE");
        String billId = IDataUtil.chkParam(input, "BILL_ID");
        String authCode = IDataUtil.chkParam(input, "AUTH_CODE");
        String expiredTime = IDataUtil.chkParam(input, "EXPIRED_TIME");
        IData result = new DataMap();
        //根据订单查询身份证号
        //订单状态是否正常 不是FA CA
        IDataset list = AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderId, subOrderId);
        if (IDataUtil.isEmpty(list)) {
            result.put("BIZ_CODE", "2998");
            result.put("BIZ_DESC", "根据订单号未查询到订单信息");
            return result;
        }
        IData order = list.first();
        String idCard = order.getString("CERTIFICATE_NO", "");

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", billId);
        param.put("PSPT_TYPE_CODE", "0");
        param.put("PSPT_ID", idCard);
        param.put("AUTH_CODE", authCode);


//        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int year = Integer.parseInt(expiredTime.substring(0, 4));
        int month = Integer.parseInt(expiredTime.substring(4, 6));
        int day = Integer.parseInt(expiredTime.substring(6, 8));
        int hrs = Integer.parseInt(expiredTime.substring(8, 10));
        int min = Integer.parseInt(expiredTime.substring(10, 12));

        param.put("AUTH_CODE_EXPIRED", sdf.format(sdf.parse(year + "-" + month + "-" + day + " " + hrs + ":" + min + ":00" )));
//        param.put("AUTH_CODE_EXPIRED", "2019-11-18 15:12:00");
        //授权码查验接口

        IData checkResult =  CSAppCall.call("SS.CheckBeforeNpUserSVC.insUserNpCheck",param).getData(0);
        if ("0".equals(checkResult.get("RESULT_CODE"))) {
            //查验成功,更新授权码，失效时间和authReqId交易流水，改状态为1
            IData param2 = new DataMap();
            param2.put("STATE", "1");
            param2.put("ORDER_ID", orderId);
            param2.put("SUBORDER_ID", subOrderId);
            param2.put("AUTH_CODE", authCode);
            param2.put("EXPIRED_TIME", expiredTime);
            param2.put("AUTH_REQ_ID", authReqId);
            AbilityPlatOrderBean.updateOAOwriteCardOrderInfoNP(param2);
            result.put("BIZ_CODE", "0000");
            result.put("BIZ_DESC", "请求成功");

        } else {
            //查验失败
            result.put("BIZ_CODE", "2998");
            result.put("BIZ_DESC", "查验失败");
        }


        return result;
    }

    /**
     * CIP00146	携转授权码查验结果反馈 新增 gtm定时调用它
     */
    public void checkNpCodeReturn(IData input) throws Exception {
        IData result = new DataMap();
        String authCode = "";
        String authCodeExpired = "";
        String custCertNo = "";
        String billId = "";
        String authReqId = "";
        String orderId = "";
        String subOrderId = "";

        IDataset list= AbilityPlatOrderBean.queryOAOwriteCardOrderInfoNp(null,null,"1",null,null);
        if (!IDataUtil.isEmpty(list)) {
            for (int i = 0, size = list.size(); i < size; i++)
            {
                authCode = list.getData(i).getString("AUTH_CODE");
                authCodeExpired = list.getData(i).getString("EXPIRED_TIME");
                custCertNo = list.getData(i).getString("CUST_CERT_NO");
                billId = list.getData(i).getString("BILL_ID");
                authReqId = list.getData(i).getString("AUTH_REQ_ID");
                orderId = list.getData(i).getString("ORDER_ID");
                subOrderId = list.getData(i).getString("SUBORDER_ID");
                IData param2 = new DataMap();
//                param2.put("AUTH_CODE", authCode);
//                param2.put("EXPIRED_TIME", authCodeExpired);
//                param2.put("AUTH_REQ_ID", authReqId);
                param2.put("ORDER_ID", orderId);
                param2.put("SUBORDER_ID", subOrderId);
                IData param = new DataMap();
                param.put("SERIAL_NUMBER", billId);
                param.put("AUTH_CODE", authCode);
                param.put("AUTH_CODE_EXPIRED", authCodeExpired);
                param.put("ROUTE_EPARCHY_CODE", "0898");
                param.put("PSPT_TYPE_CODE", "0");
                param.put("PSPT_ID", custCertNo);
                //校验授权码是否通过接口
                try {
                    IData checkResult = CSAppCall.call("SS.CreateNpUserTradeSVC.checkBeforeNpUser", param).getData(0);
                    if ("查验结果未返回，请稍后".equals(checkResult.get("RESULT_INFO"))) {
                        param2.put("STATE","1");
                        param2.put("EXEC_RESULT","查验结果未返回，请稍后");
                        AbilityPlatOrderBean.updateOAOwriteCardOrderInfoNP(param2);
                        continue;
                    }
                    if ("0".equals(checkResult.get("RESULT_CODE"))) {
                        //状态2查验成功
                        param2.put("STATE", "2");
                        result.put("BIZ_CODE", "0000");
                        result.put("BIZ_DESC", "查验成功");
                    } else {
                        //状态3查验失败
                        param2.put("STATE", "3");
                        param2.put("EXEC_RESULT", checkResult.get("RESULT_INFO"));
                        result.put("BIZ_CODE", "2998");
                        result.put("BIZ_DESC", checkResult.get("RESULT_INFO"));
                    }
                } catch (Exception e) {
                    param2.put("STATE","1");
                    param2.put("EXEC_RESULT","服务调用异常" + e.getMessage());
                    AbilityPlatOrderBean.updateOAOwriteCardOrderInfoNP(param2);
                    continue;
                }

                //反向请求出去
                IData param3 = new DataMap();
                param3.put("AUTH_REQ_ID", authReqId);
                param3.put("BILL_ID", billId);
                param3.put("ORDER_ID", orderId);
                param3.put("SUBORDER_ID", subOrderId);
                param3.put("BIZ_CODE", result.getString("BIZ_CODE"));
                param3.put("BIZ_DESC", result.getString("BIZ_DESC"));

                String apiAddress = BizEnv.getEnvString("crm.ABILITY.CIP00146");
                log.info("=============crm.ABILITY.CIP00146==========="+apiAddress);
                log.info("=============crm.ABILITY.CIP00146==========="+param3.toString());

                IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,param3);
                log.info("=============crm.ABILITY.CIP00146=====return======"+stopResult.toString());
                String resCode=stopResult.getString("resCode");
                String resMsg=stopResult.getString("resMsg");
                if (!"00000".equals(resCode)) {
                    param2.put("STATE", "3");
                    param2.put("EXEC_RESULT", "反向调用能开接口失败：" + resMsg);
                } else {
                    param2.put("EXEC_RESULT", "反向调用能开接口成功：" + resMsg);
                }
                AbilityPlatOrderBean.updateOAOwriteCardOrderInfoNP(param2);
            }

        }
    }
}