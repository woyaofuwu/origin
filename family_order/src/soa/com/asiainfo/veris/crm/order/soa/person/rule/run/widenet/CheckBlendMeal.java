
package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
/**
 * 校验取消融合套餐
 * 
 * @author Administrator
 */
public class CheckBlendMeal extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
    	
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag)){// 0查询时校验，1提交校验
        	
        	String existWid = "0";// 0没宽带  1有完工宽带（排除校园宽带）  2有未完工宽带
        	String serialNumber = databus.getString("SERIAL_NUMBER");
        	String userId = databus.getString("USER_ID");
        	//begin 1.判断用户是否已经存在宽带（非校园宽带）
        	IData widenetInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
        	if(IDataUtil.isNotEmpty(widenetInfo)){
        		//查询是否是校园宽带
        		IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber);
        		if (IDataUtil.isNotEmpty(widenetInfos)){
        			String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
        			//4是校园宽带
        			if(!"4".equals(wideType)){
        				//存在非校园宽带才拦截ban
        				existWid = "1";
        			}else{
        				//校园宽带不走规则
        				return false;
        			}
        		}
        		
        	} 
        	//end 1.判断用户是否已经存在宽带（非校园宽带）
        	
        	//bigin 2.判断新增的是不是融合套餐，如果是不走规则
        	IDataset listDiscntTrade = databus.getDataset("TF_B_TRADE_DISCNT");
        	for (int i = 0, size = listDiscntTrade.size(); i < size; i++)
            {
                IData element = listDiscntTrade.getData(i);
                //获取优惠编码
                String elementId = element.getString("DISCNT_CODE");
                String modifyTag = element.getString("MODIFY_TAG");
                if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && element.getString("USER_ID").equals(userId)){
                	IDataset commset = RouteInfoQry.getCommparaByCode("CSM", "368", elementId, "ZZZZ");
                	//如果add的是融合套餐，则不拦截flag=true
                	if(IDataUtil.isNotEmpty(commset) && commset.size()>0){
                		return false;
                    }
                }
                	
            }
        	//end 2.判断新增的是不是融合套餐，如果是不走规则
        	if("1".equals(existWid)){//存在完工的非校园宽带
        		
        		//begin 3.用户存在完工的非校园宽带并且没有宽带1+也没有宽带包年活动也没有度假宽带活动
            	//判断用户是否办理宽带1+活动
            	IDataset widenewtInfo1 = SaleActiveInfoQry.getUserSaleActiveByProductId(userId,"69908001");
            	if(IDataUtil.isNotEmpty(widenewtInfo1)&&widenewtInfo1.size()>0){
            		//用户有办理宽带1+活动,不走规则
            		return false;
            	}
            	//判断用户是否办理包年活动
            	IDataset widenewtInfo2 = SaleActiveInfoQry.getUserSaleActiveByProductId(userId,"67220428");
            	if(IDataUtil.isNotEmpty(widenewtInfo2)&&widenewtInfo2.size()>0){
            		//用户有办理包年活动,不走规则
            		return false;
            	}
            	//判断用户是否有度假宽带活动,度假宽带月、季、半年套餐（海南）
            	IDataset widenewtInfo3 = SaleActiveInfoQry.getUserSaleActiveByProductId(userId,"66002202");
            	if(IDataUtil.isNotEmpty(widenewtInfo3)&&widenewtInfo3.size()>0){
            		//用户有度假宽带活动,不走规则
            		return false;
            	}
            	//判断用户是否有度假宽带活动,赠送60元手机报停专项款（度假宽带保有专用）
            	IDataset widenewtInfo4 = SaleActiveInfoQry.getUserSaleActiveByProductId(userId,"66000279");
            	if(IDataUtil.isNotEmpty(widenewtInfo4)&&widenewtInfo4.size()>0){
            		//用户有度假宽带活动,不走规则
            		return false;
            	}
            	//判断用户是否有度假宽带活动,度假宽带2019
            	IDataset widenewtInfo5 = SaleActiveInfoQry.getUserSaleActiveByProductId(userId,"66004809");
            	if(IDataUtil.isNotEmpty(widenewtInfo5)&&widenewtInfo5.size()>0){
            		//用户有度假宽带活动,不走规则
            		return false;
            	}
            	//end 3.用户存在完工的非校园宽带并且没有宽带1+也没有宽带包年活动也没有度假宽带活动
            	//begin 4.判断删除的是否为融合套餐，是的话拦截
            	if (IDataUtil.isNotEmpty(listDiscntTrade)) {
	           		for (int i = 0, size = listDiscntTrade.size(); i < size; i++){
                        IData element = listDiscntTrade.getData(i);
                        //获取优惠编码
                        String elementId = element.getString("DISCNT_CODE");
                        String modifyTag = element.getString("MODIFY_TAG");
                        //融合套餐查询
                        if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && element.getString("USER_ID").equals(userId)){
                        	IDataset commset = RouteInfoQry.getCommparaByCode("CSM", "368", elementId, "ZZZZ");
                        	//删除的是融合套餐
                        	if(IDataUtil.isNotEmpty(commset) && commset.size()>0){
                        		return true;
                            }
                            	 
                        }
	                }
                         	
                 } 
            	//end 4.判断删除的是否为融合套餐，是的话拦截
            	 
        	}else{//判断是否有新装未完工并没有撤单的宽带
        		
        		//begin 5.判断是否有未完工宽带，没有的话不走规则
            	IData data = new DataMap();
            	data.put("SERIAL_NUMBER", "KD_"+serialNumber);
            	data.put("TRADE_TYPE_CODE", "600");
            	//isNot不为空的话返回false
            	boolean isNot = AcctInfoQry.getPayrelaAdvFlag(data);
            	if(isNot == false){
            		//false说明有未完工的
            		existWid = "2";
            	}else{
            		//没有未完工的宽带，不走规则
            		return false;
            	}
            	//end 5.判断是否有未完工宽带，没有的话不走规则
            	if("2".equals(existWid)){//存在未完工的宽带
            		//begin 6.判断是否存在预受理的宽带1+或宽带包年活动或度假宽带活动
                    IDataset data1 = SaleActiveInfoQry.getUserBookSaleActive(userId,"69908001","1");
            		if(IDataUtil.isNotEmpty(data1)&&data1.size()>0){
                		//用户有预受理的宽带1+活动
                		return false;
                	}
            		
            		IDataset data2 = SaleActiveInfoQry.getUserBookSaleActive(userId,"67220428","1");
                    if(IDataUtil.isNotEmpty(data2)&&data2.size()>0){
                		//用户有预受理的宽带包年活动
                		return false;
                	}
                    //预受理度假宽带
                    IDataset data3 = SaleActiveInfoQry.getUserBookSaleActive(userId,"66002202","1");
                    if(IDataUtil.isNotEmpty(data3)&&data3.size()>0){
                		//用户有预受理的度假宽带月、季、半年套餐（海南）,不走规则
                		return false;
                	}
                    
                    IDataset data4 = SaleActiveInfoQry.getUserBookSaleActive(userId,"66000279","1");
                    if(IDataUtil.isNotEmpty(data4)&&data4.size()>0){
                		//用户有预受理的赠送60元手机报停专项款（度假宽带保有专用）,不走规则
                		return false;
                	}
                    
                    IDataset data5 = SaleActiveInfoQry.getUserBookSaleActive(userId,"66004809","1");
                    if(IDataUtil.isNotEmpty(data5)&&data5.size()>0){
                		//用户有预受理的度假宽带2019,不走规则
                		return false;
                	}
                    //end 6.判断是否存在预受理的宽带1+或宽带包年活动或度假宽带活动
                    
                  //begin 7.判断删除的是否为融合套餐，是的话拦截
                	if (IDataUtil.isNotEmpty(listDiscntTrade)) {
    	           		for (int i = 0, size = listDiscntTrade.size(); i < size; i++){
                            IData element = listDiscntTrade.getData(i);
                            //获取优惠编码
                            String elementId = element.getString("DISCNT_CODE");
                            String modifyTag = element.getString("MODIFY_TAG");
                            //融合套餐查询
                            if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && element.getString("USER_ID").equals(userId)){
                            	IDataset commset = RouteInfoQry.getCommparaByCode("CSM", "368", elementId, "ZZZZ");
                            	//删除的是融合套餐
                            	if(IDataUtil.isNotEmpty(commset) && commset.size()>0){
                            		return true;
                                }
                                	 
                            }
    	                }
                             	
                     } 
                	//end 7.判断删除的是否为融合套餐，是的话拦截
            	}
        	}
        	
       }
        
        return false;
    }
    

}
