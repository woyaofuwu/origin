function refreshPartAtferAuth(data) {
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO=" + data.get("USER_INFO").toString() + "&CUST_INFO="
			+ data.get("CUST_INFO").toString(), 'QueryCondPart', function(data) {
		$("#QueryCondPart").removeClass("e_dis")
		$("#CUST_NAME").attr("disabled", true);
		// 查询后解开卡号输入的限制
		$("#START_CARD_NO").attr("disabled", null);
		$("#END_CARD_NO").attr("disabled", null);
		$("#baseinfo_radio_b").attr("disabled", null);
		$("#PROSECUTION_WAY").attr("disabled", null);
		$("#addButtom").attr("disabled", null);
		$("#START_CARD_NO").val("");
		$("#END_CARD_NO").val("");
		$("#START_CARD_NO").focus();
		cleanTable();
		// 设置提示信息
		if (data.get("HINT_INFO") != null && data.get("HINT_INFO") != "") {
			$("#ALERT_INFO").text(data.get("HINT_INFO"));
			$("#ALERT_INFO_DIV").css("display", "block");
		}
	}, function(error_code, error_info) {
		$.endPageLoading();
		MessageBox.alert("提示",error_info);
	});
}

// 输入起始卡号的同时，赋值给截止卡号
function startCardKeyUp() {
	if ($("#RES_KIND_CODE").val() != "C") {
		$("#END_CARD_NO").val($("#START_CARD_NO").val());
	}
}

function textToUpperCase() {
	if (event.keyCode >= 65 && event.keyCode <= 90 || event.type == "change") {
		event.srcElement.value = event.srcElement.value.toUpperCase();
	}
}

//换卡加入按钮
function addChangeValueCard(str, objTable) {
	var start = document.getElementById("START_CARD_NO");
	var end = document.getElementById("END_CARD_NO");
	var vstart = start.value;
	if (str == 'C') {
        MessageBox.confirm("提示信息","请确认一下老卡信息,卡号是：" + vstart, function(btn){
            if(btn == "ok"){
                addValueCard(str, objTable);
            }
        });
	}
}

// 根据页面radio选项决定是否显示折扣率、销售价格
function cleanTable() {
	$.cssubmit.disabledSubmitBtn(true); // 禁止提交
	var table1Dom = document.getElementById("table1"); // 清空表格
	if (table1Dom != null) { // 有价卡换卡没有table1
		var length1 = $(table1Dom).find("tbody > tr").length; // 获取表格的数据行数
		for (var i = 0; i < length1; i++) {
			table1.deleteRow(0, true);
		}
	}
	var table2Dom = document.getElementById("table2");
	var length2 = $(table2Dom).find("tbody > tr").length;
	for (var i = 0; i < length2; i++) {
		table2.deleteRow(0, true);
	}
	// 清除购卡费
	modFee("0", "20", 0);
	hk_count = 0;
}

