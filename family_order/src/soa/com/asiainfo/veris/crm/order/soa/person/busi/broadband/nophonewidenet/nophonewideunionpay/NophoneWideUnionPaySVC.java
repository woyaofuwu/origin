
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideunionpay;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class NophoneWideUnionPaySVC extends CSBizService
{
	public IData checkPaySerialNumber(IData data) throws Exception{
		data.put("SERIAL_NUMBER", data.getString("PAY_SERIAL_NUMBER"));
		//判断该号码用户信息是否有效
		IDataset userInfos = UserInfoQry.getUserInfoBySn(data.getString("PAY_SERIAL_NUMBER"), "0");
		if (IDataUtil.isEmpty(userInfos) || null == userInfos.getData(0))
        {
			CSAppException.appError("-1", "该号码"+data.getString("PAY_SERIAL_NUMBER")+"没有有效的用户信息！");
        }
		
		//add by zhangxing3 for REQ201810100002开发度假宽带租房套餐 start
		IDataset ids = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("58", userInfos.getData(0).getString("USER_ID"), "1");
		if (IDataUtil.isNotEmpty(ids))
		{
			int i  = ids.size();
			if(i > 20)
			{
				CSAppException.appError("-1", "付费号码统一付费的无手机宽带不能超过20个！");
			}
		}
		//add by zhangxing3 for REQ201810100002开发度假宽带租房套餐 end
		data.put("SERIAL_NUMBER", "KD_"+data.getString("PAY_SERIAL_NUMBER"));
		//判断该号码的宽带账号用户信息是否有效
		IDataset wideUserInfos = UserInfoQry.getUserInfoBySn(data.getString("SERIAL_NUMBER"), "0");
		if (IDataUtil.isEmpty(wideUserInfos) || null == wideUserInfos.getData(0))
        {
			CSAppException.appError("-1", "该用户"+data.getString("PAY_SERIAL_NUMBER")+"尚未开通第一条宽带或第一条宽带未完工，请稍后申请办理！");
        }else{
        	//获取该号码的宽带资料
    		IDataset dataset = WidenetInfoQry.getUserWidenetInfo(wideUserInfos.getData(0).getString("USER_ID"));
            if (IDataUtil.isEmpty(dataset))
            {
            	CSAppException.appError("-1", "根据提供服务号码"+data.getString("PAY_SERIAL_NUMBER")+"未获取到任何宽带资料信息，请确认此用户已办理过宽带业务！");
            }
        }
		IDataset userSvcStates = UserSvcStateInfoQry.getUserMainState(userInfos.getData(0).getString("USER_ID"));
		if(IDataUtil.isNotEmpty(userSvcStates)){
			if(!"0".equals(userSvcStates.getData(0).getString("STATE_CODE"))){
				CSAppException.appError("-1", "该号码"+data.getString("PAY_SERIAL_NUMBER")+"主服务状态不是开通状态，无法办理业务！");
			}
		}
        return data;
	}
	public IData checkNophone(IData data) throws Exception{
		data.put("IS_NOPHONE", "1");
		data.put("IS_CONTINUE", "1");
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("USER_EPARCHY_CODE"));
		IDataset nophoneUserInfos = UserInfoQry.getUserInfoBySn(data.getString("SERIAL_NUMBER"), "0");
		if(IDataUtil.isNotEmpty(nophoneUserInfos) && IDataUtil.isNotEmpty(nophoneUserInfos.getData(0))){
			IDataset uuInfos = RelaUUInfoQry.getRelationsByUserIdA("58", nophoneUserInfos.getData(0).getString("USER_ID"), "1");
			IDataset userSvcState =UserSvcStateInfoQry.getUserMainState(nophoneUserInfos.getData(0).getString("USER_ID", ""));
			if (IDataUtil.isNotEmpty(uuInfos))
			{
				data.put("IS_CONTINUE", "0");
			}
			if(!"N".equals(nophoneUserInfos.getData(0).getString("RSRV_TAG1"))){
				//必须是无手机宽带
				data.put("IS_NOPHONE", "0");
			}
			if (IDataUtil.isNotEmpty(userSvcState))
			{
				String stateCode=userSvcState.getData(0).getString("STATE_CODE", "");
				if(!"0".equals(stateCode)){
					data.put("IS_OPEN", "0");
				}
			}
		}
		return data;
	}
	 public IDataset getNumUcaInfo(IData param) throws Exception
	    {

	        String sn = param.getString("SERIAL_NUMBER");

	        UcaData ucaData = UcaDataFactory.getNormalUca(sn);

	        ucaData.getCustomer().toData();

	        // 获取三户资料

	        IData userInfo = ucaData.getUser().toData();
	        IData custInfo = ucaData.getCustomer().toData();
	        custInfo.putAll(ucaData.getCustPerson().toData());

	        IData result = new DataMap();

	        userInfo.put("PRODUCT_ID", ucaData.getProductId());
	        userInfo.put("BRAND_CODE", ucaData.getBrandCode());

	        result.put("USER_INFO", userInfo);
	        result.put("CUST_INFO", custInfo);

	        userInfo.putAll(param);

	        IDataset dataset = new DatasetList();
	        dataset.add(result);

	        return dataset;
	    }
}
