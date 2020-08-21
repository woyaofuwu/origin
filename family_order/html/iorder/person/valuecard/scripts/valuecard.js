//初始化
$(document).ready(function(){
	//$.feeMgr.setTestTag(true);
	//disabledArea("QueryCondPart",true);
	//disabledArea("SubmitImportGiveValueCard",true);
});
function refreshUserPartAtferAuth()
{
	if(!$.validate.verifyAll("AuthPart"))return false; 
	$.beginPageLoading();
	$.ajax.submit('AuthPart', 'loadChildInfo', '', 'UCAViewPart,userOtherInfo', function(data){
		$("#QueryCondPart").removeClass("e_dis")
		// 查询后解开卡号输入的限制
		$("#START_CARD_NO").attr("disabled",null);
		$("#END_CARD_NO").attr("disabled",null);
		$("#baseinfo_radio_b").attr("disabled",null);
		$("#PROSECUTION_WAY").attr("disabled",null);
		$("#addButtom").attr("disabled",null);
		$("#START_CARD_NO").val("");
		$("#END_CARD_NO").val("");
		$("#START_CARD_NO").focus();
		$.endPageLoading();
		cleanTable();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
    });
}
function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'QueryCondPart', function(data){
		
		disabledArea("QueryCondPart",false);
		disabledArea("SubmitImportGiveValueCard",false);
		
		$("#QueryCondPart").removeClass("e_dis");
		$("#CUST_NAME").attr("disabled",true);
		// 查询后解开卡号输入的限制
		$("#START_CARD_NO").attr("disabled",null);
		$("#END_CARD_NO").attr("disabled",null);
		$("#baseinfo_radio_b").attr("disabled",null);
		$("#PROSECUTION_WAY").attr("disabled",null);
		$("#addButtom").attr("disabled",null);
		$("#START_CARD_NO").val("");
		$("#END_CARD_NO").val("");
		$("#START_CARD_NO").focus();
		cleanTable();
		
		// 设置提示信息
		if(data.get("HINT_INFO")!=null&&data.get("HINT_INFO")!=""){
			$("#ALERT_INFO").text(data.get("HINT_INFO"));
			$("#ALERT_INFO_DIV").css("display","block");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
    });
}

// 输入起始卡号的同时，赋值给截止卡号
function startCardKeyUp() {
	if($("#RES_KIND_CODE").val()!="C"){
		$("#END_CARD_NO").val($("#START_CARD_NO").val());
	}
}

function textToUpperCase(){
	  if(event.keyCode>=65 && event.keyCode<=90 || event.type=="change"){
	    event.srcElement.value = event.srcElement.value.toUpperCase();
	  }
	}
// 根据页面radio选项决定是否显示折扣率、销售价格
function checkRadio() {
	var radio = document.getElementsByName("baseinfo_radio"); 
	var discount1 = document.getElementById("discount1");
	var discount2 = document.getElementById("discount2");
	var price1 = document.getElementById("price1"); 
	var price2 = document.getElementById("price2");
	var xCodingStr = document.getElementById("X_CODING_STR");
	var CHG_TAG = false;	// 解决：点击已经被选中的radio时触发也会清空列表
	if(radio[1].checked && radio[1].attributes["oldvalue"].nodeValue != "1") {
		radio[1].attributes["oldvalue"].nodeValue;
		discount1.style.display = '';
		radio[0].attributes["oldvalue"].nodeValue = "0";
		radio[1].attributes["oldvalue"].nodeValue = "1";
		$("#xCodingStr").val("");
		CHG_TAG = true;
	} else if(radio[0].checked && radio[0].attributes["oldvalue"].nodeValue != "1") {
		discount1.style.display = 'none'; 
		radio[0].attributes["oldvalue"].nodeValue = "1";
		radio[1].attributes["oldvalue"].nodeValue = "0";
		$("#xCodingStr").val("");
		CHG_TAG = true;
	}
	if(CHG_TAG) {
		cleanTable()
	}
}
function cleanTable(){
	// 禁止提交
	$.cssubmit.disabledSubmitBtn(true);
	// 清空表格
//		var table1 = document.getElementById("table1");
		var table1Data = table1.getData(true);
		// 有价卡换卡没有table1
		if(table1Data!=null){
			var length1 = table1Data.length;
			for(var i = 0; i < length1; i++) {
				table1.deleteRow(i,true);
			}
		}
//		var table2 = document.getElementById("table2");
		var table2Data = table2.getData(true);
		var length2 = table2Data.length;
		for(var i = 0; i < length2; i++) {
			table2.deleteRow(i,true);
		}
		// 清除购卡费
		modFee("0","20",0);
		hk_count=0;
}

