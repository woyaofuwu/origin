function contractInfo(flag){

    var groupId = $("#GROUP_ID").val();
    if(groupId == null || groupId == ""){
        $.validate.alerter.one($("cond_GROUP_ID_INPUT")[0], "未获取到集团信息！");
        return false;
    }

    var groupId = $("#GROUP_ID").val();
    var templetId = $("#cond_TEMPLET_ID").val();
    $("#cond_MINOREC_BPM_PRODUCTID").val(templetId);
    var minorecProductid =$("#cond_MINOREC_BPM_PRODUCTID").text();
    var contractId = $("#CONTRACT_ID").val();
    if("FUSECOMMUNICATIONOPEN"==templetId||"FUSECOMMUNICATIONCHANGE" == templetId){
        minorecProductid ="VP998001";
    }else  if("YIDANQINGJIUDIAAN"==templetId || "YIDANQINGJIUDIAANCHANGE"==templetId){
        minorecProductid ="VP66666";
    }else  if(("YIDANQINGSHANGPU"==templetId || "YIDANQINGSHANGPUCHANGE"==templetId)&&"1"==flag){
        minorecProductid ="VP99999";
        contractVw="1";
    }else  if(("YIDANQINGSHANGPU"==templetId || "YIDANQINGSHANGPUCHANGE"==templetId)&&"2"==flag){
        minorecProductid ="8000";
        contractVw="2";
        contractId = $("#CONTRACT_ID_VW").val();
    }

    //清除返回标记
    if("1"==flag){
        $("#CONTRACT_FLAG").val("");
    }else if("2"==flag){
        $("#CONTRACT_FLAG_VW").val("");
    }

    var pram = "&PRODUCT_ID="+minorecProductid+"&GROUP_ID="+groupId+"&CONTRACT_ID="+contractId+"&NEED_CHECKE=true";
    pram += "&ACTION=init"+"&IBSYSID="+$("#IBSYSID").val()+"&NODE_ID="+$("#NODE_ID").val()+"&OPER_TYPE="+$("#MAIN_OPER_TYPE").val();

    $.ajax.submit("", "", pram,"ElecAgreementAdd", function(data){
        $.endPageLoading();
        if(data.get("MINOREC_PRODUCT_INFO")){
            $("#MINOREC_PRODUCT_INFO").val(data.get("MINOREC_PRODUCT_INFO"));
        }
        $("#CONTRACT_CODE").attr("disabled","disabled");
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
    if("8000" == productInfo){
        $("#CONTRACT_FLAG_VW").val("true");
    }else{
        $("#CONTRACT_FLAG").val("true");
    }

}

function submitApply() {

    if($("#FLAG_HOTEL").val()=="true"&&$("#CONTRACT_FLAG").val() != "true"){
        $.validate.alerter.one($("#CONTRACT_NAME")[0], "请上传协议【"+$("#CONTRACT_NAME").val()+"】附件，并确定后再提交本页面！");
        return false;
    }

    if($("#FLAG_VW").val()=="true"&&$("#CONTRACT_FLAG_VW").val() != "true"){
        $.validate.alerter.one($("#CONTRACT_NAME_VW")[0], "请上传协议【"+$("#CONTRACT_NAME_VW").val()+"】附件，并确定后再提交本页面！");
        return false;
    }

    var submitParam = new Wade.DataMap();
    submitParam.put("COMMON_DATA", saveEosCommonData());
    submitParam.put("BUSI_SPEC_RELE", saveBusiSpecReleData());
    submitParam.put("NODE_TEMPLETE", saveNodeTempleteData());

    $.beginPageLoading("数据提交中，请稍后...");
    $.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
            $.endPageLoading();
            debugger;
            if(data.get("ASSIGN_FLAG") == "true")
            {
                MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
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
                MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
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
                MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
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

function saveEosCommonData()
{
    var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
    if(!eosCommonData.get("CUST_NAME"))
    {
        eosCommonData.put("CUST_NAME", $("#CUST_NAME").text());
    }
    return eosCommonData;
}

function saveBusiSpecReleData()
{
    var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());

    var busiSpecRele = new Wade.DataMap();
    busiSpecRele.put("NODE_ID", eosCommonData.get("NODE_ID"));

    return busiSpecRele;
}

function saveNodeTempleteData()
{
    var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());

    var nodeTemplete = new Wade.DataMap();
    nodeTemplete.put("BPM_TEMPLET_ID", eosCommonData.get("BPM_TEMPLET_ID"));
    nodeTemplete.put("BUSI_TYPE", eosCommonData.get("BUSI_TYPE"));
    nodeTemplete.put("BUSI_CODE", eosCommonData.get("BUSI_CODE"));
    nodeTemplete.put("IN_MODE_CODE", eosCommonData.get("IN_MODE_CODE"));

    return nodeTemplete;
}