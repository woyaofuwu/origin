
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;

public class MergeWideUserCreateRegSVC extends OrderService
{
	
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
    	if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
        	input.put("TRADE_TYPE_CODE", "600");
        }
    	
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public String getTradeTypeCode() throws Exception
    {
    	if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
	    	if(input.getString("WIDE_TYPE").equals("1"))//GPON
	    	{
	    		input.put("TRADE_TYPE_CODE", "600");
	    	}
        }
    	
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        orderData.setSubscribeType("300");
    }
    
    
    @Override
    public void otherTradeDeal(IData idata, BusiTradeData btd) throws Exception
    {
        MergeWideUserCreateRequestData mergeWideUserCreateRD = (MergeWideUserCreateRequestData) btd.getRD();
        
        String  wideUserSelectedServiceIds = "";
        
        List<ProductModuleData> productElements = mergeWideUserCreateRD.getProductElements();
        
        for (int i = 0; i < productElements.size(); i++)
        {
            ProductModuleData productModuleData = productElements.get(i);
            
            if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(productModuleData.getElementType()))
            {
                if (StringUtils.isNotBlank(wideUserSelectedServiceIds)) 
                {
                    wideUserSelectedServiceIds = wideUserSelectedServiceIds + "|" + productModuleData.getElementId();
                }
                else
                {
                    wideUserSelectedServiceIds = productModuleData.getElementId();
                }
            }
        }
        
        //如果用户办理了IMS固话营销活动
        if (StringUtils.isNotBlank(mergeWideUserCreateRD.getImsSaleActiveId()))
        {
            IData imsSaleActiveInParam = new DataMap();
            imsSaleActiveInParam.put("SERIAL_NUMBER",mergeWideUserCreateRD.getNormalSerialNumber());
            imsSaleActiveInParam.put("PRODUCT_ID",mergeWideUserCreateRD.getImsSaleActiveProductId());
            imsSaleActiveInParam.put("PACKAGE_ID", mergeWideUserCreateRD.getImsSaleActiveId());
            imsSaleActiveInParam.put("ACCEPT_TRADE_ID", mergeWideUserCreateRD.getTradeId());
            imsSaleActiveInParam.put("ORDER_TYPE_CODE", "600");
            imsSaleActiveInParam.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);
            
            //标记是宽带开户营销活动
            imsSaleActiveInParam.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
            
            //宽带开户支付模式：P：预先支付  A：先装后付
            imsSaleActiveInParam.put("WIDE_PAY_MODE", mergeWideUserCreateRD.getWidenetPayMode());
            
            //认证方式
            String checkMode = btd.getRD().getCheckMode();
            imsSaleActiveInParam.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
            
            IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", imsSaleActiveInParam);
            
            //将IMS固话营销活动tradeID存入  临时表
         	List<OtherTradeData> otherTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
    			
         	if (null != otherTradeDatas && otherTradeDatas.size() > 0)
            {
         		for (int i = 0; i < otherTradeDatas.size(); i++)
         		{
         			OtherTradeData tempOtherTradeData = otherTradeDatas.get(i);
         			
         			
         			if ("IMSTRADE".equals(tempOtherTradeData.getRsrvValueCode()))
         			{
         				tempOtherTradeData.setRsrvStr30(result.getData(0).getString("TRADE_ID"));
         			}
         		}
            }
        }
        
        
        //是否有和目营销活动
        if (StringUtils.isNotBlank(mergeWideUserCreateRD.getHeMuSaleActiveId()))
        {
              IData heMuSaleActiveInParam = new DataMap();
              heMuSaleActiveInParam.put("SERIAL_NUMBER",mergeWideUserCreateRD.getNormalSerialNumber());
              heMuSaleActiveInParam.put("PRODUCT_ID",mergeWideUserCreateRD.getHeMuSaleActiveProductId());
              heMuSaleActiveInParam.put("PACKAGE_ID", mergeWideUserCreateRD.getHeMuSaleActiveId());
              heMuSaleActiveInParam.put("SALEGOODS_IMEI", mergeWideUserCreateRD.getHeMuResId());
              heMuSaleActiveInParam.put("ACCEPT_TRADE_ID", mergeWideUserCreateRD.getTradeId());
              heMuSaleActiveInParam.put("ORDER_TYPE_CODE", "600");
              heMuSaleActiveInParam.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);
              
              //标记是宽带开户营销活动
              heMuSaleActiveInParam.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
              
              //宽带开户支付模式：P：预先支付  A：先装后付
              heMuSaleActiveInParam.put("WIDE_PAY_MODE", mergeWideUserCreateRD.getWidenetPayMode());
              
              //认证方式
              String checkMode = btd.getRD().getCheckMode();
              heMuSaleActiveInParam.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
              
              IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", heMuSaleActiveInParam);
              
              String tradeId = result.getData(0).getString("TRADE_ID");
              btd.getMainTradeData().setRsrvStr10(tradeId);
        }
        

        //生成宽带营销活动预受理
        if (StringUtils.isNotBlank(mergeWideUserCreateRD.getSaleActiveId()))
        {
            IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "600", mergeWideUserCreateRD.getMainProduct().getProductId(), mergeWideUserCreateRD.getSaleActiveId(), getTradeEparchyCode());
            
            if (IDataUtil.isNotEmpty(saleActiveIDataset))
            { 
                  IData saleActiveData = saleActiveIDataset.first();
                  
                  IData saleactiveData = new DataMap();
                  saleactiveData.put("SERIAL_NUMBER",mergeWideUserCreateRD.getNormalSerialNumber());
                  saleactiveData.put("PRODUCT_ID",saleActiveData.getString("PARA_CODE4"));
                  saleactiveData.put("PACKAGE_ID", saleActiveData.getString("PARA_CODE5"));
                  saleactiveData.put("ACCEPT_TRADE_ID", mergeWideUserCreateRD.getTradeId());
                  saleactiveData.put("ORDER_TYPE_CODE", "600");
                  saleactiveData.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);
                  
                  //20170511宽带开户办理了特定的活动必须同时关联魔百和开户及营销活动受理。（NGBOSS和APP，即接口也要限制） 
                  String Flag = saleActiveData.getString("PARA_CODE8");
                  String packageName = saleActiveData.getString("PARA_CODE3");
                  if("1".equals(Flag))
                  {
                	  if(StringUtils.isBlank(mergeWideUserCreateRD.getTopSetBoxBasePkgs()) || 
                			  StringUtils.isBlank(mergeWideUserCreateRD.getTopSetBoxProductId()))
                	  {
                		  CSAppException.appError("2017051101", "办理了" + packageName + "活动，必须关联办理魔百和开户！");
                	  }
                	  if(StringUtils.isBlank(mergeWideUserCreateRD.getTopSetBoxSaleActiveId()) || 
                			  !"200099".equals(mergeWideUserCreateRD.getTopSetBoxSaleActiveId()))
                	  {
                		  CSAppException.appError("2017051102", "办理了" + packageName + "活动，必须关联办理移动电视尝鲜活动(预受理) ！");
                	  }
                  }
                  
                  //标记是宽带开户营销活动
                  saleactiveData.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
                  
                  //宽带开户支付模式：P：立即支付  A：先装后付
                  saleactiveData.put("WIDE_PAY_MODE", mergeWideUserCreateRD.getWidenetPayMode());
                  
                  //认证方式
                  String checkMode = btd.getRD().getCheckMode();
                  saleactiveData.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
                  
                  IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
                  String tradeId = result.getData(0).getString("TRADE_ID");
                  btd.getMainTradeData().setRsrvStr5(tradeId);
            
            
                  if(!"".equals(mergeWideUserCreateRD.getSaleActiveIdAttr()))
                  {
                	  String para_code11 = mergeWideUserCreateRD.getSaleActiveIdAttr();
                      IDataset saleActiveList = CommparaInfoQry.getCommparaInfoByCode5("CSM", "178", "600",null, null,para_code11, "0898");
                      if(IDataUtil.isNotEmpty(saleActiveList)){
		                  IData saleactiveData2 = new DataMap();
		            	  saleactiveData2.put("SERIAL_NUMBER",mergeWideUserCreateRD.getNormalSerialNumber());
		                  saleactiveData2.put("PRODUCT_ID",saleActiveList.getData(0).getString("PARA_CODE14"));
		                  saleactiveData2.put("PACKAGE_ID", saleActiveList.getData(0).getString("PARA_CODE15"));
		                  saleactiveData2.put("ACCEPT_TRADE_ID", mergeWideUserCreateRD.getTradeId());
		                  saleactiveData2.put("ORDER_TYPE_CODE", "600");
		                  saleactiveData2.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);

		                  //标记是宽带开户营销活动
		                  saleactiveData2.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
		                  saleactiveData2.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
		                  
		                  //宽带开户支付模式：P：立即支付  A：先装后付
		                  saleactiveData.put("WIDE_PAY_MODE", mergeWideUserCreateRD.getWidenetPayMode());
		                  
		                  IDataset result2 = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData2);
		                  String tradeId2 = result2.getData(0).getString("TRADE_ID");
		                  btd.getMainTradeData().setRsrvStr9(tradeId2);
                      }
                  }
            }
            else
            {
                CSAppException.appError("-1", "宽带营销活动包不存在！");
            }
        }
        
        //是否有魔百和营销活动
        if (StringUtils.isNotBlank(mergeWideUserCreateRD.getTopSetBoxSaleActiveId()))
        {
           IDataset saleActiveIDataset = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", mergeWideUserCreateRD.getTopSetBoxSaleActiveId(), getTradeEparchyCode());
            
            if (IDataUtil.isNotEmpty(saleActiveIDataset))
            {
                  IData saleActiveData = saleActiveIDataset.first();
                  
                  IData saleactiveData = new DataMap();
                  saleactiveData.put("SERIAL_NUMBER",mergeWideUserCreateRD.getNormalSerialNumber());
                  saleactiveData.put("PRODUCT_ID",saleActiveData.getString("PARA_CODE4"));
                  saleactiveData.put("PACKAGE_ID", saleActiveData.getString("PARA_CODE5"));
                  saleactiveData.put("ACCEPT_TRADE_ID", mergeWideUserCreateRD.getTradeId());
                  saleactiveData.put("ORDER_TYPE_CODE", "600");
                  saleactiveData.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);
                  
                  //标记是宽带开户营销活动
                  saleactiveData.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
                  
                  //20170511开户办理特殊的活动，电视尝鲜不走手机开户6个月的规则。
                  saleactiveData.put("SALE_ACTIVE_ID", mergeWideUserCreateRD.getSaleActiveId());
                  
                  //宽带开户支付模式：P：预先支付  A：先装后付
                  saleactiveData.put("WIDE_PAY_MODE", mergeWideUserCreateRD.getWidenetPayMode());
                  
                  //认证方式
                  String checkMode = btd.getRD().getCheckMode();
                  saleactiveData.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
                  
                  IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
                  
                  String tradeId = result.getData(0).getString("TRADE_ID");
                  btd.getMainTradeData().setRsrvStr6(tradeId);
            }
            else
            {
                CSAppException.appError("-1", "魔百和营销活动包不存在！");
            }

        }
        
        //生成宽带调测费活动预受理
        if (StringUtils.isNotBlank(mergeWideUserCreateRD.getSaleActiveId2()))
        {
            IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "600", "FTTH", mergeWideUserCreateRD.getSaleActiveId2(), getTradeEparchyCode());
            
            if (IDataUtil.isNotEmpty(saleActiveIDataset))
            { 
                  IData saleActiveData = saleActiveIDataset.first();
                  
                  IData saleactiveData = new DataMap();
                  saleactiveData.put("SERIAL_NUMBER",mergeWideUserCreateRD.getNormalSerialNumber());
                  saleactiveData.put("PRODUCT_ID",saleActiveData.getString("PARA_CODE4"));
                  saleactiveData.put("PACKAGE_ID", saleActiveData.getString("PARA_CODE5"));
                  saleactiveData.put("ACCEPT_TRADE_ID", mergeWideUserCreateRD.getTradeId());
                  saleactiveData.put("ORDER_TYPE_CODE", "600");
                  saleactiveData.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);        
                  //标记是宽带开户营销活动
                  saleactiveData.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");                  
                  //宽带开户支付模式：P：立即支付  A：先装后付
                  saleactiveData.put("WIDE_PAY_MODE", mergeWideUserCreateRD.getWidenetPayMode());                
                  //认证方式
                  String checkMode = btd.getRD().getCheckMode();
                  saleactiveData.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
                  
                  IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
                  String tradeId = result.getData(0).getString("TRADE_ID");
                  if(null == btd.getMainTradeData().getRsrvStr5() || "".equals(btd.getMainTradeData().getRsrvStr5()))
                  {
                	  btd.getMainTradeData().setRsrvStr5(tradeId);
                  }
                  else
                  {
                	  btd.getMainTradeData().setRsrvStr5(btd.getMainTradeData().getRsrvStr5()+"|"+tradeId);
                  }
            
            }
            else
            {
                CSAppException.appError("-1", "宽带调测费营销活动包不存在！");
            }
        }
        
        //是否有魔百和调测费营销活动
        if (StringUtils.isNotBlank(mergeWideUserCreateRD.getTopSetBoxSaleActiveId2()))
        {
           IDataset saleActiveIDataset = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", mergeWideUserCreateRD.getTopSetBoxSaleActiveId2(), getTradeEparchyCode());
            
            if (IDataUtil.isNotEmpty(saleActiveIDataset))
            {
                  IData saleActiveData = saleActiveIDataset.first();
                  
                  IData saleactiveData = new DataMap();
                  saleactiveData.put("SERIAL_NUMBER",mergeWideUserCreateRD.getNormalSerialNumber());
                  saleactiveData.put("PRODUCT_ID",saleActiveData.getString("PARA_CODE4"));
                  saleactiveData.put("PACKAGE_ID", saleActiveData.getString("PARA_CODE5"));
                  saleactiveData.put("ACCEPT_TRADE_ID", mergeWideUserCreateRD.getTradeId());
                  saleactiveData.put("ORDER_TYPE_CODE", "600");
                  saleactiveData.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);
                  
                  //标记是宽带开户营销活动
                  saleactiveData.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
                  
                  //20170511开户办理特殊的活动，电视尝鲜不走手机开户6个月的规则。
                  //saleactiveData.put("SALE_ACTIVE_ID", mergeWideUserCreateRD.getSaleActiveId());
                  
                  //宽带开户支付模式：P：预先支付  A：先装后付
                  saleactiveData.put("WIDE_PAY_MODE", mergeWideUserCreateRD.getWidenetPayMode());
                  
                  //认证方式
                  String checkMode = btd.getRD().getCheckMode();
                  saleactiveData.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
                  
                  IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
                  
                  String tradeId = result.getData(0).getString("TRADE_ID");
                  if( null == btd.getMainTradeData().getRsrvStr6() || "".equals(btd.getMainTradeData().getRsrvStr6()))
                  {
                	  btd.getMainTradeData().setRsrvStr6(tradeId);
                  }
                  else
                  {
                	  btd.getMainTradeData().setRsrvStr6(btd.getMainTradeData().getRsrvStr6()+"|"+tradeId);
                  }
            }
            else
            {
                CSAppException.appError("-1", "魔百和调测费营销活动包不存在！");
            }

        }
 		//REQ202003180001 “共同战疫宽带助力”活动开发需求 add by liangdg3 start
        String timeLimitSaleActive = btd.getRD().getPageRequestData().getString("TIME_LIMIT_SALE_ACTIVE", "");
        if(StringUtils.isNotBlank(timeLimitSaleActive)){
            if(StringUtils.equals("1",btd.getRD().getPageRequestData().getString("IS_"+timeLimitSaleActive+"_ACTIVE","0"))
                ||StringUtils.equals("on",btd.getRD().getPageRequestData().getString("IS_"+timeLimitSaleActive+"_ACTIVE","0"))){//老界面传进来的是on
                //直接从页面参数中透传获取,临时的活动就不封装到请求参数对象了
                //查战役活动配置
                IDataset saleActiveIDataset = CommparaInfoQry.getCommparaAllColByParser("CSM", "178", timeLimitSaleActive, getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(saleActiveIDataset)){
                    IData saleActiveData = saleActiveIDataset.first();
                    IData saleactiveData = new DataMap();
                    saleactiveData.put("SERIAL_NUMBER",mergeWideUserCreateRD.getNormalSerialNumber());
                    saleactiveData.put("PRODUCT_ID",saleActiveData.getString("PARA_CODE1"));
                    saleactiveData.put("PACKAGE_ID", saleActiveData.getString("PARA_CODE2"));
                    saleactiveData.put("ACCEPT_TRADE_ID", mergeWideUserCreateRD.getTradeId());
                    saleactiveData.put("ORDER_TYPE_CODE", "600");
                    saleactiveData.put("WIDE_USER_SELECTED_SERVICEIDS", wideUserSelectedServiceIds);
                    //标记是宽带开户营销活动
                    saleactiveData.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
                    //认证方式
                    String checkMode = btd.getRD().getCheckMode();
                    saleactiveData.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
                    IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
                    //String tradeId = result.getData(0).getString("TRADE_ID");
                }
                else{
                    CSAppException.appError("-1", timeLimitSaleActive+"营销活动包配置不存在！");
                }
            }
        }
        //REQ202003180001 “共同战疫宽带助力”活动开发需求 add by liangdg3 end
        
    }
}
