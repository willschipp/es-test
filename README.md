## Elasticsearch Sizing Tests


### Methodology

- generated a flat file or random log information in the following format
```
\[source IP address] [yyyymmddHHMMss] GET http://[random uuid]/[random uuid]/[random uuid]/[random uuid] 0 TCP_MISS/200 - [target IP address] - 0
```
- random IP addresses are generated for source IP and destination IP address
- timestamp is derived from current time
- url consists of 4 random UUID
- randomization to ensure minimal chance of index reuse and/or compression

### testing results
- 100,000k records search for single randomized value in the set returned in 64ms
- 1m records search for single randomized value in the set returned in 83ms
- 100m records search for single randomized value in the set returned in 176ms
- 1b records search for single randomized value in the set returned in 12,672ms (12.6 seconds)

### size

100m records = 6.6GB single node, ~15min loading time with "analyze all" created (source file was 2.1GB)
1b records = 67GB single node, ~3.48hrs loading time with "analyze all" created (source file was 21GB)