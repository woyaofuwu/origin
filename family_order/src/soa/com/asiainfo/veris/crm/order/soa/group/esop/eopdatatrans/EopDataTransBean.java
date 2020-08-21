package com.asiainfo.veris.crm.order.soa.group.esop.eopdatatrans;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformDisBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSvcBean;

public class EopDataTransBean
{

    public static IData eopDataTransOfferData(String ibsysid, String subIbsysid, String recordNum) throws Exception
    {
        IData offerData = buildWorkformProduct(ibsysid, subIbsysid, recordNum);
        if(IDataUtil.isNotEmpty(offerData))
        {
            IDataset subOfferList = offerData.getDataset("SUBOFFERS", new DatasetList());
            if(IDataUtil.isNotEmpty(subOfferList))
            {//先处理子产品，减少循环次数
                for(int i = 0, size = subOfferList.size(); i < size; i++)
                {
                    IData subOffer = subOfferList.getData(i);
                    IDataset subSubOfferList = subOffer.getDataset("SUBOFFERS", new DatasetList());
                    
                    //处理子产品服务
                    IDataset subSvcDataset = buildWorkformSvc(subIbsysid, subOffer.getString("RECORD_NUM"));
                    if(IDataUtil.isNotEmpty(subSvcDataset))
                    {
                        subSubOfferList.addAll(subSvcDataset);
                    }
                    //处理子产品资费
                    IDataset subDisDataset = buildWorkformDis(subIbsysid, subOffer.getString("RECORD_NUM"));
                    if(IDataUtil.isNotEmpty(subDisDataset))
                    {
                        subSubOfferList.addAll(subDisDataset);
                    }
                }
            }
            
            //处理主产品服务
            IDataset svcDataset = buildWorkformSvc(subIbsysid, offerData.getString("RECORD_NUM"));
            if(IDataUtil.isNotEmpty(svcDataset))
            {
                subOfferList.addAll(svcDataset);
            }
            //处理主产品资费
            IDataset disDataset = buildWorkformDis(subIbsysid, offerData.getString("RECORD_NUM"));
            if(IDataUtil.isNotEmpty(disDataset))
            {
                subOfferList.addAll(disDataset);
            }
        }
        
        return offerData;
    }
    
    public static IData buildWorkformProduct(String ibsysid, String subIbsysid, String recordNum) throws Exception
    {
        IData product = new DataMap();
        if(StringUtils.isBlank(recordNum) || "0".equals(recordNum))
        {//查所有产品
            IDataset productList = WorkformProductBean.qryProductByIbsysid(ibsysid);
            if(IDataUtil.isEmpty(productList))
            {
                return product;
            }
            IData mainProduct = null;
            IDataset subProductList = new DatasetList();
            for(int i = 0, size = productList.size(); i < size; i++)
            {
                if("0".equals(productList.getData(i).getString("RECORD_NUM")))
                {//主产品
                    mainProduct = buildProductData(productList.getData(i));
                }
                else
                {
                    subProductList.add(buildProductData(productList.getData(i)));
                }
            }
            if(IDataUtil.isNotEmpty(mainProduct))
            {
                IDataset productAttrList = buildWorkformAttr(subIbsysid, "0");//主商品RECORD_CODE是0
                if(IDataUtil.isNotEmpty(productAttrList))
                {
                    mainProduct.put("OFFER_CHA", productAttrList);
                }
                if(IDataUtil.isNotEmpty(subProductList))
                {
                    for(int i = 0, size = subProductList.size(); i < size; i++)
                    {
                        IData subProduct = subProductList.getData(i);
                        IDataset subProdAttrList = buildWorkformAttr(subIbsysid, subProduct.getString("RECORD_NUM"));
                        if(IDataUtil.isNotEmpty(subProdAttrList))
                        {
                            subProduct.put("OFFER_CHA", subProdAttrList);
                        }
                    }
                    mainProduct.put("SUBOFFERS", subProductList);
                }
            }
        }
        else
        {//查指定产品
            IData workformProduct = WorkformProductBean.qryProductByPk(ibsysid, recordNum);
            if(IDataUtil.isNotEmpty(workformProduct))
            {
                product = buildProductData(workformProduct);
                IDataset productAttrList = buildWorkformAttr(subIbsysid, recordNum);
                if(IDataUtil.isNotEmpty(productAttrList))
                {
                    product.put("OFFER_CHA", productAttrList);
                }
            }
        }
        
        return product;
    }
    
    public static IDataset buildWorkformSvc(String subIbsysid, String recordNum) throws Exception
    {
        IDataset svcList = new DatasetList();
        IDataset workformSvcList = WorkformSvcBean.qrySvcBySubIbsysidAndRecordNum(subIbsysid, recordNum);
        if(IDataUtil.isNotEmpty(workformSvcList))
        {
            svcList = buildElementList(workformSvcList, UpcConst.ELEMENT_TYPE_CODE_SVC);
        }
        return svcList;
    }
    
