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

$(function(){
	
	if($.enterpriseLogin && $.enterpriseLogin.isLogin())
	{
		var loginGroupId = $.enterpriseLogin.getInfo().get("GROUP_INFO").get("GROUP_ID");
		if($("#cond_GROUP_ID_INPUT").val() == "")
		{//如果外框进行了认证，并且集团组件GROUP_ID为空，则将认证集团带入组件
			queryGroup(loginGroupId, null);
		}
		else
		{
			if(loginGroupId != $("#cond_GROUP_ID_INPUT").val() && $.isFunction(window["refreshEnterpriseLogin"]))
			{
				refreshEnterpriseLogin($("#cond_GROUP_ID_INPUT").val());
			}
		}
	}
	
});

function queryGroupInfo(el, e)
{
	if (e.keyCode != 13 && e.keyCode != 108) 
	{
		return ;
	}
	
	if(!$.validate.verifyField("cond_GROUP_ID_INPUT"))
	{
		return ;
	}
	
	var groupId = $.trim($("#cond_GROUP_ID_INPUT").val());
	queryGroup(groupId, el);
	if(!groupId)
	{
		return ;
	}
}


//手机端客户查询
function queryCustInfoTypes(){

    var groupId = $.trim($("#cond_GROUP_ID_INPUT").val());
    if("" != groupId && null != groupId)
    {
        queryGroup(groupId, "");
    }
    else
    {
        showPopup('popup01', 'queryGroupInfoItem', true);
    }

    if(!groupId)
    {
        return ;
    }
}

