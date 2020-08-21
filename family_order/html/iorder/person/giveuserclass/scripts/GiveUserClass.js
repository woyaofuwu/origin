function refreshPartAtferAuth(data) {
	$('#TOADD_SERIAL_NUMBER').val('');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	$("#bquery").attr("disabled", false);
	$.ajax.submit(null, 'loadInfo', param,
			'viceInfopart,usergiveclasspart,otherinfohiddenpart', null,
			loadInfoError);
}
 

function loadInfoError(code, info) {
	$.cssubmit.disabledSubmitBtn(true);
	MessageBox.error("错误", info);
}

function onTradeSubmit() {
	var data = $.DatasetList();
	data = getMemberSnList(1); // 列表中已操作成员（TAG=1或者0）

	var param = '&SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();
	param += '&MEB_LIST=' + data;
	//param += '&MEMBER_CANCEL=' + "0";
	//param += '&REMARK=' + $('#REMARK').val();
	if (data.length == 0) {
		MessageBox.alert("提示", "您没有进行任何操作，不能提交！");
		return false;
	}
	//alert(param);
	$.beginPageLoading("业务处理中..");
	$.ajax.submit(null, 'submitData', param, null,
			function(data) {				
				var X_RESULTCODE = data.get("X_RESULTCODE");
				$.endPageLoading();
				if (X_RESULTCODE == '0000') {
					 MessageBox.alert("信息处理成功！","");	
						$('#TOADD_SERIAL_NUMBER').val('');
						$("#bquery").attr("disabled", false);
						$.ajax.submit(null, 'clearpagedata', '&SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val(),
								'viceInfopart,usergiveclasspart,otherinfohiddenpart', null,
								loadInfoError);
					 
				} else {
					MessageBox.error("错误提示", data.get("X_RESULTINFO"));
				}
			},
			function(error_code, error_info, detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, null, null, null, detail);
			});

}

var isCheck = true; // 设置标记防止重复提交

/**
 * 添加成员号码（支持多个添加）
 */
