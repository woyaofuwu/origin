var viceLisIndex = 10;

function refreshPartAtferAuth(data) {
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&USER_ID=' + userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	$.beginPageLoading("查询中...");
	ajaxSubmit(null, 'loadInfo', param, 'batAddVicePart,viceInfopart',
			function(data) {
				$.endPageLoading();
			}, function(code, info, detail) {
				$.endPageLoading();
				$.cssubmit.disabledSubmitBtn(true);
				MessageBox.error("错误提示", info);
			}, function() {
				$.endPageLoading();
				$.cssubmit.disabledSubmitBtn(true);
				MessageBox.alert("告警提示", "查询超时!", function(btn) {
				});
			});
}

function addMeb() {
	var mainSn = $("#AUTH_SERIAL_NUMBER").val();
	var viceSerialNumber = $("#FMY_SERIAL_NUMBER").val(); // 新增成员号码

	if (viceSerialNumber.length <= 0) {
		$.TipBox.show(document.getElementById("FMY_SERIAL_NUMBER"), "请输入新增副卡号码（多个号码以空格分隔）", "red");
		return false;
	}

	viceSerialNumber = viceSerialNumber.trim().split(" ");

	if (viceSerialNumber == "") {
		$.TipBox.show(document.getElementById("FMY_SERIAL_NUMBER"), "请输入新增副卡号码（多个号码以空格分隔）", "red");
		return false;
	}
	// 删除新增副卡数组中的空字符
	for ( var i = 0; i < viceSerialNumber.length; i++) {
		var sn = viceSerialNumber[i];
		if (sn == "") {
			viceSerialNumber.splice(i, 1);
			i--;
		}
	}
	var viceInfoDataset = $.DatasetList();
	viceInfoDataset = getViceList();

	for ( var i = 0; i < viceSerialNumber.length; i++) {
		var mebSn = viceSerialNumber[i];
		// 校验批量输入的号码，不能重复
		for ( var k = i + 1; k < viceSerialNumber.length; k++) {
			var tempMebSn = viceSerialNumber[k];
			if (mebSn == tempMebSn) {
				$.TipBox.show(document.getElementById("FMY_SERIAL_NUMBER"), "对不起，您输入的手机号码[" + mebSn + "]重复，请勿重复输入！", "red");
				return false;
			}
		}
		// 校验手机号码格式
		if (!$.verifylib.checkMbphone(mebSn)) {
			$.TipBox.show(document.getElementById("FMY_SERIAL_NUMBER"), "对不起，您输入的手机号码[" + mebSn + "]格式不正确，请重新输入！", "red");
			return false;
		}
		// 不能与主号一致
		if (mebSn == mainSn) {
			$.TipBox.show(document.getElementById("FMY_SERIAL_NUMBER"), "新增的亲情号码不能和主卡号码一致,请确认！", "red");
			return false;
		}

		for ( var j = 0, size = viceInfoDataset.length; j < size; j++) {
			var tmp = viceInfoDataset.get(j);
			var sn = tmp.get('SERIAL_NUMBER_B');
			var tag = tmp.get('tag');
			if (mebSn == sn) {
				$.TipBox.show(document.getElementById("FMY_SERIAL_NUMBER"), "号码" + mebSn + "已经在成员列表", "red");
				return false;
			}
		}
	}

	addViceList(viceSerialNumber);
	$("#FMY_SERIAL_NUMBER").val("");
}

function delMeb(obj) {
	var idx = $(obj).attr("idx");

	// tag取值说明：""-未做改动，"0"-新增，"1"-删除，"2"-修改
	var tag = $("#tag_" + idx).val();

	if (tag == "" || tag == undefined) { // 存量副卡删除，改变样式，并赋值tag=1
        var sysDateT = (new Date()).format('yyyy-MM-dd');
        var sysDate = (new Date()).format('yyyy-MM-dd HH:mm:ss');
        $("#END_DATE_T_" + idx).html(sysDateT);
        $("#END_DATE_" + idx).val(sysDate);

		$("#SERIAL_NUMBER_B_T_" + idx).addClass("e_delete");
		$("#delMebBt_" + idx).css("display", "none");
		$("#resetMebBt_" + idx).css("display", "");
		$("#tag_" + idx).val("1");
	} else if (tag == "0") { // 新增副卡的删除，直接移除
		$(obj).parent(".viceList").remove();
		$("#tag_" + idx).val("");
	}
}

