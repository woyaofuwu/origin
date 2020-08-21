var valideMemberNumber = 0; // 当前生效成员数
var useableMemberNumber = 0; // 可以添加成员数
var viceLisIndex = 10; // 由于界面互联网，将成员号码列表改成list，故每个成员号码的信息的key都需要带下标，以区分。亲亲网的成员号码最大限制默认为9，为避免下标重复，故新增成员号码下标默认为大于等于9的值（下标从0开始）。
var useableShortCodes = $.DatasetList();

function refreshPartAtferAuth(data) {
    $("#TRADE_OPTION").val('NULL');
    disabledArea("batAddVicePart", false);
    disabledArea("viceInfoPart", false);
    var userId = data.get('USER_INFO').get('USER_ID');
    var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
    var param = '&USER_ID=' + userId;
    param += '&SERIAL_NUMBER=' + serialNumber;
    param += '&EPARCHY_CODE=' + data.get('USER_INFO').get('EPARCHY_CODE');
    param += '&USER_PRODUCT_ID=' + data.get('USER_INFO').get('PRODUCT_ID');
    // $.beginPageLoading();
    ajaxSubmit(null, 'loadInfo', param, 'viceInfoPart,batAddVicePart,viceInfoPartTitle,batAddVicePartTitle,HiddenPart', function (data) {
        // $.endPageLoading();
    	
    	if(data.get("RESULT_CODE") == "2998"){
    		MessageBox.error("错误提示", data.get("RESULT_INFO"));
    		return false;
    	}
    	
        var authMainFlag = data.get("AUTH_MAIN_FLAG");
        $("#AUTH_MAIN_FLAG").val(authMainFlag);
        
        valideMemberNumber = data.get("VALIDE_MEBMER_NUMBER");
        useableMemberNumber = data.get("USEABLE_MEMBER_NUMBER");
        useableShortCodes = data.get("USEABLE_SHORT_CODES");
        
        var virtualNum = data.get("VRITUAL_NUM"); //判断主副号标识
        if (virtualNum == '2') { // 如果是副号则隐藏新增成员功能
            $("#batAddVicePartTitle").css('display', 'none'); 
            $("#batAddVicePart").css('display', 'none'); 
            $("#viceInfoPartTitle").css('display', 'none'); 
        }else{
        	$("#batAddVicePartTitle").css('display', 'block'); 
            $("#batAddVicePart").css('display', 'block'); 
            $("#viceInfoPartTitle").css('display', 'block'); 
        }

        $("#VICE_SERIAL_NUMBER").focus();
    }, function (code, info, detail) {
        // $.endPageLoading();
        $.cssubmit.disabledSubmitBtn(true);
        MessageBox.error("错误提示", info);
    }, function () {
        // $.endPageLoading();
        $.cssubmit.disabledSubmitBtn(true);
        MessageBox.alert("告警提示", "查询超时!", function (btn) {
        });
    });
}


/**
 * 成员号码新增
 * @returns {Boolean}
 */
