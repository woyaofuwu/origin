
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.WidenetMoveSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.requestdata.WidenetMoveRequestData;

public class BuildWidenetMoveRequestData extends BaseBuilder implements IBuilder
{
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        WidenetMoveRequestData reqData = (WidenetMoveRequestData) brd;
        reqData.setNewStandAddress(data.getString("NEW_STAND_ADDRESS"));
        reqData.setNewStandAddressCode(data.getString("NEW_STAND_ADDRESS_CODE"));
        reqData.setNewDetailAddress(data.getString("NEW_DETAIL_ADDRESS"));
        reqData.setNewAreaCode(data.getString("NEW_AREA_CODE"));
        reqData.setNewContact(data.getString("NEW_CONTACT"));
        reqData.setNewPhone(data.getString("NEW_PHONE"));
        reqData.setNewContactPhone(data.getString("NEW_CONTACT_PHONE"));
        reqData.setRsrvStr2(data.getString("RSRV_STR2",""));
        reqData.setWideType(data.getString("WIDE_TYPE",""));
        reqData.setNewWideType(data.getString("NEW_WIDE_TYPE",""));
        reqData.setModelOldNew(data.getString("IS_EXCHANGE_MODEL",""));
        reqData.setSuggestDate(data.getString("SUGGEST_DATE","")); //预约装机时间
        String trade_type_code = brd.getTradeType().getTradeTypeCode();
        if (("606".equals(trade_type_code) || "622".equals(trade_type_code) || "623".equals(trade_type_code) )&&(!"".equals(data.getString("NEW_DETAIL_ROOM_NUM",""))))
        {
        	reqData.setNewDetailAddress(data.getString("NEW_DETAIL_ADDRESS") + "(" + data.getString("NEW_DETAIL_ROOM_NUM","") + ")");
        }
        if("1".equals(data.getString("IS_BUSINESS_WIDE"))) reqData.setIsBusiness(true);
        else reqData.setIsBusiness(false);
        
        reqData.setDeviceId(data.getString("DEVICE_ID"));
        reqData.setUserIdMobuleA(data.getString("MOBILE_USER_ID"));
        if ("636".equals(brd.getTradeType().getTradeTypeCode()))
        {
            reqData.setUserIdA(data.getString("USER_ID_A"));
            reqData.setGponUserId(data.getString("GPON_USER_ID"));
            reqData.setGponSerialNumber(data.getString("GPON_SERIAL_NUMBER"));
            reqData.setPreWideType(data.getString("PREWIDE_TYPE"));
        }
        
        //海南移动宽带业务综合优化项目新增，主要针对移机是附加的产品变更
        if ((StringUtils.isNotBlank(data.getString("IS_CHG_PROD")))&&(data.getString("IS_CHG_PROD").equals("TRUE")))
        {
        	reqData.setChgProd(true);
	        if (StringUtils.isNotBlank(data.getString("NEW_PRODUCT_ID")))
	        {
	        	reqData.setNewMainProduct(data.getString("NEW_PRODUCT_ID"));
	        }
	        if (StringUtils.isNotBlank(data.getString("BOOKING_DATE")))// 预约时间处理
	        {
	            if (ProductUtils.isBookingChange(data.getString("BOOKING_DATE")))
	            {
	            	reqData.setBookingDate(data.getString("BOOKING_DATE"));
	            	reqData.setBookingTag(true);
	            }
	        }
	        
	        IDataset selectedElements = new DatasetList();
	        if(!"".equals(data.getString("SELECTED_ELEMENTS","")))
	        	selectedElements = new DatasetList(data.getString("SELECTED_ELEMENTS",""));
	        if (selectedElements != null && selectedElements.size() > 0)
	        {
	            List<ProductModuleData> elements = new ArrayList<ProductModuleData>();
	            int size = selectedElements.size();
	            for (int i = 0; i < size; i++)
	            {
	                IData element = selectedElements.getData(i);
	                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
	                {
	                    DiscntData discntData = new DiscntData(element);
	                    elements.add(discntData);
	                }
	                else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
	                {
	                    SvcData svcData = new SvcData(element);
	                    elements.add(svcData);
	                }
	                else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(element.getString("ELEMENT_TYPE_CODE")))
	                {
	                    if (BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG")))
	                    {
	                        element.put("OPER_CODE", PlatConstants.OPER_ORDER);
	                    }
	                    else if (BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG")))
	                    {
	                        element.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
	                    }
	                    PlatSvcData platSvcData = new PlatSvcData(element);
	                    elements.add(platSvcData);
	                }
	            }
	            reqData.setProductElements(elements);
	        }
	        if (data.getString("EFFECT_NOW", "").equals("1"))
	        {
	        	reqData.setEffectNow(true);
	        }
        }
        
        if (((StringUtils.isNotBlank(data.getString("MODEL_MODE")))&&(!"".equals(data.getString("MODEL_MODE",""))))||("2".equals(data.getString("IS_EXCHANGE_MODEL"))))
        {
        	reqData.setExchangeModel(data.getString("IS_EXCHANGE_MODEL",""));
        	reqData.setChgModel(true);
        	reqData.setModelDeposit(data.getString("DEPOSIT_MONEY","0"));
        	reqData.setModelMode(data.getString("MODEL_MODE",""));
        	reqData.setModelPurchase(data.getString("PURCHASE_MONEY","0"));
        }
        
        if ((StringUtils.isNotBlank(data.getString("IS_CHG_SALE")))&&(data.getString("IS_CHG_SALE").equals("TRUE"))&&
    			(StringUtils.isNotBlank(data.getString("SALEACTIVE_PACKAGE_ID")))&&(!"".equals(data.getString("SALEACTIVE_PACKAGE_ID"))))
        {
        	reqData.setSaleactive(true);
        }
        //BUS201907310012关于开发家庭终端调测费的需求
        if ( !"".equals(data.getString("SALEACTIVE_PRODUCT_ID2","")) && !"".equals(data.getString("SALEACTIVE_PACKAGE_ID2","")))
        {
        	reqData.setSaleActiveId2(data.getString("SALEACTIVE_PACKAGE_ID2",""));
        	reqData.setSaleActiveFee2(data.getString("SALE_ACTIVE_FEE2",""));
        	reqData.setSaleactive2(true);
        }
        //BUS201907310012关于开发家庭终端调测费的需求
    	if(data.getBoolean("HAS_SEL_YEAR"))	reqData.setYearDiscnt(true);
    	if(data.getBoolean("HAS_EFF_YEAR")){
    		reqData.setEffYearDnt(true);
    		reqData.setEffYearDiscnt(data.getData("EFF_YEAR_DISCNT").toString());
    	}
    	if(data.getBoolean("HAS_EFF_ACTIVE")){
    		reqData.setEffActive(true);
    	}
    	if(data.getData("EFF_ACTIVE")!=null&&!"".equals(data.getData("EFF_ACTIVE").toString())){
    		reqData.setEffActiveInfo(data.getData("EFF_ACTIVE").toString());
    	}
    	
        reqData.setWidenetMoveNew(data.toString());
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new WidenetMoveRequestData();
    }

}
