## Spring Boot KV Store Lol
A stupidly simple (apart from just being stupid) and "unusable for Production" in-memory KV Store in Spring Boot.
DO NOT USE FOR PRODUCTION (or even for dev for that matter)

Internally uses ConcurrentHashMap.  Under the hood it uses CAS operations to ensure memory safety in multi-threaded environment.

Todo:
- Concurrency tests
- More unnecessary features
- GitHub Actions integration - Done
- Refactoring get-all operation - Done
- Memory profiling


### API:
<code>
/set?key=key&val=val
/get?key-key
/get-all
/delete?key=key
</code>

### Why?
Why not?
