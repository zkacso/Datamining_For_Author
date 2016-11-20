import requests
from pprint import pprint
from time import sleep
from datetime import date,timedelta
from lxml import html

debug = False
base_url = 'http://api.nytimes.com/svc/search/v2/articlesearch.json';
headers = {'Connection' : 'keep-alive',
           'User-Agent' : 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36',
           'Accept' : '*/*',
           'Accept-Encoding' : 'gzip, deflate, sdch',
           'Accopt-Language' : 'hu-HU,hu;q=0.8,en-US;q=0.6,en;q=0.4,de;q=0.2,fr;q=0.2',
           'Cookie' : 'RMID=007f0101167457de8d6b0006; __gads=ID=b2c476d074c39cd4:T=1474202994:S=ALNI_MaXDgbao4MahfVL9p1OmOcgpq5Mmw; optimizelyEndUserId=oeu1474202995948r0.6114797367614613; _dyid=-2643612782223782540; _dy_df_geo=Hungary..Budapest; _ga=GA1.2.90581121.1476533204; __cfduid=d2d71741047b9b006950490ee33188a2f1476533447; _cb_ls=1; _ga=GA1.4.90581121.1476533204; nyt-m=C28B46E7C0A0E235C79DC1EE4F74F6D3&e=i.1477958400&t=i.10&v=i.3&l=l.15.1429115085.1861570099.576483982.-1.-1.-1.-1.-1.-1.-1.-1.-1.-1.-1.-1&n=i.2&g=i.0&rc=i.0&er=i.1474202994&vr=l.4.4.0.0.0&pr=l.4.10.0.0.0&vp=i.0&gf=l.10.1429115085.1861570099.576483982.-1.-1.-1.-1.-1.-1.-1&ft=i.0&fv=i.0&gl=l.2.-1.-1&rl=l.1.-1&cav=i.3&imu=i.1&igu=i.1&prt=i.5&kid=i.1&ica=i.1&iue=i.0&ier=i.0&iub=i.0&ifv=i.0&igd=i.0&iga=i.1&imv=i.0&igf=i.1&iru=i.0&ird=i.0&ira=i.1&iir=i.1&gb=l.3.0.3.1474243200&abn=s.close_door_90_10_jun2016&abv=i.1; adxcl=l*45513=6013964f:1|t*45513=6013964f:1476121710|lcig=582bd9bf:1|l*45823=582bd9bf:1; adxcs=s*4534c=0:2; NYT-S=0Mej97MpVIWCHDXrmvxADeHEBTX8OSQIn7deFz9JchiALVSSvxGfo9IYV.Ynx4rkFI; walley=GA1.2.572717439.1474202987; _cb=C8SpuWDsON3VBpIsve; _chartbeat2=.1476533458083.1476540043314.1; optimizelySegments=%7B%223007620980%22%3A%22search%22%2C%223013750536%22%3A%22false%22%2C%223028090192%22%3A%22gc%22%2C%223032570147%22%3A%22none%22%2C%223315571554%22%3A%22search%22%2C%223321851195%22%3A%22false%22%2C%223334171090%22%3A%22none%22%2C%223336921036%22%3A%22gc%22%7D; optimizelyBuckets=%7B%225341442317%22%3A%225353881961%22%7D; _dycst=dk.w.c.ms.frv3.ah.; _dy_geo=HU.EU.HU_10.HU_10_Hajd%C3%BAb%C3%B6sz%C3%B6rm%C3%A9ny; _dy_df_geo=Hungary..Hajd%C3%BAb%C3%B6sz%C3%B6rm%C3%A9ny; _dy_toffset=2; _dyus_8765260=31%7C205%7C0%7C0%7C0%7C0.0.1474202996655.1476540044164.2337047.0%7C288%7C42%7C9%7C116%7C6%7C0%7C0%7C0%7C0%7C1%7C0%7C7%7C0%7C0%7C3%7C0%7C0%7C7%7C3%7C0%7C0%7C0%7C0; krux_segs=qtqvlyuhn%7Cqgmiaag4d%7Cpgxu9tt6o%7Cqticqocj0%7Cqtuorrot7%7Cqsyy8rb9l%7Co7t4tg957%7Cqx48577uu%7Cnuzmoezkg%7Cqtl1megjb%7Conrl71gcz%7Cq4a7tppje%7Cn39f0be31%7Cqsx2xjipq%7Cqsy1x9he1%7Cqua2t0dn6%7Cq4a7x5esd%7Cqsy4d7wb4; _sp_id.75b0=72cae70800a288e2.1474202996.6.1476547394.1476542684; _sp_ses.75b0=*; nyt-a=21df78071e22c27881a438ec854c6f2b' ,
           }

for i in range(1,700):
    if debug:
        print(str(i) + '. page:')
    for page in range(0,20):
        begindate = date(2015,9,1) + timedelta(days=i)
        print(begindate.strftime('%Y%m%d'))
        enddate = begindate

        api_request = requests.get(base_url + '?begin_date=' + begindate.strftime('%Y%m%d') + '&end_date=' + enddate.strftime('%Y%m%d') + '&fl=web_url%2Cbyline&page=' + str(page))
        docs = api_request.json()['response']['docs']
        for j in range(0,len(docs)):
            if debug:
                print('\t' + str(j+1) + '. article')
            url = docs[j]['web_url']
            byline = docs[j]['byline']
            if (byline is not None and byline) and (not url.startswith('http://www.nytimes.com/video')):
                people = byline['person']
                if people:
                    person = people[0]
                    if 'firstname' in person and 'lastname' in person:
                        try:
                            r = requests.get(url, headers=headers)
                            htmlpage = html.fromstring(r.text.strip().replace('\n','').replace('\r','').replace('"',''))
                            articleContent = htmlpage.xpath('//p[@class="story-body-text"]/text()')
                            articletext = ''.join(articleContent)
                            if(articletext != ''):
                                with open('nytimesarticles.csv','a',encoding='utf8') as f:
                                    f.write('"' + person['firstname'] + person['lastname'] + '";"' + articletext + '"\n')
                                with open('nytimesarticles.json','a',encoding='utf8') as j:
                                    j.write('{ "name" : "' + person['firstname'] + person['lastname'] + '", "site" : "' + articletext + '" }\n')
                        except Exception as ex:
                            print('error occured in a request')
                    if debug:
                        print(person)
        sleep(5)
