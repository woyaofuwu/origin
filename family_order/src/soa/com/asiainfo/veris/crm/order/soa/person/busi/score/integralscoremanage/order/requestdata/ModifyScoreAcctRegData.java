
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ModifyScoreAcctRegData extends BaseReqData
{

    private String PSPT_TYPE_CODE;

    private String PSPT_ID;

    private String EMAIL;

    private String CONTRACT_PHONE;

    private String NAME;

    private String ADDRESSE;

    private String START_DATE;

    private String END_DATE;
    
    private String INTEGRAL_ACCOUNT_ID;
    
    private String INTEGRAL_ACCOUNT_TYPE;
    
    private String STATUS;
    
    private String IN_TYPE;

	public String getPSPT_TYPE_CODE() {
		return PSPT_TYPE_CODE;
	}

	public void setPSPT_TYPE_CODE(String pspt_type_code) {
		PSPT_TYPE_CODE = pspt_type_code;
	}

	public String getPSPT_ID() {
		return PSPT_ID;
	}

	public void setPSPT_ID(String pspt_id) {
		PSPT_ID = pspt_id;
	}

	public String getEMAIL() {
		return EMAIL;
	}

	public void setEMAIL(String email) {
		EMAIL = email;
	}

	public String getCONTRACT_PHONE() {
		return CONTRACT_PHONE;
	}

	public void setCONTRACT_PHONE(String contract_phone) {
		CONTRACT_PHONE = contract_phone;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String name) {
		NAME = name;
	}

	public String getADDRESSE() {
		return ADDRESSE;
	}

	public void setADDRESSE(String addresse) {
		ADDRESSE = addresse;
	}

	public String getSTART_DATE() {
		return START_DATE;
	}

	public void setSTART_DATE(String start_date) {
		START_DATE = start_date;
	}

	public String getEND_DATE() {
		return END_DATE;
	}

	public void setEND_DATE(String end_date) {
		END_DATE = end_date;
	}

	public String getINTEGRAL_ACCOUNT_ID() {
		return INTEGRAL_ACCOUNT_ID;
	}

	public void setINTEGRAL_ACCOUNT_ID(String integral_account_id) {
		INTEGRAL_ACCOUNT_ID = integral_account_id;
	}

	public String getINTEGRAL_ACCOUNT_TYPE() {
		return INTEGRAL_ACCOUNT_TYPE;
	}

	public void setINTEGRAL_ACCOUNT_TYPE(String integral_account_type) {
		INTEGRAL_ACCOUNT_TYPE = integral_account_type;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String status) {
		STATUS = status;
	}

	public String getIN_TYPE() {
		return IN_TYPE;
	}

	public void setIN_TYPE(String in_type) {
		IN_TYPE = in_type;
	}

	
}
