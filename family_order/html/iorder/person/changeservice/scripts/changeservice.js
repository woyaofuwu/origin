function refreshPartAtferAuth(data)
{
    $("#mytab").afterSwitchAction(function(e, idx){
        // return window.confirm("确定要切换标签页吗？");
    });
    var serial_number = data.get("USER_INFO").get("SERIAL_NUMBER");
    var userId = data.get("USER_INFO").get("USER_ID");
    var productId = data.get("USER_INFO").get("PRODUCT_ID");

    var param = "&SERIAL_NUMBER="+serial_number+"&USER_ID="+userId+"&PRODUCT_ID="+productId;
    $.ajax.submit('', 'loadChildInfo', param, 'userSvc,editPart', function(data){
            resetSvc();
        },
        function(error_code,error_info){
            $.endPageLoading();
           MessageBox.alert(error_info);
        });
}

function changestarttime()
{
    var startDate = $('#NEW_START_DATE').val();
    var thisDate = new Date();
    var m = thisDate.getMonth() + 1;
    var d = thisDate.getDate();
    var y = thisDate.getFullYear();
    var y2 = startDate.substr(0,4);
    var m2 = startDate.substr(5,2);
    m2 =Number(m2);
    var d2 = startDate.substr(8,2);
    d2 = Number(d2);
    if(y2<y)
    {
       MessageBox.alert('执行时间不能少于或等于当前时间');
        $('#NEW_START_DATE').val('');
        return false;
    }

    if(y==y2&&m2<m)
    {
       MessageBox.alert('执行时间不能少于或等于当前时间');
        $('#NEW_START_DATE').val('');
        return false;
    }

    if(y==y2&&m==m2&&d2<d)
    {
       MessageBox.alert('执行时间不能少于或等于当前时间');
        $('#NEW_START_DATE').val('');
        return false;
    }
}

function changeendtime() {
    var start =  $('#NEW_START_DATE').val();
    var end = $('#NEW_END_DATE').val();


    var m=end.substr(5,2);
    m1 =Number(m);
    var d=end.substr(8,2);
    d1 =Number(d);
    var y=end.substr(0,4);
    var y2=start.substr(0,4);
    var m2=start.substr(5,2);
    m2 =Number(m2);
    var d2=start.substr(8,2);
    d2 =Number(d2);
    if(y<y2)
    {
       MessageBox.alert('结束时间不能少于或等于开始时间');
        $('#NEW_END_DATE').val('');
        return false;
    }

    if(y==y2&&m1<m2)
    {
       MessageBox.alert('结束时间不能少于或等于开始时间');
        $('#NEW_END_DATE').val('');
        return false;
    }

    if(y==y2&&m1==m2&&d1<d2)
    {
       MessageBox.alert('结束时间不能少于或等于开始时间');
        $('#NEW_END_DATE').val('');
        return false;
    }
}

