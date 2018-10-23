1) Ethan Anderson, Trevor Burton. Tasks were divided up equally, as most of the work was pair programming.
2) Compile: javac -Xlint:unchecked Porter.java p1.java -d .
3) Run: java com.company.p1

Description: We implemented multi-threading by creating an instance of the ExecutorService class. With this class,
we can create a thread-pool, which allows the program to re-use threads once their done (if necessary).
We used a thread-pool size of 20, so 20 queries can be executed concurrently. If more than 20 queries are used,
some queries will have to wait until a thread is available to process it.

The multi-threading implementation can be found around line 42-62, where the ExecutorService is instantiated and
the run method of each thread is shown. Each thread will run a single query by creating its own hashmap of the
rank values. The printing of the queries is done by adding the "synchronized" tag to the displayResults() method.
The synchronized tag implements the monitor structure of only allowing one thread in at a time, so it does not
have to be coded manually.

Please let us know if you have any other questions about our implementation, we'd be happy to explain more.