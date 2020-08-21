	$(document).ready(function(){
	//$("#ForeGiftPart2").attr("disabled", true);
	//$("#invoicePart").attr("disabled", true);
});
var delAble = false;// 删除行的标志
var oldInvoiceNo="";//记录执行ajax的发票号码
var invoiceEffective=false;//发票号码有效的标志

function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="
			+ (data.get("USER_INFO")).toString() + "&CUST_INFO="
			+ (data.get("CUST_INFO")).toString() + "&ACCT_INFO="
			+ (data.get("ACCT_INFO")).toString(),
			'ForeGiftPart2,hiddenPart', function() {
				$.endPageLoading();
				//$("#ForeGiftPart2").attr("disabled", false);
				//$("#invoicePart").attr("disabled", false);
				operForegiftType();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
}
function tableRowClick() {
	// 获取选择行的数据
	var rowData = $.table.get("DeptTable").getRowData();
	$("#FOREGIFT_CODE").val(rowData.get("FOREGIFT_CODE"));
	
	if($("#OP_CODE").val()=="1" ||$("#OP_CODE").val()=="2"){
		$("#PAY_MONEY").val(rowData.get("NOW_MONEY"));
	}
	

	if (rowData.get("FOREGIFT_UPDATE_TIME") == "") {
		delAble = true;
	} else {
		delAble = false;
	}
}
function operForegiftType() {
	
	var op_code = $("#OP_CODE");
	var btCreate = $("#bcreate");
	var btUpdate = $("#bupdate");
	var btDelete = $("#bdelete");
	
	$.feeMgr.clearFeeList();
	
	if (op_code.val() == "1") {

		btCreate.attr("disabled", true);
		btCreate.attr("className", "e_button-form e_dis");

		btUpdate.attr("disabled", false);
		btUpdate.attr("className", "e_button-form");
		
		btDelete.attr("disabled", true);
		btDelete.attr("className", "e_button-form e_dis");
		
	}else if(op_code.val() == "0"){

		btCreate.attr("disabled", false);
		btCreate.attr("className", "e_button-form");

		btUpdate.attr("disabled", true);
		btUpdate.attr("className", "e_button-form e_dis");
		
		btDelete.attr("disabled", false);
		btDelete.attr("className", "e_button-form");
	}else if(op_code.val() == "2"){
		$("#DeptTableValues").empty();//清空使用号码查出的押金列表
		
		btCreate.attr("disabled", true);
		btCreate.attr("className", "e_button-form e_dis");

		btUpdate.attr("disabled", false);
		btUpdate.attr("className", "e_button-form");
		
		btDelete.attr("disabled", true);
		btDelete.attr("className", "e_button-form e_dis");
		
		alert("无主发票清退,需要通过校验发票查询出押金列表!");
	}else{
		
	}
}
/**
 * 获取发票信息
 */
function checkInvoiceNo(){
	var op_code = $("#OP_CODE").val();
	if(!op_code || op_code ==""){
		alert("请选择操作类型");
		return false;
	}
	if(!$.validate.verifyAll("invoicePart"))return false; 
	if($("#INVOICE_NO").val()==""){
		if($("#INVOICE_TAG").val()=="1")//判断是否需要输入发票号码或无主发票
		{
			alert("请输入发票号码！");
		}	 		
 		return;
	}
	
	$.beginPageLoading("正在查询发票数据...");
	var refreshPart='invoicePart';
	if(op_code=="2"){
		refreshPart='invoicePart,foreGiftTable';
	}
	$.ajax.submit('hiddenPart', 'getInvoiceInfo', "&INVOICE_NO="+$("#INVOICE_NO").val()+"&OPER_TYPE="+$("#OP_CODE").val(),
					refreshPart,
			function(){
				afterFunc();
				$.endPageLoading();
			},function(error_code,error_info)
			{
				$.endPageLoading();
				$.MessageBox.error(error_code,error_info);
	});
}

function afterFunc(){
	var op_code = $("#OP_CODE").val();
	
	if($("#IS_USED").val()==""){
		alert("网络故障，查询发票号码失败！");
 		invoiceEffective=false;
 		return false;
	}else{
		oldInvoiceNo=$("#INVOICE_NO").val();//记录执行ajax的发票号码
		invoiceEffective=true;
	}
	if(op_code =="0" && $("#IS_USED").val()=="YES"){
		alert("收取押金:该发票号码已经使用，业务无法继续！");
		invoiceEffective=false;
		return false;
	}
	if(op_code =="1"){
		if($("#IS_USED").val()=="NO"){
			alert("清退押金:根据发票号码未找到收取记录，业务无法继续！");
			invoiceEffective=false;
			return false;
		}
		if($("#PROCESS_TAG").val()=="1"){
			alert("清退押金:该发票已经清退了押金！");
			invoiceEffective=false;
	 		return false;
		}
	}
	if(op_code =="2" && $("#IS_USED").val()=="NO"){
		$.feeMgr.clearFeeList();//清空费用列表的所有费用
		alert("根据发票号码未找到无主押金！");
		return false;
	}
	if(op_code =="2" && $("#IS_USED").val()=="YES"){
		$.cssubmit.disabledSubmitBtn(false);
	}
}