function addMeb() {

    var mainSn = $("#AUTH_SERIAL_NUMBER").val();

    var viceSerialNumber = $("#VICE_SERIAL_NUMBER").val(); // 新增成员号码

    if (viceSerialNumber.length <= 0) {
        $.TipBox.show(document.getElementById("VICE_SERIAL_NUMBER"), "请输入新增副卡号码", "red");
        return false;
    }

    viceSerialNumber = viceSerialNumber.trim().split(" ");

    if (viceSerialNumber == "") {
        $.TipBox.show(document.getElementById("VICE_SERIAL_NUMBER"), "请输入新增副卡号码", "red");
        return false;
    }
    // 删除新增副卡数组中的空字符
    for (var i = 0; i < viceSerialNumber.length; i++) {
        var sn = viceSerialNumber[i];
        if (sn == "") {
            viceSerialNumber.splice(i, 1);
            i--;
        }
    }
    if (viceSerialNumber.length > useableMemberNumber) {
        MessageBox.alert("告警提示", "新增副卡号码数超出限制，当前可以添加数(" + useableMemberNumber + ")，本次删除已绑定成员空闲出的位置需要提交后才能生效！");
        return false;
    }

    var viceInfoDataset = $.DatasetList();
    viceInfoDataset = getViceList();

    for (var i = 0; i < viceSerialNumber.length; i++) {
        var mebSn = viceSerialNumber[i];
        // 校验批量输入的号码，不能重复
        for (var k = i + 1; k < viceSerialNumber.length; k++) {
            var tempMebSn = viceSerialNumber[k];
            if (mebSn == tempMebSn) {
                $.TipBox.show(document.getElementById("VICE_SERIAL_NUMBER"), "对不起，您输入的手机号码[" + mebSn + "]重复，请勿重复输入！", "red");
                return false;
            }
        }
        // 校验手机号码格式
        if (!isTel(mebSn)) {
            $.TipBox.show(document.getElementById("VICE_SERIAL_NUMBER"), "对不起，您输入的手机号码[" + mebSn + "]格式不正确，请重新输入！", "red");
            return false;
        }
        //不能与主号一致
        if (mebSn == mainSn) {
            $.TipBox.show(document.getElementById("VICE_SERIAL_NUMBER"), "对不起，成员号码不能和主卡号码一样，请重新输入！", "red");
            return false;
        }

        for (var j = 0, size = viceInfoDataset.length; j < size; j++) {
            var tmp = viceInfoDataset.get(j);
            var sn = tmp.get('SERIAL_NUMBER_B');
            var tag = tmp.get('tag');
            if (mebSn == sn) {
                $.TipBox.show(document.getElementById("VICE_SERIAL_NUMBER"), "号码" + mebSn + "已经在成员列表", "red");
                return false;
            }
        }
    }

    addViceList(viceSerialNumber);
    $("#VICE_SERIAL_NUMBER").val("");
    $("#FMY_VERIFY_MODE").attr("disabled", true);// 禁用副号验证方式
}

/**
 * 成员号码删除
 * @param obj
 */
function delMeb(obj) {
    // 按钮的操作校验
    var tradeOption = $("#TRADE_OPTION").val();

    var idx = $(obj).attr("idx");
    
    // tag取值说明：""-未做改动，"0"-新增，"1"-删除
    var tag = $("#tag_" + idx).val();
    
    if (tag == "" || tag == undefined) { // 存量副卡删除，改变样式，并赋值tag=1

    	$("#SERIAL_NUMBER_B_T_" + idx).addClass("e_delete");
        var sysDateT = (new Date()).format('yyyy-MM-dd');
        var sysDate = (new Date()).format('yyyy-MM-dd HH:mm:ss');
        $("#END_DATE_T_" + idx).html(sysDateT);
        $("#END_DATE_" + idx).val(sysDate);
        $("#tag_" + idx).val("1");

        $("#delMebBt_" + idx).css("display", "none");
        $("#resetMebBt_" + idx).css("display", "");
        //$("#NEW_SHORT_CODE_" + idx).attr("disabled", true);
    } else if (tag == "0") { // 新增副卡的删除，直接移除
        $(obj).parent(".viceList").remove();
        $("#tag_" + idx).val("");
        useableMemberNumber++;
        $("#USEABLE_MEMBER_NUMBER").html(useableMemberNumber);
        $("#USED_MEMBER_NUMBER").html(3 - useableMemberNumber);
    }

    // 如果所有新增的成员号码都被删除，则解除对副号验证方式的禁用
    $("#FMY_VERIFY_MODE").attr("disabled", false);// 解除对副号验证方式的禁用
    var viceInfoDataset = getViceList();
    for (var i = 0; i < viceInfoDataset.length; i++) {
        var viceInfoData = viceInfoDataset.get(i);
        var tag = viceInfoDataset.get(i).get('tag');
        if (tag == "0") {
            $("#FMY_VERIFY_MODE").attr("disabled", true);// 如果已绑定成员号码列表中存在新增副卡，则禁用副号验证方式
            break;
        }
    }
}

/**
 * 成员号码删除后重置
 * @param obj
 */
