$(document).ready(function(){
	$("#QUERY_BTN").unbind("click");
	$("#QUERY_BTN").bind("click",function(){
		queryInfos();
	});
});


function queryInfos(){

	var area_code = $("#AREA_CODE").val();
	if(area_code == ""){
		alert("请选择地区！");
		return false;
	}
	
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


function checkPass(){
	var area_code = $("#AREA_CODE").val();
	if(area_code == ""){
		alert("请选择地区！");
		return false;
	}
	
	var datas  =  getCheckedTableData("InfoTable",null,"saleactive");
	if(datas.length == 0){
		alert("没有可以提交的数据！");
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	var param = "&datas="+datas;
	$.ajax.submit(null, 'checkPass', param, null, function(data){
		if(data){
			var msg = data.get(0).get("MSG");
			alert(msg);
			queryInfos();
		}
		$.endPageLoading();
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function checkNotPass(){
	var datas  =  getCheckedTableData("InfoTable",null,"saleactive");
	if(datas.length == 0){
		alert("没有可以提交的数据！");
		return false;
	}
	
	var remark = $("#CHECK_REMARK").val();
	$.beginPageLoading("正在查询数据...");
	var param = "&datas="+datas+"&remark="+remark;
	$.ajax.submit(null, 'checkNotPass', param, null, function(data){
		if(data){
			var msg = data.get(0).get("MSG");
			alert(msg);
			queryInfos();
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}