// 点击“加入”时作验证
function addValueCard(str, objTable) {
	if ($("#START_CARD_NO").val().length < 6 || $("#END_CARD_NO").val().length < 6) {
		MessageBox.alert("提示","状态:卡号输入错误！");
		return false;
	}
	var start = $("#START_CARD_NO");
	var end = $("#END_CARD_NO");
	var vstart = start.val();
	var vend = end.val();
	if (str == "S") {
		var discount = $("#DISCOUNT");
		var saleprice = $("#SALEPRICE");
		var radio = $("#baseinfo_radio_b");
	}
	if (str == 'C' && vstart == vend) {
		MessageBox.alert("提示","状态:原卡号与新卡号不能为同一卡号！");
		end.focus();
		return false;
	}
	if (vstart == "") {
		MessageBox.alert("提示","状态:请检查卡号！");
		start.focus();
		return false;
	}
	if (vend == "") {
		MessageBox.alert("提示","状态:请检查卡号！");
		end.focus();
		return false;
	}
	if (str != "C") {
		// 保证在同一号段，即从第一位到倒数第五位相同
		if (vstart.substring(0, vstart.length - 4) != vend.substring(0, vstart.length - 4)) {
			MessageBox.alert("提示","状态:起始卡号" + vstart.substring(0, vstart.length - 4) + "和截止卡号" + vend.substring(0, vstart.length - 4)
					+ "必须在同一号段！");
			end.focus();
			return false;
		}
		if (vstart > vend) {
			MessageBox.alert("提示","状态:请将数字较大的卡号置于截止卡号！");
			end.focus();
			return false;
		}
	}

	if (str == "S" && radio.attr("checked")) {
		if (discount.val() == "" && saleprice.val() == "") {
			MessageBox.alert("提示","状态:请输入【折扣率】或【销售价格】！");
			discount.focus();
			return false;
		}
		if (discount.val() != "" && saleprice.val() != "") {
			MessageBox.alert("提示","状态:【折扣率】和【销售价格】不可以同时填写！");
			discount.focus();
			return false;
		}
		if (!(discount.val() == "" ? false : checkFloat(discount, "【折扣率】"))
				&& !(saleprice.val() == "" ? false : checkFloat(saleprice, "【销售价格】"))) {
			return false;
		}
		if (parseFloat(discount.val()) > 10 || parseFloat(discount.val()) < 0) {
			MessageBox.alert("提示","状态:【折扣率】输入不正确！");
			return false;
		}
	}
	if (($(objTable).find("tbody > tr").length + 1) > 0) {
		if (str == "C") {
			for (var i = 0; i < $(objTable).find("tbody > tr").length; i++) {
				var tableHtmlStart = $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(1).text());
				var tableHtmlEnd = $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(2).text());
				if (vstart == tableHtmlStart || vend == tableHtmlEnd) {
					MessageBox.alert("提示","状态:输入卡号与已有号段中号码重复！");
					return false;
				}
			}
		} else {
			for (var i = 0; i < $(objTable).find("tbody > tr").length; i++) {
				var tableHtmlStart = $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(1).text());
				var tableHtmlEnd = $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(2).text());
				if (vstart >= tableHtmlStart && vstart <= tableHtmlEnd) {
					MessageBox.alert("提示","状态:输入卡号与已有号段中号码重复！");
					return false;
				}
				if (vend >= tableHtmlStart && vend <= tableHtmlEnd) {
					MessageBox.alert("提示","状态:输入卡号与已有号段中号码重复！");
					return false;
				}
			}
		}
		$("#X_CODING_STR").val(codingTable().toString());
	}
	var param = "";
	param += "&table2=" + $("#X_CODING_STR").val();
	$.beginPageLoading();
	$.ajax.submit('QueryCondPart,AuthPart', 'addClick', param, 'BasicInfosPart,SaleInfosPart', function(data) {
		$.cssubmit.disabledSubmitBtn(false);
		if (str == "C") {
			$.endPageLoading();
			return true;
		}
		var info = data.get(0, "alertInfo");
		if (info != null) {
			MessageBox.alert("提示","状态:" + info);
		}
		addFee(data, str);
		$.endPageLoading(); // today 费用组件的处理待续，先返回
		return;// today
	}, function(errorcode, errorinfo) {
		MessageBox.alert("提示",errorinfo);
		$.endPageLoading();
	});

}

// 验证浮点型数据 (整数部分一位，小数部分无或一位、两位)
function checkFloat(obj, desc) {
	var expression = /^\d+(?:\.\d{1,2})?$/;
	if (!expression.exec(obj.val())) {
		MessageBox.alert("提示","状态:" + desc + "格式错误！");
		obj.focus();
		return false;
	}
	return true;
}

// table 数据
function codingTable() {
	var result = table2.getData(true);
	return result;
}

// 加入后处理费用
function addFee(data, str) {
	var fee1 = 0;
	if (str == "S") {
		fee1 = getTable2Fee();
	} else if (str == "B") {
		fee1 = 0 - getTable2Fee();
	}
	if (str == "S" || str == "B") {
		if (hk_count == 0) {
			insertFee("0", "20", fee1);
			hk_count = 1;
		} else {
			modFee("0", "20", fee1);
		}
	}
	var trade = $.feeMgr.getFeeTrade();
}

// 获取TABLE2中总费用
function getTable2Fee() {
	var objTable = document.getElementById("table2");
	var feeTotal = 0.0;
	for (var i = 0; i < $(objTable).find("tbody > tr").length; i++) {
		var tableTdFee = $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(5).text());
		feeTotal = feeTotal + parseFloat(tableTdFee) * 100;
	}
	return feeTotal;
}

function deleteRow1(rowIndex, str) {
	$("#IS_DEL").val("true");
    MessageBox.confirm("提示信息","确定要删除该条记录？", function(btn){
        if(btn == "ok"){
            if ($("#IS_DEL").val() == "true") {
                $("#IS_DEL").val("false");
                deleteRow(rowIndex, str);
            }
        }
    });
}

// 双击TABLE2中某一行进行删除
function deleteRow(rowIndex, str) {
	var table2Dom = document.getElementById("table2");
	var fee2 = getTable2Fee();
	var fee = parseFloat($.trim($(table2Dom).find("tbody > tr").eq(rowIndex - 1).find("td").eq(5).text()));
	if (fee2 == fee) {
		hk_count = 0
	}
	// 删除记录
	table2.deleteRow(rowIndex - 1, true);
	if ($(table2Dom).find("tbody > tr").length < 1) {
		$("#X_CODING_STR").val("");
	}
	if (str == "B") {
		delFee("0", "20", -fee);
	} else {
		delFee("0", "20", fee);
	}
}

