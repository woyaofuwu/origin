var memberIndex = 9; // 由于界面互联网，将成员号码列表改成list，故每个成员号码的信息的key都需要带下标，以区分。统一付费的成员号码最大限制默认为9，故新增成员号码下标默认为9（下标从0开始）。

function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	var userInfo = data.get("USER_INFO");
	var params = "&USER_ID=" + userInfo.get("USER_ID") + "&SERIAL_NUMBER="
			+ userInfo.get("SERIAL_NUMBER") + "&USER_STATE_CODESET="
			+ userInfo.get("USER_STATE_CODESET");
	ajaxSubmit('', 'loadChildInfo', params, 'EditPart,memberSnPart,curMemberCountPart,HiddenPart', function() {
				if ($('#MEB_LIM').val() != undefined && $('#MEB_LIM').val() != "") { // 若统一付费的成员号码最大限制数配置不是空，则取配置的限制数值
					memberIndex = $('#MEB_LIM').val();
				}
				dealBtDisplay();
				$("#EditPart").removeClass("e_dis");
				$("#EditPart").attr("disabled", false);
				$("#bcreate").attr("disabled", false);
				$("#bdelete").attr("disabled", false);
				$.endPageLoading();
			}, function(code, info, detail) {
				$.endPageLoading();
				$.cssubmit.disabledSubmitBtn(true);
				MessageBox.error("错误提示", info, function(btn) {
				}, null, detail);
			}, function() {
				$.endPageLoading();
				$.cssubmit.disabledSubmitBtn(true);
				MessageBox.alert("告警提示", "查询超时!", function(btn) {
				});
			});
}

function dealBtDisplay() {
	var obj = $("#memberSnPart .memberSnList");

	var lastTimeThisMonthValue = $('#END_DATE_THIS_ACCT').val();
	for ( var i = 0; i < obj.length; i++) {
		var idx = obj.eq(i).attr("idx");
		var endDate = $("#END_DATE_" + idx).val();
		if (endDate == lastTimeThisMonthValue) {
			$("#pwdValidBt_" + idx).css("display", "none");
			$("#sucMebBt_" + idx).css("display", "none");
			$("#delMebBt_" + idx).css("display", "none");
			$("#resetMebBt_" + idx).css("display", "none");
			$("#lockMebBt_" + idx).css("display", "");
		}
	}
}

/**
 * 修改成员
 * @param memberSn
 * @returns
 */
function modifyMember(memberSn) {
	var commEndDate = $("#COMM_END_DATE").val();
	
	var obj = $("#memberSnPart .memberSnList");
	
	var lastTimeThisMonthValue = $('#END_DATE_THIS_ACCT').val();

	for ( var k = 0; k < memberSn.length; k++) {
		var mebSn = memberSn[k];
		for ( var i = 0; i < obj.length; i++) {
			var idx = obj.eq(i).attr("idx");
			var serialNumberB = $("#SERIAL_NUMBER_B_" + idx).val();
			if (serialNumberB == mebSn) {
				var endDate = $("#END_DATE_" + idx).val()
				if (endDate == lastTimeThisMonthValue) { // 如果新添加的号码已经在列表中，且UU关系的END_DATE与下个结账日在同一天，则修改该成员统一付费关系的截止时间至commEndDate（2050-12-31 23:59:59）。
					$("#END_DATE_T_" + idx).html(commEndDate);
					$("#END_DATE_" + idx).val(commEndDate);
					$("#MODIFY_TAG_" + idx).val("2");

					$("#pwdValidBt_" + idx).css("display", "");
					$("#sucMebBt_" + idx).css("display", "none");
					$("#delMebBt_" + idx).css("display", "none");
					$("#resetMebBt_" + idx).css("display", "");
					$("#lockMebBt_" + idx).css("display", "none");
				}

				memberSn.splice(k, 1);
				k--;
				break;
			}
		}
	}
	return memberSn;
}

/**
 * 添加成员list
 * @param memberSerialNumber
 */
