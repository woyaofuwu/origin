
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.BofHelper;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class SaleActiveRegSVC extends OrderService
{

    private static final long serialVersionUID = 669278851452760517L;

    @SuppressWarnings("unchecked")
    private void chanageProduct4Commpara1655(IData input, BusiTradeData btd) throws Exception
    {
        List<DiscntTradeData> tradeDiscntList = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if (CollectionUtils.isEmpty(tradeDiscntList))
            return;

        UcaData uca = btd.getRD().getUca();

        boolean isNeedProductChange = false;
        IData commparaData = new DataMap();

        for (DiscntTradeData discntTradeData : tradeDiscntList)
        {
            IDataset commpara1655Dataset = CommparaInfoQry.getCommparaAllColByParser("CSM", "1655", discntTradeData.getDiscntCode(), uca.getUserEparchyCode());
            if (IDataUtil.isEmpty(commpara1655Dataset))
                return;

            commparaData = commpara1655Dataset.getData(0);

            String userProductId = uca.getProductId();
            String commparaProductId = commparaData.getString("PARA_CODE1", "");

            if (commparaProductId.equals(userProductId))
                return;

            isNeedProductChange = true;
        }

        if (!isNeedProductChange)
            return;

        IData changeProductParam = new DataMap();

        changeProductParam.put("SERIAL_NUMBER", uca.getSerialNumber());
        changeProductParam.put("ELEMENT_TYPE_CODE", "P");
        changeProductParam.put("ELEMENT_ID", commparaData.getString("PARA_CODE1"));
        changeProductParam.put("BOOKING_TAG", "1");
        changeProductParam.put("DISCNT_STR1", commparaData.getString("PARA_CODE2"));
        changeProductParam.put("DISCNT_STR2", commparaData.getString("PARA_CODE3"));
        changeProductParam.put("MODIFY_TAG", "0");
        changeProductParam.put("BY_ACTIVE_TRANS", "1");

        CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", changeProductParam);
    }

    @SuppressWarnings("unchecked")
    private void endUserLimitActive(IData input, BusiTradeData btd) throws Exception
    {
        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();

        UcaData uca = saleActiveReqData.getUca();

        String productId = saleActiveReqData.getProductId();
        String packageId = saleActiveReqData.getPackageId();
        String eparchyCode = uca.getUserEparchyCode();
        String serialNumber = uca.getSerialNumber();
        String userActiveEndDate = SysDateMgr.getLastSecond(saleActiveReqData.getStartDate());

        SaleActiveBean saleactiveBean = BeanManager.createBean(SaleActiveBean.class);
        saleactiveBean.endUser3800Actives(uca, serialNumber, productId, packageId, userActiveEndDate, eparchyCode);
        saleactiveBean.endUser1593Actives(uca, serialNumber, productId, packageId, userActiveEndDate, eparchyCode);
    }

    public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", "240");
    }

    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "240");
    }

    @SuppressWarnings("unchecked")
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
    	
    	//多终端受理前移到MoreTerminalSaleActiveAction处理
