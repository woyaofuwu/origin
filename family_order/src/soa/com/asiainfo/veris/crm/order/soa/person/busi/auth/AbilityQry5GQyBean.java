package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class AbilityQry5GQyBean extends CSBizBean {
	static Logger logger = Logger.getLogger(AbilityQry5GQyBean.class);

	/**
	 * 5G金币，权益接口
	 * 2020-06-02
	 * wangsc10
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * CIP00152【可领取权益查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData klqqycx(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "number");
		String goodsId = input.getString("goodsId", "");
		input.put("goodsId", goodsId.toCharArray());
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00152");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00153【商品续订状态通知】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData spxdzttz(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "provinceRelationId");
		IDataUtil.chkParam(input, "orderSource");
		IDataUtil.chkParam(input, "orderId");
		IDataUtil.chkParam(input, "number");
		IDataUtil.chkParam(input, "goodsId");
		IDataUtil.chkParam(input, "status");
		if("FA".equals(input.getString("status"))){
			IDataUtil.chkParam(input, "statusDesc");
		}else if("SC".equals(input.getString("status"))){
			IDataUtil.chkParam(input, "periodType");
			IDataUtil.chkParam(input, "periodStart");
			IDataUtil.chkParam(input, "periodEnd");
		}
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00153");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
            		IData param = new DataMap();
            		param.put("PROVINCERELATIONID", input.getString("provinceRelationId", ""));
            		param.put("ORDERSOURCE", input.getString("orderSource", ""));
            		param.put("ORDERID", input.getString("orderId", ""));
            		param.put("SERIALNUMBER", input.getString("number", ""));
            		param.put("GOODSID", input.getString("goodsId", ""));
            		param.put("STATUS", input.getString("status", ""));
            		param.put("STATUSDESC", input.getString("statusDesc", ""));
            		param.put("PERIODTYPE", input.getString("periodType", ""));
            		param.put("PERIOD", input.getString("period", ""));
            		param.put("PERIODSTART", input.getString("periodStart", ""));
            		param.put("PERIODEND", input.getString("periodEnd", ""));
            		this.insertCommodityState(param);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
     * 商品续订状态表
     */
    public void  insertCommodityState(IData param)  throws Exception{
    	Dao.insert("TF_F_USER_COMMODITY_STATE", param);
    }
	
	/**
	 * CIP00154【商品续订状态查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData spxdztcx(IData input) throws Exception{
		IData result = new DataMap();
		
		IDataUtil.chkParam(input, "number");
		IDataUtil.chkParam(input, "goodsId");
		IDataUtil.chkParam(input, "provinceRelationId");
		
		IData param = new DataMap();
		param.put("SERIALNUMBER", input.getString("number", ""));
		param.put("GOODSID", input.getString("goodsId", ""));
		param.put("PROVINCERELATIONID", input.getString("provinceRelationId", ""));
		
		IDataset commodityState = this.qureyCommodityState(param);
		if (IDataUtil.isNotEmpty(commodityState)) {
			result.put("bizCode", "0000");
			result.put("bizDesc", "成功");
			result.put("status", commodityState.getData(0).getString("STATUS", ""));
			result.put("statusDesc", commodityState.getData(0).getString("STATUSDESC", ""));
			result.put("periodType", commodityState.getData(0).getString("PERIODTYPE", ""));
			result.put("period", commodityState.getData(0).getString("PERIOD", ""));
			result.put("periodStart", commodityState.getData(0).getString("PERIODSTART", ""));
			result.put("periodEnd", commodityState.getData(0).getString("PERIODEND", ""));
		}else{
			result.put("bizCode", "2998");
			result.put("bizDesc", "查询不到商品续订记录！");
		}
		
		return result;
	}
	
	public static IDataset qureyCommodityState(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        
        parser.addSQL(" SELECT T.*");
        parser.addSQL(" FROM TF_F_USER_COMMODITY_STATE T"); 
        parser.addSQL(" WHERE T.SERIALNUMBER = :SERIALNUMBER ");
        parser.addSQL(" AND T.GOODSID = :GOODSID ");
        parser.addSQL(" AND T.PROVINCERELATIONID = :PROVINCERELATIONID ");

        return Dao.qryByParse(parser);
    } 
	
	/**
	 * CIP00155【省（发起）权益商品退订】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData qysptd(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "returnType");
		IDataUtil.chkParam(input, "extReturnId");
		IDataUtil.chkParam(input, "orderId");
		IDataUtil.chkParam(input, "subOrderId");
		IDataUtil.chkParam(input, "buyerReturnTime");
		IDataUtil.chkParam(input, "extChannelReturnTime");
		IDataUtil.chkParam(input, "number");
		IData returnGoodsInfo = input.getData("returnGoodsInfo");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00155");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00156【金币余额查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData jbyecx(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "provinceCode");
		IDataUtil.chkParam(input, "mobile");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00156");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00157【金币明细查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData jbmxcx(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "provinceCode");
		IDataUtil.chkParam(input, "mobile");
		IDataUtil.chkParam(input, "queryType");
		IDataUtil.chkParam(input, "page");
		IDataUtil.chkParam(input, "rows");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00157");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00158【金币充值】接口（受理类，生成台账）
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData jbcz(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "tradeId");
		IDataUtil.chkParam(input, "provinceCode");
		IDataUtil.chkParam(input, "mobile");
		IDataUtil.chkParam(input, "actionType");
		IDataUtil.chkParam(input, "coinsType");
		IDataUtil.chkParam(input, "rechargeValue");
		IDataUtil.chkParam(input, "startDate");
		IDataUtil.chkParam(input, "endDate");
		IDataUtil.chkParam(input, "tradeTime");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00158");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00159【金币交易超时查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData jbjycscx(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "tradeId");
		IDataUtil.chkParam(input, "provinceCode");
		IDataUtil.chkParam(input, "mobile");
		IDataUtil.chkParam(input, "oprType");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00159");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00160【金币兑换商品】接口（受理类，生成台账）
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData jbdhsp(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "provinceCode");
		IDataUtil.chkParam(input, "mobile");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00160");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00161【金币渠道订单查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData jbqdddcx(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "provinceCode");
		IDataUtil.chkParam(input, "mobile");
		IDataUtil.chkParam(input, "startTime");
		IDataUtil.chkParam(input, "endTime");
		IDataUtil.chkParam(input, "page");
		IDataUtil.chkParam(input, "rows");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00161");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00162【金币可兑换商品列表查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData jbkdhsplbcx(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "accessPartnerId");
		IDataUtil.chkParam(input, "pageNum");
		IDataUtil.chkParam(input, "pageSize");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00162");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00163【金币可兑换商品详情查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData jbkdhspxqcx(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "accessPartnerId");
		IDataUtil.chkParam(input, "skuWareCode");
		String skuWareCode = input.getString("skuWareCode");
		input.put("skuWareCode", Integer.parseInt(skuWareCode));
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00163");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00164【金币可兑换商品关键词搜索】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData jbkdhspgjcss(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "accessPartnerId");
		IDataUtil.chkParam(input, "pageNumber");
		IDataUtil.chkParam(input, "pageSize");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00164");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00165【用户权益订单查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData yhqyddcx(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "number");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00165");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00166【权益订单详情查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData qyddxqc(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "orderNum");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00166");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00167【可订购权益产品查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData kdgqycpcx(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "number");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00167");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00168【权益产品详情查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData qycpxqcx(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "bpId");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00168");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
	 * CIP00169【权益订单状态查询】接口
	 * @param Idata
	 * @return
	 * @throws Exception
	 */
	public IData qyddztcx(IData input) throws Exception{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
		result.put("X_RESULTINFO", "success");
		
		IDataUtil.chkParam(input, "orderId");
		//调用能开接口
		IData retData = callAbility5GQYCIP(input,"crm.ABILITY.CIP00169");
		
		if (IDataUtil.isNotEmpty(retData)) {
            String resCode = retData.getString("resCode");
            String resMsg = retData.getString("resMsg");
            IData out = retData.getData("result");
            String bizCode = out.getString("bizCode");
            String bizDesc = out.getString("bizDesc");
            String messageId = retData.getString("messageId");
            if ("00000".equals(resCode)) {
                if (!"0000".equals(bizCode)) {
                    result.put("X_RESULTCODE", bizCode);
                    result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
                    result.put("messageId", messageId);
                    return result;
                } else {
                	 // 调用成功 
                	result.put("RESULT", out);
                	result.put("X_RESULTCODE", "0000");
            		result.put("X_RESULTINFO", "success");
            		result.put("messageId", messageId);
                }
            } else {
            	result.put("messageId", messageId);
                result.put("X_RESULTCODE", resCode);
                result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                return result;
            }
        } 
		return result;
	}
	
	/**
     * 用能力开放平台接口
     * by wangsc10
     * @param input
     */
    private static IData callAbility5GQYCIP(IData abilityData, String paramName)  throws Exception {
        IData retData = new DataMap();
        String Abilityurl = "";
        IData param1 = new DataMap();
        param1.put("PARAM_NAME", paramName);
        StringBuilder getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ");
        IDataset Abilityurls;
		Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
		if (Abilityurls != null && Abilityurls.size() > 0) {
            Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
        } else {
            CSAppException.appError("-1", paramName+"接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = Abilityurl;
        retData = AbilityEncrypting.callAbilityPlatCommon(apiAddress, abilityData);
		return retData;
        
    }
}