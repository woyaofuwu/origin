package com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.query;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;


/**
 * Created by Administrator on 2015/11/5.
 */
public class CheckParam
{

    /***
     * PUK 查询检查
     * @param param
     * @throws Exception
     */
    public static void checkUserPUKCode(IData param) throws Exception {
        if (param.containsKey("MSISDN")) {
            param.put("SERIAL_NUMBER", param.getString("MSISDN"));
        }

        chkParamNoStr(param, "OPR_NUMB", "2998");
        chkParamNoStr(param, "ID_TYPE", "2998");
        chkParamNoStr(param, "SERIAL_NUMBER", "2998");
        chkParamNoStr(param, "CHANNEL_ID", "2998");
        chkParamNoStr(param, "IDENT_CODE", "2998");


    }

    /**
     * 备卡参数信息检查
     * @param param
     * @throws Exception
     */
    public static void checkQueryUserSimBak(IData param) throws Exception
    {
        chkParamNoStr(param, "OPR_NUMB", "2998");
        chkParamNoStr(param, "ID_TYPE", "2998");
        chkParamNoStr(param, "CHANNEL_ID", "2998");
        chkParamNoStr(param, "SERIAL_NUMBER", "2998");
        chkParamNoStr(param, "IDENT_CODE", "2998");

        if (param.containsKey("MSISDN")) {
            param.put("SERIAL_NUMBER", param.getString("MSISDN"));
        }
        param.put("MSISDN", param.getString("SERIAL_NUMBER"));
    }

    /**
     * 国际漫游业务日套餐状态查询参数检查
     * @param param
     * @throws Exception
     */
    public static void checkQueryUserInterRoamDay(IData param) throws Exception {
        chkParamNoStr(param, "OPR_NUMB", "2998");
        chkParamNoStr(param, "ID_TYPE", "2998");
        chkParamNoStr(param, "CHANNEL_ID", "2998");
        chkParamNoStr(param, "SERIAL_NUMBER", "2998");
        chkParamNoStr(param, "IDENT_CODE", "2998");


        if (param.containsKey("MSISDN")) {
            param.put("SERIAL_NUMBER", param.getString("MSISDN"));
        }
    }

    /**
     * 本地营销案查询参数检查
     * @param param
     * @throws Exception
     */
    public static void checkQueryUserSaleActive(IData param) throws Exception {
        chkParamNoStr(param, "OPR_NUMB", "2998");
        chkParamNoStr(param, "ID_TYPE", "2998");
        chkParamNoStr(param, "CHANNEL_ID", "2998");
        chkParamNoStr(param, "SERIAL_NUMBER", "2998");
        chkParamNoStr(param, "IDENT_CODE", "2998");

    }

    /**
     * 检查GPRS状态查询参数是否正确
     * @param args
     * @throws Exception
     */
    public static void checkQueryUserGPRS(IData param) throws Exception {
        chkParamNoStr(param, "OPR_NUMB", "2998");
        chkParamNoStr(param, "ID_TYPE", "2998");
        chkParamNoStr(param, "CHANNEL_ID", "2998");
        chkParamNoStr(param, "SERIAL_NUMBER", "2998");
        chkParamNoStr(param, "IDENT_CODE", "2998");

    }

    /**
     * 检查充值卡记录查询参数校验
     * @param param
     * @throws Exception
     */
    public static void checkQueryValueCardUse(IData param) throws Exception {
        chkParamNoStr(param, "OPR_NUMB", "2998");
        chkParamNoStr(param, "ID_TYPE", "2998");
        chkParamNoStr(param, "CHANNEL_ID", "2998");
        chkParamNoStr(param, "SERIAL_NUMBER", "2998");
        chkParamNoStr(param, "IDENT_CODE", "2998");
        chkParamNoStr(param, "QRY_STR_TIME", "2998");
        chkParamNoStr(param, "QRY_END_TIME", "2998");

    }

