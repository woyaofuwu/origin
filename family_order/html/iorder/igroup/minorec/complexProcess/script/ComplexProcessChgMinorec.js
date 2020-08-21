var ACTION_CREATE = "0";
var ACTION_DELETE = "1";
var ACTION_EXITS = "3";
var ACTION_PASTE = "5"; // 暂停
var ACTION_CONTINUE = "6"; // 恢复
var ACTION_PREDESTROY = "7"; // 预取消
var ACTION_PREDSTBACK = "8"; // 冷冻期恢复
var contractVw ="";  //一单清商铺调电子协议标记
var productIdInfo ="";  //一单清商铺拼接产品ID
var userIdAsG ="";//已有协议的所有USERID
var productAsG ="";//已有协议的所有产品ID
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
});

function queryGroup(groupId)
{
	var param = "GROUP_ID="+groupId;
	var checkTag = $("#checkTag").val();

	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "queryCustGroupByGroupId", param, "groupBasePart,moreCustPart", function(data){
			//$.enterpriseLogin.refreshGroupInfo(groupId);
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
		pram += "&ACTION=init"+"&IBSYSID="+$("#IBSYSID").val()+"&NODE_ID="+$("#NODE_ID").val();;
	
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
    if(""==productIdInfo){
    	productIdInfo = productInfo;
    }
    if(productInfo!=productIdInfo){
		productInfo = productIdInfo+","+productInfo;
    }
    var data = new Wade.DataMap();
	data.put("CONTRACT_ID", contractInfo.get("AGREEMENT_ID"));
	data.put("CONTRACT_NAME", contractInfo.get("ARCHIVES_NAME"));
	//取合同信息的生失效时间和对应的产品用于提交判断
	data.put("OFFER_IDS", contractInfo.get("PRODUCT_ID"));
	data.put("CONTRACT_END_DATE", contractInfo.get("CONTRACT_END_DATE"));
	data.put("CONTRACT_WRITE_DATE", contractInfo.get("CONTRACT_WRITE_DATE"));
	
	var groupId = $("#cond_GROUP_ID_INPUT").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	var operType = $("#oattr_OPER_TYPE").val();
	var templetId = $("#cond_TEMPLET_ID").val();
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
	
	 var param = "&FLAG="+flag+"&CONTRACT_PRODUCT_ID="+productInfo+"&USER_ID_AS="+userIdAsG+"&PRODUCT_ID_AS="+productAsG+"&DATE_OFFERCODE="+data+"&OPER_TYPE="+operType+"&DATEMAP="+data+"&TEMPLET_ID="+templetId+"&GROUP_ID="+groupId;
	 $.beginPageLoading("数据查询中...");
		$.ajax.submit("", "setOperType",  param , part, function(data){
			$.endPageLoading();
			},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
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
		var dataset1 = new Wade.DatasetList(tableList.toString());
		$.beginPageLoading("数据删除中...");
		$.ajax.submit("", "removeaddMebSub","&DETAIL_ADDRESS_LIST="+dataset+"&DETAIL_ADDRESS_LIST1="+dataset1, "widenetResult", function(data){
			$.endPageLoading();
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

//提交
function submitApply()
{

    // 校验提交参数
    if(!checkSubmitParam())
    {
        return false;
    }

    var submitData = buildSubmitData(); // 拼凑 提交参数；

    var message = "流程创建成功!";
    $.beginPageLoading("数据提交中，请稍后...");
    $.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitData.toString(), "", function(data){
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
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}


// 校验提交数据
function checkSubmitParam() {

    //1、校验集团信息
    if(!verifyAll('groupBasePart')){
        return false;
    }

    //2、校验流程信息
    if(!verifyAll('OfferPart')){
        return false;
    }
    
    //4、校验变更协议信息
    var contractId = $("#CONTRACT_ID").val();
    if(contractId == "" || contractId == null)
    {
        contractId = $("#CONTRACT_ID_VW").val();
        if(contractId == "" || contractId == null)
        {
            MessageBox.alert("提示信息", "请录入 '电子协议信息' !");
            return false;
        }
    }

    //5、校验公共信息
    var title= $("#pattr_TITLE").val();
    var urgencyLevel= $("#pattr_URGENCY_LEVEL").val();
    var auditStaff= $("#oattr_AUDITSTAFF").val();

    if(""==title||null==title||undefined==title){
        $.validate.alerter.one($("#pattr_TITLE")[0], "请填写主题！");
        return false;
    }
    if(""==urgencyLevel||null==urgencyLevel||undefined==urgencyLevel){
        $.validate.alerter.one($("#pattr_URGENCY_LEVEL")[0], "请选择紧急程度！");
        return false;
    }
    if(""==auditStaff||null==auditStaff||undefined==auditStaff){
        $.validate.alerter.one($("#oattr_AUDITSTAFF")[0], "请选择稽核员工！");
        return false;
    }
    return true;
}

// 拼凑 提交参数；
function buildSubmitData() {

    var submitData = new Wade.DataMap();

    var offerList = saveOfferList();  // 商品信息

    var busiSpecRele = saveBusiSpecReleData();     // 流程信息
    var nodeTemplete = saveNodeTempleteData();     // 节点信息

    submitData.put("OFFER_LIST", offerList);    // 商品信息
    submitData.put("OFFER_DATA_LIST", saveOfferData());    // 商品信息

    submitData.put("BUSI_SPEC_RELE", busiSpecRele);        // 流程信息
    submitData.put("NODE_TEMPLETE", nodeTemplete);         // 节点信息
    
    submitData.put("CUST_DATA", saveEcCustomer());
    submitData.put("ORDER_DATA", saveOrderData());
    submitData.put("OTHER_LIST", saveOtherList());
    submitData.put("EOMS_ATTR_LIST", saveAttrList());
    submitData.put("COMMON_DATA", saveCommonData());

    return submitData;
}


function saveOfferList()
{
	
}


function  buildEcSubmitData(offerData, commonData,ecAccessNum) {
    var ecSubmitData = new Wade.DataMap();
    ecSubmitData.put("OPER_TYPE", "CrtUs");
    ecSubmitData.put("SERIAL_NUMBER", ecAccessNum);

    var ecProductId = offerData.get("OFFER_CODE");
    var ecProductName = offerData.get("OFFER_NAME");
    ecSubmitData.put("PRODUCT_ID", ecProductId);
    ecSubmitData.put("PRODUCT_NAME", ecProductName);

    var custInfo = saveEcCustomer();
    ecSubmitData.put("CUST_ID", custInfo.get("CUST_ID"));
    ecSubmitData.put("DEAL_TYPE", "EC");

    var offers = new Wade.DatasetList();
    offers.add(offerData);
    ecSubmitData.put("OFFERS", offers);

    ecSubmitData.put("COMMON_INFO", commonData);


    ecSubmitData.put("CUST_INFO",custInfo );

    ecSubmitData.put("ACCT_INFO", commonData.get("ACCT_INFO"));

    var contractInfo = saveContractInfo(offerData);

    var subscriber = new Wade.DataMap();
    subscriber.put("SERIAL_NUMBER", ecAccessNum);
    subscriber.put("CONTRACT_ID", contractInfo.get("CONTRACT_ID"));
    subscriber.put("CONTRACT_NAME", contractInfo.get("CONTRACT_NAME"));
    subscriber.put("CONTRACT_WRITE_DATE", contractInfo.get("CONTRACT_WRITE_DATE"));
    subscriber.put("CONTRACT_END_DATE", contractInfo.get("CONTRACT_END_DATE"));
    subscriber.put("OFFER_IDS", contractInfo.get("OFFER_IDS"));
    ecSubmitData.put("SUBSCRIBER", subscriber);

    return ecSubmitData;
}


function  buildMebSubmitData(offerData, commonData, ecAccessNum, ecOfferId) {

    var mebSubmitData = new Wade.DataMap();
    mebSubmitData.put("OPER_TYPE", "CrtMb");
    mebSubmitData.put("SERIAL_NUMBER", "");

    var memProductId = offerData.get("OFFER_CODE");
    var memProductName = offerData.get("OFFER_NAME");
    mebSubmitData.put("PRODUCT_ID", memProductId);
    mebSubmitData.put("PRODUCT_NAME", memProductName);

    mebSubmitData.put("COMMON_INFO", commonData);       // 公共信息

    var custInfo = saveEcCustomer();
    mebSubmitData.put("CUST_ID", custInfo.get("CUST_ID"));
    mebSubmitData.put("DEAL_TYPE", "MEB");
    mebSubmitData.put("CUST_INFO", custInfo);  // 集团客户信息

    var subscriber = new Wade.DataMap();
    subscriber.put("SERIAL_NUMBER", "#R_SERIAL_NUMBER#");   //存个默认值，方便以后替换
    subscriber.put("USER_ID", "#R_USER_ID#");
    mebSubmitData.put("SUBSCRIBER", subscriber);   //集团用户信息

    var offers = new Wade.DatasetList();
    offers.add(offerData);
    mebSubmitData.put("OFFERS", offers);   // 成员产品信息

    return mebSubmitData;
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
    if( brandCode == "VPMN" && templetId == "YIDANQINGSHANGPU" )
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


//保存客户信息
function saveEcCustomer()
{
    var customer = new Wade.DataMap();

//    var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
//    if(custInfo==""||custInfo==undefined){
//    	custInfo = new Wade.DataMap($("#CUST_INFO").text());
//    }
    var custInfo = "";
    if(!$.os.phone)
    {
    	custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
    }else{
    	custInfo = new Wade.DataMap($("#CUST_INFO").text());
    }
    customer.put("CUST_ID", custInfo.get("CUST_ID"));
    customer.put("CUST_NAME", custInfo.get("CUST_NAME"));
    customer.put("GROUP_ID", custInfo.get("GROUP_ID"));
    customer.put("EPARCHY_CODE", custInfo.get("EPARCHY_CODE"));
    return customer;
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

// 获取主题  公共参数
function saveOrderData() {

    var orderData = new Wade.DataMap();

    var title = $("#pattr_TITLE").val();
    var urgencyLevel = $("#pattr_URGENCY_LEVEL").val();

    orderData.put("TITLE",title);
    orderData.put("URGENCY_LEVEL",urgencyLevel);

    return orderData;

}

// 获取 other表 数据
function saveOtherList() {

    var otherDataList = new Wade.DatasetList();

    var otherData = $("#OrderPart input[name*='oattr_']");
    for(var i = 0, size = otherData.length; i < size; i++)
    {
        var otherCode = otherData[i].id;
        if(!otherCode){
            continue;
        }

        var publicValue = $("#"+otherCode).val();

        var otherInfo = new Wade.DataMap();
        otherInfo.put("ATTR_VALUE", publicValue);
        otherInfo.put("ATTR_NAME", otherData[i].getAttribute("desc"));
        otherInfo.put("ATTR_CODE", otherCode.substring(6));

        otherDataList.add(otherInfo);
    }

    return otherDataList;
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

function saveOfferData()
{
    var offerData = new Wade.DataMap();

    var productCode = $("#TEMPLET_BUSI_CODE").val();
    var productName = $("#cond_TEMPLET_ID").text();
    var productId = $("#CONTRACT_ID").val();
    var productIdVw = $("#CONTRACT_ID_VW").val();
    if(""!=productIdVw||null!=productIdVw||undefined!=productIdVw){
    	offerData.put("IS_EXPER_DATALINE",productIdVw);
    }
    offerData.put("OFFER_CODE",productCode);
    offerData.put("OFFER_NAME",productName);
    offerData.put("OFFER_ID",productId);

    return offerData ;
}

//稽核员工
function showAuditStaff(){
    ajaxSubmit("",'qryStaffinfo',null,'auditParts',function(data){
        showPopup("popup02", "auditPopupItem", true);
        $.endPageLoading();
    },function(error_code,error_info,derror){
        $.endPageLoading();
        showDetailErrorInfo(error_code,error_info,derror);
    });
}

function setReturnValue(el){
	var staffId = $(el).attr("staff_id");
    var staffPhone =  $(el).attr("staff_phone");
	$("#oattr_AUDITSTAFF").val(staffId);
	$("#oattr_AUDITPHONE").val(staffPhone);
	backPopup("popup02", "auditPopupItem", true);
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