function foregiftType() {// 选择押金默认设置打印名称和打印证件号码
	var name = $("#CUST_NAME1").val();
	var pspt = $("#PSPT_ID").val();
	var fname = $("#FOREGIFT_CUST_NAME").val();
	var fpspt = $("#FOREGIFT_PSPT_ID").val();

	if (fname == "" && fpspt == "") {
		$("#FOREGIFT_CUST_NAME").val(name);
		$("#FOREGIFT_PSPT_ID").val(pspt);
	}
}
/**
 * 收取押金
 * @returns
 */
function receiveForeGift(){

	var flag = "0";
	var foreGiftCode = $("#FOREGIFT_CODE").val();
	var payMoney = $("#PAY_MONEY").val();
	var foreCustName = $("#FOREGIFT_CUST_NAME").val();
	var forePsptId = $("#FOREGIFT_PSPT_ID").val();
	/*
	 * if(!$.isNumeric(payMoney)){ alert("金额输入不合法！"); return false; }
	 */
	if (foreGiftCode == "") {
		alert("请选择押金类型！");
		return false;
	}
	if (payMoney <= 0) {
		alert("请输入押金金额！");
		return false;
	}
	if (payMoney > 1000000) {
		alert("收取的费用不能大于一百万！");
		return false;
	}

	// 获取编辑区的数据
	var custEdit = $.ajax.buildJsonData("EditPart");
	var deptTable = $.table.get("DeptTable").getTableData(null, true);

	for (var i = 0; i < deptTable.length; i++) {
		if (deptTable.get([i], "FOREGIFT_CODE") == foreGiftCode) {// 押金类型编码  相同需要UPDATE

			var pre_money = deptTable.get([i], "PRE_MONEY");
			custEdit["PAY_MONEY"] = Math.round(parseFloat($("#PAY_MONEY").val()) * 100) / 100;// 现缴押金金额（元）
			custEdit["NOW_MONEY"] = Math.round(parseFloat(parseFloat(payMoney)+ parseFloat(pre_money)) * 100) / 100;// 现有押金金额（元）
			custEdit["FOREGIFT_CUST_NAME"] = foreCustName;
			custEdit["FOREGIFT_PSPT_ID"] = forePsptId;
			$.table.fn.selectedRow('DeptTable', $("#DeptTable")[0].rows[i + 1]);
			var rowData = $.table.get("DeptTable").getRowData();
			if (rowData.length == 0) {
				alert("请您选择记录后再进行操作！");
				return false;
			}

			// 更新表格数据
			$.table.get("DeptTable").updateRow(custEdit, i + 1);
			flag = "1";

			/*
			 * 费用组件
			 * [TRADE_TYPE_CODE,MODE,CODE,FEE,PAY,ELEMENT_ID],PAY表示实缴费用，PAY，ELEMENT_ID操作时候可以不传，其他为必传)：
			 */
			// 调用费用之前清除一次
			$.feeMgr.removeFee('290', '1', $("#FOREGIFT_CODE").val());
			
			var fee = Math.round(parseFloat(parseFloat($("#PAY_MONEY").val()) * 100));
			
			var feeData = new $.DataMap();
			feeData.put("TRADE_TYPE_CODE", "290");
			feeData.put("MODE", "1");// 大类 押金
			feeData.put("CODE", $("#FOREGIFT_CODE").val());// 小类
			feeData.put("FEE", fee);// 应缴金额
			feeData.put("PAY", fee);// 实际缴纳
			$.feeMgr.insertFee(feeData)

			break;
		}
	}
	if (flag == "0") {
		// 新增
		custEdit["FOREGIFT_CODE_BOX_TEXT"] =$("#FOREGIFT_CODE")[0].options($("#FOREGIFT_CODE")[0].selectedIndex).text;// 押金类型$("#FOREGIFT_CODE").find("option:selected").text().trim();
		custEdit["FOREGIFT_CODE"] =$("#FOREGIFT_CODE").val();// $("#FOREGIFT_CODE")[0].options($("#FOREGIFT_CODE")[0].selectedIndex).value;// 押金类型编码
		custEdit["PRE_MONEY"] = "0";// 原有押金金额（元）
		custEdit["PAY_MONEY"] = Math.round(parseFloat($("#PAY_MONEY").val()) * 100) / 100;// 现缴押金金额（元）
		custEdit["NOW_MONEY"] = Math.round(parseFloat($("#PAY_MONEY").val()) * 100) / 100;// 现有押金金额（元）
		custEdit["FOREGIFT_UPDATE_TIME"] = "";
		custEdit["FOREGIFT_CUST_NAME"] = foreCustName;
		custEdit["FOREGIFT_PSPT_ID"] = forePsptId;

		$.table.get("DeptTable").addRow(custEdit);

		/*
		 * 费用组件
		 * [TRADE_TYPE_CODE,MODE,CODE,FEE,PAY,ELEMENT_ID],PAY表示实缴费用，PAY，ELEMENT_ID操作时候可以不传，其他为必传)：
		 */
		// 调用费用之前清除一次
		$.feeMgr.removeFee('290', '1', $("#FOREGIFT_CODE").val());
		var fee = Math.round(parseFloat($("#PAY_MONEY").val()) * 100);
		var feeData = new $.DataMap();
		feeData.put("TRADE_TYPE_CODE", "290");
		feeData.put("MODE", "1");// 大类 押金
		feeData.put("CODE", $("#FOREGIFT_CODE").val());// 小类
		feeData.put("FEE", fee);// 应缴金额
		feeData.put("PAY", fee);// 实际缴纳
		$.feeMgr.insertFee(feeData)
	}
	cleanForegift();
}

