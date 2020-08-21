
var offerInfos = new Wade.DatasetList();
 
$(function() {
     
    $.each($("[id*='offer']"), function(index, value) {
         
        //var offerJSON = JSON.parse(value.textContent);
        var offerInfo = new Wade.DataMap(value.textContent);
        var key = offerInfo.get("PRODUCT_ID");
        //做一个esp产品标记
        var bandCode = offerInfo.get("EC_OFFER").get("BRAND_CODE");
        if(bandCode == "ESPG")
        {
        	offerInfo.put("DEAL_TYPE","ESP");
        }
         
        offerInfos.add(offerInfo);

        $("#" + key + "Import").beforeAction(function(e) {
            //				MessageBox.alert("业务提示",hint);
//        	if (key == "8001"){
//        		Table8001.addRow({"TRADE_ID_SUB":"<input type=\"checkbox\" name=\"TRADES8001\"  />","SHORT_CODE":"666666","SERIAL_NUMBER":"13526589658"});
//        	}
//        	if (key == "2222"){
//        		Table2222.addRow({"TRADE_ID_SUB":"<input type=\"checkbox\" name=\"TRADES2222\"   />","SHORT_CODE":"666666","SERIAL_NUMBER":"13526589658"});
//        	}	
            return confirm("原有成员数据将会覆盖，是否导入？");

        });
        // 绑定导入组件的导入后事件，第二个形参为导入完成状态，ok 为成功完成
        $("#" + key + "Import").afterAction(function(e, status) {
             
            if ('ok' == status) {
                $.beginPageLoading("正在处理...");
                ajaxSubmit('', 'importFile', "&PRODUCT_ID=" + key, '', function(data) {
                    $.endPageLoading();
                    if (key == "8001"){
                    	$("#Table8001 tbody").html("");
                    	for (var i = 0; i < data.length; i++) {
                             var info = data.get(i);
                             Table8001.addRow($.parseJSON(info.toString()));
                        }
                    	var table8001Data = new Wade.DataMap();
                    	var tableData = Table8001.getData();
                    	table8001Data.put("8001", tableData);
                	}
                    else if (key == "2222"){
                    	$("#Table2222 tbody").html("");
                       for (var i = 0; i < data.length; i++) {
                            var info = data.get(i);
                            Table2222.addRow($.parseJSON(info.toString()));
                       }
                       var table2222Data = new Wade.DataMap();
                   	   var tableData = Table2222.getData();
                   	   table2222Data.put("2222", tableData);
                	}
                    else if (key == "8000"){
                    	$("#Table8000 tbody").html("");
                    	for (var i = 0; i < data.length; i++) {
                            var info = data.get(i);
                            Table8000.addRow($.parseJSON(info.toString()));
                       }
                       var table8000Data = new Wade.DataMap();
                       var tableData = Table8000.getData();
                       table8000Data.put("8000", tableData);
                    	
                    }
                    else if (key == "380300"){
                    	$("#Table380300 tbody").html("");
                    	for (var i = 0; i < data.length; i++) {
                    		var info = data.get(i);
                    		Table380300.addRow($.parseJSON(info.toString()));
                    	}
                    	
                    }
                    else if (key == "380700"){
                    	$("#Table380700 tbody").html("");
                    	for (var i = 0; i < data.length; i++) {
                    		var info = data.get(i);
                    		Table380700.addRow($.parseJSON(info.toString()));
                    	}
                    	
                    }

                }, function(error_code, error_info, derror) {
                    $.endPageLoading();
                    showDetailErrorInfo(error_code, error_info, derror);
                });
            }
        });
    });

    
})

	//提交
	function submitApply()
	{
		 
		var submitData = buildSubmitData(); // 拼凑 提交参数；
	 
		var message = "成员补录成功!";
		$.beginPageLoading("数据提交中，请稍后...");
		$.ajax.submit("", "submit", "&SUBMIT_PARAM="+encodeURIComponent(submitData.toString()), "", function(data){
			$.endPageLoading();
			 
			if(data.get("ASSIGN_FLAG") == "true")
			{
				MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
					if("ext1" == btn){
						 
						var urlArr = data.get("ASSIGN_URL").split("?");
						var pageName = getNavTitle();
						openNav('指派', urlArr[1].substring(13), '', '', urlArr[0]);
						closeNavByTitle(pageName);
					}
					if("ok" == btn){
						$.MessageBox.confirm("提示:","您还未指派审核人，请在待办工单列表中指派",function(re){
							if(re=="ok"){
								closeNav();
							}
						});
					}
				}, {"ext1" : "指派"});
			}
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
	}
	
	// 拼凑 提交参数；
	function buildSubmitData() {
		 
		var submitData = new Wade.DataMap();
		
		submitData.put("OFFER_LIST", saveOfferInfo());    // 商品信息
		submitData.put("OFFER_DATA_LIST", saveOfferData());    // 商品信息
		
		submitData.put("COMMON_DATA", saveEosCommonData());

		var widenetFlag = $("#WIDENET_FLAG").val();
		if(widenetFlag == "true")
		{
			var wideNetOpenData = saveWideNetData();
			submitData.put("WIDENET_OPEN_DATA", wideNetOpenData);
		}
		//客户信息
	    submitData.put("CUST_DATA", saveEcCustomer());
		submitData.put("BUSI_SPEC_RELE", new Wade.DataMap($("#BUSI_SPEC_RELE").text()));        // 流程信息
		submitData.put("NODE_TEMPLETE", new Wade.DataMap($("#NODE_TEMPLETE").text()));         // 节点信息
		
		return submitData;
	}
	
	function saveOfferData()
	{
	    var offerData = new Wade.DataMap();

	    var productId = $("#TEMPLET_BUSI_CODE").val();
	    var productName = $("#cond_TEMPLET_ID").text();

	    offerData.put("OFFER_CODE",productId);
	    offerData.put("OFFER_NAME",productName);

	    return offerData ;
	}
	
	function saveEosCommonData()
	{
		var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
		return eosCommonData;
	}
	
	//保存客户信息
	function saveEcCustomer()
	{
	    var customer = new Wade.DataMap();

	    customer.put("CUST_ID", $("#cond_CUST_ID").val());
	    customer.put("CUST_NAME", $("#cond_CUST_NAME").val());
	    customer.put("GROUP_ID", $("#cond_GROUP_ID").val());
	    customer.put("EPARCHY_CODE", $("#cond_EPARCHY_CODE").val());
	    return customer;
	}
	
	//保存成员信息
	function saveOfferInfo()
	{
		 
		for(var i = 0, len = offerInfos.length; i< len;i++){
			var data = offerInfos.get(i);
			//var memberData = data.get("MEB_LIST").get(0);
			var rowsData = new Wade.DatasetList();
			var tableData;
			if(offerInfos.get(i).get("PRODUCT_ID") == "8001"){
             	tableData = Table8001.getData(true);
             	setMember(tableData, rowsData, data, "0");
 			}
			else if(offerInfos.get(i).get("PRODUCT_ID") == "8000"){
				tableData = Table8000.getData(true);
				setMember(tableData, rowsData, data, "0");
			}
			else if(offerInfos.get(i).get("PRODUCT_ID") == "2222"){
				tableData = Table2222.getData(true);
				setMember(tableData, rowsData, data, "0");
			}
			else if(offerInfos.get(i).get("PRODUCT_ID") == "380300"){
				tableData = Table380300.getData(true);
				setMember(tableData, rowsData, data, "1");
			}
			else if(offerInfos.get(i).get("PRODUCT_ID") == "380700"){
				tableData = Table380700.getData(true);
				setMember(tableData, rowsData, data, "2");
			}
			//宽带特殊处理
			else if(offerInfos.get(i).get("PRODUCT_ID") == "7341"){
//				tableData = widenetTable.getData(true);
//				setWideNetData(tableData, rowsData, data)
				rowsData = data.get("MEB_LIST");
			}
			
			data.put("MEB_LIST", rowsData);
		}
		return offerInfos;
	}
	
	/*设置宽带成员数据*/
	function setWideNetData(tableData, rowsData, offerData)
	{
	    //公共宽带资费
	    var publicAttrInfo = new Wade.DatasetList($("#cond_SELECTED_ELEMENTS").text())
	    $.each(tableData, function(index, value) {
	        var newMB = $.DataMap({
	            STAND_ADDRESS: value.get("STAND_ADDRESS"),
	            GIS1: value.get("GIS1"),
	            FLOOR_AND_ROOM_NUM: value.get("FLOOR_AND_ROOM_NUM"),
	            WIDE_SERIAL_NUMBER: value.get("WIDE_SERIAL_NUMBER"),
	            WIDE_PRODUCT_ID: value.get("WIDE_PRODUCT_ID"),
	            DEVICE_ID: value.get("DEVICE_ID"),
	            AREA_CODE: value.get("AREA_CODE"),
	            CUST_ID: offerData.get("CUST_ID"),
	            SUGGEST_DATE: $("#SUGGEST_DATE").val(),
	            CONTACT: $("#CONTACT").val(),
	            PHONE: $("#PHONE").val(),
	            EPARCHY_CODE: $("#cond_EPARCHY_CODE").val(),
	            OPEN_TYPE: $("#OPEN_TYPE").val(),
	            HGS_WIDE: $("#HGS_WIDE").val(),
	            WIDE_PRODUCT_TYPE: $("#WIDE_PRODUCT_TYPE").val(),
	            SELECTED_ELEMENTS: publicAttrInfo,
	            SERIAL_NUMBER: value.get("WIDE_SERIAL_NUMBER"),
	            MODEM_DEPOSIT: $("#MODEM_DEPOSIT").val(),
	            CONTACT_PHONE: $("#CONTACT_PHONE").val(),
	            PRODUCT_ID: $("#WIDE_PRODUCT_ID").val(),
	            PRODUCT_NAME: $("#WIDE_PRODUCT_ID").text(),
	            DETAIL_ADDRESS: value.get("STAND_ADDRESS"),
	            RSRV_STR1: "WIDENET",
	            MODEM_STYLE: $("#MODEM_STYLE").val(),
	            EC_SERIAL_NUMBER: offerData.get("EC_SERIAL_NUMBER"),
	            TRADE_TYPE_CODE: "600",
	            WIDENET_PAY_MODE: $("#WIDENET_PAY_MODE").val()
	        });
	        rowsData.add(newMB);
	    })
	}
    
    /*设置成员数据*/
	function setMember(tableData, rowsData, data, type)
	{
		$.each(tableData, function(index, value) {
			if (type == "0"){
				var newMB = new Wade.DataMap({
					CUST_ID : data.get("CUST_ID"),
					EC_SERIAL_NUMBER : data.get("EC_SERIAL_NUMBER"),
					DEAL_TYPE : data.get("DEAL_TYPE"),
					IMPORT_RESULT : "true",
					PRODUCT_ID : data.get("PRODUCT_ID"),
					RSRV_STR1 : "MEB",
					SERIAL_NUMBER : value.get("SERIAL_NUMBER"),
					SHORT_CODE : value.get("SHORT_CODE")
				});
				rowsData.add(newMB);
			}
			if (type == "1"){
				var newMB = new Wade.DataMap({
					CUST_ID : data.get("CUST_ID"),
					EC_SERIAL_NUMBER : data.get("EC_SERIAL_NUMBER"),
					DEAL_TYPE : data.get("DEAL_TYPE"),
					IMPORT_RESULT : "true",
					PRODUCT_ID : data.get("PRODUCT_ID"),
					RSRV_STR1 : "MEB",
					PHONE_NUMBER : value.get("PHONE_NUMBER"),
					MAC_NUMBER : value.get("MAC_NUMBER")
				});
				rowsData.add(newMB);
			}
			if (type == "2"){
				var newMB = new Wade.DataMap({
					CUST_ID : data.get("CUST_ID"),
					EC_SERIAL_NUMBER : data.get("EC_SERIAL_NUMBER"),
					DEAL_TYPE : data.get("DEAL_TYPE"),
					IMPORT_RESULT : "true",
					PRODUCT_ID : data.get("PRODUCT_ID"),
					RSRV_STR1 : "MEB",
					DEV_MAC_NUMBER : value.get("DEV_MAC_NUMBER"),
					DEV_SN_NUMBER : value.get("DEV_SN_NUMBER")
				});
				rowsData.add(newMB);
			}
				
		})
	}
	
	//标准地址查询
	function addressSelect(){
		 
		var serialNumber= $("#PHONE").val();
		$("#AUTH_SERIAL_NUMBER").val(serialNumber);
		var contactSn = $("#CONTACT_PHONE").val();
		var custName = $("#CONTACT").val();
		var param = "&AUTH_SERIAL_NUMBER="+serialNumber+"&CUST_NAME="+encodeURIComponent(custName)+"&CONTACT_SN="+contactSn+"&RANDOM="+Math.random();
		popupPage("标准地址选择","igroup.minorec.AddressQryNew","init",param+'&TREE_TYPE=0',"iorder","c_popup c_popup-full",afterSetDetailAddress,null);

	}
	
	function afterSetDetailAddress(){
		
		var detailAddressList = new $.DatasetList($("#DETAIL_ADDRESS_LIST").val());
		var tableList = widenetTable.getData(true);
		var tableSize = tableList.length;
		var pateAll = "";
		if(0<tableSize){
			pateAll = "widenetResult";
		}else{
			//pateAll = "widenetResult,productType,widePhone,WideProductType";
			pateAll = "widenetResult";
		}
		var groupId = $("#cond_GROUP_ID").val();
		var serialNumber= $("#AUTH_SERIAL_NUMBER").val();
		var productTypeName= $("#WIDE_PRODUCT_TYPE").text();
		var openType = detailAddressList.get(0).get("OPEN_TYPE");
		if (productTypeName != openType){
			MessageBox.alert("提示信息", "开通时已选择了【" + productTypeName + "】宽带类型，不能勾选【" + openType + "】的类型，请重新选择!");
			return;
		}
		var productType= $("#WIDE_PRODUCT_TYPE").val();
		var dataset = new Wade.DatasetList(tableList.toString());
		$.beginPageLoading("数据加载中...");
		$.ajax.submit("", "queryWidenetTable", "&WIDE_PRODUCT_TYPE="+productType+"&GROUP_ID="+groupId+"&SERIAL_NUMBER="+serialNumber+"&DETAIL_ADDRESS_LIST="+detailAddressList+"&DETAIL_ADDRESS_LIST1="+dataset, pateAll, function(data){
			$.endPageLoading();
			$("#PHONE").val(serialNumber);
		}, 
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
		
	}
	
	//宽带地址信息删除
	function removeaddMebSub(){
		//待删除的总记录
		var rowDatas = widenetTable.getCheckedRowsData("TRADES");
		if(rowDatas==null||rowDatas==""){
			MessageBox.error("提示信息", "您未勾选表格的数据，进行删除，请选择！");
			return false;
		}else{
			var tableList = widenetTable.getData(true);
			var rowSize = rowDatas.length; 
			var tableSize = tableList.length;
			var deleteList = [];
			for(var i = 0, len = rowDatas.length; i<len; i++)
			{
				deleteList.push(rowDatas[i].get("STAND_ADDRESS"));
			}
			for(var i=0, len = tableList.length;i<len;i++){
				//当表格中的记录是删除的记录
				if ($.inArray(tableList[i].get("STAND_ADDRESS"), deleteList) != "-1")
				{
					widenetTable.deleteRow(i);
				} 
			}
			$("#widenetTable tr.deleted").remove();
			if(rowSize == tableSize){
				$.beginPageLoading("数据删除中...");
				$.ajax.submit("", "", "", "widenetResult", function(data){
					$.endPageLoading();
				}, 
				function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
			}
		}

	}