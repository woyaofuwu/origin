
package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.action.finish;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleGoodsInfoQry;

/**
 * 宽带回单营销活动处理
 * 
 * @author yuyj3
 */
public class DealSaleActiveAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("RSRV_STR1","");
        String tradeId = mainTrade.getString("TRADE_ID");
        
        //增加特殊处理，如果是移机并且同时终止老营销活动，办理新营销活动，需要让老营销活动先终止，然后再办理新营销活动
        //此处处理，如果用户同时拥有一笔237台账，则直接返回
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
    	String orderId = mainTrade.getString("ORDER_ID");
        
        String productId = "";
        String packageId = "";
        IDataset saleActiveInfos = UserSaleActiveInfoQry.getBook2ValidSaleActiveByTradeId(tradeId, serialNumber);
        for (int i = 0, size = saleActiveInfos.size(); i < size; i++)
        {
            IData saleActiveInfo = saleActiveInfos.getData(i);
            serialNumber = saleActiveInfo.getString("SERIAL_NUMBER");
            productId = saleActiveInfo.getString("PRODUCT_ID_B");
            packageId = saleActiveInfo.getString("PACKAGE_ID_B");
            
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            String userId = userInfo.getString("USER_ID");
            
            IDataset validSaleActiveInfos = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(userId, productId,packageId);
            if(IDataUtil.isEmpty(validSaleActiveInfos)){
            	
            	//UserSaleActiveInfoQry.updateBook2ValidSaleActiveByTradeId(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", "");
            
	            IData param = new DataMap();
	            param.put("SERIAL_NUMBER", serialNumber);
	            param.put("PRODUCT_ID", productId);
	            param.put("PACKAGE_ID", packageId);
	            param.put("TRADE_STAFF_ID", "SUPERUSR");
	            param.put("TRADE_DEPART_ID", "36601");
	            param.put("TRADE_CITY_CODE", "HNSJ");
            	if("6800".equals(tradeTypeCode))
	            {
	                //标记是宽带开户营销活动
	                param.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
	                param.put("SALE_ACTIVE_ID", mainTrade.getString("CAMPN_ID",""));
	            }
            	
            	//营销活动转正式需要跳过工单互斥校验
                param.put("NO_TRADE_LIMIT", "TRUE");
                
	            IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param);
	            String intfTradeId = result.getData(0).getString("TRADE_ID");
	
	            UserSaleActiveInfoQry.updateBook2ValidSaleActiveByTradeId(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", intfTradeId);
	            
	            IDataset ds = UserSaleGoodsInfoQry.getByRelationTradeId(saleActiveInfo.getString("RELATION_TRADE_ID", ""));
	            if(IDataUtil.isNotEmpty(ds))
	            {
	            	IData inparam = new DataMap(); ;
		            inparam.put("RES_NO", ds.getData(0).getString("RES_CODE"));//串号
		            inparam.put("SALE_FEE", "0");//销售费用:不是销售传0
		            inparam.put("PARA_VALUE1", serialNumber);//购机用户的手机号码
		            inparam.put("PARA_VALUE7", "0");//代办费
		            inparam.put("DEVICE_COST", ds.getData(0).getString("DEVICE_COST"));//进货价格--校验接口取
		            inparam.put("TRADE_ID ",  tradeId);//台账流水 
		            inparam.put("X_CHOICE_TAG", "0");//0-终端销售,1—终端销售退货
		            inparam.put("RES_TYPE_CODE", "4");//资源类型,终端的传入4
		            inparam.put("CONTRACT_ID",  tradeId);//销售订单号
		            inparam.put("PRODUCT_MODE", "0");
		        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        	inparam.put("PARA_VALUE11", sdf.format(new Timestamp(System.currentTimeMillis())));//销售时间
		        	inparam.put("PARA_VALUE13", "0");//是否有销售酬金  0-没有 1-有
		        	inparam.put("PARA_VALUE14",  ds.getData(0).getString("DEVICE_COST"));//裸机价格  从检验接口取裸机价格
		        	inparam.put("PARA_VALUE15", "0");//客户购机折让价格
		        	inparam.put("PARA_VALUE16", "0");
		        	inparam.put("PARA_VALUE17", "0");
		        	inparam.put("PARA_VALUE18", "0");//客户实缴费用总额  //如果没有合约，就和实际付款相等就可以。 
		        	inparam.put("PARA_VALUE9", "03");//客户捆绑合约类型 //合约类型：01—全网统一预存购机 02—全网统一购机赠费 03—预存购机 
		        	inparam.put("STAFF_ID", "SUPERUSR");//销售员工
		        	inparam.put("RES_TRADE_CODE", "IMobileDeviceModifyState");

		        	IDataset sysResults = HwTerminalCall.occupyTerminalByTerminalId(inparam);
		        	if(!StringUtils.equals(sysResults.first().getString("X_RESULTCODE"), "0")){//0为成功，其他失败
		        		String x_resultinfo=sysResults.first().getString("X_RESULTINFO");
		        		if(StringUtils.isNotBlank(sysResults.first().getString("X_RESULTINFO"))){
		        			CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
		        		}
		        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"家庭IMS固话终端实占接口调用异常！");
		        	}
	            }
            }else{
            	UserSaleActiveInfoQry.updateBook2ValidSaleActiveByTradeId(tradeId, serialNumber, productId, packageId, "SUPERUSR", "36601", "");
            }
        }
    }

}
