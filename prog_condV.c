#include <unistd.h>
#include <getopt.h>
#include <malloc.h>
#include <pthread.h>
#include <stdlib.h>

#define MAX_THREAD_COUNT  400

struct job{
	double          data;
	struct job*     next;
};

struct job* job_queue;

unsigned int  		job_queue_count;
pthread_mutex_t     job_queue_count_mutex;
pthread_mutex_t     job_queue_mutex;
pthread_cond_t      pcond_var;

void initialize_vars()
{
    job_queue_count = 0 ;
    pthread_mutex_init(&job_queue_mutex,NULL);
    pthread_mutex_init(&job_queue_count_mutex,NULL);
    pthread_cond_init(&pcond_var,NULL);
}

void* 
thread_callback_enqueue(void *arg)
{
	unsigned int thread_id = *((unsigned int*)arg);
    struct job*  new_job   = NULL;

    new_job = (struct job*)calloc(1,sizeof(struct job));
	if(NULL == new_job){
        perror("Memory Allocation failed.");
    }
    else{
        new_job->data = 100.0+thread_id;
    }

	pthread_mutex_lock(&job_queue_mutex);
    pthread_mutex_lock(&job_queue_count_mutex);

    if(NULL == job_queue){
        job_queue = new_job;
    }
    else{
        new_job->next = job_queue;
        job_queue = new_job;
    }
    
    job_queue_count++;
    pthread_cond_signal(&pcond_var);

    pthread_mutex_unlock(&job_queue_count_mutex);
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
    pthread_mutex_lock(&job_queue_count_mutex);
	while(!job_queue_count){
        pthread_cond_wait(&pcond_var,&job_queue_count_mutex);
    }

    /* When we reached hear, it implyes job_queue_count > 0 */
    pthread_mutex_unlock(&job_queue_count_mutex);


	pthread_mutex_lock(&job_queue_mutex);
    pthread_mutex_lock(&job_queue_count_mutex);

	if(job_queue_count > 0){
        next_job = job_queue;
        job_queue = job_queue->next;
        job_queue_count--;
    }

    pthread_mutex_unlock(&job_queue_count_mutex);
	pthread_mutex_unlock(&job_queue_mutex);


	printf("INSIDE thread [%d], processed job with data = [%f]\n",
			thread_id,next_job->data);
	free(next_job);
}

void usage(){
    printf("Invalid Argument... [TDB]\n");
}

main(int argc, char* argv[])
{
    char        ch    = '\0';
	int         count = 0;
	int         ind   = 0;
	int 		ret   = 0;
	int 		status = 0;
	pthread_t  	thread_list[MAX_THREAD_COUNT];
	int      	thread_list_int[MAX_THREAD_COUNT];
    unsigned int max_thr_count = 0;
    unsigned int init_thr_count = 0;

    while(-1 != (ch = getopt(argc,argv,"t:h"))){
        switch(ch){
            case 'h':
                usage(); exit(EXIT_SUCCESS);
                break;
            case 't':
                max_thr_count = atoi(optarg);
                if (max_thr_count > MAX_THREAD_COUNT){
                    printf("Max supported thread = %d\n",MAX_THREAD_COUNT);
                    printf("Please specify thread count < %d\n",MAX_THREAD_COUNT);
                    exit(EXIT_SUCCESS);
                }
                break;
            default: /* '?' */
                usage();exit(EXIT_FAILURE);
                break;
        }
    }

	/* Initialize the mutex/pthread_cond_t variable */
    initialize_vars();
	job_queue = NULL;
    job_queue_count = 0;
    init_thr_count = (rand()%(max_thr_count - 1)) + 1;

	for(count=0;count< init_thr_count;count++){
		thread_list_int[count] = count;
		ret = pthread_create(&thread_list[count],NULL,
				     thread_callback_dequeue,&thread_list_int[count]);
		if(ret!=0){
			fprintf(stderr,"[%d] thread creation failed...\n",count);
			perror("Exiting...");
		}
	}

	/* 		Create the thread for enqueue. 		*/
	for(;count<max_thr_count;count++){
		thread_list_int[count] = count;
		ret =  pthread_create(&thread_list[count],NULL,
				      thread_callback_enqueue,&thread_list_int[count]);
		if(ret!=0){
			fprintf(stderr,"[%d] thread creation failed...\n",count);
			perror("Exiting...");
		}
	}

	printf("Waiting for all children threads to be terminated...\n");
	for(ind=0;ind<max_thr_count;ind++){
		if(0 != pthread_join(thread_list[ind],(void*)&status)) 
			perror("Thread Join failed.");
		printf("Completed thread [%d] with ret = [%d]\n",ind,status);
	}

	return 0;
}
