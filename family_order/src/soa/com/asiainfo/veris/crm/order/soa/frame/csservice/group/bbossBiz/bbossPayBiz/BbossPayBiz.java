
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossPayBiz;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BbossXmlMainInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeMember.ChangeBBossMemBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeMember.changeBBossRevsMemDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossOrderVerifyBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossPayBizInfoDealbean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealBBossRspInfoBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * 处理流量统付业务 反向订购流量叠加包
 * 
 * @author chenyi 2014-7-14
 */
public class BbossPayBiz
{

    /**
     * 反向订购流量叠加包 反向生成订单或者归档
     * 
     * @param data
     * @throws Exception
     */
    protected static IDataset crtBbossPayBiz(IData map) throws Exception
    {
     // 1- 定义返回结果集
        IDataset result = new DatasetList();
        
        // 2- 判断是否存在成员BB关系
        boolean isBBRelExist = checkIsRelaBBExist(map);

        String memSerialNumber = map.getString("SERIAL_NUMBER");
        // 3- 外省号码判断是否有在途工单

//        IDataset tradeInfo = TradeInfoQry.CheckIsExistNotBBSSFinishedTrade(memSerialNumber);
//        if (IDataUtil.isNotEmpty(tradeInfo))
//        {
//            int count = tradeInfo.getData(0).getInt("ROW_COUNT");
//            if (count>0)
//            {
//                rigisitXmlInfo(map);
//                return result;
//            }
//        }

        if(isBBRelExist)
        {
            // 2-1 如果是有订单就归档，如果没有订单 直接生成订单完工
            if (DealBBossRspInfoBean.isBBossRspFile(map, false)) // 如果是归档报文，则进行归档处理
            {
                result = IDataUtil.idToIds(DealBBossRspInfoBean.dealMebRspFile(map));
            }
            else
            {
                //IData returnVal = changeBBossRevsMemDataBean.makeData(map);
                //ChangeBBossMemBean bean = new ChangeBBossMemBean();
                //result = bean.crtOrder(returnVal);
            	 CreateFlowOverlyingMebBean bean = new CreateFlowOverlyingMebBean();
                 result = bean.crtTrade(map);
            }
            
        }
        // 3- BB关系不存在或成员用户信息不存在
        else 
        {
            CreateFlowOverlyingMebBean bean = new CreateFlowOverlyingMebBean();
            result = bean.crtTrade(map);
        }
        
        return result;
    }

    private static void rigisitXmlInfo(IData param)throws Exception {
        //1-  获取服务号码
        IDataset member_numberlist = IDataUtil.getDatasetSpecl("MEMBER_NUMBER", param);

        //2- 获取订购流量叠加包
        IDataset member_order_ratelist = IDataUtil.getDatasetSpecl("MEMBER_ORDER_RATE", param);

        //3 循环拆单
        for (int i = 0, sizeI = member_numberlist.size(); i < sizeI; i++)
        {
            IData singleMemberInfo = (IData) Clone.deepClone(param);
            singleMemberInfo.put("SERIAL_NUMBER", member_numberlist.get(i).toString());
            singleMemberInfo.put("MEMBER_ORDER_RATE", member_order_ratelist.get(i).toString());

            //鉴权处理，鉴权失败，直接登记xml_info表为2状态，gtm不再扫描处理
            singleMemberInfo.put("DEAL_STATE", "0");//0代表等待处理
            singleMemberInfo.put("NOT_RSP", "Y");
            CSAppCall.call("CS.bbossCenterControlSVC.rigisitXmlInfo",singleMemberInfo);
        }
    }
    /**
     * 判断成员订购关系是否存在
     * @param map
     * @return
     * @throws Exception
     */
    private static boolean checkIsRelaBBExist(IData map) throws Exception
    {
        
        String productSpecCode = map.getString("PRODUCT_SPEC_NUMBER");// BBOSS侧产品用户编码
        String productOfferId = map.getString("PRODUCTID", "");
        
        IDataset userGrpMerchInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);
        if (IDataUtil.isEmpty(userGrpMerchInfoList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
        }
        IData userGrpMerchInfo = userGrpMerchInfoList.getData(0);
        String productUserId = userGrpMerchInfo.getString("USER_ID");
        map.put("USER_ID", productUserId); //集团用户编码

        //  如果成员信息不存在直接返回false
        String memberUserId = MebCommonBean.getMemberUserId(map);
        if(StringUtils.isBlank(memberUserId))
        {
            return false;
        }
        
        String merchpId = GrpCommonBean.merchToProduct(productSpecCode,2,null);
        String relationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId("", merchpId, false);
        IDataset relaBBInfoList = RelaBBInfoQry.qryRelationBBAll(productUserId,memberUserId, relationTypeCode);

        if(IDataUtil.isNotEmpty(relaBBInfoList))
        {
            return true;
        }
        return false;
        
    }
    
