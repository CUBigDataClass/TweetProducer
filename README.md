# TweetProducer
Twitter HBC -> Kafka Produce
I would **use IntelliJ IDEA** to work with this project

## To compile
- in IDEA's Run/Debug Configurations "Command Line" field: `-e compile -Dmaven.compiler.source=1.5 -Dmaven.compiler.target=1.5`
- bash: `mvn clean compile`

## To Run

### Run zookeeper and kafka
``` bash
$ ~/kafka/bin/zookeeper-server-start.sh ~/kafka/config/zookeeper.properties
$ ~/kafka/bin/kafka-server-start.sh ~/kafka/config/server.properties
```
### Create the "tweets" topic
``` bash
$ ~/kafka/bin/kafka-create-topic.sh --topic tweets --zookeeper localhost:2181
```
### Run the producer and start the command line consumer
- in IDEA's Run/Debug Configurations "Command Line" field: `exec:java -Dexec.mainClass="TweetProducer" -Dexec.args="<your OAUTH>"`
- bash: `mvn exec:java -Dexec.mainClass="TweetProducer" -Dexec.args="<your OAUTH>"`

\<your OAUTH\> is your 4 oauth twitter credentials separated by a space, in the order they are read in the main function in TweetProducer.java

Consume the data in the command line:
``` bash
$ ~/kafka/bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic page_visits --from-beginning
```
