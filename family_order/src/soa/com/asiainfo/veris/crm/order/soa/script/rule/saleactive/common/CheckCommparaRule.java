package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveCommparaBreConst;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * 营销活动TD_S_COMMPARA表配置规则校验
 * 
 * @author songlm
 */
public class CheckCommparaRule extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 8687825166597590097L;
    private static final Logger log = Logger.getLogger(CheckCommparaRule.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckCommparaRule() >>>>>>>>>>>>>>>>>>");
        }

        String paramCode = SaleActiveCommparaBreConst.PARAM_CODE_VALUE;
        String productId = databus.getString("PRODUCT_ID");//获取当前预受理的产品ID
        String packageId = databus.getString("PACKAGE_ID");//获取当前预受理的包ID

        //获取commpara中
        //PARAM_CODE=SALEACTIVE
        //且PARA_CODE1=PRODUCT_ID或(PARA_CODE1=-1(或空)且PARA_CODE2=PACKAGE_ID)
        //且PARA_CODE2=PACKAGE_ID或(PARA_CODE2=-1(或空)且PARA_CODE1=PRODUCT_ID)
        //的规则
        IDataset commparaRuleForSaleactiveSet = CommparaInfoQry.getCommparaByParamcode("CSM", paramCode, productId, packageId);
        
        if (IDataUtil.isEmpty(commparaRuleForSaleactiveSet))
        {
        	return true;
        }

        //规则校验
        checkCommparaRule(databus, ruleParam, commparaRuleForSaleactiveSet);

        if (log.isDebugEnabled())
        {
            log.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckCommparaRule() >>>>>>>>>>>>>>>>>>");
        }
        return false;
    }
    
    /**
     * 规则校验
     * @author songlm
     */
    private boolean checkCommparaRule(IData databus, BreRuleParam ruleParam, IDataset commparaRuleForSaleactiveSet) throws Exception
    {
        boolean result = true;

        //重新构建规则数据，将IDataset转成以PARAM_ATTR为KEY，Data为VALUE的IData数据
        IData commparaRuleData = buildCommparaRuleData(commparaRuleForSaleactiveSet);

        if (log.isDebugEnabled())
        {
            log.debug("checkCommparaRule.....commparaRuleData............" + commparaRuleData);
        }
        
        //判断规则数据中的KEY主键，是否为包含判断的规则编码，如：9999，如果存在则进入规则进行判断
        if (commparaRuleData.containsKey(SaleActiveCommparaBreConst.PARAM_ATTR_RULE_9999))
        {
        	//测试
        }
        
        //用户办理了A或B或C活动（增加一个字段控制该活动目前是否生效），才可办理D活动
        if (commparaRuleData.containsKey(SaleActiveCommparaBreConst.PARAM_ATTR_RULE_670))
        {
        	result = checkCommparaRule670(databus, ruleParam, commparaRuleData.getDataset(SaleActiveCommparaBreConst.PARAM_ATTR_RULE_670));
        }
        
        //用户存在A活动下A1营销包（增加一个字段控制该活动目前是否生效），则不允许办理B活动下B1营销包
        if (commparaRuleData.containsKey(SaleActiveCommparaBreConst.PARAM_ATTR_RULE_671))
        {
        	result = checkCommparaRule671(databus, ruleParam, commparaRuleData.getDataset(SaleActiveCommparaBreConst.PARAM_ATTR_RULE_671));
        }
        
        //判断用户办理的活动（增加一个字段控制该活动目前是否生效）购买过某个机型的终端（通过机型编码判断，可配置多个机型编码，只要其中一个有效即可），才办理某个活动下某个营销包
        if (commparaRuleData.containsKey(SaleActiveCommparaBreConst.PARAM_ATTR_RULE_672))
        {
        	result = checkCommparaRule672(databus, ruleParam, commparaRuleData.getDataset(SaleActiveCommparaBreConst.PARAM_ATTR_RULE_672));
        }
        
        return result;
    }
    

    /**
     * 重新构建规则，将IDataset类型的规则转为以规则编码PARAM_ATTR为KEY的IData类型数据
     * @author songlm
     */
    private IData buildCommparaRuleData(IDataset commparaRuleForSaleactiveSet)
    {
        IData commparaRuleReturnData = new DataMap();
        for (int i = 0, size = commparaRuleForSaleactiveSet.size(); i < size; i++)
        {
            IData commparaRuleTempData = commparaRuleForSaleactiveSet.getData(i);
            String paramAttr = commparaRuleTempData.getString("PARAM_ATTR");

            IDataset tempDataset = null;
            if (commparaRuleReturnData.containsKey(paramAttr))
            {
                tempDataset = commparaRuleReturnData.getDataset(paramAttr);
                tempDataset.add(commparaRuleTempData);
            }
            else
            {
                tempDataset = new DatasetList();
                tempDataset.add(commparaRuleTempData);
            }
            commparaRuleReturnData.put(paramAttr, tempDataset);
        }

        return commparaRuleReturnData;
    }
    
    /**
     * 规则编码670
     * 依赖型规则
     * 1：用户办理了A或B或C活动，才可办理D活动
     * 2：用户办理了A活动下的A1包或B活动下的B1包或C活动下的C1包或任意活动下的XX包，才能办理D活动
     * 3：用户办理了A或B或C活动，才可办理D活动下的D1包或任意活动下的D1包
     * 4：用户办理了A活动下的A1包或B活动下的B1包或C活动下的C1包或任意活动下的XX包，才能办理D活动下的D1包或任意活动下的D1包
     * 5：判断依赖活动状态配置值，0：只要办理过不论是否失效  1：只判断有效的活动
     * 6：如果规则配置了机型编码，则继续判断用户办理活动下是否包含对应的机型
     * @author songlm
     * **/
    private boolean checkCommparaRule670(IData databus, BreRuleParam ruleParam, IDataset commparaRuleForSaleactiveData) throws Exception
    {
    	boolean isDepend = false;
    	String userId = databus.getString("USER_ID");
    	IDataset userSaleActivesALL = UserSaleActiveInfoQry.queryAllSaleActiveByUserId(userId);//用户所有的营销活动
    	IDataset userSaleActivesVALID = databus.getDataset("TF_F_USER_SALE_ACTIVE");//用户当前有效的营销活动
    	
    	//循环规则
    	for (int i = 0, size = commparaRuleForSaleactiveData.size(); i < size; i++)
        {
    		IDataset userSaleActives = null;
    		IData commparaRuleData = commparaRuleForSaleactiveData.getData(i);//获取当前规则信息
    		String paraCode3 = commparaRuleData.getString("PARA_CODE3","-1");//被依赖或互斥的PRODUCT_ID，若空则置为-1
    		String paraCode4 = commparaRuleData.getString("PARA_CODE4","-1");//被依赖或互斥的PACKAGE_ID，若空则置为-1
    		String paraCode5 = commparaRuleData.getString("PARA_CODE5","");//判断依赖或互斥活动状态配置值，0：只要办理过不论是否失效  1：只判断有效的活动
    		String paraCode6 = commparaRuleData.getString("PARA_CODE6","");//被依赖的机型编码DEVICE_MODEL_CODE

    		if("0".equals(paraCode5))//依赖或互斥的产品或包状态为不论是否失效
    		{
    			userSaleActives = userSaleActivesALL;
    		}
    		else if("1".equals(paraCode5))//依赖或互斥的产品或包状态为有效
    		{
    			userSaleActives = userSaleActivesVALID;
    		}
    		else//默认依赖或互斥的产品或包状态为有效
    		{
    			userSaleActives = userSaleActivesVALID;
    		}
    		
    		//循环用户的营销活动信息
    		for (int j = 0, s = userSaleActives.size(); j < s; j++)
            {
                IData userSaleActive = userSaleActives.getData(j);
                String saleProductId = userSaleActive.getString("PRODUCT_ID");//用户拥有的PRODUCT_ID
                String salePackageId = userSaleActive.getString("PACKAGE_ID");//用户拥有的PACKAGE_ID
                String relationTradeId = userSaleActive.getString("RELATION_TRADE_ID");
                
                //如果被依赖或互斥的paraCode4即包ID，没有配或配置为了-1，则只判断被依赖或互斥的paraCode3即PRODUCT_ID与用户拥有的PRODUCT_ID
                if (saleProductId.equals(paraCode3) && "-1".equals(paraCode4))
                {
                	if(StringUtils.isNotBlank(paraCode6))//如果PARA_CODE6配置了依赖的机型编码DEVICE_MODEL_CODE则进行判断
            		{
                		IDataset userSaleGoodsInfoSet = UserSaleGoodsInfoQry.querySaleGoodsInfo(userId, relationTradeId, paraCode6);
                    	if(IDataUtil.isNotEmpty(userSaleGoodsInfoSet))
                    	{
                    		isDepend = true;
                            break;
                    	}
            		}else{//如果没配PARA_CODE6则
            			isDepend = true;
                        break;
            		}
                }
                
                //如果被依赖或互斥的paraCode3即产品ID，没有配或配置为了-1，则只判断被依赖或互斥的paraCode4即PACKAGE_ID与用户拥有的PACKAGE_ID
                if (salePackageId.equals(paraCode4) && "-1".equals(paraCode3))
                {
                	if(StringUtils.isNotBlank(paraCode6))
            		{
                		IDataset userSaleGoodsInfoSet = UserSaleGoodsInfoQry.querySaleGoodsInfo(userId, relationTradeId, paraCode6);
                    	if(IDataUtil.isNotEmpty(userSaleGoodsInfoSet))
                    	{
                    		isDepend = true;
                            break;
                    	}
            		}else{
            			isDepend = true;
                        break;
            		}
                }
                
                //如果被依赖或互斥的paraCode3即产品ID和paraCode4即包ID都有配置，则与用户拥有的PRODUCT_ID、PACKAGE_ID都进行匹配判断
                if (saleProductId.equals(paraCode3) && salePackageId.equals(paraCode4))
                {
                	if(StringUtils.isNotBlank(paraCode6))
            		{
                		IDataset userSaleGoodsInfoSet = UserSaleGoodsInfoQry.querySaleGoodsInfo(userId, relationTradeId, paraCode6);
                    	if(IDataUtil.isNotEmpty(userSaleGoodsInfoSet))
                    	{
                    		isDepend = true;
                            break;
                    	}
            		}else{
            			isDepend = true;
                        break;
            		}
                }
            }
        }
    	
    	//如果没有匹配到存在依赖或互斥的营销活动，则提示错误信息，取compara中配置的第一条PARAM_NAME
    	if (!isDepend)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024100, commparaRuleForSaleactiveData.getData(0).getString("PARAM_NAME",""));
        }

        return true;
    }
    
    /**
     * 规则编码671
     * 互斥型规则
     * 1：用户存在A或B或C活动，不允许办理D活动
     * 2：用户存在A活动下的A1包或B活动下的B1包或C活动下的C1包或任意活动下的XX包，不允许办理D活动
     * 3：用户存在A或B或C活动，不允许办理D活动下的D1包或任意活动下的D1包
     * 4：用户存在A活动下的A1包或B活动下的B1包或C活动下的C1包或任意活动下的XX包，不允许办理D活动下的D1包或任意活动下的D1包
     * 5：判断依赖活动状态配置值，0：只要办理过不论是否失效  1：只判断有效的活动
     * @author songlm
     * **/
    private boolean checkCommparaRule671(IData databus, BreRuleParam ruleParam, IDataset commparaRuleForSaleactiveData) throws Exception
    {
    	
    	IDataset userSaleActivesALL = UserSaleActiveInfoQry.queryAllSaleActiveByUserId(databus.getString("USER_ID"));//用户所有的营销活动
    	IDataset userSaleActivesVALID = databus.getDataset("TF_F_USER_SALE_ACTIVE");//用户当前有效的营销活动
    	
    	//循环规则
    	for (int i = 0, size = commparaRuleForSaleactiveData.size(); i < size; i++)
        {
    		IDataset userSaleActives = null;
    		IData commparaRuleData = commparaRuleForSaleactiveData.getData(i);//获取当前规则信息
    		String paramName = commparaRuleData.getString("PARAM_NAME","");//错误提示信息
    		String paraCode3 = commparaRuleData.getString("PARA_CODE3","-1");//被依赖或互斥的PRODUCT_ID，若空则置为-1
    		String paraCode4 = commparaRuleData.getString("PARA_CODE4","-1");//被依赖或互斥的PACKAGE_ID，若空则置为-1
    		String paraCode5 = commparaRuleData.getString("PARA_CODE5","");//判断依赖或互斥活动状态配置值，0：只要办理过不论是否失效  1：只判断有效的活动

    		if("0".equals(paraCode5))//依赖或互斥的产品或包状态为不论是否失效
    		{
    			userSaleActives = userSaleActivesALL;
    		}
    		else if("1".equals(paraCode5))//依赖或互斥的产品或包状态为有效
    		{
    			userSaleActives = userSaleActivesVALID;
    		}
    		else//默认依赖或互斥的产品或包状态为有效
    		{
    			userSaleActives = userSaleActivesVALID;
    		}
    		
    		//循环用户的营销活动信息
    		for (int j = 0, s = userSaleActives.size(); j < s; j++)
            {
                IData userSaleActive = userSaleActives.getData(j);
                String saleProductId = userSaleActive.getString("PRODUCT_ID");//用户拥有的PRODUCT_ID
                String salePackageId = userSaleActive.getString("PACKAGE_ID");//用户拥有的PACKAGE_ID
                
                //如果被依赖或互斥的paraCode4即包ID，没有配或配置为了-1，则只判断被依赖或互斥的paraCode3即PRODUCT_ID与用户拥有的PRODUCT_ID
                if (saleProductId.equals(paraCode3) && "-1".equals(paraCode4))
                {
                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024101, paramName);
                    break;
                }
                
                //如果被依赖或互斥的paraCode3即产品ID，没有配或配置为了-1，则只判断被依赖或互斥的paraCode4即PACKAGE_ID与用户拥有的PACKAGE_ID
                if (salePackageId.equals(paraCode4) && "-1".equals(paraCode3))
                {
                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024101, paramName);
                    break;
                }
                
                //如果被依赖或互斥的paraCode3即产品ID和paraCode4即包ID都有配置，则与用户拥有的PRODUCT_ID、PACKAGE_ID都进行匹配判断
                if (saleProductId.equals(paraCode3) && salePackageId.equals(paraCode4))
                {
                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024101, paramName);
                    break;
                }
            }
        }

        return true;
    }
    
    
    /**
     * 规则编码672
     * 依赖型规则
     * 1：用户存在办理的A活动中购买过B机型的终端，才可办理C活动
     * 2：用户存在办理的A活动中购买过B机型的终端，才可办理C活动下的C1包或任意活动下的C1包
     * 3：判断依赖活动状态配置值，0：只要办理过不论是否失效  1：只判断有效的活动
     * @author songlm
     * **/
    private boolean checkCommparaRule672(IData databus, BreRuleParam ruleParam, IDataset commparaRuleForSaleactiveData) throws Exception
    {
    	boolean isDepend = false;
    	String userId = databus.getString("USER_ID");
    	
    	//循环规则
    	for (int i = 0, size = commparaRuleForSaleactiveData.size(); i < size; i++)
        {
    		IData commparaRuleData = commparaRuleForSaleactiveData.getData(i);//获取当前规则信息
    		String paraCode3 = commparaRuleData.getString("PARA_CODE3","");//被依赖的机型编码DEVICE_MODEL_CODE
    		String paraCode5 = commparaRuleData.getString("PARA_CODE5","");//判断依赖或互斥活动状态配置值，0：只要办理过不论是否失效  1：只判断有效的活动
    		
    		if(StringUtils.isBlank(paraCode3))
    		{
    			continue;
    		}
    		
    		//查询用户的salegoods中是否有规则要求的机型
    		IDataset userSaleGoodsInfo = UserSaleGoodsInfoQry.querySaleGoodsInfoByDeviceModelCode(userId, paraCode3);
    		
    		//如果没有匹配的数据
    		if(IDataUtil.isEmpty(userSaleGoodsInfo))
    		{
    			continue;
    		}
    		
    		//如果存在匹配的数据，则进行下一步对营销活动是否有效的判断
    		if(IDataUtil.isNotEmpty(userSaleGoodsInfo))
    		{
    			IDataset userSaleActives = null;
    			String relationTradeId = userSaleGoodsInfo.getData(0).getString("RELATION_TRADE_ID");
    			
    			if("0".equals(paraCode5))//依赖或互斥的产品或包状态为不论是否失效
        		{
    				userSaleActives = UserSaleActiveInfoQry.queryRelationAllSaleActive(userId, relationTradeId);//根据蔡世泳要求，取PROCESS_TAG in ('0','1')的数据
        		}
        		else if("1".equals(paraCode5))//依赖或互斥的产品或包状态为有效
        		{
        			userSaleActives = UserSaleActiveInfoQry.queryRelationSaleActiveList(userId, relationTradeId);
        		}
    			
    			if(IDataUtil.isNotEmpty(userSaleActives))
    			{
    				isDepend = true;
                    break;
    			}
    		}
        }
    	
    	//如果没有匹配到存在依赖或互斥的营销活动，则提示错误信息，取compara中配置的第一条PARAM_NAME
    	if (!isDepend)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024102, commparaRuleForSaleactiveData.getData(0).getString("PARAM_NAME",""));
        }

        return true;
    }
}