function changeState(value)
{
    if('ADD_SVC' == value)
    {
        $("#add").attr("disabled", false).removeClass("e_dis");
        $("#del").attr("disabled",true).addClass("e_dis");
        $("#reset").attr("disabled", false).removeClass("e_dis");
        //国际漫游特殊处理
        if ($('#ADD_SVC').val() == "19")
        {
            $("#NEW_END_DATE_SPAN").removeClass("e_hide");
            $("#NEW_END_DATE_SPAN1").addClass("e_hide");
            NEW_END_DATE_SEL.empty();
            NEW_END_DATE_SEL.append("30天","0");
            NEW_END_DATE_SEL.append("180天","1");
            NEW_END_DATE_SEL.append("长期","2");
            var serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
            var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");

            var param = "&SERIAL_NUMBER="+ serialNumber + "&USER_ID="+userId;
            $.ajax.submit('', 'loadFeeInfo', param, '', function(dataset)
                {
                    dataset.each
                    (
                        function(info)
                        {
                            var feeMode = info.get("FEE_MODE");
                            var feeTypeCode = info.get("FEE_TYPE_CODE");
                            var fee = info.get("FEE");

                            $.feeMgr.removeFee(info.get("TRADE_TYPE_CODE"), feeMode, feeTypeCode);//清空费用列表

                            var feeData = new $.DataMap();
                            feeData.put("TRADE_TYPE_CODE", info.get("TRADE_TYPE_CODE"));
                            feeData.put("MODE", feeMode);
                            feeData.put("CODE", feeTypeCode);
                            feeData.put("FEE",  parseInt(fee));



                           MessageBox.alert("用户话费余额不足，请先交纳"+parseInt(fee)/100+"元差额话费后再重新办理国漫开通功能");
                            //$.feeMgr.insertFee(feeData);

                        }
                    );
                },
                function(error_code,error_info){
                    $.endPageLoading();
                   MessageBox.alert(error_info);
                });


        }
        else if ($('#ADD_SVC').val() == "15")
        {
            $("#NEW_END_DATE_SPAN").removeClass("e_hide");
            $("#NEW_END_DATE_SPAN1").addClass("e_hide");
            NEW_END_DATE_SEL.empty();
            NEW_END_DATE_SEL.append("长期","2");
        }
        else
        {
            $("#NEW_END_DATE_SPAN").removeClass("e_hide");
            $("#NEW_END_DATE_SPAN1").addClass("e_hide");
        }

    }else if('DEL_SVC' == value)
    {
        $("#add").attr("disabled", true).addClass("e_dis");
        $("#del").attr("disabled",false).removeClass("e_dis");
        $("#reset").attr("disabled", false).removeClass("e_dis");
        $("#NEW_END_DATE").val("");
    }
}

function changeEndDateState()
{

    if ($('#NEW_START_DATE').val() == "")
    {
       MessageBox.alert('起始时间不能为空');
        $('#NEW_END_DATE_SEL').val("");
        return false;
    }

    if ($('#NEW_END_DATE_SEL').val() == "")
    {
        $('#NEW_END_DATE').val("");
        return false;
    }
    var param = "&NEW_END_DATE_SEL="+$('#NEW_END_DATE_SEL').val() + "&NEW_START_DATE="+$('#NEW_START_DATE').val();
    $.ajax.submit('', 'getRoamEndDate', param, '', function(data)
        {
            var endDate = data.get("END_DATE");
            $('#NEW_END_DATE').val(endDate);
        },
        function(error_code,error_info){
            $.endPageLoading();
           MessageBox.alert(error_info);
        });
}

function addSvc()
{
    var result = true;
    var startDate = $('#NEW_START_DATE').val();
    var endDate = $('#NEW_END_DATE').val();
    var svcCode = $('#ADD_SVC').val();
    var svcName = $("#ADD_SVC_float").find("li[val="+svcCode+"]").find("div").text();

    if(startDate==null || startDate.length==0)
    {
       MessageBox.alert('开始时间不能为空!');
        return false;
    }

    if(endDate==null || endDate.length==0)
    {
       MessageBox.alert('终止时间不能为空!');
        return false;
    }

    if(svcCode==null || svcCode.length==0)
    {
       MessageBox.alert('新增服务不能为空!');
        return false;
    }

    //判断是否重复新增服务
    var userSvcs = newUserSvc.getData();
    //alert(userSvcs);
    if(userSvcs.length>0) {
        for(var i=0;i<userSvcs.length;i++) {
            var data = userSvcs.get(i);
            var json = $.parseJSON(data.toString());
            $.each(json, function(name, value) {
                var scene = $("#ADD_SVC").val();
                if(name == "SERVICE_ID" && value == scene) {
                   MessageBox.alert('服务已存在列表中,不要重复添加!');
                    result = false;
                }
            });
        }
    }

    if(result)
    {
        var info = new $.DataMap();
        info["SERVICE_ID"] = svcCode;
        info["SERVICE_NAME"] = svcName;
        info["START_DATE"] = startDate;
        info["END_DATE"] = endDate;
        info["MODIFY_TAG"] = '0';
        info["MODIFY_NAME"] = '新增';
        newUserSvc.addRow(info);
        newUserSvc.adjust();

        $("#NEW_START_DATE").attr("disabled",true).addClass("e_dis");
        $("#NEW_END_DATE").attr("disabled",true).addClass("e_dis");

        $('#ADD_SVC').val('');
    }
    return result;
}

