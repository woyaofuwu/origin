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

function addRow(){
	var rowEdit = new Array();
	var edit_area_code = $("#EDIT_AREA_CODE").val();
	var edit_serial_number = $("#EDIT_SERIAL_NUMBER").val();
	
	if(edit_area_code == ""){
		alert("分公司编码不能为空！");
		return false;
	}
	if(edit_serial_number == ""){
		alert("发送号码不能为空！");
		return false;
	}
	
	var  re = /^1\d{10}$/i;
	if(!re.test(edit_serial_number))
	{
		alert("请输入1开头的11位数字号码！");
		return false;
	}	
	
	var datas = getAllTableData("InfoTable",null);
	if(datas){
		var len = datas.length;
		for(var i=0;i<len;i++){
			var data = datas.get(0);
			var _city_code = data.get("CITY_CODE");
			var _serial_number = data.get("SERIAL_NUMBER");
			if(edit_area_code == _city_code && edit_serial_number == _serial_number){
				alert("已经存在！");
				return false;
			}
			
		}
	}
	
	rowEdit["CITY_CODE"] = edit_area_code;
	rowEdit["SERIAL_NUMBER"] = edit_serial_number;
	$.table.get("InfoTable").addRow(rowEdit,null,null,null,true);
	$.cssubmit.disabledSubmitBtn(false);
}

function delRow(){
	$.table.get("InfoTable").deleteRow();
	$.cssubmit.disabledSubmitBtn(false);
}


function submitBeforeAction()
{
	var datas = $.table.get("InfoTable").getTableData();
	if(datas.length==0){
		alert("没有可提交的数据！");
		return false;
	}
	//var datas = getAllTableData("InfoTable",null);
	var param = '&DATAS='+datas;
	$.cssubmit.addParam(param);
	return true;
}