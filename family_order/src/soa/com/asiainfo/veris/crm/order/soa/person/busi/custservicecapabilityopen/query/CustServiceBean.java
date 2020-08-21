package com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.query;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;

import org.apache.axis.client.Service;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.alibaba.fastjson.JSON;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UUserTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo.QueryInfoBean;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSInputData;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSServNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSVarNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.NeaSoapBindingStub;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.SoapInputXml;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.WSSOPStub;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.WSSOP_ServiceLocator;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.holders.CWSVarNodeHolder;


public class CustServiceBean extends CSBizBean
{

    private static transient Logger logger = Logger.getLogger(CustServiceBean.class);

    public IData queryUserPUKCode(IData input)  throws Exception{
        IData param = new DataMap();
        //凭证检查
        IData userInfo = CheckParam.isCheckIdentAuth(input);


        String serial_number = input.getString("SERIAL_NUMBER");

/*        IData userInfo = UcaInfoQry.qryUserInfoBySn(serial_number);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_126, serial_number);// 该号码【%s】没有有效的用户信息
        }*/
        String userId = userInfo.getString("USER_ID");

        //获取三户资料
        IData idata = new DataMap();
        idata.clear();
        idata.put("SERIAL_NUMBER", serial_number);
        idata.put("KIND_ID", input.getString("KIND_ID"));
        idata.put("USER_ID", userId);
        idata.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
        idata.put("IDTYPE", input.getString("IDTYPE"));
        idata.put("X_GETMODE", "1");//根据USER_ID获取
        IDataset res = getUserCustAcct(idata);


        String pukcode =  res.getData(0).getString("PUK");

        if(pukcode == null || "".equals(pukcode)){

            CSAppException.apperr(CrmUserException.CRM_USER_3000);

        }

        param.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        param.put("PUK_CODE", pukcode);
        return param;


    }


