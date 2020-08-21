var ACTION_CREATE = "0";
var ACTION_DELETE = "1";
var ACTION_UPDATE = "2";
var ACTION_EXITS = "3";


/** 此段代码没有直接引用ecbusipage.js是因为成员商品受理可以不依赖集团登陆  开始 **/
var cachedCustId;
//页面激活时触发该方法，用于校验页面激活前后，登陆的政企客户是否发生改变
function onActive()
{
    var operType = $("#cond_OPER_TYPE").val();
    //未登录，但是之前有登录或者已经选择了操作，刷新
    if((!$.enterpriseLogin || !$.enterpriseLogin.isLogin()) && (operType || cachedCustId))
    {
        location.reload();
    }else if($.enterpriseLogin && $.enterpriseLogin.isLogin() && cachedCustId){//登陆了，并且老客户ID不是空，判断一下是否有更改
        var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
        var loginCustId = custInfo.get("CUST_ID");
        if (loginCustId != cachedCustId) {
            cachedCustId = loginCustId;
            $.enterpriseLogin.refreshActiveNav();
        }
    }
}

$.enterpriseLogin = (function() {
    var login, count = 0;
    if($.os.phone)
    {
        var obj = window;
        while(true)
        {
            var frame = obj.document.getElementById("custAuthFrame");
            if(frame != null &&  frame)
            {
                login = frame.contentWindow.ecLogin;
                break;
            }
            obj = obj.parent;
            count ++;
            if (count > 2) {
                break;
            }
        }
    }else{
        var scope = window;
        while (!(login = scope.parent.$.enterpriseLogin)) {
            scope = scope.parent;
            count++;
            if (count > 2) {
                break;
            }
        }
    }
    return login;
})();

$(function(){
    if($.enterpriseLogin && $.enterpriseLogin.isLogin()){
        var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
        var loginCustId = custInfo.get("CUST_ID");
        cachedCustId = loginCustId;
        $("#cond_CUST_ID").val(loginCustId);
    }
});

/** 此段代码没有直接引用ecbusipage.js是因为成员商品受理可以不依赖集团登陆  结束 **/



function queryGroupInfo(el, e)
{
    if (e.keyCode != 13 && e.keyCode != 108)
    {
        return ;
    }

    if(!$.validate.verifyField("login_groupID"))
    {
        return ;
    }

    var groupId = $.trim($("#login_groupID").val());
    queryGroup(groupId, el);
    if(!groupId)
    {
        return ;
    }
}

