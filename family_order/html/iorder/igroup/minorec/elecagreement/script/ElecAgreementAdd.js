
(function($){
    $.extend({archives:{
            events:{
                uploadImage:function(retStr){
                    MessageBox.alert("温馨提示", "上传回调成功！");
                    var archiveInfo = new Wade.DataMap($('#TEMP_ARCHIVE_DATA').val());
                    var param = "&base64Bitmap="+encodeURIComponent(retStr.toString())+"&ARCHIVES_NAME="+archiveInfo.get("ARCHIVES_NAME")+"&ARCHIVES_ID="+archiveInfo.get("ARCHIVE_ID");
                    $.beginPageLoading("数据上传中......");
                    $.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.handler.ElecAgreementHandler", "uploadImage", param,
                        function (data) {
                            $.endPageLoading();
                            var fileId = data.get("FILE_ID");
                            MessageBox.alert("温馨提示", "签名上传成功！");
                            backPopup(document.getElementById("bd"));
                        }, function(error_code,error_info,derror){
                            $.endPageLoading();
                            showDetailErrorInfo(error_code,error_info,derror);
                        }, function () {
                            $.endPageLoading();
                            MessageBox.alert("告警提示", "签名上传失败，请重新签名上传");
                    });
                },
                setImage:function(){
                    MessageBox.alert("温馨提示", "保存图片回调成功！");
                    try{
                    	window.MBOP.getEleSignBase64("$.archives.events.uploadImage");
                    }catch(e){
                    	alert("错误提示："+e);
                    }
                }
            }
        }});
})(Wade);

$(function () {
    //触发绑定事件 上传组件确定按钮
    $("#contractUpload").select(function () {
        var obj = this.val();
        var archiveId = $("#TEMP_ARCHIVE_ID").val();
        $("#FILEUPLOAD_" + archiveId).attr("fileId", obj.ID);
        backPopup("popup01");
    });
    
})

function addContract(el) {
    var contractNum = $('#CONTRACT_NUMBER').val();
    if (!contractNum) {
        $.validate.alerter.one($('#CONTRACT_NUMBER')[0], "请先占用或者填写合同编码！");
        return;
    }
    var maxNum = $(el).attr("maxNum");
    var index = $(el).attr("index");
    var archiveNum = $("#AGREDIV_" + index).find("div[id^=PRINT_]").length;
    if (archiveNum >= maxNum) {
        $.validate.alerter.one(el, "该类型电子档案，最大允许" + maxNum + "条,现已有" + archiveNum + "条，请修改现有合同信息！");
        return;
    }

    $("#TEMP_ARCHIVE_ID").val($(el).attr("index"));
    var linkAddr = $(el).attr("url");
    var agreementDefId = $(el).attr("agreementDefId");
    var groupId = $('#GROUP_ID').val();
    var productId = $('#PRODUCT_ID').val();
    var ibsysid = $('#IBSYSID').val();
    var nodeId = $('#NODE_ID').val();
    var custId = $('#cond_CUST_ID').val();
    var custName = $('#cond_CUST_NAME').val();
    var params = '&AGREEMENT_ID=' + contractNum + '&GROUP_ID=' + groupId + '&PRODUCT_ID=' + productId
        + '&IBSYSID=' + ibsysid + '&NODE_ID=' + nodeId + '&AGREEMENT_DEF_ID=' + agreementDefId
        + '&CUST_ID=' + custId + '&CUST_NAME=' + custName;
    if($.os.phone) {
        setTimeout(
            function(){
                popupPage('协议'+Date.now(), linkAddr, 'initPage', params, null, 'full', afterContract);
            },500);

    }else {
        popupPage('协议'+Date.now(), linkAddr, 'initPage', params, null, 'full', afterContract);
    }
}

function onTestHtml(obj){
	var page = 'igroup.minorec.pangsTest';
	if(obj){
		page += obj;
	}
	
	if($.os.phone) {
        setTimeout(
            function(){
                popupPage('协议'+Date.now(), page, null, null, null, 'full', afterContract);
            },500);

    }else {
        popupPage('协议'+Date.now(), page, null, null, null, 'full', afterContract);
    }
}

function testHtml1(){
	if($.os.phone) {
        setTimeout(
            function(){
				showPopupTest("popup_02", "popup_group2");
                //popupPage('协议'+Date.now(), page, null, null, null, 'full', afterContract);
            },500);

    }else {
        //popupPage('协议'+Date.now(), page, null, null, null, 'full', afterContract);
		showPopupTest("popup_02", "popup_group2");
    }
}

