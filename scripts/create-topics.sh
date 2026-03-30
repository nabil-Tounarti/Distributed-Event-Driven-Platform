#!/bin/sh

echo "Waiting for Kafka..."
sleep 15

echo "Listing topics (before)..."
kafka-topics --bootstrap-server kafka:29092 --list

echo "Creating topics..."

kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic orders --partitions 6 --replication-factor 1
kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic payments --partitions 6 --replication-factor 1
kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic inventory --partitions 6 --replication-factor 1
kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic notifications --partitions 6 --replication-factor 1

echo "Creating DLT topics..."
kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic orders.DLT --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic payments.DLT --partitions 3 --replication-factor 1

echo "Topics created"

echo "Listing topics (after)..."
kafka-topics --bootstrap-server kafka:29092 --list
