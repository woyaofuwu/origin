package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createRecepHallUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateJKDTUserBean extends GroupOrderBaseBean
{
    // 商品信息
    private IData merchInfo = null;

    // 创建商品的台账信息
    public void actOrderDataOther(IData map) throws Exception
    {

        // 获取商品信息
        merchInfo = this.getMerchInfo();

        // 创建商品的台账信息
        IDataset merchOutDataset = GrpInvoker.ivkProduct(merchInfo, BizCtrlType.CreateUser, "CreateClass");

        // 获取商品台账信息中的用户编号，手机号和帐户编号,用于和子产品创建BB关系
        IData bbossMerChInfo = getBBossMerchInfo(merchOutDataset);

        // 构造产品信息，使其与商品信息的MAP结构一致
        IDataset orderInfo = getOrderInfo();

        //在这里加个口子判断是全网产品还是本省产品

        //因为全网和本省的数据结构不一样,不确定creategroupuser取值的时候有无问题,所以如果是本省产品,把IF_JKDT这个字段设为1,如果是全网不做标记,自己的处理类会单独入那三张表
        //给orderInfo加入是从集客大厅发起的标记 便于后台数据进行判断
        int size = orderInfo.size();
        for(int i=0;i<size;i++){
            orderInfo.getData(i).put("IF_JKDT", "1");//集客大厅发起并且是本省产品才给这个标记
        }

        // 创建子产品的台账信息并且建立BB关系
        createMerchPTdInfo(bbossMerChInfo, orderInfo);
    }

    /**
     *@description 如果是BBOSS管理节点中的集团受理，需要将order表状态改成W，避免aee直接完工
     *@auhtor xunyl
     *@date 2013-12-14
     *@throws Exception
     */
    protected void cmtOrderData_() throws Exception
    {
        IData bbossMerchInfo = merchInfo.getData("BBOSS_MERCH_INFO");
        boolean isManageCreate = bbossMerchInfo.getBoolean("BBOSS_MANAGE_CREATE");

        IDataset orderInfo = getOrderInfo();
        String product_oper_code = orderInfo.getData(0).getString("PRODUCT_OPER_CODE");// 本地商品编码

        if (isManageCreate == true)
        {
            IData map = bizData.getOrder();
            map.put("ORDER_STATE", "W");
        }

        if ("6".equals(CSBizBean.getVisit().getInModeCode()) && "10".equals(product_oper_code))
        {
            IData map = bizData.getOrder();
            map.put("ORDER_STATE", "2");
        }
        // 如果为省行业网关云MAS，并且为反向过来的业务，需要服务开通发指令给省行业网关，经过协商在order表中的APP_TYPE字段中放C。
//        if ("6".equals(merchInfo.getString("IN_MODE_CODE", "")) && ("010101016".equals(merchInfo.getString("PRODUCT_ID")) || "010101017".equals(merchInfo.getString("PRODUCT_ID"))))
//        {
//            IData map = bizData.getOrder();
//            map.put("APP_TYPE", "C");
//        }
        super.cmtOrderData_();
    }

    /*
     * 创建子产品的台账信息并且建立BB关系
     */
    protected void createMerchPTdInfo(IData bbossMerChInfo, IDataset orderInfo) throws Exception
    {
        if (null == orderInfo || orderInfo.size() == 0)
        {
            // 抛出异常
        }
        for (int i = 0; i < orderInfo.size(); i++)
        {
            orderInfo.getData(i).put("OUT_MERCH_INFO", bbossMerChInfo);
            IDataset merchPoutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.CreateUser, "CreateClass");
        }
    }

    /*
     * @desctiption 获取商品台账信息的用户编号，手机号和帐户编号 (non-Javadoc)
     * @see com.ailk.csserv.bean.common.base.OrderBaseBean#setOrderTypeCode()
     */
    protected IData getBBossMerchInfo(IDataset outDataset) throws Exception
    {
        IData bbossMerchInfo = new DataMap();

        for (int i = 0; i < outDataset.size(); i++)
        {
            IData merchOutData = outDataset.getData(i);
            if (merchOutData.containsKey("BBOSS_TAG"))
            {// 说明是BBOSS开户时返回的
                return merchOutData;
            }

        }
        return bbossMerchInfo;
    }

    // 订单类型order_type_code
    protected String setOrderTypeCode() throws Exception
    {
        return "2300";
    }


}