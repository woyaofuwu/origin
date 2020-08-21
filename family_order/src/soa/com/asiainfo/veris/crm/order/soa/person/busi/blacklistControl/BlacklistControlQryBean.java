
package com.asiainfo.veris.crm.order.soa.person.busi.blacklistControl;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;


public class BlacklistControlQryBean extends CSBizBean{
	
	 //查询方法
	 public IDataset qryUserInfo(IData param,Pagination pagination) throws Exception{
		 
		 System.out.println("erererere"+pagination);
		 return Dao.qryByCodeParser("TF_F_VOLTE_BRANK_USER", "QUE_VOLTE_BRANK_USER", param,pagination);
	 }
	 //新增方法
	 public IData insertUserData(IData param) throws Exception{
		 
	 	IData data = new DataMap();
	 	IData dataTag = new DataMap();
	    data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
	    //查询黑名单表判断表中数据是否已经存在
	    IDataset dataNum = Dao.qryByCode("TF_F_VOLTE_BRANK_USER", "QRE_USER_SERIAL", data);
	    if(DataUtils.isNotEmpty(dataNum)){
	    	 CSAppException.apperr(CrmCommException.CRM_COMM_1188);
	    }else{
	    	//查询用户id
		    IDataset datas = Dao.qryByCode("TF_F_USER", "QRE_USER_ID", data);
		    if(DataUtils.isEmpty(datas)){
		    	 CSAppException.apperr(CrmCommException.CRM_COMM_1181);
		    }else{
		    	//根据手机号码查询user_id
		    	IData idata = datas.getData(0);
			    String userId = idata.getString("USER_ID");
			    param.put("USER_ID", userId);
		    	Dao.executeUpdateByCodeCode("TF_F_VOLTE_BRANK_USER", "INS_VOLTE_BRANK_USER", param);
		    	dataTag.put("SUCCESS", "新增黑名单成功");
		    }
	    }
	    return dataTag;
	 }
	 //删除的方法
	 public void delBlackUserData(IData params) throws Exception{
		 IDataset dataset = new DatasetList();
		 String[] pstpId = params.getString("monitorInfoCheckBox").split(",");
		 for (int i = 0; i < pstpId.length; i++){
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", pstpId[i]);
            dataset.add(param);
        }
		 Dao.executeBatchByCodeCode("TF_F_VOLTE_BRANK_USER", "DEL_VOLTE_BRANK_USER", dataset);
	 }
	 //批量新增batInsertUserData
	 public IData batInsertUserData(IData param) throws Exception{
        IData result = checkImportFile(param);
        IDataset successes = result.getDataset("SUCCESS");
        if (successes.size() > 0) {
                importDealAdd(successes); // 批量新增
        } else {
            CSAppException.apperr(BatException.CRM_BAT_6);
        }
        System.out.println(""+result.getString("SUCCESS"));
        System.out.println("defrg"+result.getDataset("SUCCESS").size()+result.getDataset("FAILED").size());
        return result;
	 }
	 
