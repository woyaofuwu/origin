function showOrderTablePart()
{
	var type = $("#cond_ORDER_TYPE").val();
//	alert(type);
	if(type == "0"){
		$("#OrderPart").css("display", "block");
		$("#ReturnPart").css("display", "none");
		$("#RETURN_ID_li").css("display", "none");
	} else if(type == "1"){
		$("#OrderPart").css("display", "none");
		$("#ReturnPart").css("display", "block");
		$("#RETURN_ID_li").css("display", "");
	}
}

function queryBandInfo()
{
	//查询条件校验
	if(!$.validate.verifyAll("condPart"))
	{
		return false;
	}
	var type = $("#cond_ORDER_TYPE").val();
	if(type == "0"){
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('condPart,OrderNavBarPart', 'queryBandInfo', null, 'orderInfoPart', function(data)
				{
			$.endPageLoading();
				},
				function(error_code,error_info)
				{
					$.endPageLoading();
					alert(error_info);
				});		
	}else{
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('condPart,ReturnNavBarPart', 'queryBandInfo', null, 'returnInfoPart', function(data)
				{
			$.endPageLoading();
				},
				function(error_code,error_info)
				{
					$.endPageLoading();
					alert(error_info);
				});		
	}
}


function updateStatus()
{
	var orderid = [];
	var suborderid = [];
	var returnid = [];
	var status = [];
	var type = $("#cond_ORDER_TYPE").val();
//	alert(type);
	if(type == "0"){
		$("#orderInfoPart input[name=chkbox]:checked").each(function(){
			orderid.push($.attr(this,'ORDER_ID'));
			suborderid.push($.attr(this,'SUBORDER_ID'));
			status.push($.attr(this,'ORDER_STATE'));
		});
		
		if(!orderid.length){
			alert("没有选择任何数据！");
			return;
		}
		
//		alert(orderid);
//		alert(suborderid);
//		alert(status);
		
		var param = '&ORDER_ID=' + orderid + '&SUBORDER_ID=' + suborderid + '&ORDER_STATE=' + status + '&ORDER_TYPE=' + type;
		
		$.beginPageLoading("数据更新中...");
		$.ajax.submit('', 'updateStatus', param, '', function(data) {
			$.endPageLoading();
			var result = data.get("RESULT_CODE");
			if(result == "0000"){
				alert("更新成功！");
			} else if (result == "9999"){
				alert("更新失败！");
			}
		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
		});		
	} else if(type == "1"){
		$("#returnInfoPart input[name=chkbox]:checked").each(function(){
			orderid.push($.attr(this,'ORDER_ID'));
			suborderid.push($.attr(this,'SUBORDER_ID'));
			returnid.push($.attr(this,'RETURN_ID'));
			status.push($.attr(this,'ORDER_STATE'));
		});
		
		if(!orderid.length){
			alert("没有选择任何数据！");
			return;
		}
		
//		alert(orderid);
//		alert(suborderid);
//		alert(status);
		
		var param = '&ORDER_ID=' + orderid + '&SUBORDER_ID=' + suborderid + '&RETURN_ID=' + returnid + '&ORDER_STATE=' + status + '&ORDER_TYPE=' + type;
		
		$.beginPageLoading("数据更新中...");
		$.ajax.submit('', 'updateStatus', param, '', function(data) {
			$.endPageLoading();
			var result = data.get("RESULT_CODE");
			if(result == "0000"){
				alert("更新成功！");
			} else if (result == "9999"){
				alert("更新失败！");
			}
		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
		});		
	}
	
}