function delSvc()
{
    var result = true;
    var startDate = $('#NEW_START_DATE').val();
    var endDate = $('#NEW_END_DATE').val();
    var svcCode = $('#DEL_SVC').val();
    var svcName = $("#DEL_SVC_float").find("li[val="+svcCode+"]").find("div").text();

    if(startDate==null || startDate.length==0)
    {
       MessageBox.alert('开始时间不能为空!');
        return false;
    }
    if(svcCode==null || svcCode.length==0)
    {
       MessageBox.alert('删除服务不能为空!');
        return false;
    }

    //判断是否重复新增服务
    var userSvcs = newUserSvc.getData();
    //alert(userSvcs);

    if(userSvcs.length>0) {
        for(var i=0;i<userSvcs.length;i++) {
            var data = userSvcs.get(i);
            var json = $.parseJSON(data.toString());
            $.each(json, function(name, value) {
                var scene = $("#DEL_SVC").val();
                if(name == "SERVICE_ID" && value == scene) {
                   MessageBox.alert('服务已存在列表中,不要重复删除!');
                    result = false;
                }
            });
        }
    }

    if(result)
    {
        var info = new $.DataMap();
        info["SERVICE_ID"] = svcCode;
        info["SERVICE_NAME"] = svcName;
        //info["START_DATE"] = startDate;
        info["END_DATE"] = getSystime();
        info["MODIFY_TAG"] = '1';
        info["MODIFY_NAME"] = '删除';
        newUserSvc.addRow(info);
        newUserSvc.adjust();

        $("#NEW_START_DATE").attr("disabled",true).addClass("e_dis");
        $("#NEW_END_DATE").attr("disabled",true).addClass("e_dis");
        $("#NEW_END_DATE").val("");

        $('#DEL_SVC').val('');
    }
    return result;
}

function resetSvc()
{
    // document.getElementById("EditTableBody").innerHTML = "";
    $("#EditTableBody").html("");
    $("#NEW_START_DATE").attr("disabled", true).removeClass("e_dis");
    $("#NEW_END_DATE").attr("disabled", false).removeClass("e_dis");
}
function getSystime(){

    var now=new Date();
    var year=now.getFullYear();
    var month=now.getMonth() + 1;
    var day=now.getDate();
    var hours=now.getHours();
    var minutes=now.getMinutes();
    var seconds=now.getSeconds();
    if (month < 10)
    {
        month = "0" + month;
    }
    if (day < 10)
    {
        day = "0" + day;
    }
    if (hours < 10)
    {
        hours = "0" + hours;
    }
    if (minutes < 10)
    {
        minutes = "0" + minutes;
    }
    if (seconds < 10)
    {
        seconds = "0" + seconds;
    }
    var time = year+"-" +month +"-" + day+" "+hours+":"+minutes+":"+seconds+"";
    return time;

}

function submitBeforeAction()
{

    var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
    var userSvcs =newUserSvc.getData();
    var remark = $("#REMARK").val();

    /**
     var len = userSvcs.length;
     var datas = new $.DatasetList();
     for (var i = 0; i < len; i++)
     {
         var modifyTag = userSvcs.get(i).get("tag");
         userSvcs.get(i).put("MODIFY_TAG",modifyTag);
         datas.add(userSvcs.get(i));
     }

     var hasUserSvcs = $.table.get("hasUserSvc").getTableData(null,true);
     for (var i = 0,len = hasUserSvcs.length; i < len; i++) {
		    var modifyTag = hasUserSvcs.get(i).get("MODIFY_TAG");
		    if(modifyTag && modifyTag!=""){
		    	datas.add(hasUserSvcs.get(i));
		    }
	}
     */
    if (userSvcs.length < 1) {
       MessageBox.alert("没有可以提交的数据！");
        return false;
    }
    var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
    var param = "&SERIAL_NUMBER=" + serialNumber + "&X_CODING_STR=" + userSvcs.toString() + "&REMARK=" + remark + "&USER_ID=" + userId;
    //alert(param);
    $.cssubmit.addParam(param);
    return true;

}