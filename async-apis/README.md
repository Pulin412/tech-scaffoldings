# Asynchronous Processing

## @Async

- Introduced in Spring 3.0
- Runs heavy load jobs on a separate threads.
 
<details>

<summary> 
:robot: Return type should one of - `Future`, `CompletableFuture`, `ListenableFuture` 
</summary>

**Future**:
- Future class represents a future result of an asynchronous computation. This result will eventually appear in the Future after the processing is complete.

- Example of creating a simple task - 
      
    ```java
     public class SquareCalculator {
    
       private ExecutorService executor = Executors.newSingleThreadExecutor();
    
       public Future<Integer> calculate(Integer input) {        
        return executor.submit(() -> {
            Thread.sleep(1000);
            return input * input;
        });
       }
     }
     ```
  
  - `Callable` is an interface representing a task that returns a result, and has a single `call()` method. Here we’ve created an instance of it using a lambda expression. 
  - Creating an instance of `Callable` doesn’t take us anywhere; we still have to pass this instance to an executor that will take care of starting the task in a new thread, and give us back the valuable `Future` object. That’s where `ExecutorService` comes in.
  - We used the basic `newSingleThreadExecutor()`, which gives us an `ExecutorService` capable of handling a single thread at a time. 
  - Once we have an `ExecutorService` object, we just need to call `submit()`, passing our `Callable` as an argument. Then `submit()` will start the task and return a `FutureTask` object, which is an implementation of the `Future` interface.

    >  Running the above code in 2 parallel threads - \
          private ExecutorService executor = Executors.newFixedThreadPool(2);
        

- Consuming `Future` to see the result -
  - `Future.isDone()` tells us if the executor has finished processing the task. If the task is complete, it will return true; otherwise, it returns false.
    ```java 
       Future<Integer> future = new SquareCalculator().calculate(10);
    
       while(!future.isDone()) {
       System.out.println("Calculating...");
       Thread.sleep(300);
       }
    
       Integer result = future.get();
    ```
        
  - `get()` has an overloaded version that takes a timeout and a TimeUnit as arguments -
        
    ```java 
    Integer result = future.get(500, TimeUnit.MILLISECONDS);
    ```
        
  - The difference between `get(long, TimeUnit)` and `get()` is that the former will throw a `TimeoutException` if the task doesn’t return before the specified timeout period.
  - Suppose we triggered a task, but for some reason, we don’t care about the result anymore. We can use `Future.cancel(boolean)` to tell the executor to stop the operation and interrupt its underlying thread.
        
    ```java 
     Future<Integer> future = new SquareCalculator().calculate(4);
     boolean canceled = future.cancel(true);
    ```
  
**Completable Future**:

- The `Future` interface gives a result of an asynchronous computation, but it do not have any methods to combine these computations or handle possible errors.
- Along with the `Future` interface, `CompletableFuture` **_class_** also implemented the `CompletionStage` interface. This interface defines the contract for an asynchronous computation step that we can combine with other steps.
      
  ```java 
     public Future<String> calculateAsync() throws InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
        Thread.sleep(500);
        completableFuture.complete("Hello");
        return null;
        });

        return completableFuture;
     }
  ```
      
- We simply call the method, receive the `Future` instance, and call the `get` method on it when we’re ready to block for the result.
- If we already know the result of a computation, we can use the static `completedFuture` method with an argument that represents the result of this computation. Consequently, the `get` method of the `Future` will never block, immediately returning this result instead.
      
  ```java 
     Future<String> completableFuture = calculateAsync();
     // ...
     String result = completableFuture.get();
     assertEquals("Hello", result);
  ```
      
  ```java 
     Future<String> completableFuture =
     CompletableFuture.completedFuture("Hello");
     // ...
     String result = completableFuture.get();
     assertEquals("Hello", result);
  ```

</details>

- 

## Spring WebFlux