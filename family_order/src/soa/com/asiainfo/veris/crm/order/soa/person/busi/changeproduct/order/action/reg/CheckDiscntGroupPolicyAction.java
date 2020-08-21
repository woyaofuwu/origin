package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserCommUtil;

/**
 * REQ201812250005关于开发集团客户一企一策一码的需求
 * @Description 判断是否可以办理折扣套餐
 * @author zhangxing3
 */
public class CheckDiscntGroupPolicyAction implements ITradeAction {

    @Override
    public void executeAction(BusiTradeData btd) throws Exception {
        UcaData uca = btd.getRD().getUca();
        List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐
        IDataset configs = CommparaInfoQry.getCommparaByParaAttr("CSM", "7019", CSBizBean.getTradeEparchyCode());
        String serialNumber = uca.getSerialNumber();
        //System.out.println("-----------CheckDiscntGroupPolicyAction----------serialNumber:"+serialNumber);

        if (discntTrades != null && discntTrades.size() > 0) {
            for (int i =0 ; i < discntTrades.size();i++) {
	        	DiscntTradeData DiscntTrade = discntTrades.get(i).clone();
                if (BofConst.MODIFY_TAG_ADD.equals(DiscntTrade.getModifyTag())) {
                    String discntCode = DiscntTrade.getDiscntCode();
                    if(!"".equals(serialNumber) && isInCommparaParamCodeConfigs(discntCode, configs))
                    {
                        //System.out.println("-----------CheckDiscntGroupPolicyAction----------discntCode:"+discntCode);
                    	IDataset ids = qryGroupPolicy(serialNumber,discntCode);
                        //System.out.println("-----------CheckDiscntGroupPolicyAction----------ids:"+ids);
                        //1.根据号码和折扣套餐编码校验在TF_F_GROUPPOLICY是否存在有效的记录，没有则提示不能办理；
                    	if(IDataUtil.isEmpty(ids))
                        {
                    		CSAppException.apperr(ElementException.CRM_ELEMENT_310,"您不符合折扣套餐策略,不能办理折扣套餐:"+discntCode+"!!!");
                        }
                    	//2.如果用户办理了折扣套餐，需要取消工作手机折扣套餐，月底结束；
                    	else
                    	{
                    		IDataset ids1 = qryJobMobileDiscnt(uca.getUserId());
        			        //System.out.println("==============CheckDiscntGroupPolicyAction==========ids1:"+ids1);

                    		if(IDataUtil.isNotEmpty(ids1))
                            {
                    			String discntCodeJob = ids1.getData(0).getString("DISCNT_CODE", "");
                        		List<DiscntTradeData> discntData = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(discntCodeJob);
            			        //System.out.println("==============CheckDiscntGroupPolicyAction==========discntData:"+discntData);
            			        if (ArrayUtil.isNotEmpty(discntData)){
            			        	for (int j =0; j < discntData.size(); j++){
            				        	DiscntTradeData delDiscntTD = discntData.get(j).clone();
            			
            				        	delDiscntTD.setEndDate(SysDateMgr.getLastDateThisMonth());
            				        	delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            				        	delDiscntTD.setRemark("产品变更：用户办理折扣套餐，取消工作手机折扣套餐");
            				        	//System.out.println("==============CheckDiscntGroupPolicyAction==========delDiscntTD:"+delDiscntTD);
            				            btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTD);
            			        	}
            			            
            			        }
                            }
                    	}
                    }
                }
            }
        }
        
        //3.当取消大市场产品时，对应的折扣套餐也要取消；
        List<ProductTradeData> productTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);//获取产品子台帐
        String newProductId = "";
        String oldProductId = "";
        if (productTrades != null && productTrades.size() > 0) {//说明做了主产品的变更            
            for (ProductTradeData productTrade : productTrades) {
                if (BofConst.MODIFY_TAG_DEL.equals(productTrade.getModifyTag())) {
                    oldProductId = productTrade.getProductId();
                    IDataset ids2 = qryGroupPolicyBySNProd(serialNumber,oldProductId);
                    if(IDataUtil.isNotEmpty(ids2))
                    {
                		String discntCode = ids2.getData(0).getString("DISCOUNT_OFFER_ID", "");
                		List<DiscntTradeData> discntData = btd.getRD().getUca().getUserDiscntsByDiscntCodeArray(discntCode);
    			        //System.out.println("==============CheckDiscntGroupPolicyAction==========discntData:"+discntData);
    			        if (ArrayUtil.isNotEmpty(discntData)){
    			        	for (int j =0; j < discntData.size(); j++){
    				        	DiscntTradeData delDiscntTD = discntData.get(j).clone();
    			
    				        	delDiscntTD.setEndDate(SysDateMgr.getLastDateThisMonth());
    				        	delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
    				        	delDiscntTD.setRemark("产品变更：取消大市场产品时，对应的折扣套餐也要取消！");
    				        	//System.out.println("==============CheckDiscntGroupPolicyAction==========delDiscntTD:"+delDiscntTD);
    				            btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTD);
    			        	}
    			            
    			        }
                    }
                    
                }
            }
        }
        
    }
    
    private static IDataset qryJobMobileDiscnt(String userId) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", userId);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select  s.discnt_code, t.* from tf_f_user_offer_rel t,tf_f_user_discnt s ");
        parser.addSQL(" where t.user_id= :USER_ID and t.offer_code = '601301' and t.end_date > sysdate ");
        parser.addSQL(" and t.user_id=s.user_id  and t.partition_id=s.partition_id  and s.end_date >sysdate ");
        parser.addSQL(" and s.inst_id=t.rel_offer_ins_id ");
      
        IDataset out = UserCommUtil.qryByParse(parser,Route.CONN_CRM_CG);
        return out;
    }
    
    private static IDataset qryGroupPolicyBySNProd(String serialNumber,String productId) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("MAIN_PRODUCT", productId);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select t.* from TF_F_GROUPPOLICY t ");
        parser.addSQL(" where t.serial_number = :SERIAL_NUMBER ");
        parser.addSQL(" and t.MAIN_PRODUCT_ID = :MAIN_PRODUCT ");
        parser.addSQL(" and t.remove_tag='0' ");
      
        IDataset out = UserCommUtil.qryByParse(parser,Route.CONN_CRM_CG);
        return out;
    }
    
    private static IDataset qryGroupPolicy(String serialNumber,String discntCode) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("DISCNT_CODE", discntCode);

        SQLParser parser = new SQLParser(data);
        parser.addSQL(" select t.* from TF_F_GROUPPOLICY t ");
        parser.addSQL(" where t.serial_number = :SERIAL_NUMBER ");
        parser.addSQL(" and t.discount_offer_id = :DISCNT_CODE ");
        parser.addSQL(" and t.remove_tag='0' ");
      
        IDataset out = UserCommUtil.qryByParse(parser,Route.CONN_CRM_CG);
        return out;
    }
    private static boolean isInCommparaParamCodeConfigs(String objId, IDataset configs)throws Exception
    {
        if(IDataUtil.isEmpty(configs))
        {
            return false;
        }
        
        int size = configs.size();
        for (int i = 0; i < size; i++)
        {
            IData config = configs.getData(i);
            String paramCode = config.getString("PARAM_CODE");
            if (objId.equals(paramCode))
            {
                return true;
            }
        }
        return false;
    }
}
