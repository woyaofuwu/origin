
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.BizVisit;
import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.biz.service.BizRoute;
import com.ailk.bizcommon.set.util.DataSetUtils;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.request.Wade3ClientRequest;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class HwTerminalCall
{
	protected static Logger log = Logger.getLogger(HwTerminalCall.class);
    /**
     * @Function:
     * @Description: 合约计划终端IEMI校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-29 上午11:04:13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-29 chengxf2 v1.0.0 修改原因
     */
    /*public static IDataset checkTerminalByTerminalId(String resNo, String resTypeCode, String staffId) throws Exception
    {
        IData param = new DataMap();

        param.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo");
        param.put("RES_TYPE_CODE", resTypeCode); // 资源类型
        param.put("RES_ID", ""); // 资源实物编码
        param.put("RES_NO", resNo); // 资源号
        param.put("X_GETMODE", "0"); // 只做查询，不预占
        param.put("STAFF_ID", staffId);

        return TerminalCall.callHwTerminal("ITF_MONNI", param);
    }*/

    public static IDataset getTerminalByDeviceMode(String deviceModelCode, String resTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("RES_TRADE_CODE", "IGetTerminalAllInfo");
        param.put("RES_TYPE_CODE", "4"); // 资源类型
        param.put("X_GETMODE", "0");
        param.put("TERMINAL_MODEL_CODE", deviceModelCode); // 终端类型
        param.put("RES_TYPE_ID", resTypeCode); // 机型编码

        return HwTerminalCall.callRes("RCF.resource.ITermIntfQuerySV.queryModelByCode",param);//TerminalCall.callHwTerminal("ITF_MONNI", param);
    }
    
    public static IDataset getTerminalByImei(String imei, String qryType) throws Exception
    {
        IData param = new DataMap();

        param.put("IMEI", imei);
        param.put("QRY_TYPE", qryType); 

        return HwTerminalCall.callRes("RCF.resource.ITermIntfQuerySV.queryTermByImei",param);//TerminalCall.callHwTerminal("ITF_MONNI", param);
    }
    
    
    public static IDataset getTerminalInfoByTerminalId(String resNo, String staffId, String serialNumber, String saleTag) throws Exception
    {
        IData param = new DataMap();

        param.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo");
        param.put("RES_TYPE_CODE", "4"); // 资源类型
        param.put("RES_ID", ""); // 资源实物编码
        param.put("RES_NO", resNo); // 资源号
        param.put("OCCUPY_FLAG", "OCCUPY"); // 只做查询，不预占
        param.put("STAFF_ID", staffId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("INFO_TAG", "1");
        if(StringUtils.isNotBlank(saleTag))
        {
        	param.put("SALE_TAG", saleTag);
        }
        return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.preSelTerm", param);//TerminalCall.callHwTerminal("ITF_MONNI", param);
    }

    public static IDataset getTerminalInfoByTerminalId(String resNo, String staffId, String serialNumber) throws Exception
    {
        IData param = new DataMap();

        param.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo");
        param.put("RES_TYPE_CODE", "4"); // 资源类型
        param.put("RES_ID", ""); // 资源实物编码
        param.put("RES_NO", resNo); // 资源号
        param.put("OCCUPY_FLAG", "OCCUPY"); // 只做查询，不预占
        param.put("STAFF_ID", staffId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("INFO_TAG", "1");

        return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.preSelTerm", param);//TerminalCall.callHwTerminal("ITF_MONNI", param);
    }

    public static IDataset occupyTerminalByTerminalId(IData params) throws Exception
    {
        params.put("RES_TRADE_CODE", "IMobileDeviceModifyState");

        return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.saleTermChangeState", params);//TerminalCall.callHwTerminal("ITF_MONNI", params);
    }
    
    /**
     * 供PBOSS调用
     * @param params
     * @return
      * @author: lijun17
     * @date: 2016-5-30 下午7:41:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 7:41:42 lijun17 v1.0.0 修改原因
     */
    public static IDataset occupyTerminalByTerminalIdForApp(IData params) throws Exception
    {
        params.put("RES_TRADE_CODE", "IMobileDeviceModifyState");

        return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.saleTermChangeState", params);//TerminalCall.callHwTerminalForApp("ITF_MONNI", params);
    }

    
    public static IDataset preOccupyTerminalByTerminalId(String resNo, String saleStaffId, String serialNumber, String saleTag, String netOrderId) throws Exception
    {
        IData param = new DataMap();

        param.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo");
        param.put("Reserve_id", netOrderId);
        param.put("RES_TYPE_CODE", "4"); // 资源类型
        param.put("RES_ID", ""); // 资源实物编码
        param.put("RES_NO", resNo); // 资源号
        param.put("STAFF_ID", saleStaffId); // 促销员工
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("INFO_TAG", "1");
        if(StringUtils.isNotBlank(saleTag))
        {
        	param.put("SALE_TAG", saleTag);
        }

        return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.preSelTerm", param);//TerminalCall.callHwTerminal("ITF_MONNI", param);
    }

    /**
     * @Function: querySetTopBox()
     * @Description: 机顶盒查询
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-30 下午2:41:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-30 yxd v1.0.0 修改原因
     */
    public static IDataset querySetTopBox(String serialNumber, String resNo) throws Exception
    {
        IData param = new DataMap();

        param.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo"); // 业务类型
        param.put("RES_TYPE_CODE", "4"); // 资源类型 4
        param.put("RES_ID", ""); // 资源实物编码 空
        param.put("RES_NO", resNo); // 校验的终端串号
        param.put("STAFF_ID", CSBizBean.getVisit().getStaffId()); // 终端的归属工号
        param.put("SERIAL_NUMBER", serialNumber); // 办理业务手机号码
        param.put("INFO_TAG", "1"); // 分支标示 1
        param.put("OCCUPY_FLAG", "");
        param.put("SALE_TAG", ""); // 本次业务销售终端的类型 1: 买断过一次,再优惠购机. 2: 虚拟供货的再优惠购机. 不传:普通终端优惠购机

       return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.preSelTerm", param);//TerminalCall.callHwTerminal("ITF_MONNI", param);
    }
    
    /*
     * 光猫退还需求 REQ201703230019
     */
    public static IDataset querySetTopBox1(String serialNumber, String resNo) throws Exception
    {
        IData param = new DataMap();

        param.put("RES_TRADE_CODE", "IBusiCheck"); // 业务类型
        param.put("RES_TYPE_ID", "4"); // 资源类型 4
        //param.put("RES_ID", ""); // 资源实物编码 空
        param.put("RES_NO", resNo); // 校验的终端串号
        //param.put("STAFF_ID", CSBizBean.getVisit().getStaffId()); // 终端的归属工号
        //param.put("SERIAL_NUMBER", serialNumber); // 办理业务手机号码
        //param.put("INFO_TAG", "1"); // 分支标示 1
        //param.put("OCCUPY_FLAG", "");
        //param.put("SALE_TAG", ""); // 本次业务销售终端的类型 1: 买断过一次,再优惠购机. 2: 虚拟供货的再优惠购机. 不传:普通终端优惠购机
        throw new Exception("哪操作出来的？？？？");
//        return TerminalCall.callHwTerminal("ITF_MONNI", param);
    }
    
    
    /*
     * 光猫归还前增加校验规则需求
     */
    public static IDataset addModem(IData data) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo"); // 业务类型
        param.put("RES_TYPE_CODE", "4"); // 资源类型 4
        param.put("RES_ID", ""); // 资源实物编码 空
        param.put("STAFF_ID", CSBizBean.getVisit().getStaffId()); // 终端的归属工号
        param.put("INFO_TAG", "1"); // 分支标示 1
        param.put("OCCUPY_FLAG", "");
        param.put("SALE_TAG", ""); // 本次业务销售终端的类型 1: 买断过一次,再优惠购机. 2: 虚拟供货的再优惠购机. 不传:普通终端优惠购机
        return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.preSelTerm", param);//TerminalCall.callHwTerminal("ITF_MONNI", param);
    }
    
    /**
     * @Function: querySetTopBoxForApp()
     * @Description: 机顶盒查询供PBOSS调用
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijun17
     * @date: 2016-5-30 下午7:41:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 7:41:42 lijun17 v1.0.0 修改原因
     */
    public static IDataset querySetTopBoxForApp(IData inParam) throws Exception
    {
    	IData param = new DataMap();
        param.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo"); // 业务类型
        param.put("RES_TYPE_CODE", "4"); // 资源类型 4
        param.put("RES_NO", inParam.getString("RES_ID")); // 校验的终端串号
        param.put("RES_ID", ""); // 资源实物编码 空
        param.put("STAFF_ID", inParam.getString("TRADE_STAFF_ID","SUPERUSR")); // 终端的归属工号
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER")); // 办理业务手机号码
        param.put("INFO_TAG", "1"); // 分支标示 1
        param.put("OCCUPY_FLAG", "");
        param.put("SALE_TAG", ""); // 本次业务销售终端的类型 1: 买断过一次,再优惠购机. 2: 虚拟供货的再优惠购机. 不传:普通终端优惠购机
        param.put("TRADE_STAFF_ID", inParam.getString("TRADE_STAFF_ID"));
        param.put("TRADE_DEPART_ID", inParam.getString("TRADE_DEPART_ID"));
        param.put("IN_MODE_CODE", inParam.getString("IN_MODE_CODE"));
        param.put("TRADE_CITY_CODE", inParam.getString("TRADE_CITY_CODE"));
        return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.preSelTerm", param);//TerminalCall.callHwTerminalForApp("ITF_MONNI", param);
    }

    
    public static IDataset releaseTerminalByTerminalId(IData params) throws Exception
    {
        params.put("RES_TRADE_CODE", "IMobileDeviceModifyState");

        return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.saleTermChangeState", params);//TerminalCall.callHwTerminal("ITF_MONNI", params);
    }

    /**
     * @param 终端库存查询接口
     * @param resTradeCode
     * @param resTypeCode
     * @param terminalModelCode
     * @param cityCode
     * @return
     * @throws Exception
     */
    public static IDataset saleActiveTerminalQry(String resTradeCode, String resTypeCode, String terminalModelCode, String cityCode) throws Exception
    {
        IData inData = new DataMap();
        inData.put("RES_TRADE_CODE", resTradeCode);
        inData.put("RES_TYPE_CODE", resTypeCode);
        inData.put("TERMINAL_MODEL_CODE", terminalModelCode);
        inData.put("CITY_CODE", cityCode);
        return HwTerminalCall.callRes("RCF.resource.ITermIntfQuerySV.queryTermStockNumByCounty", inData);//TerminalCall.callHwTerminal("ITF_MONNI", inData);
    }

    /**
     * @Function: saleOrChange4SetTopBox()
     * @Description: 机顶盒销售和换机接口
     * @param:param : 主台帐数据
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-30 下午2:51:36 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-30 yxd v1.0.0 修改原因
     */
    public static IDataset saleOrChange4SetTopBox(IData param) throws Exception
    {
        IData data = new DataMap();
        String actionType = param.getString("RSRV_STR1"); // 0:购买 1:换机
        String isOccupyTopsetbox = param.getString("RSRV_STR2",""); //进行变更的时候是否进行机顶盒的实占
        String newBoxId = param.getString("RSRV_STR6", ""); // 新机顶盒ID
        String oldBoxId = param.getString("RSRV_STR8", ""); // 原机顶盒ID
        
        if (StringUtils.equals("0", actionType))
        {
            double dfee = Double.valueOf(param.getString("RSRV_STR7", "0")) * 100; // 以分为单位
            int fee = (int) dfee;
            data.put("RES_NO", param.getString("RSRV_STR6"));
            data.put("REMARK", param.getString("REMARK"));
            data.put("PARA_VALUE1", param.getString("SERIAL_NUMBER"));
            data.put("SALE_FEE", fee);
            data.put("PARA_VALUE3", param.getString("CAMPN_ID"));
            data.put("PARA_VALUE4", param.getString("USER_ID"));
            data.put("PARA_VALUE5", SysDateMgr.END_DATE_FOREVER);
            data.put("PARA_VALUE7", "0");
            data.put("DEVICE_COST", fee);
            data.put("PARA_VALUE8", "0");
            data.put("PRODUCT_ID", "-1");
            data.put("PACKAGE_ID", "-1");
            data.put("TRADE_ID", param.getString("TRADE_ID"));
            data.put("SALE_TAG", "");
            data.put("X_CHOICE_TAG", actionType);
            data.put("RES_TYPE_CODE", "4");
            data.put("INFO_TAG", "1");
            data.put("CONTRACT_ID", param.getString("ORDER_ID"));
            data.put("PRODUCT_MODE", "-1");
            data.put("X_RES_NO_S", param.getString("RSRV_STR6"));
            data.put("X_RES_NO_E", param.getString("RSRV_STR6"));
            data.put("PARA_VALUE10", param.getString("RSRV_STR5"));
            data.put("PARA_VALUE11", param.getString("EXEC_TIME"));
            data.put("PARA_VALUE12", param.getString("TRADE_DEPART_ID"));
            data.put("PARA_VALUE13", "0");
            data.put("PARA_VALUE14", fee);
            data.put("PARA_VALUE15", "0");
            data.put("PARA_VALUE16", "0");
            data.put("PARA_VALUE17", fee);
            data.put("PARA_VALUE18", fee);
            data.put("PARA_VALUE9", "-1");
            data.put("PREVALUE1", "-1");
            data.put("PARA3", "0");
            data.put("PARA_VALUE6", "-1");
            data.put("PARA1", "-1");
            data.put("PARA2", "-1");
            data.put("PARA_VALUE1", param.getString("SERIAL_NUMBER"));
            data.put("USER_NAME", param.getString("CUST_NAME"));
            data.put("STAFF_ID", param.getString("TRADE_STAFF_ID"));
            data.put("RES_TRADE_CODE", "IMobileDeviceModifyState");
            
            return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.saleTermChangeState", data);//TerminalCall.callHwTerminal("ITF_MONNI", data);
        }
        else if (StringUtils.equals("1", actionType)&& (isOccupyTopsetbox.equals("2")||isOccupyTopsetbox.equals("3") )&& !StringUtils.equals(newBoxId, oldBoxId))
        {
            data.put("NEW_IMEI", param.getString("RSRV_STR6", ""));
            data.put("OLD_IMEI", param.getString("RSRV_STR8", ""));
            data.put("RES_TYPE_CODE", "4");
            data.put("RES_TRADE_CODE", "IAgentExchSale");
            data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
            
            return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.termExchSale", data);//TerminalCall.callHwTerminal("ITF_MONNI", data);
        }else{
        	return new DatasetList();
        }
        
    }
    
    /**
     * 5.26	IPHONE6裸机销售校验
     */
    public static IDataset iphone6CheckIMEISaleInfo(String iphone6_imei,String sale_tag) throws Exception
    {
    	IData param = new DataMap();
        param.put("RES_TRADE_CODE", "IChkIMEISaleInfo");
        param.put("RES_TYPE_CODE", "4");
        param.put("INV_ID", iphone6_imei);
        param.put("CHECK_TYPE", "CHKSALE2IPHONE6");//校验类型: CHKSALE2IPHONE6   Iphone6逻辑销售校验      其他待扩充
        param.put("RESERVE_TYPE", "IPHONE6_RESERVE");//预占类型：  空，不需要预占 IPHONE6_RESERVE ，iphone6合约预占
        param.put("SALE_TAG", sale_tag);//SALE_TAG 本次业务销售终端的类型   1：代表买断类   2：代表非买断
        throw new Exception("IChkIMEISaleInfo在哪操作出来的？");
//        return TerminalCall.callHwTerminal("ITF_MONNI", param);
    }
    /**
     * 5.27	IPHONE6裸机后合约办理完工
     */
    public static IDataset iContractImeiDeal(String iphone6_imei,String serial_number,String priv_id,String package_id) throws Exception
    {
    	IData param = new DataMap();
        param.put("RES_TRADE_CODE", "IContractImeiDeal");
        param.put("RES_TYPE_CODE", "4");
        param.put("INV_ID", iphone6_imei);//终端串号
        param.put("SERIAL_NUMBER", serial_number);//服务号码
        param.put("OBJECT_TYPE", "ACTION");//对象类型:固定填写”ACTION” ACTION 合约活动
        param.put("PRIV_ID", priv_id);//营销活动编码，即产品编码
        param.put("PACKAGE_ID", package_id);//产品包编码
        throw new Exception("IContractImeiDeal在哪操作出来的？");
//        return TerminalCall.callHwTerminal("ITF_MONNI", param);
    }

	
	 /**
    * 根据终端价格和终端生产厂商查询终端
    * @param startPrice
    * @param endPrice
    * @param factorCode
    * @return
    * @throws Exception
    */
   public static IDataset getTerminalByPriceOrBrand(IData input) throws Exception
   {
       IData param = new DataMap();
	    
       param.put("RES_TRADE_CODE", "ITF_CRM_GetResInfoByPrice");
       param.put("RES_TYPE_CODE", "4"); // 资源类型
       param.put("X_BRAND_NAME", input.getString("FACTOR_CODE"));
       param.put("X_STARTPRICE", input.getString("STARTPRICE"));
       param.put("X_ENDPRICE", input.getString("ENDPRICE"));

       return HwTerminalCall.callRes("RCF.resource.ITermIntfQuerySV.getTermInfoByPrice", param);//TerminalCall.callHwTerminal("ITF_MONNI", param);
   }
   
   
   /**
    * 退机顶盒接口
    * @param startPrice
    * @param endPrice
    * @param factorCode
    * @return
    * @throws Exception
    */
   public static IDataset returnTopSetBoxTerminal(IData input) throws Exception
   {	   
       input.put("RES_TRADE_CODE", "IMobileDeviceModifyState");
//       param.put("RES_NO", input.getString("RES_NO"));
//       param.put("PARA_VALUE1", input.getString("SERIAL_NUMBER"));
//       param.put("TRADE_ID", input.getString("TRADE_ID"));
       //param.put("X_CHANGEDATE", "");
       
//       param.put("RES_TYPE_CODE", "4"); // 资源类型
//       param.put("X_GETMODE", "0");
//       param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
//       param.put("RES_CODE", "");
//       param.put("Reserve_id", "");
       return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.saleTermChangeState", input);//TerminalCall.callHwTerminal("ITF_MONNI", input);
   }
   
   
   public static IDataset callHwRtnTerminalIntf(String svcName, IData inparams) throws Exception
   {
       boolean dev = BizEnv.getEnvBoolean("hw.terminal.mgr.dev", false);

       IDataset result = new DatasetList();

       String url = BizEnv.getEnvString("hw.terminal.mgr.url");

       inparams.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
       inparams.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
       inparams.put("TRADE_STAFF_ID", inparams.getString("TRADE_STAFF_ID",CSBizBean.getVisit().getStaffId()));
       inparams.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
       inparams.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
       inparams.put("TRADE_DEPART_ID", inparams.getString("TRADE_DEPART_ID",CSBizBean.getVisit().getDepartId()));
       inparams.put(Route.ROUTE_EPARCHY_CODE, inparams.getString(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId()));

       String inparams2String = Wade3DataTran.toWadeString(inparams);
       String terminalResult = Wade3ClientRequest.request(url, svcName, inparams2String, "GBK"); 
       result = Wade3DataTran.wade3To4Dataset(Wade3DataTran.strToList(terminalResult));
       
       return result;
   }

  /**
   * @Function: querySetTopBox()
   * @Description: 机顶盒查询
   * @param:
   * @return：
   * @throws：异常描述
   * @version: v1.0.0
   * @author: yxd
   * @date: 2014-7-30 下午2:41:42 Modification History: Date Author Version Description
   *        ---------------------------------------------------------* 2014-7-30 yxd v1.0.0 修改原因
   */
  public static IDataset expandSetTopBoxOccupyTime(IData data) throws Exception
  {
      IData param = new DataMap();
      param.putAll(data);
      param.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo"); // 业务类型
      param.put("RES_TYPE_CODE", "4"); // 资源类型 4
      param.put("RES_ID", ""); // 资源实物编码 空
      param.put("RES_NO", data.getString("RES_NO")); // 校验的终端串号
      param.put("STAFF_ID", CSBizBean.getVisit().getStaffId()); // 终端的归属工号
      param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER")); // 办理业务手机号码
      param.put("INFO_TAG", "1"); // 分支标示 1
      param.put("OCCUPY_FLAG", "");
      param.put("SALE_TAG", ""); // 本次业务销售终端的类型 1: 买断过一次,再优惠购机. 2: 虚拟供货的再优惠购机. 不传:普通终端优惠购机
      param.put("MONTH_PICK", data.getString("MONTH_PICK",""));	//MONTH_PICK为1是表示机顶盒预占延长到1个月的时间
      
      IData resultData=new DataMap();
      resultData.put("X_RESULTCODE", "0");
      IDataset dataSet = new DatasetList();
      dataSet.add(resultData);
      return dataSet;
//      return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.preSelTerm", param);//TerminalCall.callHwTerminal("ITF_MONNI", param);
  }
  
 /**
  * 释放对机顶盒的预占
  * @param param
  * @return
  * @throws Exception
  */
  public static IDataset releaseResTempOccupy(IData data)throws Exception{
	   IData param = new DataMap();
	   
	   param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
	   param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
	   param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	   param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	   param.put("ROUTE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
	   param.put("RES_TRADE_CODE", "ReleaseResTempoccupySingle"); // 业务类型
	   param.put("RES_TYPE_CODE", "4"); // 资源类型 4
	   param.put("X_GETMODE", "0"); //业务分支
	   param.put("RES_NO", data.getString("RES_NO")); // 校验的终端串号
	   param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER")); // 办理业务手机号码
      param.put("RES_CODE", "");
      param.put("RESERVE_ID", ""); //网厅预约流水
      
      return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.delPreSelTerm", param);//TerminalCall.callHwTerminal("ITF_MONNI", param);
  }
  
  
  /**
   * 查询终端状态信息
   * @param serialNumber
   * @param resNo
   * @return
   * @throws Exception
   */
  public static IDataset queryTerminalStateInfo(String serialNumber, String resNo) throws Exception
  {
      IData param = new DataMap();

      param.put("RES_TRADE_CODE", "ICheckMobileTerminalInfo"); // 业务类型
      param.put("RES_TYPE_CODE", "4"); // 资源类型 4
      param.put("RES_ID", ""); // 资源实物编码 空
      param.put("RES_NO", resNo); // 校验的终端串号
      param.put("STAFF_ID", CSBizBean.getVisit().getStaffId()); // 终端的归属工号
      param.put("SERIAL_NUMBER", serialNumber); // 办理业务手机号码
      param.put("INFO_TAG", "1"); // 分支标示 1
      param.put("OCCUPY_FLAG", "");
      param.put("SALE_TAG", ""); // 本次业务销售终端的类型 1: 买断过一次,再优惠购机. 2: 虚拟供货的再优惠购机. 不传:普通终端优惠购机

      return HwTerminalCall.callRes("RCF.resource.ITermIntfQuerySV.checkTermImei", param);
  }
  
  /**
   * old  ITF_MONNI
   * @param resNo
   * @param oldImei
   * @param resTypeCode
   * @param resTradeCode
   * @param checkFlag
   * @param serialNumber
   * @return
   * @throws Exception
   */
  public static IDataset changeTermValid(String resNo, String oldImei,String resTypeCode,String resTradeCode,String checkFlag,String serialNumber) throws Exception
  {
	  IData data = new DataMap();
 	  data.put("NEW_IMEI", resNo);
      data.put("OLD_IMEI", oldImei);
      data.put("RES_TYPE_CODE", resTypeCode);
      data.put("RES_TRADE_CODE", resTradeCode);
      data.put("CHECK_FLAG", checkFlag);
      data.put("SERIAL_NUMBER", serialNumber);
      return HwTerminalCall.callRes("RCF.resource.ITermIntfOperateSV.changeTermValid", data);
  }
  
  
  
  
  public static IDataset callRes(String svcCode,IData param)throws Exception{    	
  	ServiceRequest request = new ServiceRequest();
  	BizVisit visit = CSBizBean.getVisit();
  	String inCodeCode = visit.getInModeCode();
  	if(StringUtils.isBlank(inCodeCode)){
  		inCodeCode = "0";
  	}
  	param.put("IN_MODE_CODE", inCodeCode);
      request.setData(param);
      if(log.isDebugEnabled()){
      	log.debug(svcCode + " send res params: " + param);
      }
      ServiceResponse response = (ServiceResponse) BizServiceFactory.call(svcCode, request, null);
      IData head = response.getHead();
      String xResultcode = head.getString("X_RESULTCODE");
      if(!"0".equals(xResultcode)){
      	String xResultinfo = head.getString("X_RESULTINFO");
//      	CSAppException.apperr(BizException.CRM_BIZ_171, svcCode, xResultinfo);
      }
      IData resultInfo = new DataMap();
      resultInfo.put("X_RESULTCODE", head.getString("X_RESULTCODE"));
      resultInfo.put("X_RESULTINFO", head.getString("X_RESULTINFO"));
      IDataset dataset = response.getData();
      if(IDataUtil.isEmpty(dataset))
      {
    	  dataset.add(resultInfo);
      }
      else
      {
    	  dataset.getData(0).putAll(resultInfo); 
      }
      if(log.isDebugEnabled()){
      	log.debug(svcCode + " receive res result: " + dataset);
      }
      return dataset;
  }

/**
 * @Description：TODO
 * @param:@param resNo
 * @param:@param resType
 * @param:@return
 * @return IDataset
 * @throws Exception 
 * @throws
 * @Author :tanzheng
 * @date :2018-5-31上午09:40:31
 */
public static IDataset checkIsResRightType(String resNo, String resType) throws Exception {
	IData param = new DataMap();

    param.put("RES_NO", resNo); // 校验的终端串号
    param.put("RES_TYPE_ID", resType); // 校验的终端类型

    return HwTerminalCall.callRes("RCF.resource.ITermIntfQuerySV.queryTermType", param);//TerminalCall.callHwTerminalForApp("ITF_MONNI", param);

}
  


/**
 * @Description：根据终端串号获取终端销售时间和销售工号
 * @param:@param string
 * @param:@return
 * @return IData
 * @throws Exception 
 * @throws
 * @Author :tanzheng
 * @date :2018-9-4上午09:10:35
 */
public static IData getTerminalInfoByIMEI(String resCode) throws Exception {
	 String cacheKey = CacheKey.getUsedTmByIMEI(resCode);
	 Object cacheObj = null;
     boolean cacheOK = true;

     try
     {
         cacheObj = SharedCache.get(cacheKey);
     }
     catch (Exception e)
     {
         cacheOK = false;
         cacheObj = null;

         StringBuilder sb = new StringBuilder("从SharedCache获取IMEI缓存数据失败[").append(cacheKey).append("]");

         log.error(sb);
     }

     // 缓存无，从数据库中查
     if (cacheObj == null)
     {
    	 IData map = new DataMap();
         IDataset retDataset = getTerminalByImei(resCode,"2");
         System.out.println("HwTerminalCall--retDataset"+retDataset.toString());
         IData retData2 = retDataset.first();
         IDataset retDataset2 =  retData2.getDataset("OUTDATA");
	     if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
	     {
	         IData resData = retDataset2.first();
	         map.put("OP_ID", resData.getString("OP_ID"));
	         map.put("DONE_TIME", resData.getString("DONE_TIME"));
	     }
         
         // 将数据放入缓存
         if (cacheOK == true)
         {
             SharedCache.set(cacheKey, map, 600);
         }

         return map;
     }
     else
     {
         // 往后顺延
         SharedCache.touch(cacheKey, 600);
     }

     // 缓存有，直接返回缓存对象
     if (log.isDebugEnabled())
     {
         StringBuilder sb = new StringBuilder("从SharedCache获取IMEI缓存数据成功[").append(cacheKey).append("]");
         log.debug(sb);
     }

     IData map = (IData) cacheObj;

     return map;
}
}