    /**
     * 检查营销活动查询参数
     * @param param
     * @throws Exception
     */
    public static void checkGetSaleActiveInfo(IData param) throws Exception {
        chkParamNoStr(param, "OPR_NUMB", "2998");
        chkParamNoStr(param, "ID_TYPE", "2998");
        chkParamNoStr(param, "CHANNEL_ID", "2998");
        chkParamNoStr(param, "SERIAL_NUMBER", "2998");
        chkParamNoStr(param, "IDENT_CODE", "2998");

        if (param.containsKey("MSISDN")) {
            param.put("SERIAL_NUMBER", param.getString("MSISDN"));
        }
    }

    /**
     * 检查充值卡记录查询参数校验
     * @param param
     * @throws Exception
     */
    public static void checkGetUserInfo(IData param) throws Exception {
        chkParamNoStr(param, "OPR_NUMB", "2998");
        chkParamNoStr(param, "ID_TYPE", "2998");
        chkParamNoStr(param, "CHANNEL_ID", "2998");
        chkParamNoStr(param, "SERIAL_NUMBER", "2998");
        chkParamNoStr(param, "IDENT_CODE", "2998");

        if (param.containsKey("MSISDN")) {
            param.put("SERIAL_NUMBER", param.getString("MSISDN"));
        }
    }

    /**
     * 检查已订购业务查询
     * @param param
     * @throws Exception
     */
    public static void checkGetOrderSvc(IData param) throws Exception {

        String idType = param.getString("IDTYPE");
        String idValue = param.getString("IDVALUE");
        if (StringUtils.isBlank(idType) || !"01".equals(idType))
        {
            // 标识类型错误
            CSAppException.appError("", "输入参数" + "idType" + "值不能为空！");
        }
        if (StringUtils.isBlank(idValue))
        {
            // 标识号码错误
            CSAppException.appError("", "输入参数" + "idValue" + "值不能为空！");
        }

        chkParamNoStr(param, "OPR_NUMB", "2998");
        chkParamNoStr(param, "BIZ_TYPE_CODE", "2998");
        chkParamNoStr(param, "IDENT_CODE", "2998");
        chkParamNoStr(param, "TRADE_STAFF_ID", "2998");

        if (param.containsKey("IDVALUE")) {
            param.put("SERIAL_NUMBER", param.getString("IDVALUE"));
        }
    }




    /**
     * 校验传入参数是否为空
     *
     * @param data
     * @param keys
     * @throws Exception
     */
    public static void chkParamNoStr(IData data, String keys, String errorCode) throws Exception
    {
        String key = data.getString(keys, "");
        if ("".equals(key))
        {
            CSAppException.appError(errorCode, "输入参数" + keys + "值不能为空！");
        }
    }


    public static IData isCheckIdentAuth(IData param) throws  Exception {
        // 用户凭证校验
        IData _data  = CustServiceHelper.checkCertificate(param);
        IData userInfo = _data.getData("USER_INFO");
        return userInfo;
    }




/*
    public static void main(String args[]) throws  Exception {
    *//*    IData data = new DataMap();
        data.put("MSISDNS", "1234");
        checkUserPUKCode(data);*//*

        IDataset saleActive = new DatasetList();

        IData data1 = new DataMap();
        data1.put("a", 123);

        IData data2 = new DataMap();
        data2.put("a", 456);

        saleActive.add(data1);
        saleActive.add(data2);

        for (int i = 0; i < saleActive.size(); i++) {
            IData _data = saleActive.getData(i);

            _data.put("b",i + 100);
        }

    }*/

