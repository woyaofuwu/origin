package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.product.goods;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUserImeiQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

/**
 * 含有礼品码的特殊礼包类营销活动，对IMEI的记录处理，为了配合短信内容
 * 
 * 
 */
public class GiftSaleAciveImeiAction implements IProductModuleAction
{
    @SuppressWarnings("unchecked")
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
    	SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();
    	
    	//根据活动产品ID，判断是否属于特殊活动
    	String productId = saleActiveReqData.getProductId();
    	IDataset productInfos = CommparaInfoQry.getCommpara("CSM", "531", productId, "0898");
    	
    	//如果不属于特殊活动，则返回，不处理
        if(IDataUtil.isEmpty(productInfos))
        {
        	return;
        }
    	
        //SaleGoods台帐
        SaleGoodsTradeData goodsTradeData = (SaleGoodsTradeData) dealPmtd;
        String imeiCode = saleActiveReqData.getImeiCode();
        if(StringUtils.isBlank(imeiCode))
        {
        	return;
        }
        
        goodsTradeData.setResCode(imeiCode);

        //IData intfParam = new DataMap();
        //intfParam.put("RES_NO", imeiCode);
        //intfParam.put("SALE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        //intfParam.put("SERIAL_NUMBER", uca.getSerialNumber());
        //intfParam.put("CAMPN_TYPE", saleActiveReqData.getCampnType());
        //intfParam.put("PRODUCT_ID", saleActiveReqData.getProductId());
        //intfParam.put(Route.ROUTE_EPARCHY_CODE, uca.getUser().getEparchyCode());
        //IDataset termianlInfos = CSAppCall.call("CS.TerminalQuerySVC.getEnableTerminalByResNo", intfParam);
        
        //由于华为接口需要通过SALE_TAG来判断是铺货还是买断还是虚拟供货，   1: 买断过一次,再优惠购机.  2: 虚拟供货的再优惠购机.   不传:普通终端优惠购机
        //而该需求，机子不在营业销售，只是做校验，再需要记录几个跟该IMEI相关的参数如机型品牌等，所以不使用上面华为接口，要不然不同类（普通或买断或虚拟供货）的机子华为接口会报错
        //修改为直接通过dblink读华为数据库，获取需要的值，如果华为数据库没有记录，则不记录
        IDataset terminalDataset = QueryUserImeiQry.getHuaweiImeiInfos2(imeiCode);
    	if (IDataUtil.isNotEmpty(terminalDataset))
    	{
    		IData termianlInfo = terminalDataset.getData(0);
    		goodsTradeData.setDeviceBrand(termianlInfo.getString("DEVICE_BRAND","")); // 终端品牌名称
    		goodsTradeData.setDeviceModel(termianlInfo.getString("DEVICE_MODEL","")); // 终端类型名称
    		goodsTradeData.setDeviceBrandCode(termianlInfo.getString("DEVICE_BRAND_CODE","")); // 终端品牌编码
    		goodsTradeData.setDeviceModelCode(termianlInfo.getString("DEVICE_MODEL_CODE","")); // 终端类型编码
    	}
    }
}
