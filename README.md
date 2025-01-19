# Time-Based Cache

## Overview

The Time-Based Cache is a Java implementation of an in-memory cache with the following features:

- **Time-to-Live (TTL)**: Each entry in the cache expires after a specified duration.
- **FIFO Eviction**: Ensures that entries are evicted in the order they were added when the cache reaches its maximum size.
- **Automatic Cleanup**: Expired entries are periodically removed in the background.

## Features

1. **Put**: Add an entry to the cache with a time-to-live duration.
2. **Get**: Retrieve an entry by key, only if it has not expired.
3. **Remove**: Remove a specific entry by key.
4. **Clear**: Clear all entries from the cache.
5. **FIFO Eviction**: Automatically evict the oldest entry when the cache size exceeds its limit.
6. **Background Cleaner**: A daemon thread periodically removes expired entries.

## Usage

### Example

```java\
TimeBasedCache<String, String> cache = new TimeBasedCache<>(5000, 3);

// Adding entries to the cache
cache.put("key1", "value1", 2000);
cache.put("key2", "value2", 3000);
cache.put("key3", "value3", 4000);

// Retrieving an entry
System.out.println(cache.get("key1")); // Output: "value1"

// Adding another entry (evicts "key1" due to FIFO policy)
        cache.put("key4", "value4", 2000);
System.out.println(cache.get("key1")); // Output: null

// Wait for entries to expire
        Thread.sleep(5000);
System.out.println(cache.get("key2")); // Output: null
```

## Configuration

- **`cleanupIntervalMs`**: The interval in milliseconds at which expired entries are cleaned up.
- **`maxSize`**: The maximum number of entries the cache can hold.

## Unit Tests

The project includes unit tests to verify the functionality of the cache. Tests are implemented using JUnit 5 and cover the following scenarios:

1. Adding and retrieving entries
2. Expiry of entries
3. FIFO eviction
4. Removing entries
5. Clearing the cache

### Running Tests

Use the following command to run the tests:

```bash
mvn test
```

## Installation

1. Clone the repository:

```bash
git clone https://github.com/your-repo/time-based-cache.git
```

2. Navigate to the project directory:

```bash
cd time-based-cache
```

3. Build the project using Maven:

```bash
mvn clean install
```

## Contribution

Feel free to open issues or submit pull requests to improve this project. Ensure that your code is well-documented and tested.

## License

This project is licensed under the MIT License. See the LICENSE file for details.