/**
 * 删除押金
 * @returns {Boolean}
 */
function deleteForeGift() {

	if(delAble){
		var rowData = $.table.get("DeptTable").getRowData();
		var foregiftCode = rowData.get("FOREGIFT_CODE");
		if (rowData.length == 0) {
			alert("请您选择记录后再进行删除操作！");
			return false;
		}
		$.table.get("DeptTable").deleteRow();
		// 费用组件
		$.feeMgr.removeFee('290', '1', foregiftCode);
	} else {
		alert("历史押金记录不允许删除！");
	}
	cleanForegift();
}
/**
 * 清退押金
 * @returns {Boolean}
 */
function returnForeGift() {

	var foreGiftCode = $("#FOREGIFT_CODE").val();
	var payMoney = parseFloat($("#PAY_MONEY").val());
	var custEdit = $.ajax.buildJsonData("EditPart");
	var rowData = $.table.get("DeptTable").getRowData();
	var pre_money = rowData.get("PRE_MONEY");
	
	if(rowData.get("FOREGIFT_CODE") != foreGiftCode){
		alert("选择记录的押金类型与要清退的押金类型不一致！");
		return false;
	}

	if (rowData.length == 0) {
		alert("请您选择记录后再进行操作！");
		return false;
	}
	if (parseFloat(payMoney) * (-1) + parseFloat(pre_money) < 0) {

		alert("清退的押金不可以大于原有的押金！");
		return false;
	}

	if (foreGiftCode == "3" && $("#REMOVE_TAG").val() == "0") {

		if ($("#LONG_SERVICE_ID").val() == "15"
				&& $("#ABOVE_START_DATE").val() == "YES") {
			alert("必须取消国际长途才能清退押金！");
			return false;
		}
		if ($("#ROAM_SERVICE_ID").val() == "19") {
			alert("必须取消国际漫游才能清退押金！");
			return false;
		}
		
		if($("#OP_CODE").val()=="1" && $("#HFQT_PRV").val() !="1" && $("#Balance").val()=="0"){
			alert("用户需缴清当前所有话费后才能办理押金清退业务！");
			return false;
		}
		if($("#CANCEL_LONGROAM_TIME").val()=="NO")
		{
			alert("必须取消国际长途/漫游满15天才能清退押金！");
			return false;
		}
	}
	
	if(foreGiftCode=="30"&&$("#FOREGIFT_LIMIT").val()=="YES"){
		alert("188靓号抢鲜活动保证金将在活动协议规定的期限内主动退还到客户指定的银行账户，不能在前台办理押金退款业务。");
		return false;			
	}
	if (payMoney <= 0) {
		alert("清退的押金金额不能小于零！");
		return false;
	}
	var custEdit = $.ajax.buildJsonData("EditPart");
	
     /**
      * REQ201610110009_押金业务界面增加判断拦截
      * @author zhuoyingzhi
      * 20161117
      */
	if($("#OP_CODE").val() == '2'){
		//无主押金清退(效验)
		var process_tag=rowData.get("FOREGIFT_PROCESS_TAG");
		var end_date=rowData.get("FOREGIFT_END_DATE");
		var user_id=rowData.get("FOREGIFT_USER_ID");
		var rsrv_num2=rowData.get("FOREGIFT_RSRV_NUM2");
		
		var foregift_code=$("#FOREGIFT_CODE").val();
		
		var url="&FOREGIFT_PROCESS_TAG="+process_tag+"&FOREGIFT_END_DATE="+end_date+"&FOREGIFT_USER_ID="+user_id+"&FOREGIFT_RSRV_NUM2="+rsrv_num2
		        +"&FOREGIFT_CODE="+foregift_code;
		$.beginPageLoading("正在效验...");
		$.ajax.submit('', 'checkNotForeGift', url ,'', function() {
					$.endPageLoading();
					custEdit["PAY_MONEY"] = Math.round(parseFloat(payMoney) * (-1) * 100) / 100;// 现缴押金金额（元）
					custEdit["NOW_MONEY"] = Math.round(parseFloat(parseFloat(payMoney) * (-1) + parseFloat(pre_money)) * 100) / 100;

					// 更新表格数据
					$.table.get("DeptTable").updateRow(custEdit);

					var rowData2 = $.table.get("DeptTable").getRowData();
					
					refreshFee(foreGiftCode,rowData2.get("PAY_MONEY"));
					
					cleanForegift();
				}, function(error_code, error_info,detail) {
					$.endPageLoading();
					MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
				});
		
	}else{
		custEdit["PAY_MONEY"] = Math.round(parseFloat(payMoney) * (-1) * 100) / 100;// 现缴押金金额（元）
		custEdit["NOW_MONEY"] = Math.round(parseFloat(parseFloat(payMoney) * (-1) + parseFloat(pre_money)) * 100) / 100;

		// 更新表格数据
		$.table.get("DeptTable").updateRow(custEdit);

		var rowData2 = $.table.get("DeptTable").getRowData();
		
		refreshFee(foreGiftCode,rowData2.get("PAY_MONEY"));
		
		cleanForegift();
	}
}
/**
 * 更新费用组件
 * @param foreGiftCode
 * @param payMoney
 */
