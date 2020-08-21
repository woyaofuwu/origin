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

function createRelationAA(){
	var items = getCheckedValues("itemcodes");
	var itemsNum = getCheckedBoxNum("itemcodes");
	
	//是否明细账目全选
	var flag = $('#newSnInfo_CheckAll').attr('checked');
	//用户类型
	var userType = $("#pam_USER_TYPE").val();
	//统付账户
	var acctIdA = $("#pam_NOTIN_ACCT_ID_A").val();
	//被统付账户
	var acctId = $("#pam_NOTIN_ACCT_ID").val();
	//限定类型
	var limitType = $("#pam_NOTIN_LIMIT_TYPE").val();
	//限定值
	var limit = $("#pam_NOTIN_LIMIT").val();
	
	var custId = $("#pam_NOTIN_CUST_ID").val();

	
	
	if(acctId == ""){
		alert("请选择被支付账户！");
		return false;
	}
	
	if("" == limit){
		alert("限定值不能为空！");
		return false;
	}
	
	if(acctIdA == acctId){
		alert("该账户不能为自己统付！");
		return false;
	}
	
	if(!flag){
		if(itemsNum < 1){
			alert("没有选择明细帐目，无法完成拼串！");
			return false;
		}
	}
	
	if(flag &&  itemsNum > 0){
		alert("【明细账目列表】和【账目全选】不能同时勾选！");
		return false;
	}
	
	$.beginPageLoading("提交中......");
	$.ajax.submit('','createRelationAA','&ACCT_ID_A='+acctIdA+"&ACCT_ID_B="+acctId+"&LIMIT="+limit+"&LIMIT_TYPE="+limitType+
	"&FLAG="+flag+"&ITEM_CODES="+items+"&USER_TYPE="+userType+"&CUST_ID="+custId
	,'', function(data){
		$.endPageLoading(); 
		successMessage(data);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
}


function successMessage(data){ 
	var result = data.get(0);
	MessageBox.alert("账户关系绑定成功","业务流水号:"+result.get("ORDER_ID"),function(btn){
		setPopupReturnValue();
	});
}

function cancel(){
	setPopupReturnValue();
}


function getNoteItemList(e){
	var acctId = e.getAttribute("ACCT_ID");
    var eparchyCode = e.getAttribute("EPARCHY_CODE");
	if(acctId==""){
    	alert('账号为空！');
      	return false;
    }
    //被统付账户设置
    $("#pam_NOTIN_ACCT_ID").val(acctId);
    
    $.ajax.submit('','getNoteItemList', '','groupAccountPayInfo', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
}


/*综合帐目列表过滤*/
function filterNoteItems()
{
	var noteItem = $('#newSnInfo_NOTE_ITEM').val();
	
	$.beginPageLoading('正在帐目列表过滤...');
	$.ajax.submit(this,'filterNoteItems','&NOTE_ITEM='+noteItem,'groupAccountPayInfo',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}



function getAcctInfoBySn(){
	var serialNum = $("#cond_SERIAL_NUMBER").val();
	$.ajax.submit('groupAccountInfo','getAcctInfoBySn', '&SERIAL_NUMBER='+serialNum,'groupAccountInfo', function(data){
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



