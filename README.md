## Spring Boot KV Store Lol
A stupidly simple (apart from just being stupid) and "unusable for Production" in-memory KV Store in Spring Boot.
DO NOT USE FOR PRODUCTION (or even for dev for that matter)

Internally uses ConcurrentHashMap.

Todo:
- Concurrency tests
- More unnecessary features
- Refactoring get-all operation
- Memory profiling


### API:
<code>
/set?key=key&val=val
/get?key-key
/get-all
</code>

### Why?
Why not?
