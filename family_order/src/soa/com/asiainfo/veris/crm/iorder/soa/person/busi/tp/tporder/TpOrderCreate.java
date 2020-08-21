package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporder;

import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporderdetail.TpOrderDetailBean;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporderoper.TpOrderOperBean;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporderrel.TpOrderRelBean;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts.DealType;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts.OperType;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts.OrderState;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts.SecConfirm;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts.State;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class TpOrderCreate extends CSBizService {
    /**
     * @Description: 创建甩单接口，插入甩单主表，甩单详情表，甩单操作表
     * @Param input：包括号码，要甩单的业务类型，规则列表（IDataset其中包括报文）
     * @return void
     * @version: v1.0.0
     * @author: yangxm3@asiainfo.com
     * @date: 2020/7/18 15:33
     */
    public String orderCreate(IData input) throws Exception{
        TpOrderBean tpOrderBean = BeanManager.createBean(TpOrderBean.class);
        TpOrderDetailBean tpOrderDetailBean = BeanManager.createBean(TpOrderDetailBean.class);
        TpOrderOperBean tpOrderOperBean = BeanManager.createBean(TpOrderOperBean.class);
        
        String orderId = SeqMgr.getTpOrderId();
        
        IDataset addParams = new DatasetList();

        //组装甩单主表参数
        IData orderParam = creatOrderData(input,orderId);
        tpOrderBean.insertOrder(orderParam);//插入甩单主表

        //组装甩单详情表参数
        IData param = createOrderDetailData(input,orderId);
        addParams.add(param);
        tpOrderDetailBean.batInsertOrderDetail(addParams);//插入甩单详情表

        //组装甩单操作表参数
        IData operParam = creatOrderOper(orderId,OperType.creatTpOrder);
        tpOrderOperBean.insertOrderOper(operParam);//插入甩单操作表
        return orderId;
    }

    private IData creatOrderData(IData input,String orderId) throws Exception{
    	String serialNumber = input.getString(TpConsts.comKey.serialNumber);//获得受理号码
    	String custName = input.getString(TpConsts.comKey.custName);//获得客户姓名（非必填）
    	String inTradeTypeCode = input.getString(TpConsts.comKey.tradeTypeCode);//获得要甩单的业务类型
    	String eventType = input.getString("WITH_TYPE");//0：规则 1：业务
    	String eventObj = input.getString("EVENT_OBJ");
    	String soType = input.getString("SO_TYPE");
    	String authType = input.getString("AUTH_TYPE");
    	String channelId = input.getString("CHANNEL_ID");
    	String isAudit = input.getString("IS_AUDIT");
    	String isAuto = DealType.auto.equals(input.getString("DEAL_TYPE"))?"0":"1";
    	String isSplit = input.getString("IS_SPLIT");
    	String dealAction = input.getString("DEAL_ACTION");
    	
    	//校验必填
    	if(StringUtils.isEmpty(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40004);
    	}
    	if(StringUtils.isEmpty(custName)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40005);
    	}
		if(StringUtils.isEmpty(inTradeTypeCode)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40006);
		}
		if(StringUtils.isEmpty(eventType)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40007);
		}
		if(StringUtils.isEmpty(eventObj)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40008);
		}
		if(StringUtils.isEmpty(channelId)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40009);
		}
    	
    	IData orderParam = new DataMap();
        orderParam.put("ORDER_ID",orderId);
        orderParam.put("SERIAL_NUMBER",serialNumber);
        orderParam.put("CUST_NAME",custName);
        orderParam.put("IN_TRADE_TYPE_CODE",inTradeTypeCode);
        orderParam.put("EVENT_TYPE", eventType);
        orderParam.put("EVENT_OBJ", eventObj);
        orderParam.put("SO_TYPE", soType);
        orderParam.put("AUTH_TYPE", authType);
        orderParam.put("CHANNEL_ID", channelId);
        orderParam.put("ORDER_STATE",OrderState.TPORDER_STATE_0);//工单状态 待确定
        orderParam.put("IS_AUDIT",isAudit);//是否审核 0：否 1：是
        orderParam.put("IS_SEC_CONFIRM",SecConfirm.SEC_CONFIRM_0);//是否二次确认 0：否 1：是
        orderParam.put("IS_AUTO",isAuto);//是否自动处理 0：否 1：是
        orderParam.put("IS_SPLIT",isSplit);//是否拆单 0：否 1：是
        orderParam.put("FEE_STATE","0");//费用状态 待确定
        orderParam.put("PRESS_COUNT","0");//催单次数
        orderParam.put("STATE","0");//状态 待确定
        orderParam.put("CREATE_DATE", SysDateMgr.getSysDate());
        orderParam.put("EFFECTIVE_DATE", SysDateMgr.getSysDate());
        orderParam.put("OP_ID", getVisit().getStaffId());
        orderParam.put("ORG_ID", getVisit().getDepartId());
        orderParam.put("REGION_ID",getVisit().getCityCode());
        orderParam.put("DEAL_ACTION",dealAction);
        return orderParam;
    }

    public IData creatOrderOper(String orderId,String operType) throws Exception{
        IData operParam = new DataMap();
        operParam.put("ORDER_ID",orderId);
        operParam.put("OPER_ID",SeqMgr.getTpOrderOperId());
        operParam.put("OPER_TYPE",operType);//操作类型 待确定
        operParam.put("CREATE_DATE", SysDateMgr.getSysDate());
        operParam.put("DONE_DATE", SysDateMgr.getSysDate());
        operParam.put("OP_ID", getVisit().getStaffId());
        operParam.put("ORG_ID", getVisit().getDepartId());
        return operParam;
    }

    private IData createOrderDetailData(IData rule,String orderId)throws Exception{
        // 构建插入甩单详情表的数据
        IData param = new DataMap();
        param.put("ORDER_ID",orderId);
        param.put("DETAIL_ID",SeqMgr.getTpOrderDetailId());
        param.put("REMARKS",rule.getString("RULE_DETAIL"));//规则
        String message = rule.getString("MESSAGE"); //获得甩单规则报文，每2000字符分割插入表对应字段
        int len = message.length();
        int temp = 2000;
        int start = 0;
        int count = 1;
        while(start<len-temp){
            String content = message.substring(start,start+temp);
            param.put("BUSI_CONTENT_"+count,content);
            start = start+temp;
            count++;
        }

        if (count>10){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"报文超长");
        }

        if(start<len){
            String content = message.substring(start);
            param.put("BUSI_CONTENT_"+count,content);
        }

        param.put("CREATE_DATE", SysDateMgr.getSysDate());
        param.put("OP_ID", getVisit().getStaffId());
        param.put("ORG_ID", getVisit().getDepartId());
        param.put("STATE", "0");//待确定
        param.put("REGION_ID",getVisit().getCityCode());
        return param;
    }
    /**
     * @Description: 根据甩单工单号和销户单号（携转单号）插入关系表TP_ORDER_REL
     * @Param orderId：甩单单号；orderIdA：销户单号（携转单号）；aType：orderIdA的类型
     * @return void
     * @version: v1.0.0
     * @author: yangxm3@asiainfo.com
     * @date: 2020/7/18 15:33
     */
    public void insertOrderRel(String orderIdA,String aType,String orderIdB,String bType,String relType)throws Exception{
        TpOrderRelBean tpOrderRelBean = BeanManager.createBean(TpOrderRelBean.class);
        //校验甩单工单号是否存在
        checkTpOrderExist(orderIdA,aType);
        checkTpOrderExist(orderIdB,bType);

        //记录订单关系
        IDataset addRelParams = new DatasetList();
        String relId = SeqMgr.getTpOrderRelId();
        IData param = new DataMap();
        param.put("REL_ID",relId);
        param.put("ORDER_ID_A",orderIdA);
        param.put("ORDER_A_TYPE",aType);
        param.put("ORDER_ID_B",orderIdB);
        param.put("ORDER_B_TYPE",bType);
        param.put("REL_TYPE",relType);
        param.put("STATE",State.vaild);
        param.put("CREATE_DATE", SysDateMgr.getSysDate());
        param.put("EFFECTIVE_DATE", SysDateMgr.getSysDate());
        param.put("OP_ID", getVisit().getStaffId());
        param.put("ORG_ID", getVisit().getDepartId());
        param.put("REGION_ID",getVisit().getCityCode());
        addRelParams.add(param);
        tpOrderRelBean.batInsertOrderRel(addRelParams);//插入甩单关系表
    }

    /**
     * 修改甩单主表状态
     * @param orderId
     * @param state
     * @throws Exception
     */
    public void updateTpOrder(String orderId,String state)throws Exception{
        TpOrderBean tpOrderBean = BeanManager.createBean(TpOrderBean.class);
        tpOrderBean.updateTpOrder(orderId,state);
    }

    /**
     * 记录关系时如果orderId是甩单工单的则校验甩单工单是否存在
     * @param orderId
     * @param orderABType
     * @throws Exception
     */
    private void checkTpOrderExist(String orderId,String orderABType)throws Exception{
        TpOrderBean tpOrderBean = BeanManager.createBean(TpOrderBean.class);
        if(TpConsts.OrderABType.tpOrder.equals(orderABType)){
            IData data = new DataMap();
            data.put("ORDER_ID",orderId);
            IDataset orderInfos = tpOrderBean.queryTpOrderInfos(data, null);
            if(IDataUtil.isEmpty(orderInfos)||orderInfos.size()<1){
                CSAppException.apperr(TpOrderException.TP_ORDER_400010,orderId);
            }
        }
    }

}