function modifyContract(el) {
    var archiveId = $(el).parent().find("div[id^=PRINT_]").attr("archiveId");
    var params = "&ARCHIVES_ID=" + archiveId;
    var addBtn = $(el).parent().parent().parent().parent().find("li[ontap^=addContract]");
    var linkAddr = addBtn.attr("url");
    var agreementDefId = addBtn.attr("agreementDefId");
    if (!linkAddr || !archiveId || !agreementDefId) {
        return MessageBox.error("数据缺失", "合同电子档案数据丢失！");
    }
    params += "&AGREEMENT_DEF_ID=" + agreementDefId;
    params += "&SHOWBUTTON=" + $("#SHOWBUTTON").val();
    if($.os.phone) {
        setTimeout(
            function(){
                popupPage('协议'+Date.now(), linkAddr, 'initPage', params, null, 'full', afterContractModi);
            },500);

    }else {
        popupPage('协议'+Date.now(), linkAddr, 'initPage', params, null, 'full', afterContractModi);
    }
}

function afterContractModi(el) {
    // 1- 判断档案是否生成（根据回写值）
    var archiveInfo = new Wade.DataMap($('#TEMP_ARCHIVE_DATA').val());

    var MINOREC_PRODUCT_INFO = archiveInfo.get("MINOREC_PRODUCT_INFO");
    if (MINOREC_PRODUCT_INFO) {
        $("#MINOREC_PRODUCT_INFO").val(MINOREC_PRODUCT_INFO);
    }

    var archiveId = archiveInfo.get('ARCHIVE_ID', '');
    // 如果没有返回电子档案编码，直接返回。
    if (!archiveId) {
        return;
    }
    //刷新日期
    var contractDate = $("#CONTRACTDATE_" + archiveId);
    var contractStart = archiveInfo.get("CONTRACT_WRITE_DATE");
    var contractEnd = archiveInfo.get("CONTRACT_END_DATE");
    if(contractStart && contractEnd){
    	contractDate.empty();
    	contractDate.html("档案生效时间：" + contractStart + "<br>档案失效时间：" + contractEnd);
    }
    
    // 2- 将数据存入对应隐藏域
    var archiveData = $("#ARCHIVE_DATA_" + archiveId);
    archiveData.text(archiveInfo);
    archiveData.val(archiveInfo);
}

function afterContract(el) {
    // 1- 判断档案是否生成（根据回写值）
    var archiveInfo = new Wade.DataMap($('#TEMP_ARCHIVE_DATA').val());

    var MINOREC_PRODUCT_INFO = archiveInfo.get("MINOREC_PRODUCT_INFO");
    if (MINOREC_PRODUCT_INFO) {
        $("#MINOREC_PRODUCT_INFO").val(MINOREC_PRODUCT_INFO);
    }


    var index = $("#TEMP_ARCHIVE_ID").val();
    var archiveId = archiveInfo.get('ARCHIVE_ID', '');
    // 如果没有返回电子档案编码，直接返回。
    if (!archiveId) {
        return;
    }

    // 2- 生成html列表，将回写值存入对应隐藏域
    var contractStart = archiveInfo.get("CONTRACT_WRITE_DATE");
    var contractEnd = archiveInfo.get("CONTRACT_END_DATE");
    var contractNumber = archiveInfo.get("CONTRACT_NUMBER");
    var html = '';
    html += '<li>';
    html += '    <div class="main" ontap="modifyContract(this)">';
    html += '        <div class="title">' + archiveInfo.get('ARCHIVES_NAME') + '</div>';
    if (contractStart && contractEnd) {
        html += '        <div id="CONTRACTDATE_' + archiveId + '" contractnumber="' + contractNumber + '" class="content">档案生效时间：' + contractStart + '<br>档案失效时间：' + contractEnd + '</div>';
    } else {
    	//默认 从当前时间到 最大结束时间 
    	var nowStr = new Date().format("yyyy-MM-dd");
        html += '        <div id="CONTRACTDATE_' + archiveId + '" contractnumber="' + contractNumber + '"  class="content">档案生效时间：' + nowStr + '<br>档案失效时间：2050-12-31</div>';
    }
    html += '    </div>';
    html += '    <div class="side" ontap="modifyContract(this)">查看修改</div>';
    html += '    <div class="more" ontap="modifyContract(this)"></div>';
    html += '    <div id="PRINT_' + archiveId + '" name="PRINT_' + archiveId + '"  class="fn e_hide-phone" title="打印预览" ontap="popupPreview(this);" archiveId="' + archiveId + '"><span class="e_ico-print"></span>';
    html += '    <textarea id="ARCHIVE_DATA_' + archiveId + '" name="ARCHIVE_DATA_' + archiveId + '" style="display: none;"></textarea>';
    html += '    </div>';
    html += '    <div id="FILEUPLOAD_' + archiveId + '" name="FILEUPLOAD_' + archiveId + '" class="fn e_hide-phone" title="签约附件上传" archiveId="' + archiveId + '" ontap="popupUpload(this);"><span class="e_ico-upload"></span>';
    html += '    </div>';
    html += '</li>';
    $("#ContractInfo_" + index).append(html);

    // 2.2- 将数据存入对应隐藏域
    var archiveData = $("#ARCHIVE_DATA_" + archiveId);
    archiveData.text(archiveInfo);
    archiveData.val(archiveInfo);
    $("CONTRACT_NUMBER").attr("readonly", "readonly");
}


