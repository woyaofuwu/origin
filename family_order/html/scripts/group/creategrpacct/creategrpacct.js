/**检查帐户类别是不是选择托收，如果是则激活上级银行、银行名称、银行帐号组件*/
function checkPaymode(mode) {
    var grpSn = $("#GROUP_MGR_SN").val();
    var grpName = $("#GROUP_MGR_CUST_NAME").val();
	var payModeCode =  $("#acctInfo_PAY_MODE_CODE");
	var superbandCode =  $("#acctInfo_SUPER_BANK_CODE");
	var bankCode =  $("#acctInfo_BANK_CODE");
	var bandAcctNo =  $("#acctInfo_BANK_ACCT_NO");
	var bandContNo =  $("#acctInfo_CONTRACT_ID");
	var contact =  $("#acctInfo_CONTACT");
	var contactPhone = $("#POP_acctInfo_CONTACT_PHONE");	
	var consignAmount = $("#acctInfo_CONSIGN_AMOUNT"); 
	
	//根据账户类别进行必填提示
	//上级银行
	var superBankOne = document.getElementById("QrySuperBankOne");
	var superBankTwo = document.getElementById("QrySuperBankTwo");
	//银行名称
	var bankOne = document.getElementById("QryBankOne");
	var bankTwo = document.getElementById("QryBankTwo");
	//银行账号
	var bankAcctOne = document.getElementById("QryBankAcctOne");
	var bankAcctTwo = document.getElementById("QryBankAcctTwo");
		
	if(payModeCode.val()== "0") {		  
	    superbandCode.attr("disabled",true);
		bankCode.attr("disabled",true);
		bandAcctNo.attr("disabled",true);
		bandContNo.attr("disabled",true);
		contact.attr("disabled",true);
		contactPhone.attr("disabled",true);		
		$("#acctInfo_SUPER_BANK_CODE").val("");
		$("#acctInfo_BANK_CODE").val("");
		$("#acctInfo_CONTRACT_ID").val("");
		$("#acctInfo_BANK_ACCT_NO").val("");
		$("#POP_acctInfo_CONTACT_PHONE").val("");
		$("#acctInfo_CONTACT").val("");
		superbandCode.attr("nullable", "yes");
		bankCode.attr("nullable", "yes");
		bandAcctNo.attr("nullable", "yes");
		// add start by lic 2012/6/20 
		consignAmount.attr("disabled",true);
		$("#acctInfo_CONSIGN_AMOUNT").val('');
		//add end
		superBankOne.style.display = "";
		superBankTwo.style.display = "none";
		
		bankOne.style.display = "";
		bankTwo.style.display = "none";
		
		bankAcctOne.style.display = "";
		bankAcctTwo.style.display = "none";
		
	}else if(payModeCode.val() == ""){		  
	    superbandCode.attr("disabled",true);
		bankCode.attr("disabled",true);
		bandAcctNo.attr("disabled",true);
		bandContNo.attr("disabled",true);
		contact.attr("disabled",true);
		contactPhone.attr("disabled",true);		
		superbandCode.attr("nullable", "yes");
		bankCode.attr("nullable", "yes");
		bandAcctNo.attr("nullable", "yes");
		// add start by lic 2012/6/20 
		consignAmount.attr("disabled",true); 
		//add end
		superBankOne.style.display = "";
		superBankTwo.style.display = "none";
		
		bankOne.style.display = "";
		bankTwo.style.display = "none";
		
		bankAcctOne.style.display = "";
		bankAcctTwo.style.display = "none";
	}else{	 
		superbandCode.attr("disabled",false);
		$("#bankFld").attr('disabled',false);
		bankCode.attr("disabled",false);
		bandAcctNo.attr("disabled",false);
		bandContNo.attr("disabled",false);
		contact.attr("disabled",false);
		contactPhone.attr("disabled",false);		
		superbandCode.attr("nullable", "no");
		bankCode.attr("nullable", "no");
		bandAcctNo.attr("nullable", "no");
		if (mode != "1"){
			if (contact.val() == ""){
				$("#acctInfo_CONTACT").val(grpName);
			}
			if (""==contactPhone.val()){
				$("#POP_acctInfo_CONTACT_PHONE").val(grpSn);
			}
		}
		
		consignAmount.attr("disabled",true); 
		//add end
		superBankOne.style.display = "none";
		superBankTwo.style.display = "";
		
		bankOne.style.display = "none";
		bankTwo.style.display = "";
		
		bankAcctOne.style.display = "none";
		bankAcctTwo.style.display = "";
     }
} 
/** 根据上级银行获取银行名称列表*/
function ajaxGetBankCode() {
	var superBankCode = $("#acctInfo_SUPER_BANK_CODE");
	var bankCode = $("#acctInfo_BANK_CODE");	
	if (superBankCode.val()!= "") {
	   $.ajax.submit('PartsuperBankCode','queryBank','&IS_TTGRP='+$('#SELECTGROUP_IS_TTGRP').val(),'bankFld',
		   function(data)
		    { 
		        $.endPageLoading(); 
		        var x_resultcode = data.get("x_resultcode","0");
		        if(x_resultcode == '-1'){
		      	  ifcheck= false;
		      	  alert(data.get("x_resultinfo"));
		      		return;
		         } 
		        loadDataAfter();
			},
			function(error_code,error_info,derror)
			{
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    }
		 );
	}
}
function checkSuperBank(){
		$("#POP_acctInfo_BANK_CODE").val('') ;
}
function checkBank(){
	var  SUPERBANK_CODE = $("#acctInfo_SUPER_BANK_CODE").val();
	if(SUPERBANK_CODE == null || SUPERBANK_CODE == ""){
		alert("上级银行不能为空！");
		return false;	
	}
}
/**ajax调用后执行*/
function loadDataAfter() {    
	var bankCode = $("#acctInfo_BANK_CODE");
	bankCode.attr("disabled",false);
	bankCode.attr("nullable", "no");
}

