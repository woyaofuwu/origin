
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.InModeCodeUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.PayMoneyData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUserReqData;

public class GroupModuleParserBean extends CSBizBean
{

	
	private static String delValidTime = "2050-12-31 23:59:59";
    /**
     * 公用解析资源信息
     *
     * @param userId
     * @param userIdA
     * @param resList
     * @throws Exception
     */
    public static IDataset commonRes(String userId, String userIdA, IDataset resList, String acceptTime) throws Exception
    {
        IDataset resDataset = new DatasetList();

        // 处理资源信息
        for (int i = 0, row = resList.size(); i < row; i++)
        {
            IData resData = resList.getData(i);

            String modifyTag = resData.getString("MODIFY_TAG");
            String resTypeCode = resData.getString("RES_TYPE_CODE");
            String resCode = resData.getString("RES_CODE");

            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag)) // 新增资源信息
            {
                resData.put("IMSI", "0");
                resData.put("START_DATE", acceptTime);
                resData.put("END_DATE", SysDateMgr.getTheLastTime());
                resData.put("INST_ID", SeqMgr.getInstId());

                // 添加资源信息
                resDataset.add(resData);
            }
            else if (TRADE_MODIFY_TAG.MODI.getValue().equals(modifyTag) || TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
            {
                // 查询用户资源信息
                IDataset userResList = UserResInfoQry.qryUserResInfoByUserIdResType(userId, userIdA, resTypeCode, resCode);

                if (IDataUtil.isEmpty(userResList))
                {
                    CSAppException.apperr(ElementException.CRM_ELEMENT_230);
                }

                IData userReData = userResList.getData(0);

                // 设置标记
                userReData.put("MODIFY_TAG", modifyTag);

                if (TRADE_MODIFY_TAG.MODI.getValue().equals(modifyTag))
                {
                    userReData.put("IMSI", resData.getString("IMSI"));
                    userReData.put("KI", resData.getString("KI"));
                }
                else
                {
                    String startDate = userReData.getString("START_DATE");

                    String systemTime = acceptTime;

                    String lastSecond = SysDateMgr.getLastSecond(systemTime);

                    userReData.put("END_DATE", startDate.compareTo(systemTime) > 0 ? lastSecond : systemTime);
                }

                resDataset.add(userReData);
            }
        }