function queryGroup(groupId, el)
{

    var param = "GROUP_ID="+groupId;
    var checkTag = $("#checkTag").val();
    var condGroupId = $("#cond_GROUP_ID").val();
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

function queryGroupCustInfo(){

    var groupId = $.trim($("#login_groupID").val());
    if("" != groupId && null != groupId)
    {
        queryGroup(groupId, "");
    }
    else
    {
        showPopup('popup','EnterpriseLoginItem', true);
    }

    if(!groupId)
    {
        return ;
    }

}

//保存成员客户信息
function saveMemCust()
{
    var psptTypeCode = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
    if( ""== psptTypeCode || null == psptTypeCode)
    {
        MessageBox.alert("证件类型不能为空!");
        return false;
    }

    var psptId = $("#MEM_CUST_INFO_PSPT_ID").val();
    if( ""== psptId || null == psptId)
    {
        MessageBox.alert("证件号码不能为空!");
        return false;
    }

    var contactPhone = $("#MEM_CUST_INFO_CONTACT_PHONE").val();
    if( ""== contactPhone || null == contactPhone)
    {
        MessageBox.alert("联系电话不能为空!");
        return false;
    }

    var custName = $("#MEM_CUST_INFO_CUST_NAME").val();
    if( ""== custName || null == custName)
    {
        MessageBox.alert("客户名称不能为空!");
        return false;
    }

    var psptAddress = $("#MEM_CUST_INFO_PSPT_ADDRESS").val();
    if( ""== psptAddress || null == psptAddress)
    {
        MessageBox.alert("证件地址不能为空!");
        return false;
    }

    /**
     * IMS号码开户人像比对功能 校验   *************************
     * */
    var cmpTag = "1";
    $.ajax.submit(null,'isBatCmpPic','','',
        function(data){
            var flag=data.get("CMPTAG");
            if(flag=="0"){
                cmpTag = "0";
            }
            $.endPageLoading();
        },function(error_code,error_info){
            $.MessageBox.error(error_code,error_info);
            $.endPageLoading();
        },{
            "async" : false
        });

    if(cmpTag == "0"){

        //客户人像比对信息
        var picid = $("#MEM_CUST_INFO_PIC_ID").val();

        if(null != picid && picid == "ERROR")
        {
            MessageBox.error("告警提示","客户"+$("#MEM_CUST_INFO_PIC_STREAM").val(),null, null, null, null);
            return false;
        }

        //客户证件类型
        var psptTypeCode=$("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();

        //经办人信息(人像比对信息)
        var agentpicid = $("#MEM_CUST_INFO_AGENT_PIC_ID").val();

        //经办人证件类型
        var agentTypeCode = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val();

        if((psptTypeCode == "0" || psptTypeCode == "1" ) &&  picid == ""){

            //经办人名称
            var  custName = $("#MEM_CUST_INFO_AGENT_CUST_NAME").val();
            //经办人证件号码
            var  psptId = $("#MEM_CUST_INFO_AGENT_PSPT_ID").val();
            //经办人证件地址
            var  agentPsptAddr= $("#MEM_CUST_INFO_AGENT_PSPT_ADDR").val();

            if(agentTypeCode == ''&& custName == '' && psptId == '' && agentPsptAddr== ''){
                MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
                return false;
            }

            if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
                MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
                return false;
            }
        }

        if(null != agentpicid && agentpicid == "ERROR"){
            MessageBox.error("告警提示","经办人"+$("#MEM_CUST_INFO_AGENT_PIC_STREAM").val(),null, null, null, null);
            return false;
        }

        if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
            MessageBox.error("告警提示","请进行经办人摄像!",null, null, null, null);
            return false;
        }
    }
    /**IMS号码开户人像比对功能_end*************************/

    saveMemberInfo();

    $("#memCust").text($("#MEM_CUST_INFO_CUST_NAME").val());

    hidePopup('popup', 'MemCustItem', true);

    /*var groupCustId = $("#cond_CUST_ID").val();
    var groupId = $("#cond_GROUP_ID").val();
    var mebPsptId = $("#MEM_CUST_INFO_PSPT_ID").val();
    var mebCustName = $("#MEM_CUST_INFO_CUST_NAME").val();
    var psptTypeCode = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();

    var param = "&CUST_ID="+groupCustId+"&PSPT_ID="+mebPsptId+"&CUST_NAME="+mebCustName
                +"&PSPT_TYPE_CODE="+psptTypeCode+"&GROUP_ID="+groupId;

    $.beginPageLoading("证件与名称校验中......");
    $.ajax.submit(null, 'checkPsptCustName',param , '', function(data) {
         $.endPageLoading();
        var data0 = data.get(0);
        var retCode = data0.get("X_RESULTCODE");
        var retInfo = data0.get("X_RESULTINFO");
         if ("0" ==retCode)
         {
             saveMemberInfo();
             $("#memCust").text($("#MEM_CUST_INFO_CUST_NAME").val());
             hidePopup('popup', 'MemCustItem', true);
         }
         else
         {
             MessageBox.alert("提示信息",retInfo);
         }
      },
    function(error_code,error_info){
        $.endPageLoading();
        MessageBox.error("错误提示",error_info);
    });*/
}