    public static String queryQamLMRQNow(IData param,String flag) throws Exception {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");
//        IDataset discntList = getElementByPackId(packageId);
        IDataset discntList = new DatasetList();
        IDataset offerList = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, null, null);
        if(IDataUtil.isNotEmpty(offerList)){
        	int size = offerList.size();
        	for(int i = 0 ;i < size ; i++){
        		if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerList.getData(i).getString("OFFER_TYPE",""))){
        			discntList.add(offerList.getData(i));
        		}
        	}
        }
        if(discntList.isEmpty() || discntList.size() < 1){
            return "0.00";
        }

        IData _param = new DataMap();
        _param.put("PACKAGE_ID",packageId);
        _param.put("SERIAL_NUMBER",serialNumber);
        _param.put("PRODUCT_ID",productId);
        _param.put("DISCNT_CODE", fromDatasetToString(discntList, "OFFER_CODE"));
        _param.put("IDTYPE","01");
        //QUERY_TAG 0-最低消费 1-专项月租
        _param.put("QUERY_TAG", flag);
        IDataOutput output = CSAppCall.callAcct("QAM_CRM_LMRQ", _param, true);
        IData data =  output.getData().getData(0);

        if(!data.getString("RSPCODE").equals("0000")){  //调用失败抛异常
            CSAppException.apperr(CrmCommException.CRM_COMM_103, data.getString("RSPDESC"));
        }

        return data.getString("FEE")==null?"0.00":data.getString("FEE");//规范约定必须是小数点后2位

    }
    
    
    public static String queryQamLMRQ(IData param,String flag) throws Exception {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");
        IDataset discntList = getElementByPackId(packageId);
        if(discntList.isEmpty()){
            return "0.00";
        }

        IData _param = new DataMap();
        _param.put("PACKAGE_ID",packageId);
        _param.put("SERIAL_NUMBER",serialNumber);
        _param.put("PRODUCT_ID",productId);
        _param.put("DISCNT_CODE", fromDatasetToString(discntList, "ELEMENT_ID"));
        _param.put("IDTYPE","01");
        //QUERY_TAG 0-最低消费 1-专项月租
        _param.put("QUERY_TAG", flag);
        IDataOutput output = CSAppCall.callAcct("QAM_CRM_LMRQ", _param, true);
        IData data =  output.getData().getData(0);

        if(!data.getString("RSPCODE").equals("0000")){  //调用失败抛异常
            CSAppException.apperr(CrmCommException.CRM_COMM_103, data.getString("RSPDESC"));
        }

        return data.getString("FEE")==null?"0.00":data.getString("FEE");//规范约定必须是小数点后2位

    }


    /**
     * 实际消费金额
     * @param data
     * @return
     */
    public static String queryQAMBBOSSCurrentBillNew(String serialNumber) throws Exception {
        IData _param = new DataMap();
        _param.put("SERIAL_NUMBER",serialNumber);
        _param.put("IDTYPE","01");

        IDataOutput output = CSAppCall.callAcct("AM_BBOSS_CurrentBillNew", _param, true);
        IData data =  output.getData().getData(0);

        if(!data.getString("RSPCODE").equals("0000")){  //调用失败抛异常
            CSAppException.apperr(CrmCommException.CRM_COMM_103, data.getString("RSPDESC"));
        }

        return data.getString("TOTAL_BILL")==null?"0":data.getString("TOTAL_BILL");

    }


    public static IDataset getElementByPackId(String packageId) throws Exception {
        IData param = new DataMap();
        param.put("PACKAGE_ID", packageId);
        param.put("ELEMENT_TYPE_CODE", "D");
        return Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_BY_PACKID", param);
    }

    /**
     * 传入dataset，列名返回这个列所有行拼成的String
     *
     * @create_date Aug 10, 2009
     * @author heyq
     */
    public static String fromDatasetToString(IDataset dataset, String columnName) throws Exception
    {
        StringBuilder buffer = new StringBuilder();
        for (Iterator iter = dataset.iterator(); iter.hasNext();)
        {
            IData element = (IData) iter.next();
            buffer.append(element.getString(columnName));
            buffer.append("|");
        }
        return buffer.length() > 0 ? buffer.substring(0, buffer.length() - 1).toString() : "";
    }

}