    /**
     * @description 叠加包开通
     * @author xunyl
     * @date 2015-09-23
     */
    protected static IDataset bbossPayBizOrderOpenChkMeb(IData data)throws Exception
    {    
        //1- 成员开通
        IData resultInfo = payBizOpen(data);
        
        //2- BIP4B262_T4011057_0_0接口调用规则(文件接口不调用，实时接口必须所有成员开通后才调用)
        String resultCode =resultInfo.getString("RESULT_CODE","");
        String resultDesc =resultInfo.getString("RESULT_DESC","");
        IDataset openResultInfoList = new DatasetList();
        String returnFlag = data.getString("RETURN_FLAG_KT", "");
        if(!"".equals(returnFlag)){
            IDataset memberNumberInfoList = new DatasetList();
            IDataset statusInfoList = new DatasetList();
            IDataset descInfoList = new DatasetList();          
            memberNumberInfoList.add(data.getString("SERIAL_NUMBER",""));
            statusInfoList.add(resultCode);
            descInfoList.add(resultDesc);
            IData openResultInfo = new DataMap();
            openResultInfo.put("MEMBER_NUMBER", memberNumberInfoList);
            openResultInfo.put("FAIL_STATUS", statusInfoList);
            openResultInfo.put("FAIL_DESC", descInfoList);
            openResultInfoList.add(openResultInfo);  
            return openResultInfoList;
        }
        
        //3- 如果有未完工工单，进入AEE队列处理模式，否则往TF_TP_BBOSS_XML_INFO表写入开通结果       
        if(StringUtils.isNotBlank(resultDesc) && (resultDesc.contains("CRM_COMM_983") ||
                resultDesc.contains("CRM_COMM_982") || resultDesc.contains("0998") ||
                resultDesc.contains("用户有未完工的订单") || resultDesc.contains("业务受理中，请稍后"))){
            BbossXmlMainInfoQry.updXmlMainDealState(data.getString("SEQ_ID"),"0");//0代表等待处理                   
        }else{              
             IDataset xmlMainInfoList= BbossXmlMainInfoQry.qryXmlMainInfoBySeqId(data.getString("SEQ_ID"));
             IData xmlMainInfo = xmlMainInfoList.getData(0);
             xmlMainInfo.put("OPEN_RESULT_CODE", resultCode);             
             xmlMainInfo.put("OPEN_RESULT_DESC", resultDesc);   
             if(resultDesc.length()>100){
                xmlMainInfo.put("OPEN_RESULT_DESC", "其它错误");
                String[] detailErrArr = MebCommonBean.splitStringByBytes(resultDesc,4000);
                for(int i=0;i<10;i++){
                    xmlMainInfo.put("ERROR_INFO_"+(i+1), detailErrArr[i]);
                }                           
             }
             xmlMainInfo.put("DEAL_STATE", "2");//2代表叠加包开通或者成员开通    
             Dao.update("TF_TP_BBOSS_XML_INFO", xmlMainInfo,null,Route.CONN_CRM_CEN);   
        }                           
        return openResultInfoList;
    }
    
    /**
     * @description 叠加包开通
     * @author xunyl
     * @date 2015-09-23
     */
    private static IData payBizOpen(IData memberInfo)throws Exception{
        //1- 初始化开通结果
        IData openResult = new DataMap();
        openResult.put("RESULT_CODE", "00");
        openResult.put("RESULT_DESC", "开通成功");       
        
        //2- 叠加包开通接口调用
        try
        {
            CSAppCall.call("CS.BbossPayBizSVC.crtBbossPayBiz", memberInfo);
        }
        catch(Exception e)
        {     
            String errRspCode = "";
            String errRspDesc = "";
            if (e.getMessage().indexOf("`") >= 1)// 预防抛出的异常信息格式不规范
            {
                errRspCode = e.getMessage().substring(0,
                        e.getMessage().indexOf("`"));
                errRspDesc = e.getMessage().substring(
                        e.getMessage().indexOf("`") + 1);
                if (errRspDesc.contains("无用户数据")
                        || errRspDesc.contains("非移动号码")
                        || errRspDesc.contains("已销户")
                        || errRspDesc.contains("非移动号码")
                        || errRspDesc.contains("服务状态处于非正常状态")) {
                    errRspCode = "02";
                } else {
                    errRspCode = "05";
                }
                if (errRspDesc.length() > 300) {
                    errRspDesc.substring(0, 300);

                }
            } else {
                errRspCode = "05";
                errRspDesc = "其它错误";
            }

            openResult.put("RESULT_CODE", errRspCode);
            openResult.put("RESULT_DESC", errRspDesc);                     
        }
        
        //3- 返回开通结果
        return openResult;
    }
    
