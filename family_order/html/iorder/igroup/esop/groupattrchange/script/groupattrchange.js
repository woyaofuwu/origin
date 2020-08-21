var BUSI_FILE;

$(function(){
	
	var groupIdA = $("#GROUP_IDA").val();
	var groupIdB = $("#GROUP_IDB").val();
	debugger;
	if(groupIdA != ""){
		MessageBox.alert("提示信息", "您正在处理<span class='e_red'>【"+groupIdA+"】</span>的业务，认证信息会随之变化！", function(btn){
			$.enterpriseLogin.refreshGroupInfo(groupIdA);
		});
		$.ajax.submit('', 'queryCustGroupByGroupIdCycle','&GROUP_IDB='+groupIdB+'&GROUP_IDA='+groupIdA,"groupAOldBasePart,groupABasePart,GrpCustPart", function(data){
			$.endPageLoading();
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });	

	}else {
		if (!$.enterpriseLogin || !$.enterpriseLogin.isLogin()) 
		{
			MessageBox.alert("提示信息","请先在外框认证政企客户信息！", function(btn) {
				if(window.location.href.indexOf("/iorder?") > 0)
				{//界面必须在外框登录集团
					closeNav();
				}
			});
		}
		$("#DATALINE_OPER_TYPE").val("0");
		var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
		var custId = custInfo.get("CUST_ID");
		var groupId = custInfo.get("GROUP_ID");
		$.ajax.submit('', 'queryGroupByGroupIdA','&CUST_ID='+custId+'&GROUP_ID='+groupId,"groupAOldBasePart,groupABasePart", function(data){
			$.endPageLoading();
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });	
		
	}
	$("#BPM_TEMLET_ID").val("GROUPATTRCHANGE");
	$("#cond_CUSTINFO_TAG").html("客户信息（过户后归属方）");
	var datalinOperType = $("#DATALINE_OPER_TYPE").val();
	if(datalinOperType != "0"){
		$("#DATALINE_SERIAL_NUMBER").css("display", "");
		$("#pattr_GRP_SERIAL_NUMBER_B").attr("nullable", "no");
		$("#acctDealInfoGroupChange").css("display", "none");
		$("#CONTRACT_INFO").css("display", "none");
	}else{
		$("#DATALINE_OPER_TYPE").val("0");
		$("#cond_ACCT_DEAL").val("0");
		//处理账户
		initAttrAcctData();
	}
	
});
$(function(){
	debugger;
	$("#C_FILE_LIST_ATTR").text($("#C_FILE_LIST_ATTR").val());
	var attchFileId = $("#ATTACH_FILE_ID").val();
	fileupload.loadFile(attchFileId);
	$("#CONTRACT_FILE_LIST").afterAction(function(e, file){
		var data1 = new Wade.DataMap();
		data1.put("FILE_ID", file.fileId);
		data1.put("FILE_NAME", file.name);
		data1.put("ATTACH_TYPE", "C");
		$("#C_FILE_LIST").text(data1.toString());
		$("#C_FILE_LIST").val(file.fileId+':'+file.name);
		
		$("#C_FILE_LIST_NAME").val(file.name);
	});

	//$("#C_FILE_LIST_ATTACH")
	//过户依据
	$("#ATTR_CHG_FILE").afterAction(function(e, file){
		var data = new Wade.DataMap();
		data.put("FILE_ID", file.fileId);
		data.put("FILE_NAME", file.name);
		data.put("ATTACH_TYPE", "P");
		$("#C_FILE_LIST_ATTACH").text(data.toString());
		 ATTR_FILE=file.name + "|" + file.fileId;
		$("#C_FILE_LIST_ATTR").text(ATTR_FILE);
	});

//业务协议
$("#fileupload").select(function(){
	var fileList = new Wade.DatasetList();

	var obj = this.val();
	var fileIdArr = obj.ID.split(",");
	var fileNameArr = obj.NAME.split(",");
	
	for(var i = 0, size = fileIdArr.length; i < size; i++)
	{
		if(fileIdArr[i] != "")
		{
			var data = new Wade.DataMap();
			data.put("FILE_ID", fileIdArr[i]);
			data.put("FILE_NAME", fileNameArr[i]);
			data.put("ATTACH_TYPE", "P");
			fileList.add(data);
		}
	}
	$("#ATTACH_FILE_LIST").text(fileList.toString());
	
	$("#upfileText").text(obj.NAME);
	$("#upfileValue").val(obj.ID);
	
	$("#ATTACH_FILE_NAME").val(obj.NAME);
	$("#ATTACH_FILE_ID").val(obj.ID);
	BUSI_FILE=fileList.toString();
	if(null==BUSI_FILE||0<=BUSI_FILE.size)
	{
		MessageBox.alert("提示信息","请上传业务协议！");
		return;
	}
	hidePopup("upfilegroup");
});

//绑定上传组件清除按钮事件
$("#fileupload").clear(function(){
	$("#upfileText").text("");
	$("#upfileValue").val("");
});
});
/*window.onload = function addItem() {
	// 校验所有的输入框  	
	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	var groupId = groupInfo.get("GROUP_ID");
	if(""==groupId|| groupId==null){
		 $.validate.alerter.one($("#cond_GROUP_ID_INPUT")[0], "集团号码为空，请输入!\n");
		 return false;
	}
	$.beginPageLoading("数据查询中......");
	$.ajax.submit('', 'queryGroupByGroupId','&GROUP_IDA='+groupId,"OfferListPart", function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });	
	
	
}*/

