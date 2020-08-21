function queryDestroyOrder(){
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	if("" == serialNumber){
		alert("请填写手机号！");
		return false;
	}
	var param = "&REMOTE_SERIAL_NUMBER=" + serialNumber;
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('', 'queryDestroyOrder',param , 'DestroyTablePart', function(data){
		if(data && data.length) {
			//alert("查询成功");
			// var i = 0;
			// while($("input[name='ORDER_CHECK']").get(i).disabled == true) {
             //    i += 1;
             //    if(i > $("input[name='ORDER_CHECK']").size()) {
             //        $.endPageLoading();
             //        return;
			// 	}
            // }
			// $("input[name='ORDER_CHECK']").get(i).checked = "checked";
        } else {
			MessageBox.success("提示信息","无数据！");
		}
		$.endPageLoading();
		// $.cssubmit.disabledSubmitBtn(false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误提示",error_info);
    });
}

function sendOrder(){
	var remoteSns = [];
    var serialNumber = $("#cond_SERIAL_NUMBER").val();
	var checkedOrders = $("input[name=ORDER_CHECK]:checked");	
	if(checkedOrders && checkedOrders.length < 1) {
		alert("请至少选择一笔工单！");
		return;
	}else{
		for(var i = 0; i < checkedOrders.size(); i++){
			remoteSns.push(checkedOrders[i].value);
		}
	}
    $.beginPageLoading("派单中...");
	var param = "&REMOTE_SERIAL_NUMBER=" + serialNumber + "&REMOTE_ORDER_ID=" + remoteSns;
	$.ajax.submit('', 'onTradeSubmit',param , 'DestroyTablePart', function(data){
		$.endPageLoading();
		if("1"==data.get("IS_SUCCESS")){
			MessageBox.success("提示信息","派单成功！");
		}else{
			MessageBox.error("错误提示:",data.get("RESULT_DESC"));
		}
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误提示",error_info);
    });
}