        return resDataset;
    }

    public static IDataset dealAttrParam(ElementModel model, String userId, String relaInstId, String acceptTime) throws Exception
    {   
    	DecimalFormat df = new DecimalFormat("#0.##");//用于保留两位小数
    	df.setRoundingMode(RoundingMode.DOWN);//非四舍五入
        // 返回数据
        IDataset retParamList = new DatasetList();

        // 处理参数信息
        IData attrParamData = transAttrList2Map(model.getAttrParam());

        // 获取用户参数列表
        IDataset userParamList = UserAttrInfoQry.getUserAttrByUserIdInstid(userId, model.getElementTypeCode(), relaInstId);

        //处理特殊属性的入表信息，addby chenwy 2018-5-16 start
        //retParamList = DealWLWSpecialServerParam.dealSpecialServerParam(model,attrParamData,userParamList,userId,relaInstId,acceptTime);

        retParamList = DealWlwMebSpecialServerParam.dealSpecialServerParam(model,userParamList,userId,relaInstId,acceptTime);
        //有数据直接返回，不做重复处理
        if(IDataUtil.isNotEmpty(retParamList)){
        	return retParamList;
        }
        //处理特殊属性的入表信息，addby chenwy 2018-5-16 end
        
        // 根据参数标志处理
        if (TRADE_MODIFY_TAG.DEL.getValue().equals(model.getModifyTag()))
        {
            for (int i = 0, iSize = userParamList.size(); i < iSize; i++)
            {
                IData userParam = userParamList.getData(i);
                userParam.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                userParam.put("END_DATE", model.getEndDate());
            }
            retParamList = userParamList;
        }
        else
        {
            Iterator<String> iterator = attrParamData.keySet().iterator();

            while (iterator.hasNext())
            {
                String paramKey = iterator.next();
                String paramValue = attrParamData.getString(paramKey);

                boolean isExist = false;
                
                if("61301".equals(paramKey)){ 
                	Double paramchg = Double.valueOf(paramValue);
                	Double paramValueDouble  = paramchg/1024;
                	paramValue = df.format(paramValueDouble);
                }
                
                IData map = new DataMap();
                for (int i = 0, iSize = userParamList.size(); i < iSize; i++)
                {
                    IData userParamData = userParamList.getData(i);
                    String attrCode = userParamData.getString("ATTR_CODE", "");
                    String attrValue = userParamData.getString("ATTR_VALUE", "");

                    if (attrCode.equals(paramKey)) // paramKey相等,值不同
                    {
                        // 处理传空情况
                        if (StringUtils.isEmpty(paramValue) && StringUtils.isEmpty(attrValue))
                        {
                            isExist = true;
                            break;
                        }

                        if (!attrValue.equals(paramValue)) // 如果值一样没有必要再拼数据
                        {
                            if (paramKey != null && paramKey.length() > 3 && paramKey.substring(0, 3).equals("FEE"))
                            {
                                if ("".equals(paramValue))
                                {
                                    paramValue = "0";
                                }
                                else
                                {
                                    paramValue = String.valueOf(100 * Integer.parseInt(paramValue));
                                }
                            }

                            IData modParamData = (IData) Clone.deepClone(userParamData);

                            // 修改服务参数为修改标识; 修改资费参数为注销原有资费参数, 新增一条新的资费参数
                            if ("S".equals(model.getElementTypeCode()))
                            {
                                if (StringUtils.isEmpty(paramValue))
                                {
                                    String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                                    modParamData.put("END_DATE", cancelDate);
                                    modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                    modParamData.put("ATTR_VALUE", attrValue);//删除的时候填写原来的值
                                }
                                else
                                {
                                    modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    modParamData.put("ATTR_VALUE", paramValue);
                                }
                            }
                            else if ("D".equals(model.getElementTypeCode()))
                            {
                                String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                                modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                modParamData.put("END_DATE", cancelDate);

                                //资费属性变更为空值的时候，没必要新增那条空值
                                if (StringUtils.isNotEmpty(paramValue))
                                {
                                    // 新增一条数据
                                    IData addParamData = (IData) Clone.deepClone(userParamData);
                                    addParamData.put("START_DATE", SysDateMgr.getNextSecond(cancelDate));
                                    addParamData.put("INST_ID", SeqMgr.getInstId());
                                    addParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                                    addParamData.put("ATTR_VALUE", paramValue);

                                    retParamList.add(addParamData);
                                }
                            }

                            retParamList.add(modParamData);
                        }

                        isExist = true;
                        break;
                    }
                }
                if (!isExist)
                {
                    if (paramKey != null && paramKey.length() > 3 && paramKey.substring(0, 3).equals("FEE"))
                    {
                        if ("".equals(paramValue))
                        {
                            paramValue = "0";
                        }
                        else
                        {
                            paramValue = String.valueOf(100 * Integer.parseInt(paramValue));
                        }
                    }

                    // 属性值为空不入表
                    if (StringUtils.isBlank(paramValue))
                    {
                        continue;
                    }

                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    map.put("INST_TYPE", model.getElementTypeCode());
                    map.put("RELA_INST_ID", relaInstId);
                    map.put("INST_ID", SeqMgr.getInstId());
                    map.put("ATTR_CODE", paramKey);
                    map.put("ATTR_VALUE", paramValue);
                    map.put("START_DATE", model.getStartDate());
                    map.put("END_DATE", model.getEndDate());
                    map.put("USER_ID", userId);
                    map.put("ELEMENT_ID", model.getElementId());
                    retParamList.add(map);
                }
            }
        }

        return retParamList;
    }

    /**
     * 处理费用和支付列表
     *
     * @param reqData
     * @throws Exception
     */
    public static void dealFeeAndPayModeList(CreateGroupUserReqData reqData) throws Exception
    {
        List<FeeData> feeList = reqData.getFeeList();

        // 不存在费用信息
        if (feeList == null || feeList.size() == 0)
        {
            return;
        }

        String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();
        String productId = reqData.getUca().getProductId();
        String eparchyCode = CSBizBean.getTradeEparchyCode();

        float money0 = 0; // 现金
        float moneyA = 0; // 挂账
        float moneyZ = 0; // 支票

        // 处理费用列表
        for (int i = feeList.size() - 1 ; i >= 0; i--)
        {
            FeeData feeData = feeList.get(i);
            String feeMode = feeData.getFeeMode();
            String feeTypeCode = feeData.getFeeTypeCode();
            String elementId = feeData.getDiscntGiftId();
            String money = feeData.getFee();

            
//            IDataset tradeFeeList = ProductFeeInfoQry.qryFeeByFeeModeAndFeeTypeCode(tradeTypeCode, productId, elementId, feeMode, feeTypeCode, eparchyCode);
//
//            if (IDataUtil.isNotEmpty(tradeFeeList))
//            {
//                IData tradeFeeData = tradeFeeList.getData(0);
//
//                String tradeFeePayMode = tradeFeeData.getString("PAY_MODE");
//
//                feeData.setPayMode(tradeFeePayMode);
//            }
            
            //暂时只处理产品费用( ELEMENT_ID 未空或者-1)的情况
            if(StringUtils.isEmpty(elementId) || elementId.equals("-1"))
            {
                IDataset tradeFeeList = ProductFeeInfoQry.getElementFeeList(tradeTypeCode, "P", productId, "-1", "-1", "-1");
                if(IDataUtil.isNotEmpty(tradeFeeList)){
                    
                    for(int f = 0 ; f < tradeFeeList.size(); f++){
                        IData tradeFeeData = tradeFeeList.getData(f);
                        String tempFeeMode = tradeFeeData.getString("FEE_MODE");
                        String tempFeeTypeCode = tradeFeeData.getString("FEE_TYPE_CODE");
                        if(StringUtils.equals(tempFeeTypeCode, feeTypeCode) && StringUtils.equals(tempFeeMode, feeMode)){
                            String tradeFeePayMode = tradeFeeData.getString("PAY_MODE");
                            feeData.setPayMode(tradeFeePayMode);
                        }
                        
                    }
                }
            }

            String payMode = feeData.getPayMode();

            if ("A".equals(payMode)) // 挂账
            {
                moneyA += Float.valueOf(money);
                
            }
            else if ("Z".equals(payMode)) // 支票
            {
                moneyZ += Float.valueOf(money);
            }
            else
            // 现金
            {
                money0 += Float.valueOf(money);
            }
        }

        // 处理支付列表
        List<PayMoneyData> payMoneyList = new ArrayList<PayMoneyData>();

        // 现金
        PayMoneyData payMoneyData0 = new PayMoneyData();
        payMoneyData0.setPayMoneyCode("0");
        payMoneyData0.setMoney(String.valueOf(money0));

        // 挂账
        PayMoneyData payMoneyDataA = new PayMoneyData();
        payMoneyDataA.setPayMoneyCode("A");
        payMoneyDataA.setMoney(String.valueOf(moneyA));

        // 支票
        PayMoneyData payMoneyDataZ = new PayMoneyData();
        payMoneyDataZ.setPayMoneyCode("Z");
        payMoneyDataZ.setMoney(String.valueOf(moneyZ));

        payMoneyList.add(payMoneyData0);
        payMoneyList.add(payMoneyDataA);
        payMoneyList.add(payMoneyDataZ);

        // 设置会话值
        reqData.setPayMoneyList(payMoneyList);
    }

    /**
     * 解析集团用户级别产品元素信息
     *
     * @param reqData
     * @param moduleData
     * @throws Exception
     */
    public static void grpElement(GroupReqData reqData, GrpModuleData moduleData) throws Exception
    {
        IData productIdData = new DataMap(); // 产品ID

        IDataset svcList = new DatasetList(); // 服务列表
        IDataset discntList = new DatasetList(); // 资费列表
        IDataset paramList = new DatasetList(); // 服务或资费参数列表

        IDataset specialParamList = new DatasetList(); // 特殊服务参数列表

        // 查询选择产品的所有服务的IMTEA配置: S-表示为服务; 9-表示为弹出窗口 显示元素参数
        //IDataset attrItemAList = AttrItemInfoQry.getelementItemaByProductId("S", "9", reqData.getUca().getProductId(), null);
        
        // 解析产品元素信息
        IDataset elementList = moduleData.getElementInfo();

        for (int i = 0, row = elementList.size(); i < row; i++)
        {
            IData elementData = elementList.getData(i); // 取每个元素
            if("99011015".equals(elementData.getString("ELEMENT_ID"))){
            if(TRADE_MODIFY_TAG.DEL.getValue().equals(elementData.getString("MODIFY_TAG"))){
            	elementData.put("END_DATE", delValidTime);
            }
            }
            
            boolean resetFlag = false;
            // 结束时间为空 则重算时间
            if (StringUtils.isBlank(elementData.getString("END_DATE", "").trim()))
            {
                resetFlag = true;
                // 预约时间
                String bookingDate = reqData.isIfBooking() ? SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000() : "";

                if (StringUtils.isNotEmpty(bookingDate) && TRADE_MODIFY_TAG.DEL.getValue().equals(elementData.getString("MODIFY_TAG")))
                {
                    bookingDate = SysDateMgr.getLastSecond(bookingDate);// 预约注销时间传下账期前一秒
                }

                // 重算时间
                ElementUtil.dealSelectedElementStartDateAndEndDate(elementData, bookingDate, reqData.isEffectNow(), BizRoute.getRouteId());
            }

            elementData.put("ACCEPT_TIME", reqData.getAcceptTime());
            elementData.put("IF_BOOKING", reqData.isIfBooking());
            elementData.put("EFFECT_NOW", reqData.isEffectNow());
            // elementData.put("CANCEL_TAG", "0"); //在重算时间的时候其实已经把CANCEL_TAG的值put进去
            String accepttime = reqData.getAcceptTime();
            if (!resetFlag)
            {
                // 获取元素的失效方式
                String cancelTag = ProductInfoQry.queryElementEnable(elementData.getString("PRODUCT_ID"), elementData.getString("PACKAGE_ID"), elementData.getString("ELEMENT_TYPE_CODE"), elementData.getString("ELEMENT_ID"), "CANCEL_TAG");

                elementData.put("CANCEL_TAG", cancelTag);
            }

            ElementModel model = new ElementModel(elementData);

            // 处理产品信息
            if (GroupBaseConst.PRODUCT_MODE.USER_PLUS_PRODUCT.getValue().equals(model.getProductMode()))
            {
                String modifyTag = productIdData.getString(model.getProductId());

                // 如果不存在, 则新增产品处理信息
                if (StringUtils.isEmpty(modifyTag))
                {
                    productIdData.put(model.getProductId(), TRADE_MODIFY_TAG.Add.getValue());
                }
                // 如果同时存在新增和删除的操作, 则不处理产品信息
                else if (StringUtils.isNotEmpty(modifyTag) && !modifyTag.equals(model.getModifyTag()))
                {
                    productIdData.remove(model.getProductId());
                }
            }

            String instId = ""; // 成实例ID

            if ("S".equals(model.getElementTypeCode())) // 处理服务和服务参数信息
            {
                // 1、处理服务信息

                if (TRADE_MODIFY_TAG.Add.getValue().equals(model.getModifyTag())) // 新增服务
                {
                    instId = SeqMgr.getInstId();
                    model.setInstId(instId); // 新增实例标记

                    // 转换数据, 添加服务信息
                    svcList.add(model.convertData());
                }
                else
                // 修改或删除服务
                {
                    // 查询用户服务信息
                    IDataset userSvcList = UserSvcInfoQry.getUserSingleProductSvc(reqData.getUca().getUserId(), null, model.getProductId(), model.getPackageId(), model.getElementId(), model.getInstId(), null);// 集团订购-1可不传，兼容老系统BBOSS
                    // user_id_a放的商品的user_id

                    if (IDataUtil.isEmpty(userSvcList))
                    {
                        CSAppException.apperr(ElementException.CRM_ELEMENT_49, model.getElementId());
                    }

                    IData userSvcData = userSvcList.getData(0);
                    String cancelDate = ElementUtil.getCancelDate(model, accepttime);
                    instId = userSvcData.getString("INST_ID");

                    // 设置属性
                    model.setStartDate(userSvcData.getString("START_DATE"));
                    model.setInstId(instId);

                    userSvcData.put("ELEMENT_ID", userSvcData.getString("SERVICE_ID"));
                    userSvcData.put("ELEMENT_TYPE_CODE", model.getElementTypeCode());
                    userSvcData.put("MODIFY_TAG", model.getModifyTag());

                    if (TRADE_MODIFY_TAG.DEL.getValue().equals(model.getModifyTag())) // 删除
                    {
                    	if("99011015".equals(model.getElementId())){
                    		userSvcData.put("END_DATE", cancelDate);
                    	}else{
                    		userSvcData.put("END_DATE", model.getEndDate());
                    	}
                    }

                    svcList.add(userSvcData); // 添加服务信息
                }

                // 2、处理服务参数信息

                // 如果存在以弹出串口显示服务参数,并且RSRV_STR1=1(是否走特殊处理开关标识)的ITEMA配置,则走各自特殊的服务参数处理
                IDataset attrItemAList = AttrBizInfoQry.getBizAttr(model.getElementId(), "S", "ServPage", reqData.getUca().getProductId(), null);
                attrItemAList = DataHelper.filter(attrItemAList, "RSRV_STR5=1");
                if (IDataUtil.isNotEmpty(attrItemAList))
                {
                    IDataset svcParamList = model.getAttrParam();

                    if (svcParamList.size() >= 2)
                    {
                        IData specialSvcParamData = svcParamList.getData(1);
                        specialSvcParamData.put("INST_ID", instId);
                        specialSvcParamData.put("MODIFY_TAG", model.getModifyTag());
                        specialSvcParamData.put("START_DATE", model.getStartDate());
                        specialSvcParamData.put("END_DATE", model.getEndDate());
                        specialParamList.add(svcParamList);
                    }
                }
                else
                {
                    // 处理服务参数信息
                    IDataset retSvcParamList = dealAttrParam(model, reqData.getUca().getUserId(), instId, reqData.getAcceptTime());

                    paramList.addAll(retSvcParamList);
                }
            }
            else if ("D".equals(model.getElementTypeCode())) // 处理资费和资费参数信息
            {
                // 1、 处理资费信息
                if (TRADE_MODIFY_TAG.Add.getValue().equals(model.getModifyTag()))
                {
                    instId = SeqMgr.getInstId();

                    model.setInstId(instId);

                    // 转换数据, 添加服务信息
                    discntList.add(model.convertData());
                }
                else
                {
                    IDataset userDiscntList = UserDiscntInfoQry.getUserSingleProductDisParser(reqData.getUca().getUserId(), null, model.getProductId(), model.getPackageId(), model.getElementId(), model.getInstId(), null);// 集团订购-1可不传，兼容老系统BBOSS
                    // user_id_a放的商品的user_id
                    if (IDataUtil.isEmpty(userDiscntList))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_619);
                    }

                    IData userDiscntData = userDiscntList.getData(0);

                    instId = userDiscntData.getString("INST_ID");

                    // 设置属性
                    model.setStartDate(userDiscntData.getString("START_DATE"));
                    model.setInstId(instId);

                    userDiscntData.put("ELEMENT_ID", userDiscntData.getString("DISCNT_CODE"));
                    userDiscntData.put("ELEMENT_TYPE_CODE", model.getElementTypeCode());
                    userDiscntData.put("MODIFY_TAG", model.getModifyTag());

                    // 删除资费信息
                    if (TRADE_MODIFY_TAG.DEL.getValue().equals(model.getModifyTag()))
                    {
                    	if("6".equals(elementData.getString("CANCEL_TAG", "")))
                    	{
                    		userDiscntData.put("END_DATE", elementData.getString("END_DATE", ""));

                    	}
                    	else
                    	{
                    	    userDiscntData.put("END_DATE", model.getEndDate());

                    	}
                    }

                    discntList.add(userDiscntData);
                }

                // 2、处理资费参数信息
                IDataset retDiscntParamList = dealAttrParam(model, reqData.getUca().getUserId(), instId, reqData.getAcceptTime());

                paramList.addAll(retDiscntParamList);
            }
        }

        // 设置解析的数据到reqData中
        reqData.cd.putProductIdSet(productIdData);
        reqData.cd.putSvc(svcList);
        reqData.cd.putDiscnt(discntList);
        reqData.cd.putElementParam(paramList);
        reqData.cd.putSpecialSvcParam(specialParamList);
    }

    /**
     * 解析集团级资源信息
     *
     * @param reqData
     * @param moduleData
     * @throws Exception
     */
    public static void grpRes(GroupReqData reqData, GrpModuleData moduleData) throws Exception
    {
        IDataset resList = moduleData.getResInfo();

        if (IDataUtil.isEmpty(resList))
        {
            return;
        }

        IDataset resDataset = commonRes(reqData.getUca().getUserId(), null, resList, reqData.getAcceptTime());// 集团订购-1可不传，兼容老系统BBOSS
        // user_id_a放的商品的user_id

        // 设置资源信息
        reqData.cd.putRes(resDataset);
    }

    /**
     * 解析成员用户级别产品元素信息
     *
     * @param reqData
     * @param moduleData
     * @throws Exception
     */
    public static void mebElement(MemberReqData reqData, GrpModuleData moduleData) throws Exception
    {
        IData productIdData = new DataMap(); // 产品ID

        IDataset svcList = new DatasetList(); // 服务列表
        IDataset spSvcList = new DatasetList(); // 平台服务列表
        IDataset discntList = new DatasetList(); // 资费列表
        IDataset paramList = new DatasetList(); // 服务或资费参数列表

        IDataset specialParamList = new DatasetList(); // 特殊服务参数列表

        // 成员基本产品
        String baseMebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        reqData.setBaseMebProductId(baseMebProductId);

        // 查询选择产品的所有服务的IMTEA配置: S-表示为服务; 9-表示为弹出窗口 显示元素参数
        //IDataset attrItemAList = AttrItemInfoQry.getelementItemaByProductId("S", "9", baseMebProductId, null);

        // 解析产品元素信息
        IDataset elementList = moduleData.getElementInfo();

        for (int i = 0, row = elementList.size(); i < row; i++)
        {
            IData elementData = elementList.getData(i); // 取每个元素
            elementData.put("ACCEPT_TIME", reqData.getAcceptTime());
            elementData.put("IF_BOOKING", reqData.isIfBooking());
            elementData.put("EFFECT_NOW", reqData.isEffectNow());
            String accepttime = reqData.getAcceptTime();
            if("99011019".equals(elementData.getString("ELEMENT_ID"))){
            if(TRADE_MODIFY_TAG.DEL.getValue().equals(elementData.getString("MODIFY_TAG"))){
            	elementData.put("END_DATE", delValidTime);
            }
            }
          
            // elementData.put("CANCEL_TAG", "0"); //在重算时间的时候其实已经把CANCEL_TAG的值put进去
            
            // 兼容下前台或者接口某些业务productId传的不对的情况, 按道理应该要报错的
            if(StringUtils.equals(elementData.getString("PRODUCT_ID"), reqData.getGrpUca().getProductId()) && !StringUtils.equals("801110", elementData.getString("PRODUCT_ID")))
            {
                elementData.put("PRODUCT_ID", baseMebProductId);
            }

            boolean resetFlag = false;
            // 结束时间为空 则重算时间
            if (StringUtils.isBlank(elementData.getString("END_DATE", "").trim()))
            {
                resetFlag = true;
                // 预约时间
                String bookingDate = reqData.isIfBooking() ? SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000() : "";

                if (StringUtils.isNotEmpty(bookingDate) && TRADE_MODIFY_TAG.DEL.getValue().equals(elementData.getString("MODIFY_TAG")))
                {
                    bookingDate = SysDateMgr.getLastSecond(bookingDate);// 预约注销时间传下账期前一秒
                }

                // 重算时间
                ElementUtil.dealSelectedElementStartDateAndEndDate(elementData, bookingDate, reqData.isEffectNow(), BizRoute.getRouteId());
            }

            if (!resetFlag)
            {
                // 获取元素的失效方式
                String cancelTag = ProductInfoQry.queryElementEnable(elementData.getString("PRODUCT_ID"), elementData.getString("PACKAGE_ID"), elementData.getString("ELEMENT_TYPE_CODE"), elementData.getString("ELEMENT_ID"), "CANCEL_TAG");
                elementData.put("CANCEL_TAG", cancelTag);
            }

            // 处理分散接口数据
            if (InModeCodeUtil.isIntf(getVisit().getInModeCode(), reqData.getXTransCode(), reqData.getBatchId()))
            {
                IData userAcctDay = DiversifyAcctUtil.getUserAcctDay(reqData.getUca().getUserId());

                if (IDataUtil.isNotEmpty(userAcctDay))
                {
                    // 用户为分散用户, 处理元素时间
                    if (!DiversifyAcctUtil.checkUserAcctDay(userAcctDay, "1", false))
                    {
                        if (!"1".equals(elementData.getString("DIVERSIFY_ACCT_TAG", "")))
                        {
                            IData param = new DataMap();
                            param.put(GroupBaseConst.DIVERSIFY_BOOKING, reqData.isDiversifyBooking());
                            param.put(GroupBaseConst.EFFECT_NOW, reqData.isEffectNow());
                            param.put("ACCEPT_TIME", reqData.getAcceptTime());
                            param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

                            // 处理分散用户元素时间
                            DiversifyAcctUtil.dealDiversifyElementStartAndEndDate(param, elementData);
                            // DIVERSIFY_ACCT_TAG = "1" 表示已经处理, 在TradeBaseBean不再处理
                            elementData.put("DIVERSIFY_ACCT_TAG", "1");
                        }
                    }
                }
            }

            ElementModel model = new ElementModel(elementData); // 构建模型数据

            // 处理成员附加产品信息
            if (GroupBaseConst.PRODUCT_MODE.MEM_PLUS_PRODUCT.getValue().equals(model.getProductMode()))
            {
                String modifyTag = productIdData.getString(model.getProductId());

                // 如果不存在, 则新增产品处理信息
                if (StringUtils.isEmpty(modifyTag))
                {
                    productIdData.put(model.getProductId(), TRADE_MODIFY_TAG.Add.getValue());
                }
                // 如果同时存在新增和删除的操作, 则不处理产品信息
                else if (StringUtils.isNotEmpty(modifyTag) && !modifyTag.equals(model.getModifyTag()))
                {
                    productIdData.remove(model.getProductId());
                }
            }

            String instId = ""; // 实例ID

            if ("S".equals(model.getElementTypeCode())) // 处理服务
            {
                // 1、处理服务信息
                if (TRADE_MODIFY_TAG.Add.getValue().equals(model.getModifyTag())) // 新增服务
                {
                    instId = SeqMgr.getInstId(); // 生成新的实例ID

                    model.setInstId(instId);

                    String isNeedPf = GrpPfUtil.getSvcPfState(model.getModifyTag(), reqData.getUca().getUserId(), model.getElementId());

                    model.setIsNeedPf(isNeedPf);
                }
                else
                // 修改或删除服务信息
                {
                    // 查询用户服务信息
                    IDataset userSvcList = UserSvcInfoQry.getUserSingleProductSvc(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), model.getProductId(), model.getPackageId(), model.getElementId(), model.getInstId(), null);
                    String cancelDate = ElementUtil.getCancelDate(model, accepttime);
                    if (IDataUtil.isEmpty(userSvcList))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_397);
                    }

                    IData userSvcData = userSvcList.getData(0);

                    instId = userSvcData.getString("INST_ID"); // 获取实例ID

                    // 设置属性
                    model.setInstId(instId);
                    model.setStartDate(userSvcData.getString("START_DATE"));

                    if (TRADE_MODIFY_TAG.MODI.getValue().equals(model.getModifyTag())) // 修改服务
                    {
                        model.setEndDate(userSvcData.getString("END_DATE"));
                    }
                    else if (TRADE_MODIFY_TAG.DEL.getValue().equals(model.getModifyTag())) // 删除服务
                    {  
                    	if("99011019".equals(model.getElementId())){
                    		 model.setEndDate(cancelDate);
                         }else{
                        	 model.setEndDate(model.getEndDate());
                         }
                        
                        String isNeedPf = GrpPfUtil.getSvcPfState(model.getModifyTag(), reqData.getUca().getUserId(), model.getElementId());

                        model.setIsNeedPf(isNeedPf);
                    }
                }

                // 转换数据, 添加服务信息
                svcList.add(model.convertData());

                // 2、处理服务参数信息

                // 如果存在以弹出串口显示服务参数, 并且RSRV_STR1=1(是否走特殊处理开关标识)的ITEMA配置, 则走各自特殊的服务参数处理
                //if (DataHelper.filter(attrItemAList, "ID=" + model.getElementId() + ",RSRV_STR1=1").size() > 0)
                IDataset attrItemAList = AttrBizInfoQry.getBizAttr(model.getElementId(), "S", "ServPage", baseMebProductId, null);
                attrItemAList = DataHelper.filter(attrItemAList, "RSRV_STR5=1");
                if(IDataUtil.isNotEmpty(attrItemAList))
                {
                    IDataset svcParamList = model.getAttrParam();
                    int svcParamSize = svcParamList.size();
					if(svcParamSize <2)
					{ //对于直接取消的服务信息,会没有serverparam
						IDataset servParam_temp=new DatasetList();
						IData serviceparamdata = new DataMap();
						serviceparamdata.put("INST_ID", instId);
						serviceparamdata.put("MODIFY_TAG", model.getModifyTag());
						serviceparamdata.put("START_DATE", model.getStartDate());
						serviceparamdata.put("END_DATE", model.getEndDate());

						IData platsvcdata=new DataMap();
						platsvcdata.put("SERVICE_ID", model.getElementId());
						platsvcdata.put("EXPECT_TIME", model.getEndDate());
						serviceparamdata.put("PLATSVC", platsvcdata);

						servParam_temp.add(0,new DataMap());
						servParam_temp.add(1,serviceparamdata);
						specialParamList.add(servParam_temp);
					}
                    if (svcParamSize >= 2)
                    {//对于新增修改
                        IData specialSvcParamData = svcParamList.getData(1);
                        specialSvcParamData.put("INST_ID", instId);
                        specialSvcParamData.put("MODIFY_TAG", model.getModifyTag());
                        specialSvcParamData.put("START_DATE", model.getStartDate());
                        specialSvcParamData.put("END_DATE", model.getEndDate());
                        specialParamList.add(svcParamList);
                    }
                }
                else
                {
                    // 处理服务参数信息
                    IDataset retSvcParamList = dealAttrParam(model, reqData.getUca().getUserId(), instId, reqData.getAcceptTime());

                    // 添加服务参数信息
                    paramList.addAll(retSvcParamList);
                }
            }
            else if ("D".equals(model.getElementTypeCode())) // 处理资费
            {
                // 1、 处理资费信息
                if (TRADE_MODIFY_TAG.Add.getValue().equals(model.getModifyTag())) // 新增资费
                {
                    instId = SeqMgr.getInstId(); // 生成新的实例ID
                    model.setInstId(instId);
                }
                else
                {
                    // 查询用户资费信息
                    IDataset userDiscntList = UserDiscntInfoQry.getUserSingleProductDisParser(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), model.getProductId(), model.getPackageId(), model.getElementId(), model.getInstId(), null);

                    if (IDataUtil.isEmpty(userDiscntList))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_408);
                    }

                    IData userDiscntData = userDiscntList.getData(0);

                    instId = userDiscntData.getString("INST_ID");

                    // 设置属性
                    model.setInstId(instId);
                    model.setStartDate(userDiscntData.getString("START_DATE"));

                    if (TRADE_MODIFY_TAG.MODI.getValue().equals(model.getModifyTag())) // 修改
                    {
                        model.setEndDate(userDiscntData.getString("END_DATE"));
                    }
                    else if (TRADE_MODIFY_TAG.DEL.getValue().equals(model.getModifyTag())) // 删除
                    {
                        model.setEndDate(model.getEndDate());
                    }
                }
                // 添加资费信息到资费列表中
                discntList.add(model.convertData());

                // 2、 处理资费参数信息
                IDataset retDiscntParamList = dealAttrParam(model, reqData.getUca().getUserId(), instId, reqData.getAcceptTime());

                paramList.addAll(retDiscntParamList);
            }
            else if ("Z".equals(model.getElementTypeCode())) // 处理平台服务信息
            {
                instId = SeqMgr.getInstId(); // 生成新的实例ID

                model.setInstId(instId);

                model.setRsrvTag2("1");
                // 添加平台服务到平台列表中
                spSvcList.add(model.convertData());
            }
        }

        // 设置解析的数据到reqData中
        reqData.cd.putProductIdSet(productIdData);
        reqData.cd.putSvc(svcList);
        reqData.cd.putSpSvc(spSvcList);
        reqData.cd.putDiscnt(discntList);
        reqData.cd.putElementParam(paramList);
        reqData.cd.putSpecialSvcParam(specialParamList);
    }

    /**
     * 解析成员付费关系
     *
     * @param reqData
     * @param moduleData
     * @throws Exception
     */
    public static void mebPayRelation(MemberReqData reqData, GrpModuleData moduleData) throws Exception
    {
        String planTypeCode = moduleData.getPlanTypeCode();

        if (StringUtils.isBlank(planTypeCode))
        {
            planTypeCode = "P"; // 接口调过来可能没传则默认个人付费
        }

        boolean ifBooking = reqData.isIfBooking(); // 是否预约
        String firstTimeNextMonth = SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000();
        String lastTimeThisMonth = SysDateMgr.getLastDateThisMonth();

        IData userAcctDay = DiversifyAcctUtil.getUserAcctDay(reqData.getUca().getUserId());

        if (IDataUtil.isNotEmpty(userAcctDay))
        {
            // 用户为分散用户, 处理元素时间
            if (!DiversifyAcctUtil.checkUserAcctDay(userAcctDay, "1", false))
            {
                String lastDayThisAcct = userAcctDay.getString("LAST_DAY_THISACCT", "");
                String fisrtDayNextAcct = userAcctDay.getString("FIRST_DAY_NEXTACCT", "");
                if (StringUtils.isNotBlank(lastDayThisAcct))
                {
                    lastTimeThisMonth = lastDayThisAcct + SysDateMgr.getEndTime235959();
                }
                if (StringUtils.isNotBlank(fisrtDayNextAcct))
                {
                    firstTimeNextMonth = fisrtDayNextAcct + SysDateMgr.getFirstTime00000();
                }
            }
        }

        IData payPlanData = new DataMap();

        // 查询成员用户付费关系
        IDataset userPayPlanList = UserPayPlanInfoQry.getGrpMemPayPlanByUserId(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());

        // 如果不存在付费计划则新增付费计划
        if (IDataUtil.isEmpty(userPayPlanList))
        {
            payPlanData.put(planTypeCode, TRADE_MODIFY_TAG.Add.getValue());

        }
        else
        // 如果存在付费计划则判断是新增还是删除
        {
            IData userPayPlanData = userPayPlanList.getData(0);

            String userPlanTypeCode = userPayPlanData.getString("PLAN_TYPE_CODE");

            if (userPlanTypeCode.equals(planTypeCode))
            {
                // CSAppException.apperr(GrpException.CRM_GRP_640);
            }

            payPlanData.put(planTypeCode, TRADE_MODIFY_TAG.Add.getValue()); // 新增付费计划
            payPlanData.put(userPlanTypeCode, TRADE_MODIFY_TAG.DEL.getValue()); // 删除付费计划
        }

        // 处理付费计划
        IDataset payPlanList = new DatasetList(); // 付费计划

        IDataset sepcialPayList = new DatasetList(); // 特殊付费

        IDataset payRelaList = new DatasetList(); // 付费关系

        Iterator<String> iterator = payPlanData.keySet().iterator();

        while (iterator.hasNext())
        {
            String key = (String) iterator.next();

            String modifyTag = payPlanData.getString(key);

            String planName = StaticUtil.getStaticValue("PAYPLAN_PLANTYPE", key);

            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag)) // 新增付费关系处理
            {
                // 新增付费计划
                IData addPayPlanData = new DataMap();
                addPayPlanData.put("USER_ID", reqData.getUca().getUserId());
                addPayPlanData.put("USER_ID_A", reqData.getGrpUca().getUserId());
                addPayPlanData.put("PLAN_ID", SeqMgr.getPlanId());
                addPayPlanData.put("PLAN_TYPE_CODE", key);
                addPayPlanData.put("PLAN_NAME", planName);
                addPayPlanData.put("PLAN_DESC", planName);
                addPayPlanData.put("START_DATE", ifBooking ? firstTimeNextMonth : reqData.getAcceptTime());
                addPayPlanData.put("END_DATE", SysDateMgr.getTheLastTime());
                addPayPlanData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                payPlanList.add(addPayPlanData);

                // 处理集团付费
                if ("G".equals(key))
                {
                    // 获取付费编码
                    String payItemCode = AcctInfoQry.getPayItemCode(reqData.getGrpUca().getProductId());

                    if (payItemCode.equals(reqData.getGrpUca().getProductId()))//-1表示代付所有付费项
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_634);
                    }

                    // 统一付费业务副卡的号码办理集团统付业务时(包括所有的集团统付办理途径均要限制)要进行提示限制办理
                    IDataset relaList = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(reqData.getUca().getUserId(), "56", "2");

                    if (IDataUtil.isNotEmpty(relaList))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_635, reqData.getUca().getSerialNumber());
                    }

                    String startcycleid = ifBooking ? SysDateMgr.getNextCycle() : SysDateMgr.getNowCyc();
                    IData paramData = new DataMap();
                    paramData.put("START_CYCLE_ID", startcycleid);
                    GroupCycleUtil.dealPayRelaCycle(paramData); // 获取八位的START_CYCLE_ID

                    // 判断是否有付费关系
                    IDataset userPayRelaList = PayRelaInfoQry.getPyrlByPk(reqData.getUca().getUserId(), reqData.getGrpUca().getAcctId(), payItemCode, paramData.getString("START_CYCLE_ID"), null);

                    if (IDataUtil.isNotEmpty(userPayRelaList))
                    {
                        IData modPayRelaData = userPayRelaList.getData(0);
                        modPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                        // 添加付费关系
                        payRelaList.add(modPayRelaData);

                        IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(reqData.getUca().getUserId(), reqData.getGrpUca().getAcctId(), payItemCode);

                        if (IDataUtil.isNotEmpty(userSpecialPayList))
                        {
                            IData modSpecialPayData = userSpecialPayList.getData(0);
                            modSpecialPayData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                            // 添加特殊付费
                            sepcialPayList.add(modSpecialPayData);
                        }
                    }
                    else
                    {
                        // 新增付费关系
                        IData addPayRelaData = new DataMap();

                        addPayRelaData.put("USER_ID", reqData.getUca().getUserId());
                        addPayRelaData.put("ACCT_ID", reqData.getGrpUca().getAcctId());
                        addPayRelaData.put("PAYITEM_CODE", payItemCode);
                        addPayRelaData.put("ACCT_PRIORITY", "0"); // 账户优先级
                        addPayRelaData.put("USER_PRIORITY", "0"); // 用户优先级
                        addPayRelaData.put("BIND_TYPE", "0"); // 账户绑定方式
                        addPayRelaData.put("DEFAULT_TAG", "0"); // 默认标志
                        addPayRelaData.put("ACT_TAG", "1"); // 作用标志
                        addPayRelaData.put("LIMIT_TYPE", "0"); // 限定方式
                        addPayRelaData.put("LIMIT", "0"); // 限定值
                        addPayRelaData.put("COMPLEMENT_TAG", "0"); // 限定值
                        addPayRelaData.put("INST_ID", SeqMgr.getInstId()); // inst_id
                        addPayRelaData.put("START_CYCLE_ID", ifBooking ? SysDateMgr.getNextCycle() : SysDateMgr.getNowCyc()); // 开始账期
                        // 基类有对6位的处理
                        addPayRelaData.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231()); // 开始账期
                        addPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                        // 添加付费关系
                        payRelaList.add(addPayRelaData);

                        IData addSpecialPayData = (IData) Clone.deepClone(addPayRelaData);

                        addSpecialPayData.put("USER_ID_A", reqData.getGrpUca().getUserId());
                        addSpecialPayData.put("ACCT_ID_B", reqData.getUca().getAcctId());

                        addSpecialPayData.put("INST_ID", SeqMgr.getInstId()); // inst_id

                        // 添加特殊付费
                        sepcialPayList.add(addSpecialPayData);
                    }
                }
            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag)) // 删除付费关系处理
            {
                if (IDataUtil.isEmpty(userPayPlanList))
                {
                    continue;
                }

                // 注销付费计划
                IData userPayPlanData = userPayPlanList.getData(0);
                userPayPlanData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                userPayPlanData.put("END_DATE", ifBooking ? lastTimeThisMonth : reqData.getAcceptTime());

                userPayPlanList.add(userPayPlanData);

                if ("G".equals(planTypeCode))
                {
                    // 注销特殊付费关系
                    IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());

                    if (IDataUtil.isEmpty(userSpecialPayList))
                    {
                        continue;
                    }

                    IData userSpecialPayData = userSpecialPayList.getData(0);

                    userSpecialPayData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    userSpecialPayData.put("END_CYCLE_ID", SysDateMgr.getNowCyc());

                    sepcialPayList.add(userSpecialPayData);

                    String payItemCode = userSpecialPayData.getString("PAYITEM_CODE", "");

                    // 注销付费关系
                    IDataset userPayRelaList = PayRelaInfoQry.getPyrlByPk(reqData.getUca().getUserId(), reqData.getGrpUca().getAcctId(), payItemCode, userSpecialPayData.getString("START_CYCLE_ID"), null);

                    if (IDataUtil.isNotEmpty(userPayRelaList))
                    {
                        IData userPayRelaData = userPayRelaList.getData(0);

                        userPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        userPayRelaData.put("END_CYCLE_ID", SysDateMgr.getNowCyc());

                        payRelaList.add(userPayRelaData);
                    }
                }
            }
        }

        // 设置会话数据
        reqData.cd.putPayPlan(payPlanList);
        reqData.cd.putPayRelation(payRelaList);
        reqData.cd.putSpecialPay(sepcialPayList);
    }

    /**
     * 解析成员级资源信息
     *
     * @param reqData
     * @param moduleData
     * @throws Exception
     */
    public static void mebRes(MemberReqData reqData, GrpModuleData moduleData) throws Exception
    {
        IDataset resList = moduleData.getResInfo();

        if (IDataUtil.isEmpty(resList))
        {
            return;
        }

        IDataset resDataset = commonRes(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), resList, reqData.getAcceptTime());

        // 设置资源信息
        reqData.cd.putRes(resDataset);
    }

    /**
     * 参数数据IDataset转化为IData
     *
     * @param attrParamList
     * @return
     * @throws Exception
     */
    public static IData transAttrList2Map(IDataset attrParamList) throws Exception
    {
        IData attrParamMap = new DataMap();

        for (int i = 0, iSize = attrParamList.size(); i < iSize; i++)
        {
            IData paramData = attrParamList.getData(i);
            if (paramData.containsKey("ATTR_CODE"))
            {
                String attrCode = paramData.getString("ATTR_CODE");
                String attrValue = paramData.getString("ATTR_VALUE", "");

                // 金额转化(元转分)
                attrValue = transFee(attrCode, attrValue);
                 //减免条数转化为减免金额
                attrValue = transFeeByAmount(attrCode, attrValue);


                attrParamMap.put(attrCode, attrValue);
            }
        }
        return attrParamMap;
    }

    /**
     * 费用金额转换(元转分)
     *
     * @param attrCode
     * @param attrValue
     * @return
     * @throws Exception
     */
    public static String transFee(String attrCode, String attrValue) throws Exception
    {
        IDataset grpFeeList = StaticUtil.getStaticList("GROUP_TRASN_FEE", attrCode);

        // 不能匹配
        if (IDataUtil.isEmpty(grpFeeList))
        {
            return attrValue;
        }

        Pattern p = Pattern.compile("^[0-9]+\\.{0,1}[0-9]{0,2}");

        // 费用金额格式不匹配
        if (!p.matcher(attrValue).find())
        {
            CSAppException.apperr(FeeException.CRM_FEE_17);
        }
        if("30011117".equals(attrCode) || "30011107".equals(attrCode)
                || "30011337".equals(attrCode) ||"30011327".equals(attrCode)||"30011137".equals(attrCode) || "30011127".equals(attrCode)
                || "30011237".equals(attrCode) ||"30011227".equals(attrCode)
                || "40011107".equals(attrCode) ||"40011108".equals(attrCode)
                || "40011127".equals(attrCode) ||"40011128".equals(attrCode)
                || "40011227".equals(attrCode) ||"40011228".equals(attrCode)
                || "40011327".equals(attrCode) ||"40011328".equals(attrCode))
               {
               	  attrValue = String.valueOf(new BigDecimal(attrValue).multiply(new BigDecimal(1000)));
               }
        else
        {
            attrValue = String.valueOf(new BigDecimal(attrValue).multiply(new BigDecimal(100)));
        }

        attrValue = attrValue.indexOf(".") > 0 ? attrValue.substring(0, attrValue.indexOf(".")) : attrValue;

        return attrValue;
    }

    /**
     * 减免条数转化为金额(1000条短信转化为1000*100分)
     *
     * @param attrCode
     * @param attrValue
     * @return
     * @throws Exception
     */
    public static String transFeeByAmount(String attrCode, String attrValue) throws Exception
    {
        IDataset grpFeeList = StaticUtil.getStaticList("GROUP_TRASN_FEE_BY", attrCode);

        // 不能匹配
        if (IDataUtil.isEmpty(grpFeeList))
        {
            return attrValue;
        }

        Pattern p = Pattern.compile("^[0-9]+\\.{0,3}[0-9]{0,2}");

        // 费用金额格式不匹配
        if (!p.matcher(attrValue).find())
        {
            CSAppException.apperr(FeeException.CRM_FEE_17);
        }
        DecimalFormat df=new DecimalFormat("#.#");

        if("30011116".equals(attrCode) || "30011106".equals(attrCode)
         || "30011336".equals(attrCode) ||"30011326".equals(attrCode))
        {
        	  attrValue = String.valueOf(df.format((Double.parseDouble(attrValue) * 100)));

        }
        else if
        ("30011136".equals(attrCode) || "30011126".equals(attrCode)
                || "30011236".equals(attrCode) ||"30011226".equals(attrCode))
        {
        	attrValue = String.valueOf(df.format((Double.parseDouble(attrValue) * 300)));

        }
        return attrValue;
    }
}
