
function changeSameAcctTag() {
		
}

//填充账户详细
function insertAcctDescInfo(data) {
	
	$('#acct_PAY_NAME').val(data.get('PAY_NAME'));
	$('#acct_PAY_MODE_NAME').val(data.get('PAY_MODE_NAME'));
	$('#acct_SUPER_BANK_NAME').val(data.get('SUPER_BANK'));
	$('#acct_BANK_NAME').val(data.get('BANK'));
	$('#acct_CONTRACT_NO').val(data.get('CONTRACT_NO'));
	$('#acct_BANK_ACCT_NO').val(data.get('BANK_ACCT_NO'));
	$('#acct_ALLNEW_BALANCE').val(data.get('ALLNEW_BALANCE'));
	
}
//清空账户详情
function cleanAcctDescInfo() {
	
	$('#acct_PAY_NAME').val('');
	$('#acct_PAY_MODE_NAME').val('');
	$('#acct_SUPER_BANK_NAME').val('');
	$('#acct_BANK_NAME').val('');
	$('#acct_CONTRACT_NO').val('');
	$('#acct_BANK_ACCT_NO').val('');
	$('#acct_ALLNEW_BALANCE').val('');
}


function createAcct(){
	$.popupPageExternal('group.creategroupacct.CreateGroupAcct','initGroupCustInfo', '&cond_CUST_ID='+$('#GRPPAYACCOUNTEDIT_CUST_ID').val()+'&cond_PRODUCT_ID='+$('#GRPPAYACCOUNTEDIT_PRODUCT_ID').val(),'账户管理','820','650','GRPPAY_ACCOUNTEDIT_REFRESH',null,false);
}

