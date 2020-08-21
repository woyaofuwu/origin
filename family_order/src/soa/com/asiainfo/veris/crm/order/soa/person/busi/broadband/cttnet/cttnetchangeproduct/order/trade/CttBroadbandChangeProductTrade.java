
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangeproduct.order.trade;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.trade.ChangeProductTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangeproduct.order.requestdata.CttBroadbandChangeProductReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;

public class CttBroadbandChangeProductTrade extends ChangeProductTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        genChangeProductTradeData(btd);
        // super.createBusiTradeData(btd);
        geneSvcStateTradeDatas(btd);
        // getTradeProductData(btd);
        // getTradeUserData(btd);
        // getTradeUserPorductData(btd);

        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setNetTypeCode(CttConstants.NET_TYPE_CODE);
        mainTradeData.setSubscribeType(CttConstants.SUBSCRIBE_TYPE);
        mainTradeData.setPfType(CttConstants.PF_TYPE);
        mainTradeData.setOlcomTag(CttConstants.OLCOM_TAG);
        String strPTS = "00000000000000000000";
        CttBroadbandChangeProductReqData reqData = (CttBroadbandChangeProductReqData) btd.getRD();
        if (reqData.isChangeRateFlag())
        {
            strPTS = strPTS.substring(0, 3) + "1" + strPTS.substring(4);
            mainTradeData.setProcessTagSet(strPTS);
        }

    }

    public void createProductTrade(String newProductId, String oldProductId, String productChangeDate, String oldProductEndDate, BaseChangeProductReqData request, BusiTradeData btd) throws Exception
    {
        UcaData uca = request.getUca();

        // 新主产品新增台帐
        ProductTradeData newProductTradeData = new ProductTradeData();
        newProductTradeData.setProductId(newProductId);
        newProductTradeData.setBrandCode(request.getNewMainProduct().getBrandCode());
        newProductTradeData.setProductMode(request.getNewMainProduct().getProductMode());
        newProductTradeData.setStartDate(productChangeDate);
        newProductTradeData.setEndDate(SysDateMgr.getTheLastTime());
        newProductTradeData.setInstId(SeqMgr.getInstId());
        newProductTradeData.setUserId(uca.getUserId());
        newProductTradeData.setUserIdA("-1");
        newProductTradeData.setMainTag("1");
        newProductTradeData.setOldProductId(uca.getProductId());
        newProductTradeData.setOldBrandCode(uca.getBrandCode());
        newProductTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        // 老主产品结束台帐
        ProductTradeData oldProduct = uca.getUserProduct(oldProductId).get(0);
        ProductTradeData oldProductTrade = oldProduct.clone();
        oldProductTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
        oldProductTrade.setEndDate(oldProductEndDate);

        btd.add(uca.getSerialNumber(), newProductTradeData);
        btd.add(uca.getSerialNumber(), oldProductTrade);
    }

    // private void getTradeProductData(BusiTradeData btd) throws Exception
    // {
    // CttBroadbandChangeProductReqData reqData = (CttBroadbandChangeProductReqData) btd.getRD();
    // BaseChangeProductReqData changeproductReqData = (BaseChangeProductReqData) btd.getRD();
    // UcaData ucaInfo = reqData.getUca();
    // List<ProductTradeData> productList = ucaInfo.getUserProducts();
    // ProductTradeData productTradeData = null;
    // for (int i = 0; i < productList.size(); i++) {
    // productTradeData = productList.get(i);
    // if(StringUtils.equals("-1", productTradeData.getUserIdA())){
    // productTradeData.setOldProductId(productTradeData.getProductId());
    // productTradeData.setProductId(changeproductReqData.getNewMainProduct().getProductId());
    // productTradeData.setProductMode("00");
    // productTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
    // productTradeData.setBrandCode(changeproductReqData.getNewMainProduct().getBrandCode());
    // productTradeData.setStartDate(reqData.getAcceptTime());
    // productTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
    // productTradeData.setRsrvStr1("");////产品规格
    //                
    // btd.add(ucaInfo.getSerialNumber(), productTradeData);
    // }
    //        
    // }
    // }

    // private void getTradeUserPorductData(BusiTradeData btd) throws Exception
    // {
    // CttBroadbandChangeProductReqData reqData = (CttBroadbandChangeProductReqData) btd.getRD();
    // BaseChangeProductReqData changeproductReqData = (BaseChangeProductReqData) btd.getRD();
    // UcaData ucaInfo = reqData.getUca();
    // ProductTradeData productTradeData = ucaInfo.getUserMainProduct();
    // productTradeData.setProductId(changeproductReqData.getNewMainProduct().getProductId());
    // productTradeData.setBrandCode(changeproductReqData.getNewMainProduct().getBrandCode());
    // productTradeData.setMainTag(BofConst.MODIFY_TAG_UPD);
    // productTradeData.setRemark("产品变更修改");
    // btd.add(ucaInfo.getSerialNumber(), productTradeData);
    // }

    public void genChangeProductTradeData(BusiTradeData btd) throws Exception
    {
        BaseChangeProductReqData request = (BaseChangeProductReqData) btd.getRD();
        UcaData uca = request.getUca();
        String productChangeDate = null;
        String oldProductEndDate = null;
        boolean isProductChange = false;
        // 取得用户选择开通关闭或者修改的元素
        List<ProductModuleData> userSelected = request.getProductElements();
        if (userSelected == null)
        {
            userSelected = new ArrayList<ProductModuleData>();
        }
        List<ProductModuleData> operElements = new ArrayList<ProductModuleData>();
        operElements.addAll(userSelected);

        btd.getMainTradeData().setRsrvStr1(uca.getBrandCode());
        btd.getMainTradeData().setRsrvStr2(uca.getProductId());

        if (request.getNewMainProduct() != null && !uca.getProductId().equals(request.getNewMainProduct().getProductId()))
        {
            if (uca.getUserNextMainProduct() != null)
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_195);
            }
            // 主产品变更
            isProductChange = true;
            btd.getMainTradeData().setProductId(request.getNewMainProduct().getProductId());
            btd.getMainTradeData().setBrandCode(request.getNewMainProduct().getBrandCode());
            // 计算新的主产品的生效时间和老主产品的失效时间
            String newProductId = request.getNewMainProduct().getProductId();
            String oldProductId = request.getUca().getProductId();

            // productChangeDate = this.getProductChangeDate(oldProductId, newProductId, request);
            // 获得老主产品的结束时间
            List<ProductTradeData> oldUserProducts = request.getUca().getUserProducts();
            for (int i = 0; i < oldUserProducts.size(); i++)
            {
                ProductTradeData oldUserPorudcts = oldUserProducts.get(i);
                if ("1".equals(oldUserPorudcts.getMainTag()))
                {
                    productChangeDate = oldUserPorudcts.getStartDate();
                    oldProductEndDate = SysDateMgr.getLastSecond(SysDateMgr.getFirstDayOfThisMonth4WEB());//oldUserPorudcts.getEndDate();
                }
            }
            // oldProductEndDate = SysDateMgr.getLastSecond(productChangeDate);
            // 生成主产品的相关台帐
            this.createProductTrade(newProductId, oldProductId, productChangeDate, oldProductEndDate, request, btd);
            List<ProductTradeData> userProducts = uca.getUserProducts();
            // 拼装需要继承的元素
            IDataset newProductElements = ProductInfoQry.getProductElements(request.getNewMainProduct().getProductId(), uca.getUserEparchyCode());
            // 针对元素修改时的处理，需要转换产品ID和包ID
            int selectSize = userSelected.size();
            for (int i = 0; i < selectSize; i++)
            {
                ProductModuleData pmd = userSelected.get(i);
                if (BofConst.MODIFY_TAG_UPD.equals(pmd.getModifyTag()))
                {
                    IData transElement = this.getTransElement(newProductElements, pmd.getElementId(), pmd.getElementType());
                    if (transElement != null)
                    {
                        if (!transElement.getString("PRODUCT_ID", "").equals(pmd.getProductId()) || !transElement.getString("PACKAGE_ID", "").equals(pmd.getPackageId()))
                        {
                            pmd.setProductId(transElement.getString("PRODUCT_ID"));
                            pmd.setPackageId(transElement.getString("PACKAGE_ID"));
                        }
                    }
                }
            }

            // 转换主产品时，处理必选元素
            IDataset forceElements = this.getProductForceElement(newProductElements);
            if (IDataUtil.isNotEmpty(forceElements))
            {
                int forceSize = forceElements.size();
                for (int i = 0; i < forceSize; i++)
                {
                    IData forceElement = forceElements.getData(i);
                    if (!this.isExistInUserSelected(forceElement.getString("ELEMENT_TYPE_CODE"), forceElement.getString("ELEMENT_ID"), null, userSelected))
                    {
                        // 不在用户的选择列表里面
                        IDataset attrs = AttrItemInfoQry.getelementItemaByPk(forceElement.getString("ELEMENT_ID"), forceElement.getString("ELEMENT_TYPE_CODE"), uca.getUserEparchyCode(), null);
                        List<AttrData> attrDatas = new ArrayList<AttrData>();
                        if (IDataUtil.isNotEmpty(attrs))
                        {
                            int length = attrs.size();
                            for (int j = 0; j < length; j++)
                            {
                                IData attr = attrs.getData(j);
                                AttrData attrData = new AttrData();
                                attrData.setAttrCode(attr.getString("ATTR_CODE"));
                                attrData.setAttrValue(attr.getString("ATTR_INIT_VALUE"));
                                attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                                attrDatas.add(attrData);
                            }
                        }
                        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(forceElement.getString("ELEMENT_TYPE_CODE")))
                        {
                            if (uca.getUserSvcBySvcId(forceElement.getString("ELEMENT_ID")).size() > 0)
                            {
                                continue;
                            }

                            SvcData svcData = new SvcData(forceElement);
                            svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                            if (attrDatas.size() > 0)
                            {
                                svcData.setAttrs(attrDatas);
                            }
                            operElements.add(svcData);
                        }
                        else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(forceElement.getString("ELEMENT_TYPE_CODE")))
                        {
                            if (uca.getUserDiscntByDiscntId(forceElement.getString("ELEMENT_ID")).size() > 0)
                            {
                                continue;
                            }
                            DiscntData discntData = new DiscntData(forceElement);
                            discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                            if (attrDatas.size() > 0)
                            {
                                discntData.setAttrs(attrDatas);
                            }
                            operElements.add(discntData);
                        }
                    }
                }
            }
        }
        else
        {
            // 仅元素变更时，需要检查用户是否存在预约产品，如果存在预约产品的话，用户操作的元素如果在当前产品下没有，则要检查用户在预约产品下是否存在，如果存在，则根据预约产品的配置计算生失效时间
            ProductTradeData nextProduct = uca.getUserNextMainProduct();
            String sysDate = request.getAcceptTime();
            if (nextProduct != null)
            {
                IDataset oldProductElements = ProductInfoQry.getProductElements(uca.getProductId(), uca.getUserEparchyCode());
                int size = operElements.size();
                for (int i = 0; i < size; i++)
                {
                    ProductModuleData pmd = operElements.get(i);
                    IData elementConfig = this.getTransElement(oldProductElements, pmd.getElementId(), pmd.getElementType());
                    if (elementConfig == null)
                    {
                        // 在老产品下不能选择
                        if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()))
                        {
                            pmd.setStartDate(nextProduct.getStartDate());
                        }
                        else if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()) && pmd.getStartDate() != null && pmd.getStartDate().compareTo(sysDate) > 0)
                        {
                            pmd.setCancelAbsoluteDate(SysDateMgr.getLastSecond(pmd.getStartDate()));
                        }
                    }
                    else
                    {
                        pmd.setPkgElementConfig(elementConfig.getString("PACKAGE_ID"));
                    }
                }
            }
        }
        ProductTimeEnv env = new ProductTimeEnv();
        if (isProductChange)
        {
            env.setBasicAbsoluteStartDate(productChangeDate);
            String productChangeDate1 = SysDateMgr.getFirstDayOfThisMonth4WEB();

            String oldProductEndDate1 = SysDateMgr.getLastSecond(productChangeDate1);
            env.setBasicAbsoluteCancelDate(oldProductEndDate1);
        }
        env.setNoResetStartDate(true);
        // 元素拼串处理
        ProductModuleCreator.createProductModuleTradeData(operElements, btd, env);

    }

    private void geneSvcStateTradeDatas(BusiTradeData btd) throws Exception
    {
        CttBroadbandChangeProductReqData reqData = (CttBroadbandChangeProductReqData) btd.getRD();
        BaseChangeProductReqData changeproductReqData = (BaseChangeProductReqData) btd.getRD();
        UcaData ucaInfo = reqData.getUca();
        IDataset stateparams = TradeSvcStateInfoQry.querySvcStateParamByKey(CttConstants.CTT_BROADBAND_CHANGEOFFER, ucaInfo.getBrandCode(), ucaInfo.getProductId(), CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(stateparams))
        {
            for (int i = 0; i < stateparams.size(); i++)
            {
                IData stateparam = stateparams.getData(i);
                IDataset userSvcState = UserSvcStateInfoQry.getUserSvcStateBySvcId(ucaInfo.getUserId(), stateparam.getString("SERVICE_ID"), stateparam.getString("OLD_STATE_CODE"));
                if (IDataUtil.isNotEmpty(userSvcState))
                {
                    SvcStateTradeData delData = new SvcStateTradeData(userSvcState.getData(0));
                    delData.setServiceId(stateparam.getString("SERVICE_ID"));
                    delData.setEndDate(reqData.getAcceptTime());
                    delData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(ucaInfo.getSerialNumber(), delData);

                    SvcStateTradeData addData = new SvcStateTradeData();
                    addData.setUserId(ucaInfo.getUserId());
                    addData.setServiceId(stateparam.getString("SERVICE_ID"));
                    addData.setMainTag(userSvcState.getData(0).getString("MAIN_TAG"));
                    addData.setStateCode(stateparam.getString("NEW_STATE_CODE"));
                    addData.setInstId(SeqMgr.getInstId());
                    addData.setStartDate(reqData.getAcceptTime());
                    addData.setEndDate(SysDateMgr.END_TIME_FOREVER);
                    addData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    btd.add(ucaInfo.getSerialNumber(), addData);
                }
            }
        }
    }

    public String getProductChangeDate(String oldProductId, String newProductId, BaseChangeProductReqData request) throws Exception
    {
        String productChangeDate = null;
//        IDataset productTrans = ProductInfoQry.getProductTransInfo(oldProductId, newProductId);
        
        IDataset productTrans = UpcCall.queryJoinEnableModeBy2OfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, oldProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, newProductId);
        if (IDataUtil.isNotEmpty(productTrans))
        {
            IData productTran = productTrans.getData(0);
            String enableTag = productTran.getString("ENABLE_MODE");

            if (enableTag.equals("0"))
            {// 立即生效
                productChangeDate = request.getAcceptTime();
            }
            else if ((enableTag.equals("1")) || (enableTag.equals("2")))
            {// 下帐期生效
                productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
            }
            else if (enableTag.equals("3"))
            {// 按原产品的生效方式
                ProductData oldProductData = new ProductData(request.getUca().getProductId());
                String enableTagOld = oldProductData.getEnableTag();

                if ((enableTagOld.equals("0")) || (enableTagOld.equals("2")))
                {// 立即生效
                    productChangeDate = request.getAcceptTime();
                }
                else if (enableTagOld.equals("1"))
                {// 下帐期生效
                    productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
                }
            }
            else if (enableTag.equals("4"))
            {// 按新产品的生效方式
                String enableTagNew = request.getNewMainProduct().getEnableTag();

                if ((enableTagNew.equals("0")) || (enableTagNew.equals("2")))
                {// 立即生效
                    productChangeDate = request.getAcceptTime();
                }
                else if (enableTagNew.equals("1"))
                {// 下帐期生效
                    productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
                }
            }
        }
        else
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_4);
        }
        return productChangeDate;
    }

    private IDataset getProductForceElement(IDataset productElements) throws Exception
    {
        int size = productElements.size();
        IDataset result = new DatasetList();
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            if ("1".equals(element.getString("PACKAGE_FORCE_TAG")) && "1".equals(element.getString("ELEMENT_FORCE_TAG")))
            {
                result.add(element);
            }
        }
        return result;
    }

    public IData getTransElement(IDataset productElements, String elementId, String elementType)
    {
        if (productElements == null || productElements.size() <= 0)
        {
            return null;
        }
        int size = productElements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            if (element.getString("ELEMENT_ID").equals(elementId) && element.getString("ELEMENT_TYPE_CODE").equals(elementType))
            {
                return element;
            }
        }
        return null;
    }

    private boolean isExistInUserSelected(String elementTypeCode, String elementId, String instId, List<ProductModuleData> userSelected)
    {
        if (userSelected == null || userSelected.size() <= 0)
        {
            return false;
        }
        else
        {
            int size = userSelected.size();
            for (int i = 0; i < size; i++)
            {
                ProductModuleData selected = userSelected.get(i);
                if (selected.getElementType().equals(elementTypeCode) && selected.getElementId().equals(elementId))
                {
                    if (!StringUtils.isBlank(selected.getInstId()))
                    {
                        if (selected.getInstId().equals(instId))
                        {
                            return true;
                        }
                        else
                        {
                            continue;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private boolean isNeedDeal(List<ProductTradeData> userProducts, String elementProductId) throws Exception
    {
        int size = userProducts.size();
        for (int i = 0; i < size; i++)
        {
            ProductTradeData ptd = userProducts.get(i);
            if (ptd.getProductId().equals(elementProductId) && ("00".equals(ptd.getProductMode()) || "01".equals(ptd.getProductMode())))
            {
                return true;
            }
        }
        return false;
    }

}
