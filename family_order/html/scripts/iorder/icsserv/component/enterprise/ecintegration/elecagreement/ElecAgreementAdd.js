$(document).ready(function(){
    /*(function($){
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
                                var afterCall = archiveInfo.get("AFTER_FUNCTION");
                                /!*if($.isFunction(window[afterCall]))
                                {
                                    window[afterCall]();
                                }*!/
                                try{
                                    window.parent.backElecSelect();
                                }catch (e) {
                                    alert(e);
                                }
                                MessageBox.alert("温馨提示", "签名上传成功！ElecAgreementAdd.js");

                            }, function(error_code,error_info,derror){
                                $.endPageLoading();
                                showDetailErrorInfo(error_code,error_info,derror);
                            }, function () {
                                $.endPageLoading();
                                MessageBox.alert("告警提示", "签名上传失败，请重新签名上传");
                        });
                    },
                    setImage:function(ret){
                        var resultJson = $.parseJSON(ret);
                        var a= resultJson.exeResult;
                        MessageBox.alert("温馨提示", "ret.exeResult="+a);
                        if(a !=1){
                            MessageBox.alert("温馨提示", "保存图片回调成功！");
                            window.MBOP.getEleSignBase64("$.archives.events.uploadImage");
                        }
                    }
                }
            }});
    })(Wade);*/

    if("minorecUpload"==$("#ELE_NODE_ID").val()){
        $("#CONTRACT_CODE").attr("disabled","disabled");
    }

});

