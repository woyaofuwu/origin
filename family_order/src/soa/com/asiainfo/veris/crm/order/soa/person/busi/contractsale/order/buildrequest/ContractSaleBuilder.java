/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.contractsale.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgExtInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.contractsale.order.requestdata.ContractSaleReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.buildrequest.common.SaleActiveCommonBuilder;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ContractSaleBuilder.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-25 上午09:36:14 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-25 chengxf2 v1.0.0 修改原因
 */

public class ContractSaleBuilder extends SaleActiveCommonBuilder implements IBuilder
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-7-28 下午09:32:13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-28 chengxf2 v1.0.0 修改原因
     */
    private void buildDiscntData(IData discntData, ContractSaleReqData contractSaleReqData, IData param) throws Exception
    {
        if (StringUtils.isNotBlank(param.getString("CONSUME_LIMIT")))
        {
            IDataset attrs = new DatasetList();
            UcaData uca = contractSaleReqData.getUca();
            String elementId = discntData.getString("ELEMENT_ID");

            if (IDataUtil.isNotEmpty(AttrItemInfoQry.qryItemA(elementId, "D", "MONEY", uca.getUserEparchyCode(), null)))
            {
                IData moneyAttr = new DataMap();
                moneyAttr.put("ATTR_CODE", "MONEY");
                moneyAttr.put("ATTR_VALUE", param.getString("CONSUME_LIMIT"));
                moneyAttr.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                attrs.add(moneyAttr);
            }

            if (IDataUtil.isNotEmpty(AttrItemInfoQry.qryItemA(elementId, "D", "KMNum", uca.getUserEparchyCode(), null)))
            {
                IData kmnumAttr = new DataMap();
                kmnumAttr.put("ATTR_CODE", "KMNum");
                kmnumAttr.put("ATTR_VALUE", param.getString("CONSUME_LIMIT"));
                kmnumAttr.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                attrs.add(kmnumAttr);
            }

            discntData.put("ATTR_PARAM", attrs);
        }

    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-28 下午09:32:06 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-28 chengxf2 v1.0.0 修改原因
     */
    private void buildSaleDepositData(IData saleDepositData, ContractSaleReqData contractSaleReqData, IData param)
    {
        String giftFee = param.getString("GIFT_FEE", "0");
        if (StringUtils.isNotEmpty(giftFee))
        {
            saleDepositData.put("PAYMENT_ID", "160");
            saleDepositData.put("FEE", param.getString("GIFT_FEE"));
            saleDepositData.put("MONTHS", param.getString("MONTHS"));
            saleDepositData.put("DEPOSIT_TYPE", "1");
        }

        IDataset attrs = new DatasetList();

        IData offsetAttr = new DataMap();
        offsetAttr.put("ATTR_CODE", "ITEM_END_OFFSET"); // 返回月份
        offsetAttr.put("ATTR_VALUE", param.getString("MONTHS"));
        offsetAttr.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        attrs.add(offsetAttr);

        IData itemFee = new DataMap();
        itemFee.put("ATTR_CODE", "ITEM_FEE"); // 预存话费
        itemFee.put("ATTR_VALUE", param.getString("DEPOSIT_FEE", "0"));
        itemFee.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        attrs.add(itemFee);

        IData itemGiftMonths = new DataMap();
        itemGiftMonths.put("ATTR_CODE", "ITEM_GIFT_MONTHS"); // 赠送月份
        itemGiftMonths.put("ATTR_VALUE", param.getString("MONTHS"));
        itemGiftMonths.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        attrs.add(itemGiftMonths);

        IData itemGiftFee = new DataMap();
        itemGiftFee.put("ATTR_CODE", "ITEM_GIFT_FEE"); // 赠送话费
        itemGiftFee.put("ATTR_VALUE", param.getString("GIFT_FEE", "0"));
        itemGiftFee.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        attrs.add(itemGiftFee);

        IData startDateAttr = new DataMap();
        startDateAttr.put("ATTR_CODE", "ITEM_START_DATE"); // 返回开始时间
        startDateAttr.put("ATTR_VALUE", saleDepositData.getString("START_DATE"));
        startDateAttr.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        attrs.add(startDateAttr);

        saleDepositData.put("ATTR_PARAM", attrs);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-28 下午09:32:15 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-28 chengxf2 v1.0.0 修改原因
     */
    private void buildSaleGoodsData(IData saleGoodsData, ContractSaleReqData contractSaleReqData, IData param)
    {
        contractSaleReqData.setSaleGoodsImei(param.getString("RES_CODE"));// 设置终端串号
        contractSaleReqData.setSaleStaffId(param.getString("SALE_STAFF_ID"));// 终端促销员工，终端接口交互
        saleGoodsData.put("RES_CODE", param.getString("RES_CODE"));
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-25 上午09:36:15 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-25 chengxf2 v1.0.0 修改原因
     */
    protected IDataset buildSelectedElementsFee(IDataset selectedElements, IData input, String productId, String packageId, String eparchyCode) throws Exception
    {
        return null;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-25 上午09:36:15 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-25 chengxf2 v1.0.0 修改原因
     */
    protected IData getActiveDates(IData param, BaseReqData brd) throws Exception
    {
        ContractSaleReqData contractSaleReqData = (ContractSaleReqData) brd;

        String campnType = contractSaleReqData.getCampnType();
        String productId = contractSaleReqData.getProductId();
        String packageId = contractSaleReqData.getPackageId();

        UcaData uca = contractSaleReqData.getUca();
        String eparchyCode = uca.getUserEparchyCode();

        IDataset pkgExtDataSet = PkgExtInfoQry.queryPackageExtInfo(packageId, eparchyCode);
        if (IDataUtil.isEmpty(pkgExtDataSet))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_6, packageId);
        }

        IData pkgExtData = pkgExtDataSet.getData(0);
        pkgExtData.put("ACCT_DAY", uca.getAcctDay());
        pkgExtData.put("FIRST_DATE", uca.getFirstDate());
        pkgExtData.put("NEXT_ACCT_DAY", uca.getNextAcctDay());
        pkgExtData.put("NEXT_FIRST_DATE", uca.getNextFirstDate());
        pkgExtData.put("CAMPN_TYPE", campnType);
        pkgExtData.put("PRODUCT_ID", productId);
        pkgExtData.put("SERIAL_NUMBER", uca.getSerialNumber());
        pkgExtData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset saleActiveDateSet = CSAppCall.call("CS.SaleActiveDateSVC.callActiveStartEndDate", pkgExtData);
        return saleActiveDateSet.getData(0);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-28 上午10:08:40 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-28 chengxf2 v1.0.0 修改原因
     */
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ContractSaleReqData();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-25 上午09:36:15 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-25 chengxf2 v1.0.0 修改原因
     */
    protected IDataset getPayMoneyList(IData input, IDataset tradeFeeSub)
    {
        return null;
    }

    // private void buildContractDetailDesc(IData param, ContractSaleReqData brd, UcaData uca) throws Exception
    // {
    // String changeProductInfo = param.getString("CHANGEPRODUCT_INFO");
    // StringBuilder consumeDetailDesc = new StringBuilder();
    // String userEparchyCode = uca.getUserEparchyCode();
    // int consumeLimit = param.getInt("CONSUME_LIMIT");
    //    	
    // int elementConsumeLimit = 0;
    // if (StringUtils.isNotBlank(changeProductInfo))
    // {
    // IData changeProductInfoData = new DataMap(changeProductInfo);
    // Iterator iterator = changeProductInfoData.keySet().iterator();
    // while(iterator.hasNext())
    // {
    // String key = (String)iterator.next();
    // IDataset group = changeProductInfoData.getDataset(key);
    // if(IDataUtil.isNotEmpty(group))
    // {
    // for(Object o : group)
    // {
    // IData element = (IData)o;
    // String elementTypeCode = element.getString("ELEMENT_TYPE_CODE","");
    // String elementId = element.getString("ELEMENT_ID","");
    // String elementName = "";
    // int price = 0;
    // if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
    // {
    // IData discnt = DiscntInfoQry.getDiscntInfoByDiscntCode(elementId);
    // if (IDataUtil.isEmpty(discnt))
    // {
    // CSAppException.apperr(ElementException.CRM_ELEMENT_140, elementId);
    // }
    // if (!"ZZZZ".equals(discnt.getString("EPARCHY_CODE")) &&
    // !userEparchyCode.equals(discnt.getString("EPARCHY_CODE")))
    // {
    // CSAppException.apperr(ElementException.CRM_ELEMENT_140, elementId);
    // }
    // elementName = discnt.getString("DISCNT_NAME");
    // price = discnt.getInt("PRICE");
    // }
    // else if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
    // {
    // IData svc = SvcInfoQry.getServInfo(elementId);
    // if (IDataUtil.isEmpty(svc))
    // {
    // CSAppException.apperr(ElementException.CRM_ELEMENT_122, elementId);
    // }
    // elementName = svc.getString("SERVICE_NAME");
    // price = svc.getInt("PRICE");
    // }
    // else if(BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(elementTypeCode))
    // {
    // IDataset spBizs = PlatSvcInfoQry.querySpBizBySvcId(elementId);
    // if (IDataUtil.isEmpty(spBizs))
    // {
    // CSAppException.apperr(PlatException.CRM_PLAT_34, elementId);
    // }
    // elementName = spBizs.getData(0).getString("SERVICE_NAME");
    // price = spBizs.getData(0).getInt("PRICE") / 10;// 除以10是因为平台业务存的是厘
    // }
    // else if(BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode))
    // {
    // //预留，以后可能有用
    // }
    //            			
    // consumeDetailDesc.append(elementName + "(" + price/100 + "元/月)+");
    // elementConsumeLimit += price;
    // }
    //            		
    //            		
    //            		
    // }
    // }
    // }
    //        
    // if(consumeLimit > elementConsumeLimit)
    // {
    // int virtureConsume = consumeLimit - elementConsumeLimit;//虚拟保底套餐值
    // //如果实际保底值大于元素的保底值之和，则表示用户还订购了虚拟的保底消费套餐
    // consumeDetailDesc.append("保底消费" + (virtureConsume/100) + "元套餐(" + (virtureConsume/100) + "元/月)+");
    // }
    //        
    // if(consumeDetailDesc.length() > 0)
    // {
    // String sumConsumeDesc = consumeDetailDesc.substring(0, consumeDetailDesc.length() - 1);
    // sumConsumeDesc += "=" + (consumeLimit/100) + "元";
    //			
    // brd.setContractConsumeDetailDesc(sumConsumeDesc);
    // }
    //		
    // brd.setDiscount(param.getString("DISCOUNT"));
    // }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-25 上午09:36:15 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-25 chengxf2 v1.0.0 修改原因
     */
    protected IDataset getSelectedElems(IData param, BaseReqData brd, String bookDate) throws Exception
    {
        ContractSaleReqData contractSaleReqData = (ContractSaleReqData) brd;

        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");
        UcaData uca = contractSaleReqData.getUca();
        IDataset selectedElementDataset = new DatasetList();

        IDataset forceAndDefaultElements = PkgElemInfoQry.queryForceDefaultElem(productId, packageId);
        for (int index = 0, size = forceAndDefaultElements.size(); index < size; index++)
        {
            IData forceAndDefaultElement = forceAndDefaultElements.getData(index);
            forceAndDefaultElement.put("CAMPN_TYPE", contractSaleReqData.getCampnType());
            forceAndDefaultElement.put("SERIAL_NUMBER", contractSaleReqData.getUca().getSerialNumber());
            forceAndDefaultElement.put("PRODUCT_ID", contractSaleReqData.getProductId());
            forceAndDefaultElement.put("BASIC_CAL_START_DATE", contractSaleReqData.getStartDate());
            forceAndDefaultElement.put("CUSTOM_ABSOLUTE_START_DATE", bookDate);
            forceAndDefaultElement.put(Route.ROUTE_EPARCHY_CODE, uca.getUserEparchyCode());
            forceAndDefaultElement.put("END_OFFSET", param.getString("MONTHS")); // 用于计算元素的失效时间
            IDataset dataset = CSAppCall.call("CS.SaleActiveElementDateSVC.callElementStartEndDate", forceAndDefaultElement);
            IData data = dataset.getData(0);
            forceAndDefaultElement.put("START_DATE", data.getString("START_DATE"));
            forceAndDefaultElement.put("END_DATE", data.getString("END_DATE"));
            String elementTypeCode = forceAndDefaultElement.getString("ELEMENT_TYPE_CODE");
            if ("A".equals(elementTypeCode))
            {
                buildSaleDepositData(forceAndDefaultElement, contractSaleReqData, param);
            }
            else if ("D".equals(elementTypeCode))
            {
                buildDiscntData(forceAndDefaultElement, contractSaleReqData, param);
            }
            else if ("G".equals(elementTypeCode))
            {
                buildSaleGoodsData(forceAndDefaultElement, contractSaleReqData, param);
            }
            // SaleActiveUtil.filterPackageElementPropertys(forceAndDefaultElement);
            selectedElementDataset.add(forceAndDefaultElement);
        }

        // buildContractDetailDesc(param,contractSaleReqData,uca); //将合约描述信息保存起来

        return selectedElementDataset;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-25 上午09:36:15 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-25 chengxf2 v1.0.0 修改原因
     */
    protected void setCampnInfo(IData param, BaseReqData brd) throws Exception
    {
        ContractSaleReqData contractSaleReqData = (ContractSaleReqData) brd;
        String productId = param.getString("PRODUCT_ID");
        String campnType = param.getString("CAMPN_TYPE");
        String packageId = param.getString("PACKAGE_ID");
        contractSaleReqData.setCampnType(campnType);
        contractSaleReqData.setCampnId(productId);
        contractSaleReqData.setCampnCode(packageId);
        contractSaleReqData.setProductId(productId);
        contractSaleReqData.setPackageId(packageId);
    }
}