function resetMeb(obj) {
	var idx = $(obj).attr("idx");

	// tag取值说明：""-未做改动，"0"-新增，"1"-删除，"2"-修改
	var tag = $("#tag_" + idx).val();
	if (tag == "1") {
		$("#SERIAL_NUMBER_B_T_" + idx).removeClass("e_delete");
		$("#delMebBt_" + idx).css("display", "");
		$("#resetMebBt_" + idx).css("display", "none");
		$("#tag_" + idx).val("");

        var orgEndDate = $("#ORG_END_DATE_" + idx).val();
        var orgEndDateT = orgEndDate.substring(0, 10);

        $("#END_DATE_T_" + idx).html(orgEndDateT);
        $("#END_DATE_" + idx).val(orgEndDate);
	}
}

function addViceList(viceSerialNumber) {
	for ( var i = 0; i < viceSerialNumber.length; i++) {
		var mebSn = viceSerialNumber[i];
		var startDate = new Date();
		var tempNode = "";

		tempNode += '<li class="viceList" id="viceList_' + viceLisIndex + '" name="viceList" idx="' + viceLisIndex + '">';
		tempNode += '	<div id="viceValue_' + viceLisIndex + '" class="main e_green">';
		tempNode += '		<div class="title" id="SERIAL_NUMBER_B_T_' + viceLisIndex + '">' + mebSn + '</div>';
		tempNode += '		<div class="content">' + startDate.format("yyyy-MM-dd") + ' ~ 2050-12-31</div>';

		tempNode += '		<input type="hidden" name="INST_ID" id="INST_ID_' + viceLisIndex + '" value=""/>';
		tempNode += '		<input type="hidden" name="SERIAL_NUMBER_B" id="SERIAL_NUMBER_B_' + viceLisIndex + '" value="' + mebSn + '"/>';
		tempNode += '		<input type="hidden" name="START_DATE" id="START_DATE_' + viceLisIndex + '" value="' + startDate.format("yyyy-MM-dd HH:mm:ss") + '"/>';
		tempNode += '		<input type="hidden" name="END_DATE" id="END_DATE_' + viceLisIndex + '" value="2050-12-31 23:59:59"/>';
		tempNode += '		<input type="hidden" name="tag" id="tag_' + viceLisIndex + '" value="0"/>';
		tempNode += '		<input type="hidden" name="checkTag" id="checkTag_' + viceLisIndex + '" value=""/>';
		tempNode += '	</div>';
		tempNode += '	<div id="checkMebBt_' + viceLisIndex + '" idx="' + viceLisIndex + '" class="fn" onclick="checkMember(' + "'" + viceLisIndex + "'" + ');" tip="校验"><span class="e_ico-check"></span></div>';
		tempNode += '	<div id="sucMebBt_' + viceLisIndex + '" idx="' + viceLisIndex + '" class="fn" onclick="sucMeb(this);" style="display:none" tip="校验成功"><span class="e_ico-ok"></span></div>';
		tempNode += '	<div jwcid="@Any" class="fn" id="delMebBt_' + viceLisIndex + '" name="delMebBt" idx="' + viceLisIndex + '" onclick="delMeb(this);" tip="删除"><span class="e_ico-delete"></span></div>';
		tempNode += '</li>\r\n';

		$("#viceInfoListPart > ul").prepend(tempNode);
		viceLisIndex++;
	}
}

/**
 * 校验成员
 * 
 * @param idx
 */
function checkMember(idx) {
	var viceInfoDataset = $.DatasetList();
	viceInfoDataset = getViceList();
	for ( var i = 0; i < viceInfoDataset.length; i++) {
		var viceInfoData = viceInfoDataset.get(i);
		var tag = viceInfoDataset.get(i).get('tag');
		if (tag != "" && tag != undefined) { // 过滤掉新增和删除的数据，只保留存量副卡
			viceInfoDataset.remove(viceInfoData);
			i--;
		}
	}

	var mebSn = $("#SERIAL_NUMBER_B_" + idx).val();

	var param = "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val();
	param += '&SERIAL_NUMBER_B=' + mebSn + '&MEB_LIST=' + viceInfoDataset;

	$.beginPageLoading("成员号码校验...");
	ajaxSubmit(null, 'checkAddMeb', param, '', function(data) {
		$.endPageLoading();
		checkMemberSuc(data, idx);
	}, function(code, info, detail) {
		$.endPageLoading();
		$('#FMY_SERIAL_NUMBER').val('');
		MessageBox.error("错误提示", info);
	}, function() {
		$.endPageLoading();
		$.cssubmit.disabledSubmitBtn(true);
		MessageBox.alert("告警提示", "校验超时!", function(btn) {
		});
	});
}

