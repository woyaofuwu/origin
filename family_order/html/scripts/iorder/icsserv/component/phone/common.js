function getMBOPObject(){
    return window.MBOP;
}

$.extend({
    MBOP: {
        // 扫描二代身份证参数
        bluetoothReaderTag: "", // 扫描对象标识
        // 扫描二代身份证
        getMsgByEForm: function (fieldName) {
            $.MBOP.bluetoothReaderTag = fieldName;
            // alert("BTReaderTag:" + fieldName);
            getMBOPObject().btIDCRead("btIDCReadCallback");
        },
        // 人像比对摄像参数
        shotImageTag: "",      // 人像比对拍摄对象标识
        shotImageAgent: "0",   // 经办人人像比对标识，默认是客户本人，非经办人
        shotImagePath: "",     // 人像照片存储路径
        shotImageSuccess: "0", // 人像比对成功标识
        // 人像比对摄像
        shotImage: function (fieldName) {
            var jsonParam = {
                "savePath": "/image/",
                "photoName": "head",
                "isHorizontal": true,
                "isMask": false,
                "type": "people",
                "watermark": "",
                "phoneType": "camera",
                "needThumb": true,
                "isBase64": true
            };
            $.MBOP.shotImageTag = fieldName;
            $.MBOP.shotImageAgent = "0";
            $.MBOP.shotImagePath = "";
            $.MBOP.shotImageSuccess = "0";
            // alert("shotImageTag:" + fieldName);
            getMBOPObject().takePhotograph($.stringifyJSON(jsonParam), "takePhotographCallback");
        }
    }
});

