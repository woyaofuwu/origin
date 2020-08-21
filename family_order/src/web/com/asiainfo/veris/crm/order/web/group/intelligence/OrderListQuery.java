package com.asiainfo.veris.crm.order.web.group.intelligence;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IRequestCycle;

public abstract class OrderListQuery extends GroupBasePage {
    private static final String BUSI_ABILITY_CODE = "BUSI_ABILITY_CODE";
    /**
     * 初始化页面信息
     * @author chenpeng5
     * @throws Exception
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData initInfo = new DataMap();
        // 查询用户信息
        IDataset typeList = new DatasetList();
        IData typeData = new DataMap();

        typeData.put("QUERY_CODE","1");
        typeData.put("QUERY_TYPE","[1] 客户姓名");
        typeList.add( typeData );

        typeData = new DataMap();
        typeData.put("QUERY_CODE","2");
        typeData.put("QUERY_TYPE","[2] 客户证件号码");
        typeList.add( typeData );

        typeData = new DataMap();
        typeData.put("QUERY_CODE","3");
        typeData.put("QUERY_TYPE","[3] 订单编号");
        typeList.add( typeData );

        typeData = new DataMap();
        typeData.put("QUERY_CODE","4");
        typeData.put("QUERY_TYPE","[4] 业务受理号码");
        typeList.add( typeData );
        initInfo.put("TYPE_LIST", typeList);

        setInitInfo( initInfo );
    }
    public abstract void setInitInfo( IData initInfo ) ;
    public abstract void setInfos( IData orderInfo ) ;
    public abstract void setContractInfo( IData orderInfo ) ;
    public abstract void setOrderInfos( IDataset orderInfo ) ;
    public abstract void setGoodInfos( IDataset goodInfos ) ;
    public abstract void setOrderItemInfo( IData orderInfo ) ;
    public abstract void setCustInfo( IData groupInfo ) ;
    public abstract void setInfosCount( long infosCount );

    /**
     *
     *  统一使用接口 入参 CS.BusinessAbilitySVC.callBusinessCenterCommon
     *  SS.OrderTransparencySVC.customInfoQuery 查询客户资料接口 入参 CUST_ID
     *  查询订单和客户资料信息
     * @param cycle
     * @throws Exception
     */
    public void queryListInfo(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        String groupId = data.getString("GROUP_ID");
        IData groupInfo = new DataMap() ;
        if(StringUtils.isNotBlank( groupId ) ){
            //获取集团资料 需要交验下 当前CUST_ID 和 订单CUST_ID一致
            IData param = new DataMap();
            param.put("customerId",groupId);
            groupInfo = CSViewCall.callone(this, "SS.OrderTransparencySVC.customInfoQuery", param);
            groupInfo.put("HAS_GROUP_INFO","true");
            setCustInfo( groupInfo );
        }
        IDataset orderList = CSViewCall.call(this, "SS.OrderTransparencySVC.orderListQuery", data)
                .first().getDataset("orderList");
        setOrderInfos( dealShowOrders( orderList ,getPagination("olcomnav")) );
        setInfosCount( orderList.size() );
//        if(IDataUtil.isNotEmpty( groupInfo )){
//           String customerID = groupInfo.getString("customerID");
//            for (Object obj : orderList) {
//                IData temp = (IData)obj;
//                String custID = temp.getString("customerCode");
//                if(! customerID.equals( custID ) ){
//                    CSViewException.apperr( GrpException.CRM_GRP_713,"查询的订单信息有和当!" );
//                }
//            }
//        }
        setAjax( groupInfo );
    }

    /**
     * 查询订单信息
     * @param cycle
     * @throws Exception
     */
    public void orderInfoQuery(IRequestCycle cycle ) throws Exception {
        IData data = this.getData();
        IData orderInfo = CSViewCall.callone(this, "SS.OrderTransparencySVC.orderInfoQuery", data);
        setInfos( orderInfo );
    }

    /**
     * 获取合同详情
     * @param cycle
     * @throws Exception
     */
    public void getContractInfoByContractId(IRequestCycle cycle ) throws Exception {
        IData data = this.getData();
        IData contractInfo = CSViewCall.callone(this, "SS.OrderTransparencySVC.contractItemInfoQuery", data);
//        contractInfo.put("contractFile",contractInfo.getDataset("contractFileList").first());
        setContractInfo( contractInfo );
    }

    /**
     * 获取订单行
     * @param cycle
     * @throws Exception
     */
    public void getOrderItemByOrderId(IRequestCycle cycle ) throws Exception {
        IData data = this.getData();
        IData orderItemInfo = CSViewCall.callone(this, "SS.OrderTransparencySVC.orderItemInfoQuery", data);
        setOrderItemInfo( orderItemInfo );
    }

    /**
     * 获取产品列表
     * @param cycle
     * @throws Exception
     */
    public void getGoodList(IRequestCycle cycle ) throws Exception {
        IData data = this.getData();
        IDataset goodList = CSViewCall.call(this, "SS.OrderTransparencySVC.offerInfoQuery", data);
        setGoodInfos( goodList );
    }

    /**
     * 展示
     * @param orderList
     * @param page
     * @throws Exception
     */
    private IDataset dealShowOrders(IDataset orderList , Pagination page) throws Exception {
        int size = page.getPageSize();
        IDataset retList = new DatasetList();
        for (int i = 0; i <  orderList.size() ; i++) {
            if( i >= size ){
                break;
            }
            retList.add( orderList.getData(i) );
        }

        return retList;
    }

}
