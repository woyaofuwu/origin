
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

public class CreateNpUserTradeBean extends CSBizBean
{

    public static IDataset distinct(List<IDataset> sets)
    {
        IDataset datas = new DatasetList();
        Map<String, IData> map = new HashMap<String, IData>();
        for (IDataset set : sets)
        {
            for (int i = 0; i < set.size(); i++)
            {
                IData data = set.getData(i);
                map.put(data.getString("PRODUCT_TYPE_CODE"), data);
            }
        }
        for (String key : map.keySet())
        {
            datas.add(map.get(key));
        }
        return datas;
    }

    /**
     * 处理sql绑定多个值，in方式
     * 
     * @param strOldBindValue
     * @return
     * @throws Exception
     */
    public static String getMulteBindValue(String strOldBindValue) throws Exception
    {

        if (strOldBindValue.startsWith(","))
            strOldBindValue = strOldBindValue.substring(1);// 去掉首字符为","号
        if (strOldBindValue.endsWith(","))
            strOldBindValue = strOldBindValue.substring(0, strOldBindValue.length() - 1);// 去掉尾字符为","号
        // 不存在多个值
        if (strOldBindValue.indexOf(",") == -1)
        {
            return "";
        }
        return strOldBindValue;// 转换后参数
    }

    /**
     * 根据默认标记获取产品类型
     * 
     * @param strDefaultTag
     * @return
     * @throws Exception
     */
    public static IDataset getProductTypeCodeByDefaultTag(String strDefaultTag) throws Exception
    {

        IDataset typeset = null;
        if (strDefaultTag.indexOf(",") == -1)
        {// 单参数
            return ProductTypeInfoQry.getProductTypeByDefaultTag(strDefaultTag);
        }
        else
        {
            strDefaultTag = getMulteBindValue(strDefaultTag);
            String[] strBindValueArry = StringUtils.split(strDefaultTag, ',');
            List<IDataset> sets = new ArrayList<IDataset>();
            for (int i = 0; i < strBindValueArry.length; i++)
            {
                typeset = ProductTypeInfoQry.getProductTypeByDefaultTag(strBindValueArry[i]);
                sets.add(typeset);
            }
            return distinct(sets);
        }
    }

    /**
     * 根据产品标识获取产品类型
     * 
     * @param strProductId
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductTypeCodeByProductId(String strProductId, String strEparchyCode) throws Exception
    {

        IData inData = new DataMap();
        inData.put("PRODUCT_ID", strProductId);// 个人产品标识
        inData.put("EPARCHY_CODE", strEparchyCode);// 用户路由
        if (strProductId.indexOf(",") == -1)
        {// 单参数
            return UpcCall.qryCatalogByOfferId(strProductId, "P");
        }
        else
        {
            // 现没有绑定多个产品情况
            String strOldBindValue = getMulteBindValue(strProductId);
            return UpcCall.qryCatalogByOfferId(strOldBindValue, "P");
        }
    }

    /**
     * @Function: checkResExistUserNP
     * @Description: P校验证件号码是否存在携转标志为：已携出或者携入携出的用户记录
     * @param:参数描述
     * @return：true-存在, false-不存在
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-3-13 下午3:34:38 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 lijm3 v1.0.0 修改原因
     */
    public boolean checkResExistUserNP(String sn) throws Exception
    {

        IDataset ids = TradeNpQry.getAllTradeNpBySn(sn);
        if (IDataUtil.isNotEmpty(ids))
        {
            IData data = ids.getData(0);
            String npTag = data.getString("NP_TAG");
            if ("4".equals(npTag) || "8".equals(npTag))
            {
                return true;
            }

        }
        return false;
    }

