$(document).ready(function(){
	$("#simCardReUsed").attr("disabled",true);
	$("#writeCard").attr("disabled",true);
	$("#readCard").attr("disabled",true);
});


//认证组件查询之后回调的js方法
function refreshPartAtferAuthByRemote(data)
{  
	alert("1111111");
	var param ="&USER_INFO="+(data.get("USER_INFO")).toString();
	param=param+"&CUST_INFO="+(data.get("CUST_INFO")).toString();
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'RestoreUseRemoterPart1,RestoreUseRemoterPart2,RestoreUseRemoterPart3,hiddenPart', function(){
		
		//写卡相关按钮展示
		$("#simCardReUsed").attr("disabled",false);
		$("#writeCard").attr("disabled",false);
		$("#readCard").attr("disabled",false);
		var simcheck = $("#SIM_CHECK_TAG").val();
		if(simcheck != "0")
		{
			alert("该用户的原SIM非正常，需要更换新SIM卡号才能复机！");
			$('#OPER_TAG').val("2");
			$("#CSSUBMIT_BUTTON").attr("disabled",true);
		}else
		{
			$("#CSSUBMIT_BUTTON").attr("disabled",false);
			$("#CSSUBMIT_BUTTON").attr("className","e_button-page-ok");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}



//点击表格中某资源信息
function tableRowClick(obj) {
	checkDisabledOper($("#checkResBt"),1); //资源校验按钮不可操作
	
	var rowIndex = obj.rowIndex;//当前操作行
	var resTableObj = $.table.get("ResTable");//获取 资源表格对象
    var json = resTableObj.getRowData(null,rowIndex); //获得当前行数据
    var res_type_code = json.get("col_RES_TYPE_CODE");//当前行资源类型
    var res_code = json.get("col_RES_CODE");//当前行资源编码
    
    $("#rowIndex").val(rowIndex);//保存选择 index  后续【资源校验后】 时需要
    $("#IMSI").val(json.get("col_IMSI"));
    $("#RES_CODE").val(json.get("col_RES_CODE"));
    $("#RES_TYPE_CODE").val(json.get("col_RES_TYPE_CODE"));
	$("#START_DATE").val(json.get("col_START_DATE"));
	$("#END_DATE").val(json.get("col_END_DATE"));
	
	if(res_type_code=='0')
	{
		alert('服务号码不可以变更，业务无法继续！');
		return;
	}
	
	var simcheck = $("#SIM_CHECK_TAG");
	if(simcheck.val() != "0")
	{
		alert("该用户的原SIM非正常，需要更换新SIM卡号才能复机！");
	}else
	{
		//alert("该用户的原SIM正常，可以使用原SIM卡号才能复机！");
	}
	checkDisabledOper($("#checkResBt"),2);
	return;
}


//资源校验 按钮事件
function checkRes() 
{
	var newResCode =  $('#RES_CODE').val();//输入的资源编码
	if(newResCode==null || newResCode=="")
	{
		alert('请输入新资源号码再进行校验！');
		return false;
	}
	
	var oldSimCard = $('#OLD_SIM_CARD_NO').val();//用户原sim卡
    var oldPhone = $('#OLD_PHONE_NO').val();//用户原号码
    var resType = $('#RES_TYPE_CODE').val();//当前表格选择行的资源类型编码
    
    if(resType == "")
    {
      alert("无法获取到当前校验资源的类型，请重新点击资源信息表格中需要更换的数据！");
      return false;
    }
    
    
	if(resType == "0" && newResCode == oldPhone)
	{
		alert('请输入新手机号码再进行校验！');
		return false;
	}else if(resType == "1" && newResCode == oldSimCard)
	{
		alert('请输入新的SIM再进行校验！');
		return false;
	}
	
	var param = '&RES_TYPE_CODE='+resType+'&RES_CODE='+newResCode+'&OLD_SIM_CARD_NO='+oldSimCard
					+'&OLD_PHONE_NO='+oldPhone+'&PRODUCT_ID='+$('#MAIN_PRODUCT_ID').val();
	$.beginPageLoading("正在校验数据...");
	$.ajax.submit(null, 'checkNewResource',param, null, function(data){
		afterCheckNewRes(data);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
  });
}

//资源校验后
function afterCheckNewRes(data)
{
	if(data.get("RESULT_CODE")=="N")
	{
		alert("新资源校验失败！");
		return false;
	}
	
	var resType = $('#RES_TYPE_CODE').val();//当前操作的资源类型
	var imsi = data.get("IMSI");
	var ki = data.get("KI");
	var serialNumber = data.get("SERIAL_NUMBER");
	var startDate = data.get("START_DATE");
	var endDate =  data.get("END_DATE");
	if((imsi==null || imsi=="") && (serialNumber==null || serialNumber==""))
	{
		alert('该资源不存在或者已经被占用，请输入新资源号！');
		return false;
	}else
	{
		if(resType == 0)//手机号码
		{
			var rsrvtag3 =  data.get("RSRV_TAG3");
			alert('新服务号码和用户原SIM卡不匹配，请换卡！');
			return false;
		}
		alert('校验通过！');
		
		$('#OPER_TAG').val("2");
		$('#IMSI').val(imsi);
		$('#START_DATE').val(startDate);
		$('#END_DATE').val(endDate);
		$('#OPC_VALUE').val(data.get("OPC_VALUE"));
		
		//删除费用
		$.feeMgr.removeFee("310","0","10"); 
		
		//获取资源费用
		var thefee =  data.get("DEVICE_PRICE");
		if(thefee && thefee!=null && thefee!="" && parseInt(thefee)) {
			var feeData = $.DataMap();
			feeData.put("MODE","0");
			feeData.put("CODE","10");
			feeData.put("FEE",thefee);
			feeData.put("TRADE_TYPE_CODE","310");				
			$.feeMgr.insertFee(feeData);//新增费用
		}
		

		 //修改选择行的数据
		 var tableObj = $.table.get("ResTable");
		 var nowIndex =$("#rowIndex").val();
		 var rowEdit = $.ajax.buildJsonData("RestoreUseRemoterPart3");
		 rowEdit["col_RES_CODE"]=$('#RES_CODE').val();
		 rowEdit["col_START_DATE"]=startDate;
		 rowEdit["col_END_DATE"]=endDate;
		 rowEdit["col_IMSI"]=imsi;
		 rowEdit["col_KI"]=ki;
		 var updaterow = tableObj.updateRow(rowEdit);
		 
		 var thesubmit = $('#CSSUBMIT_BUTTON');
		 if(thesubmit != null && thesubmit != "")
		 {
			thesubmit.attr("disabled",false);
		    thesubmit.attr("className","e_button-page-ok");
		 }
	}
}


/******************************************* 远程写卡系列函数开***************************************************/

//写卡按钮事件    0-写卡 1-读卡
function writeCardWithRestore()
{
	var is3GUser = $("#IS_TD_USER").val();	//是否3G用户：0-否 1-是
	invokeCard(0, 'afterinvokeCard', is3GUser, 310);
}

//读卡按钮事件
function readCardWithRestore()
{
	var is3GUser = $("#IS_TD_USER").val();	//是否3G用户：0-否 1-是
	invokeCard(1, 'afterinvokeCard', is3GUser, 310);
}

//sim卡重利用
function simCardReuseCard()
{
	try{
		var is3GUser = $("#IS_TD_USER").val();	//是否3G用户：0-否 1-是
		reuseCard('afterinvokeCard',is3GUser,'310');
	}catch(e)
	{
		$.endPageLoading();
	}
}

/*回调函数：SIM再利用、写卡公用回调函数*/
function afterinvokeCard(obj){
	var newSimCardNo = obj.get('SIM_CARD_NO');
	var oldSimCard = $('#OLD_SIM_CARD_NO').val();//用户原sim卡
    var oldPhone = $('#OLD_PHONE_NO').val();//用户原号码
    $('#RES_CODE').val(newSimCardNo);
	var param = '&RES_TYPE_CODE=1&RES_CODE='+newSimCardNo+'&OLD_SIM_CARD_NO='+oldSimCard+'&OLD_PHONE_NO='+oldPhone+'&PRODUCT_ID='+$('#MAIN_PRODUCT_ID').val();
	$.beginPageLoading("正在校验数据...");
	$.ajax.submit(null, 'checkNewResource',param, null, function(data){
		afterCheckNewRes(data);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
  });
	
}

/******************************************* 远程写卡系列函数结束 ****************************************************/

//提交 台账
function prepareRestore()
{
	var data = $.table.get("ResTable").getTableData(null,true);//获取整个表格数据
	$("#X_CODING_STR").val(data) ;		
	return true
}


/******************************工具方法*****************************************/
//设置某个原始的状态
function checkDisabledOper(obj,tag){
	if(tag == 1){
		obj.attr("disabled",true);
		obj.attr("className","e_button-right e_dis");
		
	}
	if(tag ==2 ){
		obj.attr("disabled",false);
		obj.attr("className","e_button-right");
	}
}