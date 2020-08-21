
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.GGCardInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUserImeiQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 2.3.17.param_attr = 1589 >> 用户para_code2卡类型才可办理para_code1活动 该参数废除 TD_BRE_PARAMETER 传入： CARD_TYPE
 * 
 * @author Mr.Z
 */
public class CheckSimcardType extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 7932806441333937401L;

    private static Logger logger = Logger.getLogger(CheckSimcardType.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckSimcardType() >>>>>>>>>>>>>>>>>>");
        }

        IDataset userResDataset = databus.getDataset("TF_F_USER_RES");

        for (int index = 0, size = userResDataset.size(); index < size; index++)
        {
            IData resData = userResDataset.getData(index);
            if ("1".equals(resData.getString("RES_TYPE_CODE")))
            {

                if (!"1".equals(resData.getString("RSRV_TAG3")))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20157, "用户需换成[4G]卡才可办理此活动!");
                    return false;
                }
                break;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckSimcardType() >>>>>>>>>>>>>>>>>>");
        }
        
        
        
        //REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151214
        String productId = databus.getString("PRODUCT_ID");
        String packageId = databus.getString("PACKAGE_ID");
        
        //1、先判断是不是COMMPARA的531内活动
        IDataset productInfos = CommparaInfoQry.getCommpara("CSM", "531", productId, databus.getString("EPARCHY_CODE"));
        //是531内活动则继续
        if (IDataUtil.isNotEmpty(productInfos))
        {
        	String imeiCode = databus.getString("IMEI_CODE","");
            String giftCode = databus.getString("GIFT_CODE","");
            
            //2、判断IMEI和礼品优惠码都不能为空
            if(StringUtils.isBlank(imeiCode))
            {
            	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2015121501, "IMEI号不能为空！");
            }
            if(StringUtils.isBlank(giftCode))
            {
            	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2015121502, "优惠编码不能为空！");
            }
            
            //3、IMEI和礼品优惠码都不空的时候，进行校验
            if(StringUtils.isNotBlank(imeiCode) && StringUtils.isNotBlank(giftCode))
            {
            	//4、调华为接口，校验IMEI。临时改为调用华为的dblink读表
            	IDataset terminalDataset = QueryUserImeiQry.getHuaweiImeiInfos(imeiCode);
            	
            	//如果没有结果，代表状态不正常
            	if (IDataUtil.isEmpty(terminalDataset))
            	{
            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2015121504, "终端状态不正常，不允许销售！");
            	}
            	else
            	{
            		IData terminalData = terminalDataset.getData(0);

            		IData svcParam = new DataMap();
//            		IDataset salePackages = QueryUserImeiQry.qrySalePackages(productId, packageId);
            		IData pkgExtData = databus.getData("PM_OFFER_EXT");
            		pkgExtData.put("PRODUCT_ID", productId);
            		IDataset salePackages = IDataUtil.idToIds(pkgExtData);
            		
                    svcParam.put("SALE_ACTIVES", salePackages);
                    svcParam.put("DEVICE_TYPE_CODE", terminalData.getString("TERMINAL_TYPE_CODE"));
                    svcParam.put("DEVICE_MODEL_CODE", terminalData.getString("DEVICE_MODEL_CODE"));
                    svcParam.put("DEVICE_COST", terminalData.getString("DEVICE_COST"));
                    svcParam.put("SALE_PRICE", terminalData.getString("SALE_PRICE"));
                    svcParam.put(Route.ROUTE_EPARCHY_CODE, databus.getString("EPARCHY_CODE"));

                    salePackages = CSAppCall.call("CS.SalePackagesFilteSVC.filterPackagesByTerminalConfig", svcParam);

                    if (IDataUtil.isEmpty(salePackages))
                    {
                        if (StringUtils.isNotBlank(imeiCode))
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2015121510, "IMEI[" + imeiCode + "]对应机型[" + terminalData.getString("DEVICE_MODEL_CODE","") + "]不能办理该营销活动包！");
                        }
                    }
            	}
            	
            	//5、校验IMEI在本地的IMEI记录表中是否为已使用
            	IDataset imeiInfos = QueryUserImeiQry.getOtherSaleActiveImei(imeiCode);
            	//若存在记录就表示已经使用过
            	if(IDataUtil.isNotEmpty(imeiInfos))
            	{
            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2015121505, "该IMEI已使用过！");
            	}
            	
            	//6、校验礼品码
            	IDataset checkGiftCodeAndReturnConfig = new DatasetList();//礼品卡与活动包的关系配置
        		IDataset giftCodeInfos = GGCardInfoQry.getGGCardInfoByCardPassWord(giftCode);//礼品码信息
        		//如果无礼品码信息
        		if (IDataUtil.isEmpty(giftCodeInfos))
        		{
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2015121506, "礼品码无效！");
        		}
        		else
        		{
        			String processTag = giftCodeInfos.getData(0).getString("PROCESS_TAG","");//礼品码状态 0或空-未用 1-已用
        			//判断礼品码状态
        			if("1".equals(processTag))
        			{
        				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2015121507, "礼品码已使用过！");
        			}
        			else
        			{
        				String cardCode = giftCodeInfos.getData(0).getString("CARD_CODE","");
        				String itemCode = giftCodeInfos.getData(0).getString("ITEM_CODE","");
        				//如果礼品码状态正常，取出礼品码对应的CARD_CODE、ITEM_CODE，关联commpara529配置，取到可以办理的活动包信息
        				checkGiftCodeAndReturnConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "529", cardCode, itemCode, databus.getString("EPARCHY_CODE"));
        				
        				//如果配置为空
                		if(IDataUtil.isEmpty(checkGiftCodeAndReturnConfig))
                		{
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2015121508, "礼品码与活动包对应关系配置有误！");
                		}
                		else
                		{
                			//如果配置不为空，取出配置进行包过滤
                			boolean matchState = false;//初始化匹配状态为不匹配
                			for (int i = 0, size = checkGiftCodeAndReturnConfig.size(); i < size; i++)
                			{
                				IData commparaInfo = checkGiftCodeAndReturnConfig.getData(i);
                				String paraCode2 = commparaInfo.getString("PARA_CODE2","");
                				String paraCode3 = commparaInfo.getString("PARA_CODE3","");
                				
                				//如果paraCode2产品ID，paraCode3包ID不为空
                				if(StringUtils.isNotBlank(paraCode2) && StringUtils.isNotBlank(paraCode3))
                				{
                					if(paraCode2.equals(productId) && paraCode3.equals(packageId))
                					{
                						//匹配
                						matchState = true;
                					}
                				}
                				else
                				{
                					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2015121508, "礼品码与活动包对应关系配置有误！");
                				}
                			}
                			if(!matchState)
                			{
                				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2015121509, "该礼品码不能办理该活动包！");
                			}
                		}
        			}
        		}
        		
        		
            }
        }
        //end

        return true;
    }
}
