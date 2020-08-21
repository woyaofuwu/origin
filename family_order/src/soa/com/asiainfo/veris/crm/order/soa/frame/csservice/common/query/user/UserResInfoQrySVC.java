
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res.ResParaInfoQry;

public class UserResInfoQrySVC extends CSBizService
{

    public IDataset getResByUserIdResType(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String user_id_a = input.getString("USER_ID_A");
        String res_type_code = input.getString("RES_TYPE_CODE");
        String res_code = input.getString("RES_CODE");
        IDataset output = UserResInfoQry.getResByUserIdResType(user_id, user_id_a, res_type_code, res_code);
        return output;
    }

    /**
     * 根据sql_ref,eparchy_code查询用户资源信息
     * 
     * @data 2013-3-26
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserResByUserId(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String res_type_code = input.getString("RES_TYPE_CODE");
        IDataset output = UserResInfoQry.queryUserSimInfo(user_id, res_type_code);
        return output;
    }

    /**
     * 作用：根据user_id和user_id_a查找这个用户占用的集团资源
     * 
     * @param param
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset getUserResByUserIdA(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String user_id_a = input.getString("USER_ID_A");
        IDataset output = UserResInfoQry.getUserResByUserIdA(user_id, user_id_a);
        return output;
    }

    /**
     * 根据UserId获取用户最近使用的资源信息，复机时使用
     * 
     * @param userId
     * @return
     * @throws Exception
     * @auth liuke
     */
    public IDataset getUserResMaxDateByUserId(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        IDataset output = UserResInfoQry.getUserResMaxDateByUserId(user_id);
        if (IDataUtil.isNotEmpty(output))
        {
            for (int i = 0; i < output.size(); i++)
            {
                IData tempData = output.getData(i);
                String resTypeCode = tempData.getString("RES_TYPE_CODE");
                tempData.put("RES_TYPE_CODE_NAME", ResParaInfoQry.getResTypeNameByResTypeCode(resTypeCode));
            }
        }
        return output;
    }
    /**
     * k3
     * 关于海南电网有限责任公司行业应用卡业务封装接口的需求申请
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getUserOpenInfo(IData data)throws Exception{
    	IDataset rtnResult = new DatasetList();
    	IData rtnMap = new DataMap();
    	 IData params = new DataMap();
    	 IDataUtil.chkParam(data, "SERIAL_NUMBER");
    			 params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
    	         IDataset infos = CSAppCall.call("CS.UserListSVC.queryUserBySn", params);
    	         if (infos == null || infos.size() == 0)
    	         {
    	             CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取用户资料无数据!");
    	         }
    	         if(infos.size() > 0)
    	         {
    	         	data.put("USER_ID", infos.getData(0).getString("USER_ID", ""));
    	         	data.put("CUST_ID", infos.getData(0).getString("CUST_ID", ""));
    	         }
    	         if (StringUtils.isNotBlank(data.getString("USER_ID", "")))
    	         {
    	             data.put("USER_ID", data.getString("USER_ID", ""));
    	             data.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode());
    	             IDataset userInfo = CSAppCall.call("SS.GetUser360ViewSVC.qryUserInfo", data);
    	             IDataset resInfo = CSAppCall.call("SS.GetUser360ViewSVC.qryUserResSimInfo", data);
    	             IData user = new DataMap();
    	             if(IDataUtil.isNotEmpty(userInfo))
    	             {
    	                 //add by duhj 通过调产商品接口,查询产品与名牌名称  2017/03/06
    	                 IData result = CSAppCall.callOne("SS.GetUser360ViewSVC.getUserName",userInfo.getData(0));
    	             	user = userInfo.getData(0);
    	             	user.putAll(result);
    	             	rtnMap.put("SERIAL_NUMBER", user.getString("SERIAL_NUMBER"));
    	             	rtnMap.put("ICC_ID", user.getString("SIM_CARD_NO"));
    	             	rtnMap.put("PRODUCT_NAME", user.getString("PRODUCT_NAME"));
    	             	rtnMap.put("USER_STATE", user.getString("STATE_NAME"));
    	             	rtnMap.put("OPEN_DATE", user.getString("OPEN_DATE"));
    	             	rtnResult.add(rtnMap);
    	             }
    	             if(IDataUtil.isNotEmpty(resInfo)){
    	            	 rtnMap.put("IMSI", resInfo.getData(0).getString("IMSI"));
    	             }
    	       }
         
         return rtnResult;
    }
    /**
     * k3
     * 关于海南电网有限责任公司行业应用卡业务封装接口的需求申请
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset checkGroupInfoQryPermission(IData data)throws Exception{
    	String serial_number = data.getString("SERIAL_NUMBER");
    	String inStaffId = data.getString("IN_STAFFID");
    	IDataset groupInfo = CommparaInfoQry.getCommNetInfo("CSM","6887",inStaffId);
    	IDataset result = new DatasetList();
    	IData rtnMap =new DataMap();
    	Boolean flag=false;
    	if(IDataUtil.isNotEmpty(groupInfo)){
    		String groupSn = groupInfo.getData(0).getString("PARA_CODE1");
    		IDataset userProductInfo = UserProductInfoQry.getUserIdBySn(groupSn);
    		if(IDataUtil.isNotEmpty(userProductInfo)){
    			String userId = userProductInfo.getData(0).getString("USER_ID");
    			IDataset groupInfoRela = RelaUUInfoQry.qryGroupInfoRela(userId, serial_number, "28");
    			if(IDataUtil.isNotEmpty(groupInfoRela)){
    				flag=true;
    			}
    		}
    	}
    	if(flag){
    		rtnMap.put("rtnCode", "0");
    	}else{
    		rtnMap.put("rtnCode", "1");
    	}
    	result.add(rtnMap);
    	return result;
    }
}
