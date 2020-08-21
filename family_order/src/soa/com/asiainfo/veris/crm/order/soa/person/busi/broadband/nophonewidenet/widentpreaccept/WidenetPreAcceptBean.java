
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widentpreaccept;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

import java.text.SimpleDateFormat;

public class WidenetPreAcceptBean extends CSBizBean
{
	/**
	 * 查询无手机宽带账号资源
	 * @param inParam
	 * @return
	 * @throws Exception
	 * @author yuyj3
	 */
	public IDataset loadInfo(IData inParam, Pagination pagination) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        param.put("ACCEPT_START_DATE", inParam.getString("ACCEPT_START_DATE"));
        param.put("ACCEPT_END_DATE", inParam.getString("ACCEPT_END_DATE"));
        IDataset widenetAcctInfo = Dao.qryByCodeParser("TF_F_WIDENET_SYNC", "SEL_WIDENET_SYNC_BY_SERIALNUMBER1", param, pagination);
        if(IDataUtil.isNotEmpty(widenetAcctInfo)){
        	IData info = new DataMap();
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        	boolean needTips = false;
        	
        	for (int i = 0; i < widenetAcctInfo.size(); i++) {	//校验是否超出2小时预约时间
        		info = widenetAcctInfo.getData(i);
        		String acceptDate = info.getString("ACCEPT_DATE");
                java.util.Date nowDateTime = sdf.parse(SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:SS"));
                java.util.Date acceptDateTime = sdf.parse(acceptDate);
        		
                long diffTime = nowDateTime.getTime() - acceptDateTime.getTime();
                info.put("TIP_TAG", "0");
                if(diffTime > 0){	//预约时间为次日或更长的不管
                	long diffHour = diffTime / (60 * 60 * 1000);
                	if(diffHour >= 2){	//预约时间超过2小时设置警告标识
                		info.put("TIP_TAG", "1");
                		needTips = true;
                	}
                }
			}
        	
        	addTagName(widenetAcctInfo);
        	widenetAcctInfo.first().put("NEED_TIPS", needTips);
        }
        return widenetAcctInfo;
    }
	
	public IDataset loadWidenetInfo(IData inParam) throws Exception
	{
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        return Dao.qryByCodeParser("TF_F_TWO_CITY_WIDENET", "SEL_WIDENET_BY_SERIALNUMBER", param);
	}
	
	/**
	 * 撤单
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IDataset cancelPreTrade(IData inParam, Pagination pagination) throws Exception
	{
		IDataset refreshData = new DatasetList();
		IData param = new DataMap();
		String serialNumber = inParam.getString("SERIAL_NUMBER");
		String cancelReason = inParam.getString("CANCEL_REASON");
		String cancelRemark = inParam.getString("CANCEL_REMARK","");
		StringBuilder resultInfo = new StringBuilder();
		resultInfo.append(cancelReason);
        if (StringUtils.isNotBlank(cancelRemark)) {
            resultInfo.append(":");
            resultInfo.append(cancelRemark);
        }
		
		IData cancelParam = new DataMap();
		cancelParam.put("SERIAL_NUMBER", serialNumber);
		// 未开户撤单时，宽带号码、宽带地址、宽带密码和宽带套餐名称按北京要求填写
		cancelParam.put("BROADBAND_NEMBER", serialNumber);
		cancelParam.put("WIDENET_ADDR", "宽带未开户撤单");
		cancelParam.put("PASSWORD", "111111");
		cancelParam.put("OFFER", "宽带未开户撤单");
		cancelParam.put("RESULTCODE", "99");
		cancelParam.put("RESULTINFO", resultInfo.toString());	// 撤单原因

		IData abilityResult = WideNetUtil.buildAbilityData(cancelParam);

        if ("00000".equals(abilityResult.getString("resCode"))) {
            IData BJRetInfo = new DataMap(abilityResult.getString("result"));
            if ("00000".equals(BJRetInfo.getString("X_RESULTCODE"))) {
                // 撤单成功修改状态
                param.put("WIDENET_SYNC_ID", inParam.getString("WIDENET_SYNC_ID"));
                param.put("SERIAL_NUMBER", serialNumber);
                param.put("ACCEPT_TAG", "1");
                param.put("REMARKS", "已撤单");
                param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
                Dao.executeUpdateByCodeCode("TF_F_WIDENET_SYNC", "UPD_WIDENET_SYNC_BY_SERIALNUMBER2", param);

                refreshData = queryWidenetSync(param, pagination);
            } else {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "两城两宽调用北京撤单接口失败：" + BJRetInfo.getString("X_RESULTINFO"));
            }
        } else {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "两城两宽调用能力开放平台失败：" + abilityResult.getString("resMsg"));
        }
		return refreshData;
	}
	
	/**
	 * 记录同步预受理信息，提供给北京使用
	 * @return
	 * @throws Exception
	 */
	public IData syncPreTrade(IData inParam) throws Exception
	{
		IData returnInfo = new DataMap();
		//参数检查
		IData checkInfo = checkParam(inParam);
		if(IDataUtil.isNotEmpty(checkInfo)){
			return checkInfo;
		}

		convertPsptTypeCode(inParam);
		
		inParam.put("WIDENET_SYNC_ID", SeqMgr.getInstId());
		inParam.put("ACCEPT_TAG", "0");
		inParam.put("REMARKS", "已同步");
		Boolean isOK = Dao.insert("TF_F_WIDENET_SYNC", inParam);
		
		if(isOK){
			returnInfo.put("X_RESULTCODE", "0");
			returnInfo.put("X_RESULTINFO", "预受理信息同步成功！");
		}else{
			returnInfo.put("X_RESULTCODE", "-1");
			returnInfo.put("X_RESULTINFO", "同步失败！");
		}
		return returnInfo;
	}
	
