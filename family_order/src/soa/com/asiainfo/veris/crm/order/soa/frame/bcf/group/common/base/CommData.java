
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Obj2Xml;
import com.asiainfo.veris.crm.order.soa.frame.bcf.log.LogBaseBean;

public final class CommData
{
    private IData cd = new DataMap();

    public IDataset getAccount() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.ACCOUNT);
    }

    public IDataset getAskPrintInfo() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.ASK);
    }

    public IDataset getBBossSvc() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.PR_MN_OPERATE_CODE);
    }

    public IDataset getDevelopInfo() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.DEVELOP);
    }

    public IDataset getDiscnt() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.DISCNT);
    }

    public IDataset getElement() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.ELEMENT);
    }

    public IDataset getElementParam() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.ELEMENT_PARAM);
    }

    public IDataset getEosParam() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.EOS);
    }
    
    public IDataset getFeeOtherFee() throws Exception
    {
    	return getMySet(GroupBaseConst.CommDataEntity.FEE_OTHERFEE);
    }

    public IDataset getGrpPackage() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.GRP_PACKAGE);
    }

    public IDataset getManageInfo() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.MANAGE_INFO);
    }

    public IDataset getMerchDiscnt() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.MERCH_DISCNT);
    }

    public IDataset getMerchMember() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.MERCH_MEMBER);
    }

    public IDataset getMerchp() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.MERCH_P);
    }

    public IDataset getMerchpDiscnt() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.MERCH_P_DISCNT);
    }

    private IData getMyMap(GroupBaseConst.CommDataEntity entityName) throws Exception
    {

        IDataset ids = getMySet(entityName);

        IData map = null;

        if (IDataUtil.isEmpty(ids))
        {
            map = new DataMap();
            ids.add(map);
        }
        else
        {
            map = ids.getData(0);
        }

        return map;
    }

    private IDataset getMySet(GroupBaseConst.CommDataEntity entityName)
    {

        String key = entityName.toString();

        IDataset ids = cd.getDataset(key);

        if (IDataUtil.isEmpty(ids))
        {
            ids = new DatasetList();

            cd.put(key, ids);
        }

        return ids;
    }

    public IDataset getOther() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.OTHER);
    }

    public IDataset getPayCompany() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.PAY_COMPANY);
    }

    public IDataset getPayPlan() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.PAYPLAN);
    }

    public IDataset getPayRelation() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.PAYRELATION);
    }

    public IDataset getPost() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.POST);
    }

    public IDataset getPrMnOperateCode() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.BBOSS_SVC);
    }

    public IDataset getProduct() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.PRODUCT);
    }

    /**
     * 获取产品的BBOSS商品信息
     * 
     * @return 产品的BBOSS商品信息
     * @author xiajj
     * @throws Exception
     */
    public IData getProductGoodsInfo(String productId) throws Exception
    {

        IData data = cd.getData("GOODS_INFO");

        if (data == null)
        {
            return null;
        }

        return (IData) data.get(productId);
    }

    public IData getProductIdSet() throws Exception
    {
        return cd.getData(GroupBaseConst.CommDataEntity.PRODUCT_ID_SET.toString());
    }

    /**
     * 获取产品个性化参数
     * 
     * @return 产品个性化参数
     * @author xiajj
     * @throws Exception
     */
    public IDataset getProductParamList(String productId) throws Exception
    {

        IData data = cd.getData("PRODUCT_PARAM");

        if (data == null)
        {
            return new DatasetList();
        }

        return data.getDataset(productId);
    }

    public IData getProductParamMap(String productId) throws Exception
    {
        IData data = cd.getData("PRODUCT_PARAM_MAP");

        if (data == null)
        {
            return null;
        }

        return data.getData(productId);
    }

    /**
     * 获取产品元素
     * 
     * @return 产品元素
     * @author xiajj
     * @throws Exception
     */
    public IDataset getProductsElement(String productId) throws Exception
    {

        IData data = cd.getData("PRODUCTS_ELEMENT");
        if (data == null)
            return null;
        else
            return (IDataset) data.get(productId);
    }

    public IDataset getRes() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.RES);
    }

    public IDataset getSpecialPay() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.SPECIALPAY);
    }

    public IDataset getSpecialSvcParam() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.SPECIAL_SVC_PARAM);
    }

    public IDataset getSpSvc() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.SPSVC);
    }

    public IDataset getSvc() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.SVC);
    }
    
    public IDataset getOfferRel() throws Exception
    {
        return getMySet(GroupBaseConst.CommDataEntity.OFFER_REL);
    }

    public void logCommData(String strLogFile) throws Exception
    {
        Obj2Xml.toFile(LogBaseBean.LOG_PATH, strLogFile, cd);
    }

    public void putAccount(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.ACCOUNT, ids);
    }

    public void putAskPrintInfo(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.ASK, ids);
    }

    public void putBbossSvc(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.BBOSS_SVC, ids);
    }

    public void putDevelopInfo(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.DEVELOP, ids);
    }

    public void putDiscnt(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.DISCNT, ids);
    }

    public void putElementParam(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.ELEMENT_PARAM, ids);
    }

    public void putEosParam(IDataset eos) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.EOS, eos);
    }

    public void putGrpPackage(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.GRP_PACKAGE, ids);
    }

    public void putManageInfo(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.MANAGE_INFO, ids);
    }

    public void putMerchDiscnt(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.MERCH_DISCNT, ids);
    }

    public void putMerchMember(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.MERCH_MEMBER, ids);
    }

    public void putMerchp(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.MERCH_P, ids);
    }

    private void putMySet(GroupBaseConst.CommDataEntity entityName, IDataset ids) throws Exception
    {
        cd.put(entityName.toString(), ids);
    }

    public void putPayCompany(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.PAY_COMPANY, ids);
    }

    public void putPayPlan(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.PAYPLAN, ids);
    }

    public void putPayRelation(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.PAYRELATION, ids);
    }

    public void putPost(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.POST, ids);
    }

    public void putProduct(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.PRODUCT, ids);
    }
    
    public void putOfferRel(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.OFFER_REL, ids);
    }

    public void putProductIdSet(IData id) throws Exception
    {
        cd.put(GroupBaseConst.CommDataEntity.PRODUCT_ID_SET.toString(), id);
    }

    /**
     * 设置产品个性化参数
     * 
     * @param productParam
     *            产品个性化参数
     * @author xiajj
     * @throws Exception
     */
    public void putProductParamList(String productId, IDataset productParam) throws Exception
    {
        IData data = cd.getData("PRODUCT_PARAM");
        if (data == null)
        {
            data = new DataMap();
        }

        cd.put("PRODUCT_PARAM", data);
        data.put(productId, productParam);

        putProductParamMap(productId);
    }

    private void putProductParamMap(String productId) throws Exception
    {
        IDataset paramDataset = getProductParamList(productId);

        IData map = new DataMap();

        for (int i = 0; i < paramDataset.size(); i++)
        {
            IData paramData = paramDataset.getData(i);

            String attrCode = paramData.getString("ATTR_CODE");
            String attrValue = paramData.getString("ATTR_VALUE", "");

            map.put(attrCode, attrValue);
        }

        IData data = cd.getData("PRODUCT_PARAM_MAP");

        if (IDataUtil.isEmpty(data))
        {
            data = new DataMap();
        }

        data.put(productId, map);

        cd.put("PRODUCT_PARAM_MAP", data);
    }

    public void putRes(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.RES, ids);
    }

    public void putSpecialPay(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.SPECIALPAY, ids);
    }

    public void putSpecialSvcParam(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.SPECIAL_SVC_PARAM, ids);
    }

    public void putSpSvc(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.SPSVC, ids);
    }

    /**
     * 设置产品控制参数
     * 
     * @param productCtrlInfo
     *            产品控制参数
     * @author xiajj
     * @throws Exception
     */

    public void putSvc(IDataset ids) throws Exception
    {
        putMySet(GroupBaseConst.CommDataEntity.SVC, ids);
    }
    
    /**
     * 转账费用信息
     * 
     * @param ids
     * @throws Exception
     */
    public void putFeeOtherFee(IDataset ids) throws Exception
    {
    	putMySet(GroupBaseConst.CommDataEntity.FEE_OTHERFEE, ids);
    }

    /**
     * 设置产品的BBOSS商品信息
     * 
     * @param productGoodsInfo
     *            产品的BBOSS商品信息
     * @author xiajj
     * @throws Exception
     */
    public void setProductGoodsInfo(String productId, IData productGoodsInfo) throws Exception
    {

        IData data = cd.getData("GOODS_INFO");
        if (data == null)
        {
            data = new DataMap();
            cd.put("GOODS_INFO", data);
        }
        data.put(productId, productGoodsInfo);
    }

    /**
     * 设置产品元素
     * 
     * @param productsElemet
     *            产品元素
     * @author xiajj
     * @throws Exception
     */
    public void setProductsElement(String productId, IDataset productsElemet) throws Exception
    {

        IData data = cd.getData("PRODUCTS_ELEMENT");
        if (data == null)
        {
            data = new DataMap();
            cd.put("PRODUCTS_ELEMENT", data);
        }
        data.put(productId, productsElemet);
    }
}