// 提交校验
function checkBeforeSubmit() {
	var allData = table2.getData(true);
	if (allData.length == 0) {
		MessageBox.alert("提示","提示:没有有价卡信息，请添加有价卡信息");
		return false;
	}
	$("#X_CODING_STR").val(allData);
	if ($("#TRADE_TYPE_CODE").val() == "416") {
		var saleCount = 0;
		var price = 0;
		var resKindName = "";
		var objTable = document.getElementById('table2');
		for (var i = 0; i < $(objTable).find("tbody > tr").length; i++) {
			saleCount = saleCount + Number($.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(6).text()));
			price = Number($.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(4).text()));
			resKindName = $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(0).text());
		}
	}
	if ($("#TRADE_TYPE_CODE").val() == "419") {
		$.printMgr.setPrintParam("DIY_OPERFEENAME", "退款通知单");
		MessageBox.alert("提示",'提示:有价卡全部退回的话,请回收并作废该有价卡销售时开具的发票');
	}
	return true;
}

// 业务提交
function onTradeSubmit(str) {
	var param = "";
	param += "&X_CODING_STR=" + $("#X_CODING_STR").val();
	// 提交前校验
	if (!checkBeforeSubmit()) {
		return false;
	}
	var saleCount = 0; // 操作卡数量
	var total = 0; // 赠卡总金额
	var objTable = document.getElementById('table2');
	for (var i = 0; i < $(objTable).find("tbody > tr").length; i++) {
		if (str == "C") {
			saleCount = saleCount + Number($.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(5).text()));
		} else if (str == "G") {
			saleCount = saleCount + Number($.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(6).text()));

			var fee = Number($.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(6).text()))
					* Number($.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(8).text()))
			total += fee; // 计算赠送卡总额
		} else {
			saleCount = saleCount + Number($.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(6).text()));
		}
	}
	if ($("#TRADE_TYPE_CODE").val() == "430") {
		var tempFee = $("#changeFee").val();
		var totalFee = total / 100 + ".00";
		if (new Number(tempFee) < new Number(totalFee)) {
			MessageBox.alert("提示","赠送的有价卡面值[" + totalFee + "]超出可兑换话费卡金额[" + tempFee + "]，业务无法继续！");
			return false;
		}
	}
	if (str == "G" && total > Number($("#balanceDiv").val()) * 100) {// 赠送卡总额大于审批总额，不能提交
		MessageBox.alert("提示","状态:赠送量大于审批量，不予通过!");
		return false;
	}

	return true;
}

var hk_count = 0;
// 当修改客户名称的时候需要修改打印信息，修改标志
function changeInvoiceTag() {
	$("#INVOICE_TAG").val("1");
}

// 增加一笔费用
function insertFee(mode, code, fee) {
	var feeObj = new Wade.DataMap();
	feeObj.put("MODE", mode);
	feeObj.put("CODE", code);
	feeObj.put("FEE", fee);
	feeObj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
	$.feeMgr.insertFee(feeObj);
}
// 删除一笔费用
function delFee(mode, code, fee) {
	var feeObj = new Wade.DataMap();
	feeObj.put("MODE", mode);
	feeObj.put("CODE", code);
	feeObj.put("FEE", fee);
	feeObj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
	$.feeMgr.deleteFee(feeObj);
}
// 修改费用
function modFee(mode, code, fee) {
	var feeObj = new Wade.DataMap();
	feeObj.put("MODE", mode);
	feeObj.put("CODE", code);
	feeObj.put("FEE", fee);
	feeObj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
	$.feeMgr.modFee(feeObj);
}

//将TABLE2中数据拼串 已改造为
function coding(objTable, str) {
	var xCodingStr = "";
	var idata = null;
	var dataset = new Wade.DatasetList();
	for (var i = 0; i < $(objTable).find("tbody > tr").length; i++) {
		idata = new Wade.DataMap();
		idata.put("startCardNo", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(1).text()));
		idata.put("endCardNo", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(2).text()));
		idata.put("simPrice", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(3).text()));
		idata.put("singlePrice", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(4).text()));
		idata.put("totalPrice", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(5).text()));
		idata.put("rowCount", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(6).text()));
		idata.put("valueCode", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(7).text()));
		idata.put("advise_price", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(8).text()));
		idata.put("activateInfo", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(9).text()));
		idata.put("devicePrice", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(10).text()));
		idata.put("cardType", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(11).text()));
		idata.put("activeFlag", $.trim($(objTable).find("tbody > tr").eq(i).find("td").eq(12).text()));
		dataset.add(idata);
	}
	idata = null;
	xCodingStr = dataset.toString();
	dataset = null;
	return xCodingStr;
}