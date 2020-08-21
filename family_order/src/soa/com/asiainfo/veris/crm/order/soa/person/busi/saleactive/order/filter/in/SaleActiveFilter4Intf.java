
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.filter.in;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TerminalOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;

public class SaleActiveFilter4Intf implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        if (StringUtils.isBlank(input.getString("SERIAL_NUMBER")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_3);
        }

        if (StringUtils.isBlank(input.getString("PRODUCT_ID")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_1);
        }

        IData prodcutData = UProductInfoQry.qrySaleActiveProductByPK(input.getString("PRODUCT_ID"));

        if (IDataUtil.isEmpty(prodcutData))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_22, input.getString("PRODUCT_ID"));
        }

        SaleActiveBean saleActiveBean = new SaleActiveBean();
        String campnType = saleActiveBean.getCampnType(input.getString("PRODUCT_ID"));

        if (StringUtils.isBlank(campnType))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_7, input.getString("PRODUCT_ID"));
        }

        input.put("CAMPN_TYPE", campnType);

        if (StringUtils.isBlank(input.getString("PACKAGE_ID")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_2);
        }

        IData pkgData = UPackageInfoQry.getPackageByPK(input.getString("PACKAGE_ID"));
        if (IDataUtil.isEmpty(pkgData))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_23, input.getString("PACKAGE_ID"));
        }

        IDataset pkgExtDataSet = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, input.getString("PACKAGE_ID"), "TD_B_PACKAGE_EXT");//PkgExtInfoQry.queryPackageExtInfo(input.getString("PACKAGE_ID"), "0898");
        if (IDataUtil.isEmpty(pkgExtDataSet))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_6, input.getString("PACKAGE_ID"));
        }

//        ProductInfoQry productInfoQry = new ProductInfoQry();
//        IData prodcutInfos = productInfoQry.getProductPackageRelNoPriv(input.getString("PACKAGE_ID"), input.getString("PRODUCT_ID"), "0898");
        IDataset offers = UpcCall.qryOffersByCatalogId(input.getString("PRODUCT_ID"));
        boolean isContains = IDataUtil.dataSetContainsAllKeysAndValues(offers, new String[]{"OFFER_CODE", "OFFER_TYPE"}, new String[]{input.getString("PACKAGE_ID"), BofConst.ELEMENT_TYPE_CODE_PACKAGE});
        if (!isContains)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您传入的包" + input.getString("PACKAGE_ID") + "不是产品" + input.getString("PRODUCT_ID") + "下的包！");
        }

        if (StringUtils.isNotBlank(input.getString("TERMINAL_ID")))
        {
            input.put("SALEGOODS_IMEI", input.getString("TERMINAL_ID"));
        }

        if ("1".equals(input.getString("ACTIVATE_ACTIVE_4_GROUP")))
        {
            input.put("CALL_TYPE", SaleActiveConst.CALL_TYPE_ACTIVATE_ACTIVE);
        }
        
        if (!"1".equals(input.getString("AUTO_FLAG",""))){
        	if ("1".equals(input.getString("ACTION_TYPE")))
            {
                SaleActiveUtil.checkIntfInparam(input, "DEVICE_MODEL_CODE");
                SaleActiveUtil.checkIntfInparam(input, "DEVICE_MODEL");
                SaleActiveUtil.checkIntfInparam(input, "PROD_ORDER_ID");

                input.put("CALL_TYPE", SaleActiveConst.CALL_TYPE_NET_STORE);
            }
        }
        
        //网厅同步给boss的购机订单表:TF_F_TERMINALORDER 中的活动 如果是，自动化办理购机活动：  add 20141119 caolin
        if ("1".equals(input.getString("AUTO_FLAG",""))){
        	//必须参数：付费类型（货到付款、网上商城网银支付）
        	SaleActiveUtil.checkIntfInparam(input,"PAY_MONEY_CODE");
        	if(!(StringUtils.equals(input.getString("PAY_MONEY_CODE"), "L")||StringUtils.equals(input.getString("PAY_MONEY_CODE"), "8"))){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "您传入的付款方式" + input.getString("PAY_MONEY_CODE") + "不符合业务受理要求！");
        	}
        	//查找同步过来的订单
        	SaleActiveUtil.checkIntfInparam(input, "NET_ORDER_ID");
        	
        	String orderId=input.getString("NET_ORDER_ID","");		//预约订单ID，对应表TF_F_TERMINALORDER的order_id
        	/*
        	 * ORDER_STATE: 0：未处理、1：已受理、2：取消'
        	 * RSRV_STR2: 记录CRM侧该订单状态，0：已录入 1：资源状态已修改（资源已交付）
        	 */
        	IDataset terminalOrderInfos = TerminalOrderInfoQry.qryTerminalOrderInfoForDeal(input.getString("SERIAL_NUMBER"),orderId, "0","0");
            if (IDataUtil.isEmpty(terminalOrderInfos)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您好，不存在可以自动办理的订单！");
            }
            //非必须参数：用户下单价格（不传、则取TF_F_TERMINALORDER.ORDER_PRICE） 
            if(StringUtils.isEmpty(input.getString("ORDER_PRICE"))){
            	input.put("ORDER_PRICE", terminalOrderInfos.getData(0).getString("ORDER_PRICE"));
            }
            input.put("DEVICE_MODEL_CODE", terminalOrderInfos.getData(0).getString("DEVICE_MODEL_CODE",""));
		}
    }

}
