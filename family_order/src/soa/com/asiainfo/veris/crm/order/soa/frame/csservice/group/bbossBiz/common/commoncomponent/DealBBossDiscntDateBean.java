
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

public class DealBBossDiscntDateBean
{

    /**
     * 资费生效方式 新增处理流程
     * 
     * @author liuxx3
     * @date 2013-08-07
     */
    private static IDataset dealDiscntAdd(IDataset merchPElements, String userId, String payMode) throws Exception
    {
        // 进入此流程必定是新增操作 故直接判断是立即生效还是下账期生效做出处理
        for (int i = 0; i < merchPElements.size(); i++)
        {

            IData element = merchPElements.getData(i);

            // 该条元素不是资费的话直接进入下次循环
            if (!"D".equals(element.getString("ELEMENT_TYPE_CODE")))
            {
                continue;
            }

            if ("1".equals(payMode))
            {
                element.put("START_DATE", SysDateMgr.getSysTime());
            }
            else if ("2".equals(payMode))
            {
                element.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
            }

        }
        return merchPElements;
    }

    /**
     * 子方法 处理ICB参数
     * 
     * @author liuxx3
     * @date 2013-08-07
     */
    private static IData dealDiscntICBParam(IData merchPElement, IData merchPELementNew, String userId) throws Exception
    {
        // 查询出资料表中的ICB参数信息 并且处理前台信息
        IDataset userICBAttrInfos = UserAttrInfoQry.queryStartDatabyUserAttr(userId, "D", null, null);

        if (IDataUtil.isEmpty(userICBAttrInfos))
        {
            return new DataMap();
        }

        // 取出该条资费的ICB参数信息
        IDataset tradeICBAttrInfos = merchPElement.getDataset("ATTR_PARAM");
        IDataset newTradeICBAttrInfos = merchPELementNew.getDataset("ATTR_PARAM");

        // 取出该条资费的资费编码
        String tradeDiscntCode = merchPElement.getString("ELEMENT_ID");

        IDataset ICBInfos = new DatasetList();

        // 循环1查询该条资费的资料表ICB信息
        for (int i = 0; i < userICBAttrInfos.size(); i++)
        {
            IData userICBAttrInfo = userICBAttrInfos.getData(i);

            String userDiscntCode = userICBAttrInfo.getString("ELEMENT_ID");

            if (!tradeDiscntCode.equals(userDiscntCode))
            {
                continue;
            }

            IData ICBInfo = new DataMap();

            ICBInfo.put("ATTR_CODE", userICBAttrInfo.getString("ATTR_CODE"));
            ICBInfo.put("ATTR_VALUE", userICBAttrInfo.getString("ATTR_VALUE"));

            ICBInfos.add(ICBInfo);
        }

        for (int m = 0; m < tradeICBAttrInfos.size(); m++)
        {

            IData tradeICBAttrInfo = tradeICBAttrInfos.getData(m);

            String tradeAttrCode = tradeICBAttrInfo.getString("ATTR_CODE");

            for (int n = 0; n < ICBInfos.size(); n++)
            {

                IData ICBInfo = ICBInfos.getData(n);
                String AttrCode = ICBInfo.getString("ATTR_CODE");
                String AttrValue = ICBInfo.getString("ATTR_VALUE");

                if (tradeAttrCode.equals(AttrCode))
                {
                    tradeICBAttrInfo.put("ATTR_VALUE", AttrValue);
                    ICBInfos.remove(n);// 需要删除的ICB参数
                }
            }
        }

        tradeICBAttrInfos.addAll(ICBInfos);
        newTradeICBAttrInfos.addAll(ICBInfos);

        return merchPElement;
    }

    /**
     * 将资费绑定按套餐生效方式封装
     * 
     * @author liuxx3
     * @date 2013-08-07
     */
    public static IDataset dealDiscntStartDate(IDataset merchPElements, IData productInfo, String payMode) throws Exception
    {
        // 1.判断传递进来的元素信息是否为空 为空则直接返回
        if (IDataUtil.isEmpty(merchPElements))
        {
            return merchPElements;
        }
        /*
         * 2.从传递进来的productinfo 中 不管是商品级的信息 merch 还是产品级的信息merchp 都要取出userid 判断是否userid为空 或者有无userid 没有userid或者userid为空
         * 说明是商品的集合或者是新增操作 如有user_id则为变更操作 进入不同的处理流程
         */
        String userId = productInfo.getString("USER_ID", "");

        IDataset newElements = new DatasetList();

        if (StringUtils.isEmpty(userId))
        {
            newElements = dealDiscntAdd(merchPElements, userId, payMode);
        }
        else
        {
            newElements = dealDiscntUpdate(merchPElements, userId, payMode);
        }
        return newElements;
    }

    /**
     * 资费生效方式 变更处理流程
     * 
     * @author liuxx3
     * @date 2013-08-07
     */
    private static IDataset dealDiscntUpdate(IDataset merchPElements, String userId, String payMode) throws Exception
    {
        // 循环传递进来的元素集合
        IDataset newElements = new DatasetList();

        for (int i = 0; i < merchPElements.size(); i++)
        {
            IData merchPElement = merchPElements.getData(i);

            // 该条元素不是资费的话直接进入下次循环
            if (!"D".equals(merchPElement.getString("ELEMENT_TYPE_CODE")))
            {
                newElements.add(merchPElement);
                continue;
            }

            String modifyTag = merchPElement.getString("MODIFY_TAG");

            // 3.1新增操作
            if ("0".equals(modifyTag))
            {
                // 判断是否立即生效还是下账期生效
                if ("1".equals(payMode))
                {
                    merchPElement.put("START_DATE", SysDateMgr.getSysTime());
                    newElements.add(merchPElement);
                }
                else if ("2".equals(payMode))
                {
                    merchPElement.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
                    newElements.add(merchPElement);
                }

            }
            // 3.2删除操作
            else if ("1".equals(modifyTag))
            {

                // 判断是否立即生效还是下账期生效
                if ("1".equals(payMode))
                {
                    merchPElement.put("END_DATE", SysDateMgr.getSysTime());
                    newElements.add(merchPElement);
                }
                else if ("2".equals(payMode))
                {
                    merchPElement.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                    newElements.add(merchPElement);
                }

            }
            // 修改
            else if ("2".equals(modifyTag))
            {

                // 克隆一条一样的前台信息
                IData merchPELementNew = (IData) Clone.deepClone(merchPElement);
                merchPELementNew.put("MODIFY_TAG", "0");

                // 处理原前台merchPElement
                dealDiscntICBParam(merchPElement, merchPELementNew, userId);
                merchPElement.put("MODIFY_TAG", "1");

                // 判断是否立即生效还是下账期生效
                if ("1".equals(payMode))
                {
                    merchPElement.put("END_DATE", SysDateMgr.getSysTime());
                    merchPELementNew.put("START_DATE", SysDateMgr.getSysTime());
                    newElements.add(merchPElement);
                    newElements.add(merchPELementNew);

                }
                else if ("2".equals(payMode))
                {
                    merchPElement.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                    merchPELementNew.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.START_DATE_FOREVER);
                    newElements.add(merchPElement);
                    newElements.add(merchPELementNew);
                }
            }
        }

        return newElements;
    }

}