function saveMemberInfo()
{
    // 集团成员信息
    var memCust = new $.DataMap();
    memCust.put("MEM_CUST_INFO_PSPT_TYPE_CODE",$("#MEM_CUST_INFO_PSPT_TYPE_CODE").val());
    memCust.put("MEM_CUST_INFO_PSPT_ID",       $("#MEM_CUST_INFO_PSPT_ID").val());
    memCust.put("MEM_CUST_INFO_CONTACT_PHONE", $("#MEM_CUST_INFO_CONTACT_PHONE").val());
    memCust.put("MEM_CUST_INFO_CUST_NAME",     $("#MEM_CUST_INFO_CUST_NAME").val());
    memCust.put("MEM_CUST_INFO_PSPT_ADDRESS",  $("#MEM_CUST_INFO_PSPT_ADDRESS").val());
    memCust.put("MEM_CUST_INFO_POST_CODE",     $("#MEM_CUST_INFO_POST_CODE").val());

    memCust.put("MEM_CUST_INFO_PIC_ID",     $("#MEM_CUST_INFO_PIC_ID").val());
    memCust.put("MEM_CUST_INFO_PIC_STREAM", $("#MEM_CUST_INFO_PIC_STREAM").val());
    memCust.put("MEM_CUST_INFO_BACKBASE64", $("#MEM_CUST_INFO_BACKBASE64").val());
    memCust.put("MEM_CUST_INFO_FRONTBASE64",$("#MEM_CUST_INFO_FRONTBASE64").val());
    memCust.put("MEM_CUST_INFO_SCAN_TAG",   $("#MEM_CUST_INFO_SCAN_TAG").val());

    // 法人信息
    memCust.put("MEM_CUST_INFO_legalperson",     $("#MEM_CUST_INFO_legalperson").val());
    memCust.put("MEM_CUST_INFO_termstartdate", $("#MEM_CUST_INFO_termstartdate").val());
    memCust.put("MEM_CUST_INFO_termenddate", $("#MEM_CUST_INFO_termenddate").val());
    memCust.put("MEM_CUST_INFO_startdate",$("#MEM_CUST_INFO_startdate").val());

    // 机构信息
    memCust.put("MEM_CUST_INFO_orgtype",   $("#MEM_CUST_INFO_orgtype").val());
    memCust.put("MEM_CUST_INFO_effectiveDate",   $("#MEM_CUST_INFO_effectiveDate").val());
    memCust.put("MEM_CUST_INFO_expirationDate",   $("#MEM_CUST_INFO_expirationDate").val());

    $("#MEM_CUST_INFO").val(memCust.toString());

    // 经办人信息
    var agentInfo = new $.DataMap();
    agentInfo.put("MEM_CUST_INFO_AGENT_CUST_NAME", $("#MEM_CUST_INFO_AGENT_CUST_NAME").val());
    agentInfo.put("MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE", $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val());
    agentInfo.put("MEM_CUST_INFO_AGENT_PSPT_ID", $("#MEM_CUST_INFO_AGENT_PSPT_ID").val());
    agentInfo.put("MEM_CUST_INFO_AGENT_PSPT_ADDR", $("#MEM_CUST_INFO_AGENT_PSPT_ADDR").val());

    agentInfo.put("MEM_CUST_INFO_AGENT_PIC_ID",      $("#MEM_CUST_INFO_AGENT_PIC_ID").val());
    agentInfo.put("MEM_CUST_INFO_AGENT_PIC_STREAM",  $("#MEM_CUST_INFO_AGENT_PIC_STREAM").val());
    agentInfo.put("MEM_CUST_INFO_AGENT_BACKBASE64",  $("#MEM_CUST_INFO_AGENT_BACKBASE64").val());
    agentInfo.put("MEM_CUST_INFO_AGENT_FRONTBASE64", $("#MEM_CUST_INFO_AGENT_FRONTBASE64").val());
    agentInfo.put("MEM_CUST_INFO_AGENT_SCAN_TAG",    $("#MEM_CUST_INFO_AGENT_SCAN_TAG").val());
    $("#AGENT_INFO").val(agentInfo.toString());

    // 责任人信息
    var dutyInfo = new $.DataMap();
    dutyInfo.put("MEM_CUST_INFO_RSRV_STR2", $("#MEM_CUST_INFO_RSRV_STR2").val());
    dutyInfo.put("MEM_CUST_INFO_RSRV_STR3", $("#MEM_CUST_INFO_RSRV_STR3").val());
    dutyInfo.put("MEM_CUST_INFO_RSRV_STR4", $("#MEM_CUST_INFO_RSRV_STR4").val());
    dutyInfo.put("MEM_CUST_INFO_RSRV_STR5", $("#MEM_CUST_INFO_RSRV_STR5").val());
    $("#DUTY_INFO").val(dutyInfo.toString());

    // 使用人信息
    var useInfo = new $.DataMap();
    useInfo.put("MEM_CUST_INFO_USE", $("#MEM_CUST_INFO_USE").val());
    useInfo.put("MEM_CUST_INFO_USE_PSPT_TYPE_CODE", $("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE").val());
    useInfo.put("MEM_CUST_INFO_USE_PSPT_ID", $("#MEM_CUST_INFO_USE_PSPT_ID").val());
    useInfo.put("MEM_CUST_INFO_USE_PSPT_ADDR", $("#MEM_CUST_INFO_USE_PSPT_ADDR").val());
    $("#USE_INFO").val(useInfo.toString());
}


