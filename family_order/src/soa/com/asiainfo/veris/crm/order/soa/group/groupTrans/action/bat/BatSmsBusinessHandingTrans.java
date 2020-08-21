
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.callout.IUpcCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustContractInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * 集团短彩信业务子端口
 */
public class BatSmsBusinessHandingTrans implements ITrans {
    private static Logger logger = Logger.getLogger(BatNewXxtUserChangeTrans.class);

    @Override
    public void transRequestData(IData batData) throws Exception {
        // 初始化数据
        System.out.println("BatSmsBusinessHandingTrans-batData" + batData);
        // 校验请求参数
        createRequestDataSub(batData);
        // 根据条件判断调用服务
        //setSVC(batData);
    }

    /**
     * 制造参数
     *
     * @param batData
     * @throws Exception
     */
    protected void createRequestDataSub(IData batData) throws Exception {
        IData condData = batData.getData("condData", new DataMap());
        IData newQryData = batData.getData("svcData", new DataMap());//构造请求
        String mainSn = batData.getString("CONDITION5");
        String ecuserid = IDataUtil.getMandaData(condData, "USER_ID");
    //    String ecustid = IDataUtil.getMandaData(condData, "CUST_ID");

        String groupId = IDataUtil.chkParam(condData, "GROUP_ID");
        String productId = IDataUtil.chkParam(condData, "PRODUCT_ID");
        String serialNumber = IDataUtil.chkParam(condData, "COND_SERIAL_NUMBER");
        String opercode = IDataUtil.chkParam(condData, "OPER_CODE");;// 01-新增 02-修改 03-删除



        IData users = UcaInfoQry.qryUserInfoBySn(serialNumber);

        String serviceUserId=users.getString("USER_ID", "");//办理业务的手机号
//        IData mebUserInfo = UcaInfoQry.qryUserInfoBySn(mainSn);
        String externSn = batData.getString("DATA1");//扩展码
        String textEn = batData.getString("DATA2");//英文签名
        String textZh = batData.getString("DATA3");//中文签名
        String strWhiteProperty = batData.getString("DATA4");//黑白名单属性
        String offerCode = batData.getString("DATA5");//从文件传入 服务编码
        //String offerSubCode = batData.getString("DATA5");//从文件传入 //套餐编码
        String discnt1 = batData.getString("DATA5");
        String discnt2 = batData.getString("DATA6");
        String discnt3 = batData.getString("DATA7");
        String discnt4 = batData.getString("DATA8");


        if ((textZh.length()<2||textZh.length()>18)&&textZh.getBytes().length!=textZh.length()){
            CSAppException.apperr(GrpException.CRM_GRP_922);//中文签名，最多输入2-8个汉字之间
        }
        if ((textZh.length()<4||textZh.length()>36)&&textZh.getBytes().length==textZh.length()){
            CSAppException.apperr(GrpException.CRM_GRP_927);//中文签名长度只能为6～16个字符!
        }

        if (strWhiteProperty.length()<0||(!strWhiteProperty.equals("1")&&!strWhiteProperty.equals("2")&&!strWhiteProperty.equals("3")&&!strWhiteProperty.equals("4"))){
            CSAppException.apperr(GrpException.CRM_GRP_923);//输入黑白名单属性有误，请按数字形式入，1=白名单、2=黑名单、3=限制次数白名单、4=点播业务数据
        }

        if (externSn.length()<0||externSn.getBytes().length!=externSn.length()||Integer.parseInt(externSn)<0){
            CSAppException.apperr(GrpException.CRM_GRP_924);//扩展码输入有误

        }
        if (textEn.length()<4||textEn.getBytes().length!=textEn.length()){
            CSAppException.apperr(GrpException.CRM_GRP_925);//英文签名输入有误

        }
        if (offerCode.length()<3&&offerCode.getBytes().length!=offerCode.length()){
            CSAppException.apperr(GrpException.CRM_GRP_926);//服务编码输入有误

        }
     //   String[] offerSubCodes = offerSubCode.split("\\|");

//        IData offerInfo = IUpcViewCall.queryOfferByOfferId(offerId,"Y");
//        IDataset quryInputData = new DatasetList();
//
//        DataMap newQryData = new DataMap();
//        quryInputData.add(newQryData);
        IData customer = UcaInfoQry.qryGrpInfoByGrpId(groupId);//获取集团客户信息
        if (DataUtils.isNotEmpty(customer)) {
        } else {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据GROUP_ID" + groupId + "没有查询到集团信息！");
        }
        IData offerInfo = IUpcCall.queryOfferByOfferCodeNew(productId);//主产品
        System.out.println("queryOfferByOfferCodeNew" + offerInfo);

        String offerId = offerInfo.getString("OFFER_ID");
        if (DataUtils.isEmpty(offerInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据OFFER_ID" + offerId + "没有查询到商品信息！");
        }
        newQryData.put("X_TRADE_FEESUB", new DatasetList());
        newQryData.put("X_TRADE_PAYMONEY", new DatasetList());
        newQryData.put("ASKPRINT_INFO", new DatasetList());
        newQryData.put("POST_INFO", "");
        newQryData.put("REMARK", "");
        offerInfo.put("OPER_TYPE", BizCtrlType.CreateUser);
        newQryData.put("USER_ID", serviceUserId);//用户id
        newQryData.put("BATCH_OPER_TYPE", "SMSBUSINESSHANDING");//用户id


        //newQryData.put("AUDIT_STAFF_ID", new DataMap().put("AUDIT_STAFF_ID", "HNSJ1234"));//稽核人员
        DatasetList offerDatalist = new DatasetList();
        newQryData.put("ELEMENT_INFO", offerDatalist);

        String staffId = "";
        String staffName = "";
        IDataset staffs = qryAuditInfo(staffId, staffName);
        //过滤掉自己
        for (int i = 0; i < staffs.size(); i++) {//稽核人员
            IData each = staffs.getData(i);
            if(!CSBizBean.getVisit().getStaffId().equals(each.getString("STAFF_ID", ""))){
                newQryData.put("AUDIT_STAFF_ID", each.getString("STAFF_ID", ""));//稽核人员
                break;
//                staffs.remove(i);
//                i--;
            }
        }

        newQryData.put("PRODUCT_ID",productId);
        //合同信息开始
        String contractId = users.getString("CONTRACT_ID");
        DataMap contractData = new DataMap();
        newQryData.put("USER_INFO", contractData);

        if (StringUtils.isNotEmpty(contractId))
        {
            IDataset productInfos = queryProductInfo(customer.getString("CUST_ID"), contractId);
            if(productInfos != null && productInfos.size() > 0){
                StringBuffer offerIds = new StringBuffer();
                for(int j=0;j<productInfos.size();j++){
                    if(j == 0){
                        offerIds.append(productInfos.getData(j).getString("PRODUCT_ID"));
                    }else{
                        offerIds.append(","+productInfos.getData(j).getString("PRODUCT_ID"));
                    }
                }
                contractData.put("SERIAL_NUMBER",serialNumber);
                contractData.put("OFFER_IDS",offerIds.toString());
                contractData.put("USER_DIFF_CODE","");
                contractData.put("USER_ID",serviceUserId);
                contractData.put("CONTRACT_ID", contractId);
                IDataset contractInfo = CustContractInfoQry.qryContractInfoByContractId(contractId);
                if (DataUtils.isNotEmpty(contractInfo)) {
                    contractData.put("CONTRACT_WRITE_DATE", contractInfo.first().getString("CONTRACT_WRITE_DATE"));
                    contractData.put("CONTRACT_END_DATE", contractInfo.first().getString("CONTRACT_END_DATE"));
                } else {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CUST_ID" + customer.getString("CUST_ID") + "没有查询到合同信息！");
                }

            } else {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CUST_ID" + customer.getString("CUST_ID") + "没有查询到合同信息！");
            }


        }
        IDataset   mainServiceList = IUpcCall.queryOfferGroups(offerId,"");
        IDataset resultS = UpcCall.queryOfferInfoByOfferCodeAndOfferType(offerCode, BofConst.ELEMENT_TYPE_CODE_SVC);//这里报的错
        if(!IDataUtil.isNotEmpty(resultS)) {//查询不到信息报错
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据服务编码" + offerCode + "没有查询到信息！");

        }

        if(IDataUtil.isNotEmpty(resultS)){//多条也是从这里开始的
                    IData iData = (IData) resultS.get(0);
                   // IDataset   elementListddd = IUpcViewCall.queryOfferGroups(iData.getString("OFFER_ID"),"0898");
                    IData ofData = new DataMap();
                    ofData.put("START_DATE", SysDateMgr.getSysTime());
                    ofData.put("END_DATE", iData.getString("EXPIRE_DATE"));
                    ofData.put("ELEMENT_ID", iData.getString("OFFER_CODE"));



                    ofData.put("ELEMENT_TYPE_CODE", iData.getString("OFFER_TYPE"));
                    ofData.put("REPEAT_ORDER", "");
                    ofData.put("PRODUCT_ID", productId);
                    ofData.put("PACKAGE_ID", "0");
                    if(IDataUtil.isNotEmpty(mainServiceList)){
                        for (int m=0;m<mainServiceList.size();m++){
                            IData mainServiceData=mainServiceList.getData(m);
                            if(IDataUtil.isNotEmpty(mainServiceData)){
                                if ("S".equals(mainServiceData.getString("LIMIT_TYPE"))&&"0".equals(mainServiceData.getString("SELECT_FLAG"))&&offerId.equals(mainServiceData.getString("OFFER_ID"))){
                                    ofData.put("PACKAGE_ID", mainServiceData.getString("GROUP_ID"));
                                    break;
                                }
                            }

                        }

                    }
            if (opercode.equals("02")){//修改
                ofData.put("MODIFY_TAG", "2");
            }else  if (opercode.equals("03")){//删除为1
                ofData.put("MODIFY_TAG", "1");
                ofData.put("END_DATE", SysDateMgr.getSysTime());
            }else{//01 为新增
                ofData.put("MODIFY_TAG", "0");
            }

            offerDatalist.add(ofData);//主服务

            if (opercode.equals("02")||opercode.equals("01")){
                //属性值开始
                IDataset userElements = new DatasetList();
                IData modElement = new DataMap();
                ((DataMap) modElement).put("PARAM_VERIFY_SUCC","true");
                userElements.add(modElement);
                ((DataMap) ofData).put("ATTR_PARAM", userElements);
                //查询相关信息
                //ADC, MAS需要校验集团是否暂停，暂停不让做导入导出操作
                IData param = new DataMap();
                param.put("USER_ID", serviceUserId);
                IDataset userGrpPlatSvcList = CSAppCall.call( "CS.UserGrpPlatSvcInfoQrySVC.getLxtGrpPlatSvcByUserId", param);
                System.out.println("CS.UserGrpPlatSvcInfoQrySVC.getLxtGrpPlatSvcByUserId--------------" + userGrpPlatSvcList);


                IDataset userSvcList =  UserSvcInfoQry.queryUserAllSvcForAbility(serviceUserId);
                if (IDataUtil.isEmpty(userGrpPlatSvcList))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_25);//没有找到集团平台服务信息[GRP_PLATSVC],请联系管理员
                }

                if (IDataUtil.isNotEmpty(userSvcList))
                {
                    for (int i=0;i<userSvcList.size();i++) {
                        IData userSvc = userSvcList.getData(i);
                        if (offerCode.equals(userSvc.getString("SERVICE_ID"))&&opercode.equals("01")){
                            CSAppException.apperr(GrpException.CRM_GRP_921);//该集团用户已存在服务编码。无需重复添加，请确认！
                        }

                    }
                }



                //服务编码互斥规则
//                if (opercode.equals("01")){//新增
//                    IDataset groupList = UpcCall.queryOfferGroups(productId);//获取组分类
//                    logger.debug("==============================groupList" + groupList);
//                    if(DataUtils.isNotEmpty(groupList))
//                    {
//                        for(int k = 0; k < groupList.size(); k++) {
//                            IData group = groupList.getData(k);
//                            if (UpcConst.SELECT_FLAG_MUST_CHOOSE.equals(group.getString("SELECT_FLAG"))) {
//                                continue;
//                            }
//                            IData groupData = new DataMap();
//                            groupData.put("GROUP_ID", group.getString("GROUP_ID"));
//                            groupData.put("GROUP_NAME", group.getString("GROUP_NAME"));
//
//                            IDataset groupOfferList = UpcCall.queryGroupComRelOfferByGroupId(group.getString("GROUP_ID"));//通在组ID获得所有服务编码
//                            logger.debug("==============================GROUP_ID：" + group.getString("GROUP_ID"));
//
//                            if ("41012824".equals(group.getString("GROUP_ID"))){
//                                logger.debug("==============================41012824groupOfferList" + groupOfferList);
//
//                            }
//
//                            boolean  isdiff=false;
//                            for(int j = 0; j < groupOfferList.size(); j++) {
//                                IData groupOffer = groupOfferList.getData(j);
//                                String groupOfferCode = groupOffer.getString("OFFER_CODE");
//                                for (int i=0;i<userGrpPlatSvcList.size();i++) {
//                                    IData grpPlatSvc = userGrpPlatSvcList.getData(i);
//                                    if (grpPlatSvc.getString("SERVICE_ID").equals(groupOfferCode)){
//                                        logger.debug("==============================groupOfferCodeSERVICE_ID" + grpPlatSvc.getString("SERVICE_ID")+"-------------"+groupOfferCode+"----"+j);
//                                        if (isdiff){//存在并且相同
//                                            CSAppException.apperr(GrpException.CRM_GRP_928);//您已选择[集团短彩信服务包7]组内商品，该组内服务类商品选择个数不能多于1个！
//                                        }
//                                        isdiff=true;
//                                        break;
//                                    }
//                                }
//
//                                if(groupOfferCode.equals(offerCode)){
//                                    logger.debug("==============================groupOfferCodeofferCode" + groupOfferCode+"-------------"+offerCode+"----"+j);
//                                    if (isdiff){
//                                        CSAppException.apperr(GrpException.CRM_GRP_928);//您已选择[集团短彩信服务包7]组内商品，该组内服务类商品选择个数不能多于1个！
//
//                                    }
//                                    isdiff=true;
//
//                                }
//
//
//                            }
//
//
//                        }}
//
//                }

                //服务编码互斥规则
                String[] str_code = new String[]{"9107","9127","9101","9105","9104","9103","9102","9106","9991"};
                for (int i=0;i<userGrpPlatSvcList.size();i++){
                    IData grpPlatSvc=userGrpPlatSvcList.getData(i);

                    String extendCode=grpPlatSvc.getString("EC_BASE_IN_CODE")+externSn;
                    if (opercode.equals("01")&&extendCode.equals(grpPlatSvc.getString("SERV_CODE"))){//判断SERV_CODE相同则不能办理
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据服务编码" + iData.getString("OFFER_CODE") + "已存在相同的础码+扩展码信息！");

                    }

                    if (opercode.equals("01")&&"1".equals(grpPlatSvc.getString("RSRV_STR1"))&&(Arrays.asList(str_code).contains(grpPlatSvc.getString("SERVICE_ID")))){//'9107','9127','9101','9105','9104','9103','9102','9106','9991'
//                        if (grpPlatSvc.getString("SERVICE_ID").equals(productId)){//取主服务的值进行操作
                            IData platsvcDataset = new DataMap();
                            IData platsvcData = new DataMap();
                            ((DataMap) platsvcDataset).put("PLATSVC",platsvcData);
                            ((DataMap) platsvcDataset).put("ID",iData.getString("OFFER_CODE"));
                            ((DataMap) platsvcDataset).put("CANCLE_FLAG","false");
                            //PLATSVC 构造开始

                            platsvcData.put("pam_MAX_ITEM_PRE_DAY", grpPlatSvc.getString("MAX_ITEM_PRE_DAY"));
                            platsvcData.put("pam_SERVICE_ID", iData.getString("OFFER_CODE"));
                            platsvcData.put("pam_BIZ_IN_CODE", grpPlatSvc.getString("EC_BASE_IN_CODE")+externSn);//基础码+扩展码
                            platsvcData.put("pam_RSRV_STR4", grpPlatSvc.getString("RSRV_STR4"));
                            platsvcData.put("pam_IS_TEXT_ECGN", grpPlatSvc.getString("IS_TEXT_ECGN"));//签名 是否支持签名
                            platsvcData.put("pam_HASMODIFYPRV", "true");
                            platsvcData.put("pam_SMS_TEMPALTE", grpPlatSvc.getString("RSRV_NUM5"));//// 模板短信管理
                            platsvcData.put("pam_BILLING_MODE", grpPlatSvc.getString("BILLING_MODE"));
                            platsvcData.put("pam_PORT_TYPE", "2");//1=主端口 2=子端口  RSRV_STR1 //grpPlatSvc.getString("RSRV_STR1")
                            platsvcData.put("pam_ADMIN_NUM", grpPlatSvc.getString("ADMIN_NUM"));
                            platsvcData.put("pam_DEFAULT_ECGN_LANG", grpPlatSvc.getString("DEFAULT_ECGN_LANG"));
                            platsvcData.put("pam_BIZ_CODE", grpPlatSvc.getString("BIZ_CODE"));
                            platsvcData.put("pam_PLAT_SYNC_STATE", grpPlatSvc.getString("PLAT_SYNC_STATE"));
                            platsvcData.put("pam_BIZ_TYPE_CODE", grpPlatSvc.getString("BIZ_TYPE_CODE"));
                            platsvcData.put("pam_MAX_ITEM_PRE_MON", grpPlatSvc.getString("MAX_ITEM_PRE_MON"));

                            if (opercode.equals("02")){//修改
//                            ofData.put("MODIFY_TAG", "2");
                                platsvcData.put("pam_MODIFY_TAG", "2");//0为新增
                                platsvcData.put("pam_OPER_STATE", "08");//修改

                            }else{//01 为新增
//                            ofData.put("MODIFY_TAG", "0");
                                platsvcData.put("pam_MODIFY_TAG", "0");//0为新增
//                            platsvcData.put("pam_OPER_STATE", grpPlatSvc.getString("OPER_STATE"));
                                platsvcData.put("pam_OPER_STATE", "01");

                            }
                            platsvcData.put("pam_DELIVER_NUM", grpPlatSvc.getString("DELIVER_NUM"));
                            platsvcData.put("pam_BIZ_STATUS",  grpPlatSvc.getString("BIZ_STATUS"));
                            platsvcData.put("pam_RSRV_TAG2", grpPlatSvc.getString("RSRV_TAG2"));
                            platsvcData.put("pam_EC_BASE_IN_CODE_A", grpPlatSvc.getString("EC_BASE_IN_CODE_A"));
                            platsvcData.put("pam_SERV_TYPE", "123");
                            platsvcData.put("pam_EC_BASE_IN_CODE", grpPlatSvc.getString("EC_BASE_IN_CODE"));
                            platsvcData.put("pam_BIZ_NAME", grpPlatSvc.getString("BIZ_NAME"));
                            platsvcData.put("pam_PRE_CHARGE", grpPlatSvc.getString("PRE_CHARGE"));
                            platsvcData.put("pam_BILLING_TYPE", grpPlatSvc.getString("BILLING_TYPE"));
                            platsvcData.put("pam_AUTH_CODE", grpPlatSvc.getString("AUTH_CODE"));
                            platsvcData.put("pam_BIZ_ATTR", strWhiteProperty);//黑白名单属性
                            platsvcData.put("pam_WHITE_TOWCHECK", grpPlatSvc.getString("RSRV_NUM3")); // 白名单二次确认RSRV_NUM3
                            platsvcData.put("pam_TEXT_ECGN_EN", textEn);//英文签名
                            platsvcData.put("pam_ACCESS_MODE", grpPlatSvc.getString("ACCESS_MODE"));
                            platsvcData.put("pam_PRICE", grpPlatSvc.getString("PRICE"));
                            platsvcData.put("pam_IS_MAS_SERV", grpPlatSvc.getString("RSRV_STR5"));////mas增加是否mas服务器的配置RSRV_STR5
                            platsvcData.put("pam_BIZ_PRI", grpPlatSvc.getString("BIZ_PRI"));
                            platsvcData.put("pam_RSRV_STRB", "1");
                            platsvcData.put("pam_SVR_CODE_HEAD",  grpPlatSvc.getString("EC_BASE_IN_CODE"));//和EC_BASE_IN_CODE相同
                            platsvcData.put("pam_SVR_CODE_END",  externSn);//扩展码
                            platsvcData.put("pam_TEXT_ECGN_ZH", textZh);//中文签名
                            platsvcData.put("pam_SERVICE_TYPE", grpPlatSvc.getString("RSRV_NUM2"));// 业务类型
                            userElements.add(platsvcDataset);

                            break;
                            //属性值结束


//                        }
                    }else if(opercode.equals("02")){
                        if (grpPlatSvc.getString("SERVICE_ID").equals(offerCode)){//取主服务的值进行操作
                            IData platsvcDataset = new DataMap();
                            IData platsvcData = new DataMap();
                            ((DataMap) platsvcDataset).put("PLATSVC",platsvcData);
                            ((DataMap) platsvcDataset).put("ID",iData.getString("OFFER_CODE"));
                            ((DataMap) platsvcDataset).put("CANCLE_FLAG","false");
                            //PLATSVC 构造开始
                            platsvcData.put("pam_MAX_ITEM_PRE_DAY", grpPlatSvc.getString("MAX_ITEM_PRE_DAY"));
                            platsvcData.put("pam_SERVICE_ID", iData.getString("OFFER_CODE"));
                            platsvcData.put("pam_BIZ_IN_CODE", grpPlatSvc.getString("EC_BASE_IN_CODE")+externSn);//基础码+扩展码
                            platsvcData.put("pam_RSRV_STR4", grpPlatSvc.getString("RSRV_STR4"));
                            platsvcData.put("pam_IS_TEXT_ECGN", grpPlatSvc.getString("IS_TEXT_ECGN"));//签名 是否支持签名
                            platsvcData.put("pam_HASMODIFYPRV", "true");
                            platsvcData.put("pam_SMS_TEMPALTE", grpPlatSvc.getString("RSRV_NUM5"));//// 模板短信管理
                            platsvcData.put("pam_BILLING_MODE", grpPlatSvc.getString("BILLING_MODE"));
                            platsvcData.put("pam_PORT_TYPE", "2");//1=主端口 2=子端口  RSRV_STR1 //grpPlatSvc.getString("RSRV_STR1")
                            platsvcData.put("pam_ADMIN_NUM", grpPlatSvc.getString("ADMIN_NUM"));
                            platsvcData.put("pam_DEFAULT_ECGN_LANG", grpPlatSvc.getString("DEFAULT_ECGN_LANG"));
                            platsvcData.put("pam_BIZ_CODE", grpPlatSvc.getString("BIZ_CODE"));
                            platsvcData.put("pam_PLAT_SYNC_STATE", grpPlatSvc.getString("PLAT_SYNC_STATE"));
                            platsvcData.put("pam_BIZ_TYPE_CODE", grpPlatSvc.getString("BIZ_TYPE_CODE"));
                            platsvcData.put("pam_MAX_ITEM_PRE_MON", grpPlatSvc.getString("MAX_ITEM_PRE_MON"));

                            if (opercode.equals("02")){//修改
//                            ofData.put("MODIFY_TAG", "2");
                                platsvcData.put("pam_MODIFY_TAG", "2");//0为新增
                                platsvcData.put("pam_OPER_STATE", "08");//修改

                            }else{//01 为新增
//                            ofData.put("MODIFY_TAG", "0");
                                platsvcData.put("pam_MODIFY_TAG", "0");//0为新增
//                            platsvcData.put("pam_OPER_STATE", grpPlatSvc.getString("OPER_STATE"));
                                platsvcData.put("pam_OPER_STATE", "01");

                            }
                            platsvcData.put("pam_DELIVER_NUM", grpPlatSvc.getString("DELIVER_NUM"));
                            platsvcData.put("pam_BIZ_STATUS",  grpPlatSvc.getString("BIZ_STATUS"));
                            platsvcData.put("pam_RSRV_TAG2", grpPlatSvc.getString("RSRV_TAG2"));
                            platsvcData.put("pam_EC_BASE_IN_CODE_A", grpPlatSvc.getString("EC_BASE_IN_CODE_A"));
                            platsvcData.put("pam_SERV_TYPE", "123");
                            platsvcData.put("pam_EC_BASE_IN_CODE", grpPlatSvc.getString("EC_BASE_IN_CODE"));
                            platsvcData.put("pam_BIZ_NAME", grpPlatSvc.getString("BIZ_NAME"));
                            platsvcData.put("pam_PRE_CHARGE", grpPlatSvc.getString("PRE_CHARGE"));
                            platsvcData.put("pam_BILLING_TYPE", grpPlatSvc.getString("BILLING_TYPE"));
                            platsvcData.put("pam_AUTH_CODE", grpPlatSvc.getString("AUTH_CODE"));
                            platsvcData.put("pam_BIZ_ATTR", strWhiteProperty);//黑白名单属性
                            platsvcData.put("pam_WHITE_TOWCHECK", grpPlatSvc.getString("RSRV_NUM3")); // 白名单二次确认RSRV_NUM3
                            platsvcData.put("pam_TEXT_ECGN_EN", textEn);//英文签名
                            platsvcData.put("pam_ACCESS_MODE", grpPlatSvc.getString("ACCESS_MODE"));
                            platsvcData.put("pam_PRICE", grpPlatSvc.getString("PRICE"));
                            platsvcData.put("pam_IS_MAS_SERV", grpPlatSvc.getString("RSRV_STR5"));////mas增加是否mas服务器的配置RSRV_STR5
                            platsvcData.put("pam_BIZ_PRI", grpPlatSvc.getString("BIZ_PRI"));
                            platsvcData.put("pam_RSRV_STRB", "1");
                            platsvcData.put("pam_SVR_CODE_HEAD",  grpPlatSvc.getString("EC_BASE_IN_CODE"));//和EC_BASE_IN_CODE相同
                            platsvcData.put("pam_SVR_CODE_END",  externSn);//扩展码
                            platsvcData.put("pam_TEXT_ECGN_ZH", textZh);//中文签名
                            platsvcData.put("pam_SERVICE_TYPE", grpPlatSvc.getString("RSRV_NUM2"));// 业务类型
                            userElements.add(platsvcDataset);

                            break;
                            //属性值结束


                        }


                    }




                }

            }


                }

