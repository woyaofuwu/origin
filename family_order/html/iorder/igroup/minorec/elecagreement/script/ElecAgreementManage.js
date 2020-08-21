$(function(){
	
	initCustGroupContractList();
	changeDepartCounty();
});

function initCustGroupContractList()
{
	//add by xiezhenglong 海南这边，其他省份，不加view这个隐藏字段不会报错
	var view = $("#cond_VIEW").val();
	if(view!=undefined)
	{
		if(view=='true')
		{
			$("#POP_BTN_cond_AREA_CODE").css('display','');
		}
		else
		{	
			$("#POP_BTN_cond_AREA_CODE").css('display','none');
		}
	}
}

function openCustManagerPopup(val){
	var dept = $("#cond_DEPART_ID").val();
	var areaCode = $("#cond_AREA_CODE").val();
	var params = '&cond_PARAMS='+val+'&cond_CLOSEPOPUP=true&cond_DEPART_ID='+dept+"&cond_AREA_CODE="+areaCode;
//	popupDialog('选择客户经理信息', 'igroup.elecagreement.ManagerSelect', 'initCusts',params , null, '71.42em', '35.71em', true, null, null);
	popupPage('选择客户经理信息', 'igroup.elecagreement.ManagerSelect', 'initCusts', params, null, 'full', null, null);
}

function changeDepartCounty(){
	var departId = 'cond_DEPART_ID';
	var county = $("#cond_AREA_CODE").val();
	var ontap = $("#POP_"+departId).attr('ontap');
	
	$("#POP_"+departId).attr('ontap',changeDepartParams(ontap,county));
	ontap = $("#POP_BTN_"+departId).attr('ontap');
	$("#POP_BTN_"+departId).attr('ontap',changeDepartParams(ontap,county));
}

function changeDepartParams(ontap,county){
	var ontapSp = ontap.split(',');
	
	var newOntap = ontapSp[0]+','+ontapSp[1]+','+ontapSp[2]+','+ontapSp[3]+',';
	
	var params = ontapSp[4].substring(1,ontapSp[4].length-2);
	
	if(params.indexOf('MGMT_COUNTY')>-1){
		var newParams = '';
		var para = params.split('&');
		for(var i=0;i<para.length;i++){
			if(para[i].indexOf('MGMT_COUNTY')>-1){
				para[i]='MGMT_COUNTY='+county;
			}
			newParams += '&'+para[i];
		}
		params = newParams;
	}else{
		params+= "&MGMT_COUNTY="+county;
	}
	newOntap += "'"+params+"')";
	return newOntap;
}

function show(){
	showPopup('popup','qryConditionPopupItem',true);
}

function queryAgreement()
{
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("CondPart,controlPart", "queryAgreement", "", "ResultPart", function(data){
		$.endPageLoading();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
function queryAgreementDetail(obj)
{
    var param ="&AGREEMENT_ID="+$(obj).attr("agreementId")+"&CONTRACT_CODE="+$(obj).attr("contractCode")+"&PRODUCT_ID="+$(obj).attr("productId");
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("controlPart", "queryAgreementDetail", param, "ContractInfosPart", function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    showPopup('popup','ContractDetailPopupItem',true);
}
function deleteAgreement(obj)
{
	MessageBox.confirm("提示信息", "确认将该协议置为失效！", function(btn){
		if("ok" == btn){
			var param ="&AGREEMENT_ID="+$(obj).attr("agreementId")+"&ARCHIVES_STATE="+$(obj).attr("state");
			$.beginPageLoading("数据查询中...");
			$.ajax.submit("EditPart,controlPart", "deleteAgreement", param, "", function(data){
				$.endPageLoading();
				MessageBox.success("成功提示", "协议成功置为失效！");
				queryAgreement()
				
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}
	});
}

function modifyContract(el) {
    var print = $(el).parent().find("div[id^=PRINT_]");
    var archiveId = print.attr("archiveId");
    var linkAddr = print.attr("url");
    var params = "&ARCHIVES_ID=" + archiveId;
    if (!linkAddr || !archiveId) {
        return MessageBox.error("数据缺失", "合同电子档案数据丢失！");
    }
    params += "&SHOWBUTTON=false";
    popupPage('合同属性修改', linkAddr, 'initPage', params, null, 'full');
}

function popupPreview(el) {
    var archiveId = $(el).attr("archiveId");
    var params = '&ARCHIVES_ID=' + archiveId+'&timestamp='+Date.now();
    popupPage('打印预览', 'igroup.minorec.elecagreement.ElecContractPreview', 'init', params, null, 'full');
}


function showFile(obj){
    var archiveId = $(obj).attr("archiveId");
    $.beginPageLoading("数据查询中...");
    $.ajax.submit("controlPart", "queryAttachInfos", "&ARCHIVE_ID="+archiveId, "ContractFilePart", function(data){
            $.endPageLoading();
            forwardPopup(obj,'ContractFilePopupItem');

        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}