// 点击“加入”时作验证
function addValueCard(str, objTable) { 

	if($("#START_CARD_NO").val().length<6||$("#END_CARD_NO").val().length<6){
//		alert("状态:卡号输入错误！");
		$.TipBox.show(document.getElementById('START_CARD_NO'), "卡号输入错误！", "red");
		return false;
	}
	var start = $("#START_CARD_NO");
	var end = $("#END_CARD_NO");
	var vstart = start.val();
	var vend = end.val(); 
	if(str == "S") {
		var discount = $("#DISCOUNT");
		var saleprice = $("#SALEPRICE");
		var radio = $("#baseinfo_radio_b");
	} 
	if(str == 'C' && vstart == vend) {
		$.TipBox.show(document.getElementById('START_CARD_NO'), "原卡号与新卡号不能为同一卡号！", "red");
//		alert("状态:原卡号与新卡号不能为同一卡号！");
		end.focus();
		return false;
	} 	 
	if(vstart == "") {
		$.TipBox.show(document.getElementById('START_CARD_NO'), "请检查卡号！", "red");
//		alert("状态:请检查卡号！");
		start.focus();
		return false;
	}
	if(vend == "") {
		$.TipBox.show(document.getElementById('END_CARD_NO'), "请检查卡号！", "red");
//		alert("状态:请检查卡号！");
		end.focus();
		return false;
	}

	if(str != "C") {
		// 保证在同一号段，即从第一位到倒数第五位相同
		if(vstart.substring(0, vstart.length-4) != vend.substring(0, vstart.length-4)) {
			MessageBox.alert("提示", "状态:起始卡号" + vstart.substring(0, vstart.length-4) + "和截止卡号" 
					+ vend.substring(0, vstart.length-4) + "必须在同一号段！");
			end.focus();
			return false;
		}
		if(vstart > vend) {
			$.TipBox.show(document.getElementById('END_CARD_NO'), "请将数字较大的卡号置于截止卡号！", "red");
//			alert("状态:请将数字较大的卡号置于截止卡号！");
			end.focus();
			return false;
		}
		// 一次不能查询超过300条数据
		 
		//if(Number(vend.substring(vend.length-4, vend.length)) 
		//			- Number(vstart.substring(vstart.length-4,vstart.length)) > 300) {
		//	alert("状态:数量量过大将导致超时，请缩小数据段！");
		//	end.focus();
		//	return false;
		//}
		 
	}

	if(str == "S" && radio.attr("checked")) {
		if(discount.val() == "" && saleprice.val() == "") {
			$.TipBox.show(document.getElementById('DISCOUNT'), "请输入【折扣率】或【销售价格】！", "red");
//			alert("状态:请输入【折扣率】或【销售价格】！");
			discount.focus();
			return false;
		}
		if(discount.val() != "" && saleprice.val() != "") {
			$.TipBox.show(document.getElementById('SALEPRICE'), "【折扣率】和【销售价格】不可以同时填写！", "red");
//			alert("状态:【折扣率】和【销售价格】不可以同时填写！");
			discount.focus();
			return false;
		}
		if(!(discount.val()=="" ? false : checkFloat(discount, "【折扣率】")) 
					&& !(saleprice.val()=="" ? false : checkFloat(saleprice, "【销售价格】"))) {
			return false;
		}
		if(parseFloat(discount.val()) > 10 || parseFloat(discount.val()) < 0) {
			$.TipBox.show(document.getElementById('DISCOUNT'), "【折扣率】输入不正确！", "red");
//			alert("状态:【折扣率】输入不正确！");
			return false;
		}
	}
	if(objTable.length > 0) {
		if(str == "C") {
			for(var i = 0; i < objTable.length; i++) {
				if(vstart == objTable[i].get("startCardNo")
					|| vend == objTable[i].get("endCardNo")) {
					MessageBox.alert("提示", "状态:输入卡号与已有号段中号码重复！");
					return false;
				}
			}
		} else {
			for(var i = 0; i < objTable.length; i++) {
				if(vstart >= objTable[i].get("startCardNo")
					&& vstart <= objTable[i].get("endCardNo")) {
					MessageBox.alert("提示", "状态:输入卡号与已有号段中号码重复！");
					return false;
				}
				if(vend >= objTable[i].get("startCardNo")
					&& vend <= objTable[i].get("endCardNo")) {
					MessageBox.alert("提示", "状态:输入卡号与已有号段中号码重复！");
					return false;
				}
			}
		}
	}
	$("#X_CODING_STR").val(codingTable().toString());

	var param = "";
	param += "&table2=" + $("#X_CODING_STR").val();
	$.beginPageLoading();
	$.ajax.submit('QueryCondPart,AuthPart','addClick',param,'BasicInfosPart,SaleInfosPart',function(data)
	{
		$.cssubmit.disabledSubmitBtn(false);
		table1.adjust();
		table2.adjust();
		$("#TableArea").css("display", "");
		if(str=="C"){
			$.endPageLoading();
			return true;
		}
	  var info = data.get(0, "alertInfo");  
	  if(info != null) 
	  {
		    MessageBox.alert("提示", "状态:"+info);
	  }
	  addFee(data,str);
	  $.endPageLoading(); // today 费用组件的处理待续，先返回
	  return ;// today
	  		
	},function(errorcode,errorinfo){
		MessageBox.alert("提示", errorinfo);
		$.endPageLoading();
	}); 

}

