/*$Id: UserParamInfo.js,v 1.19 2013/05/02 07:41:26 liming2 Exp $*/

function initPageParam(){
    $("#pam_MAX_ITEM_PRE_DAY").val(0);
    var operState=$('#pam_MODIFY_TAG').val();
    var bodyHtml;
    if (operState == "2"){
        bodyHtml = $("#BIZ_IN_CODE").html();
        $("#SI_BASE_IN_CODE").parent().html(bodyHtml);
        $("#pam_BIZ_IN_CODE").attr("readOnly",true);
    }
    var service_id = $("#SERVICE_ID").val();

    var modifytag=$('#pam_MODIFY_TAG').val();
    if("0"==modifytag)//新增时
    {
        var operStateList = new Wade.DatasetList();
        var operState = new Wade.DataMap();
        operState.put("TEXT", "新增");
        operState.put("VALUE", "0");
        operStateList.add(operState);
        $("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);
        $("#pam_OPER_STATE").val("0"); //新增
    }else if("2"==modifytag)//对原有记录进行修改时
    {
        $('#pam_OPER_STATE').html("");
        $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
        var platsyncState=$("#pam_PLAT_SYNC_STATE").val();
        if(platsyncState == '')
        {
            platsyncState="1";
        }
        if(platsyncState=="P")//暂停
        {
            var operStateList = new Wade.DatasetList();
            var operState = new Wade.DataMap();
            operState.put("TEXT", "恢复");
            operState.put("VALUE", "05");
            operStateList.add(operState);
            $("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);

            $("#pam_OPER_STATE").val("05");//默认选为恢复
        }else if(platsyncState=="1")//正常在用
        {
            $('#pam_OPER_STATE').html("");
            $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
            var operStateList = new Wade.DatasetList();
            var operState = new Wade.DataMap();
            operState.put("TEXT", "暂停");
            operState.put("VALUE", "04");
            operStateList.add(operState);
            var operState1 = new Wade.DataMap();
            operState1.put("TEXT", "变更");
            operState1.put("VALUE", "08");
            operStateList.add(operState1);
            $("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);
            $("#pam_OPER_STATE").val("08"); //默认选为变更
        }
    }
    var bizCode=$("#pam_BIZ_CODE").val();

    if( bizCode == "AHN0019102" ||  bizCode == "2190029102" )
    {
        $('#pam_TEXT_ECGN_ZH').attr('disabled',true);//
    }

}

/**
 * 作用：用来初始化页面的显示,值会在productInfo.java里查出来后隐藏在各个服务的属性隐藏中
 * 		如果没有查出来的话，再调用getServiceParamsByajax查一把
 */
var serviceId="";
function loadUserParamInfo(){
    var prodcutId ="";
    var packageId ="";
    var param = "";
    var svcparamvalue ="";
    var userId ="";
    var urlParts = document.URL.split("?");
    var parameterParts = urlParts[1].split("&");
    var grpUserEparchyCode = $("#GRP_USER_EPARCHYCODE").val();

    try
    {
        userId=$.enterpriseLogin.getInfo().get("#USER_ID").val();//从父页面获取userId
        if(userId==undefined)
        {
            userId="";
        }
    }
    catch(e)
    {
        userId="";
    }

    try
    {
        svcparamvalue=$.getSrcWindow().selectedElements.getAttrs($("#ITEM_INDEX").val());
        if(svcparamvalue.get(0,"PARAM_VERIFY_SUCC")==undefined)
        {
            svcparamvalue="";
        }
    }
    catch(e)
    {
        svcparamvalue="";
    }


    for (i = 0; i < parameterParts.length; i++)
    {
        var pairParts = parameterParts[i].split("=");
        var pairName = pairParts[0];
        var pairValue = pairParts[1];

        if(pairName=="PRODUCT_ID")
        {
            prodcutId=pairValue;
        }
        if(pairName=="PACKAGE_ID")
        {
            packageId=pairValue;

        }
        if(pairName=="ELEMENT_ID")
        {
            serviceId=pairValue;
        }

    }

    if(svcparamvalue==""||svcparamvalue=="[]")//没有找到已存在的参数值 采用ajax异步调用 从数据库查询
    {
        if(userId != undefined && userId != "")
        {
            param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+'&EPARCHY_CODE='+grpUserEparchyCode+"&USER_ID="+userId;
        }
        else
        {
            param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+'&EPARCHY_CODE='+grpUserEparchyCode;
        }
        $.ajax.submit('this', 'getServiceParamsByajax',param ,null, function(data){
                putpagedata(data,serviceId);
                $.endPageLoading();
            },
            function(error_code,error_info,derror){
                $.endPageLoading();
                showDetailErrorInfo(error_code,error_info,derror);
            });
    }

    else
    {
        //var dataset = $.DatasetList(svcparamvalue);
        var datasetsize=svcparamvalue.getCount();
        if(datasetsize<=1) //表示没有服务的详细参数信息(因为约定第一条记录为是否需要校验,第二条记录才是具体的参数信息)
        {

            if(userId != undefined && userId != "")
            {
                param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+'&EPARCHY_CODE='+grpUserEparchyCode+"&USER_ID="+userId;
            }
            else
            {
                param='&SERVICE_ID='+serviceId+"&PRODUCT_ID="+prodcutId+"&PACKAGE_ID="+packageId+'&EPARCHY_CODE='+grpUserEparchyCode;
            }
            $.ajax.submit('this', 'getServiceParamsByajax', param, null, function(data){
                    putpagedata(data,serviceId);
                    $.endPageLoading();
                },
                function(error_code,error_info,derror){
                    $.endPageLoading();
                    showDetailErrorInfo(error_code,error_info,derror);
                });
        }
        else
        {
            putpagedata(svcparamvalue,serviceId);
        }
    }
    setDeliverNumDis();
    setStatetype();
};


/**
 * 作用：从url取关键值,并根据关键值从父页面取值   (如果URL中没有带参数，AJAX刷新后的处理)
 */
function putpagedata(dataset,serviceId)
{

    var paramVerifySucc=dataset.get(0); //如果曾经点过确认按钮，就会总是曾经点过，想抵赖是不行的
    $("#PARAM_VERIFY_SUCC").val(paramVerifySucc.get("PARAM_VERIFY_SUCC",""));//设置参数是否已经校验成功的值到页面
    var paramMap=dataset.get(1);            //改为从1取，因为 dataset的0已存放了一个表示是否点过确认按钮的状态
    var platsvcdata=paramMap.get('PLATSVC');//得到的platsvc表数据IData结构
    platsvcdata.eachKey(                    //将platsvcdata参数 填充到adcServicParamsForm

        function(key)
        {
            try
            {
                if(key=="pam_LIMIT_MAX_ITEM_PRE_DAY")
                {
                    $("#pam_MAX_ITEM_PRE_DAY").attr('max',platsvcdata.get(key,""));
                }
                if(key=="pam_LIMIT_MAX_ITEM_PRE_MON")
                {
                    $("#pam_MAX_ITEM_PRE_MON").attr('max',platsvcdata.get(key,""));
                }
                var tempElement = $("#"+key);
                if(tempElement != "undefined" && tempElement != ""){
                    tempElement.val(platsvcdata.get(key,""));
                }
            }
            catch(e)
            {
            }
        }
    );
    dealOperStateoptions(serviceId); //操作类型、服务状态设置，置恢部分输入框，管理员手机号码,个性化参数
}


/**
 * 作用：操作类型、服务状态设置，置恢部分输入框，管理员手机号码,AJAX刷新下拉列表。
 * 		基本接入号下拉列表，个性化参数
 */
function dealOperStateoptions(serviceId)
{
    var modifytag=$('#pam_MODIFY_TAG').val();
    if("0"==modifytag)//新增时
    {
        $('#pam_OPER_STATE').html("");
        $('#pam_OPER_STATE option').length = 0;  //清空操作状态选项
        jsAddItemToSelect($("#pam_OPER_STATE"), '新增', '0');
    }else if("2"==modifytag)//对原有记录进行修改时
    {
        $('#pam_OPER_STATE').html("");
        $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
        var platsyncState=$("#pam_PLAT_SYNC_STATE").val();
        if(platsyncState == '')
        {
            platsyncState="1";
        }
        if(platsyncState=="P")//暂停
        {
            $('#pam_OPER_STATE').html("");
            $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
            jsAddItemToSelect($("#pam_OPER_STATE"), '恢复', '05');
        }else if(platsyncState=="1")//正常在用
        {
            $('#pam_OPER_STATE').html("");
            $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
            jsAddItemToSelect($('#pam_OPER_STATE'), '暂停', '04');
            jsAddItemToSelect($('#pam_OPER_STATE'), '变更', '08');
        }
        //getParamLists(serviceId);      //个性化参数
    }
}

/**
 * 作用：如果用户有个性化参数，则取
 */
function getParamLists(serviceId){
    var grpUserEparchyCode = $("#GRP_USER_EPARCHYCODE").val();
    var userId=$.getSrcWindow().$("#USER_ID").val();
    if(userId==undefined)	userId="";
    var param = '&USER_ID='+userId+'&SERVICE_ID='+serviceId+'&GRP_USER_EPARCHYCODE='+grpUserEparchyCode;
    $.ajax.submit('this', 'getParamLists',param,'paramArea', function(){
            $.endPageLoading();
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}

//---------------------------------上面是初始化页面时做的操作，下面是页面中做的一些JS-----------------------------------

/**
 * 作用：下拉列表给值
 */
function jsAddItemToSelect(objSelect, objItemText, objItemValue) {
    //判断是否存在
    if (jsSelectIsExitItem(objSelect, objItemValue)) {
        MessageBox.alert("","该Item的Value值已经存在");
    } else {
        objSelect.append("<option selected value=\"" +objItemValue+"\">" +objItemText+ "</option>");
        //objSelect.options.add(varItem);
    }
}

function jsSelectIsExitItem(objSelect, objItemValue) {
    var isExit=false;
    objSelect.children("option").each(function(){
        if(this.value==objItemValue)
            return !isExit;
    });
    return isExit;
}

/**
 * 作用：动态表格列元素值的修改，
 */
function inputBlur(ipt){
    var field = ipt.getAttribute("field");
    var value = $(ipt).val();
    if(field != "" && field != null && field != value){
        ipt.parentNode.parentNode.cells[0].innerText="2";
    }
    ipt.parentNode.parentNode.cells[4].innerText=$(ipt).val();
}
//------------------------------确定时用-------------------------------------------------------------------------
function setData(){
    var minNum = $("#pam_MAX_ITEM_PRE_DAY").val();
    var maxNum = $("#pam_MAX_ITEM_PRE_MON").val();
    if ((minNum > maxNum) && (maxNum.length <= minNum.length)){
        MessageBox.alert("","每月短信数不能小于每日短信数!");
        return;
    }
    commSubmit();   //设置值到IData
}

function commSubmit()
{
    // var itemIndex= "";
    // var urlParts = document.URL.split("?");
    // var parameterParts = urlParts[1].split("&");
    // for (i = 0; i < parameterParts.length; i++)
    // {
    //     var pairParts = parameterParts[i].split("=");
    //     var pairName = pairParts[0];
    //     var pairValue = pairParts[1];
    //
    //     if(pairName=="ITEM_INDEX")
    //     {
    //         itemIndex=pairValue;
    //     }
    // }

    // var inputElements=document.getElementsByTagName('INPUT');
    // for(i=0;i<inputElements.length;i++)
    // {
    //     inputElements[i].disabled=false;
    // }
    //
    //
    // var selectElements=document.getElementsByTagName('SELECT');
    // for(i=0;i<selectElements.length;i++)
    // {
    //     selectElements[i].disabled=false;
    // }


    $("#CANCLE_FLAG").val("false");
    // var paramset = $.DatasetList();
    // var svcparamdata=$.DataMap();

    // var platsvcparam=$.ajax.buildJsonData("adcServicParamsForm_pam","pam");//获取表单以pam开头的数据
    // var platsvcdata=$.DataMap(platsvcparam);
    // svcparamdata.put("PLATSVC",platsvcdata);//PLATSVC存放页面以pam开头的数据
    svcparamdata.put("CANCLE_FLAG",$("#CANCLE_FLAG").val());
    // var serviceId=$("#SERVICE_ID").val();
    // svcparamdata.put("ID",serviceId);
    // var paramVerifySucc=new Wade.DataMap(); //加上一个表示点过确认按钮的 DataMap
    paramVerifySucc.put("PARAM_VERIFY_SUCC","true");

    // paramset.add(paramVerifySucc);
    // paramset.add(svcparamdata);
    // $("#adcServicParamsForm_pam").attr('disabled',false);//设置整个form元素都可见
    // $.getSrcWindow().selectedElements.updateAttr(itemIndex,paramset.toString());//调置到产品组件
    // $.setReturnValue();
    // setStatetype();
}

function disableEle()
{

    $("#pam_PLAT_SYNC_STATE").attr('display',true);
    if ($("#pam_BIZ_CODE").val()=='')
    {
        $("#pam_BIZ_CODE").attr('display',false);
    }else
    {
        $("#pam_BIZ_CODE").attr('display',true);
    }
    $("#pam_BIZ_NAME").attr('display',true);
    $("#pam_BIZ_STATUS").attr('display',true);
    $("#pam_PRE_CHARGE").attr('display',true);
}


/**
 * 作用：根据不同的操作类型，页面输入框的可见性.
 *  	08-变更 04- 暂停 05-恢复
 */
function setStatetype(){
    var operState=$("#pam_OPER_STATE").val();
    if (operState == "08"){
        // tableDisabled("platsvcparamtable",true);
        $("#pam_OPER_STATE").parent().attr('disabled',false);
        $("#pam_IS_TEXT_ECGN").parent().attr('disabled',false);
        $("#pam_DEFAULT_ECGN_LANG").parent().attr('disabled',false);
        $("#pam_TEXT_ECGN_ZH").parent().attr('disabled',false);
        $("#pam_TEXT_ECGN_EN").parent().attr('disabled',false);//??
        showBizInCode();
    }
    if (operState == "04"){
        tableDisabled("platsvcparamtable",true);
        showBizInCode();
    }
    if (operState == "05"){
        tableDisabled("platsvcparamtable",true);
        showBizInCode();
    }
    if (operState == "0"){
        $("#pam_PLAT_SYNC_STATE").attr('disabled',true);
        $("#pam_BIZ_CODE").attr('disabled',true);
        $("#pam_BIZ_NAME").attr('disabled',true);
        $("#pam_BIZ_TYPE_CODE").attr('disabled',true);
        $("#pam_ACCESS_MODE").attr('disabled',true);
        $("#pam_BILLING_TYPE").attr('disabled',false);
        $("#pam_PRICE").attr('disabled',false);
        $("#pam_BIZ_STATUS").attr('disabled',true);
        $("#pam_PRE_CHARGE").attr('disabled',true);
        $("#pam_SI_BASE_IN_CODE").attr('disabled',true);
    }
}

/**
 *作用：控制服务代码的显示，及校验标示的去必填
 */
function showBizInCode(){
    $("#SI_BASE_IN_CODE").parent().css("display","none");
    $("#BIZ_IN_CODE").parent().css("display","block");
    $("#pam_SVRCODETAIL").attr('nullable','yes');
}

/**
 * 作用：控制TABLE里的值是否可填
 * @param tableName
 * @param flag
 */
function tableDisabled(tableName, flag){

    $("#" + tableName + " input").each(function(){
        this.disabled=flag;
    });

    $("#" + tableName + " SELECT").each(function(){
        this.disabled=flag;
    });
}
/**
 * 作用：点取消按钮返回原值数据到父页面隐藏字段
 */
function setCancleData(obj)
{
    var serviceId="";
    var itemIndex= "";
    var urlParts = document.URL.split("?");
    var parameterParts = urlParts[1].split("&");
    var svcparamvalue="";
    try
    {
        svcparamvalue=$.getSrcWindow().selectedElements.getAttrs($("#HIDDEN_NAME").val());
        if(svcparamvalue.get(0,"PARAM_VERIFY_SUCC")==undefined)
        {
            svcparamvalue="";
        }
    }
    catch(e)
    {
        svcparamvalue="";
    }
    for (i = 0; i < parameterParts.length; i++)
    {
        var pairParts = parameterParts[i].split("=");
        var pairName = pairParts[0];
        var pairValue = pairParts[1];
        if(pairName=="ELEMENT_ID")
        {
            serviceId=pairValue;
        }
        if(pairName=="ITEM_INDEX")
        {
            itemIndex=pairValue;
        }
    }

    if(svcparamvalue==""||svcparamvalue=="[]")   //没有找到已存在的参数值 采用ajax异步调用 从数据库查询
    {
        var dataset = $.DatasetList();
        var paramVerifySuccMap=$.DataMap();
        paramVerifySuccMap.put("PARAM_VERIFY_SUCC","false");
        dataset.add(paramVerifySuccMap);
        svcparamvalue=dataset.toString();
    }
    $.getSrcWindow().selectedElements.updateAttr(itemIndex,svcparamvalue.toString());
    $.setReturnValue();

}

/**校验服务代码是否可用*/
// function checkAccessNumber(){
//     var access = $("#pam_BIZ_IN_CODE");
//     access.val("");
//     var bizInCode = $("#pam_SI_BASE_IN_CODE").val();
//     var svcCode1 = $("#pam_SVRCODETAIL").val();
//     $("#pam_SVRCODETAIL").val(svcCode1.replace(/[\W]/g,''));
//     var svcCode = $("#pam_SVRCODETAIL").val();
//     var length = svcCode.length;
//     if (length < 1){
//         MessageBox.alert("","服务代码不能为中文且必须填写!");
//         return;
//     }
//     var accessNumber = bizInCode+svcCode;
//     access.val(accessNumber);
// }

/**
 *作用：判断输入框的值
 */
function checkMessageAmount(obj){
    var rightClass = obj.getAttribute("rightClass");
    //ganquan*******************
    //根据业务属性，为amount选择不同的最大值
    var pam_BIZ_ATTR = $("#pam_BIZ_ATTR").val();
    var	amount = obj.getAttribute("maxValue");
    var inputValue = Number(obj.value);
    obj.value = Number(inputValue);
    if (rightClass <= 4)
    {
        if(Number(amount) < Number(inputValue))
        {
            parent.MessageBox.alert("","您的权限设置的每天最大短信数下发为[0],请别越权，请别越权！");
            //return;
            $("#pam_MAX_ITEM_PRE_DAY").val("");
            $("#pam_MAX_ITEM_PRE_DAY").focus();
            return;
        }
        else if(rightClass == 4)
        {
            if (inputValue == 0)
            {
                MessageBox.alert("","您的权限设置的最大下发量不能为[0],请别越权");
                obj.value = amount;
            }
        }
        else if(rightClass > 4)
        {
            MessageBox.alert("","您的权限设置的最大下发量为最大，[0]不对下发量做限制");
        }
    }
    $("#CANCLE_FLAG").val("false");
    $("#PARAM_VERIFY_SUCC").val("ture");

}

/*
   0-签约关系
   1－白名单
   2－黑名单
  如果业务类型是3或是4时，需要填写pam_BIZ_ATTR。
*/
function chargeBizAttr(){
    setDeliverNumDis();//根据选择的业务属性不同,改变限制下发次数，如果业务类型是3或是4时，需要填写此字段。填写0则没有限制。
    setPreDay();//根据选择的业务属性不同，改变对应的每月最大短信数和每天最大短信数的默认值
}

/**
 *@author:liaolc
 @function: 根据选择的业务属性不同,改变限制下发次数，如果业务类型是3或是4时，需要填写此字段。填写0则没有限制。
 */
function setDeliverNumDis(){
    var bizAttrValue=$("#pam_BIZ_ATTR").val();
    if(bizAttrValue==1||bizAttrValue==2||bizAttrValue==""||bizAttrValue==null){
        $("#pam_DELIVER_NUM").val("0");
        $("#pam_DELIVER_NUM").attr('disabled',true);
        $.Flip.get("pam_DELIVER_NUM").setDisabled(true);
    }else{
        $("#pam_DELIVER_NUM").attr('disabled',false);
        $.Flip.get("pam_DELIVER_NUM").setDisabled(false);
    }
}

/**
 *@author:ganquan
 @function: 根据选择的业务属性不同，改变对应的每月最大短信数和每天最大短信数的默认值
 */
function setPreDay(){
    var pam_BIZ_ATTR = $("#pam_BIZ_ATTR").val();
    if(pam_BIZ_ATTR == "1"){
        $("#pam_MAX_ITEM_PRE_DAY").val($("#pam_MAX_ITEM_PRE_DAY").attr("defineValue"));
        $("#pam_MAX_ITEM_PRE_MON").val($("#pam_MAX_ITEM_PRE_MON").attr("defineValue"));
    }
    else if(pam_BIZ_ATTR == "2"){
        $("#pam_MAX_ITEM_PRE_DAY").val($("#pam_MAX_ITEM_PRE_DAY").attr("defineValue1"));
        $("#pam_MAX_ITEM_PRE_MON").val($("#pam_MAX_ITEM_PRE_MON").attr("defineValue1"));
    }
}


/**校验服务代码是否可用*/
function checkAccessNumber(){
    var bizInCode = $("#pam_SI_BASE_IN_CODE").val();
    var svcCode = $("#pam_SVRCODETAIL").val();

    var bizTempStr = bizInCode.substring(0,7);

    var length = svcCode.length;

    if (isNaN(svcCode)) {
        $.validate.alerter.one($("#pam_SVRCODETAIL")[0], "只能输入数字，请重新输入!\n");
        // parent.MessageBox.alert("","只能输入数字，请重新输入!");
        $("#pam_SVRCODETAIL").val("");
        // $("#pam_SVRCODETAIL").focus();
        return;
    }

    if (length != 4 && (bizTempStr == "1065035"|| bizTempStr == "1065716") ){
        $.validate.alerter.one($("#pam_SVRCODETAIL")[0], "服务代码扩展必须为4位!\n");
        // parent.MessageBox.alert("","服务代码扩展必须为4位!");
        $("#pam_SVRCODETAIL").val("");
        // $("#pam_SVRCODETAIL").focus();
        return;
    }

    if (length != 6 && bizTempStr != "1065035" && bizTempStr != "1065716" ){
        $.validate.alerter.one($("#pam_SVRCODETAIL")[0], "服务代码扩展必须为6位，并且以01结尾!\n");
        // parent.MessageBox.alert("","服务代码扩展必须为6位，并且以01结尾!");
        $("#pam_SVRCODETAIL").val("");
        // $("#pam_SVRCODETAIL").focus();
        return;
    }
    var tempStr = svcCode.substring(length-2,length);
    if (tempStr != "01" && bizTempStr != "1065035" && bizTempStr != "1065716" ){
        $.validate.alerter.one($("#pam_SVRCODETAIL")[0], "服务代码必须以01结尾！\n");
        //parent.MessageBox.alert("","服务代码必须以01结尾！");
        $("#pam_SVRCODETAIL").val("");
        // $("#pam_SVRCODETAIL").focus();
        return;
    }
    var accessNumber = bizInCode+svcCode;

    var groupId="";
    try
    {
        groupId=$.enterpriseLogin.getInfo().get("GROUP_INFO").get("GROUP_ID");//取父页面GROUP_ID值
        if(groupId==undefined)
        {
            groupId="";
        }
    }
    catch(e)
    {
        groupId="";
    }
    $.beginPageLoading();
    $.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.param.ProxyParam", "getDumpIdByajax",'&GROUP_ID='+groupId+'&ACCESSNUMBER='+accessNumber,
        function(data){
        afterCheckAccessNumber(data);
        $.endPageLoading();
        },function(error_code,error_info,derror){
        $.endPageLoading();
        //showDetailErrorInfo(error_code,error_info,derror);
        });
}
/**校验服务代码AJAX刷新后的处理*/
function afterCheckAccessNumber(data){
    // var jsondata = $.stringifyJSON(data);
    var flag = data.get("result").get(0,"ISCHECKAACCESSNUMBER");

    // console.log("data="+data);

    var bizInCode = $("#pam_SI_BASE_IN_CODE").val();
    var svcCode = $("#pam_SVRCODETAIL").val();
    var accessNumber = bizInCode+svcCode;

    if(flag == "true"){
        $.validate.alerter.one($("#pam_SVRCODETAIL")[0], "生成服务代码可以使用！\n",'green');
        // console.log("生成服务代码可以使用！");
        // parent.MessageBox.alert("","生成服务代码可以使用！");
        $("#pam_BIZ_IN_CODE").val(accessNumber);
    }else{
        $.validate.alerter.one($("#pam_SVRCODETAIL")[0], "生成的服务代码["+accessNumber+"]不能使用，请重新输入！\n");
        // parent.MessageBox.alert("","生成的服务代码["+accessNumber+"]不能使用，请重新输入！");
        // console.log("生成的服务代码[\"+accessNumber+\"]不能使用");
        $("#pam_SVRCODETAIL").val("");
        // $("#pam_SVRCODETAIL").focus();
    }
}