    /**
     * @Function: checkSerialNumber
     * @Description: * 携转号码受理前校验 (1)系统判断申请号码的归属地和办理营业厅归属地相同； (2)系统判断申请号码当前在系统中没有有效的用户REMOVE_TAG='0'；
     *               (3)系统判断申请号码如果在系统中办过携转，那么携出时间须超120天； (4)系统判断申请号码当前在系统中不存在携入申请但还未完工的工单； (5)系统判断申请号码没有办理过联通转接业务；
     *               (6)系统调用资源接口对该号码进行资源校验； (7)系统读取当前未完工的携入申请工单数，如果超过系统设置值N，返回一个提示给用户；
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-3-4 下午3:18:30 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-4 lijm3 v1.0.0 修改原因
     */
    public IData checkSerialNumber(IData param) throws Exception
    {

        IData reData = new DataMap();
        reData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        
/*        IData npcheckParam = new DataMap(); 
        npcheckParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        npcheckParam.put("STATE", "1");
        IDataset checkResult = Dao.qryByCode("TF_B_NPCHECK", "SEL_BY_STATE_SN", npcheckParam);
        if(IDataUtil.isEmpty(checkResult)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务办理前，请确认号码已通过携入预审核！");
        }*/
        
        String home_operator = "";// 归属运营商
        String prov_code = "";
        String sn = param.getString("SERIAL_NUMBER");
        IDataset ids = TradeNpQry.getValidTradeNpBySn(sn);
        String isMobile = "";// 移动号码标识

        String npBack = param.getString("NP_BACK");

        String tradeEparchyCode = getTradeEparchyCode();

        if (IDataUtil.isEmpty(ids))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "查询号码归属无记录，携入号码无效！");
        }
        else
        {
            String snEparchyCode = ids.getData(0).getString("AREA_CODE", "").trim();
            prov_code = ids.getData(0).getString("PROV_CODE", "").trim();

            if (!tradeEparchyCode.equals(snEparchyCode))
            {
                CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "登陆员工地州:" + tradeEparchyCode + "与携入号码归属地:" + snEparchyCode + "不一致!");
            }

            String asp = ids.getData(0).getString("ASP", "").trim();

            if ("2".equals(asp))
            {
                home_operator = "003";
            }
            if ("3".equals(asp))
            {
                home_operator = "001";
            }
            if ("1".equals(asp))
            {
                home_operator = "002";
                isMobile = "1"; // 表示要携回移动号码

            }
            // else if (!"2".equals(asp) && !"3".equals(asp)) // 1移动，2联通，3电信
            // {
            // CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "联通，电信号码才能携入,请确认号码!");
            // }
            //            
            // if("1".equals(npBack) && !"1".equals(asp)){
            // CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "快速携回必须是移动携出号码才能办理！");
            //
            // }

            IDataset nps = TradeNpQry.getValidTdNpsBySn(sn);
            if (IDataUtil.isNotEmpty(nps))
            {
                String ownNetId = nps.getData(0).getString("AS_OWN_NETID");
                if (StringUtils.isNotBlank(ownNetId))
                {
                    home_operator = ownNetId.substring(0, 3);
                }
            }

        }

        IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "系统中已存在有效的" + sn + "资料，不能再携入！");
        }

        IDataset dataset = TradeNpQry.getAllTradeNpBySn(sn);
        if (IDataUtil.isEmpty(dataset) && "1".equals(isMobile))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "普通移动号码不能办理携入开户! ");
        }
        if (IDataUtil.isNotEmpty(dataset))
        {
            String portDate = dataset.getData(0).getString("PORT_OUT_DATE");
            String np_tag = dataset.getData(0).getString("NP_TAG");

            if ("4".equals(np_tag) || "8".equals(np_tag))
            {

                int dateNum = SysDateMgr.dayInterval(portDate, SysDateMgr.getSysDate());

                if ("1".equals(npBack))
                {
                    if (7 < dateNum)
                    {
                        CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "移动号码必须携出7天内才能办理快速携回业务！");
                    }
                }
                else
                {
                    if (dateNum < 120)
                    {
                        CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "携转号码必须携出120天后才能办理携入申请业务！");
                    }
                }

            }
        }

        IDataset trades = TradeInfoQry.getMainTradeBySN(sn, "40");
        if (IDataUtil.isNotEmpty(trades))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, sn + "有携入申请未完工的工单，不能再申请！");
        }

        IDataset trans = UcaInfoQry.qryUnionTransPhone(sn);

        if (IDataUtil.isNotEmpty(trans))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, sn + "办理了联通转接业务，不能携入！");
        }

        //携号转网背景下吉祥号码业务规则优化需求（上） by mengqx
        boolean beautifulSwitch = false;
        IDataset commparaInfos = CommparaInfoQry.getCommNetInfo("CSM", "40", "BEAUTIFUL_SWITCH");
        if (IDataUtil.isNotEmpty(commparaInfos)){
            if("1".equals(commparaInfos.getData(0).getString("PARA_CODE1"))){
                beautifulSwitch = true;
            }
        }

        //携入号码按我省吉祥号码规则执行，1表示开，按规则执行；关就是携入号码按普通号码规则执行。
        if(!beautifulSwitch){
            IDataset datas = ResCall.chkNpMphoneInfo(sn, "2");// 号码校验
        }else{
            IDataset checkMphoneDatas = ResCall.chkNpMphoneInfo(sn, "2");// 号码校验

            IData checkMphoneData = IDataUtil.isEmpty(checkMphoneDatas)? new DataMap():checkMphoneDatas.getData(0);
            reData.put("FEE_CODE_FEE", "0".equals(checkMphoneData.getString("RESERVE_FEE", "0")) ? "" : checkMphoneData.getString("RESERVE_FEE", "0")); // 选号费

            String saleProduct = checkMphoneData.getString("PRODUCT_ID", "");
            String salePackage = checkMphoneData.getString("PACKAGE_ID", "");
            String bindDiscnt = checkMphoneData.getString("ELEMENT_ID", "");
            String bindMonth = checkMphoneData.getString("DEPOSIT_MONTH", "");
            String saleString = "";
            String bindDiscntString = "";
            String packageName = "";
            if (StringUtils.isNotBlank(saleProduct) && StringUtils.isNotBlank(salePackage)) {
                packageName = UpcCall.qryOfferNameByOfferTypeOfferCode(salePackage, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
                saleString = saleProduct + "|" + salePackage + "|" + packageName;
            }
            if (StringUtils.isNotBlank(bindDiscnt) && StringUtils.isNotBlank(bindMonth)) {
                bindDiscntString = bindDiscnt + "|" + bindMonth;
            }
            reData.put("A_X_CODING_STR", saleString); // add by wenhj2012.12.18 REQ201211200008 69900703|60007243
            reData.put("X_BIND_DEFAULT_DISCNT", bindDiscntString); // "优惠编码|绑定月份"

            // 需要判断 returndata里的A_X_CODING_STR
            String saleProductId = reData.getString("A_X_CODING_STR", "").split("\\|")[0];
            String fee = reData.getString("FEE_CODE_FEE");
            String kindCodeBySn = reData.getString("RES_KIND_CODE_SN");
            if (!StringUtils.isBlank(saleProductId))
            {

                IDataset dataset5 = ParamInfoQry.getCode1ForOpen("OPEN");
                if (IDataUtil.isNotEmpty(dataset5))
                {
                    if ("0W".equals(kindCodeBySn))
                    {
                        reData.put("PACKAGES_SALE", "");
                        reData.put("BIND_SALE_TAG", "0");
                        reData.put("X_CODING_STR", "null");
                    }
                    else
                    {
                        IDataset packages = ProductPkgInfoQry.getPackageByProductIdForOpen(saleProductId);
                        reData.put("PACKAGES_SALE", packages);
                        reData.put("BIND_SALE_TAG", "1");
                        reData.put("X_CODING_STR", reData.getString("A_X_CODING_STR", ""));
                    }

                }
                else
                {
                    reData.put("PACKAGES_SALE", "");
                    reData.put("BIND_SALE_TAG", "0");
                    reData.put("X_CODING_STR", "null");
                }
            }
            if (StringUtils.isNotBlank(fee))
            {
                if ("0W".equals(kindCodeBySn))
                    reData.put("FEE_CODE_FEE", "");
            }


            if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "CHANGPACKAGEANDFEE")){
                IData inparam=new DataMap();
                inparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER",""));
                inparam.put("SN_CLASS", "4");
                boolean flag=checkIfBeautyNo(inparam);
                if(flag){
                    reData.put("SYSCHANGPACKAGE4", "1");
                }
            }
            if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYSCHANGPACKAGE")){
                reData.put("SYSCHANGPACKAGE", "1");
            }
        }
        //end 携号转网背景下吉祥号码业务规则优化需求（上） by mengqx

        IDataset tags = TagInfoQry.queryNormalTagInfoByTagCode(tradeEparchyCode, "NP_IN_TRADENUM_LIMIT", "CSM", "0");
        String message = "";
        if (IDataUtil.isNotEmpty(tags))
        {
            IData tagInfo = tags.getData(0);
            int tagNumber = tagInfo.getInt("TAG_NUMBER");

            if (tagNumber > 0)
            {
                int tradeNpCount = TradeNpQry.getTradeNpCountByTradeTypeCode("40");
                if (tradeNpCount >= tagNumber)
                {
                    message = tagInfo.getString("TAG_INFO");
                    reData.put("MESSAGE", message);// (7)系统读取当前未完工的携入申请工单数，如果超过系统设置值N，返回一个提示给用户
                }
            }
        }

        if (checkResExistUserNP(sn))
        {
            // 可以复机，进行提示
            reData.put("NP_B_CAN_RESTORE", "1");
        }

        reData.put("HOME_OPERATOR", home_operator);
        reData.put("PROV_CODE", prov_code);
        return reData;
    }

    /**
     * @Function: checkSimCardNo
     * @Description: sim资源可用校验
     * @param data
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-6-19 上午9:33:31
     */
    public IData checkSimCardNo(IData data) throws Exception
    {

        String serialNumber = data.getString("SERIAL_NUMBER");
        String simCardNo = data.getString("SIM_CARD_NO");
        IData returnData = new DataMap();

        IData resInfoData = new DataMap();

        IDataset checkSimDatas = ResCall.chkNpSimcardInfo(simCardNo, "2", serialNumber);
        IData checkSimData = new DataMap();
        if (IDataUtil.isNotEmpty(checkSimDatas))
        {
            checkSimData = checkSimDatas.getData(0);
        }

        resInfoData.put("IMSI", checkSimData.getString("IMSI", ""));
        resInfoData.put("KI", checkSimData.getString("KI", ""));
        resInfoData.put("CAPACITY_TYPE_CODE", checkSimData.getString("CAPACITY_TYPE_CODE", ""));// 卡空量
        resInfoData.put("OPC", checkSimData.getString("OPC", ""));
        resInfoData.put("SIM_CARD_NO", checkSimData.getString("SIM_CARD_NO", ""));
        resInfoData.put("NET_TYPE_CODE", checkSimData.getString("NET_TYPE_CODE"));
        resInfoData.put("RES_KIND_CODE", checkSimData.getString("RES_KIND_CODE"));
        resInfoData.put("RES_KIND_NAME", checkSimData.getString("RES_KIND_NAME", ""));
        resInfoData.put("RES_TYPE_CODE", checkSimData.getString("RES_TYPE_CODE", ""));
        resInfoData.put("SIM_CARD_TYPE", checkSimData.getString("RES_TYPE_CODE", "").substring(1));
        
        resInfoData.put("SIM_CARD_SALE_MONEY", "" + checkSimData.getString("SALE_MONEY", "0"));
        resInfoData.put("SIM_FEE_TAG", checkSimData.getString("FEE_TAG", ""));
        
        String resKindCode = checkSimData.getString("RES_KIND_CODE", "");
        
        returnData.put("SIM_CHECK_RESULT_CODE", "1");

      
       String  emptyCardId = checkSimData.getString("EMPTY_CARD_ID");
        
       if(StringUtils.isNotBlank(emptyCardId)){
           IDataset newEmptyCardInfos = ResCall.getEmptycardInfo(emptyCardId, "", "");// 资源接口

           IData newEmptyCardInfo = newEmptyCardInfos.getData(0);
           String newCapacityTypeCode = newEmptyCardInfo.getString("CAPACITY_TYPE_CODE"); // 这个字段资源已经废弃
           String newSimCardType = newEmptyCardInfo.getString("RES_TYPE_CODE", "").substring(1);
           // restypeCode = 1 + sim_type_code

           // returnData.put("MAIN_RSRV_STR8",str);// 预留字段用于登记主台帐:MAIN_XXX
           resInfoData.put("RES_KIND_CODE", newEmptyCardInfo.getString("RES_KIND_CODE", ""));
           resInfoData.put("RES_KIND_NAME", newEmptyCardInfo.getString("RES_KIND_NAME", ""));
           resInfoData.put("RES_TYPE_CODE", newEmptyCardInfo.getString("RES_TYPE_CODE", ""));
           resInfoData.put("SIM_CARD_TYPE", newSimCardType);
           resInfoData.put("CAPACITY_TYPE_CODE", newCapacityTypeCode);
           resKindCode = newEmptyCardInfo.getString("RES_KIND_CODE", "");
           // 是否为USIM卡,3G,将OPC记录在attr表
           // if("1Z".equals(newEmptyCardInfo.getString("RES_TYPE_CODE", ""))){
           
           // }
           
           String rsrvTag = newEmptyCardInfo.getString("RSRV_TAG1");
           if ("3".equals(rsrvTag)){
               resInfoData.put("SIM_FEE_TAG", "1");
           }
               
           
       }else{
           resInfoData.put("MAIN_RSRV_STR8", checkSimData.getString("RSRV_STR6","")+checkSimData.getString("RSRV_STR7",""));
       }
       
       String uSimOpc = checkSimData.getString("OPC", "");// usim卡的一种属性
       if ("".equals(uSimOpc))
       {
           // CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "USIM卡[" + simCardNo + "]校验出错,OPC值为空!");
       }
       else
       {
           resInfoData.put("OPC_CODE", "OPC_VALUE");
           resInfoData.put("OPC_VALUE", uSimOpc);
       }
        

        
        IData feeData = DevicePriceQry.getDevicePrice(BizRoute.getRouteId(), "-1", "40", resKindCode, "6");
        if (IDataUtil.isNotEmpty(feeData))
        {
            returnData.put("FEE_MODE", "0");
            returnData.put("FEE_TYPE_CODE", feeData.getString("FEEITEM_CODE"));
            returnData.put("FEE", feeData.getString("DEVICE_PRICE"));
        }

        returnData.put("RES_INFO_DATA", resInfoData);
        return returnData;
    }

    /**
     * @Function: getProductFeeInfo
     * @Description: 获取产品费用
     * @param data
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-6-3 下午4:16:56
     */
    public IDataset getProductFeeInfo(IData data) throws Exception
    {

        String product_id = data.getString("PRODUCT_ID");
        String brand_code = data.getString("BRAND_CODE");
        String eparchy_code = CSBizBean.getUserEparchyCode();
        IDataset dataset = ProductFeeInfoQry.getProductFeeInfo("40", product_id, "-1", "-1", "P", "3", eparchy_code);
        return dataset;

    }

    public IData getProductInfos(IData data) throws Exception
    {

        IData returnData = new DataMap();
        boolean specDealTag = false;// 不同处理标志
        String strBindSingleProduct = data.getString("EXISTS_SINGLE_PRODUCT", "");

        String strBindDefaultTag = data.getString("EXISTS_DEFAULT_TAG", "");

        // 判断标志位:只要有一个不为空，作特殊处理
        if (!StringUtils.isBlank(strBindSingleProduct) || !StringUtils.isBlank(strBindDefaultTag))
        {
            specDealTag = true;// 单产品
        }
        IDataset productTypeList = null;
        // 产品类型:不存在绑定产品:无特殊处理
        if (!specDealTag)
        {
            productTypeList = UProductInfoQry.getProductsType("0000", null);
        }
        else
        {
            if (!StringUtils.isBlank(strBindSingleProduct))
            {
                productTypeList = getProductTypeCodeByProductId(strBindSingleProduct, BizRoute.getRouteId());

                // 号码绑定单个产品时，不显示产品目录，直接将此产品下的必选包下的必选择默认元素显示
                if (productTypeList.size() > 0)
                {
                    IData productInfo = UProductInfoQry.qryProductByPK(strBindSingleProduct);
                    returnData.put("PRODUCT_NAME", productInfo.getString("PRODUCT_NAME"));
                    returnData.put("PRODUCT_ID", strBindSingleProduct);
                    returnData.put("BRAND_CODE", productInfo.getString("BRAND_CODE"));
                }
            }
            else
            {
                // 绑定默认标记
                if (!StringUtils.isBlank(strBindDefaultTag))
                {
                    productTypeList = getProductTypeCodeByDefaultTag(strBindDefaultTag);

                }
            }
        }

        IDataset productTypeListNew = new DatasetList();
        IData productTypeData = new DataMap();
        String productTypeCode = "";

        if (IDataUtil.isNotEmpty(productTypeList))
        {
            for (int i = 0; i < productTypeList.size(); i++)
            {
                productTypeData = productTypeList.getData(i);
                productTypeCode = productTypeData.getString("PRODUCT_TYPE_CODE");
                if (!"0800".equals(productTypeCode))
                {
                    productTypeListNew.add(productTypeData);
                }
            }
        }

        returnData.put("PRODUCT_TYPE_LIST", productTypeListNew);
        return returnData;
    }

    /**
     * 查询td_s_tag表参数
     * 
     * @param strEparchyCode
     * @param tagCode
     * @param userTag
     * @return
     * @throws Exception
     */
    public IData getTagInfo(String strEparchyCode, String tagCode, String userTag) throws Exception
    {
        IDataset tagList = new DatasetList();
        tagList = TagInfoQry.getTagInfo(strEparchyCode, tagCode, userTag, null);
        return IDataUtil.isEmpty(tagList) ? new DataMap() : tagList.getData(0);
    }

    /**
     * @Function: InitPara
     * @Description: 暂时不用，
     * @param data
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-5-26 上午10:57:03
     */
    public IData InitPara(IData data) throws Exception
    {
        IData returnData = new DataMap();

        IData rDualInfo;
        String strTagInfo = "";
        String strTagChar = "";
        String strTagNumber = "";
        String strOpenType = data.getString("OPEN_TYPE", ""); // 是否代理商开户，通过地址栏参数
        String strEparchyCode = CSBizBean.getTradeEparchyCode();

        // 设置开户方式 OPEN_TYPE
        returnData.put("OPEN_TYPE", strOpenType);

        // 获取开户限制标记
        rDualInfo = getTagInfo(strEparchyCode, "CS_TAG_OPEN_LIMIT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("OPEN_LIMIT_TAG", "1".equals(strTagChar) ? true : false);

        // 获取默认开户用户数
        rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_OPENLIMITCOUNT", "0");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "0");
        returnData.put("OPEN_LIMIT_COUNT", strTagNumber);

        // 获取客户名称限制标志
        rDualInfo = getTagInfo(strEparchyCode, "CS_CUSTNAME_LIMIT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("CUSTNAME_LIMIT", strTagChar);

        // 获取是否显示用户提示信息的标记(用户开户时，仅做新业务推荐用,各省都没有实现)
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_SHOWHINTINFO", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("SHOW_HINT_INFO", strTagChar);

        // 获取是否显示引导信息
        rDualInfo = getTagInfo(strEparchyCode, "CS_SHOWUSERNUM", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("SHOW_USER_NUM_TAG", "1".equals(strTagChar) ? true : false);
        if ("1".equals(strTagChar))
        {
            returnData.put("SHOW_USER_NUM", rDualInfo.getString("TAG_NUMBER", "0"));
        }

        // 获取默认密码的使用方式
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPWDMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PWD_MODE", strTagChar);

        // 获取默认密码
        rDualInfo = getTagInfo(strEparchyCode, "CS_INF_DEFAULTPWD", "0");
        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        returnData.put("DEFAULT_PWD", strTagInfo);

        // 默认密码未变更是否继续(用户开户密码必输，代理商开户默认，还要这个标志何用？)
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_PWDNOCHANGE", "0");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
        rDualInfo.put("PWDNOCHANGE", strTagNumber);

        // 获取密码长度
        rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PASSWORDLENGTH", "0");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
        returnData.put("DEFAULT_PWD_LENGTH", strTagNumber);

        // 配置是否需要新版本的身份校验,0:不需要,1:需要
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_IDCHKDEALDISMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("ID_CHKDEALDIS_MODE", strTagChar);

        // 是否使用密码键盘 0-否 1-是
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_USEPASSWDKEYBOARD", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("USE_PASSWD_KEYBOARD", strTagChar);

        // 默认用户类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTUSERTYPE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_USER_TYPE", strTagChar);

        // 默认证件类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPSPTTYPE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PSPT_TYPE", strTagChar);

        // 默认帐户类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPAYMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PAY_MODE", strTagChar);

        // 获取黑名单提示方式标记（0-不允许办理[默认]，1-提示是否继续）
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_BLACKCHECKMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("CHR_BLACKCHECKMODE", strTagChar);

        // 获取是否根据证件号码判断欠费标记(默认为不按证件号码判欠费)
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_CHECKOWEFEEBYPSPT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
        returnData.put("CHR_CHECKOWEFEEBYPSPT", "1".equals(strTagChar) ? true : false);
        returnData.put("CHR_CHECKOWEFEEBYPSPT_ALLUSER", "1".equals(strTagNumber) ? true : false);

        // 代理商开户,代理商远程写卡开户
        if ("AGENT_OPEN".equals(strOpenType) || "REMOTE_AGENT_OPEN".equals(strOpenType))
        {
            // 代理商开户是否只使用操作员部门(登录员工部门)进行资料校验：0,不使用登录员工部门,根据选择代理商,1,仅使用操作员工部门
            rDualInfo = getTagInfo(strEparchyCode, "CS_RESCHECK_BYDEPART", "0");
            strTagChar = rDualInfo.getString("TAG_CHAR", "0");
            returnData.put("RES_CHECK_BY_DEPART", strTagChar);

            // 代理商开户是否设置用户密码,1:不用输入0:输入
            rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_AUTOPASSWD", "0");
            strTagChar = rDualInfo.getString("TAG_CHAR", "1");
            returnData.put("CHR_AUTO_PASSWD", strTagChar);
        }

        // 省内异地开户(远程写卡)
        if ("PROV_REMOTE_OPEN".equals(strOpenType))
        {
            // 获取省内跨区开户默认预存款标记
            rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PROVOPENADVANCEPAY", "0");
            strTagChar = rDualInfo.getString("TAG_CHAR", "");
            if ("1".equals(strTagChar))
            {
                returnData.put("PROV_OPEN_ADVANCE_PAY_FLAG", true);// 预存款标记
                returnData.put("PROV_OPEN_ADVANCE_PAY", rDualInfo.getString("TAG_NUMBER", "0"));
            }

            // 获取省内跨区开户默认卡费标记
            rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PROVOPENOPERFEE", "0");
            strTagChar = rDualInfo.getString("TAG_CHAR", "");
            if ("1".equals(strTagChar))
            {
                returnData.put("PROV_OPEN_OPERFEE_FLAG", true);
                returnData.put("PROV_OPEN_OPERFEE", rDualInfo.getString("TAG_NUMBER", "0"));
            }
        }

        // 从标记类中获取业务办理是否可以同时输入刮刮卡号标志
        /**
         * rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_USEGGCARD", "0"); strTagChar = rDualInfo.getString("TAG_CHAR",
         * ""); returnData.put("CHR_USEGGCARD", strTagChar);
         **/

        // 开户是否支持邮寄（0-支持[默认]，1-不支持）
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_OPENPOSTINFO", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "0");
        returnData.put("CHR_OPENPOSTINFO", strTagChar);

        // 是否已检查号码选占数据
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_CHECKSELENUM", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "0");
        returnData.put("CHR_CHECKSELENUM", strTagChar);

        return returnData;
    }
    
    /**
     * REQ201601070010携入开户工单完工优化
     * 更新开始时间
     * 1、TF_B_TRADE_DISCNT. START_DATE
		2、TF_B_TRADE_SVC. START_DATE
		3、TF_B_TRADE_USER. OPEN_DATE
	   chenxy3 2016-01-11
     * */
    public void updDiscntTradeStartdate(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPD_TRADE_DISCNT_STARTDATE", inparams,Route.getJourDb(Route.CONN_CRM_CG));
    }
    public void updSvcTradeStartdate(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_B_TRADE_SVC", "UPD_TRADE_SVC_STARTDATE", inparams,Route.getJourDb(Route.CONN_CRM_CG));
    }
    public void updUserTradeOpendate(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_B_TRADE_USER", "UPD_TRADE_USER_OPENDATE", inparams,Route.getJourDb(Route.CONN_CRM_CG));
    }
    /**
     * end -----------------------------
     * */
    
    public void updCreditTradeStartdate(IData inparams) throws Exception
    {

        Dao.executeUpdateByCodeCode("TF_B_TRADE_CREDIT", "UPD_BY_TRADEID", inparams,Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    //提交前校验是否已查验
    public IData checkBeforeNpUser(IData inparams) throws Exception
    {
    	IData result = new DataMap();
    	
    	result.put("RESULT_CODE", "1");
    	
    	IDataUtil.chkParam(inparams, "SERIAL_NUMBER");
    	IDataUtil.chkParam(inparams, "AUTH_CODE");
    	String psptType = IDataUtil.chkParam(inparams, "PSPT_TYPE_CODE");
    	String psptId = IDataUtil.chkParam(inparams, "PSPT_ID");
    	if("00".equals(psptType)){
    		psptType = "0";//能开传入的证件类型为00，表示身份证
		}
    	
    	String authExp = IDataUtil.chkParam(inparams, "AUTH_CODE_EXPIRED");
    	String authCodeExp = SysDateMgr.decodeTimestamp(authExp, "yyyyMMddHHmm");
    	
    	inparams.put("AUTH_CODE_EXPIRED", authCodeExp);
    	
    	IDataset checkInfoLists = TradeNpQry.qryUserNpCheckInfosBySn(inparams);
    	if(DataUtils.isNotEmpty(checkInfoLists))
    	{
    		IData checkInfo = checkInfoLists.getData(0);
    		String resultCode = checkInfo.getString("RESULT_CODE_S");
    		String state = checkInfo.getString("STATE");
    		String checkpsptType = checkInfo.getString("CRED_TYPE");
    		String checkpsptId = checkInfo.getString("PSPT_ID");
    		if(!psptType.equals(checkpsptType) || !psptId.equals(checkpsptId))
    		{
    			result.put("RESULT_INFO", "输入的证件类型或证件号码与查验信息不符，请重新输入");
    			return result;
    		}
    		
    		if("120".equals(state) && "200".equals(resultCode))
    		{//查验成功
    			result.putAll(checkInfo);
    			result.put("RESULT_CODE", "0");
    		}
    		else
    			result.put("RESULT_INFO", stateMsg(resultCode));
    	}
    	else
    	{
    		result.put("RESULT_INFO", "请先在‘授权码查验’界面受理查验");
    	}
    	
    	return result;
    }
    
    public String stateMsg(String resultCode) throws Exception
    {
    	String msg = "查验结果未返回，请稍后";
    	if("601".equals(resultCode))
    	{//查验结果未返回
    		msg="查验失败，失败原因【一号多转】";
    	}
    	else if("604".equals(resultCode))
    	{//查验返回失败
    		msg="查验失败，失败原因【非业务受理时间】";
    	}
    	else if("606".equals(resultCode))
    	{//查验返回失败
    		msg="查验失败，失败原因【提供的授权码错误】";
    	}
    	else if("607".equals(resultCode))
    	{//查验返回失败
    		msg="查验失败，失败原因【提供的授权码不在有效期内】";
    	}
    	else if("608".equals(resultCode))
    	{//查验返回失败
    		msg="查验失败，失败原因【提供的证件类型和证件号码在携出方登记信息不符】";
    	}
    	else if("648".equals(resultCode))
    	{//查验返回失败
    		msg="查验失败，失败原因【消息中相关网络ID不正确】";
    	}
    	else if("694".equals(resultCode))
    	{//查验返回失败
    		msg="查验失败，失败原因【该号码的相关业务请求流程已经成功完成，不能重复请求】";
    	}
    	else if("695".equals(resultCode))
    	{//查验返回失败
    		msg="查验失败，失败原因【该号码的相关业务请求正在处理中，不能重复请求】";
    	}
    	return msg;
    }

    /**
     * 判断是否四级吉祥号码
     * chenxy3 20160927
     * BEAUTIFUAL_TAG：是否是吉祥号：0-非；1-是
     RSRV_STR4 级别（取值说明 1、2、3、4等）
     CLASS_ID 级别编码
     */
    public boolean checkIfBeautyNo(IData input) throws Exception{
        boolean flag=false;
        String serialNum=input.getString("SERIAL_NUMBER");
        String snClass=input.getString("SN_CLASS");
        IDataset numberInfo = ResCall.getMphonecodeInfo(serialNum);// 查询号码信息
        if(IDataUtil.isNotEmpty(numberInfo)){
            String beautyTag=numberInfo.getData(0).getString("BEAUTIFUAL_TAG","");//BEAUTIFUAL_TAG：是否是吉祥号：0-非；1-是
            if("1".equals(beautyTag)){
                String classNo=numberInfo.getData(0).getString("RSRV_STR4","");//RSRV_STR4 级别（取值说明 1、2、3、4等）
                if(snClass.equals(classNo)){
                    flag=true;
                }
            }
        }
        return flag;
    }

}
