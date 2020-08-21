
package com.asiainfo.veris.crm.order.soa.person.rule.run.createuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.AssignParaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: Check4GForOpenUser.java
 * @Description: 校验4G用户开户通用规则
 * @version: v1.0.0
 * @author: sunxin
 * @date: May 22, 2014 4:12:24 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 22, 2014 sunxin v1.0.0 修改原因
 */
public class Check4GForOpenUser extends BreBase implements IBREScript
{
    // @Override
    private static Logger logger = Logger.getLogger(Check4GForOpenUser.class);

    /**
     * @Description: 是否是4G产品
     * @param productId
     * @return
     * @throws Exception
     * @author: sunxin
     * @date: May 23, 2014 5:07:25 PM
     */
    public boolean is4GProduct(String productId) throws Exception
    {
        IDataset commpara8500 = CommparaInfoQry.getCommParas("CSM", "8555", "4G", productId, CSBizBean.getTradeEparchyCode());

        if (IDataUtil.isNotEmpty(commpara8500))
        {
            return true;
        }
        return false;
    }

    /**
     * @Description: 是否存在互斥优惠
     * @param elementId
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 23, 2014 5:18:41 PM
     */
    public boolean isExistMutexDiscnt(String elementId) throws Exception
    {
        IDataset commpara8550 = CommparaInfoQry.getCommParas("CSM", "8550", "4G", elementId, CSBizBean.getTradeEparchyCode());

        if (IDataUtil.isNotEmpty(commpara8550))
        {
            return true;
        }
        return false;
    }

    /**
     * @Description: 是否是4G优惠
     * @param elementId
     * @return
     * @throws Exception
     * @author: sunxin
     * @date: May 23, 2014 5:07:25 PM
     */
    public boolean isGprs4GDiscnt(String elementId) throws Exception
    {
        IDataset commpara8500 = CommparaInfoQry.getCommParas("CSM", "8500", "4G", elementId, CSBizBean.getTradeEparchyCode());

        if (IDataUtil.isNotEmpty(commpara8500))
        {
        	if("1".equals(commpara8500.getData(0).getString("PARA_CODE3", "0")))
        		return true;
        	else
        		return false;
        }
        return false;
    }

    /**
     * @Description: 是否4G卡用户
     * @param userId
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 23, 2014 5:33:17 PM
     */
    public boolean isLteCardUser(String userId) throws Exception
    {
        IDataset resDatas = UserResInfoQry.queryUserResByUserIdResType(userId, "1");

        if (IDataUtil.isNotEmpty(resDatas))
        {
            String simCardNo = resDatas.getData(0).getString("RES_CODE");

            // 调用资源接口
            IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", "USE");

            if (IDataUtil.isNotEmpty(simCardDatas))
            {
                String simTypeCode = simCardDatas.getData(0).getString("SIM_TYPE_CODE", "");

                IDataset assignParaInfoData = AssignParaInfoQry.getUSIM4GInfo(simTypeCode, CSBizBean.getTradeEparchyCode());

                if (StringUtils.isNotBlank(simTypeCode) && IDataUtil.isNotEmpty(assignParaInfoData))
                {
                    return true;
                }
            }
            else
            {
                CSAppException.apperr(ResException.CRM_RES_86, simCardNo);
            }
        }
        else
        {
            CSAppException.apperr(ResException.CRM_RES_85);
        }
        return false;
    }

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String strError = null;

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 Check4GForOpenUser() >>>>>>>>>>>>>>>>>>");