function addItem() {
	// 校验所有的输入框  	
	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	var groupId = groupInfo.get("GROUP_ID");
	if(""==groupId|| groupId==null){
		 $.validate.alerter.one($("#cond_GROUP_ID_INPUT")[0], "集团号码为空，请输入!\n");
		 return false;
	}
	var productId = $("#pattr_PRODUCT_ID").val();
	if(""==productId|| productId==null){
		MessageBox.alert("提示信息","请先选择需要过户的产品！");
		return false;
	}
	var groupIdB = $("#cond_GROUP_ID_INPUT").val();
	if("" == groupIdB || null==groupIdB){
		MessageBox.alert("提示信息","请选择归属集团！");
		return;
	}
	var isReadonly = $("#IS_READONLY").val();
	var grpSerialNumber = $("#pattr_GRP_SERIAL_NUMBER_B").val();
	var operType = $("#DATALINE_OPER_TYPE").val();
	$.beginPageLoading("数据查询中......");
	
			$.ajax.submit('', 'queryGroupByGroupId','&GROUP_IDA='+groupId+"&PRODUCT_ID="+productId+'&GROUP_IDB='+groupIdB+"&IS_READONLY="+isReadonly+"&GRP_SN="+grpSerialNumber,"OfferListPart,grpserial", function(data){
				if(data==null|| data==""){
					MessageBox.alert("提示信息","该归属集团没有订购该产品");
					$.endPageLoading();
				}
				$("#DATALINE_OPER_TYPE").val(operType);
				operTypeOnChange();
				$.endPageLoading();
				showPopup('mypop','chooseOfferItem',true)
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });	
		
	
}

function updateForProductIdB() {
	// 校验所有的输入框  	
	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	var groupId = groupInfo.get("GROUP_ID");
	if(""==groupId|| groupId==null){
		 $.validate.alerter.one($("#cond_GROUP_ID_INPUT")[0], "集团号码为空，请输入!\n");
		 return false;
	}
	var productId = $("#pattr_PRODUCT_ID_B").val();
	if(""==productId|| productId==null){
		MessageBox.alert("提示信息","请先选择需要过户的产品！");
		return false;
	}
	var groupIdA =$("#GROUP_ID_A").val();
	var groupIdB =$("#cond_GROUP_ID").val();
	var productIdA =$("#OFFER_CODE").val();
	if(productId!=productIdA&&groupIdA!=groupIdB){
		MessageBox.alert("提示信息","跨集团过户和跨产品过户只能使用其中一种方式！");
		return false;
	}
	var groupIdB = $("#cond_GROUP_ID_INPUT").val();
	if("" == groupIdB || null==groupIdB){
		MessageBox.alert("提示信息","请选择归属集团！");
		return;
	}
	var isReadonly = $("#IS_READONLY").val();
	var grpSerialNumber = $("#pattr_GRP_SERIAL_NUMBER_B").val();
	var operType = $("#DATALINE_OPER_TYPE").val();
	$.beginPageLoading("数据查询中......");
	
			$.ajax.submit('', 'queryCustGroupByGroupId','&GROUP_IDA='+groupId+"&PRODUCT_ID="+productId+'&GROUP_IDB='+groupIdB+"&IS_READONLY="+isReadonly+"&GRP_SN="+grpSerialNumber,"grpserial", function(data){
				$("#DATALINE_OPER_TYPE").val(operType);
				operTypeOnChange();
				$.endPageLoading();

			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });	
		
	
}
function queryGroupInfoB()
{
	var groupIdB = $("#cond_GROUP_ID_INPUT").val();
	if("" == groupIdB || null==groupIdB){
		MessageBox.alert("提示信息","请选择归属集团！");
		return;
	}
	var productId = $("#pattr_PRODUCT_ID").val();
	// $.beginPageLoading("数据查询中......");
	 $.ajax.submit('', 'queryCustGroupByGroupId','&GROUP_IDB='+groupIdB+'&PRODUCT_ID='+productId,"offerPart", function(data){
			$.endPageLoading();
			if(data==null|| data==""){
				MessageBox.alert("提示信息","该归属集团没有订购该产品");
				$.endPageLoading();
			}
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });	
		

}


