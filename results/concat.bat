FOR %%H IN (0,1,5,10) DO (
    FOR %%M IN (2000,5000) DO (
        FOR %%T IN (1,5,10) DO (
            copy nb_authorallocation_norm_%%T_0_%%H_%%M_*.csv nb_authorallocation_norm_%%T_0_%%H_%%M.csv
            copy nb_authorallocation_sem_%%T_0_%%H_%%M_*.csv nb_authorallocation_sem_%%T_0_%%H_%%M.csv
        )
    )
)
