import os
import re

unneededLinesRegex = re.compile('^{ [[].*$',re.MULTILINE) #replace with ''
keepWordsRegex = re.compile('^{ ([^,]+),.*$',re.MULTILINE) #replace with '\1'
simplifyWords = re.compile('^(.*)_.*$',re.MULTILINE) #replace with '\1' multirun
notWordLines = re.compile('^.*[^A-Za-z\n-].*$',re.MULTILINE) #replace with ''
emptyLines = re.compile('\n\n',re.MULTILINE) #replace with '\n' multirun

directory = 'D:\\egyetem\\szakdoga\\dict\\dbfiles'

for file in os.listdir(directory):
    data = open( os.path.join(directory, file),'r', encoding='utf8').read()
    data = unneededLinesRegex.sub('',data)
    data = keepWordsRegex.sub('\\1',data)
    for i in range(1,10):
        data = simplifyWords.sub('\\1',data)
    data = notWordLines.sub('',data)
    data = re.sub('--+','',data)
    for i in range(1,100):
        data = emptyLines.sub('\n',data)
    data = '\n'.join(set(data.split('\n')))
    with open(file,'w',encoding='utf8') as f:
        f.write(data)