// 扫描二代身份证信息回调函数
function btIDCReadCallback(jsonData) {
    // alert("btIDCReadCallback");
    var picInfo, code;
	if($.MBOP.bluetoothReaderTag==''){
		/*if(typeof($("#AUTH_CHECK").val())!='undefined'){
			$.MBOP.bluetoothReaderTag=$("#AUTH_CHECK").val();
		}*/
		//只有用户鉴权才会空，所以先写死
		$.MBOP.bluetoothReaderTag='AUTH_CHECK';
	}
    switch ($.MBOP.bluetoothReaderTag) {
        case "PSPT_ID": // 个人用户开户客户
            var $editArea = $("#SHOW_EDIT_AREA");
            fillUpPsptForm("PSPT_ID", "CUST_NAME,PAY_NAME", "SEX", "FOLK_CODE",
                "BIRTHDAY", "PSPT_ADDR,POST_ADDRESS", null, "PSPT_END_DATE",
                jsonData);
            $.custInfo.renderSpecialFieldByPsptId($("#PSPT_ID").val());
            $.custInfo.verifyIdCard("PSPT_ID");
            $.custInfo.verifyIdCardName("PSPT_ID");
            if (!$.custInfo.isRealName) {
                $editArea.trigger("click");
                break;
            }
            if ("CREATEUSERSW" === $("#CHECK_USER_PSPTID").val()) {
                $("#CONTACT_ADDRESS").val($("#PSPT_ADDR").val());
            }
            $.custInfo.checkRealNameLimitByPspt();
            $editArea.trigger("click");
            break;
        case "MEM_CUST_INFO_PSPT_ID": // 集团成员开户 客户证件号码
            var $editArea = $("#SHOW_EDIT_AREA");
            fillUpPsptForm("MEM_CUST_INFO_PSPT_ID", "MEM_CUST_INFO_CUST_NAME", "", "",
                "", "MEM_CUST_INFO_PSPT_ADDRESS", null, "",
                jsonData);
            $.assurecustvalidate.chkPsptId("MEM_CUST_INFO_PSPT_ID");
            $editArea.trigger("click");
            break;
        case "AGENT_PSPT_ID": // 个人用户开户经办人
            picInfo = null;
            code = $("#PSPT_TYPE_CODE").val();
            if ("D" === code || "E" === code || "G" === code || "L" === code
                || "M" === code) {
                picInfo = "PIC_INFO";
            }
            fillUpPsptForm("AGENT_PSPT_ID", "AGENT_CUST_NAME", picInfo, null,
                null, "AGENT_PSPT_ADDR", null, null, jsonData);
            $.custInfo.chkPsptId("AGENT_PSPT_ID");
            $.custInfo.verifyIdCard("AGENT_PSPT_ID");
            $.custInfo.verifyIdCardName("AGENT_PSPT_ID");
            break;
        case "MEM_CUST_INFO_AGENT_PSPT_ID": // 集团成员开户 经办人证件号码
            picInfo = null;
            code = $("#MEM_CUST_INFO_AGENT_PSPT_ID").val();
            if ("D" === code || "E" === code || "G" === code || "L" === code
                || "M" === code) {
                picInfo = "PIC_INFO";
            }
            fillUpPsptForm("MEM_CUST_INFO_AGENT_PSPT_ID", "MEM_CUST_INFO_AGENT_CUST_NAME", picInfo, null,
                null, "MEM_CUST_INFO_AGENT_PSPT_ADDR", null, null, jsonData);
            $.assurecustvalidate.chkPsptId("MEM_CUST_INFO_AGENT_PSPT_ID");
            break;
        case "USE_PSPT_ID": // 个人用户开户使用人
            fillUpPsptForm("USE_PSPT_ID", "USE", null, null, null,
                "USE_PSPT_ADDR", null, null, jsonData);
            $.custInfo.verifyIdCard("USE_PSPT_ID");
            $.custInfo.verifyIdCardName("USE_PSPT_ID");
            break;
        case "MEM_CUST_INFO_USE_PSPT_ID": //  集团成员开户  使用人
            fillUpPsptForm("MEM_CUST_INFO_USE_PSPT_ID", "MEM_CUST_INFO_USE", null, null, null,
                "MEM_CUST_INFO_USE_PSPT_ADDR", null, null, jsonData);
            $.assurecustvalidate.verifyIdCard("MEM_CUST_INFO_USE_PSPT_ID");
            $.assurecustvalidate.verifyIdCardName("MEM_CUST_INFO_USE_PSPT_ID");
            break;
        case "RSRV_STR4": // 个人用户开户责任人
            fillUpPsptForm("RSRV_STR4", "RSRV_STR2", null, null, null,
                "RSRV_STR5", null, null, jsonData);
            $.custInfo.verifyIdCard("RSRV_STR4");
            $.custInfo.verifyIdCardName("RSRV_STR4");
            break;
        case "MEM_CUST_INFO_RSRV_STR4": // 集团成员开户  责任人
            fillUpPsptForm("MEM_CUST_INFO_RSRV_STR4", "MEM_CUST_INFO_RSRV_STR2", null, null, null,
                "MEM_CUST_INFO_RSRV_STR5", null, null, jsonData);
            $.assurecustvalidate.verifyIdCard("MEM_CUST_INFO_RSRV_STR4");
            $.assurecustvalidate.verifyIdCardName("MEM_CUST_INFO_RSRV_STR4");
            break;
        case "FORM_PSPT_ID": // 过户原客户
            fillUpPsptForm("FORM_PSPT_ID", "FORM_CUST_NAME", "PIC_INFO", null,
                null, null, null, null, jsonData);
            var formPsptId = $("#FORM_PSPT_ID").val();
            if (formPsptId !== $("#OLD_PSPT_ID_NUM").val()) {
                MessageBox.alert("原客户扫描信息与原客户真实信息不一致");
                $("#FORM_PIC_ID").val("ERROR");
                $("#FORM_PIC_STREAM").val("扫描信息与原客户真实信息不一致");
                break;
            }
            if ("" !== formPsptId) {
                MessageBox.success("原客户扫描成功，原客户姓名："
                    + $("#FORM_CUST_NAME").val() + ",证件号码：" + formPsptId);
            }
            break;
        case "AUTH_CHECK": // 用户鉴权
            var jsonObj = $.parseJSON(jsonData);
			$('iframe[src*="components.auth.AuthCheckNew"]').contents().find("#cond_PSPT_ID").val(jsonObj.no);
			$('iframe[src*="components.auth.AuthCheckNew"]').contents().find("#E_NAME").val(jsonObj.name);
			$('iframe[src*="components.auth.AuthCheckNew"]').contents().find("#E_ADDRESS").val(jsonObj.address);
            break;
        case "custInfo_PSPT_ID_73": // 用户密码重置客户
            fillUpPsptForm("custInfo_PSPT_ID", "custInfo_CUST_NAME", null, null,
                null, "custInfo_PSPT_ADDR,custInfo_POST_ADDRESS", null,
                "custInfo_PSPT_END_DATE", jsonData);
            break;
        case "custInfo_AGENT_PSPT_ID_73": // 用户密码重置经办人
            picInfo = null;
            code = $("#custInfo_PSPT_TYPE_CODE").val();
            if ("E" === code || "G" === code || "D" === code || "M" === code
                || "L" === code) {
                picInfo = "PIC_INFO";
            }
            fillUpPsptForm("custInfo_AGENT_PSPT_ID", "custInfo_AGENT_CUST_NAME",
                picInfo, null, null, null, null, null, jsonData);
            break;
        case "custInfo_AGENT_PSPT_ID_142": // 补卡经办人
            picInfo = null;
            code = $("#custInfo_PSPT_TYPE_CODE").val();
            if ("E" === code || "G" === code || "D" === code || "M" === code
                || "L" === code) {
                picInfo = "PIC_INFO";
            }
            fillUpPsptForm("custInfo_AGENT_PSPT_ID", "custInfo_AGENT_CUST_NAME",
                picInfo, null, null, null, null, null, jsonData);
            break;
        case "custInfo_PSPT_ID": // APP用户密码管理
            picInfo = null;
            code = $("#custInfo_PSPT_TYPE_CODE").val();
            if ("E" === code || "G" === code || "D" === code || "M" === code
                || "L" === code) {
                picInfo = "PIC_INFO";
            }
            fillUpPsptForm("custInfo_PSPT_ID", "custInfo_CUST_NAME",
                picInfo, null, null, null, null, null, jsonData);
            break;
        case "custInfo_USE_PSPT_ID2": // 单位激活使用人
            picInfo = null;
            code = $("#custInfo_PSPT_TYPE_CODE").val();
            if ("E" === code || "G" === code || "D" === code || "M" === code
                || "L" === code) {
                picInfo = "PIC_INFO";
            }
            fillUpPsptForm("custInfo_USE_PSPT_ID2", "custInfo_USE",
                picInfo, null, null, null, null, null, jsonData);
            break;
	    
        default:
            MessageBox.alert("警告提示", "该功能未在移动端适配！");
    }
    $.MBOP.bluetoothReaderTag = "";
}