    /**
     * @Function: queryUserSimBak
     * @Description: ITF_CRM_QueryUserSimBak 备卡信息查询
     * @param:
     * @param data
     * @param:
     * @return
     * @param:
     * @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 上午11:35:03 2013-9-13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-13 longtian3 v1.0.0 TODO:
     */
    public IDataset queryUserSimBak(IData data) throws Exception
    {

        String serialNumber = IDataUtil.chkParam(data, "MSISDN");

        // 用户凭证校验
        IData userInfo = CheckParam.isCheckIdentAuth(data);
        String userId = userInfo.getString("USER_ID");


        // 获取vip信息
        //IDataset vipInfo = CustVipInfoQry.querySimBakInfo(serialNumber, userInfo.getString("REMOVE_TAG"));
        IDataset vipInfo = CustVipInfoQry.queryVipInfo(userId); // 2017/5/2 合版本 修改为此方法 duhj
        if (IDataUtil.isEmpty(vipInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_178);// 获取大客户信息无数据
        }
        // 获取vip备卡信息
        IDataset simbak = CustVipInfoQry.querySimBakByVipId(vipInfo.getData(0).getString("VIP_ID"), "0");
        if (IDataUtil.isEmpty(simbak))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_8);// 获取用户无备卡申请信息
        }

        // 如果存在备卡信息，获取备卡类型编码和名称信息
        String resNo = simbak.getData(0).getString("SIM_CARD_NO");

        IData result = new DataMap();
        result.put("SIM_CARDNO2", resNo);
        result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        return new DatasetList(result);
    }

    /**
     * @Function: queryUserInterRoamDay
     * @Description: ITF_CRM_QueryUserInterRoamDay 国际漫游业务日套餐状态查询
     * @param: @param data
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 下午02:14:21 2013-9-13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-13 longtian3 v1.0.0 TODO:
     */
    public IDataset queryUserInterRoamDay(IData data) throws Exception
    {

        // 用户凭证校验
        IData userInfo = CheckParam.isCheckIdentAuth(data);
        String userId = userInfo.getString("USER_ID");
        String eparchyCode = userInfo.getString("EPARCHY_CODE");
        IDataset roamDay = UserDiscntInfoQry.queryInterRoamDayInfo(userId, eparchyCode);

        String packName = "";
        String packCode = "";
        String packExplain = "";
        String packType = "";
        String effectTime = "";
        String validTime = "";
        String state = "";
        if (IDataUtil.isNotEmpty(roamDay))
        {
            IData tempDate = roamDay.getData(0);
            packName = tempDate.getString("PARAM_NAME");
            packCode = tempDate.getString("PARA_CODE2");
            packExplain = tempDate.getString("PACKAGE_DESC");
            packType = tempDate.getString("PARA_CODE3");
            effectTime = tempDate.getString("EFFECT_TIME");
            validTime = tempDate.getString("VALID_TIME");
            state = tempDate.getString("STATE");
        }
        else
        {
            effectTime = SysDateMgr.getSysTime();
            validTime = effectTime;
            state = "0";
        }


        effectTime = effectTime.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
        validTime = validTime.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");

        IData result = new DataMap();
        result.put("USER_ID", data.getString("SERIAL_NUMBER"));
        result.put("PACK_NAME", packName);
        result.put("PACK_CODE", packCode);
        result.put("PACK_EXPLAIN", packExplain);
        result.put("PACK_TYPE", packType);
        result.put("EFFECT_TIME", effectTime);
        result.put("VALID_TIME", validTime);
        result.put("PACK_STATE", state);
        result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        return new DatasetList(result);
    }

    /**
     * @Function: queryUserSaleActive
     * @Description: ITF_CRM_QueryUserSaleActive 本地营销案查询
     * @param: @param data
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 下午02:30:16 2013-9-13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-13 longtian3 v1.0.0 TODO:
     */
    public IDataset queryUserSaleActive(IData data) throws Exception
    {
    	IData resData = new DataMap();
        IDataset results = new DatasetList();
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        // 用户凭证校验
        IData userInfo = CheckParam.isCheckIdentAuth(data);
        String userId = userInfo.getString("USER_ID");

        String eparchyCode = userInfo.getString("EPARCHY_CODE");
        String provName = UAreaInfoQry.getAreaNameByAreaCode(getVisit().getProvinceCode());
        //String cityName = UAreaInfoQry.getAreaNameByAreaCode(eparchyCode);
        String cityName = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", eparchyCode);

        String userCity = provName + cityName;
        IDataset activeInfo = UserSaleActiveInfoQry.queryUserSaleActiveByTag(userId);
        String startDate = "";
        String endDate = "";
        String productId = "";
        String productName = "";
        if (IDataUtil.isNotEmpty(activeInfo))
        {
            startDate = activeInfo.getData(0).getString("START_DATE");
            endDate = activeInfo.getData(0).getString("END_DATE");
            productId = activeInfo.getData(0).getString("PRODUCT_ID");
            productName = activeInfo.getData(0).getString("PRODUCT_NAME");
            for (int i = 0; i < activeInfo.size(); i++)
            {
                IData userSaleActive = new DataMap();
                userSaleActive.put("USER_ID", data.getString("SERIAL_NUMBER"));

                startDate = ((IData) activeInfo.get(i)).getString("START_DATE");
                endDate = ((IData) activeInfo.get(i)).getString("END_DATE");
                productId = ((IData) activeInfo.get(i)).getString("PRODUCT_ID");
                productName = ((IData) activeInfo.get(i)).getString("PRODUCT_NAME");

                IData acctInfo = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userId);
                if (IDataUtil.isEmpty(acctInfo))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_13, "2998", "缺少用户账期数据!");
                }

                String accountDay = acctInfo.getString("FIRST_DAY_NEXTACCT");
                accountDay=SysDateMgr.decodeTimestamp(accountDay, SysDateMgr.PATTERN_STAND_SHORT);
                startDate = startDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
                endDate = endDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
                accountDay = accountDay.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");

                IData result = new DataMap();
                result.put("USER_ID", serialNumber);
                result.put("NO_PROV", userCity);
                result.put("CASE_NAME", productName);
                result.put("CASE_CODE", productId);
                result.put("EFFECT_TIME", startDate);
                result.put("VALID_TIME", endDate);
                result.put("ACCOUNT_DAY", accountDay);
                //rsltId
                result.put("RSLT_ID", i + 1);
                results.add(result);
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_13, "2998", "用户没有营销方案记录信息!");
        }

        resData.put("LOCAL_MARKERTING_CASE", results);
        resData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

        return new DatasetList(resData);
    }


    /**
     * @Function: queryUserProductInfo
     * @Description: ITF_CRM_QueryUserProductInfo 已订购业务查询
     * @param: @param data
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 下午03:43:09 2013-9-13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-13 longtian3 v1.0.0 TODO:
     */
    public IDataset queryUserProductInfo(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        IDataUtil.chkParam(data, "OPR_NUMB");
        String contactId = IDataUtil.chkParam(data, "CONTACT_ID");
        String serialNumber = IDataUtil.chkParam(data, "MSISDN");
        String identCode = data.getString("IDENT_CODE", "");
        IDataset productDataset = new DatasetList();
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
        }

        // 用户凭证校验
        String userId = userInfo.getString("USER_ID");
        if (StringUtils.isBlank(identCode))
        {
            IDataset dataset = UserIdentInfoQry.queryIdentCode(userId, identCode, contactId);
            if (IDataUtil.isEmpty(dataset))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_13, "2998", "用户没有登录或者登录已经失效!");
            }
        }

        String productId = userInfo.getString("PRODUCT_ID");
        String eparchyCode = userInfo.getString("EPARCHY_CODE");

        IDataset dataset1 = UserPlatSvcInfoQry.queryNormalPlatSvcInfoByUserId(productId, userId, "1", eparchyCode);
        IDataset dataset2 = PlatSvcInfoQry.queryPlatOrderInfobyUserId01(userId);
        IDataset dataset3 = UserPlatSvcInfoQry.queryNormalPlatSvcInfoByUserId(productId, userId, "2", eparchyCode);

        dataset1.addAll(dataset2);
        dataset1.addAll(dataset3);


        if (IDataUtil.isNotEmpty(dataset1))
        {
            IDataset temp = new DatasetList();
            IData tempData = new DataMap();
            for (int i = 0; i < dataset1.size(); i++)
            {
                IData d = new DataMap();
                d.put("BUNESS_TYPE", "02");
                d.put("BUNESS_CODE", "");

                d.put("SP_ID", dataset1.getData(i).getString("SP_ID"));
                d.put("BIZ_CODE", dataset1.getData(i).getString("BIZ_CODE") + "|" + dataset1.getData(i).getString("BIZ_TYPE_CODE"));
                d.put("BUNESS_NAME", dataset1.getData(i).getString("SERVICE_NAME"));

                String price = dataset1.getData(i).getString("PRICE", "0");
                if (price.length() > 0) {
                    if (price.startsWith("."))
                    {
                        price = "0" + price;
                    }
                }
                d.put("BUNESS_FREE", price + "元/月");

                d.put("START_TIME", dataset1.getData(i).getString("START_DATE").substring(0, 10).replace("-", ""));
                d.put("DEAD_TIME", dataset1.getData(i).getString("END_DATE").substring(0, 10).replace("-", ""));
                temp.add(d);
            }
            tempData.put("PRODUCT_TYPE", "02");// 增值类
            //进行排序操作
            if(!temp.isEmpty()){
                DataHelper.sort(temp, "START_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
            }
            tempData.put("SUB", temp.toData());
            ids.add(tempData);
        }

        // 套餐
        IDataset queryInfos = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
        if (IDataUtil.isNotEmpty(queryInfos))
        {
            IDataset dataset = new DatasetList();
            for (int i = 0; i < queryInfos.size(); i++)
            {
                String explain = queryInfos.getData(i).getString("DISCNT_EXPLAIN");
                if (explain != null && explain.length() > 128)
                {
                    explain = explain.substring(0, 128);// 接口范围长度128，实际可能更长些
                }
                String discnt = queryInfos.getData(i).getString("DISCNT_CODE", "");
                IDataset paraInfo = CommparaInfoQry.queryCommparaInfoByParaCode2("CSM", "3700", discnt);

                int tag = 0;
                String paraCode1 = "";
                String spCode = "";
                if (IDataUtil.isNotEmpty(dataset1) && IDataUtil.isNotEmpty(paraInfo))
                {
                    for (int j = 0; j < dataset1.size(); j++)
                    {
                        IData plat = dataset1.getData(j);
                        for (int k = 0; k < paraInfo.size(); k++)
                        {
                            IData para = paraInfo.getData(k);
                            if (plat.getString("SERVICE_ID", "").equals(para.getString("PARAM_CODE", "")))
                            {
                                tag++;
                                paraCode1 = para.getString("PARA_CODE1", "");
                                spCode = plat.getString("SP_CODE");
                                String price = plat.getString("PRICE", "0");
                                break;
                            }
                        }
                    }
                }

                IData temp = new DataMap();
                if (tag > 0)
                {
                    temp.put("BUNESS_TYPE", "02");
                    temp.put("BIZ_CODE", paraCode1 + "|" + "02");
                }
                else
                {
                    temp.put("BUNESS_TYPE", "01");
                    temp.put("BIZ_CODE", "");
                }
                temp.put("BUNESS_CODE", "D" + queryInfos.getData(i).getString("DISCNT_CODE"));
                temp.put("SP_ID", spCode);
                temp.put("BUNESS_NAME", queryInfos.getData(i).getString("DISCNT_NAME"));
                temp.put("BUNESS_FREE", "");
                // temp.put("ORDERING_TIME", queryInfos.getData(i).getString("START_DATE").substring(0,
                // 10).replaceAll("-", ""));
                temp.put("START_TIME", queryInfos.getData(i).getString("START_DATE").substring(0, 10).replaceAll("-", ""));
                temp.put("DEAD_TIME", queryInfos.getData(i).getString("END_DATE").substring(0, 10).replaceAll("-", ""));
                // temp.put("FEE_TYPE", "");

                dataset.add(temp);
            }

            productDataset.addAll(dataset);
        }

        // 产品
        queryInfos = UserProductInfoQry.queryUserProductByUserId(userId);
        if (IDataUtil.isNotEmpty(queryInfos))
        {
            IDataset dataset = new DatasetList();
            for (int i = 0; i < queryInfos.size(); i++)
            {
                String explain = queryInfos.getData(i).getString("PRODUCT_EXPLAIN");
                if (explain != null && explain.length() > 128)
                {
                    explain = explain.substring(0, 128);// 接口范围长度128，实际可能更长些
                }
                IData temp = new DataMap();
                temp.put("BUNESS_TYPE", "01");
                temp.put("BUNESS_CODE", "P" + queryInfos.getData(i).getString("PRODUCT_ID"));
                temp.put("SP_ID", "");
                temp.put("BIZ_CODE", "");
                temp.put("BUNESS_NAME", queryInfos.getData(i).getString("PRODUCT_NAME"));
                temp.put("BUNESS_FREE", "");
                // temp.put("ORDERING_TIME", queryInfos.getData(i).getString("START_DATE").substring(0,
                // 10).replaceAll("-", ""));
                temp.put("START_TIME", queryInfos.getData(i).getString("START_DATE").substring(0, 10).replaceAll("-", ""));
                temp.put("DEAD_TIME", queryInfos.getData(i).getString("END_DATE").substring(0, 10).replaceAll("-", ""));
                // temp.put("FEE_TYPE", "");
                dataset.add(temp);
            }
            productDataset.addAll(dataset);
        }

        IData productData = new DataMap();
        productData.put("PRODUCT_TYPE", "01");// 套餐类
        //移动接口规范：移动商城1.5.1修改，按操作时间倒序排列
        if(!productDataset.isEmpty())
            DataHelper.sort(productDataset, "START_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        productData.put("SUB", productDataset.toData());
        ids.add(productData);


        // 服务功能
        queryInfos = UserSvcInfoQry.queryGPRSSvcByUserId(userId);
        if (IDataUtil.isNotEmpty(queryInfos))
        {
            IDataset tempD = new DatasetList();
            IData tempData = new DataMap();
            for (int i = 0; i < queryInfos.size(); i++)
            {
                IData temp = new DataMap();
                temp.put("BUNESS_TYPE", "01");
                temp.put("BUNESS_CODE", "S" + queryInfos.getData(i).getString("SERVICE_ID"));
                temp.put("SP_ID", "");
                temp.put("BIZ_CODE", "");
                temp.put("BUNESS_NAME", queryInfos.getData(i).getString("SERVICE_NAME"));
                temp.put("BUNESS_FREE", "");

                // temp.put("ORDERING_TIME", "");

                temp.put("START_TIME", queryInfos.getData(i).getString("START_DATE").substring(0, 10).replaceAll("-", ""));
                temp.put("DEAD_TIME", queryInfos.getData(i).getString("END_DATE").substring(0, 10).replaceAll("-", ""));
                // temp.put("FEE_TYPE", "");
                tempD.add(temp);
            }

            tempData.put("PRODUCT_TYPE", "03");// 服务类
            //进行排序操作
            if(!tempD.isEmpty()){
                DataHelper.sort(tempD, "START_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
            }
            tempData.put("SUB", tempD.toData());
            ids.add(tempData);
        }





        // 营销活动
       /* queryInfos = UserSaleActiveInfoQry.queryUserSaleActiveByUserId(userId);
        if (IDataUtil.isNotEmpty(queryInfos))
        {
            for (int i = 0; i < queryInfos.size(); i++)
            {
                IData temp = new DataMap();
                temp.put("BUNESS_TYPE", "01");
                temp.put("BUNESS_CODE", queryInfos.getData(i).getString("CAMPN_ID"));
                temp.put("SP_ID", "");
                temp.put("BIZ_CODE", "");
                temp.put("BUNESS_NAME", queryInfos.getData(i).getString("PRODUCT_NAME") + "-" + queryInfos.getData(i).getString("PACKAGE_NAME"));
                temp.put("BUNESS_FREE", "");

                // temp.put("ORDERING_TIME", queryInfos.getData(i).getString("ACCEPT_DATE").substring(0,
                // 10).replaceAll("-", ""));
                temp.put("START_TIME", queryInfos.getData(i).getString("START_DATE").substring(0, 10).replaceAll("-", ""));
                temp.put("DEAD_TIME", queryInfos.getData(i).getString("END_DATE").substring(0, 10).replaceAll("-", ""));
                // temp.put("FEE_TYPE", "");
                productDataset.add(temp);
            }
        }*/


        IData result = new DataMap();
        //移动接口规范：移动商城1.5.1修改，按类别升序排列
        if(! ids.isEmpty()){
            DataHelper.sort(ids, "PRODUCT_TYPE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        }

        result.putAll(ids.toData());
        result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));

        return new DatasetList(result);
    }


    /**
     * GPRS状态查询
     * @param param
     * @return
     * @throws Exception
     */
    public IData getUserGPRS(IData param) throws Exception
    {
        IData returnData = new DataMap();

        //用户凭证校验
        IData userInfo = CheckParam.isCheckIdentAuth(param);
        String userId = userInfo.getString("USER_ID");

        String startDate = "";
        String endDate = "";
        String state = "";
        IDataset ids = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userId, "22");
        if (IDataUtil.isNotEmpty(ids))
        {
            startDate = ids.getData(0).getString("START_DATE");
            endDate = ids.getData(0).getString("END_DATE");
            state = "0"; //开通
        }
        else
        {
            startDate = SysDateMgr.getSysTime();
            endDate = startDate;
            state = "1"; //未开通
        }

        startDate = startDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
        endDate = endDate.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");

        returnData.put("USER_ID", param.getString("SERIAL_NUMBER"));
        returnData.put("EFFECT_TIME", startDate);
        returnData.put("VALID_TIME", endDate);
        returnData.put("STATE", state);
        returnData.put("OPR_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, SysDateMgr.getSysTime()));

        return returnData;
    }

    private IData spellReturnInfo(IData param, String[] charge)
    {
        IData tempdata = new DataMap();
        tempdata.put("SEQUENCE", charge[0]);
        tempdata.put("ACCOUNTPIN", charge[1]);
        tempdata.put("CHRGMSISDN", charge[2]);
        tempdata.put("COUNTTOTAL", charge[3]);
        tempdata.put("TRADETIME", charge[4]);
        tempdata.put("PHONE_NUMBER", param.getString("PHONE_NUMBER"));
        return tempdata;
    }

    public IDataset valueCardUseQuery(IData input) throws Exception {
        IDataset dataset = new DatasetList();

        //客户凭证验证
        CheckParam.isCheckIdentAuth(input);
        IData param = new DataMap();

        String serial_number = input.getString("SERIAL_NUMBER");
        param.put("PHONE_NUMBER", serial_number);

        String start_time = input.getString("QRY_STR_TIME");
        String end_time = input.getString("QRY_END_TIME");

        String qry_time = SysDateMgr.date2String(SysDateMgr.string2Date(start_time, "yyyyMMddHHmmss"), "yyyy-MM-dd");
        String qry_end =  SysDateMgr.date2String(SysDateMgr.string2Date(end_time, "yyyyMMddHHmmss"), "yyyy-MM-dd");

        param.put("START_DATE", qry_time);

        param.put("END_DATE", qry_end);

        CWSVarNode mVVarList[] = new CWSVarNode[4];

        int timeOut = 60000;// 调用SMPVC超时时间

        String paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "TIME_OUT");

        timeOut = Integer.parseInt(paramValue);

        CWSServNode servNode = new CWSServNode();
        servNode.setMStrServName("G2914");

        CWSServNode mVServList[] = new CWSServNode[1];
        mVServList[0] = servNode;

        CWSVarNode varNode = new CWSVarNode();

        varNode.setMStrName("G004");
        varNode.setMStrValue(input.getString("SERIAL_NUMBER"));
        mVVarList[0] = varNode;

        CWSVarNode varNode1 = new CWSVarNode();
        varNode1.setMStrName("V934");
        varNode1.setMStrValue(qry_time);
        mVVarList[1] = varNode1;

        CWSVarNode varNode2 = new CWSVarNode();
        varNode2.setMStrName("V935");
        varNode2.setMStrValue(qry_end);
        mVVarList[2] = varNode2;

        CWSInputData inputData = new CWSInputData();
        inputData.setMStrOrderID(SysDateMgr.getSysDate("yyyyMMddHHmmssSSS"));
        inputData.setMStrSerialNumber(input.getString("SERIAL_NUMBER"));
        inputData.setMStrSwitchid("SMP10");
        inputData.setNPriority(50);
        inputData.setMVServList(mVServList);
        inputData.setMVVarList(mVVarList);

        StringHolder m_strOrderID = new StringHolder();
        IntHolder m_nOperationResult = new IntHolder();
        StringHolder m_strFinishTime = new StringHolder();
        StringHolder m_strErrorDescription = new StringHolder();
        IntHolder m_nCMDCount = new IntHolder();
        StringHolder m_strAutoCMDList = new StringHolder();
        CWSVarNodeHolder m_vQueryResult = new CWSVarNodeHolder();
        
        //新联指
        String operationResult = null;
        String errorDescription = null;
        String cmdList = null;
        java.util.List<Element> queryResultList = null;

        //因外部接口地址无法调用，测试环境暂时屏蔽。

       // WSSOP_ServiceLocator wssopLocator = new WSSOP_ServiceLocator("http://10.200.141.51:20001/");
        /**
         * 合版本 2017/5/3 duhj
         */
        String checktype="0";	//默认不使用新调用方式
        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "CHECK_TYPE");
        if (StringUtils.isNotBlank(paramValue))
        	
            {checktype = paramValue;}
        String url = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "QRYURL"); 
        if(checktype.equals("1")){
			SoapInputXml soapinput = new SoapInputXml();
			URL u=new URL(url);
			NeaSoapBindingStub neasop=new NeaSoapBindingStub(u,new Service());
			String xmlParamStr = null;
			
			String serlial_number = input.getString("SERIAL_NUMBER");
			String priority = "50";
			xmlParamStr = soapinput.receiveMobileRecharge_second(SysDateMgr.getSysDate("yyyyMMddHHmmssSSS"),serlial_number,priority,"SMP10",mVServList,mVVarList);
			String res =neasop.callWSSOP(xmlParamStr); //新联指调用
			 Document dom=DocumentHelper.parseText(res);
			  Element root=dom.getRootElement();
			  operationResult=root.element("operationResult").getText();
			  errorDescription=root.element("description").getText();
			  cmdList=root.element("cmdList").getText();
			  queryResultList = root.elements("queryResultList");
		}else{
            WSSOP_ServiceLocator wssopLocator = new WSSOP_ServiceLocator(url);
            WSSOPStub binding = (WSSOPStub) wssopLocator.getWSSOP();
            binding.setTimeout(timeOut);
            binding.callWSSOP(inputData, m_strOrderID, m_nOperationResult, m_strFinishTime, m_strErrorDescription, m_nCMDCount, m_strAutoCMDList, m_vQueryResult);
		}
        IData queryResult = new DataMap();
        
        if(queryResultList != null){
    		String strName= null;
    		String strValue= null;
    		for(int i =0; i<queryResultList.size();i++){
    		   strName = ((Element) queryResultList.get(i)).element("strName").getText();
    		   strValue = ((Element) queryResultList.get(i)).element("strValue").getText();
    		   queryResult.put(strName, strValue);

    		}
    	}
        
        if(m_vQueryResult.value != null && !"".equals(m_vQueryResult.value)) {
            Iterator iterator = (Iterator) m_vQueryResult.value.iterator();
            CWSVarNode node = new CWSVarNode();
            while(iterator.hasNext()) {
                node = (CWSVarNode) iterator.next();
                queryResult.put(node.getMStrName(), node.getMStrValue());
            }
        }
        
        queryResult.put("PAYMENT_CHANNEL", "充值卡");//固定值
        