function showEle(param,contractId){
    $.beginPageLoading("信息加载中...");
    $.ajax.submit("", "", param,"ProductInfo,CONTRACT_INFO", function(data){
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

function initContract(){
    var contractNum = $('#CONTRACT_NUMBER').val();
    var productId = $('#ELE_PRODUCT_ID').val();
    var contractCode = $("#CONTRACT_CODE").val();
    var param = "&PRODUCT_ID="+productId+"&CONTRACT_ID="+contractNum+"&ACTION=init";
    $.beginPageLoading("页面加载中，请稍后...");
    $.ajax.submit("", "", param, "ProductInfo,CONTRACT_INFO", function (data) {
        $.endPageLoading();
        if(!contractNum){
            $("#CONTRACT_CODE").val(contractCode);
            chooseAgreement();
        }
    }, function (code, info) {
        $.endPageLoading();
        MessageBox.error("错误信息", info);
    });

}

function addElecContract(el) {

    var contractNum = $('#CONTRACT_NUMBER').val();

    if (!contractNum) {
        $.validate.alerter.one($('#CONTRACT_NUMBER')[0], "请先占用或者填写合同编码！");
        return;
    }

    var agreementDefId = $(el).attr("agreementDefId");

    var maxNum = $(el).attr("maxNum");
    var index = $(el).attr("index");
    var archiveNum = $("#AGREDIV_" + index).find("div[id^=PRINT_]").length;
    if (archiveNum >= maxNum) {
        $.validate.alerter.one(el, "该类型电子档案，最大允许" + maxNum + "条,现已有" + archiveNum + "条，请修改现有合同信息！");
        return;

    }
    try {
        //校验协议
        var input = new Wade.DataMap();
        input.put("AGREEMENT_DEF_ID", agreementDefId);
        input.put("AGREEMENT_ID", contractNum);
        var checkResult = checkAgreementBpm(input);
        if (checkResult.get("RESULT") == "false") {
            MessageBox.error("错误信息", checkResult.get("INFO"));
            return false;
        }
    }catch (e) {
        alert(e);
    }

    $("#TEMP_ARCHIVE_ID").val($(el).attr("index"));
    var linkAddr = $(el).attr("url");
    var groupId = $('#cond_GROUP_ID').val();
    var productId = $('#ELE_PRODUCT_ID').val();
    var ibsysid = $('#ELE_IBSYSID').val();
    var nodeId = $('#NODE_ID').val();
    var custId = $('#cond_CUST_ID').val();
    var operType = $('#OPER_TYPE').val();
    var params = '&AGREEMENT_ID=' + contractNum + '&GROUP_ID=' + groupId + '&PRODUCT_ID=' + productId
        + '&IBSYSID=' + ibsysid + '&NODE_ID=' + nodeId + '&AGREEMENT_DEF_ID=' + agreementDefId
        + '&CUST_ID=' + custId + '&OPER_TYPE='+operType;
    if($.os.phone) {
        var data = new Wade.DataMap();
        data.put("PAGE",linkAddr);
        data.put("PARAM",params);
        setTimeout(
            function(){
                window.parent.showElecFrame(data);
                //popupPage('协议'+Date.now(), linkAddr, 'initPage', params, null, 'full', afterElecContract);
            },0);

    }else {
        popupPage('协议'+Date.now(), linkAddr, 'initPage', params, null, 'full', afterElecContract);
    }
}

function modifyElecContract(el) {

    var archiveId = $(el).parent().find("div[id^=PRINT_]").attr("archiveId");
    var operType = $('#OPER_TYPE').val();
    var params = "&ARCHIVES_ID=" + archiveId;
    var addBtn = $(el).parent().parent().parent().parent().find("li[ontap^=addElecContract]");
    var linkAddr = addBtn.attr("url");
    var agreementDefId = addBtn.attr("agreementDefId");
    if (!linkAddr || !archiveId || !agreementDefId) {
        MessageBox.error("数据缺失", "合同电子档案数据丢失！");
        return false;
    }

    //校验协议
    var contractNum = $('#CONTRACT_NUMBER').val();

    if (!contractNum) {
        $.validate.alerter.one($('#CONTRACT_NUMBER')[0], "请先占用或者填写合同编码！");
        return false;
    }

    var flag = false;
    params += "&AGREEMENT_DEF_ID=" + agreementDefId;
    if('0'==$("#SHOWBUTTON"+archiveId).val()||'2'==$("#SHOWBUTTON"+archiveId).val()){
        params += "&SHOWBUTTON=true";
        flag = true;
    }else{
        params += "&SHOWBUTTON=false";
        flag = false;
    }

    try {
        if (flag) {
            var input = new Wade.DataMap();
            input.put("AGREEMENT_DEF_ID", agreementDefId);
            input.put("AGREEMENT_ID", contractNum);
            input.put("ARCHIVES_ID", archiveId);
            var checkResult = checkAgreementBpm(input);
            if (checkResult.get("RESULT") == "false") {
                MessageBox.error("错误信息", checkResult.get("INFO"));
                return false;
            }
        }
    }catch (e) {
        alert(e);
    }

    params += '&OPER_TYPE='+operType;

    if($.os.phone) {
        var data = new Wade.DataMap();
        data.put("PAGE",linkAddr);
        data.put("PARAM",params);
        setTimeout(
            function(){
                window.parent.showElecFrame(data);
                //popupPage('协议'+Date.now(), linkAddr, 'initPage', params, null, 'full', afterElecContract);
            },0);

    }else {
        popupPage('协议'+Date.now(), linkAddr, 'initPage', params, null, 'full', afterContractModi);
    }
}

function afterContractModi(el) {
    // 1- 判断档案是否生成（根据回写值）
    var archiveInfo = new Wade.DataMap($('#TEMP_ARCHIVE_DATA').val());

    var MINOREC_PRODUCT_INFO = archiveInfo.get("MINOREC_PRODUCT_INFO");
    if (MINOREC_PRODUCT_INFO != null||MINOREC_PRODUCT_INFO != ""||MINOREC_PRODUCT_INFO != undefined) {
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
        contractStart = contractStart.substr(0,10);
        contractEnd = contractEnd.substr(0,10);
        contractDate.empty();
        contractDate.html("档案生效时间：" + contractStart + "<br>档案失效时间：" + contractEnd);
    }

    // 2- 将数据存入对应隐藏域
    var archiveData = $("#ARCHIVE_DATA_" + archiveId);
    archiveData.text(archiveInfo);
    archiveData.val(archiveInfo);
}

function afterElecContract(el) {
    // 1- 判断档案是否生成（根据回写值）
    var archiveInfo = new Wade.DataMap($('#TEMP_ARCHIVE_DATA').val());

    var MINOREC_PRODUCT_INFO = archiveInfo.get("MINOREC_PRODUCT_INFO");

    if (MINOREC_PRODUCT_INFO != null||MINOREC_PRODUCT_INFO != ""||MINOREC_PRODUCT_INFO != undefined) {
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
    html += '    <div class="main">';
    html += '        <div class="title">' + archiveInfo.get('ARCHIVES_NAME') + '</div>';
    if (contractStart && contractEnd) {
        contractStart = contractStart.substr(0,10);
        contractEnd = contractEnd.substr(0,10);
        html += '        <div id="CONTRACTDATE_' + archiveId + '" contractnumber="' + contractNumber + '" class="content">档案生效时间：' + contractStart + '<br>档案失效时间：' + contractEnd + '</div>';
    } else {
        //默认 从当前时间到 最大结束时间
        var nowStr = new Date().format("yyyy-MM-dd");
        //var endStr =
        html += '        <div id="CONTRACTDATE_' + archiveId + '" contractnumber="' + contractNumber + '"  class="content">档案生效时间：' + nowStr + '<br>档案失效时间：2050-12-31</div>';
    }
    html += '    <input style="display: none"  id="SHOWBUTTON'+archiveId+'" value="0" />';
    html += '    </div>';
    html += '    <div class="side" ontap="modifyElecContract(this)">修改</div>';
    html += '    <div class="more"></div>';
    html += '    <div style="display: none" id="PRINT_' + archiveId + '" name="PRINT_' + archiveId + '"  class="fn e_hide-phone" title="打印预览" ontap="popupPreview(this);" archiveId="' + archiveId + '"><span class="e_ico-print"></span>';
    html += '    <textarea id="ARCHIVE_DATA_' + archiveId + '" name="ARCHIVE_DATA_' + archiveId + '" style="display: none;"></textarea>';
    html += '    </div>';
    html += '    <div style="display: none" id="FILEUPLOAD_' + archiveId + '" name="FILEUPLOAD_' + archiveId + '" class="fn e_hide-phone" title="签约附件上传" archiveId="' + archiveId + '" ontap="popupUpload(this);"><span class="e_ico-upload"></span>';
    html += '    <span style="display:none" id="ATTACH_FILE_'+archiveId+'" archiveId="' + archiveId +'"></span>';
    html += '    </div>';
    html += '</li>';
    $("#ContractInfo_" + index).append(html);

    // 2.2- 将数据存入对应隐藏域
    var archiveData = $("#ARCHIVE_DATA_" + archiveId);
    archiveData.text(archiveInfo);
    archiveData.val(archiveInfo);
    $("#CONTRACT_NUMBER").attr("readonly", "readonly");
    //回写合同编码
    if("8000"==$("#ELE_PRODUCT_ID").val()){
        $("#CONTRACT_ID_VW").val(contractNumber);
    }else{
        $("#CONTRACT_ID").val(contractNumber);
    }

}


function popupPreview(el) {
    var archiveId = $(el).attr("archiveId");
    var params = '&ARCHIVES_ID=' + archiveId+'&timestamp='+Date.now();
    popupPage('打印预览', 'igroup.minorec.elecagreement.ElecContractPreview', 'init', params, null, 'full');
}

function popupUpload(el) {

    //触发绑定事件 上传组件确定按钮
    $("#contractUpload").select(function () {

        var fileArchiveId = $("#TEMP_ARCHIVE_ID").val();

        var fileList = new Wade.DatasetList();

        var obj = this.val();
        var fileIdArr = obj.ID.split(",");
        var fileNameArr = obj.NAME.split(",");
        for(var i = 0, size = fileIdArr.length; i < size; i++)
        {
            if(fileIdArr[i] != "")
            {
//				var fileNameIndex = fileNameArr[i].lastIndexOf(".");
//				var fileName = fileNameArr[i].substring(0,fileNameIndex);
                if(containSpecial(fileNameArr[i])){
                    MessageBox.alert("错误", "【"+fileNameArr[i]+"】文件名称包含特殊字符，请修改后再上传！");
                    return false;
                }
                var data = new Wade.DataMap();
                data.put("FILE_ID", fileIdArr[i]);
                data.put("FILE_NAME", fileNameArr[i]);
                //data.put("ATTACH_TYPE", "P");
                fileList.add(data);
            }
        }
        $("#ATTACH_FILE_"+fileArchiveId).text(fileList.toString());

        hidePopup("popup01-1");
    });

    //var fileId = $(el).attr("fileId");
    var archiveId = $(el).attr("archiveId");
    $("#TEMP_ARCHIVE_ID").val(archiveId);

    var fileId = "";
    var flag = false;
    try{
        //报错即不为json数据结构
        var attachList = new Wade.DatasetList($("#ATTACH_FILE_"+archiveId).text());
    }catch (e) {
        flag = true;
    }
    if(!flag){
        //var attachList = new Wade.DatasetList($("#ATTACH_FILE_"+archiveId).text());
        for(var i = 0;i<attachList.length;i++){
            var fileData = attachList.get(i);
            fileId += fileData.get("FILE_ID")+",";
        }
    }

    $.beginPageLoading("协议上传初始化中");
    contractUpload.reset();
    $("#contractUpload div[class=content]").text("单文件大小限制：10MB，文件个数限制：20个")

    showPopup("popup01-1", "attachPopupItem", true);
    if (fileId) {
        fileId = fileId.substr(0,fileId.length-1);
        contractUpload.loadFile(fileId);
    }
    $.endPageLoading();
}

function containSpecial(str){
    var containSpecial = RegExp("[%?？]");//过滤%,？,?特殊字符
    return (containSpecial.test(str));
}

function changeProduct() {
    var param = "&PRODUCT_ID=" + $("#ELE_PRODUCT_ID").val()+"&ACTION=changeProduct";
    $.beginPageLoading("合同数据加载中，请稍后...");
    $.ajax.submit("", "", param, "CONTRACTS_PART,CONTRACT_INFO", function (data) {
        $.endPageLoading();

    }, function (code, info) {
        $.endPageLoading();
        MessageBox.error("错误信息", info);
    });
}
function chooseAgreement() {
    var param = "&PRODUCT_ID=" + $("#ELE_PRODUCT_ID").val();
    param += "&CONTRACT_CODE=" + $("#CONTRACT_CODE").val();
    param += "&CUST_ID=" + $("#cond_CUST_ID").val();
    param += "&ACTION=queryContractInfo";
    $.beginPageLoading("合同数据加载中，请稍后...");
    $.ajax.submit("", "", param, "CONTRACT_INFO", function (data) {
        $.endPageLoading();

    }, function (code, info) {
        $.endPageLoading();
        MessageBox.error("错误信息", info);
    });


}

function selectOldContract(el) {
    var contractId = $(el).val();
    var param = "&PRODUCT_ID=" + $("#ELE_PRODUCT_ID").val();
    param += "&BUSIFORM_OPER_TYPE=" + $("#OPER_TYPE").val();
    param += "&CUST_ID=" + $("#cond_CUST_ID").val();
    param += "&CONTRACT_CODE=" + $("#CONTRACT_CODE").val();
    if (contractId) {
        param += "&CONTRACT_ID=" + contractId;
    }
    param += "&ACTION=queryContractInfo"

    $.beginPageLoading("合同数据加载中，请稍后...");
    $.ajax.submit("", "", param, "CONTRACT_INFO", function (data) {
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

    if(!$("#CONTRACT_CODE").val()){
        MessageBox.alert("温馨提示","请先选择产品协议");
        return false;
    }

    $.beginPageLoading("合同数据加载中，请稍后...");
    $.ajax.submit("", "", "&ACTION=getContractId", "ContractNumber", function (data) {
        $.endPageLoading();
        var contractNum = data.get("CONTRACT_NUMBER");
        if (contractNum) {
            $("#CONTRACT_NUMBER").val(contractNum);
            $("#contractPreemption").attr("disabled", true);
            //$("#CONTRACT_ID").val(contractNum);
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

function checkNewArchive() {

    var flag = false;
    var listInfos = $("#CONTRACT_INFO input[id^=SHOWBUTTON]");
    if (listInfos.length <= 0) {
        return flag;
    }
    for (var i = 0; i < listInfos.length; i++) {
        var listInfo = $(listInfos[i]);
        var value = listInfo.val();
        if(value == "0" || value == "1" || value == "2"){
            flag = true;
        }
    }
    return flag;
}

function onsubmit(obj) {

    if (!$.validate.verifyAll("CONTRACT_INFO") || !$.validate.verifyAll("ProductInfo")) {
        return;
    }
    if (!checkRequireData()) {
        return;
    }
    if(!checkNewArchive()){
        MessageBox.error("提示", "请填写合同信息！");
        return false;
    }

    var returnInfo = $('#MINOREC_PRODUCT_INFO').val();
    if("" == returnInfo||null == returnInfo||undefined == returnInfo){
        MessageBox.error("错误信息", "未获取到协议正文信息！");
        return false;
    }
    //开通时一单清添加ESP产品，集团客户信息必须已经同步至ESP平台 ，变更不走。
//    if (!$('#OPER_TYPE').val()){
//    	if (returnInfo.indexOf("380700")>0 || returnInfo.indexOf("380300")>0 || returnInfo.indexOf("921015")>0){
//            var eleProductId = $("#ELE_PRODUCT_ID").val();
//            if ("VP66666" == eleProductId && $("#ESP_SNY_STATE").val() != "1"){
//                MessageBox.error("错误信息", "该集团客户没有同步ESP平台，电子协议不允许选择任何ESP产品（云WiFi安审版、云酒馆、和商务TV），请修改电子协议！");
//                return false;
//            }
//        }
//    }



    if(!$.os.phone) {

        var submitData = new Wade.DataMap();

        var attachInfos = new Wade.DatasetList();
        if($("#NEED_CHECKE").val()=="true"){
            var listInfos = $("#CONTRACT_INFO span[id^=ATTACH_FILE_]");
            if (listInfos.length <= 0) {
                MessageBox.error("提示", "请填写合同信息！");
                return;
            }
            for (var i = 0; i < listInfos.length; i++) {
                var attachInfo = new Wade.DataMap();
                var listInfo = $(listInfos[i]);
                var flag = false;
                try{
                    //报错即不为json数据结构
                    var fileList = new Wade.DatasetList(listInfo.text());
                }catch (e) {
                    flag = true;
                }
                if (flag || fileList.length<=0) {
                    $.validate.alerter.one(listInfos[i].parent, "请提交签约附件后再提交！");
                    return false;
                }
                attachInfo.put("ARCHIVES_ID", listInfo.attr("archiveId"));
                attachInfo.put("FILE_LIST", fileList.toString());
                attachInfos.add(attachInfo);
            }
        }
        submitData.put("attachInfos",attachInfos);

        $.beginPageLoading("数据提交中，请稍后...");
        $.ajax.submit("", "", "&ACTION=onsubmit&SUBMIT_DATA=" + /*encodeURIComponent(*/submitData.toString(), "ForSubmit", function (data) {
            $.endPageLoading();
            //电子合同回调方法
            afterAct();
            //initContract();
            hidePopup(obj);

        }, function (code, info) {
            $.endPageLoading();
            MessageBox.error("错误信息", info);
        });
    }else{
        //校验是否有签字附件
        var agreementAttchList = checkAgreementAttach();
        if(!agreementAttchList){
            MessageBox.error("错误信息", "未获取到协议信息！");
            return false;
        }
        for(var i = 0;i<agreementAttchList.length;i++){
            var agreementAttch = agreementAttchList.get(i);
            var archivesId = agreementAttch.get("ARCHIVES_ID");
            var attachInfo = agreementAttch.get("ARCHIVES_ATTACH");
            if(!attachInfo){
                $.validate.alerter.one(document.getElementById("SHOW_"+archivesId), "请签名后再提交！");
                return false;
            }
        }
        //电子合同回调方法
        afterAct();
        //initContract();
        hidePopup(obj);
    }
}

function checkAgreementAttach() {

    var agreementId = $("#CONTRACT_NUMBER").val();

    var agreementAttchList = null;
    if(agreementId == null || agreementId == ""){
        MessageBox.alert("温馨提示", "请先获取合同编码！");
    }else {
        //去掉前后空格
        agreementId = $.trim(agreementId);
        $.beginPageLoading("数据校验中......");
        $.ajax.submit("", "", "&ACTION=checkAgreementAttach&AGREEMENT_ID="+agreementId, "ForSubmit", function(data){
                $.endPageLoading();

                agreementAttchList = data;

            },
            function(error_code,error_info,derror){
                $.endPageLoading();
                showDetailErrorInfo(error_code,error_info,derror);
            },
            {
                async: false  //同步请求
            }
        );

        return agreementAttchList;
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

//设置返回区域的值
function afterSubmit(temp){
    $('#TEMP_ARCHIVE_DATA').val(temp);

    var contractNumber = temp.get("CONTRACT_NUMBER");
    if("8000"==$("#ELE_PRODUCT_ID").val()){
        $("#CONTRACT_ID_VW").val(contractNumber);
    }else{
        $("#CONTRACT_ID").val(contractNumber);
    }

    var MINOREC_PRODUCT_INFO = temp.get("MINOREC_PRODUCT_INFO");

    if (MINOREC_PRODUCT_INFO != null||MINOREC_PRODUCT_INFO != ""||MINOREC_PRODUCT_INFO != undefined) {
        $("#MINOREC_PRODUCT_INFO").val(MINOREC_PRODUCT_INFO);
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
        $.ajax.submit("", "", "&ACTION=checkAgreementID&AGREEMENT_ID="+agreementId, "ForSubmit", function(data){
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

function getEleData(){
    var eleData = new Wade.DataMap();
    eleData.put("CONTRACT_NUMBER",$("#CONTRACT_NUMBER").val());
    eleData.put("PRODUCT_ID",$('#ELE_PRODUCT_ID').val());

    return eleData;
}

function popupShow(el) {
    var archiveId = $(el).attr("archiveId");

    $.beginPageLoading("数据加载中......");
    $.ajax.submit("", "", "&ACTION=getContractImg&ARCHIVE_ID="+archiveId, "ForSubmit", function(data){
            $.endPageLoading();
            var imgBase64 = data.get("imgBase64");
            $("#COTRCAT_IMG").attr("src","data:image/jpg;base64,"+imgBase64);
            showPopup("popup01-2","ImgShow");

        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });

}

function checkAgreementBpm(input) {

    var param = "&ACTION=checkAgreementBpm";
    var returnData = new Wade.DataMap();
    var archivesId = input.get("ARCHIVES_ID","");
    var contractNum = input.get("AGREEMENT_ID");
    var archiveType = input.get("AGREEMENT_DEF_ID");
    if(archivesId){
        param += "&ARCHIVES_ID="+archivesId;
        param += "&IBSYSID="+$('#ELE_IBSYSID').val();
    }

    param += "&AGREEMENT_ID="+contractNum;
    param += "&AGREEMENT_DEF_ID="+archiveType;

    $.beginPageLoading("数据校验中......");
    $.ajax.submit("", "", param, "ForSubmit", function (data) {
            $.endPageLoading();
            returnData = data;
        }, function (code, info) {
            $.endPageLoading();
            MessageBox.error("错误信息", info);
        },
        {
            async: false  //同步请求
        });

    return returnData;
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