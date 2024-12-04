package uipath;


import java.util.*;

// Job status enum
enum JobStatus {
    PENDING, RUNNING, COMPLETED, FAILED
}

// Job class with builder pattern
class Job {
    private final String id;
    private final String name;
    private final int priority;
    private JobStatus status;
    private final List<Job> dependencies;
    private final Runnable task;

    private Job(Builder builder) {
        this.id = UUID.randomUUID().toString();
        this.name = builder.name;
        this.priority = builder.priority;
        this.status = JobStatus.PENDING;
        this.dependencies = new ArrayList<>();
        this.task = builder.task;
    }

    public static class Builder {
        private String name;
        private int priority;
        private Runnable task;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder task(Runnable task) {
            this.task = task;
            return this;
        }

        public Job build() {
            if (name == null || task == null) {
                throw new IllegalStateException("Job must have name and task");
            }
            return new Job(this);
        }
    }

    public void addDependency(Job job) {
        dependencies.add(job);
    }

    public boolean canExecute() {
        return dependencies.stream()
                .allMatch(job -> job.getStatus() == JobStatus.COMPLETED);
    }

    public void execute() {
        try {
            System.out.println("Executing job: " + name);
            status = JobStatus.RUNNING;
            task.run();
            status = JobStatus.COMPLETED;
            System.out.println("Completed job: " + name);
        } catch (Exception e) {
            status = JobStatus.FAILED;
            System.out.println("Failed job: " + name + " - " + e.getMessage());
        }
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public JobStatus getStatus() { return status; }
    public List<Job> getDependencies() { return dependencies; }
}

// Workflow class
class Workflow {
    private final List<Job> jobs;
    private final Map<String, Job> jobMap;

    public Workflow() {
        this.jobs = new ArrayList<>();
        this.jobMap = new HashMap<>();
    }

    public void addJob(Job job) {
        jobs.add(job);
        jobMap.put(job.getId(), job);
    }

    public void addDependency(Job job, Job dependsOn) {
        if (!jobMap.containsKey(job.getId()) || !jobMap.containsKey(dependsOn.getId())) {
            throw new IllegalArgumentException("Both jobs must be in the workflow");
        }
        job.addDependency(dependsOn);
    }

    public List<Job> getJobs() {
        return new ArrayList<>(jobs);
    }
}

// Scheduler class
class Scheduler {
    private final Queue<Job> readyQueue;
    private final Map<String, Job> allJobs;

    public Scheduler() {
        this.readyQueue = new LinkedList<>();
        this.allJobs = new HashMap<>();
    }

    public void submitWorkflow(Workflow workflow) {
        // Add all jobs to tracking map
        workflow.getJobs().forEach(job -> allJobs.put(job.getId(), job));
        
        // Add jobs with no dependencies to ready queue
        workflow.getJobs().stream()
               .filter(Job::canExecute)
               .forEach(readyQueue::offer);
    }

    public void executeNext() {
        Job job = readyQueue.poll();
        if (job != null) {
            job.execute();
            // Check for newly ready jobs
            checkAndQueueReadyJobs();
        }
    }

    private void checkAndQueueReadyJobs() {
        allJobs.values().stream()
               .filter(job -> job.getStatus() == JobStatus.PENDING)
               .filter(Job::canExecute)
               .forEach(readyQueue::offer);
    }

    public boolean hasJobsToExecute() {
        return !readyQueue.isEmpty();
    }
}

// Main demo class
public class JobScheduler {
    public static void main(String[] args) {
        // Create scheduler
        Scheduler scheduler = new Scheduler();

        // Create jobs
        Job dataLoad = new Job.Builder()
            .name("Data Load")
            .priority(1)
            .task(() -> System.out.println("Loading data..."))
            .build();

        Job dataValidate = new Job.Builder()
            .name("Data Validation")
            .priority(2)
            .task(() -> System.out.println("Validating data..."))
            .build();

        Job dataProcess = new Job.Builder()
            .name("Data Processing")
            .priority(3)
            .task(() -> System.out.println("Processing data..."))
            .build();

        // Create workflow
        Workflow workflow = new Workflow();
        workflow.addJob(dataLoad);
        workflow.addJob(dataValidate);
        workflow.addJob(dataProcess);

        // Add dependencies
        workflow.addDependency(dataValidate, dataLoad);
        workflow.addDependency(dataProcess, dataValidate);

        // Submit and execute
        System.out.println("Submitting workflow...");
        scheduler.submitWorkflow(workflow);

        // Execute all jobs
        while (scheduler.hasJobsToExecute()) {
            scheduler.executeNext();
        }
    }
}