# Real-Time Product Analytics with Kafka Streams

This project demonstrates how to build a real-time product analytics platform using Kafka Streams, solving common challenges in e-commerce analytics at scale.

## Quick Links

- [Business Problem & Solution](business-case.md)
- [Architecture Overview](architecture.md)
- [Getting Started](getting-started.md)
- [Examples](examples/index.md)
- [Monitoring & Management](monitoring.md)
- [Troubleshooting Guide](troubleshooting.md)

## Features at a Glance

- Real-time product event processing
- Time-windowed analytics
- Unique user tracking
- Aggregated product statistics
- State store management
- Scalable stream processing
- Fault-tolerant operations

## Prerequisites

- Java 17+
- Docker and Docker Compose
- Gradle

## Quick Start

```bash
# Start Kafka environment
docker-compose up -d

# Load sample data
./gradlew loadProductMetadata

# Run the application
./gradlew bootRun
```

For detailed setup instructions, see our [Getting Started Guide](getting-started.md).

## Documentation Structure

- **[Business Case](business-case.md)**: Understanding the problem and solution
- **[Architecture](architecture.md)**: System design and components
- **[Getting Started](getting-started.md)**: Setup and running the application
- **[Examples](examples/index.md)**: Demo applications and use cases
- **[Monitoring](monitoring.md)**: Observability and management
- **[Troubleshooting](troubleshooting.md)**: Common issues and solutions

## Contributing

See our [Contributing Guide](CONTRIBUTING.md) for details on how to:
- Set up your development environment
- Run tests
- Submit pull requests

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. 