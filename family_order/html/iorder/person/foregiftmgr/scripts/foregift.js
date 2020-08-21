window.onload=function(){
    // setTimeout(function () {
    //     $("#DeptTable").tap(function(e){
    //         var forgiftCode = DeptTable.getRowData(DeptTable.selected).get("FOREGIFT_CODE");
    //         var forgifttext = DeptTable.getRowData(DeptTable.selected).get("FOREGIFT_CODE_BOX_TEXT");
    //         MessageBox.alert(forgiftCode);
    //         //插入押金类型
    //         $("#FOREGIFT_CODE").val(forgiftCode);
    //         addSelectValue("#FOREGIFT_CODE",forgifttext);
    //
    //     });
    // },1000);
}
var delAble = false;// 删除行的标志
var oldInvoiceNo="";//记录执行ajax的发票号码
var invoiceEffective=false;//发票号码有效的标志
var manPicID=""//人像上传标识,提交前必须上传人像

function refreshPartAtferAuth(data) {
    $.beginPageLoading("正在查询数据...");
    $.ajax.submit('', 'loadChildInfo', "&USER_INFO="
        + (data.get("USER_INFO")).toString() + "&CUST_INFO="
        + (data.get("CUST_INFO")).toString() + "&ACCT_INFO="
        + (data.get("ACCT_INFO")).toString(),
        'ForeGiftPart2,hiddenPart', function(date) {
            $.endPageLoading();
            //$("#ForeGiftPart2").attr("disabled", false);
            //$("#invoicePart").attr("disabled", false);
            operForegiftType();
            $("#DeptTable").tap(function(e){
                var forgiftCode = DeptTable.getRowData(DeptTable.selected).get("FOREGIFT_CODE");
                var forgifttext = DeptTable.getRowData(DeptTable.selected).get("FOREGIFT_CODE_BOX_TEXT");
                //插入押金类型
                $("#FOREGIFT_CODE").val(forgiftCode);
                addSelectValue("#FOREGIFT_CODE",forgiftCode);
            });
            $('#USER_ID_FOR_BALANCE').val(date.get("USER_ID"));
        }, function(error_code, error_info,detail) {
            $.endPageLoading();
            MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
        });


    // setTimeout(function () {
    //     $("#DeptTableValues tr").bind("tap",function () {
    //         var forgiftCode = DeptTable.getRowData(DeptTable.selected).get("FOREGIFT_CODE");
    //         var forgifttext = DeptTable.getRowData(DeptTable.selected).get("FOREGIFT_CODE_BOX_TEXT");
    //         //插入押金类型
    //         $("#FOREGIFT_CODE").val(forgiftCode);
    //         addSelectValue("#FOREGIFT_CODE",forgiftCode);
    //     });
    //
    //
    //     $("#DeptTable").tap(function(e){
    //         var forgiftCode = DeptTable.getRowData(DeptTable.selected).get("FOREGIFT_CODE");
    //         var forgifttext = DeptTable.getRowData(DeptTable.selected).get("FOREGIFT_CODE_BOX_TEXT");
    //         //插入押金类型
    //         $("#FOREGIFT_CODE").val(forgiftCode);
    //         addSelectValue("#FOREGIFT_CODE",forgiftCode);
    //     });
    // },500);
}
function tableRowClick() {
    // 获取选择行的数据
    var rowData = DeptTable.getRowData(DeptTable.selected),
        opCode = $("#OP_CODE").val();
    $("#FOREGIFT_CODE").val(rowData.get("FOREGIFT_CODE"));
    if ("1" === opCode || "2" === opCode) {
        $("#PAY_MONEY").val(rowData.get("NOW_MONEY"));
    }
    delAble = rowData.get("FOREGIFT_UPDATE_TIME") === "";
}