function chooseOfferInfo(obj){
	debugger;
	//var groupId = cond_GROUP_ID_INPUT;
	var accessNum = $(obj).attr("SERIAL_NUMBER");
	var userId = $(obj).attr("USER_ID");
	var productId = $(obj).attr("PRODUCT_ID");
	var productName = $(obj).attr("PRODUCT_NAME");
	$("#cond_OFFER_NAME").val(productName);
	$("#cond_OFFER_ID").val(productId);
	 $.beginPageLoading();
	 ajaxSubmit("","queryDatalineInfos", '&SERIAL_NUMBER=' + accessNum + '&USER_ID=' + userId+ '&PRODUCT_ID=' + productId, "QryResultPart,commonPart", function(data){
		 $.endPageLoading();	
		 if(data==null|| data==""){
				MessageBox.alert("提示信息","该集团用户专线为合账用户不能过户，请重新选择！");
				//$.endPageLoading();
			}
			backPopup(obj);
		}, function(error_code, error_info) {
			MessageBox.error(error_code, error_info);
			$.endPageLoading();
		});
	 
}
function submitChangeApply(){
	debugger;
	var rowDatas = myTable.getCheckedRowsData("TRADES");
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"您没有勾选需要修改集团的专线!");
		return false;
	}
	var res = getCheckedTableData();
	var CONTRACT_FILE_LIST  = $("#C_FILE_LIST_ATTR").text();
	if(null==CONTRACT_FILE_LIST || CONTRACT_FILE_LIST == "")
	{
		MessageBox.alert("提示信息","请上传过户依据！");
		return;
	}
	
	var ATTACH_FILE_LIST  = $("#ATTACH_FILE_LIST").text();
	if(null==ATTACH_FILE_LIST || ATTACH_FILE_LIST == "")
	{
		MessageBox.alert("提示信息","请上传业务协议！");
		return;
	}
	/*if(!$.validate.verifyAll("groupBasePart")){
		return false;
	}*/
	if(!$.validate.verifyAll("GrpCustPart")){
		return false;
	}
	if(!$.validate.verifyAll("offerPart")){
		return false;
	}
	if(!$.validate.verifyAll("commonPart")){
		return false;
	}
	if(!$.validate.verifyAll("OrderPart")){
		return false;
	}
	
	var datalinOperType = $("#DATALINE_OPER_TYPE").val();
	//alert(datalinOperType);
	if(datalinOperType != "1"){
		if(!$.validate.verifyAll("QryAllPart")){
			return false;
		}
	}
	
	// 校验所有的输入框  	
	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	var groupId = groupInfo.get("GROUP_ID");
	var eomsAttrData = new Wade.DatasetList();
	var directlineTypeData = new Wade.DatasetList();
	var offerChaData = new Wade.DataMap();
	var orderData =  new Wade.DataMap();
	var MebOfferCha = new Wade.DatasetList();
	var attach = new Wade.DatasetList();
	var contractList =  new Wade.DatasetList();
	//var offerData = new Wade.DataMap();
	eomsAttrData = saveGroupData(eomsAttrData); //集团公共信息
	eomsAttrData = saveOrderInfo(eomsAttrData);//订单级信息
	eomsAttrData = saveCommonInfo(eomsAttrData);//公共信息
	eomsAttrData = saveOfferPart(eomsAttrData); //商品信息
	orderData = saveOrderData(orderData);//订单级信息
	directlineTypeData = getTables(directlineTypeData);//获取专线表格信息
	attach = saveAttachInfo(attach); //获取附件信息
	offerChaData = saveDataLineOfferChas();//product_sub表信息
//	var datalinOperType = $("#DATALINE_OPER_TYPE").val();
	if(datalinOperType != "1"){
		var acctName = "";
		var acctDeal = $("#cond_ACCT_DEAL").val();
		if(acctDeal == 1)
		{//账户合户
			acctName = $("#ACCT_COMBINE_ID").text()
		}else
		{
			var acctInfo = pageData.getAccountInfo();
			acctName = acctInfo.get("ACCT_NAME");
		}
		if(acctName == null || acctName == ""){
			MessageBox.alert("错误","您未新增账户或选择已有账户，请重新操作后再提交！");
			return false;
		}
		
		eomsAttrData = saveAttrAcctInfo(eomsAttrData);//账户信息
		contractList = saveAttrContractInfo();//合同信息
	}
	//MebOfferCha =saveOfferChas(MebOfferCha);
	offerChaData.put("OFFER_CODE",$("#OFFER_CODE").val());
	offerChaData.put("OFFER_CODE_B",$("#pattr_PRODUCT_ID_B").val());
	offerChaData.put("OFFER_NAME",$("#OFFER_NAME").val());
	offerChaData.put("OFFER_ID",$("#OFFER_ID").val());
	offerChaData.put("USER_ID",$("#pattr_USER_ID_A").val());
	//offerChaData.put("OFFER_CHA",MebOfferCha);
	var isReadonly = $("#IS_READONLY").val();
	var contractId =  $("#CONTRACT_ID").val();
	//submitParam.put("IS_READONLY",isReadonly);
	var ibsysId = $("#IBSYSID").val();
	var busiformId = $("#BUSIFORM_ID").val(); 
	var message = "";
	if(isReadonly=="true"){
		message="提交成功";
	}else{
		message="流程创建成功";
	}
	MessageBox.confirm("提示信息", "过户功能使用提示：1、专线资料于资管系统返回CRM系统的当月生效。2、专线计费于资管系统返回CRM系统次月生效。3、欠费或预存款保留在原归属集团的账户下，请通过调账工单处理。4、执行不同产品过户时，费用账目将当月立即生效。", function(btn){
		if("ok" == btn)
		{
			 $.beginPageLoading("流程提交中...");
			 ajaxSubmit("","submit", '&ROWDATAS=' + rowDatas +'&MEBSIZE='+ res +'&ATTR_FILE='+ CONTRACT_FILE_LIST +'&BUSI_FILE='+ BUSI_FILE +"&ATTR_LIST="+eomsAttrData+"&DIRECTLINE_DATA="+directlineTypeData+"&OFFER_DATA="+offerChaData.toString()+"&ORDER_DATA="+orderData.toString()+"&ATTACH_LIST="+attach+"&GROUP_ID="+groupId+"&IS_READONLY="+isReadonly+"&IBSYSID="+ibsysId+"&CONTRACT_DATA="+contractList+"&CONTRACT_ID="+contractId+"&BUSIFORM_ID="+busiformId, "", function(data){
						$.endPageLoading();	
						if(data.get("ASSIGN_FLAG") == "true")
						{
							MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
								if("ext1" == btn){
									debugger;
									var urlArr = data.get("ASSIGN_URL").split("?"); 
									var pageName = getNavTitle(); 
									openNav('指派', urlArr[1].substring(13), '', '', urlArr[0]); 
									closeNavByTitle(pageName);
								}
						if("ok" == btn){
							closeNav();
						}
						}, {"ext1" : "指派"});
						}else if(data.get("ALERT_FLAG")== "true"){
					MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
						if("ext1" == btn){
							var urlArr = data.get("ALERT_URL").split("?");
							var ALERT_NAME = data.get("ALERT_NAME");
							var pageName = getNavTitle();
							openNav(ALERT_NAME, urlArr[1].substring(13), '', '', urlArr[0]); 
							closeNavByTitle(pageName);
						}
						if("ok" == btn){
							closeNav();
						}
					}, {"ext1" : "下一步"});
				}
				else
				{
					MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
						if("ok" == btn){
							closeNav();
						}
					});
				}
			}, function(error_code, error_info) {
				MessageBox.error(error_code, error_info);
				$.endPageLoading();
			});
		}
	});
}