//验证码点击“加入”时 有价卡赠送界面\规则修改需求 如不输入移动用户手机号码或者输入非移动用户号码，则系统拦截
function checkCustomerNumber(str, objTable) {
    var SERIAL_NUMBER = $("#customerNumber").val();
    var TRADETYPECODE= $("#TRADE_TYPE_CODE").val();
    var activate = $("#REMARK").val();
    if ( activate == "" )
    {
    	$.TipBox.show(document.getElementById('REMARK_BALANCE'), "活动名称(赠送信息里面的备注)不能为空！", "red");
//    	MessageBox.alert("提示", "提示:活动名称不能为空！");
        return false;
    }
	if(SERIAL_NUMBER == "") {
		$.TipBox.show(document.getElementById('customerNumber'), "客户号码不能为空！", "red");
//		alert("状态:客户号码不能为空！");
		return false;
	}
	var params = "SERIAL_NUMBER=" + SERIAL_NUMBER+"&TRADE_TYPE_CODE="+TRADETYPECODE;
	$.ajax.submit('', 'checkCustomerNumber', params, '', function(data) {
		if (data.get("SUCC_FLAG") && "1" == data.get("SUCC_FLAG")) {
			addValueCard(str, objTable);
		} else {
			MessageBox.alert("提示", "客户号码信息无效！请重新输入！");
//			alert("状态:客户号码信息无效！请重新输入！")
			return false;
		}

	}, function(error_code, error_info) {
		if(error_code=="CRM_USER_273"){
			MessageBox.alert("提示", "CRM_USER_273:"+"获取客户号码信息资料无数据");
		}else{
			MessageBox.alert("提示", "errorinfo" +error_info);
		}
		$.endPageLoading();
	});
}
// 换卡加入按钮
function addChangeValueCard(str, objTable) { 
	var start = document.getElementById("START_CARD_NO");
	var end = document.getElementById("END_CARD_NO");
	var vstart = start.value;
	if(str =='C'){
		var result = window.confirm("提示信息:请确认一下老卡信息,卡号是："+vstart)
			if(result){
				addValueCard(str, objTable);
			};
	}
}


