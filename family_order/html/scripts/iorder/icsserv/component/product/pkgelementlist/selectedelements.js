(function(){
	$.extend(SelectedElements.prototype,{
		isEffectNow:false,
		userProductId:null,
		nextProductId:null,
		nextProductStartDate:null,
		renderComponent: function(data,routeEparchyCode,asyncFlag){
			$.beginPageLoading("已选区加载中。。。。。");
			
			if (asyncFlag==false || asyncFlag=="false")
			{
				asyncFlag=false;
			}else
			{
				asyncFlag=true;
			}
			
			this.clearAllElementFee();
			data+="&IS_RENDER=true&EPARCHY_CODE="+routeEparchyCode+"&TRADE_TYPE_CODE="+$("#SELECTED_TRADE_TYPE_CODE").val();
			if(typeof(selectedElements.getOtherParam)=="function"){
				data += selectedElements.getOtherParam();
			}
			data+="&ACCT_DAY="+$("#ACCT_DAY").val()+"&FIRST_DATE="+$("#FIRST_DATE").val()+"&NEXT_ACCT_DAY="+$("#NEXT_ACCT_DAY").val()+"&NEXT_FIRST_DATE="+$("#NEXT_FIRST_DATE").val();
			$.ajax.submit(null,null,data,$("#SELECTEDELEMENTS_COMPONENT_ID").val(),selectedElements.initSelectedElements,selectedElements.errProcess,{async:asyncFlag});
		},
		
		clearCache:function(){
			this.isEffectNow = false;
			this.userProductId = null;
			this.nextProductId = null;
			this.nextProductStartDate = null;
			productEnv.eparchyCode = null;
			productEnv.userId = null;
			productEnv.serialNumber = null;
		},
		
		setAcctDayInfo: function(acctDay,firstDate,nextAcctDay,nextFirstDate){
			$("#ACCT_DAY").val(acctDay);
			$("#FIRST_DATE").val(firstDate);
			$("#NEXT_ACCT_DAY").val(nextAcctDay);
			$("#NEXT_FIRST_DATE").val(nextFirstDate);
		},
		
		initInvoice:function(){
			if(typeof(Invoice) != "undefined" && Invoice.init){
				Invoice.init();
			}
		},
		
		initSelectedElements: function(data){
			selectedElements.initInvoice();
			var dataset = null;
			if(typeof(data)=="string"){
				dataset = new $.DatasetList(data);
			}
			else if(typeof(data)=="object"){
				dataset = data;
			}
			else{
				MessageBox.alert("数据格式错误");
				$.endPageLoading();
				return;
			}
			if(dataset&&dataset.length>0){
			    var elements = dataset.get(0).get("SELECTED_ELEMENTS");
			    if(elements&&elements.length>0){
			    	selectedElements.selectedEls = elements;
			    	//费用处理
			    	var length = elements.length;
			    	for(var i=0;i<length;i++){
			    		var element = elements.get(i);
			    		if(element.get("MODIFY_TAG")=="0"){
			    			var feeData = element.get("FEE_DATA");
			    			if(feeData!=null&&typeof(feeData)!="undefined"&&feeData.length>0){
			    				var feeSize = feeData.length;
			    				for(var j=0;j<feeSize;j++){
			    					var fee = feeData.get(j);
			    					$.feeMgr.insertFee(fee);
			    				}
			    			}
			    		}
			    	}
			    }
			    else{
			    	selectedElements.selectedEls = new $.DatasetList();
			    }

				if($("#AFTER_RENDER_ACTION").val()!=""&&$("#AFTER_RENDER_ACTION").val()!="undefined"){
					eval($("#AFTER_RENDER_ACTION").val());
				}

				if($("#ELEMENT_EXTEND_ACTION").val()!="" && $("#ELEMENT_EXTEND_ACTION").val()!="undefined"){
					eval($("#ELEMENT_EXTEND_ACTION").val());
				}
			}
			else{
				selectedElements.selectedEls = new $.DatasetList();
			}
			//$("#elementPanel").css("display","none");
			elementAttr.attrBack();
			if ("110" === $("#SELECTED_TRADE_TYPE_CODE").val()) {
                var staffId = $("#STAFF_ID").val();
                if (!localStorage.getItem(staffId + "_COMP_ELEMENTS")) {
                    $("#openDefaultOp").css("display", "");
                    $("#hideDefaultOp").css("display", "none").trigger("tap");
                } else {
                    if ("show" === localStorage.getItem(staffId + "_COMP_ELEMENTS")) {
                        $("#openDefaultOp").css("display", "none").trigger("tap");
                        $("#hideDefaultOp").css("display", "");
                    } else {
                        $("#openDefaultOp").css("display", "");
                        $("#hideDefaultOp").css("display", "none").trigger("tap");
                    }
                }
            } else {
                $("#hideDefaultOp").trigger("tap");
            }
            // var currentDay = new Date().format('yyyy-MM-dd');
            // $("#BOOKING_DATE").val(currentDay);
			$.endPageLoading();
		},
		
		errProcess:function(errorCode,errorinfo){
			//$("#elementPanel").css("display","none");
			elementAttr.attrBack();
			MessageBox.alert(errorinfo);
			$.endPageLoading();
		},
		
		setEnvProductId:function(userProductId,nextProductId,nextProductStartDate){
			this.userProductId = userProductId;
			this.nextProductId = nextProductId;
			this.nextProductStartDate = nextProductStartDate;
		},
		
		//datas为pkgElement对象的集合
		addElements: function(elementIds){
			//添加到已选区
			$.beginPageLoading("已选区加载中。。。。。");
			var params = "&ELEMENTS="+elementIds.toString()+"&EPARCHY_CODE="+productEnv.eparchyCode;
			if($("#basicStartDateControlId").val()!=""){
				params+="&BASIC_START_DATE="+$("#"+$("#basicStartDateControlId").val()).val();
			}
			if($("#basicCancelDateControlId").val()!=""){
				params+="&BASIC_CANCEL_DATE="+$("#"+$("#basicCancelDateControlId").val()).val();
			}
			if($("#ELEMENT_EXTEND_FIELD").val()!=""){
				params+="&ELEMENT_EXTEND_FIELD="+$("#"+$("#ELEMENT_EXTEND_FIELD").val()).val();
			}
			//add by songxw REQ201710090019  关于《铁通4007业务迁移支撑实施方案》的需求 start
			var packageId = elementIds.get(0).get('PACKAGE_ID');
			var effectNow = true;
			if(packageId=='99830102'){
				effectNow = false;
			}
			if(this.isEffectNow){
				params+="&EFFECT_NOW="+effectNow;
			}
			//add by songxw end
			if(typeof(selectedElements.getOtherParam)=="function"){
				params += selectedElements.getOtherParam();
			}
			params+="&SELECTED_ELEMENTS="+ encodeURIComponent(this.selectedEls.toString()) +"&USER_ID="+productEnv.userId+"&TRADE_TYPE_CODE="+$("#SELECTED_TRADE_TYPE_CODE").val();
			params+="&CALL_SVC="+$("#callAddElementSvc").val();
			if(this.userProductId!=null){
				params+="&USER_PRODUCT_ID="+this.userProductId;
			}
			if(this.nextProductId!=null){
				params+="&NEXT_PRODUCT_ID="+this.nextProductId;
			}
			if(this.nextProductStartDate!=null){
				params+="&NEXT_PRODUCT_START_DATE="+this.nextProductStartDate;
			}
			//add by zhangxing3 for REQ201809300014新增线上无手机宽带开户功能的需求—BOSS新增界面 
			if("680" == $("#SELECTED_TRADE_TYPE_CODE").val())
			{
				params+="&BOOKTAG="+$("#BOOKTAG").val();
			}
			//add by zhangxing3 for REQ201809300014新增线上无手机宽带开户功能的需求—BOSS新增界面 
			params+="&ACCT_DAY="+$("#ACCT_DAY").val()+"&FIRST_DATE="+$("#FIRST_DATE").val()+"&NEXT_ACCT_DAY="+$("#NEXT_ACCT_DAY").val()+"&NEXT_FIRST_DATE="+$("#NEXT_FIRST_DATE").val();
			hhSubmit(null,"com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.pkgelementlist.SelectedElementsHandler","dealElement", params, selectedElements.afterAddElements,selectedElements.errProcess);
		},
		
		afterAddElements:function(data){
			var length = data.length;
			if(length>0){
				var info = data.get(0);
				if(info.get("ERROR_INFO")){
					var result = window.confirm(info.get("ERROR_INFO").replace(/<br>/ig,"\n")+"\n点击“确定”按钮继续本次操作，但请按照提示处理不符合要求的元素\n点击“取消”按钮取消本次操作");
					if(!result){
						$.endPageLoading();
						return;
					}
				}
				for(var i=0;i<length;i++){
					var el = data.get(i);
					if(el.get("ELEMENT_TYPE_CODE")=="D"){
						selectedElements.drawDiscnt(el);
					}
					else if(el.get("ELEMENT_TYPE_CODE")=="S" || el.get("ELEMENT_TYPE_CODE")=="Z"){
						selectedElements.drawSvc(el);
					}
					selectedElements.selectedEls.add(el);
					if(el.get("MODIFY_TAG")=="0"){
		    			var feeData = el.get("FEE_DATA");
		    			if(feeData!=null&&typeof(feeData)!="undefined"&&feeData.length>0){
		    				var feeSize = feeData.length;
		    				for(var j=0;j<feeSize;j++){
		    					var fee = feeData.get(j);
		    					$.feeMgr.insertFee(fee);
		    				}
		    			}
		    			if(el.get("CHOICE_START_DATE")=="true"){
		    				var isConfirm = window.confirm(el.get("ELEMENT_NAME")+"可以选择立即生效，点击“确定”按钮选择立即生效\n点击“取消”按钮采用默认生效方式");
		    				if(isConfirm){
		    					//$("#"+el.get("ITEM_INDEX")+"_START_DATE").val(el.get("EFFECT_NOW_START_DATE").substring(0,10));
		    					selectedElements.setStartDateView(el.get("ITEM_INDEX"),el.get("EFFECT_NOW_START_DATE"));
		    					selectedElements.changeStartDate($("#"+el.get("ITEM_INDEX")+"_START_DATE"));
		    				}
		    			}
		    		}
				}
				$.endPageLoading();
				
				if($("#ELEMENT_EXTEND_ACTION").val()!="" && $("#ELEMENT_EXTEND_ACTION").val()!="undefined"){
					eval($("#ELEMENT_EXTEND_ACTION").val());
				}
			}
			
			/*if(typeof(changeProductExtend) != "undefined" && changeProductExtend.setInvoiceInfo)
			{
				changeProductExtend.setInvoiceInfo();
			}*/
			
			$.endPageLoading();
		},
		
		drawSvc: function(el){
			var drawArea = $("#SelectSvcUl");
			var html=[];
			
			var elementId = el.get("ELEMENT_ID");
			var packageId = el.get("PACKAGE_ID");
			var elConfig = offerList.getElement(packageId,elementId,"S");
			if(!elConfig)
			{
				elConfig = offerList.getElement(packageId,elementId,"Z");
			}
			
			var elForceTag = "";
			var elementName = "";
			var elementType = "";
			var reOrder = "";
			var explain = "";
			if(elConfig!=null&&typeof(elConfig)!="undefined"){
				elForceTag = elConfig.selectFlag;
				elementName = elConfig.elementName;
				elementType = elConfig.elementType;
				reOrder = elConfig.reOrder;
				explain = elConfig.explain;
			}
			else{
				elForceTag = el.get("FORCE_TAG");
				elementName = el.get("ELEMENT_NAME");
				elementType = el.get("ELEMENT_TYPE_CODE");
				reOrder = el.get("REORDER");
				explain = el.get("EXPLAIN");
			}

			var disabled = false;
			var state = el.get("MODIFY_TAG");
			var style = "";
			var itemIndex = selectedElements.selectedEls.length;
			el.put("ITEM_INDEX",itemIndex);
			el.put("ELEMENT_NAME",elementName);
			if(state=="0"){
				style="new";
			}
			
			if(elForceTag == "0"){
				disabled = true;
			} 
			html.push('<li>');
			html.push('<label class="group link"  itemIndex="'+itemIndex+'" elementId="'+elementId+'" element="div" title="'+elementName+'" elementType="'+elementType+'">');
			html.push('<div class="content">');
            html.push('<div class="fn">');
            html.push('<input type="checkbox" name="SELECTED_SVC_CHECKBOX" itemIndex="'+itemIndex+'" value="'+elementId+'" class="e_checkbox" checked="true"'+(disabled==true?'disabled=true':'')+' onclick="selectedElements.checkBoxAction(this)"/>');
            html.push('</div>');
			html.push('<div class="main"><div class="title"  style="height:1.43em;">' + elementName + '</div>');
			html.push('</div>');
			html.push('</div>');
			html.push('</label>');
            if(el.get("ATTR_PARAM")&&el.get("ATTR_PARAM").length>0){
                html.push('<div class="fn fn-orange" id="'+itemIndex+'_ATTRPARAM" name="'+itemIndex+'_ATTRPARAM" x-wade-float="elementAttrFloat" itemIndex="'+itemIndex+'" elementId="'+elementId+'" element="div" title="'+elementName+'" elementType="'+elementType+'" onclick='+"selectedElements.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'));"+'><span class="e_ico-unfold"></span></div>');
            }
			html.push('</li>');
			
			$.insertHtml('afterbegin',drawArea,html.join(""));
			if(reOrder!='R'){
				$("#PELI_"+elementType+"_"+elementId).attr("className","e_dis");
				$("#PELI_"+elementType+"_"+elementId).attr("disabled","true");
			}
		},
		
		drawDiscnt: function(el){
			var drawArea = $("#SelectDiscntUl");
			var html=[];
			
			var elementId = el.get("ELEMENT_ID");
			var packageId = el.get("PACKAGE_ID");
			var elConfig = offerList.getElement(packageId,elementId,"D");
			
			var elForceTag = "";
			var elementName = "";
			var elementType = "";
			var reOrder = "";
			var explain = "";
			if(elConfig!=null&&typeof(elConfig)!="undefined"){
				elForceTag = elConfig.selectFlag;
				elementName = elConfig.elementName;
				elementType = elConfig.elementType;
				reOrder = elConfig.reOrder;
				explain = elConfig.explain;
			}
			else{
				elForceTag = el.get("FORCE_TAG");
				elementName = el.get("ELEMENT_NAME");
				elementType = el.get("ELEMENT_TYPE_CODE");
				reOrder = el.get("REORDER");
				explain = el.get("EXPLAIN");
			}
	
			var disabled = false;
			var state = el.get("MODIFY_TAG");
			var style = "";
			var itemIndex = selectedElements.selectedEls.length;
			el.put("ITEM_INDEX",itemIndex);
			el.put("ELEMENT_NAME",elementName);
			if(state=="0"){
				style="new";
			}
			
			if(elForceTag == "0"){
				disabled = true;
			}
			html.push('<li class="link">');//
			if(el.get("ATTR_PARAM")&&el.get("ATTR_PARAM").length>0){
				showFlg = true;
				html.push('<label class="group link" title="'+elementName+'" element="div"   elementId="'+elementId+'" itemIndex="'+itemIndex+'" elementType="D">');
				html.push('<div class="content">');
                html.push('<div class="fn">');
                html.push('<input type="checkbox" name="SELECTED_DISCNT_CHECKBOX" itemIndex="'+itemIndex+'" value="'+elementId+'" class="e_checkbox" checked="true" '+(disabled==true?'disabled=true':'')+' onclick="selectedElements.checkBoxAction(this)"/>');
                html.push('</div>');
				html.push('<div class="main">');
				html.push('<div class="title">' + elementName + '</div>');
				html.push('<div class="content">');
				html.push('<span itemIndex="'+itemIndex+'" id="'+itemIndex+'_START_DATE" value="'+el.get('START_DATE').substring(0,10)+'">'+el.get('START_DATE').substring(0,10)+'</span>');
				html.push(' ~ ');
				html.push('<span itemIndex="'+itemIndex+'" id="'+itemIndex+'_END_DATE" value="'+el.get('END_DATE').substring(0,10)+'">'+el.get('END_DATE').substring(0,10)+'</span>');
				html.push('</div>');
				html.push('</div>');
				html.push('</div>');
				html.push('</label>');
                html.push('<div class="fn fn-orange" id="'+itemIndex+'_ATTRPARAM" name="'+itemIndex+'_ATTRPARAM" x-wade-float="elementAttrFloat" elementId="'+elementId+'" itemIndex="'+itemIndex+'" elementType="D" onclick='+"selectedElements.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'));"+'><span class="e_ico-unfold"></span></div>');
            }else{
				if(el.get("SELF_END_DATE")=="true" || el.get("CHOICE_START_DATE")=="true" || el.get("CHOICE_END_DATE")=="true"){
					showFlg = true;
					html.push('<label title="'+elementName+'" class="group link" element="div"   elementId="'+elementId+'" itemIndex="'+itemIndex+'" elementType="D" onclick='+"selectedElements.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'))"+'>');
				}else{
					html.push('<label title="'+elementName+'"class="group link">');
				}
				html.push('<div class="content">');
                html.push('<div class="fn">');
                html.push('<input type="checkbox" name="SELECTED_DISCNT_CHECKBOX" itemIndex="'+itemIndex+'" value="'+elementId+'" class="e_checkbox" checked="true" '+(disabled==true?'disabled=true':'')+' onclick="selectedElements.checkBoxAction(this)"/>');
                html.push('</div>');
				html.push('<div class="main">');
				html.push('<div class="title">' + elementName + '</div>');//
				html.push('<div class="content">');
				html.push('<span jwcid="@Insert" itemIndex="'+itemIndex+'" id="'+itemIndex+'_START_DATE" value="'+el.get('START_DATE').substring(0,10)+'">'+el.get('START_DATE').substring(0,10)+'</span>');
				html.push(' ~ ');
				html.push('<span jwcid="@Insert" itemIndex="'+itemIndex+'" id="'+itemIndex+'_END_DATE" value="'+el.get('END_DATE').substring(0,10)+'">'+el.get('END_DATE').substring(0,10)+'</span>');
				html.push('</div>');
				html.push('</div>');
				html.push('</div>');
				html.push('</label>');
			}

			html.push('</li>');
			
			
			
			/**
			html.push('<tr class="'+style+'" title="'+elementName+'">');
			html.push('<td class="e_center"><input type="checkbox" name="SELECTED_DISCNT_CHECKBOX" itemIndex="'+itemIndex+'" value="'+elementId+'" class="e_checkbox" checked="true" '+(disabled==true?'disabled=true':'')+' onclick="selectedElements.checkBoxAction(this)"/></td>');
			html.push('<td class="wrap" packageId="'+packageId+'" onclick="selectedElements.selectElementPackage(this)">');
			if(el.get("ATTR_PARAM")&&el.get("ATTR_PARAM").length>0){
				html.push('<a href="#nogo" id="'+itemIndex+'_ATTRPARAM" name="'+itemIndex+'_ATTRPARAM" elementId="'+elementId+'" itemIndex="'+itemIndex+'" elementType="D" onclick='+"selectedElements.showAttr(this,this.getAttribute('elementId'),this.getAttribute('elementType'))"+' class="select"></a>');
			}
			html.push(elementName+'</td>');
			html.push('<td class="e_center edit">');
			html.push('<span class="e_select"><span><span>');
			html.push('<select id="'+itemIndex+'_START_DATE" '+(el.get("CHOICE_START_DATE")=="true"?"":"disabled='false'")+' itemIndex="'+itemIndex+'" onchange="selectedElements.changeStartDate(this)" style="width:91px">');
			html.push('<option value="'+el.get("START_DATE").substring(0,10)+'">'+el.get("START_DATE").substring(0,10)+'</option>');
			if(el.get("CHOICE_START_DATE")=="true"){
				html.push('<option value="'+el.get("EFFECT_NOW_START_DATE").substring(0,10)+'">'+el.get("EFFECT_NOW_START_DATE").substring(0,10)+'</option>');
			}
			html.push("</select></span></span></span>");
			html.push('</td>');
			html.push('<td class="e_center edit">');
			if(el.get("SELF_END_DATE")=="true"){
				html.push('<span class="e_input"><span>');
				html.push('<input type="text" id="'+itemIndex+'_END_DATE" minName="'+itemIndex+'_START_DATE" itemIndex="'+itemIndex+'" desc="结束日期" time="false" datatype="date" format="yyyy-MM-dd" nullable="no" type="text" value="'+el.get("END_DATE").substring(0,10)+'" onblur="selectedElements.checkDate(this)" style="width:91px"/>');
				html.push('</span></span>');
			}
			else{
				html.push('<span class="e_select"><span><span>');
				html.push('<select id="'+itemIndex+'_END_DATE" disabled="'+(el.get("CHOICE_END_DATE")=="true"?"true":"false")+' itemIndex="'+itemIndex+'" onchange="selectedElements.changeEndDate(this)" style="width:91px">');
				html.push('<option value="'+el.get("END_DATE").substring(0,10)+'">'+el.get("END_DATE").substring(0,10)+'</option>');
				if(el.get("CHOICE_END_DATE")=="true"){
					html.push('<option value="'+el.get("EFFECT_NOW_END_DATE").substring(0,10)+'">'+el.get("EFFECT_NOW_END_DATE").substring(0,10)+'</option>');
				}
				html.push("</select></span></span></span>");
			}
			html.push('</td>');
			html.push('</tr>');**/
			$.insertHtml('afterbegin',drawArea,html.join(""));
			if(reOrder!='R'){
				$("#PELI_"+elementType+"_"+elementId).attr("className","e_dis");
				$("#PELI_"+elementType+"_"+elementId).attr("disabled","true");
			}
		},
		
		checkDate:function(eventObj){
			var obj = $(eventObj);
			var val = obj.val();
			var itemIndex = obj.attr("itemIndex");
			if(!itemIndex){
				itemIndex = $("#"+obj.attr("id")).attr("itemIndex");
			}
			var el = this.selectedEls.get(itemIndex);
			var isCheck = $.verifylib.checkDate(val, "yyyy-MM-dd");
			if(isCheck){
				var startDateObj = $("#"+itemIndex+"_START_DATE");
				if(startDateObj.val()>val){
					MessageBox.alert("结束时间不能小于开始时间");
					obj.val(el.get("END_DATE").substring(0,10));
					return;
				}
				else{
					var isDate = this.isDate(val);
					if(isDate){
						//el.put("END_DATE",val+" 23:59:59");
					}
					else{
						MessageBox.alert("输入的日期不正确");
						obj.val(el.get("END_DATE").substring(0,10));
						return;
					}
				}
			}
			else{
				MessageBox.alert("输入有误，请重新输入");
				obj.val(el.get("END_DATE").substring(0,10));
			}
		},
		
		resetDate:function(element){
			selectedElements.setStartDateView(element.get("ITEM_INDEX"),element.get("START_DATE"));
			selectedElements.setEndDateView(element.get("ITEM_INDEX"),element.get("END_DATE"));
		},
		
		checkBoxAction:function(elCheckBox){
			var itemIndex = $(elCheckBox).attr("itemIndex");
			var el = this.selectedEls.get(itemIndex);
			if(elCheckBox.checked){
			    var obj = $("#"+itemIndex+"_ATTRPARAM")
				if(obj){
					obj.attr("disabled","");
				}
				if(el.get("MODIFY_TAG")=="1"){
					if(el.get("ELEMENT_TYPE_CODE")=="D"){
						//elCheckBox.parentNode.parentNode.className="";
						obj.removeClass("e_delete");
					}
					else if(el.get("ELEMENT_TYPE_CODE")=="S"){
						//elCheckBox.parentNode.parentNode.parentNode.className="";
						obj.removeClass("e_delete");
					}
					if(el.get("MODIFY_ATTR")=="true"){
						el.put("MODIFY_TAG","2");
					}
					else{
						el.put("MODIFY_TAG","exist");
					}

					el.put("START_DATE",el.get("OLD_START_DATE"));
					el.put("END_DATE",el.get("OLD_END_DATE"));
					if(el.get("ELEMENT_TYPE_CODE")=="D"){
						this.resetDate(el);
					}
					return;
				}
				else if(el.get("MODIFY_TAG")=="0_1"){
					el.put("MODIFY_TAG","0");
					var feeData = el.get("FEE_DATA");
	    			if(feeData!=null&&typeof(feeData)!="undefined"&&feeData.length>0){
	    				var feeSize = feeData.length;
	    				for(var j=0;j<feeSize;j++){
	    					var fee = feeData.get(j);
	    					$.feeMgr.insertFee(fee);
	    				}
	    			}
	    		
	    		if($("#ELEMENT_EXTEND_ACTION").val()!="" && $("#ELEMENT_EXTEND_ACTION").val()!="undefined"){
					eval($("#ELEMENT_EXTEND_ACTION").val());
				}
	    			
	    		//海南添加
					/*if(typeof(changeProductExtend) != "undefined" && changeProductExtend.setInvoiceInfo)
					{
						changeProductExtend.setInvoiceInfo();
					}*/
					return;
				}
			}
			else{
				var obj = $("#"+itemIndex+"_ATTRPARAM")
				if(obj){
					obj.attr("disabled",true);
				}	
				if(el.get("MODIFY_TAG")=="exist"||el.get("MODIFY_TAG")=="2"){
					//表示是用户原有的元素
					if(el.get("ELEMENT_TYPE_CODE")=="D"){
						//elCheckBox.parentNode.parentNode.className="e_del";
						obj.addClass("e_delete");
					}
					else if(el.get("ELEMENT_TYPE_CODE")=="S"){
						//elCheckBox.parentNode.parentNode.parentNode.className="e_del";
						obj.addClass("e_delete");
					}
					el.put("MODIFY_TAG","1");//删除
					
					el.put("OLD_START_DATE",el.get("START_DATE"));
					el.put("OLD_END_DATE",el.get("END_DATE"));
				}
				else if(el.get("MODIFY_TAG")=="0"){
					el.put("MODIFY_TAG","0_1");
					var feeData = el.get("FEE_DATA");
	    			if(feeData!=null&&typeof(feeData)!="undefined"&&feeData.length>0){
	    				var feeSize = feeData.length;
	    				for(var j=0;j<feeSize;j++){
	    					var fee = feeData.get(j);
	    					$.feeMgr.deleteFee(fee);
	    				}
	    			}
	    			
	    			//海南添加
					/*if(typeof(changeProductExtend) != "undefined" && changeProductExtend.setInvoiceInfo)
					{
						changeProductExtend.setInvoiceInfo();
					}*/
					if($("#ELEMENT_EXTEND_ACTION").val()!="" && $("#ELEMENT_EXTEND_ACTION").val()!="undefined"){
						eval($("#ELEMENT_EXTEND_ACTION").val());
					}
					return;
				}
			}
			//$("#elementPanel").css("display","none");
			elementAttr.attrBack();
			if(el.get("MODIFY_TAG")=="1"){
				var tempEls = new $.DatasetList();
				tempEls.add(el);
				var params = "&IS_ELEMENT=true&ELEMENTS="+tempEls.toString()+"&EPARCHY_CODE="+productEnv.eparchyCode;
				if($("#basicStartDateControlId").val()!=""){
					params+="&BASIC_START_DATE="+$("#"+$("#basicStartDateControlId").val()).val();
				}
				if($("#basicCancelDateControlId").val()!=""){
					params+="&BASIC_CANCEL_DATE="+$("#"+$("#basicCancelDateControlId").val()).val();
				}
				if(this.isEffectNow){
					params+="&EFFECT_NOW=true";
				}
				if(typeof(selectedElements.getOtherParam)=="function"){
					params += selectedElements.getOtherParam();
				}
				if(this.userProductId!=null){
					params+="&USER_PRODUCT_ID="+this.userProductId;
				}
				if(this.nextProductId!=null){
					params+="&NEXT_PRODUCT_ID="+this.nextProductId;
				}
				if(this.nextProductStartDate!=null){
					params+="&NEXT_PRODUCT_START_DATE="+this.nextProductStartDate;
				}
				params+="&SELECTED_ELEMENTS="+ encodeURIComponent(this.selectedEls.toString())+"&USER_ID="+productEnv.userId+"&CALL_SVC="+$("#callAddElementSvc").val()+"&TRADE_TYPE_CODE="+$("#SELECTED_TRADE_TYPE_CODE").val();
				params+="&ACCT_DAY="+$("#ACCT_DAY").val()+"&FIRST_DATE="+$("#FIRST_DATE").val()+"&NEXT_ACCT_DAY="+$("#NEXT_ACCT_DAY").val()+"&NEXT_FIRST_DATE="+$("#NEXT_FIRST_DATE").val();
				$.beginPageLoading("已选区加载中。。。。。");
				hhSubmit(null,"com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.pkgelementlist.SelectedElementsHandler","dealElement", params, function(data){selectedElements.afterCheckBoxAction(data,elCheckBox)},function(errorCode,errorInfo){selectedElements.errProcessReverse(errorCode,errorInfo,elCheckBox)});
			}
		},
		
		errProcessReverse:function(errorCode,errorInfo,elCheckBox){
			//$("#elementPanel").css("display","none");
			elementAttr.attrBack();
			MessageBox.alert(errorInfo);
			elCheckBox.click();
			$.endPageLoading();
		},
		
		afterCheckBoxAction: function(data,elCheckBox){
			var element = data.get(0);
			if(element.get("ERROR_INFO")){
				var result = window.confirm(element.get("ERROR_INFO").replace(/<br>/ig,"\n")+"\n点击“确定”按钮继续本次操作，但请按照提示处理不符合要求的元素\n点击“取消”按钮取消本次操作");
				if(!result){
					elCheckBox.click();
					$.endPageLoading();
					return;
				}
			}
			
			//如果套餐配置的取消模式是7
			if("7"==element.get("CANCEL_MODE")){
				//判断套餐是否已经生效，如果生效则不能取消，如果未生效则可以取消
				var date1 = new Date(element.get("START_DATE"));
				var date2 = new Date();
				if(date1.getTime()<date2.getTime()){
					alert("套餐已经生效，不能取消");
					elCheckBox.click();
					$.endPageLoading();
					return;
					
				}
			}
			
			var temp = selectedElements.selectedEls.get(element.get("ITEM_INDEX"));
			temp.put("END_DATE",element.get("END_DATE"));
			temp.put("EFFECT_NOW_START_DATE",element.get("EFFECT_NOW_START_DATE"));
			temp.put("EFFECT_NOW_END_DATE",element.get("EFFECT_NOW_END_DATE"));
			temp.put("OLD_EFFECT_NOW_START_DATE",element.get("OLD_EFFECT_NOW_START_DATE"));
			temp.put("OLD_EFFECT_NOW_END_DATE",element.get("OLD_EFFECT_NOW_END_DATE"));
			if(element.get("ELEMENT_TYPE_CODE")=="D"){
				selectedElements.setEndDateView(element.get("ITEM_INDEX"),temp.get("END_DATE"));
				/**
				var html=[];
				html.push('<option value="'+temp.get("END_DATE").substring(0,10)+'">'+temp.get("END_DATE").substring(0,10)+'</option>');
				$.insertHtml('afterbegin',$("#"+element.get("ITEM_INDEX")+"_END_DATE"),html.join(""));
				$("#"+element.get("ITEM_INDEX")+"_END_DATE").val(temp.get("END_DATE").substring(0,10));**/
				//$("#"+element.get("ITEM_INDEX")+"_END_DATE").attr("disabled","");
			}
			
			//海南添加
			/*if($("#ELEMENT_EXTEND_ACTION").val()!="" && $("#ELEMENT_EXTEND_ACTION").val()!="undefined"){
				eval($("#ELEMENT_EXTEND_ACTION").val());
			}
			if(typeof(changeProductExtend) != "undefined" && changeProductExtend.setInvoiceInfo)
			{
				changeProductExtend.setInvoiceInfo();
			}*/
			
			$.endPageLoading();
		},
		
		getElement:function(elementId){
			var length = this.selectedEls.length;
			for(var i=0;i<length;i++){
				var temp = this.selectedEls.get(i);
				if(elementId == temp.get("ELEMENT_ID")){
					return temp;
				}
			}
		},
		
		checkIsExist: function(elementId,elementType){
			var length = this.selectedEls.length;
			for(var i=0;i<length;i++){
				var selectedEl = this.selectedEls.get(i);
				if(selectedEl.get("ELEMENT_ID")==elementId&&selectedEl.get("ELEMENT_TYPE_CODE")==elementType){
					return true;
				}
			}
			return false;
		},
		
		showAttr: function(eventObj,elementId,elementType){
			//点击事件
			if($("#elementAttrFloat").hasClass("c_float-show")) {
				$("#elementAttrFloat").removeClass("c_float-show");
				return;
			}
			var itemIndex = eventObj.getAttribute("itemIndex");
			var tempElement = selectedElements.selectedEls.get(itemIndex);
			var modifyTag = tempElement.get("MODIFY_TAG");
			if(modifyTag=="1"){
				return ;
			}
			var elementStartDate = tempElement.get("START_DATE");
			var elementEndDate = tempElement.get("END_DATE");
			var elementName = tempElement.get("ELEMENT_NAME");
			
			var params = "&ELEMENT_ID="+elementId+"&ELEMENT_TYPE_CODE="+elementType+"&ITEM_INDEX="+eventObj.getAttribute("itemIndex")+"&EPARCHY_CODE="+productEnv.eparchyCode;	
			if(typeof(selectedElements.getOtherParam)=="function"){
				if(selectedElements.getOtherParam().indexOf("&PRODUCT_ID=") == -1){
					params += "&PRODUCT_ID="+tempElement.get("PRODUCT_ID");
				}
				params += selectedElements.getOtherParam();
			}
			
			params += "&ELEMENT_NAME="+elementName;
			var sDateType = "select";
			var eDateType = "select";
			var sDateDisabled = (tempElement.get("CHOICE_START_DATE")=="true"?true:false);
			var eDateDisabled = (tempElement.get("CHOICE_END_DATE")=="true"?true:false);//禁用
			if(elementType=="D"){
				if(tempElement.get("CHOICE_START_DATE")=="true"){
					elementStartDate = tempElement.get("START_DATE").substring(0,10)+","+tempElement.get("EFFECT_NOW_START_DATE").substring(0,10);
				}
				
				if(tempElement.get("SELF_END_DATE")=="true"){
					eDateType = "datefield";
				}else{
					if(tempElement.get("CHOICE_END_DATE")=="true"){
						elementEndDate = tempElement.get("END_DATE").substring(0,10)+","+tempElement.get("EFFECT_NOW_END_DATE").substring(0,10);
					}
				}
			}
			params += "&ELEMENT_START_DATE="+elementStartDate+"&ELEMENT_END_DATE="+elementEndDate+"&sDateType="+sDateType+"&eDateType="+eDateType+"&eDisabled="+eDateDisabled+"&sDisabled="+sDateDisabled;
			$.ajax.submit(null,null,params,$("#ELEMENTATTR_COMPONENT_ID").val(),function(data){selectedElements.afterShowAttr(data,eventObj,elementId,elementType)});
		},
		
		afterShowAttr: function(data,eventObj,elementId,elementType){ 
			var itemIndex = eventObj.getAttribute("itemIndex");
			//设置回填值
			var tempElement = selectedElements.selectedEls.get(itemIndex); 
			if(data&&data.length>0){
				//只有属性类型为9时才会执行弹出自定义窗口设置属性
				var productId = tempElement.get("PRODUCT_ID");
				for(var i=0;i<data.length;i++){
					var popupAttr = data.get(i);
					if(popupAttr.get("PRODUCT_ID")==productId){
						var param = "&ELEMENT_ID="+tempElement.get("ELEMENT_ID")+"&ELEMENT_TYPE_CODE="+tempElement.get("ELEMENT_TYPE_CODE")+"&PRODUCT_ID="+tempElement.get("PRODUCT_ID")+"&PACKAGE_ID="+tempElement.get("PACKAGE_ID")+"&ITEM_INDEX="+itemIndex;
						if(selectedElements.buildPopupAttrParam){
							param+=selectedElements.buildPopupAttrParam();
						}
						// $.popupPage(popupAttr.get("ATTR_FIELD_CODE"),popupAttr.get("ATTR_FIELD_NAME"),param,popupAttr.get("TITLE"),popupAttr.get("WIDTH"),popupAttr.get("HEIGHT"));
                        popupPage(popupAttr.get("TITLE"),popupAttr.get("ATTR_FIELD_CODE"),popupAttr.get("ATTR_FIELD_NAME"),param,null,"c_popup c_popup-half");
						return;
					}
				}
			}
			var attrs = tempElement.get("ATTR_PARAM");
			var length = attrs.length;
			for(var i=0;i<length;i++){
				var attr = attrs.get(i);
				var attrCode = attr.get("ATTR_CODE");
				var attrValue = attr.get("ATTR_VALUE");
				if(attrValue){
					var attrObj = $("#"+attrCode);
					/*--------add by chenzg@20161025--REQ201610170027关于开发工作手机省内资费套餐的需求--begin-----*/
					//124485流量(M/G), 144485分钟数, 154485功能费
					if("10010201"==elementId || "10010202"==elementId){
						if("154485" == attrCode || "144485" == attrCode || "124485" == attrCode){
							if(tempElement.get("CHECK_OK") == undefined || tempElement.get("CHECK_OK")==true){
								if("154485" == attrCode){
									attrValue = attrValue/100;		//集团套餐功能费，分转为元
								}else if("144485" == attrCode){
									attrValue = attrValue/60;		//集团套餐分钟数，秒转为分
								}else if("124485" == attrCode){
									attrValue = (attrValue/1024/1024)+"M"; //集团套餐流量数，字节转为M
								}
							}
							//分钟或流量属性绑定自动计算功能费事件
							if("144485" == attrCode || "124485" == attrCode){
								attrObj.attr("chgItemIndex", itemIndex);
								/*绑定onchange事件，用于输入分钟数/流量时，自动计算功能费*/
								attrObj.change(function(){
									var chgAttrCode = $(this).attr("id");
									var chgAttrVal = $(this).val();
									var chgItemIndex = $(this).attr("chgItemIndex")
									//MessageBox.alert(chgAttrCode+"="+chgAttrVal+",chgItemIndex="+chgItemIndex);
									var chgEle = selectedElements.selectedEls.get(chgItemIndex);
									var chgAttrs = chgEle.get("ATTR_PARAM");
									var chgAttr = selectedElements.getAttrParam(chgAttrs, chgAttrCode);
									chgAttr.put("ATTR_VALUE", chgAttrVal);
									var calcOk = selectedElements.calcFunFee(chgEle);
									var attr154485 = selectedElements.getAttrParam(chgAttrs, "154485");	//功能费
									$("#154485").val(attr154485.get("ATTR_VALUE")/100);					//显示为页面上功能费
									//$("#elementPanel").css("display","");
									$("#elementAttrFloat").addClass("c_float-show");
								});
							}
							
							if("154485" == attrCode){
								attrObj.attr("disabled", "true");	//功能费不可编辑
							}
						}
					} 
					
					
					/*--------add by chenzg@20161025--REQ201610170027关于开发工作手机省内资费套餐的需求--end-------*/
					attrObj.val(attrValue);
                    if($("#"+attrCode).attr("x-wade-uicomponent")=="select") {
                        //设置文本值
                        selectedElements.addSelectValue("#"+attrCode,attrValue);
                    }
					if(attrObj.attr("type")=="radio"||attrObj.attr("type")=="checkbox"){
						if(attrValue!=""&&attrObj.val()==attrValue){
							attrObj.attr("checked",true);
							attrObj.click();
						}
					}
				}
				//REQ201709220007 关于调整省内IDC流量计费保底比例规则的需求 100099属性值等100005||100003 add by songxw start
				if(("819001"==elementId || "819002"==elementId)&&attrCode=="100099"){//待定
					$("#100099").parent().parent().attr("style", "display:none");
				//	$("#100099").parent().parent().parent().parent().attr("style", "display:none");
				}
				//REQ201709220007 关于调整省内IDC流量计费保底比例规则的需求 100099属性值等100005||100003 add by songxw end
				if("20200511" == attrCode || "20200512" == attrCode || "20200513" == attrCode){
					var attrCodeObject = $("#" + attrCode);
					if(attrCodeObject){
						attrCodeObject.parent().parent().attr("style", "display:none");
					}
				}
			}
			//多媒体桌面电话
			if(elementId=='800109'){
				if(($("#METHOD_NAME").val()=='ChgUs' || $("#METHOD_NAME").val()=='CrtUs') && $("#Y_DIS").val()=='true'){
					var attrObj1 = $("#20000000");
					attrObj1.attr("disabled", "true");	//功能费不可编辑
					attrObj1.val("0");
				}
			}
			var obj = $(eventObj);
			selectedElements.setFloatLayer(obj, "elementAttrFloat", 'right',20);
            $("#elementAttrFloat").addClass("c_float-show");
			/**
			var obj = $(eventObj);
			var o = $(eventObj).offset();
			var top = $.format.px2rem(o.top+$(eventObj).height())*7.14;
			var left = $.format.px2rem(o.left)*7.3;
			var htmlHieght = $.format.px2rem($(document.body).height())*7.14;
			var elementAttrHiegth =$.format.px2rem($("#elementAttrFloat").height())*7.14;
			var bottomVal = $(document.body).height()-((o.top+$(eventObj).height())+$("#elementAttrFloat").height());
			if(bottomVal<0){
				top = top+$.format.px2rem(bottomVal)*7.14-2;
			}
			$("#elementAttrFloat").css("top", top + "em");
			$("#elementAttrFloat").css("left", left + "em");
			$("#elementAttrFloat").addClass("c_float-show");**/
				
		},
		
		updateAttr:function(itemIndex,attrParam){
			var tempElement = this.selectedEls.get(itemIndex);
			if(tempElement.get("MODIFY_TAG")!="0"){
				tempElement.put("MODIFY_TAG","2");
				tempElement.put("MODIFY_ATTR","true");
			}
			var newAttrParam = new $.DatasetList(attrParam);
			tempElement.put("ATTR_PARAM",newAttrParam);
		},
		
		getAttrs:function(itemIndex){
			var tempElement = this.selectedEls.get(itemIndex);
			return tempElement.get("ATTR_PARAM");
		},
		
		confirmAttr: function(itemIndex){
			if($.validate.verifyAll('elementPanel')){
				var tempElement = this.selectedEls.get(itemIndex);
				var attrs = tempElement.get("ATTR_PARAM");
				var length = attrs.length;
				var isUpdate = false;
				var elementTypeCode = tempElement.get("ELEMENT_TYPE_CODE")
				//修改优惠时间start
				var elementStartDate = $("#ELEMENT_START_DATE").val();
				var elementEndDate = $("#ELEMENT_END_DATE").val();
				var oldStartDate = tempElement.get("START_DATE");
				var oldEndDate = tempElement.get("END_DATE");
				
				var checkResult = selectedElements.checkDateNew(elementStartDate,elementEndDate);
				if(checkResult==false){
					return;
				}
				if(elementTypeCode=="D"){
					if(oldStartDate.substring(0,10)!=elementStartDate){
						
						if(elementStartDate==(tempElement.get("EFFECT_NOW_START_DATE").substring(0,10))){
							tempElement.put("OLD_EFFECT_NOW_START_DATE",el.get("START_DATE"));
							tempElement.put("OLD_EFFECT_NOW_END_DATE",el.get("END_DATE"));
							tempElement.put("START_DATE",el.get("EFFECT_NOW_START_DATE"));
							tempElement.put("END_DATE",el.get("EFFECT_NOW_END_DATE"));
						}else if(elementStartDate==tempElement.get("OLD_EFFECT_NOW_START_DATE").substring(0,10)){
							tempElement.put("START_DATE",el.get("OLD_EFFECT_NOW_START_DATE"));
							tempElement.put("END_DATE",el.get("OLD_EFFECT_NOW_END_DATE"));
						}
						
						selectedElements.setStartDateView(itemIndex,tempElement.get("START_DATE"));
					}
					
					if(oldEndDate.substring(0,10)!=elementEndDate){
						if(tempElement.get("SELF_END_DATE")=="true"){
							tempElement.put("END_DATE",elementEndDate+" 23:59:59");
						}
						selectedElements.setEndDateView(itemIndex,tempElement.get("END_DATE"));
					}
				}
				//修改优惠时间end
		
				var elementId = tempElement.get("ELEMENT_ID");
				var monthFuncFee = 0; //月功能费
				var monthCallFee = 0 ; //月通信费
				var singleAttrValue = 0;//定义一个单个属性值变量
				
				for(var i=0;i<length;i++){
					var attr = attrs.get(i);
					var attrCode = attr.get("ATTR_CODE");
					var attrValue = attr.get("ATTR_VALUE");
					var newAttrValue = $("#"+attrCode).val();
					 
					if(attrValue!=newAttrValue){
						attr.put("ATTR_VALUE",newAttrValue);
						isUpdate = true;
					}
					//和对讲产品特殊处理
					if('9901001' == elementId||'96041176' == elementId){//和对讲（待配置后换成本地资费）
						var filter = /^\d+(\.\d+)?$/;
					    if ("00039801"==attrCode&&!filter.test(newAttrValue)) {
					        $.showWarnMessage("资费值提示", "和对讲资费详情"+attrCode+"应为数字");
					        attr.put("ATTR_VALUE",6);
					        return false;
					    }
						if("00039801"==attrCode&&newAttrValue<4.2){
							$.showWarnMessage("资费值提示", "和对讲资费详情"+attrCode+"价格不能低于4.2元/月/人");
							attr.put("ATTR_VALUE",6);
							return false;
						}
						if ("000117601"==attrCode&&!filter.test(newAttrValue)) {
							$.showWarnMessage("资费值提示", "和对讲(功能费)资费详情"+attrCode+"应为数字");
							attr.put("ATTR_VALUE",6);
					        return false;
					    }
						if("000117601"==attrCode&&newAttrValue<6){
							$.showWarnMessage("资费值提示", "和对讲(功能费)资费详情"+attrCode+"价格不能低于6元/月/人");
							attr.put("ATTR_VALUE",6);
							return false;
						}
					}
                    //千里眼产品特殊处理
                    if('1514' == elementId){//千里眼
                        if("000151401"==attrCode&&newAttrValue<=0){
                            $.showWarnMessage("资费值提示", "千里眼(摄像头功能费)资费详情"+attrCode+"价格不能等于或低于0元/月/路");
                            return false;
                        }
                    }
					//REQ201709220007关于调整省内IDC流量计费保底比例规则的需求 add by songxw start
					if(elementId == "819001"||elementId == "819002"){
						if(attrCode == "100005")
						{
							singleAttrValue = newAttrValue;
						}
						if(attrCode == "100099")
						{
							var val100001 = $("#100001").val();
							val100001 = val100001.replace("Mb","");
							var val100005 = $("#100005").val();
							var val100099 = val100001+"||"+val100005;
							$("#100099").val(val100099);
							attr.put("ATTR_VALUE",val100099);
						}
					}
					//REQ201709220007关于调整省内IDC流量计费保底比例规则的需求 add by songxw end
				}
 
				
				if(isUpdate){
					if(tempElement.get("MODIFY_TAG")!="0"){
						tempElement.put("MODIFY_TAG","2");
						tempElement.put("MODIFY_ATTR","true");
					}
				}
				//REQ201709220007关于调整省内IDC流量计费保底比例规则的需求 add by songxw start
				if(elementId == "819001"||elementId == "819002")
				{
					//var reg = /^[0-9]*[1-9][0-9]*$/;
					var reg = /^\d+$/;
					if(!reg.test(singleAttrValue))
					{
						MessageBox.alert('保底比例_资费属性必须为正整数!');
						return false;
					}

					if(parseInt(singleAttrValue) > 100)
					{
						MessageBox.alert('保底比例_资费属性不能大于100，请重新输入!');
						return false ;
					}
				}
				//REQ201709220007关于调整省内IDC流量计费保底比例规则的需求 add by songxw end
				if("10010201"==elementId || "10010202"==elementId){
					selectedElements.calcFunFee(tempElement);	//特殊处理集团工作套餐的功能费
				}
				//$("#elementPanel").css("display","none");
				elementAttr.attrBack();
			}
		},
		/*add by chenzg@20161025 计算集团工作套餐功能费*/
		calcFunFee: function(element){
			var elementId = element.get("ELEMENT_ID");
			var attrs = element.get("ATTR_PARAM");
			var attrCode = "";
			if("10010201"==elementId){
				attrCode = "144485";	//工作手机省内语音包的分钟数属性
			}else if("10010202"==elementId){
				attrCode = "124485";	//工作手机省内流量包的流量属性
			}
			//分钟数对应的功能费
			if("144485"==attrCode){
				var attr144485 = selectedElements.getAttrParam(attrs, attrCode);
				var oneAttrValue = attr144485.get("ATTR_VALUE");		//分钟数
				var flag = $.verifylib.checkPInteger(oneAttrValue);
				if(!flag || parseInt(oneAttrValue,10)<100){
					var checkInfo = "分钟数校验：分钟数必须是大于0的整数，最低100分钟起步!";
					element.put("CHECK_OK", false);
					element.put("CHECK_INFO", checkInfo);
					MessageBox.alert(checkInfo);
					return false;
				}
				var funFee = 0;
				//100-500 0.07
				if(oneAttrValue<=500){
					funFee = 0.07 * oneAttrValue;
				}
				//501-1000 0.05 
				else if(oneAttrValue<=1000 && oneAttrValue>500){
					funFee = 0.07 * 500 + 0.05*(oneAttrValue - 500)
				}
				//1000分钟以上 0.03 
				else if(oneAttrValue>1000){
					funFee = 0.07 * 500 + 0.05*500 + 0.03*(oneAttrValue - 1000)
				}
				var attr154485 = selectedElements.getAttrParam(attrs, "154485");
				attr154485.put("ATTR_VALUE", Math.round(funFee)*100);	//功能费：元四舍五入后转为分
				attr144485.put("ATTR_VALUE", oneAttrValue*60);			//分钟数分转为秒
				element.put("CHECK_OK", true);
			}
			//流量对应的功能费
			else if("124485"==attrCode){
				var attr124485 = selectedElements.getAttrParam(attrs, attrCode);
				var attr154485 = selectedElements.getAttrParam(attrs, "154485");
				var oneAttrValue = attr124485.get("ATTR_VALUE");											//流量
				//MessageBox.alert("attr124485="+attr124485);
				var gprsNum = oneAttrValue.substring(0, oneAttrValue.length-1);								//流量值
				var unit = oneAttrValue.substring(oneAttrValue.length-1, oneAttrValue.length);				//流量单位
				if((unit=="M" || unit=="G")&&($.verifylib.checkNumberRange(gprsNum, 0, 99999))){
					if(unit=="M" && parseFloat(gprsNum,10)<500){
						var checkInfo = "流量校验：流量最低500M起步！";
						element.put("CHECK_OK", false);
						element.put("CHECK_INFO", checkInfo);
						MessageBox.alert(checkInfo);
						return false;
					}
					if(unit=="G" && parseFloat(gprsNum,10)<(500/1024)){
						var checkInfo = "流量校验：流量最低500M起步！";
						element.put("CHECK_OK", false);
						element.put("CHECK_INFO", checkInfo);
						MessageBox.alert(checkInfo);
						return false;
					}
					if(unit=="G"){
						gprsNum = Math.round(1024 * gprsNum);	//G转为M
					}
				}else{
					var checkInfo = "流量校验：流量请输入大于0的数字并以单位M或G结尾，比如：700M,2G等！";
					element.put("CHECK_OK", false);
					element.put("CHECK_INFO", checkInfo);
					MessageBox.alert(checkInfo);
					return false;
				}
				
				var funFee = 0;
				//500M-1G 0.01元/M 
				if(gprsNum<=1024){
					funFee = 0.01 * gprsNum;
				}
				//1G-10G 10元/G 
				else if(gprsNum<=1024*10 && gprsNum>1024){
					funFee = 0.01 * 1024 + 10*((gprsNum-1024)/1024)
				}
				//10G以上 8元/G 
				else if(gprsNum>1024*10){
					funFee = 0.01 * 1024 + 10*9 + 8*((gprsNum-1024*10)/1024)
				}
				var attr154485 = selectedElements.getAttrParam(attrs, "154485");
				attr154485.put("ATTR_VALUE", Math.round(funFee)*100);	//功能费：元四舍五入后转为分
				attr124485.put("ATTR_VALUE", gprsNum*1024*1024);		//流量数M转为B(字节)
				element.put("CHECK_OK", true);
			}
			
			return true;
		},
		/*add by chenzg@20161025 计算集团工作套餐功能费*/
		getAttrParam: function(attrs, attrCodeVal){
			for(var i=0;i<attrs.length;i++){
				var attr = attrs.get(i);
				var attrCode = attr.get("ATTR_CODE");
				if(attrCodeVal == attrCode){
					return attr;
				}
			}
		},
		
		cancelElementAttr: function(itemIndex){
			//$("#elementPanel").css("display","none");
			elementAttr.attrBack();
			$("#elementPanelUL").html("");
		},
		
		
		disableAll: function(){
			var els = $("#" + $("#SELECTEDELEMENTS_COMPONENT_ID").val() + " input[name]");
			var length = els.length;
			for(var i=0;i<length;i++){
				els[i].disabled = true;
			}
		},
		
		checkForcePackage: function(){
			var packages = $("#packages").children();
			var size = packages.length;
			for(var i=0;i<size;i++){
				//var thePackages = $(packages[i]);
				var thePackage = $(packages[i]);//$(thePackages[0]);
				if(thePackage.attr("forceTag")=="1"){
					var length = this.selectedEls.length;
					var isHas = false;
					for(var j=0;j<length;j++){
						var temp = this.selectedEls.get(j);
						if((temp.get("MODIFY_TAG")=="0" || temp.get("MODIFY_TAG")=="exist" || temp.get("MODIFY_TAG") == "2")&& (temp.get("PACKAGE_ID") == thePackage.attr("groupId") || temp.get("NEW_PACKAGE_ID") == thePackage.attr("groupId"))){
							isHas = true;
							break;
						}
					}
					if(!isHas){
						MessageBox.alert("包"+thePackage.attr("groupName")+"是必选包，必须添加该包下的至少一个元素");
						return false;
					}
				}
			}
			return true;
		},
		
		effectNow:function(){
			this.isEffectNow = true;
			var length = this.selectedEls.length;
			for(var i=0;i<length;i++){
				var temp = this.selectedEls.get(i);
				if(temp.get("MODIFY_TAG")=="0" ||temp.get("MODIFY_TAG")=="1" || temp.get("MODIFY_TAG")=="0_1"){
					$("#"+temp.get("ITEM_INDEX")+"_START_DATE").attr("disabled",true);
					if((temp.get("START_DATE").substring(0,10)==temp.get("EFFECT_NOW_START_DATE").substring(0,10))&&temp.get("MODIFY_TAG")=="0"){
						continue; 
					}
					if((temp.get("END_DATE").substring(0,10)==temp.get("EFFECT_NOW_END_DATE").substring(0,10))&&temp.get("MODIFY_TAG")=="1"){
						continue; 
					}
					temp.put("OLD_EFFECT_NOW_START_DATE",temp.get("START_DATE"));
					temp.put("START_DATE",temp.get("EFFECT_NOW_START_DATE"));
					temp.put("OLD_EFFECT_NOW_END_DATE",temp.get("END_DATE"));
					temp.put("END_DATE",temp.get("EFFECT_NOW_END_DATE"));
					if(temp.get("ELEMENT_TYPE_CODE")=="D"){
						this.resetDate(temp);
					}
				}
			}
		},
		
		effectBookingDate:function(){
			this.isEffectNow = false;
			var length = this.selectedEls.length;
			var tempEls = new $.DatasetList();
			var bookingDate = $("#BOOKING_DATE").val();
			
			for(var i=0;i<length;i++){
				var temp = this.selectedEls.get(i);
				if(temp.get("MODIFY_TAG")=="0" || temp.get("MODIFY_TAG")=="0_1"){
					//temp.put("START_DATE", bookingDate);
					tempEls.add(temp);
				}else if(temp.get("MODIFY_TAG")=="1"){
					tempEls.add(temp);
				}
			}
			
			if(tempEls.length > 0)
			{
				var params = "&ELEMENTS="+tempEls.toString()+"&EPARCHY_CODE="+productEnv.eparchyCode;
				if($("#basicStartDateControlId").val()!=""){
					params+="&BASIC_START_DATE="+$("#"+$("#basicStartDateControlId").val()).val();
				}
				if($("#basicCancelDateControlId").val()!=""){
					params+="&BASIC_CANCEL_DATE="+$("#"+$("#basicCancelDateControlId").val()).val();
				}
				if(this.isEffectNow){
					params+="&EFFECT_NOW=true";
				}
				if(typeof(selectedElements.getOtherParam)=="function"){
					params += selectedElements.getOtherParam();
				}
				if(this.userProductId!=null){
					params+="&USER_PRODUCT_ID="+this.userProductId;
				}
				if(this.nextProductId!=null){
					params+="&NEXT_PRODUCT_ID="+this.nextProductId;
				}
				if(this.nextProductStartDate!=null){
					params+="&NEXT_PRODUCT_START_DATE="+this.nextProductStartDate;
				}
				if(typeof($("#NEW_PRODUCT_ID").val()) !="undefined" && $("#NEW_PRODUCT_ID").val() != null && $("#NEW_PRODUCT_ID").val() != ''){
					params+="&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val();
				}
				params+="&SELECTED_ELEMENTS="+encodeURIComponent(this.selectedEls.toString())+"&USER_ID="+productEnv.userId+"&CALL_SVC="+$("#callAddElementSvc").val()+"&TRADE_TYPE_CODE="+$("#SELECTED_TRADE_TYPE_CODE").val();
				params+="&ACCT_DAY="+$("#ACCT_DAY").val()+"&FIRST_DATE="+$("#FIRST_DATE").val()+"&NEXT_ACCT_DAY="+$("#NEXT_ACCT_DAY").val()+"&NEXT_FIRST_DATE="+$("#NEXT_FIRST_DATE").val();
				$.beginPageLoading("已选区加载中。。。。。");
				hhSubmit(null,"com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.pkgelementlist.SelectedElementsHandler","dealElement", params, selectedElements.afterEffectBookingDate,selectedElements.errProcess);
			}
		},
		
		afterEffectBookingDate:function(data){
			if(data.length > 0){
				for(var i=0; i<data.length; i++){
					var element = data.get(i);
					var temp = selectedElements.selectedEls.get(element.get("ITEM_INDEX"));
					temp.put("START_DATE",element.get("START_DATE"));
					temp.put("END_DATE",element.get("END_DATE"));
					temp.put("EFFECT_NOW_START_DATE",element.get("EFFECT_NOW_START_DATE"));
					temp.put("EFFECT_NOW_END_DATE",element.get("EFFECT_NOW_END_DATE"));
					temp.put("OLD_EFFECT_NOW_START_DATE",element.get("OLD_EFFECT_NOW_START_DATE"));
					temp.put("OLD_EFFECT_NOW_END_DATE",element.get("OLD_EFFECT_NOW_END_DATE"));
					if(element.get("ELEMENT_TYPE_CODE")=="D"){
						selectedElements.setStartDateView(element.get("ITEM_INDEX"),temp.get("START_DATE"));
						selectedElements.setEndDateView(element.get("ITEM_INDEX"),temp.get("END_DATE"));
						/**
						var startDateObj = $("#"+element.get("ITEM_INDEX")+"_START_DATE");
						var endDataObj = $("#"+element.get("ITEM_INDEX")+"_END_DATE");
						if("SELECT" == startDateObj[0].nodeName){
							startDateObj.empty();
							var html=[];
							html.push('<option value="'+element.get("START_DATE").substring(0,10)+'">'+temp.get("START_DATE").substring(0,10)+'</option>');
							$.insertHtml('afterbegin',$("#"+element.get("ITEM_INDEX")+"_START_DATE"),html.join(""));
							$("#"+element.get("ITEM_INDEX")+"_START_DATE").val(temp.get("START_DATE").substring(0,10));
						}
						if("INPUT" == startDateObj[0].nodeName){
							$("#"+element.get("ITEM_INDEX")+"_START_DATE").val(temp.get("START_DATE").substring(0,10));
						}
						if("SELECT" == endDataObj[0].nodeName){
							endDataObj.empty();
							html=[];
							html.push('<option value="'+temp.get("END_DATE").substring(0,10)+'">'+temp.get("END_DATE").substring(0,10)+'</option>');
							$.insertHtml('afterbegin',$("#"+element.get("ITEM_INDEX")+"_END_DATE"),html.join(""));
							$("#"+element.get("ITEM_INDEX")+"_END_DATE").val(temp.get("END_DATE").substring(0,10));
						}
						if("INPUT" == endDataObj[0].nodeName){
							$("#"+element.get("ITEM_INDEX")+"_END_DATE").val(temp.get("END_DATE").substring(0,10));
						}
						**/
						/**
						 * 无手机宽带产品变更 选择预约日期费用会改变，因此不能插入，而是要改变
						 * chenxy3 2017-1-9
						 * */
						var feeData = element.get("FEE_DATA");
		    			if(feeData!=null&&typeof(feeData)!="undefined"&&feeData.length>0){
		    				var feeSize = feeData.length;
		    				for(var j=0;j<feeSize;j++){
		    					var fee = feeData.get(j);
		    					var tradeTypeCode=fee.get("TRADE_TYPE_CODE");
		    					if("681"==tradeTypeCode){
		    						$.feeMgr.modFee(fee);//已经存在钱，再插就累加了，只能修改。
		    						//$.feeMgr.insertFee(fee);
		    					}
		    				}
		    			}

					}	
				}
			}
			$.endPageLoading();
		},
		
		unEffectNow:function(){
			this.isEffectNow = false;
			var length = this.selectedEls.length;
			for(var i=0;i<length;i++){
				var temp = this.selectedEls.get(i);
				if(temp.get("CHOICE_START_DATE")=="true"){
					//$("#"+temp.get("ITEM_INDEX")+"_START_DATE").attr("disabled",false);
				}
				if(temp.get("MODIFY_TAG")=="0" ||temp.get("MODIFY_TAG")=="1" || temp.get("MODIFY_TAG")=="0_1"){
					if((temp.get("OLD_EFFECT_NOW_START_DATE")==null||temp.get("START_DATE").substring(0,10)==temp.get("OLD_EFFECT_NOW_START_DATE").substring(0,10))&&temp.get("MODIFY_TAG")=="0"){
						continue; 
					}
					if((temp.get("OLD_EFFECT_NOW_END_DATE")==null||temp.get("END_DATE").substring(0,10)==temp.get("OLD_EFFECT_NOW_END_DATE").substring(0,10))&&temp.get("MODIFY_TAG")=="1"){
						continue; 
					}
					temp.put("START_DATE",temp.get("OLD_EFFECT_NOW_START_DATE"));
					temp.put("END_DATE",temp.get("OLD_EFFECT_NOW_END_DATE"));
					if(temp.get("ELEMENT_TYPE_CODE")=="D"){
						this.resetDate(temp);
					}
				}
			}
		},
		
		getSubmitData:function(){
			var length = this.selectedEls.length;
			var submitData = $.DatasetList();
			for(var i=0;i<length;i++){
				var temp = this.selectedEls.get(i);
				if(temp.get("MODIFY_TAG")=="0" ||temp.get("MODIFY_TAG")=="1"||temp.get("MODIFY_TAG")=="2"){
					var data = new $.DataMap();
					data.put("ELEMENT_ID",temp.get("ELEMENT_ID"));
					data.put("ELEMENT_TYPE_CODE",temp.get("ELEMENT_TYPE_CODE"));
					data.put("PRODUCT_ID",temp.get("PRODUCT_ID"));
					data.put("PACKAGE_ID",temp.get("PACKAGE_ID"));
					if(temp.get("ATTR_PARAM")!=null&&temp.get("ATTR_PARAM").length>0){
						data.put("ATTR_PARAM",temp.get("ATTR_PARAM"));
					}
					data.put("MODIFY_TAG",temp.get("MODIFY_TAG"));
					data.put("START_DATE",temp.get("START_DATE"));
					data.put("END_DATE",temp.get("END_DATE"));
					data.put("INST_ID",temp.get("INST_ID"));
					//temp.removeKey("ELEMENT_NAME");
					//temp.removeKey("ELEMENT_FORCE_TAG");
					//temp.removeKey("PACKAGE_FORCE_TAG");
					//temp.removeKey("OLD_START_DATE");
					//temp.removeKey("OLD_END_DATE");
					//temp.removeKey("OLD_PRODUCT_ID");
					//temp.removeKey("OLD_PACKAGE_ID");
					//temp.removeKey("EFFECT_NOW_START_DATE");
					//temp.removeKey("EFFECT_NOW_END_DATE");
					//temp.removeKey("OLD_EFFECT_NOW_START_DATE");
					//temp.removeKey("OLD_EFFECT_NOW_END_DATE");
					//temp.removeKey("DISABLED");
					//temp.removeKey("ITEM_INDEX");
					if(temp.containsKey("CHECK_OK")){
						data.put("CHECK_OK", temp.get("CHECK_OK"));
						data.put("CHECK_INFO", temp.get("CHECK_INFO"));
					}
					submitData.add(data);
				}
			}
			return submitData;
		},
		
		changeStartDate: function(eventObj,showDatePanel){
			var obj = $(eventObj);
			var itemIndex = obj.attr("itemIndex");
			if(!itemIndex){
				itemIndex = $("#"+obj.attr("id")).attr("itemIndex");
			}
			var el = this.selectedEls.get(itemIndex);
			//var elEnd = $("#"+itemIndex+"_END_DATE");
			var elEnd = $("#ELEMENT_END_DATE");
			var value = obj.val();
			var endDate = el.get("END_DATE");
			if(value==(el.get("EFFECT_NOW_START_DATE").substring(0,10))){
				if(showDatePanel!="true"){//弹出时间选择弹出层的时候先不更新el，等点确定再更新
					el.put("OLD_EFFECT_NOW_START_DATE",el.get("START_DATE"));
					el.put("OLD_EFFECT_NOW_END_DATE",el.get("END_DATE"));
					el.put("START_DATE",el.get("EFFECT_NOW_START_DATE"));
					el.put("END_DATE",el.get("EFFECT_NOW_END_DATE"));
				}
				
				endDate = el.get("EFFECT_NOW_END_DATE");
			}
			else if(value==el.get("OLD_EFFECT_NOW_START_DATE").substring(0,10)){
				if(showDatePanel!="true"){
					el.put("START_DATE",el.get("OLD_EFFECT_NOW_START_DATE"));
					el.put("END_DATE",el.get("OLD_EFFECT_NOW_END_DATE"));
				}
				endDate = el.get("OLD_EFFECT_NOW_END_DATE");
			}
			if(showDatePanel=="true"){
				if(el.get("SELF_END_DATE")!="true"){
					ELEMENT_END_DATE.empty();
					ELEMENT_END_DATE.append(endDate.substring(0,10),endDate.substring(0,10));
					/**
					$("#"+itemIndex+"_END_DATE").empty();
					html = [];
					html.push('<option value="'+el.get("END_DATE").substring(0,10)+'">'+el.get("END_DATE").substring(0,10)+'</option>');
					$.insertHtml('afterbegin',$("#"+itemIndex+"_END_DATE"),html.join(""));**/
				}
				elEnd.val(endDate.substring(0,10));
			}else{
				selectedElements.setEndDateView(itemIndex,endDate);
			}
			
		},
		
		changeEndDate:function(eventObj){
		
		},
		
		getSelectedElsData:function(){
			var length = this.selectedEls.length;
			var selectedElesData = $.DatasetList();
			for(var i=0;i<length;i++){
				var temp = this.selectedEls.get(i);
				if(temp.get("MODIFY_TAG") != "0_1"){
					var data = new $.DataMap();
					data.put("ELEMENT_ID",temp.get("ELEMENT_ID"));
					data.put("ELEMENT_TYPE_CODE",temp.get("ELEMENT_TYPE_CODE"));
					data.put("PRODUCT_ID",temp.get("PRODUCT_ID"));
					data.put("PACKAGE_ID",temp.get("PACKAGE_ID"));
					if(temp.get("ATTR_PARAM")!=null&&temp.get("ATTR_PARAM").length>0){
						data.put("ATTR_PARAM",temp.get("ATTR_PARAM"));
					}
					data.put("MODIFY_TAG",temp.get("MODIFY_TAG"));
					data.put("START_DATE",temp.get("START_DATE"));
					data.put("END_DATE",temp.get("END_DATE"));
					data.put("INST_ID",temp.get("INST_ID"));
					
					selectedElesData.add(data);
				}
			}
			return selectedElesData;
		},
		
		selectElementPackage:function(eventObj){
			var obj = $(eventObj);
			var packageId = obj.attr("packageId");
			try{
				offerList.selectedGroupAction(packageId);
			}
			catch(e){
			
			}
		},
		
		isDate:function(dateValue) { 
			var regex = new RegExp("^(?:(?:([0-9]{4}(-|\/)(?:(?:0?[1,3-9]|1[0-2])(-|\/)(?:29|30)|((?:0?[13578]|1[02])(-|\/)31)))|([0-9]{4}(-|\/)(?:0?[1-9]|1[0-2])(-|\/)(?:0?[1-9]|1\\d|2[0-8]))|(((?:(\\d\\d(?:0[48]|[2468][048]|[13579][26]))|(?:0[48]00|[2468][048]00|[13579][26]00))(-|\/)0?2(-|\/)29))))$"); 
			if (!regex.test(dateValue)) { 
				return false; 
			} 
			return true;
		},
		
		getOtherParam: function(){
			var data = '';
			if(typeof(getOtherParam)=='function'){
				data+=getOtherParam();
			}
			if(typeof($("#BOOKING_DATE").val()) != "undefined" && $("#BOOKING_DATE").val() != null && $("#BOOKING_DATE").val() != ''){
				data+= "&BOOKING_DATE="+$("#BOOKING_DATE").val();
			}
			return data;
		},
		
		clearAllElementFee:function(){
			var length = this.selectedEls.length;
			for(var i=0;i<length;i++){
				var el = this.selectedEls.get(i);
				if(el.get("MODIFY_TAG")=="0"){
					var feeData = el.get("FEE_DATA");
	    			if(feeData!=null&&typeof(feeData)!="undefined"&&feeData.length>0){
	    				var feeSize = feeData.length;
	    				for(var j=0;j<feeSize;j++){
	    					var fee = feeData.get(j);
	    					
	    					/**
	    					 * 无手机宽带产品变更 这里要清理掉所有费用，因为费用已经改变了。
	    					 * chenxy3 2017-01-10
	    					 * */
	    					var tradeTypeCode=fee.get("TRADE_TYPE_CODE");
	    					if("681"==tradeTypeCode){
	    						$.feeMgr.clearFeeList(tradeTypeCode,"2");
	    					}else{
	    						$.feeMgr.deleteFee(fee);
	    					}
	    				}
	    			}
    			}
			}
		},
		checkDateNew:function(startDate,endDate){
			var isCheck = $.verifylib.checkDate(endDate, "yyyy-MM-dd");
			if(isCheck){
				if(startDate>endDate){
					MessageBox.alert("结束时间不能小于开始时间");
					return false;
				}
				else{
					var isDate = this.isDate(startDate);
					if(isDate){
					}
					else{
						MessageBox.alert("输入的开始日期不正确");
						return false;
					}
					
					var isDate = this.isDate(endDate);
					if(isDate){
					}
					else{
						MessageBox.alert("输入的结束日期不正确");
						return false;
					}
					return true;
					
				}
			}
			else{
				MessageBox.alert("输入有误，请重新输入");
				return false;
			}
		},
		setStartDateView:function(itemIndex,startDate){
			var html=[];
			$("#"+itemIndex+"_START_DATE").empty();
			html.push('<span value="'+startDate.substring(0,10)+'">'+startDate.substring(0,10)+'</span>');
			$.insertHtml('afterbegin',$("#"+itemIndex+"_START_DATE"),html.join(""));
			$("#"+itemIndex+"_START_DATE").val(startDate.substring(0,10));
		},
		setEndDateView:function(itemIndex,endDate){
			var html=[];
			$("#"+itemIndex+"_END_DATE").empty();
			html.push('<span value="'+endDate.substring(0,10)+'">'+endDate.substring(0,10)+'</span>');
			$.insertHtml('afterbegin',$("#"+itemIndex+"_END_DATE"),html.join(""));
			$("#"+itemIndex+"_END_DATE").val(endDate.substring(0,10));
		},
		showHideSvc:function(obj){
			var svcPart = $("#srvDisplayPart");
			var showFlag = $(obj).attr("showFlag");
			if(showFlag == "0"){
				$(obj).attr("showFlag","1");
				svcPart.removeClass("e_hide-x");
				$(obj).empty();
				var html = [];
				html.push('<span class="e_ico-fold"></span>隐藏');
				$.insertHtml('afterbegin',$(obj),html.join(""));
			}else{
				$(obj).attr("showFlag","0");
				svcPart.addClass("e_hide-x");
				$(obj).empty();
				var html = [];
				html.push('<span class="e_ico-unfold"></span>显示');
				$.insertHtml('afterbegin',$(obj),html.join(""));
			}
			
			
		},
        addSelectValue: function (id,value) {
            $(id+"_float").find("li").removeClass("on");
            $(id+"_float").find("li[val="+value+"]").addClass("on");
            var text = $(id+"_float").find("li[val="+value+"]").find("div").text();
            // var val = $(id+"_float").find("li[val="+value+"]").attr("val");
            $(id+ "_span span").text(text);
            // $(id+ "_span input").val(val);
        },
		setFloatLayer:function(o, id, align, width) {
			var floatEl = $("#" + id);
			floatEl.css("top",$(o).offset().top + $(o).height() - 1 + "px");
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
		},
        changeDefaultOp:function (obj,op) {
            if(op=='1'){
                $(obj).hide().next().show();
                $('#selectedDiv li').each(function()
                {
                    if($(this).find('input').attr('disabled'))
                    {
                        $(this).removeClass('e_hide')
                    }});
                if ("110" === $("#SELECTED_TRADE_TYPE_CODE").val()) {
                    localStorage.setItem($("#STAFF_ID").val() + "_COMP_ELEMENTS", "show");
                }
            }
            if(op==2){
                $(obj).hide().prev().show();
                $('#selectedDiv li').each(function()
                {
                    if($(this).find('input').attr('disabled'))
                    {
                        $(this).addClass('e_hide')
                    }});
                if ("110" === $("#SELECTED_TRADE_TYPE_CODE").val()) {
                    localStorage.setItem($("#STAFF_ID").val() + "_COMP_ELEMENTS", "hide");
                }
            }
        }
	});
}
)();