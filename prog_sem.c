#include <malloc.h>
#include <pthread.h>
#include <semaphore.h>

#define MAX_THREAD_COUNT  40

struct job{
	double          data;
	struct job*     next;
};

struct job* job_queue;

sem_t  		    	job_queue_count;
pthread_mutex_t     job_queue_mutex = PTHREAD_MUTEX_INITIALIZER; 

void* 
thread_callback_enqueue(void *arg)
{
	unsigned int thread_id = *((unsigned int*)arg);
	struct job*  new_job   = NULL;
	
	pthread_mutex_lock(&job_queue_mutex);
	new_job = (struct job*)calloc(1,sizeof(struct job));
	if(NULL == new_job) perror("Memory Allocation failed.");
	new_job->data = 100.0+thread_id;

	if(NULL == job_queue){
		job_queue = new_job;
	}
	else{
		new_job->next = job_queue;
		job_queue = new_job;
	}
	
	/* Post the information through sem_t variable. */
	sem_post(&job_queue_count);

	pthread_mutex_unlock(&job_queue_mutex);
	printf("INSIDE thread [%d], enqued job with data = [%f]\n",
			thread_id,new_job->data);
}

void*
thread_callback_dequeue(void* arg)
{
	int thread_id = *((int*)arg);
	struct job*  next_job  = NULL;

	/* Wait untill job_queue_count > 0 */
	sem_wait(&job_queue_count);

	pthread_mutex_lock(&job_queue_mutex);
	next_job = job_queue;
	job_queue = job_queue->next;
	pthread_mutex_unlock(&job_queue_mutex);

	printf("INSIDE thread [%d], processed job with data = [%f]\n",
			thread_id,next_job->data);
	free(next_job);
}

main()
{
	int             count = 0;
	int             ind   = 0;
	int 		ret   = 0;
	int 		status = 0;
	pthread_t  	thread_list[MAX_THREAD_COUNT];
	int      	thread_list_int[MAX_THREAD_COUNT];

	/* Initialize the sem_t variable */
	sem_init(&job_queue_count,0,0);
	job_queue = NULL;

	for(count=0;count<MAX_THREAD_COUNT/3;count++){
		thread_list_int[count] = count;
		ret = pthread_create(&thread_list[count],NULL,
				     thread_callback_dequeue,&thread_list_int[count]);
		if(ret!=0){
			fprintf(stderr,"[%d] thread creation failed...\n",count);
			perror("Exiting...");
		}
	}

	/* 		Create the thread for enqueue. 		*/
	for(;count<MAX_THREAD_COUNT;count++){
		thread_list_int[count] = count;
		ret =  pthread_create(&thread_list[count],NULL,
				      thread_callback_enqueue,&thread_list_int[count]);
		if(ret!=0){
			fprintf(stderr,"[%d] thread creation failed...\n",count);
			perror("Exiting...");
		}
	}

	printf("Waiting for all children threads to be terminated...\n");
	for(ind=0;ind<MAX_THREAD_COUNT;ind++){
		if(0 != pthread_join(thread_list[ind],(void*)&status)) 
			perror("Thread Join failed.");
		printf("Completed thread [%d] with ret = [%d]\n",ind,status);
	}

	return 0;
}
