
function queryWidenetConsts(){
		//查询条件校验
	if(!$.validate.verifyAll("ConditionPart")) {
		return false;
	}
	
	
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	if($.trim(startDate).length > 7){
		startDate = startDate.substring(0,7);
	}
	if($.trim(endDate).length > 7){
		endDate = endDate.substring(0,7);
	}
	if(startDate != endDate){
		alert("开始时间和结束时间必须在同一个月内!");
		return false;
	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('ConditionPart', 'queryWidenetConsts', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//稽核通过
function checkDealPassOK()
{	
	var param =  '';
	//var datas = $.table.get("UnfinishTrade").getTableData();
	//alert("data=" + datas);
	//param += '&MEB_LIST='+datas;
	//alert("param=" + param);
	
	var data = $.table.get("UnfinishTrade").getCheckedRowDatas();
	if(data.length == 0) {
		alert("您没有进行任何操作，不能提交！");
		return false;
	}
	param += '&NUMBER_LIST='+data;
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('QueryListPart,RemarkPart', 'updateConstsPass', param, 'QueryListPart', function(data)
	{
		MessageBox.alert("提示","操作成功！",function(btn)
	{
		if(btn=="ok")
		{
			window.location.href = window.location.href;
		}
	});
		
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//稽核不通过
function checkDealPassFall()
{

	var param =  '';
	var data = $.table.get("UnfinishTrade").getCheckedRowDatas();
	if(data.length == 0) {
		alert("您没有进行任何操作，不能提交！");
		return false;
	}
	param += '&NUMBER_LIST='+data;
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('QueryListPart,RemarkPart', 'updateConstsNoPass', param, 'QueryListPart', function(data)
	{
		MessageBox.alert("提示","操作成功！",function(btn)
	{
		if(btn=="ok")
		{
			window.location.href = window.location.href;
		}
	});
		
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

