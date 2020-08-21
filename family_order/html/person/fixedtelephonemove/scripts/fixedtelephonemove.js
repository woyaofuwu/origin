/**
 * 选择固话号码action
 * 需要把局向传入
 */
function selSerialNumberAction()
{
	var staffId = top.$("#staffId").val();
	var deptId = top.$("#deptId").val();
	var loginEpachyId = top.$("#loginEpachyId").val();
	
	//TODO:先注释掉资源的号码选择界面,自己造测试数据
	$.popupPage('brm.officeNumberSelect', 'init', '&refresh=true&cond_FOFFICE_ID='+$("#SIGN_PATH").val()+'&cond_STAFF_IF_IN='+staffId+'&cond_OPER_DEPART_ID='+deptId+'&cond_EPARCHY_CODE='+loginEpachyId, '选择固话号码', '620', '400','NEW_SERIAL_NUMBER','',subsys_cfg.pbossintf);
	
	//selSerialNumberAfterAction();
}


/**
 * 换号选择事件
 */
function checkChangeNbr()
{
	var selSerObj = document.getElementById("NEW_SERIAL_NUMBER"); //固话号码选择input对象
	var serialNumberTdObj = document.getElementById("serialNumberTD"); //固话号码选择TD对象
	
	var changeNbrHintTd = document.getElementById("changeNbrHintTD"); //换号提醒checkbox所在 TD对象
	var changeNbrHintObj = document.getElementById("CHANGE_NUMBER_HINT"); //换号提醒checkbox对象
	
	var changeNbrMonthObj = document.getElementById("CHANGE_NUMBER_MONTH"); //提醒时间select对象
	var changeNbrMontTD = document.getElementById("changeNbrMonthTD"); //提示时间select所在TD对象
	
	var newNumberObj = document.getElementById("NEW_SERIAL_NUMBER"); //固话号码
	var resType = document.getElementById("RES_TYPE_CODE"); //资源类型（大类）
	var resKind = document.getElementById("RES_KIND_CODE"); //资源类型（小类）
	var switchId = document.getElementById("SWITCH_ID"); //交换机编号
	var switchType = document.getElementById("SWITCH_TYPE"); //交换机类型
	
	
	var changeNbrObj = document.getElementById("CHANGE_NUMBER"); //固话号码选择checkbox对象
	if(changeNbrObj.checked)
	{
		serialNumberTdObj.style.display = "";
		changeNbrHintTD.style.display = "";
		
		newNumberObj.nullable="no";
		//resType.nullable = "no";
		resKind.nullable = "no";
		switchId.nullable = "no";
		switchType.nullable = "no";
	}else{
		serialNumberTdObj.style.display = "none";
		changeNbrHintTd.style.display = "none";
		changeNbrMontTD.style.display = "none";
		
		changeNbrHintObj.checked = false; //设置换号提醒checkbox不选中
		
		newNumberObj.nullable="yes";
		resType.nullable = "yes";
		resKind.nullable = "yes";
		switchId.nullable = "yes";
		switchType.nullable = "yes";
		
		selSerObj.value = ""; //清空新固话号码显示
		newNumberObj.value = ""; //清空新号码值
		resType.value = ""; //清空资源大类值
		resKind.value = ""; //清空资源小类值
		switchId.value = "";	//清空交换机编号
		switchType.value = "";	//清空交换机类型
	}
}

/**
 * 换号提醒选择事件
 */
function checkChangeNbrHint()
{
	var changeNbrHintObj = document.getElementById("CHANGE_NUMBER_HINT");	//换号提醒checkbox
	var changeNbrMonthTD = document.getElementById("changeNbrMonthTD");	//换号提醒td
	var changenbrMonthObj = document.getElementById("CHANGE_NUMBER_MONTH");	//换号提醒select
	
	if(changeNbrHintObj.checked)	//换号提醒checkbox，展示换号提醒td
	{
		changeNbrMonthTD.style.display = "";
	}else{
		changeNbrMonthTD.style.display = "none";
		changenbrMonthObj.options[0].selected = true;
	}
}


/**
 * 选择换号提醒时间
 */
function selectHintMonth(){	
	var viewFee = $.feeMgr.getViewFee();
	if(viewFee && viewFee.get("FEE_LIST")){
		var feeList = viewFee.get("FEE_LIST");
		feeList.each(function(item, index, totalCount){
			if(item.get("CODE") == "9004"){
				$.feeMgr.removeFee("9703","0","9004");//移除fee_type_code:9004的费用
			}
			return true;
		});
	}
	
	
	//计算改号告知fee：1一个月6元，最多3个月
	var month = parseInt($("#CHANGE_NUMBER_MONTH").val());
	if(month>0){
		var monthFeeObj = $.DataMap();
		monthFeeObj.put("MODE","0");
		monthFeeObj.put("CODE","9004");
		monthFeeObj.put("FEE",month*600);
		monthFeeObj.put("TRADE_TYPE_CODE","9703");
		$.feeMgr.insertFee(monthFeeObj);
	}
	$("#MONTH").val(month);
	
}

