if(typeof(BatActiveCreateUser)=="undefined"){
	window["BatActiveCreateUser"]=function(){
		
	};
	var batactivecreateuser = new BatActiveCreateUser();
}

(function(){
	$.extend(BatActiveCreateUser.prototype,{
		
	checkPaymode: function (){	
		var pay_mode_code=$("#PAY_MODE_CODE").val();
		$("#SUPER_BANK_CODE").val("");
	    $("#BANK_CODE").val("");
	    $("#BANK_ACCT_NO").val("");
	    $("#POP_BANK_CODE").val("");
	    
		if(pay_mode_code == '0') 
		{
		    $("#SUPER_BANK_CODE").attr("disabled", true);
		    $("#BANK_CODE").attr("disabled", true);
		    $("#POP_BANK_CODE").attr("disabled", true);
		    $("#BANK_ACCT_NO").attr("disabled", true);
		    
		    $("#bname").attr("class", "");
		    $("#sname").attr("class", "");
		    $("#baname").attr("class", "");
		    $("#POP_BANK_CODE").attr("nullable", "yes");
		    $("#BANK_CODE").attr("nullable", "yes");
		    $("#BANK_ACCT_NO").attr("nullable", "yes");
		    $("#SUPER_BANK_CODE").attr("nullable", "yes");
		} 
		else 
		{
		    $("#SUPER_BANK_CODE").attr("disabled", false);
		    $("#BANK_CODE").attr("disabled", false);
		    $("#BANK_ACCT_NO").attr("disabled", false);
		    
		    $("#bname").attr("class", "e_required");
		    $("#sname").attr("class", "e_required");
		    $("#baname").attr("class", "e_required");
		    $("#POP_BANK_CODE").attr("nullable", "no");
		    $("#BANK_CODE").attr("nullable", "no");
		    $("#BANK_ACCT_NO").attr("nullable", "no");
		    $("#SUPER_BANK_CODE").attr("nullable", "no");
		}
	},

	queryBanks: function (){
		var pay_mode_code=$("#PAY_MODE_CODE").val();
		if(pay_mode_code == '0') 
		{
			
		}else{
			var superbank_code = $("#SUPER_BANK_CODE").val();
			if(superbank_code == ''){
			
			}else{
				popupPage('bat.batbankquery.BankPopupQry','queryBankInfo','&SUPER_BANK_CODE='+$("#SUPER_BANK_CODE").val()+"&IS_POP=BANKQRY",'选择银行',900,490,'BANK_CODE');
			}
		}
	}
	});
	}
)();