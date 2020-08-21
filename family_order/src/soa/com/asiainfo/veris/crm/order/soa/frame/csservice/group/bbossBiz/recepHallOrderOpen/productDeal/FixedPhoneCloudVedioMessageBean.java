package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.recepHallOrderOpen.productDeal;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;

public class FixedPhoneCloudVedioMessageBean extends CSBizBean {
    private static final Logger log = Logger.getLogger(FixedPhoneCloudVedioMessageBean.class);

    public IDataset fixedPhoneCloudVedioCreateUser(IData map) throws Exception {
        //最终固化云视讯开户结果
        IDataset createdResult = new DatasetList();
        createdResult = createFixedUser(map);
        return createdResult;
    }

    private IDataset createFixedUser(IData map) throws Exception {
        IData params = new DataMap();//调开户接口的入参
        String serialNumber = "";
        serialNumber = map.getString("serialNumber","");
        //拿到EC之后能查到那些东西
        String mpCustCode = map.getString("CUSTOMER_NUMBER");
        IData qryCustResult = queryCustGroupInfoByMpCustCode(mpCustCode);
        String postAddress = qryCustResult.getString("groupAddr","");//不知道传不传，反正集客大厅没数据过来,先按证件地址传
        String userEpachyCode = qryCustResult.getString("userEpachyCode","");

        //String productId = getProductId();
        //先写成801111，后面根据产商品配置修改代码
        String productId = "801111";

        String contactPhone = map.getString("CONTACT_PHONE");//联系人电话

        String psptAddr = postAddress;//证件地址

        //拼参数开始
        params.put("POST_ADDRESS",postAddress);
        params.put("TELTYPE","1");
        params.put("USER_EPARCHY_CODE",userEpachyCode);
        params.put("PRODUCT_ID",productId);
        params.put("SERIAL_NUMBER",serialNumber);
        params.put("FILE_ID","");//授权书,不知道在哪取
        params.put("FUNCTIONARY_CUST_NAME",map.getString("CONTACTNAME"));//那就联系人吧

        /**PRODUCT_PARAM_INFO节点 start*/
        IData productParamInfoData = new DataMap();
        IData productParamData = new DataMap();
        productParamData.put("PRODUCT_ID",productId);
        //这一块前台是要传密码的，但是不知道密码从哪来，所以不传
        productParamInfoData.put("PRODUCT_PARAM_INFO",productParamData);
        params.put("PRODUCT_PARAM_INFO",productParamInfoData);
        /**PRODUCT_PARAM_INFO节点 end*/

        /**ELEMENT_INFO节点 start*/
        IData elementInfoData = new DataMap();
        IData elementData = new DataMap();
        elementData.put("START_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));//PATTERN_STAND
        elementData.put("ELEMENT_TYPE_CODE","S");
        elementData.put("MODIFY_TAG","0");
        elementData.put("PRODUCT_ID",productId);
        elementData.put("END_DATE",SysDateMgr.END_DATE_FOREVER);
        elementData.put("PACKAGE_ID","-1");
        elementData.put("ELEMENT_ID","8173");//说是之前传的8173,CreateCentrexSuperTeleGroupMember
        elementInfoData.put("ELEMENT_INFO",elementData);
        params.put("ELEMENT_INFO",elementInfoData);
        /**ELEMENT_INFO节点 end*/

        params.put("ROUTE_EPARCHY_CODE",userEpachyCode);
        params.put("OPERATOR_PSPT_ADDR",psptAddr);
        params.put("CONTACT_PHONE",contactPhone);
        params.put("PSPT_ID",qryCustResult.getString("PSPT_ID",""));//证件信息 AGREE_FILE_ID
        params.put("AGREE_FILE_ID","");//不知道在哪取
        params.put("FUNCTIONARY_PSPT_ID",qryCustResult.getString("PSPT_ID",""));//不知道在哪取
        params.put("OPERATOR_PSPT_ID",qryCustResult.getString("PSPT_ID",""));//不知道在哪取
        params.put("USER_TYPE_CODE","0");//不知道是啥，看IMS是0
        params.put("PAY_MODE_CODE","0");//OPEN_LIMIT
        params.put("OPEN_LIMIT",qryCustResult.getString("OPEN_LIMIT",""));
        params.put("PSPT_ADDR",postAddress);//OPERATOR_CUST_NAME
        params.put("OPERATOR_CUST_NAME",map.getString("CONTACTNAME"));//
        params.put("FUNCTIONARY_PSPT_TYPE_CODE","1");//责任人证件类型,IMS是1 FUNCTIONARY_PSPT_ADDR
        params.put("FUNCTIONARY_PSPT_ADDR",postAddress);
        params.put("PSPT_END_DATE",SysDateMgr.END_DATE_FOREVER);
        params.put("CUST_NAME",qryCustResult.getString("custName",""));
        params.put("PAY_NAME","");//不知道从哪取
        params.put("CITY_NAME","");//不知道从哪取

        /**RES_INFO节点start*/
        IData resInfoData = new DataMap();
        IData resData = new DataMap();
        resInfoData.put("resInfo",resData);
        resData.put("modifyTag","0");//开户都是新增
        resData.put("resTypeCode","0");//看IMS语音是0
        resData.put("resCode",serialNumber);
        resData.put("checked","true");//不知道这里是布尔还是字符串
        resData.put("disabled","true");//不知道这里是布尔还是字符串
        params.put("RES_INFO",resInfoData);
        /**RES_INFO节点end*/
        //拼参数结束
        log.debug("调开户接口前的入参====>" + params);
        IDataset resultOpenGroupMember = CSAppCall.call("CS.OpenGroupMemberSVC.crtTrade",params);
        log.debug("调用开户后的结果result====>" + resultOpenGroupMember);
        return resultOpenGroupMember;
    }

    //获取productId
    /*public static String getProductId() throws Exception {
        String productId = "";//不知道配的什么,说是调个接口可以拿到

        String offerCode = "";
        IDataset localMappingDataset = UpcCall.queryLocalMapping("5001702","1");//5001702是固化云视讯集团编码,type=1是产品
        if(localMappingDataset.size() > 0 && localMappingDataset != null){
            offerCode = localMappingDataset.first().getString("OFFER_CODE","");
            productId = offerCode;
        }
        return productId;
    }*/

    //根据MpCustCode获取集团相关信息
    private IData queryCustGroupInfoByMpCustCode(String mpCustCode) throws Exception {
        //接口查询结果
        IData result = new DataMap();

        IDataset groupInf = GrpInfoQry.queryCustGroupInfoByMpCustCode(mpCustCode, null);

        if (IDataUtil.isEmpty(groupInf))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_899, mpCustCode);
        }

