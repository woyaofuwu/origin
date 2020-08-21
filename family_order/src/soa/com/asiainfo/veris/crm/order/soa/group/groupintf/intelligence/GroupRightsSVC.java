
package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.user.UUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;


/**
 * 智慧中台 —— 集团权益订购&&退订
 * @date 20200808
 * @author zhengkai5
 * */
public class GroupRightsSVC extends CSBizService
{
    
    private static final long serialVersionUID = 1L;

    public final static String CreateUser = "CrtUs"; // 集团产品新增

    public final static String DestoryUser = "DstUs"; // 集团产品注销

    /**
     *  政企权益订购
     *
     */
    public IData groupRightsOrder(IData input) throws Exception
    {
        IData result = new DataMap();
        String operType = IDataUtil.chkParam(input,"OPER_TYPE");
        if(CreateUser.equals(operType))
        {
            result = createGroupRights(input);
        }
        else  if(DestoryUser.equals(operType))
        {
            result = destoryGroupRights(input);
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_713,"操作类型不符合要求！");
        }

        return result ;
    }


    public IData createGroupRights(IData input) throws Exception
    {
        IData svcData = new DataMap();
        String groupId = IDataUtil.chkParam(input,"GROUP_ID");
        String productId = IDataUtil.chkParam(input,"OFFER_CODE");
        String offerType = IDataUtil.chkParam(input,"OFFER_TYPE");
        String contractId = IDataUtil.chkParam(input,"CONTRACT_ID");
        String contractWriteDate = IDataUtil.chkParam(input,"CONTRACT_WRITE_DATE");
        String contractEndDate = IDataUtil.chkParam(input,"CONTRACT_END_DATE");

        IData grpCustInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        if(IDataUtil.isEmpty(grpCustInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_190, groupId);
        }

        String custId = grpCustInfo.getString("CUST_ID");
        svcData.put("CUST_ID",custId);
        svcData.put("PRODUCT_ID",productId);

        IDataset resInfo = new DatasetList();
        IData res = getSerialNumber(groupId,productId,offerType,getTradeEparchyCode());
        resInfo.add(res);
        svcData.put("RES_INFO",resInfo);

        String serialNumber = res.getString("SERIAL_NUMBER");
        svcData.put("SERIAL_NUMBER",serialNumber);

        IData userInfo = new DataMap();
        userInfo.put("SERIAL_NUMBER",serialNumber);
        userInfo.put("OFFER_IDS",productId);
        userInfo.put("CONTRACT_ID",contractId);
        userInfo.put("CONTRACT_WRITE_DATE",contractWriteDate);
        userInfo.put("CONTRACT_END_DATE",contractEndDate);
        svcData.put("USER_INFO",userInfo);
        svcData.put(Route.USER_EPARCHY_CODE,getTradeEparchyCode());

        String custName = grpCustInfo.getString("CUST_NAME");
        IData acctInfo = new DataMap();
        acctInfo.put("PAY_MODE_CODE","0");
        acctInfo.put("ACCT_NAME",custName);
        acctInfo.put("PAY_NAME",custName);
        acctInfo.put("ACCT_TYPE","0");
        acctInfo.put(Route.USER_EPARCHY_CODE,getTradeEparchyCode());
        acctInfo.put("EPARCHY_CODE",getTradeEparchyCode());
        svcData.put("ACCT_INFO", acctInfo);

        svcData.put("ACCT_IS_ADD", true);

        IDataset result = CSAppCall.call("CS.CreateGroupUserSvc.createGroupUser",svcData);
        return result.first() ;
    }

    public IData destoryGroupRights(IData input) throws Exception
    {
        String userId = IDataUtil.chkParam(input,"USER_ID");
        String productId = IDataUtil.chkParam(input,"PRODUCT_ID");

        IDataset userProInfo = ProductInfoQry.qryProductByUserIdAndProId(userId,productId);
        if(IDataUtil.isEmpty(userProInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_93);// 获取集团用户订购产品资料无信息！
        }

        IData svcData = new DataMap();
        svcData.put("USER_ID", userId);
        svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        svcData.put("IF_BOOKING", "false");
        svcData.put("REASON_CODE", null);
        svcData.put("REMARK", "集团权益注销");
        svcData.put("IF_CENTRETYPE", null);
        svcData.put("PRODUCT_ID", productId);

        IDataset result = CSAppCall.call("CS.DestroyGroupUserSvc.destroyGroupUser",svcData);
        return result.first() ;
    }

    public IData getSerialNumber(String groupId, String productId,String productType ,String grpUserEparchyCode) throws Exception {
        if (StringUtils.isEmpty(groupId) || StringUtils.isEmpty(productId)) {
            return null;
        }

        IDataset attrInfo = AttrBizInfoQry.getBizAttr(productId, productType, BizCtrlType.CreateUser, "TradeTypeCode",null);
        String tradeTypeCode = "";
        if (IDataUtil.isNotEmpty(attrInfo))
        {
            tradeTypeCode = attrInfo.first().getString("ATTR_VALUE", "");
        }
        if (StringUtils.isEmpty(tradeTypeCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据" + productId + "没找到业务类型");
        }

        // 避免服务号码的重复 add begin
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);
        param.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);

        IData grpSnData = new DataMap();
        for (int i = 0; i < 10; i++) {
            grpSnData = CSAppCall.callOne("CS.GrpGenSnSVC.genGrpSn", param);
            String serialNumber = grpSnData.getString("SERIAL_NUMBER", "");
            if (StringUtils.isEmpty(serialNumber)) {
                break;
            }

            IData userList = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);
            param.clear();
            param.put("SERIAL_NUMBER", serialNumber);
            param.put(Route.USER_EPARCHY_CODE, grpUserEparchyCode);
            param.put("TRADE_TYPE_CODE", tradeTypeCode);
            IData tradeList = CSAppCall.callOne("CS.TradeInfoQrySVC.getMainTradeBySN", param);
            if (IDataUtil.isEmpty(userList) && IDataUtil.isEmpty(tradeList)) {
                break;
            }
        }
        // 避免服务号码的重复 add end

        String serialNumber = grpSnData.getString("SERIAL_NUMBER", "");
        String ifResCode = grpSnData.getString("IF_RES_CODE", "");
        String resTypeCode =  "L";
        // 服务号码信息
        IData infoData = new DataMap();
        infoData.put("SERIAL_NUMBER", serialNumber);
        infoData.put("RES_TYPE_CODE", resTypeCode);
        infoData.put("IF_RES_CODE", ifResCode);
        infoData.put("MODIFY_TAG", "0");
        return infoData;
    }

    /**
     * 根据成员用户号码，查询归属的集团
     *  qryGrpInfoByMebSn
     * @param
     * */
    public IDataset qryGrpInfoByMebSn(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        String serialNumber = IDataUtil.chkParam(input,"SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber,null);
        if(IDataUtil.isEmpty(userInfo))
        {
            return new DatasetList();
        }
        IDataset relaInfo = RelaUUInfoQry.getAllRelaUUByUserB(userInfo.getString("USER_ID"));
        if(IDataUtil.isNotEmpty(relaInfo))
        {
            for (int i =0;i<relaInfo.size();i++)
            {
                String grpUserId = relaInfo.getData(i).getString("USER_ID_A");
                String grpSn = relaInfo.getData(i).getString("SERIAL_NUMBER_A");
                IData grpUserInfo = UcaInfoQry.qryGrpInfoByUserId(grpUserId);
                if(IDataUtil.isNotEmpty(grpUserInfo))
                {
                    String custId = grpUserInfo.getString("CUST_ID");
                    IData grpInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
                    if(IDataUtil.isNotEmpty(grpInfo))
                    {
                        IData param = new DataMap();
                        param.put("SERIAL_NUMBER",grpSn);
                        param.put("USER_ID",grpUserId);
                        param.put("CUST_NAME",grpInfo.getString("CUST_NAME"));
                        param.put("CUST_ID",grpInfo.getString("CUST_ID"));
                        result.add(param);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 根据集团客户编码，查询所有成员信息
     * qryGrpInfoByMemberInfo
     * @param
     * */
    public IDataset qryMemInfoByGrpCustId(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        String custId = IDataUtil.chkParam(input,"CUST_ID");
        IDataset grpUserInfo = UUserInfoQry.qryUserInfoByCustId(custId);
        if(IDataUtil.isEmpty(grpUserInfo))
        {
            return result;
        }

        for (int i = 0 ;i<grpUserInfo.size() ;i++)
        {
            IData userInfo = grpUserInfo.getData(i);
            String userId = userInfo.getString("USER_ID");
            IDataset mebInfo = RelaUUInfoQry.getAllRelaUUByUserA(userId);
            if(IDataUtil.isNotEmpty(mebInfo))
            {
                for(int j = 0;j<mebInfo.size() ; j++)
                {
                    IData meb = mebInfo.getData(j);
                    IData info = UcaInfoQry.qryUserInfoByUserId(meb.getString("USER_ID_B"));
                    if(IDataUtil.isNotEmpty(info))
                    {
                        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(info.getString("CUST_ID"));

                        IData param = new DataMap();
                        param.put("SERIAL_NUMBER",meb.getString("SERIAL_NUMBER_B"));
                        param.put("USER_ID",meb.getString("USER_ID_B"));
                        param.put("CUST_NAME",custInfo.getString("CUST_NAME"));
                        param.put("CUST_ID",custInfo.getString("CUST_ID"));

                        result.add(param);
                    }
                }
            }
        }

        return result;
    }
}