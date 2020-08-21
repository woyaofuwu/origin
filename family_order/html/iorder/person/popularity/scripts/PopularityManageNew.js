$(window).load(function() {
	$("#QRY_POPULARITY_TYPE").val("2");
	$("#POPULARITY_TYPE").val("2");
	$("#QRY_OFFER_CODE").focus();
	$("#QRY_OFFER_CODE").bind("keydown", onQryOfferCodeInputKeyDown);
	$("#OFFER_CODE").bind("keydown", onOfferCodeInputKeyDown);
});

function onQryOfferCodeInputKeyDown(e) {
	if (e.keyCode == 13 || e.keyCode == 108) {
		queryPopularity(e);
	}
}

function onOfferCodeInputKeyDown(e) {
	if (e.keyCode == 13 || e.keyCode == 108) {
		queryCode(e);
	}
}

/**
 * 查询
 * @author zhaohj3
 */
function queryPopularity() {
	$.beginPageLoading();

	ajaxSubmit('PopularityCond', 'queryPopularity', null, 'PopularityList,addPart',
			function(data) {
				$.endPageLoading();
			},  function(code, info, detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", info);
			}, function() {
				$.endPageLoading();
				MessageBox.alert("告警提示", "查询超时!", function(btn) {
				});
			});
}

/**
 * 新增
 * @returns {Boolean}
 * @author zhaohj3
 */
function addPopularity() {
	if (!checkPopularity()) {
		return false;
	}

	var editPopularityType = $("#POPULARITY_TYPE").val();
	var editPopularityTradeType = $("#POPULARITY_TRADE_TYPE").val();
	var editOfferCode = $("#OFFER_CODE").val();
	
	var allRowData = PopularityTable.getData(true);
	if(allRowData != null && allRowData != "") {
		// 新增的两条同商品类型的推荐业务不能重复
		for ( var k = 0; k < allRowData.length; k++) {
			if (editPopularityType == allRowData[k].get("POPULARITY_TYPE")
					&& editPopularityTradeType == allRowData[k].get("POPULARITY_TRADE_TYPE")
					&& editOfferCode == allRowData[k].get("OFFER_CODE")) {
				MessageBox.alert("提示信息", "该推荐业务已经在推荐列表中，不能重复新增，请选中后在热门推荐详情中编辑进行修改！");
				return false;
			}
		}
	}

	var addRowData = PopularityTable.getData();
	var editPriority = $("#PRIORITY").val();
	if(addRowData != null && allRowData != "") {
		// 新增的两条同商品类型的推荐业务的优先级不能重复
		for ( var k = 0; k < addRowData.length; k++) {
			if (editPopularityType == addRowData[k].get("POPULARITY_TYPE")
					&& editPopularityTradeType == addRowData[k].get("POPULARITY_TRADE_TYPE")
					&& editPriority == addRowData[k].get("PRIORITY")) {
				MessageBox.alert("提示信息", "新增的同一类型同一商品类型，优先级不能重复！");
				return false;
			}
		}
	}
	
	$.beginPageLoading("新增热门推荐信息...");
	ajaxSubmit('', 'getDefaultParam', null, '',
			function(data) {
				$.endPageLoading();
				var editJsonData = getEditJsonData(data);
				PopularityTable.addRow(editJsonData);
			},  function(code, info, detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", info);
			}, function() {
				$.endPageLoading();
				MessageBox.alert("告警提示", "查询超时!", function(btn) {
				});
			});
}

/**
 * 修改
 * @returns {Boolean}
 * @author zhaohj3
 */
function editPopularity() {
	if (!checkPopularity()) {
		return false;
	}

	var rowIndex = PopularityTable.selected;
	if (rowIndex == undefined) {
		MessageBox.alert("提示信息", "请选择需要修改的数据!");
		return false;
	}
	
	var rowData = PopularityTable.getRowData(rowIndex);
	
	var editOfferCode = $("#OFFER_CODE").val();
	var rowOfferCode = rowData.get("OFFER_CODE");
	
	if (editOfferCode != rowOfferCode) {
		MessageBox.alert("提示信息", "不能变更推荐业务商品编码!");
		return false;
	}
	
	$.beginPageLoading("新增热门推荐信息...");
	ajaxSubmit('', 'getDefaultParam', null, '',
			function(data) {
				$.endPageLoading();
				var editJsonData = getEditJsonData(data);
				PopularityTable.updateRow(editJsonData, rowIndex);
			},  function(code, info, detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", info);
			}, function() {
				$.endPageLoading();
				MessageBox.alert("告警提示", "查询超时!", function(btn) {
				});
			});
}

