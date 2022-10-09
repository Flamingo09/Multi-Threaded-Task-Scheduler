# Design a Multi-Threaded-Task-Scheduler

## Requirements
1. User should be able to schedule ad-hoc events
2. User should be able to schedule a recurring event with fixed rate
3. User should be able to schedule a recurring event with some delay rate

Extended Requirements
1. start time, end time for job scheduler


## Non functional requirements
1. Async
2. Concurrent
3. Multithreading

## Proposed Solutions
I will be using Java as a preferred language

### Entities
1. ScheduledTask - What I want to actually run
2. TaskCreatorService -> factory class to create ScheduledTask.
3. SchedulerExecutor - this is where i'll trigger all APIs
4. SchedulerService - Can I delegate the actual business logic here
5. TaskType - {RUN_ONCE, RECUR, RECUR_WITH_WAIT}
6. Queue - PriorityQueue -> push the job and poll from it

### Design patterns
1. Singleton - SchedulerService

### Data structure and algorithms
1. PriorityQueue

### Multithreading
1. Executors
2. Concurrency
3. Synchronization
4. Locks
5. Conditions
   (ThreadPoolExecutor)

### Questions
1. Deadlock ?
2. How to track error
   i. while scheduling not much to fail
   ii. While execution, yes it can -- logging and monitoring
