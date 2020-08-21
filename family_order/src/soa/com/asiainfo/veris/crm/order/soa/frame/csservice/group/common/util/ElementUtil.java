
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserCommUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ElementUtil
{
    /**
     * 计算新增元素的生效失效时间
     *
     * @param element
     * @param basicStartDate
     *            计算元素的基本时间点，如果是预约业务，需要传入预约业务的时间作为基本时间点
     * @param effectNow
     *            元素是否立即生效
     * @throws Exception
     */
    public static void calAddEleStartAndEndDate(ProductModuleData pmd, IData element, String basicStartDate, boolean effectNow) throws Exception
    {

        ProductTimeEnv env = new ProductTimeEnv();

        if (StringUtils.isNotBlank(basicStartDate))
        {
            env.setBasicAbsoluteStartDate(basicStartDate);
        }

        if ("5".equals(pmd.getEnableTag()) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(pmd.getElementType()))
        {
            element.put("EFFECT_NOW_START_DATE", SysDateMgr.getFirstDayOfNextMonth());
            element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
        }
        else
        {
            element.put("EFFECT_NOW_START_DATE", StringUtils.isNotBlank(basicStartDate) ? basicStartDate : SysDateMgr.getSysTime());
            element.put("EFFECT_NOW_END_DATE", SysDateMgr.endDate(element.getString("EFFECT_NOW_START_DATE"), pmd.getEndEnableTag(), pmd.getEndAbsoluteDate(), pmd.getEndOffSet(), pmd.getEndUnit()));
        }

        pmd.setStartDate(null);
        pmd.setEndDate(null);
        String startDate = ProductModuleCalDate.calStartDate(pmd, env);
        element.put("START_DATE", startDate);
        String endDate = ProductModuleCalDate.calEndDate(pmd, startDate);
        element.put("END_DATE", endDate);

        if (effectNow)
        {
            element.put("OLD_EFFECT_NOW_START_DATE", startDate);
            element.put("OLD_EFFECT_NOW_END_DATE", endDate);
            element.put("START_DATE", element.getString("EFFECT_NOW_START_DATE"));
            element.put("END_DATE", element.getString("EFFECT_NOW_END_DATE"));
        }

        if ("3".equals(pmd.getEndEnableTag()) && StringUtils.isBlank(basicStartDate))
        {
            element.put("CHOICE_START_DATE", "true");
        }
        if ("2".equals(pmd.getEndEnableTag()) || StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_CRM_DISCNTDATECHG"))
        {
            element.put("SELF_END_DATE", "true");// 自选时间
        }

    }

    /**
     * 计算取消元素的终止时间
     *
     * @param element
     * @param basicEndDate
     * @param effectNow
     * @throws Exception
     */
    public static void calDelEleEndDate(ProductModuleData pmd, IData element, String basicEndDate, boolean effectNow) throws Exception
    {

        ProductTimeEnv env = new ProductTimeEnv();
        element.put("EFFECT_NOW_START_DATE", element.getString("START_DATE"));
        element.put("EFFECT_NOW_END_DATE", StringUtils.isNotBlank(basicEndDate) ? basicEndDate : SysDateMgr.getSysTime());

        if (StringUtils.isNotBlank(basicEndDate))
        {
            env.setBasicAbsoluteCancelDate(basicEndDate);
        }
//        pmd.setEndDate(null);
        if(StringUtils.isNotBlank(pmd.getCancelTag()) && !"4".equals(pmd.getCancelTag())){
        	pmd.setEndDate(null);
        }
        String cancelDate = ProductModuleCalDate.calCancelDate(pmd, env);
        element.put("END_DATE", cancelDate);
        if (effectNow)
        {
            element.put("OLD_EFFECT_NOW_START_DATE", element.getString("START_DATE"));
            element.put("OLD_EFFECT_NOW_END_DATE", cancelDate);
            element.put("END_DATE", element.getString("EFFECT_NOW_END_DATE"));
        }

    }

    public static IDataset dealSelectedElementAttrs(IData element, String eparchyCode) throws Exception
    {

        IDataset attrs = new DatasetList();
        String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
        String elementId = element.getString("ELEMENT_ID");
        
        if(StringUtils.isNotEmpty(elementTypeCode) && elementTypeCode.equals("S"))
        {
            IDataset servPage = AttrBizInfoQry.getBizAttr(elementId, "S", "ServPage", null, null);
            if(IDataUtil.isNotEmpty(servPage))
            {
                element.put("ATTR_PARAM_TYPE", "9");
                IData userattr = new DataMap();
                userattr.put("PARAM_VERIFY_SUCC", "false");
                attrs.add(userattr);
                return attrs;
            }
        }
        
        IDataset attrItemAList = AttrItemInfoQry.getElementItemA(element.getString("ELEMENT_TYPE_CODE"), element.getString("ELEMENT_ID"), eparchyCode);
        attrs = makeAttrs(null, attrItemAList);
        return attrs;
    }

    /**
     * 计算元素的起止时间
     *
     * @param element
     * @param basicDate
     * @param effectNow
     * @param eparchyCode
     * @throws Exception
     */
    public static void dealSelectedElementStartDateAndEndDate(IData element, String basicDate, boolean effectNow, String eparchyCode) throws Exception
    {
        dealSelectedElementStartDateAndEndDate(element, basicDate, effectNow, eparchyCode, null, false);
    }

    /**
     * 计算元素的起止时间，包含特殊逻辑处理
     *
     * @param element
     * @param basicDate
     * @param effectNow
     * @param eparchyCode
     * @param userElements
     * @throws Exception
     */
    public static void dealSelectedElementStartDateAndEndDate(IData element, String basicDate, boolean effectNow, String eparchyCode, IDataset userElements) throws Exception
    {
        dealSelectedElementStartDateAndEndDate(element, basicDate, effectNow, eparchyCode, userElements, true);
    }

    /**
     * 计算元素的起止时间
     *
     * @param element
     * @param basicDate
     * @param effectNow
     * @param eparchyCode
     * @param userElements
     * @param specialDeal
     * @throws Exception
     */
    public static void dealSelectedElementStartDateAndEndDate(IData element, String basicDate, boolean effectNow, String eparchyCode, IDataset userElements, boolean specialDeal) throws Exception
    {
        ProductModuleData pmd = null;

        if (element.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_SVC))
        {
            pmd = new SvcData(element);
        }
        else if (element.getString("ELEMENT_TYPE_CODE", "").equals(BofConst.ELEMENT_TYPE_CODE_PLATSVC)) 
        {
            if (BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG")))
            {
                element.put("OPER_CODE", PlatConstants.OPER_ORDER);
            }
            else if (BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG")))
            {
                element.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
            }
            pmd = new PlatSvcData(element);
            pmd.setPkgElementConfig(element.getString("PACKAGE_ID"));
        }
        else
        {
            pmd = new DiscntData(element);
        }

        if (specialDeal)
        {

            // 1. TD_B_PRODUCT_PACKAGE 表中的RSRV_STR1 = 1 时的逻辑处理,如果不是第一次订购包内的元素，则元素下账期生效
            String productId = element.getString("PRODUCT_ID", "");
            String packageId = element.getString("PACKAGE_ID", "");
            IData productPackageData = ProductPkgInfoQry.getProductPackageRelNoPriv(productId, packageId, eparchyCode);
            if (IDataUtil.isNotEmpty(productPackageData))
            {
                String packageStr1 = productPackageData.getString("PROD_PACK_REL_STR1", "");
                if (packageStr1.equals("1"))
                {
                    if (!ifFirstAddWithPack(userElements, packageId, element.getString("ELEMENT_TYPE_CODE", "")))
                        pmd.setEnableTag("1");
                }
            }

        }

        if (BofConst.MODIFY_TAG_ADD.equals(pmd.getModifyTag()))
        {
            calAddEleStartAndEndDate(pmd, element, basicDate, effectNow);
        }
        else if (BofConst.MODIFY_TAG_DEL.equals(pmd.getModifyTag()))
        {
            calDelEleEndDate(pmd, element, basicDate, effectNow);
          //套餐取消方式
			element.put("CANCEL_MODE",pmd.getCancelTag());
        }

        // 把元素的失效方式返回，免得后面再查一遍
        element.put("CANCEL_TAG", pmd.getCancelTag());

    }

    public static String getCancelDate(ElementModel model, String currentSysDate) throws Exception
    {
        // 如果元素未生效(元素开始时间大于系统时间), 注销到当前时间的前一秒
        if (model.getStartDate().compareTo(currentSysDate) > 0)
        {
            return SysDateMgr.getLastSecond(currentSysDate);
        }

        // 获取元素的时效方式
        String cancelTag = "0";
        IData enableData = UPackageElementInfoQry.queryElementEnableMode(model.getProductId(), model.getPackageId(), model.getElementId(), model.getElementTypeCode());
        if(IDataUtil.isNotEmpty(enableData))
        {
            cancelTag = enableData.getString("CANCEL_TAG");
        }

        String cancelDate = "";

        if (GroupBaseConst.CancelMode.Now.getValue().equals(cancelTag) || "6".equals(cancelTag))
        {
            cancelDate = SysDateMgr.getLastSecond(currentSysDate);
        }
        else if (GroupBaseConst.CancelMode.Yesterday.getValue().equals(cancelTag))
        {
            cancelDate = SysDateMgr.getYesterdayDate() + SysDateMgr.getEndTime235959();
        }
        else if (GroupBaseConst.CancelMode.Today.getValue().equals(cancelTag))
        {
            cancelDate = SysDateMgr.getSysDate() + SysDateMgr.getEndTime235959();
        }
        else if (GroupBaseConst.CancelMode.LastDate.getValue().equals(cancelTag))
        {
            cancelDate = SysDateMgr.getDateLastMonthSec(currentSysDate);
        }
        else if (GroupBaseConst.CancelMode.Error.getValue().equals(cancelTag)||"7".equals(cancelTag))
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_240);
        }

        return cancelDate;
    }

    public static String getCancelDate_chgUserElem(IData packageData, String currentSysDate, String startDate, String endDate) throws Exception
    {
        // 未生效时注销
        if (currentSysDate.compareTo(startDate) < 0)
        {
            // 当前时间减一秒
            return SysDateMgr.getLastSecond(currentSysDate);
        }

        // 非立即生效
        String strCancelDate = endDate.substring(0, 10);

        // 查优惠取消标记

        String packageid = packageData.getString("PACKAGE_ID", "-1");
        String elementtypecode = packageData.getString("ELEMENT_TYPE_CODE", "-1");
        String elementid = packageData.getString("ELEMENT_ID", "-1");

        IDataset ids = PkgElemInfoQry.getServElementByPk(packageid, elementtypecode, elementid);

        if (IDataUtil.isNotEmpty(ids))
        {
            IData idElement = ids.getData(0);

            String strCancelTag = idElement.getString("CANCEL_TAG", "-1");

            // 0-立即取消（当前时间）
            if (strCancelTag.equals(GroupBaseConst.CancelMode.Now.getValue()))
            {
                return SysDateMgr.getLastSecond(currentSysDate);
            }
        }

        return strCancelDate + SysDateMgr.getEndTime235959();
    }

    /**
     * 获取注销时失效时间
     *
     * @param Date
     * @param Mode
     * @return
     */
    public static String getCancelDate_dstUserElem(IData packageData, String currentSysDate, String startDate, String endDate) throws Exception
    {
        // 未生效时注销
        if (currentSysDate.compareTo(startDate) < 0)
        {
            // 当前时间减一秒
            return SysDateMgr.getLastSecond(currentSysDate);
        }

        String productId = packageData.getString("PRODUCT_ID");
        String package_id = packageData.getString("PACKAGE_ID", "");
        String element_type_code = packageData.getString("ELEMENT_TYPE_CODE", "");
        String element_id = packageData.getString("ELEMENT_ID", "");
        String strCancelTag = ProductInfoQry.queryElementEnable(productId, package_id, element_type_code, element_id, "CANCEL_TAG");

        String strCancelDate = "";

        if (strCancelTag.equals(GroupBaseConst.CancelMode.Now.getValue()))
        {
            strCancelDate = SysDateMgr.getLastSecond(currentSysDate);
        }
        else if (strCancelTag.equals(GroupBaseConst.CancelMode.Yesterday.getValue()))
        {
            strCancelDate = SysDateMgr.getYesterdayDate() + SysDateMgr.getEndTime235959();
        }
        else if (strCancelTag.equals(GroupBaseConst.CancelMode.Today.getValue()))
        {
            strCancelDate = SysDateMgr.getSysDate() + SysDateMgr.getEndTime235959();
        }
        else if (strCancelTag.equals(GroupBaseConst.CancelMode.LastDate.getValue()))
        {
            strCancelDate = SysDateMgr.getDateLastMonthSec(currentSysDate);
        }
        else if (strCancelTag.equals(GroupBaseConst.CancelMode.Error.getValue()))
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_240);
        }

        return strCancelDate;
    }

    /**
     * 获取集团成员注销时间
     *
     * @param model
     * @param currentSysDate
     * @return
     * @throws Exception
     */
    public static String getCancelDateForDstMb(ElementModel model, String currentSysDate) throws Exception
    {
        String cancelDate = getCancelDate(model, currentSysDate);
        
    	if("R".equals(model.getElementTypeCode())){
    		return cancelDate;
    	}

        // 获取元素的时效方式
        String cancelTag = UProductInfoQry.queryElementEnable(model.getElementTypeCode(), model.getElementId());
        String rsrvStr3 = ProductInfoQry.queryElementEnableExtend(model.getProductId(), model.getPackageId(), model.getElementTypeCode(), model.getElementId(), "RSRV_STR3");

        // 海南特殊需求
        if (GroupBaseConst.CancelMode.LastDate.getValue().equals(cancelTag) && "1".equals(rsrvStr3))
        {
            cancelDate = currentSysDate;
        }
        
        return cancelDate;
    }

    /**
     * 获取集团注销时间
     *
     * @param model
     * @param currentSysDate
     * @return
     * @throws Exception
     */
    public static String getCancelDateForDstUs(ElementModel model, String currentSysDate) throws Exception
    {
        String cancelDate = getCancelDate(model, currentSysDate);

        // 获取元素的时效方式
        String cancelTag = ProductInfoQry.queryElementEnable(model.getProductId(), model.getPackageId(), model.getElementTypeCode(), model.getElementId(), "CANCEL_TAG");

        String rsrvStr3 = ProductInfoQry.queryElementEnableExtend(model.getProductId(), model.getPackageId(), model.getElementTypeCode(), model.getElementId(), "RSRV_STR3");

        // 海南特殊需求
        if (GroupBaseConst.CancelMode.LastDate.getValue().equals(cancelTag) && "1".equals(rsrvStr3))
        {
            cancelDate = currentSysDate;
        }

        return cancelDate;
    }

    public static IData getStartDateAndEndDate(boolean effectNow, IData packageData, String modfiTag, String startDate, String endDate) throws Exception
    {

        IData result = new DataMap();

        // 1,如果元素是立即生效
        if (effectNow)
        {
            // 新增
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modfiTag))
            {
                result.put("START_DATE", SysDateMgr.getSysTime());
                result.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modfiTag))
            {
                result.put("START_DATE", startDate);
                result.put("END_DATE", SysDateMgr.getSysTime());
            }
        }
        else
        {
            if (TRADE_MODIFY_TAG.DEL.getValue().equals(modfiTag))
            {
                result.put("START_DATE", startDate);
                String temp_endDate = getCancelDate_chgUserElem(packageData, SysDateMgr.getSysTime(), startDate, endDate);
                result.put("END_DATE", temp_endDate);
            }
            else
            {
                String temp_startDate = SysDateMgr.getEnableDate(packageData.getString("START_DATE"));
                String temp_endDate = SysDateMgr.getEndDate(packageData.getString("END_DATE"));
                result.put("START_DATE", temp_startDate);
                result.put("END_DATE", temp_endDate);
            }

        }
        return result;
    }

    public static boolean ifFirstAddWithPack(IDataset userElements, String packageId, String elementTypeCode) throws Exception
    {
        boolean ifFirstAddEle = true;
        if (IDataUtil.isNotEmpty(userElements))
        {
            int elesSize = userElements.size();
            for (int i = 0; i < elesSize; i++)
            {
                IData tempUserEle = userElements.getData(i);
                String tempPackageId = tempUserEle.getString("PACKAGE_ID", "");
                String tempModifyTag = tempUserEle.getString("MODIFY_TAG", "");
                String tempElementTypeCode = tempUserEle.getString("ELEMENT_TYPE_CODE", "");
                if (tempPackageId.equals(packageId) && tempElementTypeCode.equals(elementTypeCode) && !tempModifyTag.equals("0_1") && !tempModifyTag.equals("0"))
                {
                    ifFirstAddEle = false;
                    break;
                }
            }
        }

        return ifFirstAddEle;
    }

    private static IDataset makeAttrs(IDataset userAttrs, IDataset elementItemAList)
    {

        if (elementItemAList != null && elementItemAList.size() > 0)
        {
            int size = elementItemAList.size();
            IDataset returnAttrs = new DatasetList();
            int depositRate =0;
            int flowCount = 0;
            boolean dealAllFlowFlag = false;
            String elementId = "";
            for (int i = 0; i < size; i++)
            {
                IData attr = new DataMap();
                IData itemA = elementItemAList.getData(i);
                attr.put("ATTR_CODE", itemA.getString("ATTR_CODE"));
                attr.put("ATTR_VALUE", "");
                if (userAttrs != null && userAttrs.size() > 0)
                {
                    int uSize = userAttrs.size();
                    for (int j = 0; j < uSize; j++)
                    {
                        IData userAttr = userAttrs.getData(j);
                        if (itemA.getString("ATTR_CODE").equals(userAttr.getString("ATTR_CODE")))
                        {
                            attr.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
                            break;
                        }
                    }
                }
                else
                {	
                	//*******************处理集团自由充（全量统付）的属性，初始化计算流量总额 start**************/
                	if("7361".equals(itemA.getString("ATTR_CODE"))){//集团自由充（全量统付）的流量份数
                		flowCount=Integer.parseInt(itemA.getString("ATTR_INIT_VALUE"));
                	}else if("7362".equals(itemA.getString("ATTR_CODE"))){
                		dealAllFlowFlag =true;//处理 集团自由充（全量统付）的流量总额
                		elementId = itemA.getString("ID");
                	}
                	//*******************处理集团自由充（全量统付）的属性，初始化计算流量总额 end**************/
                	attr.put("ATTR_VALUE", itemA.getString("ATTR_INIT_VALUE"));
                }
                returnAttrs.add(attr);          
            }
          //*******************处理集团自由充（全量统付）的属性，初始化计算流量总额 start**************/
            if(dealAllFlowFlag){
            	for (int i = 0; i < returnAttrs.size(); i++)
                {
                    IData attr = returnAttrs.getData(i);
                    if("7362".equals(attr.getString("ATTR_CODE"))
                    		&&flowCount>0){//出现流量总额的属性时，计算总额
                    	IData param = new DataMap();
                    	param.put("PARAM_CODE",elementId);  
                    	param.put("PARAM_ATTR", 886);
                    	param.put("SUBSYS_CODE", "CSM");
                    	
    					try {
    						IDataset flowValues = PkgElemInfoQry.getElementAttr4ProFlow(param);
    						int flowValue = Integer.parseInt(String.valueOf(flowValues.first().get("PARA_CODE1")));
    	                	int allFlow = flowCount*flowValue;
    	                	attr.put("ATTR_SINGLE_FLOW", String.valueOf(flowValues.first().get("PARA_CODE1")));//单个套餐的流量值
    	                	attr.put("ATTR_VALUE", String.valueOf(allFlow));
    	                	
    					} catch (Exception e) { 
    						e.printStackTrace();
    					}
                    }
                }             
            }
          //*******************处理集团自由充（全量统付）的属性，初始化计算流量总额 end**************/
            
            return returnAttrs;
        }
        else    
        {
            return null;
        }
    }

    /**
     * 因为多个元素操作时，元素间存在时间差，验证或者提交时需要重新整理计算时间，使元素的时间点在同一点
     *
     * @param elementData
     * @param nowTime
     * @throws Exception
     */
    public static void reCalcElementDateByNowTime(IData elementData, String nowTime) throws Exception
    {
        String modifyTagString = elementData.getString("MODIFY_TAG", "");
        String startDateString = elementData.getString("START_DATE", "");
        String endDateString = elementData.getString("END_DATE", "");
        String nowTimeLastSec = SysDateMgr.getLastSecond(nowTime);
        if (modifyTagString.equals(BofConst.MODIFY_TAG_ADD))
        {
            if (startDateString.compareTo(nowTime) < 0)
            {
                elementData.put("START_DATE", nowTime);
            }
        }
        else if (modifyTagString.equals(BofConst.MODIFY_TAG_DEL) || modifyTagString.equals(BofConst.MODIFY_TAG_UPD))
        {

            if (endDateString.compareTo(nowTime) <= 0)
            {
                elementData.put("END_DATE", nowTimeLastSec);
            }
        }

    }
    
    public static boolean checkDiscntGroupPolicyRule(String userId, String discntCode) throws Exception
    {
    	String serialNumber = "";
        IDataset configs = CommparaInfoQry.getCommparaByParaAttr("CSM", "7019", CSBizBean.getTradeEparchyCode());
        //System.out.println("-----------checkDiscntGroupPolicyRule----------configs:"+configs);
        IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, "0");
        if (IDataUtil.isNotEmpty(userInfo))
        {
        	serialNumber = userInfo.getString("SERIAL_NUMBER", "");
        }
        //System.out.println("-----------checkDiscntGroupPolicyRule----------serialNumber:"+serialNumber);

        if(!"".equals(serialNumber) && isInCommparaParamCodeConfigs(discntCode, configs))
        {
        	IDataset ids = qryGroupPolicy(serialNumber,discntCode);
            //System.out.println("-----------checkDiscntGroupPolicyRule----------ids:"+ids);

        	if(IDataUtil.isEmpty(ids))
            {
        		CSAppException.apperr(ElementException.CRM_ELEMENT_310,"您不符合折扣套餐策略,不能办理折扣套餐:"+discntCode+"!");
        		return false;
            }
        }
        return true;
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
