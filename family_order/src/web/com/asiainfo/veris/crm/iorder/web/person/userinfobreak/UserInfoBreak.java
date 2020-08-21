package com.asiainfo.veris.crm.iorder.web.person.userinfobreak;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserInfoBreak extends PersonBasePage {
	
	public void qryUserData(IRequestCycle cycle)throws Exception {
		
		IData param = getData();
		
		IData inParam = new DataMap();
		inParam.put("CUST_NAME", param.getString("CUST_NAME"));
		inParam.put("PSTP_ID", param.getString("PSTP_ID"));
		inParam.put("START_DATE", param.getString("START_DATE"));
		
		IDataOutput output = CSViewCall.callPage(this, "SS.UserInfoBreakQrySVC.getUserInfo", inParam,null);
		
		this.setInfos(output.getData());
		setCount(output.getDataCount());
	}
	
	public void delBlackUser(IRequestCycle cycle)throws Exception {
		
		IData param = getData();
		CSViewCall.call(this, "SS.UserInfoBreakQrySVC.delBlackUser", param);
		
		qryUserData(cycle);
	}
	
	public void importUserData(IRequestCycle cycle) throws Exception{
		IData param = getData();
		String fileId = param.getString("FILE_ID");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        // 通过指定的配置文件，以及页面指定的excel导入数据文件取出数据集
        IData fileData = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/bat/ImportUserInfoBreak.xml"));
        
        int rightCount = fileData.getInt("rightCount", 0);
        int errorCount = fileData.getInt("errorCount", 0);

        // 文件解析是否成功
        if (IDataUtil.isEmpty(fileData)) {
            CSViewException.apperr(BatException.CRM_BAT_89);
            return;
        }
        // 判断是否有数据
        if (rightCount + errorCount == 0) {
            CSViewException.apperr(BatException.CRM_BAT_86);
            return;
        }
        
        if (rightCount > 0) {
        	IDataset[] datasets = (IDataset[]) fileData.get("right");
        	
        	if(datasets!=null&&datasets.length>0){
        		
        		for(int i=0;i<datasets[0].size();i++){
        			IData data=datasets[0].getData(i);
        			
        			IData inParam = new DataMap();
        			inParam.put("CUST_NAME", data.getString("CUST_NAME"));
        			inParam.put("PSPT_TYPE_CODE", data.getString("PSPT_TYPE_CODE"));
        			inParam.put("PSTP_ID", data.getString("PSTP_ID"));
        			inParam.put("START_DATE", data.getString("START_DATE"));
        			inParam.put("OP_ID", getVisit().getStaffId());
        														
        			CSViewCall.callPage(this, "SS.UserInfoBreakQrySVC.insertUserData", inParam,null);
        			
        		}
        		
        		
        		
        	}
        
  
        }
	}
	
	public void insertUserData(IRequestCycle cycle)throws Exception {
		
		IData param = getData();
		
		IData inParam = new DataMap();
		inParam.put("CUST_NAME", param.getString("CUST_NAME"));
		inParam.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE"));
		inParam.put("PSTP_ID", param.getString("PSTP_ID"));
		inParam.put("START_DATE", param.getString("START_DATE"));
		inParam.put("OP_ID", getVisit().getStaffId());
		
		IData retData=new DataMap();
		retData.put("RESULT_CODE", "0");
		
		if(param.getString("PSTP_ID","").length()<2){
			retData.put("RESULT_CODE", "-1");
			retData.put("RESULT_MSG", "证件号码位数不对！");
		}else{
			String reString=checkPspt(param.getString("PSTP_ID"), "", param.getString("PSPT_TYPE_CODE"));											
			if(StringUtils.isNotEmpty(reString)){
				retData.put("RESULT_CODE", "-1");
				retData.put("RESULT_MSG", reString);
			}
		}
		CSViewCall.callPage(this, "SS.UserInfoBreakQrySVC.insertUserData", inParam,null);
		setAjax(retData);
	}
	
	public void onInitTrade(IRequestCycle cycle) throws Exception{
		
		IData paramsData=new DataMap();
        paramsData.put("SUBSYS_CODE", "CSM");
        paramsData.put("PARAM_ATTR", "3451");
        
        IDataset resultData=CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByAttr", paramsData);
        
        setCode(resultData);
	}
	
	/**
     * check pspt
     * 
     * @param value
     * @param desc
     * @param psptTypeCode
     *            ：证件类型
     * @return 成功返回空字符串，失败则返回错误信息
     */
	public String checkPspt(String value, String desc, String psptTypeCode)
    {
        String[] errors =
        { "验证通过", "身份证号码位数不对", "身份证号码不合法", "身份证号码校验错误", "身份证地区非法", "身份证出生日期不符合要求", "身份证为空" };
        if (value == null || "".equals(value))
            return desc + "(" + value + ")" + errors[4] + ";";
        IData area = new DataMap();
        area.put("11", "\u5317\u4EAC");
        area.put("12", "\u5929\u6D25");
        area.put("13", "\u6CB3\u5317");
        area.put("14", "\u5C71\u897F");
        area.put("15", "\u5185\u8499\u53E4");
        area.put("21", "\u8FBD\u5B81");
        area.put("22", "\u5409\u6797");
        area.put("23", "\u9ED1\u9F99\u6C5F");
        area.put("31", "\u4E0A\u6D77");
        area.put("32", "\u6C5F\u82CF");
        area.put("33", "\u6D59\u6C5F");
        area.put("34", "\u5B89\u5FBD");
        area.put("35", "\u798F\u5EFA");
        area.put("36", "\u6C5F\u897F");
        area.put("37", "\u5C71\u4E1C");
        area.put("41", "\u6CB3\u5357");
        area.put("42", "\u6E56\u5317");
        area.put("43", "\u6E56\u5357");
        area.put("44", "\u5E7F\u4E1C");
        area.put("45", "\u5E7F\u897F");
        area.put("46", "\u6D77\u5357");
        area.put("50", "\u91CD\u5E86");
        area.put("51", "\u56DB\u5DDD");
        area.put("52", "\u8D35\u5DDE");
        area.put("53", "\u4E91\u5357");
        area.put("54", "\u897F\u85CF");
        area.put("61", "\u9655\u897F");
        area.put("62", "\u7518\u8083");
        area.put("63", "\u9752\u6D77");
        area.put("64", "\u5B81\u590F");
        area.put("65", "\u65B0\u7586");
        area.put("71", "\u53F0\u6E7E");
        area.put("81", "\u9999\u6E2F");
        area.put("82", "\u6FB3\u95E8");
        area.put("91", "\u56FD\u5916");

        String idcard = value, Y, JYM;
        String S, M, ereg;
        Calendar c = Calendar.getInstance();
        if (idcard.charAt(idcard.length() - 1) == '*')
            idcard = idcard.substring(0, idcard.length() - 1) + 'X';

        if (!area.containsKey(idcard.substring(0, 2)))
        {
            return desc + "(" + value + ")" + errors[4] + ";";
        }
        switch (idcard.length())
        {
            case 15:
                if ((Integer.parseInt(idcard.substring(6, 8)) + 1900) % 4 == 0 || ((Integer.parseInt(idcard.substring(6, 8)) + 1900) % 100 == 0 && (Integer.parseInt(idcard.substring(6, 8)) + 1900) % 4 == 0))
                {
                    ereg = "^[1-9][0-9]{5}([0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$";
                }
                else
                {
                    ereg = "^[1-9][0-9]{5}([0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-9]))[0-9]{3}$";
                }
                boolean bTemp = Pattern.compile(ereg).matcher(idcard).find();
                if (bTemp)
                {
                    Matcher matches = Pattern.compile(ereg).matcher(idcard);
                    c.setTime(new java.util.Date());
                    int nowY = c.get(Calendar.YEAR);
                    if (matches.groupCount() > 0)
                    {
                        if (Integer.parseInt(("19" + idcard.substring(6, 8))) + 100 < nowY)
                        {
                            return desc + "(" + value + ")" + errors[5] + ";";
                        }
                    }
                    return "";
                }
                else
                {
                    return desc + "(" + value + ")" + errors[2] + ";";
                }
            case 18:
                if (Integer.parseInt(idcard.substring(6, 10)) % 4 == 0 || (Integer.parseInt(idcard.substring(6, 10)) % 100 == 0 && Integer.parseInt(idcard.substring(6, 10)) % 4 == 0))
                {
                    ereg = "^[1-9][0-9]{5}((19|20)[0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$";
                }
                else
                {
                    ereg = "^[1-9][0-9]{5}((19|20)[0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-9]))[0-9]{3}[0-9Xx]$";
                }
                boolean bTemp18 = Pattern.compile(ereg).matcher(idcard).find();
                if (bTemp18)
                {
                    Pattern pattern = Pattern.compile(ereg);
                    Matcher matches = pattern.matcher(idcard);
                    c.setTime(new java.util.Date());
                    int nowY = c.get(Calendar.YEAR);
                    if (matches.groupCount() > 0)
                    {
                        int iYear = Integer.parseInt(idcard.substring(6, 10));
                        /*------modify by chenzg@20131122--身份证件类型新增户口本(REQ201311080002)---begin----*/
                        /* 户口本证件类型，用户的也是身份证，但是校验身份证号码时，不限制必须大于15岁 */
                        if ("2".equals(psptTypeCode))
                        {
                            if ((iYear + 100) < nowY)
                            {
                                return desc + "(" + value + ")" + errors[5] + ";";
                            }
                        }
                        else
                        {
                           // if ((iYear + 15) > nowY || (iYear + 100) < nowY)
                        	if ((iYear + 100) < nowY)
                            {
                                return desc + "(" + value + ")" + errors[5] + ";";
                            }
                        }
                        /*------modify by chenzg@20131122--身份证件类型新增户口本(REQ201311080002)---end------*/
                    }
                    return "";
                }
                else
                {
                    return desc + "(" + value + ")" + errors[2] + ";";
                }
            default:
                return desc + "(" + value + ")" + errors[2] + ";";
        }
    }
	
	public abstract void setInfos(IDataset infos);
	public abstract void setCode(IDataset code);
    public abstract void setInfo(IData info);
	public abstract void setCount(long count);
	
}