//    	String productId2 = btd.getRD().getPageRequestData().getString("PRODUCT_ID2");
//    	String packageId2 = btd.getRD().getPageRequestData().getString("PACKAGE_ID2");
//    	String saleGoodsImei2 = btd.getRD().getPageRequestData().getString("SALEGOODS_IMEI2");
//    	String productId3 = btd.getRD().getPageRequestData().getString("PRODUCT_ID3");
//    	String packageId3 = btd.getRD().getPageRequestData().getString("PACKAGE_ID3");
//    	String saleGoodsImei3 = btd.getRD().getPageRequestData().getString("SALEGOODS_IMEI3");
//    	if(StringUtils.isNotBlank(productId2)&&StringUtils.isNotBlank(packageId2)){
//    		 IData activeParam = new DataMap();
//    		 activeParam.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
//    		 activeParam.put("PRODUCT_ID", productId2);
//    		 activeParam.put("PACKAGE_ID", packageId2);
//    		 activeParam.put("SALEGOODS_IMEI", saleGoodsImei2);
//    		 activeParam.put("NO_TRADE_LIMIT", "TRUE");
//    		 activeParam.put("SKIP_RULE", "TRUE");
//    		 CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", activeParam);
//    	}
//    	if(StringUtils.isNotBlank(productId3)&&StringUtils.isNotBlank(packageId3)){
//   		 IData activeParam = new DataMap();
//   		 activeParam.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
//   		 activeParam.put("PRODUCT_ID", productId3);
//   		 activeParam.put("PACKAGE_ID", packageId3);
//   		 activeParam.put("SALEGOODS_IMEI", saleGoodsImei3);
//   		 activeParam.put("NO_TRADE_LIMIT", "TRUE");
//   		 activeParam.put("SKIP_RULE", "TRUE");
//   		 CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", activeParam);
//     	}
    	
    	
    	boolean isNotPreTrade = BofHelper.isNotPreTrade(btd.getRD());
    	String orderTypeCode = getOrderTypeCode();
    	//移机时办理营销活动，现网营销活动的终止不走这里
    	if(isNotPreTrade&&!"606".equals(orderTypeCode))
    	{
        // 办理营销活动X，立即终止营销活动Y
        endUserLimitActive(input, btd);

        // 办理营销活动中存在优惠X，则将用户的主产品变更为Y
        chanageProduct4Commpara1655(input, btd);
    	}
    	//这里还是得自己调用接口终止活动，不能增加1593的配置，否则会导致营销活动受理那里宽带1+和包年活动不再拦截。
    	
		List<SaleActiveTradeData> saleActiveTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
    	for (int i = 0, size = saleActiveTradeDatas.size(); i < size; i++)
        {
            SaleActiveTradeData saleActiveTradeData = saleActiveTradeDatas.get(i);
            String productId=saleActiveTradeData.getProductId();
            String packageId=saleActiveTradeData.getPackageId();
            String campnType=saleActiveTradeData.getCampnType();
            String modifyTag=saleActiveTradeData.getModifyTag();
            if("69908001".equals(productId) && BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
            	/**
            	 * 查看是否存在包年套餐
            	 * 如果存在，则处理：
            	 * 终止包年营销活动
            	 */
            	IDataset yearActives=SaleActiveInfoQry.getUserSaleActiveInfoInUse(btd.getRD().getUca().getUserId(), "67220428");
            	if(yearActives!=null && yearActives.size()>0){
            		IData activeEndDataParam = new DataMap();

                    activeEndDataParam.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
                    activeEndDataParam.put("PRODUCT_ID", yearActives.getData(0).getString("PRODUCT_ID"));
                    activeEndDataParam.put("PACKAGE_ID", yearActives.getData(0).getString("PACKAGE_ID"));
                    activeEndDataParam.put("CAMPN_TYPE", yearActives.getData(0).getString("CAMPN_TYPE"));
                    activeEndDataParam.put("RELATION_TRADE_ID", yearActives.getData(0).getString("RELATION_TRADE_ID"));
                    activeEndDataParam.put("IS_RETURN", "0");
                    activeEndDataParam.put("CALL_TYPE", SaleActiveConst.CALL_TYPE_ACTIVE_TRANS);
                    
                    //edit by zhangxing3 for “不能立即终止包年营销活动，应该终止到月底”
                    String bookingDate = btd.getRD().getPageRequestData().getString("BOOKING_DATE", "");
                    //System.out.println("---------------SaleActiveRegSVC-------------------bookingDate:"+bookingDate);
                    String endDate = "";
                    if (!"".equals(bookingDate))
                    {
                    	endDate = SysDateMgr.getAddMonthsLastDay(-1,bookingDate);
                    	if (endDate.length() == 10)
                    	{
                    		activeEndDataParam.put("FORCE_END_DATE", endDate+" 23:59:59");
                    	}
                    	else {
                    		activeEndDataParam.put("FORCE_END_DATE", endDate);
                    	}
                    }
                    else {
                    	endDate = SysDateMgr.getLastDateThisMonth();
                    	if (endDate.length() == 10)
                    	{
                    		activeEndDataParam.put("FORCE_END_DATE", endDate+" 23:59:59");
                    	}
                    	else {
                    		activeEndDataParam.put("FORCE_END_DATE", endDate);
                    	}
                    }
                    //System.out.println("---------------SaleActiveRegSVC-------------------activeEndDataParam:"+activeEndDataParam);

                    activeEndDataParam.put("END_DATE_VALUE", BofConst.MODIFY_TAG_FORCE_END);
                    
                    activeEndDataParam.put("PRE_TYPE", btd.getRD().getPreType());
                    //edit by zhangxing3 for “不能立即终止包年营销活动，应该终止到月底”
                    
                    CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg", activeEndDataParam);
            	}
            }
            if("66000602".equals(productId) && BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
            	/**
            	 * 查看是否存在宽带1+
            	 * 如果存在，则处理：
            	 * 终止宽带1+营销活动
            	 */
            	IDataset userActives=SaleActiveInfoQry.getUserSaleActiveInfoInUse(btd.getRD().getUca().getUserId(), "69908001");
            	if(userActives!=null && userActives.size()>0){
            		IData activeEndDataParam = new DataMap();

                    activeEndDataParam.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
                    activeEndDataParam.put("PRODUCT_ID", userActives.getData(0).getString("PRODUCT_ID"));
                    activeEndDataParam.put("PACKAGE_ID", userActives.getData(0).getString("PACKAGE_ID"));
                    activeEndDataParam.put("CAMPN_TYPE", userActives.getData(0).getString("CAMPN_TYPE"));
                    activeEndDataParam.put("RELATION_TRADE_ID", userActives.getData(0).getString("RELATION_TRADE_ID"));
                    activeEndDataParam.put("IS_RETURN", "0");
                    activeEndDataParam.put("CALL_TYPE", SaleActiveConst.CALL_TYPE_ACTIVE_TRANS);
                    String ForceEndDate =SysDateMgr.getDateLastMonthSec( btd.getRD().getAcceptTime() );
                    activeEndDataParam.put("FORCE_END_DATE", ForceEndDate);
                    activeEndDataParam.put("END_DATE_VALUE", BofConst.MODIFY_TAG_FORCE_END);

                    CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg", activeEndDataParam);
            	}
            }
        }

        // 办理套餐升档优惠活动, 办理该营销产品活动需同事变更用户主套餐产品；
        String productId = input.getString("PRODUCT_ID", "");
    	String packageId = input.getString("PACKAGE_ID", "");

    	IDataset active = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1303", productId, packageId);
    	if (IDataUtil.isNotEmpty(active)){// 校验当前用户主套餐
            // 用户当前的主套餐id
            String userMainProductId =btd.getRD().getUca().getProductId();
            // 用户预约的主套餐id
            ProductTradeData productTradeData= btd.getRD().getUca().getUserNextMainProduct();
            // 办理活动需要用户变更的主套餐id
            String mainProductId = active.first().getString("PARA_CODE2","");

            if (!mainProductId.equals(userMainProductId)){ // 用户当前主产品套餐不是该营销包匹配的主产品
                String flag = "0";
                // 将用户主套餐变更成配置的主套餐前，先判断当前用户是否存在预约的产品变更
                if (null != productTradeData){// 如果预约变更的主产品为指定变更的主产品，就不需要再做产品变更了。
                    String nextProductId = productTradeData.getProductId();
                    if (mainProductId.equals(nextProductId)){// 不需要再产品变更了
                        flag = "1";
                    }
                }
                if ("0".equals(flag)){
                    // 默认定义主套餐生效时间：次月生效
                    String StartDate = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate());
                    // 获取营销活动台账信息，从而得到营销活动生效时间。
                    List<SaleActiveTradeData> saleActiveTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
                    for (int i=0; i<saleActiveTradeData.size();i++){
                        SaleActiveTradeData saleActiveTradeData1 = saleActiveTradeData.get(i);
                        if (productId.equals(saleActiveTradeData1.getProductId())){
                            StartDate = saleActiveTradeData1.getStartDate();
                        }
                    }

                    // 办理产品变更前的参数准备
                    IData productChangeParam = new DataMap();
                    productChangeParam.put("EPARCHY_CODE", getUserEparchyCode());
                    productChangeParam.put("ROUTE_EPARCHY_CODE", getUserEparchyCode());
                    productChangeParam.put("USER_ID", btd.getRD().getUca().getUserId());
                    productChangeParam.put("NEW_PRODUCT_ID",mainProductId);
                    productChangeParam.put("BOOKING_DATE",StartDate);

                    IDataset result = CSAppCall.call("CS.SelectedElementSVC.getUserElements", productChangeParam);
                    IDataset elements = result.getData(0).getDataset("SELECTED_ELEMENTS");
                    IDataset selectedElements = new DatasetList();
                    for (int i = 0; i < elements.size(); i++)
                    {
                        IData element = elements.getData(i);
                        String modifyTag = element.getString("MODIFY_TAG");
                        if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                        {
                            IData data = new DataMap();
                            data.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
                            data.put("ELEMENT_TYPE_CODE", element.getString("ELEMENT_TYPE_CODE"));
                            data.put("PRODUCT_ID", element.getString("PRODUCT_ID"));
                            data.put("PACKAGE_ID", element.getString("PACKAGE_ID"));
                            data.put("ATTR_PARAM", element.getDataset("ATTR_PARAM"));
                            data.put("MODIFY_TAG", modifyTag);
                            data.put("START_DATE", element.getString("START_DATE"));
                            data.put("END_DATE", element.getString("END_DATE"));
                            data.put("INST_ID", element.getString("INST_ID"));
                            selectedElements.add(data);
                        }
                    }
                    IData param = new DataMap();
                    param.put("SELECTED_ELEMENTS",selectedElements);
                    param.put("SERIAL_NUMBER",input.get("SERIAL_NUMBER"));
                    param.put("NEW_PRODUCT_ID",mainProductId);
                    param.put("CHECK_MODE","ONE_KEY");
                    param.put("AUTH_SERIAL_NUMBER",input.get("SERIAL_NUMBER"));
                    param.put("SALE_ACTIVE_PRODUCT_ID",productId);
                    // 根据营销活动生效时间，设置主产品生效时间
                    param.put("BOOKING_DATE",StartDate);
                    param.put("BOOKING_TAG", "1");
                    param.put("START_DATE", StartDate);
                    param.put("ROUTE_EPARCHY_CODE",getUserEparchyCode());


                    IDataset tradeRst = new DatasetList();
                    tradeRst = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", param);

                }
            }else {// 当前用户主套餐已经为目标套餐，还需要判断是否存在预约产品变更。
                if (null != productTradeData){// 如果还存在预约的主产品，需要报错，不能再继续往下办理
                    // 您已预约生效的主产品不是该营销包指定的主产品，不能办理该营销活动!
                    CSAppException.apperr(ProductException.CRM_PRODUCT_525);
                }
            }
        }
    }
}
