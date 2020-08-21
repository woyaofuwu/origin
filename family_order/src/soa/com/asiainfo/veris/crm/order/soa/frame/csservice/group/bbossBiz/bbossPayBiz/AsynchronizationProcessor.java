package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossPayBiz;


import com.ailk.biz.BizEnv;
import com.ailk.biz.BizVisit;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.client.ServiceFactory;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public abstract class AsynchronizationProcessor extends Thread
{
	/**
     * 传入参数
     */
    private IData param = new DataMap();
    
    private BizVisit visit = null;
    
    private Object peek = null;
	
	/**
     * @param param 传入参数
     */

	public AsynchronizationProcessor(IData param, BizVisit visit,Object service) {
        
        this.param = param;
        this.visit = visit;        
        this.peek = service;
    }

    @Override
    public void run() {

    	  SessionManager manager = SessionManager.getInstance();

    	  try {
    	    // 激活会话,并设置上下文对象
    	    manager.start();
    	    manager.setContext(this.peek, this.visit);

    	   //todo 这里可以调服务
    	    this.rigisitXmlInfo(param);
    	    
    	    manager.commit();
    	  } catch (Exception e) {
    	    try {
				manager.rollback();
				e.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
    	  } finally {
    	    try {
				manager.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	  }

    }
    
    
    /**
     * @description 流量叠加包拆单入表
     * @author xunyl
     * @date 2016-02-21
     */
    private void rigisitXmlInfo(IData param)throws Exception {
        //1-  获取服务号码
        IDataset member_numberlist = IDataUtil.getDatasetSpecl("MEMBER_NUMBER", param);
        
        //2- 获取订购流量叠加包
        IDataset member_order_ratelist = IDataUtil.getDatasetSpecl("MEMBER_ORDER_RATE", param);
        
        //3 循环拆单
        for (int i = 0, sizeI = member_numberlist.size(); i < sizeI; i++)
        {
            IData singleMemberInfo = (IData) Clone.deepClone(param);            
            singleMemberInfo.put("SERIAL_NUMBER", member_numberlist.get(i).toString());
            singleMemberInfo.put("MEMBER_ORDER_RATE", member_order_ratelist.get(i).toString());
            
            //鉴权处理，鉴权失败，直接登记xml_info表为2状态，gtm不再扫描处理
            singleMemberInfo.put("DEAL_STATE", "0");//0代表等待处理
            baseMemCheckForPayBiz(singleMemberInfo);   
            
            CSAppCall.call("CS.bbossCenterControlSVC.rigisitXmlInfo",singleMemberInfo);
        }                
    }
    
    /**
     * @description 成员基本校验
     * @author xunyl
     * @date 2015-09-23
     */
    private static void baseMemCheckForPayBiz(IData data)throws Exception{
        //1- 校验成员用户是否存在        
        IData inparam = (IData) Clone.deepClone(data);       
        inparam.put("REMOVE_TAG", "0");        
        String url = BizEnv.getEnvString("service.router.addr", "");
        IDataInput idataInput = new DataInput();
        idataInput.getData().putAll(inparam);
        IData headData = new DataMap();
        headData.put("STAFF_EPARCHY_CODE", "0898");
        headData.put("LOGIN_EPARCHY_CODE", "0898");
        headData.put("STAFF_ID", "IBOSS000");
        headData.put("DEPART_ID", "00309");
        headData.put("IN_MODE_CODE", "6");
        idataInput.getHead().putAll(headData);
        IDataOutput idataOutput =ServiceFactory.call(url,"CS.UserInfoQrySVC.getUserInfoBySnNoProduct",idataInput,null, false, true);
        IDataset memberUserInfoList = idataOutput.getData();           
        if (IDataUtil.isEmpty(memberUserInfoList))
        {
            data.put("OPEN_RESULT_CODE", "02");
            data.put("OPEN_RESULT_DESC", "用户状态不正常");
            data.put("DEAL_STATE", "2");//2代表处理完成
            return ;
        }
        
        //3- 校验用户叠加包套餐ID是否存在getBizAttr
        String memOrderRate = data.getString("MEMBER_ORDER_RATE","");
        String productSpecNumber = data.getString("PRODUCT_SPEC_NUMBER", "");                    
        idataInput.getData().put("ID", "1");
        idataInput.getData().put("ID_TYPE", "B");
        idataInput.getData().put("ATTR_OBJ","PRO");
        idataInput.getData().put("ATTR_VALUE",productSpecNumber);
        idataOutput =ServiceFactory.call(url,"CS.AttrBizInfoQrySVC.getBizAttrByAttrValue",idataInput,null, false, true);
        IDataset attrBizInfoList= idataOutput.getData();           
        if(IDataUtil.isEmpty(attrBizInfoList)){
            return;
        }
        String productId = attrBizInfoList.getData(0).getString("ATTR_CODE","");                
        idataInput.getData().put("ID", productId);
        idataInput.getData().put("ID_TYPE", "D");
        idataInput.getData().put("ATTR_OBJ","FluxPay");
        idataInput.getData().put("ATTR_CODE",memOrderRate);
        idataOutput =ServiceFactory.call(url,"CS.AttrBizInfoQrySVC.getBizAttr",idataInput,null, false, true);
        IDataset discntInfos= idataOutput.getData(); 
        
        if (IDataUtil.isEmpty(discntInfos))
        {             
            data.put("OPEN_RESULT_CODE", "04");
            data.put("OPEN_RESULT_DESC", "用户叠加包套餐ID错误");
            data.put("DEAL_STATE", "2");//2代表处理完成
            return ;
        }  
        
        //4-判断是否是国际流量统付,是否开通国际漫游或国际漫游数据功能
        if("99910".equals(productSpecNumber)){
        	//String routeId = RouteInfoQry.getEparchyCodeBySnForCrm(data.getString("SERIAL_NUMBER"));
        	String memberUserId = memberUserInfoList.getData(0).getString("USER_ID");
        	String routeId = memberUserInfoList.getData(0).getString("EPARCHY_CODE");
        	//该处判断，湖南比较特殊，可以通过  19 和 89 服务 都可以判断是否开通国际漫游，其他省份只可以通过 19 服务去判断
        	inparam.clear();
        	inparam.put("USER_ID", memberUserId);
            inparam.put("SERVICE_ID", "19");
            inparam.put(Route.ROUTE_EPARCHY_CODE, routeId);
        	IDataset userSvc19 = CSAppCall.call("CS.UserSvcInfoQrySVC.qryUserSvcByUserSvcId",inparam); 
        	if(IDataUtil.isEmpty(userSvc19)){
        		data.put("OPEN_RESULT_CODE", "06");
            	data.put("OPEN_RESULT_DESC", "未开通国际漫游或国际漫游数据功能");
            	data.put("DEAL_STATE", "2");//2代表处理完成
            	return;
        	}
        }
    }
}
