# TweetProducer
Twitter HBC -> Kafka Produce
I would **use IntelliJ IDEA** to work with this project

## To compile
- in IDEA's Run/Debug Configurations "Command Line" field: `-e compile -Dmaven.compiler.source=1.5 -Dmaven.compiler.target=1.5`
- bash: `mvn -e compile -Dmaven.compiler.source=1.5 -Dmaven.compiler.target=1.5`

## To Run
- in IDEA's Run/Debug Configurations "Command Line" field: `exec:java -Dexec.mainClass="TweetGenerator" -Dexec.args="<your OAUTH>"`
- bash: `mvn exec:java -Dexec.mainClass="TweetGenerator" -Dexec.args="<your OAUTH>"`

*<your OAUTH> is your 4 oauth twitter credentials separated by a space, in the order they are read in the main function in TweetProducer.java
