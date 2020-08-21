function submitCheck() {
	var obj=$("input[name=inforecvid]:checked");
	if(obj == null || obj.length == 0) {
			alert("请选择要反馈的订单！");
			return false;
	}
	
	var data = $.table.get("saleActiveTable").getTableData(null,true);
	var prex = "TID";
	var p_list="";
	var imeiObj="";
	for(var i=0;i<data.length;i++){
		var idname = i+prex;
		var che = $("#"+idname);
		if(che && che.attr("checked") == true){
			
			if(che.attr("typeCode")=='2'){
			    imeiObj = $('#'+i+'CONTRACT_IMEI').val();
			    
			 if (imeiObj=="" || imeiObj == undefined) {
				alert("已选订单中商品类型为'合约产品'的，请输入合约IMEI号！");
				return false;
			  }
			}
			
			p_list+=che.attr("checkParam")+","+imeiObj+";";
		}
	
	}
	  $("#comminfo_PIDINFO").val(p_list);
	
	var disNumber = $('#SHIPPING_ID').val();
	var companyNmuber = $('#SHIPPING_COMPANY').val();

	if (disNumber == "" || disNumber == undefined) {
		alert("配送单号不能为空!");
		return false;
	}
//	if (companyNmuber == "" || companyNmuber == undefined) {
//		alert("请选择物流公司!");
//		return false;
//	}

	return true;
}

function setLogComName(obj) {

	var index = obj.selectedIndex;

	var text = obj.options[index].text;
	if (text) {
		getElement("LOGISTICS_COMPANY_NAME").value = text;
	}
}

// 查询订单
function queryOrder() {
	var TID = $("#TID").val();
	// 查询条件校验
	if (TID == "") {
		alert("请输入正确的订单号");
		return false;
	}
	$.beginPageLoading("正在查询数据...");

	$.ajax.submit('QueryPart', 'queryDistributionOrder', null, 'RefreshPart1',
			function(data) {
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});

}