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
    ajaxSubmit(null, 'loadInfo', param, 'FamilyInfoPart,viceInfoPart,batAddVicePart,viceInfoPartTitle,batAddVicePartTitle,HiddenPart', function (data) {
        // $.endPageLoading();
        var isJwt = data.get("isJwt");
        var authMainFlag = data.get("AUTH_MAIN_FLAG");
        $("#AUTH_MAIN_FLAG").val(authMainFlag);
        if (isJwt == 'true') {
            MessageBox.alert("信息提示", "监务通用户不能办理短号！");
        }
        valideMemberNumber = data.get("VALIDE_MEBMER_NUMBER");
        useableMemberNumber = data.get("USEABLE_MEMBER_NUMBER");
        useableShortCodes = data.get("USEABLE_SHORT_CODES");

        $("#FamilyInfoPart").css('display', '');
        $("#batAddVicePartTitle").css('display', '');
        $("#batAddVicePart").css('display', '');
        $("#Destroy").css('display', '');

        if (authMainFlag == 'false') { // 如果查询的服务号码为亲亲网副卡，则只允许对自生进行删除亲亲网成员操作

            MessageBox.success("提示信息", "当前服务号码已加入[" + data.get("MAIN_SERIAL_NUMBER") + "]号码组建的亲亲网，只允许对自生进行删除亲亲网成员操作");
            // 禁止变更短号功能在html中实现：将短号选择框置灰
            $("#FamilyInfoPart").css('display', 'none'); // 隐藏主卡信息以及一键注销功能
            $("#batAddVicePartTitle").css('display', 'none'); // 隐藏新增成员功能
            $("#batAddVicePart").css('display', 'none');
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

function changeMainShortCode(obj) {
    var oldFmyShortCode = $("#OLD_FMY_SHORT_CODE").val();
    var newShortCode = obj.val(); // 修改后的新短号
    // shortCodeUsedFlag：短号占用状态：false-空闲;true-占用
    var shortCodeUsedFlag = checkShortCode(true, newShortCode); // 校验主卡短号是否被占用
    if (shortCodeUsedFlag) {
        MessageBox.alert("错误提示", "短号[" + newShortCode + "]已被占用，请重新选择！");
        obj.val(oldFmyShortCode); // 短号被占用，恢复成选择前的短号
        return false;
    } else {
        $("#OLD_FMY_SHORT_CODE").val(newShortCode);
    }
}

/**
 * 成员号码新增
 * @returns {Boolean}
 */
function addMeb() {
    if (!$.validate.verifyAll("FamilyInfoPart")) {
        return false;
    }

    if (!$.validate.verifyAll("batAddVicePart")) {
        return false;
    }

    var mainSn = $("#AUTH_SERIAL_NUMBER").val();

    var viceSerialNumber = $("#VICE_SERIAL_NUMBER").val(); // 新增成员号码

    if (viceSerialNumber.length <= 0) {
        $.TipBox.show(document.getElementById("VICE_SERIAL_NUMBER"), "请输入新增副卡号码（多个号码以空格分隔）", "red");
        return false;
    }

    viceSerialNumber = viceSerialNumber.trim().split(" ");

    if (viceSerialNumber == "") {
        $.TipBox.show(document.getElementById("VICE_SERIAL_NUMBER"), "请输入新增副卡号码（多个号码以空格分隔）", "red");
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

    addViceListNew(viceSerialNumber);
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
    if (tradeOption == 'DESTROY') {
        return; // 如果点了注销按钮，禁止对成员号码删除
    }

    var idx = $(obj).attr("idx");

    // tag取值说明：""-未做改动，"0"-新增，"1"-删除，"2"-修改
    var tag = $("#tag_" + idx).val();
    if (tag == "" || tag == undefined || tag == "2") { // 存量副卡删除，改变样式，并赋值tag=1
        if (tag == "2") { // 存量副卡修改后删除，短号恢复成原始短号
            var orgShortCode = $("#ORG_SHORT_CODE_B_" + idx).val(); // 存量成员原始短号
            // shortCodeUsedFlag：短号占用状态：false-空闲;true-占用
            var shortCodeUsedFlag = checkShortCode(false, orgShortCode); // 校验成员短号是否被占用

            if (shortCodeUsedFlag) {
                MessageBox.alert("错误提示", "短号[" + orgShortCode + "]已被占用，请重新选择！");
                return false;
            }
            $("#NEW_SHORT_CODE_" + idx).val(orgShortCode);
            $("#SHORT_CODE_B_" + idx).val(orgShortCode);
        }
        $("#SERIAL_NUMBER_B_T_" + idx).addClass("e_delete");
        var sysDateT = (new Date()).format('yyyy-MM-dd');
        var sysDate = (new Date()).format('yyyy-MM-dd HH:mm:ss');
        $("#END_DATE_T_" + idx).html(sysDateT);
        $("#END_DATE_" + idx).val(sysDate);
        $("#tag_" + idx).val("1");

        $("#delMebBt_" + idx).css("display", "none");
        $("#resetMebBt_" + idx).css("display", "");
        $("#NEW_SHORT_CODE_" + idx).attr("disabled", true);
    } else if (tag == "0") { // 新增副卡的删除，直接移除
        $(obj).parent(".viceList").remove();
        $("#tag_" + idx).val("");
        useableMemberNumber++;
        $("#USEABLE_MEMBER_NUMBER").html(useableMemberNumber);
        $("#USED_MEMBER_NUMBER").html(9 - useableMemberNumber);
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
    // tag取值说明：""-未做改动，"0"-新增，"1"-删除，"2"-修改
    var tag = $("#tag_" + idx).val();
    if (tag == "1") { // 存量副卡点击删除按钮后又恢复，改变样式，并赋值tag=""
        $("#SERIAL_NUMBER_B_T_" + idx).removeClass("e_delete");
        $("#delMebBt_" + idx).css("display", "");
        $("#resetMebBt_" + idx).css("display", "none");
        if ($("#AUTH_MAIN_FLAG").val() != 'false') { // 如果工号有变更亲亲网短号权限，且服务号码不是副卡，则短号选择框可选择
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
 * 成员号码短号修改
 * @param id
 * @returns {Boolean}
 */
function changeShortCode(obj) {
    var idx = $("#" + obj.id).attr("idx");

    var orgShortCode = $("#ORG_SHORT_CODE_B_" + idx).val(); // 存量成员原始短号
    var oldShortCode = $("#SHORT_CODE_B_" + idx).val(); // 本次修改状态前的短号
    var newShortCode = $("#" + obj.id).val(); // 修改后的新短号

    // shortCodeUsedFlag：短号占用状态：false-空闲;true-占用
    var shortCodeUsedFlag = checkShortCode(false, newShortCode); // 校验成员短号是否被占用

    if (shortCodeUsedFlag) {
        MessageBox.alert("错误提示", "短号[" + newShortCode + "]已被占用，请重新选择！");
        $("#" + obj.id).val(oldShortCode);
        return false;
    }

    $("#SHORT_CODE_B_" + idx).val(newShortCode);

    // tag取值说明：""-未做改动，"0"-新增，"1"-删除，"2"-修改
    var tag = $("#tag_" + idx).val();
    if (tag != "0") { // 已有成员号码短号变更时，tag的处理
        if (newShortCode == orgShortCode) { // 成员短号改为原始短号，tag=""，视为未作修改
            $("#tag_" + idx).val("");
        } else {
            $("#tag_" + idx).val("2");
        }
    }
}

/**
 * 成员号码新增js生成（改造）
 * REQ201912260028取消亲亲网副号码添加确认规则的需求
 * 去除密码，短信校验，默认免密
 * @param viceSerialNumber
 */
function addViceListNew(viceSerialNumber) {
    var fmyVerifyMode = $('#FMY_VERIFY_MODE').val();
    var startDate = new Date();
    var viceDiscntCode = $('#VICE_DISCNT_CODE').val();
    var viceDiscntName = VICE_DISCNT_CODE.selectedText;
    var viceAppDiscntCode = $('#VICE_APP_DISCNT_CODE').val();
    var viceAppDiscntName = VICE_APP_DISCNT_CODE.selectedText;
    if (viceAppDiscntCode == '' || viceAppDiscntCode == null) {
        viceAppDiscntName = "";
    }
    var param = "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val() + '&MEB_LIST=' + viceSerialNumber;
    $.beginPageLoading("副号码校验...");
    ajaxSubmit(null, 'checkAddMebMul', param, null, function (data) {
        var mebCheckList = $.DatasetList();
        mebCheckList=data.get("MEB_CHECK_LIST");
        for (var i = 0; i < viceSerialNumber.length; i++) {
            var mebCheck = $.DataMap();
            mebCheck=mebCheckList.get(i);
            var errorInfo=mebCheck.get("ERROR_INFO");
            var errorTag=mebCheck.get("ERROR_TAG");
            var serialNumberB=mebCheck.get("SERIAL_NUMBER_B");

            for (var j = 0; j < useableShortCodes.length; j++) {
                var useableShortCode = $.DataMap();
                useableShortCode = useableShortCodes.get(j);
                var newShortCode = useableShortCode.get("DATA_ID");

                var shortCodeUsedFlag = checkShortCode(false, newShortCode); // 校验成员短号是否被占用

                if (!shortCodeUsedFlag) { // 短号未被使用
                    break;
                } else {
                    newShortCode = "";
                }
            }
            var validClass='';
            var checkTag='';
            if(errorTag=='1'){
                validClass='e_orange';
                checkTag='';
            }else{
                validClass='e_green';
                checkTag='1';
            }
            var tempNode = "";
            tempNode += '<li class="viceList" id="viceList_' + viceLisIndex + '" name="viceList" idx="' + viceLisIndex + '">';
            tempNode += '	<div id="viceValue_' + viceLisIndex + '" class="main">';
            tempNode += '		<div class="title"><span class="'+validClass+'" id="SERIAL_NUMBER_B_T_' + viceLisIndex + '">' + serialNumberB + '</span></div>';
            tempNode += '		<div class="content">';
            tempNode += '		<span class="'+validClass+'" title="'+errorInfo+'" id="VALID_INFO' + viceLisIndex + '">' + errorInfo + '</span>';

            tempNode += '		</div>';

            tempNode += '		<input type="hidden" name="INST_ID_B" id="INST_ID_B_' + viceLisIndex + '" value=""/>';
            tempNode += '		<input type="hidden" name="SERIAL_NUMBER_B" id="SERIAL_NUMBER_B_' + viceLisIndex + '" value="' + serialNumberB + '" datatype="mbphone" maxLength="11" value="" desc="副号码" nullable="no"/>';
            tempNode += '		<input type="hidden" name="DISCNT_CODE_B" id="DISCNT_CODE_B_' + viceLisIndex + '" value="' + viceDiscntCode + '"/>';
            tempNode += '		<input type="hidden" name="DISCNT_NAME_B" id="DISCNT_NAME_B_' + viceLisIndex + '" value="' + viceDiscntName + '"/>';
            tempNode += '		<input type="hidden" name="SHORT_CODE_B" id="SHORT_CODE_B_' + viceLisIndex + '" value="' + newShortCode + '"/>';
            tempNode += '		<input type="hidden" name="START_DATE" id="START_DATE_' + viceLisIndex + '" value="' + startDate.format("yyyy-MM-dd HH:mm:ss") + '"/>';
            tempNode += '		<input type="hidden" name="END_DATE" id="END_DATE_' + viceLisIndex + '" value="2050-12-31 23:59:59"/>';
            tempNode += '		<input type="hidden" name="ORG_END_DATE" id="ORG_END_DATE_' + viceLisIndex + '"/>';
            tempNode += '		<input type="hidden" name="APP_DISCNT_CODE_B" id="APP_DISCNT_CODE_B_' + viceLisIndex + '" value="' + viceAppDiscntCode + '"/>';
            tempNode += '		<input type="hidden" name="APP_DISCNT_NAME_B" id="APP_DISCNT_NAME_B_' + viceLisIndex + '" value="' + viceAppDiscntName + '"/>';
            tempNode += '		<input type="hidden" name="MEMBER_ROLE_B" id="MEMBER_ROLE_B_' + viceLisIndex + '" value=""/>';
            tempNode += '		<input type="hidden" name="NICK_NAME_B" id="NICK_NAME_B_' + viceLisIndex + '" value=""/>';
            tempNode += '		<input type="hidden" name="MEMBER_KIND_B" id="MEMBER_KIND_B_' + viceLisIndex + '" value=""/>';
            tempNode += '		<input type="hidden" name="MEB_VERIFY_MODE" id="MEB_VERIFY_MODE_' + viceLisIndex + '" value="2"/>';
            tempNode += '		<input type="hidden" name="tag" id="tag_' + viceLisIndex + '" value="0"/>';
            tempNode += '		<input type="hidden" name="checkTag" id="checkTag_' + viceLisIndex + '" value="'+checkTag+'"/>';
            tempNode += '	</div>';

            tempNode += '	<input type="hidden" name="POP_SERIAL_NUMBER_B_' + viceLisIndex + '" id="POP_SERIAL_NUMBER_B_' + viceLisIndex + '"  islocal="false" cachesn="false" isauth="true" checktag="01000" exceptaction="exceptAction(state, data)" tradeaction="mebCheck(' + "'pwdValidBt'" + "," + "'" + viceLisIndex + "'" + ')" bindevent="false" desc=""/>';
            tempNode += '	<div id="viceShortCode_' + viceLisIndex + '" class="side" idx="' + viceLisIndex + '">';
            tempNode += '	</div>';
            tempNode += '	<div name="delMebBt" id="delMebBt_' + viceLisIndex + '" idx="' + viceLisIndex + '" class="fn" onclick="delMeb(this);" tip="删除"><span class="e_ico-delete"></span></div>';
            tempNode += '</li>\r\n';

            $("#viceInfoPart > ul").prepend(tempNode);

            //

            $.Select.append("viceShortCode_" + viceLisIndex, {
                id: "NEW_SHORT_CODE_" + viceLisIndex,
                name: "NEW_SHORT_CODE",
                addDefault: false,
                textField: "DATA_NAME",
                valueField: "DATA_ID",
                value: newShortCode,
                className: "e_select e_select-r e_select-s",
                optionWidth: "6"
            }, useableShortCodes);

            $("#NEW_SHORT_CODE_" + viceLisIndex).attr("idx", viceLisIndex);

            $("#NEW_SHORT_CODE_" + viceLisIndex).bind("change", function () {
                changeShortCode(this); // 添加多个成员时，绑定change事件存在问题：index
            });
            useableMemberNumber--;
            $("#USEABLE_MEMBER_NUMBER").html(useableMemberNumber);
            $("#USED_MEMBER_NUMBER").html(9 - useableMemberNumber);
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
 * 成员号码新增js生成
 * @param viceSerialNumber
 */
function addViceList(viceSerialNumber) {
    var fmyVerifyMode = $('#FMY_VERIFY_MODE').val();
    var startDate = new Date();
    var viceDiscntCode = $('#VICE_DISCNT_CODE').val();
    var viceDiscntName = VICE_DISCNT_CODE.selectedText;
    var viceAppDiscntCode = $('#VICE_APP_DISCNT_CODE').val();
    var viceAppDiscntName = VICE_APP_DISCNT_CODE.selectedText;
    if (viceAppDiscntCode == '' || viceAppDiscntCode == null) {
        viceAppDiscntName = "";
    }

    var verifyModeList = $.DatasetList();
    ajaxSubmit(null, 'getVerifyModeList', null, null, function (data) {
        verifyModeList = data.get("VERIFY_MODE_LIST");
        for (var i = 0; i < viceSerialNumber.length; i++) {
            var serialNumberB = viceSerialNumber[i];
            for (var j = 0; j < useableShortCodes.length; j++) {
                var useableShortCode = $.DataMap();
                useableShortCode = useableShortCodes.get(j);
                var newShortCode = useableShortCode.get("DATA_ID");

                var shortCodeUsedFlag = checkShortCode(false, newShortCode); // 校验成员短号是否被占用

                if (!shortCodeUsedFlag) { // 短号未被使用
                    break;
                } else {
                    newShortCode = "";
                }
            }
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

            tempNode += '		<input type="hidden" name="INST_ID_B" id="INST_ID_B_' + viceLisIndex + '" value=""/>';
            tempNode += '		<input type="hidden" name="SERIAL_NUMBER_B" id="SERIAL_NUMBER_B_' + viceLisIndex + '" value="' + serialNumberB + '" datatype="mbphone" maxLength="11" value="" desc="副号码" nullable="no"/>';
            tempNode += '		<input type="hidden" name="DISCNT_CODE_B" id="DISCNT_CODE_B_' + viceLisIndex + '" value="' + viceDiscntCode + '"/>';
            tempNode += '		<input type="hidden" name="DISCNT_NAME_B" id="DISCNT_NAME_B_' + viceLisIndex + '" value="' + viceDiscntName + '"/>';
            tempNode += '		<input type="hidden" name="SHORT_CODE_B" id="SHORT_CODE_B_' + viceLisIndex + '" value="' + newShortCode + '"/>';
            tempNode += '		<input type="hidden" name="START_DATE" id="START_DATE_' + viceLisIndex + '" value="' + startDate.format("yyyy-MM-dd HH:mm:ss") + '"/>';
            tempNode += '		<input type="hidden" name="END_DATE" id="END_DATE_' + viceLisIndex + '" value="2050-12-31 23:59:59"/>';
            tempNode += '		<input type="hidden" name="ORG_END_DATE" id="ORG_END_DATE_' + viceLisIndex + '"/>';
            tempNode += '		<input type="hidden" name="APP_DISCNT_CODE_B" id="APP_DISCNT_CODE_B_' + viceLisIndex + '" value="' + viceAppDiscntCode + '"/>';
            tempNode += '		<input type="hidden" name="APP_DISCNT_NAME_B" id="APP_DISCNT_NAME_B_' + viceLisIndex + '" value="' + viceAppDiscntName + '"/>';
            tempNode += '		<input type="hidden" name="MEMBER_ROLE_B" id="MEMBER_ROLE_B_' + viceLisIndex + '" value=""/>';
            tempNode += '		<input type="hidden" name="NICK_NAME_B" id="NICK_NAME_B_' + viceLisIndex + '" value=""/>';
            tempNode += '		<input type="hidden" name="MEMBER_KIND_B" id="MEMBER_KIND_B_' + viceLisIndex + '" value=""/>';
            tempNode += '		<input type="hidden" name="MEB_VERIFY_MODE" id="MEB_VERIFY_MODE_' + viceLisIndex + '"/>';
            tempNode += '		<input type="hidden" name="tag" id="tag_' + viceLisIndex + '" value="0"/>';
            tempNode += '		<input type="hidden" name="checkTag" id="checkTag_' + viceLisIndex + '" value=""/>';
            tempNode += '	</div>';

            tempNode += '	<input type="hidden" name="POP_SERIAL_NUMBER_B_' + viceLisIndex + '" id="POP_SERIAL_NUMBER_B_' + viceLisIndex + '"  islocal="false" cachesn="false" isauth="true" checktag="01000" exceptaction="exceptAction(state, data)" tradeaction="mebCheck(' + "'pwdValidBt'" + "," + "'" + viceLisIndex + "'" + ')" bindevent="false" desc=""/>';
            tempNode += '	<div id="viceShortCode_' + viceLisIndex + '" class="side" idx="' + viceLisIndex + '">';
            tempNode += '	</div>';
            tempNode += '	<div name="delMebBt" id="delMebBt_' + viceLisIndex + '" idx="' + viceLisIndex + '" class="fn" onclick="delMeb(this);" tip="删除"><span class="e_ico-delete"></span></div>';
            tempNode += '</li>\r\n';

            $("#viceInfoPart > ul").prepend(tempNode);

            //

            $.Select.append("viceShortCode_" + viceLisIndex, {
                id: "NEW_SHORT_CODE_" + viceLisIndex,
                name: "NEW_SHORT_CODE",
                addDefault: false,
                textField: "DATA_NAME",
                valueField: "DATA_ID",
                value: newShortCode,
                className: "e_select e_select-r e_select-s",
                optionWidth: "6"
            }, useableShortCodes);

            $("#NEW_SHORT_CODE_" + viceLisIndex).attr("idx", viceLisIndex);

            $("#NEW_SHORT_CODE_" + viceLisIndex).bind("change", function () {
                changeShortCode(this); // 添加多个成员时，绑定change事件存在问题：index
            });
            useableMemberNumber--;
            $("#USEABLE_MEMBER_NUMBER").html(useableMemberNumber);
            $("#USED_MEMBER_NUMBER").html(9 - useableMemberNumber);
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
 * 校验短号是否被占用
 * @param isMain 是否主卡：true-主卡，false-成员
 * @param newShortCode
 * @returns {Boolean}
 */
function checkShortCode(isMain, newShortCode) {
    var viceInfoDataset = $.DatasetList();
    viceInfoDataset = getViceList();
    // shortCodeUsedFlag：短号占用状态：false-空闲;true-占用
    var shortCodeUsedFlag = false; // 空闲

    var mainShortCode = $("#FMY_SHORT_CODE").val();//主号码短号
    if (!isMain && newShortCode != "" && newShortCode == mainShortCode) {
        shortCodeUsedFlag = true; // 占用
        return shortCodeUsedFlag;
    }

    for (var k = 0; k < viceInfoDataset.length; k++) {
        var shortCode = viceInfoDataset.get(k).get('SHORT_CODE_B');
        if (newShortCode != "" && newShortCode == shortCode) {
            shortCodeUsedFlag = true; // 占用
            break;
        }
    }
    return shortCodeUsedFlag;
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
        viceInfoData.put("INST_ID_B", $("#INST_ID_B_" + idx).val());
        viceInfoData.put("SERIAL_NUMBER_B", $("#SERIAL_NUMBER_B_" + idx).val());
        viceInfoData.put("DISCNT_CODE_B", $("#DISCNT_CODE_B_" + idx).val());
        viceInfoData.put("DISCNT_NAME_B", $("#DISCNT_NAME_B_" + idx).val());
        viceInfoData.put("SHORT_CODE_B", $("#SHORT_CODE_B_" + idx).val());
        viceInfoData.put("START_DATE", $("#START_DATE_" + idx).val());
        viceInfoData.put("END_DATE", $("#END_DATE_" + idx).val());
        viceInfoData.put("APP_DISCNT_CODE_B", $("#APP_DISCNT_CODE_B_" + idx).val());
        viceInfoData.put("APP_DISCNT_NAME_B", $("#APP_DISCNT_NAME_B_" + idx).val());
        viceInfoData.put("MEMBER_ROLE_B", $("#MEMBER_ROLE_B_" + idx).val());
        viceInfoData.put("NICK_NAME_B", $("#NICK_NAME_B_" + idx).val());
        viceInfoData.put("MEMBER_KIND_B", $("#MEMBER_KIND_B_" + idx).val());
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
                resetMemCheckResult(idx);
                $.userCheck.checkUser(fieldId);
            }
        });
        return;
    }
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
                mebCheck(smsBtn, idx);
            }
        });
        return;
    }
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
                mebCheck(noBtn, idx);
                return;
            }
        });
        return;
    }
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
    ajaxSubmit(null, 'checkAddMeb', param, null, function () {
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
//办理一键注销业务
function onDesTradeSubmit() {
    // 获取服务号码
    var serialNum = $("#AUTH_SERIAL_NUMBER").val();
    // 获取注销按钮的样式
    var className = $("#Destroy").attr("class");
    if (serialNum == '') {
        MessageBox.alert("信息提示", "请先进行服务号码的查询！");
        if (className == 'e_button-blue') {
            $("#Destroy").removeClass().addClass('e_button-red');
        }
        return false;
    }
    if ($("#FMY_SHORT_CODE").val() != $("#ORG_FMY_SHORT_CODE").val()) { // 已做主卡短号变更，不允许进行注销
        MessageBox.alert("信息提示", "请先完成其他业务未完成的操作，再进行注销业务的操作！");
        return false;
    }
    // 按钮的操作校验
    var tradeOption = $("#TRADE_OPTION").val();

    // tag取值说明：""-未做改动，"0"-新增，"1"-删除，"2"-修改
    var viceInfoDataset = getViceList();
    for (var i = 0; i < viceInfoDataset.length; i++) {
        var viceInfoData = viceInfoDataset.get(i);
        var tag = viceInfoDataset.get(i).get('tag');
        if (tag == "0") {
            tradeOption = "CREATE";
            break;
        } else if (tag == "1") {
            tradeOption = "DELETE";
            break;
        } else if (tag == "2") {
            tradeOption = "CHANGE";
            break;
        }
    }

    if (tradeOption == 'NULL') { // 没有成员新增、删除、变更，可以进行一键注销
        // 判断该服务号码是否能进行注销业务的办理
        var shortCode = $("#FMY_SHORT_CODE").val();
        if (shortCode == '') {
            MessageBox.error("错误", "您还未开通亲亲网业务,不能办理该业务");
            return false;
        }
        // 点击注销后，将业务操作改为注销状态
        $("#TRADE_OPTION").val('DESTROY');

        $("#FMY_SHORT_CODE").attr("disabled", true); // 主卡短号置灰
        disabledArea("batAddVicePart", true); // 成员添加部分置灰
        disabledArea("viceInfoPart", true);	// 成员列表部分置灰
        $("#Destroy").removeClass().addClass("e_button-blue");

        return;
    } else if (tradeOption == 'DESTROY') { // 已经点击注销按钮，再次点击取消注销
        $("#TRADE_OPTION").val('NULL');

        $("#FMY_SHORT_CODE").attr("disabled", false); // 主卡短号可编辑
        disabledArea("batAddVicePart", false); // 成员添加部分可编辑
        disabledArea("viceInfoPart", false); // 成员列表部分可编辑
        $("#Destroy").removeClass().addClass("e_button-red");

        return;
    } else { // 做了成员新增、删除或变更操作，不允许注销
        MessageBox.alert("信息提示", "请先完成其他业务未完成的操作，再进行注销业务的操作！");
        return false;
    }
}

//业务提交
function onTradeSubmit() {
    var param = '&SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();

    var unCheckedMember = "";

    // tradeOption：DESTROY-一键注销，NULL-没有操作或者做了成员新增、删除或变更操作
    var tradeOption = $("#TRADE_OPTION").val();
    param += '&TRADE_OPTION=' + tradeOption;
    if (tradeOption != 'DESTROY') { // 非一键注销受理，获取受理数据
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
        // 主卡短号变更
        if ($("#FMY_CREATE_FLAG").val() == "true" && $("#FMY_SHORT_CODE").val() != $("#ORG_FMY_SHORT_CODE").val()) {
            var mainInfoData = $.DataMap();
            mainInfoData.put("INST_ID_B", $("#MAIN_INST_ID_B").val());
            mainInfoData.put("SERIAL_NUMBER_B", $("#AUTH_SERIAL_NUMBER").val());
            mainInfoData.put("SHORT_CODE_B", $("#FMY_SHORT_CODE").val());
            mainInfoData.put("START_DATE", $("#MAIN_START_DATE").val());
            mainInfoData.put("END_DATE", $("#MAIN_END_DATE").val());
            mainInfoData.put("tag", "2");
            viceInfoDataset.add(mainInfoData);
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
            MessageBox.confirm("确认提示", "新增成员[" + unCheckedMember + "]未通过校验，本次受理不会加入该亲亲网！是否继续提交？", function (btn) {
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
    } else {
        $.cssubmit.addParam(param);
        return true;
    }
}