function findForegiftValByName(keyName,objName) {
    $.each($("#"+keyName+"_float li"),function (name,value) {
        if($(this).attr("title")==objName) {return this.attr("val");}
    },objName);
}
function operForegiftType() {

    var op_code = $("#OP_CODE");
    var btCreate = $("#bcreate");
    var btUpdate = $("#bupdate");
    var btDelete = $("#bdelete");

    $.feeMgr.clearFeeList();

    if (op_code.val() == "1") {

        btCreate.attr("disabled", true);
        btCreate.attr("className", "e_button-form e_dis");

        btUpdate.attr("disabled", false);
        btUpdate.attr("className", "e_button-form");

        btDelete.attr("disabled", true);
        btDelete.attr("className", "e_button-form e_dis");

    }else if(op_code.val() == "0"){

        btCreate.attr("disabled", false);
        btCreate.attr("className", "e_button-form");

        btUpdate.attr("disabled", true);
        btUpdate.attr("className", "e_button-form e_dis");

        btDelete.attr("disabled", false);
        btDelete.attr("className", "e_button-form");
    }else if(op_code.val() == "2"){
        $("#DeptTableValues").empty();//清空使用号码查出的押金列表

        btCreate.attr("disabled", true);
        btCreate.attr("className", "e_button-form e_dis");

        btUpdate.attr("disabled", false);
        btUpdate.attr("className", "e_button-form");

        btDelete.attr("disabled", true);
        btDelete.attr("className", "e_button-form e_dis");

       MessageBox.alert("无主发票清退,需要通过校验发票查询出押金列表!");
    }else{

    }
}
/**
 * 获取发票信息
 */
function checkInvoiceNo(){
    var op_code = $("#OP_CODE").val();
    if(!op_code || op_code ==""){
       MessageBox.alert("请选择操作类型");
        return false;
    }
    if(!$.validate.verifyAll("invoicePart"))return false;
    if($("#INVOICE_NO").val()==""){
        if($("#INVOICE_TAG").val()=="1")//判断是否需要输入发票号码或无主发票
        {
           MessageBox.alert("请输入发票号码！");
        }
        return;
    }

    $.beginPageLoading("正在查询发票数据...");
    var refreshPart='invoicePart';
    if(op_code=="2"){
        refreshPart='invoicePart,foreGiftTable';
    }
    $.ajax.submit('hiddenPart', 'getInvoiceInfo', "&INVOICE_NO="+$("#INVOICE_NO").val()+"&OPER_TYPE="+$("#OP_CODE").val(),
        refreshPart,
        function(data){
            afterFunc();
            $('#FOREGIFT_4A_TAG').val(data.get('FOREGIFT_4A_TAG'));
            $('#FOREGIFT_4A_TYPE').val(data.get('FOREGIFT_4A_TYPE'));
            $('#FOREGIFT_REMARK').val(data.get('FOREGIFT_REMARK'));
            $.endPageLoading();
        },function(error_code,error_info)
        {
            $.endPageLoading();
            $.MessageBox.error(error_code,error_info);
        });
}

function afterFunc(){
    var op_code = $("#OP_CODE").val();

    if($("#IS_USED").val()==""){
       MessageBox.alert("网络故障，查询发票号码失败！");
        invoiceEffective=false;
        return false;
    }else{
        oldInvoiceNo=$("#INVOICE_NO").val();//记录执行ajax的发票号码
        invoiceEffective=true;
    }
    if(op_code =="0" && $("#IS_USED").val()=="YES"){
       MessageBox.alert("收取押金:该发票号码已经使用，业务无法继续！");
        invoiceEffective=false;
        return false;
    }
    if(op_code =="1"){
        if($("#IS_USED").val()=="NO"){
           MessageBox.alert("清退押金:根据发票号码未找到收取记录，业务无法继续！");
            invoiceEffective=false;
            return false;
        }
        if($("#PROCESS_TAG").val()=="1"){
           MessageBox.alert("清退押金:该发票已经清退了押金！");
            invoiceEffective=false;
            return false;
        }
    }
    if(op_code =="2" && $("#IS_USED").val()=="NO"){
        $.feeMgr.clearFeeList();//清空费用列表的所有费用
       MessageBox.alert("根据发票号码未找到无主押金！");
        return false;
    }
    if(op_code =="2" && $("#IS_USED").val()=="YES"){
        $.cssubmit.disabledSubmitBtn(false);
    }
}

function foregiftType() {// 选择押金默认设置打印名称和打印证件号码
    var name = $("#CUST_NAME1").val();
    var pspt = $("#PSPT_ID").val();
    var fname = $("#FOREGIFT_CUST_NAME").val();
    var fpspt = $("#FOREGIFT_PSPT_ID").val();

    if (fname == "" && fpspt == "") {
        $("#FOREGIFT_CUST_NAME").val(name);
        $("#FOREGIFT_PSPT_ID").val(pspt);
    }
}
/**
 * 收取押金
 * @returns
 */
