
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.elem;

import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.script.rule.common.SplCheckByRegular;

public class ChangeProductDealDiscntAction implements ITradeAction
{

    private void addBrandChangeDiscnt(IDataset brandChangeConfigs, BusiTradeData btd, UcaData uca, ProductTradeData productTrade, IDataset productElements) throws Exception
    {
        String firstDayNextMonth = SysDateMgr.getFirstDayOfNextMonth();
        if (IDataUtil.isNotEmpty(brandChangeConfigs))
        {
            int size = brandChangeConfigs.size();
            for (int i = 0; i < size; i++)
            {
                IData config = brandChangeConfigs.getData(i);
                String discntCode = config.getString("PARA_CODE1");
                String paraCode2 = config.getString("PARA_CODE2");
                String paraCode8 = config.getString("PARA_CODE8");
                String paraCode6 = config.getString("PARA_CODE6");
                String paraCode3 = config.getString("PARA_CODE3");
                String paraCode4 = config.getString("PARA_CODE4");
                String paraCode5 = config.getString("PARA_CODE5");
                String paramName = config.getString("PARAM_NAME");
                // String paraCode15 = config.getString("PARA_CODE15");//控制批量开户不绑定300M3G流量
                if ("1".equals(paraCode3))
                {
                    continue;
                }
                if (StringUtils.isNotBlank(paraCode2) && !SplCheckByRegular.matcherText(paraCode2, uca.getSerialNumber()))
                {
                    continue;
                }
                if (StringUtils.isNotBlank(paraCode6) && !btd.getTradeTypeCode().equals(paraCode6))
                {
                    continue;
                }
                /*
                 * if(StringUtils.isNotBlank(btd.getMainTradeData().getBatchId()) && StringUtils.isNotBlank(paraCode15)
                 * && "1".equals(paraCode15)) { continue; }
                 */

                String startDate = productTrade.getStartDate();
                String endDate = productTrade.getEndDate();
                if (StringUtils.isNotBlank(paraCode4))
                {
                    if (paraCode4.indexOf("-") < 0 && "01234566789".indexOf(paraCode4) > 0)
                    {
                        endDate = SysDateMgr.endDate(productTrade.getStartDate(), "1", "", paraCode4, "2");
                    }
                    else if (paraCode4.indexOf("-") > 0)
                    {
                        endDate = paraCode4;
                    }
                }

                if (StringUtils.isNotBlank(paraCode5))
                {
                    if ("FirstDayOfNextMonth".equals(paraCode5))
                    {
                        startDate = firstDayNextMonth;
                    }
                    else if (paraCode5.indexOf("-") > 0)
                    {
                        startDate = paraCode5;
                    }
                }

                List<DiscntTradeData> userDiscnts = uca.getUserDiscntByDiscntId(discntCode);
                if (userDiscnts != null && userDiscnts.size() > 0)
                {
                    boolean isFind = false;
                    for (DiscntTradeData userDiscnt : userDiscnts)
                    {
                        if (!BofConst.MODIFY_TAG_DEL.equals(userDiscnt.getModifyTag()))
                        {
                            isFind = true;
                            break;
                        }
                        else if (BofConst.MODIFY_TAG_DEL.equals(userDiscnt.getModifyTag()))
                        {
                            // newSvcStartDate
                            String userDiscntEndDate = userDiscnt.getEndDate();
                            if (userDiscntEndDate.compareTo(startDate) >= 0)
                            {
                                // 用户删除的这个元素的结束时间大于等于新增时的开始时间
                                // 则新增时的开始时间改为结束时间的下一秒
                                startDate = SysDateMgr.getNextSecond(userDiscntEndDate);
                            }
                        }
                    }

                    if (isFind)
                    {
                        continue;
                    }
                }
                IData transElement = this.getTransElement(productElements, discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT);
                String productId = "-1";
                String packageId = "-1";
                if (IDataUtil.isNotEmpty(transElement))
                {
                    productId = transElement.getString("PRODUCT_ID");
                    packageId = transElement.getString("PACKAGE_ID");
                }
                DiscntTradeData newDiscnt = new DiscntTradeData();
                newDiscnt.setUserId(uca.getUserId());
                newDiscnt.setProductId(productId);
                newDiscnt.setPackageId(packageId);
                newDiscnt.setElementId(discntCode);
                newDiscnt.setInstId(SeqMgr.getInstId());
                newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                newDiscnt.setSpecTag("0");
                newDiscnt.setStartDate(startDate);
                newDiscnt.setEndDate(endDate);
                newDiscnt.setRemark(paramName);
                btd.add(uca.getSerialNumber(), newDiscnt);
            }
        }
    }