////初始化销售品组件
function initOfferPopupItem()
{
    if(!$.os.phone){
        var groupInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
        if(typeof(groupInfo) == "undefined" || groupInfo == null || groupInfo == "")
        {
            MessageBox.error("提示信息", "请先认证政企客户信息！");
            return false;
        }
    }

    enterpriseCatalog.setOfferListDivSize();
    showPopup('popup', 'offerPopupItem',true);
}

//销售品列表选择销售品(入参：销售品编码，销售品名称，主销售品实例id，操作类型)
function chooseOfferInfo(el, operType)
{
    var offerCode = $(el).attr('OFFER_CODE');
    var offerId = $(el).attr('OFFER_ID');

    //设置销售品隐藏域
    $("#cond_OFFER_ID").val(offerId);
    $("#cond_OFFER_CODE").val(offerCode);
    $("#cond_OPER_TYPE").val(operType);

    //清空销售品设置组件的缓存数据
    $("#prodDiv_OFFER_ID").val("");
    $("#prodDiv_TOP_OFFER_ID").val("");
    $("#prodDiv_OFFER_DATA").val("");
    $("#prodDiv_OFFER_INDEX").val("");

    $("#class_OfferDataBackup").val("");
    $("#class_SELECT_GROUP_OFFER").val("");

    if("110000801110" == offerId)
    {
        $("#IMS_CLIENT").css("display","");
    }
    else
    {
        $("#IMS_CLIENT").css("display","none");
    }

    if(operType == "CrtUs")
    {//集团销售品开户

        //加载必选销售品信息
        initRequriedOffer(offerId, offerCode);
    }

    backPopup(el);

    //关闭集团目录popupItem
    enterpriseCatalog.closeEcCataPopupItem();
}