// 验证浮点型数据 (整数部分一位，小数部分无或一位、两位)
function checkFloat(obj, desc) {
	var expression = /^\d+(?:\.\d{1,2})?$/;
	if(!expression.exec(obj.val())) {
		MessageBox.alert("提示", "状态:"+desc + "格式错误！");
		obj.focus();
		return false;
	}
	return true;
}

// table 数据
function codingTable(){
	
//	return $.table.get("table2").getTableData(null,true);
	return table2.getData(true);
}

// 将TABLE2中数据拼串 已改造为
function coding(objTable, str) {
	var xCodingStr = "";
	var idata = null;
	var dataset = new Wade.DatasetList();

	for(var i = 1; i < objTable.rows.length; i++) {
		idata = new Wade.DataMap();
		idata.put("startCardNo", objTable.rows[i].cells[1].innerHTML);
		idata.put("endCardNo", objTable.rows[i].cells[2].innerHTML);
		idata.put("simPrice", objTable.rows[i].cells[3].innerHTML);
		idata.put("singlePrice", objTable.rows[i].cells[4].innerHTML);
		idata.put("totalPrice", objTable.rows[i].cells[5].innerHTML);
		idata.put("rowCount", objTable.rows[i].cells[6].innerHTML);
		idata.put("valueCode", objTable.rows[i].cells[7].innerHTML);
		idata.put("advise_price", objTable.rows[i].cells[8].innerHTML);
		idata.put("activateInfo", objTable.rows[i].cells[9].innerHTML);
		idata.put("devicePrice", objTable.rows[i].cells[10].innerHTML);
		idata.put("cardType", objTable.rows[i].cells[11].innerHTML);
		idata.put("activeFlag", objTable.rows[i].cells[12].innerHTML);
		
		dataset.add(idata);
	}
	idata = null;
	xCodingStr = dataset.toString();
	dataset = null;
	return xCodingStr;
}

// 加入后处理费用
function addFee(data,str) {
	var fee1 = 0;
	if(str == "S") {
		fee1 = getTable2Fee();
	} else if(str == "B") {
		fee1 = 0 - getTable2Fee();
	}
	
	if(str == "S" || str == "B") {
		if(hk_count==0){
			insertFee("0","20",fee1);
			hk_count=1;
		}else{
			modFee("0","20",fee1);
		}
	}
	var trade = $.feeMgr.getFeeTrade() ;

}

// 获取TABLE2中总费用****************************************
function getTable2Fee() {
//	var objTable = document.getElementById("table2");
	var objTable = table2.getData(true);
	var feeTotal = 0.0;
	for(var i = 0; i < objTable.length; i++) {
		feeTotal = feeTotal + parseFloat(objTable[i].get("totalPrice")) * 100;
	}
	return feeTotal;
}


function deleteRow1(rowIndex, str) {
	$("#IS_DEL").val("true");
	MessageBox.confirm("确认提示", "确定要删除该条记录？", function(btn){
		if(btn == "ok"){ 
			if($("#IS_DEL").val()=="true"){
				$("#IS_DEL").val("false");
				deleteThisRow(rowIndex, str);
			}
		}
		
	});
}

// 双击TABLE2中某一行进行删除
function deleteThisRow(rowIndex, str) {
 
//    var table2 = document.getElementById("table2");
    var table2Data = table2.getData(true);
    var fee2 = getTable2Fee();
    var fee = parseFloat(table2.getRowData(rowIndex-1, null).get("totalPrice")) * 100;
    if(fee2==fee){
		hk_count=0;
	}
	// 删除记录
    table2.deleteRow(rowIndex-1,true);
	if(table2Data.length < 2) {
    	$("#X_CODING_STR").val("");
   	}
	if(str=="B"){
		delFee("0","20",-fee);
	}else{
		delFee("0","20",fee);
	}
}

// 点击“加入”时作验证 ( 赠送时)
function addValueCardGive(str, objTable) {
   var SERIAL_NUMBER = $("#customerNumber").val();
   var activate = $("#REMARK").val();
    if ( activate == "" )
    {
    	$.TipBox.show(document.getElementById('REMARK_BALANCE'), "活动名称(赠送信息里面的备注)不能为空！", "red");
//    	MessageBox.alert("提示", "提示:活动名称不能为空！");
        return false;
    }
	if(SERIAL_NUMBER == "") {
		$.TipBox.show(document.getElementById('customerNumber'), "客户号码不能为空！", "red");
//		alert("状态:客户号码不能为空！");
		return false;
	}
    addValueCard(str, objTable) ;
}

