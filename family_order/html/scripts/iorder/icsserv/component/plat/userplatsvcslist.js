if(typeof(UserPlatSvcsList)=="undefined"){window["UserPlatSvcsList"]=function(){};var userPlatSvcsList = new UserPlatSvcsList();}
(function(){
	$.extend(UserPlatSvcsList.prototype,{
		selectedElements: new $.DatasetList(),
		allCancels: new $.DataMap(),
		allSwitch: new $.DataMap(),
		eparchyCode:null,
		operCode: "",
		userId:"",
        opId : "",
        isHot:true,
        delHtml :  "<div class='statu statu-right statu-red'>退订</div>",
        pusHtml :  "<div class='statu statu-right statu-orange'>暂停</div>",
        resHtml :  "<div class='statu statu-right statu-green'>恢复</div>",
        addHtml :  "<div class='statu statu-right statu-blue'>新增</div>",
		renderComponent: function(userId,eparchyCode){
			var data="&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode+"&DISABLE_OPERATION="+$("#disableOperation").val();
			this.eparchyCode = eparchyCode;
			this.userId = userId;
			$.ajax.submit(null,null,data,$("#USERPLATSVCSLIST_COMPONENT_ID").val(),userPlatSvcsList.afterRender,function(error_code,error_info){
                $.endPageLoading();
				MessageBox.alert(error_info);
            },{async:false});
            $.endPageLoading();
		},
		
		afterRender: function(data){
			if(data&&data.length>0){
				userPlatSvcsList.selectedElements = data;
				for(var i=0;i<data.length;i++){
					var isInternetTvSvc=data.get(i).get("INTERNET_TV_SVC");
					//todo 互联网电视不能进行任何操作
					if(isInternetTvSvc=="1"){
                        $("#del"+i).hide();
                        $("#pus"+i).hide();
                        $("#res"+i).hide();
					}
                    //判断是否有暂停操作
                    $.each($("#"+i+"_OPERCODE_float li"),function (name,value) {
                        var operCode = $(this).attr("val");
                        var operValue = $(this).attr("title");
                        if(operCode == "04") {
                            $("#pus"+i).show();
                        }
                        if(operCode == "05") {
                            $("#res"+i).show();
                        }
                        if(operCode == "07") {
                            //样式变化
                            $("#del"+i).show();
                        }
                    });
				}
			}
			else{
				userPlatSvcsList.selectedElements = new $.DatasetList();
			}
		},
        //展示操作浮动层
        toggleOp:function (o) {
            if($(o).find('.op').hasClass('op-show-phone')) {
                $('.op').removeClass('op-show-phone');
            } else {
                $('.op').removeClass('op-show-phone');
                $(o).find('.op').addClass('op-show-phone');
            }
        },
        //点击删除，或者退订
        del: function (eventObj,isNewEle) {
            //判断是否为新增元素
            var itemIndex = $(eventObj).attr("itemIndex");
            if(isNewEle){
                var element = this.selectedElements.get(itemIndex);
                element.put("MODIFY_TAG","exist");
                element.remove("OPER_CODE");
                $(eventObj).parent().parent().parent().addClass("e_hide");
                return;
            }
            //如果为非新增元素
            var thisEle = this.selectedElements.get(itemIndex);
            thisEle.put("OPER_CODE","07");
            thisEle.put("MODIFY_TAG","1");
            thisEle.put("IS_WRITE_ATTR",true);
            //样式变化
            $(eventObj).parents("li").find(".del").hide();
            $(eventObj).parents("li").find(".delRestore").show();
            $(eventObj).parents("li").find(".statu").remove();
            $(eventObj).parents("li").append(userPlatSvcsList.delHtml);
        },
        // 暂停平台业务
        pus:function (eventObj){
            var itemIndex = $(eventObj).attr("itemIndex");
            var tempEle = this.selectedElements.get(itemIndex);
            tempEle.put("MODIFY_TAG","2");
            tempEle.put("OPER_CODE","04");
            tempEle.put("IS_WRITE_ATTR",true);
            //样式变化
            $(eventObj).parents("li").find(".pause").hide();
            $(eventObj).parents("li").find(".pauseRestore").show();
            $(eventObj).parents("li").find(".statu").remove();
            $(eventObj).parents("li").append(userPlatSvcsList.pusHtml);
        },
        // 恢复平台业务
        res:function (eventObj){
            var itemIndex = $(eventObj).attr("itemIndex");
            var tempEle = this.selectedElements.get(itemIndex);
            tempEle.put("MODIFY_TAG","2");
            tempEle.put("OPER_CODE","05");
            //样式变化
            $(eventObj).parents("li").find(".restore").hide();
            $(eventObj).parents("li").find(".resRestore").show();
            $(eventObj).parents("li").find(".statu").remove();
            $(eventObj).parents("li").append(userPlatSvcsList.resHtml);
        },
        //全部还原
        restore:function (eventObj){
            var itemIndex = $(eventObj).attr("itemIndex");
            //判断是否有暂停操作
            $.each($("#"+itemIndex+"_OPERCODE_float li"),function (name,value) {
                var operCode = $(this).attr("val");
                var operValue = $(this).attr("title");
                if(operCode == "04") {
                    $(eventObj).parents("li").find(".pause").show();
                    $(eventObj).parents("li").find(".pauseRestore").hide();
                }
                if(operCode == "05") {
                    $(eventObj).parents("li").find(".restore").show();
                    $(eventObj).parents("li").find(".resRestore").hide();
                }
                if(operCode == "07") {
                    //样式变化
                    $(eventObj).parents("li").find(".del").show();
                    $(eventObj).parents("li").find(".delRestore").hide();
                }
            });
            var element  = this.selectedElements.get(itemIndex);
            element.put("MODIFY_TAG","exist");
            element.remove("OPER_CODE");
            $(eventObj).parents("li").find(".statu").remove();
        },
        //设置属性悬浮位置
        setFloatLayer :function (o, id, align, width) {
            var floatEl = $("#" + id);
            var bottom = $(window).height()-$(o).offset().top-$(o).height();
            if($(o).parent().parent().parent().attr("serviceName") == 'WLAN' && bottom<219 && $(o).offset().top>219) {
                floatEl.css("top",$(o).offset().top-219 + "px");
            }
            else if($(o).parent().parent().parent().attr("serviceName") == 'WLAN' && bottom<219 && $(o).offset().top<219){
                $("#SubmitPart").parent().after("<div class=\"c_space-3\"></div><div class=\"c_space-3\"></div><div class=\"c_space-3\"></div><div class=\"c_space-3\"></div><div class=\"c_space-3\"></div>");
                floatEl.css("top",$(o).offset().top + $(o).height() - 1 + "px");
            }
            else {
            floatEl.css("top",$(o).offset().top + $(o).height() - 1 + "px");}
            if(width) {
                if(typeof width == "number") {
                    floatEl.css("width", width + "em");
                } else if(typeof width == "string") {
                    floatEl.css("width", $("#" + width).width() + "px");
                }
            } else {
                floatEl.css("width",$(o).width() + "px");
            }
            if(!align || align == "left") {
                floatEl.css("left",$(o).offset().left + "px");
            } else {
                floatEl.css("left",$(o).offset().left - floatEl.width() + $(o).width() + "px");
            }
            while ( o.tagName != "BODY") {
                if($(o).hasClass("c_scroll")) {
                    $(o).bind("scroll",function(){
                        hideFloatLayer(id);
                    })
                }
                o = o.parentNode;
            }
        },
        //绘制悬浮moreFn的selected
        drawMoreFnFloat:function (eventObj) {
            document.getElementById("moreFnUl").innerHTML="";
            var index = $(eventObj).attr("itemIndex");
            var serviceName=$(eventObj).attr("serviceName");
            var item = "#" + index + "_ATTRPARAM";
            //如果参数存在则展示参数信息
            //先要判断当前元素参数是否存在
            var drawMoreFn = [];
            if ($(item).attr("itemIndex") != undefined) {
                var floatStr = '#' + $(item).attr("itemIndex") + '_OPERCODE_float';
                var eachItems = $(floatStr).find("li");
                var elementId = $(item).attr("elementId");
                var elementType = $(item).attr("elementType");
                var itemIndex = $(item).attr("itemIndex");
                eachItems.each(function (idx, item) {
                    var opCode = $(item).attr("val");
                    var opValue = $(item).attr("title")
                    if (opCode == "03" || opCode == "08" || opCode == "10" || opCode == "11" || opCode == "12" || opCode == "17" || opCode == "18") {
                        drawMoreFn.push('<li class="link" ontap="hideFloatLayer(moreFn); userPlatSvcsList.showMoreFn(this,'+itemIndex+','+elementId+',\''+elementType+'\',\''+opCode+'\',\''+opValue+'\',\''+serviceName+'\')">');
                        drawMoreFn.push('<div class="main">'+opValue+'</div>');
                        drawMoreFn.push('</li>');
                    }
                    else if(opCode !="04"&&opCode !="05"&&opCode !="07"){
                        drawMoreFn.push('<li class="link" ontap="hideFloatLayer(moreFn);userPlatSvcsList.changeOperCode('+itemIndex+',\''+opCode+'\',\''+opValue+'\')">');
                        drawMoreFn.push('<div class="main">'+opValue+'</div>');
                        drawMoreFn.push('</li>');
                    }
                });
                $("#moreFnUl").append(drawMoreFn.join(""));
            }
            else {
                var itemIndex = $(eventObj).attr("itemIndex");
                var floatStr = '#' + itemIndex + '_OPERCODE_float';
                var eachItems = $(floatStr).find("li");
                eachItems.each(function (idx, item) {
                    var opCode = $(item).attr("val");
                    var opValue = $(item).attr("title")
                    if(opCode !="04"&&opCode !="05"&&opCode !="07"){
                        drawMoreFn.push('<li class="link" ontap="hideFloatLayer(moreFn);userPlatSvcsList.changeOperCode('+itemIndex+',\''+opCode+'\',\''+opValue+'\')">');
                        drawMoreFn.push('<div class="main">'+opValue+'</div>');
                        drawMoreFn.push('</li>');
                    }
                });
                $("#moreFnUl").append(drawMoreFn.join(""));
            }
        },
        //当点击其他没有参数设置时候
        changeOperCode: function(itemIndex,operCode,opValue){
            if(operCode=="GIFT") {
                userPlatSvcsList.changeOrderGiftOper(operCode,itemIndex);
                return;
            }
            var element = this.selectedElements.get(itemIndex);
            element.put("MODIFY_TAG","2");
            element.put("OPER_CODE",operCode);
            element.put("IS_WRITE_ATTR",true);
            MessageBox.alert(opValue+'设置成功！');
            //绘制参数操作
            $("#del"+itemIndex).parents("li").find(".statu").remove();
            $("#del"+itemIndex).parents("li").append("<div class='statu statu-right statu-green'>"+opValue+"</div>");
        },
        //当为密码变更，用户资料变更，套餐订购，套餐变更，套餐退订，预约和预约取消时候显示参数设置界面
        showMoreFn:function (eventObj,itemIndex,elementId,elementType,opCode,opValue,serviceName) {
            $("#SERVICE_NAME_HEAD_POP").text(serviceName);
            var element = this.selectedElements.get(itemIndex);
            element.put("OPER_CODE",opCode);
            userPlatSvcsList.showAttr(itemIndex,elementId,elementType,false,opCode,opValue);
            $("#attrTitle").html(opValue);
            $("#attrPanel").attr("style","display:");
            $("#svcAttrSubmit").removeClass("e_hide");
            $("#giftPanel").attr("style","display:none");
            showPopup('platSvcAttr','elementAttr',true);
        },
        //当为新增，有参数情况
        showMoreFnNew:function (eventObj) {
            var   itemIndex = $(eventObj).attr("itemIndex");
            var   elementId = $(eventObj).attr("elementId");
            var   elementType = $(eventObj).attr("elementType");
            var   opCode = "06";
            var   opValue = "订购";
            var   serviceName = $(eventObj).attr("serviceName");
            $("#SERVICE_NAME_HEAD_POP").text(serviceName);
            var element = this.selectedElements.get(itemIndex);
            element.put("OPER_CODE",opCode);
            userPlatSvcsList.showAttr(itemIndex,elementId,elementType,false,opCode,opValue);
            $("#attrTitle").html(opValue);
            $("#attrPanel").attr("style","display:");
            $("#svcAttrSubmit").removeClass("e_hide");
            $("#giftPanel").attr("style","display:none");
            showPopup('platSvcAttr','elementAttr',true);
        },
        //当为参数显示的时候展示
        showIsView:function (eventObj) {
             var itemIndex = $(eventObj).attr("itemIndex");
             var elementId = $(eventObj).attr("elementId");
             var elementType = $(eventObj).attr("elementType");
             var serviceName = $(eventObj).attr("serviceName");
            var element = this.selectedElements.get(itemIndex);
            userPlatSvcsList.showAttr(itemIndex,elementId,elementType,true);
            $("#attrTitle").html(serviceName);
            $("#attrPanel").attr("style","display:");
            $("#svcAttrSubmit").addClass("e_hide");
            $("#giftPanel").attr("style","display:none");
            showPopup('platSvcAttr','elementAttr',true);
        },
        //添加新的元素
		addElement: function(serviceId,isHot){
            if(isHot) {
                userPlatSvcsList.isHot = true;
            }
            else {
                userPlatSvcsList.isHot = false;
            }
            if (serviceId == '98001901' || serviceId == '80002009' || serviceId == '55687442'
                || serviceId == '55627768'){
                MessageBox.alert("提示","此用户可办理相应福包业务，可通过产品变更界面进行办理。");
            }
            var str = userPlatSvcsList.findElement(serviceId);
            if(str == "true"){
                if (serviceId != '55588338' && serviceId != '55588339' && serviceId != '55588340'){
                    MessageBox.alert("用户已经存在该服务，不能重复订购");
                    return;
                }
            }
            else if(str == "false") {
                var data = "&SERVICE_ID="+serviceId+"&IS_ELEMENT=true&USER_ID="+this.userId+"&ROUTE_EPARCHY_CODE="+this.eparchyCode;
                $.ajax.submit(null,null,data,$("#USERPLATSVCSLIST_COMPONENT_ID").val(),userPlatSvcsList.afterAddElement);
            }
            else{
                hidePopup('serviceAddPopup');
                return;
            }
		},
        //当点击列表的时候如果有参数则显示参数信息
        showAttr: function(itemIndex,elementId,elementType,isView,operCode){
            var tempElement = userPlatSvcsList.selectedElements.get(itemIndex);
            if(!isView)
            {
                tempElement.put("IS_WRITE_ATTR",false); //需要填写完整ATTR，提交前通过该字段校验
            }
            userPlatSvcsList.operCode = operCode;
            var params = "&ELEMENT_ID="+elementId+"&ELEMENT_TYPE_CODE="+elementType+"&ITEM_INDEX="+itemIndex+"&EPARCHY_CODE="+this.eparchyCode+"&USER_ID="+this.userId;
            if(operCode!=null&&typeof(operCode)!="undefined"){
                params+="&DISPLAY_CONDITION="+operCode;
            }

            $.ajax.submit(null,null,params,$("#ELEMENTATTR_COMPONENT_ID").val(),function(data){
                console.log(data);
                userPlatSvcsList.afterShowAttr(itemIndex,elementId,elementType,isView)},function(error_code,error_info){
                MessageBox.alert(error_info);
            },{async:false});
        },
        //显示参数界面
        afterShowAttr: function(itemIndex,elementId,elementType,isView){
            //设置回填值
            var tempElement = userPlatSvcsList.selectedElements.get(itemIndex);
            var attrs = tempElement.get("ATTR_PARAM");
            var length = attrs.length;

            for(var i=0;i<length;i++){
                var attr = attrs.get(i);
                var attrCode = attr.get("ATTR_CODE");
                var attrValue = attr.get("ATTR_VALUE");
                if(attrValue){
                    $("#"+attrCode).val(attrValue);
                    if($("#"+attrCode).attr("x-wade-uicomponent")=="select") {
                        //设置文本值
                        userPlatSvcsList.addSelectValue("#"+attrCode,attrValue);
                    }
                }
                if(isView){
                    $("#"+attrCode).attr("disabled",true);
                }
            }

            //手机支付特殊处理，默认填写证件号码
            if(elementId == '99081371')
            {
                var psptType =  $("#CARDTYPE").val();
                var psptNO = $("#CARDNUM").val();
                if(psptType == '')
                {
                    $("#CARDNUM").val($("#PSPT_ID").val());
                }
                if(psptNO == '')
                {
                    $("#CARDTYPE").val('00');//默认取身份证
                }
            }
        },
        //参数修改点击确定
        confirmAttr: function(itemIndex){
            var tempElement = this.selectedElements.get(itemIndex);
            if($.validate.verifyAll('elementPanelPlatSvc')){
                var attrs = tempElement.get("ATTR_PARAM");
                var length = attrs.length;
                var isUpdate = false;
                for(var i=0;i<length;i++){
                    var attr = attrs.get(i);
                    var attrCode = attr.get("ATTR_CODE");
                    var attrValue = attr.get("ATTR_VALUE");
                    var newAttrValue = $("#"+attrCode).val();
                    if($.trim(attrValue)!= $.trim(newAttrValue)){
                        attr.put("ATTR_VALUE",newAttrValue);
                        isUpdate = true;
                    }
                }
                if(tempElement.get("SERVICE_ID") == "98001901"){
                    //var tr = $("#userPlatSvcTable").find("tr").eq(itemIndex);
                    //var td = tr.find("td").eq(4);
                    var td = $("#"+itemIndex+"_PRICE");
                    if(newAttrValue == '1'){
                        td.html("0.0元/月");
                    }else if(newAttrValue == '2'){
                        td.html("5.0元/月");
                    }else if(newAttrValue == '3'){
                        td.html("6.0元/月");
                    }
                }
                if(isUpdate){
                    tempElement.put("MODIFY_TAG","2");
                    //绘制参数操作
                    $("#del"+itemIndex).parents("li").find(".statu").remove();
                    $("#del"+itemIndex).parents("li").append("<div class='statu statu-right statu-green'>"+$("#attrTitle").text()+"</div>");
                }
                if(tempElement.get("SERVICE_ID") == '99166951'){//手机支付需要校验客户实名制信息
                    //auth查询结果
                    var isRealName =  $("#IS_REAL_NAME").val();
                    var psptTypeCode = $("#PSPT_TYPE_CODE").val();
                    var custName = $("#CUST_NAME").val();
                    var psptId = $("#PSPT_ID").val();
                    if(isRealName != '1'){
                        MessageBox.alert("提示","非实名制用户不能办理手机支付业务");
                        return false;
                    }
                    for(var i=0;i<length;i++){
                        var attr = attrs.get(i);
                        var attrCode = attr.get("ATTR_CODE");
                        var attrValue = attr.get("ATTR_VALUE");
                        if($.trim(attrCode)=='101'&&$.trim(attrValue)!= $.trim(custName)){//客户姓名
                            MessageBox.alert("提示","客户姓名与登记信息不符");
                            return false;
                        }
                        if($.trim(attrCode)=='103'&&$.trim(attrValue)!= userPlatSvcsList.changePsptType($.trim(psptTypeCode))){//证件类型
                            MessageBox.alert("提示","证件类型与登记信息不符");
                            return false;
                        }
                        if($.trim(attrCode)=='104'&&$.trim(attrValue)!= $.trim(psptId)){//证件号码
                            MessageBox.alert("提示","证件号码与登记信息不符");
                            return false;
                        }
                    }
                }
                tempElement.put("IS_WRITE_ATTR",true); //已经填写完整ATTR
                hidePopup('platSvcAttr');
            }
        },
        //todo 绘制新元素,赠送操作码绘制
		afterAddElement: function(data){
            debugger;
            if(data) {
                var html = [];
                var itemIndex = userPlatSvcsList.selectedElements.length;
                html.push('<li ontap="userPlatSvcsList.toggleOp(this);" id="'+itemIndex+'_li'+'">');
                html.push('<div class="content">');
                html.push('        <div class="main">');
                html.push('        <div class="title e_size-s">');
                html.push('        <a itemIndex='+itemIndex+' ontap="userPlatSvcsList.showServiceDetail(\''+itemIndex+'\',\'true\')" href="#nogo">');
                html.push('        <span>'+data.get("SERVICE_NAME")+'</span>');
                html.push('        </a>');
                html.push('        </div>');
                html.push('        <div class="content">服务商：');
                html.push('<span >'+data.get("SP_NAME")+'</span>');
                html.push('        </div>');
                html.push('        <div class="content">有效期：');
                if(null != data.get("START_DATE") && null != data.get("END_DATE")){
                html.push('<span >'+data.get("START_DATE")+'～'+data.get("END_DATE")+'</span>');
                }
                html.push('        </div>');
                html.push('        </div>');
                html.push('        <div class="side e_red e_size-m" style="width:6em;">');
                html.push('        <span id="'+itemIndex+'_PRICE">'+data.get("PRICE")+'元/月</span>');
                html.push('        </div>');
                html.push('        </div>');
                html.push('        <div class="op op-float">');
                html.push('        <ul>');
                html.push('        <li itemIndex='+itemIndex+' id=del'+itemIndex+' class="del e_red" ontap="userPlatSvcsList.del(this,true);" >');
                html.push('        <span class="e_ico-delete"></span> 删除');
                html.push('        </li>');
                if (!userPlatSvcsList.isHot) {
                    if (data.get('SUPPORT_OPERS').toString().indexOf("GIFT")>-1) {
                        html.push('<li x-wade-float="moreFn" elementType="Z" elementId=\"'+data.get("SERVICE_ID")+'\" itemIndex=\"' + itemIndex + '\" serviceName=\"' + data.get("SERVICE_NAME") + '\" ontap="userPlatSvcsList.setFloatLayer(this,\'moreFn\',\'auto\');userPlatSvcsList.drawMoreGift(this);toggleFloatLayer(\'moreFn\',\'block\');"><span class="e_ico-unfold e_size-s"></span> 更多操作</li>');
                    }
                    if(data.get("ATTR_PARAM")&&data.get("ATTR_PARAM").length>0){
                        html.push('<li x-wade-float="moreFn" elementType="Z" elementId=\"'+data.get("SERVICE_ID")+'\" itemIndex=\"' + itemIndex + '\" serviceName=\"' + data.get("SERVICE_NAME") + '\" ontap="userPlatSvcsList.setFloatLayer(this,\'moreFn\',\'auto\');userPlatSvcsList.showMoreFnNew(this);toggleFloatLayer(\'moreFn\',\'block\');"><span class="e_ico-unfold e_size-s"></span> 显示参数</li>');
                    }
                }
                html.push('    </ul>');
                html.push('    </div>');
                html.push('    </li>');
                //暂时存放data数据
                html.push('<span class="e_hide" id="'+itemIndex+'_userPlatSvcValue'+'">'+data+'</span>');
                $("#servicesUl").prepend(html.join("")+"\r\n");
                //绘制操作码
                var afterBody = [];
                afterBody.push('<div id="'+itemIndex+'_OPERCODE_float" class="c_float" style="left: 0px; top: -2px; width: 0px;">');
                afterBody.push('<div class="bg"></div><div class="content" style="height: 36px;">');
                afterBody.push('<div class="c_scrollContent"><div class="c_list c_list-pc-s c_list-phone-line ">');
                afterBody.push('<ul>');
                var supportOpers = data.get('SUPPORT_OPERS');
                for(var i=0;i<supportOpers.length;i++){
                    var oper = supportOpers.get(i);
                    afterBody.push('<li class="link" idx="'+i+'" title="'+oper.get("OPER_NAME")+'" val="'+oper.get("OPER_CODE")+'">');
                    afterBody.push('<div class="main">');
                    afterBody.push(oper.get("OPER_NAME"));
                    afterBody.push('</div>');
                    afterBody.push('</li>');
                }
                afterBody.push('</ul>');
                afterBody.push('</div>');
                afterBody.push('</div>');
                afterBody.push('</div>');
                afterBody.push('</div>');
                $("body").append(afterBody.join(""));
                var map = new $.DataMap();
                map.put("SERVICE_ID",data.get("SERVICE_ID"));
                map.put("BIZ_STATE_CODE","A");
                map.put("MODIFY_TAG","0");
                map.put("OPER_CODE","06");
                if(data.get("ATTR_PARAM")) {
                    if (userPlatSvcsList.isHot) {
                        //固定特级会员属性
                        var mapattr = new $.DataMap();
                        var attr = new $.DatasetList();
                        mapattr.put("ATTR_VALUE", "3");
                        mapattr.put("ATTR_CODE", "302");
                        attr.add(mapattr)
                        map.put("ATTR_PARAM", attr);
                    }
                    else {
                        map.put("ATTR_PARAM", data.get("ATTR_PARAM"));
                    }
                }
                userPlatSvcsList.selectedElements.add(map);
                //插入新增标识(并且默认订购)
                hidePopup('serviceAddPopup');
                $("#del"+itemIndex).parents("li").append(userPlatSvcsList.addHtml);
            };
		},
        //清理
        clearCache: function(data){
            userPlatSvcsList.selectedElements.clear();
            userPlatSvcsList.allCancels.clear();
            userPlatSvcsList.allSwitch.clear();
            userPlatSvcsList.eparchyCode = null;
            userPlatSvcsList.operCode = "";
            userPlatSvcsList.userId = "";
        },
        //寻找元素
        findElement: function(serviceId){
            if(this.selectedElements!=null){
                var size = this.selectedElements.length;
                for(var i=0;i<size;i++){
                    var data = this.selectedElements.get(i);
                    if(data.get("SERVICE_ID")==serviceId){
                        //如果该元素有e_hide样式，则打开样式
                        if($("#"+i+"_li").hasClass("e_hide")) {
                            $("#"+i+"_li").removeClass("e_hide");
                            var thisEle = this.selectedElements.get(i);
                            thisEle.put("OPER_CODE","06");
                            thisEle.put("MODIFY_TAG","0");
                            thisEle.put("IS_WRITE_ATTR",true);
                            return "hasAdd";
                        }
                        return "true";
                    }
                }
            }
            return "false";
        },
        //判断元素是否存在
        isExist: function(temp,flag){
            var size = this.selectedElements.length;
            for(var i=0;i<size;i++){
                var element = this.selectedElements.get(i);
                if(flag=="1"&&element.get("BIZ_TYPE_CODE")==temp){
                    return true;
                }
                else if(flag=="2"&&element.get("ORG_DOMAIN")==temp){
                    return true;
                }
            }
            return false;
        },
        changePsptType: function(psptTypeCode){
            var result;
            if(psptTypeCode=="0"||psptTypeCode=="1"){//本外地身份证
                result="00";
//			}else if(psptTypeCode=="2"){//VIP卡
//				result="01";
            }else if(psptTypeCode=="2"){//户口本
                result="02";
            }else if(psptTypeCode=="3"){//军人证
                result="03";
//			}else if(psptTypeCode=="2"){//警察证
//				result="04";
            }else if(psptTypeCode=="O"){//港澳居民往来内地通行证
                result="05";
            }else if(psptTypeCode=="N"){//台湾居民来往大陆通行证
                result="06";
            }else if(psptTypeCode=="A"){//护照
                result="07";
            }else if(psptTypeCode=="E"){//单位营业执照（组织机构代码证）副本原件
                result="08";
//			}else if(psptTypeCode=="E"){//加盖公章的营业执照（组织机构代码证）复印件
//				result="09";
            }else{//其他
                result="99";
            }
            return result;
        },
        //添加全退订
		addAllCancel: function(ObjValue,isSp){
            var actValue = ObjValue.substr(ObjValue.indexOf("_")+1);
			if(ObjValue.indexOf("onValue")>-1){
				if(isSp){
					//关闭其他互斥的开关
                    $.each($("#CANCEL_PART").find("input[name=SpServie]"),function (name,value) {
                        if($(this).val() != ObjValue) {
                            $(this).parent().attr("class","e_switch e_switch-off");
                        }
                    })
                    this.allCancels.put("SP",actValue);
                }
				else{
					this.allCancels.put(actValue,actValue);
				}
			}
			else{
				this.allCancels.removeKey(actValue);
			}
		},
        //添加开关
		addAllSwitch: function(value,isOpen){
            var actValue = value.substr(value.indexOf("_")+1);
			var temp = new $.DataMap();
			var changeStatus = value.indexOf("onValue")>-1;
			if(changeStatus == isOpen) {
                this.allSwitch.removeKey(actValue);
            }
            else{
                temp.put("SERVICE_ID",actValue);
                if(isOpen){
                    temp.put("OPER_CODE","91");
                }
                else{
                    temp.put("OPER_CODE","90");
                }
                this.allSwitch.put(actValue,temp);
            }
		},
        //sp特殊处理
		getAllCancelSpCode: function(){
			var result = new $.DataMap();
			var size = this.selectedElements.length;
			for(var i=0;i<size;i++){
				var element = this.selectedElements.get(i);
				if(element.get("SP_CODE")&&element.get("ORG_DOMAIN")=="DSMP"){
					result.put(element.get("SP_CODE"),element.get("SP_NAME"));
				}
			}
			return result;
		},
        showServiceDetail: function(index,data) {
			var item = "#"+index+"_ATTRPARAM";
			//展示基本信息
            var itemIdex = "#"+index;
            var prevValue = $.trim(($(itemIdex+'_userPlatSvcValue').text()).toString());
            var userPlatSvc = $.parseJSON(prevValue.toString());
            if(data) {
                $("#SERVICE_STATUS_POP").text(userPlatSvc.BIZ_STATE);
                $("#BUS_TYPE_POP").text(userPlatSvc.BIZ_TYPE_NAME);
                $("#SPBIZ_BILL_TYPE_POP").text(userPlatSvc.BILL_TYPE);
                $("#IN_CARD_NO_POP").text('');
			}
			else {
			     var busiType = $(itemIdex+'_BUS_TYPE').val();
                 var statu    = $(itemIdex+'_SERVICE_STATUS').val();
                 var billType = $(itemIdex+'_SPBIZ_BILL_TYPE').val();
                 $("#SERVICE_STATUS_POP").text(statu);
                 $("#BUS_TYPE_POP").text(busiType);
                 $("#SPBIZ_BILL_TYPE_POP").text(billType);
                 $("#IN_CARD_NO_POP").text(userPlatSvc.IN_CARD_NO);
            }
            $("#SERVICE_NAME_HEAD_POP").text(userPlatSvc.SERVICE_NAME);
            var startDate = userPlatSvc.START_DATE;
            var endDate = userPlatSvc.END_DATE;
            $("#START_DATE_POP").text(new Date(Date.parse(startDate.replace(/-/g,"/"))).format('yyyy-MM-dd'));
            $("#END_DATE_POP").text(new Date(Date.parse(startDate.replace(/-/g,"/"))).format('yyyy-MM-dd'));
            $("#BIZ_CODE_POP").text(userPlatSvc.BIZ_CODE);
            $("#SP_NAME_POP").text(userPlatSvc.SP_NAME);
            $("#SP_CODE_POP").text(userPlatSvc.SP_CODE);
            $("#PRICE_POP").text(userPlatSvc.PRICE);
            showPopup('serviceDetailPopup','UI-detail',true);
		},
        //提交时候调用
        getOperElements: function(eventObj){
            var length = this.selectedElements.length;
            var submitElements = new $.DatasetList();
            for(var i=0;i<length;i++){
                var element = this.selectedElements.get(i);
                if(element.get("MODIFY_TAG")=="0"||element.get("MODIFY_TAG")=="1"||element.get("MODIFY_TAG")=="2"){
                    if(element.get("OPER_CODE") == "06"){
                        element.removeKey("GIFT_SERIAL_NUMBER");
                        element.removeKey("GIFT_START_DATE");
                        element.removeKey("GIFT_END_DATE");
                    }
                    element.removeKey("ORG_DOMAIN");
                    element.removeKey("SP_CODE");
                    element.removeKey("SP_NAME");
                    //element.removeKey("BIZ_TYPE_CODE");
                    submitElements.add(element);
                }
            }
            return submitElements;
        },
        //单元素为新增，时候的更多选项
        drawMoreGift:function (eventObj) {
            document.getElementById("moreFnUl").innerHTML="";
            var itemIndex = $(eventObj).attr("itemIndex");
            var serviceName=$(eventObj).attr("serviceName");
            var elementId = $(eventObj).attr("elementId");
            var elementType = $(eventObj).attr("elementType");
            var drawMoreFn = [];
            //如果参数存在则展示参数信息
            //先要判断当前元素参数是否存在
            var floatStr = '#' + itemIndex + '_OPERCODE_float';
            var eachItems = $(floatStr).find("li");
            eachItems.each(function (idx, item) {
                var opCode = $(item).attr("val");
                var opValue = $(item).attr("title");
                if(opCode=="GIFT"){
                drawMoreFn.push('<li class="link" ontap="hideFloatLayer(\'moreFn\');userPlatSvcsList.changeOperCode('+itemIndex+',\''+opCode+'\',\''+opValue+'\')">');
                drawMoreFn.push('<div class="main">'+opValue+'</div>');
                drawMoreFn.push('</li>');
                }
                // if(opCode=="06"){
                //     drawMoreFn.push('<li class="link" ontap="hideFloatLayer(\'moreFn\');userPlatSvcsList.showMoreFn(this,'+itemIndex+','+elementId+',\''+elementType+'\',\''+opCode+'\',\''+opValue+'\',\''+serviceName+'\')">');
                //     drawMoreFn.push('<div class="main">'+opValue+'</div>');
                //     drawMoreFn.push('</li>');
                // }
            });
            $("#moreFnUl").append(drawMoreFn.join(""));
        },
        //当为GIFT
        changeOrderGiftOper: function(oper,itemIndex){
            var element = this.selectedElements.get(itemIndex);
            element.put("OPER_CODE",oper);
            var giftSerialNumber = element.get("GIFT_SERIAL_NUMBER");
            var giftStartDate = element.get("GIFT_START_DATE");
            var giftEndDate = element.get("GIFT_END_DATE");
            if(giftSerialNumber!="undefined"&&giftSerialNumber){
                $("#GIFT_SERIAL_NUMBER").val(giftSerialNumber);
            }
            else{
                $("#GIFT_SERIAL_NUMBER").val("");
            }
            $("#GIFT_START_DATE").val(giftStartDate);
            if(giftEndDate!="undefined"&&giftEndDate){
                $("#GIFT_END_DATE").val(giftEndDate);
            }
            else{
                $("#GIFT_END_DATE").val("");
            }
            $("#GIFT_ITEM_INDEX").val(itemIndex);
            //弹出GIFT框
            $("#attrPanel").attr("style","display:none");
            $("#giftPanel").attr("style","display:");
            showPopup('platSvcAttr','elementAttr',true);
        },
        //参数确定
        confirmGift:function(){
            if($.validate.verifyAll('giftPanel')){
                var giftSerialNumber = $("#GIFT_SERIAL_NUMBER").val();
                var serialNumber = $("#SERIAL_NUMBER").val();

                if($.trim(giftSerialNumber) == '')
                {
                    MessageBox.alert("提示","赠送号码不能为空");
                    return false;
                }

                if(giftSerialNumber == serialNumber)
                {
                    MessageBox.alert("提示","赠送号码不能是本人的号码");
                    return false;
                }

                var giftStartDate = $("#GIFT_START_DATE").val();
                var giftEndDate = $("#GIFT_END_DATE").val();
                var giftItemIndex = $("#GIFT_ITEM_INDEX").val();
                if(giftEndDate<=giftStartDate){
                    MessageBox.alert("提示","赠送结束时间不能小于赠送开始时间");
                    return false;
                }
                var itemIndex = $("#GIFT_ITEM_INDEX").val();
                var element = this.selectedElements.get(itemIndex);
                element.put("GIFT_SERIAL_NUMBER",giftSerialNumber);
                element.put("GIFT_START_DATE",giftStartDate);
                element.put("GIFT_END_DATE",giftEndDate);
                MessageBox.alert("赠送设置成功！");
                hidePopup('platSvcAttr');
            }
        },
        addSelectValue: function (id,value) {
            $(id+"_float").find("li").removeClass("on");
            $(id+"_float").find("li[val="+value+"]").addClass("on");
            var text = $(id+"_float").find("li[val="+value+"]").find("div").text();
            // var val = $(id+"_float").find("li[val="+value+"]").attr("val");
            $(id+ "_span span").text(text);
            // $(id+ "_span input").val(val);
        }

});
})();

if (typeof(SelectedElements) == "undefined") {window["SelectedElements"]=function(){};var selectedElements = new SelectedElements();}
(function(){
	$.extend(SelectedElements.prototype,{
		confirmAttr: function(itemIndex){
			userPlatSvcsList.confirmAttr(itemIndex);
		}
	});
	
	$.extend(SelectedElements.prototype,{
		cancelElementAttr: function(itemIndex){
			$("#elementPanel").css("display","none");
			$("#elementPanelUL").html();
		}
	});
})();