    private void addProductDiscnt(IDataset addProductConfigs, BusiTradeData btd, UcaData uca, ProductTradeData productTrade, IDataset productElements) throws Exception
    {
        String firstDayNextMonth = SysDateMgr.getFirstDayOfNextMonth();
        if (IDataUtil.isNotEmpty(addProductConfigs))
        {
            int size = addProductConfigs.size();
            for (int i = 0; i < size; i++)
            {
                IData config = addProductConfigs.getData(i);
                String discntCode = config.getString("PARA_CODE1");
                String paraCode2 = config.getString("PARA_CODE2");
                // String paraCode8 = config.getString("PARA_CODE8");
                String paraCode6 = config.getString("PARA_CODE6");
                String paraCode3 = config.getString("PARA_CODE3");
                // String paraCode7 = config.getString("PARA_CODE7");
                String paraCode4 = config.getString("PARA_CODE4");
                String paraCode5 = config.getString("PARA_CODE5");
                String paramName = config.getString("PARAM_NAME");
                if ("1".equals(paraCode3))
                {
                    continue;
                }
                if (StringUtils.isNotBlank(paraCode2) && !SplCheckByRegular.matcherText(paraCode2, uca.getSerialNumber()))
                {
                    continue;
                }
                /*
                 * if (StringUtils.isNotBlank(paraCode8)) { String advancePay = btd.getMainTradeData().getAdvancePay();
                 * if (Integer.parseInt(advancePay) < Integer.parseInt(paraCode8)) { continue; } }
                 */
                if (StringUtils.isNotBlank(paraCode6) && !btd.getTradeTypeCode().equals(paraCode6))
                {
                    continue;
                }

                String startDate = productTrade.getStartDate();
                String endDate = productTrade.getEndDate();
                if (StringUtils.isNotBlank(paraCode4))
                {
                    if (paraCode4.indexOf("-") < 0 && "01234566789".indexOf(paraCode4) > 0)
                    {
                        endDate = SysDateMgr.endDate(productTrade.getStartDate(), "1", "", paraCode4, "2");
                    }
                    else if (paraCode4.indexOf("-") > 0)
                    {
                        endDate = paraCode4;
                    }
                }

                if (StringUtils.isNotBlank(paraCode5))
                {
                    if ("FirstDayOfNextMonth".equals(paraCode5))
                    {
                        startDate = firstDayNextMonth;
                    }
                    else if (paraCode5.indexOf("-") > 0)
                    {
                        startDate = paraCode5;
                    }
                }
                else
                {
                    startDate = btd.getRD().getAcceptTime();
                }

                List<DiscntTradeData> userDiscnts = uca.getUserDiscntByDiscntId(discntCode);
                if (userDiscnts != null && userDiscnts.size() > 0)
                {
                    boolean isFind = false;
                    for (DiscntTradeData userDiscnt : userDiscnts)
                    {
                        if (!BofConst.MODIFY_TAG_DEL.equals(userDiscnt.getModifyTag()))
                        {
                            isFind = true;
                            break;
                        }
                        else if (BofConst.MODIFY_TAG_DEL.equals(userDiscnt.getModifyTag()))
                        {
                            // newSvcStartDate
                            String userDiscntEndDate = userDiscnt.getEndDate();
                            if (userDiscntEndDate.compareTo(startDate) >= 0)
                            {
                                // 用户删除的这个元素的结束时间大于等于新增时的开始时间
                                // 则新增时的开始时间改为结束时间的下一秒
                                startDate = SysDateMgr.getNextSecond(userDiscntEndDate);
                            }
                        }
                    }
                    if (isFind)
                    {
                        continue;
                    }
                }
                IData transElement = this.getTransElement(productElements, discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT);
                String productId = "-1";
                String packageId = "-1";
                if (IDataUtil.isNotEmpty(transElement))
                {
                    productId = transElement.getString("PRODUCT_ID");
                    packageId = transElement.getString("PACKAGE_ID");
                }
                DiscntTradeData newDiscnt = new DiscntTradeData();
                newDiscnt.setUserId(uca.getUserId());
                newDiscnt.setProductId(productId);
                newDiscnt.setPackageId(packageId);
                newDiscnt.setElementId(discntCode);
                newDiscnt.setInstId(SeqMgr.getInstId());
                newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                newDiscnt.setSpecTag("0");
                newDiscnt.setStartDate(startDate);
                newDiscnt.setEndDate(endDate);
                newDiscnt.setRemark(paramName);
                btd.add(uca.getSerialNumber(), newDiscnt);

            }
        }
    }

    private void delAutoDiscnt(IDataset autoDiscntConfigs, UcaData uca, BusiTradeData btd, String newProductStartDate) throws Exception
    {
        if (IDataUtil.isNotEmpty(autoDiscntConfigs))
        {
            int size = autoDiscntConfigs.size();
            for (int i = 0; i < size; i++)
            {
                IData config = autoDiscntConfigs.getData(i);
                String discntCode = config.getString("PARA_CODE1");
                String paraCode2 = config.getString("PARA_CODE2");
                String endDate = "";
                if ("1".equals(paraCode2))
                {
                    endDate = SysDateMgr.getLastDateThisMonth();
                }
                else if ("0".equals(paraCode2))
                {
                    endDate = btd.getRD().getAcceptTime();
                }
                else if ("2".equals(paraCode2))
                {
                    if (newProductStartDate.compareTo(SysDateMgr.getFirstDayOfNextMonth()) >= 0)
                    {
                        endDate = SysDateMgr.getLastDateThisMonth();
                    }
                    else
                    {
                        endDate = btd.getRD().getAcceptTime();
                    }
                }
                else
                {
                    continue;
                }
                if (DataBusUtils.isPassiveAddElement(discntCode, "D"))
                {
                    continue;
                }
                List<DiscntTradeData> userDiscnts = uca.getUserDiscntByDiscntId(discntCode);
                if (userDiscnts != null && userDiscnts.size() > 0)
                {
                    for (DiscntTradeData userDiscnt : userDiscnts)
                    {
                        if (BofConst.MODIFY_TAG_USER.equals(userDiscnt.getModifyTag()))
                        {
                            DiscntTradeData discntTrade = userDiscnt.clone();
                            discntTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                            discntTrade.setEndDate(endDate);
                            btd.add(uca.getSerialNumber(), discntTrade);
                        }
                    }
                }
            }
        }
    }