function fillUpPsptForm(idField, nameField, sexField, nationField, birthdayField,
                        addressField, departmentField, validDateField, jsonStr) {
    var jsonObj = $.parseJSON(jsonStr);
    if (jsonObj && 0 === jsonObj.exeResult) {
        var id          = jsonObj.no,
            name        = jsonObj.name,
            sex         = jsonObj.sex,
            nation      = jsonObj.nation,
            birthday    = jsonObj.birthday,
            address     = jsonObj.address,
            department  = jsonObj.department,
            validDate   = jsonObj.endDate,
            frontBase64 = jsonObj.base64Bitmap;

        if (validDate && "" !== validDate) {
            // 当前日期格式化
            var now = new Date(),
                month = (now.getMonth() + 1).toString(),
                day = now.getDate().toString(),
                validDate2 = parseInt(validDate.substring(validDate.indexOf("-") + 1).replace(".", "").replace(".", ""));

            month = 1 === month.length ? "0" + month : month;
            day = 1 === day.length ? "0" + day : day;
            now = now.getFullYear().toString() + month + day;

            if (validDate2 < now) {
                MessageBox.alert("提示", "证件有效期已过！有效期为：" + validDate);
                return;
            } else {
                var validDate3 = formatDate(validDate.substr(validDate.indexOf("-") + 1, validDate.length).replace(".", "-").replace(".", "-"));
                setEFormElementValue(validDateField, validDate3);
            }
        }

        var picTag = "PIC_INFO" === sexField;
        if (name && "" !== name) setEFormElementValue(nameField, name);
        if (sex && "" !== sex && !picTag) setEFormElementValue(sexField, sex);
        if (nation && "" !== nation) setEFormElementValue(nationField, nation);

        var front = "",
            custInfo = "custInfo" === idField.split("_")[0] ? "custInfo_" : "";
        if (picTag) {
            if ("FORM_PSPT_ID" === idField) { // 原客户证件扫描
                front = "FORM_FRONTBASE64";
                setEFormElementValue("FORM_SCAN_TAG", "0");
            } else if ("AGENT_PSPT_ID" === idField) { // 经办人证件扫描
                front = custInfo + "AGENT_FRONTBASE64";
                setEFormElementValue(custInfo + "AGENT_SCAN_TAG", "0");
            }else if ("custInfo_USE_PSPT_ID2" == idField) { // 使用人证件扫描
                front = custInfo + "USE_FRONTBASE64";
                setEFormElementValue(custInfo + "USE_SCAN_TAG", "0");
            }
        } else { // 客户证件扫描
            front = custInfo + "FRONTBASE64";
            setEFormElementValue(custInfo + "SCAN_TAG", "0");
        }


        if (8 === birthday.length) {
            setEFormElementValue(birthdayField, birthday.substring(0, 4) + "-" + birthday.substring(4, 6) + "-" + birthday.substring(6));
        } else if (birthday && "" !== birthday) {
            setEFormElementValue(birthdayField, formatDate(birthday.replace("年", "-").replace("月", "-").replace("日", "")));
        }
        if (id && "" !== id) setEFormElementValue(idField, id);
        if (address && "" !== address) setEFormElementValue(addressField, address);
        if (department && "" !== department) setEFormElementValue(departmentField, department);
        /*alert("frontBase64: " + frontBase64.substring(0,40));
        alert("front: " + front);*/
        if (frontBase64 && "" !== frontBase64) setEFormElementValue(front, frontBase64);
        // alert("front.val(): " + front.val().substring(0,40));
    }
}

