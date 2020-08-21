

function init()
{
	
}
 
function importClick() {
	//查询条件校验
	if(!$.validate.verifyAll("fileListPart"))
	{
		return false;
	}
	
	var param = "";
	
	if($("#cflag").attr("checked") == true)
	{
		param="&Flag=1";
	}
	
	$.beginPageLoading("数据录入中..");
	$.ajax.submit('fileListPart', 'importClick',  param, 'showPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function checkClick(){
  
	var data = $.table.get("phoneTable").getTableData(null, true);
	if(data.length<1)
	{
		alert('没有导入信息,请点击导入');
		return false;
	}
	
	var param = "";
	if($("#cflag").attr("checked") == true)
	{
		param="&Flag=1";
	}
	
	$.beginPageLoading("数据录入中..");
	$.ajax.submit('showPart,fileListPart,refreshSList,hiddenPart', 'checkClick',  param, 'serviceListPart', function(data){
		var msg = data.get("MSG");
		var SUM = data.get("SUM");
		if(msg)
		{
			alert("msg"+msg);
		}
		else
		{
			var SUM = data.get("SUM"); 
			$("#fPhoneSum").val(SUM);
		}
		
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
 

function updateDealMark(chk){
	if (chk.attr("checked") == true) {
		chk.attr("rowIndex", $.table.get("SendTable").getTable().attr("selected"));
	}
}

function exportBeforeAction(domid)
{
	var params = "";
	var dataset = new Wade.DatasetList();
	var idata = new Wade.DataMap();
	var data = $.table.get("fPhoneTable").getTableData(null, true);
	if(data.length<1)
	{
		alert('没有过滤信息,请点击过滤');
		return false;
	}
	params = "&TAB_DATA="+data.toString();
	$.Export.get(domid).setParams(params);
	return true;

}

function popupPagePlat(obj)
{
	$("#SERVICE_ID").val($(obj).attr('service_id'));
	$("#SERVICE_NAME").val($(obj).attr('service_name'));
	$('#popup').css('display','none');
}

function openList()
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit(null, 'loadChildInfo',  null,
	'refreshSList', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	
	$('#popup').css('display','');
}














