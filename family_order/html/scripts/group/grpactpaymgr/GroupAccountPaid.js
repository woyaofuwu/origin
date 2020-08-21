// 集团资料查询成功后调用的方法
function selectGroupAfterAction(data){
	// 填充集团客户显示信息
	insertGroupCustInfo(data);
	var cust_id = data.get("CUST_ID");
	$("#pam_NOTIN_CUST_ID").val(cust_id);
	$.ajax.submit('groupAccountInfo','queryGroupAccountInfo', '&CUST_ID='+cust_id,'groupAccountInfo', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


// 集团资料查询成功后调用的方法
function selectGroupAfterActionByEdit(data){
	// 填充集团客户显示信息
	var cust_id = data.get("CUST_ID");
	$("#pam_NOTIN_CUST_ID").val(cust_id);
	$.ajax.submit('','queryGroupAccountInfo', '&CUST_ID='+cust_id,'groupAccountInfo', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });	
}

function refresh(){
	var cust_id = 	$("#pam_NOTIN_CUST_ID").val();
	$.ajax.submit('groupAccountInfo','queryGroupAccountInfo', '&CUST_ID='+cust_id,'groupAccountInfo', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

// 集团资料查询失败后调用的方法
function selectGroupErrorAfterActionByEdit(data){
	// 清空填充的集团客户信息内容
    clearGroupCustInfo();
}


function createRelationAA(){
	var acctId = $("#pam_NOTIN_ACCT_ID_A").val();
	var eparchyCode = $("#pam_NOTIN_EPARCHY_CODE_A").val();
	var custId = $("#pam_NOTIN_CUST_ID").val();
	if(acctId == null || acctId ==""){
		alert('请选择一个统付账户！');
		return false;
	}
	popupPage('group.grpactpaymgr.GroupAcctPaidEdit&listener=initial', '','&ACCT_ID_A='+acctId+'&EPARCHY_CODE_A='+eparchyCode+'&refresh=true&CUST_ID='+custId,'账户代付新增', '800', '600');
}


function getRelationAA(e){
	var acctId = e.getAttribute("ACCT_ID");
    var eparchyCode = e.getAttribute("EPARCHY_CODE");
	if(acctId==""){
    	alert('账号为空！');
      	return false;
    }
    $("#pam_NOTIN_ACCT_ID_A").val(acctId);
    $("#pam_NOTIN_EPARCHY_CODE_A").val(eparchyCode);
	
	$.ajax.submit('','getRelationAA', '&ACCT_ID_A='+acctId+'&EPARCHY_CODE_A='+eparchyCode,'groupAccountPayInfo', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
    
}

function delRelationAA(){
	var checkedValues = getCheckedValues("relationAA");
   	var checkedNum = getCheckedBoxNum("relationAA");
  	if(checkedNum > 1){
  		alert('你选择了'+checkedNum+'个付费关系，'+'一次只能删除一个！');
  		return false;
  	}else if(checkedNum == 0 ){
  		alert('请选择需要删除的账户关系！');
  		return false;
  	}
  	
  	$.beginPageLoading("提交中......");
  	$.ajax.submit('','delRelationAA', '&CHECKED_VALUES='+checkedValues,'groupAccountPayInfo', function(data){
  		successMessage(data);
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
  	
}

function successMessage(data){ 
	var result = data.get(0);
	MessageBox.alert("账户关系绑定成功","业务流水号:"+result.get("ORDER_ID"),function(btn){
	});
}



function getNoteItemList(e){
	var acctId = e.getAttribute("ACCT_ID");
    var eparchyCode = e.getAttribute("EPARCHY_CODE");
	if(acctId==""){
    	alert('账号为空！');
      	return false;
    }
    
    $.ajax.submit('','getNoteItemList', '','groupAccountPayInfo', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
}





function changeUserType(){
	var radioItems = document.all('USERTYPE');
	var queryTypeOne = document.getElementById("QueryTypeOne");
	var queryTypeTwo = document.getElementById("QueryTypeTwo");
	
	if(radioItems[0].checked == true){
		queryTypeOne.style.display='';
	    queryTypeTwo.style.display='none';
	    $("pam_USER_TYPE").val("0");
	}else if(radioItems[1].checked == true){
		queryTypeOne.style.display='none';
	    queryTypeTwo.style.display='';
	    $("pam_USER_TYPE").val("1");
	}
	
}

