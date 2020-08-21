if (typeof(PlatOrderTab) == "undefined") {
    window["PlatOrderTab"] = function () {
    };
    var platOrderTab = new PlatOrderTab();
}
(function () {
    $.extend(PlatOrderTab.prototype, {
        afterSubmitSerialNumber: function (data) {
            $.beginLoading("页面加载中............")
            //先清理
            userPlatSvcsList.clearCache();
            //绑定搜索的onchange方法
            platOrderAdd.renderComponent();
            var userId = data.get("USER_INFO").get("USER_ID");
            var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
            var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
            var psptId = data.get("CUST_INFO").get("ORIGIN_PSPT_ID");
            $("#USER_ID").val(userId);
            $("#USER_EPARCHY_CODE").val(eparchyCode);
            $("#SERIAL_NUMBER").val(serialNumber);
            $("#PSPT_ID").val(psptId);
            $("#IS_REAL_NAME").val(data.get("CUST_INFO").get("IS_REAL_NAME"));
            $("#CUST_NAME").val(data.get("CUST_INFO").get("ORIGIN_CUST_NAME"));
            $("#PSPT_TYPE_CODE").val(data.get("CUST_INFO").get("PSPT_TYPE_CODE"));

            userPlatSvcsList.renderComponent(userId, eparchyCode);
            
            //清理缓存
            platOrderAdd.isRefreshCancelAndSwitch=true;
            $("#platAddSearchList").html("");
            $("#searchResultDetail").html("");
            $("#platSearch").val("");
            
            parent.$.endPageLoading();
        },

        submitDatas: function () {
            var data = userPlatSvcsList.getOperElements();
            var param = '';
            if (data && data.length > 0) {
                //platOrder.specialDeal(data); //提交前进行的特殊处理
                param += "&SELECTED_ELEMENTS=" + data.toString();
            }
            if (userPlatSvcsList.allCancels && userPlatSvcsList.allCancels.length > 0) {
                param += "&ALL_CANCEL=" + userPlatSvcsList.allCancels.toString();
            }
            if (userPlatSvcsList.allSwitch && userPlatSvcsList.allSwitch.length > 0) {
                var switches = new $.DatasetList();
                userPlatSvcsList.allSwitch.eachKey(function (key) {
                    var temp = userPlatSvcsList.allSwitch.get(key);
                    switches.add(temp);
                });
                param += "&ALL_SWITCH=" + switches.toString();
            }

            var elements = userPlatSvcsList.selectedElements;
            for (var i = 0; i < elements.length; i++) {
                if (elements.get(i).get("IS_WRITE_ATTR") == false) {
                    MessageBox.alert("提示", "您还没有确定填写的属性值，请点击确认，再提交");
                    return false;
                }
            }
            if (param.length <= 0) {
                MessageBox.alert("提示", "您没有进行任何操作，不能提交");
                return false;
            }
            else {
                // console.log('新平台业务参数:'+param);
                param += "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val();
                // return;
                // console.log('提交参数：'+param);
                // debugger;
                $.cssubmit.addParam(param);
                return true;
            }
        },

        afterSubmit: function (data) {
            $.showSucMessage("业务受理成功", "工单流水号" + data.get("ORDER_ID"));
        },
        //参数属性特殊Js
        changeWlanPackType: function (eventObj) {

            if (userPlatSvcsList.operCode != "" && userPlatSvcsList.operCode != "07") {
                if (eventObj.value == '1') {
                    $("#401_2").attr("disabled", true);
                    $("#401").attr("disabled", null);

                    $("#401_2").val("");

                }
                else if (eventObj.value == '2') {
                    $("#401").attr("disabled", true);
                    $("#401_2").attr("disabled", null);


                    $("#401").val("");
                }
                else {
                    $("#401_2").attr("disabled", true);
                    $("#401").attr("disabled", true);
                }
            }

        },
        //特殊JS
        specialDeal: function (data) {
            //针对WLAN的特殊处理
            for (var i = 0; i < data.length; i++) {
                var element = data.get(i);
                //WLAN
                if (element.get("SERVICE_ID") == "98000201") {
                    var attrs = element.get("ATTR_PARAM");
                    var temAttrs = new $.DatasetList();
                    var attrLength = attrs.length;
                    for (var j = 0; j < attrs.length; j++) {
                        //旧密码不提交
                        if (attrs.get(j).get("ATTR_CODE") != "OLD_PASSWORD") {
                            temAttrs.add(attrs.get(j));
                        }
                    }

                    element.put("ATTR_PARAM", temAttrs);
                }
            }
        },

    });
})();
