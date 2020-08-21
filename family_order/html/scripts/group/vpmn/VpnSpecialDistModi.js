//成员号码资料查询成功后调用的方法
function selectMemberInfoAfterAction(data){ 
  if(data == null)
	return; 
    var memcustInfo = data.get("MEB_CUST_INFO");
    var memuserInfo = data.get("MEB_USER_INFO"); 
    var memuseracctdayInfo = data.get("MEB_ACCTDAY_INFO"); 
    //生成成员客户信息
	insertMemberCustInfo(memcustInfo);
	//生成成员用户信息
	insertMemberUserInfo(memuserInfo);
	//生成成员的账期信息
	insertUserAcctDayInfo(memuseracctdayInfo); 
    getVpnInfo();
}
 

//成员资料查询失败后调用的方法
function selectMemberInfoAfterErrorAction() {

	clearMemberCustInfo();
	clearMemberUserInfo(); 
	clearUserAcctDayInfo();
}

function getVpnInfo(){ 
	$.beginPageLoading();
	$.ajax.submit('QueryCondPart','queryVpnInfo', '','acctArea,HiddenPart', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function unedit(){ 
	$('#DISCNTTYPET').val('');
	if(''!=$('#DISCNTTYPE').val() && null!=$('#DISCNTTYPE').val())
	{
		$('#DISCNTTYPET').attr('disabled',true);
		
		//modify for diversify_acct
		var hintinfo = '下月1日生效，下月按新的套餐计费'; 
		var firstdaynextacctday = $('#USER_ACCTDAY_NEXT_FIRST_DATE').val(); 
		if(''!=firstdaynextacctday)
			hintinfo = '下账期'+firstdaynextacctday+'生效,按新的套餐计费';
		 
		if(!confirm(hintinfo))
		{ 
			$('#DISCNTTYPE').val(''); 	
		}
	}
	
	if(''==$('#DISCNTTYPE').val() || null==$('#DISCNTTYPE').val())
	{ 		
		$('#DISCNTTYPET').attr('disabled',false);
	} 	
}
function uneditt(){  
	$('#DISCNTTYPE').val('');
	if(''!=$('#DISCNTTYPET').val() && null!=$('#DISCNTTYPET').val()){ 
		$('#DISCNTTYPE').attr('disabled',true);
		if(!confirm('收取两个套餐功能费，分钟数分别赠送，不套算'))
		{ 
			$('#DISCNTTYPE').val(''); 
		}
	}
	if(''==$('#DISCNTTYPET').val() || null==$('#DISCNTTYPET').val())
	{
		$('#DISCNTTYPE').attr('disabled',false);
	}
}
function onSubmitBaseTradeCheck(){  
	if(''==$('#DISCNTTYPET').val() && ''==$('#DISCNTTYPE').val())
	{ 
		showDetailErrorInfo('0','请选择要变更的套餐!','请选择要变更的套餐!');
		return false;
	} 
	var authTag = $("#GROUP_AUTH_FLAG").val();
	if(authTag!= 'true'){
		alert('号码未验证，请验证！');
		groupAuthStart();
		return false;
	}
	return true; 
	
	 
}

//USERCHECK查询失败后调用的方法
function userCheckErrAction(state,data) {
	selectMemberInfoErrAfterAction();
	
	if(state == 'USER_CUSTOM'){//网外号码
		$("#GROUP_AUTH_FLAG").val("true");
		queryMemberInfo();
		return;
	}
}
//-------------------------------------------
function init() {
	var custName = $('#CUST_NAME').val();
	if(custName ==''){
		$('#bsubmit').attr('disabled','true');	
	}
	else {
		$('#bsubmit').attr('disabled','false');
	}
	
}
 
function authAfterFunction() {
	var flag = $('#GROUP_AUTH_FLAG').val();
	var group = $('#cond_SERIAL_NUMBER').val();
	
	if (flag == "true") {
	var nvf=Wade.nav.getActiveNavFrame();
	nvf=nvf?nvf:parent;
	
		if(nvf){
        	 //$('#otherMemSerial').click(); 
        	$('#otherMemSerial').click(); 
			//ajaxDirect4CS(this, 'queryVpnInfo', '&cond_SERIAL_NUMBER='+group, 'VpnPart,HiddenPart',false,ajaxSubVpnInfo); 
       	}
   	}
   	else {
   		return false;
   	}
      
}

function ajaxSubVpnInfo(){
    var group = $('#cond_SERIAL_NUMBER').val();
	$('#TRADE_TYPE_CODE').val("3035");
	$('#AUTH_SERIAL_NUMBER').val(group);
	
	$('#authButton').click();
	return false;
}

	 