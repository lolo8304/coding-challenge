
Here is a list of commonly used commands supported by a normal Memcached server:

1. **get**: Retrieves the value associated with a specific key.
   ```
   get <key>
   ```

2. **set**: Stores a value in the cache with a specified key.
   ```
   set <key> <flags> <exptime> <bytes> [noreply]
   <value>
   ```

3. **add**: Stores a value in the cache only if the key does not already exist.
   ```
   add <key> <flags> <exptime> <bytes> [noreply]
   <value>
   ```

4. **replace**: Stores a value in the cache only if the key already exists.
   ```
   replace <key> <flags> <exptime> <bytes> [noreply]
   <value>
   ```

5. **delete**: Deletes a key-value pair from the cache.
   ```
   delete <key> [noreply]
   ```

6. **incr**: Increments the value associated with a key by a specified amount.
   ```
   incr <key> <value> [noreply]
   ```

7. **decr**: Decrements the value associated with a key by a specified amount.
   ```
   decr <key> <value> [noreply]
   ```

8. **append**: Appends data to the value associated with a key.
   ```
   append <key> <bytes> [noreply]
   <value>
   ```

9. **prepend**: Prepends data to the value associated with a key.
   ```
   prepend <key> <bytes> [noreply]
   <value>
   ```

10. **stats**: Retrieves server statistics.
    ```
    stats [stat_type]
    ```

11. **version**: Retrieves the version information of the Memcached server.
    ```
    version
    ```

12. **flush_all**: Clears all key-value pairs from the cache.
    ```
    flush_all [delay]
    ```

13. **quit**: Closes the connection to the Memcached server.
    ```
    quit
    ```

14. **shutdown**: Initiates the graceful shutdown of the Memcached server (implementation-specific).
    ```
    shutdown
    ```

These commands cover basic functionalities such as getting, setting, deleting, incrementing, decrementing values, as well as server statistics and management. It's important to note that additional Memcached server implementations may support additional commands or variations of these commands.


Certainly! Here are a few more commands that are commonly supported by Memcached servers:

15. **cas**: Compares and sets a value in the cache if the key exists and has not been modified since retrieval.
    ```
    cas <key> <flags> <exptime> <bytes> <cas_unique> [noreply]
    <value>
    ```

16. **get_multi**: Retrieves multiple values associated with multiple keys in a single request.
    ```
    get_multi <key1> <key2> <key3> ...
    ```

17. **touch**: Updates the expiration time of a key without modifying its value.
    ```
    touch <key> <exptime> [noreply]
    ```

18. **stats**: Retrieves detailed statistics about the Memcached server and its items.
    ```
    stats [stat_type]
    ```

19. **verbosity**: Adjusts the verbosity level of the server's logging output.
    ```
    verbosity <level> [noreply]
    ```

20. **slabs reassign**: Moves items between slabs to optimize memory allocation.
    ```
    slabs reassign <source_classid> <target_classid>
    ```

21. **flush_all**: Clears all key-value pairs from the cache.
    ```
    flush_all [delay]
    ```

22. **stats items**: Retrieves detailed statistics about the items in the cache.
    ```
    stats items [slab_id]
    ```

23. **stats settings**: Retrieves the current configuration settings of the Memcached server.
    ```
    stats settings
    ```

24. **stats sizes**: Retrieves size statistics of items stored in the cache.
    ```
    stats sizes
    ```

25. **stats slabs**: Retrieves detailed information about slab classes and memory allocation.
    ```
    stats slabs
    ```

These additional commands provide more functionality and insights into the Memcached server's operation, including detailed statistics, configuration settings, and memory management operations.

Please note that the availability and behavior of these commands may vary depending on the specific Memcached server implementation and its version.


Certainly! Here are a few more commands that are commonly supported by Memcached servers:

26. **touch_all**: Updates the expiration time of multiple keys without modifying their values.
    ```
    touch_all <exptime> <key1> <key2> <key3> ...
    ```

27. **incr_multi**: Increments the values of multiple keys by specified amounts.
    ```
    incr_multi <value1> <key1> <value2> <key2> <value3> <key3> ...
    ```

28. **decr_multi**: Decrements the values of multiple keys by specified amounts.
    ```
    decr_multi <value1> <key1> <value2> <key2> <value3> <key3> ...
    ```

29. **prepend_multi**: Prepends data to the values of multiple keys.
    ```
    prepend_multi <bytes1> <value1> <key1> <bytes2> <value2> <key2> ...
    ```

30. **append_multi**: Appends data to the values of multiple keys.
    ```
    append_multi <bytes1> <value1> <key1> <bytes2> <value2> <key2> ...
    ```

31. **incrby**: Increments the value associated with a key by a specified increment.
    ```
    incrby <key> <increment> [noreply]
    ```

32. **decrby**: Decrements the value associated with a key by a specified decrement.
    ```
    decrby <key> <decrement> [noreply]
    ```

33. **gat**: Retrieves the value associated with a key and updates its expiration time in a single command.
    ```
    gat <exptime> <key>
    ```

34. **gats**: Retrieves the value and its CAS token associated with a key, updating its expiration time in a single command.
    ```
    gats <exptime> <key>
    ```

35. **stats cachedump**: Retrieves detailed information about items stored in a particular slab class.
    ```
    stats cachedump <slab_id> <limit>
    ```

These additional commands provide more advanced functionality, including batch operations, expiration time updates, and detailed information about cached items.

Remember that the availability and behavior of these commands may depend on the specific Memcached server implementation and its version. It's always a good practice to consult the documentation or specific resources related to your Memcached server implementation for complete and accurate information about the supported commands.


Certainly! Here is a comprehensive list of commands supported by Memcached servers:

1. **add**: Stores a value in the cache only if the key does not already exist.
   ```
   add <key> <flags> <exptime> <bytes> [noreply]
   <value>
   ```

2. **append**: Appends data to the value associated with a key.
   ```
   append <key> <bytes> [noreply]
   <value>
   ```

3. **cas**: Compares and sets a value in the cache if the key exists and has not been modified since retrieval.
   ```
   cas <key> <flags> <exptime> <bytes> <cas_unique> [noreply]
   <value>
   ```

4. **decr**: Decrements the value associated with a key by a specified amount.
   ```
   decr <key> <value> [noreply]
   ```

5. **delete**: Deletes a key-value pair from the cache.
   ```
   delete <key> [noreply]
   ```

6. **flush_all**: Clears all key-value pairs from the cache.
   ```
   flush_all [delay]
   ```

7. **get**: Retrieves the value associated with a specific key.
   ```
   get <key>
   ```

8. **gets**: Retrieves the value and its CAS token associated with a key.
   ```
   gets <key>
   ```

9. **incr**: Increments the value associated with a key by a specified amount.
   ```
   incr <key> <value> [noreply]
   ```

10. **prepend**: Prepends data to the value associated with a key.
    ```
    prepend <key> <bytes> [noreply]
    <value>
    ```

11. **replace**: Stores a value in the cache only if the key already exists.
    ```
    replace <key> <flags> <exptime> <bytes> [noreply]
    <value>
    ```

12. **set**: Stores a value in the cache with a specified key.
    ```
    set <key> <flags> <exptime> <bytes> [noreply]
    <value>
    ```

13. **add_multi**: Stores multiple key-value pairs in the cache, only if the keys do not already exist.
    ```
    add_multi <key1> <flags1> <exptime1> <bytes1> [noreply]
    <value1>
    <key2> <flags2> <exptime2> <bytes2> [noreply]
    <value2>
    ...
    ```

14. **append_multi**: Appends data to the values associated with multiple keys.
    ```
    append_multi <key1> <bytes1> [noreply]
    <value1>
    <key2> <bytes2> [noreply]
    <value2>
    ...
    ```

15. **cas_multi**: Compares and sets values in the cache for multiple keys.
    ```
    cas_multi <key1> <flags1> <exptime1> <bytes1> <cas_unique1> [noreply]
    <value1>
    <key2> <flags2> <exptime2> <bytes2> <cas_unique2> [noreply]
    <value2>
    ...
    ```

16. **decr_multi**: Decrements the values associated with multiple keys by specified amounts.
    ```
    decr_multi <key1> <value1> [noreply]
    <key2> <value2> [noreply]
    ...
    ```

17. **delete_multi**: Deletes multiple key-value pairs from the cache.
    ```
    delete_multi <key1> [noreply]
    <key2> [noreply]
    ...
    ```

18. **get_multi**: Retrieves the values associated with multiple keys in a single request.
    ```
    get_multi <key1> <key2> ...
    ```

19. **gets_multi**: Retrieves the values and CAS tokens associated with multiple keys in a single request.
    ```
    gets_multi <key1> <key2> ...
    ```

20. **incr_multi**: Increments the values associated with multiple keys by specified amounts.
    ```
    incr_multi <key1> <value1> [noreply]
    <key2> <value2> [noreply]
    ...
    ```

21. **prepend_multi**: Prepends data to the values associated with multiple keys.
    ```
    prepend_multi <key1> <bytes1> [noreply]
    <value1>
    <key2> <bytes2> [noreply]
    <value2>
    ...
    ```

22. **replace_multi**: Stores multiple key-value pairs in the cache, only if the keys already exist.
    ```
    replace_multi <key1> <flags1> <exptime1> <bytes1> [noreply]
    <value1>
    <key2> <flags2> <exptime2> <bytes2> [noreply]
    <value2>
    ...
    ```

23. **set_multi**: Stores multiple key-value pairs in the cache.
    ```
    set_multi <key1> <flags1> <exptime1> <bytes1> [noreply]
    <value1>
    <key2> <flags2> <exptime2> <bytes2> [noreply]
    <value2>
    ...
    ```

24. **stats**: Retrieves server statistics.
    ```
    stats [stat_type]
    ```

25. **touch**: Updates the expiration time of a key without modifying its value.
    ```
    touch <key> <exptime> [noreply]
    ```

26. **version**: Retrieves the version information of the Memcached server.
    ```
    version
    ```

27. **verbosity**: Adjusts the verbosity level of the server's logging output.
    ```
    verbosity <level> [noreply]
    ```

28. **slabs reassign**: Moves items between slabs to optimize memory allocation.
    ```
    slabs reassign <source_classid> <target_classid>
    ```

29. **stats cachedump**: Retrieves detailed information about items stored in a particular slab class.
    ```
    stats cachedump <slab_id> <limit>
    ```

30. **stats items**: Retrieves detailed statistics about the items in the cache.
    ```
    stats items [slab_id]
    ```

31. **stats settings**: Retrieves the current configuration settings of the Memcached server.
    ```
    stats settings
    ```

32. **stats sizes**: Retrieves size statistics of items stored in the cache.
    ```
    stats sizes
    ```

33. **stats slabs**: Retrieves detailed information about slab classes and memory allocation.
    ```
    stats slabs
    ```

34. **quit**: Closes the connection to the Memcached server.
    ```
    quit
    ```

35. **shutdown**: Initiates the graceful shutdown of the Memcached server.
    ```
    shutdown
    ```

Please note that while this list covers many of the common Memcached commands, the availability and behavior of these commands may vary depending on the specific Memcached server implementation and its version. Additionally, some commands may have variations or additional options supported by certain