// 提交校验
function checkBeforeSubmit()
{   
//	var allData = $.table.get("table2").getTableData(null,true);
	var allData = table2.getData(true);
	if(allData.length==0){
		MessageBox.alert("提示", "没有有价卡信息，请添加有价卡信息");
		return false;
	}
	$("#X_CODING_STR").val(allData);
	if($("#TRADE_TYPE_CODE").val()=="416"){
		//$.cssubmit.bindCallBackEvent(setAlertBack);//设置提交业务后回调事件
		var saleCount = 0;
		var price = 0;
		var resKindName="";
		var objTable = document.getElementById('table2');
		for(var i = 1; i < objTable.rows.length; i++) {
			saleCount = saleCount + Number(objTable.rows[i].cells[6].innerHTML);
			price = Number(objTable.rows[i].cells[4].innerHTML);
			resKindName = objTable.rows[i].cells[0].innerHTML;
		}
		
		//$.printMgr.setPrintParam("BRAND_MODEL",resKindName);
		//$.printMgr.setPrintParam("UNIT","张");
		//$.printMgr.setPrintParam("QUANTITY",saleCount);
		//$.printMgr.setPrintParam("PRICE",price);
		return true;
	}
	/**
	 * 有价卡赠送
	 * 修改没有选择赠送信息里面的备注也能提交问题
	 * @author zhuoyingzhi
	 * @date 20180404
	 */
	if($("#TRADE_TYPE_CODE").val()=="418"){
	    var activate = $("#REMARK").val();
	    if ( activate == "" )
	    {
	    	MessageBox.alert("提示", "活动名称(赠送信息里面的备注)不能为空！");
	        return false;
	    }else{
	    	return true
	    }		
	}
	if($("#TRADE_TYPE_CODE").val()=="419"){
		/**
		 * REQ201802260006_有价卡退卡增加付款方式选项的优化
		 * @author zhuoyingzhi
		 * @date 20180417
		 */
		var payTypeCode=$("#BACK_PAY_MONEY_CODE").val();
		if(payTypeCode == ''){
			MessageBox.alert("提示", '付款方式不能为空');
			return false;
		}
		/******************************/
		$.printMgr.setPrintParam("DIY_OPERFEENAME", "退款通知单");
		MessageBox.alert("提示", '有价卡全部退回的话,请回收并作废该有价卡销售时开具的发票',function(btn){
			if(btn == "ok"){
				$.cssubmit.submitTrade();
				return true;
			}
		});
	}
}
// 业务提交
function onTradeSubmit(str){
	var param = "";
	param += "&X_CODING_STR=" + $("#X_CODING_STR").val();
	// 提交前校验
	if(!checkBeforeSubmit()) {
		return false;
	}
	
	var saleCount = 0;	// 操作卡数量
	var total = 0; // 赠卡总金额
//	var objTable = document.getElementById('table2');
	var objTable = table2.getData(true);
	for(var i = 0; i < objTable.length; i++) {
		if(str == "C") {
			saleCount = saleCount + Number(objTable[i].get("totalPrice"));
		} else if(str =="G") {
			saleCount = saleCount + Number(objTable[i].get("rowCount"));
			var fee = Number(objTable[i].get("rowCount"))*Number(objTable[i].get("statusCode"));
			total += fee; // 计算赠送卡总额
		}else{
			saleCount = saleCount + Number(objTable[i].get("rowCount"));
		}
	}
	if($("#TRADE_TYPE_CODE").val()=="430"){
		var tempFee = $("#changeFee").val();
		var totalFee = total/100+".00";
	    if (new Number(tempFee)<new Number(totalFee)){
	    	MessageBox.alert("提示", "赠送的有价卡面值["+totalFee+"]超出可兑换话费卡金额["+tempFee+"]，业务无法继续！");
	       return false;
	    }
	}
	if(str=="G"&&total>Number($("#balanceDiv").val())*100){// 赠送卡总额大于审批总额，不能提交
		MessageBox.alert("提示", "状态:赠送量大于审批量，不予通过!");
		return false;
	}
	
	return true;
}


