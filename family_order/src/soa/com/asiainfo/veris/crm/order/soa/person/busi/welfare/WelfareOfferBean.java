
package com.asiainfo.veris.crm.order.soa.person.busi.welfare;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.BusinessAbilityCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 权益自有商品受理Bean
 * @Auther: zhenggang
 * @Date: 2020/7/3 9:58
 * @version: V1.0
 */
public class WelfareOfferBean extends CSBizBean {
    private static final Logger log = Logger.getLogger(WelfareOfferBean.class);

    /**
     * @param: 接口返回提示
     */
    public IData prepareOutResult(int i, String rtnMsg, IData outData) {
        IData object = new DataMap();
        IData result = new DataMap();

        if (i == 0)//成功
        {
            object.put("resultRows", outData.getString("resultRows", "1"));
            outData.remove("resultRows");
            object.put("result", outData);
            object.put("respCode", "0");
            object.put("respDesc", "success");

            result.put("object", object);
            result.put("rtnCode", "0");
            result.put("rtnMsg", "成功!");
            return result;
        } else if (i == 1)//失败
        {
            object.put("result", outData);
            object.put("resultRows", 0);
            object.put("respCode", "-1");
            object.put("respDesc", rtnMsg);

            result.put("object", object);
            result.put("rtnCode", "-9999");
            result.put("rtnMsg", "失败");

            return result;
        }
        return null;
    }

    /**
     * @Description 产品变更前，选择产品后增加权益弹窗提醒
     * @Auther: liwei29
     * @Date: 2020/7/13 18:04
     * @version: V1.0
     */