function resetMeb(obj) {
    var idx = $(obj).attr("idx");
    // tag取值说明：""-未做改动，"0"-新增，"1"-删除
    var tag = $("#tag_" + idx).val();
    if (tag == "1") { // 存量副卡点击删除按钮后又恢复，改变样式，并赋值tag=""
        $("#SERIAL_NUMBER_B_T_" + idx).removeClass("e_delete");
        $("#delMebBt_" + idx).css("display", "");
        $("#resetMebBt_" + idx).css("display", "none");
        if ($("#AUTH_MAIN_FLAG").val() != 'false') { 
            $("#NEW_SHORT_CODE_" + idx).attr("disabled", false);
        }
        $("#tag_" + idx).val("");

        var orgEndDate = $("#ORG_END_DATE_" + idx).val();
        var orgEndDateT = orgEndDate.substring(0, 10);

        $("#END_DATE_T_" + idx).html(orgEndDateT);
        $("#END_DATE_" + idx).val(orgEndDate);
    }
}


/**
 * 成员号码新增js生成
 * @param viceSerialNumber
 */
function addViceList(viceSerialNumber) {
    var startDate = new Date();

    var verifyModeList = $.DatasetList();
    
    ajaxSubmit(null, 'getVerifyModeList', null, null, function (data) {
        verifyModeList = data.get("VERIFY_MODE_LIST");
        for (var i = 0; i < viceSerialNumber.length; i++) {
            var serialNumberB = viceSerialNumber[i];
            
            var tempNode = "";
            tempNode += '<li class="viceList" id="viceList_' + viceLisIndex + '" name="viceList" idx="' + viceLisIndex + '">';
            tempNode += '	<div id="viceValue_' + viceLisIndex + '" class="main">';
            tempNode += '		<div class="title"><span class="e_orange" id="SERIAL_NUMBER_B_T_' + viceLisIndex + '">' + serialNumberB + '</span></div>';
            tempNode += '		<div class="content">';
            for (var j = 0; j < verifyModeList.length; j++) {
                if ('0' == verifyModeList.get(j).get("DATA_ID")) {
                    tempNode += '			<a href="#nogo" ontap="pwdValidate(this)" name="pwdValidBt" id="pwdValidBt_' + viceLisIndex + '" idx="' + viceLisIndex + '">[密码]</a>';
                }
                if ('1' == verifyModeList.get(j).get("DATA_ID")) {
                    tempNode += '			<a href="#nogo" ontap="smsValidate(this)" name="smsValidBt" id="smsValidBt_' + viceLisIndex + '" idx="' + viceLisIndex + '">[短信]</a>';
                }
                if ('2' == verifyModeList.get(j).get("DATA_ID")) {
                    tempNode += '			<a href="#nogo" ontap="noValidate(this)" name="noValidBt" id="noValidBt_' + viceLisIndex + '" idx="' + viceLisIndex + '">[免密码]</a>';
                }
            }
            tempNode += '		</div>';

            tempNode += '		<input type="hidden" name="SERIAL_NUMBER_B" id="SERIAL_NUMBER_B_' + viceLisIndex + '" value="' + serialNumberB + '" datatype="mbphone" maxLength="11" value="" desc="副号码" nullable="no"/>';
            tempNode += '		<input type="hidden" name="START_DATE" id="START_DATE_' + viceLisIndex + '" value="' + startDate.format("yyyy-MM-dd HH:mm:ss") + '"/>';
            tempNode += '		<input type="hidden" name="END_DATE" id="END_DATE_' + viceLisIndex + '" value="2050-12-31 23:59:59"/>';
            tempNode += '		<input type="hidden" name="ORG_END_DATE" id="ORG_END_DATE_' + viceLisIndex + '"/>';
            tempNode += '		<input type="hidden" name="MEB_VERIFY_MODE" id="MEB_VERIFY_MODE_' + viceLisIndex + '"/>';
            tempNode += '		<input type="hidden" name="tag" id="tag_' + viceLisIndex + '" value="0"/>';
            tempNode += '		<input type="hidden" name="checkTag" id="checkTag_' + viceLisIndex + '" value=""/>';
            tempNode += '	</div>';

            tempNode += '	<input type="hidden" name="POP_SERIAL_NUMBER_B_' + viceLisIndex + '" id="POP_SERIAL_NUMBER_B_' + viceLisIndex + '"  islocal="false" cachesn="false" isauth="true" checktag="01000" exceptaction="exceptAction(state, data)" tradeaction="mebCheck(' + "'pwdValidBt'" + "," + "'" + viceLisIndex + "'" + ')" bindevent="false" desc=""/>';
            tempNode += '	<div name="delMebBt" id="delMebBt_' + viceLisIndex + '" idx="' + viceLisIndex + '" class="fn" onclick="delMeb(this);" tip="删除"><span class="e_ico-delete"></span></div>';
            tempNode += '</li>\r\n';
            
            
            $("#viceInfoPart > ul").prepend(tempNode);


            $.Select.append("viceShortCode_" + viceLisIndex, {
                id: "NEW_SHORT_CODE_" + viceLisIndex,
                name: "NEW_SHORT_CODE",
                addDefault: false,
                textField: "DATA_NAME",
                valueField: "DATA_ID",
                className: "e_select e_select-r e_select-s",
                optionWidth: "6"
            }, useableShortCodes);

            //$("#NEW_SHORT_CODE_" + viceLisIndex).attr("idx", viceLisIndex);
            
            useableMemberNumber--;
            $("#USEABLE_MEMBER_NUMBER").html(useableMemberNumber);
            $("#USED_MEMBER_NUMBER").html(2 - useableMemberNumber);
            viceLisIndex++;
        }
        $.endPageLoading();
    }, function (code, info, detail) {
        $.endPageLoading();
        MessageBox.error("错误提示", info);
    }, function () {
        $.endPageLoading();
        MessageBox.alert("告警提示", "查询超时!", function (btn) {
        });
    });
}