function popupPreview(el) {
    var archiveId = $(el).attr("archiveId");
    var params = '&ARCHIVES_ID=' + archiveId+'&timestamp='+Date.now();
    popupPage('打印预览', 'igroup.minorec.elecagreement.ElecContractPreview', 'init', params, null, 'full');
}

function popupUpload(el) {
    var fileId = $(el).attr("fileId");
    var archiveId = $(el).attr("archiveId");
    $("#TEMP_ARCHIVE_ID").val(archiveId);
    $.beginPageLoading("协议上传初始化中");
    showPopup("popup01", "attachPopupItem", true);
    if (fileId) {
        contractUpload.loadFile(fileId);
    }
    $.endPageLoading();
}
function changeProduct() {
    var param = "&PRODUCT_ID=" + $("#PRODUCT_ID").val();
    $.beginPageLoading("合同数据加载中，请稍后...");
    $.ajax.submit("", "changeProduct", param, "CONTRACTS_PART,CONTRACT_INFO", function (data) {
        $.endPageLoading();

    }, function (code, info) {
        $.endPageLoading();
        MessageBox.error("错误信息", info);
    });
}
function chooseAgreement() {
    var param = "&PRODUCT_ID=" + $("#PRODUCT_ID").val();
    param += "&CONTRACT_CODE=" + $("#CONTRACT_CODE").val();
    param += "&CUST_ID=" + $("#cond_CUST_ID").val();
    $.beginPageLoading("合同数据加载中，请稍后...");
    $.ajax.submit("", "queryContractInfo", param, "CONTRACT_INFO", function (data) {
        $.endPageLoading();

    }, function (code, info) {
        $.endPageLoading();
        MessageBox.error("错误信息", info);
    });


}

function selectOldContract(el) {
    var contractId = $(el).val();
    var param = "&PRODUCT_ID=" + $("#PRODUCT_ID").val();
    param += "&BUSIFORM_OPER_TYPE=" + $("#OPER_TYPE").val();
    param += "&CUST_ID=" + $("#cond_CUST_ID").val();
    param += "&CONTRACT_CODE=" + $("#CONTRACT_CODE").val();
    if (contractId) {
        param += "&CONTRACT_ID=" + contractId;
    }

    $.beginPageLoading("合同数据加载中，请稍后...");
    $.ajax.submit("", "queryContractInfo", param, "CONTRACT_INFO", function (data) {
        $.endPageLoading();
        if (contractId) {
            $("contractPreemption").attr("disabled", true);
        }
        //返回订购页面的参数
        var agreementData=new Wade.DataMap();
        agreementData.put("AGREEMENT_ID",data.get("AGREEMENT_ID"));
        agreementData.put("CONTRACT_WRITE_DATE",data.get("START_DATE"));
        agreementData.put("CONTRACT_END_DATE",data.get("END_DATE"));
        agreementData.put("ARCHIVES_NAME",data.get("ARCHIVES_NAME"));
        agreementData.put("PRODUCT_ID",data.get("PRODUCTS"));
        $("#MINOREC_PRODUCT_INFO").val(agreementData);

    }, function (code, info) {
        $.endPageLoading();
        MessageBox.error("错误信息", info);
    });
}