/**
 * 选择标志地址
 */
function afterSelectStandAddress()
{
	$("#STAND_ADDRESS").val($("#POP_cond_REGION_ID").val());  //赋值标准地址
	$("#STAND_ADDRESS_CODE").val($("#cond_REGION_ID").val());	//赋值标准地址编码
	$("#DETAIL_ADDRESS").val($("#POP_cond_REGION_ID").val());	//赋值详细地址
	//$("#SIGN_PATH").val($("#foffice_id").val());	//赋值新局向
	//alert("4444");
	var chgNbrObj = $("#CHANGE_NUMBER"); //换号checkbox
	var signPathObj = $("#SIGN_PATH");	  //新局向
	var oldSignPathObj = $("#OLD_SIGN_PATH");	  //旧局向
	
	if(signPathObj.value!='' && signPathObj.value!=oldSignPathObj.value)//新局向和原局向不同一定要换号
	{
		chgNbrObj.checked = true; //设置换号checkbox选中
		chgNbrObj.disabled = true;	//设置换号checkbox disabled
	}else{
		chgNbrObj.checked = false;
		chgNbrObj.disabled = false;
	}
	
	//checkChangeNbr(); //调用换号checkbox事件
}



/**
 * 选择固话号码后afterAction事件
 */
function selSerialNumberAfterAction()
{	
	//先注释选号逻辑,自己造测试数据
	$("#NEW_SERIAL_NUMBER").val($("#SERIAL_NUMBER").val());
	$("#RES_TYPE_CODE").val($("#SN_RES_TYPE_CODE").val());
	$("#RES_KIND_CODE").val($("#SN_RES_KIND_CODE").val());
	$("#SWITCH_ID").val($("#SWITCH_ID").val());
	$("#SWITCH_TYPE").val($("#SWITCH_TYPE").val());
}

/**
 * 判断是否能提交
 * @return {Boolean}
 */
function canSubmit(obj){
	
	if(!verifyAll(obj))
	{
		return false;
	}

	var chgNbrObj = $("#CHANGE_NUMBER"); //换号
	if(chgNbrObj.attr("checked"))
	{
		//var oldSerialNumberObj = $("#SERIAL_NUMBER");
		var oldSerialNumberObj = $("#AUTH_SERIAL_NUMBER");
		var newSerialNumberObj = $("#NEW_SERIAL_NUMBER");
		
		if(oldSerialNumberObj.val() == newSerialNumberObj.val())
		{
			alert("新固话号码不能和原固话号码一样!");
			return false;
		}
	}
/*	var oldSignPath =  $("#OLD_SIGN_PATH").val();
	var newSignPath =  $("#SIGN_PATH").val();
	if(oldSignPath!=newSignPath){
		if(!chgNbrObj.attr("checked"))
		{
			alert("原地址和新地址的局向不一致，请进行换号操作!");
			return false;
		}
		//chgNbrObj.attr("checked") = true;
	}
	*/
	var changeNbrHint = $("#CHANGE_NUMBER_HINT");
	if(changeNbrHint.checked)//如果选择了开通换号提醒
	{
		var changeNbrMonthObj =  $("#CHANGE_NUMBER_MONTH"); //提醒时间
		if(changeNbrMonthObj.val() == '0')
		{
			alert("请选择换号提醒服务【提醒时间】!");
			return false;
		}
	}
	return true;
}

function afterChooseAddress(){
	var chgNbrObj = $("#CHANGE_NUMBER"); //换号
	var oldSignPath =  $("#OLD_SIGN_PATH").val();
	var newSignPath =  $("#SIGN_PATH").val();
		if(oldSignPath!=newSignPath){
			chgNbrObj.click();
			checkChangeNbr();

		}
}


function standAddressClick(){
	//afterSelectStandAddress();
	$("#STAND_ADDRESS_CODE").val($("#cond_REGION_ID").val());	//赋值标准地址编码
}

/**
 * 固话号码后查询获取页面用户信息
 */
function afterSubmitSerialNumber(data){
	$.ajax.submit('AuthPart', 'loadTradeInfo', "&USER_ID="+data.get("USER_INFO").get("USER_ID"), 'installInfoPart', function(data){
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}