//结合单独的Calendar组件使用时，设置Calendar组件的事件
var activeDateField; // 多个DateField使用同一个弹出日期组件时，将当前DateFieldid元素保存在变量中

// 刷新日期组件显示的日期
function setCalendar1Value(dateField){
	//获取当前操作dateField元素
	var el = dateField && dateField.nodeType == 1 ? dateField : document.getElementById(dateField);
	if(!el) return;
	calendar1.val(el.value); //设置日期组件值
	activeDateField = el;
}

// 绑定日期组件的日期选择事件
$("#calendar").select(function(e){
	if(activeDateField && activeDateField.nodeType){
		$(activeDateField).val( this.val() ); // 进行日期选择时候，把选择的日期值返回到DateField元素上
		// alert(this.lunarVal()); // 获取农历日期值
	}
});

// 绑定日期清除按钮事件
$("#calendar").clear(function(e){
	if(activeDateField && activeDateField.nodeType){
		$(activeDateField).val( "" );
	}
});

//加载页面集团信息
$(document).ready(function(){
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	
	$("#CLASS_NAME").html(custInfo.get("CLASS_NAME"));
	$("#GROUP_ADDR").html(custInfo.get("GROUP_ADDR"));
	$("#CITY_NAME").html(custInfo.get("CITY_NAME"));
	$("#CALLING_TYPE_NAME").html(custInfo.get("CALLING_TYPE_NAME"));
	$("#SUB_CALLING_TYPE_NAME").html(custInfo.get("SUB_CALLING_TYPE_NAME"));
	$("#EMAIL").html(custInfo.get("EMAIL"));
	$("#POST_CODE").html(custInfo.get("POST_CODE"));
	$("#RSRV_STR2").html(custInfo.get("RSRV_STR2"));
	$("#RSRV_STR7").html(custInfo.get("RSRV_STR7"));
});

function changeCalendar(){
	var value = $("#OPERATION").val();
	
	if(value == "1"){
		$("#date").css("display","");
	}else{
		$("#date").css("display","none");
	}
}

/*function showProducts(){
	
	
	$.beginPageLoading('正在查询用户信息...');
	$.ajax.submit(null,'queryUserInfoByGroupId','&GROUP_ID='+groupId,'OfferListPart',function(data){
		showPopup('mypop','chooseOfferItem');
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
	hidePopup($("#mypop"));
	
}*/

function qryLineInfos(){
	var groupInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	
	if(groupInfo == undefined || groupInfo == null || groupInfo==''){
		alert("请先登录集团外框！");
		return false;
	}
	
	var groupId = groupInfo.map.GROUP_ID;
	
	$.beginPageLoading('正在查询专线信息...');
	$.ajax.submit('qryInfo','qryLineInfos','&GROUP_ID='+groupId,'LineInfoPart',function(data){
		
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function checkCondition(obj){
	var objVal = $(obj).val();
	if(objVal != undefined && objVal!=null && objVal != ''){
		if(!$.isNumeric(objVal)){
			alert($(obj).attr("desc")+"不合法，请重新填写！");
			$(obj).val("");
			return false;
		}
	}
}

function submit(){
	var rowDatas = myTable.getCheckedRowsData("TRADES");
	
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"请您先选择需要处理的专线!");
		return false;
	}
	
	var productNoAll = "";
	for(var i=0;i<rowDatas.length;i++){
		var rowData = rowDatas.get(i);
		var productNo = rowData.get("PRODUCT_NO");
		productNoAll += productNo+",";
	}
	
	alert(productNoAll);
}