//加载必选销售品信息
function initRequriedOffer(offerId, offerCode)
{
    $.beginPageLoading("数据加载中......");
    $.ajax.submit("", "queryBaseOffer", "&OFFER_CODE="+offerCode+"&CUST_ID="+$("#cond_CUST_ID").val(), "mainOfferPart,ResourcePart,ImsInfoPart,AgentInfoPart,DutyInfoPart", function(data){

            $.endPageLoading();
            var offer = data.get("OFFER_DATA");

            var offerData = new Wade.DataMap();
            offerData.put("OFFER_ID", offerId);
            offerData.put("OFFER_CODE", offerCode);
            offerData.put("OFFER_NAME", offer.get("OFFER_NAME"));
            offerData.put("BRAND_CODE", offer.get("BRAND_CODE"));
            offerData.put("OPER_CODE", ACTION_CREATE);
            PageData.setData($("#class_"+offerId), offerData);

            $("#mainOfferPart").css("display", "");

            $("#cond_EC_BRAND_CODE").val(offer.get("BRAND_CODE"));
            $("#BRAND_CODE").val(offer.get("BRAND_CODE"));

            var eparchyCode = data.get("EPARCHY_CODE");//登录员工地州
            if("110000801110" == offerId)
            {
                $("#IMS_CLIENT").css("display","");
                if("0731" == eparchyCode)
                {
                    $("#MEM_PREPAY_INFO").css("display", "");
                }
                else
                {
                    $("#MEM_PREPAY_INFO").css("display", "none");
                }
            }
            else
            {
                $("#IMS_CLIENT").css("display", "none");
                $("#MEM_PREPAY_INFO").css("display", "none");
            }
            var imsFlag = data.get("IMSG_FLAG");
            if("true" == imsFlag)
            {
                // 经办人
                $("#MEM_CUST_INFO_AGENT_PSPT_TYPE").attr("nullable", "no");
                $("#MEM_CUST_INFO_AGENT_PSPT_ID").attr("nullable", "no");
                $("#MEM_CUST_INFO_AGENT_NAME").attr("nullable", "no");
                $("#MEM_CUST_INFO_AGENT_ADDR").attr("nullable", "no");

                // 责任人
                $("#MEM_CUST_INFO_RSRV_STR2").attr("nullable", "no");
                $("#MEM_CUST_INFO_RSRV_STR2").attr("nullable", "no");
                $("#MEM_CUST_INFO_RSRV_STR4").attr("nullable", "no");
                $("#MEM_CUST_INFO_RSRV_STR5").attr("nullable", "no");
            }
            //手动刷新scroller组件
            editMainScroll.refresh();

            //关闭集团目录popupItem
            enterpriseCatalog.closeEcCataPopupItem();

        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}


//资源号码校验
function checkResSn(ressn, restypecode, restypename)
{
    var offerId = $("#cond_OFFER_ID").val();
    if(offerId.length == 0 || offerId == null || offerId == "")
    {
        MessageBox.error("错误信息", "请先选择 商品！");
        return false;
    }

    if(ressn.length == 0){
        MessageBox.alert("提示信息","资源号码不能为空！");
        return false;
    }
    if(ressn.length < 5){
        MessageBox.alert("提示信息","资源号码位数不能小于5位！");
        return false;
    }
    if(restypecode=="0" && ressn.length<11){
        MessageBox.alert("提示信息","请输入正确的手机号码！");
        return false;
    }
    if(restypecode=="1" && ressn.length<20){
        MessageBox.alert("提示信息","请检查输入SIM卡号是否相同或错误！");
        return false;
    }
    var productID = $("#cond_OFFER_CODE").val();

    $.beginPageLoading("资源校验中...");
    $.ajax.submit(null, "checkResourceInfo", "&RES_TYPE_CODE="+restypecode + "&RES_TYPE="+restypename + "&RES_VALUE="+ressn + "&PRODUCT_ID="+productID, "",
        function(data){
            $.endPageLoading();
            afterCheckResSn(ressn, data);
        },
        function(error_code, error_info, derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code, error_info, derror);
        }
    );
}

//资源号码校验回调函数
function afterCheckResSn(ressn, data)
{
    var resultcode = data.get("X_RESULTCODE");
    var resultInfo = data.get("X_RESULTINFO");

    if (resultcode == "0"){
        $("#ACCESS_NUM_CHECKED").val(ressn);
        $("#cond_SERIAL_NUMBER").val(ressn);
        $("#RES_CHECKED_OK").css("display", "");
        $("#LIMIT_MONEY").val(data.get("LIMIT_MONEY"));//IMS吉祥号码保底金额 单位分转化为元
        $("#LIMIT_MONEY_ELEMENTS").val(data.get("LIMIT_MONEY_ELEMENTS"));
    } else {
        $("#cond_SERIAL_NUMBER").val("");
        $("#ACCESS_NUM_CHECKED").val("");
        $("#RES_CHECKED_OK").css("display", "none");
        MessageBox.alert("提示", resultInfo);
    }

}

//保存成员客户信息
function saveMemCustInfo()
{
    var memCustData = new Wade.DataMap();

    var memCustInfo = new Wade.DataMap($("#MEM_CUST_INFO").val());
    var agentInfo = new Wade.DataMap($("#AGENT_INFO").val());
    var dutyInfo = new Wade.DataMap($("#DUTY_INFO").val());
    var useInfo = new Wade.DataMap($("#USE_INFO").val());

    memCustData.put("BACKBASE64",      memCustInfo.get("MEM_CUST_INFO_BACKBASE64"));   // 身份证反面照
    memCustData.put("BIRTHDAY",        memCustInfo.get("MEM_CUST_INFO_BIRTHDAY"));
    memCustData.put("CONTACT_PHONE",   memCustInfo.get("MEM_CUST_INFO_CONTACT_PHONE"));
    memCustData.put("CUST_NAME",       memCustInfo.get("MEM_CUST_INFO_CUST_NAME"));
    memCustData.put("FRONTBASE64",     memCustInfo.get("MEM_CUST_INFO_FRONTBASE64"));   // 身份证正面照
    memCustData.put("PIC_ID",          memCustInfo.get("MEM_CUST_INFO_PIC_ID"));             // 客户照片ID
    memCustData.put("PIC_STREAM",      memCustInfo.get("MEM_CUST_INFO_PIC_STREAM"));     // 拍摄人像照片流
    memCustData.put("POST_CODE",       memCustInfo.get("MEM_CUST_INFO_POST_CODE"));        // 邮政编码
    memCustData.put("PSPT_ADDRESS",    memCustInfo.get("MEM_CUST_INFO_PSPT_ADDRESS"));
    memCustData.put("PSPT_ID",         memCustInfo.get("MEM_CUST_INFO_PSPT_ID"));
    memCustData.put("PSPT_TYPE_CODE",  memCustInfo.get("MEM_CUST_INFO_PSPT_TYPE_CODE"));
    memCustData.put("REAL_NAME",       memCustInfo.get("MEM_CUST_INFO_REAL_NAME"));          // 实名制标识
    memCustData.put("SCAN_TAG",        memCustInfo.get("MEM_CUST_INFO_SCAN_TAG"));           // 客户扫描标志

    // 经办人信息
    memCustData.put("AGENT_BACKBASE64",     agentInfo.get("MEM_CUST_INFO_AGENT_BACKBASE64"));
    memCustData.put("AGENT_CUST_NAME",      agentInfo.get("MEM_CUST_INFO_AGENT_NAME"));
    memCustData.put("AGENT_FRONTBASE64",    agentInfo.get("MEM_CUST_INFO_AGENT_FRONTBASE64"));
    memCustData.put("AGENT_PIC_ID",         agentInfo.get("MEM_CUST_INFO_AGENT_PIC_ID"));
    memCustData.put("AGENT_PIC_STREAM",     agentInfo.get("MEM_CUST_INFO_AGENT_PIC_STREAM"));
    memCustData.put("AGENT_PSPT_ADDR",      agentInfo.get("MEM_CUST_INFO_AGENT_ADDR"));
    memCustData.put("AGENT_PSPT_ID",        agentInfo.get("MEM_CUST_INFO_AGENT_PSPT_ID"));
    memCustData.put("AGENT_PSPT_TYPE_CODE", agentInfo.get("MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE"));
    memCustData.put("AGENT_SCAN_TAG",       agentInfo.get("MEM_CUST_INFO_AGENT_SCAN_TAG"));

    // 使用人信息
    memCustData.put("USR",                useInfo.get("MEM_CUST_INFO_USE"));               // 使用人姓名
    memCustData.put("USR_PSPT_TYPE_CODE", useInfo.get("MEM_CUST_INFO_USE_PSPT_TYPE_CODE"));
    memCustData.put("USR_PSPT_ID",        useInfo.get("MEM_CUST_INFO_USE_PSPT_ID"));
    memCustData.put("USR_PSPT_ADDR",      useInfo.get("MEM_CUST_INFO_USE_PSPT_ADDR"));

    // 责任人信息
    memCustData.put("RSRV_STR2", dutyInfo.get("MEM_CUST_INFO_RSRV_STR2"));  // 责任人姓名
    memCustData.put("RSRV_STR3", dutyInfo.get("MEM_CUST_INFO_RSRV_STR3"));  // 责任人证件类型
    memCustData.put("RSRV_STR4", dutyInfo.get("MEM_CUST_INFO_RSRV_STR4"));  // 责任人证件号码
    memCustData.put("RSRV_STR5", dutyInfo.get("MEM_CUST_INFO_RSRV_STR5"));  // 责任人地址

    // 法人信息
    memCustData.put("legalperson",   memCustInfo.get("MEM_CUST_INFO_legalperson"));        // 法人
    memCustData.put("termstartdate", memCustInfo.get("MEM_CUST_INFO_termstartdate"));    //营业开始日期
    memCustData.put("termenddate",   memCustInfo.get("MEM_CUST_INFO_termenddate"));        // 营业结束日期
    memCustData.put("startdate",     memCustInfo.get("MEM_CUST_INFO_startdate"));            // 成立日期

    // 机构信息
    memCustData.put("orgtype",        memCustInfo.get("MEM_CUST_INFO_orgtype"));                // 机构类型
    memCustData.put("effectiveDate",  memCustInfo.get("MEM_CUST_INFO_effectiveDate"));    // 有效日期
    memCustData.put("expirationDate", memCustInfo.get("MEM_CUST_INFO_expirationDate"));  // 失效日期

    pageData.setMemCustInfo(memCustData);
}

//保存成员用户信息
function saveMemSubscriberInfo()
{
    var memSubscriber = new Wade.DataMap();
    memSubscriber.put("USER_TYPE_CODE", $("#MEM_USER_INFO_USER_TYPE_CODE").val());
    memSubscriber.put("REMARK", $("#REMARKS").val());

    pageData.setMemSubscriber(memSubscriber);
}

//保存成员账户信息
function saveMemAcctInfo()
{
    var memAcctInfo = new Wade.DataMap();
    memAcctInfo.put("PAY_NAME", $("#MEM_ACCT_INFO_PAY_NAME").val());
    memAcctInfo.put("PAY_MODE_CODE", $("#MEM_ACCT_INFO_PAY_MODE_CODE").val());

    pageData.setMemAccountInfo(memAcctInfo);
}

//保存公共信息
function saveCommonInfo()
{
    var commonData = pageData.getCommonInfo();
    commonData.put("CUST_INFO_TELTYPE", $("#CUST_INFO_TELTYPE").val());  //IMS客户端

    if($("#IS_EXPER").val())
    {
        commonData.put("IS_EXPER", $("#IS_EXPER").val());  //是否体验客户
    }

    var resList = new Wade.DatasetList();
    var res = new Wade.DataMap();
    res.put("RES_CODE", $("#cond_SERIAL_NUMBER_INPUT").val());
    res.put("RES_TYPE_CODE", $("#cond_SERIAL_NUMBER_INPUT").attr("restypecode"));
    res.put("RES_TYPE", $("#cond_SERIAL_NUMBER_INPUT").attr("restype"));
    resList.add(res);
    commonData.put("RES_INFO", resList);

    commonData.put("CUST_ID", $("#cond_CUST_ID").val());
    commonData.put("GROUP_ID", $("#cond_GROUP_ID").val());

    pageData.setCommonInfo(commonData);

}

//保存产品信息
function saveProductInfo()
{
    var commonData = pageData.getCommonInfo();

    commonData.put("PRODUCT_ID", $("#cond_OFFER_CODE").val());
    commonData.put("OFFER_CODE", $("#cond_OFFER_CODE").val());

    pageData.setCommonInfo(commonData);
}

function checkBeforeSubmit()
{
    if(!$.os.phone){
        // 登陆校验
        if (!$.enterpriseLogin.isLogin()) {
            MessageBox.error("错误信息", "请先验证的集团客户！");
            return false;
        }
    }

    var offerId = $("#cond_OFFER_ID").val();
    if(offerId == null || offerId == "")
    {
        MessageBox.error("错误信息", "请选择商品！");
        return false;
    }

    var mainOfferLis = $("#mainOfferPart").find("div[class=side]");
    for(var i = 0; i < mainOfferLis.length; i++)
    {
        if($(mainOfferLis[i]).css("display") != "none")
        {
            MessageBox.alert("提示信息", "请先设置商品信息，再提交！");
            return false;
        }
    }

    var checkSn = $("#ACCESS_NUM_CHECKED").val();
    if("" == checkSn || null == checkSn)
    {
        MessageBox.alert("提示信息", "请先认证手机号码，再提交！");
        return false;
    }

    // 校验客户信息
    var memCustInfo = new Wade.DataMap($("#MEM_CUST_INFO").val());
    if("" == memCustInfo || null == memCustInfo || memCustInfo.length == 0)
    {
        MessageBox.alert("提示信息", "请先设置客户信息，再提交！");
        return false;
    }

    // 校验账户信息等
    var s = $.validate.verifyAll("UserAcctInfoPart");
    if(s == false){
        return false;
    }

    /**
     * IMS号码开户人像比对功能 校验
     * */
    var cmpTag = "1";
    $.ajax.submit(null,'isBatCmpPic','','',
        function(data){
            var flag=data.get("CMPTAG");
            if(flag=="0"){
                cmpTag = "0";
            }
            $.endPageLoading();
        },function(error_code,error_info){
            $.MessageBox.error(error_code,error_info);
            $.endPageLoading();
        },{
            "async" : false
        });

    if(cmpTag == "0"){

        //客户人像比对信息
        var picid = $("#MEM_CUST_INFO_PIC_ID").val();

        if(null != picid && picid == "ERROR")
        {
            MessageBox.error("告警提示","客户"+$("#MEM_CUST_INFO_PIC_STREAM").val(),null, null, null, null);
            return false;
        }

        //客户证件类型
        var psptTypeCode=$("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();

        //经办人信息(人像比对信息)
        var agentpicid = $("#MEM_CUST_INFO_AGENT_PIC_ID").val();

        //经办人证件类型
        var agentTypeCode = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val();

        if((psptTypeCode == "0" || psptTypeCode == "1" ) &&  picid == ""){

            //经办人名称
            var  custName = $("#MEM_CUST_INFO_AGENT_CUST_NAME").val();
            //经办人证件号码
            var  psptId = $("#MEM_CUST_INFO_AGENT_PSPT_ID").val();
            //经办人证件地址
            var  agentPsptAddr= $("#MEM_CUST_INFO_AGENT_PSPT_ADDR").val();

            if(agentTypeCode == ''&& custName == '' && psptId == '' && agentPsptAddr== ''){
                MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
                return false;
            }

            if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
                MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
                return false;
            }
        }

        if(null != agentpicid && agentpicid == "ERROR"){
            MessageBox.error("告警提示","经办人"+$("#MEM_CUST_INFO_AGENT_PIC_STREAM").val(),null, null, null, null);
            return false;
        }

        if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
            MessageBox.error("告警提示","请进行经办人摄像!",null, null, null, null);
            return false;
        }
    }
    /**IMS号码开户人像比对功能_end*************************/

    return true;
}


