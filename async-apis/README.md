# Asynchronous Processing

## @Async

- Introduced in Spring 3.0
- Runs heavy load jobs on a separate threads.
- Return type should one of - `Future`, `CompletableFuture`, `ListenableFuture`

<details>

<summary> 
:point_right: Read further
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

- When the client initiates a request, it goes through all matched filters in the filter chain until it arrives at the `DispatcherServlet` instance. 
- Then, the servlet takes care of the async dispatching of the request. It marks the request as started by calling `AsyncWebRequest#startAsync`, transfers the request handling to an instance of `WebAsyncManager`, and finishes its job without committing the response. The filter chain also is traversed in the reverse direction to the root. 
- `WebAsyncManager` submits the request processing job in its associated `ExecutorService`. Whenever the result is ready, it notifies `DispatcherServlet` for returning the response to the client.
- Demo :

  - [AppRunner.java](https://github.com/Pulin412/tech-scaffoldings/blob/main/async-apis/src/main/java/com/tech/scaffolding/asyncapis/AppRunner.java) is added to call the [service](https://github.com/Pulin412/tech-scaffoldings/blob/main/async-apis/src/main/java/com/tech/scaffolding/asyncapis/service/JokeService.java#L24) asynchronously multiple times.
  - [taskExecutor](https://github.com/Pulin412/tech-scaffoldings/blob/main/async-apis/src/main/java/com/tech/scaffolding/asyncapis/AsyncApisApplication.java#L20) bean is added to have the custom Executor to limit the number of concurrent threads to two and limit the size of the queue to 500.
  - Output -
  
     ```shell
     2024-02-04T19:37:34.112+01:00  INFO 9492 --- [           main] c.tech.scaffolding.asyncapis.AppRunner   : Sending Requests at - 2024-02-04T19:37:34.112305
     2024-02-04T19:37:34.115+01:00  INFO 9492 --- [    AsyncTest-2] c.t.s.asyncapis.service.JokeService      : Fetching a joke....current thread - AsyncTest-2
     2024-02-04T19:37:34.115+01:00  INFO 9492 --- [    AsyncTest-1] c.t.s.asyncapis.service.JokeService      : Fetching a joke....current thread - AsyncTest-1
     2024-02-04T19:37:37.488+01:00  INFO 9492 --- [    AsyncTest-1] c.t.s.asyncapis.service.JokeService      : Fetching a joke....current thread - AsyncTest-1
     2024-02-04T19:37:40.623+01:00  INFO 9492 --- [           main] c.tech.scaffolding.asyncapis.AppRunner   : Elapsed time: 6510
     2024-02-04T19:37:40.629+01:00  INFO 9492 --- [           main] c.tech.scaffolding.asyncapis.AppRunner   : --> <200 OK OK,{"id":"AI6hiGtzAd","joke":"Where do bees go to the bathroom?  The BP station.","status":200}
     ```
  
  - Note that the first two calls happen in separate threads (AsyncTest-2, AsyncTest-1) and the third one is parked until one of the two threads became available. To compare how long this takes without the asynchronous feature, try commenting out the `@Async` annotation and runing the service again. The total elapsed time should increase noticeably, because each query takes at least a second. 
  - You can also tune the `Executor` to increase the `corePoolSize` attribute for instance.


## Spring WebFlux

- The term, `reactive`, refers to programming models that are built around reacting to change — network components reacting to I/O events, UI controllers reacting to mouse events, and others. In that sense, non-blocking is reactive, because, instead of being blocked, we are now in the mode of reacting to notifications as operations complete or data becomes available.
- `Reactor` is the reactive library of choice for Spring `WebFlux`. It provides the `Mono` and `Flux` API types to work on data sequences of 0..1 (Mono) and 0..N (Flux) through a rich set of operators aligned with the ReactiveX vocabulary of operators.
- As a general rule, a WebFlux API accepts a plain `Publisher` as input, adapts it to a Reactor type internally, uses that, and returns either a `Flux` or a `Mono` as output.

<details>
<summary> 
:point_right: Reactive Publisher/Subscriber Interfaces
</summary>

- To build a `Flow`, we can use three main abstractions and compose them into asynchronous processing logic.
- Every `Flow` needs to process events that are published to it by a `Publisher` instance; the Publisher has one method – `subscribe()`.
- The receiver of messages needs to implement the `Subscriber` interface. This has four methods that need to be overridden – `onSubscribe(), onNext(), onError(), onComplete()`.
- If we want to transform incoming message and pass it further to the next `Subscriber`, we need to implement the `Processor` interface.
- Publisher/Subscriber Implementation : 

  - The onSubscribe() method is called before processing starts
      ```java
      public class EndSubscriber<T> implements Subscriber<T> {
          private Subscription subscription;
          public List<T> consumedElements = new LinkedList<>();
  
          @Override
          public void onSubscribe(Subscription subscription) {
              this.subscription = subscription;
              subscription.request(1);
          }
      }
      ```
    
  - onNext() – this is called whenever the Publisher publishes a new message:
     ```java 
     @Override
     public void onNext(T item) {
      System.out.println("Got : " + item);
      consumedElements.add(item);
      subscription.request(1);
     }
    ```
  > When we started the subscription in the `onSubscribe()` method and when we processed a message we need to call the `request()` method on the Subscription to signal that the current Subscriber is ready to consume more messages.

  - onError() – which is called whenever some exception will be thrown in the processing, as well as onComplete() – called when the Publisher is closed:
    ```java 
      @Override
      public void onError(Throwable t) {
       t.printStackTrace();
      }
  
      @Override
      public void onComplete() {
       System.out.println("Done");
      }
    ```

  - Test the Producer flow : 
  ```java 
  @Test
  public void whenSubscribeToIt_thenShouldConsumeAll()
  throws InterruptedException {

    // given
    SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
    EndSubscriber<String> subscriber = new EndSubscriber<>();
    publisher.subscribe(subscriber);
    List<String> items = List.of("1", "x", "2", "x", "3", "x");

    // when
    assertThat(publisher.getNumberOfSubscribers()).isEqualTo(1);
    items.forEach(publisher::submit);
    publisher.close();

    // then
     await().atMost(1000, TimeUnit.MILLISECONDS)
       .until(
         () -> assertThat(subscriber.consumedElements)
         .containsExactlyElementsOf(items)
     );
  }
  ```
  > `SubmissionPublisher` class – a construct from the `java.util.concurrent` – which implements the `Publisher` interface.
  
  > We’re calling the `close()` method on the instance of the `Publisher`. It will invoke `onComplete()` callback underneath on every `Subscriber` of the given `Publisher`.  

</details>

<details>
<summary> 
:point_right: Mono
</summary>

- `Mono` is a special type of `Publisher`. 
- A Mono object represents a single or empty value. This means it can emit only one value at most for the `onNext()` request and then terminates with the `onComplete()` signal. In case of failure, it only emits a single `onError()` signal.

- Example : 
  ```java 
  @Test
  public void givenMonoPublisher_whenSubscribeThenReturnSingleValue() {
      Mono<String> helloMono = Mono.just("Hello");
      StepVerifier.create(helloMono)
        .expectNext("Hello")
        .expectComplete()
        .verify();
  }
  ```
- When `helloMono` is subscribed, it emits only one value and then sends the signal of completion.

</details>

<details>
<summary> 
:point_right: Flux
</summary>

- `Flux` is a standard `Publisher` that represents 0 to N asynchronous sequence values. 
- This means that it can emit 0 to many values, possibly infinite values for `onNext()` requests, and then terminates with either a completion or an error signal.

- Example: 
  ```java 
  @Test
  public void givenFluxPublisher_whenSubscribedThenReturnMultipleValues() {
      Flux<String> stringFlux = Flux.just("Hello", "Baeldung");
      StepVerifier.create(stringFlux)
        .expectNext("Hello")
        .expectNext("Baeldung")
        .expectComplete()
        .verify();
  }
  ```

</details>

> `Mono` and `Flux` are both implementations of the `Publisher` interface. In simple terms, we can say that when we’re doing something like a computation or making a request to a database or an external service, and expecting a maximum of one result, then we should use `Mono`. 

> When we’re expecting multiple results from our computation, database, or external service call, then we should use `Flux`.

> `Mono` is more relatable to the `Optional` class in Java since it contains 0 or 1 value, and `Flux` is more relatable to `List` since it can have N number of values.

- `WebClient` is used to make the API calls and same is explored in the AppRunner class.
 

## Final Comparisons

- `CompletableFuture` with `RestTemplate`: 
  - This approach is more traditional and is suitable for applications that are not fully reactive. 
  - `RestTemplate` is a blocking HTTP client, and while `@Async` and `CompletableFuture` introduce asynchrony, the underlying operation still blocks the thread during the HTTP call. 
  - This method can be more straightforward to integrate into applications that are already using Spring's @Async support but don't require a fully non-blocking stack.


- `Mono` with `WebClient`: 
  - This approach is fully reactive and non-blocking, making it the better choice for reactive applications. 
  - `WebClient` is designed to work within the Spring WebFlux framework, which supports reactive streams. 
  - This method allows for more efficient resource utilization, especially under high load, by avoiding thread blocking during I/O operations.