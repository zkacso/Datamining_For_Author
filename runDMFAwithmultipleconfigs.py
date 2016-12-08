import subprocess

filepath = 'C:\\GitRepository\\Datamining_For_Author\\DMFA\\out\\artifacts\\DMFA_jar\\DMFA.jar'

numIterations = '10000'
highCuts = ['0.0','0.01','0.05','0.1']
maxWords = ['2000','5000']
lowCut = '0.0'
trainingSetSizes = ['1','5','10']
for ix in range(1,20):
    for highCut in highCuts:
        for maxWord in maxWords:
            for trainingSetSize in trainingSetSizes:
                subprocess.call(['java','-jar',filepath,'-lowCut',lowCut,'-highCut',highCut,'-maxWords',maxWord,'-trainingSetSize',trainingSetSize,'-ix',str(ix)])
                subprocess.call(['java','-jar',filepath,'-lowCut',lowCut,'-highCut',highCut,'-maxWords',maxWord,'-trainingSetSize',trainingSetSize,'-ix',str(ix),'-semanticFields'])