/*      测试数据 
        queryResult.put("ACCOUNTPIN", "18289501414");
        queryResult.put("SEQUENCE", "11511030340592793");
        queryResult.put("COUNTTOTAL/100", "30");
        queryResult.put("CRDDAY", "20171231235959");
        queryResult.put("TRADETIME", "20160801105116");
        queryResult.put("CRDFLG_T", "3");
        queryResult.put("TradeTypeDesc", "01");
        queryResult.put("ACTDAY", "20171231235959");*/
        
        dataset.add(queryResult);
        IDataset result = new DatasetList();

        for(int i = 0 ; i < dataset.size() ; i++){

            IData resultdata = new DataMap();
            IData data = dataset.getData(i);
            String inchargenum = data.getString("ACCOUNTPIN");            
            resultdata.put("USER_ID", inchargenum);
            IData userInfo = UcaInfoQry.qryUserInfoBySn(inchargenum);

            String userCity = "海南海口";
            if (IDataUtil.isNotEmpty(userInfo)) {
                String eparchycode = userInfo.getString("EPARCHY_CODE", "").trim();

                if (!eparchycode.equals("")) {
                    userCity = UAreaInfoQry.getAreaNameByAreaCode(eparchycode);
                }
            }
            resultdata.put("NO_PROV", userCity);

            resultdata.put("PAYMENT_CHANNEL", dataset.getData(i).getString("PAYMENT_CHANNEL"));
            resultdata.put("CARD_NUMBER", dataset.getData(i).getString("SEQUENCE"));
            resultdata.put("SERIAL_NUMBER", serial_number);
            resultdata.put("CARD_NO", dataset.getData(i).getString("COUNTTOTAL/100"));
            resultdata.put("VALID_TIME", dataset.getData(i).getString("CRDDAY"));
            resultdata.put("TRANS_ACTIONTIME",dataset.getData(i).getString("TRADETIME"));

            String state = dataset.getData(i).getString("CRDFLG_T");
            String card_state = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), "res","RES_STATE_DEF", new java.lang.String [] {"RES_TYPE_ID","TABLE_COL","STATE_CODE"}, "STATE_NAME", new java.lang.String [] {"3","RES_STATE",state});
            resultdata.put("CARD_STATE",card_state);
            resultdata.put("SUM_TYPE",dataset.getData(i).getString("TradeTypeDesc"));
            resultdata.put("REMARK",dataset.getData(i).getString("ACTDAY"));

            result.add(resultdata);
        }

        return result;
    }

    /**
     * @Function: getMobileUserInfo
     * @Description: ITF_CRM_GetUserInfo4Mobile 一级BOSS手机客户端接口-个人信息查询
     * @param: @param data
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 5:02:46 PM Jul 24, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Jul 24, 2013 longtian3 v1.0.0 TODO:
     */
    /**
     * 手机营业厅-个人信息查询
     *
     * @param input
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-06-24 15:59:15
     */
    public IData getMobileUserInfo(IData data) throws Exception
    {
        IData result = new DataMap();

        //对身份凭证进行鉴权
        IData userData = CheckParam.isCheckIdentAuth(data);
        String userId = userData.getString("USER_ID");

        QueryInfoBean queryInfoBean = BeanManager.createBean(QueryInfoBean.class);
        data.put("X_GETMODE", 1);// 1-输入用户标识
        data.put("USER_ID", userId);
        // IDataset
        IDataset dataset = queryInfoBean.getUserCustAcct(data);

        // 用户姓名
        String userName = dataset.getData(0).getString("CUST_NAME", "");
        // 用户品牌
        String userBrand = dataset.getData(0).getString("BRAND_CODE", "");
        // 客户等级
        String userLevel = dataset.getData(0).getString("CLASS_ID", "");
        // 用户状态
        String userStatus = dataset.getData(0).getString("USER_STATE_CODESET", "");
        // 入网时间
        String userBegin = dataset.getData(0).getString("IN_DATE", "");
        // Email地址
        String email = dataset.getData(0).getString("EMAIL", "");
        // 邮寄地址
        String userAdd = dataset.getData(0).getString("POST_ADDRESS", "");
        // 邮政编码
        String zipCode = dataset.getData(0).getString("POST_CODE", "");
        // 联系电话
        String userNum = dataset.getData(0).getString("PHONE", "");

        // SIM
        String simCardNo = dataset.getData(0).getString("SIM_CARD_NO", "");

        // 归属地
        String provName = UAreaInfoQry.getAreaNameByAreaCode(getVisit().getProvinceCode());
        String cityName = UAreaInfoQry.getAreaNameByAreaCode(userData.getString("EPARCHY_CODE"));

        String userCity = provName + cityName;

        userBrand = convertBrandCode(userBrand);
        userLevel = getCustLevelParam(userLevel);
        userStatus = getUserStateParam(userStatus);
        userBegin = userBegin.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");

        // 客户名称限制32位
        char ch[] = userName.toCharArray();
        int j = 0;
        for (int i = 0; i < ch.length; i++)
        {
            if (ch[i] > 255)
            {
                j = j + 2;
            }
            else
            {
                j = j + 1;
            }
            if (j > 32)
            {
                j = i;
                break;
            }
        }
        if (j > ch.length)
            j = ch.length;
        userName = userName.substring(0, j);


        // 中高端标识
        IDataset highCust = CustVipInfoQry.queryHighCustByUserId(userId, "*");
        String highLevelId = "";
        if (IDataUtil.isNotEmpty(highCust))
        {
            highLevelId = "1";
        }
        else
        {
            highLevelId = "0";
        }

        String provInfo = StaticInfoQry.qryProvCode(getVisit().getProvinceCode());

        result.put("USER_NAME", blurCustomerNameNewRule(userName));// 此处需要模糊化，以后进行配置
        // StringUtilForIntf.blurCustomerNameNewRule(userName));
        result.put("USER_BRAND", userBrand);
        result.put("USER_LEVEL", userLevel);
        result.put("USER_STATUS", userStatus);
        result.put("USER_BEGIN", userBegin);
        result.put("EMAIL", email);
        result.put("USER_ADD", userAdd);
        result.put("ZIP_CODE", zipCode);
        result.put("USER_NUM", userNum);
        result.put("SIMCARD_NO", simCardNo);
        result.put("STAR_CLASS", highLevelId);
        result.put("CITY_CODE", userCity);
        result.put("PROV_CODE", provInfo);

        //result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));

        // 手机营业厅二期改造
        IDataset vipInfo = CustVipInfoQry.qryVipInfoByUserId(userId);
        if (IDataUtil.isEmpty(vipInfo))
        {
            // 没有大客户信息
            result.put("USER_ID", "00");// 普通客户
        }
        else
        {
            result.put("USER_ID", "01");// VIP客户
            result.put("VIP_NUMBER", vipInfo.getData(0).getString("VIP_CARD_NO", ""));// VIP卡号
            String vipTypeCode = vipInfo.getData(0).getString("VIP_TYPE_CODE", "");// VIP等级
            if ("1".equals(vipTypeCode))
            {
                result.put("VIP_LEVEL", "00");
            }
            else if ("2".equals(vipTypeCode))
            {
                result.put("VIP_LEVEL", "01");
            }
            else if ("3".equals(vipTypeCode))
            {
                result.put("VIP_LEVEL", "02");
            }
            else
            {
                // 除1、2、3外其他归为普通大客户
                result.put("USER_ID", "00");// 普通客户
            }

            if ("01".equals(result.getString("USER_ID")))
            {// VIP客户
                String vipCardEndDate = ((IData) vipInfo.get(0)).getString("VIP_CARD_END_DATE", "");// VIP有效期
                if (!"".equals(vipCardEndDate))
                {
                    vipCardEndDate = vipCardEndDate.substring(0, 10).replace("-", "");
                }
                result.put("VIP_DATE", vipCardEndDate);

                String custManagerId = ((IData) vipInfo.get(0)).getString("CUST_MANAGER_ID", "");
                result.put("CUSTOMER_INFO", UStaffInfoQry.getStaffSnByStaffId(custManagerId));// 客户经理联系电话
                if ("".equals(result.getString("CUSTOMER_INFO", "")))
                {// 配合一级BOSS，客户经理电话为空时默认
                    result.put("CUSTOMER_INFO", "13900000000");
                }

                String vip_class_id = vipInfo.getData(0).getString("VIP_CLASS_ID", "");

                // 机场VIP免费剩余次数
                int totalCount = 0;// 总免费次数
                int usedCount = 0;// 已用免费次数
                int planNunmber = 0;// 剩余免费次数
                if ("1".equals(vipTypeCode) || "2".equals(vipTypeCode) || "3".equals(vipTypeCode))
                {
                    if ("1".equals(vip_class_id))
                    { // 钻卡，最多免费次数默认12
                        totalCount = 12;
                    }
                    else if ("2".equals(vip_class_id))
                    {// 金卡，最多免费次数默认6
                        totalCount = 6;
                    }
                    else if ("3".equals(vip_class_id))
                    {// 银卡，最多免费次数默认3
                        totalCount = 3;
                    }
                    else
                    {
                        totalCount = 0;
                    }
                }
                IDataset ids = UserOtherInfoQry.getUserOtherByUseridRsrvcode(userData.getString("USER_ID"), "AREM", null);
                if (IDataUtil.isNotEmpty(ids))
                {
                    usedCount = ids.getData(0).getInt("RSRV_STR1", 0);
                }
                if (totalCount > usedCount)
                {
                    planNunmber = totalCount - usedCount;
                }

                result.put("PLAN_NUMBER", planNunmber);
            }
        }
        //移动商城1.6添加
        // 1、4G用户标识字段。0：是4G 1：非4G，
     /*   IDataset resF = CSAppCall.call("SS.UsimUserSVC.query4GSimCard", data);
        String is4gFlag = resF.getData(0).getString("X_TAGCHAR");
        if ("false".equals(is4gFlag))
        {
            result.put("4G_FLAG", "1");//非4G
        }
        else
        {
            result.put("4G_FLAG", "0");//是4G
        }*/

        //2、VOLTE标识字段
     /*   result.put("VOLTE_FLAG", "1");
        //3、 AccoutDay 出账日标识字段
        IDataset userAcctDayInfo = UserAcctDayInfoQry.getUserAcctDay(userId);
        if (!userAcctDayInfo.isEmpty())
        {
            result.put("ACCOUT_DAY", userAcctDayInfo.getData(0).getString("ACCT_DAY"));
        }
        else
        {
            result.put("ACCOUT_DAY", "1");
        }*/


        //移动商城1.5添加
        //1.添加用户实名制信息
        IData queryData = UcaInfoQry.qryCustomerInfoByCustId(userData.getString("CUST_ID"));
        if(queryData == null)
            CSAppException.apperr(CrmUserException.CRM_USER_397);
        result.put("REAL_NAME_INFO", "1".equals(queryData.getString("IS_REAL_NAME")) ? "2" : "1");//2:已登记，1:未登记
        result.put("USER_UNIQUE", userData.getString("USER_ID"));//因上面代码以用USER_ID字段，此处另起名称
        return result;
    }


    /*
     * 品牌代码转换 输入值： G001：全球通 G002：神州行 G010：动感地带 返回值： 01：全球通；02：神州行；03：动感地带；09：其他品牌
     */
    public String convertBrandCode(String brandCode) throws Exception
    {
        String group_brand = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", new String[]{"PARAM_ATTR",
                        "PARAM_CODE"},
                "PARA_CODE1", new String[]{"998", brandCode});
        String result = "";
        if (StringUtils.isBlank(group_brand))
        {
            return "";
        }

        if ("0".equals(group_brand))
        {
            result = "01";// 全球通
        }
        else if ("1".equals(group_brand))
        {
            result = "02";// 神州行
        }
        else if ("2".equals(group_brand))
        {
            result = "03";// 动感地带
        }
        else if ("3".equals(group_brand))
        {
            result = "02";// 神州行
        }
        else
        {
            result = "09";// 其它品牌
        }

        return result;
    }

    private String convertVipLevel(String vipLevel)
    {
        String result = "";
        if ((vipLevel == null) || ("".equals(vipLevel)))
        {
            return "";
        }

        if ("1".equals(vipLevel))
        {
            result = "";// 贵宾卡
        }
        else if ("2".equals(vipLevel))
        {
            result = "02";// 银卡
        }
        else if ("3".equals(vipLevel))
        {
            result = "01";// 金卡
        }
        else if ("4".equals(vipLevel))
        {
            result = "00";// 钻石卡
        }

        return result;
    }

    private int queryFreeCount(String vip_class_id, String vip_type_code, String userId) throws Exception
    {
        int freeCount = 0;// 剩余免费次数
        int userFreeCount = 0;// 已用免费次数
        int totalFreeCount = 0;// 总的免费次数

        if ("0".equals(vip_type_code) || "2".equals(vip_type_code) || "5".equals(vip_type_code))
        {
            if ("4".equals(vip_class_id))
            { // 钻卡，最多免费次数默认12
                totalFreeCount = 12;
            }
            else if ("3".equals(vip_class_id))
            {// 金卡，最多免费次数默认6
                totalFreeCount = 6;
            }
            else if ("2".equals(vip_class_id))
            {// 银卡，最多免费次数默认3
                totalFreeCount = 3;
            }
            else
            {
                totalFreeCount = 0;
            }
        }

        String rsrvValueCode = "AREM";
        IDataset freeCountInfos = UserOtherInfoQry.getUserOther(userId, rsrvValueCode);
        if (freeCountInfos != null && freeCountInfos.size() > 0)
        {
            userFreeCount = freeCountInfos.getData(0).getInt("RSRV_STR1", 0);
        }

        if (totalFreeCount > userFreeCount)
        {
            freeCount = totalFreeCount - userFreeCount;
            if ("2".equals(vip_type_code) && "4".equals(vip_class_id) && freeCount <= 6)
            {
                freeCount = 12;
            }
            else if ("2".equals(vip_type_code) && "3".equals(vip_class_id) && freeCount <= 3)
            {
                freeCount = 6;
            }
        }
        return freeCount;
    }

    /**
     * 客户级别
     */
    private String getCustLevelParam(String param)
    {
        String result = "";

        if ("0".equals(param))
            result = "300";
        else if ("1".equals(param))
            result = "304";
        else if ("2".equals(param))
            result = "303";
        else if ("3".equals(param))
            result = "302";
        else if ("4".equals(param))
            result = "301";
        else if ("A".equals(param))
            result = "302";
        else if ("B".equals(param))
            result = "301";
        else if ("C".equals(param))
            result = "303";
        else if ("D".equals(param))
            result = "302";
        else if ("E".equals(param))
            result = "301";
        else
            result = "100";

        return result;
    }

    /**
     * 获取用户状态编码
     */
    private String getUserStateParam(String param)
    {
        String result = "";

        if ("0".equals(param))
            result = "00";
        else if ("1".equals(param))
            result = "02";
        else if ("2".equals(param))
            result = "02";
        else if ("3".equals(param))
            result = "02";
        else if ("4".equals(param))
            result = "02";
        else if ("5".equals(param))
            result = "02";
        else if ("6".equals(param))
            result = "04";
        else if ("7".equals(param))
            result = "02";
        else if ("8".equals(param))
            result = "03";
        else if ("9".equals(param))
            result = "03";
        else if ("A".equals(param))
            result = "01";
        else if ("B".equals(param))
            result = "01";
        else if ("C".equals(param))
            result = "02";
        else if ("D".equals(param))
            result = "02";
        else if ("E".equals(param))
            result = "04";
        else if ("F".equals(param))
            result = "03";
        else if ("G".equals(param))
            result = "01";
        else if ("H".equals(param))
            result = "03";
        else if ("I".equals(param))
            result = "02";
        else if ("J".equals(param))
            result = "02";
        else if ("K".equals(param))
            result = "02";
        else if ("L".equals(param))
            result = "02";
        else if ("M".equals(param))
            result = "02";
        else if ("N".equals(param))
            result = "00";
        else if ("O".equals(param))
            result = "02";
        else if ("Q".equals(param))
            result = "02";
        else
            result = "00";

        return result;
    }

    /**
     *
     * @param name
     * @return	保留姓名最后一位,其它使用x模糊化
     */
    public String blurCustomerNameNewRule(String name)
    {
        //返回模糊后名字
        StringBuilder retName = new StringBuilder();

        if (name == null || "".equals(name))
        {
            return "";
        }

        // x模糊
        for (int i = 0; i < name.length() - 1; i++)
        {
            retName.append("x");
        }

        retName.append(name.substring(name.length() - 1, name.length()));

        return retName.toString();
    }

    /**************************************************************************************************
     * 根据用户号码[serialNumber]，查询用户信用等级信息<BR/>
     * @param pd
     * @param serialNumber
     * @return
     * @throws Exception
     */
    IData queryUserCreditInfo(String serialNumber) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("X_IDTYPE", "4");
        inParam.put("ID", serialNumber);
        IDataset userInfo = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        String userId = userInfo.getData(0).getString("USER_ID");
        IData userData = CreditCall.queryUserCreditInfos(userId);
        //-1未评级//0	准星级//1	一星级//2	二星级//3	三星级//4	四星级//5	五星级//6	五星金//7	五星钻
        //信控返回-1时转换为0
        if (Integer.valueOf(userData.getString("CREDIT_CLASS", "0")) < 0)
        {
            //CSAppException.appError("520002", "用户[" + serialNumber + "]的信用等级未评级，请联系管理员！");mod by wangdelong 错误码改造
            userData.put("CREDIT_CLASS", "0");
        }
        if (Integer.valueOf(userData.getString("STAR_SCORE", "0")) < 0)
            userData.put("STAR_SCORE", "0");
        return userData;
    }


    public IDataset querySimBakInfo(String serialNumber, String removeTag) throws Exception {
        DataMap param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("REMOVE_TAG", removeTag);
        return Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_SN_ALLINFO", param);
    }


    /**
     * @Function: 用户活动查询/客户购机信息查询
     * @Description: 接口定义 ITF_CRM_GetUserSaleActiveInfo
     * @param： IData
     * @return：IDataset
     * @throws：Exception
     * @version: v1.0.0
     * @author: huanghui@asiainfo-linkage.com
     * @date: 下午2:57:18 2013-6-21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-6-21 huanghui v1.0.0 TODO:
     */
    public IDataset getSaleActiveInfo(IData data) throws Exception
    {
        //客户凭证检查

        IDataset results = new DatasetList();
        IDataset userInfo = new DatasetList();
        String eparchyCode = BizRoute.getRouteId();
        // 获取用户USER_ID

        if (data.getString("PRODUCT_ID") == null || "".equals(data.getString("PRODUCT_ID")))
        {
            data.put("PRODUCT_ID", "0");
        }
        else
        {
            data.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        }

        if (data.getString("PROCESS_TAG") == null || "".equals(data.getString("PROCESS_TAG")))
        {
            data.put("PROCESS_TAG", "9");
        }
        else
        {
            data.put("PROCESS_TAG", data.getString("PROCESS_TAG"));
        }

        if (data.getString("START_DATE") == null || "".equals(data.getString("START_DATE")))
        {
            data.put("START_DATE", SysDateMgr.decodeTimestamp("2000-01-01", SysDateMgr.PATTERN_STAND));
        }
        else
        {
            data.put("START_DATE", data.getString("START_DATE"));
        }

        if (data.getString("END_DATE") == null || "".equals(data.getString("END_DATE")))
        {
            data.put("END_DATE", SysDateMgr.decodeTimestamp("2050-01-01", SysDateMgr.PATTERN_STAND));
        }
        else
        {
            data.put("END_DATE", data.getString("END_DATE"));
        }
        String userid = ((IData) userInfo.get(0)).getString("USER_ID");
        data.put("USER_ID", userid);
        results = UserSaleActiveInfoQry.getSaleActiveInfoOnInterface(data);
        return results;
    }


    /**
     * @des 三户资料查询
     * @author huangsl
     * @param input
     *            SERIAL_NUMBER USER_ID CUST_ID ACCT_ID VIP_NO X_GETMODE(0-输入服务号码取正常用户 1-输入用户标识 2-输入客户标识取正常用户
     *            3-输入服务号码取所有用户 4-输入服务号码取所有非正常用户 5-输入帐户标识取正常用户[不支持这种方式] 6-输入VIP卡号取正常用户 7-输入服务号码取最后销户用户[该号码必须无正常用户]
     * @return
     * @throws Exception
     */
    public IDataset getUserCustAcct(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        int getMode = input.getInt("X_GETMODE");
        String userId = input.getString("USER_ID", "");
        String custId = input.getString("CUST_ID", "");
        String serialNumber = input.getString("SERIAL_NUMBER", "");
        IDataset users = null;
        if (getMode == 0)
        {// 0-输入服务号码取正常用户
            if (StringUtils.isBlank(serialNumber))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_695, "SERIAL_NUMBER");
            }
            users = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn(serialNumber));
        }
        else if (getMode == 1)
        {// 1-输入用户标识
            if (StringUtils.isBlank(userId))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_695, "USER_ID");
            }
            users = IDataUtil.idToIds(UcaInfoQry.qryUserInfoByUserId(userId));
        }
        else if (getMode == 2)
        {// 2-输入客户标识取正常用户
            if (StringUtils.isBlank(custId))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_695, "CUST_ID");
            }
            users = UserInfoQry.getUserInfoByCstId(custId, null);
        }
        else if (getMode == 3)
        {// 3-输入服务号码取所有用户
            if (StringUtils.isBlank(serialNumber))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_695, "SERIAL_NUMBER");
            }
            users = UserInfoQry.queryAllUserInfoBySn(serialNumber);
        }
        else if (getMode == 4)
        {// 4-输入服务号码取所有非正常用户
            if (StringUtils.isBlank(serialNumber))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_695, "SERIAL_NUMBER");
            }
            users = UserInfoQry.getUserInfoBySnDestroyAll(serialNumber, null);
        }
        else if (getMode == 5)
        {// 5-输入帐户标识取正常用户[不支持这种方式]
            CSAppException.apperr(CrmCommException.CRM_COMM_1113);
            return null;
        }
        else if (getMode == 6)
        {// 6-输入VIP卡号取正常用户
            if (StringUtils.isBlank(input.getString("VIP_NO")))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_695, "VIP_NO");
            }
            users = CustVipInfoQry.queryVipInfoByVipNo("0", input.getString("VIP_NO"));
        }
        else if (getMode == 7)
        {// 7-输入服务号码取最后销户用户[该号码必须无正常用户]
            if (StringUtils.isBlank(serialNumber))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_695, "SERIAL_NUMBER");
            }
            users = UserInfoQry.getDestroyUserInfoBySn(serialNumber);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1113);
            return null;
        }

        if (IDataUtil.isEmpty(users))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        int usersSize = users.size();
        for (int i = 0; i < usersSize; i++)
        {
            if (users.getData(i) == null || StringUtils.isBlank(users.getData(i).getString("USER_ID")))
            {
                continue;
            }
            IData result = getUserCustAcctInfo(users.getData(i).getString("USER_ID"));
            results.add(result);
        }
        int resultsSize = results.size();
        for (int i = 0; i < resultsSize; i++)
        {
            IData tmp = results.getData(i);
            if (IDataUtil.isEmpty(tmp))
            {
                continue;
            }
            tmp.put("BRAND", UBrandInfoQry.getBrandNameByBrandCode(tmp.getString("BRAND_CODE")));
            tmp.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(tmp.getString("PRODUCT_ID")));
            tmp.put("X_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("EPARCHY_CODE")));
            tmp.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("CITY_CODE")));
            tmp.put("USER_TYPE", UUserTypeInfoQry.getUserTypeByUserTypeCode(tmp.getString("USER_TYPE_CODE")));

            String acctTag = tmp.getString("ACCT_TAG");
            if ("0".equals(acctTag))
                tmp.put("X_ACCT_TAG_NAME", "正常处理");
            else if ("1".equals(acctTag))
                tmp.put("X_ACCT_TAG_NAME", "定时激活");
            else if ("2".equals(acctTag))
                tmp.put("X_ACCT_TAG_NAME", "待激活用户");
            else if ("Z".equals(acctTag))
                tmp.put("X_ACCT_TAG_NAME", "不出帐");
            else
                tmp.put("X_ACCT_TAG_NAME", "未知出账标志");

            String prepayTag = tmp.getString("PREPAY_TAG");
            if ("0".equals(prepayTag))
                tmp.put("X_PREPAY_TAG_NAME", "后付费");
            else if ("1".equals(prepayTag))
                tmp.put("X_PREPAY_TAG_NAME", "预付费");
            else
                tmp.put("X_PREPAY_TAG_NAME", "未知预付费标志");

            String removeTag = tmp.getString("REMOVE_TAG");
            if ("0".equals(removeTag))
                tmp.put("X_REMOVE_TAG_NAME", "正常");
            else if ("1".equals(removeTag))
                tmp.put("X_REMOVE_TAG_NAME", "主动预销号");
            else if ("2".equals(removeTag))
                tmp.put("X_REMOVE_TAG_NAME", "主动销号");
            else if ("3".equals(removeTag))
                tmp.put("X_REMOVE_TAG_NAME", "欠费预销号");
            else if ("4".equals(removeTag))
                tmp.put("X_REMOVE_TAG_NAME", "欠费销号");
            else if ("5".equals(removeTag))
                tmp.put("X_REMOVE_TAG_NAME", "开户返销");
            else if ("6".equals(removeTag))
                tmp.put("X_REMOVE_TAG_NAME", "过户注销");
            else
                tmp.put("X_REMOVE_TAG_NAME", "未知注销标志");

            String openMode = tmp.getString("OPEN_MODE");
            if ("0".equals(openMode))
                tmp.put("X_OPEN_MODE_NAME", "正常");
            else if ("1".equals(openMode))
                tmp.put("X_OPEN_MODE_NAME", "预开未返单");
            else if ("2".equals(openMode))
                tmp.put("X_OPEN_MODE_NAME", "预开已返单");
            else if ("3".equals(openMode))
                tmp.put("X_OPEN_MODE_NAME", "过户新增");
            else if ("4".equals(openMode))
                tmp.put("X_OPEN_MODE_NAME", "当日返单并过户");
            else
                tmp.put("X_OPEN_MODE_NAME", "未知开户方式标志");

            // 获取担保类型
            tmp.put("ASSURE_TYPE", StaticUtil.getStaticValue("TD_S_ASSURETYPE", tmp.getString("ASSURE_TYPE_CODE")));

            tmp.put("X_DEVELOP_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("DEVELOP_EPARCHY_CODE")));
            tmp.put("X_DEVELOP_CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("DEVELOP_CITY_CODE")));
            tmp.put("X_DEVELOP_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tmp.getString("DEVELOP_DEPART_ID")));
            tmp.put("X_DEVELOP_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(tmp.getString("DEVELOP_STAFF_ID")));
            tmp.put("X_IN_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tmp.getString("IN_DEPART_ID")));
            tmp.put("X_IN_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(tmp.getString("IN_STAFF_ID")));
            tmp.put("X_REMOVE_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("REMOVE_EPARCHY_CODE")));
            tmp.put("X_REMOVE_CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tmp.getString("REMOVE_CITY_CODE")));
            tmp.put("X_REMOVE_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tmp.getString("REMOVE_DEPART_ID")));

            tmp.put("REMOVE_REASON", StaticUtil.getStaticValue("DESTROY_REASON", tmp.getString("REMOVE_REASON_CODE")));
            // 获取客户类型
            tmp.put("X_CUST_TYPE", StaticUtil.getStaticValue("CUST_TYPE", tmp.getString("CUST_TYPE")));
            // 获取客户状态
            tmp.put("X_CUST_STATE", StaticUtil.getStaticValue("CUST_STATE", tmp.getString("CUST_STATE")));

            tmp.put("PSPT_TYPE", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_PASSPORTTYPE", new String[]
                    { "EPARCHY_CODE", "PSPT_TYPE_CODE" }, "PSPT_TYPE", new String[]
                    { tmp.getString("EPARCHY_CODE"), tmp.getString("PSPT_TYPE_CODE") }));

            // 获取客户性别
            tmp.put("X_SEX", StaticUtil.getStaticValue("SEX", tmp.getString("SEX")));
            // 获取客户国家名称
            tmp.put("NATIONALITY_NAME", StaticUtil.getStaticValue("TD_S_NATIONALITY", tmp.getString("NATIONALITY_CODE")));
            // 获取客户籍贯
            tmp.put("LOCAL_NATIVE_NAME", StaticUtil.getStaticValue("TD_S_LOCAL_NATIVE", tmp.getString("LOCAL_NATIVE_CODE")));
            // 获取客户语言
            tmp.put("LANGUAGE_NAME", StaticUtil.getStaticValue("TD_S_LANGUAGE", tmp.getString("LANGUAGE_CODE")));
            // 获取客户民族
            tmp.put("FOLK", StaticUtil.getStaticValue("TD_S_FOLK", tmp.getString("FOLK_CODE")));
            // 获取客户工作类型
            tmp.put("JOB_TYPE", StaticUtil.getStaticValue("TD_S_JOBTYPE", tmp.getString("JOB_TYPE_CODE")));
            // 获取客户教育程度
            tmp.put("EDUCATE_DEGREE", StaticUtil.getStaticValue("CUSTPERSON_EDUCATEDEGREECODE", tmp.getString("EDUCATE_DEGREE_CODE")));
            // 获取客户信仰
            tmp.put("RELIGION_NAME", StaticUtil.getStaticValue("TD_S_RELIGION", tmp.getString("RELIGION_CODE")));
            // 获取客户收入等级
            tmp.put("REVENUE_LEVEL", StaticUtil.getStaticValue("TD_S_REVENUE_LEVEL", tmp.getString("REVENUE_LEVEL_CODE")));
            // 获取客户婚姻状况
            tmp.put("X_MARRIAGE", StaticUtil.getStaticValue("CUSTPERSON_MARRIAGESTATE", tmp.getString("MARRIAGE")));
            // 获取客户性格类型
            tmp.put("CHARACTER_TYPE", StaticUtil.getStaticValue("TD_S_CHARACTERTYPE", tmp.getString("CHARACTER_TYPE_CODE")));
            // 获取客户优先联系方式
            tmp.put("CONTACT_TYPE", StaticUtil.getStaticValue("TD_S_CONTACTTYPE", tmp.getString("CONTACT_TYPE_CODE")));
            // 获取账户类型
            tmp.put("PAY_MODE", StaticUtil.getStaticValue("TD_S_PAYMODE", tmp.getString("PAY_MODE_CODE")));
            // 获取银行名称
            tmp.put("BANK", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK", "BANK_CODE", "BANK", tmp.getString("BANK_CODE")));
            // 获取大客户经理相关信息
            IData custMgrdata = UStaffInfoQry.qryCustManagerInfoByCustManagerId(tmp.getString("CUST_MANAGER_ID"));
            if (IDataUtil.isEmpty(custMgrdata))
            {
                tmp.put("VIP_MANAGER_NAME", "");
                tmp.put("LINK_PHONE", "");
            }
            else
            {
                tmp.put("VIP_MANAGER_NAME", custMgrdata.getString("CUST_MANAGER_NAME"));
                tmp.put("LINK_PHONE", custMgrdata.getString("LINK_PHONE"));
            }

            // 获取大客户归属集团名称
            if (StringUtils.isBlank(tmp.getString("VPMN_GROUP_ID")))
            {
                tmp.put("VPMN_GROUP_NAME", "");
            }
            else
            {
                tmp.put("VPMN_GROUP_NAME", tmp.getString("CUST_NAME"));
            }

            // 获取担保客户名称
            if (StringUtils.isBlank(tmp.getString("ASSURE_CUST_ID")))
            {
                tmp.put("ASSURE_NAME", "");
            }
            else
            {
                tmp.put("ASSURE_NAME", tmp.getString("CUST_NAME"));
            }

            // 大客户信息
            tmp.put("CLASS_ID2", tmp.getString("CLASS_ID"));
            tmp.put("CLIENT_INFO5", tmp.getString("CLASS_NAME"));
            tmp.put("CLIENT_INFO1", tmp.getString("VIP_MANAGER_NAME"));
            tmp.put("CLIENT_INFO2", tmp.getString("LINK_PHONE"));

            // 去客户昵称信息
            IDataset custTitleData = UserOtherInfoQry.getUserOther(userId, "CTHN");
            String custTitle = "";
            if (IDataUtil.isNotEmpty(custTitleData))
            {
                custTitle = custTitleData.getData(0).getString("RSRV_VALUE", "");
            }
            // 客户昵称
            tmp.put("RSRV_VALUE", custTitle);

        }
        return results;
    }

    /**
     * 根据USER_ID获取3户资料
     *
     * @param USER_ID
     * @return IData
     * @author huangsl
     */
    private IData getUserCustAcctInfo(String userId) throws Exception
    {
        // 根据UserId查UCA
        UcaData uca = UcaDataFactory.getUcaByUserId(userId);
        IData result = new DataMap();

        // 转换VIP信息

        if (uca == null || uca.getVip() == null)
        {
            // 没有大客户信息
            result.put("VIP_TAG", "N");
            result.put("CLASS_NAME", "");
        }
        else
        {
            result.put("VIP_TAG", "Y");
            String className = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(uca.getVip().getVipTypeCode(), uca.getVip().getVipClassId());
            result.put("CLASS_NAME", className);
        }

        // 转换SIM卡信息
        List<ResTradeData> resList = uca.getUserAllRes();
        Iterator it = resList.iterator();
        while (it.hasNext())
        {
            ResTradeData resData = (ResTradeData) it.next();
            if ("1".equals(resData.getResTypeCode()))
            {
                IDataset simDatas = ResCall.getSimCardInfo("1", null, resData.getImsi(), null);
                if (IDataUtil.isNotEmpty(simDatas))
                {
                    result.put("SIM_CARD_NO", simDatas.getData(0).getString("SIM_CARD_NO"));
                    result.put("PUK", simDatas.getData(0).getString("PUK"));
                }
            }
        }
        // 取需要的信息
        // 用户
        result.putAll(uca.getUser().toData());
        result.put("BRAND_CODE", uca.getBrandCode());
        result.put("PRODUCT_ID", uca.getProductId());
        result.put("EPARCHY_CODE", uca.getUserEparchyCode());
        // result.put("SCORE_VALUE", uca.getUserScore());
        // result.put("CREDIT_CLASS", uca.getUserCreditClass());
        // result.put("CREDIT_VALUE", uca.getUserCreditValue());

        // 客户
        CustomerTradeData custData = uca.getCustomer();
        result.put("CUST_NAME", custData.getCustName());
        result.put("CUST_TYPE", custData.getCustType());
        result.put("CUST_STATE", custData.getCustState());
        result.put("OPEN_LIMIT", custData.getOpenLimit());
        result.put("CUST_PASSWD", custData.getCustPasswd());
        result.put("PSPT_TYPE_CODE", custData.getPsptTypeCode());
        result.put("PSPT_ID", custData.getPsptId());
        // 个人客户、集团客户
        // 取客户资料
        if (uca.getCustPerson() == null && uca.getCustGroup() == null)
        {
            // 客户资料不存在;客户详细信息不存在
            return null;
        }

        if ("0".equals(custData.getCustType()))// 个人客户
        {
            if (uca.getCustPerson() != null)
            {
                result.putAll(uca.getCustPerson().toData());
            }
        }
        else
        // 集团客户
        {
            if (uca.getCustGroup() != null)
            {
                result.putAll(uca.getCustGroup().toData());
            }
        }

        // 账户
        if (uca.getAccount() != null)
        {
            result.putAll(uca.getAccount().toData());
        }

        // VIP信息
        VipTradeData vips = uca.getVip();
        if (vips != null)
        {
            result.put("VIP_NO", vips.getVipCardNo());
            result.put("CLASS_ID", vips.getVipClassId());
            result.put("VIP_TYPE_CODE", vips.getVipTypeCode());
            result.put("CUST_MANAGER_ID", vips.getCustManagerId());// 客户经理编码
            result.put("VPMN_GROUP_ID", vips.getGroupId());// 集团标志
        }

        // 用户主体服务
        List<SvcStateTradeData> listSvc = uca.getUserSvcsState();
        if (listSvc == null || listSvc.size() < 1)
        {
            // 获取主体服务失败
            result.put("X_SVCSTATE_EXPLAIN", "获取用户主体服务失败");
        }
        else
        {
            int listSvcSize = listSvc.size();
            for (int i = 0; i < listSvcSize; i++)
            {
                if ("1".equals(listSvc.get(i).getMainTag()))
                {
                    // 获取服务状态
                    IDataset svc = USvcStateInfoQry.qryStateNameBySvcIdStateCode(listSvc.get(i).getServiceId(), listSvc.get(i).getStateCode());
                    String svcStateExplan = "";
                    for (int j = 0; j < svc.size(); j++)
                    {
                        svcStateExplan += ((IData) (svc.get(j))).getString("STATE_NAME");
                        if (j != (svc.size() - 1))
                            svcStateExplan += "、";
                    }
                    result.put("X_SVCSTATE_EXPLAIN", svcStateExplan);
                }
            }
        }
        return result;
    }


    public IDataset querySaleActiveInfo(IData data) throws Exception {

        // 校验入参
        IData userInfo = CheckParam.isCheckIdentAuth(data);

        String serialNumber = data.getString("SERIAL_NUMBER");
        //获取用户USER_ID
        data.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        data.put("REMOVE_TAG", "0");
        data.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE","00"));

        String startDate  = data.getString("START_DATE","2000-01-01" + SysDateMgr.START_DATE_FOREVER);
        String endDate    = data.getString("END_DATE", SysDateMgr.END_DATE_FOREVER);
        String processTag = data.getString("PROCESS_TAG", "9");

        data.put("USER_ID", userInfo.getString("USER_ID"));
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);
        data.put("PROCESS_TAG", processTag);
        data.put("PRODUCT_ID", data.getString("PRODUCT_ID","0"));

        IDataset saleActive = UserSaleActiveInfoQry.getSaleActiveInfoOnInterface(data);

        if (saleActive.size() == 0) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "营销活动查询记录为空");
        }

        return saleActive;
    }
    /**
     * 最低消费查询
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryMiniExpenditure(IData inparams) throws Exception {

        IDataset infos = querySaleActiveInfo(inparams);

        for(int i=0;i<infos.size();i++) {
            IData theSaleActive = infos.getData(i);

            theSaleActive.put("ACITVITY_NO",theSaleActive.getString("PACKAGE_ID",""));//海南现场数据情况做的修改
            theSaleActive.put("ACITVITY_NAME",theSaleActive.getString("PACKAGE_NAME",""));//海南现场数据情况做的修改
            theSaleActive.put("START_DATE", SysDateMgr.decodeTimestamp(theSaleActive.getString("START_DATE", ""), SysDateMgr.PATTERN_TIME_YYYYMMDD));
            theSaleActive.put("END_DATE", SysDateMgr.decodeTimestamp(theSaleActive.getString("END_DATE", ""), SysDateMgr.PATTERN_TIME_YYYYMMDD));

            //最低下发金额
            //QUERY_TAG 0-最低消费 1-专项月租
            String lowestSum = CheckParam.queryQamLMRQNow(theSaleActive, "0");
            theSaleActive.put("LOWEST_SUM",lowestSum);

            //实际消费金额
            String realSum = CheckParam.queryQAMBBOSSCurrentBillNew(theSaleActive.getString("SERIAL_NUMBER"));

            /**************************合版本  duhj 2017/5/3  start*********************************/
            if (realSum != null && !realSum.trim().equals("")) {
                //保留小数点后2位
                if (realSum.indexOf(".") != -1) {
                    String s = realSum.substring(realSum.lastIndexOf(".") + 1);
                    if (s.length() < 2) {
                        if (s.length() == 0) {
                            realSum += "00";
                        } else if (s.length() == 1) {
                            realSum += "0";
                        }
                    }
                } else {
                    realSum += ".00";
                }
            }
            /**************************合版本  duhj 2017/5/3  end*********************************/

            theSaleActive.put("REAL_SUM", realSum);
            if(Double.parseDouble(realSum)>=Double.parseDouble(lowestSum)){
                theSaleActive.put("ACHIEVED", "是");
            }else{
                theSaleActive.put("ACHIEVED", "否");
            }        

        }

        return  infos;
    }

    /**
     * 专项月租查询
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset querySpecialMonthRent(IData params) throws Exception {
        IDataset infos = querySaleActiveInfo(params);

        for(int i=0;i<infos.size();i++) {
            IData theSaleActive = infos.getData(i);

            theSaleActive.put("ACITVITY_NO",theSaleActive.getString("PACKAGE_ID",""));//海南现场数据情况做的修改
            theSaleActive.put("ACITVITY_NAME",theSaleActive.getString("PACKAGE_NAME",""));//海南现场数据情况做的修改
            theSaleActive.put("OPERATION_NAME",theSaleActive.getString("PRODUCT_NAME",""));
            theSaleActive.put("EFFECT_DATE", SysDateMgr.decodeTimestamp(theSaleActive.getString("START_DATE", ""), SysDateMgr.PATTERN_TIME_YYYYMMDD));
            theSaleActive.put("ABATE_DATE", SysDateMgr.decodeTimestamp(theSaleActive.getString("END_DATE", ""), SysDateMgr.PATTERN_TIME_YYYYMMDD));
               //最低下发金额
            //QUERY_TAG 0-最低消费 1-专项月租
            String paySum = CheckParam.queryQamLMRQNow(theSaleActive, "1");
            theSaleActive.put("PAY_SUM", paySum);

        }

        return  infos;

    }

public static void main(String[] args) throws Exception {
	  java.util.List<Element> queryResultList = null;
	  java.util.List<Element> queryResultList1 = null;
	  java.lang.Object res ="<?xml version="+"'1.0'"+" encoding="+"'utf-8'"+"?> "+
"<outputData>"+
	    "<orderId>231</orderId>"+
	    "<operationResult>0</operationResult>"+
	    "<cmdList>`SC`00681.00internalPPS     000228ceDLGCON    00000015TXBEG     QUERY CHRG CARDSTAT:SEQUENCE=13147180143114925  8CF2A8A9&#xA;SC`00681.00internalPPS     000228ceDLGCON    00000015TXBEG     QUERY CHRG CARDSTAT:SEQUENCE=13147180143114925  8CF2A8A9&#xA;</cmdList>"+
	    " <queryResultList>" +
        "<strName>" +
            "CRDDAY"  +
       " </strName> " +
       " <strValue> " +
         "2018-12-31"  +
       " </strValue> " +
 " </queryResultList>"+
	    "</outputData>";
	 Document dom=DocumentHelper.parseText((java.lang.String)res);
	  Element root=dom.getRootElement();
	  String operationResult=root.element("operationResult").getText();
	  String  cmdList=root.element("cmdList").getText();
	  queryResultList = root.elements("queryResultList");
	  queryResultList1 = root.elements("cmdList");
	  String strName= null;
		String strValue= null;
		 IData queryResult = new DataMap();
		/*for(int i =0; i<queryResultList1.size();i++){
		   strName = ((Element) queryResultList1.get(i)).element("SEQUENCE").getText();
		   strValue = ((Element) queryResultList1.get(i)).element("CARDSTAT").getText();
		   queryResult.put(strName, strValue);

		}*/
		
		  
	        
	        if(queryResultList != null){
	    		for(int i =0; i<queryResultList.size();i++){
	    		   strName = ((Element) queryResultList.get(i)).element("strName").getText();
	    		   strValue = ((Element) queryResultList.get(i)).element("strValue").getText();
	    		   queryResult.put(strName, strValue);
	    		   String a = queryResult.getString(" CRDDAY ","");
	    		   String a1 = queryResult.getString("CRDDAY","");

	    		}
	    	}
	        IDataset dataset = new DatasetList();
	        dataset.add(queryResult);
	        IDataset result = new DatasetList();

	        for(int i = 0 ; i < dataset.size() ; i++){

	            IData resultdata = new DataMap();
	            IData data = dataset.getData(i);
	            String inchargenum = data.getString("ACCOUNTPIN");            
	            resultdata.put("USER_ID", inchargenum);

	            String userCity = "海南海口";
	            resultdata.put("NO_PROV", userCity);
String a = queryResult.getString("CRDDAY","");
	            resultdata.put("PAYMENT_CHANNEL", dataset.getData(i).getString("PAYMENT_CHANNEL"));
	            resultdata.put("CARD_NUMBER", dataset.getData(i).getString("SEQUENCE"));
	            resultdata.put("VALID_TIME", dataset.getData(i).getString("CRDDAY"));


	            result.add(resultdata);
	        }

}
}
