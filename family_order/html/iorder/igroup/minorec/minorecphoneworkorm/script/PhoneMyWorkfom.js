function queryInfos(el) {

    if(isNull($("#cond_GROUP_ID").val()) && isNull($("#cond_IBSYSID").val())){
        MessageBox.error("请至少输入一个查询条件！");
        return false;
    }

    $.beginPageLoading('正在查询数据...');
    $.ajax.submit("QueryInfo", "queryInfos", "", "queryPart", function(data){
            $.endPageLoading();
            hidePopup(el);
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        }
    );
}

function cancelWorkform(el) {
    var busiformId = $(el).attr("busiformId");
    MessageBox.confirm("提示信息", "是否发起撤单？", function(btn){
        if("ok" == btn)
        {
            $.beginPageLoading('正在撤单...');
            $.ajax.submit('','cancelWorkform',"&BUSIFORM_ID="+busiformId,'',function(data){
                $.endPageLoading();
                MessageBox.success("提交", "提交成功！",function(){
                    //closeNav();
                });
            },function(error_code,error_info,derror){
                $.endPageLoading();
                showDetailErrorInfo(error_code,error_info,derror);
            });
        }
    });
}

function goToWorkTask(el) {
    var todoUrl = $(el).attr("todoUrl");
    var urlArr = todoUrl.split("?");
    openNav('工单办理', encodeURI(urlArr[1].substring(13)), '', '', urlArr[0]);
}


function isNull(obj) {
    if(obj == null || obj == "" || obj == undefined){
        return true;
    }
    return false;
}