	public IDataset updatePreTrade(IData inparams, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		String serialNumber = inparams.getString("SERIAL_NUMBER");
		String id = inparams.getString("WIDENET_SYNC_ID");
		String rsrvNum1 = inparams.getString("RSRV_NUM1");
		String rsrvNum2 = inparams.getString("RSRV_NUM2");
		String rsrvStr1 = inparams.getString("RSRV_STR1","");
		String rsrvStr2 = inparams.getString("RSRV_STR2","");
		
		param.put("WIDENET_SYNC_ID", id);
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("RSRV_NUM1", rsrvNum1);
		param.put("RSRV_NUM2", rsrvNum2);
		param.put("RSRV_STR1", rsrvStr1);
		param.put("RSRV_STR2", rsrvStr2);
		
		Dao.executeUpdateByCodeCode("TF_F_WIDENET_SYNC", "UPD_WIDENET_SYNC_BY_SERIALNUMBER1", param);

        return queryWidenetSync(param,pagination);
	}
	
	
	
	public IData checkParam(IData param) throws Exception
	{
		IData checkInfo = new DataMap();
		StringBuilder info = new StringBuilder();
		
		if(StringUtils.isBlank(param.getString("SERIAL_NUMBER",""))){	//服务号码
			info.append("参数：SERIAL_NUMBER 不存在！");
			checkInfo.put("X_RESULTCODE", "-1");
			checkInfo.put("X_RESULTINFO", info.toString());
			return checkInfo;
		}
		if(StringUtils.isBlank(param.getString("CUST_NAME",""))){	//客户名称
			info.append("参数：CUST_NAME 不存在！");
			checkInfo.put("X_RESULTCODE", "-1");
			checkInfo.put("X_RESULTINFO", info.toString());
			return checkInfo;
		}
		if(StringUtils.isBlank(param.getString("PSPT_TYPE_CODE",""))){	//客户证件类型
			info.append("参数：PSPT_TYPE_CODE 不存在！");
			checkInfo.put("X_RESULTCODE", "-1");
			checkInfo.put("X_RESULTINFO", info.toString());
			return checkInfo;
		}
		if(StringUtils.isBlank(param.getString("PSPT_ID",""))){	//证件号码
			info.append("参数：PSPT_ID 不存在！");
			checkInfo.put("X_RESULTCODE", "-1");
			checkInfo.put("X_RESULTINFO", info.toString());
			return checkInfo;
		}
		if(StringUtils.isBlank(param.getString("PSPT_ADDR",""))){	//证件地址
			info.append("参数：PSPT_ADDR 不存在！");
			checkInfo.put("X_RESULTCODE", "-1");
			checkInfo.put("X_RESULTINFO", info.toString());
			return checkInfo;
		}
		if(StringUtils.isBlank(param.getString("PHONE",""))){	//联系电话
			info.append("参数：PHONE 不存在！");
			checkInfo.put("X_RESULTCODE", "-1");
			checkInfo.put("X_RESULTINFO", info.toString());
			return checkInfo;
		}
		if(StringUtils.isBlank(param.getString("NET_WIDE",""))){	//带宽
			info.append("参数：NET_WIDE 不存在！");
			checkInfo.put("X_RESULTCODE", "-1");
			checkInfo.put("X_RESULTINFO", info.toString());
			return checkInfo;
		}
		if(StringUtils.isBlank(param.getString("DISCNT_TYPE",""))){	//套餐类型
			info.append("参数：DISCNT_TYPE 不存在！");
			checkInfo.put("X_RESULTCODE", "-1");
			checkInfo.put("X_RESULTINFO", info.toString());
			return checkInfo;
		}
		if(StringUtils.isBlank(param.getString("WIDENET_ADDR",""))){	//宽带安装地址
			info.append("参数：WIDENET_ADDR 不存在！");
			checkInfo.put("X_RESULTCODE", "-1");
			checkInfo.put("X_RESULTINFO", info.toString());
			return checkInfo;
		}
		if(StringUtils.isBlank(param.getString("ACCEPT_DATE",""))){	//预受理时间
			info.append("参数：ACCEPT_DATE 不存在！");
			checkInfo.put("X_RESULTCODE", "-1");
			checkInfo.put("X_RESULTINFO", info.toString());
			return checkInfo;
		}
		return checkInfo;
	}
	
