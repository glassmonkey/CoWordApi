var kafka = require('kafka-node');
var jsYaml = require('js-yaml');
var fs = require("fs");
var conf  = jsYaml.safeLoad(fs.readFileSync('config.yml', 'utf-8'));
var Producer = kafka.Producer,
    KeyedMessage = kafka.KeyedMessage,
    client = new kafka.Client(conf.servers.word_zookeeper_server),
    producer = new Producer(client),
    km = new KeyedMessage('key', 'message');

producer.on('error', function (err) {});

var Consumer = kafka.Consumer,
    client = new kafka.Client(conf.servers.word_zookeeper_server),
    consumer = new Consumer(
        client, [{
            topic: 'topic1',
            offset: 0
        }], {
            autoCommit: false,
            encoding: 'utf8'
        }
    );

var kafka = {
    'send':function(message,func){
        producer.send([{ topic: 'topic1', messages: message, partition: 0}], func)
    },
    'onMessage':function(func){
        consumer.on('message', func);
    }
}
module.exports.kafka = kafka;