    public IData queryWelfareByUpProduct(IData input) throws Exception {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "查询成功！");
        result.put("TIP_INFO", "");
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "NEW_PRODUCT_ID");
        IData param = new DataMap();
        String newProductId = input.getString("NEW_PRODUCT_ID", "");
        String serialNum = input.getString("SERIAL_NUMBER", "");
        param.put("SERIAL_NUMBER", serialNum);
        param.put("REMOVE_TAG", "0");
        String newContain = "";
        String nowContain = "";
        String nowUserContain = "";
        try {
            IDataset userInfos = Dao.qryByCode("TF_F_USER", "SEL_ALL_BY_SN",
                    param);
            if (IDataUtil.isEmpty(userInfos)) {
                result.put("X_RESULTCODE", "2005");
                result.put("X_RESULTINFO", "用户不存在！");

            }
            else {
                String userid = userInfos.getData(0).getString("USER_ID");
                //inparam.put("PRODUCT_ID", newProductId);
                // 查询当前主产品信息
                IDataset mainProductInfo = UserProductInfoQry.queryMainProductNow(userid);
                String mainInstId = mainProductInfo.getData(0).getString("INST_ID");
                String nowProductId = mainProductInfo.getData(0).getString(
                        "PRODUCT_ID");

                // 获得新旧主产品名称
                String newProductName = UpcCall.qryOfferNameByOfferTypeOfferCode(newProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
                String nowProductName = UpcCall.qryOfferNameByOfferTypeOfferCode(nowProductId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);

                //String startDate = SysDateMgr.getSysTime();
                /*根据产品id查询关联的权益
                 *获取新旧产品是否具有关联权益
                 */
                IDataset addRelWelfareOfferCfgs = UpcCall.queryMainOfferRelaWelfareOffers(newProductId);
                if(IDataUtil.isNotEmpty(addRelWelfareOfferCfgs)){
                    for (int i = 0; i < addRelWelfareOfferCfgs.size(); i++) {
                        newContain += addRelWelfareOfferCfgs.getData(i).getString("OFFER_NAME")+",";

                    }
                }
                //通过productid可以找到Inst关联的订购offer_rel关系是否有Q存在,若存在则查询出来
                //查询是否有Q关联
                IDataset offerList = UserOfferRelInfoQry.qryUserAllOfferRelByRelOfferTypeAndInstId("Q", mainInstId);
                if (IDataUtil.isNotEmpty(offerList)) {
                    offerList = DataHelper.distinct(offerList, "REL_OFFER_CODE", ",");
                    for (int i = 0; i < offerList.size(); i++) {
                        IDataset offerName = UpcCall.queryOfferNameByOfferCodeAndType("Q", offerList.getData(i).getString("REL_OFFER_CODE"));
                        if (IDataUtil.isNotEmpty(offerName)) {
                            String offerTip = offerName.getData(i).getString("OFFER_NAME");
                            nowContain += offerTip+",";
                        }
                    }
                }


                String smsContent = "您好！您办理了产品变更业务，新办理的产品为" + newProductName;
                if (StringUtils.isNotBlank(newContain)) {
                    smsContent += "，该套餐关联的权益包有：" + newContain + "。";
                } else {
                    smsContent += "，该套餐未具有关联的权益。";
                }
                smsContent += "取消的产品为" + nowProductName;
                if (StringUtils.isNotBlank(nowContain)) {
                    smsContent += "，该套餐关联的权益包有" + nowContain + "。";
                } else {
                    smsContent += "，该套餐未具有关联的权益。";
                }
                /**startDate=startDate.substring(0,10);
                 smsContent += "您办理的"
                 + newProductName
                 + "于"
                 + startDate
                 + "生效，变更生效后，"
                 + "已开通的"
                 + nowUserContain
                 + "业务将按新生效套餐的标准收取，"
                 + "您可发短信0000到10086查询、取消已开通业务（宽带及魔百和等可通过营业厅或10086热线变更、取消），"
                 + "更多详情可拨打10086热线咨询。【中国移动】 ";*/
                result.put("TIP_INFO", smsContent);

            }
        } catch (Exception e) {
            result.put("X_RESULTCODE", "2998");
            // result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RESULTINFO", "其他异常情况");
        }

        return result;
    }

    /**
     * @Description 三户资料查询权益中心使用
     * @Auther: liwei29
     * @Date: 2020/7/15 18:04
     * @version: V1.0
     */

    public IData queryUseCustInfoBySn(IData input) throws Exception {
        IData result = new DataMap();
        String Status = "";//用户状态：0/1，0为正常，1为非正常
        String Msg = "SUCCESS";
        String userid = "";
        String custName = "";
        try {

            IDataUtil.chkParam(input, "SERIAL_NUMBER");

            String serialNum = input.getString("SERIAL_NUMBER", "");
            //根据手机号码查找user
            UcaData uca = UcaDataFactory.getNormalUca(serialNum);
            String acctId = uca.getAcctId();
            String userState = uca.getUser().getState();
            userid = uca.getUserId();
            custName=uca.getCustomer().getCustName();

                if ("0".equals(userState)) {
                    Status = "0";
                } else {
                    Status = "1";
                }

            result.put("STATUS", Status);
            result.put("USER_ID", userid);
            result.put("CUST_NAME", custName);
            result.put("ACCT_ID", acctId);

        } catch (Exception e) {

            Msg = e.getMessage();
        }
        result.put("MSG", Msg);
        return result;
    }

    /**
     * @Description 三户资料查询权益中心使用
     * @Auther: liwei29
     * @Date: 2020/7/15 18:04
     * @version: V1.0
     */

    public IData queryBusinessCenterTest(IData input) throws Exception {
        IData inparam = new DataMap();
        String Msg = "";
        IData result = new DataMap();
        try {

            String serialNum = input.getString("SERIAL_NUMBER", "");
            inparam.put("tel_Num",serialNum);
            String paramName = "HAIN_UNHQ_QYorderList";
            result = BusinessAbilityCall.callBusinessCenterCommon(paramName,inparam);

        } catch (Exception e) {

            Msg = e.getMessage();
        }
       // result.put("MSG", Msg);
        return result;
    }

    /**
     * @Description
     * @Auther: liwei29
     * @Date: 2020/7/15 18:04
     * @version: V1.0
     */

    public IData welFareOrderReturn(IData input) throws Exception {
        IData inparam = new DataMap();
        String Msg = "";
        IData result = new DataMap();
        try {

            String serialNum = input.getString("SERIAL_NUMBER", "");
            String userId = input.getString("USER_ID", "");
            String rightId = input.getString("RIGHT_ID", "");
            String outOrderId = input.getString("OUT_ORDER_ID", "");
            String userType = "0";
            String numberType = "1";
            String operType ="1";
            IDataset returnOrderVos = new DatasetList();
            IData output = new DataMap();
            output.put("rightId",rightId);
            output.put("userId",userId);
            output.put("userType",userType);
            output.put("numberType",numberType);
            output.put("telnum",serialNum);
            output.put("outOrderId",outOrderId);
            returnOrderVos.add(output);
            inparam.put("operType",operType);
            inparam.put("returnOrderVos",returnOrderVos);
            String paramName = "HAIN_UNHT_QYreturnOrCreateOrder";
            result = BusinessAbilityCall.callBusinessCenterCommon(paramName,inparam);

        } catch (Exception e) {

            Msg = e.getMessage();
        }
        // result.put("MSG", Msg);
        return result;
    }


}
