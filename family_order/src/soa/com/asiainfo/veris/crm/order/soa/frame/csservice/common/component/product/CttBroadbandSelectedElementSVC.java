
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;

public class CttBroadbandSelectedElementSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IDataset dealSelectedElementAttrs(IData element, String eparchyCode) throws Exception
    {

        IDataset attrs = new DatasetList();
        IDataset attrItemAList = AttrItemInfoQry.getElementItemA(element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), eparchyCode);
        // ADC、MAS弹窗

        if (IDataUtil.isNotEmpty(attrItemAList) && element.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
        {
            for (int i = 0; i < attrItemAList.size(); i++)
            {
                IData attrItemAMap = attrItemAList.getData(i);
                String attrtypecode = attrItemAMap.getString("ATTR_TYPE_CODE", "0");
                if (attrtypecode.equals("9"))
                {
                    element.put("ATTR_PARAM_TYPE", "9");
                    IData userattr = new DataMap();
                    userattr.put("PARAM_VERIFY_SUCC", "false");
                    attrs.add(userattr);
                    return attrs;
                }
            }
        }

        attrs = this.makeAttrs(null, attrItemAList);
        return attrs;
    }

    public IDataset dealSelectedElementsForChg(IData data) throws Exception
    {
        // 对于元素的处理，如果用户有下月生效的产品，则产品组件展示的是下月生效的产品包元素，如果元素在用户的原产品下有，则以原产品的生效方式为准
        // 如果元素在用户的原产品下没有，则以新产品的生效方式为准
        String eparchyCode = data.getString("EPARCHY_CODE");
        this.setRouteId(eparchyCode);
        String userProductId = data.getString("USER_PRODUCT_ID");
        String nextProductId = data.getString("NEXT_PRODUCT_ID");

        int discntCount = 0;
        int svcCount = 0;
        int discntFee = 0;

        // 费用处理相关数据
        String oldEndoffset = "";
        String oldEndunit = "";
        String iEndOffset = "";
        String iEndUnit = "";
        String iRsrvTag1 = "0";

        String sysDate = SysDateMgr.getSysTime();
        IDataset elements = new DatasetList(data.getString("ELEMENTS"));
        IDataset selectElements = new DatasetList(data.getString("SELECTED_ELEMENTS"));
        String oldStartDate = sysDate;
        int oldDiscntFee = 0;
        IDataset discntFeeSet = new DatasetList();

        // selectElements中存放的是在已选区中的所有元素，产品变更是删除用户原来所有的基础资费，变更新的资费信息。
        // 即需要判断在已选区中的新增基础资费和服务元素只有一个
        for (int i = 0; i < selectElements.size(); i++)
        {
            IData selecElement = selectElements.getData(i);
            if (selecElement.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_DISCNT) && StringUtils.equals("1", selecElement.getString("MODIFY_TAG", "1")))
            {
                oldStartDate = selecElement.getString("START_DATE", sysDate);

                // 计算用户原来的资费的金额
                IData param = new DataMap();
                param.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
                param.put("EPARCHY_CODE", eparchyCode);
                param.put("PRODUCT_ID", "-1");
                param.put("PACKAGE_ID", "-1");
                param.put("ELEMENT_TYPE_CODE", "D");
                param.put("TRADE_FEE_TYPE", "3");
                param.put("ELEMENT_ID", selecElement.getString("ELEMENT_ID"));
                IDataset oldDiscntFeeSet = ProductFeeInfoQry.getElementFeeBySql(param);
                if (IDataUtil.isNotEmpty(oldDiscntFeeSet))
                {
                    String oldFee = oldDiscntFeeSet.getData(0).getString("FEE");
                    oldDiscntFee = oldDiscntFee + Integer.parseInt(oldFee);
                }

                // 获得失效偏移值
                IData elementEnableMode = UPackageElementInfoQry.queryElementEnableMode(selecElement.getString("PRODUCT_ID"), selecElement.getString("PACKAGE_ID","-1"), selecElement.getString("ELEMENT_ID"), "D");
                
                if (IDataUtil.isNotEmpty(elementEnableMode))
                {
                    oldEndoffset = elementEnableMode.getString("END_OFFSET");
                    oldEndunit = elementEnableMode.getString("END_UNIT");
                }
            }
            else if (selecElement.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC)
                    && (StringUtils.equals("0", selecElement.getString("MODIFY_TAG", "0")) || StringUtils.equals("exist", selecElement.getString("MODIFY_TAG", "0"))) && !StringUtils.equals("501", selecElement.getString("ELEMENT_ID")))
            {
                svcCount++;
            }
            else if (selecElement.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_DISCNT)
                    && (StringUtils.equals("0", selecElement.getString("MODIFY_TAG", "0"))))
            {
                discntCount++;
            }
        }

        for (int i = 0; i < elements.size(); i++)
        {
            IData element = elements.getData(i);
            ProductTimeEnv env = new ProductTimeEnv();
            ProductModuleData pmd = null;
            if (element.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
            {
                pmd = new SvcData(element);
                if(!StringUtils.equals(pmd.getElementId(), "501")){
                	svcCount++;
                }
            }
            else
            {
                pmd = new DiscntData(element);
                discntCount++;
            }
            if (discntCount > 1)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "新增的优惠信息只能有一条！");
            }
            if (svcCount > 1 )
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "服务信息只能有一条！");
            }

            if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()))
            {

                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(pmd.getElementType()))
                {
                    String endDate = ProductModuleCalDate.calEndDate(pmd, oldStartDate);
                    element.put("START_DATE", oldStartDate);
                    element.put("END_DATE", endDate);

                    IData param = new DataMap();
                    param.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
                    param.put("EPARCHY_CODE", eparchyCode);
                    param.put("PRODUCT_ID", "-1");
                    param.put("PACKAGE_ID", "-1");
                    param.put("ELEMENT_TYPE_CODE", "D");
                    param.put("TRADE_FEE_TYPE", "3");
                    param.put("ELEMENT_ID", pmd.getElementId());
                    discntFeeSet = ProductFeeInfoQry.getElementFeeBySql(param);
                    if (IDataUtil.isNotEmpty(discntFeeSet))
                    {
                        String fee = discntFeeSet.getData(0).getString("FEE");
                        discntFee = discntFee + Integer.parseInt(fee);
                    }

                    if (StringUtils.isEmpty(userProductId) && StringUtils.isEmpty(nextProductId))
                    {
                        userProductId = pmd.getProductId();
                    }

                    // 获得失效偏移值
//                    IDataset packageElementInfos = PkgElemInfoQry.getServElementByPk(pmd.getPackageId(), "D", pmd.getElementId());
                    
                    // 获得失效偏移值
//                    IDataset packageElementInfos = UpcCall.queryOfferEnableModeByCond(BofConst.ELEMENT_TYPE_CODE_DISCNT, pmd.getElementId());
                    
                    IDataset relOfferExtChas = UpcCall.qryRelOfferExtChaByOfferIdRelOfferId(pmd.getProductId(), BofConst.ELEMENT_TYPE_CODE_PRODUCT, pmd.getElementId(), BofConst.ELEMENT_TYPE_CODE_DISCNT);
                    
//                    if (IDataUtil.isNotEmpty(packageElementInfos))
//                    {
//                        IData packageElementInfo = packageElementInfos.getData(0);
                    iEndOffset = pmd.getEndOffSet();
                    iEndUnit = pmd.getEndUnit();
                    
                    if (IDataUtil.isNotEmpty(relOfferExtChas))
                    {
                        iRsrvTag1 = relOfferExtChas.getData(0).getString("RSRV_TAG1", "0");
                    }
//                    }
                }
                else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(pmd.getElementType()))
                {
                    String endDate = ProductModuleCalDate.calEndDate(pmd, oldStartDate);
                    element.put("START_DATE", oldStartDate);
                    element.put("END_DATE", endDate);
                }
                else if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()) && BofConst.ELEMENT_TYPE_CODE_SVC.equals(pmd.getElementType()))
                {
                    element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
                    element.put("EFFECT_NOW_END_DATE", sysDate);
                    pmd.setEndDate(null);
                    element.put("END_DATE", sysDate);
                }
                IDataset attrs = dealSelectedElementAttrs(element, eparchyCode);

                if (attrs != null && attrs.size() > 0)
                {
                    element.put("ATTR_PARAM", attrs);
                }
                if ("3".equals(pmd.getEnableTag()) && StringUtils.isBlank(env.getBasicAbsoluteStartDate()))
                {
                    element.put("CHOICE_START_DATE", "true");
                }
                if ("2".equals(pmd.getEndEnableTag()) || StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "SYS_CRM_DISCNTDATECHG"))
                {
                    element.put("SELF_END_DATE", "false");// 自选时间
                }
            }

        }

        if (IDataUtil.isNotEmpty(discntFeeSet))
        {
            if ("9725".equals(data.getString("TRADE_TYPE_CODE")))
            {
                int size = elements.size();
                for (int i = 0; i < size; i++)
                {
                    IData element = elements.getData(i);
                    if ("0".equals(element.getString("MODIFY_TAG")) && "D".equals(element.getString("ELEMENT_TYPE_CODE")))
                    {
                        IDataset feeDatas = new DatasetList();

                        String selectedPId = "";
                        if (StringUtils.isEmpty(nextProductId))
                        {
                            selectedPId = userProductId;
                        }
                        else
                        {
                            selectedPId = nextProductId;
                        }
                        String tempfee = "0";
                        // //只有当用户的老优惠不是短套餐时,产品变更为短套餐才收取相应的安装费用
                        if ((Integer.parseInt(oldEndunit) == 2 || Integer.parseInt(oldEndunit) == 3) && Integer.parseInt(oldEndoffset) > 3)
                        {
                            if ((Integer.parseInt(iEndUnit) == 2 || Integer.parseInt(iEndUnit) == 3) && Integer.parseInt(iEndOffset) <= 3)
                            {
                                if (Integer.parseInt(iRsrvTag1) == 1)
                                {
                                    tempfee = "8000";
                                }
                                else
                                {
                                    if (selectedPId == "50001002")
                                    {// 商业
                                        tempfee = "5000";
                                    }
                                    else
                                    {
                                        tempfee = "2000";
                                    }
                                }
                            }
                        }
                        int oldFee = oldDiscntFee - Integer.parseInt(tempfee);
                        int fee = discntFee - oldFee;
                        IData feeConfig = discntFeeSet.getData(0);

                        if ("0".equals(feeConfig.getString("PAY_MODE")))
                        {
                            IData feeData = new DataMap();
                            feeData.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
                            feeData.put("MODE", feeConfig.getString("FEE_MODE"));
                            feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
                            feeData.put("FEE", fee);
                            feeDatas.add(feeData);
                            element.put("FEE_DATA", feeDatas);
                        }
                    }
                }
            }
            else
            {
                for (int i = 0; i < elements.size(); i++)
                {
                    IData element = elements.getData(i);
                    if ("0".equals(element.getString("MODIFY_TAG")) && "D".equals(element.getString("ELEMENT_TYPE_CODE")))
                    {
                        IDataset feeDatas = new DatasetList();
                        IData feeConfig = discntFeeSet.getData(0);

                        if ("0".equals(feeConfig.getString("PAY_MODE")))
                        {
                            IData feeData = new DataMap();
                            feeData.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
                            feeData.put("MODE", feeConfig.getString("FEE_MODE"));
                            feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
                            feeData.put("FEE", discntFee);
                            feeDatas.add(feeData);
                        }

                        // 加载短套餐工料费用
                        String tempfee = "0";
                        String selectedPId = "";
                        if (StringUtils.isEmpty(nextProductId))
                        {
                            selectedPId = userProductId;
                        }
                        else
                        {
                            selectedPId = nextProductId;
                        }
                        if ((Integer.parseInt(iEndUnit) == 2 || Integer.parseInt(iEndUnit) == 3) && Integer.parseInt(iEndOffset) <= 3)
                        {
                            if (Integer.parseInt(iRsrvTag1) == 1)
                            {
                                tempfee = "8000";
                            }
                            else
                            {
                                if (selectedPId == "50001001")
                                {// 个人
                                    tempfee = "2000";
                                }
                                else if (selectedPId == "50001002")
                                {// 商业
                                    tempfee = "5000";
                                }
                                else
                                {
                                    tempfee = "2000";
                                }
                            }
                            IData feeData = new DataMap();
                            feeData.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
                            feeData.put("MODE", "0");
                            feeData.put("CODE", "9202");
                            feeData.put("FEE", tempfee);
                            feeDatas.add(feeData);
                        }
                        element.put("FEE_DATA", feeDatas);
                    }
                }
            }
        }

        return elements;
    }

    /**
     * 获取END_DATE时间通过TD_S_COMMPARA表配置
     * 
     * @param commpara
     * @param elementId
     * @param elementTypeCode
     * @param cancelDate
     * @return
     * @throws Exception
     */
    public String getCancelDateByCommpara(IDataset commpara, String elementId, String elementTypeCode, String cancelDate) throws Exception
    {
        for (int i = 0; i < commpara.size(); i++)
        {
            String oldDiscnt = commpara.getData(i).getString("PARA_CODE1");
            String workFlag = commpara.getData(i).getString("PARA_CODE3");

            if (oldDiscnt.equals(elementId) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
            {
                if ("0".equals(workFlag))// 可配置para_code3,0为分散用户也结束到月末,1为结束到账期末
                {
                    cancelDate = SysDateMgr.getLastDateThisMonth();
                }
                else
                {
                    cancelDate = SysDateMgr.cancelDate("3", "", "", "");// 本账期末
                }
            }
        }

        return cancelDate;
    }

    private String getProductChangeDate(String oldProductId, String newProductId, IData temp) throws Exception
    {

        String productChangeDate = null;
//        IDataset productTrans = ProductInfoQry.getProductTransInfo(oldProductId, newProductId);
        
        IDataset productTrans = UpcCall.queryJoinEnableModeBy2OfferId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, oldProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT, newProductId);
        
        if (productTrans != null && productTrans.size() > 0)
        {
            IData productTran = productTrans.getData(0);
            temp.putAll(productTran);
            String enableTag = productTran.getString("ENABLE_MODE");
            
            if (enableTag.equals("0"))
            {// 立即生效
                productChangeDate = SysDateMgr.getSysTime();
            }
            else if ((enableTag.equals("1")) || (enableTag.equals("2")))
            {// 下帐期生效

                productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
            }
            else if (enableTag.equals("3"))
            {
                // 按原产品的生效方效

                IData productInfo = UProductInfoQry.qryProductByPK(oldProductId);
                String enableTagOld = productInfo.getString("ENABLE_TAG");

                if ((enableTagOld.equals("0")) || (enableTagOld.equals("2")))
                {// 立即生效
                    productChangeDate = SysDateMgr.getSysTime();
                }
                else if (enableTagOld.equals("1"))
                {// 下帐期生效

                    productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
                }
            }
            else if (enableTag.equals("4"))
            {
                // 按新产品的生效方式

                IData productInfo = UProductInfoQry.qryProductByPK(newProductId);
                String enableTagNew = productInfo.getString("NEW_PRODUCT_ID");

                if ((enableTagNew.equals("0")) || (enableTagNew.equals("2")))
                {// 立即生效
                    productChangeDate = SysDateMgr.getSysTime();
                }
                else if (enableTagNew.equals("1"))
                {// 下帐期生效

                    productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
                }
            }
        }
        return productChangeDate;
    }

    private IData getProductDefaultLong(IDataset productElements) throws Exception
    {
        int size = productElements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            String elementId = element.getString("ELEMENT_ID");
            if ("1".equals(element.getString("ELEMENT_DEFAULT_TAG")) && ("13".equals(elementId) || "14".equals(elementId) || "15".equals(elementId)) && (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE"))))
            {
                return element;
            }
        }
        return null;
    }

    private IData getProductDefaultRoam(IDataset productElements) throws Exception
    {
        int size = productElements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            String elementId = element.getString("ELEMENT_ID");
            if ("1".equals(element.getString("ELEMENT_DEFAULT_TAG"))
                    && ("16".equals(elementId) || "17".equals(elementId) || "18".equals(elementId) || "19".equals(elementId) || "100".equals(elementId) || "101".equals(elementId) || "122".equals(elementId) || "134".equals(elementId))
                    && (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE"))))
            {
                return element;
            }
        }
        return null;
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

    private IData getTransElement(IDataset productElements, String elementId, String elementType)
    {

        if (productElements == null || productElements.size() <= 0)
        {
            return null;
        }
        for (int i = 0; i < productElements.size(); i++)
        {
            IData element = productElements.getData(i);
            if (element.getString("ELEMENT_ID").equals(elementId) && element.getString("ELEMENT_TYPE_CODE").equals(elementType))
            {
                return element;
            }
        }
        return null;
    }

    private IDataset getUserAttrByRelaInstId(IDataset userAttrs, String relaInstId)
    {

        IDataset temp = new DatasetList();
        int size = userAttrs.size();
        for (int i = 0; i < size; i++)
        {
            IData userAttr = userAttrs.getData(i);
            if (relaInstId.equals(userAttr.getString("RELA_INST_ID")))
            {
                IData map = new DataMap();
                map.put("ATTR_CODE", userAttr.getString("ATTR_CODE"));
                map.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
                temp.add(map);
            }
        }
        return temp;
    }

    public IDataset getUserElements(IData param) throws Exception
    {

        String eparchyCode = param.getString("EPARCHY_CODE");
        this.setRouteId(eparchyCode);
        IData result = new DataMap();
        IDataset resultList = new DatasetList();
        String userId = param.getString("USER_ID");
        if (userId == null || "".equals(userId))
        {
            return null;
        }

        IDataset userSvcElements = UserSvcInfoQry.queryUserSvcsInSelectedElements(userId);
        IDataset userDiscntElements = UserDiscntInfoQry.queryUserDiscntsInSelectedElements(userId);
        
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
        // 当有下月生效的新主品时，需要对优惠元素串和服务元素串进行过滤

        // 判断用户是否有下月生效的主产品
        String userProductId = param.getString("USER_PRODUCT_ID", "");
        String nextProductId = "";
        String sysDate = SysDateMgr.getSysTime();
        if (IDataUtil.isNotEmpty(userMainProducts)) {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++) {
                IData userProduct = userMainProducts.getData(i);
                if (userProduct.getString("START_DATE").compareTo(sysDate) > 0) {
                    nextProductId = userProduct.getString("PRODUCT_ID");
                }
                else{
                    userProductId = userProduct.getString("PRODUCT_ID");
                }
            }
        }
        
        //给产商品元素填充包ID
        ProductUtils.fillUserElementPackageId(userSvcElements, userProductId);
        ProductUtils.fillUserElementPackageId(userDiscntElements, userProductId);
        
        String newProductId = param.getString("NEW_PRODUCT_ID", "");

        IDataset nowProductRelationElements = new DatasetList();
        IDataset nextProductRelationElements = new DatasetList();
        
        if (StringUtils.isBlank(nextProductId)) {
            nowProductRelationElements = UProductInfoQry.queryAllProductElements(userProductId);
        } else {
            nextProductRelationElements = UProductInfoQry.queryAllProductElements(nextProductId);
        }
        IDataset allElements = OfferUtil.unionAll(nowProductRelationElements, nextProductRelationElements);
        userSvcElements = OfferUtil.siftIn(userSvcElements, allElements);
        userDiscntElements = OfferUtil.siftIn(userDiscntElements, allElements);
        OfferUtil.fillElements(userSvcElements, userDiscntElements, allElements);
        
        IDataset selectedElements = new DatasetList();
        DataHelper.sort(userSvcElements, "INST_ID", IDataset.TYPE_STRING);
        DataHelper.sort(userDiscntElements, "INST_ID", IDataset.TYPE_STRING);
        IDataset userAttrs = UserAttrInfoQry.queryUserAllAttrs(userId);
        // 因为查出来的元素是连带属性一起查的，因此一个元素如果有多个属性的话，会有多条记录，需要进行合并

        IDataset svcResult = new DatasetList();
        if (userSvcElements != null && userSvcElements.size() > 0)
        {
            String instId = "";
            int size = userSvcElements.size();
            IData tempSvc = null;
            for (int i = 0; i < size; i++)
            {
                IData userSvc = userSvcElements.getData(i);
                if (!instId.equals(userSvc.getString("INST_ID")))
                {
                    if (tempSvc != null)
                    {
                        svcResult.add(tempSvc);
                        tempSvc = null;
                    }
                    if ("".equals(userSvc.getString("ATTR_CODE", "")))
                    {
                        userSvc.remove("ATTR_CODE");
                        svcResult.add(userSvc);
                    }
                    else
                    {
                        tempSvc = userSvc;
                        IDataset attrTempList = new DatasetList();
                        IData attrTemp = new DataMap();
                        attrTemp.put("ATTR_CODE", userSvc.getString("ATTR_CODE"));
                        attrTemp.put("ATTR_VALUE", "");
                        attrTempList.add(attrTemp);
                        tempSvc.remove("ATTR_CODE");
                        tempSvc.put("ATTR_PARAM", attrTempList);
                        if (i == size - 1)
                        {
                            svcResult.add(tempSvc);
                        }
                    }
                }
                else
                {
                    IData attrTemp = new DataMap();
                    attrTemp.put("ATTR_CODE", userSvc.getString("ATTR_CODE"));
                    attrTemp.put("ATTR_VALUE", "");
                    tempSvc.getDataset("ATTR_PARAM").add(attrTemp);
                    if (i == size - 1)
                    {
                        svcResult.add(tempSvc);
                    }
                }
                instId = userSvc.getString("INST_ID");
            }
            size = svcResult.size();
            for (int i = 0; i < size; i++)
            {
                IData svc = svcResult.getData(i);
                IDataset userElementAttrs = this.getUserAttrByRelaInstId(userAttrs, svc.getString("INST_ID"));
                IDataset attrResult = this.makeAttrs(userElementAttrs, svc.getDataset("ATTR_PARAM"));
                if (attrResult != null && attrResult.size() > 0)
                {
                    svc.put("ATTR_PARAM", attrResult);
                }
            }
        }
        IDataset discntResult = new DatasetList();
        if (userDiscntElements != null && userDiscntElements.size() > 0)
        {
            String instId = "";
            int size = userDiscntElements.size();
            IData tempDiscnt = null;
            for (int i = 0; i < size; i++)
            {
                IData userDiscnt = userDiscntElements.getData(i);
                if (!instId.equals(userDiscnt.getString("INST_ID")))
                {
                    if (tempDiscnt != null)
                    {
                        discntResult.add(tempDiscnt);
                        tempDiscnt = null;
                    }
                    if ("".equals(userDiscnt.getString("ATTR_CODE", "")))
                    {
                        userDiscnt.remove("ATTR_CODE");
                        discntResult.add(userDiscnt);
                    }
                    else
                    {
                        tempDiscnt = userDiscnt;
                        IDataset attrTempList = new DatasetList();
                        IData attrTemp = new DataMap();
                        attrTemp.put("ATTR_CODE", userDiscnt.getString("ATTR_CODE"));
                        attrTemp.put("ATTR_VALUE", "");
                        attrTempList.add(attrTemp);
                        tempDiscnt.remove("ATTR_CODE");
                        tempDiscnt.put("ATTR_PARAM", attrTempList);
                        if (i == size - 1)
                        {
                            discntResult.add(tempDiscnt);
                        }
                    }
                }
                else
                {
                    IData attrTemp = new DataMap();
                    attrTemp.put("ATTR_CODE", userDiscnt.getString("ATTR_CODE"));
                    attrTemp.put("ATTR_VALUE", "");
                    tempDiscnt.getDataset("ATTR_PARAM").add(attrTemp);
                    if (i == size - 1)
                    {
                        discntResult.add(tempDiscnt);
                    }
                }
                instId = userDiscnt.getString("INST_ID");
            }
            size = discntResult.size();
            for (int i = 0; i < size; i++)
            {
                IData discnt = discntResult.getData(i);
                IDataset userElementAttrs = this.getUserAttrByRelaInstId(userAttrs, discnt.getString("INST_ID"));
                IDataset attrResult = this.makeAttrs(userElementAttrs, discnt.getDataset("ATTR_PARAM"));
                if (attrResult != null && attrResult.size() > 0)
                {
                    discnt.put("ATTR_PARAM", attrResult);
                }
                
                //start by songlm J2EE铁通营改增遗留需求--宽带套餐变更只能变更生效时间最晚的套餐（针对宽带续费立即生效，而在用户优惠表中产生生效时间还未到的优惠）
                //先判断宽带的优惠是否存在多于1条
                if(size > 1)
                {
                    if(discnt.getString("START_DATE").compareTo(SysDateMgr.getSysDate())>0)//如果是还未生效的优惠，删除处理
                    {
                    	discnt.put("MODIFY_TAG", "1");//删除
                    	discnt.put("PACKAGE_FORCE_TAG", "1");
                        discnt.put("ELEMENT_FORCE_TAG", "1");
                        discnt.put("END_DATE", SysDateMgr.getLastSecond(SysDateMgr.getFirstDayOfThisMonth4WEB()));//结束在当前时间
                    }
                    else//如果是当前生效中的优惠，需要做保留不变处理
                    {
                    	discnt.put("MODIFY_TAG", "exist");
                    	discnt.put("PACKAGE_FORCE_TAG", "1");
                        discnt.put("ELEMENT_FORCE_TAG", "1");
                        //结束时间不变，采用原来的值
                    }
                }
                else//如果宽带的优惠只有1条，则按照原来代码的处理方式进行删除
                {
                	discnt.put("MODIFY_TAG", "1");
                	discnt.put("PACKAGE_FORCE_TAG", "1");
                    discnt.put("ELEMENT_FORCE_TAG", "1");
                    discnt.put("END_DATE", SysDateMgr.getLastSecond(SysDateMgr.getFirstDayOfThisMonth4WEB()));
                }
                //end

            }
        }

        if (svcResult.size() > 0)
        {
            selectedElements.addAll(svcResult);
        }
        if (discntResult.size() > 0)
        {
            selectedElements.addAll(discntResult);
        }
        if (!"".equals(newProductId) && !userProductId.equals(newProductId))
        {
            // IData productTran = new DataMap();

//            String productChangeDate = SysDateMgr.getFirstDayOfThisMonth();
            String productChangeDate = SysDateMgr.getFirstDayOfThisMonth4WEB();
            // IData newProductInfo =
            // ProductInfoQry.getProductByPK(newProductId);
            // String newBrandCode = newProductInfo.getString("BRAND_CODE");
            String oldProductEndDate = SysDateMgr.getLastSecond(productChangeDate);
            result.put("NEW_PRODUCT_START_DATE", productChangeDate);
            result.put("OLD_PRODUCT_END_DATE", oldProductEndDate);

            // 判断是否打开立即生效对话框
            IDataset newProductElements = ProductInfoQry.getProductElements(newProductId, eparchyCode);

            IDataset forceElements = this.getProductForceElement(newProductElements);
            if (forceElements != null && forceElements.size() > 0)
            {
                int size = forceElements.size();
                for (int i = 0; i < size; i++)
                {
                    boolean flag = false;
                    IData element = forceElements.getData(i);
                    for (int j = 0; j < selectedElements.size(); j++)
                    {
                        IData selectedElement = selectedElements.getData(j);
                        if (element.getString("ELEMENT_ID").equals(selectedElement.getString("ELEMENT_ID")) && element.getString("ELEMENT_TYPE_CODE", "").equals(selectedElement.getString("ELEMENT_TYPE_CODE")))
                        {
                            element.put("PACKAGE_FORCE_TAG", "1");
                            element.put("ELEMENT_FORCE_TAG", "1");
                            flag = true;
                            break;
                        }
                    }
                    if (!flag)
                    {
                        IDataset attrItemAList = AttrItemInfoQry.getElementItemA(element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), eparchyCode);
                        IDataset attrs = this.makeAttrs(null, attrItemAList);
                        if (attrs != null && attrs.size() > 0)
                        {
                            element.put("ATTR_PARAM", attrs);
                        }
                        element.remove("START_DATE");
                        element.remove("END_DATE");
                        ProductTimeEnv env = new ProductTimeEnv();
                        env.setBasicAbsoluteStartDate(productChangeDate);
                        ProductModuleData forceElement = null;
                        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
                        {
                            forceElement = new SvcData(element);
                            element.put("EFFECT_NOW_START_DATE", SysDateMgr.getSysTime());
                            element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), forceElement.getEndEnableTag(), forceElement.getEndAbsoluteDate(), forceElement.getEndOffSet(), forceElement.getEndUnit()));
                        }
                        String startDate = ProductModuleCalDate.calStartDate(forceElement, env);
                        element.put("START_DATE", startDate);
                        String endDate = ProductModuleCalDate.calEndDate(forceElement, startDate);
                        element.put("END_DATE", endDate);
                        element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
                        {
                            element.put("ELEMENT_NAME", USvcInfoQry.getSvcNameBySvcId(element.getString("ELEMENT_ID")));
                        }

                        selectedElements.add(element);
                    }
                }
            }

        }
        DataHelper.sort(selectedElements, "ELEMENT_TYPE_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND, "MODIFY_TAG", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        result.put("SELECTED_ELEMENTS", selectedElements);
        resultList.add(result);
        return resultList;
    }

    public IDataset getUserOpenElements(IData param) throws Exception
    {
        String eparchyCode = param.getString("EPARCHY_CODE");
        this.setRouteId(eparchyCode);
        String newProductId = param.getString("NEW_PRODUCT_ID", "");
        IDataset selectedElements = new DatasetList();
        IData result = new DataMap();
        IDataset resultList = new DatasetList();

        ProductTimeEnv env = new ProductTimeEnv();
        String startDate = SysDateMgr.getSysTime();
        env.setBasicAbsoluteStartDate(startDate);
        result.put("NEW_PRODUCT_START_DATE", startDate);
        
        //必选或者默认的元素
        IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, newProductId, "1", "1");
        
        forceElements = ProductUtils.offerToElement(forceElements, newProductId);
        if (forceElements != null && forceElements.size() > 0)
        {
            int size = forceElements.size();
            for (int i = 0; i < size; i++)
            {
                IData element = forceElements.getData(i);

                IDataset attrItemAList = AttrItemInfoQry.getElementItemA(element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), eparchyCode);
                IDataset attrs = this.makeAttrs(null, attrItemAList);
                if (attrs != null && attrs.size() > 0)
                {
                    element.put("ATTR_PARAM", attrs);
                }
                element.remove("START_DATE");
                element.remove("END_DATE");
                ProductModuleData forceElement = null;
                if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
                {
                    forceElement = new SvcData(element);
                }
                else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
                {
                    forceElement = new DiscntData(element);
                }
                startDate = ProductModuleCalDate.calStartDate(forceElement, env);
                element.put("START_DATE", startDate);
                String endDate = ProductModuleCalDate.calEndDate(forceElement, startDate);
                element.put("END_DATE", endDate);
                element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
                {
                    element.put("ELEMENT_NAME", USvcInfoQry.getSvcNameBySvcId(element.getString("ELEMENT_ID")));
                }
                else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
                {
                    element.put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(element.getString("ELEMENT_ID")));
                }
                selectedElements.add(element);

            }
        }

        // 加载相关费用
        int size = selectedElements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = selectedElements.getData(i);
            if ("0".equals(element.getString("MODIFY_TAG")))
            {
                IDataset feeConfigs = ProductFeeInfoQry.getElementFeeList(param.getString("TRADE_TYPE_CODE"), BofConst.ELEMENT_TYPE_CODE_PRODUCT, element.getString("PRODUCT_ID"), element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), element.getString("PACKAGE_ID"));
                if (IDataUtil.isEmpty(feeConfigs))
                {
                    continue;
                }
                int feeSize = feeConfigs.size();
                IDataset feeDatas = new DatasetList();
                for (int j = 0; j < feeSize; j++)
                {
                    IData feeConfig = feeConfigs.getData(j);
                    if (!"0".equals(feeConfig.getString("PAY_MODE")))
                    {
                        continue;
                    }
                    IData feeData = new DataMap();
                    feeData.put("TRADE_TYPE_CODE", param.getString("TRADE_TYPE_CODE"));
                    feeData.put("MODE", feeConfig.getString("FEE_MODE"));
                    feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
                    feeData.put("FEE", feeConfig.getString("FEE"));
                    feeDatas.add(feeData);
                }
                element.put("FEE_DATA", feeDatas);
            }
        }

        DataHelper.sort(selectedElements, "ELEMENT_TYPE_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND, "MODIFY_TAG", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        result.put("SELECTED_ELEMENTS", selectedElements);
        resultList.add(result);
        return resultList;
    }

    private IDataset getUserOpenProductForceElement(IDataset productElements) throws Exception
    {
        int size = productElements.size();
        IDataset result = new DatasetList();
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            if (("1".equals(element.getString("PACKAGE_DEFAULT_TAG")) || "1".equals(element.getString("PACKAGE_FORCE_TAG"))) && ("1".equals(element.getString("ELEMENT_DEFAULT_TAG")) || "1".equals(element.getString("ELEMENT_FORCE_TAG"))))
            {
                result.add(element);
            }
        }
        return result;
    }

    private IDataset makeAttrs(IDataset userAttrs, IDataset elementItemAList)
    {

        if (elementItemAList != null && elementItemAList.size() > 0)
        {
            int size = elementItemAList.size();
            IDataset returnAttrs = new DatasetList();
            for (int i = 0; i < size; i++)
            {
                IData attr = new DataMap();
                IData itemA = elementItemAList.getData(i);
                attr.put("ATTR_CODE", itemA.getString("ATTR_CODE"));
                attr.put("ATTR_VALUE", "");
                if (userAttrs != null && userAttrs.size() > 0)
                {
                    int uSize = userAttrs.size();
                    for (int j = 0; j < uSize; j++)
                    {
                        IData userAttr = userAttrs.getData(j);
                        if (itemA.getString("ATTR_CODE").equals(userAttr.getString("ATTR_CODE")))
                        {
                            attr.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
                            break;
                        }
                    }
                }
                else
                {
                    attr.put("ATTR_VALUE", itemA.getString("ATTR_INIT_VALUE"));
                }
                returnAttrs.add(attr);
            }
            return returnAttrs;
        }
        else
        {
            return null;
        }
    }
}
