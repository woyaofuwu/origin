/* 刷新页面区域 */
function refreshPartAtferAuth(data)
{

    var familyTradeType=$("#FAMILY_MEMBER_TRADE_TYPE").val();

    var param = "&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER") +"&TRADE_TYPE_CODE="+familyTradeType;
    param=param+"&MEMBER_USER_ID="+data.get("USER_INFO").get("USER_ID");

    $.beginPageLoading("正在查询数据...");

    $.ajax.submit('', 'loadChildInfo',param, 'FamilyInfoPart,FamilyMemberRelationPart,HiddenPart', function(reData){

            if("458"==familyTradeType) {
/*
                $("#phoneMmemberTitle").removeAttr("style");
*/
                $("#phoneMmemberTitle").css("display","block")
                $("#memberPayRelationTitle").css("display","none")
            }else if("454"==familyTradeType){
                $("#memberPayRelationTitle").css("display","block")
                $("#phoneMmemberTitle").css("display","none")

            }

            $.endPageLoading();
        },
        function(error_code,error_info){
            $.endPageLoading();
            $.cssubmit.disabledSubmitBtn(true);
            $("#phoneMmemberTitle").css("display","none");
            $("#memberPayRelationTitle").css("display","none");
            $("#FamilyMemberRelationPart").css("display","none");

            $.MessageBox.error(error_code,error_info);
        });
}

function changeCheckBox(obj) {

    //如果选中 修改当前隐藏域 当前选中的共享标识为1 反之 为0
    if(obj.checked){
        $(obj).parent("div").children("input[name='NOW_TAG']").val("0");// ADD
    }else{
        $(obj).parent("div").children("input[name='NOW_TAG']").val("1");// DEL

    }
}

function submitBeforeCheck(){

    var _this = $("#FamilyMemberRelationPart").find("li");
    var tag = true;
    var memberInfoList = $.DatasetList();
    //判断是否添加或删除的共享关系，无操作不让提交
    _this.each(function(){
        var nowShareTag = $(this).find("input[name='NOW_TAG']").val();
        var originalShareTag = $(this).find("input[name='ORIGINAL_TAG']").val();
        var memberSerialNumber = $(this).find("input[name='MEMBER_SERIAL_NUMBER']").val();

        //如果新老没有变化，说明无新操作，不想后台传输这个号码资料
        if(nowShareTag == originalShareTag){
            return true;//continue;
        }
        var memberInfo = $.DataMap();
        //新老有变化，说明有新操作，取当前的标记
        memberInfo.put("MEMBER_SERIAL_NUMBER",memberSerialNumber);
        memberInfo.put("TAG",nowShareTag);
        if(tag){
            tag=false;
        }
        memberInfoList.add(memberInfo);
    });
    if(tag){
        MessageBox.alert("提示", "没有添加或删除手机成员共享关系，请先进行操作","", null);
        return false;
    }
    var param = "&MEMBER_LIST="+memberInfoList;
    param=param+"&TRADE_TYPE_CODE="+$("#FAMILY_MEMBER_TRADE_TYPE").val();
    $.cssubmit.addParam(param);

    return true;

}