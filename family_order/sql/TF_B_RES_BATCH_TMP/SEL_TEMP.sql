SELECT RES_NO TERMINAL_ID,B.RESULT,B.UP_FLAG ,B.LOG_ID FROM TF_B_RES_BATCH_TMP A,TF_R_TERMINAL_ARRIVAL B WHERE A.RES_NO=B.TERMINAL_ID