//执行成功回调
function setAlertBack(data)
{
	if(data && data.length>0)
	{
		var content = "点【确定】继续业务受理。";	
		if(data && data instanceof $.DatasetList && data.length>0)
		{
			var orderId = data.get(0).get("ORDER_ID");
			content ="<br/>客户订单标识：" + orderId + "<br/>点【确定】继续业务受理。";
		}
		$.cssubmit.showMessage("success", "业务受理成功", content, true);
	}
}


var hk_count = 0;
// 当修改客户名称的时候需要修改打印信息，修改标志
function changeInvoiceTag(){
	$("#INVOICE_TAG").val("1");
}

// //////////////////////////////////////
// 增加一笔费用
function insertFee(mode,code,fee){
	
	var feeObj = new Wade.DataMap(); 
	feeObj.put("MODE",mode);
	feeObj.put("CODE",code);
	feeObj.put("FEE",fee);
	feeObj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());

	$.feeMgr.insertFee(feeObj);
}
// 删除一笔费用
function delFee(mode,code,fee){
	var feeObj = new Wade.DataMap(); 
	feeObj.put("MODE",mode);
	feeObj.put("CODE",code);
	feeObj.put("FEE",fee);
	feeObj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
	$.feeMgr.deleteFee(feeObj);
}
// 修改费用
function modFee(mode,code,fee){
	var feeObj = new Wade.DataMap(); 
	feeObj.put("MODE",mode);
	feeObj.put("CODE",code);
	feeObj.put("FEE",fee);
	feeObj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
	$.feeMgr.modFee(feeObj);
}
// 有价卡赠送时显示余额
function showBalance(obj){
	$("#balanceDiv").val(obj.value/100);
//	$("#AUDIT_STAFF_ID").val(obj.options[obj.selectedIndex].text);
	$("#AUDIT_STAFF_ID").val(obj.selectedText);
}
/**
 * 批量加入
 * @returns {Boolean}
 */
function importGiveValueData(){

	if($("#cond_give_value").val()==""){
		$.TipBox.show(document.getElementById('UP_FILE_TEXT'), "加入文件不能为空！", "red");
//		alert('加入文件不能为空！');
		return false;
	}
   var activate = $("#REMARK").val();
    if ( activate == "" )
    {
    	$.TipBox.show(document.getElementById('REMARK_BALANCE'), "备注不能为空！", "red");
//        alert("提示:备注不能为空！");
        return false;
    }
	
	$.beginPageLoading("努力加入中...");
	$.ajax.submit('QueryCondPart,SubmitImportGiveValueCard,AuthPart','importGiveValueData','','BasicInfosPart,SaleInfosPart',function(data){
		$.cssubmit.disabledSubmitBtn(false);

		  if ("3" == data.get("checkCustomerNumber")) {
			  MessageBox.alert("提示",  "您输入的号码存在非移动号码，请核对再继续！");
			} else {
				MessageBox.alert("提示", "加入成功！");
				table1.adjust();
				table2.adjust();
				$("#TableArea").css("display", "");
			}
		addFee(data,"G");
		$.endPageLoading();
		return;
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
	});
}

/**
 * upload 上传逻辑
 */

//绑定上传组件清除按钮事件
function clearUpFile(){
	$("#UP_FILE_TEXT").val("");
	$("#UP_FILE_ID").val("");
}
//多文件上传,绑定上传组件确定按钮事件
function okUpFile(){
	var obj = $("#FILE_UPLOAD").val();
	$("#UP_FILE_TEXT").val(obj.NAME);
	$("#UP_FILE_TEXT").attr('tip',obj.NAME);
	$("#cond_give_value").val(obj.ID);
	hidePopup('UI-popup','UI-popup-upload');
}

function checkUserinit(){
	if($("#CUST_NAME").val() == ""){
		return false;
	}
	return true;
}