/**
 * 删除
 * @author zhaohj3
 */
function deletePopularity() {
	var rowIndex = PopularityTable.selected;
	if (rowIndex == undefined) {
		MessageBox.alert("提示信息", "请选择需要删除的数据!");
		return false;
	}
	PopularityTable.deleteRow(rowIndex);
}

/**
 * 检查新增、修改的必填选项
 * @returns {Boolean}
 * @author zhaohj3
 */
function checkPopularity() {
	if ($("#POPULARITY_TYPE").val() == "1") { // 热门
		MessageBox.alert("提示信息", "不允许新增或修改热门业务!");
		return false;
	}
	if (!$.validate.verifyAll("addPart")) {
		return false;
	}

	if ($("#POPULARITY_TRADE_TYPE").val() == "2") { // 营销活动
		if ($("#PRODUCT_ID").val() == undefined || $("#PRODUCT_ID").val() == "") {
			$.TipBox.show(document.getElementById("PRODUCT_ID"), "营销方案不能为空！", "red");
			return false;
		} else if ($("#CAMPN_TYPE").val() == undefined || $("#CAMPN_TYPE").val() == "") {
			$.TipBox.show(document.getElementById("CAMPN_TYPE"), "活动类型不能为空！", "red");
			return false;
		}
	}
	
	return true;
}

/**
 * 获取编辑数据
 * @param data
 * @returns {___anonymous3152_4082}
 */
function getEditJsonData(data) {
	var editJsonData = {
			"POPULARITY_TRADE_TYPE":$("#POPULARITY_TRADE_TYPE").val(),
			"POPULARITY_TRADE_TYPE_NAME":$("#POPULARITY_TRADE_TYPE").text(),
			"POPULARITY_TYPE":$("#POPULARITY_TYPE").val(),
			"POPULARITY_TYPE_NAME":$("#POPULARITY_TYPE").text(),
			"OFFER_CODE":$("#OFFER_CODE").val(),
			"OFFER_NAME":$("#OFFER_NAME").val(),
			"OFFER_DESCRIPTION":$("#OFFER_DESCRIPTION").val(),
			"POPULARITY_DEFAULT_ICON":$("#POPULARITY_DEFAULT_ICON").val(),
			"POPULARITY_ICON":$("#POPULARITY_ICON").val(),
			"PRIORITY":$("#PRIORITY").val(),
			"START_DATE":$("#START_DATE").val(),
			"END_DATE":$("#END_DATE").val(),
			"UPDATE_TIME":(new Date()).format('yyyy-MM-dd HH:mm:ss'),
			"UPDATE_STAFF_ID":data.get("STAFF_ID"),
			"UPDATE_DEPART_ID":data.get("DEPART_ID"),
			"PRODUCT_ID":$("#PRODUCT_ID").val(),
			"PRODUCT_NAME":$("#PRODUCT_NAME").val(),
			"CAMPN_TYPE":$("#CAMPN_TYPE").val(),
			"CAMPN_NAME":$("#CAMPN_NAME").val(),
			"MENU_ID":$("#MENU_ID").val(),
			"MENU_TITLE":$("#MENU_ID").text(),
			"REMARKS":$("#REMARKS").val()
		};
	return editJsonData;
}

/**
 * 点击热门推荐表格，提取行数据
 * @param obj
 * @author zhaohj3
 */
