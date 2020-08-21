
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res.ResParaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.PersonUtil;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: Check4GUserCommRule.java
 * @Description: 校验4G用户通用规则 【TradeCheckBefore】
 * @version: v1.0.0
 * @author: maoke
 * @date: May 22, 2014 4:12:24 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 22, 2014 maoke v1.0.0 修改原因
 */
public class Check4GUserCommRule extends BreBase implements IBREScript
{
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
     * @author: maoke
     * @date: May 23, 2014 5:07:25 PM
     */
    public boolean isGprs4GDiscnt(String elementId) throws Exception
    {
        IDataset commpara8500_8800 = CommparaInfoQry.getCheck4GUserCommRule("CSM", "4G", elementId, CSBizBean.getTradeEparchyCode());

        if (IDataUtil.isNotEmpty(commpara8500_8800))
        {
        	if("1".equals(commpara8500_8800.getData(0).getString("PARA_CODE3", "0")))
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
            IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", "1");

            if (IDataUtil.isNotEmpty(simCardDatas))
            {
                String simTypeCode = simCardDatas.getData(0).getString("RES_TYPE_CODE", "0").substring(1);// 对应老系统的simtypecode

                IDataset assignParaInfoData = ResParaInfoQry.checkUser4GUsimCard(simTypeCode);

                if (StringUtils.isNotBlank(simTypeCode) && IDataUtil.isNotEmpty(assignParaInfoData))
                {
                    return true;
                }
            }
            else
            {
                // CSAppException.apperr(ResException.CRM_RES_86, simCardNo);
                return false;// 因测试资料不全 暂时返回false
            }
        }
        else
        {
            // CSAppException.apperr(ResException.CRM_RES_85);
            return false;// 因测试资料不全 暂时返回false
        }
        return false;
    }

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        
        if(CSBizBean.getVisit().getStaffId().indexOf("CREDIT") > -1)//信控过来不要做校验
        {
            return false;
        }
        
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData) && !"1".equals(reqData.getString("BY_ACTIVE_TRANS","")))//如果是营销活动传过来,则不执行此规则校验
            {
                IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));

                if (IDataUtil.isNotEmpty(selectedElements))
                {
                    String userId = databus.getString("USER_ID");

                    boolean lteTag = PersonUtil.isLteCardUser(userId);// 4GLTE卡标识
                    
                    //add by xingkj3 非4G卡不能办理的产品
                    String newProductId=databus.getString("NEW_PRODUCT_ID");
                    IDataset commpara8555 = CommparaInfoQry.getCommParas("CSM", "8555", "4G", newProductId, CSBizBean.getTradeEparchyCode());
                    if (IDataUtil.isNotEmpty(commpara8555)){
                    	IData data=commpara8555.getData(0);
                    	if("1".equals(data.getString("PARA_CODE10", ""))&&!lteTag){
                    	//if("1".equals(data.getString("PARA_CODE10", ""))){
                    		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", "非4G用户不能办理该产品");
                            return true;
                    	}
                    }
                    
                    for (int i = 0, size = selectedElements.size(); i < size; i++)
                    {
                        IData element = selectedElements.getData(i);

                        String elementId = element.getString("ELEMENT_ID");
                        String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                        String modifyTag = element.getString("MODIFY_TAG");

                        //-------------------------yanwu4G产品验证begin--------------------------------------------
                        if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) && BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
                        	IData ProData = UProductInfoQry.qryProductByPK(elementId);
                        	String strRsrvTag3 = "";
                        	if( IDataUtil.isNotEmpty(ProData) ){
                        		strRsrvTag3 = ProData.getString("RSRV_TAG3");
                        	}
/*                        	if ( "4".equals(strRsrvTag3) && !lteTag)// 4G套餐、非4G卡
                            {
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", "该类型卡无法使用4G网络，请放入LTE-USIM卡!");

                                return true;
                            }*/
                        }
                        //-------------------------yanwu4G产品验证end----------------------------------------------
                        
                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                        {
                            boolean isGprs4GDiscntTag = isGprs4GDiscnt(elementId);
                            if (isGprs4GDiscntTag && !lteTag)// 4G套餐、非4G卡
                            {
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20160411", "该类型卡无法使用4G网络，请放入LTE-USIM卡!");

                                return true;
                            }

                            boolean isExistMutexDiscntTag = isExistMutexDiscnt(elementId);
                            if (isExistMutexDiscntTag && lteTag)// 4G卡 有互斥优惠
                            {
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", "USIM卡用户不能订购流量包月不限量、超低价流量资费、闲时流量套餐等优惠,优惠编码【" + elementId + "】" +
                                		"名称【"+UDiscntInfoQry.getDiscntNameByDiscntCode(elementId)+"】");

                                return true;
                            }
                        }

                        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode) && BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                        {
/*                            if (lteTag && "22".equals(elementId))
                            {
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", "为了保证号码正常使用,USIM卡号码不能取消GPRS功能!");

                                return true;
                            }*/
                        }
                    }
                }
            }
        }
        return false;
    }
}
