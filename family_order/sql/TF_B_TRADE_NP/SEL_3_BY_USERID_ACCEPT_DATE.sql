SELECT  Np.user_id,
		Np.Result_Message,
       Np.Accept_Date,
       Np.Port_In_Netid
	FROM Tf_b_Trade_Np     Np
	WHERE Np.Trade_Type_Code = '41'
   And Np.Rsrv_Str5 Like '%FAIL|%'
   AND Np.ACCEPT_DATE >=
       TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
   AND Np.ACCEPT_DATE <
       TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')) + 1