function receiveForeGift(){

    var flag = "0";
    var foreGiftCode = $("#FOREGIFT_CODE").val();
    var payMoney = $("#PAY_MONEY").val();
    var foreCustName = $("#FOREGIFT_CUST_NAME").val();
    var forePsptId = $("#FOREGIFT_PSPT_ID").val();
    /*
     * if(!$.isNumeric(payMoney)){MessageBox.alert("金额输入不合法！"); return false; }
     */
    if (foreGiftCode == "") {
       MessageBox.alert("请选择押金类型！");
        return false;
    }
    if (payMoney <= 0) {
       MessageBox.alert("请输入押金金额！");
        return false;
    }
    if (payMoney > 1000000) {
       MessageBox.alert("收取的费用不能大于一百万！");
        return false;
    }

    // 获取编辑区的数据
    var custEdit = new $.DataMap();//$.ajax.buildJsonData("EditPart");
    custEdit.put("OP_CODE",$("#OP_CODE").val());
	custEdit.put("FOREGIFT_CODE",$("#FOREGIFT_CODE").val());
    custEdit.put("PAY_MONEY",$("#PAY_MONEY").val());
    custEdit.put("FOREGIFT_CUST_NAME",$("#FOREGIFT_CUST_NAME").val());
    custEdit.put("FOREGIFT_PSPT_ID",$("#FOREGIFT_PSPT_ID").val());
    var deptTable = DeptTable.getData();

    for (var i = 0; i < deptTable.length; i++) {
        if (deptTable.get([i], "FOREGIFT_CODE") == $("#FOREGIFT_CODE").val()) {// 押金类型编码  相同需要UPDATE

            var pre_money = deptTable.get([i], "PRE_MONEY");
            custEdit.put("PAY_MONEY", Math.round(parseFloat($("#PAY_MONEY").val()) * 100) / 100);// 现缴押金金额（元）
            custEdit.put("NOW_MONEY", Math.round(parseFloat(parseFloat(payMoney)+ parseFloat(pre_money)) * 100) / 100);// 现有押金金额（元）
            custEdit.put("FOREGIFT_CUST_NAME",foreCustName);
            custEdit.put("FOREGIFT_PSPT_ID", forePsptId);
            // var rowData = DeptTable.getRowData($("#DeptTable")[0].rows[i + 1]);
            // if (rowData.length == 0) {
            //    MessageBox.alert("请您选择记录后再进行操作！");
            //     return false;
            // }

            // 更新表格数据
            DeptTable.updateRow($.parseJSON(custEdit.toString()), i);
            flag = "1";

            /*
             * 费用组件
             * [TRADE_TYPE_CODE,MODE,CODE,FEE,PAY,ELEMENT_ID],PAY表示实缴费用，PAY，ELEMENT_ID操作时候可以不传，其他为必传)：
             */
            // 调用费用之前清除一次
            $.feeMgr.removeFee('290', '1', $("#FOREGIFT_CODE").val());

            var fee = Math.round(parseFloat(parseFloat($("#PAY_MONEY").val()) * 100));

            var feeData = new $.DataMap();
            feeData.put("TRADE_TYPE_CODE", "290");
            feeData.put("MODE", "1");// 大类 押金
            feeData.put("CODE", $("#FOREGIFT_CODE").val());// 小类
            feeData.put("FEE", fee);// 应缴金额
            feeData.put("PAY", fee);// 实际缴纳
            $.feeMgr.insertFee(feeData)

            break;
        }
    }
    if (flag == "0") {
        // 新增
        custEdit["FOREGIFT_CODE_BOX_TEXT"] =$("#FOREGIFT_CODE").text();//$("#FOREGIFT_CODE")[0].options($("#FOREGIFT_CODE")[0].selectedIndex).text;// 押金类型$("#FOREGIFT_CODE").find("option:selected").text().trim();
        custEdit["FOREGIFT_CODE"] =$("#FOREGIFT_CODE").val();// $("#FOREGIFT_CODE")[0].options($("#FOREGIFT_CODE")[0].selectedIndex).value;// 押金类型编码
        custEdit["PRE_MONEY"] = "0";// 原有押金金额（元）
        custEdit["PAY_MONEY"] = Math.round(parseFloat($("#PAY_MONEY").val()) * 100) / 100;// 现缴押金金额（元）
        custEdit["NOW_MONEY"] = Math.round(parseFloat($("#PAY_MONEY").val()) * 100) / 100;// 现有押金金额（元）
        custEdit["FOREGIFT_UPDATE_TIME"] = "";
        custEdit["FOREGIFT_CUST_NAME"] = foreCustName;
        custEdit["FOREGIFT_PSPT_ID"] = forePsptId;

        DeptTable.addRow(custEdit);

        /*
         * 费用组件
         * [TRADE_TYPE_CODE,MODE,CODE,FEE,PAY,ELEMENT_ID],PAY表示实缴费用，PAY，ELEMENT_ID操作时候可以不传，其他为必传)：
         */
        // 调用费用之前清除一次
        $.feeMgr.removeFee('290', '1', $("#FOREGIFT_CODE").val());
        var fee = Math.round(parseFloat($("#PAY_MONEY").val()) * 100);
        var feeData = new $.DataMap();
        feeData.put("TRADE_TYPE_CODE", "290");
        feeData.put("MODE", "1");// 大类 押金
        feeData.put("CODE", $("#FOREGIFT_CODE").val());// 小类
        feeData.put("FEE", fee);// 应缴金额
        feeData.put("PAY", fee);// 实际缴纳
        $.feeMgr.insertFee(feeData)
    }
    cleanForegift();
}