function addMember() {
	// 添加后禁用添加按钮，防止延时同号码添加###不可用，若页面存在缓存，则属性可能不生效
	// $("#bquery").attr("disabled",true);
	if (isCheck) {
		isCheck = false; // 设置禁用，返回错误或添加成功则放开

		var memberSnList = $("#TOADD_SERIAL_NUMBER").val(); // 新增成员list

		if (memberSnList.length <= 0) {
			$.TipBox.show(document.getElementById("TOADD_SERIAL_NUMBER"),
					"请输入需要赠送的全球通权益号码（多个号码以空格分隔）", "red");
			isCheck = true;
			return false;
		}
		memberSnList = memberSnList.trim();

		if (memberSnList.length > 1) {
			memberSnList = memberSnList.split(" ");
		}

		if (memberSnList == "") {
			$.TipBox.show(document.getElementById("TOADD_SERIAL_NUMBER"),
					"请输入需要赠送的全球通权益号码（多个号码以空格分隔）", "red");
			isCheck = true;
			return false;
		}

		// 删除新增号码数组中的空字符
		for ( var i = 0; i < memberSnList.length; i++) {
			var sn = memberSnList[i];
			if (sn == "") {
				memberSnList.splice(i, 1);
				i--;
			}
		}
		
		var memberSnExistsDataset = $.DatasetList();
		memberSnExistsDataset = getMemberSnList(0); // 列表中已有成员
		var AddMebMaxlNum = $("#AddMebMaxNum").val();
		
		if ((parseInt(memberSnList.length) + parseInt(memberSnExistsDataset.length)) > AddMebMaxlNum) {
			MessageBox.alert("提示",
			// '您共享的成员号码数量已经达到4个上限，不能再增加成员号码！您还可以添加 '+
			// (4-parseInt(memberSnExistsDataset.length)) +' 个成员');
					'您赠送的号码数量已经达到' + AddMebMaxlNum + '个上限，不能添加号码！');
			isCheck = true;
			return false;
		}

		var mainSn = $("#AUTH_SERIAL_NUMBER").val();
		for ( var i = 0; i < memberSnList.length; i++) {
			var mebSn = memberSnList[i];
			// 校验批量输入的号码，不能重复
			for ( var k = i + 1; k < memberSnList.length; k++) {
				var tempMebSn = memberSnList[k];
				if (mebSn == tempMebSn) {
					MessageBox.alert("提示", "对不起，您输入的手机号码[" + mebSn
							+ "]重复，请勿重复输入！");
					isCheck = true;
					return false;
				}
			}
			// 校验手机号码格式
			if (!$.verifylib.checkMbphone(mebSn)) {
				MessageBox.alert("提示", "对不起，您输入的手机号码[" + mebSn
						+ "]格式不正确，请重新输入！");
				isCheck = true;
				return false;
			}
			// 不能与主号一致
			if (mebSn == mainSn) {
				MessageBox.alert("提示", "对不起，赠送的码不能和主卡号码一样，请重新输入！");
				isCheck = true;
				return false;
			}

			// 与已有号码校验
			for ( var j = 0, size = memberSnExistsDataset.length; j < size; j++) {
				var tmp = memberSnExistsDataset.get(j);
				var existsSn = tmp.get('SERIAL_NUMBER');
				var TAG = tmp.get('TAG');

				if (existsSn == mebSn && TAG != "1") { // 输入号码与列表中存在的未操作的号码相同
					MessageBox.alert("提示", "号码" + mebSn + "已经存在,请重新输入！");
					isCheck = true;
					return false;
				}

			}
		}

		// 新增成员号码校验
		var param = "&USER_CLASS="+$("#USER_CLASS").val()+"&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val()+'&SERIAL_NUMBER_B_LIST=' + memberSnList;		
		$.beginPageLoading("号码校验...");
		$.ajax.submit('', 'checkAddMeb', param, '', function(rtnData) {
			$.endPageLoading();
			if (rtnData != null && rtnData.length > 0) {
				$("#TOADD_SERIAL_NUMBER").val("");
				if (rtnData.get("successList").length > 0) {
					// MessageBox.alert("提示",
					// "您将邀请副卡使用共享套餐内的流量，24小时内生效。生效后，每张副卡收取月功能费10元。");
				addMemberList(rtnData.get("successList"));
			}
			if (rtnData.get("errorList").length > 0) {
				MessageBox.alert("提示", rtnData.get("errorList"));
				var errorNumList = rtnData.get("errorNumList");
				$("#TOADD_SERIAL_NUMBER").val(errorNumList);

				// 释放添加按钮
				isCheck = true;
				// $("#bquery").attr("disabled",false);
			}
		}
	}, function(errorcode, errorinfo) {
		$.endPageLoading();
		$('#TOADD_SERIAL_NUMBER').val('');
		MessageBox.alert("提示", errorinfo);
	}	);
	}
}

/**
 * 获取已绑定成员号码列表数据
 * 
 * @returns
 */
function getMemberSnList(e) {
	var memberSnDataset = $.DatasetList();

	var obj = $("#viceInfopart .memberSnList");

	if (e == 0) { // 添加号码
		for ( var i = 0; i < obj.length; i++) {
			var idx = obj.eq(i).attr("idx");
			var memberSnData = $.DataMap();
			var TAGvalue = $("#TAG_" + idx).val();
			if (TAGvalue == '-1' || TAGvalue == '0' || TAGvalue == '1') {//未改动和新增和删除
				memberSnData.put("SERIAL_NUMBER",$("#SERIAL_NUMBER_B_T_" + idx).html());
				memberSnData.put("TAG", $("#TAG_" + idx).val());
				//memberSnData.put("DEAL_TAG", $("#DEAL_TAG_" + idx).val());
				memberSnDataset.add(memberSnData);
			}
		}
	}
	if (e == 1) { // 提交
		for ( var i = 0; i < obj.length; i++) {
			var idx = obj.eq(i).attr("idx");
			var TAG = $("#TAG_" + idx).val();
			if (TAG == "0" || TAG == "1") { // 新增0或删除1
				var memberSnData = $.DataMap();
				memberSnData.put("SERIAL_NUMBER",$("#SERIAL_NUMBER_B_T_" + idx).html());
				memberSnData.put("TAG", TAG);
				//memberSnData.put("DEAL_TAG", $("#DEAL_TAG_" + idx).val());
				memberSnDataset.add(memberSnData);
			}
		}
	}
	return memberSnDataset;
}

/**
 * 成员添加
 * 
 * @param memberList
 */
