import os
import re

articlesPath = 'C:\\GitRepository\\Datamining_For_Author\\nytimes\\nytimesarticles.csv'
outputFilePath = 'semanticArticles.csv'
semanticFieldDirectory = 'semanticfieldfiles'
notCharactersRegex = re.compile('[^A-Za-z ]')

semanticFieldsMap = {}

for semanticField in os.listdir(semanticFieldDirectory):
    with open(os.path.join(semanticFieldDirectory,semanticField),'r',encoding='utf8') as file:
        for word in file:
            semanticFieldsMap[word[:-1]] = semanticField

with open(articlesPath,'r',encoding='utf8') as articlesFile:
    with open(outputFilePath,'w',encoding='utf8') as outputFile:
        for article in articlesFile:
            author = article.split('";"')[0][1:]
            newArticleText = []
            articleText = article.split('";"')[1][:-1].replace('\t',' ')
            for i in range(1,100):
                articleText = articleText.replace('  ',' ')
            articleText = notCharactersRegex.sub('',articleText)
            for word in articleText.split(' '):
                if word in semanticFieldsMap:
                    newArticleText.append(semanticFieldsMap[word])

            outputFile.write('"' + author + '";"' + ' '.join(newArticleText) + '"\n')
