if(typeof(BatModifyacycinfo)=="undefined"){
	window["BatModifyacycinfo"]=function(){
		
	};
	var batmodifyacycinfo = new BatModifyacycinfo();
}

(function(){
	$.extend(BatModifyacycinfo.prototype,{
	
	resolveReturnValues: function (){
	
		var value =$("#BANK_CODE1111").val();
		$("#BANK_CODE1111").val('选择用户');
		var returnValues = value.split('|');
		$("#PAY_NAME").val(returnValues[0]);
		$("#PAY_MODE_CODE").val(returnValues[1]);
		$("#PAY_MODE_CODE").find("option[value='"+returnValues[1]+"'").attr("selected",true);
		$("#CONTRACT_NO").val(returnValues[2]);
		$("#POST_CODE").val(returnValues[3]);
		$("#POST_ADDRESS").val(returnValues[4]);
		$("#BANK_CODE").val(returnValues[5]);
		$("#POP_BANK_CODE").val(returnValues[6]);
		$("#BANK_ACCT_NO").val(returnValues[7]);
		$("#RSRV_STR5").val(returnValues[8]);

		var oldinfo = new Wade.DataMap();
		oldinfo.put('PAY_NAME', returnValues[0]);
		oldinfo.put('PAY_MODE_CODE', returnValues[1]);
		oldinfo.put('PAY_MODE', returnValues[8]);
		oldinfo.put('CONTRACT_NO', returnValues[2]);
		oldinfo.put('POST_CODE', returnValues[3]);
		oldinfo.put('POST_ADDRESS', returnValues[4]);
		oldinfo.put('BANK_CODE', returnValues[5]);
		oldinfo.put('POP_BANK_CODE', returnValues[6]);
		oldinfo.put('BANK_ACCT_NO', returnValues[7]);
		oldinfo.put('RSRV_STR5', returnValues[8]);
		
		$("#IMPORT_COND").val(oldinfo);
		if(returnValues[1] == '0') 
		{
			
		    $("#BANK_CODE").attr("disabled", true);
		    $("#POP_BANK_CODE").attr("disabled", true);
		    $("#BANK_ACCT_NO").attr("disabled", true);
		    $("#CONTRACT_NO").attr("disabled", true);
		    $("#RSRV_STR5").attr("disabled", true);
		    
		    $("#bname").attr("class", "");
		    $("#cname").attr("class", "");
		    $("#baname").attr("class", "");
		    $("#zname").attr("class", "");
		    $("#POP_BANK_CODE").attr("nullable", "yes");
		    $("#BANK_CODE").attr("nullable", "yes");
		    $("#CONTRACT_NO").attr("nullable", "yes");
		    $("#BANK_ACCT_NO").attr("nullable", "yes");
		    $("#RSRV_STR5").attr("nullable", "yes");
		    $("#RSRV_STR5").val("");
		} 
		else 
		{
		    $("#SUPER_BANK_CODE").attr("disabled", false);
		    $("#BANK_CODE").attr("disabled", false);
		    $("#CONTRACT_NO").attr("disabled", false);
		    $("#BANK_ACCT_NO").attr("disabled", false);
		    $("#RSRV_STR5").attr("disabled", false);
		    
		    $("#bname").attr("class", "e_required");
		    $("#cname").attr("class", "e_required");
		    $("#baname").attr("class", "e_required");
		    $("#zname").attr("class", "e_required");
		    $("#POP_BANK_CODE").attr("nullable", "no");
		    $("#BANK_CODE").attr("nullable", "no");
		    $("#CONTRACT_NO").attr("nullable", "no");
		    $("#BANK_ACCT_NO").attr("nullable", "no");
		    $("#RSRV_STR5").attr("nullable", "no");
		    $("#RSRV_STR5").val("1");
		}
	},

	checkPaymode: function (){	
		var pay_mode_code=$("#PAY_MODE_CODE").val();
		$("#BANK_CODE").val("");
	    $("#CONTRACT_NO").val("");
	    $("#BANK_ACCT_NO").val("");
	    $("#POP_BANK_CODE").val("");
		$("#RSRV_STR5").val("");
		
		if(pay_mode_code == '0') 
		{
			
		    $("#BANK_CODE").attr("disabled", true);
		    $("#POP_BANK_CODE").attr("disabled", true);
		    $("#BANK_ACCT_NO").attr("disabled", true);
		    $("#CONTRACT_NO").attr("disabled", true);
		    $("#RSRV_STR5").attr("disabled", true);
		    
		    $("#bname").attr("class", "");
		    $("#cname").attr("class", "");
		    $("#baname").attr("class", "");
		    $("#zname").attr("class", "");
		    $("#POP_BANK_CODE").attr("nullable", "yes");
		    $("#BANK_CODE").attr("nullable", "yes");
		    $("#CONTRACT_NO").attr("nullable", "yes");
		    $("#BANK_ACCT_NO").attr("nullable", "yes");
		    $("#RSRV_STR5").attr("nullable", "yes");
		    $("#RSRV_STR5").val("");
		} 
		else 
		{
		    $("#SUPER_BANK_CODE").attr("disabled", false);
		    $("#BANK_CODE").attr("disabled", false);
		    $("#CONTRACT_NO").attr("disabled", false);
		    $("#BANK_ACCT_NO").attr("disabled", false);
		    $("#RSRV_STR5").attr("disabled", false);
		    
		    $("#bname").attr("class", "e_required");
		    $("#cname").attr("class", "e_required");
		    $("#baname").attr("class", "e_required");
		    $("#zname").attr("class", "e_required");
		    $("#POP_BANK_CODE").attr("nullable", "no");
		    $("#BANK_CODE").attr("nullable", "no");
		    $("#CONTRACT_NO").attr("nullable", "no");
		    $("#BANK_ACCT_NO").attr("nullable", "no");
		    $("#RSRV_STR5").attr("nullable", "no");
		    $("#RSRV_STR5").val("1");
		}
	},

	queryBanks: function (){
		var pay_mode_code=$("#PAY_MODE_CODE").val();
		if(pay_mode_code == '0') 
		{
			
		}else{
			popupPage('bat.batbankquery.BankPopupQry','initPage','&IS_POP=BANKQRY','选择银行',900,490,'BANK_CODE');
		}
	}

	});
	}
)();

