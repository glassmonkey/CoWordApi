#!/usr/bin/env python
# -*- coding:utf-8 -*-

import sys
import time
import yaml
import re

# Tweepyライブラリをインポート
import tweepy
import json

from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream

#設定ファイルを読み込む
conf = yaml.load(open('config.yml').read().decode('utf-8'))

#mongoDBを使うかどうかに関して、コマンドライン引数を使用
argLen = len(sys.argv)
usePrint = False

if "print" in sys.argv:
    usePrint = True
    print "tweetPrint: ON"

# 各種キーをセット
def initAuth():
    auth = tweepy.OAuthHandler(conf['twitter_keys']['CONSUMER_KEY'], conf['twitter_keys']['CONSUMER_SECRET'])
    auth.set_access_token(conf['twitter_keys']['ACCESS_TOKEN'], conf['twitter_keys']['ACCESS_SECRET'])
    return auth

def trimSrc(str):
    src = re.sub("<.*?>", "", str)
    return src

file = open(conf['whitelist'])
wl = []
for line in file:
    wl.append(line.rstrip("\r\n"))
file.close()

class StdOutListener(StreamListener):
    def __init__(self):
         super(StdOutListener,self).__init__()

    def on_data(self, data):
        try:
            if data.startswith("{"):
                jsonData = json.loads(data)
                # if jsonData['lang'] == 'ja' and 'bot' not in jsonData['source']:
                if jsonData['lang'] == 'ja' and trimSrc(jsonData['source']).encode('utf-8') in wl:
                    message = jsonData['text'].encode('utf-8')
                    # message = trimSrc(jsonData['source'].encode('utf-8'))
                    # デバッグ用print
                    if usePrint == True:
                        print message
                    #self.producer.send_Data(message);
                # mongoDBにtweetデータを挿入
        except Exception as e:
            print e;
        return True

    def on_error(self, status):
        if usePrint == True:
            print status#"error"
        return False

if __name__ == '__main__':
    l = StdOutListener()
    auth = initAuth()
    stream = Stream(auth, l)
    while True:
        try:
            stream.sample()  # ツイートのランダムサンプリングを取得する場合
        except Exception as e:
            # twitterに弾かれた場合は少し待って接続し直します
            # デバッグ用print
            if usePrint == True:
                print e
            time.sleep(60)
            stream = Stream(auth, l)