        IData custIdParams = new DataMap();
        if(groupInf != null && groupInf.size() > 0){
            //客户地州编码
            result.put("userEpachyCode",groupInf.getData(0).getString("EPARCHY_CODE",""));
            //集团地址
            result.put("groupAddr",groupInf.getData(0).getString("GROUP_ADDR",""));
            //集团联系电话
            result.put("groupContactPhone",groupInf.getData(0).getString("GROUP_CONTACT_PHONE",""));
            custIdParams.put("CUST_ID",groupInf.getData(0).getString("CUST_ID"));
        }

        IDataset custInfoDataset = CustomerInfoQry.getCustInfoByCustIdOnly(custIdParams);

        if(custInfoDataset != null && custInfoDataset.size() > 0){
            IData custData = custInfoDataset.first();
            //客户类型
            result.put("custType",custData.getString("CUST_TYPE",""));
            //客户分类
            result.put("custKind",custData.getString("CUST_KIND",""));
            //证件类别
            result.put("psptTypeCode",custData.getString("PSPT_TYPE_CODE",""));
            //证件号
            result.put("psptId",custData.getString("PSPT_ID",""));
            //开户限制数
            result.put("openLimit",custData.getString("OPEN_LIMIT",""));
            //客户密码
            result.put("custPasswd",custData.getString("CUST_PASSWD",""));
            //客户姓名
            result.put("custName",custData.getString("CUST_NAME",""));
        }

        log.debug("接口查询结果result===>" + result);

        return result;
    }

    /**
     *固化云视讯号码注销
     * */
    public IDataset fixedPhoneCloudVedioCancleUser(IData map) throws Exception {
        //销户的结果
        IData params = new DataMap();
        params.put("SERIAL_NUMBER",map.getString("serialNumber",""));
        params.put(Route.USER_EPARCHY_CODE,map.getString("epachyCode",""));
        params.put(Route.ROUTE_EPARCHY_CODE,map.getString("epachyCode",""));
        params.put("REMOVE_REASON", "集客大厅发起销户");
        //TD_S_STATIC的TD_B_REMOVE_REASON_GROUP字段
        params.put("REMOVE_REASON_CODE", "10");
        params.put("REMARK", "");
        IDataset cancleResult = CSAppCall.call("CS.RemoveGroupMemberSVC.removeMember", params);
        log.debug("固化云视讯号码注销的处理结果" + cancleResult);
        return cancleResult;
    }
}