function preemptionContractId() {

    $.beginPageLoading("合同数据加载中，请稍后...");
    $.ajax.submit("", "getContractId", "", "", function (data) {
        $.endPageLoading();
        var contractNum = data.get("CONTRACT_NUMBER");
        if (contractNum) {
            $("#CONTRACT_NUMBER").val(contractNum);
            $("#contractPreemption").attr("disabled", true);
        }
    }, function (code, info) {
        $.endPageLoading();
        MessageBox.error("错误信息", info);
    });
}

function checkRequireData() {
    // 1- 校验每类数量
    var flag = true;
    $("#CONTRACT_INFO li[minNum]").each(function (index, value) {
        var minNum = $(value).attr("minNum");
        var maxNum = $(value).attr("maxNum");
        var index = $(value).attr("index");
        var archiveNum = $("#AGREDIV_" + index).find("div[id^=PRINT_]").length;
        if (archiveNum < minNum || archiveNum > maxNum) {
            $.validate.alerter.one(value, "该类型电子档案，最少需要" + minNum + "条，最大允许" + maxNum + "条，请修改！");
            flag = false;
        }
    });
    return flag;
}

function onsubmit(obj) {
    if (!$.validate.verifyAll("CONTRACT_INFO") || !$.validate.verifyAll("ProductInfo")) {
        return;
    }
    if (!checkRequireData()) {
        return;
    }

    if ($("#insertpage").val() == "true") {
        var ret={};
        var returnInfo = new Wade.DataMap($('#MINOREC_PRODUCT_INFO').val());
        returnInfo.put("PRODUCT_ID",$("#PRODUCT_ID_IN").val())
        ret.MINOREC_PRODUCT_INFO = returnInfo;
        setPopupReturnValue(this,ret ,false);
    }

    if(!$.os.phone) {
        var submitData = new Wade.DataMap();

        var attachInfos = new Wade.DatasetList();
        var listInfos = $("#CONTRACT_INFO div[name^=FILEUPLOAD_]");
        if (listInfos.length <= 0) {
            MessageBox.error("提示", "请填写合同信息！");
            return;
        }
        for (var i = 0; i < listInfos.length; i++) {
            var attachInfo = new Wade.DataMap();
            var listInfo = $(listInfos[i]);
            if (!listInfo.attr("fileId")) {
                $.validate.alerter.one(listInfos[i], "请提交签约附件后再提交！");
                return;
            }
            attachInfo.put("ARCHIVES_ID", listInfo.attr("archiveId"));
            attachInfo.put("FILE_ID", listInfo.attr("fileId"));
            attachInfos.add(attachInfo);
        }
        submitData.put("attachInfos",attachInfos);

        $.beginPageLoading("数据提交中，请稍后...");
        $.ajax.submit("", "onsubmit", "&SUBMIT_DATA=" + encodeURIComponent(submitData.toString()), "", function (data) {
            $.endPageLoading();

            hidePopup(obj);

        }, function (code, info) {
            $.endPageLoading();
            MessageBox.error("错误信息", info);
        });
    }
}

function checkContractNumber(obj){
	if($("[contractnumber]").size()>0){
		MessageBox.alert("温馨提示", "该合同编码已生成合同信息，不能再编辑合同编码！");
		$(obj).attr("disabled", true);
		var contractPreemption = $("#contractPreemption");
		if(contractPreemption){
			$("#contractPreemption").css('display', 'none');
		}
	}
}
function checkAgreementID(){
	var agreementId = $("#CONTRACT_NUMBER").val();
	if(agreementId == null || agreementId == ""){
		MessageBox.alert("温馨提示", "请先输入合同编码！");
	}else{
	    //去掉前后空格
        agreementId = $.trim(agreementId);
        $("#CONTRACT_NUMBER").val(agreementId);

		$.beginPageLoading("数据加载中......");
		$.ajax.submit("", "checkAgreementID", "&AGREEMENT_ID="+agreementId, "", function(data){
				$.endPageLoading();
				var used = data.get("USED");
				if(used == "Y"){
					$("#CONTRACT_NUMBER").val("");
					MessageBox.alert("温馨提示", "该合同编码为空或已被使用，请重新输入！");
				}
				
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
	}
}

function GetQueryString(param) { //param为要获取的参数名 注:获取不到是为null
	var currentUrl = window.location.href; //获取当前链接
	var arr = currentUrl.split("?");//分割域名和参数界限
	if (arr.length > 1) {
		arr = arr[1].split("&");//分割参数
		for (var i = 0; i < arr.length; i++) {
			var tem = arr[i].split("="); //分割参数名和参数内容
			if (tem[0] == param) {
				return tem[1];
			}
		}
		return null;
	} else {
		return null;
	}
}