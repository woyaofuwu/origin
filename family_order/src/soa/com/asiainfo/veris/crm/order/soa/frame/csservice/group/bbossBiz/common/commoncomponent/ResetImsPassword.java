
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeMember.ChangeBBossMemBean;

/*
 * @description 企业飞信成员开通工单的过程中会涉及到重置序列号功能，该功能需要发送服务开通，因此选择走成员变更流程
 * @author xunyl
 * @date 2013-10-12
 */
public class ResetImsPassword
{

    /*
     * @description 拼装成员变更中的产品信息
     * @author xunyl
     * @date 2013-10-12
     */
    protected static IData getProductInfo(String serialNumber, IData userProductInfo, IData map) throws Exception
    {
        // 1- 定义返回结果
        IData merchpInfo = new DataMap();

        // 2- 添加产品用户编号
        String productUserId = userProductInfo.getString("USER_ID");
        merchpInfo.put("USER_ID", productUserId);

        // 3- 添加成员手机号码
        merchpInfo.put("SERIAL_NUMBER", serialNumber);

        // 4- 添加扩展属性信息(成员开通工单中的产品参数IMS密码还没有产生用户资料，无需更改参数的资料信息)
        IDataset paramInfoList = new DatasetList();
        merchpInfo.put("PRODUCT_PARAM_INFO", paramInfoList);

        // 5- 添加BBOSS侧产品信息
        IData bbossProductInfo = new DataMap();
        String memType = map.getString("USER_TYPE", "");
        bbossProductInfo.put("MEB_TYPE", memType);
        bbossProductInfo.put("MEB_OPER_CODE", "7");
        bbossProductInfo.put("USER_ID", productUserId);
        merchpInfo.put("PRODUCT_INFO", bbossProductInfo);

        // 6- 添加反向受理标记(成员开通工单中的序列号重置不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");

        // 7- 返回结果
        return merchpInfo;
    }

    /*
     * @descripition 拼装成员变更数据(工单开通中的重置序列号与CRM侧发起或者BBOSS发起的重置序列号数据来源不一致，需要区分对待)
     * @author xunyl
     * @date 2013-10-12
     */
    public static IData makeData(String serialNumber, IData userProductInfo, IData map) throws Exception
    {
        // 1- 定义返回结果
        IData result = new DataMap();

        // 2- 添加重置后的序列号
        String imsPassword = StrUtil.getRandomNumAndChar(8);

        // 3- 添加商品信息(走成员变更流程，不需要商品信息)
        IData inparam = new DataMap();
        inparam.put("MERCH_INFO", new DataMap());

        // 4- 添加产品信息
        IData merchpInfo = getProductInfo(serialNumber, userProductInfo, map);
        inparam.put("ORDER_INFO", IDataUtil.idToIds(merchpInfo));

        // 5- 调用变更的基类进行处理
        ChangeBBossMemBean bean = new ChangeBBossMemBean();
        IDataset returnVal = bean.crtOrder(inparam);

        // 6- 返回结果中添加重置后的序列号
        result = returnVal.getData(0);
        result.put("IMS_PASSWORD", imsPassword);

        // 返回结果
        return result;
    }
}
