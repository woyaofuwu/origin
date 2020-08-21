
package com.asiainfo.veris.crm.order.soa.person.busi.userinfobreakimportqry;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo.ManageBlackBean;


public class UserInfoBreakImportQrySVC extends CSBizService{


    /**
	 * 
	 */
    private static final long serialVersionUID = 6620110267234240129L;

    
    /**
     * 批量获取黑名单信息
     * 
     * @param param
     * @return
     * @throws Exception
     * @author weipeng.feng
     * @date 2018-1-12
     */
    public static IDataset batchQryUserServiceState(IData param) throws Exception
    {
    	IDataset serviceStateList = new DatasetList();
    	
    	try {
        	IDataset set = new DatasetList(); // 上传excel文件内容明细
            String fileId = param.getString("cond_STICK_LIST"); // 上传有价卡excelL文件的编号
            String[] fileIds = fileId.split(",");
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
            for (String strfileId : fileIds)
            {
                IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/UesrInfoBreakImport.xml"));
                IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
                set.addAll(suc[0]);
            }
            
            if(IDataUtil.isNotEmpty(set)){
                for (int i = 0; i < set.size(); i++)
                {
                    IData result = new DataMap();
                    result.clear();
                    IData  b=new DataMap();
                    	   b.clear();
                    	   b=set.getData(i);
                    
                    //客户号码
                    if("".equals(b.getString("PSTP_ID"))||b.getString("PSTP_ID")==null){
                    	CSAppException.apperr(CrmCommException.CRM_COMM_1165);
                    }
                    
                /**单个查询    开始**/
                    UserInfoBreakImportQryBean userInfoQryBreakBean=BeanManager.createBean(UserInfoBreakImportQryBean.class);
                    IDataset userInfoList = (IDataset) userInfoQryBreakBean.qryUserInfo(b.getString("PSTP_ID"));
                    if(userInfoList == null || "".equals(userInfoList) || userInfoList.size() == 0){
                    	//CSAppException.apperr(CrmUserException.CRM_USER_344,b.getString("SERIAL_NUMBER"));
                    	IData errNum = new DataMap();
                    	errNum.put("PSTP_ID", b.getString("PSTP_ID"));
                    	errNum.put("REMARK", "用户不存在");
                    	serviceStateList.add(errNum);
                    	continue;
                    }else{
                    	for (int ii = 0; ii < userInfoList.size(); ii++)
                        {
                            IData userInfo = userInfoList.getData(ii);
                            
                            /*userInfo.put("CUST_NAME", userInfo.getString("CUST_NAME"));
                            userInfo.put("PSPT_TYPE_CODE", userInfo.getString("PSPT_TYPE_CODE"));
                            userInfo.put("PSTP_ID", userInfo.getString("PSTP_ID"));
                            userInfo.put("START_DATE", userInfo.getString("START_DATE"));*/
                            serviceStateList.add(userInfo);
                        }
                    	
                    }
                    
                    /**单个查询    结束**/
                    
                }
            	
            }else{
            	//模版为空提示错误
            	CSAppException.apperr(CrmCommException.CRM_COMM_1166);
            	return null;
            }
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
    	
    	 return serviceStateList;    	
    }
	
}