function getCheckedTableData()
{
	var chk = document.getElementsByName("TRADES");
	
	if(chk) {
		for(var i = 0;i < chk.length; i++){
            if(chk[i].checked){
                if(i == chk.length - 1){
                    return true;
                }
            }
       	}
		
	}
	return false;
}

function saveGroupData(eomsAttrData) {
	debugger;
	var chaSpecObjs = $("#groupBasePart input");

	for (var i = 0, size = chaSpecObjs.length; i < size; i++) {
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue = "";
		if (chaSpecObjs[i].type == "checkbox" || chaSpecObjs[i].type == "radio") {
			chaValue = chaSpecObjs[i].checked ? 1 : 0;
			chaSpecCode = chaSpecObjs[i].name;
		} else {
			chaValue = $("#" + chaSpecCode).val();
		}

		var offerChaSpecData = new Wade.DataMap();
		offerChaSpecData.put("ATTR_VALUE", chaValue);
		offerChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
		offerChaSpecData.put("ATTR_CODE", chaSpecCode.substring(5));

		eomsAttrData.add(offerChaSpecData);
	}
	var input = new Wade.DataMap();
	var productName = $("#cond_OFFER_NAME").val();
	input.put("ATTR_VALUE",productName);
	input.put("ATTR_NAME","业务类型（原产品名称）");
	input.put("ATTR_CODE","PRODUCTNAME");
	eomsAttrData.add(input);
	
	
	return eomsAttrData;
}

function saveOrderInfo(eomsAttrData){
	var orderInfos = $("#OrderPart input");
	for(var i = 0, size = orderInfos.length; i < size; i++)
	{
		var orderCode = orderInfos[i].id;
		var orderValue = $("#"+orderCode).val();
		
		var orderInfoData = new Wade.DataMap();
		orderInfoData.put("ATTR_VALUE", orderValue);
		orderInfoData.put("ATTR_NAME", orderInfos[i].getAttribute("desc"));
		orderInfoData.put("ATTR_CODE", orderCode.substring(6));
		
		eomsAttrData.add(orderInfoData);
	}
	return eomsAttrData;
}

function saveCommonInfo(eomsAttrData){
	var commonInfos = $("#commonPart input");
	for(var i = 0, size = commonInfos.length; i < size; i++)
	{
		var orderCode = commonInfos[i].id;
		var orderValue = $("#"+orderCode).val();
		
		var orderInfoData = new Wade.DataMap();
		orderInfoData.put("ATTR_VALUE", orderValue);
		orderInfoData.put("ATTR_NAME", commonInfos[i].getAttribute("desc"));
		orderInfoData.put("ATTR_CODE", orderCode.substring(6));
		
		eomsAttrData.add(orderInfoData);
	}
	return eomsAttrData;
	
}

function getTables(eomsAttrData){
	debugger;
	var tableList = myTable.getCheckedRowsData("TRADES");
	var dataset = new Wade.DatasetList();
    for (var i = 0; i < tableList.length; i++)
    {
    	var attrData  =  new Wade.DatasetList();
        var data = tableList.get(i);
        for(var j = 0;j < data.items.length;j++){
    	  var lineData =  new Wade.DataMap();
   		  lineData.put("ATTR_VALUE", data.items[j]);
   		  lineData.put("ATTR_CODE", data.keys[j]);
   		  lineData.put("ATTR_NAME", "");
   		  attrData.add(lineData);
        }
        eomsAttrData.add(attrData);
      //  dataset.add(data);
       // eomsAttrData.add(data);
    }
  //  eomsAttrData.add(attrData);
    return eomsAttrData;
}

//保存普通专线产品参数
function saveDataLineOfferChas()
{
	debugger;
	var offerChaList = new Wade.DatasetList();
	var mebOfferCode = $("#MEB_OFFER_CODE").val();
	var tableList = myTable.getCheckedRowsData("TRADES");
	var dataset = new Wade.DatasetList();
    for (var i = 0; i < tableList.length; i++)
    {
    	var MebOfferCha  = new Wade.DataMap();
        var data = tableList.get(i);
        for(var j = 0;j < data.items.length;j++){
   		  	if("USER_ID_B" == data.keys[j]){
   		  		MebOfferCha.put("USER_ID",data.items[j]);
   		  	}
   		  	if("SERIAL_NUMBER_B" == data.keys[j]){
		  		MebOfferCha.put("SERIAL_NUMBER",data.items[j]);
		  	}
        }
      //  MebOfferCha.put("OFFER_CHA",attrData);
        MebOfferCha.put("OFFER_CODE",mebOfferCode);
        MebOfferCha.put("OFFER_NAME",$("#MEB_OFFER_NAME").val());
        MebOfferCha.put("OFFER_TYPE","P");
		MebOfferCha.put("OFFER_ID",$("#MEB_OFFER_ID").val());
		MebOfferCha.put("OPER_CODE", "2");
        offerChaList.add(MebOfferCha);
    }
		
	var result = new Wade.DataMap();
	result.put("SUBOFFERS", offerChaList);
	return result;
}

