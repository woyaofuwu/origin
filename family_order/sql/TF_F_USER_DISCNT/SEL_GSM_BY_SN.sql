SELECT A.SERIAL_NUMBER,
	   A.USER_CLASS,
	   A.USER_ID,
	   A.START_DATE,
	   A.IN_DATE
  FROM TF_F_USER_INFO_CLASS A
  WHERE 1=1
  AND SERIAL_NUMBER = :SERIAL_NUMBER
  AND A.END_DATE > SYSDATE
  AND A.START_DATE < SYSDATE