        String lteTag = "";// 4GLTE卡标识
        String productId = "";// 产品id
        String brandCode = "";//品牌
        
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        IDataset listTradeRes = databus.getDataset("TF_B_TRADE_RES");
        IDataset listTradeProduct = databus.getDataset("TF_B_TRADE_PRODUCT");
        IDataset listTradeUser = databus.getDataset("TF_B_TRADE_USER");
        if (IDataUtil.isNotEmpty(listTradeRes))
        {
            for (int i = 0, size = listTradeRes.size(); i < size; i++)
            {
                IData listTradeRe = listTradeRes.getData(i);
                if ("1".equals(listTradeRe.getString("RES_TYPE_CODE")))
                    lteTag = listTradeRe.getString("RSRV_TAG3");
            }
        }
        
/*         if(IDataUtil.isNotEmpty(listTradeSvc)) 
         { 
        	 boolean exit =false;
        	 for(int i=0, size=listTradeSvc.size(); i<size; i++) 
	         {
        		IData tradeSvc = listTradeSvc.getData(i); 
	         	String serviceId = tradeSvc.getString("SERVICE_ID"); 
	         	String modifyTag = tradeSvc.getString("MODIFY_TAG"); 
	         	if (BofConst.MODIFY_TAG_ADD.equals(modifyTag)) 
		         	{ 
	         			if ("22".equals(serviceId)) 
				        { 
		         		 exit=true;
				        } 
		         	} 
	         }
        	 if((!exit)&&"1".equals(lteTag)){
        		 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,12062701,"为了保证号码正常使用，办理USIM卡业务前，请先开通GPRS功能。"); 
		         return true;
        	 }
         }*/
         
        if (IDataUtil.isNotEmpty(listTradeDiscnt))
        {
            for (int i = 0, size = listTradeDiscnt.size(); i < size; i++)
            {
                IData tradeDiscnt = listTradeDiscnt.getData(i);
                productId = tradeDiscnt.getString("PRODUCT_ID");
                String discntCode = tradeDiscnt.getString("DISCNT_CODE");
                String modifyTag = tradeDiscnt.getString("MODIFY_TAG");
                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {
                    boolean isGprs4GDiscntTag = isGprs4GDiscnt(discntCode);
                    if (isGprs4GDiscntTag && !"1".equals(lteTag))// 4G套餐、非4G卡
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20160411", "该类型卡无法使用4G网络，请放入LTE-USIM卡!");

                        return true;
                    }

                    boolean isExistMutexDiscntTag = isExistMutexDiscnt(discntCode);
                    if (isExistMutexDiscntTag && "1".equals(lteTag))// 4G卡
                    // 有互斥优惠
                    {
                        strError = "USIM卡用户不能订购流量包月不限量、超低价流量资费、闲时流量套餐等优惠,优惠编码为【" + discntCode + "】";
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 12062701, strError);
                        return true;
                    }
                }
            }
        }
        // cpe 开户判断非4G卡无法使用CPE产品
        // chenxy3
        if(IDataUtil.isNotEmpty(listTradeProduct)){
        	brandCode = listTradeProduct.getData(0).getString("BRAND_CODE");
        	
        	//add by xingkj3 非4G卡不能办理的产品
            String newProductId=listTradeProduct.getData(0).getString("PRODUCT_ID");
            IDataset commpara8555 = CommparaInfoQry.getCommParas("CSM", "8555", "4G", newProductId, CSBizBean.getTradeEparchyCode());
            if (IDataUtil.isNotEmpty(commpara8555)){
            	IData data=commpara8555.getData(0);
            	if("1".equals(data.getString("PARA_CODE10", ""))&&!"1".equals(lteTag)){
            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "10", "非4G用户不能办理该产品");
                    return true;
            	}
            }
            
        }
        boolean is4GProductTag = is4GProduct(productId); 
        if ("CPE1".equals(brandCode) &&  !"1".equals(lteTag))// 4G产品、非4G卡 
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 12062701, "该类型卡无法使用CPE产品，请放入LTE-USIM卡!");
            return true;
        } 
        String serialNum="";//手机号
        //UcaData ucaData = (UcaData) databus.get("UCADATA");
        if(listTradeUser!=null && listTradeUser.size()>0){
        	serialNum = listTradeUser.getData(0).getString("SERIAL_NUMBER");
        }
        if("CPE1".equals(brandCode) && !"147".equals(serialNum.substring(0,3))){
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 12062702, "CPE业务无法使用非147开头号码!");
            return true;
        }
        
        return false;
    }
}