        //查询TRADE_TYPE_CODE
        String tradeTypeCode = getAttrValueFromAttrBiz(productId, "P", BizCtrlType.CreateUser, "TradeTypeCode");
        System.out.println("getAttrValueFromAttrBiz--------------" + tradeTypeCode);

        if (StringUtils.isEmpty(tradeTypeCode)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据" + productId + "没找到业务类型");
        }
        offerInfo.put("TRADE_TYPE_CODE", tradeTypeCode);
        IData ajaxData = new DataMap();
        //集团定制
        String useTag = IUpcCall.getUseTagByProductId(offerInfo.getString("OFFER_CODE"));
        if ("1".equals(useTag)) {
            //定制初始化
            IDataset mebOffers = IUpcCall.queryOfferJoinRelAndOfferByOfferId(offerId, "1", "", "");
            System.out.println("queryOfferJoinRelAndOfferByOfferId--------------" + mebOffers);
            if (DataUtils.isEmpty(mebOffers)) {
                useTag = "0";//如果不存在成员商品，则不需要定制
            } else {
                IDataset userGrpPackageList = queryEcPackages(productId, serviceUserId);//带packageid 的
                System.out.println("queryEcPackages--------------" + userGrpPackageList);

                newQryData.put("GRP_PACKAGE_INFO", userGrpPackageList);

            }
        }
        logger.debug("==============================组装数据" + offerDatalist);
        builderSvcData(newQryData);
    }

    /**使用custId,contractId查询合同信息
     * @param custId
     * @param contractId
     * @return
     * @throws Exception
     */
    public IDataset queryProductInfo(String custId, String contractId) throws Exception
    {
        IData data = new DataMap();
        data.put("CUST_ID", custId);
        data.put("CONTRACT_ID", contractId);
        IDataset allProductInfo = CSAppCall.call("CS.CustContractProductInfoQrySVC.qryContractProductByContIdForGrp", data);
        if(allProductInfo == null){
            allProductInfo = new DatasetList();
        }
        return allProductInfo;
    }
    /**
     * 提交事务 调用接口
     *
     * @param
     * @throws Exception
     */
    protected void builderSvcData(IData svcdata) throws Exception {

//
        logger.debug("==============================前台传入数据{}" + svcdata);
        IData svcParam = svcdata;
        if (IDataUtil.isNotEmpty(svcdata.getData("AUDIT_INFO"))) {
            IData auditInfo = svcdata.getData("AUDIT_INFO");
            if (!StringUtils.isBlank(auditInfo.getString("AUDIT_STAFF_ID"))) {
                svcParam.put("AUDIT_STAFF_ID", auditInfo.getString("AUDIT_STAFF_ID", ""));
            }
            if (!StringUtils.isBlank(auditInfo.getString("MEB_VOUCHER_FILE_LIST"))) {
                svcParam.put("VOUCHER_FILE_LIST", auditInfo.getString("MEB_VOUCHER_FILE_LIST", ""));
            }
        }


        /*************************调用规则productinfo 开始*************************/

        //   checkEcProductInfoRule(svcParam,allElement,operType);
        /*************************调用规则productinfo 结束*************************/
        svcParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
        svcParam.put(Route.USER_EPARCHY_CODE, "0898");
        System.out.println(svcParam);
//        String svc = "CS.CreateGroupUserSvc.createGroupUser"; //新增办理的类
        String svc = "CS.ChangeUserElementSvc.changeUserElement"; //变更办理类

        logger.debug("sundz=============================调用服务 {} 数据 {}" + svc + svcParam);
        // 根据条件判断调用服务
        setSVC(svcParam);
        // 调登记服务
        //IDataset result = CSAppCall.call(svc, svcParam);
        //logger.debug("sundz=============================服务返回结果:{}" + result);
        //logger.debug("sundz=============================服务流水号:{}, 请求入参:{}" + result.first().getString("ORDER_ID") + svcParam);


    }
    // 根据条件判断调用服务
    protected void setSVC(IData batData) throws Exception {
        String svcName = "";
//        IData svcData = batData.getData("svcData", new DataMap());
        svcName = "CS.ChangeUserElementSvc.changeUserElement";
        batData.put("REAL_SVC_NAME", svcName);

    }
    public IDataset queryEcPackages(String productId, String ecUserId) throws Exception {
        IDataset userGrpPackageList = new DatasetList();
        IDataset elementList = new DatasetList();
        if (StringUtils.isNotBlank(ecUserId)) {
            IData inparam = new DataMap();
            inparam.put("USER_ID", ecUserId);
            elementList = CSAppCall.call("CS.UserGrpPkgInfoQrySVC.getUserGrpPackageForGrp", inparam);
            if (DataUtils.isEmpty(elementList)) {
                return userGrpPackageList;
            }
            for (int i = 0; i < elementList.size(); i++) {
                IData temp = elementList.getData(i);
                IData userGrpPackage = new DataMap();
                userGrpPackage.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
                userGrpPackage.put("ELEMENT_ID", temp.getString("ELEMENT_ID"));
                userGrpPackage.put("PACKAGE_ID", temp.getString("PACKAGE_ID"));
                userGrpPackage.put("ELEMENT_NAME", temp.getString("ELEMENT_NAME"));
                userGrpPackage.put("ELEMENT_TYPE_CODE", temp.getString("ELEMENT_TYPE_CODE"));
                userGrpPackage.put("MODIFY_TAG", "EXIST");//默认不变

                IDataset element = IUpcCall.qryOfferByOfferIdRelOfferId(temp.getString("PRODUCT_ID"), "P", temp.getString("ELEMENT_ID"), temp.getString("ELEMENT_TYPE_CODE"), "");
                if (DataUtils.isNotEmpty(element)) {
                    userGrpPackage.put("SELECT_FLAG", element.first().getString("FORCE_TAG", ""));
                }

                userGrpPackageList.add(userGrpPackage);
            }
        } else {

            String user_eparchy_code = "";
            IData inparam = new DataMap();
            inparam.put("PRODUCT_ID", productId);
            inparam.put(Route.USER_EPARCHY_CODE, user_eparchy_code);
            elementList = CSAppCall.call("CS.ProductInfoQrySVC.getMebProductForceElements", inparam);
            for (int i = 0; i < elementList.size(); i++) {
                IData temp = elementList.getData(i);
                logger.debug("sundz=============================temp:" + temp);

                IData userGrpPackage = new DataMap();
                userGrpPackage.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
                userGrpPackage.put("ELEMENT_ID", temp.getString("ELEMENT_ID"));
                userGrpPackage.put("PACKAGE_ID", temp.getString("PACKAGE_ID"));
                userGrpPackage.put("ELEMENT_FORCE_TAG", temp.getString("ELEMENT_FORCE_TAG"));
                userGrpPackage.put("ELEMENT_NAME", temp.getString("ELEMENT_NAME"));
                userGrpPackage.put("MODIFY_TAG", "0");
                userGrpPackage.put("ELEMENT_TYPE_CODE", temp.getString("ELEMENT_TYPE_CODE"));
                userGrpPackage.put("SELECT_FLAG", temp.getString("ELEMENT_FORCE_TAG"));
                userGrpPackageList.add(userGrpPackage);
            }

            // setEcPackages(userGrpPackageList);
        }
        String user_eparchy_code = "";

        //获取必选包数据
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", productId);
        inparam.put("EPARCHY_CODE", user_eparchy_code);
        IDataset mebPkgList = CSAppCall.call("CS.ProductPkgInfoQrySVC.getMebForcePackageByGrpProId", inparam);
        return userGrpPackageList;
    }


    //处理报错问题
    public static IDataset qryAuditInfo(String staffId,String staffName) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("STAFF_ID", staffId);
        inparam.put("STAFF_NAME", staffName);
        inparam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset result = CSAppCall.call("SS.AuditStaffInfoQrySVC.qryAuditStaffInfo", inparam);
        return result;
    }

    public static String getAttrValueFromAttrBiz(String id,String idType,String obj,String code) throws Exception
    {
        String buisType = "";
        IData tempBiz = new DataMap();
        tempBiz.put("ID", id);
        tempBiz.put("ID_TYPE", idType);
        tempBiz.put("ATTR_OBJ", obj);
        tempBiz.put("ATTR_CODE", code);
        IDataset result = CSAppCall.call("CS.AttrBizInfoQrySVC.getBizAttr", tempBiz);

        if (IDataUtil.isNotEmpty(result))
        {
            buisType = result.first().getString("ATTR_VALUE", "");
        }
        return buisType;
    }


}
