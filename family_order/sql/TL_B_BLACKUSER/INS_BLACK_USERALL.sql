INSERT INTO TL_B_BLACKUSER
   (ACCEPT_MONTH, IN_TIME, SERIAL_NUMBER,USER_ID, PROCESS_TAG, DATA_TYPE, EXEC_TIME,BEGIN_DATE,END_DATE,EFFECT_TAG,REMARK)
 VALUES
   (:ACCEPT_MONTH,
   	to_date(:IN_TIME,'yyyy-mm-dd hh24:mi:ss'),
    :SERIAL_NUMBER,
    TO_NUMBER(:USER_ID),
    :PROCESS_TAG,
    :DATA_TYPE,
    SYSDATE,
    to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss'),
    to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),
    '1',
    '黑名单用户')