function PopularityTableClick(obj) {
	var rowData = PopularityTable.getRowData(obj.rowIndex - 1);
	$("#OFFER_CODE").val(rowData.get("OFFER_CODE"));
	$("#OFFER_NAME").val(rowData.get("OFFER_NAME"));
	$("#POPULARITY_TRADE_TYPE").val(rowData.get("POPULARITY_TRADE_TYPE"));
	$("#POPULARITY_TYPE").val(rowData.get("POPULARITY_TYPE"));
	$("#START_DATE").val(rowData.get("START_DATE"));
	$("#END_DATE").val(rowData.get("END_DATE"));
	$("#OFFER_DESCRIPTION").val(rowData.get("OFFER_DESCRIPTION"));
	$("#POPULARITY_DEFAULT_ICON").val(rowData.get("POPULARITY_DEFAULT_ICON"));
	$("#POPULARITY_ICON").val(rowData.get("POPULARITY_ICON"));
	$("#PRIORITY").val(rowData.get("PRIORITY"));
	$("#PRODUCT_NAME").val(rowData.get("PRODUCT_NAME"));
	$("#PRODUCT_ID").val(rowData.get("PRODUCT_ID"));
	$("#CAMPN_TYPE").val(rowData.get("CAMPN_TYPE"));
	$("#CAMPN_NAME").val(rowData.get("CAMPN_NAME"));
	$("#MENU_ID").val(rowData.get("MENU_ID"));
	$("#REMARKS").val(rowData.get("REMARKS"));
	if(rowData.get("POPULARITY_TRADE_TYPE") == "2"){	//营销活动
		showMoreInfo();
	}
}

/**
 * 提交
 * @returns {Boolean}
 */
function submitPopularity() {
	var submitData = PopularityTable.getData();
	if (submitData == null || submitData.length == 0) {
		MessageBox.alert("提示信息", "您没有进行任何操作，不能提交！");
		return false;
	}
	var params = "&POPULARITY_LIST=" + submitData;
	
	$.beginPageLoading("数据提交中...");
	ajaxSubmit('', 'submitPopularity', params, 'PopularityList', function(rtnData) {
		MessageBox.success("成功提示", "操作成功！", function(btn) {
			if (btn == "ok") {
				window.location.href = window.location.href;
			}
		});
		$.endPageLoading();
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "操作超时!", function(btn) {
		});
	});
}
/**
 * 重置
 * @author zhaohj3
 */
function resetPage() {
	window.location.reload();
}

/**
 * 查询编码信息
 */
function queryCode(){
	var offer_code = $("#OFFER_CODE").val();
	var popularityType = $("#POPULARITY_TYPE").val();
	var popularityTradeType = $("#POPULARITY_TRADE_TYPE").val();
	if (popularityTradeType == null || popularityTradeType == "") {
		$("#POPULARITY_TRADE_TYPE").focus();
		$.TipBox.show(document.getElementById("POPULARITY_TRADE_TYPE_LI"), "请选择商品类型！", "red");
		return false;
	}
	if (offer_code == null || offer_code == "") {
		$("#OFFER_CODE").focus();
		$.TipBox.show(document.getElementById("OFFER_CODE"), "请输入商品编码！", "red");
		return false;
	}
	var params = "&OFFER_CODE=" + offer_code + "&POPULARITY_TYPE="
			+ popularityType + "&POPULARITY_TRADE_TYPE=" + popularityTradeType;
		
	$.beginPageLoading("查询中...");
	ajaxSubmit('', 'queryCode', params, 'addPart', function(rtnData){
		$.endPageLoading();
		showMoreInfo();
		$("#OFFER_CODE").val(offer_code);
		$("#POPULARITY_TRADE_TYPE").val(popularityTradeType);
		if(rtnData == null || rtnData == "" || rtnData.length == 0){
			MessageBox.alert("提示", "没有查询到相关数据！");
		}else if(rtnData.get('ISEXIST') == 1){
			MessageBox.alert("确认提示", "该推荐业务已存在，请选中后在热门推荐详情中编辑进行修改!！");

			$("#QRY_OFFER_CODE").val($("#OFFER_CODE").val());
			$("#QRY_POPULARITY_TYPE").val($("#POPULARITY_TYPE").val());
			queryPopularity();
		}
	},
	function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", info);
	});
}

/**
 * 
 */
function chgPopularityTradeType() {
	showMoreInfo();
	queryCode();
}

/**
 * 展示/隐藏营销活动必填信息
 */
function showMoreInfo() {
	var type = $("#POPULARITY_TRADE_TYPE").val();
	if (type == 2) { // 营销活动
		$("#PRODUCT_ID_LI").css("display", "");
		$("#CAMPN_TYPE_LI").css("display", "");
	} else {
		$("#PRODUCT_ID_LI").css("display", "none");
		$("#CAMPN_TYPE_LI").css("display", "none");
	}
}