	 private IData checkImportFile(IData params) throws Exception {
		 
        IDataset succds = new DatasetList();
        IDataset faileds = new DatasetList();
        IData fileData = params.getData("FILEDATA");
        IDataset[] datasets = (IDataset[]) fileData.get("right");
        System.out.println("swswswsw"+datasets);
        for (int i = 0; i < datasets.length; i++) {
            IDataset dataset = datasets[i];
            System.out.println("swswfdfdswsw"+dataset);
            System.out.println("swswfdfdswsw"+datasets.length);
            if (IDataUtil.isEmpty(dataset)) {
                CSAppException.apperr(BatException.CRM_BAT_20);
            }
            if (dataset.size() > 1000) {
                CSAppException.apperr(BatException.CRM_BAT_21, "1000");
            }
            for (int j = 0; j < dataset.size(); j++) {
            	
                IData data = dataset.getData(j);
                System.out.println("swswfdfdxdwsfrfrwsw"+data+j);
	            System.out.println("swswfdfddsdssdeddwsw"+dataset.size());
                String serialNum = data.getString("SERIAL_NUMBER", "");
                System.out.println("rewrwrw"+j);
                if (StringUtils.isEmpty(serialNum)) {
                    data.put("REMARK", data.getString("REMARK") + "||错误描述：手机号码不能为空！");
                    faileds.add(data);
                    dataset.remove(j);
                    continue;
                }
                //查询黑名单表判断表中数据是否已经存在
    		    IDataset dataSerialNum = Dao.qryByCode("TF_F_VOLTE_BRANK_USER", "QRE_USER_SERIAL", data);
    		    if(DataUtils.isNotEmpty(dataSerialNum)){
    		    	System.out.println("fdfsdfaadweedeadsfd"+dataSerialNum);
    		    	data.put("REMARK", data.getString("REMARK") + "||错误描述：手机号码已存在！");
                    faileds.add(data);
                    dataset.remove(j);
                    j--;
                    continue;
    		    }
    		    //查询用户id
    		    IData dataNum = new DataMap();
                dataNum.put("SERIAL_NUMBER", serialNum);
			    IDataset dataId = Dao.qryByCode("TF_F_USER", "QRE_USER_ID", dataNum);
			    if(DataUtils.isEmpty(dataId)){
			    	data.put("REMARK", data.getString("REMARK") + "||错误描述：手机号码不合法！");
                    faileds.add(data);
                    dataset.remove(j);
                    j--;
                    continue;
			    }else{
			    	IData idata = dataId.getData(0);
	    		    String userId = idata.getString("USER_ID");
	    		    if(StringUtils.isNotBlank(userId)){
	    		    	data.put("USER_ID", userId);
	    		    }else{
	    		    	data.put("REMARK", data.getString("REMARK") + "||错误描述：手机号码没有对应的userId！");
	                    faileds.add(data);
	                    dataset.remove(j);
                        j--;
	                    continue;
	    		    	 
	    		    }
			    }
    		    
                if (dataset.size() > 1) {//如果是多条数据导入，这里需要判断是否有重复的数据
                    for (int k = j + 1; k < dataset.size(); k++) {
                        if (StringUtils.equals(serialNum, dataset.getData(k).getString("SERIAL_NUMBER")) ) {
                            dataset.getData(k).put("REMARK", data.getString("REMARK") + "||错误描述：文件中存在重复的号码!");
                            faileds.add(dataset.getData(k));
                            //如果存在重复的号码的话，就将这条重复的号码删除
                            dataset.remove(k);
                            k--;
                            //跳出上级的本次循环，继续下次循环
                            continue;
                        }
                    }
                }
                //数据解析校验通过后，将数据封装
                data.put("SERIAL_NUMBER", serialNum);
                data.put("UPDATE_TIME", SysDateMgr.getSysDate());
                data.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                succds.add(data);
            }
        }
        IData result = new DataMap();
        result.put("SUCCESS", succds);
        result.put("FAILED", faileds);
        return result;
	 }
	 
	 private void importDealAdd(IDataset dataset) throws Exception {
		 IDataset addParams = new DatasetList();
		 if(IDataUtil.isNotEmpty(dataset) && dataset.size() > 0){		 
			 	
		        for (int i = 0; i < dataset.size(); i++) {
		            IData data = dataset.getData(i);
		            System.out.println("rexsxsrer"+dataset.size());
		            // 构建插入数据库的数据
		            IData param = new DataMap();		            
	                param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER").trim());
	                param.put("UPDATE_TIME", data.getString("UPDATE_TIME"));
	                param.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID"));
	                param.put("USER_ID", data.getString("USER_ID"));
		            addParams.add(param);
		      } 
		        Dao.insert("TF_F_VOLTE_BRANK_USER", addParams, Route.getCrmDefaultDb());

		 }

	 }
	 
}