/**
 * 删除押金
 * @returns {Boolean}
 */
function deleteForeGift() {
    if(DeptTable.selected != null) {
    if(DeptTable.getRowData(DeptTable.selected).get("FOREGIFT_UPDATE_TIME") == ""){
        var rowData = DeptTable.getRowData(DeptTable.selected);
        var foregiftCode = rowData.get("FOREGIFT_CODE");
        if (rowData.length == 0) {
           MessageBox.alert("请您选择记录后再进行删除操作！");
            return false;
        }
        DeptTable.deleteRow(DeptTable.selected);
        // 费用组件
        $.feeMgr.removeFee('290', '1', foregiftCode);
    }
    else {
       MessageBox.alert("历史押金记录不允许删除！");
    }
    } else {
       MessageBox.alert("请选择要删除的行！");
    }
    cleanForegift();
}
/**
 * 清退押金
 * @returns {Boolean}
 */
function returnForeGift() {
    if(DeptTable.selected == null) {
        MessageBox.alert("请先选中清退的行！");
    }

    var foreGiftCode = $("#FOREGIFT_CODE").val();
    var payMoney = parseFloat($("#PAY_MONEY").val());
    // 获取编辑区的数据
    var rowData = DeptTable.getRowData(DeptTable.selected);
    var pre_money = rowData.get("PRE_MONEY");

    if(rowData.get("FOREGIFT_CODE") != foreGiftCode){
       MessageBox.alert("选择记录的押金类型与要清退的押金类型不一致！");
        return false;
    }

    if (rowData.length == 0) {
       MessageBox.alert("请您选择记录后再进行操作！");
        return false;
    }
    if (parseFloat(payMoney) * (-1) + parseFloat(pre_money) < 0) {

       MessageBox.alert("清退的押金不可以大于原有的押金！");
        return false;
    }

    if (foreGiftCode == "3" && $("#REMOVE_TAG").val() == "0") {

        if ($("#LONG_SERVICE_ID").val() == "15"
            && $("#ABOVE_START_DATE").val() == "YES") {
           MessageBox.alert("必须取消国际长途才能清退押金！");
            return false;
        }
        if ($("#ROAM_SERVICE_ID").val() == "19") {
           MessageBox.alert("必须取消国际漫游才能清退押金！");
            return false;
        }

        if($("#OP_CODE").val()=="1" && $("#HFQT_PRV").val() !="1" && $("#Balance").val()=="0"){
           MessageBox.alert("用户需缴清当前所有话费后才能办理押金清退业务！");
            return false;
        }
        if($("#CANCEL_LONGROAM_TIME").val()=="NO")
        {
           MessageBox.alert("必须取消国际长途/漫游满15天才能清退押金！");
            return false;
        }
    }

    if(foreGiftCode=="30"&&$("#FOREGIFT_LIMIT").val()=="YES"){
       MessageBox.alert("188靓号抢鲜活动保证金将在活动协议规定的期限内主动退还到客户指定的银行账户，不能在前台办理押金退款业务。");
        return false;
    }
    if (payMoney <= 0) {
       MessageBox.alert("清退的押金金额不能小于零！");
        return false;
    }
    // 获取编辑区的数据
    var custEdit = new $.DataMap();//$.ajax.buildJsonData("EditPart");
    custEdit.put("OP_CODE",$("#OP_CODE").val());
    custEdit.put("FOREGIFT_CODE",$("#FOREGIFT_CODE").val());
    custEdit.put("PAY_MONEY",$("#PAY_MONEY").val());
    custEdit.put("FOREGIFT_CUST_NAME",$("#FOREGIFT_CUST_NAME").val());
    custEdit.put("FOREGIFT_PSPT_ID",$("#FOREGIFT_PSPT_ID").val());
    /**
     * REQ201610110009_押金业务界面增加判断拦截
     * @author zhuoyingzhi
     * 20161117
     */
    var forGift4AType=$("#FOREGIFT_4A_TYPE").val().split('|');
    var isforGift4A=false;
    for(var i= 0;i<forGift4AType.length;i++){
        if(foreGiftCode==forGift4AType[i]){
            isforGift4A=true;
            break;
        }
    }

    if($("#OP_CODE").val() == '2'){
        //无主押金清退(效验)
        var process_tag=rowData.get("FOREGIFT_PROCESS_TAG");
        var end_date=rowData.get("FOREGIFT_END_DATE");
        var user_id=rowData.get("FOREGIFT_USER_ID");
        var rsrv_num2=rowData.get("FOREGIFT_RSRV_NUM2");

        var foregift_code=$("#FOREGIFT_CODE").val();

        var url="&FOREGIFT_PROCESS_TAG="+process_tag+"&FOREGIFT_END_DATE="+end_date+"&FOREGIFT_USER_ID="+user_id+"&FOREGIFT_RSRV_NUM2="+rsrv_num2
            +"&FOREGIFT_CODE="+foregift_code;
        $.beginPageLoading("正在效验...");
        $.ajax.submit('', 'checkNotForeGift', url ,'', function() {
            $.endPageLoading();
            //如果是租机押金、赠机押金，调金库认证,含租金都认证，由上级主管确认客户已退机
            if($("#FOREGIFT_4A_TAG").val()=='1'&&isforGift4A){
                $.beginPageLoading("正进行金库认证...");
                $.treasury.auth("FOREGIFT_4A_DepositRefund",
                    function (ret) {
                        $.endPageLoading();
                        if (true === ret) {
                            MessageBox.alert("认证成功");
                            refreshFroeGiftInfo(custEdit,payMoney,foreGiftCode,pre_money)
                        } else {
                            MessageBox.alert("认证失败");
                        }
                    });


            }else{
                refreshFroeGiftInfo(custEdit,payMoney,foreGiftCode,pre_money)
            }

        }, function(error_code, error_info,detail) {
            $.endPageLoading();
            MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
        });
    }else{
        var SERIAL_NUMBER = $("#AUTH_SERIAL_NUMBER").val();
        var USER_ID_FOR_BALANCE = $("#USER_ID_FOR_BALANCE").val();
        var url="&SERIAL_NUMBER="+SERIAL_NUMBER+"&USER_ID_FOR_BALANCE="+USER_ID_FOR_BALANCE;
        if(foreGiftCode=='3'){
            //话费类押金清退(效验)
            $.beginPageLoading("正在效验...");
            $.ajax.submit('', 'checkForeGift',url ,'', function() {
                $.endPageLoading();
                refreshFroeGiftInfo(custEdit,payMoney,foreGiftCode,pre_money)
            }, function(error_code, error_info,detail) {
                $.endPageLoading();
                MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
            });
        }else if($("#FOREGIFT_4A_TAG").val()=='1'&&isforGift4A){
            //如果是租机押金、赠机押金，调金库认证，由上级主管确认客户已退机
            $.beginPageLoading("正进行金库认证...");
            $.treasury.auth("FOREGIFT_4A_DepositRefund",
                function (ret) {
                    $.endPageLoading();
                    if (true === ret) {
                        MessageBox.alert("认证成功");
                        refreshFroeGiftInfo(custEdit,payMoney,foreGiftCode,pre_money)
                    } else {
                        MessageBox.alert("认证失败");
                    }
                });

        }else{
            refreshFroeGiftInfo(custEdit,payMoney,foreGiftCode,pre_money)
        }

    }


}

