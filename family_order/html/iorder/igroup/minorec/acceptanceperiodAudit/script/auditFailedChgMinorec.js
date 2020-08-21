var ACTION_CREATE = "0";
var ACTION_DELETE = "1";
var ACTION_UPDATE = "2";
var ACTION_EXITS = "3";
var ACTION_PASTE = "5"; // 暂停
var ACTION_CONTINUE = "6"; // 恢复
var ACTION_PREDESTROY = "7"; // 预取消
var ACTION_PREDSTBACK = "8"; // 冷冻期恢复
var contractVw ="";  //一单清商铺调电子协议标记
var productIdInfo ="";  //一单清商铺拼接产品ID
var userIdAsG ="";//已有协议的所有USERID
var productAsG ="";//已有协议的所有产品ID
var delEcOfferList = new Wade.DatasetList();//存放删除集团的数据
var delMebList = new Wade.DatasetList();//存放删除宽带成员的数据

$(function(){
	var groupId = $.trim($("#cond_GROUP_ID_INPUT").val());
	queryGroup(groupId);
	if(!groupId)
	{
		return ;
	}
	
	var templetId = $("#cond_TEMPLET_ID").val();
	$("#cond_TEMPLET_ID_BL").val(templetId);
	$("#cond_MINOREC_BPM_PRODUCTID").val(templetId);
	
	delMebList = new Wade.DatasetList($("#WIDE_DELMBR_LIST").text());
	
});

