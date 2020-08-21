function openOfferPopupItem(el)
{
	var offerId = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").val();
	var offerCode = $(el).closest("li").find("input:hidden[id*=CHILD_OFFER_ID_]").attr("OFFER_CODE");
	
	var offerMemberInfo = new Wade.DatasetList($("#OFFER_MEMBER_DATA_"+offerId).text());
	var memberOfferData = new Wade.DataMap($("#MEB_OFFER_DATA_"+offerId).text());
	var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA_"+offerId).text());
	var ecCommonInfoData = new Wade.DataMap($("#EC_COMMON_INFO_DATA_"+offerId).text());
	var operTypee= $("#oattr_OPER_TYPE").val();
	
	var pram = "&OPER_TYPE="+operTypee+"&EC_COMMON_INFO_DATA="+encodeURIComponent(ecCommonInfoData.toString())+"&EC_OFFER_DATA="+encodeURIComponent(ecOfferData.toString())+"&MEB_OFFER_DATA="+encodeURIComponent(memberOfferData.toString())+"&OFFER_MEMBER_DATA="+encodeURIComponent(offerMemberInfo.toString());
	 ajaxSubmit("",'analyslsOfferData',pram,"offermeberPrat,ecCommonPrat,memberOfferPrat,grpPackagePrat,ecOfferChaPrat",function(data){
	        showPopup("popup02", "setOfferDataPratPopup", true);
	        $.endPageLoading();
	    },function(error_code,error_info,derror){
	        $.endPageLoading();
	        showDetailErrorInfo(error_code,error_info,derror);
	    }
	 );
}

function checkSubmit(obj){

	var auditText = $("#oattr_AUDIT_TEXT").val();
	if(""==auditText||undefined==auditText||null==auditText)
	{
		 $.validate.alerter.one($("#oattr_AUDIT_TEXT")[0], "请填写审核意见！");
	     return false;
	}
	var checkInfo = "";
	var message = "";
	if("1"==obj)
	{
		checkInfo = "您确定是否审核不通过？";
        message = "审核不通过,请重新提交申请！";
	}
	else if("2"==obj)
	{
		checkInfo = "您确定是否审核通过？";
        message = "审核通过！";
	}

	$("#oattr_ADULT_RESULT").val(obj);

	MessageBox.confirm("提示信息", checkInfo, function(btn){
		if("ok" == btn)
		{
            var submitData = buildSubmitData(); // 拼凑 提交参数；
            submitData.put("ADULT_RESULT",obj);

			$.beginPageLoading('审核中...');
			$.ajax.submit('','submit',"&SUBMIT_PARAM="+submitData.toString(),'',function(data){
				$.endPageLoading();
				MessageBox.success("提交", message+"定单号："+data.get("IBSYSID"),function(){
					if("ok" == btn){
			            closeNav();
			         }
				});
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}
	});
}

function buildSubmitData()
{
    var submitData = new Wade.DataMap();
    var busiSpecRele = saveBusiSpecReleData();     // 流程信息
    var nodeTemplete = saveNodeTempleteData();     // 节点信息

    submitData.put("BUSI_SPEC_RELE", busiSpecRele);        // 流程信息
    submitData.put("NODE_TEMPLETE", nodeTemplete);         // 节点信息
    
    submitData.put("CUST_DATA", saveEcCustomer());
    submitData.put("ORDER_DATA", saveOrderData());
    submitData.put("OTHER_LIST", saveOtherList());
    submitData.put("EOMS_ATTR_LIST", saveAttrList());
    submitData.put("COMMON_DATA", saveCommonData());
    
    submitData.put("OFFER_LIST", saveAddMebOfferList());
    return submitData;
}


function saveAddMebOfferList()
{

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

//保存客户信息
function saveEcCustomer()
{
    var customer = new Wade.DataMap();

    var groupId = $("#GROUP_ID").val();
    var custId = $("#CUST_ID").val();
    var custName = $("#CUST_NAME").val();
    var eparchyCode = $("#EPARCHY_CODE").val();

    customer.put("CUST_ID", custId);
    customer.put("CUST_NAME", custName);
    customer.put("GROUP_ID", groupId);
    customer.put("EPARCHY_CODE", eparchyCode);
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


// 获取 other表 数据
function saveOtherList() {

    var otherDataList = new Wade.DatasetList();

    var orderPart = $("#OrderPart input[name*='oattr_']");
    for(var i = 0, size = orderPart.length; i < size; i++)
    {
        var orderData = orderPart[i].id;
        if(!orderData){
            continue;
        }

        var data = $("#"+orderData).val();

        var otherInfo = new Wade.DataMap();
        otherInfo.put("ATTR_VALUE", data);
        otherInfo.put("ATTR_NAME", orderPart[i].getAttribute("desc"));
        otherInfo.put("ATTR_CODE", orderData.substring(6));

        otherDataList.add(otherInfo);
    }

    var hiddenData = $("#HiddenPart input[name*='oattr_']");
    for(var i = 0, size = hiddenData.length; i < size; i++)
    {
        var otherCode = hiddenData[i].id;
        if(!otherCode){
            continue;
        }

        var publicValue = $("#"+otherCode).val();

        var otherInfo = new Wade.DataMap();
        otherInfo.put("ATTR_VALUE", publicValue);
        otherInfo.put("ATTR_NAME", hiddenData[i].getAttribute("desc"));
        otherInfo.put("ATTR_CODE", otherCode.substring(6));

        otherDataList.add(otherInfo);
    }

    var AuditData = $("#AuditPart TextArea[name*='oattr_']");
    for(var i = 0, size = AuditData.length; i < size; i++)
    {
        var otherCode = AuditData[i].id;
        if(!otherCode){
            continue;
        }

        var publicValue = $("#"+otherCode).val();

        var otherInfo = new Wade.DataMap();
        otherInfo.put("ATTR_VALUE", publicValue);
        otherInfo.put("ATTR_NAME", hiddenData[i].getAttribute("desc"));
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
    commData.put("PRODUCT_ID", commData.get("BUSI_CODE"));
    commData.put("GROUP_ID",$("#GROUP_ID").val());
    return commData;
}

function showElec(el){
	var params = "&SHOWBUTTON=false";
	var linkAddr = $(el).attr("url");
	var archiveId = $(el).attr("archiveId");
	params += "&ARCHIVES_ID="+archiveId;
	
	popupPage('返回', linkAddr, 'initPage', params, null, 'full', null);
}