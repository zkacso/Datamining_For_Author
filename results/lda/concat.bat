FOR %%H IN (0,1,5,10) DO (
    FOR %%M IN (2000,5000) DO (
            copy lda_authorallocation_norm_*_0_%%H_%%M_*.csv asd\lda_authorallocation_norm_0_%%H_%%M.csv
            copy lda_authorallocation_sem_*_0_%%H_%%M_*.csv asd\lda_authorallocation_sem_0_%%H_%%M.csv
    )
)