function queryGroup(groupId)
{
	var param = "GROUP_ID="+groupId;
	var checkTag = $("#checkTag").val();

	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "queryCustGroupByGroupId", param, "groupBasePart,moreCustPart", function(data){

		if(!$.os.phone){
			$.enterpriseLogin.refreshGroupInfo(groupId);
		}
		$.endPageLoading();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//选择电子合同
function contractInfo(flag){
	var groupId = $("#cond_GROUP_ID").val();
	if(groupId == null || groupId == ""){
		$.validate.alerter.one($("cond_GROUP_ID_INPUT")[0], "未获取到集团信息，请输入集团编码后回车键查询！");
		$("#cond_TEMPLET_ID").val("");
		return false;
	}
	
	var templetId = $("#cond_TEMPLET_ID").val();
	if(undefined==templetId||""==templetId||null==templetId){
		$.validate.alerter.one($("#cond_TEMPLET_ID")[0], "未选择产品，请先选择产品！");
		return false;
	}
	
	var groupId = $("#cond_GROUP_ID_INPUT").val();
	var offerCode = $("#cond_OFFER_CODE").val();
	$("#cond_MINOREC_BPM_PRODUCTID").val(templetId);
	var minorecProductid =$("#cond_MINOREC_BPM_PRODUCTID").text();
	var contractId = $("#CONTRACT_ID").val();
	if("FUSECOMMUNICATIONCHANGE"==templetId){
		minorecProductid ="VP998001";
	}else  if("YIDANQINGJIUDIAANCHANGE"==templetId){
		minorecProductid ="VP66666";
	}else  if("YIDANQINGSHANGPUCHANGE"==templetId&&"1"==flag){
		minorecProductid ="VP99999";
		contractVw="1";
	}else  if("YIDANQINGSHANGPUCHANGE"==templetId&&"2"==flag){
		minorecProductid ="8000";
		contractVw="2";
		contractId = $("#CONTRACT_ID_VW").val();
	}
	
	var pram = "&PRODUCT_ID="+minorecProductid+"&GROUP_ID="+groupId+"&insertpage="+"true"+"&CONTRACT_ID="+contractId+"&NEED_CHECKE=true";
		pram += "&ACTION=init"+"&IBSYSID="+$("#IBSYSID").val()+"&NODE_ID="+$("#NODE_ID").val();
	
	$.ajax.submit("", "", pram,"ElecAgreementAdd", function(data){
		$.endPageLoading();
		if(data.get("MINOREC_PRODUCT_INFO")){
			$("#MINOREC_PRODUCT_INFO").val(data.get("MINOREC_PRODUCT_INFO"));
		}
		showPopup('popup04', 'ElecAgreementAdd');
		},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	},{async:true});
	
}


//电子合同回调方法
function afterAct(){

    var contractInfo = new Wade.DataMap($("#MINOREC_PRODUCT_INFO").val());
    var productInfo = contractInfo.get("PRODUCT_ID");
	var templetId = $("#cond_TEMPLET_ID").val();
    if("YIDANQINGSHANGPUCHANGE"==templetId){
   	 if(""==productIdInfo){
   	    	productIdInfo = productInfo;
   	    }else if(productIdInfo.indexOf("8000") >= 0&&"8000"!=productInfo){
   	    	productInfo="8000,"+productInfo;
   	    	productIdInfo=productInfo;
   	    }else if(productIdInfo.indexOf("8000") < 0&&"8000"!=productInfo){
   	    	productIdInfo = productInfo;
   	    }else if("8000"==productInfo&&productIdInfo!=productInfo&&productIdInfo.indexOf("8000") < 0){
   	    	productInfo="8000,"+productIdInfo;
   	    	productIdInfo=productInfo;
   	    }else{
   	    	productInfo = productIdInfo;
   	    }
   	 
   }

    var data = new Wade.DataMap();
	data.put("CONTRACT_ID", contractInfo.get("AGREEMENT_ID"));
	data.put("CONTRACT_NAME", contractInfo.get("ARCHIVES_NAME"));
	//取合同信息的生失效时间和对应的产品用于提交判断
	data.put("OFFER_IDS", contractInfo.get("PRODUCT_ID"));
	data.put("CONTRACT_END_DATE", contractInfo.get("CONTRACT_END_DATE"));
	data.put("CONTRACT_WRITE_DATE", contractInfo.get("CONTRACT_WRITE_DATE"));
	
	var groupId = $("#cond_GROUP_ID_INPUT").val();
	var operType = $("#oattr_OPER_TYPE").val();

	var userIdAs = $("#USER_ID_AS").val();//已有协议的userid
	var userIdAsVw = $("#USER_ID_AS_VW").val();//已有协议的V网userid
	var productAs = $("#EC_PRODUCT_ID_AS").val();//已有协议订购的产品
	var productAsVw = $("#EC_PRODUCT_ID_AS_VW").val();//已有协议V网产品
	
	var part = "contractInfoPart";
	if("YIDANQINGSHANGPUCHANGE"==templetId&&"1"==contractVw){
		if(""==userIdAsG){
			userIdAsG = userIdAs;
		}else if(userIdAsG!=userIdAs){
			userIdAsG =userIdAsG+","+userIdAs;
		}
		
		if(""==productAsG){
			productAsG = productAs;
		}else if(productAsG!=productAs){
			productAsG =productAsG+","+productAs;
		}
	}else  if("YIDANQINGSHANGPUCHANGE"==templetId&&"2"==contractVw){
		if(""==userIdAsG){
			userIdAsG = userIdAsVw;
		}else if(userIdAsG!=userIdAsVw){
			userIdAsG =userIdAsG+","+userIdAsVw;
		}
		if(""==productAsG){
			productAsG = productAsVw;
		}else if(productAsG!=productAsVw){
			productAsG =productAsG+","+productAsVw;
		}
		part = "contractVwPart";
	}else{
		userIdAsG = userIdAs;
		productAsG = productAs;
	}
	
	var flag = "";//判断是否手机端登陆，给标记
	if ($.os.phone) {
		flag = "PHONE";
	} else {
		flag = "PC";
	}
	 var param ="&FLAG="+flag+"&CONTRACT_PRODUCT_ID="+productInfo+"&USER_ID_AS="+userIdAsG+"&PRODUCT_ID_AS="+productAsG+"&DATE_OFFERCODE="+data+"&OPER_TYPE="+operType+"&DATEMAP="+data+"&TEMPLET_ID="+templetId+"&GROUP_ID="+groupId;

	 $.beginPageLoading("数据查询中...");
		$.ajax.submit("", "setOperType",  param ,part, function(data){
			$.endPageLoading();
				if("AddMeb"==operType){
					$("#WIDE_PRODUCT_ID").attr("disabled", true);
				}
			},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
}


//标准地址查询
function addressSelect(){
	var offerData = new Wade.DataMap($("#CHILD_OFFER_DATA_"+"110000007341").text());
	if(0==offerData.length){
		MessageBox.error("提示信息", "您未设置集团商务宽带产品参数，请设置后再进行地址查询！");
		return false;
	}
	var ecOfferData = offerData.get("EC_OFFER");
	var serialNumber= ecOfferData.get("SERIAL_NUMBER");
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
		pateAll = "widenetResult,productType,widePhone,WideProductType";
	}
	var groupId = $("#cond_GROUP_ID").val();
	var serialNumber= $("#AUTH_SERIAL_NUMBER").val();
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

//表格加载宽带产品
function changeWideProduct(obj){

	var productId = $("#WIDE_PRODUCT_ID").val();
	var tableList = widenetTable.getData(true);
	var tableSize = tableList.length;
	if(0<tableSize){
	    for (var i = 0; i < tableList.length; i++) {
	      var data = tableList.get(i);
	      widenetTable.updateRow({"WIDE_PRODUCT_NAME":$("#WIDE_PRODUCT_ID").text()},i);
	    }
	}
	
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "getWidenetUserOpenElement", '&PRODUCT_ID='+productId, "saleActivePartAttr", function(data){
		$.endPageLoading();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}

function removeaddMebSub(){

	var rowDatas = widenetTable.getCheckedRowsData("TRADES");
	if(rowDatas==null||rowDatas==""){
		MessageBox.error("提示信息", "您未勾选表格的数据，进行删除，请选择！");
		return false;
	}else{
		var tableList = widenetTable.getData(true);
		var rowSize = rowDatas.length;
		var tableSize = tableList.length;
		var dataset = new Wade.DatasetList(rowDatas.toString());
		for (var i = 0, size = dataset.length; i < size; i++) {
			var mebSn = dataset.get(i);
			delMebList.add(mebSn);
		}
		
		var dataset1 = new Wade.DatasetList(tableList.toString());
		$.beginPageLoading("数据删除中...");
		$.ajax.submit("", "removeaddMebSub","&DETAIL_ADDRESS_LIST="+dataset+"&DETAIL_ADDRESS_LIST1="+dataset1, "widenetResult", function(data){
			$.endPageLoading();
			 $("#WIDE_DELMBR_LIST").text(delMebList.toString());
		}, 
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
		
		if(rowSize == tableSize){
			$("#WIDE_PRODUCT_TYPE").val("");
			$("#WIDE_PRODUCT_ID").val("");
		}
	}

}

function showAcctAddPopup(el) 
{
	showPopup('popup', 'accountPopupItem', true);
	accountPopupItem.showAddPopup();
	var data = {};
	var ecChildAcctData = new Wade.DataMap($("#CHILD_EC_ACCOUNT_DATA").text());
	if(ecChildAcctData.length > 0)
	{
		data = ecChildAcctData;
	}
	else
	{
		data["ACCT_NAME"] = createAcctName();
	}
	accountPopupItem.fillAcctPopup(data);
}

//合户操作：显示账户列表
function showAcctCombPopup(el)
{
//	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	 var custInfo = "";
	    if(!$.os.phone)
	    {
	    	custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	    }else{
	    	custInfo = new Wade.DataMap($("#CUST_INFO").text());
	    }
	var custId = $("#cond_CUST_ID").val();
	
	$.beginPageLoading("数据加载中......");
	$.ajax.submit("", "queryEcAccountList", "&CUST_ID="+custId, "ecAccountListPart", function(data){
		$.endPageLoading();
		
		var ecChildAcctData = new Wade.DataMap($("#CHILD_EC_ACCOUNT_DATA").text());
		if(ecChildAcctData.length > 0)
		{
			var acctId = ecChildAcctData.get("ACCT_ID");
			if(acctId)
			{
				$("#"+acctId).addClass("checked");
			}
		}
		
		showPopup('popup', 'chooseEcAccount', true);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function accountPopupItemCallback(data) 
{
	var acctInfo = data.get("ACCT_INFO");
	$("#CHILD_EC_ACCOUNT_DATA").text(acctInfo.toString());
	
	// 改变选择框样式
	if (data) {
		$("#i_acctSelPart .label").text("账户名称");
		$("#i_acctSelPart .value").html('<span class="text" id="ACCT_COMBINE_ID">'+acctInfo.get("ACCT_NAME")+'</span>');
	}
}

//生成账户名称：集团名称_商品名称
function createAcctName()
{
//	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
//	var acctName = groupInfo.get("CUST_NAME");
//	if(!acctName){
//		acctName = groupInfo.get("GROUP_NAME");
//	}
	var acctName="";
	if(!$.os.phone){
		var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
		acctName = groupInfo.get("CUST_NAME");
		if(!acctName){
			acctName = groupInfo.get("GROUP_NAME");
		}
	}else{
		acctName = $("#cond_CUST_NAME").val();
	}
	
	if(acctName&&acctName.length > 100)
	{
		acctName = acctName.substring(0, 99);
	}
	
	return acctName;
}

//合户操作：选择付费账户
function selectAccount(el)
{
	$("#ecAccountUL li").each(function(){
		$(this).removeClass("checked");
	});
	$(el).addClass("checked");
	
	var acctId = $(el).attr("id");
	var acctName = $(el).children().find("div[class=title]").html();
	
	var acctInfo = new Wade.DataMap();
	acctInfo.put("ACCT_ID", acctId);
	acctInfo.put("ACCT_NAME", acctName);
	acctInfo.put("OPER_CODE", ACTION_EXITS);
	$("#CHILD_EC_ACCOUNT_DATA").text(acctInfo.toString());
	
	var html = "<span class='text'>"+acctName+"</span>";
	html += "<span id='ACCT_COMBINE_ID' style='display:none'>"+acctId+"</span>";
	$("#i_acctCombPart .value").html(html);
	
	hidePopup('popup');
}

//加载付费模式组件
function initPayPlanItem()
{
	queryPayPlanInfoForYDQ();

	//修改确定按钮样式
	$("#payPlanSubmitBtn").removeClass("e_button-green");
	$("#payPlanSubmitBtn").addClass("e_button-blue");
}

//付费类型组件回调
function payplanMgrCallback(payplan)
{
	if($("#CHILD_MEB_PAY_PLAN").length > 0)
	{
		$("#CHILD_MEB_PAY_PLAN").val("");
		$("#CHILD_MEB_PAY_PLAN_float").find("ul").children().each(function(){
			if($(this).attr("idx") != "0")
			{
				$(this).remove();
			}
		});
		
		if(payplan.indexOf("P") > -1)
		{
			var selHtml = "<li class='link' idx='1' title='个人付费' val='P'><div class='main'>个人付费</div></li>";
			$("#CHILD_MEB_PAY_PLAN_float").find("ul").append(selHtml);
		}
		if(payplan.indexOf("G") > -1)
		{
			var selHtml = "<li class='link' idx='2' title='集团付费' val='G'><div class='main'>集团付费</div></li>";
			$("#CHILD_MEB_PAY_PLAN_float").find("ul").append(selHtml);
		}
	}
}



//提交
function submitApply()
{

    var operType = $("#oattr_OPER_TYPE").val();  // 操作类型

    // 校验提交参数
    if(!checkSubmitParam(operType))
    {
        return false;
    }

    // 构建提交参数
    var submitData = buildSubmitData(operType);
    submitData.put("OPER_TYPE",operType);

    var message = "订单创建成功！";

    $.beginPageLoading("数据提交中......");
    $.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitData.toString(), "", function(data){
            $.endPageLoading();
            if(data.get("ASSIGN_FLAG") == "true")
            {
                MessageBox.success(message, "订单号："+data.get("IBSYSID"), function(btn){
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
            }else if(data.get("ALERT_FLAG")== "true"){
                MessageBox.success(message, "订单号："+data.get("IBSYSID"), function(btn){
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
                MessageBox.success(message, "订单号："+data.get("IBSYSID"), function(btn){
                    if("ok" == btn){
                        closeNav();
                    }
                });
            }
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}


// 校验提交数据
function checkSubmitParam(operType) {

    //1、校验集团信息
    if(!verifyAll('groupBasePart')){
        return false;
    }

    //2、校验流程信息
    if(!verifyAll('OfferPart')){
        return false;
    }

    //4、校验变更电子协议信息
    var contractId = $("#CONTRACT_ID").val();
    if(contractId == "" || contractId == null || contractId == undefined)
    {
        contractId = $("#CONTRACT_ID_VW").val();
        if(contractId == "" || contractId == null || contractId == undefined)
        {
            MessageBox.alert("提示信息", "请录入 '变更协议信息' !");
            return false;
        }
    }

    //5、校验公共信息
    var title= $("#pattr_TITLE").val();
    var urgencyLevel= $("#pattr_URGENCY_LEVEL").val();
    var auditStaff= $("#oattr_AUDITSTAFF").val();


    if(""==title||null==title||undefined==title){
        $.validate.alerter.one($("#oattr_TITLE")[0], "请填写主题！");
        return false;
    }
    if(""==urgencyLevel||null==urgencyLevel||undefined==urgencyLevel){
        $.validate.alerter.one($("#oattr_URGENCY_LEVEL")[0], "请选择紧急程度！");
        return false;
    }
    if(""==auditStaff||null==auditStaff||undefined==auditStaff){
        $.validate.alerter.one($("#oattr_AUDITSTAFF")[0], "请选择稽核员工！");
        return false;
    }

    var flag = true;
    var memberFlag = false;

    //6、校验产品信息
    if( "AddMeb" == operType || "CrtUser" == operType )
    {
        // 新增成员 ，新增集团 校验产品信息
        var errorMessage = "存在 ‘可设置’ 的商品，请录入 ！";
        $("#paramOfferPart").find("div[id*=CHILD_OFFER_DATA_]").each(function ()
        {
            var productId = this.id;
            var childData = productId.replace(/CHILD_OFFER_DATA_/g, "CHILD_OFFER_ID_");
            var offerName = $("#" + childData).attr("offer_name");

            var childOffer = new Wade.DataMap($(this).text());
            if (childOffer == null || childOffer == "" || childOffer.length == 0) {
                flag = false;
                errorMessage = "请录入 '" + offerName + "' 产品信息 ! ";
                return;
            }

            // 7、校验宽带信息
            var offerCode = $("#" + childData).attr("offer_code");
            if (offerCode == "7341")
            {
                var tableData = widenetTable.getData(true);
                if (tableData == null || tableData == "" || tableData.length == 0) {
                    flag = false;
                    errorMessage = "请录入 '宽带地址信息' !";
                    return;
                }

                var publicAttrInfo = new Wade.DatasetList($("#cond_SELECTED_ELEMENTS").text());
                if (publicAttrInfo == null || publicAttrInfo == "" || publicAttrInfo.length == 0) {
                    flag = false;
                    errorMessage = "请录入 '宽带产品信息' !";
                    return;
                }

                var hgsWide = $("#HGS_WIDE").val();
                var contactPhone = $("#CONTACT_PHONE").val();
                var contact = $("#CONTACT").val();
                var phone = $("#PHONE").val();
                var modemStyle = $("#MODEM_STYLE").val();
                var widenetPayMode = $("#WIDENET_PAY_MODE").val();
                if (hgsWide == null || hgsWide == "") {
                    errorMessage = "请录入 '开户方式' !";
                    flag = false;
                    return;
                }
                if (contactPhone == null || contactPhone == "") {
                    errorMessage = "请录入 '联系人电话' !";
                    flag = false;
                    return;
                }
                if (contact == null || contact == "") {
                    errorMessage = "请录入 '联系人' !";
                    flag = false;
                    return;
                }
                if (phone == null || phone == "") {
                    errorMessage = "请录入 '联系电话' !";
                    flag = false;
                    return;
                }
                if (modemStyle == null || modemStyle == "") {
                    errorMessage = "请录入 'MODEM方式' !";
                    flag = false;
                    return;
                }
                if (widenetPayMode == null || widenetPayMode == "") {
                    errorMessage = "请录入 '支付模式' !";
                    flag = false;
                    return;
                }
                memberFlag = true;
            }
            else
            {
                if( "AddMeb" == operType)
                {
                    // PC端必须校验 成员信息
                    var mebList = childOffer.get("MEB_LIST");
                    if (mebList != null && mebList != "" && mebList.length > 0)
                    {
                        memberFlag = true ;
                    }
                }
            }
        });
    }
    else if( "DelMeb" == operType )
    {
        $("#paramOfferPart").find("div[id*=CHILD_DELMEB_DATA_]").each(function ()
        {
            var productId = this.id;
            var childData = productId.replace(/CHILD_DELMEB_DATA_/g, "CHILD_OFFER_ID_");

            var offerName = $("#" + childData).attr("offer_name");
            var offerCode = $("#" + childData).attr("offer_code");

            if("7341" == offerCode)
            {
                //校验宽带成员信息
                var addressList = new Wade.DatasetList($("#WIDE_DELMBR_LIST").text());
                if (addressList == null || addressList == "" || addressList.length == 0) {
                    flag = false;
                    errorMessage = "请录入 '" + offerName + "' 产品需要删除的成员信息 ! ";
                    return;
                }
                memberFlag = true;
            }
            else
            {
                var childOffer = new Wade.DatasetList($(this).text());
                // PC端必须校验 成员信息
                if (childOffer != null && childOffer != "" && childOffer.length > 0)
                {
                    memberFlag = true;
                }
            }
        })
    }
    else if( "DstUser" == operType )
    {
        // 注销集团
        var delSubOfferData = new Wade.DatasetList($("#SUB_DELECOFFER_LIST").text());
        if (delSubOfferData == null || delSubOfferData == "" || delSubOfferData.length == 0)
        {
            flag = false;
            errorMessage = "请录入 需要注销的集团产品  ! ";
        }
    }
    else if( "ChgWn" == operType )
    {
        // 宽带变更
        var newProductId = $("#WIDE_PRODUCT_ID").val();
        var oldProductId = $("#WIDE_PRODUCT_ID_LS").val();
        if(newProductId == oldProductId)
        {
            flag = false;
            errorMessage = "新的宽带产品 和 老的宽带产品相同，请重新选择宽带产品 ! ";
        }
    }

    if (!flag) {
        MessageBox.alert("提示信息", errorMessage);
        return false;
    }

    if( "AddMeb" == operType || "DelMeb" == operType )
    {
        if (!memberFlag)
        {
            MessageBox.alert("提示信息", "请录入成员信息！");
            return false;
        }
    }

    return true;
}


function buildSubmitData(operType)
{

    var submitData = new Wade.DataMap();

    submitData.put("OFFER_DATA_LIST", saveOfferData());    // 商品信息
    submitData.put("OFFER_LIST", saveOfferList(operType));    // 商品信息

    submitData.put("BUSI_SPEC_RELE", saveBusiSpecReleData());        // 流程信息
    submitData.put("NODE_TEMPLETE", saveNodeTempleteData());         // 节点信息

    submitData.put("CUST_DATA", saveEcCustomer());
    submitData.put("ORDER_DATA", saveOrderData(operType));
    submitData.put("OTHER_LIST", saveOtherList());
    submitData.put("EOMS_ATTR_LIST", saveAttrList());
    submitData.put("COMMON_DATA", saveCommonData());

    return submitData;
}


function saveOfferData()
{
    var offerData = new Wade.DataMap();

    var productId = $("#TEMPLETE_BUSI_CODE").val();
    var productName = $("#cond_TEMPLET_ID").text();

    offerData.put("OFFER_CODE",productId);
    offerData.put("OFFER_NAME",productName);

    return offerData ;
}


function saveOfferList(operType) {
    var offerList = new Wade.DatasetList();
    var orderStaffId = $("#attr_ORDERSTAFFID").val();
    var orderStaffPhone = $("#attr_ORDERPHONE").val();
    if("AddMeb" == operType || "CrtUser" == operType || "ChgWn" == operType || "DelMeb" == operType)
    {
        $("#paramOfferPart").find("div[id*=CHILD_OFFER_DATA_]").each(function () {

            var childOffer = new Wade.DataMap($(this).text());

            var offerDataId = this.id;

            var offerData = new Wade.DataMap();

            if (childOffer != null && childOffer != "" && childOffer.length > 0) {
                var ecOfferInfo = childOffer.get("EC_OFFER");

                var ecOfferDataId = offerDataId.replace(/CHILD_OFFER_DATA_/g, "CHILD_OFFER_ID_");
                var bandCode = $("#" + ecOfferDataId).attr("brand_code");
                var productId = $("#" + ecOfferDataId).attr("offer_code");
                var offerName = $("#" + ecOfferDataId).attr("offer_name");

                ecOfferInfo.put("OFFER_TYPE", "P");
                ecOfferInfo.put("OFFER_NAME", offerName);
                ecOfferInfo.put("PRODUCT_NAME", offerName);
                ecOfferInfo.put("BRAND_CODE", bandCode);

                //还要把主商品的协议时间、缴费方式放到子商品数据结构中
                var ecCommonInfo = childOffer.get("EC_COMMON_INFO");

                if (!ecCommonInfo) {
                    ecCommonInfo = new Wade.DataMap();
                }

                if (bandCode == "ESPG") {
                    offerData.put("DEAL_TYPE", "ESP");
                    offerData.put("ORDER_STAFF_ID", orderStaffId);
                    offerData.put("ORDER_STAFF_PHONE", orderStaffPhone);
                    ecOfferInfo.put("OPER_TYPE", operType);
                }
                else {
                    ecCommonInfo.put("PAY_CYCLE_INFO", buildPayCycleInfo());
                }

                ecCommonInfo.put("CONTRACT_INFO", saveContractInfo(ecOfferInfo));

                offerData.put("OPER_TYPE", operType);
                offerData.put("EC_OFFER", ecOfferInfo);
                offerData.put("EC_COMMON_INFO", ecCommonInfo);

                if(childOffer.get("MEB_OFFER"))
                {
                    offerData.put("MEB_OFFER", childOffer.get("MEB_OFFER"));
                }

                if(childOffer.get("MEB_COMMON_INFO"))
                {
                    offerData.put("MEB_COMMON_INFO", childOffer.get("MEB_COMMON_INFO"));
                }

                if ("AddMeb" == operType || "CrtUser" == operType)
                {
                    if (productId == "7341")
                    {
                        offerData.put("WIDENET_OPEN_DATA", saveWideNetData());
                    }
                    else
                    {
                        if (childOffer.get("MEB_LIST"))
                        {
                            offerData.put("MEB_LIST", childOffer.get("MEB_LIST"));
                        }
                    }
                }
                else if("ChgWn" == operType)
                {
                    // 成员宽带产品变更
                    offerData.put("WIDENET_OPEN_DATA", saveChangeWideNetData());
                }
                else if ("DelMeb" == operType)
                {
                    var delMebOfferId = offerDataId.replace(/CHILD_OFFER_DATA_/g, "CHILD_MEB_OFFER_");
                    var delMebOfferData = new Wade.DataMap($("#" + delMebOfferId).text());
                    if (delMebOfferData)
                    {
                        delMebOfferData.put("EC_SERIAL_NUMBER", ecOfferInfo.get("SERIAL_NUMBER"));
                        offerData.put("MEB_OFFER", delMebOfferData);

                        // 删除成员列表
                        if (productId == "7341")
                        {
                            // 注销 成员宽带
                            offerData.put("WIDENET_OPEN_DATA", saveWideNetCancelData(delMebOfferData));
                        }
                        else
                        {
                            var delDataId = offerDataId.replace(/CHILD_OFFER_DATA_/g, "CHILD_DELMEB_DATA_");
                            var mebSubcribeList = new Wade.DatasetList($("#" + delDataId).text());
                            if (mebSubcribeList)
                            {
                                offerData.put("MEB_LIST", mebSubcribeList);
                            }
                        }
                    }
                }
                offerList.add(offerData);
            }
        });
    }
    else if("DstUser" == operType)
    {
        // 注销集团集团列表
        var delSubOfferData = new Wade.DatasetList($("#SUB_DELECOFFER_LIST").text());

        for (var i = 0; i < delSubOfferData.length; i++)
        {
            var delSubOffer = delSubOfferData[i];

            var delEcCommonInfo = new Wade.DataMap();
            delEcCommonInfo.put("CONTRACT_INFO", saveContractInfo(delSubOffer));

            var delOfferData = new Wade.DataMap();
            delOfferData.put("OPER_TYPE", operType);
            delOfferData.put("EC_COMMON_INFO", delEcCommonInfo);
            delOfferData.put("EC_OFFER", delSubOffer);

            var bandCode = delSubOffer.get("BRAND_CODE");
            if (bandCode == "ESPG") {
                delOfferData.put("DEAL_TYPE", "ESP");
                delOfferData.put("ORDER_STAFF_ID", $("#attr_DstUser_ORDERSTAFFID").val());
                delOfferData.put("ORDER_STAFF_PHONE", $("#attr_DstUser_ORDERPHONE").val());
            }

            offerList.add(delOfferData);
        }

    }

    return offerList;
}


//构建缴费信息
function buildPayCycleInfo()
{
    var payInfo = new Wade.DataMap();

    var payType = $("#cond_PAY_TYPE").val();
    payInfo.put("PAY_TYPE", payType);
    if(payType == "1")
    {//1-预付费
        payInfo.put("PAY_CYCLE", "0");
    }
    else
    {
        payInfo.put("PAY_CYCLE", $("#cond_PAY_CYCLE").val());
    }
    payInfo.put("PRO_START_DATE", $("#cond_PRO_START_DATE").val());
    payInfo.put("PRO_END_DATE", $("#cond_PRO_END_DATE").val());

    return payInfo;
}

//保存合同信息
function saveContractInfo(offerData)
{
    var templetId = $("#cond_TEMPLET_ID").val();
    var brandCode = offerData.get("BRAND_CODE");
    var contract = new Wade.DataMap();

    var contractId ;
    var contractName ;
    var writeData ;
    var endDate ;
    var offerIds ;
    if( brandCode == "VPMN" && templetId == "YIDANQINGSHANGPUCHANGE" )
    {
        contractId = $("#CONTRACT_ID_VW").val();
        contractName = $("#CONTRACT_NAME_VW").val();
        writeData = $("#CONTRACT_WRITE_DATE_VW").val();
        endDate = $("#CONTRACT_END_DATE_VW").val();
        offerIds = $("#OFFER_IDS_VW").val();
    }
    else
    {
        contractId = $("#CONTRACT_ID").val();
        contractName = $("#CONTRACT_NAME").val();
        writeData = $("#CONTRACT_WRITE_DATE").val();
        endDate = $("#CONTRACT_END_DATE").val();
        offerIds = $("#OFFER_IDS").val();
    }

    contract.put("CONTRACT_ID", contractId);
    contract.put("CONTRACT_NAME", contractName);
    contract.put("CONTRACT_WRITE_DATE", writeData);
    contract.put("CONTRACT_END_DATE", endDate);
    contract.put("OFFER_IDS", offerIds);
    return contract;
}


function saveWideNetData() {

    var wideNetInfo = saveWidenetInfo();  // 宽带基本信息

    var custInfo = saveEcCustomer();
    wideNetInfo.put("CUST_ID",custInfo.get("CUST_ID"));
    wideNetInfo.put("PRODUCT_ID",wideNetInfo.get("WIDE_PRODUCT_ID"));

    var publicAttrInfo = new Wade.DatasetList($("#cond_SELECTED_ELEMENTS").text());
    wideNetInfo.put("SELECTED_ELEMENTS",publicAttrInfo);

    var addressList = saveAddressList();
    wideNetInfo.put("ADDRESS_LIST",addressList);

    return wideNetInfo;

}




function saveWidenetInfo() {
    var hgsWide = $("#HGS_WIDE").val();                   //开户方式
    var suggestDate = $("#SUGGEST_DATE").val();           //预约施工时间
    var contact = $("#CONTACT").val();                    //联系人
    var contactPhone = $("#CONTACT_PHONE").val();          //联系人电话
    var phone = $("#PHONE").val();                         //联系电话
    var modemStyle = $("#MODEM_STYLE").val();              //MODEM方式
    var modemDeposit = $("#MODEM_DEPOSIT").val();          //押金金额
    var wideProductType   = $("#WIDE_PRODUCT_TYPE").val(); //宽带类型
    var wideProductId   = $("#WIDE_PRODUCT_ID").val();     //宽带产品
    var widenetPayMode   = $("#WIDENET_PAY_MODE").val();   //支付模式
    var openType   = $("#OPEN_TYPE").val();     //业务类型

    var wideNetInfo = new Wade.DataMap();
    wideNetInfo.put("HGS_WIDE",hgsWide );
    wideNetInfo.put("SUGGEST_DATE",suggestDate );
    wideNetInfo.put("CONTACT",contact );
    wideNetInfo.put("CONTACT_PHONE",contactPhone );
    wideNetInfo.put("PHONE",phone );
    wideNetInfo.put("MODEM_STYLE",modemStyle );
    wideNetInfo.put("MODEM_DEPOSIT",modemDeposit );
    wideNetInfo.put("WIDE_PRODUCT_TYPE",wideProductType );
    wideNetInfo.put("WIDE_PRODUCT_ID",wideProductId );
    wideNetInfo.put("WIDENET_PAY_MODE",widenetPayMode );
    wideNetInfo.put("TRADE_TYPE_CODE","600" );
    wideNetInfo.put("EPARCHY_CODE","0898" );
    wideNetInfo.put("OPEN_TYPE",openType );

    return wideNetInfo;

}

//获取地址信息列表
function saveAddressList() {

    var tableData = widenetTable.getData(true);
    return tableData;
}


// 宽带成员注销 参数拼接
function saveWideNetCancelData(delMebOfferData)
{
    var wideNetInfo = saveCancelWidenetBase();  // 宽带基本信息

    var custInfo = saveEcCustomer();
    wideNetInfo.put("CUST_ID",custInfo.get("CUST_ID"));
    wideNetInfo.put("PRODUCT_ID",delMebOfferData.get("OFFER_CODE"));

    var addressList = new Wade.DatasetList($("#WIDE_DELMBR_LIST").text());   // 注销成员列表
    wideNetInfo.put("ADDRESS_LIST",addressList);

    return wideNetInfo;

}

function saveCancelWidenetBase()
{
    var wideNetInfo = new Wade.DataMap();
    wideNetInfo.put("AUTH_CHECK_PSPT_TYPE_CODE","0" );

    wideNetInfo.put("AUTH_USER_SCORE","0" );
    wideNetInfo.put("CHECK_MODE","1" );
    wideNetInfo.put("DESTORYFLG","1" );
    wideNetInfo.put("DESTORYREASON","1" );

    wideNetInfo.put("INSTALL_FEE_TAG","0" );
    wideNetInfo.put("MODEM_RETUAN","0" );
    wideNetInfo.put("REMOTECARD_TYPE","1" );
    wideNetInfo.put("ROUTE_EPARCHY_CODE","0898" );

    wideNetInfo.put("SUBMIT_SOURCE","CRM_PAGE" );
    wideNetInfo.put("TRADE_TYPE_CODE","605" );
    wideNetInfo.put("EPARCHY_CODE","0898" );
    wideNetInfo.put("WIDE_PRODUCT_TYPE",$("#WIDE_PRODUCT_TYPE").val()  );  //宽带类型

    /*  wideNetInfo.put("HIDDEN_STATE","" );
      wideNetInfo.put("AUTH_SERIAL_NUMBER","" );     //集团服务号码
      wideNetInfo.put("SELECTED_AUTH_USER","" );     //成员userId
      wideNetInfo.put("SERIAL_NUMBER","" );          //成员宽带号码
      wideNetInfo.put("WIDE_NET_SERIAL_NUMBER","" ); //成员宽带号码
      wideNetInfo.put("WIDE_TYPE_CODE","" );         //宽带类型*/

    return wideNetInfo;
}

// 宽带 成员变更 数据拼接
function saveChangeWideNetData()
{
    var wideNetInfo = saveChangeWidenetBase();  // 宽带基本信息

    var custInfo = saveEcCustomer();
    wideNetInfo.put("CUST_ID",custInfo.get("CUST_ID"));

    wideNetInfo.put("NEW_PRODUCT_ID", $("#WIDE_PRODUCT_ID").val() );   //变更后的宽带产品编码
    /*wideNetInfo.put("SELECTED_AUTH_USER","" );     //成员的USERID
    wideNetInfo.put("AUTH_SERIAL_NUMBER","" );   //集团的服务号码*/

    /*var publicAttrInfo = new Wade.DatasetList($("#cond_SELECTED_ELEMENTS").text());
    wideNetInfo.put("SELECTED_ELEMENTS",publicAttrInfo);*/

    var addressList = saveAddressList();
    wideNetInfo.put("ADDRESS_LIST",addressList);

    return wideNetInfo;
}


function saveChangeWidenetBase()
{
    var changeWideNetInfo = new Wade.DataMap();
    changeWideNetInfo.put("AUTH_CHECK_PSPT_TYPE_CODE","0" );

    changeWideNetInfo.put("AUTH_USER_SCORE","0" );
    changeWideNetInfo.put("CHANGE_TYPE","1" );
    changeWideNetInfo.put("CHANGE_UP_DOWN_TAG","1" );
    changeWideNetInfo.put("CHECK_MODE","1" );
    changeWideNetInfo.put("REMOTECARD_TYPE","1" );
    changeWideNetInfo.put("TRADE_TYPE_CODE","601" );
    changeWideNetInfo.put("WIDE_USER_CREATE_SALE_ACTIVE","1" );
    changeWideNetInfo.put("WIDE_PRODUCT_TYPE",$("#WIDE_PRODUCT_TYPE").val() );
    changeWideNetInfo.put("WIDE_USER_SELECTED_SERVICEIDS",$("#WIDE_PRODUCT_ID").val() );   //变更后的宽带产品编码

    return changeWideNetInfo;
}

//保存客户信息
function saveEcCustomer()
{
    var customer = new Wade.DataMap();

    if(!$.os.phone){
        var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
    }else{
        var custInfo = new Wade.DataMap($("#CUST_INFO").text());
    }

    customer.put("CUST_ID", custInfo.get("CUST_ID"));
    customer.put("CUST_NAME", custInfo.get("CUST_NAME"));
    customer.put("GROUP_ID", custInfo.get("GROUP_ID"));
    customer.put("EPARCHY_CODE", custInfo.get("EPARCHY_CODE"));
    return customer;
}


// 获取主题  公共参数
function saveOrderData(operType) {

    var orderData = new Wade.DataMap();

    var title = $("#pattr_TITLE").val();
    var urgencyLevel = $("#pattr_URGENCY_LEVEL").val();

    orderData.put("TITLE",title);
    orderData.put("URGENCY_LEVEL",urgencyLevel);
    orderData.put("OPER_TYPE",operType);

    return orderData;

}

function saveNodeTempleteData()
{
    var eosCommonData = new Wade.DataMap($("#NODE_TEMPLETE").text());

    var nodeTemplete = new Wade.DataMap();
    nodeTemplete.put("BPM_TEMPLET_ID", eosCommonData.get("BPM_TEMPLET_ID"));
    nodeTemplete.put("NODE_ID", eosCommonData.get("NODE_ID"));
    return nodeTemplete;
}

function saveBusiSpecReleData()
{
    var eosCommonData = new Wade.DataMap($("#BUSI_SPEC_RELE").text());

    var busiSpecRele = new Wade.DataMap();
    busiSpecRele.put("BPM_TEMPLET_ID", eosCommonData.get("BPM_TEMPLET_ID"));
    busiSpecRele.put("BUSI_TYPE", eosCommonData.get("BUSI_TYPE"));
    busiSpecRele.put("BUSI_CODE", eosCommonData.get("BUSI_CODE"));
    busiSpecRele.put("TEMPLET_ID", eosCommonData.get("TEMPLET_ID"));
    busiSpecRele.put("IN_MODE_CODE", eosCommonData.get("IN_MODE_CODE"));
    busiSpecRele.put("BUSIFORM_OPER_TYPE", eosCommonData.get("BUSIFORM_OPER_TYPE"));
    return busiSpecRele;
}


// 获取 other表 数据
function saveOtherList() {

    var otherDataList = new Wade.DatasetList();

    var OrderPart = $("#OrderPart input[name*='oattr_']");
    for(var i = 0, size = OrderPart.length; i < size; i++)
    {
        var OrderPartId = OrderPart[i].id;
        if(!OrderPartId){
            continue;
        }

        var publicValue = $("#"+OrderPartId).val();

        var otherInfo = new Wade.DataMap();
        otherInfo.put("ATTR_VALUE", publicValue);
        otherInfo.put("ATTR_NAME", OrderPart[i].getAttribute("desc"));
        otherInfo.put("ATTR_CODE", OrderPartId.substring(6));

        otherDataList.add(otherInfo);
    }

    var offerPart = $("#OfferPart input[name*='oattr_']");
    for(var i = 0, size = offerPart.length; i < size; i++)
    {
        var offerPartId = offerPart[i].id;
        if(!offerPartId){
            continue;
        }

        var publicValue = $("#"+offerPartId).val();

        var otherInfo = new Wade.DataMap();
        otherInfo.put("ATTR_VALUE", publicValue);
        otherInfo.put("ATTR_NAME", offerPart[i].getAttribute("desc"));
        otherInfo.put("ATTR_CODE", offerPartId.substring(6));

        otherDataList.add(otherInfo);
    }

    return otherDataList;
}


//  获取 attr 表 信息
function saveAttrList()
{
    var  eomsAttrData = new Wade.DatasetList();

    var publicInfos = $("#OrderPart input[name*='pattr_']");
    for(var i = 0, size = publicInfos.length; i < size; i++)
    {
        var publicCode = publicInfos[i].id;
        if(!publicCode){
            continue;
        }

        var publicValue = $("#"+publicCode).val();

        var publicInfoData = new Wade.DataMap();
        publicInfoData.put("ATTR_VALUE", publicValue);
        publicInfoData.put("ATTR_NAME", publicInfos[i].getAttribute("desc"));
        publicInfoData.put("ATTR_CODE", publicCode.substring(6));

        eomsAttrData.add(publicInfoData);
    }
    return eomsAttrData;
}


// 获取公共参数
function saveCommonData ()
{
    var commData = new Wade.DataMap();
    if($("#EOS_COMMON_DATA").text()!='')
    {
        commData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
    }
    else
    {
        commData.put("IBSYSID",           $("#IBSYSID").val());
        commData.put("BUSIFORM_ID",       $("#BUSIFORM_ID").val());
        commData.put("BUSIFORM_NODE_ID",  $("#BUSIFORM_NODE_ID").val());
        commData.put("BPM_TEMPLET_ID ",   $("#BPM_TEMPLET_ID ").val());
        commData.put("BUSI_CODE",         $("#BUSI_CODE").val());
        commData.put("BUSIFORM_OPER_TYPE",$("#BUSIFORM_OPER_TYPE").val());
        commData.put("FLOW_MAIN_ID",      $("#FLOW_MAIN_ID").val());
        commData.put("NODE_ID",           $("#NODE_ID").val());

    }
    commData.put("PRODUCT_ID",commData.get("BUSI_CODE"));
    commData.put("GROUP_ID",$("#cond_GROUP_ID").val());
    return commData;
}


function getHtmlParams(){
	var data = new Wade.DataMap();
	data.put("cond_GROUP_ID_INPUT",$("#cond_GROUP_ID_INPUT").val());
	data.put("cond_TEMPLET_ID",$("#cond_TEMPLET_ID").val());
	data.put("cond_TEMPLET_ID_BL",$("#cond_TEMPLET_ID_BL").val());
	data.put("cond_MINOREC_BPM_PRODUCTID",$("#cond_MINOREC_BPM_PRODUCTID").val());
	data.put("ESP_SNY_STATE",$("#ESP_SNY_STATE").val());

	var contractData = new Wade.DataMap();
	contractData.put("CONTRACT_ID",$("#CONTRACT_ID").val());
	contractData.put("CONTRACT_NAME",$("#CONTRACT_NAME").val());
	contractData.put("OFFER_IDS",$("#OFFER_IDS").val());
	contractData.put("CONTRACT_END_DATE",$("#CONTRACT_END_DATE").val());
	contractData.put("CONTRACT_WRITE_DATE",$("#CONTRACT_WRITE_DATE").val());
	contractData.put("TEMPLET_BUSI_CODE",$("#TEMPLET_BUSI_CODE").val());
	data.put("CONTRACT_DATA",contractData);
	var contractVwData = new Wade.DataMap();
	contractVwData.put("CONTRACT_ID_VW",$("#CONTRACT_ID_VW").val());
	contractVwData.put("CONTRACT_NAME_VW",$("#CONTRACT_NAME_VW").val());
	contractVwData.put("OFFER_IDS_VW",$("#OFFER_IDS_VW").val());
	contractVwData.put("CONTRACT_END_DATE_VW",$("#CONTRACT_END_DATE_VW").val());
	contractVwData.put("CONTRACT_WRITE_DATE_VW",$("#CONTRACT_WRITE_DATE_VW").val());
	data.put("CONTRACTVW_DATA",contractVwData);

    data.put("ELEDATA",getEleData());
	
	return data;
}
