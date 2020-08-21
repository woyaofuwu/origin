package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.util.SaleActiveBreUtil;

public class CheckWidenetProductRule extends BreBase implements IBREScript
{

	private static transient Logger logger = Logger.getLogger(CheckWidenetProductRule.class);
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
    	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx38 " + param);
        // TODO Auto-generated method stub
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        String kdUserId = databus.getString("USER_ID");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            String serialNumber = databus.getString("SERIAL_NUMBER");
            
            String strBrandCode = "";
            String userId = null;
            
            IDataset listTradeUser = databus.getDataset("TF_F_USER");
            if(IDataUtil.isNotEmpty(listTradeUser)){
            	strBrandCode = listTradeUser.getData(0).getString("RSRV_STR10","");
            }
            if("BNBD".equals(strBrandCode)){//商务宽带
            	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
                userId = userInfo.getString("USER_ID");
                
            } else {
            	
	            if (serialNumber.substring(0, 3).equals("KD_"))
	            {
	                serialNumber = serialNumber.substring(3);
	            }	            
	            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
                userId = userInfo.getString("USER_ID");
            }
            
        	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxxsaleActiveInfos3_70 " + serialNumber);
            String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx69 " + tradeTypeCode);

            IData reqData = databus.getData("REQDATA");// 请求的数据
        	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx72 " + reqData);
            String productId = reqData.getString("NEW_PRODUCT_ID");
            if (StringUtils.isBlank(productId))
            {
                UcaData ucaData = (UcaData) databus.get("UCADATA");
                productId = ucaData.getProductId();
            }
            IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "173", tradeTypeCode, CSBizBean.getTradeEparchyCode());
        	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx81 " + commparaInfos);
            if (IDataUtil.isNotEmpty(commparaInfos))
            {
                IDataset cparamInfos = CParamQry.getLimitActives(userId, tradeTypeCode);
            	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx86 " + cparamInfos);
                	
                if (IDataUtil.isNotEmpty(cparamInfos))
                {
                	
                	//根据新规则，升档可以随意变更，不判断最后一个月限制，降档需要判断
                	if(StringUtils.equals("1", cparamInfos.getData(0).getString("FLAG", "1"))){  //该活动如果是最后一个月不做拦截。
                	
                		boolean falg = true ;
                        String updown_tag = databus.getString("CHANGE_UP_DOWN_TAG","");//降档，升档标志，1=升档，2=降档，0=不变档，3=产品变了，速率不变
                        String specialSaleFlag = databus.getString("SPECIAL_SALE_FLAG","");

                		if(("2".equals(updown_tag) || "3".equals(updown_tag)) || "0".equals(updown_tag))
                		{
                			String bookDate = databus.getString("BOOKING_DATE","");
                			String endDate = cparamInfos.getData(0).getString("END_DATE", ""); //结束时间
                    		if(bookDate != null && !"".equals(bookDate))
                    		{
                    			if(SysDateMgr.compareTo(bookDate, endDate) > 0)
                    			{
                    				falg = false ;
                    			}
                    		}
                    		
                    		if("1".equals(specialSaleFlag))
                    		{
                    			falg = false ;
                    		}
                		}
                		else if("1".equals(updown_tag))
                		{
                			falg = false ;
                		}
                    	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx119 " + falg);
                		
                		if(falg)
                		{
                			// 查询看用户的产品是不是不受限制
                    		IDataset commProductInfos = CommparaInfoQry.getCommparaProductInfoByCode(kdUserId, tradeTypeCode);
                        	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx126 " + commProductInfos);
                    		if (IDataUtil.isEmpty(commProductInfos))
                        	{
                    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604021", "该用户办理了[" + cparamInfos.getData(0).getString("PRODUCT_NAME", "") + "]活动,不能办理该业务!");

                        	}
                    		else
                    		{
                    			commProductInfos = CommparaInfoQry.getCommpara("CSM", "175", productId, CSBizBean.getTradeEparchyCode());
                            	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx136 " + commProductInfos);
                    			if (IDataUtil.isEmpty(commProductInfos))
                    			{
                    				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604022", "宽带1+用户只能变更为4M提8M套餐！");
                    			}
                    		}
                		}
                	}
                }
            }
        	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx156 " + databus.getString("OPEN_DATE"));
            String openDate = databus.getString("OPEN_DATE").substring(0, 7);
            String nowDate = SysDateMgr.getSysTime().substring(0, 7);
            if (openDate.equals(nowDate) && !"631".equals(tradeTypeCode))
            {// 校园宽带不需要
                // 开户当月默认不给产品变更，除非是设置的员工优惠套餐
                boolean isStaffDiscntFlg = false;
                String typeId = "";
                String selElements = reqData.getString("SELECTED_ELEMENTS");
                String widetype=reqData.getString("WIDE_TYPE","1");//宽带类型，这里要用宽带类型判断
                if (tradeTypeCode.equals("601") && widetype.equals("1"))
                { // GPON的员工优惠套餐
                    typeId = "GPON_STAFF_DISCNT";
                }
                else if (tradeTypeCode.equals("601") && widetype.equals("2") )
                { // ADSL的员工优惠套餐
                    typeId = "ADSL_STAFF_DISCNT";
                }
                else if (tradeTypeCode.equals("601") && (widetype.equals("3") || widetype.equals("5")))
                { // FTTH的员工优惠套餐
                    typeId = "FTTH_STAFF_DISCNT";
                }
                else
                {
                    typeId = "GPON_STAFF_DISCNT";
                }
            	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx172 " + typeId);
                IDataset staffDiscntData = StaticInfoQry.getStaticValueByTypeId(typeId);
            	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx175 " + staffDiscntData);
            	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx176 " + selElements);
                if (StringUtils.isNotBlank(selElements) && IDataUtil.isNotEmpty(staffDiscntData))
                {
                    IDataset selectSet = new DatasetList(selElements);
                    for (int i = 0; i < selectSet.size(); i++)
                    {
                        IData element = selectSet.getData(i);
                    	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx184 " + element);
                        if (element.getString("MODIFY_TAG").equals("0") && element.getString("ELEMENT_TYPE_CODE").equals("D"))
                        {
                            boolean blnFlg = false;
                            String discntCode = element.getString("ELEMENT_ID");
                            for (int j = 0; j < staffDiscntData.size(); j++)
                            {
                                if (discntCode.equals(staffDiscntData.getData(j).getString("DATA_ID")))
                                {
                                    blnFlg = true;
                                    break;
                                }
                            }
                            if (blnFlg)
                            {
                                isStaffDiscntFlg = true;
                            }
                            else
                            {
                                // 只要有一个新增的优惠不是员工优惠套餐就不给办理
                                isStaffDiscntFlg = false;
                                break;
                            }
                        }
                    }
                }

                if (!isStaffDiscntFlg)
                {
                    IDataset saleActiveInfos1 = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
                	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx230 " + saleActiveInfos1);
                    if (IDataUtil.isNotEmpty(saleActiveInfos1))
                    {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604023", "开户当月不能办理非员工套餐的产品变更！");
                }
            }
            }

            /***
             * 
             * 宽带用户含有某些优惠则不能办理宽带产品变更，续约优惠动作除外
             *
             ***/
            
            //1、获取宽带用户end_date > sysdate的优惠信息
            IDataset kdUserDiscntInfos = UserDiscntInfoQry.getSpecDiscnt(kdUserId);
        	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx227 " + kdUserDiscntInfos);
            //2、循环用户的优惠
            for (int i = 0; i < kdUserDiscntInfos.size(); i++)
            {
            	IData kdUserDiscntInfo = kdUserDiscntInfos.getData(i);
            	String discntCode = kdUserDiscntInfo.getString("DISCNT_CODE");
            	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx234 " + kdUserDiscntInfo);
            	
            	//3、查看该优惠是否存在不允许的有业务类型
            	IDataset commpara525Infos = CommparaInfoQry.getCommparaInfoByCode("CSM", "525", tradeTypeCode, discntCode, CSBizBean.getTradeEparchyCode());
            	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx238 " + commpara525Infos);
            	
            	//4、如果存在配置，即用户含有end_date>sysdate的优惠，且属于不允许的业务类型时
            	if (IDataUtil.isNotEmpty(commpara525Infos))
                {
            		//5、判断用户是否可以变更
            		String canChangeTag = commpara525Infos.getData(0).getString("PARA_CODE2","");//获取该优惠是否是可以取消的配置
                    boolean haveRight = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CANCEL_YEAR_WIDENET");//操作员是否含有可以取消优惠的权限
                    //如果操作员含有变更优惠的权限，且该优惠配置了可以变更，则允许变更，不做拦截
                    if(haveRight && StringUtils.isNotBlank(canChangeTag) && "1".equals(canChangeTag))
                    {
                    	continue;
                    }
                    
                    //如果优惠时间是最后一个月，则允许变更，不做拦截
                    String canChangeMonthTag = commpara525Infos.getData(0).getString("PARA_CODE3","0");//获取该优惠结束前多久可以变更
                    String sysDate = SysDateMgr.getSysTime();
                    String endDate = kdUserDiscntInfo.getString("END_DATE");//优惠结束时间
                    int intervalMoths = SaleActiveBreUtil.getIntervalMoths(sysDate, endDate);
                    if(intervalMoths <= Integer.parseInt(canChangeMonthTag))
                    {
                    	continue;
                    }
                    
            		//如果用户没有可变更权限，也不在结束前允许变更的月份内，则继续判断是否存在换产品、换服务或换优惠、取消服务或取消优惠、修改服务或修改优惠等情况，除了能续约优惠自身，其他操作不被允许
            		//6、获取元素变更信息
            		IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
                    if (IDataUtil.isNotEmpty(selectedElements))
                    {
                    	//7、循环变更的元素
                    	for (int j = 0, size = selectedElements.size(); j < size; j++)
                        {
                            IData element = selectedElements.getData(j);

                            String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                            String elementId = element.getString("ELEMENT_ID");
                            String modifyTag = element.getString("MODIFY_TAG");
                        	logger.error("CheckWidenetProductRulexxxxxxxxxxxxxxxxxxx275 " + element);
                            //8、如果该优惠是新增、优惠元素类型、且是续约自身，则允许
                            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && discntCode.equals(elementId))
                            {
                            	continue;
                            }
                            else//9、如果是其他操作，如修改、删除等，则拦截
                            {
                            	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604024", "用户套餐不允许办理宽带产品变更！或您无取消操作权限！");
                            	return true;
                            }
                        }
                    }
                }
            }
            
            /***
             * 
             * 宽带续约的规则
             * 判断续约是否是提前两个月内，和是否已经续约
             * 只影响C类型元素续约自身时
             *
             ***/
            
            //1、获取元素的变动数据
            IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
            if (IDataUtil.isNotEmpty(selectedElements))
            {
            	//2、循环变更的元素
            	for (int j = 0, size = selectedElements.size(); j < size; j++)
                {
                    IData element = selectedElements.getData(j);

                    String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                    String elementId = element.getString("ELEMENT_ID");
                    String modifyTag = element.getString("MODIFY_TAG");
//                    String orderMode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "ORDER_MODE", elementId);
//                    String rsrvStr3 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "RSRV_STR3", elementId);//可提前续约的月份配置
                    
                    if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
                    {
                        String orderMode = "";
                        
                        //可提前续约的月份配置
                        String rsrvStr3 = "";
                        IDataset offerExtInfo = UpcCall.qryOfferExtChaByOfferId(elementId, BofConst.ELEMENT_TYPE_CODE_DISCNT, "TD_B_DISCNT");
                        
                        if (IDataUtil.isNotEmpty(offerExtInfo))
                        {
                            orderMode = offerExtInfo.getData(0).getString("ORDER_MODE");
                            rsrvStr3 = offerExtInfo.getData(0).getString("RSRV_STR3");
                        }
                        
                      //3、如果是C类型（可续约）的元素、是新增操作、是优惠元素，则进入，除此之外的不进该逻辑，将影响减低到最小
                        if ( "C".equals(orderMode))
                        {
                            //4、如果用户含有未生效的该优惠，代表用户已做过产品变更，或已续约过，则不给变更
                            IDataset kdUserDiscntCount = UserDiscntInfoQry.getFutureUserDiscnt(kdUserId, elementId);
                            if(IDataUtil.isNotEmpty(kdUserDiscntCount))
                            {
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604025", "用户含有未生效的优惠，已续约过不能再办理！");
                                return true;
                            }
                            
                            //5、这里是为了区别出是[月套餐换为年套餐]，还是[年套餐续约]
                            IDataset kdUserVaildDiscntInfos = UserDiscntInfoQry.getNowVaildByUserIdDiscnt(kdUserId, elementId);
                            //6、如果用户自身就含有有效的该优惠，代表是续约
                            if(IDataUtil.isNotEmpty(kdUserVaildDiscntInfos))
                            {
                                if(StringUtils.isBlank(rsrvStr3))
                                {
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604026", "未配置可提前续约的月份值！");
                                    return true;
                                }
                                
                                //7、判断优惠是否是结束前[rsrvStr3]个月内，只允许在规定的时间内续约
                                String sysTime = SysDateMgr.getSysTime();
                                String endDate = kdUserVaildDiscntInfos.getData(0).getString("END_DATE");//已有优惠结束时间
                                //8、计算出当前时间与优惠结束时间的月份差，相同月份差值为1
                                int intervalMoths = SaleActiveBreUtil.getIntervalMoths(sysTime, endDate);

                                //9、如果当前时间在优惠结束时间前超过配置月份，则不允许办理
                                if (intervalMoths > Integer.parseInt(rsrvStr3))
                                {
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604027", "只能在结束前" + rsrvStr3 + "个月内办理续约！");
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            
            /**
             * REQ201607050007 关于移动电视尝鲜活动的需求
             * 办理了魔百和要求的那几种活动，则不允许做产品变更降档操作。
             * chenxy3 20160719
             * */
            String updown_tag = databus.getString("CHANGE_UP_DOWN_TAG","");//降档，升档标志，1=升档，2=降档，0=不变档，3=产品变了，速率不变
            if("2".equals(updown_tag)){
            	IDataset saleActives=CommparaInfoQry.getCommByParaAttr("CSM", "9191", "0898");
            	for(int k=0;k<saleActives.size();k++){
            		IData activeData=saleActives.getData(k);
            		String prodId=activeData.getString("PARAM_CODE","");
            		String packId=activeData.getString("PARA_CODE1","");
            		String allowProdIds=activeData.getString("PARA_CODE23","");//允许变更的产品ID 
            		StringTokenizer st=new StringTokenizer(allowProdIds,"|"); 
            		String allow="false";
    				while(st.hasMoreElements()){ 
    					String allowProdId=st.nextToken();
    					if(productId.equals(allowProdId)){ //当前变更的新宽带产品
    						allow="true";
    						break;
    					}
    				}
    				if("false".equals(allow)){
	            		IDataset userSaleActives = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId,  prodId, packId);
	            		//IDataset userSaleActives = databus.getDataset("TF_F_USER_SALE_ACTIVE");
	            		for(int j=0; j<userSaleActives.size(); j++){
	            			String userProdId=userSaleActives.getData(j).getString("PRODUCT_ID","");
	            			String userPackId=userSaleActives.getData(j).getString("PACKAGE_ID","");
	            			String userPackName=userSaleActives.getData(j).getString("PACKAGE_NAME","");
	            			if(prodId.equals(userProdId) && packId.equals(userPackId)){
	            				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604028", "用户存在营销活动【"+userPackName+"】，不允许对宽带做降档操作！");
	            				return true;
	            			}
	            		}
	            	}
            	}
            }
        }
        else if (StringUtils.equals("0", xChoiceTag))
        {
        	String wideUserId = databus.getString("USER_ID");
        	
        	IDataset userDiscntElements = UserDiscntInfoQry.queryUserDiscntsInSelectedElements(wideUserId);
        	
        	// 查询改优惠配置
			IDataset discntCommparas = CommparaInfoQry.getCommparaAllColByParser("CSM", "532", "MONTH_DISCNT","0898");

			if (IDataUtil.isNotEmpty(userDiscntElements) && IDataUtil.isNotEmpty(discntCommparas))
			{
				IData userDiscntElement = null;
				IData discntCommpara = null;
				
				for (int i = 0; i < userDiscntElements.size(); i++)
				{
					userDiscntElement = userDiscntElements.getData(i);
					
					for (int j = 0; j < discntCommparas.size(); j++)
					{
						discntCommpara = discntCommparas.getData(j);
						
						if (userDiscntElement.getString("ELEMENT_ID","").equals(discntCommpara.getString("PARA_CODE1")))
						{
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201806011", "用户订购了宽带包月套餐的不能办理产品变更业务！");
            				return true;
						}
					}
				}
			}
        	
        }

        return false;
    }

}