//保存普通专线产品参数
function saveOfferChas(MebOfferCha)
{ 
	debugger;
	var tableList = myTable.getCheckedRowsData("TRADES");
	var dataset = new Wade.DatasetList();
	
    for (var i = 0; i < tableList.length; i++)
    {
    	var attrData = new Wade.DatasetList();
        var data = tableList.get(i);
        for(var j = 0;j < data.items.length;j++){
        	var lineData =  new Wade.DataMap();
   		  	lineData.put("ATTR_VALUE", data.items[j]);
   		  	lineData.put("ATTR_CODE", data.keys[j]);
   		  	lineData.put("ATTR_NAME", "");
   		  	attrData.add(lineData);
        }
        MebOfferCha.add(attrData);
    }
	return MebOfferCha;
}

function saveOrderData(eomsAttrData){
	var orderInfos = $("#OrderPart input");
	for(var i = 0, size = orderInfos.length; i < size; i++)
	{
		var orderCode = orderInfos[i].id;
		var orderValue = $("#"+orderCode).val();
		eomsAttrData.put(orderCode.substring(6),orderValue);
	}
	return eomsAttrData;
}

function saveOfferPart(eomsAttrData){
	var commonInfos = $("#offerPart input");
	for(var i = 0, size = commonInfos.length; i < size; i++)
	{
		var orderCode = commonInfos[i].id;
		var orderValue = $("#"+orderCode).val();
		
		var orderInfoData = new Wade.DataMap();
		orderInfoData.put("ATTR_VALUE", orderValue);
		orderInfoData.put("ATTR_NAME", commonInfos[i].getAttribute("desc"));
		orderInfoData.put("ATTR_CODE", orderCode.substring(6));
		
		eomsAttrData.add(orderInfoData);
	}
	return eomsAttrData;
}

function saveAttachInfo(attach){
	//其他附件
	var attachList = new Wade.DatasetList($("#ATTACH_FILE_LIST").text());
	for(var i = 0, size = attachList.length; i < size; i++)
	{
		attachList.get(i).put("REMARK", $("#ATTACH_REMARK").val()); 
	}
	BUSI_FILE = attachList.toString();
	//合同附件
	var contractFile = new Wade.DataMap($("#C_FILE_LIST_ATTACH").text());
	if(contractFile.items!=""){
		attachList.add(contractFile);
	}
	var datalinOperType = $("#DATALINE_OPER_TYPE").val();
	if(datalinOperType != "1"){
		//合同附件
		var contractFilecontract = new Wade.DataMap($("#C_FILE_LIST").text());
		if(contractFile.items!=""){
			attachList.add(contractFilecontract);
		}
	}
	return attachList;	
}

function operTypeOnChange()
{
	var datalineOperType = $("#DATALINE_OPER_TYPE").val();
	if(datalineOperType == 0)
	{//新增-新增集团产品用户
		$("#DATALINE_SERIAL_NUMBER").css("display", "none");
		$("#pattr_GRP_SERIAL_NUMBER_B").attr("nullable", "yes");
		$("#pattr_GRP_SERIAL_NUMBER_B").val("");
		
		$("#acctDealInfoGroupChange").css("display", "");
		$("#CONTRACT_INFO").css("display", "");
	}
	else
	{//已有-在已有集团产品用户下新增专线
		$("#DATALINE_SERIAL_NUMBER").css("display", "");
		$("#pattr_GRP_SERIAL_NUMBER_B").attr("nullable", "no");
		$("#acctDealInfoGroupChange").css("display", "none");
		$("#CONTRACT_INFO").css("display", "none");
		
	}
}

