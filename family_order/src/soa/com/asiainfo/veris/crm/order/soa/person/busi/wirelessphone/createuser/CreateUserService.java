/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CreateUserService.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-8 上午10:23:33 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-8 chengxf2 v1.0.0 修改原因
 */

public class CreateUserService extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-8 上午10:24:10 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-8 chengxf2 v1.0.0 修改原因
     */
    public IData checkSerialNumber(IData input) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = input.getString("SERIAL_NUMBER");
        IDataset outDataset = TradeInfoQry.getMainTradeBySn(serialNumber, this.getRouteId());
        if (IDataUtil.isNotEmpty(outDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_93);// 有未完工工单，业务不能继续办理！
        }
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1153, serialNumber);// 号码[%s]已经开户，请重新输入
        }
        IData fixedEasyPhoneInfo = PBossCall.getFixedEasyPhoneInfo(serialNumber);
        if (IDataUtil.isEmpty(fixedEasyPhoneInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1154, serialNumber);// 没有获取到号码[%s]的绑定号码
        }
        String strSerialNubberBinding = fixedEasyPhoneInfo.getString("MPHONE_NO", "");
        if (StringUtils.isBlank(strSerialNubberBinding))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1154, serialNumber);// 没有获取到号码[%s]的绑定号码
        }
        userInfo = UcaInfoQry.qryUserMainProdInfoBySn(strSerialNubberBinding);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1155, strSerialNubberBinding);// 被绑定号码[%s]无用户资料
        }
        String bindProductId = userInfo.getString("PRODUCT_ID");
        String productId = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", new String[]
        { "SUBSYS_CODE", "PARAM_ATTR" }, "PARAM_CODE", new String[]
        { "CSM", "9723" });
        if (!StringUtils.equals(bindProductId, productId))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1164, strSerialNubberBinding);// 被绑定号码[%s]产品信息错误
        }
        String userId = userInfo.getString("USER_ID");
        IDataset userSimInfos = UserResInfoQry.queryUserSimInfo(userId, "1");// 查询SIM卡资源信息
        if (IDataUtil.isEmpty(userSimInfos))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1165);// 查询SIM卡信息错误
        }
        IDataset relaUUInfos = RelaUUInfoQry.getRelaUUInfoByUserIdA(userId, "T2");
        if (IDataUtil.isNotEmpty(relaUUInfos))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1166, strSerialNubberBinding);// [%s]已经被其他商务电话用户绑定
        }
        result.put("SERIAL_NUMBER_BIND", strSerialNubberBinding);
        result.put("SIM_CARD_NO", userSimInfos.get(0, "RES_CODE"));
        result.put("IMSI", userSimInfos.get(0, "IMSI"));
        result.put("KI", userSimInfos.get(0, "KI"));
        return result;
    }

    /**
     * @Function: getProductFeeInfo()
     * @Description: 查询产品费用
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-19 上午10:27:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-19 yxd v1.0.0 修改原因
     */
    public IData getProductFeeInfo(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String fee = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", new String[]
        { "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, "PARA_CODE1", new String[]
        { "CSM", "9721", productId });
        IData feeData = new DataMap();
        if (StringUtils.isNotBlank(fee))
        {
            feeData.put("FEE_MODE", "2");
            feeData.put("FEE_TYPE_CODE", "0");
            feeData.put("FEE", fee);
        }
        else
        {
            feeData.put("FEE_MODE", "2");
            feeData.put("FEE_TYPE_CODE", "0");
            feeData.put("FEE", "24000");
        }
        return feeData;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-8 下午05:30:41 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-8 chengxf2 v1.0.0 修改原因
     */
    public IData onInitTrade(IData input) throws Exception
    {
        IData output = new DataMap();
        IDataset productTypeList = UProductInfoQry.getProductsType("4000", null);// 商务电话产品类型;
        output.put("PRODUCT_TYPE_LIST", productTypeList);
        return output;
    }

}
