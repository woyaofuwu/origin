if (typeof(PlatOrderAdd) == "undefined") {
    window["PlatOrderAdd"] = function () {
    };
    var platOrderAdd = new PlatOrderAdd();
}
(function () {
    $.extend(PlatOrderAdd.prototype, {
        isShowTwoPop: false,
        isRefreshCancelAndSwitch: true,
        //搜索
        renderComponent: function () {
            // $("#platSearch").change(function (e, text, val) {
            //     platOrderAdd.optionEnterAction(val, text);
            // });
        },
        optionEnterAction: function (serviceId, serviceName) {
            //禁用搜索按钮
            var disableSearch = $("#searchBtn").attr("isdisable");
            if ("true" == disableSearch) {
                MessageBox.alert("提示", "搜索功能已经被禁用");
                return;
            }
            platOrderAdd.setPlatSvc();
        },
        setPlatSvc: function () {
            var cond = $("#platSearch").val();
            var data = '&COND=' + cond;
            // $.beginLoading("正在载入数据...", "platAddSearchList", {full:true});
            $.ajax.submit(null, "setPlatSvc", data, "searchResultList,searchResultDetail", platOrderAdd.afterSearch,function(error_code,error_info){
                MessageBox.alert(error_info);
            },{async:true});
            // $.endLoading();
        },
        afterSearch: function (data) {
            for(var i=0;i<data.length;i++) {
                var busTypeCode = data.get(i).get("BIZ_TYPE_CODE");
                var busTypeName = $("#PLAT_BUS_TYPE_float").find("li[val="+busTypeCode+"]").find("div").text();
                //draw搜索列表
                var html = [];
                html.push("<li itemIndex="+i+">");
                html.push("<div class='group link' ontap=platOrderAdd.showItem("+i+")>");
                html.push("<div class='content'>");
                html.push("<div class='main'>");
                html.push("<div class='title e_size-s'>"+data.get(i).get("SERVICE_NAME")+"</div>");
                html.push("<div class='content'>厂商："+data.get(i).get("SP_NAME")+"</div>");
                html.push("<div class='content'>编码："+data.get(i).get("SP_CODE")+"</div>");
                html.push("</div>");
                html.push("<div class='side e_red e_size-m'>"+data.get(i).get("PRICE")/1000+"元/月"+"</div>");
                html.push("<div class='more'></div>");
                html.push("</div>");
                html.push(" </div>");

                html.push("<div class='side'><button ontap='userPlatSvcsList.addElement("+data.get(i).get("SERVICE_ID")+")' class='e_button-r e_blue'>订购</button></div>");
                html.push("</li>");
                $("#platAddSearchList").append(html.join(""));
                //搜索详情
                var html2 = [];
                html2.push("<ul itemIndex="+i+">");
                html2.push("        <li>");
                html2.push("        <span class='label'>业务类型：</span>");
                html2.push("    <span class='value'>"+busTypeName+"</span>");
                html2.push("        </li>");
                html2.push("        <li>");
                html2.push("        <span class='label'>业务编码：</span>");
                html2.push("    <span class='value'>"+busTypeCode+"</span>");
                html2.push("        </li>");
                html2.push("        <li>");
                html2.push("        <span class='label'>服务商名称：</span>");
                html2.push("    <span class='value'>"+data.get(i).get("SP_NAME")+"</span>");
                html2.push("        </li>");
                html2.push("        <li>");
                html2.push("        <span class='label'>服务商编码：</span>");
                html2.push("    <span class='value'>"+data.get(i).get("SP_CODE")+"</span>");
                html2.push("        </li>");
                html2.push("        <li>");
                html2.push("        <span class='label'>计费类型：</span>");
                html2.push("    <span class='value'>"+data.get(i).get("BILL_TYPE")+"</span>");
                html2.push("        </li>");
                html2.push("        <li>");
                html2.push("        <span class='label'>价格：</span>");
                html2.push("    <span class='value'>"+data.get(i).get("PRICE")/1000+"元/月</span>");
                html2.push("    </li>");
                html2.push("    </ul>");
                $("#searchResultDetail").append(html2.join(""));
            }
        },
        showItem:function(item) {
           $("#searchResultDetail ul").addClass("e_hide");
           $("#searchResultDetail").find("ul[itemIndex="+item+"]").removeClass("e_hide");
           platOrderAdd.isShowTwoPop=true;
           forwardPopup('serviceAddPopup','UI-detail2');
        },
        checkFocus:function(thisObj) {
            if(platOrderAdd.isShowTwoPop){
                backPopup('serviceAddPopup','platAddPop');
                platOrderAdd.isShowTwoPop=false;
            }
        },
        //热门
        show: function (el, str) {
            var o;
            typeof el == "object" ? o = el : o = platOrderAdd.get(el);
            o.style.display = "";
            if (o.id == 'hideHot') {
                platOrderAdd.showHotArea();
            }
            if (str) {
                o.style.display = str;
            }
        },
        showHotArea: function () {
            $.ajax.submit(null, null, '&TABSET=KEYBUSINESS_AREA', 'platOrderAdd', platOrderAdd.afterHotAction);
        },
        afterHotAction: function (data) {
            var area = $("#keyBusinessArea");
            area.empty();
            var html = [];
            if (data) {
                var length = data.length;
                for (var i = 0; i < length; i++) {
                    var map = data.get(i);
                    html.push('<li class="link" serviceId="' + map.get("SERVICE_ID") + '" ontap="platOrderAdd.addHotAction(this,true)">');
                    html.push('    <div class="main">' + map.get("SERVICE_NAME") + '</div>');
                    html.push('    <div class="side">' + map.get("PRICE") / 1000 + '元/月</div>');
                    html.push('</li>');
                }
                $.insertHtml('beforeend', area, html.join(""));
            }
        },
        addHotAction: function (eventObj, isHot) {
            if (userPlatSvcsList) {
                var serviceId = $(eventObj).attr("serviceId");
                userPlatSvcsList.addElement(serviceId, isHot);
            }
        },
        hide: function (el) {
            var o;
            typeof el == "object" ? o = el : o = platOrderAdd.get(el);
            o.style.display = "none";
        },
        //批量
        toggle: function (els) {
            for (var i = 0; i < arguments.length; i++) {
                var o;
                var el = arguments[i];
                typeof el == "string" ? o = platOrderAdd.get(el) : o = el;
                o.style.display == "none" ? o.style.display = "" : o.style.display = "none";
            }
            ;
        },
        get: function (id) {
            if (document.getElementById(id)) {
                return document.getElementById(id);
            } else if (parent.document.getElementById(id)) {
                return parent.document.getElementById(id);
            } else if (parent.parent.document.getElementById(id)) {
                return parent.parent.document.getElementById(id);
            } else {
                return false;
            }
        },
        // 显示浮动层
        myShowFloatLayerFn :function(o) {
        //判断是否是点击完查询之后
        if($("#USER_ID").val()=='')
        {
            return;
        }
        // 获取 button 位置并设置给 floatLayer
        var left = $(o).offset().left;
        var top = $(o).offset().top;
        var width = o.offsetWidth;
        var height = $(o).height();
        var x = document.body.offsetWidth - left - width;
        $("#myFloatLayer").css("top",top + height);
        $("#myFloatLayer").css("right",x);
        //获取全部取消或者开关数据,并且JS绘制
        if(platOrderAdd.isRefreshCancelAndSwitch) {
        var data="&TABSET=ALL_CANCEL_AREA";
        $.ajax.submit(null,null,data,$("#PLATORDERADD_COMPONENT_ID").val(),platOrderAdd.afterSwitchAllCancel,function(error_code,error_info){
            MessageBox.alert(error_info);
        },{async:false});

        var data="&TABSET=SWITCH_AREA&USER_ID="+$("#USER_ID").val()+"&ROUTE_EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val();
        $.ajax.submit(null,null,data,$("#PLATORDERADD_COMPONENT_ID").val(),platOrderAdd.afterSwitchSwitch);
            platOrderAdd.isRefreshCancelAndSwitch = false;
        }
        // 打开 floatLayer
       toggleFloatLayer('myFloatLayer');
       },

       //ajax回调绘制开关和全退订
       //全退订
       afterSwitchAllCancel: function (data) {
           document.getElementById("CANCEL_PART").innerHTML="";
           if (data) {
               var length = data.length;
               for (var i = 0; i < length; i++) {
                    var html = [];
                    var flag = false;
                    var map = data.get(i);
                    //写特殊情况selected
                    if (map.get("DATA_ID") == "SP") {
                        var spMap = userPlatSvcsList.getAllCancelSpCode();
                        if (spMap.length > 0) {
                            spMap.eachKey(function (key) {
                                platOrderAdd.drawCancel(i+key,spMap.get(key),key,true);
                            });
                        }
                    }

                    else if (map.get("DATA_ID") == "DSMP" || map.get("DATA_ID") == "MUSC") {
                        if (userPlatSvcsList.isExist(map.get("DATA_ID"), "2")) {
                            flag = true;
                        }
                    }
                    else if (userPlatSvcsList.isExist(map.get("DATA_ID"), "1")) {
                        flag = true;
                    }
                    if (flag) {
                        platOrderAdd.drawCancel(i,map.get("DATA_NAME"),map.get("DATA_ID"));
                    }

                }

            }
        },
        //绘制全退
        drawCancel :function (i,name,value,isSp) {
            var html=[];
            html.push('<li>');
            html.push('    <div class="label">' + name + '</div>');
            html.push('    <div class="value">');
            html.push('        <div class="e_switch e_switch-off">');
            html.push('            <div class="e_switchOn"></div>');
            html.push('            <div class="e_switchOff"></div>');
            if(isSp){
            html.push('            <input type="hidden" name="SpServie" id="myCancel_' + i + '" />');
            }
            else{
            html.push('            <input type="hidden" id="myCancel_' + i + '" />');
            }
            html.push('        </div>');
            html.push('    </div>');
            html.push('</li>');
            $("#CANCEL_PART").append(html.join(""));
            window["myCancel_" + i + ""] = new Wade.Switch("myCancel_" + i + "", {
                switchOn: false,
                onValue: "onValue_" + value,
                offValue: "offValue_" + value
            });
            if(isSp){
                    $("#myCancel_" + i + "").change(function () {
                    userPlatSvcsList.addAllCancel(this.value,true);
                });
            }
            else{
                    $("#myCancel_" + i + "").change(function () {
                    userPlatSvcsList.addAllCancel(this.value);
            });
            }
        },
        //开关
        afterSwitchSwitch: function (data) {
            document.getElementById("SWITCH_PART").innerHTML="";
            if (data) {
                var length = data.length;
                for (var i = 0; i < length; i++) {
                    var html = [];
                    var map = data.get(i);
                    platOrderAdd.drawSwitch(i,map.get("DATA_NAME"),map.get("DATA_ID"),map.get("IS_CLOSE") == "false");
                }
            }
        },
        //绘制开关
        drawSwitch :function (i,name,value,isOpen) {
            var defaultStatus;
            var openStatus=false;
            if(isOpen){ 
                defaultStatus="e_switch-on";
                openStatus=true;
            }
            else {
                defaultStatus="e_switch-off";
            }
            var html=[];
            html.push('<li>');
            html.push('    <div class="label">' + name + '</div>');
            html.push('    <div class="value">');
            html.push('        <div class="e_switch '+defaultStatus+'">');
            html.push('            <div class="e_switchOn"></div>');
            html.push('            <div class="e_switchOff"></div>');
            html.push('            <input type="hidden" id="mySwitch_' + i + '" />');
            html.push('        </div>');
            html.push('    </div>');
            html.push('</li>');
            $("#SWITCH_PART").append(html.join(""));
            window["mySwitch_" + i + ""] = new Wade.Switch("mySwitch_" + i + "", {
                switchOn: openStatus,
                onValue: "onValue_" + value,
                offValue: "offValue_" + value
            });
            $("#mySwitch_" + i + "").change(function () {
                userPlatSvcsList.addAllSwitch(this.value,isOpen);
            });
        }
    });
})();

