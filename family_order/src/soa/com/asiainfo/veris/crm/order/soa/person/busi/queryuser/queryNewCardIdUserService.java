package com.asiainfo.veris.crm.order.soa.person.busi.queryuser;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class queryNewCardIdUserService extends CSBizService{


	private static final long serialVersionUID = 1L;
	
	public IDataset Query(IData data) throws Exception {

		queryNewCardIdUserBean bean = BeanManager.createBean(queryNewCardIdUserBean.class);
		IDataset retruninfo = new DatasetList();
		IData redata = new DataMap();
		redata.put("CARD_ID_NUM", data.getString("CARD_ID_NUM", "").trim());

		IDataset ds = bean.Query(data);
		if (null != ds && ds.size() > 0){
			/*redata.put("INFOMSG", "该用户非新身份证用户。");*/
			//redata.put("INFOMSG", "该用户为非新身份证用户（即非纯新增用户）");
			IDataset allUsers = bean.QueryAllUserByPsptId(data);
			if(null != allUsers && allUsers.size() > 0){
				retruninfo = allUsers;
				return retruninfo;
			}else{
				redata.put("INFOMSG", "该用户为非新身份证用户（即非纯新增用户）");
				redata.put("USER_TYPE", "0");
			}
		}
		else {
			//redata.put("INFOMSG", "该用户为新身份证用户，符合新身份证用户主套餐分阶酬金，为用户开户且符合条件可获得15元酬金。");
			redata.put("INFOMSG", "该用户为新身份证用户（即纯新增用户）");
			redata.put("USER_TYPE", "1");
		}
		
		retruninfo.add(redata);
		return retruninfo;	    	 

	}
	
	public IData QueryAllUsersByPsptId(IData data) throws Exception {

		queryNewCardIdUserBean bean = BeanManager.createBean(queryNewCardIdUserBean.class);
		IDataset retruninfo = new DatasetList();
		IData redata = new DataMap();
		IData reMap = new DataMap();
		redata.put("CARD_ID_NUM", data.getString("CARD_ID_NUM", "").trim());
		
		reMap = checkPspt(data.getString("CARD_ID_NUM", "").trim());
		
		if("-1".equals(reMap.getString("X_RESULT_CODE", ""))){
			return reMap;
		}
		
		reMap.put("X_RESULT_CODE", "0");
		reMap.put("X_RESULT_INFO", "查询成功");

		try {
			IDataset ds = bean.Query(data);
			if (null != ds && ds.size() > 0){
				/*redata.put("INFOMSG", "该用户非新身份证用户。");*/
				//redata.put("INFOMSG", "该用户为非新身份证用户（即非纯新增用户）");
				//retruninfo = bean.QueryAllUserByPsptId(data);
				IDataset allUsers = bean.QueryAllUserByPsptId(data);
				if(null != allUsers && allUsers.size() > 0){
					retruninfo = allUsers;
				}else{
					redata.put("INFOMSG", "该用户为非新身份证用户（即非纯新增用户）");
					redata.put("USER_TYPE", "0");
					retruninfo.add(redata);
				}
			}
			else {
				//redata.put("INFOMSG", "该用户为新身份证用户，符合新身份证用户主套餐分阶酬金，为用户开户且符合条件可获得15元酬金。");
				redata.put("INFOMSG", "该用户为新身份证用户（即纯新增用户）");
				redata.put("USER_TYPE", "1");
				retruninfo.add(redata);
			}
		} catch (Exception e) {
			reMap.put("X_RESULT_CODE", "-1");
			reMap.put("X_RESULT_INFO", e.getMessage());
		}
		reMap.put("X_RESULT_DATA", retruninfo);
		return reMap;	    	 
	}
	
	private IData checkPspt(String pstpId)
    {
        String[] errors =
        { "验证通过", "身份证号码位数不对", "身份证号码不合法", "身份证号码校验错误", "身份证地区非法", "身份证出生日期不符合要求", "身份证为空" };
        IData reMap = new DataMap();
        reMap.put("X_RESULT_CODE", "0");
		reMap.put("X_RESULT_INFO", "查询成功");
		reMap.put("X_RESULT_DATA", new DatasetList());
        
        if (pstpId == null || "".equals(pstpId) || pstpId.length()==1){
        	reMap.put("X_RESULT_CODE", "-1");
    		reMap.put("X_RESULT_INFO", errors[4]);
    		return reMap;
        }
            
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

        String idcard = pstpId, Y, JYM;
        String S, M, ereg;
        Calendar c = Calendar.getInstance();
        if (idcard.charAt(idcard.length() - 1) == '*')
            idcard = idcard.substring(0, idcard.length() - 1) + 'X';

        if (!area.containsKey(idcard.substring(0, 2)))
        {
        	reMap.put("X_RESULT_CODE", "-1");
    		reMap.put("X_RESULT_INFO", errors[4]);
    		return reMap;
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
                        	reMap.put("X_RESULT_CODE", "-1");
                    		reMap.put("X_RESULT_INFO", errors[4]);
                        }
                    }
                }
                else
                {
                	reMap.put("X_RESULT_CODE", "-1");
            		reMap.put("X_RESULT_INFO", errors[2]);
                }
                return reMap;
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
                    	if ((iYear + 100) < nowY)
                        {
                    		reMap.put("X_RESULT_CODE", "-1");
                    		reMap.put("X_RESULT_INFO", errors[5]);
                        }
                    }
                }
                else
                {
                	reMap.put("X_RESULT_CODE", "-1");
            		reMap.put("X_RESULT_INFO", errors[2]);
                }
                return reMap;
            default:
            	reMap.put("X_RESULT_CODE", "-1");
        		reMap.put("X_RESULT_INFO", errors[2]);
        }
        
        return reMap;
    }
	
}
