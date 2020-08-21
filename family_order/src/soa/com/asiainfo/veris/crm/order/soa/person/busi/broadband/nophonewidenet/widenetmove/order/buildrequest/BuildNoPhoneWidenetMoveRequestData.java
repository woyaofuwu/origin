package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove.order.requestdata.NoPhoneWidenetMoveRequestData;

public class BuildNoPhoneWidenetMoveRequestData extends BaseBuilder implements IBuilder
{
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
    	NoPhoneWidenetMoveRequestData reqData = (NoPhoneWidenetMoveRequestData) brd;
        reqData.setNewStandAddress(data.getString("NEW_STAND_ADDRESS"));//标准地址
        reqData.setNewStandAddressCode(data.getString("NEW_STAND_ADDRESS_CODE"));//标准地址CODE
        reqData.setNewDetailAddress(data.getString("NEW_DETAIL_ADDRESS"));//详细地址
        reqData.setNewAreaCode(data.getString("NEW_AREA_CODE"));//地区，AREA_CODE
        reqData.setNewContact(data.getString("NEW_CONTACT"));//联系人
        reqData.setNewPhone(data.getString("NEW_PHONE"));//联系电话
        reqData.setNewContactPhone(data.getString("NEW_CONTACT_PHONE"));//联系人电话
        reqData.setRsrvStr2(data.getString("RSRV_STR2",""));//作用未知，貌似是老宽带的productMode
        reqData.setWideType(data.getString("WIDE_TYPE",""));//作用未知，貌似是老的宽带类型
        reqData.setNewWideType(data.getString("NEW_WIDE_TYPE",""));//移机后的新宽带类型   1-移动FTTB 2-铁通ADSL 3-移动FTTH 5-铁通FTTH 6-铁通FTTB
        reqData.setDeviceId(data.getString("DEVICE_ID"));//设备号
        
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
	        
	        if(!"".equals(data.getString("SELECTED_ELEMENTS",""))){
	        	selectedElements = new DatasetList(data.getString("SELECTED_ELEMENTS",""));
	        }
	        
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
        	reqData.setModelMode(data.getString("MODEL_MODE",""));//测试环境只有3-租赁
        	reqData.setModelPurchase(data.getString("PURCHASE_MONEY","0"));
        }
    	
        reqData.setWidenetMoveNew(data.toString());
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new NoPhoneWidenetMoveRequestData();
    }
}