function feedBackInfo(){
	var type = $("#cond_ORDER_TYPE").val();

	var orderid = [];
	var suborderid = [];
	var returnid = [];
	var status = [];
	var oprnum = [];
	var createtime = [];
	var updatetime = [];
	var acceptdate = [];
	var addition = [];
	//备注
	var rsrvStr3 = [];
//	alert(type);
	if(type == "0"){
		$("#orderInfoPart input[name=chkbox]:checked").each(function(){
			orderid.push($.attr(this,'ORDER_ID'));
			suborderid.push($.attr(this,'SUBORDER_ID'));
			status.push($.attr(this,'ORDER_STATE'));
			createtime.push($.attr(this,'CREATE_TIME'));
			updatetime.push($.attr(this,'UPDATE_TIME'));
			addition.push($.attr(this,'ADDITION'));
			//备注
			rsrvStr3.push($.attr(this,'RSRV_STR3'));
			
		});
		
		if(!orderid.length){
			alert("没有选择任何数据！");
			return;
		}
		var param = '&ORDER_ID=' + orderid + '&SUBORDER_ID=' + suborderid + '&ORDER_STATE=' + status + '&ORDER_TYPE=' + type
					+'&RSRV_STR3=' + rsrvStr3;
		$.beginPageLoading("数据更新中...");
		$.ajax.submit('', 'updateStatus', param, '', function(data) {
			$.endPageLoading();
		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
		});	
		
		var feedparam = '&ORDER_ID=' + orderid + '&SUBORDER_ID=' + suborderid + '&ORDER_STATE=' + status + '&CREATE_TIME=' + createtime + '&UPDATE_TIME=' + updatetime + '&ORDER_TYPE=' + type + '&ADDITION=' + addition;
		$.beginPageLoading("信息反馈中...");
		$.ajax.submit('', 'feedBackInfo', feedparam, 'OrderPart', function(data) {
			$.endPageLoading();
			var result = data.get("RESULT_CODE");
			if(result == "0000"){
				alert(data.get("RESULT_INFO"));
			} else {
				alert(data.get("RESULT_INFO"));
			}
		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
		});	
		
		
	} else if(type == "1"){
		$("#returnInfoPart input[name=chkbox]:checked").each(function(){
			orderid.push($.attr(this,'ORDER_ID'));
			suborderid.push($.attr(this,'SUBORDER_ID'));
			returnid.push($.attr(this,'RETURN_ID'));
			status.push($.attr(this,'ORDER_STATE'));
			acceptdate.push($.attr(this,'ACCEPT_DATE'));
			updatetime.push($.attr(this,'UPDATE_TIME'));
			oprnum.push($.attr(this,'OPR_NUM'));
			addition.push($.attr(this,'ADDITION'));
		});
		
		if(!orderid.length){
			alert("没有选择任何数据！");
			return;
		}
		
		var param = '&ORDER_ID=' + orderid + '&SUBORDER_ID=' + suborderid + '&RETURN_ID=' + returnid + '&ORDER_STATE=' + status + '&ORDER_TYPE=' + type;
		$.beginPageLoading("数据更新中...");
		$.ajax.submit('', 'updateStatus', param, '', function(data) {
			$.endPageLoading();
		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
		});		
		
		var feedparam = '&ORDER_ID=' + orderid + '&RETURN_ID=' + returnid + '&SUBORDER_ID=' + suborderid + '&ORDER_STATE=' + status + '&ACCEPT_DATE=' + acceptdate + '&UPDATE_TIME=' + updatetime + '&OPR_NUM=' + oprnum + '&ORDER_TYPE=' + type  + '&ADDITION=' + addition;
		$.beginPageLoading("信息反馈中...");
		$.ajax.submit('', 'feedBackInfo', feedparam, 'ReturnPart', function(data) {
			$.endPageLoading();
			var result = data.get("RESULT_CODE");
			if(result == "0000"){
				alert(data.get("RESULT_INFO"));
			} else {
				alert(data.get("RESULT_INFO"));
			}
		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
		});	
	}	
}
 
function changeCheckBoxVar(obj){
	var value = $(obj).val();
	$(obj).parent().parent().children().eq(0).children('input').attr('ORDER_STATE',value);
	if('IN' == value){
		var additionInfo = $.popupDialog('bandfeedback.BandFeedBackPopUp', '', '', '联系人信息填写','800', '200');
		$(obj).parent().parent().children().eq(0).children('input').attr('ADDITION',additionInfo);
	} else {
		$(obj).parent().parent().children().eq(0).children('input').attr('ADDITION',' , , ');
	}
}

function returnSubmit()
{
	//校验
	if(!$.validate.verifyAll("additionPart"))
	{
		return false;
	}
	var additionname = $("#ADDITION_NAME").val();
	var additionphone = $("#ADDITION_PHONE").val();
	var additionremark = $("#ADDITION_REMARK").val();
	
	var returnValue = additionname + ";" + additionphone + ";" + additionremark;
	
	$.closeDialog(returnValue);
}

/**
 * 关于一二级能力开放平台对接
 * @author zhuoyingzhi
 * @date 20180514
 * @param obj
 */
function changeCheckRsrvStrVar(obj){
	var value = $(obj).val();
	$(obj).parent().parent().children().eq(0).children('input').attr('RSRV_STR3',value);
}
