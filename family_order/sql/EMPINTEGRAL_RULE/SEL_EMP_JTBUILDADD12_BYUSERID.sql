--IS_CACHE=N
SELECT DECODE(count(1),0,0,1) 加入过
FROM TF_F_RELATION_UU U
WHERE U.USER_ID_B = TO_NUMBER(:USER_ID)
 AND U.RELATION_TYPE_CODE = '45'
 AND U.END_DATE <= TRUNC(LAST_DAY(SYSDATE) + 1) - 1 / 86400
 AND U.END_DATE >ADD_MONTHS(TRUNC(LAST_DAY(SYSDATE) + 1) - 1 / 86400, -13)