/**
 * 校验手机号码格式
 * @param str
 * @returns
 */
function isTel(str) {
    var reg = /^([0-9]|[\-])+$/g;
    if (str.length !== 11) {
        return false;
    } else {
        return reg.exec(str);
    }
}

/**
 * 获取页面上所有成员号码数据（包括未做修改的存量成员-tag=""）
 * @returns
 */
function getViceList() {
    var obj = $("#viceInfoPart .viceList");
    var viceInfoDataset = $.DatasetList();
    
    for (var i = 0; i < obj.length; i++) {
        var idx = obj.eq(i).attr("idx");
        
        var viceInfoData = $.DataMap();
        viceInfoData.put("SERIAL_NUMBER_B", $("#SERIAL_NUMBER_B_" + idx).val());
        viceInfoData.put("START_DATE", $("#START_DATE_" + idx).val());
        viceInfoData.put("END_DATE", $("#END_DATE_" + idx).val());
        viceInfoData.put("MEB_VERIFY_MODE", $("#MEB_VERIFY_MODE_" + idx).val());
        viceInfoData.put("tag", $("#tag_" + idx).val());
        viceInfoData.put("checkTag", $("#checkTag_" + idx).val());
        
        viceInfoDataset.add(viceInfoData);
    }
    return viceInfoDataset;
}

/**
 * 密码校验
 * @param obj
 */
function pwdValidate(obj) {
    var idx = $(obj).attr("idx");
    var fieldId = "SERIAL_NUMBER_B_" + idx;
    if ("1" == $("#checkTag_" + idx).val()) {
    	MessageBox.confirm("确认提示", "已通过校验，是否重新校验？", function (btn) {
            if (btn == "ok") {
            	$("#CHECK_ADD_MEB").val("3");
                resetMemCheckResult(idx);
                $.userCheck.checkUser(fieldId);
            }
        });
        return;
    }
    $("#CHECK_ADD_MEB").val("3");
    $.userCheck.checkUser(fieldId);
}

/**
 * 短信校验
 * @param obj
 */
function smsValidate(obj) {
    var smsBtn = $(obj).attr("name");
    var idx = $(obj).attr("idx");
    if ("1" == $("#checkTag_" + idx).val()) {
    	MessageBox.confirm("确认提示", "已通过校验，是否重新校验？", function (btn) {
            if (btn == "ok") {
            	$("#CHECK_ADD_MEB").val("1");
                mebCheck(smsBtn, idx);
            }
        });
        return;
    }
    $("#CHECK_ADD_MEB").val("1");
    
    mebCheck(smsBtn, idx);
}

/**
 * 免密码校验
 * @param obj
 */