	public IDataset queryWidenetSync(IData param, Pagination pagination) throws Exception
	{
		IDataset widenetInfo = Dao.qryByCodeParser("TF_F_WIDENET_SYNC", "SEL_WIDENET_SYNC_BY_SERIALNUMBER2", param, pagination);
		addTagName(widenetInfo);
		return widenetInfo;
	}
	
	private void addTagName(IDataset paras)
	{
		IData info = new DataMap();
		for (int i = 0; i < paras.size(); i++) {
			info = paras.getData(i);
			
			String tag = info.getString("ACCEPT_TAG",""); // 翻译同步标记
			if("0".equals(tag)){
				info.put("ACCEPT_TAG_NAME", "已同步");
			}else if("1".equals(tag)){
				info.put("ACCEPT_TAG_NAME", "已撤单");
			}else if("2".equals(tag)){
				info.put("ACCEPT_TAG_NAME", "已开户");
			}else if("3".equals(tag)){
				info.put("ACCEPT_TAG_NAME", "已拆机");
			}else if("4".equals(tag)){
				info.put("ACCEPT_TAG_NAME", "已销户");
			}else{
				info.put("ACCEPT_TAG_NAME", "");
			}
		}
	}

    /**
     * 将北京传入的证件类型转换成海南的证件类型编码
     * @param param
     */
	private void convertPsptTypeCode(IData param) {
	    int psptTypeCodeBJ = Integer.parseInt(param.getString("PSPT_TYPE_CODE"));
	    String psptTypeCodeHN;
	    switch (psptTypeCodeBJ) {
            case  2: psptTypeCodeHN = "A"; break; // 外籍护照
            case  3: psptTypeCodeHN = "3"; break; // 军人证
            case  7: psptTypeCodeHN = "M"; break; // 组织机构代码
            case  9: psptTypeCodeHN = "E"; break; // 营业执照
            case 11: psptTypeCodeHN = "O"; break; // 港澳居民来往内地通行证
            case 12: psptTypeCodeHN = "N"; break; // 台湾居民来往大陆通行证
            case 13: psptTypeCodeHN = "G"; break; // 事业单位法人证书
            case 14: psptTypeCodeHN = "L"; break; // 社会团体法人登记证书
            case 15: psptTypeCodeHN = "D"; break; // 单位证明
            case 20: psptTypeCodeHN = "P"; break; // 外国人永久居留身份证
            case 30: psptTypeCodeHN = "Q"; break; // 港澳台居民居住证
            default: psptTypeCodeHN = "1";        // 外地身份证
        }

        param.remove("PSPT_TYPE_CODE");
	    param.put("PSPT_TYPE_CODE", psptTypeCodeHN);
    }
}
