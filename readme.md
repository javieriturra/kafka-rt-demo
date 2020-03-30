# Kafka Spark POC

Written in Scala, this project introduces basic Structured Streaming features of Apache Spark like:
- Kafka events producing and consuming
- Event Watermarking, Unordered data, Delayed data 
- JSON transformations
- Joins between streaming dataframes and batch dataframes
- Data aggregation in time windows
- Processing Triggers
- Real Time micro batches and output modes (_append_, _update_, _complete_)
- Loading batch data from files. Data caching

## Prerequisites

- Docker Engine
- Docker Compose
- sbt (Scala Build Tool)
- IntelliJ (optional)

## Running the Services

Up the docker service kafka, defined into the folder docker-build-kafka, i.e.:

```cd docker-build-kafka```

```docker-compose up```

## Running the Apps

To run the apps, use IntelliJ's right click menu on the files. Alternatively, you can run the apps in the console.
The recommendation is to run the apps in __black__ simultaneously.

|Producers         |Consumers                       |
|---               |---                             |
|RateToConsoleApp  |KafkaEventCountApp              |
|__RateToKafkaApp__|__KafkaEventCountByLocationApp__|
|                  |KafkaToConsoleApp               | 

Please read the documentation to understand what does each program.

## TODO

- More details on explanations
- Writing to a Time Series Database
- Spark Cluster Usage
- Real Time Visualization
- More realistic emulated data
- Connecting to a real data source
