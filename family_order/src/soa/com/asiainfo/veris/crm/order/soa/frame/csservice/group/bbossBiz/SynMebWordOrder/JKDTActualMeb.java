package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.SynMebWordOrder;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.client.ServiceFactory;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.GrpIntf;

/**
 * @program: zd_hainan_order
 * @description:
 * @author: zhangchengzhi
 * @create: 2018-12-06 07:31
 **/

public class JKDTActualMeb {


    public void mebWorkRun(IData param) {
        SessionManager manager = SessionManager.getInstance();
        try {
            // 激活会话,并设置上下文对象

            // 循环处理多个成员服务号码
            IDataset singleSerialNumberList = GrpIntf.dealserialNumberListJKDT(param);
            if(IDataUtil.isNotEmpty(singleSerialNumberList))
            {
                for (int i = 0; i < singleSerialNumberList.size(); i++)
                {
                    IData mebWordOrderMap = singleSerialNumberList.getData(i);
                    //鉴权处理，鉴权失败，直接登记xml_info表为2状态，gtm不再扫描处理
                    mebWordOrderMap.put("DEAL_STATE", "0");//0代表等待处理
                    dealJKDTMemInfo(mebWordOrderMap);
                    CSAppCall.call("CS.bbossCenterControlSVC.rigisitXmlInfo",mebWordOrderMap);
                }
            }
            manager.commit();
        } catch (Exception e) {
            try {
                manager.rollback();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } finally {
            try {
                manager.destroy();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void dealJKDTMemInfo(IData inparam) throws Exception {
        // 1- 集团用户信息不存在，说明本省非用户开展，工单无法开通
        IData inparams = (IData) Clone.deepClone(inparam);
        String offerId = inparam.getString("PRODUCTID", "");
        inparams.put("PRODUCT_OFFER_ID", offerId);
        String url = BizEnv.getEnvString("service.router.addr", "");
        IDataInput idataInput = new DataInput();
        idataInput.getData().putAll(inparams);
        IData headData = new DataMap();
        headData.put("STAFF_EPARCHY_CODE", "0898");
        headData.put("LOGIN_EPARCHY_CODE", "0898");
        headData.put("STAFF_ID", "IBOSS000");
        headData.put("DEPART_ID", "00309");
        headData.put("IN_MODE_CODE", "6");
        idataInput.getHead().putAll(headData);

        IDataOutput idataOutput;
        IDataset userProductInfoList;
        userProductInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(offerId);
        if (IDataUtil.isEmpty(userProductInfoList)) {
            inparam.put("OPEN_RESULT_CODE", "99");
            inparam.put("OPEN_RESULT_DESC", "成员归属省不在业务开展省内，无法同步成员签约关系");
            inparam.put("DEAL_STATE", "2");//2代表处理完成
            return;
        }

        // 2- 成员用户信息不存在，工单无法开通
        idataInput.getData().put("REMOVE_TAG", "0");
        idataOutput = ServiceFactory.call(url, "CS.UserInfoQrySVC.getUserInfoBySnNoProduct", idataInput, null, false, true);
        IDataset memberUserInfoList = idataOutput.getData();

        IDataset prodCommparaList = CommparaInfoQry.getCommpara("CSM", "9089",
                userProductInfoList.getData(0).getString("PRODUCT_SPEC_CODE", ""), "ZZZZ");
        //非本省成员&&集团允许外省成员开通 情况直接开通，不做后续校验
        if (IDataUtil.isEmpty(memberUserInfoList) && IDataUtil.isNotEmpty(prodCommparaList)){
            return;
        }


        if (IDataUtil.isEmpty(memberUserInfoList)) {
            inparam.put("OPEN_RESULT_CODE", "13");
            inparam.put("OPEN_RESULT_DESC", "用户状态不正常");
            inparam.put("DEAL_STATE", "2");  //2代表处理完成
            return;
        }

        // 3- 如果该当业务为集团一点支付，成员帐户为预付费帐户，工单无法开通
        IData memUserInfo = memberUserInfoList.getData(0);
        IData userProductInfo = userProductInfoList.getData(0);
        String productSpecCode = userProductInfo.getString("PRODUCT_SPEC_CODE", "");
        if ("99902".equals(productSpecCode)) {
            if (!"0".equals(memUserInfo.getString("PREPAY_TAG"))) {
                inparam.put("OPEN_RESULT_CODE", "12");
                inparam.put("OPEN_RESULT_DESC", "成员帐户为预付费帐户，工单无法开通");
                inparam.put("DEAL_STATE", "2");//2代表处理完成
                return;
            }
        }

        // 4- 多媒体彩铃，成员事先没有开通多媒体桌面电话或者已经开通了一号通产品，工单无法开通(多媒体彩铃成员产品与多媒体桌面电话依赖，与一号通互斥)
        String memUserId = memUserInfo.getString("USER_ID");
        if ("910401".equals(productSpecCode)) {
            // 4-1- 根据成员用户编号查询UU关系表，查看该成员是否开通了多媒体桌面电话
            idataInput.getData().put("USER_ID_B", memUserId);
            idataInput.getData().put("RELATION_TYPE_CODE", "S1");
            idataOutput = ServiceFactory.call(url, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserRelarIdB", idataInput, null, false, true);
			 String sn = idataInput.getData().getString("SERIAL_NUMBER");
            int len=sn.length();
            IDataset multTabTelUserRelaList = idataOutput.getData();
           if(len!=11){
            	
            
            if (IDataUtil.isEmpty(multTabTelUserRelaList)) {
                inparam.put("OPEN_RESULT_CODE", "13");
                inparam.put("OPEN_RESULT_DESC", "成员必须先订购多媒体桌面电话才能订购该产品!");
                inparam.put("DEAL_STATE", "2");//2代表处理完成
                return;
            }
 }

            // 4-2- 根据成员用户编号查询UU关系表，查看该成员是否开通了一号通产品
            idataInput.getData().put("USER_ID_B", memUserId);
            idataInput.getData().put("RELATION_TYPE_CODE", "E2");
            idataOutput = ServiceFactory.call(url, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserRelarIdB", idataInput, null, false, true);
            IDataset oneCardUserRelaList = idataOutput.getData();
            if (!IDataUtil.isEmpty(oneCardUserRelaList)) {
                inparam.put("OPEN_RESULT_CODE", "13");
                inparam.put("OPEN_RESULT_DESC", "多媒体彩铃与一号通产品互斥，不能订购该产品!");
                inparam.put("DEAL_STATE", "2");//2代表处理完成
                return;
            }
        }
    }

}