// 人像比对摄像回调函数
function takePhotographCallback(jsonStr) {
    // alert("takePhotographCallback");
    var jsonObj = $.parseJSON(jsonStr);
    if (jsonObj && 0 === jsonObj.exeResult) {
        var custName, psptId, psptType, front, picIdField, picStreamField, picPathField,
            blackTypeCode = $("#BLACK_TRADE_TYPE").val(),
            serialNumber = "100" === blackTypeCode ? $("#AUTH_SERIAL_NUMBER").val() : $("#SERIAL_NUMBER").val(),
            clazz = "com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.BaseInfoFieldHandler",
            picPath = jsonObj.filepath,
            picStream = jsonObj.imageBase64;

        switch ($.MBOP.shotImageTag) {
            case "PIC": // 个人用户开户客户
                custName = $("#CUST_NAME").val();
                psptId = $("#PSPT_ID").val();
                psptType = $("#PSPT_TYPE_CODE").val();
                front = $("#FRONTBASE64").val();
                picIdField = "PIC_ID";
                picStreamField = "PIC_STREAM";
                picPathField = "PIC_PATH";
                break;
            case "MEM_CUST_INFO_PIC": //  集团成员开户 客户
                custName = $("#MEM_CUST_INFO_CUST_NAME").val();
                psptId = $("#MEM_CUST_INFO_PSPT_ID").val();
                psptType = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
                front = $("#MEM_CUST_INFO_FRONTBASE64").val();
                picIdField = "MEM_CUST_INFO_PIC_ID";
                picStreamField = "MEM_CUST_INFO_PIC_STREAM";
                picPathField = "MEM_CUST_INFO_PIC_PATH";
                break;
            case "AGENT": // 个人用户开户经办人
                custName = $("#AGENT_CUST_NAME").val();
                psptId = $("#AGENT_PSPT_ID").val();
                psptType = $("#AGENT_PSPT_TYPE_CODE").val();
                front = $("#AGENT_FRONTBASE64").val();
                picIdField = "AGENT_PIC_ID";
                picStreamField = "AGENT_PIC_STREAM";
                picPathField = "AGENT_PIC_PATH";
                $.MBOP.shotImageAgent = "1";
                break;
            case "MEM_CUST_INFO_AGENT": // 个人用户开户经办人
                custName = $("#MEM_CUST_INFO_AGENT_CUST_NAME").val();
                psptId = $("#MEM_CUST_INFO_AGENT_PSPT_ID").val();
                psptType = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val();
                front = $("#MEM_CUST_INFO_AGENT_FRONTBASE64").val();
                picIdField = "MEM_CUST_INFO_AGENT_PIC_ID";
                picStreamField = "MEM_CUST_INFO_AGENT_PIC_STREAM";
                picPathField = "MEM_CUST_INFO_AGENT_PIC_PATH";
                $.MBOP.shotImageAgent = "1";
                break;
            case "custInfo_PIC_ID_73": // 用户密码重置客户
                custName = $("#custInfo_CUST_NAME").val();
                psptId = $("#custInfo_PSPT_ID").val();
                psptType = $("#custInfo_PSPT_TYPE_CODE").val();
                front = $("#custInfo_FRONTBASE64").val();
                picIdField = "custInfo_PIC_ID";
                picStreamField = "custInfo_PIC_STREAM";
                picPathField = "custInfo_PIC_PATH";
                blackTypeCode = "73";
                serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
                clazz = "com.asiainfo.veris.crm.iorder.web.person.changepassword.ModifyUserPwdInfoNew";
                break;
            case "custInfo_PIC_ID_142":
                custName = $("#AUTH_CHECK_CUSTINFO_CUST_NAME").val();
                if (custName === "")
                    custName = $("#UCA_CUST_NAME").val();
                psptId = $("#AUTH_CHECK_PSPT_ID").val();
                psptType = $("#AUTH_CHECK_PSPT_TYPE_CODE").val();
                front = $("#FRONTBASE64").val();
                picIdField = "custInfo_PIC_ID";
                picStreamField = "custInfo_PIC_STREAM";
                picPathField = "custInfo_PIC_PATH";
                blackTypeCode = "142";
                serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
                clazz = "com.asiainfo.veris.crm.iorder.web.person.personview.simcardmgr.RemoteCard";
                break;
            case "custInfo_AGENT_PIC_ID_73": // 用户密码重置经办人
                custName = $("#custInfo_AGENT_CUST_NAME").val();
                psptId = $("#custInfo_AGENT_PSPT_ID").val();
                psptType = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
                front = $("#custInfo_AGENT_FRONTBASE64").val();
                picIdField = "custInfo_AGENT_PIC_ID";
                picStreamField = "custInfo_AGENT_PIC_STREAM";
                picPathField = "custInfo_AGENT_PIC_PATH";
                blackTypeCode = "73";
                serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
                clazz = "com.asiainfo.veris.crm.iorder.web.person.changepassword.ModifyUserPwdInfoNew";
                $.MBOP.shotImageAgent = "1";
                break;
            case "custInfo_AGENT_PIC_ID_142":
                custName = $("#custInfo_AGENT_CUST_NAME").val();
                psptId = $("#custInfo_AGENT_PSPT_ID").val();
                psptType = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
                front = $("#custInfo_AGENT_FRONTBASE64").val();
                picIdField = "custInfo_AGENT_PIC_ID";
                picStreamField = "custInfo_AGENT_PIC_STREAM";
                picPathField = "custInfo_AGENT_PIC_PATH";
                blackTypeCode = "142";
                serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
                clazz = "com.asiainfo.veris.crm.iorder.web.person.personview.simcardmgr.RemoteCard";
                $.MBOP.shotImageAgent = "1";
                break;
            case "FORM": // 过户原客户
                custName = $("#FORM_CUST_NAME").val();
                psptId = $("#FORM_PSPT_ID").val();
                psptType = $("#FORM_PSPT_TYPE_CODE").val();
                front = $("#FORM_FRONTBASE64").val();
                picIdField = "FORM_PIC_ID";
                picStreamField = "FORM_PIC_STREAM";
                picPathField = "FORM_PIC_PATH";
                break;
            case "custInfo_USE_PIC_ID_494": // 单位证件激活
                custName = $("#custInfo_USE").val();
                psptId = $("#custInfo_USE_PSPT_ID2").val();
                psptType = $("#custInfo_USE_PSPT_TYPE_CODE").val();
                front = $("#custInfo_USE_FRONTBASE64").val();
                picIdField = "custInfo_USE_PIC_ID";
                picStreamField = "custInfo_PIC_STREAM";
                picPathField = "custInfo_USE_PIC_PATH";
                break;
            default:
                MessageBox.alert("警告提示", "该功能未在移动端适配！");
        }
        $.MBOP.shotImageTag = "";

        $("#" + picIdField).val(picPath);
        $("#" + picStreamField).val(picStream);
        $("#" + picPathField).val(picPath);
        var param = "&BLACK_TRADE_TYPE=" + blackTypeCode
            + "&CERT_ID=" + psptId + "&CERT_NAME=" + custName
            + "&CERT_TYPE=" + psptType + "&PIC_STREAM=" + escape(encodeURIComponent(picStream))
            + "&FRONTBASE64=" + escape(encodeURIComponent(front))
            + "&SERIAL_NUMBER=" + serialNumber;
        /*alert("路径：" + picPath + "\n业务类型：" + blackTypeCode
            + "\n证件号码：" + psptId + "\n客户姓名：" + custName
            + "\n证件类型：" + psptType + "\n手机号码：" + serialNumber
            + "\n监听类：" + clazz);
        alert("拍摄流：" + picStream.substring(0,40));
        alert("身份证人脸流：" + front.substring(0,40));*/
        $.beginPageLoading("正在进行人像比对。。。");
        $.httphandler.post(clazz, "cmpPicInfo", param,
            function (ajaxData) {
                $.endPageLoading();
                if (ajaxData && "0" === ajaxData.get("X_RESULTCODE")) {
                    $.MBOP.shotImageSuccess = "1";
                    $.MBOP.shotImagePath = picPath;
                    MessageBox.success("成功提示", "人像比对成功！");
                    return true;
                } else if (ajaxData && "1" === ajaxData.get("X_RESULTCODE")) {
                    $("#" + picIdField).val("ERROR");
                    $("#" + picStreamField).val(ajaxData.get("X_RESULTINFO"));
                    MessageBox.error("告警提示", ajaxData.get("X_RESULTINFO"));
                    return false;
                }
                else if (ajaxData && ajaxData.get("X_RESULTCODE") == "3") {
                    MessageBox.confirm("提示", "该身份证在公安部人像库未留存头像，请现场进行人工核验！", function (btn) {
                        if (btn == 'cancel') {
                            $.cssubmit.closeMessage(true);
                        }
                    }, {'ok': "核验通过", 'cancel': '核验不通过'});
                }
                // alert("............." + ajaxData);
            }, function () {
                $.endPageLoading();
                $("#" + picIdField).val("ERROR");
                $("#" + picStreamField).val("人像比对失败，请重新拍摄！");
                MessageBox.error("告警提示", "人像比对失败，请重新拍摄！");
            });
    } else {
        MessageBox.error("告警提示", "拍摄失败，请重新拍摄！");
    }
}