function addMember(memberSerialNumber) {
	var fmyCurCount = $('#FMY_CUR_COUNT').html();

	var startDate = $("#COMM_START_DATE").val();
	var endDate = $("#COMM_END_DATE").val();
	var modifyTag = "0";

	for ( var i = 0; i < memberSerialNumber.length; i++) {
		var serialNumberB = memberSerialNumber[i];
		var tempNode = "";
		tempNode +=  '<li id="memberSnList_' + memberIndex + '" class="memberSnList" idx="'+ memberIndex + '" name="memberSnList">';
		tempNode +=  '	<div class="main memberSnValue e_green" id="memberSnList_' + memberIndex + '">';
		tempNode += '		<div class="title" id="SERIAL_NUMBER_B_T_' + memberIndex + '">' + serialNumberB + '</div>';
		tempNode += '		<div class="content" id="START_DATE_T_' + memberIndex + '">开始时间：' + startDate + '</div>';
		tempNode += '		<div class="content" id="END_DATE_T_' + memberIndex + '">结束时间：' + endDate + '</div>';

		tempNode += '		<input type="hidden" name="ORG_END_DATE" id="ORG_END_DATE_'+ memberIndex + '"/>';
		tempNode += '		<input type="hidden" name="SERIAL_NUMBER_B" id="SERIAL_NUMBER_B_'+ memberIndex + '" value="'+ serialNumberB + '" datatype="mbphone" maxLength="11" desc="副号码" nullable="no"/>';
		tempNode += '		<input type="hidden" name="START_DATE" id="START_DATE_'+ memberIndex + '" value="'+ startDate + '"/>';
		tempNode += '		<input type="hidden" name="END_DATE" id="END_DATE_'+ memberIndex + '" value="'+ endDate + '"/>';
		tempNode += '		<input type="hidden" name="RSRV_TAG1" id="RSRV_TAG1_'+ memberIndex + '"/>';
		tempNode += '		<input type="hidden" name="MODIFY_TAG" id="MODIFY_TAG_'+ memberIndex + '" value="'+ modifyTag + '"/>';
		tempNode += '		<input type="hidden" name="SELF_START_DATE" id="SELF_START_DATE_'+ memberIndex + '" value=""/>';
		tempNode += '		<input type="hidden" name="NOW_ACCT_DAY" id="NOW_ACCT_DAY_'+ memberIndex + '" value=""/>';
		tempNode += '		<input type="hidden" name="checkTag" id="checkTag_'+ memberIndex + '"/>';
		tempNode +=  '	</div>';
		
		tempNode +=  '	<input type="hidden" name="POP_SERIAL_NUMBER_B_'+ memberIndex +'" id="POP_SERIAL_NUMBER_B_'+ memberIndex+'" checktag="11000" islocal="false" cachesn="false" isauth="true" tradeaction="checkMember('+ "'" + memberIndex + "'" + ')" bindevent="true" desc=""/>';

		tempNode +=  '	<div id="pwdValidBt_'+ memberIndex +'" idx="' + memberIndex + '" class="fn" onclick="pwdValidate(this);" tip="校验"><span class="e_ico-check"></span></div>';
		tempNode +=  '	<div id="sucMebBt_'+ memberIndex +'" idx="' + memberIndex + '" class="fn" onclick="sucMeb(this);" style="display:none" tip="校验成功"><span class="e_ico-ok"></span></div>';
		tempNode +=  '	<div id="delMebBt_'+ memberIndex +'" idx="' + memberIndex + '" class="fn" onclick="delMeb(this);" tip="删除"><span class="e_ico-delete"></span></div>';
		tempNode +=  '</li>\r\n';
		
		$("#memberSnPart > ul").prepend(tempNode);

		var fmyCurCount = parseFloat(fmyCurCount) + 1;
		$("#FMY_CUR_COUNT").html(fmyCurCount);
		memberIndex++;
	}
}

/**
 * 获取已绑定成员号码列表数据
 * @returns
 */
function getMemberSnList() {
	var memberSnDataset = $.DatasetList();

	var obj = $("#memberSnPart .memberSnList");
	
	for(var i = 0; i < obj.length; i++) {
		var idx = obj.eq(i).attr("idx");
		var memberSnData = $.DataMap();
		memberSnData.put("SERIAL_NUMBER_B", $("#SERIAL_NUMBER_B_" + idx).val());
		memberSnData.put("ORG_END_DATE", $("#ORG_END_DATE_" + idx).val());
		memberSnData.put("START_DATE", $("#START_DATE_" + idx).val());
		memberSnData.put("END_DATE", $("#END_DATE_" + idx).val());
		memberSnData.put("RSRV_TAG1", $("#RSRV_TAG1_" + idx).val());
		memberSnData.put("MODIFY_TAG", $("#MODIFY_TAG_" + idx).val());
		memberSnData.put("SELF_START_DATE", $("#SELF_START_DATE_" + idx).val());
		memberSnData.put("NOW_ACCT_DAY", $("#NOW_ACCT_DAY_" + idx).val());
		memberSnData.put("checkTag", $("#checkTag_" + idx).val());
		
		memberSnDataset.add(memberSnData);
	}
	return memberSnDataset;
}

/**
 * 密码校验
 * @param obj
 */