function initAttrAcctData(){
	var acctDeal = $("#cond_ACCT_DEAL").val();
	$.each($("#cond_ACCT_DEAL_span span"),function(){
		var idx = this.getAttribute("idx");
		if(idx==acctDeal){
			this.setAttribute("class","e_segmentOn");
			return false;
		}
	});
	
	var acctInfo = new Wade.DataMap($("#APPLY_ACCT_INFO").text());
	
	if(acctDeal == "1"){
		var custId = $("#cond_CUST_ID").val();
		var acctId = acctInfo.get("ACCT_ID");
		$.beginPageLoading("数据加载中......");
		$.ajax.submit("", "queryEcAccountList", "CUST_ID="+custId, "ecAccountListPart", function(data){
			$.endPageLoading();
			
			if(acctId)
			{
				$("#"+acctId).addClass("checked");
			}
			
			//showPopup('popup', 'chooseEcAccount', true);
			
			var acctName = acctInfo.get("ACCT_NAME");
			if(acctName == undefined){
				acctName = "";
			}
			var html = "<span class='text' id='ACCT_NAME_span'>"+acctName+"</span>";
			html += "<span id='ACCT_COMBINE_ID' style='display:none'>"+acctId+"</span>";
			$("#i_acctCombPart .value").html(html);
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
		
	}else if(acctDeal == "0"){
		debugger;
		var acctName = acctInfo.get("ACCT_NAME");
		if(acctName == undefined){
			acctName = "";
		}
		$("#i_acctSelPart .label").text("账户名称");
		$("#i_acctSelPart .value").html('<span class="text" id="ACCT_NAME_span">'+acctName+'</span>');
		
		$("#accountPopupItemGroupAttr_ACCT_NAME").val(acctName);
		var acctType = acctInfo.get("ACCT_TYPE");
		$("#accountPopupItemGroupAttr_ACCT_TYPE").val(acctType);
		if(acctType==1||acctType==2||acctType==3){
			$("#accountPopupItemGroupAttr_CONTRACT_ID").val(acctInfo.get("ACCT_CONTRACT_ID"));
			$("#accountPopupItemGroupAttr_SUPER_BANK_CODE").val(acctInfo.get("SUPER_BANK_CODE"));
			
			changeSuperBankCode(acctInfo.get("SUPER_BANK_CODE"));
			
			//$("#accountPopupItemGroupAttr_BANK_CODE_span span").text("工商银行河南分理处");
			$("#accountPopupItemGroupAttr_BANK_CODE").attr("value",acctInfo.get("BANK_CODE"));
			//$("#accountPopupItemGroupAttr_BANK_CODE").text(acctInfo.get("BANK_NAME"));
			$("#accountPopupItemGroupAttr_BANK_ACCT_NO").val(acctInfo.get("BANK_ACCT_NO"));
		}
		pageData.setAccountInfo(acctInfo);
	}
	
	acctDealChangeGroupAttr(document.getElementById("cond_ACCT_DEAL"));
	
}

function acctDealChangeGroupAttr(el)
{
	var acctDeal = el.value;
	if(acctDeal == "1")
	{
		$("#i_acctCombPart").css("display", "");
		$("#i_acctSelPart").css("display", "none");
	}
	else
	{
		$("#i_acctCombPart").css("display", "none");
		$("#i_acctSelPart").css("display", "");
	}
}

function showAcctAddPopupGroupAttr(el) {
	var offerCode=$("#cond_OFFER_CODE").val();
	/*var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var custId= custInfo.get("CUST_ID");*/
	/*if("9898"==offerCode||"7345"==offerCode){
		openNav("集团账户管理", "igroup.creategroupacct", "queryAcctInfoList", "&PRODUCT_ID="+offerCode+"&CUST_ID="+custId, "/order/iorder");
		$("#cond_ACCT_DEAL").val("1");
		$("#i_acctCombPart").css("display", "");
		$("#i_acctSelPart").css("display", "none");
	}else{*/
		showPopup('popup', 'accountPopupItemGroupAttr', true);
		accountPopupItemGroupAttr.showAddPopup();
		var data = {};
		data["ACCT_NAME"] = createAcctName();
		accountPopupItemGroupAttr.fillAcctPopup(data);
	/*}*/
}

//合户操作：显示账户列表
function showAcctCombPopupGroupAttr(el)
{
	
	/*alert($.enterpriseLogin.getInfo().get("CUST_INFO"));
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var custId = custInfo.get("CUST_ID");*/
	var custId = $("#cond_CUST_ID").val();
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryEcAccountList", "CUST_ID="+custId, "ecAccountListPart", function(data){
		$.endPageLoading();
		var acctId = $("#ACCT_COMBINE_ID").html();
		if(acctId)
		{
			$("#"+acctId).addClass("checked");
		}
		
		showPopup('popup', 'chooseEcAccount', true);
		
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
}

//合户操作：选择付费账户
function selectAccountGroupAttr(el)
{
	$("#ecAccountUL li").each(function(){
		$(this).removeClass("checked");
	});
	$(el).addClass("checked");
	
	var acctId = $(el).attr("id");
	var acctName = $(el).children().find("div[class=title]").html();
	var html = "<span class='text' id='ACCT_NAME_span'>"+acctName+"</span>";
	html += "<span id='ACCT_COMBINE_ID' style='display:none'>"+acctId+"</span>";
	$("#i_acctCombPart .value").html(html);
	
	hidePopup('popup');
}

//生成账户名称：集团名称_商品名称
function createAcctName()
{	
	var acctName = $("#cond_CUSTOMNAME").val();
	
	if(acctName&&acctName.length > 100)
	{
		acctName = acctName.substring(0, 99);
	}

	return acctName;
}

//生成账户名称：集团名称_商品名称
function getAcctName()
{	
	var acctName = $("#ACCT_COMBINE_ID").html();
	
	if(acctName&&acctName.length > 100)
	{
		acctName = acctName.substring(0, 99);
	}

	return acctName;
}

/**
 * 账户新增回调
 */
function accountPopupItemCallbackGroupAttr(data) {
	var acctInfo = data.get("ACCT_INFO");
	pageData.setAccountInfo(acctInfo);
	var acctName = $("#accountPopupItemGroupAttr_ACCT_NAME").val(); 
	// 改变选择框样式
	if (data) {
		$("#i_acctSelPart .label").text("账户名称");
		$("#i_acctSelPart .value").html('<span class="text" id="ACCT_NAME_span">'+acctName+'</span>');
	}
}


function ifNewContract(){
	debugger;
	var operType =  $("#DATALINE_OPER_TYPE").val();
	var ifNew =  $("#C_IF_NEW_CONTRACT").val();
	if(operType== '0' && ifNew=='1'){
		MessageBox.alert("提示信息", "产品服务为新增,合同只可新增！");
		$("#C_IF_NEW_CONTRACT").val("");
		return false;
	}
//	if(operType== '1' && ifNew=='0'){
//		MessageBox.alert("提示信息", "产品服务为已有,合同只可选择已有！");
//		$("#C_IF_NEW_CONTRACT").val("");
//		return false;
//	}
	var param = '';
	
	//审核不通过，重回APPLY节点记号
	var isReadonly = '';
	isReadonly = $("#IS_READONLY").val();
	if(isReadonly){
		param += "&IS_READONLY="+isReadonly;
		param += "&IBSYSID="+$("#IBSYSID").val();
		param += "&NODE_ID="+$("#NODE_ID").val();
	}
	var custId = $("#cond_GROUP_ID_INPUT").val();
	param += "&CUST_ID="+custId;
	param += "&IF_NEW="+ifNew;
	var custId = $("#cond_CUST_ID").val();
							 						   
	if(ifNew=='0'){//新增
		$("#newContractPart").css("display", "block");//新增 合同信息元素
		$("#contractSelectPart").css("display", "none");//已有 合同选择元素
		$("#contractContentPart").css("display", "none");//已有 合同信息元素
		$("#C_CONTRACT").attr("nullable", "yes");//已有合同
		var node = new Wade.DataMap($("#NODE_TEMPLETE").text());
		var bpmTempletId = node.get("BPM_TEMPLET_ID");
//		$("#C_CONTRACT_NAME_OLD").attr("disabled", "false");//合同名称
//		$("#C_CONTRACT_NAME_OLD").attr("nullable", "yes");//合同名称
//		$("#C_PRODUCT_NAME").attr("nullable", "yes");//产品名称
//		$("#C_PRODUCT_START_DATE_OLD").attr("nullable", "yes");//开始时间
//		$("#C_PRODUCT_END_DATE_OLD").attr("nullable", "yes");//结束时间
		
		$.beginPageLoading("正在处理...");
		ajaxSubmit(this,'initContract',param,"newContractPart",function(data){
			$.endPageLoading();
			if(isReadonly=="true"){
				$("#C_FILE_LIST").text(data.toString());
			}
			//获取合同附件name和id
			$("#CONTRACT_FILE_LIST").afterAction(function(e, file){
				var data = new Wade.DataMap();
				data.put("FILE_ID", file.fileId);
				data.put("FILE_NAME", file.name);
				data.put("ATTACH_TYPE", "C");
				$("#C_FILE_LIST").text(data.toString());
				$("#C_FILE_LIST").val(file.fileId+':'+file.name);
				$("#C_FILE_LIST_NAME").val(file.name);
			});
		},null);
	}else if(ifNew=='1'){//已有
		$("#newContractPart").css("display", "none");//新增 合同信息元素
		$("#contractSelectPart").css("display", "block");//已有 合同选择元素
		$("#contractContentPart").css("display", "block");//已有 合同信息元素
		
		$.beginPageLoading("正在处理...");
		ajaxSubmit(this,'initContract',param,"contractSelectPart",function(data){
			$.endPageLoading();
			/*if(isReadonly=="true"){
				chooseContract();
			}*/
		},null);
	}else if(new Wade.DataMap($("#NODE_TEMPLETE").text()).get("BPM_TEMPLET_ID")=='EDIRECTLINECONTRACTCHANGE'){
		$("#newContractPart").css("display", "");
		$("#contractSelectPart").css("display", "none");
	}
	else{
		$("#newContractPart").css("display", "none");
		$("#contractSelectPart").css("display", "none");
	}
	
}

function saveAttrContractInfo(){
	var ifNewContract = $("#C_IF_NEW_CONTRACT").val();
	var contractDataset = new Wade.DatasetList();
	
	var ifNew = $("#ContractPart input");
	for(var i = 0, size = ifNew.length; i < size; i++)
	{
		var ifNewCode = ifNew[i].id;
		var ifNewValue = $("#"+ifNewCode).val();
		
		var ifNewData = new Wade.DataMap();
		ifNewData.put("ATTR_VALUE", ifNewValue);
		ifNewData.put("ATTR_NAME", ifNew[i].getAttribute("desc"));
		ifNewData.put("ATTR_CODE", ifNewCode);
		
		contractDataset.add(ifNewData);
	}
	
	if(ifNewContract=='0'){
		var newContract = $("#newContractPart input");
		for(var i = 0, size = newContract.length; i < size; i++)
		{
			var newContractCode = newContract[i].id;
			if(newContractCode==''||newContractCode=='CONTRACT_FILE_LIST_name'||newContractCode=='CONTRACT_FILE_LIST'){
				continue;
			}
			var newContractValue = $("#"+newContractCode).val();
			
			if(newContractCode=='C_CONTRACT_NAME_NEW'){
				newContractCode = 'C_CONTRACT_NAME';
			}
			
			var contractData = new Wade.DataMap();
			contractData.put("ATTR_VALUE", newContractValue);
			contractData.put("ATTR_NAME", newContract[i].getAttribute("desc"));
			contractData.put("ATTR_CODE", newContractCode);
			
			contractDataset.add(contractData);
		}
	}else if(ifNewContract=='1'){
		var contract = $("#C_CONTRACT").val();
		var contracts = $("#contractContentPart input");
		for(var i = 0, size = contracts.length; i < size; i++)
		{
			var contractsCode = contracts[i].id;
			var contractsValue = $("#"+contractsCode).val();
			
			if(contractsCode=='C_CONTRACT_NAME_OLD'){
				contractsCode = 'C_CONTRACT_NAME';
			}
			
			var contractData = new Wade.DataMap();
			contractData.put("ATTR_VALUE", contractsValue);
			contractData.put("ATTR_NAME", contracts[i].getAttribute("desc"));
			contractData.put("ATTR_CODE", contractsCode);
			
			contractDataset.add(contractData);
		}
		var newContract = $("#newContractPart input");
		for(var i = 0, size = newContract.length; i < size; i++)
		{
			var newContractCode = newContract[i].id;
			if(newContractCode==''||newContractCode=='CONTRACT_FILE_LIST_name'||newContractCode=='CONTRACT_FILE_LIST'){
				continue;
			}
			var newContractValue = $("#"+newContractCode).val();
			
			if(newContractCode=='C_CONTRACT_NAME_NEW'){
				newContractCode = 'C_CONTRACT_NAME';
			}
			
			var contractData = new Wade.DataMap();
			contractData.put("ATTR_VALUE", newContractValue);
			contractData.put("ATTR_NAME", newContract[i].getAttribute("desc"));
			contractData.put("ATTR_CODE", newContractCode);
			
			contractDataset.add(contractData);
		}
	}else{
		var contract = $("#C_CONTRACT").val();
		var contracts = $("#newContractPart input");
		for(var i = 0, size = contracts.length; i < size; i++)
		{
			var contractsCode = contracts[i].id;
			if(contractsCode!=null&&contractsCode!=''){
				var contractsValue = $("#"+contractsCode).val();
				
				var contractData = new Wade.DataMap();
				contractData.put("ATTR_VALUE", contractsValue);
				contractData.put("ATTR_NAME", contracts[i].getAttribute("desc"));
				contractData.put("ATTR_CODE", contractsCode);
				
				contractDataset.add(contractData);
			}
			
		}
		var contractLineInfo = $("#contractLineInfoPart span[class=value]");
		var recordNum=0;
		for(var i = 0, size = contractLineInfo.length; i < size; i++)
		{
			var contractsCode = contractLineInfo[i].id;
			if(contractsCode!=null&&contractsCode!=''){
				if(contractsCode=='NOTIN_LINE_NO'){
					recordNum++;
				}
				var contractsValue = $("#"+contractsCode).text();
				
				var contractData = new Wade.DataMap();
				contractData.put("ATTR_VALUE", contractsValue);
				contractData.put("ATTR_NAME", "");
				contractData.put("ATTR_CODE", contractsCode);
				contractData.put("RECORD_NUM", recordNum);
				contractDataset.add(contractData);
			}
			
		}
		
	}
	return contractDataset;
}

function saveAttrAcctInfo(eomsAttrData){
	var acctDeal = $("#cond_ACCT_DEAL").val();
	var acctDealData = new Wade.DataMap();
	acctDealData.put("ATTR_CODE","ACCT_DEAL");
	acctDealData.put("ATTR_NAME","账户操作");
	acctDealData.put("ATTR_VALUE",acctDeal);
	eomsAttrData.add(acctDealData);						
	//var acctInfoDataset = new Wade.DataMap();
	if(acctDeal == 1)
	{//账户合户
		var acctId = $("#ACCT_COMBINE_ID").html();
		var acctInfoDataset1 = new Wade.DataMap();
		acctInfoDataset1.put("ATTR_VALUE", acctId);
		acctInfoDataset1.put("ATTR_NAME", "账户");
		acctInfoDataset1.put("ATTR_CODE", "ACCT_ID");
		eomsAttrData.add(acctInfoDataset1)
		var acctInfoDataset2 = new Wade.DataMap();
		acctInfoDataset2.put("ATTR_VALUE", ACTION_EXITS);
		acctInfoDataset2.put("ATTR_NAME", "账户操作编码");
		acctInfoDataset2.put("ATTR_CODE", "OPER_CODE");
		eomsAttrData.add(acctInfoDataset2);
		var acctNameData = new Wade.DataMap();
		acctNameData.put("ATTR_VALUE", $("#ACCT_NAME_span").text());
		acctNameData.put("ATTR_NAME", "账户名称");
		acctNameData.put("ATTR_CODE", "ACCT_NAME");
		eomsAttrData.add(acctNameData);					 
	}
	else
	{//账户新增
		//var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
		var acctInfo = pageData.getAccountInfo();
//		alert(acctInfo);
		if (acctInfo && acctInfo.length > 0) {
			var contractId = acctInfo.get("CONTRACT_ID");
			acctInfo.put("ACCT_CONTRACT_ID",contractId);
			acctInfo.removeKey("CONTRACT_ID");					 
		} else {
			var data = new $.DataMap();
			acctInfo = new $.DataMap();
			acctInfo.put("ACCT_NAME", getAcctName());
			acctInfo.put("ACCT_TYPE", "0");
		}
		
		acctInfo.eachKey(function(key, index, totalCount){
			var acctInfoData = new Wade.DataMap();
			acctInfoData.put("ATTR_CODE", key);
			acctInfoData.put("ATTR_VALUE", acctInfo.get(key));
			eomsAttrData.add(acctInfoData)
		});		
	}
	return eomsAttrData;
}