/**
 * 校验成功
 * 
 * @param data
 * @param idx
 */
function checkMemberSuc(data, idx) {
	$("#checkTag_" + idx).val("1"); // 校验成功赋值

	$("#checkMebBt_" + idx).css("display", "none");
	$("#sucMebBt_" + idx).css("display", "");
}

/**
 * 校验成功
 * 
 * @param obj
 */
function sucMeb(obj) {
	var idx = $(obj).attr("idx");
	if ("1" == $("#checkTag_" + idx).val()) {
		MessageBox.success("提示信息", "已通过校验,无须重复校验!");
		return;
	}
}

/**
 * 获取页面上所有成员号码数据（包括未做修改的存量成员-tag=""）
 * 
 * @returns
 */
function getViceList() {
	var obj = $("#viceInfoListPart .viceList");
	var viceInfoDataset = $.DatasetList();

	for ( var i = 0; i < obj.length; i++) {
		var idx = obj.eq(i).attr("idx");
		var viceInfoData = $.DataMap();
		viceInfoData.put("INST_ID", $("#INST_ID_" + idx).val());
		viceInfoData.put("SERIAL_NUMBER_B", $("#SERIAL_NUMBER_B_" + idx).val());
		viceInfoData.put("START_DATE", $("#START_DATE_" + idx).val());
		viceInfoData.put("END_DATE", $("#END_DATE_" + idx).val());
		viceInfoData.put("tag", $("#tag_" + idx).val());
		viceInfoData.put("checkTag", $("#checkTag_" + idx).val());

		viceInfoDataset.add(viceInfoData);
	}
	return viceInfoDataset;
}

function onTradeSubmit() {
	var unCheckedMember = "";

	var viceInfoDataset = $.DatasetList();
	viceInfoDataset = getViceList();
	for ( var i = 0; i < viceInfoDataset.length; i++) {
		var viceInfoData = viceInfoDataset.get(i);
		var tag = viceInfoDataset.get(i).get('tag');
		var checkTag = viceInfoDataset.get(i).get('checkTag');
		var serialNumberB = viceInfoDataset.get(i).get('SERIAL_NUMBER_B');
		if (tag == "" || tag == undefined) { // 未进行修改的存量成员号码数据不提交到后台
			viceInfoDataset.remove(viceInfoData);
			i--;
		}
		if (tag == "0" && checkTag != "1") { // 未通过校验的成员号码数据不提交到后台
			unCheckedMember = unCheckedMember + serialNumberB + ",";
			viceInfoDataset.remove(viceInfoData);
			i--;
		}
	}

	var param = '&SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();
	param += '&MEB_LIST=' + viceInfoDataset;
	param += '&REMARK=' + $('#REMARK').val();

	if (viceInfoDataset.length <= 0) {
		if (unCheckedMember != "") {
			unCheckedMember = unCheckedMember.substring(0, unCheckedMember.length - 1);
			MessageBox.alert("信息提示", "新增成员[" + unCheckedMember + "]未通过校验，无有效提交数据！");
		} else {
			MessageBox.alert("信息提示", "您没有进行任何操作，不能进行提交！");
		}
		return false;
	} else if (unCheckedMember != "") {
		unCheckedMember = unCheckedMember.substring(0, unCheckedMember.length - 1);
		MessageBox.confirm("确认提示", "新增成员[" + unCheckedMember + "]未通过校验，本次受理不会加入该亲情网！是否继续提交？", function(btn) {
			if (btn == "ok") {
				$.cssubmit.addParam(param);
				$.cssubmit.submitTrade();
				return true;
			} else if (btn == "cancel") {
				return false;
			}
		});
	} else {
		$.cssubmit.addParam(param);
		return true;
	}
}