/**
 * 刷新押金资料列表信息
 * @param custEdit
 * @param payMoney
 * @param foreGiftCode
 */
function refreshFroeGiftInfo(custEdit,payMoney,foreGiftCode,pre_money){
    custEdit.put("PAY_MONEY", Math.round(parseFloat(payMoney) * (-1) * 100) / 100);// 现缴押金金额（元）
    custEdit.put("NOW_MONEY", Math.round(parseFloat(parseFloat(payMoney) * (-1) + parseFloat(pre_money)) * 100) / 100);

    // 更新表格数据
    DeptTable.updateRow($.parseJSON(custEdit.toString()), DeptTable.selected);

    var rowData2 =DeptTable.getRowData(DeptTable.selected);

    refreshFee(foreGiftCode,rowData2.get("PAY_MONEY"));

    cleanForegift();
}

/**
 * 更新费用组件
 * @param foreGiftCode
 * @param payMoney
 */
function refreshFee(foreGiftCode,payMoney){
    $.feeMgr.removeFee('290', '1', foreGiftCode);

    var fee = Math.round(parseFloat(payMoney) * 100);
    var feeData = new $.DataMap();
    feeData.put("TRADE_TYPE_CODE", "290");
    feeData.put("MODE", "1");// 大类 押金
    feeData.put("CODE", foreGiftCode);// 小类
    feeData.put("FEE", fee);
    $.feeMgr.insertFee(feeData);
}
/**
 * 清空编辑区
 */