function noValidate(obj) {
    var noBtn = $(obj).attr("name");
    var idx = $(obj).attr("idx");
    if ("1" == $("#checkTag_" + idx).val()) {
    	MessageBox.confirm("确认提示", "已通过校验，是否重新校验？", function (btn) {
            if (btn == "ok") {
            	$("#CHECK_ADD_MEB").val("2");
                mebCheck(noBtn, idx);
                return;
            }
        });
        return;
    }
    $("#CHECK_ADD_MEB").val("2");
    
    mebCheck(noBtn, idx);
}

/**
 * 副号码校验
 * @param idx
 */
function mebCheck(btn, idx) {

    var serialNumberB = $("#SERIAL_NUMBER_B_" + idx).val();

    var param = "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val() + '&SERIAL_NUMBER_B=' + serialNumberB;

    $.beginPageLoading("副号码校验...");
    resetMemCheckResult(idx);
    ajaxSubmit(null, 'checkAddMeb', param, null, function (data) {
    	
    	//alert(data.get("RESULT_CODE")+"---------------"+data.get("RESULT_INFO"));
    	if(data.get("RESULT_CODE") == "2998"){
    		$.endPageLoading();
    		MessageBox.error("错误提示", data.get("RESULT_INFO"));
    		return false;
    	}
    	
    	$("#checkTag_" + idx).val("1"); // 校验成功赋值
    	
	    if ('pwdValidBt' == btn) {
	        $("#MEB_VERIFY_MODE_" + idx).val("0"); // 副卡校验方式
	    }
	    if ('smsValidBt' == btn) {
	        $("#MEB_VERIFY_MODE_" + idx).val("1"); // 副卡校验方式
	    }
	    if ('noValidBt' == btn) {
	        $("#MEB_VERIFY_MODE_" + idx).val("2"); // 副卡校验方式
	    }
	
	    if (document.getElementById("SERIAL_NUMBER_B_T_" + idx)) {
	        $("#SERIAL_NUMBER_B_T_" + idx).attr("class", "e_green");
	    }
	
	    if (document.getElementById(btn + "_" + idx)) {
	        $("#" + btn + "_" + idx).attr("class", "e_green");
	    }
	    
        $.endPageLoading();
    }, function (code, info, detail) {
        $.endPageLoading();
        MessageBox.error("错误提示", info);
    }, function () {
        $.endPageLoading();
        MessageBox.alert("告警提示", "校验超时!", function (btn) {
        });
    });
}

function exceptAction(state, data) {
    if (state == "USER_KEY_NULL") {
        MessageBox.alert("信息提示", "该用户没有设置密码！");
        return false;
    }
    return true;
}

/**
 * 成员号码新增校验，重置
 * @param idx
 */
function resetMemCheckResult(idx) {
    $("#checkTag_" + idx).val(""); // 默认校验失败赋值
    $("#MEB_VERIFY_MODE_" + idx).val(""); // 副卡校验方式

    if (document.getElementById("SERIAL_NUMBER_B_T_" + idx)) {
        $("#SERIAL_NUMBER_B_T_" + idx).attr("class", "e_orange");
    }

    if (document.getElementById("pwdValidBt_" + idx)) {
        $("#pwdValidBt_" + idx).attr("class", "");
    }

    if (document.getElementById("smsValidBt_" + idx)) {
        $("#smsValidBt_" + idx).attr("class", "");
    }

    if (document.getElementById("noValidBt_" + idx)) {
        $("#noValidBt_" + idx).attr("class", "");
    }
}


//业务提交
function onTradeSubmit() {
	
    var param = '&SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();

    var unCheckedMember = "";

    var checkMeb =$("#CHECK_ADD_MEB").val(); 
	if(checkMeb == "1"){ // 1表示短信认证
		param += "&CHECK_ADD_MEB=" + checkMeb;
	}
	
	if(checkMeb == "2"){ // 2表示免密认证
		param += "&CHECK_ADD_MEB=" + checkMeb;
	}
	
	if(checkMeb == "3"){ // 3表示密码认证
		param += "&CHECK_ADD_MEB=" + checkMeb;
	}
	
	var viceInfoDataset = getViceList();
	for (var i = 0; i < viceInfoDataset.length; i++) {
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

    param += '&MEB_LIST=' + viceInfoDataset.toString();
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
        MessageBox.confirm("确认提示", "新增成员[" + unCheckedMember + "]未通过校验，本次受理不会加入多人约消组网！是否继续提交？", function (btn) {
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