
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.product.goods;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TerminalOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

/**
 * 对终端调用华为终端校验接口，进行资源校验、预占；并记录终端信息。
 * 
 * @author Mr.Z
 */
public class TerminalCheckProductAction implements IProductModuleAction
{
    @SuppressWarnings("unchecked")
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {// 加购物车不进行终端预占,如果预占了会导致购物车结算报错 add by duhj
                return;
            }
        }

        SaleGoodsTradeData goodsTradeData = (SaleGoodsTradeData) dealPmtd;
        String resTypeCode = goodsTradeData.getResTypeCode();
        String resTag = goodsTradeData.getResTag();

        if (!"4".equals(resTypeCode) || !"1".equals(resTag))
            return;

        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();

        String preType = saleActiveReqData.getPreType();
        String isConFirm = saleActiveReqData.getIsConfirm();

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
            return;

        if (SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(saleActiveReqData.getCallType()))
            return;

//        IDataset goodsInfos = SaleGoodsInfoQry.querySaleGoodsByGoodsId(goodsTradeData.getElementId());
//        IData goodsInfo = goodsInfos.getData(0);
        //String tagSet = goodsInfo.getString("TAG_SET", "");
        String tagSet = SaleActiveUtil.getSaleGoodsTagSet(goodsTradeData.getElementId(), 2);

        String saleGoodsImei = "", saleSatffId = "";

//        if (tagSet.length() > 2 && tagSet.substring(1, 2).equals("0")) // 购买的终端
        if(StringUtils.isNotBlank(tagSet) && tagSet.equals("0"))
        {
            saleGoodsImei = saleActiveReqData.getSaleGoodsImei();
            String _saleStaffId = saleActiveReqData.getSaleStaffId();
            if (StringUtils.isBlank(_saleStaffId))
            {
                _saleStaffId = CSBizBean.getVisit().getStaffId();
            }
            saleSatffId = _saleStaffId;
            goodsTradeData.setResCode(saleGoodsImei);
            goodsTradeData.setRsrvTag1("0"); // 0 购买
        }
        else
        {
            saleGoodsImei = goodsTradeData.getResCode();
            saleSatffId = goodsTradeData.getRsrvStr8();
            goodsTradeData.setRsrvTag1("1"); // 1 赠送
        }

        IData termianlInfo = null;
        
        String skuCode = "";

        if (StringUtils.isNotBlank(saleGoodsImei) && !"0".equals(saleGoodsImei))
        {
            IData intfParam = new DataMap();
            intfParam.put("RES_NO", saleGoodsImei);
            intfParam.put("SALE_STAFF_ID", saleSatffId);
            intfParam.put("SERIAL_NUMBER", uca.getSerialNumber());
            intfParam.put("CAMPN_TYPE", saleActiveReqData.getCampnType());
            intfParam.put("PRODUCT_ID", saleActiveReqData.getProductId());
            intfParam.put("BUY_TYPE", goodsTradeData.getRsrvTag1());
            intfParam.put("CALL_TYPE", saleActiveReqData.getCallType());
            intfParam.put(Route.ROUTE_EPARCHY_CODE, uca.getUser().getEparchyCode());
            IDataset terminal = TerminalOrderInfoQry.qryTerminalOrderInfoBySn(uca.getSerialNumber());
            if(IDataUtil.isNotEmpty(terminal)){
            	if(terminal.size()>0){
            		/**
            		 * REQ201601240002 关于修改电子渠道终端销售数据统计的需求
            		 * chenxy3 2016/2/2
            		 * 调用华为接口前：
            		 * ID如果为空，则传ID空值调华为接口。ID不为空也需要判断是否超过24小时（rsrv_num1设置），
            		 * 超过了也传ID为空。不超过24小时则将ID值传给华为。
            		 * */ 
            		String id=terminal.getData(0).getString("ID","");

            		if(id!=null && !"".equals(id)){       
            			//取备用字段，用于判断（今天-开始时间）是否超过该字段。
            			String rsrvNum1=terminal.getData(0).getString("RSRV_NUM1");
            			//code_code中增加，今天-开始时间=小时数（保留一位小数）
                		String minusHour=terminal.getData(0).getString("MINUS_HOUR"); 
                		boolean flag=true;
                		//如果备用字段rsrv_num1为空，则默认24小时
                		if(rsrvNum1!=null && !"".equals(rsrvNum1)){
                			flag=Double.parseDouble(minusHour)<Double.parseDouble(rsrvNum1)?true:false;
                		}else{
                			flag=Double.parseDouble(minusHour)<24?true:false;
                		}
                		//满足条件传ID，否则传空
                		if(flag){
                			intfParam.put("NET_ORDER_ID", id);
                		}else{
                			intfParam.put("NET_ORDER_ID", "");
                		} 
                		
            		}else{
            			intfParam.put("NET_ORDER_ID", "");
            		}
            	}
            }
            IDataset termianlInfos = CSAppCall.call("CS.TerminalCheckSVC.preOccupyTerminalByResNo", intfParam);
            termianlInfo = termianlInfos.getData(0);
			//REQ201802050023+终端机型统计优化 by mengqx 20180726
            //增加记录sku编码
            skuCode = termianlInfo.getString("RES_SKU_ID");
        }

        if (saleActiveReqData.isNetStoreActive())
        {
            goodsTradeData.setResCode("0");

            IData intfParam = new DataMap();
            intfParam.put("DEVICE_MODEL_CODE", saleActiveReqData.getDeviceModelCode());
            intfParam.put("RES_TYPE_ID", saleActiveReqData.getResTypeId());
            intfParam.put(Route.ROUTE_EPARCHY_CODE, uca.getUser().getEparchyCode());
            IDataset termianlInfos = CSAppCall.call("CS.TerminalQuerySVC.getTerminalByDeviceModelCode", intfParam);
            termianlInfo = termianlInfos.getData(0);
            
            //REQ201802050023+终端机型统计优化 by mengqx 20180726
            //增加记录sku编码
            skuCode = saleActiveReqData.getDeviceModelCode();
        }

        goodsTradeData.setDeviceBrand(termianlInfo.getString("DEVICE_BRAND")); // 终端品牌名
        goodsTradeData.setDeviceBrandCode(termianlInfo.getString("DEVICE_BRAND_CODE")); // 终端品牌编码
        goodsTradeData.setDeviceCost(termianlInfo.getString("DEVICE_COST")); // 终端成本价
        goodsTradeData.setDeviceModel(termianlInfo.getString("DEVICE_MODEL")); // 终端类型名称
        goodsTradeData.setDeviceModelCode(termianlInfo.getString("DEVICE_MODEL_CODE")); // 终端类型编码
        goodsTradeData.setRsrvStr1(termianlInfo.getString("SUPPLY_COOP_ID")); // 提供商编码
        goodsTradeData.setRsrvStr6(termianlInfo.getString("SALE_PRICE")); // 终端销售价
        goodsTradeData.setRsrvStr7(termianlInfo.getString("DEPUTY_FEE")); // 代办费
        goodsTradeData.setRsrvStr9(termianlInfo.getString("TERMINAL_TYPE_CODE")); // 终端类型编码
        
        //REQ201802050023+终端机型统计优化 by mengqx 20180726
        //增加记录sku编码
        goodsTradeData.setRsrvStr5(skuCode);
        //
        
        
    }
}
