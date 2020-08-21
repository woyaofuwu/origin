$(document).ready(function(){
	$("#QUERY_BTN").unbind("click");
	$("#QUERY_BTN").bind("click",function(){
		queryInfos();
	});
});


function queryInfos(){

	var service_type = $("#SERVICE_TYPE").val();
	var state = $("#STATE").val();
	var create_time = $("#CREATE_TIME").val();
	var npcode_list = $("#NPCODE_LIST").val();

//	if(service_type == ""){
//		alert("请选择！NP业务类型！");
//		return false;
//	}
//	
//	if(state == ""){
//		alert("请选择！数据审计状态！");
//		return false;
//	}
//	
//	if(create_time == ""){
//		alert("数据生成时不能为空！");
//		return false;
//	}
//	
//	if(npcode_list == ""){
//		alert("Np号码不能为空！");
//		return false;
//	}
	$.beginPageLoading("正在查询数据...");
	var param = "";
	$.ajax.submit('QueryCondPart,infofonav', 'getInfos', param, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//子页面弹出
function showChildPage(obj,obj1,obj2)
{
	
	popupPage('telaudit.NpAuditInfo', 'getInfo', obj2,"携转号码审计查询详情", 820, 800,'popupFlow' );
}

function myRedirectToNav(paramName, url, func, familyNum, urlParam)
{
	var param = "&AUTH_AUTO=true&SERIAL_NUMBER="+familyNum;
  	if(typeof(urlParam) != "undefined" && urlParam != "")
  	{
  		param += urlParam;
  	}
  	openNav(paramName, url + "&#38;listener=" + func, urlParam);
}