/**  */
function refresh(){
	var group_page = $("#pam_NOTIN_GROUP_ID").val();//跳转过来的
	var group = $("#POP_cond_GROUP_ID").val();
	if (group == "" && group_page == ""){
		alert ('请输入正确的集团编码！');
		return false;
	}else{
		$("#createArea").css("display","none");	
		$.beginPageLoading();
		$.ajax.submit('GroupInfoPart','queryAcctInfos',null, 'acctArea',
		    function(data)
		    { 
		        $.endPageLoading();
		        var x_resultcode = data.get("x_resultcode","0");
		        if(x_resultcode == '-1'){
		      	  ifcheck= false;
		      	  alert(data.get("x_resultinfo"));
		      		return;
		         } 
			},
			function(error_code,error_info,derror)
			{
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    }
		 );
      }
}

/** initGroupAcctEdit */
function init() {
	var bcreate = $("#bcreate"); 
	var bupdate = $("#bupdate"); 
	var bdelete = $("#bdelete"); 
	var back = $("#back"); 
	var acctinfo = $("#AcctInfo");
	
	bcreate.css("display","none");
	bupdate.css("display","none");
	bdelete.css("display","none");
	back.css("display","");
	acctinfo.css("display","none");
	 
	var acct_id =$("#acctInfo_ACCT_ID").val();
	
	if (acct_id == "") {		
		bcreate.css("display","");
	} else {
	    bupdate.css("display","");
	    
		var isDeletePriv =$("#GROUP_ACCT_DEL").val();
		if('true' == isDeletePriv){
	    	bdelete.css("display","");
		}
		
	    acctinfo.css("display","");	
	}
	checkPaymode(1);
	
}

/**新增按钮的ajax刷新 开始*/  
function createAcct() {
	$.beginPageLoading();
	$.ajax.submit('GroupInfoPart','initPageEdit','&IS_TTGRP='+$('#SELECTGROUP_IS_TTGRP').val()+'&cond_PRODUCT_ID='+$('#GROUP_PRODUCT_ID').val(),'createArea',
			 function(data)
			    { 
			        $.endPageLoading(); 
			        var x_resultcode = data.get("x_resultcode","0");
			        if(x_resultcode == '-1'){
			      	  ifcheck= false;
			      	  alert(data.get("x_resultinfo"));
			      		return;
			         } 
			        createAcctAfter();
				},
				function(error_code,error_info,derror)
				{
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
			    }
			 ); 
}
function createAcctAfter(grpSn){
	init();
	var custId=$("#CUST_ID").val();
	$("#CUST_ID_HIDE").val(custId);
	$("#createArea").css("display","");
	$('#GrpSn').val(grpSn); 
}
/**新增按钮的ajax刷新 结束*/

/**账户按钮的ajax刷新 开始*/
function acctInfo(acctId,grpSn) {
    document.getElementById("acctInfo_CHECK").checked = false;
    $.beginPageLoading();
	$.ajax.submit('acctArea','queryAcctInfo','&ACCT_ID='+acctId+'&IS_TTGRP='+$('#SELECTGROUP_IS_TTGRP').val(),'createArea',
			function(data)
		    { 
		        $.endPageLoading(); 
		        var x_resultcode = data.get("x_resultcode","0");
		        if(x_resultcode == '-1'){
		      	  ifcheck= false;
		      	  alert(data.get("x_resultinfo"));
		      		return;
		         }
                var ToBbossAcct = data.get("RSRV_STR6");
                // 判断是否已经同步
                if (ToBbossAcct == "TOBBOSSACCT") {
                    document.getElementById('acctInfo_CHECK').checked = true;
                    changeSmsFlag();
                }
		        createAcctAfter(grpSn);
			},
			function(error_code,error_info,derror)
			{
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    }
		 );  
}
/**取消按钮的ajax刷新 结束*/
function canleAction(){
	$("#createArea").css("display","none");
	return false;
}