    private void delDiscnt(IDataset cancelConfigs, UcaData uca, BusiTradeData btd, String startDate, String tag) throws Exception
    {
        if (IDataUtil.isNotEmpty(cancelConfigs))
        {
            int size = cancelConfigs.size();
            for (int i = 0; i < size; i++)
            {
                IData config = cancelConfigs.getData(i);
                String discntCode = config.getString("PARA_CODE1");
                String paraCode2 = config.getString("PARA_CODE2");
                String paraCode7 = config.getString("PARA_CODE7");
                if (StringUtils.isNotBlank(paraCode2) && !SplCheckByRegular.matcherText(paraCode2, uca.getSerialNumber()))
                {
                    continue;
                }
                if ("1".equals(paraCode7))
                {
                    continue;
                }
                if (DataBusUtils.isPassiveAddElement(discntCode, "D"))
                {
                    continue;
                }

                String yesterday = SysDateMgr.addDays(startDate, -1) + SysDateMgr.getEndTime235959();
                List<DiscntTradeData> userDiscnts = uca.getUserDiscntByDiscntId(discntCode);
                if (userDiscnts != null && userDiscnts.size() > 0)
                {
                    for (DiscntTradeData userDiscnt : userDiscnts)
                    {
                        if (BofConst.MODIFY_TAG_USER.equals(userDiscnt.getModifyTag()))
                        {
                            DiscntTradeData discntTrade = userDiscnt.clone();
                            discntTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                            discntTrade.setEndDate(yesterday);
                            if ("P".equals(tag))
                                discntTrade.setRemark("产品绑定优惠取消");
                            else if ("B".equals(tag))
                                discntTrade.setRemark("品牌绑定优惠取消");
                            btd.add(uca.getSerialNumber(), discntTrade);
                        }
                    }
                }
            }
        }
    }

    // @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        UcaData uca = btd.getRD().getUca();
        List<ProductTradeData> productTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        if (productTrades != null && productTrades.size() > 0)
        {
            for (ProductTradeData productTrade : productTrades)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(productTrade.getModifyTag()) && "1".equals(productTrade.getMainTag()))
                {
//                    IDataset productElements = ProductInfoQry.getProductElements(productTrade.getProductId(), uca.getUserEparchyCode());
                    IDataset offerElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productTrade.getProductId(), null, null);
                    IDataset productElements = ProductUtils.offerToElement(offerElements,productTrade.getProductId());
                    IDataset addProductConfigs = CommparaInfoQry.getCommpara("CSM", "257", productTrade.getProductId(), uca.getUserEparchyCode());

                    if (StringUtils.isNotBlank(productTrade.getOldProductId()))
                    {
                        IDataset cancelProductConfigs = CommparaInfoQry.getCommpara("CSM", "257", productTrade.getOldProductId(), uca.getUserEparchyCode());
                        this.delDiscnt(cancelProductConfigs, uca, btd, productTrade.getStartDate(), "P");
                    }

                    IDataset brandChangeConfigs = CommparaInfoQry.getCommpara("CSM", "256", productTrade.getBrandCode(), uca.getUserEparchyCode());

                    if (StringUtils.isNotBlank(productTrade.getOldBrandCode()) && !productTrade.getBrandCode().equals(productTrade.getOldBrandCode()))
                    {
                        IDataset cancelBrandChangeConfigs = CommparaInfoQry.getCommpara("CSM", "256", productTrade.getOldBrandCode(), uca.getUserEparchyCode());
                        this.delDiscnt(cancelBrandChangeConfigs, uca, btd, productTrade.getStartDate(), "B");
                    }
                    /*
                     * IDataset delAutoDiscntConfigs = CommparaInfoQry.getCommpara("CSM", "270",
                     * productTrade.getProductId(), uca.getUserEparchyCode()); this.delAutoDiscnt(delAutoDiscntConfigs,
                     * uca, btd, productTrade.getStartDate());
                     */

                    this.addProductDiscnt(addProductConfigs, btd, uca, productTrade, productElements);
                    this.addBrandChangeDiscnt(brandChangeConfigs, btd, uca, productTrade, productElements);
                }
            }
        }
    }

    private IData getTransElement(IDataset productElements, String elementId, String elementType)
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
}