function submitAll(el) {
    if(!checkBeforeSubmit())
    {
        return false;
    }

    //保存成员产品信息
    saveProductInfo();

    //保存成员客户信息
    saveMemCustInfo();

    //保存成员账户信息
    saveMemSubscriberInfo();

    //保存成员账户信息
    saveMemAcctInfo();

    //保存公共信息
    saveCommonInfo();

    PageData.submit();
}



//获取实名制流水
function  getTradeSend(){
    var transactionId = $("#TRANSACTION_ID").val();
    if(!isNull(transactionId)){
        return ;
    }
    $.beginPageLoading("获取实名制流水......");
    $.ajax.submit(null,'getTradeSend',null,null,function(data){
            $("#TRANSACTION_ID").val(data.get("TRANSACTION_ID"));//实名制流水
            $("#REALNAME_SEQ").val(data.get("TRANSACTION_ID"));//实名制流水

            var tradeData = $("#TRADE_DATA").val();
            if(tradeData){
                var tradeDataMap = $.DataMap(tradeData);
                tradeDataMap.put("TRANSACTION_ID",data.get("TRANSACTION_ID"));
                tradeDataMap.put("REALNAME_FLAG",$("#IS_REAL").val());
                $("#TRADE_DATA").val(tradeDataMap.toString());
            }
            $.endPageLoading();
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            alert(error_info);
            //showDetailErrorInfo(error_code,error_info,derror);
        });
}


function openStaffPopup(fieldName)
{
    $("#staffSelFrame").contents().find("#field_name").val(fieldName);
    showPopup('staffPicker','staffPickerHome');
}


function isChinese(val)
{
    var model = /^[\u4E00-\ue864\u3400-\u4DB5]+$/;

    if(model.exec(val)) return true;

    return false;
}

function isContainPartSpecialEN(val) //包含简体汉字与（）-
{
    var model= /^[A-Za-z0-9\u4E00-\u9FA5\u3400-\u4DB5\(\)\（\）\-\—]+$/;
    if(!model.exec(val)) return true;
    return false;
}