function pwdValidate(obj) {
	var idx = $(obj).attr("idx");
	if ("1" == $("#checkTag_" + idx).val()) {
		MessageBox.success("提示信息", "已通过校验,无须重复校验!");
		return;
	}
	var fieldId = "SERIAL_NUMBER_B_" + idx;

    var snObj = $("#" + fieldId);
    snObj.val($.trim(snObj.val()));
    if (snObj && snObj != "") {
        ajaxSubmit('', 'checkLeaderSerialNumberAndPermission', "&SERIAL_NUMBER=" + snObj.val(), '', function (data) {
            var resultCode = data.get(0).get("X_RESULTCODE");
            if (resultCode == '-1') {
                $.userCheck.checkUser(fieldId);
            } else {
                checkMember(idx);
            }
        }, function (code, info, detail) {
            $.endPageLoading();
            MessageBox.error("错误提示", info);
        }, function () {
            $.endPageLoading();
            MessageBox.alert("告警提示", "查询超时!");
        });
    } else {
        MessageBox.alert("告警提示", "请输入成员号码!");
    }
}

/**
 * 校验成功
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
 * 删除成员
 * @param obj
 */
function delMeb(obj) {
	var idx = $(obj).attr("idx");
	// modifyTag取值说明：""-未做改动，"0"-新增，"1"-删除，"2"-修改
	var modifyTag = $("#MODIFY_TAG_"+idx).val();
	var lastTimeThisMonthValue = $('#END_DATE_THIS_ACCT').val();
	if (modifyTag == "" || modifyTag == undefined) { // 存量副卡删除，改变样式，并赋值tag=1
		$("#SERIAL_NUMBER_B_T_" + idx).addClass("e_delete");
		$("#delMebBt_" + idx).css("display", "none");
		$("#resetMebBt_" + idx).css("display", "");
		$("#MODIFY_TAG_" + idx).val("1");
		$("#END_DATE_T_" + idx).html(lastTimeThisMonthValue);
		$("#END_DATE_" + idx).val(lastTimeThisMonthValue);
	} else if (modifyTag == "0") { // 新增副卡的删除，直接移除
		$(obj).parent(".memberSnList").remove();
		var fmyCurCount=$('#FMY_CUR_COUNT').html();
		var toFmyCurCount = parseFloat(fmyCurCount) - 1;
		$('#FMY_CUR_COUNT').html(toFmyCurCount);
	}
}

/**
 * 恢复
 * @param obj
 */
function resetMeb(obj) {
	var idx = $(obj).attr("idx");
	// tag取值说明：""-未做改动，"0"-新增，"1"-删除，"2"-修改
	var orgEndDate = $("#ORG_END_DATE_" + idx).val();
	var lastTimeThisMonthValue = $('#END_DATE_THIS_ACCT').val();
	var modifyTag = $("#MODIFY_TAG_" + idx).val();
	if (modifyTag == "1") { // 存量副卡点击删除按钮后又恢复，改变样式，并赋值tag=""
		$("#SERIAL_NUMBER_B_T_" + idx).removeClass("e_delete");
		$("#delMebBt_" + idx).css("display", "");
		$("#resetMebBt_" + idx).css("display", "none");
		$("#MODIFY_TAG_" + idx).val("");
		$("#checkTag_" + idx).val("");
		$("#END_DATE_T_" + idx).html(orgEndDate);
		$("#END_DATE_" + idx).val(orgEndDate);
	} else if (modifyTag == "2") { // 恢复修改成员数据
		$("#delMebBt_" + idx).css("display", "none");
		$("#resetMebBt_" + idx).css("display", "none");
		$("#pwdValidBt_" + idx).css("display", "none");
		$("#sucMebBt_" + idx).css("display", "none");
		$("#lockMebBt_" + idx).css("display", "");
		$("#MODIFY_TAG_" + idx).val("");
		$("#checkTag_" + idx).val("");
		$("#END_DATE_T_" + idx).html(lastTimeThisMonthValue);
		$("#END_DATE_" + idx).val(lastTimeThisMonthValue);
	}
}

/**
 * 锁定成员
 * @param obj
 */
function lockMeb(obj) {
	var idx = $(obj).attr("idx");
	var lastTimeThisMonthValue = $('#END_DATE_THIS_ACCT').val();
	var endDate = $("#END_DATE_" + idx).val();
	if (endDate == lastTimeThisMonthValue) {
		MessageBox.success("提示信息", "此成员结束时间为["+lastTimeThisMonthValue+"]，不能再删除!");
		return;
	}
}

/**
 * 校验成员
 * @param idx
 */
