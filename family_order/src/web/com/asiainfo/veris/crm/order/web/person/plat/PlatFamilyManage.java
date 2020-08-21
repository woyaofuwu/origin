
package com.asiainfo.veris.crm.order.web.person.plat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class PlatFamilyManage extends PersonBasePage
{
    public abstract void setFamilyMember(IDataset familyMember);
    public abstract void setFamilyMemberinfo(IData familyMemberinfo);
    public abstract void setCustInfo(IData CustInfo);
    
    
    
    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset dataset = new DatasetList();

        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        data.put("USER_EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        IData param = new DataMap();
        param.put("USER_ID",userInfo.getString("USER_ID"));
        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        param.put("STATUS", "1");
        
        IDataset datas = CSViewCall.call(this, "SS.QryPlatSVC.getFamilyCircle", param);

        // 查询子业务信息
        setFamilyMember(datas);

        IDataset custInfos = null;
      /*  IData params = new DataMap();
        params.put("X_GETMODE", 0);
        params.put("SERIAL_NUMBER", condParams.getString("SERIAL_NUMBER", ""));
        // 查询用户信息
        IDataset userInfos = CSViewCall.call(this, "SS.QueryInfosSVC.getUserInfo", params);
        params.clear();
        if (null != userInfos && userInfos.size() > 0)
        {*/
        // 查询客户信息
        param.clear();
        // params.put("X_GETMODE", 8);//根据服务号码获取客户信息
        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        param.put("CUST_ID", userInfo.getString("CUST_ID"));
        custInfos = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryPerInfoByCustId", param);
        if (custInfos != null && custInfos.size() > 0)
        {
            setCustInfo(custInfos.getData(0));
        }
        //}
        
        //查询成功
        this.setAjax("FLAG", "true");
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
		IData inparam = getData();
		
		//获取对象
		String familyMembers = inparam.getString("FAMILY_CIRCLE_LIST");
		String[] fm = familyMembers.split("\\|");
		List family = new ArrayList();
		
		StringBuffer err_msisdn = new StringBuffer("");
		boolean flag = false;
		for(int i = 0 ; i < fm.length ; i++){
			IData fc = new DataMap();
			String[] fs = fm[i].split("\\^");
			if(fs[0].equals(inparam.getString("SERIAL_NUMBER"))){
				if (!"".equals(fs[0])){
					fc.put("MAIN_MSISDN", fs.length >= 1 ? fs[0] : "");
					fc.put("TARGET_MSISDN", fs.length >= 2 ? fs[1] : "");
					if(fs.length >= 2 && !checkPhone(fs[1])){
						err_msisdn.append(fs[1]+";");
					}
					fc.put("TARGET_NAME", fs.length >= 3 ? fs[2] : "");
					if (fs.length >= 4){
						fc.put("TARGET_ROLE", (null != fs[3]&&"户主".equals(fs[3]) ? "1" : "0"));
					}
					family.add(fc);
					flag = true;
				}
			}
		}
		if (null == family || family.size() <= 0){
			for(int i = 0 ; i < fm.length ; i++){
				IData fc = new DataMap();
				String[] fs = fm[i].split("\\^");
				if (!"".equals(fs[0])){
					fc.put("MAIN_MSISDN", fs.length >= 1 ? fs[0] : "");
					fc.put("TARGET_MSISDN", fs.length >= 2 ? fs[1] : "");
					if(fs.length >= 2 && !checkPhone(fs[1])){
						err_msisdn.append(fs[1]+";");
					}
					fc.put("TARGET_NAME", fs.length >= 3 ? fs[2] : "");
					if (fs.length >= 4){
						fc.put("TARGET_ROLE", (null != fs[3]&&"户主".equals(fs[3]) ? "1" : "0"));
					}
					family.add(fc);
				}
			}
		}
		//校验录入的成员中是否存在号码不合规现象
		if (err_msisdn.length() > 0)
		{
			this.setAjax("ERR_MSG", "录入的亲情圈成员号码格式不对，号码为["+err_msisdn.toString().substring(0, err_msisdn.toString().length()-1)+"]请确认！");
			return ;
		}

		IDataset add = new DatasetList();
		IDataset del = new DatasetList();
		IDataset upd = new DatasetList();
		
        //IData param = new DataMap();
        //param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        //param.putAll(data);

        //IDataset dataset = CSViewCall.call(this, "SS.ModifyPostInfoRegSVC.tradeReg", param);
        //setAjax(dataset);
		String serial_number = inparam.getString("SERIAL_NUMBER");
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("STATUS", "1");
        
        IDataset result = CSViewCall.call(this, "SS.QryPlatSVC.getFamilyCircle", param);
		IDataset result_new = new DatasetList();
        
		if (result != null && result.size() > 0 && !flag){
			inparam.put("MAIN_MSISDN", ((IData)result.get(0)).getString("MAIN_MSISDN"));
		}else{
			for(int i = 0 ; i < result.size() ; i++){
				IData fc = result.getData(i);
				if (null != fc.getString("MAIN_MSISDN") &&
						fc.getString("MAIN_MSISDN").equals(inparam.getString("SERIAL_NUMBER"))){
					result_new.add(fc);
				}
			}
			inparam.put("MAIN_MSISDN",serial_number);
		}
		//全删除的情况
		if (family == null || family.size() <= 0){
			boolean flag_a = false;
			for(int i = 0 ; i < result.size() ; i++){
				IData fc = result.getData(i);
				if (null != fc.getString("MAIN_MSISDN") &&
						fc.getString("MAIN_MSISDN").equals(inparam.getString("SERIAL_NUMBER"))){
					flag_a = true;
					break;
				}
			}
			if (flag_a){
				inparam.put("MAIN_MSISDN",serial_number);
			}else{
				inparam.put("MAIN_MSISDN", ((IData)result.get(0)).getString("MAIN_MSISDN"));
			}
			result_new = result;
		}
		
		
		//成员处理分组
		getMemberGroup(serial_number,family, add, del, upd, result_new);
		
		
		IDataset familyCircleList = this.getFamilyCircleList(add, upd, del, result_new);
		
		inparam.put("SERIAL_NUMBER", inparam.getString("MAIN_MSISDN",""));
		
		inparam.put("USER_LIST", familyCircleList);

        result = CSViewCall.call(this, "SS.QryPlatSVC.familyCircleCallIBoss", inparam);
		
		String msg = "\n";
		msg = msg + ((add.size() > 0) ? "新增亲情圈成员["+add.size()+"]名\n," : "");
		msg = msg + ((upd.size() > 0) ? "更新亲情圈成员["+upd.size()+"]名\n," : "");
		msg = msg + ((del.size() > 0) ? "删除亲情圈成员["+del.size()+"]名," : "");
		this.setAjax("SUCCESS_MSG", msg.substring(0, msg.length() - 1)+"。");
		
		IDataset fms = new DatasetList();
		fms.addAll(family);
		setFamilyMember(fms);
    }
    
    /**
	 * 判断该手机号码是否是移动手机号段
	 * 
	 * @param phone
	 * @return
	 */
	public boolean checkPhone(String phone) {

		phone = phone.trim();
		if (phone == null || phone.equals("") || phone.length() != 11)
		{
			// System.out.println("手机号码必须11位");
			return false;
		}
		return true;
		
		/*// 移动134、135、136、137、138、139、150、151、158、159 、188（tdcdma)
		Pattern pattern = Pattern.compile("13[0-9]{9}||15[0,1,8,9][0-9]{8}||18[8][0-9]{8}");// 手机号码的正则表达式
		Matcher matcher = pattern.matcher(phone);
		if (matcher.matches())
		{
			// System.out.println("是手机号码");
			return true;
		}

		// System.out.println("不是手机号码");
		return false;*/
	}
	
	/***
	 * 区分增、删、改数据
	 * @param dao
	 * @param family
	 * @param add
	 * @param del
	 * @param upd
	 * @param result
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void getMemberGroup(String serial_number,List family, IDataset add,
			IDataset del, IDataset upd, IDataset result) throws Exception {
		//PlatFamilyManageBean platBean = new PlatFamilyManageBean();
		//亲情成员全部删除
		boolean flag = false;
		if (family.size() == 0 && result.size() > 0){
			//platBean.updateMembers(pd, result);
			del.addAll(result);
			flag = true;
			return;
		}
		boolean exists = false;
		for(int i = 0 ; null != family && i < family.size() ; i++){
			IData temp = (IData) family.get(i);
			if ("1".equals(temp.getString("TARGET_ROLE"))){
				exists = true;					//如果户主已删除，则将其下成员全部删除
				break;
			}
		}
		//如果新的列表中不存在户主，则原列表全删除
		if (!exists){
			for (int i = 0 ; i < result.size() ; i++){
				IData temp =  result.getData(i);
				if (temp.getString("MAIN_MSISDN").equals(serial_number)){
					del.add(temp);
				}
			}
			for(int i = 0 ; i < del.size() ; i++){
				result.remove(del.get(i));
			}
			
			//记录缺少的成员
			for(int j = 0 ; !flag && j < result.size() ; j++){
				IData temp =  result.getData(j);
				boolean exists_flag = false;
	
				for(int i = 0 ; !flag && i < family.size() ; i++){
					IData rtemp = (IData)family.get(i);
					if (!temp.getString("MAIN_MSISDN").equals(serial_number) 
							&& temp.getString("MAIN_MSISDN").equals(rtemp.getString("MAIN_MSISDN"))
							&& temp.getString("TARGET_MSISDN").equals(rtemp.getString("TARGET_MSISDN"))){
						exists_flag = true;
						break;
					}
				}
				if (!exists_flag){
					del.add(temp);
				}
			}
			
			add.clear();
			upd.clear();
		}else{
			//亲情成员首次添加
			if (family.size() > 0 && result.size() ==0){
				if(null == result){
					result = new DatasetList();
				}
				add.addAll(family);
				//result.addAll(family);
				//platBean.instMembers(pd, result);
				
				flag = true;
				return;
			}
			
			for(int i = 0 ; !flag && i < family.size() ; i++){
				IData temp = (IData) family.get(i);
				boolean new_flag = true;
				IData rtemp = new DataMap();
				for(int j = 0 ; j < result.size() ; j++){
					rtemp = result.getData(j);
					if (temp.getString("MAIN_MSISDN").equals(rtemp.getString("MAIN_MSISDN"))
							&& temp.getString("TARGET_MSISDN").equals(rtemp.getString("TARGET_MSISDN"))
							&& !temp.getString("TARGET_NAME").equals(rtemp.getString("TARGET_NAME"))){
						new_flag = false;
						upd.add(temp);
						break;
					}else if (temp.getString("MAIN_MSISDN").equals(rtemp.getString("MAIN_MSISDN"))
							&& temp.getString("TARGET_MSISDN").equals(rtemp.getString("TARGET_MSISDN"))){
						new_flag = false;
						break;
					}
				}
				//原亲情圈中不存在
				if (new_flag){
					add.add(temp);
				}
			}
			//记录缺少的成员
			for(int j = 0 ; !flag && j < result.size() ; j++){
				IData temp =  result.getData(j);
				boolean exists_flag = false;
	
				for(int i = 0 ; !flag && i < family.size() ; i++){
					IData rtemp = (IData)family.get(i);
					if (temp.getString("MAIN_MSISDN").equals(rtemp.getString("MAIN_MSISDN"))
							&& temp.getString("TARGET_MSISDN").equals(rtemp.getString("TARGET_MSISDN"))){
						exists_flag = true;
						break;
					}
				}
				if (!exists_flag){
					del.add(temp);
				}
			}
		}
	}
	
	/***
	 * 组装同步IBOSS的成员列表
	 * @param add
	 * @param upd
	 * @param del
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public IDataset getFamilyCircleList(IDataset add,IDataset upd,IDataset del,IDataset result)throws Exception{
		IDataset familyCircleList = new DatasetList();
		IData member = new DataMap();
		IData temp = new DataMap();
		for(int i = 0 ; null != del && i < del.size() ; i++){
			temp = del.getData(i);
			member = new DataMap();
			member.put("MID_VALUE", temp.getString("TARGET_MSISDN"));
			member.put("MEM_NAME", temp.getString("TARGET_NAME"));
			member.put("MEM_ROLE", temp.getString("TARGET_ROLE"));
			member.put("OPR_CODE", "02");		//01 - 新增  02 –删除   03 –变更成员
			familyCircleList.add(member);
		}
		//否则组装新增与更新列表
		for(int i = 0 ; null != add && i < add.size() ; i++){
			temp = add.getData(i);
			member = new DataMap();
			member.put("MID_VALUE", temp.getString("TARGET_MSISDN"));
			member.put("MEM_NAME", temp.getString("TARGET_NAME"));
			member.put("MEM_ROLE", temp.getString("TARGET_ROLE"));
			member.put("OPR_CODE", "01");		//01 - 新增  02 –删除   03 –变更成员
			familyCircleList.add(member);
		}
		for(int i = 0 ; null != upd && i < upd.size() ; i++){
			temp = upd.getData(i);
			member = new DataMap();
			member.put("MID_VALUE", temp.getString("TARGET_MSISDN"));
			member.put("MEM_NAME", temp.getString("TARGET_NAME"));
			member.put("MEM_ROLE", temp.getString("TARGET_ROLE"));
			member.put("OPR_CODE", "03");		//01 - 新增  02 –删除   03 –变更成员
			familyCircleList.add(member);
		}
		return familyCircleList;
	}
}
