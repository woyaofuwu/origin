SELECT A.PRODUCT_ID FROM (
SELECT T.PRODUCT_ID FROM TF_F_USER_PRODUCT T WHERE USER_ID=:USER_ID AND T.UPDATE_TIME >= TRUNC(SYSDATE,'dd') ORDER BY T.END_DATE DESC) A WHERE ROWNUM <= 2