function queryCustInfoByCustName()
{
    if(!$.validate.verifyField("cond_CUST_NAME_INPUT"))
    {
        return ;
    }

    var custName = $.trim($("#cond_CUST_NAME_INPUT").val());
    var param = "CUST_NAME="+custName;
    $.beginPageLoading("数据查询中...");
    $.ajax.submit("", "queryCustGroupByCustName", param, "groupUserPart", function(json)
        {
            $.endPageLoading();
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}

function selectGroup(obj)
{
    var groupId = $(obj).find("span").text();
    if("" != groupId && null != groupId)
    {
        $("#cond_GROUP_ID_INPUT").val(groupId);

        queryGroup(groupId, "");

        hidePopup("popup01","moreCustomerPopupItem",true);
    }
}


function queryGroup(groupId, el)
{
	var param = "GROUP_ID="+groupId;
	var checkTag = $("#checkTag").val();
	var condGroupId = $("#cond_GROUP_ID").val();
	if(""!=condGroupId){
		MessageBox.confirm("提示信息", "重新输入集团会清空所有数据，是否确定？", function(btn){
			if("ok" == btn)
			{
				$.beginPageLoading("数据加载中...");
				$.ajax.submit("", "", "", "OfferPart,opercontractPart,contractAllPart,paramHeadPart", function(data){
					$.endPageLoading();
					$("#oattr_OPER_TYPE").val("");
				    $("#opercontractPart").css("display","none");
				    $("#contractAllPart").css("display","none");
				    $("#paramHeadPart").css("display","none");
					productIdInfo="";
					userIdAsG ="";//已有协议的所有USERID
				    productAsG ="";//已有协议的所有产品ID
					},function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
			}else{
				$("#cond_GROUP_ID_INPUT").val(condGroupId);
			}
			//checkEspSnyState(templetId);
		});
	}

	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "queryCustGroupByGroupId", param, "groupBasePart,moreCustPart", function(data){
		if(!$.os.phone){
			$.enterpriseLogin.refreshGroupInfo(groupId);
		}
		$.endPageLoading();
		if(el)
		{
			el.blur();
		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
//选择产品
function changeTempletId(){
	var groupId = $("#cond_GROUP_ID").val();
	if(groupId == null || groupId == ""){
		$.validate.alerter.one($("cond_GROUP_ID_INPUT")[0], "未获取到集团信息，请输入集团编码后回车键查询！");
		$("#cond_TEMPLET_ID").val("");
		return false;
	}
	
	var  minorecProductid = $("#cond_MINOREC_BPM_PRODUCTID").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	
	if(""==minorecProductid||undefined==minorecProductid||null==minorecProductid){
		$("#cond_TEMPLET_ID_BL").val(templetId);
		$("#cond_MINOREC_BPM_PRODUCTID").val(templetId);
	}else{
		MessageBox.confirm("提示信息", "重新选择产品会清空信息，是否选择？", function(btn){
			if("ok" == btn)
			{
 				$.beginPageLoading("数据加载中...");
 				$.ajax.submit("", "", "", "opercontractPart,contractAllPart,paramHeadPart", function(data){
 					$.endPageLoading();
 					productIdInfo="";
					userIdAsG ="";//已有协议的所有USERID
				    productAsG ="";//已有协议的所有产品ID
				    $("#opercontractPart").css("display","");
				    $("#contractAllPart").css("display","none");
				    $("#paramHeadPart").css("display","none");
				    if("YIDANQINGSHANGPUCHANGE"==templetId){
						 $("#ArchivesGGPart").css("display","");
						 $("#ArchivesVWPart").css("display","");
					 }else{
						 $("#ArchivesGGPart").css("display","");
						 $("#ArchivesVWPart").css("display","none");
					 }
 					},function(error_code,error_info,derror){
 					$.endPageLoading();
 					showDetailErrorInfo(error_code,error_info,derror);
 				});
 			}else{
				var  operTypeBl = $("#oattr_OPER_TYPE_BL").val();
				$("#oattr_OPER_TYPE").val(operTypeBl);
			}
		});
	}
	 $("#opercontractPart").css("display","");
	 if("YIDANQINGSHANGPUCHANGE"==templetId){
		 $("#ArchivesGGPart").css("display","");
		 $("#ArchivesVWPart").css("display","");
	 }else{
		 $("#ArchivesGGPart").css("display","");
		 $("#ArchivesVWPart").css("display","none");
	 }
}


//选择已有协议
function queryArchivesInfo(obj){
	
	var groupId = $("#cond_GROUP_ID").val();
	if(groupId == null || groupId == ""){
		$.validate.alerter.one($("cond_GROUP_ID_INPUT")[0], "未获取到集团信息，请输入集团编码后回车键查询！");
		$("#cond_TEMPLET_ID").val("");
		return false;
	}
    
	var templetId = $("#cond_TEMPLET_ID").val();
	
	if(templetId == null || templetId == ""){
		$.validate.alerter.one($("cond_TEMPLET_ID")[0], "未选择变更的流程，请先选择流程！");
		return false;
	}
	
	$.beginPageLoading("数据加载中...");
	$.ajax.submit("", "queryArchivesInfo", "&GROUP_ID="+groupId+"&TEMPLET_ID="+templetId+"&FLAG="+obj, "pagePart", function(data){
		showPopup("qryPopup", "qryPopupItem", true);
		$.endPageLoading();
		},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}


function setArchives(obj){
	var contractId = $(obj).attr("CONTRACT_ID");//合同协议编码
	var ecProductId = $(obj).attr("EC_PRODUCT_ID");//已订购的产品ID
	var productId = $(obj).attr("PRODUCT_ID");//虚拟的产品ID
	var serialNumber = $(obj).attr("SERIAL_NUMBER");//所有服务号码
	var userId = $(obj).attr("USER_ID");//所有产品的userId
	var archivesName = $(obj).attr("ARCHIVES_NAME");//所有产品的userId
	
	if("8000"==ecProductId){
		$("#contractAllPart").css("display","");
		$("#contractVwPart").css("display","");
		$("#CONTRACT_ID_AS_VW").val(contractId);
		$("#EC_PRODUCT_ID_AS_VW").val(ecProductId);
		$("#PRODUCT_ID_AS_VW").val(productId);
		$("#SERIAL_NUMBER_AS_VW").val(serialNumber);
		$("#USER_ID_AS_VW").val(userId);
		$("#ARCHIVES_NAME_AS_VW").val(archivesName);
	}else{
		$("#contractAllPart").css("display","");
		$("#contractInfoPart").css("display","");
		$("#CONTRACT_ID_AS").val(contractId);
		$("#EC_PRODUCT_ID_AS").val(ecProductId);
		$("#PRODUCT_ID_AS").val(productId);
		$("#SERIAL_NUMBER_AS").val(serialNumber);
		$("#USER_ID_AS").val(userId);
		$("#ARCHIVES_NAME_AS").val(archivesName);
	}

	
	
	
	
	 backPopup(obj);
}

function changeContractType(){
	
	var templetId = $("#cond_TEMPLET_ID").val();
	if(undefined==templetId||""==templetId||null==templetId){
		$.validate.alerter.one($("#cond_TEMPLET_ID")[0], "未选择产品，请先选择产品！");
		$("#cond_CONTRACT_TYPE").val("");
		return false;
	}
	var contRactType = $("#cond_CONTRACT_TYPE").val();
	if("YIDANQINGSHANGPUCHANGE"==templetId&&"1"==contRactType){
		$("#contractInfoPart").css("display", "");
		$("#contractVwPart").css("display", "");
		$("#contractPart").css("display", "none");
	}else  if("YIDANQINGSHANGPUCHANGE"!=templetId&&"1"==contRactType){
		$("#contractInfoPart").css("display", "");
		$("#contractVwPart").css("display", "none");
		$("#contractPart").css("display", "none");
	}else  if("2"==contRactType){
		$("#contractInfoPart").css("display", "none");
		$("#contractVwPart").css("display", "none");
		$("#contractPart").css("display", "");
	}
}

//选择电子合同
function contractInfo(flag){
	var templetId = $("#cond_TEMPLET_ID").val();
	var groupId = $("#cond_GROUP_ID_INPUT").val();
	var offerCode = $("#cond_OFFER_CODE").val();
	var operType = $("#oattr_OPER_TYPE").val();
	var minorecProductid =$("#cond_MINOREC_BPM_PRODUCTID").text();
	var contractId = $("#CONTRACT_ID_AS").val();
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
		contractId = $("#CONTRACT_ID_AS_VW").val();
	}
	
	var pram = "&OPER_TYPE="+operType+"&PRODUCT_ID="+minorecProductid+"&CONTRACT_ID="+contractId+"&GROUP_ID="+groupId+"&insertpage="+"true";
	
	pram += "&ACTION=init";
	
	$.ajax.submit("", "", pram,"ElecAgreementAdd", function(data){
		$.endPageLoading();
		if(data.get("MINOREC_PRODUCT_INFO")){
			$("#MINOREC_PRODUCT_INFO").val(data.get("MINOREC_PRODUCT_INFO"));
		}
		showPopup('popup04', 'ElecAgreementAdd');
		},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});

	
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
	
	var part = "HiddenPart,contractInfoPart,paramHeadPart";
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
		part = "HiddenPart,contractVwPart,paramHeadPart";
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
		$.ajax.submit("", "setOperType",  param , part , function(data){
			$("#paramHeadPart").css("display","");
			$.endPageLoading();
			},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
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

function deleteProductId(el){
	var offerId = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").val();
	var offerCode = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("OFFER_CODE");
	MessageBox.confirm("提示信息", "确定删除该产品吗？", function(btn){
		if("ok" == btn)
		{
		    // 融合通信快速办理  ，多媒体桌面电视是必选产品；不能删除；   add by  zhengkai5
            var offerCode = $(el).closest("li").attr("offercode");
            var templetId = $("#cond_TEMPLET_ID").val();
		    if(templetId == "FUSECOMMUNICATIONOPEN" && offerCode == "2222")
		    {
                MessageBox.alert("告警提示","融合通信快速办理,多媒体桌面电视是必选产品；不能删除! ");
            }
            else
            {
                $(el).closest("li").remove();
            }
			if("7341"==offerCode){
				$("#ParamAddress").css("display", "none");
			}else{
				$("#ParamAddress").css("display", "");
			}
			
		}
	});
	
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

function chooseOfferAfterAction(offerCode, offerName, userId)
{
	var param = "&EC_USER_ID="+userId+"&OFFER_CODE="+offerCode+"&OPER_TYPE="+$("oattr_OPER_TYPE").val()+"&CUST_ID="+$("#cond_CUST_ID").val();
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "queryOffer", param, "OfferPart,ParamPart", function(data){
		$.endPageLoading();
//		$("#cond_OFFER_CODE").val(offerCode);
//		$("#cond_OFFER_NAME").val(offerName);
		$("#OFFER_CODE_LI").css("display", "");
		$("#ParamPart").css("display", "");
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
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
    var submitData = buildSubmitData();
    var message = "订单创建成功！";
    
    $.beginPageLoading("数据提交中......");
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
                        	if($.os.phone){
                                window.parent.MBOP.closeWebPlugin();
                            }else{
                                closeNav();
							}
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
                	if($.os.phone){
                        window.parent.MBOP.closeWebPlugin();
                    }else{
                        closeNav();
					}
                }
            }, {"ext1" : "下一步"});
        }
        else
        {
            MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
                if("ok" == btn){
                	if($.os.phone){
                        window.parent.MBOP.closeWebPlugin();
                    }else{
                        closeNav();
					}
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

    //3、校验已有协议信息
    var contractId = $("#CONTRACT_ID_AS").val();
    if(contractId == "" || contractId == null)
    {
        contractId = $("#CONTRACT_ID_AS_VW").val();
        if(contractId == "" || contractId == null)
        {
            MessageBox.alert("提示信息", "请录入 '电子协议信息' !");
            return false;
        }
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


function buildSubmitData()
{

    var submitData = new Wade.DataMap();

    var busiSpecRele = saveBusiSpecReleData();  // 节点信息
    var nodeTemplete = saveNodeTempleteData();  // 流程信息

    submitData.put("OFFER_DATA_LIST", saveOfferData());    // 商品信息
    submitData.put("OFFER_LIST", saveOfferList()); // 商品信息
    
    submitData.put("BUSI_SPEC_RELE", busiSpecRele);
    submitData.put("NODE_TEMPLETE", nodeTemplete);

    submitData.put("CUST_DATA", saveEcCustomer());
    submitData.put("ORDER_DATA", saveOrderData());
    submitData.put("OTHER_LIST", saveOtherList());
    submitData.put("EOMS_ATTR_LIST", saveAttrList());
    submitData.put("COMMON_DATA", saveCommonData());

    return submitData;
}

//获取 attr 表 信息
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

    var productCode = $("#TEMPLETE_BUSI_CODE").val();
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

function saveOfferList()
{
	var offerList = new Wade.DatasetList();
    
    var productId = $("#OFFER_IDS").val();
    
    
    var serialNumber = $("#SERIAL_NUMBER_AS").val();
    if(serialNumber==null || serialNumber==""){
    	serialNumber = $("#SERIAL_NUMBER_AS_VW").val();
    }
    if(""!=productId && null!=productId){
        var productIdList=productId.split(",");
    	var serialNumberList=serialNumber.split(",");
        for(var i=0;i<productIdList.length;i++){
        	var childSubmitData = new Wade.DataMap();
        	var ecCommonInfo = new Wade.DataMap();
        	var serialNumber = serialNumberList[i];
        	ecCommonInfo.put("CONTRACT_INFO", contractDate(productIdList[i]));
    	 	childSubmitData.put("EC_COMMON_INFO",ecCommonInfo);
    	    childSubmitData.put("PRODUCT_ID",productIdList[i]);
    		//新增产品时,插入date表产品服务号为-1
    		if(serialNumber == null|| serialNumber == ""){
    			serialNumber="-1";
    		}
    	    childSubmitData.put("SERIAL_NUMBER",serialNumber);
    	    offerList.add(childSubmitData);
        }
    }
    var offerIds = $("#OFFER_IDS_VW").val();
    if(offerIds=="8000"){
    	var childSubmitData = new Wade.DataMap();
    	var ecCommonInfo = new Wade.DataMap();
    	ecCommonInfo.put("CONTRACT_INFO", contractDate(offerIds));
	 	childSubmitData.put("EC_COMMON_INFO",ecCommonInfo);
	    childSubmitData.put("PRODUCT_ID",offerIds);
	    childSubmitData.put("SERIAL_NUMBER",$("#SERIAL_NUMBER_AS_VW").val());
	    offerList.add(childSubmitData);
    }
    return offerList;
}

function  buildEcSubmitData(offerData, commonData,ecAccessNum) {
    var ecSubmitData = new Wade.DataMap();
    ecSubmitData.put("OPER_TYPE", "CrtUs");
    ecSubmitData.put("SERIAL_NUMBER", ecAccessNum);

    var offers = new Wade.DatasetList();
    offers.add(offerData);
    ecSubmitData.put("OFFERS", offers);

    ecSubmitData.put("COMMON_INFO", commonData);

    ecSubmitData.put("CUST_INFO", saveEcCustomer());

    ecSubmitData.put("ACCT_INFO", commonData.get("ACCT_INFO"));

    var contractInfo = saveContractInfo();

    var subscriber = new Wade.DataMap();
    subscriber.put("SERIAL_NUMBER", ecAccessNum);
    subscriber.put("CONTRACT_ID", contractInfo.get("CONTRACT_ID"));
    subscriber.put("CONTRACT_WRITE_DATE", contractInfo.get("CONTRACT_WRITE_DATE"));
    subscriber.put("CONTRACT_END_DATE", contractInfo.get("CONTRACT_END_DATE"));
    subscriber.put("OFFER_IDS", contractInfo.get("OFFER_IDS"));
    ecSubmitData.put("SUBSCRIBER", subscriber);
    return ecSubmitData;
}


function  buildMebSubmitData(offerData, commonData, ecAccessNum, ecOfferId) {

    var mebSubmitData = new Wade.DataMap();
    mebSubmitData.put("OPER_TYPE", "CrtMb");

    var ecSubscriber = new Wade.DataMap();
    ecSubscriber.put("ACCESS_NUM", ecAccessNum);
    mebSubmitData.put("EC_SUBSCRIBER", ecSubscriber);

    mebSubmitData.put("COMMON_INFO", commonData);

    var splitPriceOffer = new Wade.DataMap($("#WN_PRICEOFFER_SPLIT_DATA").text());
    if(splitPriceOffer.length > 0)
    {
        var bindPriceOffer = splitPriceOffer.get(ecOfferId);
        if(bindPriceOffer != null && bindPriceOffer.length > 0)
        {
            var subOffers = offerData.get("SUBOFFERS");
            if(typeof(subOffers) == "undefined" || subOffers.length == 0)
            {
                subOffers = new Wade.DatasetList();
            }
            bindPriceOffer.put("OPER_CODE", "0");
            subOffers.add(bindPriceOffer);
            offerData.put("SUBOFFERS", subOffers);
        }
    }

    var offers = new Wade.DatasetList();
    offers.add(offerData);

    var member = new Wade.DataMap();
    member.put("OFFERS", offers);

    var members = new Wade.DatasetList();
    members.add(member);
    mebSubmitData.put("MEMBERS", members);

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
function saveContractInfo()
{
    var contract = new Wade.DataMap();
    var contractId = $("#CONTRACT_ID").val();
    var contractName = $("#CONTRACT_NAME").val();
    var writeData = $("#CONTRACT_WRITE_DATE").val();
    var endDate = $("#CONTRACT_END_DATE").val();
    var offerIds = $("#OFFER_IDS").val();
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


// 获取主题  公共参数
function saveOrderData() {

    var orderData = new Wade.DataMap();

    var title = $("#pattr_TITLE").val();
    var urgencyLevel = $("#pattr_URGENCY_LEVEL").val();

    orderData.put("TITLE",title);
    orderData.put("URGENCY_LEVEL",urgencyLevel);

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
    var isPhone = new Wade.DataMap();
    if($.os.phone)
    {
        isPhone.put("ATTR_VALUE", "true");
    }
    else
    {
        isPhone.put("ATTR_VALUE", "false");
    }
    isPhone.put("ATTR_NAME", "是否手机端登录");
    isPhone.put("ATTR_CODE", "IS_PHONE");

    otherDataList.add(isPhone);
    return otherDataList;
}


// 获取公共参数
function saveCommonData ()
{
    var commonData = new Wade.DataMap();
    if($("#EOS_COMMON_DATA").text()!='')
    {
        commonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
    }

    var operType = $("#cond_OPER_TYPE").val();
    var productId = $("#TEMPLETE_BUSI_CODE").val();

    commonData.put("OPER_TYPE", operType);
    commonData.put("BUSIFORM_OPER_TYPE", operType);
    commonData.put("PRODUCT_ID", productId);

    return commonData;
}

//手机端重置按钮
function reloadNav(){
	var condGroupId = $("#cond_GROUP_ID").val();
	if(""!=condGroupId){
		MessageBox.confirm("提示信息", "重置会清空信息，是否选择？", function(btn){
			if("ok" == btn)
			{
				$.beginPageLoading("数据加载中...");
				$.ajax.submit("", "", "", "paramHeadPart,HiddenPart,contractAllPart,opercontractPart,OfferPart,groupBasePart", function(data){
					$.endPageLoading();
					$("#opercontractPart").css("display", "none");
					$("#contractAllPart").css("display", "none");
					$("#paramHeadPart").css("display", "none");
					productIdInfo="";
					},function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
			}
		});
	}
}

//保存合同信息
function contractDate(offerId)
{
    var contract = new Wade.DataMap();
    
    var contractId;
    var contractName;
    var writeData;
    var endDate;
    var offerIds;
    
    if(offerId=="8000"){
        contractId= $("#CONTRACT_ID_VW").val();
        contractName= $("#CONTRACT_NAME_VW").val();
        writeData= $("#CONTRACT_WRITE_DATE_VW").val();
        endDate= $("#CONTRACT_END_DATE_VW").val();
        offerIds= $("#OFFER_IDS_VW").val();
    }else{
        contractId= $("#CONTRACT_ID").val();
        contractName= $("#CONTRACT_NAME").val();
        writeData= $("#CONTRACT_WRITE_DATE").val();
        endDate= $("#CONTRACT_END_DATE").val();
        offerIds= $("#OFFER_IDS").val();
    }
    contract.put("CONTRACT_ID", contractId);
    contract.put("CONTRACT_NAME", contractName);
    contract.put("CONTRACT_WRITE_DATE", writeData);
    contract.put("CONTRACT_END_DATE", endDate);
    contract.put("OFFER_IDS", offerIds);
    return contract;
}