function submitAcct(obj){
    
    var consignAmount = $("#acctInfo_CONSIGN_AMOUNT").val();
	if(!checkMoney(consignAmount)){
		alert("请输入正确的金额格式！");
		$("#acctInfo_CONSIGN_AMOUNT").val('');
		return false;
	}
		
	 if($("#acctInfo_BANK_CODE").val() == '' && $("#acctInfo_PAY_MODE_CODE").val() != '0')
   {
   		alert('银行名称不能为空');
   		return false;
   }
	 if(obj=='2'){//修改
		 if($("#POP_acctInfo_BANK_CODE").val() == '' && $("#acctInfo_PAY_MODE_CODE").val() != '0'){
			alert('银行名称不能为空');
   			return false;
		}
	 }

	 var groupcustcode = 0;
	var check = 0;
	var acctSign = $("#acctInfo_CHECK").val();
	var resultz = false;
	if(acctSign=='1'){//如果同步到集团总部,则需要判断该集团是否为全网集团
        check = 1;
		$.beginPageLoading();
		var group_id = $("#GROUP_ID").val();
		$.ajax.submit('', 'queryIfNetGroup', '&GROUP_ID='+group_id, '', function(data)
				{
					$.endPageLoading();
					var flag = data.get("FLAG");
					if(flag=="0"){
						alert('该集团不是全网集团,不可同步集团总部');
                        resultz = true;
					}else{
						groupcustcode = data.get("GROUPCUSTCODE");
                    }
				},
				function(error_code,error_info)
				{
					$.endPageLoading();
					alert(error_info);
			    },{async:false});
	}

    if (resultz) {
        return false;
    }
	 
	if ($.validate.verifyAll('createArea')){
		$.beginPageLoading();
		$.ajax.submit('createArea','onSubmitBaseTrade','&state='+obj+'&productId='+$('#GROUP_PRODUCT_ID').val()+
			'&GROUPCUSTCODE='+groupcustcode + '&CHECK='+ check,'createArea',
				function(data)
			    { 
			        $.endPageLoading(); 
			        var tradeData;
			    	if(data instanceof $.DatasetList){
			    		tradeData = data.get(0);
			    	}else if(data instanceof $.DataMap){
			    		tradeData = data;
			    	}
			        var x_resultcode = tradeData.get("x_resultcode","0");
			        if(x_resultcode == '-1'){
			      	  ifcheck= false;
			      	  alert(data.get("x_resultinfo"));
			      		return;
			        }
			        if(tradeData.get("HAS_PAYMODE_CHGPRIV", false)){
			        	alert("您已特殊办理银行托收业务，为避免停机给您造成不便，请您务必及时缴清往月欠费。");
			        }
			        showSuccess(data);
			        selectGroupErrorAfterAction(); 
				},
				function(error_code,error_info,derror)
				{
					$.endPageLoading(); 
					$.showMessage("error", "业务受理失败", "错误编码："+error_code+"\n错误信息："+error_info);  
			    }
			 );   
	}else{
		return false;
	}
} 
/**提示信息*/
function showMessageBox(mess, mb){
	MessageBox.show({
	           title:'系统提示：',
	           msg: mess,
	           buttons: MessageBox.OK  
	       });
} 
    

/**查询之后显示新增按钮*/
function displaybtnCreate(){  
   $("#bcreateTop").css("display","");
   $("#bcreateTop1").css("display","none");//隐藏跳转过来的
   $("#createArea").css("display","none");  //账户明细
}
$(function(){
    $("#POP_cond_GROUP_ID").focus();
    $("#createArea").css("display","none");
});
    
function queryAcctInfoList(){    
	$.ajax.setup({async:false});
	$.beginPageLoading();
	$.ajax.submit('GroupInfoPart','queryAcctInfoList', '','acctArea', function(data){ 
		$.endPageLoading();
	},
	function(error_code,error_info,derror){ 
		showDetailErrorInfo(error_code,error_info,derror);
		$.endPageLoading();
    });
	$.ajax.setup({async:true});
}
//集团资料查询失败后调用的方法
function selectGroupErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();
    $('#acctInfos').html('');
    $('#createArea').css("display","none");
    $("#bcreateTop").css("display","none");  //新增按钮

}

/**
用途：检查输入字符串是否符合金额格式 
格式定义为整数
*/
function checkMoney(obj){
	var regu = "^[0-9]*(\.[0-9]{1,2})?$"; 
	//var regu = "^[0-9]*$"; 
	var reg = new RegExp(regu); 
	
	if (reg.test(obj)) { 
		return true; 
	} else { 
		return false; 
	} 
}

function changeSmsFlag(){
	var smsFlag = $("#acctInfo_CHECK").attr("checked");
	if (smsFlag == true) {
		$('#acctInfo_CHECK').attr('value','1');
	} else {
		$('#acctInfo_CHECK').attr('value','0');
	}
}