function cleanForegift() {
    $("#FOREGIFT_CODE").val("");
    $("#PAY_MONEY").val("");
}
/**
 * 提交结果
 * @param obj
 * @returns {Boolean}
 */
function submitDepts(obj) {

    var add = false;// 收取押金标志
    var upd = false;// 清退押金标志
    var addPay = 0;
    var updPay = 0;
    var info = "";

    var deptTable = DeptTable.getData();
    for ( var i = 0; i < deptTable.length; i++) {
        pay = parseFloat(deptTable.get([ i ], "PAY_MONEY"));
        if (pay > 0) {
            add = true;
            addPay += pay;
        } else if (pay < 0) {
            upd = true;
            updPay += pay;
        }
    }

    if (addPay > 1000000) {
       MessageBox.alert("收取的费用不能大于一百万！");
        return false;
    }
    if (updPay == 0 && addPay == 0) {
       MessageBox.alert("没有数据可以提交！");
        return false;
    }
    if (add && upd) {
       MessageBox.alert("同一笔业务不可以收取、清退同时操作！");
        return false;
    }

    if($("#INVOICE_TAG").val()=="1")//判断是否需要输入发票号码或无主发票
    {
        if($("#INVOICE_NO").val()=="")
        {
           MessageBox.alert("请输入发票号码！");
            return false;
        }
        if(!invoiceEffective)//发票号码无效
        {
           MessageBox.alert("发票校验没有通过，操作无法提交！");
            return false;
        }
        if(oldInvoiceNo !=$("#INVOICE_NO").val())//发票号码无效
        {
           MessageBox.alert("校验的发票号码与输入的发票号码不一致，请确认！");
            return false;
        }
        if($("#IS_USED").val()=="YES"&&add)
        {
           MessageBox.alert("该发票号码已被使用！");
            return false;
        }
        if($("#PROCESS_TAG").val()=="1" && upd)
        {
           MessageBox.alert("该发票已经清退了押金！");
            return false;
        }
        if(upd){
            var invoiceFeeSum=-parseFloat($("#INVOICE_FEE_SUM").val())/100;

            if(invoiceFeeSum!=updPay){
               MessageBox.alert("清退金额和发票总金额不一致！");
                return false;
            }
        }
    }

    if(manPicID == ''){
        MessageBox.alert("提交前请进行人像采集!");
        return false;
    }

    if($("#AUTH_SERIAL_NUMBER").val()==""){
        $("#CSSUBMIT_BUTTON").attr("cancelRule",true);
    }

    var param = "&USER_FOREGIFTS="+deptTable;
    $.cssubmit.addParam(param);

    $.printMgr.setPrintParam("REMARK",$("#FOREGIFT_REMARK").val());
    return true;
}
function addSelectValue(id,value) {
    $(id+"_float").find("li").removeClass("on");
    $(id+"_float").find("li[val="+value+"]").addClass("on");
    var text = $(id+"_float").find("li[val="+value+"]").find("div").text();
    // var val = $(id+"_float").find("li[val="+value+"]").attr("val");
    $(id+ "_span span").text(text);
    // $(id+ "_span input").val(val);
}
/**
 * 人像采集
 * @param picid
 * @param picstream
 * @returns {boolean}
 */
