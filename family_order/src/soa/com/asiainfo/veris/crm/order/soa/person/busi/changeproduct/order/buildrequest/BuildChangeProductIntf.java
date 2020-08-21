
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.exception.UserDiscntException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserTdExpactQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductBean;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;
import com.asiainfo.veris.crm.order.soa.person.common.util.PersonUtil;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildChangeProductIntf.java
 * @Description: 产品变更接口核心数据构建类【适用单、多个元素】
 * @version: v1.0.0
 * @author: maoke
 * @date: Jul 9, 2014 11:48:43 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jul 9, 2014 maoke v1.0.0 修改原因
 */
public class BuildChangeProductIntf extends BaseBuilder implements IBuilder
{

	protected static final Logger log = Logger.getLogger(BuildChangeProductIntf.class);
	
	protected IData input;
	
    /**
     * @Description: 根据product_id获取该产品下的所有元素
     * @param request
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 9, 2014 2:38:21 PM
     */
    public static IDataset getProductElements(ChangeProductReqData request) throws Exception
    {
        String productId = "";

        IDataset productElements = null;

        UcaData uca = request.getUca();

        ProductTradeData nextMainProduct = uca.getUserNextMainProduct();

        ProductData newMainProduct = request.getNewMainProduct();

        if (newMainProduct != null && !uca.getProductId().equals(newMainProduct.getProductId()))// 当既有预约产品,又办理新产品时候报错
        {
            if (nextMainProduct != null)
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_195);
            }
        }

        if (newMainProduct != null)// 取新产品ID
        {
            productId = newMainProduct.getProductId();
        }
        else if (nextMainProduct != null)// 取预约产品ID
        {
            productId = nextMainProduct.getProductId();
        }
        else
        {
            productId = uca.getProductId();
        }

        productElements = ProductElementsCache.getProductElements(productId);//ProductInfoQry.getProductElements(productId, uca.getUserEparchyCode());

        return productElements;
    }

    /**
     * @Description: 添加标准GPRS优惠【902】
     * @param productElements
     * @param pmds
     * @throws Exception
     * @author: maoke
     * @date: Aug 18, 2014 8:57:28 PM
     */
    public void addDefaultGprsDiscnt(IDataset productElements, List<ProductModuleData> pmds,UcaData uca) throws Exception
    {
        if (!isExistProductModuleData(BofConst.ELEMENT_TYPE_CODE_DISCNT, PersonConst.GPRS_DEFAULT_DISCNT_CODE, BofConst.MODIFY_TAG_ADD, pmds))
        {

            List<DiscntTradeData> list = uca.getUserDiscnts();
            for(DiscntTradeData data : list){
                if(PersonConst.GPRS_DEFAULT_DISCNT_CODE.equals(data.getDiscntCode())){
                    return;
                }
            }

            DiscntData discnt = new DiscntData();

            discnt.setElementId(PersonConst.GPRS_DEFAULT_DISCNT_CODE);
            discnt.setModifyTag(BofConst.MODIFY_TAG_ADD);

            IData pkgElement = this.getPackageElement(productElements, PersonConst.GPRS_DEFAULT_DISCNT_CODE, BofConst.ELEMENT_TYPE_CODE_DISCNT);
            if (pkgElement != null)// 设置PRODUCT_ID,PACKAGE_ID
            {
                discnt.setProductId(pkgElement.getString("PRODUCT_ID"));
                discnt.setPackageId(pkgElement.getString("PACKAGE_ID"));
            }
            else
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_111, PersonConst.GPRS_DEFAULT_DISCNT_CODE);
            }
            discnt.setStartDate(SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate()));
            pmds.add(discnt);
        }
    }
    
    @Override
    public BaseReqData buildRequestData(IData param) throws Exception
    {
    	String strBatChId = param.getString("BATCH_ID");
    	String strBatchOperType = param.getString("BATCH_OPER_TYPE");
    	if(StringUtils.isNotBlank(strBatChId) && "MODIFYPRODUCT_NAME".equals(strBatchOperType))
    	{
    		//param.put("IS_REAL_NAME", "1");
    		//param.put("ACCT_TAG", "0");
    		param.put("SKIP_RULE", "TRUE");
    	}
    	BaseReqData brd = super.buildRequestData(param);
    	return brd;
    }

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ChangeProductReqData request = (ChangeProductReqData) brd;
        ChangeProductBean productBean=new ChangeProductBean();
        
        String strBatChId = param.getString("BATCH_ID");
    	String strBatchOperType = param.getString("BATCH_OPER_TYPE");
    	
    	if(StringUtils.isNotBlank(strBatChId) && StringUtils.isNotBlank(strBatchOperType))
    	{
    		input = new DataMap();
        	input.put("BATCH_ID", strBatChId);
        	input.put("BATCH_OPER_TYPE", strBatchOperType);
    	}
        
        UcaData uca = brd.getUca();

        if (uca.getUserMainProduct() != null && !StringUtils.equals("00", uca.getUserMainProduct().getProductMode()) && !StringUtils.equals("15", uca.getUserMainProduct().getProductMode()))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_246);
        }

        String eparchyCode = uca.getUserEparchyCode();
        String serialNumber = uca.getSerialNumber();
        String userId = uca.getUserId();

        // 预约时间处理
        if (StringUtils.isNotBlank(param.getString("BOOKING_DATE")))
        {
            if (ProductUtils.isBookingChange(param.getString("BOOKING_DATE")))
            {
                request.setBookingDate(param.getString("BOOKING_DATE"));
                request.setBookingTag(true);
            }
        }

        // 新增国际长途服务时,此值为19为同时添加国际漫游服务标识
        // 两城一家、非常假期删除的元素
        request.setOptionParam1(param.getString("RSRV_STR1"));

        IDataset selectedElementsTemp = new DatasetList(param.getString("SELECTED_ELEMENTS"));

        IDataset selectedElements = new DatasetList();
        
        for(int i = 0, size = selectedElementsTemp.size(); i < size; i++)// 排除服务或者平台重复的元素【短厅过来有重复ID出现,比如彩铃等】
        {
            IData elementTemp = selectedElementsTemp.getData(i);

            String elementIdTemp = elementTemp.getString("ELEMENT_ID");
            String elementTypeCodeTemp = elementTemp.getString("ELEMENT_TYPE_CODE");
            String modifyTagTemp = elementTemp.getString("MODIFY_TAG");

            if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCodeTemp) || BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(elementTypeCodeTemp))
            {
                boolean isExists = false;
                for(int j = 0; j < selectedElements.size(); j++)
                {
                    IData elementExist = selectedElements.getData(j);

                    String elementId = elementExist.getString("ELEMENT_ID");
                    String elementTypeCode = elementExist.getString("ELEMENT_TYPE_CODE");
                    String modifyTag = elementExist.getString("MODIFY_TAG");

                    if(elementIdTemp.equals(elementId) && elementTypeCodeTemp.equals(elementTypeCode) && modifyTagTemp.equals(modifyTag))
                    {
                        isExists = true;
                        break;
                    }
                }
                if(!isExists)
                {
                    selectedElements.add(elementTemp);
                }
            }
            else
            {
                selectedElements.add(elementTemp);
            }
        }
        
        if (selectedElements != null && selectedElements.size() > 0)
        {
            // 如有主产品变更,先设置主产品
            for (int i = 0, size = selectedElements.size(); i < size; i++)
            {
                IData element = selectedElements.getData(i);

                String elementId = element.getString("ELEMENT_ID");
                String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");

                if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode))
                {
                    request.setNewMainProduct(elementId);
                    break;
                }
            }

            IDataset productElements = this.getProductElements(request);

            // 处理元素
            List<ProductModuleData> elements = new ArrayList<ProductModuleData>();
            
            for (int i = 0, size = selectedElements.size(); i < size; i++)
            {
                IData element = selectedElements.getData(i);

                String elementId = element.getString("ELEMENT_ID");
                String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                String modifyTag = element.getString("MODIFY_TAG");
                String instId = element.getString("INST_ID");

                if (!BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode))
                {
                    if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))// 新增
                    {
                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))// 判定优惠是否已经存在
                        {
                            List<DiscntTradeData> userDiscnts = uca.getUserDiscntByDiscntId(elementId);
                            if (userDiscnts != null && userDiscnts.size() > 0)
                            {
                            	OfferCfg offerCfg = OfferCfg.getInstance(elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT);
                                String orderMode = offerCfg != null?offerCfg.getOrderMode():"";//StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "ORDER_MODE", elementId);
                                if (!"R".equals(orderMode) && !"C".equals(orderMode) && !isExistsLastMonthByCityVacation(elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT, uca))// 两城一家
                                // 非常假期
                                // 截止到本月末的要绕过
                                {
                                    String discntName = offerCfg !=null ? offerCfg.getOfferName(): "";
                                    
                                    //验证是否是非常假期或者两城一家的套餐
                                    int judgeResult=productBean.judgeIsCityVacationData(elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT);
                                    if(judgeResult!=0&&CSBizBean.getVisit().getInModeCode()!=null&&CSBizBean.getVisit().getInModeCode().equals("5")){
                                    	if(judgeResult==1||judgeResult==2){		//1是两城一家 或者  2是非常假期
                                    		CSAppException.apperr(ElementException.CRM_ELEMENT_310, productBean.getWarnInfo(judgeResult, discntName,CSBizBean.getVisit().getInModeCode()));
                                    	}else{
                                    		CSAppException.apperr(ElementException.CRM_ELEMENT_36, discntName);
                                    	}
                                    }else{
                                    	CSAppException.apperr(ElementException.CRM_ELEMENT_36, discntName);
                                    }
                                    
                                }
                            }
                        }
                        else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))// 判定服务是否已经存在
                        {
                            List<SvcTradeData> userSvcs = uca.getUserSvcBySvcId(elementId);
                            if (userSvcs != null && userSvcs.size() > 0)
                            {
                            	OfferCfg offerCfg = OfferCfg.getInstance(elementId, BofConst.ELEMENT_TYPE_CODE_SVC);
                                String orderMode = offerCfg != null ? offerCfg.getOrderMode() : "";//StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_SERVICE", "SERVICE_ID", "ORDER_MODE", elementId);
                                if (!"R".equals(orderMode) && !"C".equals(orderMode))
                                {
                                    String serviceName = offerCfg.getOfferName();
                                    CSAppException.apperr(ElementException.CRM_ELEMENT_34, serviceName);
                                }
                            }
                        }

                        IData pkgElement =this.getPackageElement(productElements, elementId, elementTypeCode);
                        if (pkgElement != null)// 设置PRODUCT_ID,PACKAGE_ID
                        {
                            element.put("PRODUCT_ID", pkgElement.getString("PRODUCT_ID"));
                            element.put("PACKAGE_ID", pkgElement.getString("PACKAGE_ID"));
                            if("52000010".equals(elementId)){		//流量卡公用流量包优惠
                                element.put("REMARK", param.getString("FLOW_CARD", ""));
                            }
                        }
                        else
                        {
                            if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
                            {
                                String isPlatOrder = param.getString("IS_PLAT_ORDER");
                                if(StringUtils.isNotBlank(isPlatOrder) && "true".equals(isPlatOrder))
                                {
                                    this.buildSvcDataByPmdNoElementId(brd.getAcceptTime(), element, elements);
                                    
                                    continue;
                                }
                                else
                                {
                                	if (param.getString("ELEMENT_NOT_IN_PROD_PACK_FLAG","").equals("1")) {//指定PRODUCT_ID,PACKAGE_ID为-1
                                        element.put("PRODUCT_ID", "-1");
                                        element.put("PACKAGE_ID", "-1");
                                	} else if (param.getString("ELEMENT_NOT_IN_PROD_PACK_FLAG","").equals("N")) {//不用判断在组里,取生效的PRODUCT_ID
                                		    String productId = "";
                                	        ProductTradeData nextMainProduct = uca.getUserNextMainProduct();
                                	        ProductData newMainProduct = request.getNewMainProduct();
                                	        if (newMainProduct != null && !uca.getProductId().equals(newMainProduct.getProductId()))// 当既有预约产品,又办理新产品时候报错
                                	        {
                                	            if (nextMainProduct != null)
                                	            {
                                	                CSAppException.apperr(ProductException.CRM_PRODUCT_195);
                                	            }
                                	        }
                                	        if (newMainProduct != null)// 取新产品ID
                                	        {
                                	            productId = newMainProduct.getProductId();
                                	        }
                                	        else if (nextMainProduct != null)// 取预约产品ID
                                	        {
                                	            productId = nextMainProduct.getProductId();
                                	        }
                                	        else
                                	        {
                                	            productId = uca.getProductId();
                                	        }
	                                		element.put("PRODUCT_ID", productId);
	                              		    element.put("PACKAGE_ID", "-1");
	                              		    if(StringUtils.isNotEmpty(param.getString("END_DATE"))){
	                              		    	element.put("END_DATE", param.getString("END_DATE"));
	                              		    }
                              		    
                                	}else{
                                		CSAppException.apperr(ProductException.CRM_PRODUCT_111, elementId);
                                	}
                                }
                            }
                            else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
                            {
                                if (this.isPlatDiscntAndBuildData(elementId, elements, uca))// 或许是平台服务
                                {
                                    continue;
                                }
                                else if (this.isNewVpmnDiscntAndBuildData(elementId, request, uca))// 或许是VPMN优惠
                                {
                                    continue;
                                }else if(isAbilityInterRoamDirectDiscnt(elementId)){		//如果是能力平台过来的国漫定向套餐
                                	element.put("PRODUCT_ID", "-1");
                                    element.put("PACKAGE_ID", "99990000");
                                }
                                else if("52000010".equals(elementId)){		//流量卡公用流量包优惠
                                	element.put("PRODUCT_ID", "-1");
                                    element.put("PACKAGE_ID", "-1");
                                    element.put("REMARK", param.getString("FLOW_CARD", ""));
                                }
                                else
                                {
                                    CSAppException.apperr(ProductException.CRM_PRODUCT_111, elementId);
                                }
                            }
                        }
                        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))// 服务新增
                        {
                            SvcData svcData = new SvcData(element);
                            svcData.setStartDate(brd.getAcceptTime());// 服务立即生效
                            elements.add(svcData);
                        }
                        else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))// 优惠新增
                        {
                            DiscntData discnt = new DiscntData(element);
                            elements.add(discnt);

                            // 如果没有设置attr属性 如存在属性且属性值不能为空 则设置默认属性
                            IDataset attrs = UItemAInfoQry.queryOfferChaByIdAndIdType(BofConst.ELEMENT_TYPE_CODE_DISCNT, elementId, eparchyCode);
                            List<AttrData> attrDatas = new ArrayList<AttrData>();
                            if (IDataUtil.isEmpty(element.getDataset("ATTR_PARAM")) && IDataUtil.isNotEmpty(attrs))
                            {
                                int length = attrs.size();
                                for (int j = 0; j < length; j++)
                                {
                                    IData attr = attrs.getData(j);
                                    if ("0".equals(attr.getString("ATTR_CAN_NULL")))
                                    {
                                        AttrData attrData = new AttrData();
                                        attrData.setAttrCode(attr.getString("ATTR_CODE"));
                                        attrData.setAttrValue(attr.getString("ATTR_INIT_VALUE"));
                                        attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                                        attrDatas.add(attrData);
                                    }
                                }
                                discnt.setAttrs(attrDatas);
                            }

                            this.expactElementBybatch(elementId, eparchyCode, serialNumber, userId, request);
                        }
                        else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(elementTypeCode))// 平台服务订购
                        {
                            element.put("OPER_CODE", PlatConstants.OPER_ORDER);
                            PlatSvcData platSvcData = new PlatSvcData(element);
                            elements.add(platSvcData);
                        }
                    }
                    else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag) || BofConst.MODIFY_TAG_UPD.equals(modifyTag))// 删除、修改
                    {
                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
                        {
                            List<DiscntTradeData> userDiscnts = new ArrayList<DiscntTradeData>();
                            if(StringUtils.isNotBlank(instId))
                            {
                                DiscntTradeData userDiscnt = uca.getUserDiscntByInstId(instId);
                                if(userDiscnt != null)
                                {
                                    userDiscnts.add(userDiscnt);
                                }
                            }
                            else
                            {
                                userDiscnts = uca.getUserDiscntByDiscntId(elementId);
                            }
                            if (userDiscnts == null || userDiscnts.size() <= 0)// 校验用户是否订购优惠
                            {
                                CSAppException.apperr(ElementException.CRM_ELEMENT_38, elementId);
                            }

                            if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))// 当MODIFY_TAG为删除且UCA元素MODIFY_TAG非删除时做删除操作
                            {
                                if (PersonConst.GPRS_DEFAULT_DISCNT_CODE.equals(elementId))// 标准GPRS优惠不能删除
                                {
                                	OfferCfg offerCfg = OfferCfg.getInstance(elementId, elementTypeCode);
                                    String elementName = offerCfg != null?offerCfg.getOfferName():"";

                                    CSAppException.apperr(UserDiscntException.CRM_USER_DISCNT_5, elementName);
                                }

                                for (DiscntTradeData userDiscnt : userDiscnts)
                                {
                                    if (!userDiscnt.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
                                    {
                                        DiscntData discnt = new DiscntData(userDiscnt.toData());
                                        discnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                        elements.add(discnt);
                                    }
                                }
                            }
                            else if (BofConst.MODIFY_TAG_UPD.equals(modifyTag))
                            {
                                for (DiscntTradeData userDiscnt : userDiscnts)
                                {
                                    if (!userDiscnt.getModifyTag().equals(BofConst.MODIFY_TAG_UPD))
                                    {
                                        DiscntData discnt = new DiscntData(userDiscnt.toData());
                                        discnt.setModifyTag(BofConst.MODIFY_TAG_UPD);
                                        discnt.setAttrs(getAttrDataByModify(element, uca));
                                        elements.add(discnt);
                                    }
                                }
                            }
                        }
                        else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
                        {
                            List<SvcTradeData> userSvcs = uca.getUserSvcBySvcId(elementId);
                            if (userSvcs == null || userSvcs.size() <= 0)
                            {
                                CSAppException.apperr(ElementException.CRM_ELEMENT_37, elementId);
                            }

                            SvcTradeData userSvc = userSvcs.get(0);
                            if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))// 当MODIFY_TAG为删除且UCA元素MODIFY_TAG非删除时做删除操作
                            {
                                if (!userSvc.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
                                {
                                    SvcData svc = new SvcData(userSvc.toData());
                                    svc.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                    elements.add(svc);

                                    // 海南取消彩铃连带取消铃音盒
                                    this.cancelBellRingByDelColorRingSvc(element, elements, brd);
                                }
                            }
                            else if (BofConst.MODIFY_TAG_UPD.equals(modifyTag))
                            {
                                if (!userSvc.getModifyTag().equals(BofConst.MODIFY_TAG_UPD))
                                {
                                    SvcData svc = new SvcData(userSvc.toData());
                                    svc.setModifyTag(BofConst.MODIFY_TAG_UPD);
                                    svc.setAttrs(getAttrDataByModify(element, uca));
                                    elements.add(svc);
                                }
                            }
                        }
                        else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(elementTypeCode))
                        {
                            if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))// 平台服务退订
                            {
                                element.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
                                PlatSvcData platSvcData = new PlatSvcData(element);
                                elements.add(platSvcData);
                            }
                        }
                    }
                }
            }

            // 其他元素处理
            this.elementOtherDeal(productElements, elements, uca);
            
            if(!StringUtils.isEmpty(param.getString("FLOW_PAYMENT_ID",""))){
    			request.setRemark(param.getString("FLOW_PAYMENT_ID")); //移动商城接口1.8-流量直充接口 保存操作流水号 add by lihb3 
    		}
            
            if(!StringUtils.isEmpty(param.getString("PRINT_TICKET",""))){//移动商城接口1.8-流量直充接口 保存订单金额以便打印发票 add by lihb3 
            	request.setRsrvStr1("UMMP_FLOW");
            	request.setRsrvStr2(param.getString("PAYMENT"));       //实付金额       	
            	request.setRsrvStr3(param.getString("REL_PAYMENT"));   //实付金额+折减金额
    		}
            if(StringUtils.isNotEmpty( param.getString("ELEC_TAG",""))){//移动商城接口2.5等接口打发票用
            	request.setRsrvStr1(param.getString("ELEC_TAG",""));
            	request.setRsrvStr2(param.getString("PAY_MONEY"));       //实付金额       	
            	request.setRsrvStr3(param.getString("POINT_CHANGE_MONEY"));  //积分抵扣金额
            	request.setRemark(param.getString("TRANSACTION_ID"));
    		} 
            request.setProductElements(elements);
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

    /**
     * @Description: 处理优惠
     * @param productElements
     * @param pmd
     * @param pmds
     * @param uca
     * @throws Exception
     * @author: maoke
     * @date: Aug 18, 2014 8:37:00 PM
     */
    public void dealDiscntElement(IDataset productElements, ProductModuleData pmd, List<ProductModuleData> pmds, UcaData uca) throws Exception
    {
        String elementId = pmd.getElementId();
        String modifyTag = pmd.getModifyTag();
        String discntElementType = UDiscntInfoQry.getDiscntTypeByDiscntCode(elementId);
        if (StringUtils.isNotBlank(discntElementType) && (PersonConst.DISCNT_TYPE_5.equals(discntElementType) || PersonConst.DISCNT_TYPE_7.equals(discntElementType)))
        {
            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                if (PersonConst.DISCNT_TYPE_5.equals(discntElementType))
                {
                    this.dealSameTypeGprsDiscnt(PersonConst.DISCNT_TYPE_5, pmds, uca);

                    this.dealSameTypeGprsDiscnt(PersonConst.DISCNT_TYPE_LLCX, pmds, uca);
                }
                if (PersonConst.DISCNT_TYPE_7.equals(discntElementType))
                {
                    this.dealSameTypeGprsDiscnt(PersonConst.DISCNT_TYPE_7, pmds, uca);
                }
            }
            if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                List<SvcTradeData> userSvcs = uca.getUserSvcBySvcId(PersonConst.SERVICE_ID_22);

                if (userSvcs != null && userSvcs.size() > 0)// 默认添加GPRS标准资费
                {
                    this.addDefaultGprsDiscnt(productElements, pmds,uca);
                }
            }
        }
    }

    /**
     * @Description: 处理GPRS服务
     * @param productElements
     * @param pmd
     * @param pmds
     * @param uca
     * @throws Exception
     * @author: maoke
     * @date: Aug 18, 2014 9:05:52 PM
     */
    public void dealGprsSvcElement(IDataset productElements, ProductModuleData pmd, List<ProductModuleData> pmds, UcaData uca) throws Exception
    {
        String elementId = pmd.getElementId();
        String modifyTag = pmd.getModifyTag();

        if (PersonConst.SERVICE_ID_22.equals(elementId))
        {
            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                // 如本次有添加gprs优惠 则返回
                if (pmds != null && pmds.size() > 0)
                {
                    for (ProductModuleData productData : pmds)
                    {
                        String tempElementId = productData.getElementId();
                        String tempModify = productData.getModifyTag();
                        String tempElementType = productData.getElementType();
                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(tempElementType))
                        {
                            String tempDiscntType = UDiscntInfoQry.getDiscntTypeByDiscntCode(tempElementId);
                            if (StringUtils.isNotBlank(tempDiscntType) && PersonConst.DISCNT_TYPE_5.equals(tempDiscntType))
                            {
                                if (BofConst.MODIFY_TAG_ADD.equals(tempModify))
                                {
                                    return;
                                }
                            }
                        }
                    }

                    // 默认添加gprs优惠
                    this.addDefaultGprsDiscnt(productElements, pmds,uca);
                }
            }
            if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
            	if(IDataUtil.isNotEmpty(this.input))
            	{
            		String strBatChId = this.input.getString("BATCH_ID");
                	String strBatchOperType = this.input.getString("BATCH_OPER_TYPE");
                	if(StringUtils.isNotBlank(strBatChId) && "SERVICECHGSPEC".equals(strBatchOperType))
                	{
                		return;
                	}
            	}
            	
                List<DiscntTradeData> discntList = uca.getUserDiscnts();

                for (DiscntTradeData discntData : discntList)
                {
                    String tempElementId = discntData.getElementId();
                    String tempModifyTag = discntData.getModifyTag();

                    // 非本次删除的元素且存在依赖22的元素
                    if (!BofConst.MODIFY_TAG_DEL.equals(tempModifyTag) && this.isRelayGprsSvcByElementId(tempElementId))
                    {
                        String orderMode = OfferCfg.getInstance(tempElementId, BofConst.ELEMENT_TYPE_CODE_DISCNT).getOrderMode();//StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "ORDER_MODE", tempElementId);

                        if (!"R".equals(orderMode))
                        {
                            DiscntData discnt = new DiscntData(discntData.toData());
                            discnt.setModifyTag(BofConst.MODIFY_TAG_DEL);

                            pmds.add(discnt);
                        }
                    }
                }
            }
        }
    }

    /**
     * @Description: 删除同类型优惠
     * @param discntElementType
     * @param pmds
     * @param uca
     * @throws Exception
     * @author: maoke
     * @date: Aug 18, 2014 9:24:57 PM
     */
    public void dealSameTypeGprsDiscnt(String discntElementType, List<ProductModuleData> pmds, UcaData uca) throws Exception
    {
        List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();

        if (userDiscnts != null && userDiscnts.size() > 0)
        {
            for (DiscntTradeData userDiscnt : userDiscnts)
            {
                String tempElementId = userDiscnt.getElementId();
                String tempModifyTag = userDiscnt.getModifyTag();
                String tempDiscntType = UDiscntInfoQry.getDiscntTypeByDiscntCode(tempElementId);

                if (discntElementType.equals(tempDiscntType))
                {
                    if (!isExistProductModuleData(BofConst.ELEMENT_TYPE_CODE_DISCNT, tempElementId, tempModifyTag, pmds))
                    {
                        DiscntData discnt = new DiscntData(userDiscnt.toData());
                        discnt.setModifyTag(BofConst.MODIFY_TAG_DEL);

                        pmds.add(discnt);
                    }
                }
            }
        }
    }

    /**
     * @Description: 元素其他处理【添加默认元素、删除互斥元素等】
     * @param pmds
     * @param uca
     * @throws Exception
     * @author: maoke
     * @date: Aug 18, 2014 5:22:35 PM
     */
    public void elementOtherDeal(IDataset productElements, List<ProductModuleData> pmds, UcaData uca) throws Exception
    {
        if (pmds != null && pmds.size() > 0)
        {
            int size = pmds.size();

            for (int i = 0; i < size; i++)
            {
                ProductModuleData pmd = pmds.get(i);

                String elementTypeCode = pmd.getElementType();

                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
                {
                    this.dealDiscntElement(productElements, pmd, pmds, uca);
                }
                else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
                {
                    this.dealGprsSvcElement(productElements, pmd, pmds, uca);
                }
            }
        }
    }

    /**
     * @Description: 批量过来【关于配合集团“和4G，点亮精彩世界”开发专属流量包的需求】
     * @param elementId
     * @param eparchyCode
     * @param serialNumber
     * @param userId
     * @param request
     * @throws Exception
     * @author: maoke
     * @date: Aug 28, 2014 4:14:27 PM
     */
    public void expactElementBybatch(String elementId, String eparchyCode, String serialNumber, String userId, ChangeProductReqData request) throws Exception
    {
        String batchId = request.getBatchId();
        if (StringUtils.isNotBlank(batchId))
        {
            IDataset commpara9008 = CommparaInfoQry.getCommparaByCode1("CSM", "9008", elementId, eparchyCode);

            if (IDataUtil.isNotEmpty(commpara9008))
            {
                String paramCode = commpara9008.getData(0).getString("PARAM_CODE");

                if ("WX".equals(paramCode) || "YG".equals(paramCode))
                {
                    IDataset userTdExpact = UserTdExpactQry.qryUserTdExpactInfoBySn(serialNumber);

                    if (IDataUtil.isEmpty(userTdExpact))
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_244);
                    }
                    else
                    {
                        // 判断是否是4G用户
                        IData tdExpactData = userTdExpact.getData(0);
                        if ("1".equals(tdExpactData.getString("PRIZE_TYPE_CODE", "")) && "1".equals(tdExpactData.getString("RSRV_STR1", "")))
                        {
                            if (!PersonUtil.isLteCardUser(userId))
                            {
                                CSAppException.apperr(ProductException.CRM_PRODUCT_245);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @Description: 构建修改ATTR属性的返回值
     * @param element
     * @param uca
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 9, 2014 9:16:29 PM
     */
    public List<AttrData> getAttrDataByModify(IData element, UcaData uca) throws Exception
    {
        List<AttrData> modAttrDatas = new ArrayList<AttrData>();

        IDataset attrDatas = element.getDataset("ATTR_PARAM");
        String modifyTag = element.getString("MODIFY_TAG");

        if (IDataUtil.isNotEmpty(attrDatas))
        {
            for (int k = 0; k < attrDatas.size(); k++)
            {
                String attrCode = attrDatas.getData(k).getString("ATTR_CODE");
                String attrValue = attrDatas.getData(k).getString("ATTR_VALUE");

                List<AttrTradeData> userAttrs = uca.getUserAttrsByAttrCode(attrCode);

                if (userAttrs == null || userAttrs.size() <= 0)
                {
                    CSAppException.apperr(ElementException.CRM_ELEMENT_37, attrCode);
                }

                if (BofConst.MODIFY_TAG_UPD.equals(modifyTag))
                {
                    // 属性值更改
                    for (AttrTradeData userAttr : userAttrs)
                    {
                        if (!userAttr.getModifyTag().equals(BofConst.MODIFY_TAG_UPD))
                        {
                            AttrData atrr = new AttrData();

                            atrr.setAttrCode(attrCode);
                            atrr.setAttrValue(attrValue);
                            atrr.setModifyTag(BofConst.MODIFY_TAG_UPD);

                            modAttrDatas.add(atrr);
                        }
                    }
                }
            }
        }

        return modAttrDatas;
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ChangeProductReqData();
    }

    /**
     * @Description: 根据元素循环判断是否再所有元素集合中
     * @param productElements
     * @param elementId
     * @param elementTypeCode
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 9, 2014 2:40:48 PM
     */
    public IData getPackageElement(IDataset productElements, String elementId, String elementTypeCode) throws Exception
    {
        int size = productElements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            if (elementId.equals(element.getString("ELEMENT_ID")) && elementTypeCode.equals(element.getString("ELEMENT_TYPE_CODE")))
            {
                return element;
            }
        }
        return null;
    }

    /**
     * @Description: 元素是否已经存在
     * @param elementType
     * @param elementId
     * @param modifyTag
     * @param pmds
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Aug 18, 2014 7:47:20 PM
     */
    public boolean isExistProductModuleData(String elementType, String elementId, String modifyTag, List<ProductModuleData> pmds) throws Exception
    {
        if (pmds != null && pmds.size() > 0)
        {
            for (ProductModuleData pmd : pmds)
            {
                String pmdElementType = pmd.getElementType();
                String pmdElementId = pmd.getElementId();
                String pmdModifyTag = pmd.getModifyTag();

                if (elementType.equals(pmdElementType) && elementId.equals(pmdElementId) && modifyTag.equals(pmdModifyTag))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @Description: 是否存在两城一家、非常假期优惠结束到本月末的优惠
     * @param elementId
     * @param elementType
     * @param uca
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 9, 2014 4:44:54 PM
     */
    public boolean isExistsLastMonthByCityVacation(String elementId, String elementType, UcaData uca) throws Exception
    {
        boolean isCityVacationTag = false;// 元素是否属于两城一家 非常假期包

        IDataset cityVacationData = StaticUtil.getStaticList("INTF_CITY_VACATION_PKG");

        if (IDataUtil.isNotEmpty(cityVacationData))
        {
            for (int i = 0, size = cityVacationData.size(); i < size; i++)
            {
                IData tempData = cityVacationData.getData(i);
                String packageId = tempData.getString("DATA_ID");

                IDataset packageElement = UpcCall.queryGroupComRel(packageId, elementType, elementId);

                if (IDataUtil.isNotEmpty(packageElement))
                {
                    isCityVacationTag = true;
                    break;
                }
            }

        }

        if (isCityVacationTag)
        {
            // 根据优惠编码获取当前有效的优惠
            List<DiscntTradeData> userDiscnts = uca.getUserDiscntByDiscntId(elementId);

            if (userDiscnts != null && userDiscnts.size() > 0)
            {
                DiscntTradeData userDiscnt = userDiscnts.get(0);

                String endDate = SysDateMgr.decodeTimestamp(userDiscnt.getEndDate(), SysDateMgr.PATTERN_STAND);
                String lastDateThisMonth = SysDateMgr.decodeTimestamp(SysDateMgr.getLastDateThisMonth(), SysDateMgr.PATTERN_STAND);

                if (endDate.compareTo(lastDateThisMonth) <= 0)
                {
                    return true;
                }
            }
        }

        return false;
    }
    
    /**
     * @Description: 判定主产品变更时是否存在VPMN优惠同时构建相应数据
     * @param elementId
     * @param request
     * @param uca
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 26, 2014 3:07:04 PM
     */
    public boolean isNewVpmnDiscntAndBuildData(String elementId, ChangeProductReqData request, UcaData uca) throws Exception
    {
        // 主产品变更时
        if (request.getNewMainProduct() != null && StringUtils.isNotBlank(request.getNewMainProduct().getProductId()))
        {
            String userId = uca.getUserId();
            String eparchyCode = uca.getUserEparchyCode();
            String bookingDate = request.getBookingDate();
            String newProductId = request.getNewMainProduct().getProductId();

            IData userVpmn = new ChangeProductBean().getUserVpmnData(uca);

            if (IDataUtil.isNotEmpty(userVpmn))
            {
                String oldVpmnDiscnt = userVpmn.getString("OLD_VPMN_DISCNT", "");
                String vpmnUserIdA = userVpmn.getString("VPMN_USER_ID_A");
                String vpmnProductId = userVpmn.getString("VPMN_PRODUCT_ID");

                IData newVpmnData = new ChangeProductBean().getNewVpmnDiscnt(newProductId, oldVpmnDiscnt, eparchyCode, bookingDate, vpmnUserIdA, vpmnProductId);

                if (IDataUtil.isNotEmpty(newVpmnData))
                {
                    IDataset newVpmnList = newVpmnData.getDataset("NEW_VPMN_DISCNT");

                    if (IDataUtil.isNotEmpty(newVpmnList))
                    {
                        for (int i = 0, size = newVpmnList.size(); i < size; i++)
                        {
                            IData newVpmnDiscnt = newVpmnList.getData(i);

                            String vpmnElementId = newVpmnDiscnt.getString("ELEMENT_ID");

                            if (elementId.equals(vpmnElementId))
                            {
                                request.setOldVpmnDiscnt(oldVpmnDiscnt);
                                request.setNewVpmnDisnct(vpmnElementId);

                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @Description: 是否平台优惠并构建平台业务数据【select * from td_s_commpara t where t.param_attr = 3700 and t.end_date > sysdate】
     * @param
     * @param elements
     * @param uca
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 26, 2014 3:51:38 PM
     */
    public boolean isPlatDiscntAndBuildData(String discntCode, List<ProductModuleData> elements, UcaData uca) throws Exception
    {
        IDataset configs = CommparaInfoQry.getCommparaByAttrCode2("CSM", "3700", discntCode, uca.getUserEparchyCode(), null);

        if (IDataUtil.isNotEmpty(configs))
        {
            String operCode = "";
            IDataset attrDatas = new DatasetList();

            IData config = configs.getData(0);

            String serviceId = config.getString("PARAM_CODE");// 平台服务ID
            String attrCode = config.getString("PARA_CODE3");// ATTR_CODE
            String attrValue = config.getString("PARA_CODE1");// ATTR_VALUE

            if ("98002401".equals(serviceId) || "98009201".equals(serviceId))// WLAN或者高校WLAN注册
            {
                String wlanOperCode = "";

                List<PlatSvcTradeData> userWlans = uca.getUserPlatSvcByServiceId(serviceId);
                if (userWlans != null && userWlans.size() > 0)
                {
                    PlatSvcTradeData userWlan = userWlans.get(0);

                    AttrTradeData userWlanAttr = uca.getUserAttrsByRelaInstIdAttrCode(userWlan.getInstId(), attrCode);

                    if (userWlanAttr != null)
                    {
                        if (!attrValue.equals(userWlanAttr.getAttrValue()))
                        {
                            if ("00000".equals(userWlanAttr.getAttrValue()))
                            {
                                wlanOperCode = PlatConstants.OPER_ORDER_TC;
                            }
                            else
                            {
                                wlanOperCode = PlatConstants.OPER_CHANGE_TC;
                            }
                        }
                        else
                        {
                            CSAppException.apperr(PlatException.CRM_PLAT_0975_7);
                        }
                    }
                    else
                    {
                        wlanOperCode = PlatConstants.OPER_ORDER_TC;
                    }
                }
                else
                {
                    wlanOperCode = PlatConstants.OPER_ORDER;
                }

                if (StringUtils.isNotBlank(operCode) && !operCode.equals(wlanOperCode))
                {
                    CSAppException.apperr(PlatException.CRM_PLAT_1004);
                }

                operCode = wlanOperCode;

                String discntType = "";
                if ("401".equals(attrCode))
                {
                    discntType = "1";
                }
                else
                {
                    discntType = "2";
                }

                if (PlatConstants.OPER_ORDER.equals(operCode))
                {
                    IData attrBusiLevel = new DataMap();
                    attrBusiLevel.put("ATTR_CODE", "001");
                    attrBusiLevel.put("ATTR_VALUE", "10");
                    attrDatas.add(attrBusiLevel);

                    IData attrPasswd = new DataMap();
                    attrPasswd.put("ATTR_CODE", "AIOBS_PASSWORD");
                    attrPasswd.put("ATTR_VALUE", PlatUtils.geneComplexRandomPassword());
                    attrDatas.add(attrPasswd);

                    IData attrPackageType = new DataMap();
                    attrPackageType.put("ATTR_CODE", "SEL_TYPE");
                    attrPackageType.put("ATTR_VALUE", discntType);
                    attrDatas.add(attrPackageType);
                }
            }

            if (StringUtils.isNotBlank(attrCode) && StringUtils.isNotBlank(attrValue))
            {
                IData attr = new DataMap();

                attr.put("ATTR_CODE", attrCode);
                attr.put("ATTR_VALUE", attrValue);

                attrDatas.add(attr);
            }

            IData plat = new DataMap();
            plat.put("SERVICE_ID", serviceId);
            plat.put("OPER_CODE", StringUtils.isNotBlank(operCode) ? operCode : PlatConstants.OPER_ORDER);
            plat.put("ATTR_PARAM", attrDatas);

            if (IDataUtil.isNotEmpty(plat))
            {
                PlatSvcData psd = new PlatSvcData(plat);
                elements.add(psd);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * @Description: 构建当产品下面没有此元素时候的服务数据对象
     * @param startDate
     * @param element
     * @param elements
     * @throws Exception
     * @author: maoke
     * @date: Sep 28, 2014 6:28:54 PM
     */
    public void buildSvcDataByPmdNoElementId(String startDate, IData element, List<ProductModuleData> elements)throws Exception
    {
        SvcData svcData = new SvcData(element);
        svcData.setProductId("50000000");
        svcData.setPackageId("50000000");
        svcData.setStartDate(startDate);
        svcData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        elements.add(svcData);
    }
    
    /**
     * @Description: 元素是否完全依赖GPRS服务
     * @param elementId
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Aug 13, 2014 9:56:19 PM
     */
    public boolean isRelayGprsSvcByElementId(String elementId) throws Exception
    {
        boolean rst = UpcCall.hasSpecificOfferRelThisTwoOffer(elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT, "22", BofConst.ELEMENT_TYPE_CODE_SVC, "2");
        return rst;
    }
    
    public boolean isAbilityInterRoamDirectDiscnt(String discntCode)throws Exception{
    	IDataset discntConfigDatas =UpcCall.queryGroupComRel("99990000", BofConst.ELEMENT_TYPE_CODE_DISCNT, discntCode);//PkgElemInfoQry.getServElementByPk("99990000", "D", discntCode);
		if(IDataUtil.isNotEmpty(discntConfigDatas)){
			return true;
		}else{
			return false;
		}
    	
    }
}