/**
 * 合户时 按集团编码
*/
function  refeshAcctDetailInfoByAcctId(){
	cleanAcctDescInfo();
	var acctId = $('#acct_ACCT_ID').val();
	var productId = $('#GRPPAYACCOUNTEDIT_PRODUCT_ID').val();
	if(acctId == '')
	{	
		return false;
	}
	if(productId == '')
	{	
		return false;
	}
	
	$.beginPageLoading();
	hhSubmit(null,"com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.grppayaccountedit.GrpPayAccountEditHttpHandler","getAcctByActId", "&ACCT_ID="+$("#acct_ACCT_ID").val()+"&LISTENER=getAcctByActId"+"&PRODUCT_ID="+productId, 
	function(data){
		if(data != null){
			insertAcctDescInfo(data);
		}
		$.endPageLoading();
		
		
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
}


/**
 * 合户 按服务号码查询
*/
function  refeshAcctBySn(){

	var serialNumber = $('#acct_GRP_SERIAL_NUMBER').val();
	if(serialNumber == '' || serialNumber == null){
		alert('服务号码不能为空\uFF01');
		return false;
	}
	if (/^[1][13458][0-9](\d{8})$/.test(serialNumber)) {
		$.beginPageLoading();
		var compid = $('#GRPPAYACCOUNTEDIT_COMPONENT_ID').val();
		$.ajax.submit(null,null,'&GRP_SERIAL_NUMBER='+serialNumber+'&LISTENER=getAcctByPsnSn',compid+'.OtherAcctPart',function(data){
			$.endPageLoading();
			var resultcode = data.get('X_RESULTCODE','0');
	   		if(resultcode!='0'){
	    	
	   			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
	   		}
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
		
	}
	else
	{
		$.beginPageLoading();
		var compid = $('#GRPPAYACCOUNTEDIT_COMPONENT_ID').val();
		$.ajax.submit(null,null,'&GRP_SERIAL_NUMBER='+serialNumber+'&LISTENER=getAcctByGrpSn',compid+'.OtherAcctPart',function(data){
			$.endPageLoading();
			var resultcode = data.get('X_RESULTCODE','0');
	   		if(resultcode!='0'){
	    	
	   			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
	   		}
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	}
}

/**
 * 合户时 按合同号查询
*/
function  refeshAcctByContract(){
	if($('#acct_ACCT_NUMBER').val() == '')
	{
		alert('请输入合同号\uFF01');
		return false;
	}
	 
	$.beginPageLoading();
	var compid = $('#GRPPAYACCOUNTEDIT_COMPONENT_ID').val();
	$.ajax.submit(null,null,'&ACCT_CONTRACT_NO='+$('#acct_ACCT_NUMBER').val()+'&LISTENER=getAccountInfoByContract',compid+'.OtherAcctPart',function(data){
		$.endPageLoading();
		var resultcode = data.get('X_RESULTCODE','0');
   		if(resultcode!='0'){
    	
   			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
   		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
}

/**
 * 合户时 按集团编码
*/
function  refeshAcctByCstId(){
	if($('#POP_cond_GROUP_ID').val() == '')
	{
		alert('请输入集团客户编码\uFF01');
		return false;
	}
	
	$.beginPageLoading();
	var compid = $('#GRPPAYACCOUNTEDIT_COMPONENT_ID').val();
	$.ajax.submit(null,null,'&POP_cond_GROUP_ID='+$('#POP_cond_GROUP_ID').val()+'&LISTENER=getAcctByGrpId',compid+'.OtherAcctPart',function(data){
		$.endPageLoading();
		var resultcode = data.get('X_RESULTCODE','0');
   		if(resultcode!='0'){
    	
   			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
   		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


function renderGrpPayAccountEditPart(custId){
	$('#GRPPAYACCOUNTEDIT_CUST_ID').val(custId);
	refresh();
}

/**
 * 按集团客户编码CUSTID查询
*/
function  refresh(){
	cleanAcctDescInfo();
	var custId = $('#GRPPAYACCOUNTEDIT_CUST_ID').val()
	if(custId == '')
	{
		return false;
	}
	
	$.beginPageLoading();
	var compid = $('#GRPPAYACCOUNTEDIT_COMPONENT_ID').val();
	$.ajax.submit(null,null,'&cond_CUST_ID='+custId+'&LISTENER=getAcctByCustId',compid+'.OtherAcctPart',function(data){
		$.endPageLoading();
		var resultcode = data.get('X_RESULTCODE','0');
   		if(resultcode!='0'){
    	
   			$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
   		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

/**
 * 选择不合户时，不同的查询条件
*/
function changeQueryType() {
   var choose=$("#QueryType").val();
   if (choose=="1") { //按集团编码
      $("#QueryTypeOne").css("display","");
      $("#QueryTypeTwo").css("display","none");
      $("#QueryTypeThree").css("display","none");
      $('#POP_cond_GROUP_ID').val("");
      $('#acct_GRP_SERIAL_NUMBER').val("");
      $('#acct_ACCT_NUMBER').val("");      
   }else if (choose=="2") { //按服务号码
      $("#QueryTypeOne").css("display","none");
      $("#QueryTypeTwo").css("display","");
      $("#QueryTypeThree").css("display","none");      
      $('#POP_cond_GROUP_ID').val("");
      $('#acct_GRP_SERIAL_NUMBER').val("");
      $('#acct_ACCT_NUMBER').val("");          
   }else if( choose=="3")//按合同号
   {
      $("#QueryTypeOne").css("display","none");
      $("#QueryTypeTwo").css("display","none");
      $("#QueryTypeThree").css("display","");  
      $('#POP_cond_GROUP_ID').val("");
      $('#acct_GRP_SERIAL_NUMBER').val("");
      $('#acct_ACCT_NUMBER').val("");          
   }   
}

function queryAcct()
{
	var choose=$("#QueryType").val();
	if (choose=="1") { //按集团编码
		refeshAcctByCstId();
	}
	else if (choose=="2") { //按服务号码
		refeshAcctBySn();
    }
    else if( choose=="3") {//按合同号
    	refeshAcctByContract();
    }
}
	


//账户资料检查 1合户 0不合户
function checkAcctInfo()
{
	var acctId = $("#acct_ACCT_ID").val();
	
	if(acctId == "") {			
		
		alert('请选择账户信息下的[账户列表]中的账户！');
		return false;
							
	}
	
	return true;
}