    public static IDataset buildWorkformDis(String subIbsysid, String recordNum) throws Exception
    {
        IDataset disList = new DatasetList();
        IDataset workformDisList = WorkformDisBean.qryDisBySubIbsysidAndRecordNum(subIbsysid, recordNum);
        if(IDataUtil.isNotEmpty(workformDisList))
        {
            disList = buildElementList(workformDisList, UpcConst.ELEMENT_TYPE_CODE_DISCNT);
        }
        return disList;
    }
    
    private static IDataset buildElementList(IDataset workformEleList, String offerType) throws Exception
    {
        IDataset eleList = new DatasetList();
        
        String indexName = "SVC_INDEX";
        if(UpcConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerType))
        {
            indexName = "DIS_INDEX";
        }
        
        IDataset eleBakList = new DatasetList();//存放服务
        IDataset eleAttrBakList = new DatasetList();//存放服务属性
        Set<String> groupSeqSet = new HashSet<String>();//GROUP_SEQ标识同一组，即归属同一个服务
        for(int i = 0, size = workformEleList.size(); i < size; i++)
        {
            IData workformEle = workformEleList.getData(i);
            if("-1".equals(workformEle.getString("PARENT_ATTR_CODE")))
            {
                eleBakList.add(workformEle);
                groupSeqSet.add(workformEle.getString("GROUP_SEQ"));
            }
            else
            {
                eleAttrBakList.add(workformEle);
            }
        }
        if(IDataUtil.isNotEmpty(eleBakList))
        {
            Iterator<String> itr = groupSeqSet.iterator();
            while(itr.hasNext())
            {
                String groupSeq = itr.next();
                IData element = new DataMap();
                for(int i = 0, size = eleBakList.size(); i < size; i++)
                {
                    IData eleBak = eleBakList.getData(i);
                    if(groupSeq.equals(eleBak.getString("GROUP_SEQ")) && !"OFFER_CHA".equals(eleBak.getString("ATTR_NAME")))
                    {//OFFER_CHA后续处理
                        element.put(eleBak.getString("ATTR_NAME"), eleBak.getString("ATTR_VALUE"));
                        element.put(indexName, eleBak.getString("ATTR_CODE"));
                    }
                }
                if(IDataUtil.isNotEmpty(element))
                {
                    if(IDataUtil.isNotEmpty(eleAttrBakList))
                    {
                        IDataset eleAttrList = new DatasetList();
                        String parentAttrCode = element.getString(indexName);
                        for(int i = 0, size = eleAttrBakList.size(); i < size; i++)
                        {
                            IData eleAttrBak = eleAttrBakList.getData(i);
                            if(parentAttrCode.equals(eleAttrBak.getString("PARENT_ATTR_CODE")))
                            {
                                IData eleAttr = new DataMap();
                                eleAttr.put("CHA_SPEC_CODE", eleAttrBak.getString("ATTR_NAME"));
                                eleAttr.put("CHA_VALUE", eleAttrBak.getString("ATTR_VALUE"));
                                eleAttr.put("CHA_SPEC_ID", eleAttrBak.getString("RSRV_STR1"));
                                eleAttrList.add(eleAttr);
                            }
                        }
                        if(IDataUtil.isNotEmpty(eleAttrList))
                        {
                            element.put("OFFER_CHA", eleAttrList);
                        }
                    }
                    eleList.add(element);
                }
            }
        }
        return eleList;
    }
    
    private static IDataset buildWorkformAttr(String subIbsysid, String recordNum) throws Exception
    {
        IDataset attrList = new DatasetList();
        IDataset workformAttrList = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysid, recordNum);
        if(IDataUtil.isNotEmpty(workformAttrList))
        {
            for(int i = 0, size = workformAttrList.size(); i < size; i++)
            {
                attrList.add(buildAttrData(workformAttrList.getData(i)));
            }
        }
        return attrList;
    }
    
    private static IData buildProductData(IData workformProduct) throws Exception
    {
        IData productData = new DataMap();
        productData.put("OFFER_ID", workformProduct.getString("RSRV_STR1"));
        productData.put("OFFER_NAME", workformProduct.getString("PRODUCT_NAME"));
        productData.put("OFFER_CODE", workformProduct.getString("PRODUCT_ID"));
        productData.put("OFFER_TYPE", UpcConst.ELEMENT_TYPE_CODE_PRODUCT);
        productData.put("RECORD_NUM", workformProduct.getString("RECORD_NUM"));
        
        return productData;
    }
    
    private static IData buildAttrData(IData workformAttr) throws Exception
    {
        IData attrData = new DataMap();
        attrData.put("CHA_SPEC_CODE", workformAttr.getString("ATTR_CODE"));
        attrData.put("CHA_VALUE", workformAttr.getString("ATTR_VALUE"));
        return attrData;
    }
}
