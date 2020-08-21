$(document).ready(function(){
	$("#QUERY_BTN").unbind("click");
	$("#QUERY_BTN").bind("click",function(){
		queryInfos();
	});
});


function queryInfos(){

	var area_code = $("#AREA_CODE").val();
	var start_date = $("#START_DATE").val();
	var end_date = $("#END_DATE").val();
	if(area_code == ""){
		alert("请选择地区！");
		return false;
	}
	
	if(start_date == ""){
		alert("开始时间不能为空！");
		return false;
	}
	
	if(end_date == ""){
		alert("结束时间不能为空！");
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	var param = "";
	$.ajax.submit('QueryCondPart,infofonav', 'getInfos', param, 'QueryListPart', function(data){
		if(data && data.length>0){
			
			$("#AREA_CODE_H").val(data.get(0).get("AREA_CODE_H"));
			
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