function addMemberList(memberList) {

	var TAG = "0"; // 新增标记
	var addStartDate = "立即"; // 开始时间
	var memberSnExistsDataset = $.DatasetList();
	memberSnExistsDataset = getMemberSnList(0); // 列表中已有成员
	var count = parseInt(memberSnExistsDataset.length);

	for ( var i = 0; i < memberList.length; i++) {
		var thsiIndex = "A" + count;
		var serialNumberB = memberList[i].get('SERIAL_NUMBER_B');
		var startDate = memberList[i].get('START_DATE');
		var endDate = memberList[i].get('END_DATE');
		var tempNode = "";
		tempNode += '<li id="memberSnList_' + thsiIndex
				+ '" class="memberSnList" idx="' + thsiIndex
				+ '" name="memberSnList">';
		tempNode += '	<div class="main memberSnValue e_green" id="memberSnList_' + thsiIndex + '">';
		tempNode += '		<div class="title" id="SERIAL_NUMBER_B_T_' + thsiIndex
				+ '">' + serialNumberB + '</div>';
		//tempNode += '		<div class="content" id="USER_CLASS_' + thsiIndex+ '">赠送等级：' + $("#USER_CLASS_NAME").val() + '</div>';

		/*
		 * tempNode += ' <div class="content" id="START_DATE_T_' + thsiIndex +
		 * '">开始时间：' + startDate + '</div>'; tempNode += ' <div class="content"
		 * id="END_DATE_T_' + thsiIndex + '">结束时间：' + endDate + '</div>';
		 * tempNode += ' <div class="content" id="RSRV_TAG1_T_' + thsiIndex +
		 * '">是否统付：'+'' + '</div>'; tempNode += ' <input type="hidden"
		 * name="INST_ID" id="INST_ID_'+ thsiIndex + '"/>';
		 */
/*		tempNode += '		<input type="hidden" name="DEAL_TAG" id="DEAL_TAG_' + thsiIndex + '"/>';
*/		
		tempNode += '		<input type="hidden" name="TAG" id="TAG_' + thsiIndex + '" value="0"/>'; // 新增
		tempNode += '	</div>';

		tempNode += '	<div id="delMebBt_'
				+ thsiIndex
				+ '" idx="'
				+ thsiIndex
				+ '" class="fn" onclick="delMeb(this);" tip="删除"><span class="e_ico-delete"></span></div>';
		tempNode += '</li>\r\n';

		$("#viceInfopart > ul").prepend(tempNode);

		count++;
	}
	// 成员添加html后解除按钮限制
	// $("#bquery").attr("disabled",false);
	isCheck = true;
}

/**
 * 删除成员
 * @param obj
 */
function delMeb(obj) {
	var idx = $(obj).attr("idx");
	// TAG取值说明："1-"-未做改动，"0"-新增，"1"-删除，"2"-已删除
	var modifyTag = $("#TAG_" + idx).val();
	if (modifyTag == "-1") { // 存量副卡删除，改变样式，并赋值TAG=1
		$("#SERIAL_NUMBER_B_T_" + idx).addClass("e_delete");
		//$("#USER_CLASS_" + idx).addClass("e_delete");
		
		/*$("#START_DATE_T_" + idx).addClass("e_delete");
		$("#END_DATE_T_" + idx).addClass("e_delete");*/
		$("#delMebBt_" + idx).css("display", "none");
		$("#resetMebBt_" + idx).css("display", "");
		$("#TAG_" + idx).val("1");
	} else if (modifyTag == "0") { // 新增副卡的删除，直接移除
		$(obj).parent(".memberSnList").remove();
	}
}

/**
 * 恢复
 * @param obj
 */
function resetMeb(obj) {
	var idx = $(obj).attr("idx");
	// TAG取值说明："-1"-未做改动，"0"-新增，"1"-删除，"2"-已删除
	var modifyTag = $("#TAG_" + idx).val();
	if (modifyTag == "1") { // 存量副卡点击删除按钮后又恢复，改变样式，并赋值TAG=""
		$("#SERIAL_NUMBER_B_T_" + idx).removeClass("e_delete");
		$("#USER_CLASS_" + idx).removeClass("e_delete");
		
		/*$("#START_DATE_T_" + idx).removeClass("e_delete");
		$("#END_DATE_T_" + idx).removeClass("e_delete");*/
		$("#delMebBt_" + idx).css("display", "");
		$("#resetMebBt_" + idx).css("display", "none");
		$("#TAG_" + idx).val("-1");
	}
}
