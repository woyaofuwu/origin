(function ($) {
    $.extend({
        custValidate: {
            psptTypeCodeKey: "PSPT_TYPE_CODE", // 页面中定义的证件类型ID
            psptIdKey:       "PSPT_ID",        // 页面中定义的证件号码ID
            psptAddressKey:  "PSPT_ADDRESS",   // 页面中定义的证件号码ID
            custNameKey:     "CUST_NAME",      // 页面中定义的客户名称ID
            contactNameKey:  "CONTACT_NAME",   // 页面中定义的联系人名称ID
            contactPhoneKey: "CONTACT_PHONE",  // 页面中定义的联系电话ID

            // 工具校验方法
            isBlank: function (key) {
                if (!key || key == null || $.trim(key).length == 0)
                    return true;
            },

            isNotBlank: function (key) {
                if (key && key != null && $.trim(key).length > 0)
                    return true;
            },

            isChinese: function (val) {
                var model = /^[\u4E00-\u9FA5]+$/;
                return model.exec(val);
            },

            isNumber: function (val) {
                var model = /^[0-9]+$/;
                return model.exec(val);
            },

            countChinese: function (val) {
                var varLen = val.length;
                var chineseCount = 0;

                for (var i = 0, row = varLen; i < row; i++) {
                    if ($.custValidate.isChinese(val.substr(i, 1))) {
                        chineseCount++;
                    }
                }

                return chineseCount;
            },

            // 判断是否身份证份证号码
            isIdCard: function (psptId) {
                var area = {
                    11: "\u5317\u4eac",
                    12: "\u5929\u6d25",
                    13: "\u6cb3\u5317",
                    14: "\u5c71\u897f",
                    15: "\u5185\u8499\u53e4",
                    21: "\u8fbd\u5b81",
                    22: "\u5409\u6797",
                    23: "\u9ed1\u9f99\u6c5f",
                    31: "\u4e0a\u6d77",
                    32: "\u6c5f\u82cf",
                    33: "\u6d59\u6c5f",
                    34: "\u5b89\u5fbd",
                    35: "\u798f\u5efa",
                    36: "\u6c5f\u897f",
                    37: "\u5c71\u4e1c",
                    41: "\u6cb3\u5357",
                    42: "\u6e56\u5317",
                    43: "\u6e56\u5357",
                    44: "\u5e7f\u4e1c",
                    45: "\u5e7f\u897f",
                    46: "\u6d77\u5357",
                    50: "\u91cd\u5e86",
                    51: "\u56db\u5ddd",
                    52: "\u8d35\u5dde",
                    53: "\u4e91\u5357",
                    54: "\u897f\u85cf",
                    61: "\u9655\u897f",
                    62: "\u7518\u8083",
                    63: "\u9752\u6d77",
                    64: "\u5b81\u590f",
                    65: "\u65b0\u7586",
                    71: "\u53f0\u6e7e",
                    81: "\u9999\u6e2f",
                    82: "\u6fb3\u95e8",
                    91: "\u56fd\u5916"
                };

                psptId = psptId.toUpperCase();

                if (area[parseInt(psptId.substr(0, 2))] == null) {
                    MessageBox.alert("\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u91cc\u5730\u533a\u4e0d\u5bf9\uff01");
                    return false;
                }

                if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(psptId))) {
                    MessageBox.alert("\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u957f\u5ea6\u4e0d\u5bf9\uff0c\u6216\u8005\u53f7\u7801\u4e0d\u7b26\u5408\u89c4\u5b9a\uff01\n15\u4f4d\u53f7\u7801\u5e94\u5168\u4e3a\u6570\u5b57\uff0c18\u4f4d\u53f7\u7801\u672b\u4f4d\u53ef\u4ee5\u4e3a\u6570\u5b57\u6216X\u3002 ");
                    return false;
                }

                // 下面分别分析出生日期和校验位
                var len, re;
                len = psptId.length;
                var arrSplit = "";
                var dtmBirth = "";

                if (len == 15) {
                    re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
                    arrSplit = psptId.match(re);  // 检查生日日期是否正确
                    dtmBirth = new Date("19" + arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
                }

                if (len == 18) {
                    re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
                    arrSplit = psptId.match(re);  // 检查生日日期是否正确
                    dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
                }

                var bGoodDay = (dtmBirth.getFullYear() == Number(arrSplit[2]))
                        && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3]))
                        && (dtmBirth.getDate() == Number(arrSplit[4]));
                if (!bGoodDay) {
                    MessageBox.alert("\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u91cc\u51fa\u751f\u65e5\u671f\u4e0d\u5bf9\uff01");
                    return false;
                }
                return true;
            },

            // 校验联系人名称
            validateContactName: function (contactNameKey, custNameKey) {
                if ($.custValidate.isNotBlank(contactNameKey)) {
                    $.custValidate.contactNameKey = contactNameKey;
                }

                if ($.custValidate.isNotBlank(custNameKey)) {
                    $.custValidate.custNameKey = custNameKey;
                }

                // 客户名称
                var custName = $("#" + $.custValidate.custNameKey).val();

                if ($.custValidate.isBlank(custName)) {
                    if ($.custValidate.isBlank(custName)) {
                        MessageBox.alert("客户名称不能为空!");
                        return false;
                    }
                }

                // 联系人名称
                var contactName = $("#" + $.custValidate.contactNameKey).val();

                if ($.custValidate.isBlank(contactName)) {
                    if ($.custValidate.isBlank(contactName)) {
                        MessageBox.alert("联系人名称不能为空!");
                        $("#" + $.custValidate.contactNameKey).val("");
                        return false;
                    }
                }

                if (contactName != custName) {
                    MessageBox.alert("联系人名称必须与客户名称相同!");
                    $("#" + $.custValidate.contactNameKey).val("");
                    return false;
                }

                return true;
            },

            // 校验联系人电话
            validateContactPhone: function (contactPhoneKey) {
                if ($.custValidate.isNotBlank(contactPhoneKey)) {
                    $.custValidate.contactPhoneKey = contactPhoneKey;
                }

                // 联系人电话
                var contactPhone = $("#" + $.custValidate.contactPhoneKey).val();

                if ($.custValidate.isBlank(contactPhone)) {
                    MessageBox.alert("联系人电话不能为空!");
                    $("#" + $.custValidate.contactPhoneKey).val("");
                    return false;
                }

                if (contactPhone.trim().length < 6 || !$.custValidate.isNumber(contactPhone)) {
                    MessageBox.alert("联系人电话只能为大于6位的数字!");
                    $("#" + $.custValidate.contactPhoneKey).val("");
                    return false;
                }

                return true;
            },

            // 校验证件类型
            validatePsptTypeCode: function (psptTypeCodeKey) {
                if ($.custValidate.isNotBlank(psptTypeCodeKey)) {
                    $.custValidate.psptTypeCodeKey = psptTypeCodeKey;
                }

                // 获取证件类型
                var psptTypeCode = $("#" + $.custValidate.psptTypeCodeKey).val();
                if ($.custValidate.isBlank(psptTypeCode)) {
                    MessageBox.alert("证件类型不能为空!");
                    $("#" + $.custValidate.psptTypeCodeKey).focus();
                    return false;
                }

                return true;
            },

            // 校验客户名称
            validateCustName: function (psptTypeCodeKey, custNameKey) {
                // 校验证件类型
                if (!$.custValidate.validatePsptTypeCode(psptTypeCodeKey))
                    return false;

                // 获取证件类型
                var psptTypeCode = $("#" + $.custValidate.psptTypeCodeKey).val();

                if ($.custValidate.isNotBlank(custNameKey)) {
                    $.custValidate.custNameKey = custNameKey;
                }

                // 获取客户名称
                var custName = $("#" + $.custValidate.custNameKey).val();
                if ($.custValidate.isBlank(custName)) {
                    MessageBox.alert("客户名称不能为空!");
                    return false;
                }

                // 身份证、户口簿、军官证、警官证、港澳居民来往内地通信证、台湾居民来往大陆通信证：客户名称须大于等于两个汉字
                if (/^[0,1,2,C,G,H]*$/.exec(psptTypeCode)) {
                    if (custName.length < 2 || $.custValidate.countChinese(custName) < 2) {
                        MessageBox.alert("客户名称必须大于或等于两个汉字!");
                        $("#" + $.custValidate.custNameKey).val("");
                        return false;
                    }
                }
                // 护照：客户名称须大于三个字符, 不能全为阿拉伯数字
                else if (/^[A]*$/.exec(psptTypeCode)) {
                    if (custName.length < 2 || $.custValidate.isNumber(custName)) {
                        MessageBox.alert("客户名称须大于或等于两个字符,不能全为阿拉伯数字两!");
                        $("#" + $.custValidate.custNameKey).val("");
                        return false;
                    }
                }

                return true;
            },

            // 校验证件号码
            validatePsptId: function (psptTypeCodeKey, psptIdKey) {
                // 校验证件类型
                if (!$.custValidate.validatePsptTypeCode(psptTypeCodeKey))
                    return false;

                // 获取证件类型
                var psptTypeCode = $("#" + $.custValidate.psptTypeCodeKey).val();

                // 初始化
                if ($.custValidate.isNotBlank(psptIdKey)) {
                    $.custValidate.psptIdKey = psptIdKey;
                }

                // 获取证件号码
                var psptId = $("#" + $.custValidate.psptIdKey).val();

                if ($.custValidate.isBlank(psptId)) {
                    MessageBox.alert("证件号码不能为空!");
                    return false;
                }

                if (/^[0,1,2,J]*$/.exec(psptTypeCode)) { // 身份证号码须为15位或18位, 户口簿必须填写身份证号码
                    if (!$.custValidate.isIdCard(psptId)) {
                        $("#" + $.custValidate.psptIdKey).val("");
                        return false;
                    }
                } else if (/^[A,C,G]*$/.exec(psptTypeCode)) { // 军官证、警官证、护照：证件号码须大于等于6位字符
                    if (!$.verifylib.checkMinLength(psptId, 6)) {
                        MessageBox.alert("证件号码须大于或等于6位字符!");
                        $("#" + $.custValidate.psptIdKey).val("");
                        return false;
                    }
                } else if (/^[H]*$/.exec(psptTypeCode)) {
                    if ($.verifylib.checkLength(psptId, 8)) {
                        if (!$.custValidate.isNumber(psptId)) {
                            MessageBox.alert("证件号码为8位时, 必须全为数字!");
                            $("#" + $.custValidate.psptIdKey).val("");
                            return false;
                        }
                    } else if ($.verifylib.checkLength(psptId, 9)) {
                        if (!/^[H,M]*$/.exec(psptId.substr(0, 1)) || !$.custValidate.isNumber(psptId.substr(1))) {
                            MessageBox.alert("证件号码输入有误!");
                            $("#" + $.custValidate.psptIdKey).val("");
                            return false;
                        }
                    } else if ($.verifylib.checkLength(psptId, 11)) {
                        if (/^[H,M]*$/.exec(psptId.substr(0, 1)) || !$.custValidate.isNumber(psptId.substr(1))) {
                            MessageBox.alert("证件号码输入有误!");
                            $("#" + $.custValidate.psptIdKey).val("");
                            return false;
                        }
                    } else {
                        MessageBox.alert("证件号码输入有误!");
                        $("#" + $.custValidate.psptIdKey).val("");
                        return false;
                    }
                }

                return true;
            },

            // 校验证件地址
            validatePsptAddress: function (psptTypeCodeKey, psptAddressKey) {
                // 校验证件类型
                if (!$.custValidate.validatePsptTypeCode(psptTypeCodeKey))
                    return false;

                // 获取证件类型
                var psptTypeCode = $("#" + $.custValidate.psptTypeCodeKey).val();

                // 初始化
                if ($.custValidate.isNotBlank(psptAddressKey)) {
                    $.custValidate.psptAddressKey = psptAddressKey;
                }

                // 获取证件地址
                var psptAddress = $("#" + $.custValidate.psptAddressKey).val();

                if ($.custValidate.isBlank(psptAddress)) {
                    MessageBox.alert("证件地址不能为空!");
                    $("#" + $.custValidate.psptAddressKey).val("");
                    return false;
                }

                // 身份证、户口簿：须大于等于8个汉字
                if (/^[0,1,2,C,G,H,J]*$/.exec(psptTypeCode)) {
                    if ($.custValidate.countChinese(psptAddress) < 8) {
                        MessageBox.alert("证件地址须大于或等于8个汉字!");
                        $("#" + $.custValidate.psptAddressKey).val("");
                        return false;
                    }
                } else if (/^[A]*$/.exec(psptTypeCode)) {
                    if ($.custValidate.countChinese(psptAddress) < 2) {
                        MessageBox.alert("证件地址须大于或等于2个汉字!");
                        $("#" + $.custValidate.psptAddressKey).val("");
                        return false;
                    }
                }

                return true;
            }
        }
    });
})(Wade);