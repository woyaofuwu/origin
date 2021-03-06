INSERT INTO TF_B_TRADE_SVCSTATE
(TRADE_ID, ACCEPT_MONTH, USER_ID,SERVICE_ID,STATE_CODE,MODIFY_TAG,MAIN_TAG,START_DATE,END_DATE)
SELECT
TO_NUMBER(:TRADE_ID),:ACCEPT_MONTH, TO_NUMBER(:USER_ID),:SERVICE_ID, :STATE_CODE, :MODIFY_TAG,
DECODE(B.PACKAGE_TYPE_CODE, '0', '1', '0'), NVL(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'), SYSDATE),
NVL(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') - 0.00001,DECODE(:MODIFY_TAG, '1', SYSDATE, SYSDATE + 10000))
FROM TF_F_USER_ELEMENT A, TD_B_PACKAGE B
WHERE USER_ID = TO_NUMBER(:USER_ID)
AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
AND A.ELEMENT_TYPE_CODE = 'S'
AND A.ELEMENT_ID = :SERVICE_ID
AND A.PACKAGE_ID = B.PACKAGE_ID