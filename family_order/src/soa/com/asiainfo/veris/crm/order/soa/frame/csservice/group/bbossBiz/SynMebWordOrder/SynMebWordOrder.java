
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.SynMebWordOrder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.ECFetionConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss.BbossTradeQueryBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BbossXmlMainInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceptionHallMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class SynMebWordOrder extends CSBizService
{

    private static final long serialVersionUID = 1L;

    // 开通失败交易包流水号(工单无法开通时需要反馈明细信息给BBOSS)
    private String oprNumb = "";

    // 开通失败返回码(工单无法开通时需要反馈明细信息给BBOSS)
    private String resultCode = "00";

    // 开通失败详细信息反馈(工单无法开通时需要反馈明细信息给BBOSS)
    private String resultDesc = "处理成功";

    // 单个成员扩展属性编号列表
    private IDataset characterIds = new DatasetList();

    // 单个成员扩展属性名称列表
    private IDataset characterNames = new DatasetList();

    // 单个成员扩展属性值列表
    private IDataset characterValues = new DatasetList();

    /*
     * @description 处理成员签约关系工单
     * @author xunyl
     * @date 2014-06-21
     */
    public IDataset dealMebWordOrder(IData map) throws Exception
    {
        // 1- 定义返回结果
        IData resultInfo = new DataMap();
        resultInfo = initRspWebWorkOrderResult(map);

        // 2- 获取用户订购的产品信息(用户订购的产品信息不存在，说明本省非业务开展省)
        String offerId = map.getString("PRODUCTID", "");
        if (StringUtils.isBlank(offerId))
        {
        	offerId = map.getString("PRODUCT_ID", "");
		}
        IDataset userProductInfoList;
        String receptionHallMem=map.getString("RECEPTIONHALLMEM");//集客大厅受理标记 如果为集客大厅受理则查集客大厅表
        if(StringUtils.isNotBlank(receptionHallMem)){
            userProductInfoList =UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(offerId);
            map.put("MERCH_SPEC_CODE", userProductInfoList.getData(0).getString("MERCH_SPEC_CODE"));
        }else{
            userProductInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(offerId);
        }


        // 3- 处理签约工单
        String oSubTypeID = map.getString("ACTION", "");
        String serialNumber = map.getString("SERIAL_NUMBER", "");
        dealMemInfo(serialNumber, oSubTypeID, userProductInfoList, map);
        //add by xuzh5 2019-7-5 16:28:02 REQ201905210032  关于优化省行业网关云MAS业务异网号码成员添加流程
        map.put("MAS_KEY", userProductInfoList.getData(0).getString("PRODUCT_SPEC_CODE", ""));

        // 4- 基本校验没有问题并且系统没有台帐信息存在的情况下，创建台帐
        if(StringUtils.equals("00", resultCode) && !isExistTradeInfo(map))
        {
            IData temData = (IData) Clone.deepClone(map);
            temData.put("ORDER_NO", "");
            crtTradeInfo(temData);
        }

        //5- 如果有未完工工单，进入AEE队列处理模式，否则往TF_TP_BBOSS_XML_INFO表写入开通结果
        if(StringUtils.isNotBlank(resultDesc) && (resultDesc.contains("CRM_COMM_983") ||
                resultDesc.contains("CRM_COMM_982")|| resultDesc.contains("0998"))  ||
                resultDesc.contains("用户有未完工的订单") || resultDesc.contains("业务受理中，请稍后")){
            BbossXmlMainInfoQry.updXmlMainDealState(map.getString("SEQ_ID"),"0");//0代表等待处理
        }else{
        	if(null != map.getString("SEQ_ID") &&! "".equals(map.getString("SEQ_ID"))){
	            IDataset xmlMainInfoList= BbossXmlMainInfoQry.qryXmlMainInfoBySeqId(map.getString("SEQ_ID"));
	            IData xmlMainInfo = xmlMainInfoList.getData(0);
	            xmlMainInfo.put("OPEN_RESULT_CODE", resultCode);
	            xmlMainInfo.put("OPEN_RESULT_DESC", resultDesc);
	            if(IDataUtil.isNotEmpty(characterIds)){
	                xmlMainInfo.put("CHARACTER_ID", characterIds);
	                xmlMainInfo.put("CHARACTER_NAME", characterNames);
	                xmlMainInfo.put("CHARACTER_VALUE", characterValues);
	            }
	            xmlMainInfo.put("DEAL_STATE", "2");//2代表叠加包开通或者成员开通
	            Dao.update("TF_TP_BBOSS_XML_INFO", xmlMainInfo,null,Route.CONN_CRM_CEN);
        	}
        }

        //6- BIP4B257_T4101035_0_0接口调用规则(文件接口不调用，实时接口必须所有成员开通后才调用)
        String returnFlag = map.getString("RETURN_FLAG_KT", "");
        if(!"".equals(returnFlag)){
            //添加异常信息
            if(!StringUtils.equals("00", resultCode)){
                IDataset errSerialNumberList = new DatasetList();
                IDataset errOprNumbList = new DatasetList();
                IDataset errRspCodeList = new DatasetList();
                IDataset errRspDescList = new DatasetList();
                resultInfo.put("SERIAL_NUMBER", errSerialNumberList.add(serialNumber));
                resultInfo.put("OPR_NUMB", errOprNumbList.add(oprNumb));
                resultInfo.put("RSPCODE", errRspCodeList.add(resultCode));
                resultInfo.put("RSPDESC", errRspDescList.add(resultDesc));
                resultInfo.put("X_RESULTCODE", resultCode);
                resultInfo.put("X_RESULTINFO", resultDesc);
            }
            //添加反馈参数列表信息
            if (IDataUtil.isNotEmpty(characterIds))
            {
                IDataset memberNumberList =new DatasetList();
                IDataset characterIdList = new DatasetList();
                IDataset characterNameList = new DatasetList();
                IDataset characterValueList = new DatasetList();
                resultInfo.put("MEMBER_NUMBER", memberNumberList.add(serialNumber));
                resultInfo.put("CHARACTER_ID", characterIdList.add(characterIds));
                resultInfo.put("CHARACTER_NAME", characterNameList.add(characterNames));
                resultInfo.put("CHARACTER_VALUE", characterValueList.add(characterValues));
            }
        }

        //7- 返回结果
        return IDataUtil.idToIds(resultInfo);
    }

    /*
     * @description 初始化成员签约工单反馈信息
     * @author xunyl
     * @date 2014-06-21
     */
    private  IData initRspWebWorkOrderResult(IData data) throws Exception
    {
        // 1- 定义返回结果
        IData initRspInfo = new DataMap();

        // 2- 初始化返回结果
        initRspInfo.put("SUBSCRIBE_ID", data.getString("ORDER_NO", ""));
        initRspInfo.put("PKGSEQ", data.getString("PKGSEQ", ""));
        initRspInfo.put("PROVINCE_CODE", "HNAN");
        initRspInfo.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", ""));
        initRspInfo.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        initRspInfo.put("TRADE_CITY_CODE", getVisit().getCityCode());
        initRspInfo.put("TRADE_DEPART_ID", getVisit().getDepartId());
        initRspInfo.put("TRADE_STAFF_ID", getVisit().getStaffId());
        initRspInfo.put("TRADE_DEPART_PASSWD", ""); // 渠道接入密码此密码由BOSS制定，接入渠道必填
        initRspInfo.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        initRspInfo.put("ROUTETYPE", "00");
        initRspInfo.put("ROUTEVALUE", "000");
        initRspInfo.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        initRspInfo.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        initRspInfo.put("RSPCODE", "00");
        initRspInfo.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);

        // 3- 返回初始化信息
        return initRspInfo;
    }

    /**
     * @description 判断工单签约时系统是否存在台帐
     * @author xunyl
     * @date 2015-05-08
     */
    private boolean isExistTradeInfo(IData map)throws Exception{
          // 1- 定义返回结果
        boolean result = false;

        // 2- 获取产品订购编号
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRODUCTID", map);
        if (IDataUtil.isEmpty(productIdList))
        {
        	productIdList = IDataUtil.getDatasetSpecl("PRODUCT_ID", map);;
		}
        String productOfferingId = (String) productIdList.get(0);

        // 3- 根据产品订购关系编号查询相应的台账信息
        String memUserId = MebCommonBean.getMemberUserId(map);

        String receptionHallMem=map.getString("RECEPTIONHALLMEM");//集客大厅受理标记 如果为集客大厅受理则查集客大厅表
        IDataset tradeMerchpMebInfoList =new DatasetList();
        if(StringUtils.isNotBlank(receptionHallMem)){
            tradeMerchpMebInfoList = TradeReceptionHallMebInfoQry.qryMerchMebInfoByUserIdOfferIdRouteId(memUserId, productOfferingId, map.getString(Route.ROUTE_EPARCHY_CODE));
        }else{
            BbossTradeQueryBean bbossQueryBean = new BbossTradeQueryBean();
            tradeMerchpMebInfoList = bbossQueryBean.getMerchpMebTradeInfoByUserId(memUserId, productOfferingId, map.getString(Route.ROUTE_EPARCHY_CODE));

        }

        // 4- 判断查询结果，存在台账信息则为报文归档，否则不是归档报文
        if (IDataUtil.isNotEmpty(tradeMerchpMebInfoList))
        {
            result = true;
        }

        // 5- 返回结果
        return result;
    }

    /**
     * @description 开通工单签约关系同步，没有台帐的创建台帐
     * @author xunyl
     * @date 2015-05-08
     */
    private void crtTradeInfo(IData idata)throws Exception{
        try{
            // 1- 获取一级BOSS传递的成员操作类型
            String oSubTypeID = idata.getString("ACTION", "");

            // 2- 添加用户路由(后台生成虚拟号码用)
            idata.put(Route.USER_EPARCHY_CODE,idata.get(Route.ROUTE_EPARCHY_CODE));

            String receptionHallMem=idata.getString("RECEPTIONHALLMEM");//集客大厅受理标记 如果为集客大厅受理则查集客大厅表

            if(StringUtils.isEmpty(receptionHallMem)){
                // 3- 根据成员操作类型调用不同的服务进行处理
                if ("1".equals(oSubTypeID)){// 成员新增
                    CSAppCall.call("CS.CreateBBossMemSVC.crtOrder", idata);
                }else if ("0".equals(oSubTypeID)){// 成员删除
                    CSAppCall.call("CS.DestroyBBossMemSVC.dealBBossMebBiz", idata);
                }else{// 成员变更
                    CSAppCall.call("CS.ChangeBBossMemSVC.crtOrder", idata);
                }
            }else{
                // 3- 根据成员操作类型调用不同的服务进行处理
                if ("1".equals(oSubTypeID)){// 成员新增
                    CSAppCall.call("CS.CreateReceptionHallMemSVC.crtOrder", idata);
                }else if ("0".equals(oSubTypeID)){// 成员删除
                    CSAppCall.call("CS.DestroyReceptionHallMemSVC.dealReceptionHallMebBiz", idata);
                }else{// 成员变更
                    CSAppCall.call("CS.ChangeReceptionHallMemSVC.crtOrder", idata);
                }
            }


        }catch(Exception e){
            //异常信息反馈            
            oprNumb = idata.getString("PKGSEQ", "");
            if (e.getMessage().indexOf("`") >= 1)// 预防抛出的异常信息格式不规范
            {
                resultCode = e.getMessage().substring(0,
                        e.getMessage().indexOf("`"));
                resultDesc = e.getMessage().substring(
                        e.getMessage().indexOf("`") + 1);
                if (resultDesc.contains("无用户数据")
                        || resultDesc.contains("非移动号码")
                        || resultDesc.contains("已销户")
                        || resultDesc.contains("非移动号码")
                        || resultDesc.contains("服务状态处于非正常状态")) {
                    resultCode = "13";

                } else {
                    resultCode = "99";

                }
                if (resultDesc.length() > 300) {
                    resultDesc.substring(0, 300);

                }
            }
            else
            {
                resultCode = "99";
                resultDesc = "其它错误";
            }
        }
    }

    /*
     * @description 处理单条成员信息
     * @author xunyl
     * @date 2013-10-11
     */
    private void dealMemInfo(String serialNumber, String curSubTypeID, IDataset userProductInfoList, IData map) throws Exception
    {
        //1- 成员用户账期非自然账期，工单无法开通

        //集团V网允许非本省开通
        IDataset prodCommparaList = CommparaInfoQry.getCommpara("CSM", "9089",
                userProductInfoList.getData(0).getString("PRODUCT_SPEC_CODE", ""), "ZZZZ");
        if (IDataUtil.isNotEmpty(prodCommparaList)){
            return;
        }

        IDataset memUserInfoList = UserInfoQry.getEffUserInfoBySn(serialNumber,"0",CSBizBean.getUserEparchyCode());
        if (IDataUtil.isEmpty(memUserInfoList))
        {
        	oprNumb = map.getString("PKGSEQ", "");
            resultCode = "99";
            resultDesc = "成员用户资料不存在！";
            return;
		}
        IData memUserInfo = memUserInfoList.getData(0);
        String memUserId = memUserInfo.getString("USER_ID");
        IData userAcctDay = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(memUserId);
        boolean ifacctdayone = DiversifyAcctUtil.checkUserAcctDay(userAcctDay, "1", false);
        if (!ifacctdayone)
        {
            oprNumb = map.getString("PKGSEQ", "");
            resultCode = "99";
            resultDesc = "成员用户账期非自然账期，工单无法开通";
            return;
        }

        //2- 成员操作类型为重置序列号的场合，扩展属性中应该拼写重置后序列号
        if ("7".equals(curSubTypeID))
        {
            try
            {
                resetImsPassword(serialNumber, memUserId, map);
                return;
            }
            catch (Exception e)
            {
                oprNumb = map.getString("PKGSEQ", "");
                resultCode = "13";
                return;
            }
        }
    }

    /*
     * @description 成员开通工单中重置序列号
     * @author xunyl
     * @date 2013-10-14
     */
    private void resetImsPassword(String serialNumber, String memUserId, IData userProductInfo) throws Exception
    {
        IData inparam = new DataMap();
        String imsPassword = StrUtil.getRandomNumAndChar(8);
        inparam.put("USER_PASSWD2", imsPassword);
        inparam.put("SERIAL_NUMBER", serialNumber);
        String productUserId = userProductInfo.getString("USER_ID");
        inparam.put("USER_ID", productUserId);
        IDataset impuInfoList = UserImpuInfoQry.queryUserImpuInfoByUserType(memUserId, "2", CSBizBean.getUserEparchyCode());
        IData impuInfo = impuInfoList.getData(0);
        inparam.put("IMPUINFO", impuInfo);
        inparam.put("ECFETION_TAG", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        // ChangeIMSpasswdBean bean = new ChangeIMSpasswdBean();
        // bean.crtTrade(inparam);

        characterIds.add(ECFetionConstants.FC_MEMB_EXTEND_PARAM_PASSWORD_CODE);
        characterNames.add(ECFetionConstants.FC_MEMB_EXTEND_PARAM_PASSWORD_NAME);
        characterValues.add(imsPassword);
    }
}