function refreshFee(foreGiftCode,payMoney){
	$.feeMgr.removeFee('290', '1', foreGiftCode);
	
	var fee = Math.round(parseFloat(payMoney) * 100);
	var feeData = new $.DataMap();
	feeData.put("TRADE_TYPE_CODE", "290");
	feeData.put("MODE", "1");// 大类 押金
	feeData.put("CODE", foreGiftCode);// 小类
	feeData.put("FEE", fee);
	$.feeMgr.insertFee(feeData);
}
/**
 * 清空编辑区
 */
function cleanForegift() {
	$("#FOREGIFT_CODE").val("");
	$("#PAY_MONEY").val("");
}
/**
 * 提交结果
 * @param obj
 * @returns {Boolean}
 */
function submitDepts(obj) {
	
	var add = false;// 收取押金标志
	var upd = false;// 清退押金标志
	var addPay = 0;
	var updPay = 0;
	var info = "";

	var deptTable = $.table.get("DeptTable").getTableData(null, true);
	for ( var i = 0; i < deptTable.length; i++) {
		pay = parseFloat(deptTable.get([ i ], "PAY_MONEY"));
		if (pay > 0) {
			add = true;
			addPay += pay;
		} else if (pay < 0) {
			upd = true;
			updPay += pay;
		}
	}

	if (addPay > 1000000) {
		alert("收取的费用不能大于一百万！");
		return false;
	}
	if (updPay == 0 && addPay == 0) {
		alert("没有数据可以提交！");
		return false;
	}
	if (add && upd) {
		alert("同一笔业务不可以收取、清退同时操作！");
		return false;
	}
	
	if($("#INVOICE_TAG").val()=="1")//判断是否需要输入发票号码或无主发票
	{
		if($("#INVOICE_NO").val()=="")
		{
			alert("请输入发票号码！");
			return false;
		}
		if(!invoiceEffective)//发票号码无效
	 	{
	 		alert("发票校验没有通过，操作无法提交！");
	 		return false;
	 	}
		if(oldInvoiceNo !=$("#INVOICE_NO").val())//发票号码无效
	 	{
	 		alert("校验的发票号码与输入的发票号码不一致，请确认！");
	 		return false;
	 	}
		if($("#IS_USED").val()=="YES"&&add)	 	
	 	{
	 		alert("该发票号码已被使用！");
	 		return false;
	 	}
		if($("#PROCESS_TAG").val()=="1" && upd)	 	
	 	{
	 		alert("该发票已经清退了押金！");
	 		return false;
	 	}
		if(upd){
			var invoiceFeeSum=-parseFloat($("#INVOICE_FEE_SUM").val())/100;
			
			if(invoiceFeeSum!=updPay){
				alert("清退金额和发票总金额不一致！");
				return false;
			}
		}
	}
	
	if($("#AUTH_SERIAL_NUMBER").val()==""){
		$("#CSSUBMIT_BUTTON").attr("cancelRule",true);
	}

	var param = "&USER_FOREGIFTS="+deptTable;
	$.cssubmit.addParam(param);
	
	return true;
}