    /**
     * chenyi 2014-7-14 流量叠加包开通工单处理
     * modify chengjian
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public  static IData bbossPayBizOrderOpenChkGrp(IData data) throws Exception
    {
        // 1-初始化返回结果
        IData map = new DataMap();
        
        map.put("ORDER_RESULT", "00");
        map.put("MEMBER_ORDER_NUMBER", data.getString("MEMBER_ORDER_NUMBER", ""));
        map.put("X_RESULTINFO".toUpperCase(), IntfField.SUUCESS_CODE[1]);
        map.put("X_RESULTCODE".toUpperCase(), IntfField.SUUCESS_CODE[0]);
        map.put("RSPCODE", "0000");
        
        // 2-校验BBOSS侧产品订购关系是否存在
        String offerId = data.getString("PRODUCT_ID", "");
        IDataset merchPInfos = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(offerId);
        if (IDataUtil.isEmpty(merchPInfos))
        {
            map.clear();
            map.put("MEMBER_ORDER_NUMBER", data.getString("MEMBER_ORDER_NUMBER", ""));
            map.put("ORDER_RESULT", "05");
            map.put("RESULT_DESC", "根据产品订单编号:[" + offerId + "]找不到对应的订购关系");
            return map;
        }

        // 3-校验产品集团下发的产品编码和本地对应的产品编码是否一致
        String productSpecNumber = data.getString("PRODUCT_SPEC_NUMBER", "");
        IData merchPInfo = merchPInfos.getData(0);
        if (!productSpecNumber.equals(merchPInfo.getString("PRODUCT_SPEC_CODE", "")))
        {
            map.clear();
            map.put("MEMBER_ORDER_NUMBER", data.getString("MEMBER_ORDER_NUMBER", ""));
            map.put("ORDER_RESULT", "05");
            map.put("RESULT_DESC", "集团下发的产品编码和本地对应的产品编码不一致");
            return map;
        }

        // 4-非流量统付业务，不能办理该业务
        String productId = GrpCommonBean.merchToProduct(productSpecNumber, 2, "");
        if (!BbossPayBizInfoDealbean.isFluxTFBusiness(productId))
        {
            map.clear();        
            map.put("MEMBER_ORDER_NUMBER", data.getString("MEMBER_ORDER_NUMBER", ""));
            map.put("ORDER_RESULT", "05");
            map.put("RESULT_DESC", "非流量统付业务，不能办理该业务 ");
            return map;
        }

        // 5-月末最后两天不可以订购叠加流量包
        //国际流量统付跳过此限制
       /* if ((!"99910".equals(productSpecNumber)) && SysDateMgr.dayInterval(SysDateMgr.getSysDate(), SysDateMgr.getLastDateThisMonth()) < 2)
        {
            map.clear();
            map.put("MEMBER_ORDER_NUMBER", data.getString("MEMBER_ORDER_NUMBER", ""));
            map.put("ORDER_RESULT", "05");
            map.put("RESULT_DESC", "月末最后两天不可以订购叠加流量包");
            return map;
        }*/

        // 6- 校验模式是否为指定用户,定额统付
        String attr_code =productSpecNumber+"4008";
        String payType = "";
        String productUserId = merchPInfo.getString("USER_ID");// 产品用户ID

        IDataset userInfos = UserAttrInfoQry.getUserAttrByUserInstType(productUserId, attr_code);
        if (IDataUtil.isNotEmpty(userInfos))
        {
            payType = userInfos.getData(0).getString("ATTR_VALUE");
        }
        /*
         * 国际流量统付改造 productSpecNumber=99910
         * chenmw3
         * 2016-12-13
         */
        if (!"4".equals(payType) && !"5".equals(payType) && !"99910".equals(productSpecNumber))
        {
            map.clear();
            map.put("MEMBER_ORDER_NUMBER", data.getString("MEMBER_ORDER_NUMBER", ""));
            map.put("ORDER_RESULT", "05");
            map.put("RESULT_DESC", "用户模式不为指定用户,定额统付，不可定制流量叠加包");
            return map;
        }
        //7-保存成功订单信息
        if("00".equals(map.getString("ORDER_RESULT"))){
             BbossOrderVerifyBean.saveOrderInfo(data.getString("MEMBER_ORDER_NUMBER", ""), "S");
        }
        return map;
    }
}