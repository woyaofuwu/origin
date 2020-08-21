function queryESimQrCode()
{
    if(!$.validate.verifyField($("#SERIAL_NUMBER")[0]))
    {
        return false;
    }
    $.beginPageLoading("努力加载中...");
    $.ajax.submit('QueryPart', 'queryESimQrCode', '', 'QueryListPart', function(data){
            $.endPageLoading();
            if(data == null || data == "" || data.length == 0) {
                MessageBox.alert("提示", "没有查询到相关数据！");
            }
        },
        function(error_code,error_info){
            $.endPageLoading();
            alert(error_info);
        });
}

/**
 * 查看二维码
 */
function showQRCode(rowIndex){
    var rowValue = QrCodeTable.getRowData(rowIndex-1);
    var activationCode = rowValue.get("ACTIVATION_CODE");
    var serialNumber = rowValue.get("SERIAL_NUMBER");
    var bizTypeCode = rowValue.get("BIZ_TYPE_CODE");
    popupDialog('二维码', 'qrcode.ESimQRCodeImg', 'imgInit', '&ACTIVATION_CODE='+activationCode+'&SERIAL_NUMBER='+serialNumber+'&BIZ_TYPE_CODE='+bizTypeCode, null, '450px', '500px');
}

