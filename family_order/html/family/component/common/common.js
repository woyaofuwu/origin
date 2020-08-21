"use strict"
var common = {
    tpl: {},
    setEnv: function (tradeTypeCode, eparchyCode) {
        common.tradeTypeCode = tradeTypeCode;
        common.eparchyCode = eparchyCode;
    },
    createRole: function (RoleChild, param) {
        var tplU = RoleChild.tpl;
        var tpl = null;
        try {
            tpl = common.getTpl(tplU);
        } catch (e) {
        }
        if (tpl == null) {
            return;
        }
        if (param == null) {
            param = {};
        }
        param.tpl = tpl;
        return new RoleChild(param);
    },
    getTpl: function (tplU) {
        if (!(tplU in common.tpl)) {
            $.ajaxRequest({
                url: tplU, // Http 请求地址
                type: "GET", // Http 请求方式
                dataType: "text",// |"json"|"script"|"jsonp"
                async: false, // 是否异步的 XMLHttpRequest 请求，默认为 true,
                timeout: 2000, // 设置超时时间
                success: function (data) {// data返回的数据,当dataType为text时、data为字符串，为json时、data为JSON-Object,为script或jsonp时data参数为undefined
                    common.tpl[tplU] = data;
                },
                error: function (status, errMsg) {
                    alert("加载异常！");
                    return false;
                }
            });
        }
        return common.tpl[tplU];
    }
}
var familyCaller = {
    dynamicParams: null,
    dynamicParamData: $.DataMap(),

    setParam: function (key, value) {
        (this.dynamicParamData).put(key, value);
    },

    addParam: function (paramStr) {
        this.dynamicParams = paramStr;
    },

    clearParam: function () {
        this.dynamicParams = null;
        this.dynamicParamData.clear();
    },

    check: function (busiType, triggerType, checkData, func, errfunc) {
        var checkParam = "&BUSI_TYPE=" + busiType + "&TRIGGER_TYPE=" + triggerType;
        if (checkData && checkData.length > 0) {
            checkData.eachKey(function (key, index, totalcount) {
                checkParam += "&" + key + "=" + checkData.get(key);
            });
        }
        if (this.dynamicParams && (this.dynamicParams).length > 0) {
            checkParam += this.dynamicParams;
        }
        var _self = this;
        if (this.dynamicParamData && this.dynamicParamData.length > 0) {
            this.dynamicParamData.eachKey(function (key, value) {
                checkParam += "&" + key + "=" + value;
            });
        }
        $.beginPageLoading("业务校验。。。");
        hhSubmit(null, constdata.HANDLER, "check", checkParam, function (data) {
            $.endPageLoading();
            _self.clearParam();
            if (data != null && data.length > 0) {
                _self.showRuleResult(data, func, errfunc);
            }
        }, function (errorCode, errorInfo) {
            $.endPageLoading();
            _self.clearParam();
            MessageBox.alert("错误提示", "校验报错！", null, null, errorInfo, errorInfo);
        });
    },

    showRuleResult: function (data, func, errfunc) {
        var errorSet = data.get("RULE_INFO");
        if (errorSet && errorSet.length > 0) {
            var errStr = "";
            for (var i = 0; i < errorSet.length; i++) {
                var item = errorSet.get(i);
                if (item.get("ERROR_INFO") != null && item.get("ERROR_INFO") != '') {
                    if (i != errorSet.length) {
                        errStr += (i + 1) + ":【" + item.get("ERROR_CODE") + "】" + item.get("ERROR_INFO") + ";<br/>";
                    } else {
                        errStr += (i + 1) + ":【" + item.get("ERROR_CODE") + "】" + item.get("ERROR_INFO") + ";";
                    }
                }
            }
            MessageBox.error("错误提示", errStr, function () {
                if (errfunc && typeof (errfunc) == "function")
                    errfunc();
            });
        } else {
            if (func && typeof (func) == "function")
                func();
        }
    },

    filterOffers: function (busiType, roleCode, roleType, transData, condData, func) {
        var transParam = "&BUSI_TYPE=" + busiType + "&ROLE_CODE=" + roleCode + "&ROLE_TYPE=" + roleType;

        if (transData && transData.length > 0) {
            transParam += "&TRANS_DATA=" + encodeURIComponent(transData.toString());
        }
        if (condData && condData.length > 0) {
            transParam += "&COND_DATA=" + encodeURIComponent(condData.toString());
        }

        if (this.dynamicParams && (this.dynamicParams).length > 0) {
            transParam += this.dynamicParams;
        }
        var _self = this;
        if (this.dynamicParamData && this.dynamicParamData.length > 0) {
            this.dynamicParamData.eachKey(function (key, value) {
                transParam += "&" + key + "=" + value;
            });
        }

        $.beginPageLoading("数据过滤。。。");
        hhSubmit(null, constdata.HANDLER, 'filterOffers', transParam, function (data) {
            $.endPageLoading();
            _self.clearParam();
            if (func && typeof (func) == "function")
                func(data);
        }, function (error_code, errorInfo, errorDetail) {
            $.endPageLoading();
            _self.clearParam();
            MessageBox.alert("错误提示", "参数转换报错！", null, null, errorInfo, errorDetail);
        });
    }
}
var Loading = {
    beginBoxLoading: function (e) {
        var ltIE9 = constdata.isLtIE9
        var dom = typeof (e) == "string" ? $(e) : (e.length == 1 ? e : $($.filter(".c_box", e)));
        dom.children().hide();
        var spinner = dom.find("div[name=spinner]");
        if (spinner.length > 0) {
            spinner.show();
        } else {
            var tmp = '<div name="spinner">';
            if (ltIE9) {
                tmp += '<div class="c_msg c_msg-loading">';
                tmp += '<div class="wrapper">';
                tmp += '<div class="emote"></div>';
                tmp += '<div class="info">';
                tmp += '<div class="text">';
                tmp += '<div class="content">加载中。。。</div>';
                tmp += '</div></div></div></div>';
            } else {
                tmp += '<div class="spinner">';
                tmp += ' <div class="rect1"></div>';
                tmp += ' <div class="rect2"></div>';
                tmp += ' <div class="rect3"></div>';
                tmp += ' <div class="rect4"></div>';
                tmp += ' <div class="rect5"></div>';
                tmp += '</div>';
            }
            tmp += '</div>';
            dom.append(tmp);
        }
    },
    endBoxLoading: function (e) {
        var dom = typeof (e) == "string" ? $(e) : (e.length == 1 ? e : $($.filter(".c_box", e)));
        dom.children().show();
        dom.find("div[name=spinner]").hide();
    }
};