function identification(picid,picstream){
    var SERIAL_NUMBER = $("#AUTH_SERIAL_NUMBER").val();
    var INVOICE_NO = $("#INVOICE_NO").val();
    if(SERIAL_NUMBER=="" || SERIAL_NUMBER == null){
        if(INVOICE_NO=="" || INVOICE_NO == null){
            MessageBox.alert("请输入手机号码或校验发票！");
            return false;
        }
    }
    var toDay=new Date();
    var toMonth=(toDay.getMonth()+1).toString();
    if(toMonth.length==1){
        toMonth="0"+toMonth;
    }
    var thisDay=toDay.getDate().toString();
    if(thisDay.length==1){
        thisDay="0"+thisDay;
    }
    var op_time=toDay.getFullYear().toString()+"-"+toMonth+"-"+thisDay
        +" "+toDay.getHours().toString()+":"+toDay.getMinutes().toString()+":"+toDay.getSeconds().toString();
    var work_no = $("#STAFF_ID").val();
    var op_code = $("#TRADE_TYPE_CODE").val();
    var op_code_name = "";
    if(op_code == "290"){
        op_code_name = "押金业务";
    }

    var bossOriginalXml = [];
    bossOriginalXml.push('<?xml version="1.0" encoding="utf-8"?>');
    bossOriginalXml.push('<req>');
    bossOriginalXml.push('	<billid>'+'</billid>');
    bossOriginalXml.push('	<brand_name>'+'</brand_name>');
    bossOriginalXml.push('	<brand_code>'+'</brand_code>');
    bossOriginalXml.push('	<work_name>'+'</work_name>');
    bossOriginalXml.push('	<work_no>' + work_no + '</work_no>');
    bossOriginalXml.push('	<org_info>'+'</org_info>');
    bossOriginalXml.push('	<org_name>'+'</org_name>');
    if(SERIAL_NUMBER!=null&&SERIAL_NUMBER != ""){
        bossOriginalXml.push('	<phone>'+$("#AUTH_SERIAL_NUMBER").val()+'</phone>');
    }else if(INVOICE_NO != null&&INVOICE_NO != ""){
        bossOriginalXml.push('	<phone>'+$("#INVOICE_NO").val()+'</phone>');
    }

    bossOriginalXml.push('	<serv_id>'+'</serv_id>');
    bossOriginalXml.push('	<op_time>' + op_time + '</op_time>');
    bossOriginalXml.push('	<op_code>' + op_code + '</op_code>');
    bossOriginalXml.push('	<op_code_name>' + op_code_name + '</op_code_name>');

    bossOriginalXml.push('	<busi_list>');
    bossOriginalXml.push('		<busi_info>');
    bossOriginalXml.push('			<op_code>'+op_code+'</op_code>');
    bossOriginalXml.push('			<sys_accept>'+'</sys_accept>');
    bossOriginalXml.push('			<busi_detail>'+'</busi_detail>');
    bossOriginalXml.push('		</busi_info>');
    bossOriginalXml.push('	</busi_list>');

    bossOriginalXml.push('	<verify_mode>'+'</verify_mode>');
    bossOriginalXml.push('	<id_card>'+'</id_card>');
    bossOriginalXml.push('	<cust_name>'+'</cust_name>');
    bossOriginalXml.push('	<copy_flag></copy_flag>');
    bossOriginalXml.push('	<agm_flag></agm_flag>');
    bossOriginalXml.push('</req>');
    var bossOriginaStr = bossOriginalXml.join("");
    //调用拍照方法
    var resultInfo = makeManActiveX.Identification(bossOriginaStr);

    
    //获取保存结果
    var result = makeManActiveX.IdentificationInfo.result;
    //获取保存照片ID
    manPicID = makeManActiveX.IdentificationInfo.pic_id;

    if (manPicID != '') {
        MessageBox.success("成功提示", "人像摄像成功", function (btn) {
            if ("ok" === btn) {
                //获取照片流
                var picStream = makeManActiveX.IdentificationInfo.pic_stream;
                picStream = escape(encodeURIComponent(picStream));
                if ("0" == result) {
                    $("#" + picid).val(manPicID);
                    $("#" + picstream).val(picStream);
                }else{
                    MessageBox.error("告警提示","拍摄失败！请重新拍摄",null, null, null, null);
                }
            }
        });
    } else {
        MessageBox.error("错误提示", "人像摄像失败");
        return false;
    }
}