function checkMember(idx) {
	if ("2" == $("#MODIFY_TAG_" + idx).val()) {
		$("#checkTag_" + idx).val("1"); // 校验成功赋值
		$("#pwdValidBt_" + idx).css("display", "none");
		$("#sucMebBt_" + idx).css("display", "");
		return; // 修改的成员不需要走下面新增成员的校验流程
	}
	$.beginPageLoading("正在校验副号码...");
	var snb = $("#SERIAL_NUMBER_B_" + idx).val();
	ajaxSubmit('', 'checkBySerialNumber', "&CHECK_SERIAL_NUMBER=" + snb + "&MAIN_SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val(), '',
			function(data) {
				$.endPageLoading();
				var msg = data.get("MSG");
				var code = data.get("CODE");

				if (code != 0 && code != 3) {
					MessageBox.alert(msg);
					return;
				} else if (code == 3) {
					MessageBox.confirm("确认提示", "[" + snb + "]已经办理集团统付业务，在统一付费业务生效期间，您的费用将由主号码支付，原有的集团付费业务将暂时失效。您是否继续办理？",function(btn) {
						if (btn == "ok") {
							checkMemberSuc(data, idx);
						} else if (btn == "cancel") {
							return false;
						}
					});
				} else {
					checkMemberSuc(data, idx);
				}
			}, function(code, info, detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", info);
			}, function() {
				$.endPageLoading();
				MessageBox.alert("告警提示", "查询超时!");
			});
}

/**
 * 校验成功
 * @param data
 * @param idx
 */
function checkMemberSuc(data,idx) {
	$("#checkTag_" + idx).val("1"); // 校验成功赋值
	
	$("#pwdValidBt_" + idx).css("display", "none");
	$("#sucMebBt_" + idx).css("display", "");
	
	var selfStartDate = data.get("SELF_START_DATE");
	var acctDay = data.get("ACCT_DAY");

	$("#SELF_START_DATE_" + idx).val(selfStartDate);
	$("#NOW_ACCT_DAY_" + idx).val(acctDay);
}

/**
 * 提交
 * @returns {Boolean}
 */
function submitTrade() {
	var warnInfo = "";
	var startDate = "";
	var unCheckedMember = "";
	var memberSnDataset = $.DatasetList();
	memberSnDataset = getMemberSnList();

	for ( var i = 0; i < memberSnDataset.length; i++) {
		var memberSnData = memberSnDataset.get(i);
		var modifyTag = memberSnDataset.get(i).get('MODIFY_TAG');
		var checkTag = memberSnDataset.get(i).get('checkTag');
		var serialNumberB = memberSnDataset.get(i).get('SERIAL_NUMBER_B');
		if (modifyTag == "" || modifyTag == undefined) { // 未进行修改的存量成员号码数据不提交到后台
			memberSnDataset.remove(memberSnData);
			i--;
		} else if ((modifyTag == "0" || modifyTag == "2") && checkTag != "1") { // 未通过校验的新增+未通过校验的修改成员号码数据不提交到后台
			unCheckedMember = unCheckedMember + serialNumberB + ","; // 未通过校验的新增或者修改成员号码
			memberSnDataset.remove(memberSnData);
			i--;
		} else {
			var targetAcctDay = memberSnDataset.get(i).get('NOW_ACCT_DAY');
			var selfStartDate = memberSnDataset.get(i).get('SELF_START_DATE');
			// 如果副号码不为1号
			if ("1" != targetAcctDay) {
				warnInfo += "号码" + serialNumberB + "的结账日为: " + targetAcctDay + "号；"
			}
			if (startDate < selfStartDate) {
				startDate = selfStartDate;
			}
		}
	}

	if ("1" != $("#MAIN_ACCT_DAY").val()) {
		warnInfo += "号码" + $("#AUTH_SERIAL_NUMBER").val() + "的结账日为: " + $("#MAIN_ACCT_DAY").val() + "号；";
	}
	if (warnInfo != "") {
		warnInfo += "办理统一付费业务受理后结账日将变为1号，" + startDate + "立即生效!";
	}
	
	var param = "&MEMBER_DATAS=" + memberSnDataset.toString();
	
	if (memberSnDataset.length <= 0) {
		if (unCheckedMember != "") {
			unCheckedMember = unCheckedMember.substring(0, unCheckedMember.length - 1);
			MessageBox.alert("信息提示", "新增成员[" + unCheckedMember + "]未通过校验，无有效提交数据！");
		} else {
			MessageBox.alert("信息提示", "您未做任何操作，不允许提交订单！");
		}
		return false;
	} else if (unCheckedMember != "") { // 如果界面存在未校验数据，提示用户是否提交
		unCheckedMember = unCheckedMember.substring(0, unCheckedMember.length - 1);
		MessageBox.confirm("确认提示", "新增成员[" + unCheckedMember + "]未通过校验，本次受理不会加入统一付费关系！是否继续提交？", function(btn) {
			if (btn == "ok") {
				MessageBox.alert("提示信息", warnInfo, function() {
					$.cssubmit.addParam(param);
					$.cssubmit.submitTrade();
					return;
				});
			} else if (btn == "cancel") {
				return false;
			}
		});
	} else { // 提交
		if (warnInfo != "") {
			MessageBox.alert("提示信息", warnInfo, function(){
				$.cssubmit.addParam(param);
				$.cssubmit.submitTrade();
				return;
			});
		} else {
			$.cssubmit.addParam(param);
			return true;
		}
	}
}