function checkAndSyncCustInfoToIms(obj) {
    //先校验
    var cardInfoOver = false;
    if ($.validate.verifyAll('EditInfoArea')) {
        cardInfoOver = true;
    }
    parent.window.custInfoAdepter.setPopupReturnValueToIms({
        'CARD_INFO_OVER': cardInfoOver,
        'IMS_UNIQUE_ID': $("#IMS_UNIQUE_ID").val(),
        'PSPT_TYPE_CODE': $("#PSPT_TYPE_CODE").val(),
        'PSPT_TYPE_TEXT': PSPT_TYPE_CODE.selectedText,
        'ID_CARD_NO': $("#PSPT_ID").val(),
        'BIRTH_DAY': $("#BIRTHDAY").val(),
        'CUST_NAME': $("#CUST_NAME").val(),
        'PSPT_ADDR': $("#PSPT_ADDR").val(),
        'SEX': $("#SEX").val(),
        'PSPT_END_DATE': $("#PSPT_END_DATE").val(),
        'FOLK_CODE': $("#FOLK_CODE").val(),

        'AGENT_PSPT_TYPE_CODE': $("#AGENT_PSPT_TYPE_CODE").val(),
        'AGENT_PSPT_ID': $("#AGENT_PSPT_ID").val(),
        'AGENT_CUST_NAME': $("#AGENT_CUST_NAME").val(),
        'AGENT_PSPT_ADDR': $("#AGENT_PSPT_ADDR").val(),

        'USE': $("#USE").val(),
        'USE_PSPT_TYPE_CODE': $("#USE_PSPT_TYPE_CODE").val(),
        'USE_PSPT_ID': $("#USE_PSPT_ID").val(),
        'USE_PSPT_ADDR': $("#USE_PSPT_ADDR").val(),

        'READ_CARD_PZ_VALUE': $("#READ_CARD_PZ_VALUE").val(),
        'custInfo_CONTACT': $("#custInfo_CONTACT").val(),
        'custInfo_CONTACT_PHONE': $("#custInfo_CONTACT_PHONE").val(),
        'custinfo_ReadCardFlag1': $("#custinfo_ReadCardFlag1").val(),


        'READ_CARD_PZ1': $("#READ_CARD_PZ1").val(),
        'READ_CARD_PZ_VALUE1': $("#READ_CARD_PZ_VALUE1").val(),
        'AGENT_PIC_ID': $("#AGENT_PIC_ID").val(),
        'PIC_ID': $("#PIC_ID").val(),
        'PIC_STREAM': $("#PIC_STREAM").val(),
        'AGENT_PIC_STREAM': $("#AGENT_PIC_STREAM").val(),

    });
    hidePopup(this);
}
