
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;

public class BuildChangeProduct extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        BaseChangeProductReqData request = (BaseChangeProductReqData) brd;

        if (StringUtils.isNotBlank(param.getString("NEW_PRODUCT_ID")))
        {
            request.setNewMainProduct(param.getString("NEW_PRODUCT_ID"));
        }
        // 预约时间处理
        if (StringUtils.isNotBlank(param.getString("BOOKING_DATE")))
        {
            if (ProductUtils.isBookingChange(param.getString("BOOKING_DATE")))
            {
                request.setBookingDate(param.getString("BOOKING_DATE"));
                request.setBookingTag(true);
            }
        }

        IDataset selectedElements = new DatasetList(param.getString("SELECTED_ELEMENTS"));
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

                    // 海南取消彩铃连带取消铃音盒
                    this.cancelBellRingByDelColorRingSvc(element, elements, brd);
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
            request.setProductElements(elements);
        }
        if (param.getString("EFFECT_NOW", "").equals("1"))
        {
            request.setEffectNow(true);
        }
        
        if(StringUtils.isNotBlank(param.getString("SHAREMEALONE")))
        {
        	request.setRsrvStr7(param.getString("SHAREMEALONE"));
        }
        
        if(StringUtils.isNotBlank(param.getString("SHAREMEALTWO")))
        {
        	request.setRsrvStr8(param.getString("SHAREMEALTWO"));
        }
    }

    /**
     * @Description: 取消彩铃连带取消铃音盒
     * @param element
     * @param elements
     * @param brd
     * @throws Exception
     * @author: maoke
     * @date: Sep 2, 2014 11:31:18 AM
     */
    public void cancelBellRingByDelColorRingSvc(IData element, List<ProductModuleData> elements, BaseReqData brd) throws Exception
    {
        String modifyTag = element.getString("MODIFY_TAG");
        String elementId = element.getString("ELEMENT_ID");

        if (BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "20".equals(elementId))
        {
            UcaData uca = brd.getUca();
            List<PlatSvcTradeData> plats = uca.getUserPlatSvcs();

            if (plats != null && plats.size() > 0)
            {
                for (PlatSvcTradeData plat : plats)
                {
                    String serviceId = plat.getElementId();
                    String platModifyTag = plat.getModifyTag();
                    
                    PlatOfficeData platOfficeData = null;
                    
                    try{
                        platOfficeData = PlatOfficeData.getInstance(serviceId);
                    }
                    catch(Exception e)
                    {
                        
                    }
                    
                    if (platOfficeData != null)
                    {
                        String bizTypeCode = platOfficeData.getBizTypeCode();

                        if (StringUtils.isNotBlank(bizTypeCode) && "LY".equals(bizTypeCode))
                        {
                            if (!BofConst.MODIFY_TAG_DEL.equals(platModifyTag))
                            {
                                IData platData = new DataMap();
                                platData.put("ELEMENT_ID", serviceId);
                                platData.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);

                                PlatSvcData platSvcData = new PlatSvcData(platData);

                                elements.add(platSvcData);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new BaseChangeProductReqData();
    }
}
