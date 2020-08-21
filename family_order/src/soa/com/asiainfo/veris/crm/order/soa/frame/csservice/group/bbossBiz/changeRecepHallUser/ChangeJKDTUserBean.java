package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeRecepHallUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class ChangeJKDTUserBean extends GroupOrderBaseBean{
    // 商品信息
    private IData merchInfo = null;

    @Override
    public void actOrderDataOther(IData map) throws Exception
    {
        // 1- 获取商品信息
        merchInfo = map.getData("MERCH_INFO");

        // 2- 创建商品的台账信息
        merchInfo.put("ORDER_ID", getOrderId());
        
        String merchpOperType = merchInfo.getData("GOOD_INFO").getString("MERCH_OPER_CODE");
        String ctrlType = "";
        
        if(GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchpOperType)){
        	ctrlType = BizCtrlType.ChangeUserPast;
        }else if(GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchpOperType)){
        	ctrlType = BizCtrlType.ChangeUserContinue;
        }else if(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue().equals(merchpOperType)){
        	ctrlType = BizCtrlType.ChangeUserChangeDiscnt;
        }else if(GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(merchpOperType)){
        	ctrlType = BizCtrlType.ChangeUserPreDel;
        }else if(GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue().equals(merchpOperType)){
        	ctrlType = BizCtrlType.ChangeUserCanclePreDel;
        }else if(GroupBaseConst.MERCH_STATUS.MERCH_PASTE_MEBFLUX.getValue().equals(merchpOperType)){
        	ctrlType = BizCtrlType.ChangeUserPastMebFlux;
        }else if(GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE_MEBFLUX.getValue().equals(merchpOperType)){
        	ctrlType = BizCtrlType.ChangeUserContinueMebFlux;
        }else if(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue().equals(merchpOperType)){
            ctrlType = BizCtrlType.ChangeUserProAttr;
        }
        else if(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue().equals(merchpOperType)){
            ctrlType = BizCtrlType.ChangeUserProAttr;
        }
        
        IDataset merchOutDataset = GrpInvoker.ivkProduct(merchInfo, ctrlType, "CreateClass");

        // 3- 获取产品信息
        IDataset orderInfo = getOrderInfo();

        // 4- 创建子产品的台账信息
        if (IDataUtil.isNotEmpty(orderInfo))
        {
            createMerchPTdInfo(orderInfo);
        }
    }

    /**
     * 创建子产品的台账信息
     */
    protected void createMerchPTdInfo(IDataset orderInfo) throws Exception
    {
        if (null == orderInfo || orderInfo.size() == 0)
        {
            // 抛出异常
            CSAppException.apperr(CrmUserException.CRM_USER_787);
        }
        for (int i = 0; i < orderInfo.size(); i++)
        {

            // 获取处理标记,调用相应的处理类进行处理
            String dealType = orderInfo.getData(i).getString("DEAL_TYPE");
            orderInfo.getData(i).remove("DEAL_TYPE");

            IDataset merchPoutDataset = new DatasetList();
            if (GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_ADD.getValue().equals(dealType))
            {// 对应产品操作为新增产品

                merchPoutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.CreateUser, "CreateClass");

            }
            else if (GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CANCEL.getValue().equals(dealType))
            {// 对应的产品操作为取消产品订购
                merchPoutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), BizCtrlType.DestoryUser, "CreateClass");
            }
            else if (GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CHANGE.getValue().equals(dealType) || GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE.getValue().equals(dealType))
            {// 对应的产品操作为属性变更、资费变更、产品暂停、恢复，预取消，冷冻期预取消恢复等

            	//获取产品操作类型
                String productOperType = orderInfo.getData(i).getString("PRODUCT_OPER_TYPE");
            	String ctrlType = "";
            	
            	if(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE.getValue().equals(productOperType)){
            		ctrlType = BizCtrlType.ChangeUserPast;
            	}else if(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE.getValue().equals(productOperType)){
            		ctrlType = BizCtrlType.ChangeUserContinue;
            	}else if(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue().equals(productOperType)){
            		ctrlType = BizCtrlType.ChangeUserChangeDiscnt;
            	}else if(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDESTORY.getValue().equals(productOperType)){
            		ctrlType = BizCtrlType.ChangeUserPreDel;
            	}else if(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLEPREDESTORY.getValue().equals(productOperType)){
            		ctrlType = BizCtrlType.ChangeUserCanclePreDel;
            	}else if(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE_MEBFLUX.getValue().equals(productOperType)){
            		ctrlType = BizCtrlType.ChangeUserPastMebFlux;
            	}else if(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE_MEBFLUX.getValue().equals(productOperType)){
            		ctrlType = BizCtrlType.ChangeUserContinueMebFlux;
                }else if(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue().equals(productOperType)){
            	    ctrlType = BizCtrlType.ChangeUserProAttr;
                }
                merchPoutDataset = GrpInvoker.ivkProduct(orderInfo.getData(i), ctrlType, "CreateClass");

            }
        }
    }

    /**
     *@description  如果是BBOSS管理节点中的集团变更，需要将order表状态改成W，避免aee直接完工
     *@auhtor xunyl
     *@date 2013-12-14
     *@throws Exception
     */
    protected void cmtOrderData_() throws Exception
    {
        if(StringUtils.equals("6", CSBizBean.getVisit().getInModeCode()))
        {
            IData bbossMerchInfo = getMerchInfo();
            boolean isManageCreate =bbossMerchInfo.getBoolean("BBOSS_MANAGE_CHANGE");
            if(isManageCreate == true)
            {
                IData map = bizData.getOrder();
                map.put("ORDER_STATE", "W");
            }
        }

        // 如果为省行业网关云MAS，并且为反向过来的业务，需要服务开通发指令给省行业网关，经过协商在order表中的APP_TYPE字段中放L。
        if ("6".equals(getMerchInfo().getString("IN_MODE_CODE", "")) && ("010101016".equals(getMerchInfo().getString("PRODUCT_ID")) || "010101017".equals(getMerchInfo().getString("PRODUCT_ID"))))
        {
            IData map = bizData.getOrder();
            map.put("APP_TYPE", "C");
        }
        super.cmtOrderData_();
    }
    /*
     * @description 处理集团BBOSS成员的的产品变更
     * @date 2013-04-22
     * @author xunyl
     */

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "2300";
    }
}
